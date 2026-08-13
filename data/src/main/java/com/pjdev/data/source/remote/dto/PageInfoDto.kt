package com.pjdev.data.source.remote.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PageInfoDto(
    val next: String?,
    val prev: String?,
)
