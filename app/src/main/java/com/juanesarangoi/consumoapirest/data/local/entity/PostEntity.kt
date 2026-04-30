package com.juanesarangoi.consumoapirest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
