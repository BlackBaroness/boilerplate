package io.github.blackbaroness.boilerplate

fun <T> MutableSet<T>.invertPresence(value: T): Boolean {
    repeat(5) {
        if (add(value)) return true
        if (remove(value)) return false
    }
    error("Couldn't invert presence in 5 tries")
}
