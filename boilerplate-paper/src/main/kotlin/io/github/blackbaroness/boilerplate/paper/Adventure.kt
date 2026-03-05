package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.adventure.ExtendedAudience
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

@Suppress("ObjectPropertyName")
private var _bukkitAudiences: BukkitAudiences? = null

val Boilerplate.bukkitAudiences: BukkitAudiences
    get() = _bukkitAudiences ?: throw IllegalStateException("Adventure is not initialized")

fun Boilerplate.initializeAdventure(plugin: Plugin) {
    Boilerplate.initializeLogger(plugin)

    if (_bukkitAudiences != null) {
        // Already initialized
        return
    }

    if (isNativeAdventureApiAvailable) {
        // No need to initialize since we can use the native API
        plugin.slF4JLogger.info("Using the native Adventure")
        return
    }

    plugin.slF4JLogger.info("Using the embedded Adventure")
    _bukkitAudiences = BukkitAudiences.create(plugin)
}

val Boilerplate.isNativeAdventureApiAvailable by lazy {
    try {
        ItemStack(Material.STONE).editMeta { it.displayName(Component.empty()) }
        return@lazy true
    } catch (_: NoSuchMethodError) {
        return@lazy false
    } catch (_: LinkageError) {
        return@lazy false
    }
}

fun Boilerplate.destroyAdventure() {
    _bukkitAudiences?.close()
    _bukkitAudiences = null
}

val CommandSender.adventure: Audience
    get() = ExtendedAudience(
        if (Boilerplate.isNativeAdventureApiAvailable) {
            this
        } else if (this is Player) {
            Boilerplate.bukkitAudiences.player(this)
        } else {
            Boilerplate.bukkitAudiences.sender(this)
        }
    )

@Deprecated("Not a good solution tbh")
val Collection<CommandSender>.adventure: Audience
    get() = Audience.audience(map { it.adventure })
