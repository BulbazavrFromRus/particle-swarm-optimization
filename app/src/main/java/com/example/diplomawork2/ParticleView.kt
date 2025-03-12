package com.example.diplomawork2
import Particle
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val particles = mutableListOf<Particle>()
    private val targets = mutableListOf<Target>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED }

    private val cohesionWeight = 0.005f
    private val separationWeight = 0.1f
    private val alignmentWeight = 0.05f
    private val minDistance = 50f
    private val maxSpeed = 5f

    init {
        generateParticles(100)
    }

    private fun generateParticles(count: Int) {
        for (i in 0 until count) {
            val x = Random.nextFloat() * width
            val y = Random.nextFloat() * height
            val radius = Random.nextFloat() * 10 + 5
            val speedX = Random.nextFloat() * 4 - 2
            val speedY = Random.nextFloat() * 4 - 2
            val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
            particles.add(Particle(x, y, speedX, speedY, radius, color))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        // Отрисовка частиц
        for (particle in particles) {
            paint.color = particle.color
            canvas.drawCircle(particle.position.x, particle.position.y, particle.radius, paint)
        }

        // Отрисовка целей
        for (target in targets) {
            canvas.drawCircle(target.x, target.y, 50f, targetPaint) // Радиус 15
        }

        updateParticles()
        postInvalidateOnAnimation()
    }

    private fun updateParticles() {
        for (particle in particles) {
            particle.update(particles, width.toFloat(), height.toFloat(), cohesionWeight, separationWeight, alignmentWeight, minDistance, maxSpeed)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {

                targets.add(Target(it.x, it.y)) // Добавляем цель
                    //invalidate() // Перерисовываем экран
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        particles.clear()
        generateParticles(100)
    }
}

// Определение класса цели
data class Target(val x: Float, val y: Float)
