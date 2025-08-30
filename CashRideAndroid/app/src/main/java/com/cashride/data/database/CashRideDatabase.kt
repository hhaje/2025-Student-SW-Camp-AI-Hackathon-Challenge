package com.cashride.data.database

import androidx.room.*
import com.cashride.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(
    entities = [User::class, Ride::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CashRideDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun rideDao(): RideDao
}

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun fromCouponList(value: List<Coupon>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toCouponList(value: String): List<Coupon> {
        return Gson().fromJson(value, object : TypeToken<List<Coupon>>() {}.type)
    }

    @TypeConverter
    fun fromSafetyEventList(value: List<SafetyEvent>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toSafetyEventList(value: String): List<SafetyEvent> {
        return Gson().fromJson(value, object : TypeToken<List<SafetyEvent>>() {}.type)
    }
}
