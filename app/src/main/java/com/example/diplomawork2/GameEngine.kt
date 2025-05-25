package com.example.diplomawork2

class GameEngine(
    private val gameState: GameState,
    private val particleManager: ParticleManager,
    private val targetManager: TargetManager,
    private val base: Base

    ) {

    fun updateGame(width: Float, height: Float){
        if(gameState.isGameActive) {
            gameState.gameTimer -= 0.016f
            targetManager.updateTargets()
            particleManager.updateParticles(targetManager.targets, width, height)

            base.update(targetManager.targets)

        }
    }

}