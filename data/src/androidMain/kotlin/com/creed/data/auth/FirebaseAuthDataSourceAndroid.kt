package com.creed.data.auth

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

/**
 * Android implementation of Firebase authentication using the working code from composeApp
 */
class FirebaseAuthDataSourceAndroid(val context: Context) : FirebaseAuthDataSource, ActivityManager {

    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var currentActivity: Activity? = null

    // Implementation of ActivityManager interface
    override fun setCurrentActivity(activity: Activity?) {
        currentActivity = activity
    }

    override suspend fun signInWithPhoneNumber(
        phone: String,
        onCodeSent: () -> Unit,
        onVerificationCompleted: (String) -> Unit,
        onError: (String) -> Unit
    ) = suspendCancellableCoroutine { continuation ->
        val activity = context as Activity
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

    override suspend fun verifyCode(
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
}
