package io.github.blackbaroness.boilerplate.serialization.kotlinx

import io.github.blackbaroness.boilerplate.InetAddressMatcher
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class InetAddressMatcherStringSetSerializer : KSerializer<InetAddressMatcher> {

    private val delegate = SetSerializer(String.serializer())

    @OptIn(InternalSerializationApi::class)
    override val descriptor = buildSerialDescriptor("InetAddressMatcher", SerialKind.CONTEXTUAL, delegate.descriptor)

    override fun serialize(encoder: Encoder, value: InetAddressMatcher) {
        delegate.serialize(encoder, value.patterns)
    }

    override fun deserialize(decoder: Decoder): InetAddressMatcher {
        val patterns = delegate.deserialize(decoder)
        return InetAddressMatcher(patterns)
    }
}
