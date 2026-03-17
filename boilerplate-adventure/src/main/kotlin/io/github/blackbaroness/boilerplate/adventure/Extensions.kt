package io.github.blackbaroness.boilerplate.adventure

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.CharacterAndFormat
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent

fun ComponentLike.replace(what: String, with: String): Component =
    asComponent().replaceText { builder -> builder.matchLiteral(what).replacement(with) }

fun ComponentLike.replace(what: String, with: ComponentLike): ComponentLike =
    asComponent().replaceText { builder -> builder.matchLiteral(what).replacement(with) }

val ComponentLike.asLegacy: String
    get() = LegacyComponentSerializer.legacySection().serialize(asComponent())

val ComponentLike.asPlain: String
    get() = PlainTextComponentSerializer.plainText().serialize(asComponent())

val Component.asJson: String
    get() = GsonComponentSerializer.gson().serialize(this)

fun String.parseJsonToComponent(): Component =
    GsonComponentSerializer.gson().deserialize(this)

fun Audience.sendMessage(unparsed: String, vararg tagResolvers: TagResolver) {
    if (unparsed.isEmpty()) return
    sendMessage(unparsed.parseMiniMessage(*tagResolvers))
}

fun Audience.sendMessage(unparsed: String, tagResolvers: Iterable<TagResolver>) {
    if (unparsed.isEmpty()) return
    sendMessage(unparsed, TagResolver.resolver(tagResolvers))
}

fun Audience.sendMessage(unparsed: String, tagResolvers: Collection<TagResolver>) {
    if (unparsed.isEmpty()) return
    sendMessage(unparsed, TagResolver.resolver(tagResolvers))
}

fun Audience.sendActionBar(unparsed: String, vararg tagResolvers: TagResolver) {
    if (unparsed.isEmpty()) return
    sendActionBar(unparsed.parseMiniMessage(*tagResolvers))
}

fun Audience.sendActionBar(unparsed: String, tagResolvers: Iterable<TagResolver>) {
    if (unparsed.isEmpty()) return
    sendActionBar(unparsed, TagResolver.resolver(tagResolvers))
}

fun Audience.sendActionBar(unparsed: String, tagResolvers: Collection<TagResolver>) {
    if (unparsed.isEmpty()) return
    sendActionBar(unparsed, TagResolver.resolver(tagResolvers))
}

fun List<TextColor>.createMiniMessageGradient() = when (size) {
    0 -> ""
    1 -> "<color:${single().asHexString()}>"
    else -> "<gradient:${joinToString(separator = ":") { it.asHexString() }}>"
}

fun Component.apply(characterAndFormat: CharacterAndFormat) = when (val format = characterAndFormat.format()) {
    is TextDecoration -> decorate(format)
    is TextColor -> color(format)
    else -> throw IllegalArgumentException("Unsupported: $characterAndFormat")
}

fun String.parseMiniMessage(miniMessage: MiniMessage = MiniMessage.miniMessage()): Component {
    return parseMiniMessage(TagResolver.empty(), miniMessage)
}

fun String.parseMiniMessage(tagResolver: TagResolver, miniMessage: MiniMessage = MiniMessage.miniMessage()): Component {
    if (isEmpty()) return Component.empty()
    return miniMessage.deserialize(this, tagResolver)
        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
}

fun String.parseMiniMessage(
    vararg tagResolvers: TagResolver,
    miniMessage: MiniMessage = MiniMessage.miniMessage(),
): Component {
    val builder = TagResolver.builder()
    for (resolver in tagResolvers) {
        builder.resolver(resolver)
    }
    return parseMiniMessage(builder.build(), miniMessage)
}

@JvmName("parseMiniMessageArray")
fun String.parseMiniMessage(
    tagResolvers: Array<TagResolver>,
    miniMessage: MiniMessage = MiniMessage.miniMessage(),
): Component {
    val builder = TagResolver.builder()
    for (resolver in tagResolvers) {
        builder.resolver(resolver)
    }
    return parseMiniMessage(builder.build(), miniMessage)
}

fun String.parseMiniMessage(
    tagResolvers: Iterable<TagResolver>,
    miniMessage: MiniMessage = MiniMessage.miniMessage(),
): Component {
    val builder = TagResolver.builder()
    for (resolver in tagResolvers) {
        builder.resolver(resolver)
    }
    return parseMiniMessage(builder.build(), miniMessage)
}

@Deprecated(message = "String.parseMiniMessage(Iterable) already handles that")
fun String.parseMiniMessage(tagResolvers: Collection<TagResolver>): Component {
    return parseMiniMessage(tagResolvers as Iterable<TagResolver>)
}

val ComponentLike.isEmpty
    get() = this.asComponent() == Component.empty()

val ComponentLike.asBungeeCordComponents: Array<BaseComponent>
    get() = BungeeComponentSerializer.get().serialize(asComponent())

val Array<BaseComponent>.asAdventureComponent: Component
    get() = BungeeComponentSerializer.get().deserialize(this)

inline fun buildComponent(action: TextComponent.Builder.() -> Unit): Component {
    val builder = Component.text()
    action.invoke(builder)
    return builder.build()
}

fun TextComponent.Builder.append(rawString: String, vararg tagResolvers: TagResolver) {
    append(rawString.parseMiniMessage(*tagResolvers))
}
