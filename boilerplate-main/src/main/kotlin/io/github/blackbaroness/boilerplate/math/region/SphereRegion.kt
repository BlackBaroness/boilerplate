package io.github.blackbaroness.boilerplate.math.region

import org.joml.Vector3d
import org.joml.Vector3dc
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SphereRegion(
    val center: Vector3dc,
    val radius: Double,
) : Region {

    override fun contains(point: Vector3dc): Boolean {
        return point.distanceSquared(center) <= radius * radius
    }

    fun generateSurfacePointsByAngle(stepDegrees: Double): Sequence<Vector3d> {
        val step = Math.toRadians(stepDegrees)
        return generateSurfacePoints(step)
    }

    fun generateSurfacePointsByDistance(desiredDistance: Double): Sequence<Vector3d> {
        val angularStep = desiredDistance / radius
        return generateSurfacePoints(angularStep)
    }

    fun generateSurfacePoints(stepRadians: Double): Sequence<Vector3d> = sequence {
        var theta = 0.0
        while (theta <= PI) {
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)

            var phi = 0.0
            while (phi < 2 * PI) {
                val x = center.x() + radius * sinTheta * cos(phi)
                val y = center.y() + radius * cosTheta
                val z = center.z() + radius * sinTheta * sin(phi)
                yield(Vector3d(x, y, z))
                phi += stepRadians
            }
            theta += stepRadians
        }
    }
}
