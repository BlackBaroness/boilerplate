package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import java.io.InputStream

@Suppress("ObjectPropertyName")
private var _logger: Logger? = null

@Suppress("UnusedReceiverParameter")
val Boilerplate.logger: Logger get() = _logger ?: JavaPlugin.getProvidingPlugin(Boilerplate::class.java).slF4JLogger

@Suppress("UnusedReceiverParameter")
fun Boilerplate.initializeLogger(plugin: Plugin) {
    if (_logger == null) {
        _logger = plugin.slF4JLogger
    }
}

@Suppress("UnusedReceiverParameter")
fun Boilerplate.resolveNamespacedKey(input: String): NamespacedKey? =
    if (input.contains(':'))
        NamespacedKey.fromString(input)
    else
        NamespacedKey.minecraft(input)

val NamespacedKey.asMinimalString
    get() = if (namespace == NamespacedKey.MINECRAFT) key else asString()

fun Plugin.getResourceOrThrow(name: String): InputStream {
    return getResource(name) ?: error("Resource '$name' is missing")
}

inline fun <reified T> Server.findService(): T? {
    return servicesManager.getRegistration(T::class.java)?.provider
}
