package com.pjdev.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.pjdev.data.paging.CharacterRemoteMediator
import com.pjdev.data.source.local.database.MultiverseDatabase
import com.pjdev.data.source.local.mapper.toCharacter
import com.pjdev.data.source.local.mapper.toCharacterDetail
import com.pjdev.data.source.local.mapper.toEpisode
import com.pjdev.data.source.remote.api.RickAndMortyApi
import com.pjdev.data.source.remote.error.toDomainFailure
import com.pjdev.data.source.remote.mapper.toEntity
import com.pjdev.data.source.remote.mapper.toEpisodeCrossRefs
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.repository.CharacterRepository
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi,
    private val database: MultiverseDatabase,
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(
        name: String?,
    ): Flow<PagingData<Character>> {
        val searchQuery = name
            ?.trim()
            ?.lowercase(Locale.ROOT)
            .orEmpty()

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            remoteMediator = CharacterRemoteMediator(
                api = api,
                database = database,
                name = name,
            ),
            pagingSourceFactory = {
                database
                    .characterDao()
                    .getCharactersPagingSource(
                        searchQuery = searchQuery,
                    )
            },
        ).flow.map { pagingData ->
            pagingData.map { characterEntity ->
                characterEntity.toCharacter()
            }
        }
    }

    override suspend fun getCharacterDetail(
        id: Int,
    ): CharacterDetail {
        val cachedCharacterDetail = getCachedCharacterDetail(
            characterId = id,
        )

        return try {
            refreshCharacterDetail(
                characterId = id,
            )

            getCachedCharacterDetail(
                characterId = id,
            ) ?: cachedCharacterDetail
            ?: throw IllegalStateException(
                "Character $id was not available after refresh.",
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (throwable: Throwable) {
            cachedCharacterDetail
                ?: throw throwable.toDomainFailure()
        }
    }

    private suspend fun refreshCharacterDetail(
        characterId: Int,
    ) {
        val characterDto = api.getCharacter(
            id = characterId,
        )

        val episodeIds = characterDto.episode.mapNotNull { episodeUrl ->
            episodeUrl
                .substringAfterLast('/')
                .toIntOrNull()
        }

        val episodeDtos = when (episodeIds.size) {
            0 -> emptyList()

            1 -> listOf(
                api.getEpisode(
                    id = episodeIds.first(),
                ),
            )

            else -> api.getEpisodes(
                ids = episodeIds.joinToString(","),
            )
        }

        database.withTransaction {
            val characterDao = database.characterDao()
            val episodeDao = database.episodeDao()

            characterDao.upsertCharacters(
                characters = listOf(
                    characterDto.toEntity(),
                ),
            )

            episodeDao.upsertEpisodes(
                episodes = episodeDtos.map { episodeDto ->
                    episodeDto.toEntity()
                },
            )

            episodeDao.clearCharacterEpisodeCrossRefs(
                characterId = characterId,
            )

            episodeDao.upsertCharacterEpisodeCrossRefs(
                crossRefs = characterDto.toEpisodeCrossRefs(),
            )
        }
    }

    private suspend fun getCachedCharacterDetail(
        characterId: Int,
    ): CharacterDetail? {
        val characterEntity = database
            .characterDao()
            .getCharacterById(
                characterId = characterId,
            )
            ?: return null

        val episodes = database
            .episodeDao()
            .getEpisodesForCharacter(
                characterId = characterId,
            )
            .map { episodeEntity ->
                episodeEntity.toEpisode()
            }

        return characterEntity.toCharacterDetail(
            episodes = episodes,
        )
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 5
    }
}