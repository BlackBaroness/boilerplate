package io.github.blackbaroness.boilerplate.adventure

import io.github.blackbaroness.boilerplate.*
import io.github.blackbaroness.durationserializer.DurationFormats
import io.github.blackbaroness.durationserializer.format.DurationFormat
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.text.DecimalFormat
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAccessor

fun Boilerplate.tagResolver(name: String, value: String): TagResolver =
    Placeholder.unparsed(name, value)

fun Boilerplate.tagResolver(name: String, value: ComponentLike): TagResolver =
    Placeholder.component(name, value)

fun Boilerplate.tagResolver(name: String, value: Char): TagResolver =
    tagResolver(name, value.toString())

fun Boilerplate.tagResolver(
    name: String,
    value: Number,
    format: DecimalFormat = defaultDecimalFormat.get(),
): TagResolver = tagResolver(name, format.format(value))

fun Boilerplate.tagResolver(
    name: String,
    value: Duration,
    accuracy: ChronoUnit = ChronoUnit.SECONDS,
    format: DurationFormat = DurationFormats.mediumLengthRussian(),
): TagResolver = tagResolver(name, value.truncate(accuracy).format(format))

fun Boilerplate.tagResolver(
    name: String,
    value: TemporalAccessor,
    nice: Boolean = true,
): TagResolver = tagResolver(name, value, if (nice) Boilerplate.niceDateFormatter else Boilerplate.shortDateFormatter)

fun Boilerplate.tagResolver(
    name: String,
    value: TemporalAccessor,
    formatter: DateTimeFormatter,
): TagResolver = tagResolver(name, formatter.format(value))
