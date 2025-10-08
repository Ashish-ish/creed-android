package com.creed.data.local

import kotlinx.coroutines.flow.Flow

interface KeyValueStorage {

    suspend fun putString(key: String, value: String)
    suspend fun putInt(key: String, value: Int)
    suspend fun putLong(key: String, value: Long)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun putDouble(key: String, value: Double)

    fun getStringValueUpdates(key: String, default: String? = null): Flow<String?>
    fun getIntValueUpdates(key: String, default: Int? = null): Flow<Int?>
    fun getLongValueUpdates(key: String, default: Long? = null): Flow<Long?>
    fun getBooleanValueUpdates(key: String, default: Boolean? = null): Flow<Boolean?>
    fun getDoubleValueUpdates(key: String, default: Double? = null): Flow<Double?>

    suspend fun getString(key: String, default: String? = null): String?
    suspend fun getInt(key: String, default: Int? = null): Int?
    suspend fun getLong(key: String, default: Long? = null): Long?
    suspend fun getBoolean(key: String, default: Boolean? = null): Boolean?
    suspend fun getDouble(key: String, default: Double? = null): Double?

    suspend fun remove(key: String)

}

object StorageKeys {
    const val FIREBASE_TOKEN = "FIREBASE_TOKEN"
    const val AUTH_TOKEN = "AUTH_TOKEN"
    const val REFRESH_TOKEN = "REFRESH_TOKEN"
    const val USER_ID = "USER_ID"
    const val USER_JSON = "USER_JSON"
    const val LOCATION_LAT = "LOCATION_LAT"
    const val LOCATION_LNG = "LOCATION_LNG"
    const val LOCATION = "LOCATION"
}
