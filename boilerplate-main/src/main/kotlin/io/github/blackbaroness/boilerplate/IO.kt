package io.github.blackbaroness.boilerplate

import java.io.*
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.*
import kotlin.io.DEFAULT_BUFFER_SIZE
import kotlin.io.buffered
import kotlin.io.bufferedReader
import kotlin.io.copyTo
import kotlin.io.forEachLine
import kotlin.io.path.*
import kotlin.use

fun copyAndClose(from: InputStream, to: OutputStream) =
    from.use { to.use { from.copyTo(to) } }

inline fun Path.useChunks(chunkSize: Int, action: (ByteBuffer) -> Unit) {
    require(chunkSize >= 1) { "chunkSize must be >= 1" }

    // Don't use a buffer when the chunk size is big enough
    val channel = if (chunkSize >= DEFAULT_BUFFER_SIZE) {
        FileChannel.open(this, StandardOpenOption.READ)
    } else {
        Channels.newChannel(inputStream().buffered())
    }

    channel.use {
        val buffer = ByteBuffer.allocate(chunkSize)
        while (it.read(buffer) != -1) {
            buffer.flip()
            action(buffer.asReadOnlyBuffer())
            buffer.clear()
        }
    }
}

fun Path.findSingleFile(glob: String = "*"): Path {
    val entries = listDirectoryEntries(glob)
    when (entries.size) {
        0 -> throw IllegalStateException("$this contains no file matching glob '$glob'")
        1 -> return entries.first()
        else -> throw IllegalStateException("$this contains more than one file matching glob '$glob'")
    }
}

private const val VAR_INT_CONTINUATION_BIT = 0x80
private const val VAR_INT_VALUE_MASK = 0x7F
private const val VAR_INT_MAX_SIZE = 5

fun OutputStream.writeVarInt(value: Int) {
    var v = value

    while ((v and VAR_INT_VALUE_MASK.inv()) != 0) {
        // Write the lower 7 bits and set the continuation bit.
        write((v and VAR_INT_VALUE_MASK) or VAR_INT_CONTINUATION_BIT)
        v = v ushr 7
    }

    // Write the final byte without the continuation bit.
    write(v)
}

fun InputStream.readVarInt(): Int {
    var result = 0
    var shift = 0

    repeat(VAR_INT_MAX_SIZE) { index ->
        val b = read()
        if (b == -1) {
            throw EOFException("Unexpected end of stream while reading VarInt")
        }

        // Merge the lower 7 bits into the result.
        result = result or ((b and VAR_INT_VALUE_MASK) shl shift)

        // If the continuation bit is not set, this is the final byte.
        if ((b and VAR_INT_CONTINUATION_BIT) == 0) {
            // For Int, the 5th byte may contain only 4 meaningful bits.
            if (index == VAR_INT_MAX_SIZE - 1 && (b and 0xF0) != 0) {
                throw IllegalArgumentException("VarInt is too long for Int")
            }
            return result
        }

        shift += 7
    }

    throw IllegalArgumentException("VarInt is too long (more than 5 bytes)")
}

fun Class<*>.saveResource(name: String, to: Path, overwrite: Boolean, textReplacements: Map<String, String> = mapOf()) {
    if (to.exists()) {
        if (!overwrite)
            return

        if (to.isDirectory())
            error("${to.absolutePathString()} is directory, cannot save resource '$name' to it")

        to.deleteExisting()
    }

    to.createParentDirectories()

    val input = classLoader.getResourceAsStream(name) ?: error("Resource '$name' does not exist")
    if (textReplacements.isEmpty()) {
        copyAndClose(input, to.outputStream())
        return
    }

    input.bufferedReader().use { input ->
        to.writer().use { output ->
            input.forEachLine { line ->
                output.appendLine(line.replace(textReplacements))
            }
        }
    }
}

fun Path.rename(newName: String): Path {
    val target = resolveSibling(newName)
    moveTo(target)
    return target
}

fun Path.resolve(first: String, vararg more: String): Path {
    var path = this.resolve(first)
    more.forEach {
        path = path.resolve(it)
    }
    return path
}

fun Path.calculateSizeRecursively(): Long {
    if (!exists()) return 0L
    if (isRegularFile()) return fileSize()
    return walk().sumOf { it.fileSize() }
}

fun createLinkForcibly(from: Path, to: Path) {
    to.createParentDirectories()
    to.deleteForcibly()
    to.createLinkPointingTo(from)
}

@OptIn(ExperimentalPathApi::class)
fun Path.deleteForcibly(vararg patterns: String = arrayOf("**"), matcherSyntax: String = "glob") {
    if (!exists()) return

    val includeMatchers = patterns
        .filterNot { it.startsWith("!") }
        .map { FileSystems.getDefault().getPathMatcher("$matcherSyntax:$it") }

    val excludeMatchers = patterns
        .filter { it.startsWith("!") }
        .map { FileSystems.getDefault().getPathMatcher("$matcherSyntax:${it.removePrefix("!")}") }

    val allErrors = mutableMapOf<Path, MutableList<Throwable>>()

    val base = toAbsolutePath().normalize()
    fun shouldBeDeleted(path: Path): Boolean {
        val relative = base.relativize(path.toAbsolutePath().normalize())
        return includeMatchers.any { it.matches(relative) } &&
            excludeMatchers.none { it.matches(relative) }
    }

    fun deleteFile(path: Path) {
        runCatching { path.toFile().setWritable(true) }
        runCatching { Files.setAttribute(path, "dos:readonly", false, LinkOption.NOFOLLOW_LINKS) }
        path.deleteIfExists()
    }

    if (!isDirectory()) {
        if (shouldBeDeleted(this)) {
            deleteFile(this)
        }
        return
    }

    Files.walk(this).sorted(Comparator.reverseOrder()).forEach { path ->
        try {
            if (!shouldBeDeleted(path)) return@forEach

            if (path.isDirectory()) {
                val hasProtectedEntries = path.walk().any { !shouldBeDeleted(it) }
                if (hasProtectedEntries) return@forEach
                path.deleteRecursively()
                return@forEach
            }
            deleteFile(path)
        } catch (e: Throwable) {
            allErrors.computeIfAbsent(path) { mutableListOf() }.add(e)
        }
    }

    if (allErrors.isEmpty()) return

    error(
        buildString {
            appendLine("Failed to delete some files:")
            for ((file, errors) in allErrors) {
                appendLine("> Path '${file.absolutePathString()}' because of errors:")
                for (error in errors) {
                    val rootCause = error.rootCause()
                    appendLine("--- ${rootCause::class.simpleName} '${rootCause.message}'")
                }
            }
        }
    )
}

fun InputStream.readBytesStrictly(size: Int): ByteArray {
    val array = ByteArray(size)
    val read = read(array)
    require(read == size) { "Wanted to read $size bytes but was able to read only $read" }
    return array
}

inline fun BufferedReader.forEachLineFast(action: (String) -> Unit) {
    while (true) {
        action.invoke(readLine() ?: return)
    }
}

val OutputStream.asNotCloseable: OutputStream get() = NonCloseableOutputStream(this)

class NonCloseableOutputStream(out: OutputStream) : FilterOutputStream(out) {
    override fun close() = flush() // never close, just flush
}

val InputStream.asNotCloseable: InputStream get() = NonCloseableInputStream(this)

class NonCloseableInputStream(out: InputStream) : FilterInputStream(out) {
    override fun close() = Unit // never close, just flush
}
