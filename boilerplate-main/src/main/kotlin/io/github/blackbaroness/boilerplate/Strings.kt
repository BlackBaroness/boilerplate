package io.github.blackbaroness.boilerplate

fun String.detectLineSeparator() = when {
    contains("\r\n") -> "\r\n"
    contains("\n") -> "\n"
    contains("\r") -> "\r"
    else -> null
}

fun String.replace(replacements: Map<String, String>) =
    replacements.entries.fold(this) { acc, (k, v) -> acc.replace(k, v) }

fun String.replace(vararg replacements: Pair<String, String>) =
    replacements.fold(this) { acc, (k, v) -> acc.replace(k, v) }

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

fun String.removePrefixOrNull(prefix: String): String? {
    return removePrefix(prefix).takeIf { it != this }
}

fun String.startsWithAny(vararg prefixes: String, ignoreCase: Boolean = false): Boolean {
    return prefixes.any { startsWith(it, ignoreCase = ignoreCase) }
}

fun String.takeIfNotBlank(): String? = takeIf { it.isNotBlank() }
