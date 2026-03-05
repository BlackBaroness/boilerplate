package io.github.blackbaroness.boilerplate.adventure.processor

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.function.UnaryOperator
import java.util.regex.Pattern

class LegacyMiniMessagePostProcessor : UnaryOperator<Component> {

    private val config = TextReplacementConfig.builder()
        .match(Pattern.compile(".*"))
        .replacement { match, _ -> LegacyComponentSerializer.legacyAmpersand().deserialize(match.group()) }
        .build()

    override fun apply(component: Component): Component {
        return component.replaceText(config)
    }
}
