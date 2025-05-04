package com.example.diplomawork2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.core.content.ContextCompat
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

    private val baseRadius = 50f
    private val base = Base(
        PointF(0f, 0f),
        baseRadius,
        1000
    )

    private val targetRadius = 30f
    private val cohesionWeight = 0.005f
    private val separationWeight = 0.1f
    private val alignmentW = 0.05f
    private val minDistance = 50f
    private val maxSpeed = 5f
    private val droneCount = 10
    private var gameDuration = 30f

    private var gameTimer = 0f
    private var isGameActive = false
    private var isGameFinished = false
    private var level = 1
    private var isVictory = false

    private var backgroundResource = 0
    private var scaledBitmap: Bitmap? = null

    init {
        setWillNotDraw(false)
        generateParticles()
    }


    fun setCustomBackground(resId: Int) {
        backgroundResource = resId
        if (width > 0 && height > 0) {
            val backgroundBitmap = BitmapFactory.decodeResource(resources, backgroundResource)
            if (backgroundBitmap != null) {
                scaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, true)
            } else {
                // Фон не найден, можно задать фон по умолчанию или оставить scaledBitmap = null
                scaledBitmap = null
            }
            invalidate()
        }
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Now it's safe to call setBackground

        setCustomBackground(backgroundResource)
        base.position.x = w / 2f - base.radius
        base.position.y = h - base.radius * 2
        generateParticles()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        scaledBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

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
        val basePaint = Paint()
        basePaint.color = ContextCompat.getColor(context, R.color.base_color)
        basePaint.alpha = (base.health * 255 / 1000)
        canvas.drawCircle(base.position.x + base.radius, base.position.y + base.radius, base.radius, basePaint)
    }

    private fun drawDrones(canvas: Canvas) {
        particles.forEach { drone ->
            if (drone.bullets <= 0) {
                paint.color = Color.RED // Красный цвет для уничтоженных дронов
            } else {
                paint.color = ContextCompat.getColor(context, R.color.drone_color)
            }
            canvas.drawCircle(drone.position.x, drone.position.y, drone.radius, paint)
            paint.color = Color.WHITE
            canvas.drawRect(
                drone.position.x - 2f,
                drone.position.y + 20f,
                drone.position.x + 2f * (drone.bullets.toFloat() / 20),
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
        val levelText = context.getString(R.string.level_text, level)
        canvas.drawText(levelText, 20f, 30f, textPaint)

        val timerText = String.format(
            context.getString(R.string.time_text),
            gameTimer
        )
        canvas.drawText(timerText, 20f, 80f, textPaint)

        if (isGameFinished) {
            val activity = context as? AppCompatActivity
            val nextLevelButton = activity?.findViewById<Button>(R.id.next_level_button)
            val restartButton = activity?.findViewById<Button>(R.id.restart_button)
            restartButton?.visibility = View.VISIBLE

            if (isVictory) {
                nextLevelButton?.visibility = View.VISIBLE
                canvas.drawText(
                    context.getString(R.string.win_text),
                    width / 2f - 150f,
                    height / 2f - 30f,
                    textPaint
                )
            } else {
                canvas.drawText(
                    context.getString(R.string.lose_text),
                    width / 2f - 150f,
                    height / 2f - 30f,
                    textPaint
                )
            }
        } else {
            val activity = context as? AppCompatActivity
            val nextLevelButton = activity?.findViewById<Button>(R.id.next_level_button)
            val restartButton = activity?.findViewById<Button>(R.id.restart_button)

            restartButton?.visibility = View.GONE
            nextLevelButton?.visibility = View.GONE
        }
    }

    private fun updateGame() {
        if (isGameActive) {
            gameTimer -= 0.016f
            updateTargets()
            updateParticles()
            base.update(targets)
            checkGameOver()
        }
    }

    private fun updateTargets() {
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
        if (base.health <= 0) {
            isGameActive = false
            isGameFinished = true
            isVictory = true // Победа, если база уничтожена целями
        } else if (gameTimer <= 0) {
            isGameActive = false
            isGameFinished = true
            isVictory = false // Поражение, если время истекло
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
        base.health = 1000
        generateParticles()
        targets.clear()
        val activity = context as? AppCompatActivity
        val nextLevelButton = activity?.findViewById<Button>(R.id.next_level_button)
        val restartButton = activity?.findViewById<Button>(R.id.restart_button)
        restartButton?.visibility = View.GONE
        nextLevelButton?.visibility = View.GONE
        isVictory = false
    }

    fun restartGame() {
        startNewGame()
        level = 1
        invalidate()
    }

    fun nextLevel() {
        level++
        gameTimer = gameDuration
        base.health = 1000
        generateParticles()
        targets.clear()
        val activity = context as? AppCompatActivity
        val nextLevelButton = activity?.findViewById<Button>(R.id.next_level_button)
        val restartButton = activity?.findViewById<Button>(R.id.restart_button)
        restartButton?.visibility = View.GONE
        nextLevelButton?.visibility = View.GONE
        isGameActive = true
        isGameFinished = false
        isVictory = false
    }

    private fun generateParticles() {
        if (width > 0 && height > 0) {
            particles.clear()
            val droneCount = level * 10
            repeat(droneCount) {
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
    }

}
