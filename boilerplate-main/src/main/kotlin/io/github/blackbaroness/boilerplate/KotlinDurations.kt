package io.github.blackbaroness.boilerplate

import io.github.blackbaroness.durationserializer.format.DurationFormat
import kotlin.time.Duration
import kotlin.time.toJavaDuration

val Duration.isZero: Boolean
    get() = this.inWholeMilliseconds == 0L

fun Duration.atLeast(duration: Duration): Duration {
    return if (this < duration) duration else this
}

fun Duration.format(format: DurationFormat) =
    toJavaDuration().format(format)

val Duration.asMinecraftTicks
    get() = inWholeMilliseconds / 50
