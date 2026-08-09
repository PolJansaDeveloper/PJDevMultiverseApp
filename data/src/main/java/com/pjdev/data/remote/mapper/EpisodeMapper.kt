package com.pjdev.data.remote.mapper

import com.pjdev.data.remote.dto.EpisodeDto
import com.pjdev.domain.model.Episode
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val episodeDateFormatter =
    DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

fun EpisodeDto.toEpisode(): Episode {
    return Episode(
        id = id,
        name = name,
        code = episode,
        airDate = LocalDate.parse(airDate, episodeDateFormatter),
    )
}
