package com.pjdev.data.source.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.pjdev.data.source.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query(
        """
        SELECT *
        FROM remote_keys
        WHERE searchQuery = :searchQuery
        LIMIT 1
        """
    )
    suspend fun getRemoteKey(
        searchQuery: String,
    ): RemoteKeyEntity?

    @Upsert
    suspend fun upsertRemoteKey(
        remoteKey: RemoteKeyEntity,
    )

    @Query(
        """
        DELETE FROM remote_keys
        WHERE searchQuery = :searchQuery
        """
    )
    suspend fun clearRemoteKey(
        searchQuery: String,
    )
}

