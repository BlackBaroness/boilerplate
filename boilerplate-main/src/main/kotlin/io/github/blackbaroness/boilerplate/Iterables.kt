package io.github.blackbaroness.boilerplate

import com.google.common.collect.Iterators

inline fun <T> Iterable<T>.forEachOther(action: (T, T) -> Unit) =
    forEach { t1 -> forEach { t2 -> action(t1, t2) } }


fun <T> Iterable<T>.cycle(): Iterator<T> {
    return Iterators.cycle(this)
}
