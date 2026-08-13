package com.pjdev.data.source.local.mapper

import com.pjdev.data.source.local.entity.EpisodeEntity
import com.pjdev.domain.model.Episode
import java.time.LocalDate

fun EpisodeEntity.toEpisode(): Episode {
    return Episode(
        id = id,
        name = name,
        code = code,
        airDate = LocalDate.parse(airDate),
    )
}
