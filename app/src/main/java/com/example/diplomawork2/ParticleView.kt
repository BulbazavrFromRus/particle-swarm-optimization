package com.example.diplomawork2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.Button
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
    private val baseRadius = 50f
    private val basePosition = PointF(0f, 0f)
    private var baseHealth = 1000
    private val basePaint = Paint().apply { color = Color.GREEN }
    private val targetRadius = 30f
    private var selectedTarget: Target? = null
    private val cohesionWeight = 0.005f
    private val separationWeight = 0.1f
    private val alignmentW = 0.05f
    private val minDistance = 50f
    private val maxSpeed = 5f
    private val droneCount = 10
    private var mediaPlayer: MediaPlayer
    private var upgradeSpeedButton: Button? = null
    private var restartButton: Button? = null

    init {
        setWillNotDraw(false)
        mediaPlayer = MediaPlayer.create(context, R.raw.gimn)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { mediaPlayer.start() }
    }

    private fun generateParticles(count: Int) {
        particles.clear()
        repeat(count) {
            val centerX = width / 2f
            val centerY = height / 2f
            val radius = 200f
            val angle = it * 360f / count
            val x = centerX + radius * kotlin.math.cos(angle * Math.PI / 180).toFloat()
            val y = centerY + radius * kotlin.math.sin(angle * Math.PI / 180).toFloat()

            particles.add(
                Particle(
                    x, y,
                    Random.nextFloat() * 2 - 1,
                    Random.nextFloat() * 2 - 1,
                    15f, Color.BLUE, bullets = 200
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        // База
        basePaint.alpha = (baseHealth * 255 / 1000).toInt()
        canvas.drawCircle(
            basePosition.x + baseRadius,
            basePosition.y + baseRadius,
            baseRadius,
            basePaint
        )

        // Дроны
        particles.forEach { drone ->
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

        // Проверка состояния игры
        if (baseHealth <= 0) {
            canvas.drawText("Поражение! База уничтожена", 100f, height/2f, Paint().apply {
                color = Color.RED
                textSize = 50f
            })
            restartButton?.visibility = View.VISIBLE
            return
        }

        updateParticles()
        postInvalidateOnAnimation()
    }

    private fun updateTargets() {
        targets.forEach { target ->
            if (!target.isBeingDragged) {
                val dx = basePosition.x + baseRadius - target.x
                val dy = basePosition.y + baseRadius - target.y
                val distance = sqrt(dx*dx + dy*dy)

                if (distance > 0) {
                    target.x += dx / distance * target.speed
                    target.y += dy / distance * target.speed
                }

                if (distance < baseRadius) {
                    baseHealth -= 10
                    target.health = 0
                }
            }
        }
    }

    private fun updateParticles() {
        updateTargets()
        particles.forEach { drone ->
            drone.update(
                particles = particles,
                targets = targets.filter { it.health > 0 },
                screenWidth = width.toFloat(),
                screenHeight = height.toFloat(),
                cohesionW = cohesionWeight,
                separationW = separationWeight,
                alignmentW = alignmentW,
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
        if (w > 0 && h > 0) {
            generateParticles(droneCount)

            (parent as ViewGroup).apply {
                // Кнопка апгрейда
                upgradeSpeedButton = Button(context).apply {
                    text = "Ускорить цели"
                    setOnClickListener {
                        targets.forEach { it.speed *= 1.2f }
                    }
                    layoutParams = AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        100, // x
                        100 // y
                    )
                }.also { addView(it) }

                // Кнопка перезапуска
                restartButton = Button(context).apply {
                    text = "Перезапустить"
                    setOnClickListener {
                        restartGame()
                    }
                    layoutParams = AbsoluteLayout.LayoutParams(
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                        100, // x
                        height - 100 // y
                    )
                    visibility = View.GONE
                }.also { addView(it) }
            }
        }
    }

    private fun restartGame() {
        baseHealth = 100
        particles.clear()
        targets.clear()
        generateParticles(droneCount)
        restartButton?.visibility = View.GONE
        postInvalidateOnAnimation()
    }
}
