package com.example.booky.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM saved_books ORDER BY dateSaved DESC")
    fun getAllSavedBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM saved_books WHERE isDownloaded = 1 ORDER BY dateSaved DESC")
    fun getDownloadedBooks(): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBook(book: BookEntity)

    @Delete
    suspend fun deleteBook(book: BookEntity)

    @Query("SELECT * FROM saved_books WHERE id = :id")
    suspend fun getBookById(id: String): BookEntity?
    
    @Query("UPDATE saved_books SET isDownloaded = :isDownloaded, localFilePath = :path WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean, path: String?)
}
