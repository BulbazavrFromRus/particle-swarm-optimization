package com.example.diplomawork2

import android.animation.Animator
import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
    }

    var base = Base(PointF(0f, 0f), 50f, 1000)

    private val pulseAnimation = PulseAnimation(warningTimeSeconds = 10f)
    private val gameState = GameState(gameDuration = 30.000f)
    private val particleManager = ParticleManager()
    private val targetManager = TargetManager(base)

    var username: String? = null
    var databaseHelper: DatabaseHelper

    var explosionAnimationView: LottieAnimationView? = null
    private var victoryAnimationView: LottieAnimationView? = null
    private var lossAnimationView: LottieAnimationView?= null

    var pulseScale  = 1f
    var pulseIncreasing = true

    private var backgroundResource = 0
    private var scaledBitmap: Bitmap? = null

    //GameEngine
    private val gameEngine = GameEngine(gameState, particleManager, targetManager, base)

    init {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        username = sharedPref.getString("username", null)
        databaseHelper = DatabaseHelper(context)
        setWillNotDraw(false)
        // Генерируем частицы согласно уровню и размеру
        if (width > 0 && height > 0) {
            particleManager.generateParticles(gameState.level * 10, width, height, context)
        }
    }

    // --- Animation view setters ---
    @JvmName("setExplosionAnimationViewExplicit")
    fun setExplosionAnimationView(view: LottieAnimationView) {
        this.explosionAnimationView = view
    }

    fun setVictoryAnimationView(view: LottieAnimationView) {
        this.victoryAnimationView = view
    }

    fun setLossAnimationView(view: LottieAnimationView){
        this.lossAnimationView = view
    }

    fun hideAllAnimations() {
        explosionAnimationView?.apply {
            cancelAnimation()
            visibility = View.GONE
        }
        victoryAnimationView?.apply {
            cancelAnimation()
            visibility = View.GONE
        }
        lossAnimationView?.apply {
            cancelAnimation()
            visibility = View.GONE
        }
    }

    // --- Background ---
    fun setCustomBackground(resId: Int) {
        backgroundResource = resId
        if (width > 0 && height > 0) {
            val backgroundBitmap = BitmapFactory.decodeResource(resources, backgroundResource)
            scaledBitmap = backgroundBitmap?.let {
                Bitmap.createScaledBitmap(it, width, height, true)
            }
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setCustomBackground(backgroundResource)
        base.position.x = w / 2f - base.radius
        base.position.y = h - base.radius * 2
        particleManager.generateParticles(gameState.level * 10, w, h, context)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        scaledBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }

        drawBase(canvas)
        drawDrones(canvas)
        drawTargets(canvas)
        drawGameInfo(canvas)

        if (!gameState.isGameFinished) {
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
        particleManager.particles.forEach { drone ->
            paint.color = if (drone.bullets <= 0) Color.RED else ContextCompat.getColor(context, R.color.drone_color)
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
        targetManager.targets.forEach { target ->
            targetPaint.color = Color.argb(
                255,
                255,
                (255 * (target.health.toFloat() / 100)).toInt(),
                0
            )
            canvas.drawCircle(target.x, target.y, 30f, targetPaint)
            if (target.isBeingDragged) {
                targetPaint.color = Color.CYAN
                canvas.drawCircle(target.x, target.y, 35f, targetPaint)
            }
        }
    }

    private fun drawGameInfo(canvas: Canvas) {
        val levelText = context.getString(R.string.level_text, gameState.level)
        canvas.drawText(levelText, 20f, 50f, textPaint)

        val timerText = String.format(
            context.getString(R.string.time_text),
            gameState.gameTimer
        )

        val x = 20f
        val y = 100f

        if (gameState.gameTimer <= pulseAnimation.warningTimeSeconds) {
            textPaint.color = Color.RED
            canvas.save()
            canvas.scale(pulseScale, pulseScale, x, y)
            canvas.drawText(timerText, x, y, textPaint)
            canvas.restore()
        } else {
            textPaint.color = Color.WHITE
            canvas.drawText(timerText, x, y, textPaint)
        }

        val activity = context as? AppCompatActivity
        val nextLevelButton = activity?.findViewById<Button>(R.id.next_level_button)
        val restartButton = activity?.findViewById<Button>(R.id.restart_button)

        if (gameState.isGameFinished) {
            restartButton?.visibility = View.VISIBLE
            if (gameState.isVictory) {
                nextLevelButton?.visibility = View.VISIBLE
            } else {
                nextLevelButton?.visibility = View.GONE
            }
        } else {
            restartButton?.visibility = View.GONE
            nextLevelButton?.visibility = View.GONE
        }
    }

    private fun updateGame() {
       /* if (gameState.isGameActive) {
            gameState.gameTimer -= 0.016f
            targetManager.updateTargets()
            particleManager.updateParticles(targetManager.targets, width.toFloat(), height.toFloat())
            base.update(targetManager.targets)
            checkGameOver()
        }
        if (gameState.gameTimer <= pulseAnimation.warningTimeSeconds && gameState.isGameActive) {
            pulseAnimation.update(gameState.gameTimer)
            pulseScale = pulseAnimation.pulseScale
            pulseIncreasing = pulseAnimation.pulseIncreasing
            postInvalidateOnAnimation()
        }*/

        // Делегируем обновление игрового состояния в GameEngine
        gameEngine.updateGame(width.toFloat(), height.toFloat())
        checkGameOver()

        // Обновляем пульсацию, если осталось меньше warningTimeSeconds и игра активна
        if (gameState.gameTimer <= pulseAnimation.warningTimeSeconds && gameState.isGameActive) {
            pulseAnimation.update(gameState.gameTimer)
            pulseScale = pulseAnimation.pulseScale
            pulseIncreasing = pulseAnimation.pulseIncreasing
            postInvalidateOnAnimation() // Запрос перерисовки для анимации
        }
    }

    private fun checkGameOver() {
        if (base.health <= 0) {
            gameState.stopGame(victory = true)
            vibratePhone()
            playExplosionSound()
            showExplosionAnimation()
            showVictoryAnimation()
        } else if (gameState.gameTimer <= 0) {
            gameState.stopGame(victory = false)
            showLossAnimation()
        }
    }

    private fun vibratePhone() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    private fun playExplosionSound() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.explosionsound)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }

    private fun showExplosionAnimation() {
        explosionAnimationView?.apply {
            visibility = VISIBLE
            setAnimation(R.raw.explosion)
            repeatCount = LottieDrawable.INFINITE
            speed = 0.5f
            playAnimation()
            setLayerType(LAYER_TYPE_SOFTWARE, null)
            removeAllAnimatorListeners()
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator) {}
                override fun onAnimationEnd(p0: Animator) { visibility = GONE }
                override fun onAnimationCancel(p0: Animator) {}
                override fun onAnimationRepeat(p0: Animator) {}
            })
        }
    }

    private fun showVictoryAnimation() {
        if (!gameState.victoryAnimationShown) {
            gameState.victoryAnimationShown = true
            victoryAnimationView?.apply {
                visibility = VISIBLE
                setAnimation(R.raw.victory)
                repeatCount = LottieDrawable.INFINITE
                speed = 0.5f
                playAnimation()
                setLayerType(LAYER_TYPE_SOFTWARE, null)
                removeAllAnimatorListeners()
                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationEnd(p0: Animator) { visibility = GONE }
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
            }
        }
    }

    private fun showLossAnimation() {
        if (!gameState.lossAnimationShown) {
            gameState.lossAnimationShown = true
            lossAnimationView?.apply {
                visibility = VISIBLE
                setAnimation(R.raw.loss)
                repeatCount = LottieDrawable.INFINITE
                speed = 0.5f
                playAnimation()
                setLayerType(LAYER_TYPE_SOFTWARE, null)
                removeAllAnimatorListeners()
                addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationEnd(p0: Animator) { visibility = GONE }
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}
                })
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameState.isGameFinished) return true

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!gameState.isGameActive) {
                    startNewGame()
                }

                val target = targetManager.targets.find {
                    sqrt((event.x - it.x).pow(2) + (event.y - it.y).pow(2)) < 30f * 2
                }

                if (target != null) {
                    target.isBeingDragged = true
                    targetManager.targets.remove(target)
                    targetManager.targets.add(target)
                } else {
                    targetManager.targets.add(Target(event.x, event.y))
                }
            }

            MotionEvent.ACTION_MOVE -> {
                targetManager.targets.find { it.isBeingDragged }?.let {
                    it.x = event.x
                    it.y = event.y
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                targetManager.targets.find { it.isBeingDragged }?.let {
                    it.isBeingDragged = false
                }
            }
        }
        return true
    }

    fun startNewGame() {
        gameState.startNewGame()
        base.health = 1000
        particleManager.generateParticles(gameState.level * 10, width, height, context)
        targetManager.targets.clear()
        val activity = context as? AppCompatActivity
        activity?.findViewById<Button>(R.id.next_level_button)?.visibility = GONE
        activity?.findViewById<Button>(R.id.restart_button)?.visibility = GONE
        gameState.isVictory = false
        explosionAnimationView?.visibility = GONE
        invalidate()
    }

    fun restartGame() {
        startNewGame()
        gameState.level = 1
        invalidate()
    }

    fun nextLevel() {
        gameState.nextLevel()
        username?.let { user ->
            val currentRecord = databaseHelper.getRecord(user)
            if (gameState.level > currentRecord) {
                databaseHelper.updateRecord(user, gameState.level)
            }
            explosionAnimationView?.apply {
                cancelAnimation()
                visibility = GONE
            }
        }
        base.health = 1000
        particleManager.generateParticles(gameState.level * 10, width, height, context)
        targetManager.targets.clear()
        val activity = context as? AppCompatActivity
        activity?.findViewById<Button>(R.id.next_level_button)?.visibility = GONE
        activity?.findViewById<Button>(R.id.restart_button)?.visibility = GONE
        invalidate()
    }
}
