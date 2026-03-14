package io.github.blackbaroness.boilerplate.bungeecord

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.adventure.ExtendedAudience
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin

@Suppress("ObjectPropertyName")
private var _bungeeAudiences: BungeeAudiences? = null

@Suppress("UnusedReceiverParameter")
val Boilerplate.bungeeAudiences: BungeeAudiences
    get() = _bungeeAudiences ?: throw IllegalStateException("Adventure is not initialized")

@Suppress("UnusedReceiverParameter")
fun Boilerplate.initializeAdventure(plugin: Plugin) {
    Boilerplate.initializeLogger(plugin)

    if (_bungeeAudiences != null) {
        // Already initialized
        return
    }

    _bungeeAudiences = BungeeAudiences.create(plugin)
}

@Suppress("UnusedReceiverParameter")
fun Boilerplate.destroyAdventure() {
    _bungeeAudiences?.close()
    _bungeeAudiences = null
}

val CommandSender.adventure: Audience
    get() = ExtendedAudience(
        if (this is ProxiedPlayer) {
            Boilerplate.bungeeAudiences.player(this)
        } else {
            Boilerplate.bungeeAudiences.sender(this)
        }
    )
