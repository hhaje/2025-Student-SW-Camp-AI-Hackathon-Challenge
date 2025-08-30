package com.cashride.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "rides")
data class Ride(
    @PrimaryKey
    val rideId: String,
    val userId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val safetyScore: Int = 100,
    val distance: Double = 0.0,
    val avgSpeed: Double = 0.0,
    val events: List<SafetyEvent> = emptyList(),
    val status: RideStatus = RideStatus.ACTIVE
) : Parcelable

@Parcelize
data class SafetyEvent(
    val type: String,
    val scoreChange: Int,
    val description: String,
    val timestamp: Long
) : Parcelable

enum class RideStatus {
    ACTIVE, COMPLETED, CANCELLED
}

@Parcelize
data class RidingReport(
    val distanceKm: Double,
    val avgSpeed: Double,
    val safetyScore: Int,
    val scorePoints: Int,
    val actionBonus: Int,
    val distanceMultiplier: Double,
    val comboMultiplier: Double,
    val totalPointsEarned: Int,
    val consecutiveRides: Int,
    val safetyActions: List<SafetyEvent>,
    val penaltyActions: List<SafetyEvent>
) : Parcelable
