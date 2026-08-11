package com.pjdev.pjdevmultiverseapp.di

import com.pjdev.domain.repository.CharacterRepository
import com.pjdev.domain.usecase.GetCharacterDetailUseCase
import com.pjdev.domain.usecase.GetCharactersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideGetCharactersUseCase(
        characterRepository: CharacterRepository,
    ): GetCharactersUseCase {
        return GetCharactersUseCase(characterRepository)
    }

    @Provides
    fun provideGetCharacterDetailUseCase(
        characterRepository: CharacterRepository,
    ): GetCharacterDetailUseCase {
        return GetCharacterDetailUseCase(characterRepository)
    }
}
