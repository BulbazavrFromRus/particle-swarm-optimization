// ParticleView.kt
package com.example.diplomawork2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
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
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
    }

    // Параметры базы
    private val baseRadius = 50f
    private var basePosition = PointF(0f, 0f)
    private var baseHealth = 1000
    private val basePaint = Paint().apply { color = Color.GREEN }

    // Настройки игры
    private val targetRadius = 30f
    private val cohesionWeight = 0.005f
    private val separationWeight = 0.1f
    private val alignmentW = 0.05f
    private val minDistance = 50f
    private val maxSpeed = 5f
    private val droneCount = 10
    private val gameDuration = 300f

    // Состояние игры
    private var gameTimer = 0f
    private var isGameActive = false
    private var isGameFinished = false
    private var restartButton: Button? = null

    init {
        setWillNotDraw(false)
        setupRestartButton()
    }

    private fun setupRestartButton() {
        restartButton = Button(context).apply {
            text = "Новая игра"
            setBackgroundColor(Color.LTGRAY)
            setTextColor(Color.BLACK)
            setOnClickListener { restartGame() }
            visibility = View.GONE

            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL)
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                bottomMargin = 50
            }
            layoutParams = params
        }

        if (parent is RelativeLayout) {
            (parent as RelativeLayout).addView(restartButton)
        } else {
            (context as MainActivity).runOnUiThread {
                (context as MainActivity).addContentView(restartButton, restartButton!!.layoutParams)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        drawBase(canvas)
        drawDrones(canvas)
        drawTargets(canvas)
        drawGameInfo(canvas)

        if (!isGameFinished) {
            updateGame()
            postInvalidateDelayed(16)
        }
    }

    private fun drawBase(canvas: Canvas) {
        basePaint.alpha = (baseHealth * 255 / 1000).toInt()
        canvas.drawCircle(
            basePosition.x + baseRadius,
            basePosition.y + baseRadius,
            baseRadius,
            basePaint
        )
    }

    private fun drawDrones(canvas: Canvas) {
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
    }

    private fun drawTargets(canvas: Canvas) {
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
    }

    private fun drawGameInfo(canvas: Canvas) {
        canvas.drawText(
            "Время: ${(gameTimer).toInt()} сек",
            20f,
            60f,
            textPaint
        )

        if (isGameFinished) {
            val text = if (baseHealth <= 0) "Поражение!" else "Победа!"
            val color = if (baseHealth <= 0) Color.RED else Color.GREEN
            textPaint.color = color
            canvas.drawText(text, width / 2f - 150f, height / 2f, textPaint)
            restartButton?.visibility = View.VISIBLE
        } else {
            restartButton?.visibility = View.GONE
        }
    }

    private fun updateGame() {
        if (isGameActive) {
            gameTimer -= 0.016f
            updateTargets()
            updateParticles()
            checkGameOver()
        }
    }

    private fun updateTargets() {
        targets.forEach { target ->
            val dx = basePosition.x + baseRadius - target.x
            val dy = basePosition.y + baseRadius - target.y
            val distance = sqrt(dx * dx + dy * dy)

            if (distance > 0) {
                target.x += dx / distance * target.speed
                target.y += dy / distance * target.speed
            }

            if (distance < baseRadius) {
                baseHealth -= 10
                target.health = 0
            }
        }
        targets.removeAll { it.health <= 0 }
    }

    private fun updateParticles() {
        particles.forEach { drone ->
            drone.update(
                particles = particles,
                targets = targets,
                screenWidth = width.toFloat(),
                screenHeight = height.toFloat(),
                cohesionW = cohesionWeight,
                separationW = separationWeight,
                alignmentW = alignmentW,
                minDist = minDistance,
                maxSpeed = maxSpeed
            )
        }
    }

    private fun checkGameOver() {
        if (baseHealth <= 0 || gameTimer <= 0) {
            isGameActive = false
            isGameFinished = true
            restartButton?.visibility = View.VISIBLE
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isGameFinished) return true

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!isGameActive) {
                    startNewGame()
                }

                val target = targets.find {
                    sqrt(
                        (event.x - it.x).pow(2) +
                                (event.y - it.y).pow(2)
                    ) < targetRadius * 2
                }

                if (target != null) {
                    target.isBeingDragged = true
                    targets.remove(target)
                    targets.add(target)
                } else {
                    targets.add(Target(event.x, event.y))
                }
            }

            MotionEvent.ACTION_MOVE -> {
                targets.find { it.isBeingDragged }?.let {
                    it.x = event.x
                    it.y = event.y
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                targets.find { it.isBeingDragged }?.let {
                    it.isBeingDragged = false
                }
            }
        }
        return true
    }

    private fun startNewGame() {
        isGameActive = true
        isGameFinished = false
        gameTimer = gameDuration
        baseHealth = 1000
        generateParticles()
        targets.clear()
        restartButton?.visibility = View.GONE
    }

    private fun restartGame() {
        startNewGame()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        basePosition.x = w / 2f - baseRadius
        basePosition.y = h - baseRadius * 2
        generateParticles()
    }

    private fun generateParticles() {
        particles.clear()
        repeat(droneCount) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    speedX = Random.nextFloat() * 2 - 1,
                    speedY = Random.nextFloat() * 2 - 1,
                    radius = 15f,
                    color = Color.BLUE,
                    bullets = 200
                )
            )
        }
    }
}
