package com.cashride.data.database

import androidx.room.*
import com.cashride.data.models.Ride
import com.cashride.data.models.RideStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
    @Query("SELECT * FROM rides WHERE userId = :userId ORDER BY startTime DESC")
    fun getUserRides(userId: String): Flow<List<Ride>>

    @Query("SELECT * FROM rides WHERE rideId = :rideId")
    suspend fun getRide(rideId: String): Ride?

    @Query("SELECT * FROM rides WHERE userId = :userId AND status = :status LIMIT 1")
    suspend fun getActiveRide(userId: String, status: RideStatus = RideStatus.ACTIVE): Ride?

    @Insert
    suspend fun insertRide(ride: Ride)

    @Update
    suspend fun updateRide(ride: Ride)

    @Query("UPDATE rides SET safetyScore = :score WHERE rideId = :rideId")
    suspend fun updateSafetyScore(rideId: String, score: Int)

    @Query("UPDATE rides SET endTime = :endTime, status = :status WHERE rideId = :rideId")
    suspend fun endRide(rideId: String, endTime: Long, status: RideStatus)

    @Query("DELETE FROM rides WHERE rideId = :rideId")
    suspend fun deleteRide(rideId: String)
}
