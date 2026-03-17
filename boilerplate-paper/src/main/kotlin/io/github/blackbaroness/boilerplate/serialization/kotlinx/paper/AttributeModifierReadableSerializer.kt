package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper

import io.github.blackbaroness.boilerplate.paper.asMinimalString
import io.github.blackbaroness.boilerplate.serialization.kotlinx.SurrogateSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import java.util.*

class AttributeModifierReadableSerializer :
    SurrogateSerializer<AttributeModifier, AttributeModifierReadableSerializer.Surrogate>(
        Surrogate.serializer(),
        AttributeModifier::class
    ) {

    override fun toSurrogate(value: AttributeModifier) = Surrogate(
        id = value.key.asMinimalString,
        amount = value.amount,
        operation = value.operation,
        slotGroup = value.slotGroup
    )

    override fun fromSurrogate(value: Surrogate) = AttributeModifier(
        NamespacedKey.fromString(value.id) ?: error("Invalid attribute modifier id '${value.id}'"),
        value.amount,
        value.operation,
        value.slotGroup
    )

    @Serializable
    data class Surrogate(
        val id: String = UUID.randomUUID().toString(),
        val amount: Double,
        val operation: AttributeModifier.Operation,
        val slotGroup: @Contextual EquipmentSlotGroup,
    )
}
