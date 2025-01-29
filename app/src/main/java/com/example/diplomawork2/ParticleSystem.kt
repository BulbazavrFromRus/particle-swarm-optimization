package com.example.diplomawork2


import kotlin.random.Random

data class Particle(var x: Float, var y: Float, var vx: Float, var vy: Float, var bestX: Float, var bestY: Float)

class ParticleSystem(private val width: Int, private val height: Int) {
    private val particles: MutableList<Particle> = mutableListOf()

    init {
        for (i in 0 until 100) {
            particles.add(Particle(
                Random.nextFloat() * width, Random.nextFloat() * height,
                Random.nextFloat() * 2 - 1, Random.nextFloat() * 2 - 1,
                Random.nextFloat() * width, Random.nextFloat() * height
            ))
        }
    }

    fun update() {
        for (particle in particles) {
            // Update speed based on best position
            val inertia = 0.7f
            val cognitive = 1.5f
            val social = 1.5f

            val targetX = (particle.bestX - particle.x) * cognitive
            val targetY = (particle.bestY - particle.y) * cognitive

            val globalTargetX = (width / 2f - particle.x) * social
            val globalTargetY = (height / 2f - particle.y) * social

            particle.vx = inertia * particle.vx + targetX + globalTargetX
            particle.vy = inertia * particle.vy + targetY + globalTargetY

            // Update position
            particle.x += particle.vx
            particle.y += particle.vy

            // Boundary checks
            if (particle.x < 0) particle.x = 0f
            if (particle.x > width) particle.x = width.toFloat()
            if (particle.y < 0) particle.y = 0f
            if (particle.y > height) particle.y = height.toFloat()

            // Update the best position
            if (calculateFitness(particle) < calculateFitness(particle.copy(x = particle.bestX, y = particle.bestY))) {
                particle.bestX = particle.x
                particle.bestY = particle.y
            }
        }
    }

    private fun calculateFitness(particle: Particle): Float {
        // For example, a simple function to minimize, e.g., distance from the center
        val dx = particle.x - width / 2f
        val dy = particle.y - height / 2f
        return dx * dx + dy * dy
    }

    fun getParticles(): List<Particle> = particles
}

