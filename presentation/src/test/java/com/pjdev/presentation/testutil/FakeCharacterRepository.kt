package com.pjdev.presentation.testutil

import androidx.paging.PagingData
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCharacterRepository : CharacterRepository {

    val requestedNames = mutableListOf<String?>()
    val requestedCharacterIds = mutableListOf<Int>()

    var characterDetail: CharacterDetail? = null
    var detailException: Throwable? = null

    override fun getCharacters(
        name: String?,
    ): Flow<PagingData<Character>> {
        requestedNames += name
        return flowOf(PagingData.empty())
    }

    override suspend fun getCharacterDetail(
        id: Int,
    ): CharacterDetail {
        requestedCharacterIds += id

        detailException?.let { throw it }

        return checkNotNull(characterDetail) {
            "Character detail was not configured for this test."
        }
    }
}
