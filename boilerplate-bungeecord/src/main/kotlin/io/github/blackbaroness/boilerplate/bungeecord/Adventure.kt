package io.github.blackbaroness.boilerplate.bungeecord

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.adventure.ExtendedAudience
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.Connection
import net.md_5.bungee.api.plugin.Plugin

@Suppress("ObjectPropertyName")
private var _bungeeAudiences: BungeeAudiences? = null

@Suppress("ObjectPropertyName")
private var _enableBungeeCordAudienceWrapper: Boolean = false

@Suppress("UnusedReceiverParameter")
var Boilerplate.enableBungeeCordAudienceWrapper: Boolean
    set(value) {
        _enableBungeeCordAudienceWrapper = value
    }
    get() = _enableBungeeCordAudienceWrapper

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
    get() {
        val audience = Boilerplate.bungeeAudiences.sender(this)
        return if (_enableBungeeCordAudienceWrapper) ExtendedAudience(audience) else audience
    }

val ComponentLike.asBungeeCordComponents: Array<BaseComponent>
    get() = BungeeComponentSerializer.get().serialize(asComponent())

val Array<BaseComponent>.asAdventureComponent: Component
    get() = BungeeComponentSerializer.get().deserialize(this)

val Array<BaseComponent>.asSingleComponent: BaseComponent
    get() = singleOrNull() ?: TextComponent().apply { extra = this@asSingleComponent.toList() }

fun Connection.disconnect(reason: ComponentLike) =
    disconnect(reason.asBungeeCordComponents.asSingleComponent)
