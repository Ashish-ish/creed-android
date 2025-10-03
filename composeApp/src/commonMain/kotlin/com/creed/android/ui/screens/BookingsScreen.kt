package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator

@Composable
fun BookingsScreen(navigator: Navigator) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past")

    // Mock booking data
    val mockUpcomingBookings = remember {
        listOf(
            MockBooking("1", "SportsPlex Arena", "Cricket", "Today, 6:00 PM - 7:00 PM", "₹800", "Confirmed"),
            MockBooking("2", "Elite Sports Club", "Badminton", "Tomorrow, 8:00 AM - 9:00 AM", "₹600", "Confirmed"),
            MockBooking("3", "Champions Ground", "Football", "Dec 2, 7:00 PM - 8:00 PM", "₹1200", "Confirmed")
        )
    }

    val mockPastBookings = remember {
        listOf(
            MockBooking("4", "Urban Sports Hub", "Tennis", "Nov 25, 5:00 PM - 6:00 PM", "₹700", "Completed"),
            MockBooking("5", "Victory Sports Complex", "Basketball", "Nov 20, 9:00 AM - 10:00 AM", "₹500", "Completed"),
            MockBooking("6", "Phoenix Arena", "Football", "Nov 15, 6:00 PM - 7:00 PM", "₹1000", "Cancelled")
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        Text(
            text = "My Bookings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 -> BookingsList(bookings = mockUpcomingBookings, isUpcoming = true)
            1 -> BookingsList(bookings = mockPastBookings, isUpcoming = false)
        }
    }
}

@Composable
fun BookingsList(bookings: List<MockBooking>, isUpcoming: Boolean) {
    if (bookings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isUpcoming) "No upcoming bookings" else "No past bookings",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isUpcoming) "Book your first sports ground!" else "Your booking history will appear here",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookings) { booking ->
                BookingCard(booking = booking, isUpcoming = isUpcoming)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingCard(booking: MockBooking, isUpcoming: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.arenaName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = booking.sport,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                StatusChip(status = booking.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = booking.dateTime,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = booking.totalAmount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (isUpcoming && booking.status == "Confirmed") {
                    OutlinedButton(
                        onClick = { /* TODO: Implement booking cancellation */ }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "Confirmed" -> Pair(Color(0xFF4CAF50), Color.White)
        "Completed" -> Pair(Color(0xFF2196F3), Color.White)
        "Cancelled" -> Pair(Color(0xFFF44336), Color.White)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

data class MockBooking(
    val id: String,
    val arenaName: String,
    val sport: String,
    val dateTime: String,
    val totalAmount: String,
    val status: String
)
