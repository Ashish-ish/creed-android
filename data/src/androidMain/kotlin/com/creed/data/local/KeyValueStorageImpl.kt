package com.creed.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AndroidKeyValueStorage(context: Context) : KeyValueStorage {

    private val Context.dataStore by preferencesDataStore(name = "app_prefs")
    private val dataStore = context.dataStore

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun putInt(key: String, value: Int) {
        dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    override suspend fun putLong(key: String, value: Long) {
        dataStore.edit { it[longPreferencesKey(key)] = value }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    override suspend fun putDouble(key: String, value: Double) {
        dataStore.edit { it[doublePreferencesKey(key)] = value }
    }

    override fun getStringValueUpdates(key: String, default: String?): Flow<String?> =
        dataStore.data.map { it[stringPreferencesKey(key)] ?: default }

    override fun getIntValueUpdates(key: String, default: Int?): Flow<Int?> =
        dataStore.data.map { it[intPreferencesKey(key)] ?: default }

    override fun getLongValueUpdates(key: String, default: Long?): Flow<Long?> =
        dataStore.data.map { it[longPreferencesKey(key)] ?: default }

    override fun getBooleanValueUpdates(key: String, default: Boolean?): Flow<Boolean?> =
        dataStore.data.map { it[booleanPreferencesKey(key)] ?: default }

    override fun getDoubleValueUpdates(key: String, default: Double?): Flow<Double?> =
        dataStore.data.map { it[doublePreferencesKey(key)] ?: default }

    override suspend fun getString(key: String, default: String?): String? =
        dataStore.data.map { it[stringPreferencesKey(key)] ?: default }.first()

    override suspend fun getInt(key: String, default: Int?): Int? =
        dataStore.data.map { it[intPreferencesKey(key)] ?: default }.first()

    override suspend fun getLong(key: String, default: Long?): Long? =
        dataStore.data.map { it[longPreferencesKey(key)] ?: default }.first()

    override suspend fun getBoolean(key: String, default: Boolean?): Boolean? =
        dataStore.data.map { it[booleanPreferencesKey(key)] ?: default }.first()

    override suspend fun getDouble(key: String, default: Double?): Double? =
        dataStore.data.map { it[doublePreferencesKey(key)] ?: default }.first()

    override suspend fun remove(key: String) {
        dataStore.edit { it.remove(stringPreferencesKey(key)) }
    }
}