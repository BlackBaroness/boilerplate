package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger

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
