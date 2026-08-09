package com.pjdev.data.remote.mapper

import com.pjdev.data.remote.dto.EpisodeDto
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class EpisodeMapperTest {

    @Test
    fun `toEpisode maps episode dto to domain model`() {
        val episodeDto = EpisodeDto(
            id = 1,
            name = "Pilot",
            airDate = "December 2, 2013",
            episode = "S01E01",
        )

        val result = episodeDto.toEpisode()

        assertEquals(1, result.id)
        assertEquals("Pilot", result.name)
        assertEquals("S01E01", result.code)
        assertEquals(
            LocalDate.of(2013, 12, 2),
            result.airDate,
        )
    }
}
