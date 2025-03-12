package com.example.diplomawork2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val particles = mutableListOf<Particle>()
    private val targets = mutableListOf<Target>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // Настройки
    private val cohesionWeight = 0.005f
    private val separationWeight = 0.1f
    private val alignmentWeight = 0.05f
    private val minDistance = 50f
    private val maxSpeed = 5f
    private val droneCount = 10  // Количество дронов

    init {
        setWillNotDraw(false)
        generateParticles(droneCount)
    }

    private fun generateParticles(count: Int) {
        particles.clear()
        repeat(count) {
            val x = Random.nextFloat() * width
            val y = Random.nextFloat() * height
            val radius = 15f  // Фиксированный размер дрона
            val speedX = Random.nextFloat() * 4 - 2
            val speedY = Random.nextFloat() * 4 - 2
            particles.add(
                Particle(
                    x, y, speedX, speedY,
                    radius, Color.BLUE, bullets = 2000
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        // Отрисовка дронов
        particles.filter { it.isActive }.forEach { drone ->
            paint.color = drone.color
            canvas.drawCircle(drone.position.x, drone.position.y, drone.radius, paint)

            // Индикатор патронов
            paint.color = Color.WHITE
            canvas.drawRect(
                drone.position.x - 15f,
                drone.position.y + 20f,
                drone.position.x + 15f * (drone.bullets.toFloat() / 20),
                drone.position.y + 23f,
                paint
            )
        }

        // Отрисовка целей
        targets.filter { it.health > 0 }.forEach { target ->
            targetPaint.color = Color.argb(
                255,
                255,
                (255 * (target.health.toFloat() / 100)).toInt(),
                0
            )
            canvas.drawCircle(target.x, target.y, 30f, targetPaint)
        }

        updateParticles()
        postInvalidateOnAnimation()
    }

    private fun updateParticles() {
        particles.forEach { drone ->
            drone.update(
                particles = particles,
                targets = targets.filter { it.health > 0 },
                screenWidth = width.toFloat(),
                screenHeight = height.toFloat(),
                cohesionW = cohesionWeight,
                separationW = separationWeight,
                alignmentW = alignmentWeight,
                minDist = minDistance,
                maxSpeed = maxSpeed
            )
        }
        targets.removeAll { it.health <= 0 }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                targets.add(Target(it.x, it.y))
                invalidate()
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            generateParticles(droneCount)
        }
    }
}

data class Target(
    val x: Float,
    val y: Float,
    var health: Int = 100  // Здоровье цели
)
