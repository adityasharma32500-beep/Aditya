package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Only one user profile
    val coins: Int = 0,
    val hearts: Int = 5,
    val lastHeartRegenTime: Long = 0,
    val currentLevelId: Int = 1,
    val unlockedAvatars: String = "1", // comma separated IDs
    val selectedAvatar: Int = 1
)
