package com.pjdev.data.repository

import com.pjdev.data.remote.api.RickAndMortyApi
import com.pjdev.data.remote.dto.CharacterDto
import com.pjdev.data.remote.dto.CharacterResponseDto
import com.pjdev.data.remote.dto.EpisodeDto
import com.pjdev.data.remote.dto.LocationDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class CharacterRepositoryImplTest {

    @Test
    fun `getCharacterDetail requests single episode when character has one episode`() = runTest {
        val api = FakeRickAndMortyApi(
            character = createCharacter(
                episodeUrls = listOf(
                    "https://rickandmortyapi.com/api/episode/1",
                ),
            ),
            singleEpisode = createEpisode(
                id = 1,
                name = "Pilot",
                code = "S01E01",
            ),
        )

        val repository = CharacterRepositoryImpl(api)

        val result = repository.getCharacterDetail(1)

        assertEquals(1, api.requestedEpisodeId)
        assertNull(api.requestedEpisodeIds)

        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals(1, result.episodes.size)

        assertEquals(
            LocalDate.of(2013, 12, 2),
            result.episodes.first().airDate,
        )
    }

    @Test
    fun `getCharacterDetail requests episodes in bulk when character has multiple episodes`() = runTest {
        val api = FakeRickAndMortyApi(
            character = createCharacter(
                episodeUrls = listOf(
                    "https://rickandmortyapi.com/api/episode/1",
                    "https://rickandmortyapi.com/api/episode/2",
                ),
            ),
            multipleEpisodes = listOf(
                createEpisode(
                    id = 1,
                    name = "Pilot",
                    code = "S01E01",
                ),
                createEpisode(
                    id = 2,
                    name = "Lawnmower Dog",
                    code = "S01E02",
                ),
            ),
        )

        val repository = CharacterRepositoryImpl(api)

        val result = repository.getCharacterDetail(1)

        assertNull(api.requestedEpisodeId)
        assertEquals("1,2", api.requestedEpisodeIds)

        assertEquals(2, result.episodes.size)
        assertEquals("Pilot", result.episodes[0].name)
        assertEquals("Lawnmower Dog", result.episodes[1].name)
    }

    private fun createCharacter(
        episodeUrls: List<String>,
    ): CharacterDto {
        return CharacterDto(
            id = 1,
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

    private fun createEpisode(
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

    private class FakeRickAndMortyApi(
        private val character: CharacterDto,
        private val singleEpisode: EpisodeDto? = null,
        private val multipleEpisodes: List<EpisodeDto> = emptyList(),
    ) : RickAndMortyApi {

        var requestedEpisodeId: Int? = null
            private set

        var requestedEpisodeIds: String? = null
            private set

        override suspend fun getCharacters(
            page: Int,
            name: String?,
        ): CharacterResponseDto {
            error("Not required for this test")
        }

        override suspend fun getCharacter(
            id: Int,
        ): CharacterDto {
            return character
        }

        override suspend fun getEpisode(
            id: Int,
        ): EpisodeDto {
            requestedEpisodeId = id
            return requireNotNull(singleEpisode)
        }

        override suspend fun getEpisodes(
            ids: String,
        ): List<EpisodeDto> {
            requestedEpisodeIds = ids
            return multipleEpisodes
        }
    }
}
