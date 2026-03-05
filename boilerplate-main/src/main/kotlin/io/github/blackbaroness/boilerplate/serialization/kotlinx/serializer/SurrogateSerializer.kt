package io.github.blackbaroness.boilerplate.serialization.kotlinx.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

abstract class SurrogateSerializer<TARGET : Any, SURROGATE>(
    private val delegate: KSerializer<SURROGATE>,
    targetClass: KClass<TARGET>,
) : KSerializer<TARGET> {

    override val descriptor = SerialDescriptor(targetClass.qualifiedName!!, delegate.descriptor)

    override fun serialize(encoder: Encoder, value: TARGET) =
        delegate.serialize(encoder, toSurrogate(value))

    override fun deserialize(decoder: Decoder): TARGET =
        fromSurrogate(delegate.deserialize(decoder))

    protected abstract fun toSurrogate(value: TARGET): SURROGATE

    protected abstract fun fromSurrogate(value: SURROGATE): TARGET
}
