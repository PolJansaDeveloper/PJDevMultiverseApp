package com.pjdev.domain.model

data class CharacterDetail(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val status: String,
    val species: String,
    val origin: String,
    val location: String,
    val episodes: List<Episode>
)
