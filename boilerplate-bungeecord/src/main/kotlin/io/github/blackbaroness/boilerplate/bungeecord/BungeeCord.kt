package io.github.blackbaroness.boilerplate.bungeecord

import io.github.blackbaroness.boilerplate.Boilerplate
import net.md_5.bungee.api.plugin.Plugin
import java.util.logging.Logger

@Suppress("ObjectPropertyName")
private var _logger: Logger? = null

@Suppress("UnusedReceiverParameter")
val Boilerplate.logger: Logger get() = _logger ?: throw IllegalArgumentException("No logger was set")

@Suppress("UnusedReceiverParameter")
fun Boilerplate.initializeLogger(plugin: Plugin) {
    if (_logger == null) {
        _logger = plugin.logger
    }
}
