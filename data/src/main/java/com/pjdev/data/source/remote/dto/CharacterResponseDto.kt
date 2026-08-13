package com.pjdev.data.source.remote.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CharacterResponseDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>,
)
