package com.cashride.data.repository

import com.cashride.data.database.CashRideDatabase
import com.cashride.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.math.*

class CashRideRepository(private val database: CashRideDatabase) {
    
    private val userDao = database.userDao()
    private val rideDao = database.rideDao()
    
    // User 관련 함수들
    fun getUser(userId: String): Flow<User?> = userDao.getUser(userId)
    
    suspend fun initializeUser(userId: String) {
        val existingUser = userDao.getUserOnce(userId)
        if (existingUser == null) {
            val newUser = User(userId = userId)
            userDao.insertUser(newUser)
        }
    }
    
    suspend fun updateUserPoints(userId: String, points: Int) {
        userDao.updatePoints(userId, points)
    }
    
    // Ride 관련 함수들
    suspend fun startRide(userId: String): String {
        val rideId = UUID.randomUUID().toString()
        val ride = Ride(
            rideId = rideId,
            userId = userId,
            startTime = System.currentTimeMillis()
        )
        rideDao.insertRide(ride)
        return rideId
    }
    
    suspend fun addSafetyEvent(rideId: String, eventType: String): SafetyEventResult {
        val ride = rideDao.getRide(rideId) ?: return SafetyEventResult.RideNotFound
        
        val scoreChanges = mapOf(
            // 보너스 액션
            "perfect_stop" to 10,
            "safe_zone_slow" to 5,
            "good_parking" to 15,
            "speed_limit" to 5,
            "smooth_brake" to 3,
            "signal_obey" to 10,
            
            // 패널티 액션
            "zone_speeding" to -15,
            "harsh_decel" to -10,
            "harsh_accel" to -10,
            "phone_usage" to -20,
            "illegal_parking" to -25,
            "speeding" to -15,
            "harsh_brake" to -10
        )
        
        val eventNames = mapOf(
            "perfect_stop" to "완벽한 정지",
            "safe_zone_slow" to "안전구간 서행",
            "good_parking" to "추천 주차구역 이용",
            "speed_limit" to "속도 준수",
            "smooth_brake" to "부드러운 제동",
            "signal_obey" to "신호 준수",
            "zone_speeding" to "Zone 과속",
            "harsh_decel" to "급감속",
            "harsh_accel" to "급출발",
            "phone_usage" to "주행 중 폰 사용",
            "illegal_parking" to "불법 주차",
            "speeding" to "과속",
            "harsh_brake" to "급제동"
        )
        
        val scoreChange = scoreChanges[eventType] ?: 0
        val newScore = (ride.safetyScore + scoreChange).coerceIn(0, 120)
        
        val safetyEvent = SafetyEvent(
            type = eventType,
            scoreChange = scoreChange,
            description = eventNames[eventType] ?: eventType,
            timestamp = System.currentTimeMillis()
        )
        
        val updatedRide = ride.copy(
            safetyScore = newScore,
            events = ride.events + safetyEvent
        )
        
        rideDao.updateRide(updatedRide)
        
        return SafetyEventResult.Success(newScore, scoreChange, safetyEvent.description)
    }
    
    suspend fun endRide(rideId: String): RidingReport? {
        val ride = rideDao.getRide(rideId) ?: return null
        val user = userDao.getUserOnce(ride.userId) ?: return null
        
        // 가상 주행 데이터 생성
        val distance = (1.0..5.0).random()
        val avgSpeed = (12.0..25.0).random()
        
        // 포인트 계산
        val safetyScore = ride.safetyScore
        val scorePoints = calculateScorePoints(safetyScore)
        val actionBonus = ride.events.filter { it.scoreChange > 0 }.size * 2
        val distanceMultiplier = distance * 0.5
        
        // 연속 안전운전 콤보
        val newConsecutiveRides = if (safetyScore >= 80) {
            user.consecutiveSafeRides + 1
        } else {
            0
        }
        
        val comboMultiplier = 1.0 + (newConsecutiveRides * 0.05)
        val totalPoints = ((scorePoints * distanceMultiplier + actionBonus) * comboMultiplier).toInt()
        
        // 사용자 정보 업데이트
        val updatedUser = user.copy(
            totalPoints = user.totalPoints + totalPoints,
            consecutiveSafeRides = newConsecutiveRides,
            totalDistance = user.totalDistance + distance,
            ridesHistory = user.ridesHistory + rideId
        )
        userDao.updateUser(updatedUser)
        
        // 라이드 완료
        val completedRide = ride.copy(
            endTime = System.currentTimeMillis(),
            status = RideStatus.COMPLETED,
            distance = distance,
            avgSpeed = avgSpeed
        )
        rideDao.updateRide(completedRide)
        
        return RidingReport(
            distanceKm = distance,
            avgSpeed = avgSpeed,
            safetyScore = safetyScore,
            scorePoints = scorePoints,
            actionBonus = actionBonus,
            distanceMultiplier = distanceMultiplier,
            comboMultiplier = comboMultiplier,
            totalPointsEarned = totalPoints,
            consecutiveRides = newConsecutiveRides,
            safetyActions = ride.events.filter { it.scoreChange > 0 },
            penaltyActions = ride.events.filter { it.scoreChange < 0 }
        )
    }
    
    private fun calculateScorePoints(safetyScore: Int): Int {
        if (safetyScore < 40) return 0
        
        val normalizedScore = minOf(safetyScore, 100) / 100.0
        val bonusScore = maxOf(0, safetyScore - 100) / 20.0
        
        return ((normalizedScore.pow(2) + bonusScore) * 0.2 * 100).toInt()
    }
    
    suspend fun purchaseCoupon(userId: String, couponId: String): PurchaseResult {
        val user = userDao.getUserOnce(userId) ?: return PurchaseResult.UserNotFound
        val coupon = AVAILABLE_COUPONS.find { it.id == couponId } ?: return PurchaseResult.CouponNotFound
        
        if (user.totalPoints < coupon.cost) {
            return PurchaseResult.InsufficientPoints
        }
        
        val purchasedCoupon = PurchasedCoupon(
            couponId = coupon.id,
            couponName = coupon.name,
            cost = coupon.cost,
            purchaseDate = System.currentTimeMillis()
        )
        
        val updatedUser = user.copy(
            totalPoints = user.totalPoints - coupon.cost,
            purchasedCoupons = user.purchasedCoupons + purchasedCoupon
        )
        
        userDao.updateUser(updatedUser)
        return PurchaseResult.Success(purchasedCoupon)
    }
}

sealed class SafetyEventResult {
    object RideNotFound : SafetyEventResult()
    data class Success(val currentScore: Int, val scoreChange: Int, val description: String) : SafetyEventResult()
}

sealed class PurchaseResult {
    object UserNotFound : PurchaseResult()
    object CouponNotFound : PurchaseResult()
    object InsufficientPoints : PurchaseResult()
    data class Success(val purchase: PurchasedCoupon) : PurchaseResult()
}
