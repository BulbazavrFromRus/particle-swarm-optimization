package com.example.diplomawork2

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class GameStateTest {
    @Test
    fun testStopGameLoss() {
        val state = GameState()

        state.stopGame(victory = false)

        assertFalse(state.isGameActive)
        assertTrue(state.isGameFinished)
        assertFalse(state.isVictory)
        //assertTrue(state.isVictory)
    }

    @Test
    fun testNextLevel() {
        val state = GameState(level = 1, gameDuration = 30f)
        state.startNewGame()
        state.nextLevel()

        //в случае если тесты не выполняется в ошибке вылетает сообщение, которое стоит на первом месте в тестах
        assertEquals("Уровень должен увеличиться на 1",2, state.level)
        assertTrue("Игра должна быть активной после перехода на следующий уровень",state.isGameActive)
        assertFalse("Игра не должна быть завершена после перехода на следующий уровень",state.isGameFinished)
        assertFalse("Победа должна быть сброшена после перехода на следующий уровень",state.isVictory)
        assertEquals("Таймер должен быть сброшен на длительность игры",30f, state.gameTimer)
    }

    @Test
    fun testStopGameVictory() {
        val state = GameState()
        state.stopGame(victory = true)

        assertFalse("Игра должна быть неактивной после остановки", state.isGameActive)
        assertTrue("Игра должна быть завершена после остановки", state.isGameFinished)
        assertTrue("Победа должна быть установлена, если остановка с победой", state.isVictory)

    }

}
