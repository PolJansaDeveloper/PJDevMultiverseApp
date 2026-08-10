package com.pjdev.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pjdev.data.paging.CharacterPagingSource
import com.pjdev.data.remote.api.RickAndMortyApi
import com.pjdev.data.remote.error.toDomainFailure
import com.pjdev.data.remote.mapper.toCharacterDetail
import com.pjdev.data.remote.mapper.toEpisode
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
) : CharacterRepository {

    override fun getCharacters(
        name: String?,
    ): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
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
        return runCatching {
            val character = api.getCharacter(id)

            val episodeIds = character.episode.mapNotNull { episodeUrl ->
                episodeUrl.substringAfterLast('/').toIntOrNull()
            }

            val episodes = when (episodeIds.size) {
                0 -> emptyList()

                1 -> listOf(
                    api.getEpisode(episodeIds.first()).toEpisode(),
                )

                else -> api.getEpisodes(
                    episodeIds.joinToString(","),
                ).map { episode ->
                    episode.toEpisode()
                }
            }

            character.toCharacterDetail(episodes)
        }.getOrElse { throwable ->
            throw throwable.toDomainFailure()
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 5
    }
}
