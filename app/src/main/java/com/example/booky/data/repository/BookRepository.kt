package com.example.booky.data.repository

import com.example.booky.data.api.BookDoc
import com.example.booky.data.api.OpenLibraryApi
import com.example.booky.data.local.BookDao
import com.example.booky.data.local.BookEntity
import kotlinx.coroutines.flow.Flow

class BookRepository(
    private val api: OpenLibraryApi,
    private val bookDao: BookDao
) {
    suspend fun searchBooks(query: String, page: Int = 1) = api.searchBooks(query = query, page = page)

    suspend fun searchByAuthor(author: String) = api.searchBooks(author = author)

    suspend fun searchByCategory(subject: String) = api.searchBooks(subject = subject)

    fun getSavedBooks(): Flow<List<BookEntity>> = bookDao.getAllSavedBooks()

    fun getDownloadedBooks(): Flow<List<BookEntity>> = bookDao.getDownloadedBooks()

    suspend fun saveBook(book: BookDoc) {
        val existing = bookDao.getBookById(book.key)
        val entity = BookEntity(
            id = book.key,
            title = book.title,
            author = book.authorName,
            coverUrl = book.coverUrl,
            subjects = book.subjects?.joinToString(","),
            iaIds = book.ia?.joinToString(","),
            isDownloaded = existing?.isDownloaded ?: false,
            localFilePath = existing?.localFilePath
        )
        bookDao.saveBook(entity)
    }

    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean, path: String?) {
        bookDao.updateDownloadStatus(id, isDownloaded, path)
    }

    suspend fun deleteBook(book: BookEntity) {
        bookDao.deleteBook(book)
    }

    suspend fun isBookSaved(id: String): Boolean {
        return bookDao.getBookById(id) != null
    }
}
