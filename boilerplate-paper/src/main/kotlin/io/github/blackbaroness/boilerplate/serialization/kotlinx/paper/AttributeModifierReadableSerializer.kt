package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper

import io.github.blackbaroness.boilerplate.serialization.kotlinx.SurrogateSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import java.util.*

@Suppress("DEPRECATION", "removal")
class AttributeModifierReadableSerializer :
    SurrogateSerializer<AttributeModifier, AttributeModifierReadableSerializer.Surrogate>(
        Surrogate.serializer(),
        AttributeModifier::class
    ) {

    override fun toSurrogate(value: AttributeModifier) = Surrogate(
        value.uniqueId, value.name, value.operation, value.amount, value.slot
    )

    override fun fromSurrogate(value: Surrogate) =
        AttributeModifier(value.uuid, value.name, value.amount, value.operation, value.slot)

    @Serializable
    data class Surrogate(
        val uuid: @Contextual UUID = UUID.randomUUID(),
        val name: String,
        val operation: AttributeModifier.Operation,
        val amount: Double,
        val slot: EquipmentSlot?,
    )
}
