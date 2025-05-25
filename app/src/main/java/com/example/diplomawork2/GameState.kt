package com.example.diplomawork2

class GameState(
    var isGameActive: Boolean = false,
    var isGameFinished: Boolean = false,
    var isVictory: Boolean = false,
    var victoryAnimationShown: Boolean = false,
    var lossAnimationShown: Boolean = false,
    var level: Int = 1,
    var gameDuration: Float = 30.000f,
    var gameTimer: Float = 0f
) {
    fun startNewGame() {
        isGameActive = true
        isGameFinished = false
        isVictory = false
        victoryAnimationShown = false
        lossAnimationShown = false
        gameTimer = gameDuration
    }

    fun stopGame(victory: Boolean) {
        isGameActive = false
        isGameFinished = true
        isVictory = victory
    }

    fun nextLevel() {
        level++
        startNewGame()
    }
}
