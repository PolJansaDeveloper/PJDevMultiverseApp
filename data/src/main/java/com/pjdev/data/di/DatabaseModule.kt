package com.pjdev.data.di

import android.content.Context
import androidx.room.Room
import com.pjdev.data.source.local.dao.CharacterDao
import com.pjdev.data.source.local.dao.EpisodeDao
import com.pjdev.data.source.local.dao.RemoteKeyDao
import com.pjdev.data.source.local.database.MIGRATION_1_2
import com.pjdev.data.source.local.database.MIGRATION_2_3
import com.pjdev.data.source.local.database.MultiverseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMultiverseDatabase(
        @ApplicationContext context: Context,
    ): MultiverseDatabase {
        return Room.databaseBuilder(
            context,
            MultiverseDatabase::class.java,
            DATABASE_NAME,
        )
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(
        database: MultiverseDatabase,
    ): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideEpisodeDao(
        database: MultiverseDatabase,
    ): EpisodeDao {
        return database.episodeDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeyDao(
        database: MultiverseDatabase,
    ): RemoteKeyDao {
        return database.remoteKeyDao()
    }

    private const val DATABASE_NAME = "multiverse.db"
}
