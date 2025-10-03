package com.creed.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaDetailScreen(navigator: Navigator, arenaId: String) {
    // Mock arena data with enhanced information
    val mockArena = remember {
        MockArenaDetail(
            id = arenaId,
            name = "ABC Sector 51, Noida",
            subtitle = "Premium Sports Complex",
            description = "State-of-the-art sports facility with multiple courts and modern amenities. Perfect for professional and recreational players.",
            address = "Sector 51, Noida (2.3 KM)",
            imageUrl = "https://media.istockphoto.com/id/1662909784/photo/american-football-arena-with-yellow-goal-post-grass-field-and-blurred-fans-at-playground-view.jpg?s=612x612&w=0&k=20&c=aDvhsRqKqAEzdH56Q0BqrmvEzmkvPlBThpsQR8Blfl0=",
            rating = 4.4f,
            reviewCount = 178,
            sports = listOf(
                MockSport("1", "Swimming", "Olympic size pool", "🏊"),
                MockSport("2", "Badminton", "Professional courts", "🏸"),
                MockSport("3", "Tennis", "Clay and hard courts", "🎾")
            ),
            amenities = listOf(
                MockAmenity("shower", "Shower", "🚿"),
                MockAmenity("changing_room", "Changing Room", "👔"),
                MockAmenity("parking", "Parking", "🅿️"),
                MockAmenity("wifi", "WiFi", "📶")
            ),
            nearbyArenas = listOf(
                MockNearbyArena("2", "ABC Sector 51", "Sector 51, Noida", 2.5f, 4.8f, "₹200-500", listOf("Swimming", "Badminton", "Tennis")),
                MockNearbyArena("3", "SportZone Central", "Sector 18, Noida", 3.2f, 4.6f, "₹150-400", listOf("Swimming", "Basketball", "Badminton"))
            ),
            priceRange = "₹200-500"
        )
    }

    Box(modifier = Modifier.fillMaxSize()
        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Hero Image with Collapsing Toolbar Effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    // Arena Image Background
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        KamelImage(
                            resource = asyncPainterResource(mockArena.imageUrl),
                            contentDescription = "Arena Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            onFailure = {
                                println("Kamel image error "+it.message)
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Location and Navigation
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📍",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = mockArena.address,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Google Maps",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { /* TODO: Open Google Maps */ }
                        ) {
                            Text("🧭", fontSize = 24.sp)
                        }
                    }
                }
            }

            item {
                // Google Reviews Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Google Reviews",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${mockArena.rating}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " ⭐ ",
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${mockArena.reviewCount} reviews",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                // Select your Creed (Sports)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Select your Creed",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(mockArena.sports) { sport ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = sport.emoji,
                                        fontSize = 32.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = sport.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Amenities
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Amenities",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(mockArena.amenities) { amenity ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = amenity.emoji,
                                        fontSize = 32.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = amenity.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Nearby Arena
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Nearby Arena",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        mockArena.nearbyArenas.forEach { nearbyArena ->
                            NearbyArenaItem(
                                arena = nearbyArena,
                                onClick = {
                                    navigator.navigate(Route.ArenaDetail.create(nearbyArena.id))
                                }
                            )
                            if (nearbyArena != mockArena.nearbyArenas.last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            item {
                // Bottom spacing for sticky buttons
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Transparent Top Bar (will be handled by Android navigation wrapper for insets)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigator.goBack() }
                    ) {
                        Text("←", fontSize = 24.sp, color = Color.Black)
                    }

                    Text(
                        text = mockArena.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row {
                    IconButton(
                        onClick = { /* TODO: Add to favorites */ }
                    ) {
                        Text("♡", fontSize = 24.sp, color = Color.Black)
                    }

                    IconButton(
                        onClick = { /* TODO: Share arena */ }
                    ) {
                        Text("↗", fontSize = 24.sp, color = Color.Black)
                    }
                }
            }
        }

        // Bottom Sticky Buttons (will be handled by Android navigation wrapper for insets)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    // Navigate to slot picker with first available sport
                    if (mockArena.sports.isNotEmpty()) {
                        navigator.navigate(
                            Route.SlotPicker.create(mockArena.id, mockArena.sports.first().id)
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("BOOK SLOT")
            }

            OutlinedButton(
                onClick = { /* TODO: Find play partner */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("BOOK COURT", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun NearbyArenaItem(
    arena: MockNearbyArena,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Arena thumbnail
            Card(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏟️", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = arena.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⭐ ${arena.rating}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = arena.address,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // Sports tags
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(arena.sports.take(3)) { sport ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = sport,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (arena.sports.size > 3) {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "+${arena.sports.size - 3}",
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "${arena.distance} km away",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = arena.priceRange,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

data class MockArenaDetail(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val address: String,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val sports: List<MockSport>,
    val amenities: List<MockAmenity>,
    val nearbyArenas: List<MockNearbyArena>,
    val priceRange: String
)

data class MockSport(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String
)

data class MockAmenity(
    val id: String,
    val name: String,
    val emoji: String
)

data class MockNearbyArena(
    val id: String,
    val name: String,
    val address: String,
    val distance: Float,
    val rating: Float,
    val priceRange: String,
    val sports: List<String>
)
