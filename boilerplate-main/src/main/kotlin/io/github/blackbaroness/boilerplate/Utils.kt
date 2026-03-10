package io.github.blackbaroness.boilerplate

import java.util.*

fun String.toUuidOrNull(): UUID? = try {
    when (this.length) {
        36 -> UUID.fromString(this)
        32 -> StringBuilder(this)
            .insert(8, '-')
            .insert(13, '-')
            .insert(18, '-')
            .insert(23, '-')
            .toString()
            .let { UUID.fromString(it) }

        else -> null
    }
} catch (_: IllegalArgumentException) {
    null
}

inline fun <reified T> isClassPresent(): Boolean =
    runCatching { Class.forName(T::class.qualifiedName) }.isSuccess

inline fun repeat(range: IntRange, action: (Int) -> Unit) {
    repeat(range.random(), action)
}

inline fun repeat(times: Long, action: (Long) -> Unit) {
    for (index in 0 until times) {
        action(index)
    }
}
