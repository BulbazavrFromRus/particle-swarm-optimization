package com.example.diplomawork2
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test



class PulseAnimationTest {


    @Test
    fun testPulseActivatesWhenTimerBelowWarning(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val particleView = ParticleView(context)

        // Устанавливаем параметры для теста
        particleView.warningTimeMillis = 10_000L
        particleView.gameTimer = 9_000f // 9 секунд - меньше warningTimeMillis
        particleView.pulseScale = 1f
        particleView.pulseIncreasing = true
        particleView.pulseStep = 0.02f
        particleView.pulseMinScale = 1f
        particleView.pulseMaxScale = 1.3f

        // Вызываем метод обновления пульсации
        particleView.updatePulse()

        // Проверяем, что пульсация активна — pulseScale увеличилось
        assertTrue("Pulse scale should increase when timer below warning",
            particleView.pulseScale > 1f)
    }
}