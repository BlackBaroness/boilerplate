package io.github.blackbaroness.boilerplate.paper.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier

@Serializable
data class AttributeConfiguration(
    val attribute: @Contextual Attribute,
    val modifier: @Contextual AttributeModifier,
)
