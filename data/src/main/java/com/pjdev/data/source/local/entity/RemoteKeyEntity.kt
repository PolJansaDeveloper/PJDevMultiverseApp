package com.pjdev.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "remote_keys",
)
data class RemoteKeyEntity(
    @PrimaryKey
    val searchQuery: String,
    val nextPage: Int?,
    @ColumnInfo(defaultValue = "0")
    val lastUpdatedAtMillis: Long,
)
