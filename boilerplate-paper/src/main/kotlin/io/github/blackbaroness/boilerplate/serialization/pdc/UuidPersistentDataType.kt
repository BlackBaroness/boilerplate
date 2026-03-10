package io.github.blackbaroness.boilerplate.serialization.pdc

import io.github.blackbaroness.boilerplate.toByteArray
import io.github.blackbaroness.boilerplate.toUuid
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import java.util.*

object UuidPersistentDataType : PersistentDataType<ByteArray, UUID> {

    override fun getPrimitiveType() = ByteArray::class.java

    override fun getComplexType() = UUID::class.java

    override fun toPrimitive(complex: UUID, context: PersistentDataAdapterContext): ByteArray {
        return complex.toByteArray()
    }

    override fun fromPrimitive(primitive: ByteArray, context: PersistentDataAdapterContext): UUID {
        require(primitive.size == 16) { "UUID byte array must be 16 bytes, got ${primitive.size}" }
        return primitive.toUuid()
    }
}
