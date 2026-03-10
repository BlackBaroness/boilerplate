package io.github.blackbaroness.boilerplate

fun <T> Sequence<T>.cycle() = sequence {
    while (true) yieldAll(this@cycle)
}

fun <KEY, VALUE> Sequence<Map.Entry<KEY, Iterable<VALUE>>>.flattenValues(): Sequence<Pair<KEY, VALUE>> {
    return flatMap { (key, value) -> value.map { key to it } }
}
