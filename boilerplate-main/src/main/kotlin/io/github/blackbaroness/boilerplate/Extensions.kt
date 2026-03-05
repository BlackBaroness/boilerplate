package io.github.blackbaroness.boilerplate

import java.util.*

inline fun <reified T> isClassPresent() =
    runCatching { Class.forName(T::class.qualifiedName) }.isSuccess

fun <T> Collection<T>.containsAny(of: Collection<T>): Boolean {
    val set = of.toSet()
    for (element in this) {
        if (set.contains(element)) {
            return true
        }
    }
    return false
}

fun <T> Sequence<T>.cycle() = sequence {
    while (true) yieldAll(this@cycle)
}

fun Iterable<String>.bulkReplace(what: String, with: String): List<String> {
    return this.map { it.replace(what, with) }
}

fun Iterable<String>.insertReplacing(what: String, with: Iterable<String>): List<String> = buildList {
    for (originalString in this@insertReplacing) {
        if (what in originalString) {
            addAll(with)
        } else {
            add(originalString)
        }
    }
}

fun <KEY, VALUE> Sequence<Map.Entry<KEY, Iterable<VALUE>>>.flattenValues(): Sequence<Pair<KEY, VALUE>> {
    return flatMap { (key, value) -> value.map { key to it } }
}

fun Throwable.rootCause(): Throwable {
    var cause = this.cause ?: return this

    while (true) {
        cause = cause.cause ?: return cause
    }
}

inline fun <T> Iterable<T>.forEachOther(action: (T, T) -> Unit) =
    forEach { t1 -> forEach { t2 -> action(t1, t2) } }

fun <K, V, R> Map<K, V?>.mapNotNullValues(transform: (K, V) -> R): Map<K, R> {
    return this
        .filterValues { it != null }
        .mapValues { transform(it.key, it.value!!) }
}

fun String?.toUuidOrNull(): UUID? {
    if (this == null) return null

    return try {
        UUID.fromString(this)
    } catch (_: IllegalArgumentException) {
        null
    }
}

val ipv4AddressRegex by lazy {
    Regex("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}\$")
}

val String.isValidIpv4Address: Boolean
    get() = matches(ipv4AddressRegex)


inline fun repeat(range: IntRange, action: (Int) -> Unit) {
    val times = range.random()
    repeat(times, action)
}
