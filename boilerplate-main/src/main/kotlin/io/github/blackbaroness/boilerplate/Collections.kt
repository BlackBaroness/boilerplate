package io.github.blackbaroness.boilerplate

fun <T> Collection<T>.containsAny(of: Collection<T>): Boolean {
    for (element in this) {
        if (element in of) {
            return true
        }
    }
    return false
}

fun <K, V, R> Map<K, V?>.mapNotNullValues(transform: (K, V) -> R): Map<K, R> {
    return this
        .filterValues { it != null }
        .mapValues { transform(it.key, it.value!!) }
}

inline fun <KEY, OLD_VALUE, NEW_VALUE> Map<KEY, OLD_VALUE>.mapValuesFast(transform: (Map.Entry<KEY, OLD_VALUE>) -> NEW_VALUE): MutableMap<KEY, NEW_VALUE> {
    val result = HashMap<KEY, NEW_VALUE>(size)
    for (entry in this) {
        val transformed = transform.invoke(entry)
        result[entry.key] = transformed
    }
    return result
}

inline fun <ELEMENT, KEY, VALUE> Collection<ELEMENT>.fastToMap(
    map: MutableMap<KEY, VALUE> = HashMap(size),
    key: (ELEMENT) -> KEY,
    value: (ELEMENT) -> VALUE,
): Map<KEY, VALUE> {
    for (element in this) {
        map[key(element)] = value(element)
    }
    return map
}

fun <T> Collection<T>.joinToStringWithSmartSeparators(
    normalSeparator: CharSequence,
    lastSeparator: CharSequence,
    toString: (T) -> String = { it.toString() },
): String {
    if (isEmpty())
        return ""

    if (size == 1)
        return toString(first())

    val collection = this
    val lastIndex = collection.size - 1
    return buildString {
        collection.forEachIndexed { index, element ->
            // append the element (everything before it should be appended with the previous element)
            append(toString(element))

            if (lastIndex == index) {
                // this element is the last
                return@forEachIndexed
            }

            if (lastIndex - index == 1) {
                // the next element is the last
                append(lastSeparator)
                return@forEachIndexed
            }

            // the next element is not the last
            append(normalSeparator)
        }
    }
}

inline fun <T, reified R> List<T>.mapToArray(transform: (T) -> R): Array<R> {
    return Array(size) { index -> transform.invoke(this[index]) }
}
