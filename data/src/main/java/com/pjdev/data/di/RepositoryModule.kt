package com.pjdev.data.di

import com.pjdev.data.repository.CharacterRepositoryImpl
import com.pjdev.domain.repository.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        implementation: CharacterRepositoryImpl,
    ): CharacterRepository
}
