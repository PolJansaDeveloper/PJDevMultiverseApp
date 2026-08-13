package com.pjdev.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pjdev.data.source.local.database.MultiverseDatabase
import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.data.source.local.entity.CharacterEpisodeCrossRef
import com.pjdev.data.source.local.entity.EpisodeEntity
import com.pjdev.data.source.remote.api.RickAndMortyApi
import com.pjdev.data.source.remote.dto.CharacterDto
import com.pjdev.data.source.remote.dto.EpisodeDto
import com.pjdev.data.source.remote.dto.LocationDto
import com.pjdev.domain.error.DomainException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CharacterRepositoryImplTest {

    private lateinit var api: RickAndMortyApi
    private lateinit var database: MultiverseDatabase
    private lateinit var repository: CharacterRepositoryImpl

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

        repository = CharacterRepositoryImpl(
            api = api,
            database = database,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `online detail stores character and single episode in Room`() =
        runTest {
            val characterDto = createCharacterDto(
                episodeUrls = listOf(
                    episodeUrl(EPISODE_ONE_ID),
                ),
            )

            val episodeDto = createEpisodeDto(
                id = EPISODE_ONE_ID,
                name = "Pilot",
                code = "S01E01",
            )

            coEvery {
                api.getCharacter(CHARACTER_ID)
            } returns characterDto

            coEvery {
                api.getEpisode(EPISODE_ONE_ID)
            } returns episodeDto

            val result = repository.getCharacterDetail(
                id = CHARACTER_ID,
            )

            assertEquals(
                CHARACTER_ID,
                result.id,
            )

            assertEquals(
                "Rick Sanchez",
                result.name,
            )

            assertEquals(
                1,
                result.episodes.size,
            )

            assertEquals(
                "Pilot",
                result.episodes.first().name,
            )

            assertEquals(
                LocalDate.of(2013, 12, 2),
                result.episodes.first().airDate,
            )

            val cachedCharacter = database
                .characterDao()
                .getCharacterById(
                    characterId = CHARACTER_ID,
                )

            val cachedEpisodes = database
                .episodeDao()
                .getEpisodesForCharacter(
                    characterId = CHARACTER_ID,
                )

            assertNotNull(cachedCharacter)

            assertEquals(
                "Rick Sanchez",
                cachedCharacter?.name,
            )

            assertEquals(
                1,
                cachedEpisodes.size,
            )

            assertEquals(
                "Pilot",
                cachedEpisodes.first().name,
            )

            coVerify(exactly = 1) {
                api.getCharacter(CHARACTER_ID)
            }

            coVerify(exactly = 1) {
                api.getEpisode(EPISODE_ONE_ID)
            }

            coVerify(exactly = 0) {
                api.getEpisodes(any())
            }
        }

    @Test
    fun `multiple episodes preserve character episode order`() =
        runTest {
            val characterDto = createCharacterDto(
                episodeUrls = listOf(
                    episodeUrl(EPISODE_TWO_ID),
                    episodeUrl(EPISODE_ONE_ID),
                ),
            )

            val firstEpisodeDto = createEpisodeDto(
                id = EPISODE_ONE_ID,
                name = "Pilot",
                code = "S01E01",
            )

            val secondEpisodeDto = createEpisodeDto(
                id = EPISODE_TWO_ID,
                name = "Lawnmower Dog",
                code = "S01E02",
            )

            coEvery {
                api.getCharacter(CHARACTER_ID)
            } returns characterDto

            /*
             * The API response is intentionally returned in a different order
             * to verify that Room uses the cross-reference position.
             */
            coEvery {
                api.getEpisodes("2,1")
            } returns listOf(
                firstEpisodeDto,
                secondEpisodeDto,
            )

            val result = repository.getCharacterDetail(
                id = CHARACTER_ID,
            )

            assertEquals(
                2,
                result.episodes.size,
            )

            assertEquals(
                "Lawnmower Dog",
                result.episodes[0].name,
            )

            assertEquals(
                "Pilot",
                result.episodes[1].name,
            )

            coVerify(exactly = 1) {
                api.getEpisodes("2,1")
            }

            coVerify(exactly = 0) {
                api.getEpisode(any())
            }
        }

    @Test
    fun `network failure returns cached detail when available`() =
        runTest {
            insertCachedCharacterDetail()

            coEvery {
                api.getCharacter(CHARACTER_ID)
            } throws IOException("No internet connection")

            val result = repository.getCharacterDetail(
                id = CHARACTER_ID,
            )

            assertEquals(
                CHARACTER_ID,
                result.id,
            )

            assertEquals(
                "Cached Rick",
                result.name,
            )

            assertEquals(
                1,
                result.episodes.size,
            )

            assertEquals(
                "Cached Pilot",
                result.episodes.first().name,
            )

            coVerify(exactly = 1) {
                api.getCharacter(CHARACTER_ID)
            }
        }

    @Test
    fun `network failure without cache returns network domain error`() =
        runTest {
            coEvery {
                api.getCharacter(CHARACTER_ID)
            } throws IOException("No internet connection")

            val failure = runCatching {
                repository.getCharacterDetail(
                    id = CHARACTER_ID,
                )
            }.exceptionOrNull()

            assertTrue(
                failure is DomainException.Network,
            )

            coVerify(exactly = 1) {
                api.getCharacter(CHARACTER_ID)
            }
        }

    private suspend fun insertCachedCharacterDetail() {
        database.characterDao().upsertCharacters(
            characters = listOf(
                CharacterEntity(
                    id = CHARACTER_ID,
                    name = "Cached Rick",
                    status = "Alive",
                    species = "Human",
                    origin = "Cached Earth",
                    location = "Cached Citadel",
                    imageUrl = "https://example.com/cached-rick.jpg",
                    episodeCount = 1,
                ),
            ),
        )

        database.episodeDao().upsertEpisodes(
            episodes = listOf(
                EpisodeEntity(
                    id = EPISODE_ONE_ID,
                    name = "Cached Pilot",
                    code = "S01E01",
                    airDate = "2013-12-02",
                ),
            ),
        )

        database
            .episodeDao()
            .upsertCharacterEpisodeCrossRefs(
                crossRefs = listOf(
                    CharacterEpisodeCrossRef(
                        characterId = CHARACTER_ID,
                        episodeId = EPISODE_ONE_ID,
                        position = 0,
                    ),
                ),
            )
    }

    private fun createCharacterDto(
        episodeUrls: List<String>,
    ): CharacterDto {
        return CharacterDto(
            id = CHARACTER_ID,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            origin = LocationDto(
                name = "Earth",
            ),
            location = LocationDto(
                name = "Citadel of Ricks",
            ),
            image = "https://example.com/rick.jpg",
            episode = episodeUrls,
        )
    }

    private fun createEpisodeDto(
        id: Int,
        name: String,
        code: String,
    ): EpisodeDto {
        return EpisodeDto(
            id = id,
            name = name,
            airDate = "December 2, 2013",
            episode = code,
        )
    }

    private fun episodeUrl(
        episodeId: Int,
    ): String {
        return "https://rickandmortyapi.com/api/episode/$episodeId"
    }

    private companion object {
        const val CHARACTER_ID = 1

        const val EPISODE_ONE_ID = 1
        const val EPISODE_TWO_ID = 2
    }
}