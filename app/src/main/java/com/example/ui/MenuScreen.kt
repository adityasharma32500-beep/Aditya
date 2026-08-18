package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.Levels
import com.example.viewmodel.GameState
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: GameViewModel,
    state: GameState,
    onLevelSelected: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Path Arrows", fontWeight = FontWeight.Bold) },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Hearts", tint = Color.Red)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${state.profile.hearts}", fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Icon(Icons.Default.Star, contentDescription = "Coins", tint = Color(0xFFFFD700))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${state.profile.coins}", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Level",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(Levels.allLevels) { level ->
                    val isUnlocked = level.id <= state.profile.currentLevelId
                    LevelCard(
                        levelId = level.id,
                        difficulty = level.difficulty,
                        isUnlocked = isUnlocked,
                        onClick = {
                            if (isUnlocked) {
                                if (state.profile.hearts > 0) {
                                    onLevelSelected(level.id)
                                } else {
                                    // Show no hearts message (could use a snackbar or dialog)
                                }
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (state.profile.hearts == 0) {
                Button(onClick = { viewModel.buyHeart() }) {
                    Text("Buy Heart (50 Coins)")
                }
            }
        }
    }
}

@Composable
fun LevelCard(levelId: Int, difficulty: String, isUnlocked: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer else Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$levelId",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = difficulty,
                fontSize = 12.sp,
                color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer else Color.DarkGray
            )
        }
    }
}
