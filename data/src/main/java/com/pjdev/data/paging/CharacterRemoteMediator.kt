package com.pjdev.data.paging

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pjdev.data.source.local.database.MultiverseDatabase
import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.data.source.local.entity.RemoteKeyEntity
import com.pjdev.data.source.remote.api.RickAndMortyApi
import com.pjdev.data.source.remote.error.toDomainFailure
import com.pjdev.data.source.remote.mapper.toEntity
import com.pjdev.data.source.remote.mapper.toQueryEntities
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import retrofit2.HttpException
import kotlin.time.Duration.Companion.milliseconds
import androidx.paging.ExperimentalPagingApi

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val api: RickAndMortyApi,
    private val database: MultiverseDatabase,
    name: String?,
) : RemoteMediator<Int, CharacterEntity>() {

    private val remoteName = name
        ?.trim()
        ?.takeIf { it.isNotBlank() }

    private val searchQuery = remoteName
        ?.lowercase(Locale.ROOT)
        .orEmpty()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE

            LoadType.PREPEND -> {
                return MediatorResult.Success(
                    endOfPaginationReached = true,
                )
            }

            LoadType.APPEND -> {
                val remoteKey = database
                    .remoteKeyDao()
                    .getRemoteKey(searchQuery)

                val nextPage = remoteKey?.nextPage
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = true,
                    )

                nextPage
            }
        }

        return try {
            val response = loadPageWithRateLimitRetry(
                page = page,
            )

            val endOfPaginationReached =
                response.info.next == null

            database.withTransaction {
                val characterDao = database.characterDao()
                val remoteKeyDao = database.remoteKeyDao()

                if (loadType == LoadType.REFRESH) {
                    characterDao.clearCharacterQuery(
                        searchQuery = searchQuery,
                    )

                    remoteKeyDao.clearRemoteKey(
                        searchQuery = searchQuery,
                    )
                }

                val startPosition = characterDao
                    .getCharacterQueryCount(
                        searchQuery = searchQuery,
                    )

                characterDao.upsertCharacters(
                    characters = response.results.map {
                        it.toEntity()
                    },
                )

                characterDao.upsertCharacterQueries(
                    characterQueries = response.results
                        .toQueryEntities(
                            searchQuery = searchQuery,
                            startPosition = startPosition,
                        ),
                )

                remoteKeyDao.upsertRemoteKey(
                    RemoteKeyEntity(
                        searchQuery = searchQuery,
                        nextPage = if (endOfPaginationReached) {
                            null
                        } else {
                            page + 1
                        },
                    ),
                )
            }

            MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached,
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Throwable) {
            MediatorResult.Error(
                exception.toDomainFailure(),
            )
        }
    }

    private suspend fun loadPageWithRateLimitRetry(
        page: Int,
    ) = try {
        api.getCharacters(
            page = page,
            name = remoteName,
        )
    } catch (exception: HttpException) {
        if (exception.code() != HTTP_TOO_MANY_REQUESTS) {
            throw exception
        }

        delay(
            rateLimitRetryDelay(
                exception = exception,
            ).milliseconds,
        )

        api.getCharacters(
            page = page,
            name = remoteName,
        )
    }

    private fun rateLimitRetryDelay(
        exception: HttpException,
    ): Long {
        val retryAfterSeconds = exception
            .response()
            ?.headers()
            ?.get(RETRY_AFTER_HEADER)
            ?.toLongOrNull()

        return retryAfterSeconds
            ?.times(MILLIS_PER_SECOND)
            ?.coerceIn(
                minimumValue = MIN_RETRY_DELAY_MILLIS,
                maximumValue = MAX_RETRY_DELAY_MILLIS,
            )
            ?: DEFAULT_RETRY_DELAY_MILLIS
    }

    private companion object {
        const val INITIAL_PAGE = 1

        const val HTTP_TOO_MANY_REQUESTS = 429
        const val RETRY_AFTER_HEADER = "Retry-After"

        const val MILLIS_PER_SECOND = 1_000L
        const val DEFAULT_RETRY_DELAY_MILLIS = 1_000L
        const val MIN_RETRY_DELAY_MILLIS = 750L
        const val MAX_RETRY_DELAY_MILLIS = 3_000L
    }
}