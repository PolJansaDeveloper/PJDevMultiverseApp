package com.pjdev.domain.repository

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getCharacters(
        name: String?,
    ): Flow<PagingData<Character>>

    suspend fun getCharacterDetail(
        id: Int,
    ): CharacterDetail
}
