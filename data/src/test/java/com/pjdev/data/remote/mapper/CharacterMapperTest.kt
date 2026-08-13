package com.pjdev.data.remote.mapper

import com.pjdev.data.source.remote.dto.CharacterDto
import com.pjdev.data.source.remote.dto.LocationDto
import com.pjdev.data.source.remote.mapper.toCharacter
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterMapperTest {

    @Test
    fun `toCharacter maps character dto to domain model`() {
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
                "https://rickandmortyapi.com/api/episode/2",
            ),
        )

        val result = characterDto.toCharacter()

        assertEquals(1, result.id)
        assertEquals("Rick Sanchez", result.name)
        assertEquals("https://example.com/rick.jpg", result.imageUrl)
        assertEquals(2, result.episodeCount)
    }
}
