package io.github.blackbaroness.boilerplate.serialization.kotlinx

import kotlinx.serialization.Serializable
import org.joml.Vector3d
import org.joml.Vector3dc

class Vector3dcCompositeSerializer : SurrogateSerializer<Vector3dc, Vector3dcCompositeSerializer.Surrogate>(
    Surrogate.serializer(),
    Vector3dc::class
) {

    override fun toSurrogate(value: Vector3dc) = Surrogate(
        x = value.x(),
        y = value.y(),
        z = value.z()
    )

    override fun fromSurrogate(value: Surrogate): Vector3dc = Vector3d(
        value.x,
        value.y,
        value.z
    )

    @Serializable
    data class Surrogate(
        val x: Double,
        val y: Double,
        val z: Double,
    )
}
