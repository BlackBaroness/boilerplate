package io.github.blackbaroness.boilerplate

import com.google.common.hash.Funnel
import com.google.common.hash.Hashing

@Suppress("UnstableApiUsage")
inline fun <T, E> Collection<T>.requireUniquenessByHash(funnel: Funnel<E>, action: (T) -> E) {
    val hashFunction = Hashing.murmur3_128()
    val discovered = HashSet<String>()
    for (element in this) {
        val key = action.invoke(element)
        val hash = hashFunction.hashObject(key, funnel).toString()
        if (!discovered.add(hash)) {
            error("Duplicate entry found: $element (by key: $key)")
        }
    }
}
