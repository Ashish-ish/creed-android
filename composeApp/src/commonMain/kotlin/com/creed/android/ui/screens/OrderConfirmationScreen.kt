package com.creed.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    navigator: Navigator,
    bookingId: String
) {
    var isPaymentProcessing by remember { mutableStateOf(false) }
    var isPaymentComplete by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Mock booking data
    val mockBooking = remember {
        MockBookingConfirmation(
            id = bookingId,
            arenaName = "SportsPlex Arena",
            sport = "Cricket",
            date = "Dec 1, 2024",
            timeSlot = "18:00 - 19:00",
            courtPrice = 1000,
            equipment = listOf(
                MockEquipmentItem("Cricket Bat", 2, 50),
                MockEquipmentItem("Cricket Ball", 1, 20)
            ),
            totalAmount = 1120
        )
    }

    LaunchedEffect(isPaymentComplete) {
        if (isPaymentComplete) {
            showSuccessAnimation = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Confirm Booking") },
            navigationIcon = {
                IconButton(
                    onClick = { navigator.goBack() },
                    enabled = !isPaymentProcessing
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (showSuccessAnimation) {
            // Success Animation
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = showSuccessAnimation,
                    enter = scaleIn() + fadeIn()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(80.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Booking Confirmed!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )

                Text(
                    text = "Your booking has been successfully confirmed.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        navigator.navigate(Route.Bookings, true)
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("View My Bookings")
                }

                TextButton(
                    onClick = {
                        navigator.navigate(Route.MainScreen, true)
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Back to Home")
                }
            }
        } else {
            // Booking Details
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Booking Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Booking Summary",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        BookingDetailRow("Arena", mockBooking.arenaName)
                        BookingDetailRow("Sport", mockBooking.sport)
                        BookingDetailRow("Date", mockBooking.date)
                        BookingDetailRow("Time", mockBooking.timeSlot)

                        Divider(modifier = Modifier.padding(vertical = 12.dp))

                        BookingDetailRow("Court Fee", "₹${mockBooking.courtPrice}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Equipment Card
                if (mockBooking.equipment.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Equipment",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            mockBooking.equipment.forEach { equipment ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${equipment.name} x${equipment.quantity}")
                                    Text("₹${equipment.pricePerHour * equipment.quantity}")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Total Amount Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Amount",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${mockBooking.totalAmount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Payment Button
                Button(
                    onClick = {
                        isPaymentProcessing = true
                        // TODO: Integrate RazorPay payment
                        // Simulate payment processing
                        // In real implementation, this would trigger RazorPay SDK
                        GlobalScope.launch {
                            delay(3000)
                            isPaymentProcessing = false
                            isPaymentComplete = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPaymentProcessing
                ) {
                    if (isPaymentProcessing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp).background(color = MaterialTheme.colorScheme.onPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing Payment...")
                        }
                    } else {
                        Text("Pay ₹${mockBooking.totalAmount}")
                    }
                }

                Text(
                    text = "By proceeding, you agree to our Terms & Conditions",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BookingDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}

data class MockBookingConfirmation(
    val id: String,
    val arenaName: String,
    val sport: String,
    val date: String,
    val timeSlot: String,
    val courtPrice: Int,
    val equipment: List<MockEquipmentItem>,
    val totalAmount: Int
)

data class MockEquipmentItem(
    val name: String,
    val quantity: Int,
    val pricePerHour: Int
)
