package io.github.blackbaroness.boilerplate.serialization.kotlinx.paper

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.EquipmentSlotGroup

class EquipmentSlotGroupSerializer : KSerializer<EquipmentSlotGroup> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(EquipmentSlotGroup::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EquipmentSlotGroup) {
        encoder.encodeString(
            when (value) {
                EquipmentSlotGroup.ANY -> "any"
                EquipmentSlotGroup.MAINHAND -> "mainhand"
                EquipmentSlotGroup.OFFHAND -> "offhand"
                EquipmentSlotGroup.HAND -> "hand"
                EquipmentSlotGroup.FEET -> "feet"
                EquipmentSlotGroup.LEGS -> "legs"
                EquipmentSlotGroup.CHEST -> "chest"
                EquipmentSlotGroup.HEAD -> "head"
                EquipmentSlotGroup.ARMOR -> "armor"
                EquipmentSlotGroup.BODY -> "body"
                EquipmentSlotGroup.SADDLE -> "saddle"
                else -> throw UnsupportedOperationException(value.toString())
            }
        )
    }

    override fun deserialize(decoder: Decoder): EquipmentSlotGroup {
        return when (val id = decoder.decodeString()) {
            "any" -> EquipmentSlotGroup.ANY
            "mainhand" -> EquipmentSlotGroup.MAINHAND
            "offhand" -> EquipmentSlotGroup.OFFHAND
            "hand" -> EquipmentSlotGroup.HAND
            "feet" -> EquipmentSlotGroup.FEET
            "legs" -> EquipmentSlotGroup.LEGS
            "chest" -> EquipmentSlotGroup.CHEST
            "head" -> EquipmentSlotGroup.HEAD
            "armor" -> EquipmentSlotGroup.ARMOR
            "body" -> EquipmentSlotGroup.BODY
            "saddle" -> EquipmentSlotGroup.SADDLE
            else -> throw IllegalArgumentException("Unknown EquipmentSlotGroup: $id")
        }
    }
}
