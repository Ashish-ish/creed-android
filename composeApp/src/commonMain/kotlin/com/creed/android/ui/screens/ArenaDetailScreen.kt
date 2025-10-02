package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.creed.android.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaDetailScreen(navController: NavHostController, arenaId: String) {
    // Mock arena data
    val mockArena = remember {
        MockArenaDetail(
            id = arenaId,
            name = "SportsPlex Arena",
            subtitle = "Premium Sports Complex",
            description = "State-of-the-art sports facility with multiple courts and modern amenities. Perfect for professional and recreational players.",
            address = "123 Sports Street, Koramangala, Bangalore - 560034",
            images = listOf("image1", "image2", "image3"),
            sports = listOf(
                MockSport("1", "Cricket", "Professional cricket ground with floodlights"),
                MockSport("2", "Football", "FIFA standard football field"),
                MockSport("3", "Tennis", "Clay and hard courts available")
            )
        )
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { mockArena.images.size })

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text(mockArena.name) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("←") // Using text arrow instead of Icon
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Image Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) { page ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Image ${page + 1}")
                        }
                    }
                }

                // Page Indicator
                if (mockArena.images.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(mockArena.images.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .padding(2.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (index == pagerState.currentPage)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                    modifier = Modifier.fillMaxSize()
                                ) {}
                            }
                        }
                    }
                }
            }

            item {
                // Arena Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = mockArena.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (mockArena.subtitle.isNotEmpty()) {
                            Text(
                                text = mockArena.subtitle,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = mockArena.description,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📍", // Using emoji instead of Icon
                                fontSize = 16.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = mockArena.address,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { /* TODO: Open map */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("View on Map")
                        }
                    }
                }
            }

            item {
                // Sports Tabs
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "Available Sports",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                        )

                        TabRow(
                            selectedTabIndex = selectedTabIndex
                        ) {
                            mockArena.sports.forEachIndexed { index, sport ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(sport.name) }
                                )
                            }
                        }

                        // Selected Sport Details
                        val selectedSport = mockArena.sports[selectedTabIndex]
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = selectedSport.description,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Button(
                                onClick = {
                                    navController.navigate(
                                        Route.SlotPicker.create(mockArena.id, selectedSport.id)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Book ${selectedSport.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MockArenaDetail(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val address: String,
    val images: List<String>,
    val sports: List<MockSport>
)

data class MockSport(
    val id: String,
    val name: String,
    val description: String
)
