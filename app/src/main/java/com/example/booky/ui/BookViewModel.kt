package com.example.booky.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booky.data.api.BookDoc
import com.example.booky.data.local.BookEntity
import com.example.booky.data.pref.AppSettings
import com.example.booky.data.pref.LayoutType
import com.example.booky.data.pref.UserPreferences
import com.example.booky.data.repository.BookRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class BookViewModel(
    private val repository: BookRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _searchResult = MutableStateFlow<List<BookDoc>>(emptyList())
    
    private val _isOnlyAvailable = MutableStateFlow(false)
    val isOnlyAvailable: StateFlow<Boolean> = _isOnlyAvailable.asStateFlow()

    val searchResult: StateFlow<List<BookDoc>> = combine(_searchResult, _isOnlyAvailable) { results, onlyAvailable ->
        if (onlyAvailable) {
            results.filter { it.ia?.isNotEmpty() == true }
        } else {
            results
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedBook = MutableStateFlow<BookDoc?>(null)
    val selectedBook: StateFlow<BookDoc?> = _selectedBook.asStateFlow()

    val settings: StateFlow<AppSettings> = userPreferences.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val savedBooks = repository.getSavedBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedBooks = repository.getDownloadedBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadInitialBooks()
    }

    private fun loadInitialBooks() {
        val randomPage = Random.nextInt(1, 10)
        searchBooks("Arabic", randomPage)
    }

    fun toggleAvailabilityFilter() {
        _isOnlyAvailable.value = !_isOnlyAvailable.value
    }

    fun selectBook(book: BookDoc) {
        _selectedBook.value = book
    }

    fun selectBookFromEntity(entity: BookEntity) {
        _selectedBook.value = BookDoc(
            key = entity.id,
            title = entity.title,
            authorNames = entity.author.split(",").map { it.trim() },
            firstPublishYear = null,
            subjects = entity.subjects?.split(",")?.map { it.trim() },
            coverI = null,
            languages = null,
            ia = entity.iaIds?.split(",")?.map { it.trim() },
            formats = null
        )
    }

    fun clearSelectedBook() {
        _selectedBook.value = null
    }

    fun searchBooks(query: String, page: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.searchBooks(query, page)
                _searchResult.value = response.docs
            } catch (e: Exception) {
                _searchResult.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.searchByCategory(category)
                _searchResult.value = response.docs
            } catch (e: Exception) {
                _searchResult.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveBook(book: BookDoc) {
        viewModelScope.launch {
            repository.saveBook(book)
        }
    }

    fun updateDownloadStatus(id: String, isDownloaded: Boolean, path: String?) {
        viewModelScope.launch {
            repository.updateDownloadStatus(id, isDownloaded, path)
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            userPreferences.updateFontSize(size)
        }
    }

    fun updateFontFamily(family: String) {
        viewModelScope.launch {
            userPreferences.updateFontFamily(family)
        }
    }

    fun updateThemeColor(color: Int) {
        viewModelScope.launch {
            userPreferences.updateThemeColor(color)
        }
    }
    
    fun updateDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            userPreferences.updateDarkMode(isDark)
        }
    }

    fun updateLayoutType(layoutType: LayoutType) {
        viewModelScope.launch {
            userPreferences.updateLayoutType(layoutType)
        }
    }
}
