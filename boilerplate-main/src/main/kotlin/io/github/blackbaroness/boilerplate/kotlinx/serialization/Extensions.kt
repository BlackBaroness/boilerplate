package io.github.blackbaroness.boilerplate.kotlinx.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModuleBuilder
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

inline fun <reified T : Any> SerializersModuleBuilder.contextual(serializer: KSerializer<T>) =
    contextual(T::class, serializer)

inline fun <reified T> StringFormat.read(file: Path): T =
    decodeFromString(file.readText())

inline fun <reified T> StringFormat.write(file: Path, value: T) {
    file.createParentDirectories()
    file.writeText(encodeToString(value))
}

inline fun <reified T> StringFormat.update(readFrom: Path, writeTo: Path = readFrom, default: () -> T): T {
    if (readFrom.exists()) {
        val value = read<T>(readFrom)
        write(writeTo, value)
        return value
    }

    write(writeTo, default.invoke())
    return read(writeTo)
}
