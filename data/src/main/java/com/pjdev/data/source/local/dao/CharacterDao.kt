package com.pjdev.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.data.source.local.entity.CharacterQueryEntity

@Dao
interface CharacterDao {

    @Upsert
    suspend fun upsertCharacters(
        characters: List<CharacterEntity>,
    )

    @Upsert
    suspend fun upsertCharacterQueries(
        characterQueries: List<CharacterQueryEntity>,
    )

    @Query(
        """
        SELECT characters.*
        FROM characters
        INNER JOIN character_queries
            ON characters.id = character_queries.characterId
        WHERE character_queries.searchQuery = :searchQuery
        ORDER BY character_queries.position ASC
        """
    )
    fun getCharactersPagingSource(
        searchQuery: String,
    ): PagingSource<Int, CharacterEntity>

    @Query(
        """
        SELECT *
        FROM characters
        WHERE id = :characterId
        LIMIT 1
        """
    )
    suspend fun getCharacterById(
        characterId: Int,
    ): CharacterEntity?

    @Query(
        """
        SELECT COUNT(*)
        FROM character_queries
        WHERE searchQuery = :searchQuery
        """
    )
    suspend fun getCharacterQueryCount(
        searchQuery: String,
    ): Int

    @Query(
        """
        DELETE FROM character_queries
        WHERE searchQuery = :searchQuery
        """
    )
    suspend fun clearCharacterQuery(
        searchQuery: String,
    )
}

