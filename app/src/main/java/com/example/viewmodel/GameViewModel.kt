package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.UserProfile
import com.example.game.Levels
import com.example.game.Snake
import com.example.game.Level
import com.example.game.Coordinate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class GameState(
    val profile: UserProfile = UserProfile(),
    val isGameActive: Boolean = false,
    val currentLevel: Level = Levels.allLevels[0],
    val activeSnakes: List<Snake> = emptyList(),
    val animatingSnakes: List<Snake> = emptyList(), // Store snakes that are currently flying out
    val remainingMoves: Int = 0,
    val timeRemaining: Int = 0,
    val isWin: Boolean = false,
    val isGameOver: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val userProfileDao = AppDatabase.getDatabase(application).userProfileDao()
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null
    private var isPlaying = false

    init {
        viewModelScope.launch {
            userProfileDao.getUserProfile().collect { profile ->
                if (profile == null) {
                    userProfileDao.insertProfile(UserProfile())
                } else {
                    _uiState.value = _uiState.value.copy(profile = profile)
                }
            }
        }
    }

    fun startGame(levelId: Int) {
        val level = Levels.getLevel(levelId)
        _uiState.value = _uiState.value.copy(
            isGameActive = true,
            currentLevel = level,
            activeSnakes = level.snakes,
            animatingSnakes = emptyList(),
            remainingMoves = level.moves,
            timeRemaining = level.timeLimit,
            isWin = false,
            isGameOver = false
        )
        isPlaying = true
        startTimer()
        
        // Deduct heart
        viewModelScope.launch {
            if (_uiState.value.profile.hearts > 0) {
                userProfileDao.addHearts(-1)
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isPlaying && _uiState.value.timeRemaining > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(timeRemaining = _uiState.value.timeRemaining - 1)
                if (_uiState.value.timeRemaining <= 0) {
                    gameOver(false)
                }
            }
        }
    }

    fun onSnakeTapped(snakeId: Int) {
        if (!isPlaying) return
        val state = _uiState.value
        val snake = state.activeSnakes.find { it.id == snakeId } ?: return

        // Raycast logic to check if blocked
        var isBlocked = false
        val head = snake.body[0]
        var currentX = head.x + snake.dir.dx
        var currentY = head.y + snake.dir.dy

        while (currentX in -2..10 && currentY in -2..10) {
            val blockedByOther = state.activeSnakes.any { otherSnake ->
                otherSnake.id != snake.id && otherSnake.body.any { it.x == currentX && it.y == currentY }
            }
            if (blockedByOther) {
                isBlocked = true
                break
            }
            currentX += snake.dir.dx
            currentY += snake.dir.dy
        }

        if (!isBlocked) {
            // Remove from active, add to animating
            val newActive = state.activeSnakes.filter { it.id != snake.id }
            val newAnimating = state.animatingSnakes + snake
            val newMoves = state.remainingMoves - 1

            _uiState.value = state.copy(
                activeSnakes = newActive,
                animatingSnakes = newAnimating,
                remainingMoves = newMoves
            )

            // Remove from animating after delay (animation duration)
            viewModelScope.launch {
                delay(500)
                _uiState.value = _uiState.value.copy(
                    animatingSnakes = _uiState.value.animatingSnakes.filter { it.id != snake.id }
                )
            }

            checkWinCondition(newActive, newMoves)
        } else {
            // Can't move. You can add a shake animation effect if you want
            val newMoves = state.remainingMoves - 1
            _uiState.value = state.copy(remainingMoves = newMoves)
            checkWinCondition(state.activeSnakes, newMoves)
        }
    }

    private fun checkWinCondition(activeSnakes: List<Snake>, remainingMoves: Int) {
        if (activeSnakes.isEmpty()) {
            gameOver(true)
        } else if (remainingMoves <= 0) {
            gameOver(false)
        }
    }

    private fun gameOver(win: Boolean) {
        isPlaying = false
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isWin = win, isGameOver = true)

        viewModelScope.launch {
            if (win) {
                userProfileDao.addCoins(50)
                val currentLevelId = _uiState.value.profile.currentLevelId
                if (currentLevelId == _uiState.value.currentLevel.id) {
                    val nextLevel = currentLevelId + 1
                    userProfileDao.updateCurrentLevel(nextLevel)
                }
            }
        }
    }
    
    fun resetGame() {
        _uiState.value = _uiState.value.copy(
            isGameActive = false,
            isGameOver = false,
            isWin = false
        )
    }

    fun buyHeart() {
        viewModelScope.launch {
            val profile = _uiState.value.profile
            if (profile.coins >= 50) {
                userProfileDao.addCoins(-50)
                userProfileDao.addHearts(1)
            }
        }
    }
}
