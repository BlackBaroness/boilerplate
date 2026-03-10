package io.github.blackbaroness.boilerplate

fun Throwable.rootCause(): Throwable {
    var cause = this.cause ?: return this

    while (true) {
        cause = cause.cause ?: return cause
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Throwable.`throw`(): Nothing = throw this
