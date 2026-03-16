package io.github.blackbaroness.boilerplate

inline fun <T, reified R> Array<T>.mapToArray(transform: (T) -> R): Array<R> {
    return Array(size) { index -> transform.invoke(this[index]) }
}
