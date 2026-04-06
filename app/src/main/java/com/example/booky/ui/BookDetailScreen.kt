package com.example.booky.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.booky.data.api.BookDoc
import com.example.booky.data.api.getDownloadUrl
import com.example.booky.data.api.getReadUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: BookDoc,
    onBackClick: () -> Unit,
    onReadClick: (String) -> Unit,
    onDownloadClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val readUrl = book.getReadUrl()
    val downloadUrl = book.getDownloadUrl()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "تفاصيل الكتاب",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "شاهد هذا الكتاب: ${book.title} - ${book.authorName}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                SelectionContainer {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "المؤلف: ${book.authorName}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        if (book.firstPublishYear != null) {
                            Text(
                                text = "سنة النشر: ${book.firstPublishYear}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    readUrl?.let { onReadClick(it) }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = readUrl != null,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("قراءة")
                            }

                            FilledTonalButton(
                                onClick = {
                                    downloadUrl?.let {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                        context.startActivity(intent)
                                        onDownloadClick()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = downloadUrl != null,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("تحميل")
                            }
                        }

                        if (readUrl == null) {
                            Text(
                                text = "* هذا الكتاب قد لا يتوفر للقراءة المباشرة أو التحميل حالياً.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "عن الكتاب:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            val subjects = book.subjects?.take(5)?.joinToString(", ") ?: "لا توجد تصنيفات متاحة"
                            Text(
                                text = "التصنيفات: $subjects\n\nيوفر هذا التطبيق وصولاً إلى الكتب عبر Open Library. إذا كان الكتاب متاحاً في Internet Archive، يمكنك قراءته مباشرة أو تحميله بصيغة PDF.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
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
fun BookDetailPreview() {
    BookDetailScreen(
        book = BookDoc(
            key = "1",
            title = "مقدمة ابن خلدون",
            authorNames = listOf("ابن خلدون"),
            firstPublishYear = 1377,
            subjects = listOf("History"),
            coverI = null,
            languages = listOf("ara"),
            ia = listOf("muqaddimah0000ibnk"),
            formats = null
        ),
        onBackClick = {},
        onReadClick = {}
    )
}
