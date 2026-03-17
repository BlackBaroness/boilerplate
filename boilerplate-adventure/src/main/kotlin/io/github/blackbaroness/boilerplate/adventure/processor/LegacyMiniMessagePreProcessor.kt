package io.github.blackbaroness.boilerplate.adventure.processor

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.util.function.UnaryOperator

class LegacyMiniMessagePreProcessor : UnaryOperator<String> {

    override fun apply(component: String) = component.replace(
        LegacyComponentSerializer.SECTION_CHAR,
        LegacyComponentSerializer.AMPERSAND_CHAR
    )
}
