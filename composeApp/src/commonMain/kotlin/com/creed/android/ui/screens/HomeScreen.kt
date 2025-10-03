package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import com.creed.android.ui.screens.home.FeaturedArenaUi
import com.creed.android.ui.screens.home.HomeEvent
import com.creed.android.ui.screens.home.HomeViewModel
import com.creed.android.ui.screens.home.RecentStatus
import com.creed.android.ui.screens.home.RecentUi
import com.creed.android.ui.screens.home.SportChipUi
import com.creed.android.ui.screens.home.SummaryState
import com.creed.android.ui.screens.home.SummaryType
import com.creed.android.ui.screens.home.UpcomingUi
import com.creed.android.ui.screens.home.*
import kotlinx.datetime.*
import org.koin.compose.koinInject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    navigator: Navigator,
    viewModel: HomeViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeEvent.Refresh)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Sticky Top Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = state.greeting,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Text(
                        text = state.user?.firstName ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                Card(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 16.sp)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        when {
            state.isLoading && state.user == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onEvent(HomeEvent.Refresh) }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Summary Row (optional - only show if has data)
                    if (state.summary.upcomingCount > 0 ||
                        state.summary.favouritesCount > 0 ||
                        state.summary.thisMonthCount > 0) {
                        item {
                            SummarySection(
                                summary = state.summary,
                                onSummaryClick = { type ->
                                    viewModel.onEvent(HomeEvent.OnSummaryClick(type))
                                }
                            )
                        }
                    }

                    // Upcoming Session Section
                    if (state.upcoming.isNotEmpty()) {
                        item {
                            UpcomingSessionSection(
                                upcoming = state.upcoming,
                                showViewAll = state.upcoming.firstOrNull()?.showViewAll == true,
                                onViewAllClick = {
                                    viewModel.onEvent(HomeEvent.OnViewAllUpcoming)
                                }
                            )
                        }
                    }

                    // Book by Sport Section
                    if (state.sports.isNotEmpty()) {
                        item {
                            BookBySportSection(
                                sports = state.sports,
                                onSportClick = { sportId ->
                                    viewModel.onEvent(HomeEvent.OnSportChipClick(sportId))
                                }
                            )
                        }
                    }

                    // Featured Arenas Section
                    if (state.featured.isNotEmpty()) {
                        item {
                            FeaturedArenasSection(
                                arenas = state.featured,
                                showViewAll = state.featured.firstOrNull()?.showViewAll == true,
                                onArenaClick = { arenaId ->
                                    viewModel.onEvent(HomeEvent.OnArenaClick(arenaId))
                                    navigator.navigate(Route.ArenaDetail.create(arenaId))
                                },
                                onViewAllClick = {
                                    viewModel.onEvent(HomeEvent.OnViewAllFeatured)
                                }
                            )
                        }
                    }

                    // Recent Activity Section
                    if (state.recent.isNotEmpty()) {
                        item {
                            RecentActivitySection(
                                activities = state.recent,
                                showViewAll = state.recent.firstOrNull()?.showViewAll == true,
                                onViewAllClick = {
                                    viewModel.onEvent(HomeEvent.OnViewAllRecent)
                                },
                                onRescheduleClick = { it ->
                                    viewModel.onEvent(HomeEvent.OnRescheduleClick(it))
                                },
                                onCancelClick = { it ->
                                    viewModel.onEvent(HomeEvent.OnCancelClick(it))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummarySection(
    summary: SummaryState,
    onSummaryClick: (SummaryType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Upcoming",
            count = summary.upcomingCount,
            subtitle = "Bookings",
            onClick = { onSummaryClick(SummaryType.UPCOMING) }
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "Favourites",
            count = summary.favouritesCount,
            subtitle = "Arenas",
            onClick = { onSummaryClick(SummaryType.FAVOURITES) }
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "This Month",
            count = summary.thisMonthCount,
            subtitle = "Sessions",
            onClick = { onSummaryClick(SummaryType.THIS_MONTH) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun UpcomingSessionSection(
    upcoming: List<UpcomingUi>,
    showViewAll: Boolean,
    onViewAllClick: () -> Unit
) {
    Column {
        SectionHeader(
            title = "Next Booking",
            showViewAll = showViewAll,
            onViewAllClick = onViewAllClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        upcoming.forEach { session ->
            UpcomingSessionCard(session = session)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingSessionCard(session: UpcomingUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = session.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = session.sport,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = session.date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = session.time,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* Handle view details */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("View Details")
            }
        }
    }
}

@Composable
fun BookBySportSection(
    sports: List<SportChipUi>,
    onSportClick: (String) -> Unit
) {
    Column {
        SectionHeader(
            title = "Book by Sport",
            showViewAll = false,
            onViewAllClick = { }
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 4
        ) {
            sports.forEach { sport ->
                SportChip(
                    sport = sport,
                    onClick = { onSportClick(sport.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportChip(
    sport: SportChipUi,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = sport.icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sport.name,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FeaturedArenasSection(
    arenas: List<FeaturedArenaUi>,
    showViewAll: Boolean,
    onArenaClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        SectionHeader(
            title = "Featured Arenas",
            showViewAll = showViewAll,
            onViewAllClick = onViewAllClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        arenas.forEach { arena ->
            FeaturedArenaCard(
                arena = arena,
                onClick = { onArenaClick(arena.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FeaturedArenaCard(
    arena: FeaturedArenaUi,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Arena image placeholder
            Card(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏟️", fontSize = 24.sp)
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = arena.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Text(
                            text = arena.rating.toString(),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = if (arena.isFavorite) " ❤️" else " 🤍",
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = arena.location,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    arena.sports.take(3).forEach { sport ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = sport,
                                    fontSize = 10.sp
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                    if (arena.additionalSportsCount > 0) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = "+${arena.additionalSportsCount}",
                                    fontSize = 10.sp
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = arena.distance,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = arena.priceRange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivitySection(
    activities: List<RecentUi>,
    showViewAll: Boolean,
    onRescheduleClick: (String) -> Unit,
    onCancelClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        SectionHeader(
            title = "Recent Activity",
            showViewAll = showViewAll,
            onViewAllClick = onViewAllClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        activities.forEach { activity ->
            RecentActivityCard(
                activity = activity,
                onRescheduleClick = { onRescheduleClick(activity.id) },
                onCancelClick = { onCancelClick(activity.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentActivityCard(
    activity: RecentUi,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = activity.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = activity.sport,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                when (activity.status) {
                    RecentStatus.UPCOMING -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Upcoming",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    RecentStatus.COMPLETED -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Completed",
                                fontSize = 10.sp,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    RecentStatus.CANCELLED -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Cancelled",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Date",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = activity.date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column {
                    Text(
                        text = "Time",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = activity.time,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column {
                    Text(
                        text = "Price",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = activity.price,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    showViewAll: Boolean,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (showViewAll) {
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
fun getGreeting(): String {
    val now = Clock.System.now()
    val hour = now.toLocalDateTime(TimeZone.currentSystemDefault()).hour
    return when (hour) {
        in 5..11 -> "Good Morning,"
        in 12..16 -> "Good Afternoon,"
        else -> "Good Evening,"
    }
}
