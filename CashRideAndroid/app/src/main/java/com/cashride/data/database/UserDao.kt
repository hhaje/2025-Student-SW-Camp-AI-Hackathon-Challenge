package com.cashride.data.database

import androidx.room.*
import com.cashride.data.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUser(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserOnce(userId: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET totalPoints = :points WHERE userId = :userId")
    suspend fun updatePoints(userId: String, points: Int)

    @Query("UPDATE users SET consecutiveSafeRides = :consecutiveRides WHERE userId = :userId")
    suspend fun updateConsecutiveRides(userId: String, consecutiveRides: Int)

    @Query("UPDATE users SET totalDistance = :distance WHERE userId = :userId")
    suspend fun updateTotalDistance(userId: String, distance: Double)
}
