package com.example.booky.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val subjects: String?, // Comma-separated
    val lastReadPage: Int = 0,
    val dateSaved: Long = System.currentTimeMillis(),
    val iaIds: String? = null, // Comma-separated Internet Archive IDs
    val localFilePath: String? = null,
    val isDownloaded: Boolean = false
)
