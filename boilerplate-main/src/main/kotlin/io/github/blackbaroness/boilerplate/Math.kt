package io.github.blackbaroness.boilerplate

import java.time.Instant
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun calculateEstimatedTimeOfArrival(startDate: Instant, elementsDone: Long, elementsTotal: Long): Duration? {
    if (elementsDone == 0L) return null

    val millisPassed = Instant.now().toEpochMilli() - startDate.toEpochMilli()
    val millisPerElement = millisPassed.toDouble() / elementsDone

    val elementsLeft = elementsTotal - elementsDone
    val millisLeft = (millisPerElement * elementsLeft).roundToLong()

    return millisLeft.milliseconds
}

fun calculatePercent(current: Long, max: Long): Double {
    require(max > 0) { "Max value must be greater than 0" }
    val percent = current.toDouble() / max.toDouble() * 100
    require(percent in 0.0..100.0) { "Percent must be in range [0..100], got $percent" }
    return percent
}
