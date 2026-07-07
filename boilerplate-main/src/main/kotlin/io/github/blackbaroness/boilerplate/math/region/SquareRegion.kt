package io.github.blackbaroness.boilerplate.math.region

import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.abs

class SquareRegion(
    val center: Vector3dc,
    val side: Double,
) : Region {

    val minX: Double = center.x() - side / 2
    val maxX: Double = center.x() + side / 2

    val minZ: Double = center.z() - side / 2
    val maxZ: Double = center.z() + side / 2

    override fun contains(point: Vector3dc): Boolean {
        val halfSide = side / 2

        return abs(point.x() - center.x()) <= halfSide &&
            abs(point.z() - center.z()) <= halfSide
    }

    fun generateSurfacePointsByDistance(desiredDistance: Double): Sequence<Vector3d> {
        return generateSurfacePoints(desiredDistance)
    }

    fun generateSurfacePoints(step: Double): Sequence<Vector3d> = sequence {
        val y = center.y()

        var x = minX
        while (x <= maxX) {
            yield(Vector3d(x, y, minZ))
            yield(Vector3d(x, y, maxZ))
            x += step
        }

        var z = minZ
        while (z <= maxZ) {
            yield(Vector3d(minX, y, z))
            yield(Vector3d(maxX, y, z))
            z += step
        }
    }

    fun intersects(min: Vector3dc, max: Vector3dc): Boolean {
        return max.x() >= minX &&
            min.x() <= maxX &&
            max.z() >= minZ &&
            min.z() <= maxZ
    }
}
