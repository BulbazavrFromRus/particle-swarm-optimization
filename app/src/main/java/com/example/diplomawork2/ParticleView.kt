package com.example.diplomawork2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val particles = mutableListOf<Particle>()
    private val targets = mutableListOf<Target>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val targetRadius = 30f

    private val cohesionWeight = 0.005f
    private val separationWeight = 0.1f
    private val alignmentWeight = 0.05f
    private val minDistance = 50f
    private val maxSpeed = 5f
    private val droneCount = 10

    private var selectedTarget: Target? = null
    private var mediaPlayer: MediaPlayer

    init {
        setWillNotDraw(false)
        mediaPlayer = MediaPlayer.create(context, R.raw.gimn)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mediaPlayer.start() }
    }

    private fun generateParticles(count: Int) {
        particles.clear()
        repeat(count) {
            val x = Random.nextFloat() * width
            val y = Random.nextFloat() * height
            particles.add(
                Particle(
                    x, y,
                    Random.nextFloat() * 4 - 2,
                    Random.nextFloat() * 4 - 2,
                    15f, Color.BLUE, bullets = 200
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        // Дроны
        particles.filter { it.isActive }.forEach { drone ->
            paint.color = drone.color
            canvas.drawCircle(drone.position.x, drone.position.y, drone.radius, paint)

            paint.color = Color.WHITE
            canvas.drawRect(
                drone.position.x - 15f,
                drone.position.y + 20f,
                drone.position.x + 15f * (drone.bullets.toFloat() / 20),
                drone.position.y + 23f,
                paint
            )
        }

        // Цели
        targets.forEach { target ->
            targetPaint.color = Color.argb(
                255,
                255,
                (255 * (target.health.toFloat() / 100)).toInt(),
                0
            )
            canvas.drawCircle(target.x, target.y, targetRadius, targetPaint)

            if (target.isBeingDragged) {
                targetPaint.color = Color.CYAN
                canvas.drawCircle(target.x, target.y, targetRadius + 5f, targetPaint)
            }
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
        event?.let { e ->
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    selectedTarget = targets.filter {
                        sqrt(
                            (e.x - it.x).pow(2) +
                                    (e.y - it.y).pow(2)
                        ) < targetRadius * 2
                    }.minByOrNull {
                        sqrt(
                            (e.x - it.x).pow(2) +
                                    (e.y - it.y).pow(2)
                        )
                    }

                    selectedTarget?.let {
                        it.isBeingDragged = true
                        targets.remove(it)
                        targets.add(it)
                    } ?: run {
                        targets.add(Target(e.x, e.y))
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    selectedTarget?.let {
                        it.x = e.x
                        it.y = e.y
                        invalidate()
                    }
                }

                MotionEvent.ACTION_UP -> {
                    selectedTarget?.isBeingDragged = false
                    selectedTarget = null
                }

                else -> {}
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) generateParticles(droneCount)
    }
}
