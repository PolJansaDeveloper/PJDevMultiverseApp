package com.pjdev.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "episodes",
)
data class EpisodeEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val code: String,
    val airDate: String,
)
