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
    private val particleCount = 100
    private val particles = mutableListOf<Particle>()
    private val paint = Paint()

    // Класс для представления частицы
    data class Particle(var position: PointF, var velocity: PointF, var radius: Float, var color: Int)

    init {
        // Инициализация частиц
        for (i in 0 until particleCount) {
            val x = Random.nextFloat() * width
            val y = Random.nextFloat() * height
            val radius = Random.nextFloat() * 10 + 5 // Радиус от 5 до 15
            val speedX = Random.nextFloat() * 4 - 2 // Скорость от -2 до 2
            val speedY = Random.nextFloat() * 4 - 2 // Скорость от -2 до 2
            val color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)) // Случайный цвет

            // Создаем частицы с заданными параметрами
            particles.add(Particle(PointF(x, y), PointF(speedX, speedY), radius, color))
        }

        // Обработчик для обновления экрана
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                updateParticles() // Обновляем частицы
                invalidate() // Перерисовываем экран
                handler.postDelayed(this, 16) // 60 FPS (16 ms)
            }
        })
    }

    private fun updateParticles() {
        for (particle in particles) {
            // Обновление позиции частицы
            particle.position.x += particle.velocity.x
            particle.position.y += particle.velocity.y

            // Проверка на границы экрана
            if (particle.position.x < 0 || particle.position.x > width) {
                particle.velocity.x = -particle.velocity.x // Реверсируем направление по оси x
            }
            if (particle.position.y < 0 || particle.position.y > height) {
                particle.velocity.y = -particle.velocity.y // Реверсируем направление по оси y
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK) // Задний фон

        for (particle in particles) {
            paint.color = particle.color
            canvas.drawCircle(particle.position.x, particle.position.y, particle.radius, paint)
        }
    }
}
