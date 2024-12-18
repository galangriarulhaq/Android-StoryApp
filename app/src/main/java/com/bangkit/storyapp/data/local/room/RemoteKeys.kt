package com.bangkit.storyapp.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val storyId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
