package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.Coordinate
import com.example.game.Direction
import com.example.game.Snake
import com.example.viewmodel.GameState
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    state: GameState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level ${state.currentLevel.id}") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetGame()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox(label = "Moves", value = "${state.remainingMoves}")
                StatBox(label = "Time", value = "${state.timeRemaining}s")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Game Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(16.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            ) {
                GameGridCanvas(
                    state = state,
                    onSnakeTapped = { snakeId ->
                        viewModel.onSnakeTapped(snakeId)
                    }
                )
            }

            if (state.isGameOver) {
                Spacer(modifier = Modifier.height(32.dp))
                if (state.isWin) {
                    Text(
                        text = "Level Complete!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("+50 Coins")
                } else {
                    Text(
                        text = "Game Over!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFF44336),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    viewModel.resetGame()
                    onBack()
                }) {
                    Text("Return to Menu")
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 24.sp)
    }
}

@Composable
fun GameGridCanvas(
    state: GameState,
    onSnakeTapped: (Int) -> Unit
) {
    val gridSize = 8 // 8x8 grid
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .pointerInput(state.activeSnakes) {
                detectTapGestures { offset ->
                    val cellSize = size.width / gridSize
                    val tapX = (offset.x / cellSize).toInt()
                    val tapY = (offset.y / cellSize).toInt()

                    val tappedSnake = state.activeSnakes.find { snake ->
                        snake.body.any { it.x == tapX && it.y == tapY }
                    }
                    if (tappedSnake != null) {
                        onSnakeTapped(tappedSnake.id)
                    }
                }
            }
    ) {
        val cellSize = size.width / gridSize
        val cornerRadius = CornerRadius(cellSize * 0.4f, cellSize * 0.4f)
        val inset = cellSize * 0.1f

        // Draw grid lines (optional)
        for (i in 0..gridSize) {
            drawLine(
                color = Color.LightGray,
                start = Offset(i * cellSize, 0f),
                end = Offset(i * cellSize, size.height),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, i * cellSize),
                end = Offset(size.width, i * cellSize),
                strokeWidth = 2f
            )
        }

        // Draw active snakes
        for (snake in state.activeSnakes) {
            drawSnake(snake, cellSize, inset, cornerRadius, Offset.Zero)
        }

        // Draw animating snakes
        for (snake in state.animatingSnakes) {
            // Need a Compose Animation wrapper for each animating snake to move it
            // For simplicity in Canvas, we'll draw them based on their state, 
            // but true animation requires managing a List of Animatable states.
            // Let's implement a simple translation based on a fast coroutine if needed, 
            // or just let them disappear. We will use a quick hack with a LaunchedEffect below.
        }
    }
    
    // We can layer an animating canvas on top for animating snakes
    AnimatingSnakesOverlay(state.animatingSnakes)
}

@Composable
fun AnimatingSnakesOverlay(animatingSnakes: List<Snake>) {
    val gridSize = 8
    
    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        animatingSnakes.forEach { snake ->
            // Create an animation state for this snake
            val translation = remember { Animatable(0f) }
            
            LaunchedEffect(snake.id) {
                translation.animateTo(
                    targetValue = 10f, // Move 10 cells away
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                )
            }
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = size.width / gridSize
                val cornerRadius = CornerRadius(cellSize * 0.4f, cellSize * 0.4f)
                val inset = cellSize * 0.1f
                
                val offset = Offset(
                    x = snake.dir.dx * translation.value * cellSize,
                    y = snake.dir.dy * translation.value * cellSize
                )
                
                drawSnake(snake, cellSize, inset, cornerRadius, offset)
            }
        }
    }
}

fun DrawScope.drawSnake(
    snake: Snake,
    cellSize: Float,
    inset: Float,
    cornerRadius: CornerRadius,
    animOffset: Offset
) {
    // Draw the body segments
    for (i in snake.body.indices) {
        val segment = snake.body[i]
        val left = segment.x * cellSize + inset + animOffset.x
        val top = segment.y * cellSize + inset + animOffset.y
        val rectSize = cellSize - inset * 2
        
        drawRoundRect(
            color = snake.color,
            topLeft = Offset(left, top),
            size = Size(rectSize, rectSize),
            cornerRadius = cornerRadius
        )
        
        // Draw head indicator (arrow)
        if (i == 0) {
            val cx = left + rectSize / 2
            val cy = top + rectSize / 2
            val headPath = Path().apply {
                when (snake.dir) {
                    Direction.UP -> {
                        moveTo(cx - rectSize * 0.3f, cy + rectSize * 0.2f)
                        lineTo(cx, cy - rectSize * 0.3f)
                        lineTo(cx + rectSize * 0.3f, cy + rectSize * 0.2f)
                    }
                    Direction.DOWN -> {
                        moveTo(cx - rectSize * 0.3f, cy - rectSize * 0.2f)
                        lineTo(cx, cy + rectSize * 0.3f)
                        lineTo(cx + rectSize * 0.3f, cy - rectSize * 0.2f)
                    }
                    Direction.LEFT -> {
                        moveTo(cx + rectSize * 0.2f, cy - rectSize * 0.3f)
                        lineTo(cx - rectSize * 0.3f, cy)
                        lineTo(cx + rectSize * 0.2f, cy + rectSize * 0.3f)
                    }
                    Direction.RIGHT -> {
                        moveTo(cx - rectSize * 0.2f, cy - rectSize * 0.3f)
                        lineTo(cx + rectSize * 0.3f, cy)
                        lineTo(cx - rectSize * 0.2f, cy + rectSize * 0.3f)
                    }
                }
            }
            drawPath(
                path = headPath,
                color = Color.White,
                style = Stroke(width = 4f)
            )
        }
        
        // Connect segments (visual only, for corners)
        if (i < snake.body.size - 1) {
            val nextSegment = snake.body[i + 1]
            val cx1 = segment.x * cellSize + cellSize / 2 + animOffset.x
            val cy1 = segment.y * cellSize + cellSize / 2 + animOffset.y
            val cx2 = nextSegment.x * cellSize + cellSize / 2 + animOffset.x
            val cy2 = nextSegment.y * cellSize + cellSize / 2 + animOffset.y
            
            drawLine(
                color = snake.color,
                start = Offset(cx1, cy1),
                end = Offset(cx2, cy2),
                strokeWidth = rectSize
            )
        }
    }
}
