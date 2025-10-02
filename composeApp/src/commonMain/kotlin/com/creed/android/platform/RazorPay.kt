package com.creed.android.platform

/**
 * Expected declarations for RazorPay payment integration
 * Implemented natively on each platform
 */
expect fun launchRazorPay(
    activity: Any, // Activity on Android, UIViewController on iOS
    orderId: String,
    amountCents: Int,
    onSuccess: (String) -> Unit,
    onError: (Int, String) -> Unit
)
