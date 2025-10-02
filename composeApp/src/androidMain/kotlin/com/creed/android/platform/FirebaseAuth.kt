/*
 * ProGuard rules for Firebase (add to proguard-rules.pro):
 * -keep class com.google.firebase.** { *; }
 * -keep class com.google.android.gms.** { *; }
 * -dontwarn com.google.firebase.**
 * -dontwarn com.google.android.gms.**
 *
 * Gradle dependencies (add to composeApp/build.gradle.kts):
 * implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
 * implementation("com.google.firebase:firebase-auth")
 * implementation("com.google.firebase:firebase-crashlytics")
 * implementation("com.google.firebase:firebase-perf")
 * implementation("com.google.firebase:firebase-analytics")
 */

package com.creed.android.platform

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

private var storedVerificationId: String? = null
private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
private var currentActivity: Activity? = null

// Function to set the current activity - call this from MainActivity
fun setCurrentActivity(activity: Activity?) {
    currentActivity = activity
}

/** Native Firebase Phone Authentication for Android */
actual suspend fun signInWithPhoneNumber(
    phone: String,
    onCodeSent: () -> Unit,
    onVerificationCompleted: (String) -> Unit,
    onError: (String) -> Unit
) = suspendCancellableCoroutine { continuation ->
    val activity = currentActivity
    if (activity == null) {
        onError("Activity context not available")
        continuation.resume(Unit)
        return@suspendCancellableCoroutine
    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-verification completed
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                onVerificationCompleted(tokenTask.result?.token ?: "")
                            } else {
                                onError("Failed to get ID token: ${tokenTask.exception?.message}")
                            }
                        }
                    } else {
                        onError("Sign in failed: ${task.exception?.message}")
                    }
                    continuation.resume(Unit)
                }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            onError("Verification failed: ${e.message}")
            continuation.resume(Unit)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            resendToken = token
            onCodeSent()
            continuation.resume(Unit)
        }
    }

    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
        .setPhoneNumber(phone)
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(activity)
        .setCallbacks(callbacks)
        .build()

    PhoneAuthProvider.verifyPhoneNumber(options)
}

/** Native Firebase OTP Verification for Android */
actual suspend fun verifyCode(
    code: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) = suspendCancellableCoroutine { continuation ->
    val verificationId = storedVerificationId
    if (verificationId == null) {
        onError("No verification ID available")
        continuation.resume(Unit)
        return@suspendCancellableCoroutine
    }

    val credential = PhoneAuthProvider.getCredential(verificationId, code)
    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.user?.getIdToken(false)?.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        onSuccess(tokenTask.result?.token ?: "")
                    } else {
                        onError("Failed to get ID token: ${tokenTask.exception?.message}")
                    }
                }
            } else {
                onError("Verification failed: ${task.exception?.message}")
            }
            continuation.resume(Unit)
        }
}

/** Native Firebase Analytics logging for Android */
actual fun logAnalyticsEvent(name: String, params: Map<String, Any>) {
    val activity = currentActivity ?: return
    val analytics = FirebaseAnalytics.getInstance(activity)
    val bundle = Bundle().apply {
        params.forEach { (key, value) ->
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Double -> putDouble(key, value)
                is Boolean -> putBoolean(key, value)
                else -> putString(key, value.toString())
            }
        }
    }
    analytics.logEvent(name, bundle)
}

/** Native Firebase Crashlytics exception recording for Android */
actual fun recordCrashlyticsException(throwable: Throwable) {
    FirebaseCrashlytics.getInstance().recordException(throwable)
}

/** Native Firebase Performance trace for Android */
actual fun startPerformanceTrace(name: String): PlatformTrace {
    val trace = FirebasePerformance.getInstance().newTrace(name)
    trace.start()
    return AndroidPlatformTrace(trace)
}

/** Native Firebase Crashlytics user ID setting for Android */
actual fun setCrashlyticsUserId(id: String) {
    FirebaseCrashlytics.getInstance().setUserId(id)
}

/** Android implementation of PlatformTrace */
actual open class PlatformTrace(private val trace: Trace) {
    actual fun stop() {
        trace.stop()
    }
}

class AndroidPlatformTrace(private val trace: Trace) : PlatformTrace(trace)

// Helper function to get current activity context
private fun getCurrentActivity(): Activity? {
    return currentActivity
}

/** Composable helper to get current activity */
@Composable
fun getActivity(): Activity? {
    return LocalActivity.current
}
