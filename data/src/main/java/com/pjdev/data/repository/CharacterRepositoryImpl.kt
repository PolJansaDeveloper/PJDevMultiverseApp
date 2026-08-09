package com.pjdev.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pjdev.data.paging.CharacterPagingSource
import com.pjdev.data.remote.api.RickAndMortyApi
import com.pjdev.data.remote.mapper.toCharacterDetail
import com.pjdev.data.remote.mapper.toEpisode
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
) : CharacterRepository {

    override fun getCharacters(
        name: String?,
    ): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                CharacterPagingSource(
                    api = api,
                    name = name,
                )
            },
        ).flow
    }

    override suspend fun getCharacterDetail(
        id: Int,
    ): CharacterDetail {
        val characterDto = api.getCharacter(id)

        // Character responses contain episode URLs instead of episode objects.
        // Extracting their IDs lets us request the episode details in bulk.
        val episodeIds = characterDto.episode.mapNotNull { episodeUrl ->
            episodeUrl.substringAfterLast('/').toIntOrNull()
        }

        val episodes = when (episodeIds.size) {
            0 -> emptyList()

            // The API returns a single object for one ID and a list for multiple IDs.
            1 -> listOf(
                api.getEpisode(episodeIds.first()).toEpisode(),
            )

            else -> api
                .getEpisodes(episodeIds.joinToString(","))
                .map { it.toEpisode() }
        }

        return characterDto.toCharacterDetail(episodes)
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
