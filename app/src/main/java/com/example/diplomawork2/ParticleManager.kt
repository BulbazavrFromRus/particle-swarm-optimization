package com.example.diplomawork2

import android.content.Context
import androidx.core.content.ContextCompat
import kotlin.random.Random

class ParticleManager(
    private val cohesionWeight: Float = 0.005f,
    private val separationWeight: Float = 0.1f,
    private val alignmentWeight: Float = 0.05f,
    private val minDistance: Float = 50f,
    private val maxSpeed: Float = 5f
) {
    val particles = mutableListOf<Particle>()

    fun generateParticles(count: Int, width: Int, height: Int, context: Context) {
        particles.clear()
        repeat(count) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    speedX = Random.nextFloat() * 2 - 1,
                    speedY = Random.nextFloat() * 2 - 1,
                    radius = 15f,
                    color = ContextCompat.getColor(context, R.color.drone_color),
                    bullets = 2000
                )
            )
        }
    }

    fun updateParticles(targets: List<Target>, screenWidth: Float, screenHeight: Float) {
        particles.forEach { drone ->
            drone.update(
                particles = particles,
                targets = targets,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                cohesionW = cohesionWeight,
                separationW = separationWeight,
                alignmentW = alignmentWeight,
                minDist = minDistance,
                maxSpeed = maxSpeed
            )
        }
    }
}
