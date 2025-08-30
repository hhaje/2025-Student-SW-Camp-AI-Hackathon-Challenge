package com.cashride.app.data

import kotlin.math.max
import kotlin.math.min

/**
 * 라이딩 세션 데이터
 */
data class RidingSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var distance: Double = 0.0, // km
    var averageSpeed: Double = 0.0, // km/h
    val actions: MutableList<SafetyAction> = mutableListOf(),
    var isActive: Boolean = true
) {
    /**
     * 안전 점수 계산
     * 공식: 안전 점수 = 100 - 패널티 + 보너스
     */
    fun calculateSafetyScore(): Int {
        val totalScoreChange = actions.sumOf { it.scoreChange }
        val safetyScore = 100 + totalScoreChange
        return max(0, min(120, safetyScore))
    }
    
    /**
     * 긍정적 행동 수 계산
     */
    fun getPositiveActionCount(): Int {
        return actions.count { it.scoreChange > 0 }
    }
    
    /**
     * 부정적 행동 수 계산
     */
    fun getNegativeActionCount(): Int {
        return actions.count { it.scoreChange < 0 }
    }
    
    /**
     * 운행 시간 계산 (분)
     */
    fun getDurationMinutes(): Long {
        val end = endTime ?: System.currentTimeMillis()
        return (end - startTime) / (1000 * 60)
    }
}
