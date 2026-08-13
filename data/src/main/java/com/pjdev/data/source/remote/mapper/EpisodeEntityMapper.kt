package com.pjdev.data.source.remote.mapper

import com.pjdev.data.source.local.entity.EpisodeEntity
import com.pjdev.data.source.remote.dto.EpisodeDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val episodeDateFormatter =
    DateTimeFormatter.ofPattern(
        "MMMM d, yyyy",
        Locale.ENGLISH,
    )

fun EpisodeDto.toEntity(): EpisodeEntity {
    return EpisodeEntity(
        id = id,
        name = name,
        code = episode,
        airDate = LocalDate
            .parse(airDate, episodeDateFormatter)
            .toString(),
    )
}
