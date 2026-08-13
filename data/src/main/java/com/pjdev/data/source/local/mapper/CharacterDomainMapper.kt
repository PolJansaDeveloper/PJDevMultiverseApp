package com.pjdev.data.source.local.mapper

import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.domain.model.Character
import com.pjdev.domain.model.CharacterDetail
import com.pjdev.domain.model.Episode

fun CharacterEntity.toCharacter(): Character {
    return Character(
        id = id,
        name = name,
        imageUrl = imageUrl,
        episodeCount = episodeCount,
    )
}

fun CharacterEntity.toCharacterDetail(
    episodes: List<Episode>,
): CharacterDetail {
    return CharacterDetail(
        id = id,
        name = name,
        imageUrl = imageUrl,
        status = status,
        species = species,
        origin = origin,
        location = location,
        episodes = episodes,
    )
}