package com.creed.data.auth

import android.app.Activity

/**
 * Interface for managing Android activity context for Firebase authentication
 */
interface ActivityManager {
    fun setCurrentActivity(activity: Activity?)
}
