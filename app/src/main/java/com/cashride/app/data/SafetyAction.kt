package com.cashride.app.data

/**
 * 안전운전 액션 타입
 */
enum class SafetyActionType {
    // 패널티 액션
    ZONE_SPEEDING,      // Zone 과속 (-15점)
    SUDDEN_BRAKE,       // 급감속 (-10점)
    SUDDEN_START,       // 급출발 (-10점)
    PHONE_USAGE,        // 주행 중 폰 사용 (-20점)
    ILLEGAL_PARKING,    // 불법 주차 (-25점)
    
    // 보너스 액션
    PERFECT_STOP,       // 완벽한 정지 (+10점)
    SAFE_ZONE_SLOW,     // 안전 Zone 서행 (+5점)
    RECOMMENDED_PARKING // 추천 주차구역 이용 (+15점)
}

/**
 * 안전운전 액션 데이터
 */
data class SafetyAction(
    val type: SafetyActionType,
    val description: String,
    val scoreChange: Int,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 안전운전 액션 정의
 */
object SafetyActions {
    val actions = mapOf(
        SafetyActionType.ZONE_SPEEDING to SafetyAction(
            SafetyActionType.ZONE_SPEEDING,
            "어린이/노인 보호구역에서 과속",
            -15
        ),
        SafetyActionType.SUDDEN_BRAKE to SafetyAction(
            SafetyActionType.SUDDEN_BRAKE,
            "급감속 감지",
            -10
        ),
        SafetyActionType.SUDDEN_START to SafetyAction(
            SafetyActionType.SUDDEN_START,
            "급출발 감지",
            -10
        ),
        SafetyActionType.PHONE_USAGE to SafetyAction(
            SafetyActionType.PHONE_USAGE,
            "주행 중 폰 사용",
            -20
        ),
        SafetyActionType.ILLEGAL_PARKING to SafetyAction(
            SafetyActionType.ILLEGAL_PARKING,
            "불법 주차",
            -25
        ),
        SafetyActionType.PERFECT_STOP to SafetyAction(
            SafetyActionType.PERFECT_STOP,
            "완벽한 정지",
            10
        ),
        SafetyActionType.SAFE_ZONE_SLOW to SafetyAction(
            SafetyActionType.SAFE_ZONE_SLOW,
            "안전 Zone 서행",
            5
        ),
        SafetyActionType.RECOMMENDED_PARKING to SafetyAction(
            SafetyActionType.RECOMMENDED_PARKING,
            "추천 주차구역 이용",
            15
        )
    )
}
