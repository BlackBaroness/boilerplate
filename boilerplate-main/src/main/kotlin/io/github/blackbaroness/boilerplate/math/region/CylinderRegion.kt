package io.github.blackbaroness.boilerplate.math.region

import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.*

class CylinderRegion(
    val center: Vector3dc,
    val radius: Double,
    val height: Double,
) : Region {

    override fun contains(point: Vector3dc): Boolean {
        val dx = point.x() - center.x()
        val dz = point.z() - center.z()
        val dy = point.y() - center.y()

        val distanceSq = dx * dx + dz * dz
        val withinRadius = distanceSq <= radius * radius
        val withinHeight = abs(dy) <= height / 2

        return withinRadius && withinHeight
    }

    fun generateSurfacePointsByAngle(
        stepDegrees: Double,
        verticalStep: Double = 1.0,
        includeBottom: Boolean = true,
        includeTop: Boolean = true,
    ): Sequence<Vector3d> {
        val angularStep = Math.toRadians(stepDegrees)
        return generateSurfacePoints(angularStep, verticalStep, includeBottom = includeBottom, includeTop = includeTop)
    }

    fun generateSurfacePointsByDistance(
        desiredDistance: Double,
        includeBottom: Boolean = true,
        includeTop: Boolean = true,
    ): Sequence<Vector3d> {
        val angularStep = desiredDistance / radius
        val verticalStep = desiredDistance
        val radialStep = desiredDistance
        return generateSurfacePoints(angularStep, verticalStep, radialStep, includeBottom, includeTop)
    }

    /**
     * Генерирует точки по всей поверхности цилиндра:
     * - боковая поверхность
     * - верхняя и нижняя крышки (круги)
     */
    fun generateSurfacePoints(
        angularStep: Double,
        verticalStep: Double,
        radialStep: Double = 0.5,
        includeBottom: Boolean,
        includeTop: Boolean,
    ): Sequence<Vector3d> = sequence {
        val yBottom = center.y() - height / 2
        val yTop = center.y() + height / 2

        var y = yBottom
        while (y <= yTop) {
            var angle = 0.0
            while (angle < 2 * PI) {
                val x = center.x() + radius * cos(angle)
                val z = center.z() + radius * sin(angle)
                yield(Vector3d(x, y, z))
                angle += angularStep
            }
            y += verticalStep
        }

        if (includeBottom)
            yieldAll(generateCap(yBottom, angularStep, radialStep))

        if (includeTop)
            yieldAll(generateCap(yTop, angularStep, radialStep))
    }

    private fun generateCap(yPlane: Double, angularStep: Double, radialStep: Double) = sequence {
        var r = 0.0
        while (r <= radius) {
            var angle = 0.0
            while (angle < 2 * PI) {
                val x = center.x() + r * cos(angle)
                val z = center.z() + r * sin(angle)
                yield(Vector3d(x, yPlane, z))
                angle += angularStep
            }
            r += radialStep
        }
    }

    fun intersects(min: Vector3dc, max: Vector3dc): Boolean {
        val boxMinY = min.y()
        val boxMaxY = max.y()
        val cylMinY = center.y() - height / 2
        val cylMaxY = center.y() + height / 2

        // First, check if the vertical ranges overlap
        if (boxMaxY < cylMinY || boxMinY > cylMaxY) return false

        // Now check the 2D (X, Z) overlap in the horizontal plane
        val closestX = max(min.x(), min(center.x(), max.x()))
        val closestZ = max(min.z(), min(center.z(), max.z()))

        val dx = closestX - center.x()
        val dz = closestZ - center.z()

        val distanceSq = dx * dx + dz * dz

        return distanceSq <= radius * radius
    }
}
