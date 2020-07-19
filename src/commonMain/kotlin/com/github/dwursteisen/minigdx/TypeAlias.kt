package com.github.dwursteisen.minigdx

/**
 * Temporal unit
 */
typealias Seconds = Float

/**
 * Percent: values between 0.0 and 1.0
 */
typealias Percent = Number

/**
 * Bytemask: value can be changed using binary operation.
 */
typealias ByteMask = Int

typealias Degree = Number
typealias Coordinate = Number

typealias Pixel = Int

fun Number.toPercent(): Float {
    val v = this.toFloat()
    require(v in 0.0..1.0)
    return v
}
