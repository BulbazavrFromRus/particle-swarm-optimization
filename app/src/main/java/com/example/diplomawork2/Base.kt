package com.example.diplomawork2
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import kotlin.math.sqrt

class Base(
    var position: PointF,
    var radius: Float,
    var health: Int,
    var color: Int = Color.GREEN
) {

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = color
        paint.alpha = (health * 255 / 1000).toInt()
        canvas.drawCircle(position.x + radius, position.y + radius, radius, paint)
    }

    fun update(targets: List<Target>) {
        targets.forEach { target ->
            val dx = position.x + radius - target.x
            val dy = position.y + radius - target.y
            val distance = sqrt(dx * dx + dy * dy)

            if (distance < radius) {
                health -= 10
                target.health = 0
            }
        }
    }
}
