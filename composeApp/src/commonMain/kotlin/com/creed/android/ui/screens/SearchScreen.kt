package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.creed.android.navigation.Route
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<MockArena>>(emptyList()) }

    // Mock search results
    val allArenas = remember {
        listOf(
            MockArena("1", "SportsPlex Arena", "Koramangala", listOf("Cricket", "Football", "Tennis")),
            MockArena("2", "Elite Sports Club", "Indiranagar", listOf("Badminton", "Basketball")),
            MockArena("3", "Champions Ground", "Whitefield", listOf("Cricket", "Football")),
            MockArena("4", "Urban Sports Hub", "Electronic City", listOf("Tennis", "Squash", "Badminton")),
            MockArena("5", "Victory Sports Complex", "HSR Layout", listOf("Cricket", "Basketball", "Tennis")),
            MockArena("6", "Phoenix Arena", "Marathahalli", listOf("Football", "Badminton"))
        )
    }

    // Debounced search effect
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            isSearching = true
            delay(500) // Debounce delay
            searchResults = allArenas.filter { arena ->
                arena.name.contains(searchQuery, ignoreCase = true) ||
                arena.distance.contains(searchQuery, ignoreCase = true) ||
                arena.sports.any { it.contains(searchQuery, ignoreCase = true) }
            }
            isSearching = false
        } else {
            searchResults = emptyList()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearch = { },
            active = false,
            onActiveChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search arenas, sports, or locations...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        ) {}

        // Search Results
        when {
            searchQuery.isBlank() -> {
                // Show popular searches or categories
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Popular Sports",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val popularSports = listOf("Cricket", "Football", "Badminton", "Tennis", "Basketball")
                    popularSports.forEach { sport ->
                        FilterChip(
                            onClick = { searchQuery = sport },
                            label = { Text(sport) },
                            selected = false,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            isSearching -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            searchResults.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found for \"$searchQuery\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${searchResults.size} results found",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(searchResults) { arena ->
                        SearchResultCard(
                            arena = arena,
                            searchQuery = searchQuery,
                            onClick = {
                                navController.navigate(Route.ArenaDetail.create(arena.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultCard(
    arena: MockArena,
    searchQuery: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = arena.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = arena.distance,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                arena.sports.forEach { sport ->
                    AssistChip(
                        onClick = { },
                        label = { Text(sport, fontSize = 10.sp) }
                    )
                }
            }
        }
    }
}

data class MockArena(
    val id: String,
    val name: String,
    val distance: String,
    val sports: List<String>
)
