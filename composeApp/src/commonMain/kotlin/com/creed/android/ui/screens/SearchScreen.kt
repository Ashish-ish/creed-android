package com.creed.android.ui.screens

import androidx.compose.foundation.background
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
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import com.creed.android.vm.SearchViewModel
import com.creed.domain.models.SearchArena
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navigator: Navigator) {
    val viewModel: SearchViewModel = koinInject()
    var searchQuery by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    // Debounced search effect
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            delay(500)
            viewModel.search(searchQuery)
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

            state is com.creed.android.vm.SearchState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state is com.creed.android.vm.SearchState.Success -> {
                val arenas = (state as com.creed.android.vm.SearchState.Success).arenas
                if (arenas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found for \"$searchQuery\"",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(arenas) { arena ->
                            SearchArenaItem(arena = arena, onClick = {
                                navigator.navigate(Route.ArenaDetail.create(arena.id))
                            })
                        }
                    }
                }
            }

            state is com.creed.android.vm.SearchState.Error -> {
                val message = (state as com.creed.android.vm.SearchState.Error).message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun SearchArenaItem(arena: SearchArena, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Arena image (use Kamel if needed)
            Box(
                modifier = Modifier.size(60.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("🏟️", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(arena.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(arena.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(arena.address.fullAddress, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row {
                    Text("⭐ ${arena.rating.average}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${arena.priceRange.currency} ${arena.priceRange.min}-${arena.priceRange.max}", fontSize = 12.sp)
                }
            }
        }
    }
}
