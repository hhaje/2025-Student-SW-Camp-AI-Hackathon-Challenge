package com.cashride.app.data

import kotlin.math.min
import kotlin.math.max

/**
 * 사용자 레벨
 */
enum class UserLevel(val displayName: String, val minPoints: Int, val maxPoints: Int, val bonus: Double) {
    NEWBIE("새내기 라이더", 0, 99, 1.0),
    SAFE("안전 라이더", 100, 299, 1.1),
    VETERAN("베테랑 라이더", 300, 699, 1.2),
    MASTER("마스터 라이더", 700, 1499, 1.3),
    LEGEND("레전드 라이더", 1500, Int.MAX_VALUE, 1.5);
    
    companion object {
        fun fromPoints(points: Int): UserLevel {
            return values().lastOrNull { points >= it.minPoints } ?: NEWBIE
        }
    }
}

/**
 * 배지 타입
 */
enum class BadgeType(val displayName: String, val emoji: String, val requirement: String) {
    // 연속 안전운행 배지
    SAFE_STREAK_5("안전왕 5연속", "🔥", "5회 연속 80점 이상"),
    SAFE_STREAK_10("안전왕 10연속", "⭐", "10회 연속 80점 이상"),
    SAFE_STREAK_20("안전왕 20연속", "👑", "20회 연속 80점 이상"),
    
    // 포인트 배지
    POINT_MASTER("포인트 마스터", "💎", "총 100포인트 달성"),
    POINT_KING("포인트 킹", "🚀", "총 500포인트 달성"),
    POINT_LEGEND("포인트 레전드", "🌟", "총 1000포인트 달성"),
    
    // 특별 행동 배지
    STOP_MASTER("정지선 마스터", "🛑", "완벽한 정지 10회"),
    SAFE_ZONE_GUARDIAN("안전구간 수호자", "🐌", "안전 Zone 서행 20회"),
    PARKING_EXPERT("주차 달인", "🅿️", "추천 주차구역 이용 15회")
}

/**
 * 사용자 프로필
 */
data class UserProfile(
    val userId: String = "user_001",
    var totalPoints: Int = 0,
    var consecutiveSafeRides: Int = 0,
    var totalRides: Int = 0,
    var totalDistance: Double = 0.0,
    val badges: MutableSet<BadgeType> = mutableSetOf(),
    val ridingHistory: MutableList<RidingSession> = mutableListOf(),
    
    // 특별 행동 카운터
    var perfectStops: Int = 0,
    var safeZoneSlows: Int = 0,
    var recommendedParkings: Int = 0
) {
    /**
     * 사용자 레벨 계산
     */
    fun getUserLevel(): UserLevel {
        return UserLevel.fromPoints(totalPoints)
    }
    
    /**
     * 다음 레벨까지 필요한 포인트
     */
    fun getPointsToNextLevel(): Int {
        val currentLevel = getUserLevel()
        val nextLevel = UserLevel.values().find { it.minPoints > currentLevel.minPoints }
        return nextLevel?.let { it.minPoints - totalPoints } ?: 0
    }
    
    /**
     * 안전 포인트 계산 및 적립
     */
    fun addSafetyPoints(session: RidingSession): Int {
        val safetyScore = session.calculateSafetyScore()
        
        // 40점 미만은 포인트 지급 없음
        if (safetyScore < 40) return 0
        
        // 포인트 계산 공식
        val normalizedScore = min(safetyScore, 100) / 100.0
        val bonusScore = max(0, safetyScore - 100) / 20.0
        val basePoints = (normalizedScore * normalizedScore + bonusScore) * 0.2 * 100
        val distancePoints = basePoints * session.distance * 0.5
        val actionBonus = session.getPositiveActionCount() * 2
        
        // 콤보 배율 (80점 이상일 때만 콤보 유지)
        val comboMultiplier = if (safetyScore >= 80) {
            min(2.0, 1.0 + (consecutiveSafeRides * 0.05))
        } else {
            consecutiveSafeRides = 0 // 콤보 초기화
            1.0
        }
        
        // 레벨 보너스
        val levelBonus = getUserLevel().bonus
        
        val finalPoints = ((distancePoints + actionBonus) * comboMultiplier * levelBonus).toInt()
        
        // 포인트 적립
        totalPoints += finalPoints
        
        // 연속 안전운행 업데이트
        if (safetyScore >= 80) {
            consecutiveSafeRides++
        }
        
        // 특별 행동 카운터 업데이트
        updateActionCounters(session)
        
        // 배지 체크
        checkAndAwardBadges()
        
        // 통계 업데이트
        totalRides++
        totalDistance += session.distance
        ridingHistory.add(session)
        
        return finalPoints
    }
    
    private fun updateActionCounters(session: RidingSession) {
        session.actions.forEach { action ->
            when (action.type) {
                SafetyActionType.PERFECT_STOP -> perfectStops++
                SafetyActionType.SAFE_ZONE_SLOW -> safeZoneSlows++
                SafetyActionType.RECOMMENDED_PARKING -> recommendedParkings++
                else -> {}
            }
        }
    }
    
    private fun checkAndAwardBadges() {
        // 연속 안전운행 배지
        if (consecutiveSafeRides >= 20) badges.add(BadgeType.SAFE_STREAK_20)
        if (consecutiveSafeRides >= 10) badges.add(BadgeType.SAFE_STREAK_10)
        if (consecutiveSafeRides >= 5) badges.add(BadgeType.SAFE_STREAK_5)
        
        // 포인트 배지
        if (totalPoints >= 1000) badges.add(BadgeType.POINT_LEGEND)
        if (totalPoints >= 500) badges.add(BadgeType.POINT_KING)
        if (totalPoints >= 100) badges.add(BadgeType.POINT_MASTER)
        
        // 특별 행동 배지
        if (perfectStops >= 10) badges.add(BadgeType.STOP_MASTER)
        if (safeZoneSlows >= 20) badges.add(BadgeType.SAFE_ZONE_GUARDIAN)
        if (recommendedParkings >= 15) badges.add(BadgeType.PARKING_EXPERT)
    }
}
