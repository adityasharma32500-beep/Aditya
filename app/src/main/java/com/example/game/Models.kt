package com.example.game

import androidx.compose.ui.graphics.Color

data class Coordinate(val x: Int, val y: Int)

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0)
}

data class Snake(
    val id: Int,
    val color: Color,
    val dir: Direction,
    val body: List<Coordinate> // body[0] is the head
)

data class Level(
    val id: Int,
    val moves: Int,
    val timeLimit: Int, // seconds
    val difficulty: String, // "Easy", "Medium", "Hard", "Normal"
    val snakes: List<Snake>
)
