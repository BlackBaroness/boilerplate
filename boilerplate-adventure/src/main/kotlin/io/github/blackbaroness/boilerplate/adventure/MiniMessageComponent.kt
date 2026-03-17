package io.github.blackbaroness.boilerplate.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class MiniMessageComponent(val originalString: String, val parsed: Component) : ComponentLike by parsed {

    fun resolve(vararg tagResolvers: TagResolver): ComponentLike {
        if (tagResolvers.isEmpty()) return this
        return originalString.parseMiniMessage(*tagResolvers)
    }
}

val String.asMiniMessageComponent: MiniMessageComponent
    get() = MiniMessageComponent(this, parseMiniMessage())

val ComponentLike.asMiniMessageComponent: MiniMessageComponent
    get() {
        if (this is MiniMessageComponent) return this
        return MiniMessageComponent(
            MiniMessage.miniMessage().serialize(asComponent())
                .removePrefix("<!italic><!underlined><!strikethrough><!bold><!obfuscated>"),
            asComponent()
        )
    }
