package com.example.diplomawork2

import android.graphics.PointF
import kotlin.math.sqrt
import kotlin.random.Random

data class Particle(
    var position: PointF,
    var velocity: PointF,
    var radius: Float,
    var color: Int,
    var personalBestPosition: PointF = PointF(position.x, position.y),
    var personalBestValue: Float = Float.MAX_VALUE
) {

    /*Весовой коэффициент для силы сближения; управляет тем, насколько сильно частица стремится собраться с соседями.*/
    private val cohesionWeight = 0.000001f

    /*Увеличенный вес для разделения; управляет тем, насколько сильно частица отталкивается, если она слишком близко к другой частице.*/
    private val separationWeight = 1.1f

    /*Вес для согласованности, управляет тем, насколько сильно частица согласует свою скорость с соседями.*/
    private val alignmentWeight = 0.1f

    /**/
    private val minDistance = 50f

    fun update(particles: List<Particle>, screenWidth: Float, screenHeight: Float) {
        // Сброс векторов
        var cohesion = PointF(0f, 0f)
        var separation = PointF(0f, 0f)
        var alignment = PointF(0f, 0f)
        var neighborCount = 0

        // Рассматриваем соседей для создания векторов
        for (particle in particles) {

            /* Проверка, чтобы избежать самопроверки (частица не будет сравнивать сама с собой).*/
            if (particle != this) {

                /*: Вычисляет расстояние до соседней частицы.*/
                val distance = position.distanceTo(particle.position)

                // Сбор в кучу (Cohesion)
                cohesion.x += particle.position.x
                cohesion.y += particle.position.y

                // Разделение (Separation)
                if (distance < minDistance) {
                    separation.x -= (particle.position.x - position.x) // Отталкиваемся от слишком близкого соседа
                    separation.y -= (particle.position.y - position.y)
                }

                // Согласованность (Alignment)
                alignment.x += particle.velocity.x
                alignment.y += particle.velocity.y

                neighborCount++
            }
        }

        // Учитываем, если есть соседи
        if (neighborCount > 0) {
            cohesion.x /= neighborCount
            cohesion.y /= neighborCount
            cohesion.x = (cohesion.x - position.x) * cohesionWeight
            cohesion.y = (cohesion.y - position.y) * cohesionWeight

            separation.x /= neighborCount
            separation.y /= neighborCount
            separation.x *= separationWeight
            separation.y *= separationWeight

            alignment.x /= neighborCount
            alignment.y /= neighborCount
            alignment.x = (alignment.x - velocity.x) * alignmentWeight
            alignment.y = (alignment.y - velocity.y) * alignmentWeight
        }

        // Обновляем скорость с учетом всех трех векторов
        velocity.x += cohesion.x + separation.x + alignment.x
        velocity.y += cohesion.y + separation.y + alignment.y

        // Ограничиваем максимальную скорость
        val maxSpeed = 5.0f
        velocity.x = velocity.x.coerceIn(-maxSpeed, maxSpeed)
        velocity.y = velocity.y.coerceIn(-maxSpeed, maxSpeed)

        // Обновляем позицию
        position.x += velocity.x
        position.y += velocity.y

        // Проверка на границы
        if (position.x < 0) {
            position.x = 0f
            velocity.x = -velocity.x // Изменение направления на противоположное
        } else if (position.x > screenWidth) {
            position.x = screenWidth
            velocity.x = -velocity.x // Изменение направления на противоположное
        }

        if (position.y < 0) {
            position.y = 0f
            velocity.y = -velocity.y // Изменение направления на противоположное
        } else if (position.y > screenHeight) {
            position.y = screenHeight
            velocity.y = -velocity.y // Изменение направления на противоположное
        }
    }

    private fun PointF.distanceTo(other: PointF): Float {
        return sqrt(((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)).toDouble()).toFloat()
    }
}
