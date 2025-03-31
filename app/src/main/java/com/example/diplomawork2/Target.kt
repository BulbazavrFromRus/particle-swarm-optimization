package com.example.diplomawork2

data class Target(
    var x: Float,
    var y: Float,
    var health: Int = 100,
    var isBeingDragged: Boolean = false,
    var speed: Float = 1.5f
)
