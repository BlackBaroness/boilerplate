package io.github.blackbaroness.boilerplate

import com.charleskorn.kaml.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import kotlin.io.path.*
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

fun YamlMap.navigate(vararg path: String): YamlNode {
    var current: YamlNode = this
    for (string in path) {
        current = current.yamlMap.get<YamlNode>(string) ?: error("Path '$string' not found on ${current.path}")
    }
    return current
}

val YamlNode.asString: String
    get() = yamlScalar.content

inline fun <reified T : Any> Boilerplate.loadYamlConfigurationFile(
    yaml: Yaml,
    file: Path,
    removeNulls: Boolean = false,
    noinline default: () -> T,
) = loadYamlConfigurationFile(
    yaml = yaml,
    file = file,
    serializer = serializer<T>(),
    removeNulls = removeNulls,
    default = default,
    clazz = T::class
)

fun <T : Any> Boilerplate.loadYamlConfigurationFile(
    yaml: Yaml,
    file: Path,
    serializer: KSerializer<T>,
    removeNulls: Boolean = false,
    default: () -> T,
    clazz: KClass<T>,
): T {
    file.createParentDirectories()
    val dirty = createTempFile(prefix = "${file.name}.", directory = file.parent, suffix = ".tmp")
    try {
        return loadYamlConfigurationFile0(
            yaml = yaml,
            file = file,
            dirtyFile = dirty,
            removeNulls = removeNulls,
            serializer = serializer,
            default = default,
            clazz = clazz
        )
    } finally {
        runCatching { dirty.deleteIfExists() }
    }
}

private fun <T : Any> loadYamlConfigurationFile0(
    yaml: Yaml,
    file: Path,
    dirtyFile: Path,
    removeNulls: Boolean,
    serializer: KSerializer<T>,
    default: () -> T,
    clazz: KClass<T>,
): T {
    fun decode(text: String): T {
        return try {
            yaml.decodeFromString(serializer, text)
        } catch (e: Throwable) {
            throw IllegalStateException("Failed to parse YAML. Full text:\n$text", e)
        }
    }

    var result = if (file.exists()) {
        decode(file.readText())
    } else {
        default.invoke()
    }

    var text = extractHeaderLines(clazz).joinToString(prefix = "# ", separator = "\n# ", postfix = "\n")
    text += yaml.encodeToString(serializer, result)
    text = replaceEmptyCommentsWithEmptyLines(text)
    if (removeNulls) {
        text = removeNullFields(text)
    }

    result = decode(text)

    dirtyFile.createParentDirectories()
    dirtyFile.writeText(text, options = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
    dirtyFile.moveTo(file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)

    return result
}

private fun extractHeaderLines(clazz: KClass<*>): Array<out String> {
    return clazz.findAnnotation<YamlHeader>()?.lines.orEmpty()
}

private fun replaceEmptyCommentsWithEmptyLines(text: String): String = buildString {
    for (line in text.lineSequence()) {
        if (line.trim().singleOrNull() == '#') {
            appendLine()
        } else {
            appendLine(line)
        }
    }
}

private fun removeNullFields(text: String): String {
    val lines = text.lines()
    val out = StringBuilder()
    var i = 0

    fun indentOf(s: String): Int = s.indexOfFirst { !it.isWhitespace() }.let { if (it == -1) Int.MAX_VALUE else it }
    fun isNullField(s: String): Boolean = s.trimEnd().endsWith(": null")

    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trimEnd()

        if (isNullField(line)) {
            i++
            continue
        }

        val indent = indentOf(line)
        if (trimmed.endsWith(":")) {
            var j = i + 1
            var sawChild = false
            var allChildrenNull = true

            while (j < lines.size) {
                val child = lines[j]
                val childIndent = indentOf(child)

                if (child.isBlank()) {
                    j++
                    continue
                }

                if (childIndent <= indent) break

                sawChild = true
                if (!isNullField(child)) {
                    allChildrenNull = false
                    break
                }
                j++
            }

            if (sawChild && allChildrenNull) {
                i = j
                continue
            }
        }

        out.appendLine(line)
        i++
    }

    return out.toString()
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class YamlHeader(vararg val lines: String)
