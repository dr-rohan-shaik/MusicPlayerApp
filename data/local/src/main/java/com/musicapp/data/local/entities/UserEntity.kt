package com.musicapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?,
    val avatarUrl: String?,
    val tier: Int, // 0=FREE, 1=PREMIUM
    val country: String?,
    val createdAt: Long
)