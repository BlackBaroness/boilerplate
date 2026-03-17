package io.github.blackbaroness.boilerplate

import io.github.blackbaroness.durationserializer.DurationSerializer
import io.github.blackbaroness.durationserializer.format.DurationFormat
import java.time.Duration
import java.time.temporal.ChronoUnit

val Duration.asMinecraftTicks
    get() = toMillis() / 50

fun Duration.format(format: DurationFormat) =
    DurationSerializer.serialize(this, format)

fun Duration.truncate(unit: ChronoUnit, avoidZero: Boolean = true): Duration {
    val duration = truncatedTo(unit)
    if (avoidZero && duration < unit.duration) {
        return duration.plus(unit.duration)
    }

    return duration
}
