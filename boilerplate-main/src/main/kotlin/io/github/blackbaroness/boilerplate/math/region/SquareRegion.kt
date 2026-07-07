package io.github.blackbaroness.boilerplate.math.region

import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.abs

class SquareRegion(
    val center: Vector3dc,
    val side: Double,
) : Region {

    override fun contains(point: Vector3dc): Boolean {
        val halfSide = side / 2

        return abs(point.x() - center.x()) <= halfSide &&
            abs(point.z() - center.z()) <= halfSide
    }

    fun generateSurfacePointsByDistance(desiredDistance: Double): Sequence<Vector3d> {
        return generateSurfacePoints(desiredDistance)
    }

    /**
     * Генерирует точки по контуру квадрата в плоскости X/Z.
     */
    fun generateSurfacePoints(step: Double): Sequence<Vector3d> = sequence {
        val halfSide = side / 2
        val minX = center.x() - halfSide
        val maxX = center.x() + halfSide
        val minZ = center.z() - halfSide
        val maxZ = center.z() + halfSide
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
        val halfSide = side / 2

        val regionMinX = center.x() - halfSide
        val regionMaxX = center.x() + halfSide
        val regionMinZ = center.z() - halfSide
        val regionMaxZ = center.z() + halfSide

        return max.x() >= regionMinX &&
            min.x() <= regionMaxX &&
            max.z() >= regionMinZ &&
            min.z() <= regionMaxZ
    }
}
