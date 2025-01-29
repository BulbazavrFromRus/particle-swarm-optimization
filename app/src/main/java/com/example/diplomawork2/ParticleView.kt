package com.example.diplomawork2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class ParticleView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val particleCount = 10
    private val particles = mutableListOf<Particle>()
    private val paint = Paint()

    // Инициализация частиц
    init {
        for (i in 0 until particleCount) {
            val x = Random.nextFloat() * width
            val y = Random.nextFloat() * height
            val radius = Random.nextFloat() * 10 + 5 // Радиус от 5 до 15
            val speedX = Random.nextFloat() * 2 - 1 // Случайная скорость от -1 до 1
            val speedY = Random.nextFloat() * 2 - 1 // Случайная скорость от -1 до 1
            val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)) // Случайный цвет

            particles.add(Particle(PointF(x, y), PointF(speedX, speedY), radius, color))
        }

        // Обновление экрана
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                updateParticles()
                invalidate() // Перерисовываем экран
                handler.postDelayed(this, 16) // 60 FPS (16 ms)
            }
        })
    }

    // Обновляем частицы
    private fun updateParticles() {
        val screenWidth = width.toFloat()
        val screenHeight = height.toFloat()

        for (particle in particles) {
            particle.update(particles, screenWidth, screenHeight)
        }
    }

    // Рисование частиц на экране
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK) // Задний фон

        for (particle in particles) {
            paint.color = particle.color
            canvas.drawCircle(particle.position.x, particle.position.y, particle.radius, paint)
        }
    }
}
