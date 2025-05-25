package com.example.diplomawork2

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class PulseAnimationTest {

    @Test
    fun pulseIncreasesAndDecreasesBetweenMinAndMaxScale() {
        val pulse = PulseAnimation(warningTimeSeconds = 10f)
        pulse.pulseScale = 1f
        pulse.pulseIncreasing = true

        pulse.update(5f) // время меньше warningTimeSeconds
        assertTrue(pulse.pulseScale > 1f)

        pulse.pulseScale = 1.3f
        pulse.pulseIncreasing = true
        pulse.update(5f)
        assertTrue(!pulse.pulseIncreasing) // переключение направления
    }

    @Test
    fun pulseResetsWhenTimeAboveWarningThreshold() {
        val pulse = PulseAnimation(warningTimeSeconds = 10f)
        pulse.pulseScale = 1.2f
        pulse.update(20f) // время больше warningTimeSeconds
        assertEquals(1f, pulse.pulseScale)
    }
}
