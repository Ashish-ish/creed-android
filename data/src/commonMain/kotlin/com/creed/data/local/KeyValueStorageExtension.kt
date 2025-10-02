package com.creed.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

suspend inline fun <reified T> KeyValueStorage.putObject(key: String, value: T) {
    val json = Json.encodeToString(value)
    putString(key, json)
}

suspend inline fun <reified T> KeyValueStorage.getObject(key: String): T? {
    val json = getString(key) ?: return null
    return Json.decodeFromString(json)
}

inline fun <reified T> KeyValueStorage.getObjectValueUpdates(key: String): Flow<T?> {
    return getStringValueUpdates(key).map { json ->
        json?.let { Json.decodeFromString<T>(it) }
    }
}
