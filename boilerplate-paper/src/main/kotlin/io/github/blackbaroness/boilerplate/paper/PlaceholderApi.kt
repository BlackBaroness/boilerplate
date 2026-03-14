package io.github.blackbaroness.boilerplate.paper

import io.github.blackbaroness.boilerplate.Boilerplate
import io.github.blackbaroness.boilerplate.adventure.parseMiniMessage
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Boilerplate.papiTagResolver(player: Player?, selfClosing: Boolean = true) =
    TagResolver.resolver("papi") { argumentQueue, _ ->
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Boilerplate.logger.info("PlaceholderAPI is missing, unable to resolve <papi> placeholders")
            return@resolver Tag.selfClosingInserting(Component.text("PlaceholderAPI is missing"))
        }

        val papiPlaceholder = argumentQueue.popOr("use <papi:placeholder>").value()
        val parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%$papiPlaceholder%")

        val component = try {
            papiPlaceholder.parseMiniMessage()
        } catch (_: Exception) {
            LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder)
        }

        if (selfClosing) Tag.selfClosingInserting(component) else Tag.inserting(component)
    }
