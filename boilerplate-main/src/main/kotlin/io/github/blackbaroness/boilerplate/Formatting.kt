package io.github.blackbaroness.boilerplate

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("ObjectPropertyName")
internal val _russianLocale: Locale = Locale.forLanguageTag("ru-RU")
val Boilerplate.russianLocale get() = _russianLocale

@Suppress("ObjectPropertyName")
internal val _defaultDecimalFormat: ThreadLocal<DecimalFormat> = ThreadLocal.withInitial {
    val symbols = DecimalFormatSymbols(_russianLocale)
    symbols.decimalSeparator = ','
    symbols.groupingSeparator = '.'
    DecimalFormat("#,###.##", symbols)
}
val Boilerplate.defaultDecimalFormat get() = _defaultDecimalFormat

@Suppress("ObjectPropertyName")
internal val _niceDateFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("HH:mm z (d MMMM yyyy)", _russianLocale)
    .withLocale(_russianLocale)
    .withZone(ZoneId.systemDefault())
val Boilerplate.niceDateFormatter get() = _niceDateFormatter

@Suppress("ObjectPropertyName")
internal val _shortDateFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("HH:mm d.MM.yyyy z", _russianLocale)
    .withLocale(_russianLocale)
    .withZone(ZoneId.systemDefault())
val Boilerplate.shortDateFormatter get() = _shortDateFormatter
