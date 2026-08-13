package com.pjdev.pjdevmultiverseapp.navigation

import kotlinx.serialization.Serializable

@Serializable
data object CharacterListDestination

@Serializable
data class CharacterDetailDestination(
    val characterId: Int,
)