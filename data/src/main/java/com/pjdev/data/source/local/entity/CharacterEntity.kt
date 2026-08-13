package com.pjdev.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
)
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
    val episodeCount: Int,
)
