package com.example.diplomawork2

import android.graphics.Color
import android.graphics.PointF
import kotlin.math.pow
import kotlin.math.sqrt

data class Particle(
    var position: PointF,
    var velocity: PointF,
    var radius: Float,
    var color: Int,
    var bullets: Int = 2000,
    var target: Target? = null,
    var isActive: Boolean = true
) {
    constructor(
        x: Float, y: Float, speedX: Float, speedY: Float,
        radius: Float, color: Int, bullets: Int = 2000
    ) : this(
        PointF(x, y), PointF(speedX.toFloat(), speedY.toFloat()), radius, color, bullets
    )

    fun update(
        particles: List<Particle>,
        targets: List<Target>,
        screenWidth: Float,
        screenHeight: Float,
        cohesionW: Float,
        separationW: Float,
        alignmentW: Float,
        minDist: Float, maxSpeed: Float
    ) {
        if (!isActive) return

        var cohesion = PointF()
        var separation = PointF()
        var alignment = PointF()
        var neighborCount = 0

        // Улучшение выбора цели
        val availableTargets = targets.filter { it.health > 0 }
        if (availableTargets.isEmpty()) return

        if (target == null || target!!.health <= 0 || target!!.isBeingDragged) {
            target = availableTargets.minByOrNull {
                sqrt(
                    (it.x - position.x).pow(2) +
                            (it.y - position.y).pow(2)
                )
            }
        }

        target?.let { t ->
            val dx = t.x - position.x
            val dy = t.y - position.y
            val distanceToTarget = sqrt(dx*dx + dy*dy)

            // Увеличение радиуса атаки
            if (distanceToTarget < 100f && bullets > 0) {
                t.health--
                bullets--
                if (t.health <= 0) target = null
            }

            velocity.x = dx.coerceIn(-maxSpeed, maxSpeed)
            velocity.y = dy.coerceIn(-maxSpeed, maxSpeed)
        }

        // Улучшение поведения роя
        for (particle in particles) {
            if (particle != this && particle.isActive) {
                val dx = particle.position.x - position.x
                val dy = particle.position.y - position.y
                val distance = sqrt(dx * dx + dy * dy)

                if (distance < minDist) {
                    separation.x -= dx
                    separation.y -= dy
                }

                cohesion.x += particle.position.x
                cohesion.y += particle.position.y
                alignment.x += particle.velocity.x
                alignment.y += particle.velocity.y
                neighborCount++
            }
        }

        if (neighborCount > 0) {
            val invNeighbors = 1f / neighborCount
            cohesion.set(
                (cohesion.x * invNeighbors - position.x) * cohesionW,
                (cohesion.y * invNeighbors - position.y) * cohesionW
            )

            separation.set(separation.x * separationW, separation.y * separationW)
            alignment.set(
                (alignment.x * invNeighbors - velocity.x) * alignmentW,
                (alignment.y * invNeighbors - velocity.y) * alignmentW
            )
        }

        velocity.x = (velocity.x + cohesion.x + separation.x + alignment.x).coerceIn(-maxSpeed, maxSpeed)
        velocity.y = (velocity.y + cohesion.y + separation.y + alignment.y).coerceIn(-maxSpeed, maxSpeed)

        position.x += velocity.x
        position.y += velocity.y

        position.x = position.x.coerceIn(0f, screenWidth)
        position.y = position.y.coerceIn(0f, screenHeight)

        if (bullets <= 0) {
            isActive = false
            color = Color.argb(128, 255, 0, 0)
        }
    }
}
