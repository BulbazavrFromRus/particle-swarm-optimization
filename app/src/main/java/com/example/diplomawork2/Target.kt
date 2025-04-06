package com.example.diplomawork2

data class Target(
    var x: Float,
    var y: Float,
    var health: Int = 1000,
    var isBeingDragged: Boolean = false,
    var speed: Float = 3.5f
)
