package com.example.diplomawork2

class PulseAnimation(
    var pulseScale: Float = 1f,
    var pulseIncreasing: Boolean = true,
    val pulseMinScale: Float = 1f,
    val pulseMaxScale: Float = 1.3f,
    val pulseStep: Float = 0.02f,
    val warningTimeSeconds: Float = 10f
) {
    fun update(gameTimerSeconds: Float) {
        if (gameTimerSeconds <= warningTimeSeconds) {
            if (pulseIncreasing) {
                pulseScale += pulseStep
                if (pulseScale >= pulseMaxScale) pulseIncreasing = false
            } else {
                pulseScale -= pulseStep
                if (pulseScale <= pulseMinScale) pulseIncreasing = true
            }
        } else {
            pulseScale = 1f
        }
    }
}
