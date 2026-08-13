package com.pjdev.data.source.remote.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pjdev.data.source.local.database.MultiverseDatabase
import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.data.source.local.entity.CharacterQueryEntity
import com.pjdev.data.source.local.entity.RemoteKeyEntity
import com.pjdev.data.source.remote.api.RickAndMortyApi
import com.pjdev.data.source.remote.dto.CharacterDto
import com.pjdev.data.source.remote.dto.CharacterResponseDto
import com.pjdev.data.source.remote.dto.LocationDto
import com.pjdev.data.source.remote.dto.PageInfoDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CharacterRemoteMediatorTest {

    private lateinit var api: RickAndMortyApi
    private lateinit var database: MultiverseDatabase

    @Before
    fun setUp() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room
            .inMemoryDatabaseBuilder(
                context,
                MultiverseDatabase::class.java,
            )
            .allowMainThreadQueries()
            .build()

        api = mockk()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `initialize launches refresh when cache is empty`() =
        runTest {
            val mediator = createMediator(
                name = null,
            )

            val result = mediator.initialize()

            assertEquals(
                RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH,
                result,
            )
        }

    @Test
    fun `initialize skips refresh when cache is fresh`() =
        runTest {
            insertCachedQuery(
                searchQuery = SEARCH_QUERY,
                lastUpdatedAtMillis =
                    CURRENT_TIME_MILLIS -
                            23.hours.inWholeMilliseconds,
            )

            val mediator = createMediator(
                name = SEARCH_NAME,
            )

            val result = mediator.initialize()

            assertEquals(
                RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH,
                result,
            )
        }

    @Test
    fun `initialize launches refresh when cache is stale`() =
        runTest {
            insertCachedQuery(
                searchQuery = SEARCH_QUERY,
                lastUpdatedAtMillis =
                    CURRENT_TIME_MILLIS -
                            25.hours.inWholeMilliseconds,
            )

            val mediator = createMediator(
                name = SEARCH_NAME,
            )

            val result = mediator.initialize()

            assertEquals(
                RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH,
                result,
            )
        }

    @Test
    fun `refresh stores characters and creates next remote key`() =
        runTest {
            val characterDto = createCharacterDto(
                id = CHARACTER_ONE_ID,
                name = "Rick Sanchez",
            )

            coEvery {
                api.getCharacters(
                    page = 1,
                    name = null,
                )
            } returns createResponse(
                characters = listOf(characterDto),
                hasNextPage = true,
            )

            val result = load(
                mediator = createMediator(
                    name = null,
                ),
                loadType = LoadType.REFRESH,
            )

            assertSuccessfulLoad(
                result = result,
                endOfPaginationReached = false,
            )

            val cachedCharacter = database
                .characterDao()
                .getCharacterById(
                    characterId = CHARACTER_ONE_ID,
                )

            val remoteKey = remoteKey(
                searchQuery = "",
            )

            assertNotNull(cachedCharacter)
            assertEquals(
                "Rick Sanchez",
                cachedCharacter?.name,
            )
            assertEquals(
                1,
                cachedCount(""),
            )
            assertEquals(
                2,
                remoteKey?.nextPage,
            )
            assertEquals(
                CURRENT_TIME_MILLIS,
                remoteKey?.lastUpdatedAtMillis,
            )

            coVerify(exactly = 1) {
                api.getCharacters(
                    page = 1,
                    name = null,
                )
            }
        }

    @Test
    fun `append uses stored next page and preserves refresh timestamp`() =
        runTest {
            insertCachedQuery(
                searchQuery = SEARCH_QUERY,
                lastUpdatedAtMillis = FRESH_CACHE_TIME_MILLIS,
                nextPage = 2,
            )

            coEvery {
                api.getCharacters(
                    page = 2,
                    name = SEARCH_NAME,
                )
            } returns createResponse(
                characters = listOf(
                    createCharacterDto(
                        id = CHARACTER_TWO_ID,
                        name = "Morty Smith",
                    ),
                ),
                hasNextPage = false,
            )

            val result = load(
                mediator = createMediator(
                    name = SEARCH_NAME,
                ),
                loadType = LoadType.APPEND,
            )

            assertSuccessfulLoad(
                result = result,
                endOfPaginationReached = true,
            )

            val remoteKey = remoteKey(
                searchQuery = SEARCH_QUERY,
            )

            assertEquals(
                2,
                cachedCount(SEARCH_QUERY),
            )
            assertNull(
                remoteKey?.nextPage,
            )
            assertEquals(
                FRESH_CACHE_TIME_MILLIS,
                remoteKey?.lastUpdatedAtMillis,
            )

            coVerify(exactly = 1) {
                api.getCharacters(
                    page = 2,
                    name = SEARCH_NAME,
                )
            }
        }

    @Test
    fun `refresh failure preserves existing cached data`() =
        runTest {
            insertCachedQuery(
                searchQuery = SEARCH_QUERY,
                lastUpdatedAtMillis = FRESH_CACHE_TIME_MILLIS,
                nextPage = 2,
            )

            coEvery {
                api.getCharacters(
                    page = 1,
                    name = SEARCH_NAME,
                )
            } throws IOException(
                "No internet connection",
            )

            val result = load(
                mediator = createMediator(
                    name = SEARCH_NAME,
                ),
                loadType = LoadType.REFRESH,
            )

            assertTrue(
                result is RemoteMediator.MediatorResult.Error,
            )

            val cachedCharacter = database
                .characterDao()
                .getCharacterById(
                    characterId = CHARACTER_ONE_ID,
                )

            val remoteKey = remoteKey(
                searchQuery = SEARCH_QUERY,
            )

            assertEquals(
                1,
                cachedCount(SEARCH_QUERY),
            )

            assertNotNull(
                cachedCharacter,
            )

            assertEquals(
                2,
                remoteKey?.nextPage,
            )

            assertEquals(
                FRESH_CACHE_TIME_MILLIS,
                remoteKey?.lastUpdatedAtMillis,
            )
        }

    @Test
    fun `search 404 clears query membership and returns successful empty result`() =
        runTest {
            insertCachedQuery(
                searchQuery = SEARCH_QUERY,
                lastUpdatedAtMillis = FRESH_CACHE_TIME_MILLIS,
            )

            coEvery {
                api.getCharacters(
                    page = 1,
                    name = SEARCH_NAME,
                )
            } throws createHttpException(
                code = HTTP_NOT_FOUND,
            )

            val result = load(
                mediator = createMediator(
                    name = SEARCH_NAME,
                ),
                loadType = LoadType.REFRESH,
            )

            assertSuccessfulLoad(
                result = result,
                endOfPaginationReached = true,
            )

            val remoteKey = remoteKey(
                searchQuery = SEARCH_QUERY,
            )

            val cachedCharacter = database
                .characterDao()
                .getCharacterById(
                    characterId = CHARACTER_ONE_ID,
                )

            assertEquals(
                0,
                cachedCount(SEARCH_QUERY),
            )

            assertNull(
                remoteKey,
            )

            /*
             * The shared entity remains cached.
             * Only its membership in this search is removed.
             */
            assertNotNull(
                cachedCharacter,
            )
        }

    @Test
    fun `append 404 preserves cached results and marks pagination complete`() =
        runTest {
            insertCachedQuery(
                searchQuery = SEARCH_QUERY,
                lastUpdatedAtMillis = FRESH_CACHE_TIME_MILLIS,
                nextPage = 2,
            )

            coEvery {
                api.getCharacters(
                    page = 2,
                    name = SEARCH_NAME,
                )
            } throws createHttpException(
                code = HTTP_NOT_FOUND,
            )

            val result = load(
                mediator = createMediator(
                    name = SEARCH_NAME,
                ),
                loadType = LoadType.APPEND,
            )

            assertSuccessfulLoad(
                result = result,
                endOfPaginationReached = true,
            )

            val remoteKey = remoteKey(
                searchQuery = SEARCH_QUERY,
            )

            assertEquals(
                1,
                cachedCount(SEARCH_QUERY),
            )

            assertNull(
                remoteKey?.nextPage,
            )

            assertEquals(
                FRESH_CACHE_TIME_MILLIS,
                remoteKey?.lastUpdatedAtMillis,
            )
        }

    private suspend fun load(
        mediator: CharacterRemoteMediator,
        loadType: LoadType,
    ): RemoteMediator.MediatorResult {
        return mediator.load(
            loadType = loadType,
            state = createPagingState(),
        )
    }

    private fun assertSuccessfulLoad(
        result: RemoteMediator.MediatorResult,
        endOfPaginationReached: Boolean,
    ) {
        assertTrue(
            result is RemoteMediator.MediatorResult.Success,
        )

        assertEquals(
            endOfPaginationReached,
            (result as RemoteMediator.MediatorResult.Success)
                .endOfPaginationReached,
        )
    }

    private suspend fun cachedCount(
        searchQuery: String,
    ): Int {
        return database
            .characterDao()
            .getCharacterQueryCount(
                searchQuery = searchQuery,
            )
    }

    private suspend fun remoteKey(
        searchQuery: String,
    ): RemoteKeyEntity? {
        return database
            .remoteKeyDao()
            .getRemoteKey(
                searchQuery = searchQuery,
            )
    }

    private fun createMediator(
        name: String?,
    ): CharacterRemoteMediator {
        return CharacterRemoteMediator(
            api = api,
            database = database,
            name = name,
            currentTimeMillis = {
                CURRENT_TIME_MILLIS
            },
        )
    }

    private suspend fun insertCachedQuery(
        searchQuery: String,
        lastUpdatedAtMillis: Long,
        nextPage: Int? = 2,
    ) {
        database.characterDao().upsertCharacters(
            characters = listOf(
                createCharacterEntity(
                    id = CHARACTER_ONE_ID,
                    name = "Cached Rick",
                ),
            ),
        )

        database.characterDao().upsertCharacterQueries(
            characterQueries = listOf(
                CharacterQueryEntity(
                    searchQuery = searchQuery,
                    characterId = CHARACTER_ONE_ID,
                    position = 0,
                ),
            ),
        )

        database.remoteKeyDao().upsertRemoteKey(
            remoteKey = RemoteKeyEntity(
                searchQuery = searchQuery,
                nextPage = nextPage,
                lastUpdatedAtMillis = lastUpdatedAtMillis,
            ),
        )
    }

    private fun createPagingState(): PagingState<Int, CharacterEntity> {
        return PagingState(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            leadingPlaceholderCount = 0,
        )
    }

    private fun createResponse(
        characters: List<CharacterDto>,
        hasNextPage: Boolean,
    ): CharacterResponseDto {
        return CharacterResponseDto(
            info = PageInfoDto(
                next = if (hasNextPage) {
                    "https://rickandmortyapi.com/api/character?page=2"
                } else {
                    null
                },
                prev = null,
            ),
            results = characters,
        )
    }

    private fun createCharacterDto(
        id: Int,
        name: String,
    ): CharacterDto {
        return CharacterDto(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            origin = LocationDto(
                name = "Earth",
            ),
            location = LocationDto(
                name = "Citadel of Ricks",
            ),
            image = "https://example.com/$id.jpg",
            episode = emptyList(),
        )
    }

    private fun createCharacterEntity(
        id: Int,
        name: String,
    ): CharacterEntity {
        return CharacterEntity(
            id = id,
            name = name,
            status = "Alive",
            species = "Human",
            origin = "Earth",
            location = "Citadel of Ricks",
            imageUrl = "https://example.com/$id.jpg",
            episodeCount = 0,
        )
    }

    private fun createHttpException(
        code: Int,
    ): HttpException {
        return HttpException(
            Response.error<CharacterResponseDto>(
                code,
                "".toResponseBody(null),
            ),
        )
    }

    private companion object {
        const val CHARACTER_ONE_ID = 1
        const val CHARACTER_TWO_ID = 2

        const val SEARCH_NAME = "Rick"
        const val SEARCH_QUERY = "rick"

        const val PAGE_SIZE = 20
        const val HTTP_NOT_FOUND = 404

        const val CURRENT_TIME_MILLIS = 1_800_000_000_000L

        const val FRESH_CACHE_TIME_MILLIS =
            CURRENT_TIME_MILLIS - 60_000L
    }
}
