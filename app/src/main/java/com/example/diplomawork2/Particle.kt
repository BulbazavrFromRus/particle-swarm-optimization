import android.graphics.PointF
import kotlin.math.sqrt

data class Particle(
    var position: PointF,
    var velocity: PointF,
    var radius: Float,
    var color: Int
) {
    constructor(x: Float, y: Float, speedX: Float, speedY: Float, radius: Float, color: Int) : this(
        PointF(x, y), PointF(speedX, speedY), radius, color
    )

    fun update(particles: List<Particle>, screenWidth: Float, screenHeight: Float, cohesionW: Float, separationW: Float, alignmentW: Float, minDist: Float, maxSpeed: Float) {
        var cohesion = PointF()
        var separation = PointF()
        var alignment = PointF()
        var neighborCount = 0

        for (particle in particles) {
            if (particle != this) {
                val dx = particle.position.x - position.x
                val dy = particle.position.y - position.y
                val distance = sqrt(dx * dx + dy * dy)

                if (distance < minDist) {
                    separation.x -= dx
                    separation.y -= dy
                }

                cohesion.x += particle.position.x
                cohesion.y += particle.position.y
                alignment.x += particle.velocity.x
                alignment.y += particle.velocity.y
                neighborCount++
            }
        }

        if (neighborCount > 0) {
            val invNeighbors = 1f / neighborCount
            cohesion.x = (cohesion.x * invNeighbors - position.x) * cohesionW
            cohesion.y = (cohesion.y * invNeighbors - position.y) * cohesionW
            separation.x *= separationW
            separation.y *= separationW
            alignment.x = (alignment.x * invNeighbors - velocity.x) * alignmentW
            alignment.y = (alignment.y * invNeighbors - velocity.y) * alignmentW
        }

        velocity.x = (velocity.x + cohesion.x + separation.x + alignment.x).coerceIn(-maxSpeed, maxSpeed)
        velocity.y = (velocity.y + cohesion.y + separation.y + alignment.y).coerceIn(-maxSpeed, maxSpeed)

        position.x += velocity.x
        position.y += velocity.y

        if (position.x < 0 || position.x > screenWidth) velocity.x = -velocity.x
        if (position.y < 0 || position.y > screenHeight) velocity.y = -velocity.y
    }
}
