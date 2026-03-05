package io.github.blackbaroness.boilerplate

import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries

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
