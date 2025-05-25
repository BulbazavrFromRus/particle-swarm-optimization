package com.example.diplomawork2

import kotlin.math.sqrt

class TargetManager(
    private val base: Base
) {
    val targets = mutableListOf<Target>()

    fun updateTargets() {
        targets.forEach { target ->
            val dx = base.position.x + base.radius - target.x
            val dy = base.position.y + base.radius - target.y
            val distance = sqrt(dx * dx + dy * dy)

            if (distance > 0) {
                target.x += dx / distance * target.speed
                target.y += dy / distance * target.speed
            }

            if (distance < base.radius) {
                base.health -= 10
                target.health = 0
            }
        }
        targets.removeAll { it.health <= 0 }
    }
}
