package com.pjdev.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pjdev.data.source.local.dao.CharacterDao
import com.pjdev.data.source.local.dao.EpisodeDao
import com.pjdev.data.source.local.dao.RemoteKeyDao
import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.data.source.local.entity.CharacterEpisodeCrossRef
import com.pjdev.data.source.local.entity.CharacterQueryEntity
import com.pjdev.data.source.local.entity.EpisodeEntity
import com.pjdev.data.source.local.entity.RemoteKeyEntity

@Database(
    entities = [
        CharacterEntity::class,
        CharacterQueryEntity::class,
        RemoteKeyEntity::class,
        EpisodeEntity::class,
        CharacterEpisodeCrossRef::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class MultiverseDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao

    abstract fun episodeDao(): EpisodeDao

    abstract fun remoteKeyDao(): RemoteKeyDao
}
