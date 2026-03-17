package io.github.blackbaroness.boilerplate

fun AutoCloseable.closeQuietly() {
    try {
        close()
    } catch (_: Throwable) {
    }
}

fun <CLOSEABLE : AutoCloseable, RESULT> CLOSEABLE.useQuietly(action: (CLOSEABLE) -> RESULT): RESULT {
    try {
        return action.invoke(this)
    } finally {
        closeQuietly()
    }
}
