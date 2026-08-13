package com.pjdev.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pjdev.data.source.local.entity.CharacterEpisodeCrossRef
import com.pjdev.data.source.local.entity.EpisodeEntity

@Dao
interface EpisodeDao {

    @Upsert
    suspend fun upsertEpisodes(
        episodes: List<EpisodeEntity>,
    )

    @Upsert
    suspend fun upsertCharacterEpisodeCrossRefs(
        crossRefs: List<CharacterEpisodeCrossRef>,
    )

    @Query(
        """
        SELECT episodes.*
        FROM episodes
        INNER JOIN character_episode_cross_ref AS cross_ref
            ON episodes.id = cross_ref.episodeId
        WHERE cross_ref.characterId = :characterId
        ORDER BY cross_ref.position ASC
        """
    )
    suspend fun getEpisodesForCharacter(
        characterId: Int,
    ): List<EpisodeEntity>

    @Query(
        """
        DELETE FROM character_episode_cross_ref
        WHERE characterId = :characterId
        """
    )
    suspend fun clearCharacterEpisodeCrossRefs(
        characterId: Int,
    )
}