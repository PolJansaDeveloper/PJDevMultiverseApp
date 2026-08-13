package com.pjdev.data.remote.mapper

import com.pjdev.data.source.remote.dto.CharacterDto
import com.pjdev.data.source.remote.dto.LocationDto
import com.pjdev.data.source.remote.mapper.toCharacterDetail
import com.pjdev.domain.model.Episode
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CharacterDetailMapperTest {

    @Test
    fun `toCharacterDetail maps character dto and episodes to domain model`() {
        val characterDto = CharacterDto(
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
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
            ),
        )

        val episodes = listOf(
            Episode(
                id = 1,
                name = "Pilot",
                code = "S01E01",
                airDate = LocalDate.of(2013, 12, 2),
            ),
        )

        val result = characterDto.toCharacterDetail(episodes)

        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals("https://example.com/rick.jpg", result.imageUrl)
        assertEquals("Alive", result.status)
        assertEquals("Human", result.species)
        assertEquals("Earth", result.origin)
        assertEquals("Citadel of Ricks", result.location)
        assertEquals(episodes, result.episodes)
    }
}
