package io.github.blackbaroness.boilerplate

inline fun <reified T> T.toIterator(): Iterator<T> = iterator { yield(this@toIterator) }
