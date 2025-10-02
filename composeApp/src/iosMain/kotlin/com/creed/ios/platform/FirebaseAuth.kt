/*
 * CocoaPods dependencies (add to iosApp/Podfile):
 * pod 'Firebase/Auth', '~> 10.27.0'
 * pod 'Firebase/Crashlytics', '~> 10.27.0'
 * pod 'Firebase/Performance', '~> 10.27.0'
 * pod 'Firebase/Analytics', '~> 10.27.0'
 *
 * Info.plist keys (add to iosApp/iosApp/Info.plist):
 * <key>CFBundleURLTypes</key>
 * <array>
 *   <dict>
 *     <key>CFBundleURLName</key>
 *     <string>REVERSED_CLIENT_ID from GoogleService-Info.plist</string>
 *     <key>CFBundleURLSchemes</key>
 *     <array>
 *       <string>REVERSED_CLIENT_ID value</string>
 *     </array>
 *   </dict>
 * </array>
 */

package com.creed.ios.platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.UIKit.UIViewController
import kotlin.coroutines.resume

// Import Firebase iOS framework bindings
@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

import kotlinx.cinterop.*
import cocoapods.FirebaseAuth.*
import cocoapods.FirebaseAnalytics.*
import cocoapods.FirebaseCrashlytics.*
import cocoapods.FirebasePerformance.*

private var storedVerificationId: String? = null

/** Native Firebase Phone Authentication for iOS */
actual suspend fun signInWithPhoneNumber(
    phone: String,
    onCodeSent: () -> Unit,
    onVerificationCompleted: (String) -> Unit,
    onError: (String) -> Unit
) = suspendCancellableCoroutine { continuation ->
    val auth = FIRAuth.auth()

    auth.signInWithPhoneNumber(phone, completion = { verificationId, error ->
        if (error != null) {
            onError("Phone auth failed: ${error.localizedDescription}")
            continuation.resume(Unit)
            return@signInWithPhoneNumber
        }

        if (verificationId != null) {
            storedVerificationId = verificationId
            onCodeSent()
            continuation.resume(Unit)
        } else {
            onError("Failed to get verification ID")
            continuation.resume(Unit)
        }
    })
}

/** Native Firebase OTP Verification for iOS */
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

    val credential = FIRPhoneAuthProvider.provider().credentialWithVerificationID(verificationId, verificationCode = code)
    val auth = FIRAuth.auth()

    auth.signInWithCredential(credential) { result, error ->
        if (error != null) {
            onError("Code verification failed: ${error.localizedDescription}")
            continuation.resume(Unit)
            return@signInWithCredential
        }

        result?.user?.getIDTokenWithCompletion { token, tokenError ->
            if (tokenError != null) {
                onError("Failed to get ID token: ${tokenError.localizedDescription}")
            } else if (token != null) {
                onSuccess(token)
            } else {
                onError("Failed to get ID token")
            }
            continuation.resume(Unit)
        }
    }
}

/** Native Firebase Analytics logging for iOS */
actual fun logAnalyticsEvent(name: String, params: Map<String, Any>) {
    val parameters = params.mapValues { (_, value) ->
        when (value) {
            is String -> value as NSString
            is Number -> value
            is Boolean -> value
            else -> value.toString() as NSString
        }
    }

    FIRAnalytics.logEventWithName(name, parameters = parameters)
}

/** Native Firebase Crashlytics exception recording for iOS */
actual fun recordCrashlyticsException(throwable: Throwable) {
    val crashlytics = FIRCrashlytics.crashlytics()
    val error = NSError.errorWithDomain(
        domain = "KotlinException",
        code = 0,
        userInfo = mapOf(
            "message" to (throwable.message ?: "Unknown error"),
            "stackTrace" to throwable.stackTraceToString()
        )
    )
    crashlytics.recordError(error)
}

/** Native Firebase Performance trace for iOS */
actual fun startPerformanceTrace(name: String): PlatformTrace {
    val trace = FIRPerformance.startTraceWithName(name)
    return IOSPlatformTrace(trace)
}

/** Native Firebase Crashlytics user ID setting for iOS */
actual fun setCrashlyticsUserId(id: String) {
    val crashlytics = FIRCrashlytics.crashlytics()
    crashlytics.setUserID(id)
}

/** iOS implementation of PlatformTrace */
actual class PlatformTrace(private val trace: FIRTrace) {
    actual fun stop() {
        trace.stop()
    }
}

class IOSPlatformTrace(private val trace: FIRTrace) : PlatformTrace(trace)

/** Helper function to get current UIViewController */
fun getCurrentViewController(): UIViewController? {
    // This should be implemented based on your navigation setup
    // You might need to use LocalUIViewController.current in Compose context
    return null
}
