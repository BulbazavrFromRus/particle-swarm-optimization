package com.example.diplomawork2

data class Target(
    var x: Float, // Измените val на var
    var y: Float, // Измените val на var
    var health: Int = 100,
    var isBeingDragged: Boolean = false // Добавьте флаг перемещения
)

