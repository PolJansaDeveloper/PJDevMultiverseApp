package com.pjdev.data.source.remote.mapper

import com.pjdev.data.source.remote.dto.CharacterDto
import com.pjdev.domain.model.Character

fun CharacterDto.toCharacter(): Character {
    return Character(
        id = id,
        name = name,
        imageUrl = image,
        episodeCount = episode.size,
    )
}
