package io.github.blackbaroness.boilerplate.paper.serialization.kotlinx

import de.tr7zw.nbtapi.NBT
import io.github.blackbaroness.boilerplate.paper.model.NbtItem
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NbtItemSerializer : KSerializer<NbtItem> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(NbtItem::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NbtItem) =
        encoder.encodeString(value.nbtString)

    override fun deserialize(decoder: Decoder): NbtItem {
        val string = decoder.decodeString()

        // check if the nbt is valid to find errors early
        NBT.itemStackFromNBT(NBT.parseNBT(string))
            ?: throw IllegalStateException("Not a valid item NBT: '$string'")

        return NbtItem(string)
    }
}
