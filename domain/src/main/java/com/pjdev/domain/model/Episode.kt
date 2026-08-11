package com.pjdev.domain.model

import java.time.LocalDate

data class Episode(
    val id: Int,
    val name: String,
    val code: String,
    val airDate: LocalDate
)
