import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class ParticleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
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
        for (particle in particles) {
            paint.color = particle.color
            canvas.drawCircle(particle.position.x, particle.position.y, particle.radius, paint)
        }
        updateParticles()
        postInvalidateOnAnimation()
    }

    private fun updateParticles() {
        for (particle in particles) {
            particle.update(particles, width.toFloat(), height.toFloat(), cohesionWeight, separationWeight, alignmentWeight, minDistance, maxSpeed)
        }
    }
}