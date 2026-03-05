package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.copyAndClose
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.outputStream

internal var logger: Logger? = null

internal val loggerSafe get() = logger ?: JavaPlugin.getProvidingPlugin(Boilerplate::class.java).slF4JLogger

fun Boilerplate.initializeLogger(plugin: Plugin) {
    logger = plugin.slF4JLogger
}

fun Boilerplate.resolveNamespacedKey(input: String): NamespacedKey? =
    if (input.contains(':'))
        NamespacedKey.fromString(input)
    else
        NamespacedKey.minecraft(input)

val NamespacedKey.asMinimalString
    get() = if (namespace == NamespacedKey.MINECRAFT) key else asString()

fun Plugin.saveResource(internalPath: String, destination: Path, overwrite: Boolean = false) {
    if (destination.exists() && !overwrite) {
        slF4JLogger.warn("Could not save '$internalPath' to '$destination' because it already exists.")
        return
    }

    destination.deleteIfExists()
    destination.createParentDirectories()

    (getResource(internalPath) ?: throw IllegalArgumentException("Could not find resource '$internalPath'")).use {
        copyAndClose(it, destination.outputStream())
    }
}
