package com.example.booky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.booky.ui.*
import com.example.booky.ui.theme.BookyTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class NavItem(val route: String, val label: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as BookyApplication
        val viewModel = BookViewModel(app.repository, app.userPreferences)

        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settings.collectAsState()

            BookyTheme(darkTheme = settings.isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        val noBottomBarRoutes = listOf("detail", "reader")
                        val showBottomBar = currentDestination?.route !in noBottomBarRoutes && 
                                           currentDestination?.route?.startsWith("reader") == false
                        
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                val items = listOf(
                                    NavItem("search", "بحث", Icons.Default.Search),
                                    NavItem("saved", "المحفوظات", Icons.Default.Favorite),
                                    NavItem("downloaded", "المحملة", Icons.Default.Download),
                                    NavItem("settings", "الإعدادات", Icons.Default.Settings)
                                )
                                items.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "search",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("search") {
                            SearchScreen(
                                viewModel = viewModel,
                                onBookClick = { book ->
                                    viewModel.selectBook(book)
                                    navController.navigate("detail")
                                }
                            )
                        }
                        composable("saved") {
                            SavedBooksScreen(
                                viewModel = viewModel,
                                onBookClick = { entity ->
                                    viewModel.selectBookFromEntity(entity)
                                    navController.navigate("detail")
                                }
                            )
                        }
                        composable("downloaded") {
                            DownloadedBooksScreen(
                                viewModel = viewModel,
                                onBookClick = { entity ->
                                    viewModel.selectBookFromEntity(entity)
                                    navController.navigate("detail")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(viewModel = viewModel)
                        }
                        composable("detail") {
                            val selectedBook by viewModel.selectedBook.collectAsState()
                            selectedBook?.let { book ->
                                BookDetailScreen(
                                    book = book,
                                    onBackClick = {
                                        navController.popBackStack()
                                        viewModel.clearSelectedBook()
                                    },
                                    onReadClick = { url ->
                                        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                        navController.navigate("reader/$encodedUrl/${book.title}")
                                    },
                                    onDownloadClick = {
                                        viewModel.updateDownloadStatus(book.key, true, "simulated_path/${book.key}.pdf")
                                    }
                                )
                            } ?: run {
                                LaunchedEffect(Unit) {
                                    navController.popBackStack()
                                }
                            }
                        }
                        composable(
                            route = "reader/{url}/{title}",
                            arguments = listOf(
                                navArgument("url") { type = NavType.StringType },
                                navArgument("title") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            val title = backStackEntry.arguments?.getString("title") ?: "قارئ الكتب"
                            BookReaderScreen(
                                url = java.net.URLDecoder.decode(url, StandardCharsets.UTF_8.toString()),
                                title = title,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenContent() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Booky App\nModern Book Library",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    BookyTheme(darkTheme = false) {
        MainScreenContent()
    }
}
