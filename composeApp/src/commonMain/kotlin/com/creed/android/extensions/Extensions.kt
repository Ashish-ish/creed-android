package com.creed.android.extensions

import kotlin.math.pow
import kotlin.math.round

fun Double.toFixed(decimals: Int): String {
    val multiplier = 10.0.pow(decimals)
    val rounded = round(this * multiplier) / multiplier
    return rounded.toString()
}