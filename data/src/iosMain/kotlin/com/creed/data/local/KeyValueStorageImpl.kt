package com.creed.data.local

import platform.Foundation.NSUserDefaults

class IosKeyValueStorage : KeyValueStorage {
    private val defaults = NSUserDefaults.standardUserDefaults
    override suspend fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }
    override suspend fun getString(key: String): String? =
        defaults.stringForKey(key)
    override suspend fun clear() {
        // Copilot: Remove only app keys
        // For demo, remove all
        defaults.removePersistentDomainForName("creed_prefs")
    }
}

