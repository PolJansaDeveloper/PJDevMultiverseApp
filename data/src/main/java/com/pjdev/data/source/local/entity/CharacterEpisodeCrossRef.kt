package com.pjdev.data.source.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "character_episode_cross_ref",
    primaryKeys = ["characterId", "episodeId"],
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = EpisodeEntity::class,
            parentColumns = ["id"],
            childColumns = ["episodeId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["characterId"]),
        Index(value = ["episodeId"]),
    ],
)
data class CharacterEpisodeCrossRef(
    val characterId: Int,
    val episodeId: Int,
)
