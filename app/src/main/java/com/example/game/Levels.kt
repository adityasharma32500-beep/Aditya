package com.example.game

import androidx.compose.ui.graphics.Color

object GameColors {
    val Orange = Color(0xFFFFA500)
    val LightBlue = Color(0xFF00BFFF)
    val Pink = Color(0xFFFF69B4)
    val Green = Color(0xFF32CD32)
    val Red = Color(0xFFFF4500)
    val Purple = Color(0xFF8A2BE2)
    val DarkBlue = Color(0xFF00008B)
    val Yellow = Color(0xFFFFD700)
    val Cyan = Color(0xFF00FFFF)
}

object Levels {
    val allLevels = listOf(
        Level(
            id = 1,
            moves = 4,
            timeLimit = 60,
            difficulty = "Easy",
            snakes = listOf(
                Snake(
                    id = 1,
                    color = GameColors.Orange,
                    dir = Direction.UP,
                    body = listOf(Coordinate(2, 2), Coordinate(2, 3), Coordinate(2, 4), Coordinate(2, 5), Coordinate(2, 6))
                ),
                Snake(
                    id = 2,
                    color = GameColors.LightBlue,
                    dir = Direction.LEFT,
                    body = listOf(Coordinate(2, 6), Coordinate(3, 6), Coordinate(3, 5), Coordinate(3, 4), Coordinate(3, 3), Coordinate(3, 2))
                ),
                Snake(
                    id = 3,
                    color = GameColors.Pink,
                    dir = Direction.RIGHT,
                    body = listOf(Coordinate(5, 2), Coordinate(4, 2), Coordinate(4, 3), Coordinate(4, 4), Coordinate(4, 5), Coordinate(4, 6))
                ),
                Snake(
                    id = 4,
                    color = GameColors.Green,
                    dir = Direction.DOWN,
                    body = listOf(Coordinate(5, 6), Coordinate(5, 5), Coordinate(5, 4), Coordinate(5, 3), Coordinate(5, 2))
                )
            )
        ),
        Level(
            id = 2,
            moves = 4,
            timeLimit = 60,
            difficulty = "Normal",
            snakes = listOf(
                Snake(
                    id = 1,
                    color = GameColors.Red,
                    dir = Direction.UP,
                    body = listOf(Coordinate(1, 2), Coordinate(1, 3), Coordinate(2, 3), Coordinate(2, 2), Coordinate(3, 2), Coordinate(3, 3), Coordinate(4, 3), Coordinate(4, 2), Coordinate(5, 2), Coordinate(5, 3), Coordinate(6, 3), Coordinate(6, 2))
                ),
                Snake(
                    id = 2,
                    color = GameColors.Purple,
                    dir = Direction.LEFT,
                    body = listOf(Coordinate(1, 6), Coordinate(2, 6), Coordinate(2, 5), Coordinate(1, 5), Coordinate(1, 4), Coordinate(2, 4), Coordinate(3, 4))
                ),
                Snake(
                    id = 3,
                    color = GameColors.Pink,
                    dir = Direction.DOWN,
                    body = listOf(Coordinate(4, 6), Coordinate(4, 5), Coordinate(3, 5), Coordinate(3, 4), Coordinate(4, 4))
                ),
                Snake(
                    id = 4,
                    color = GameColors.DarkBlue,
                    dir = Direction.RIGHT,
                    body = listOf(Coordinate(6, 6), Coordinate(5, 6), Coordinate(5, 5), Coordinate(6, 5), Coordinate(6, 4), Coordinate(5, 4), Coordinate(4, 4)) // Adjust slightly so it doesn't overlap perfectly
                )
            )
        ),
        Level(
            id = 3,
            moves = 5,
            timeLimit = 45,
            difficulty = "Hard",
            snakes = listOf(
                Snake(
                    id = 1,
                    color = GameColors.Yellow,
                    dir = Direction.UP,
                    body = listOf(Coordinate(3,3), Coordinate(3,4), Coordinate(4,4))
                )
            )
        ),
        Level(
            id = 4,
            moves = 6,
            timeLimit = 90,
            difficulty = "Hard",
            snakes = listOf(
                Snake(
                    id = 1,
                    color = GameColors.Red,
                    dir = Direction.UP,
                    body = listOf(Coordinate(5,1), Coordinate(5,2), Coordinate(5,3), Coordinate(5,4), Coordinate(5,5), Coordinate(5,6), Coordinate(4,6), Coordinate(4,5), Coordinate(3,5), Coordinate(3,6), Coordinate(2,6), Coordinate(2,5), Coordinate(2,4), Coordinate(2,3), Coordinate(2,2), Coordinate(3,2), Coordinate(4,2))
                )
            )
        ),
        Level(
            id = 5,
            moves = 8,
            timeLimit = 60,
            difficulty = "Normal",
            snakes = listOf(
                Snake(
                    id = 1,
                    color = GameColors.Red,
                    dir = Direction.RIGHT,
                    body = listOf(Coordinate(6, 3), Coordinate(5, 3), Coordinate(4, 3), Coordinate(3, 3), Coordinate(2, 3), Coordinate(2, 2))
                ),
                Snake(
                    id = 2,
                    color = GameColors.LightBlue,
                    dir = Direction.DOWN,
                    body = listOf(Coordinate(4, 6), Coordinate(4, 5), Coordinate(4, 4), Coordinate(5, 4), Coordinate(6, 4))
                ),
                Snake(
                    id = 3,
                    color = GameColors.Green,
                    dir = Direction.LEFT,
                    body = listOf(Coordinate(2, 5), Coordinate(3, 5), Coordinate(4, 5), Coordinate(5, 5), Coordinate(6, 5))
                )
            )
        )
    )

    fun getLevel(id: Int): Level {
        return allLevels.find { it.id == id } ?: allLevels.first()
    }
}
