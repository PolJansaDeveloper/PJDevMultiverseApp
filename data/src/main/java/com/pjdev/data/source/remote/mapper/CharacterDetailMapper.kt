package com.pjdev.data.source.remote.mapper

import com.pjdev.data.source.remote.dto.CharacterDto
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.model.Episode

fun CharacterDto.toCharacterDetail(
    episodes: List<Episode>,
): CharacterDetail {
    return CharacterDetail(
        id = id,
        name = name,
        imageUrl = image,
        status = status,
        species = species,
        origin = origin.name,
        location = location.name,
        episodes = episodes,
    )
}
