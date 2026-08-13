package com.pjdev.data.source.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.pjdev.data.source.local.database.MultiverseDatabase
import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.data.source.local.entity.RemoteKeyEntity
import com.pjdev.data.source.remote.api.RickAndMortyApi
import com.pjdev.data.source.remote.dto.CharacterResponseDto
import com.pjdev.data.source.remote.error.toDomainFailure
import com.pjdev.data.source.remote.mapper.toEntity
import com.pjdev.data.source.remote.mapper.toQueryEntities
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import retrofit2.HttpException
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val api: RickAndMortyApi,
    private val database: MultiverseDatabase,
    name: String?,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis,
) : RemoteMediator<Int, CharacterEntity>() {

    private val remoteName = name
        ?.trim()
        ?.takeIf { it.isNotBlank() }

    private val searchQuery = remoteName
        ?.lowercase(Locale.ROOT)
        .orEmpty()

    override suspend fun initialize(): InitializeAction {
        val cachedCharacterCount = database
            .characterDao()
            .getCharacterQueryCount(
                searchQuery = searchQuery,
            )

        val remoteKey = database
            .remoteKeyDao()
            .getRemoteKey(
                searchQuery = searchQuery,
            )

        if (
            cachedCharacterCount == 0 ||
            remoteKey == null
        ) {
            return InitializeAction.LAUNCH_INITIAL_REFRESH
        }

        val cacheAgeMillis =
            currentTimeMillis() - remoteKey.lastUpdatedAtMillis

        val isCacheFresh =
            cacheAgeMillis <= CACHE_TIMEOUT.inWholeMilliseconds

        /*
         * Fresh cached data is displayed immediately without an unnecessary
         * remote refresh. Stale data remains available while Paging refreshes.
         */
        return if (isCacheFresh) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>,
    ): MediatorResult {
        val page = resolvePage(
            loadType = loadType,
        ) ?: return MediatorResult.Success(
            endOfPaginationReached = true,
        )

        val loadResult = runCatching {
            loadAndPersistPage(
                loadType = loadType,
                page = page,
            )
        }

        val failure = loadResult.exceptionOrNull()

        return if (failure == null) {
            loadResult.getOrThrow()
        } else {
            handleLoadFailure(
                loadType = loadType,
                throwable = failure,
            )
        }
    }

    private suspend fun resolvePage(
        loadType: LoadType,
    ): Int? {
        return when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE

            LoadType.PREPEND -> null

            LoadType.APPEND -> database
                .remoteKeyDao()
                .getRemoteKey(
                    searchQuery = searchQuery,
                )
                ?.nextPage
        }
    }

    private suspend fun loadAndPersistPage(
        loadType: LoadType,
        page: Int,
    ): MediatorResult {
        val response = loadPageWithRateLimitRetry(
            page = page,
        )

        val endOfPaginationReached =
            response.info.next == null

        persistPage(
            loadType = loadType,
            page = page,
            response = response,
            requestCompletedAtMillis = currentTimeMillis(),
        )

        return MediatorResult.Success(
            endOfPaginationReached = endOfPaginationReached,
        )
    }

    private suspend fun persistPage(
        loadType: LoadType,
        page: Int,
        response: CharacterResponseDto,
        requestCompletedAtMillis: Long,
    ) {
        database.withTransaction {
            val characterDao = database.characterDao()
            val remoteKeyDao = database.remoteKeyDao()

            val previousRemoteKey = remoteKeyDao
                .getRemoteKey(
                    searchQuery = searchQuery,
                )

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
                characters = response.results.map { characterDto ->
                    characterDto.toEntity()
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
                remoteKey = RemoteKeyEntity(
                    searchQuery = searchQuery,
                    nextPage = if (response.info.next == null) {
                        null
                    } else {
                        page + 1
                    },
                    lastUpdatedAtMillis = resolveCacheTimestamp(
                        loadType = loadType,
                        previousRemoteKey = previousRemoteKey,
                        requestCompletedAtMillis =
                            requestCompletedAtMillis,
                    ),
                ),
            )
        }
    }

    private fun resolveCacheTimestamp(
        loadType: LoadType,
        previousRemoteKey: RemoteKeyEntity?,
        requestCompletedAtMillis: Long,
    ): Long {
        return if (loadType == LoadType.REFRESH) {
            requestCompletedAtMillis
        } else {
            previousRemoteKey
                ?.lastUpdatedAtMillis
                ?: requestCompletedAtMillis
        }
    }

    private suspend fun handleLoadFailure(
        loadType: LoadType,
        throwable: Throwable,
    ): MediatorResult {
        return when {
            throwable is CancellationException -> {
                throw throwable
            }

            throwable is HttpException &&
                    throwable.code() == HTTP_NOT_FOUND &&
                    remoteName != null -> {
                handleSearchNotFound(
                    loadType = loadType,
                )
            }

            else -> {
                MediatorResult.Error(
                    throwable.toDomainFailure(),
                )
            }
        }
    }

    private suspend fun handleSearchNotFound(
        loadType: LoadType,
    ): MediatorResult {
        database.withTransaction {
            val characterDao = database.characterDao()
            val remoteKeyDao = database.remoteKeyDao()

            when (loadType) {
                LoadType.REFRESH -> {
                    /*
                     * A filtered 404 represents a successful search with no
                     * results, so stale memberships must be removed.
                     */
                    characterDao.clearCharacterQuery(
                        searchQuery = searchQuery,
                    )

                    remoteKeyDao.clearRemoteKey(
                        searchQuery = searchQuery,
                    )
                }

                LoadType.APPEND -> {
                    /*
                     * Preserve cached search results while marking remote
                     * pagination as complete.
                     */
                    val currentRemoteKey = remoteKeyDao
                        .getRemoteKey(
                            searchQuery = searchQuery,
                        )

                    remoteKeyDao.upsertRemoteKey(
                        remoteKey = RemoteKeyEntity(
                            searchQuery = searchQuery,
                            nextPage = null,
                            lastUpdatedAtMillis = currentRemoteKey
                                ?.lastUpdatedAtMillis
                                ?: currentTimeMillis(),
                        ),
                    )
                }

                LoadType.PREPEND -> Unit
            }
        }

        return MediatorResult.Success(
            endOfPaginationReached = true,
        )
    }

    private suspend fun loadPageWithRateLimitRetry(
        page: Int,
    ): CharacterResponseDto {
        return try {
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

        const val HTTP_NOT_FOUND = 404
        const val HTTP_TOO_MANY_REQUESTS = 429

        const val RETRY_AFTER_HEADER = "Retry-After"

        const val MILLIS_PER_SECOND = 1_000L
        const val DEFAULT_RETRY_DELAY_MILLIS = 1_000L
        const val MIN_RETRY_DELAY_MILLIS = 750L
        const val MAX_RETRY_DELAY_MILLIS = 3_000L

        val CACHE_TIMEOUT = 24.hours
    }
}
