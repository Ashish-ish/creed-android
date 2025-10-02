package com.creed.ios.platform

actual fun launchRazorPay(
    activity: Any,
    orderId: String,
    amountCents: Int,
    onSuccess: (paymentId: String) -> Unit,
    onError: (code: Int, message: String) -> Unit
) {
    // Copilot: Integrate RazorPay KMP SDK for iOS
    onSuccess("paymentId123") // Placeholder
}

