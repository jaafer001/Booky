package com.example.booky.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booky.data.api.BookDoc
import com.example.booky.data.local.BookEntity
import com.example.booky.data.pref.LayoutType

@Composable
fun SearchScreen(
    viewModel: BookViewModel,
    onBookClick: (BookDoc) -> Unit
) {
    val searchResults by viewModel.searchResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val savedBooks by viewModel.savedBooks.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isOnlyAvailable by viewModel.isOnlyAvailable.collectAsState()

    SearchContent(
        searchResults = searchResults,
        isLoading = isLoading,
        savedBooks = savedBooks,
        layoutType = settings.layoutType,
        isOnlyAvailable = isOnlyAvailable,
        onSearchQueryChange = { viewModel.searchBooks(it) },
        onCategoryClick = { viewModel.searchByCategory(it) },
        onSaveClick = { viewModel.saveBook(it) },
        onBookClick = onBookClick,
        onToggleLayout = {
            val newLayout = if (settings.layoutType == LayoutType.ROW) LayoutType.GRID else LayoutType.ROW
            viewModel.updateLayoutType(newLayout)
        },
        onToggleFilter = { viewModel.toggleAvailabilityFilter() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    searchResults: List<BookDoc>,
    isLoading: Boolean,
    savedBooks: List<BookEntity>,
    layoutType: LayoutType,
    isOnlyAvailable: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSaveClick: (BookDoc) -> Unit,
    onBookClick: (BookDoc) -> Unit,
    onToggleLayout: () -> Unit,
    onToggleFilter: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf(
        "تاريخ" to "History",
        "إسلامي" to "Islamic",
        "خيال" to "Fiction",
        "علوم" to "Science",
        "عربي" to "Arabic"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.length > 2) onSearchQueryChange(it)
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "بحث عن كتاب أو مؤلف...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            
            FilterChip(
                selected = isOnlyAvailable,
                onClick = onToggleFilter,
                label = { Icon(Icons.Default.FilterList, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                IconButton(onClick = onToggleLayout) {
                    Icon(
                        imageVector = if (layoutType == LayoutType.ROW) Icons.Default.GridView else Icons.Default.List,
                        contentDescription = "Toggle Layout",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = false,
                    onClick = { onCategoryClick(category.second) },
                    label = { Text(category.first) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "لم يتم العثور على نتائج",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "جرب البحث بكلمات مختلفة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            if (layoutType == LayoutType.ROW) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(searchResults) { book ->
                        val isSaved = savedBooks.any { it.id == book.key }
                        BookItem(
                            book = book,
                            isSaved = isSaved,
                            onSaveClick = { onSaveClick(book) },
                            onClick = { onBookClick(book) },
                            layoutType = LayoutType.ROW
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(searchResults) { book ->
                        val isSaved = savedBooks.any { it.id == book.key }
                        BookItem(
                            book = book,
                            isSaved = isSaved,
                            onSaveClick = { onSaveClick(book) },
                            onClick = { onBookClick(book) },
                            layoutType = LayoutType.GRID
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenPreview() {
    SearchContent(
        searchResults = listOf(
            BookDoc("1", "تاريخ العرب", listOf("فيليب حتى"), 1937, null, null, null, null, null)
        ),
        isLoading = false,
        savedBooks = emptyList(),
        layoutType = LayoutType.ROW,
        isOnlyAvailable = false,
        onSearchQueryChange = {},
        onCategoryClick = {},
        onSaveClick = {},
        onBookClick = {},
        onToggleLayout = {},
        onToggleFilter = {}
    )
}
