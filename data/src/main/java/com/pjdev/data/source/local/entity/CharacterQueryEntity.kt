package com.pjdev.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "character_queries",
    primaryKeys = ["searchQuery", "characterId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["characterId"]),
        Index(value = ["searchQuery", "position"]),
    ],
)
data class CharacterQueryEntity(
    val searchQuery: String,
    val characterId: Int,
    val position: Int,
)