package com.cashride.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String = "demo_user",
    val totalPoints: Int = 0,
    val consecutiveSafeRides: Int = 0,
    val totalDistance: Double = 0.0,
    val ridesHistory: List<String> = emptyList(),
    val badges: List<String> = emptyList(),
    val purchasedCoupons: List<Coupon> = emptyList(),
    val joinDate: Long = System.currentTimeMillis()
) : Parcelable

@Parcelize
data class UserLevel(
    val name: String,
    val minPoints: Int,
    val maxPoints: Int,
    val icon: String,
    val benefits: List<String>
) : Parcelable

val USER_LEVELS = listOf(
    UserLevel("새내기 라이더", 0, 99, "🐣", listOf("기본 포인트 적립")),
    UserLevel("안전 라이더", 100, 299, "🚀", listOf("포인트 적립률 +10%", "월 1회 무료 쿠폰")),
    UserLevel("베테랑 라이더", 300, 699, "⭐", listOf("포인트 적립률 +20%", "월 2회 무료 쿠폰")),
    UserLevel("마스터 라이더", 700, 1499, "🏆", listOf("포인트 적립률 +30%", "주간 특별 쿠폰")),
    UserLevel("레전드 라이더", 1500, Int.MAX_VALUE, "👑", listOf("포인트 적립률 +50%", "전용 고객 서비스"))
)

fun getUserLevel(totalPoints: Int): UserLevel {
    return USER_LEVELS.find { totalPoints in it.minPoints..it.maxPoints } ?: USER_LEVELS.last()
}
