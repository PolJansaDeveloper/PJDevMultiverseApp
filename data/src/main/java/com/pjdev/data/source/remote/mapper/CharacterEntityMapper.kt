package com.pjdev.data.source.remote.mapper

import com.pjdev.data.source.local.entity.CharacterEntity
import com.pjdev.data.source.local.entity.CharacterEpisodeCrossRef
import com.pjdev.data.source.local.entity.CharacterQueryEntity
import com.pjdev.data.source.remote.dto.CharacterDto

fun CharacterDto.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        origin = origin.name,
        location = location.name,
        imageUrl = image,
        episodeCount = episode.size,
    )
}

fun List<CharacterDto>.toQueryEntities(
    searchQuery: String,
    startPosition: Int,
): List<CharacterQueryEntity> {
    return mapIndexed { index, character ->
        CharacterQueryEntity(
            searchQuery = searchQuery,
            characterId = character.id,
            position = startPosition + index,
        )
    }
}

fun CharacterDto.toEpisodeCrossRefs(): List<CharacterEpisodeCrossRef> {
    return episode.mapNotNull { episodeUrl ->
        val episodeId = episodeUrl
            .substringAfterLast('/')
            .toIntOrNull()

        episodeId?.let {
            CharacterEpisodeCrossRef(
                characterId = id,
                episodeId = it,
            )
        }
    }
}