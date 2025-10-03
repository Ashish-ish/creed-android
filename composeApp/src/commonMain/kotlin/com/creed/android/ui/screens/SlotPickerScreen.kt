package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotPickerScreen(navigator: Navigator) {
    var selectedDateIndex by remember { mutableStateOf(0) }
    var selectedSlot by remember { mutableStateOf<MockSlot?>(null) }
    var selectedEquipment by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var showEquipmentBottomSheet by remember { mutableStateOf(false) }

    // Mock data
    val mockSlots = remember {
        listOf(
            MockSlot("06:00", "07:00", false, 800),
            MockSlot("07:00", "08:00", true, 800),
            MockSlot("08:00", "09:00", false, 800),
            MockSlot("09:00", "10:00", false, 800),
            MockSlot("10:00", "11:00", true, 800),
            MockSlot("17:00", "18:00", false, 1000),
            MockSlot("18:00", "19:00", false, 1000),
            MockSlot("19:00", "20:00", true, 1000),
            MockSlot("20:00", "21:00", false, 1000),
            MockSlot("21:00", "22:00", false, 1000)
        )
    }

    val mockEquipment = remember {
        listOf(
            MockEquipment("1", "Cricket Bat", 50, 10),
            MockEquipment("2", "Cricket Ball", 20, 15),
            MockEquipment("3", "Stumps Set", 100, 5),
            MockEquipment("4", "Protective Gear", 80, 8)
        )
    }

    val weekDates = remember {
        listOf("Today", "Tomorrow", "Dec 3", "Dec 4", "Dec 5", "Dec 6", "Dec 7")
    }

    val totalPrice = remember(selectedSlot, selectedEquipment) {
        val slotPrice = selectedSlot?.price ?: 0
        val equipmentPrice = selectedEquipment.entries.sumOf { (equipmentId, quantity) ->
            mockEquipment.find { it.id == equipmentId }?.pricePerHour?.times(quantity) ?: 0
        }
        slotPrice + equipmentPrice
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Select Time Slot") },
            navigationIcon = {
                IconButton(onClick = { navigator.goBack() }) {
                    Text("←") // Using text arrow instead of Icon
                }
            }
        )

        // Date Selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select Date",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(weekDates.size) { index ->
                        DateChip(
                            dateText = weekDates[index],
                            isSelected = selectedDateIndex == index,
                            onClick = { selectedDateIndex = index }
                        )
                    }
                }
            }
        }

        // Slots Grid
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Available Slots for ${weekDates[selectedDateIndex]}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(mockSlots) { slot ->
                        SlotCard(
                            slot = slot,
                            isSelected = selectedSlot == slot,
                            onClick = {
                                if (!slot.isBooked) {
                                    selectedSlot = slot
                                }
                            }
                        )
                    }
                }
            }
        }

        // Selected Slot Info
        if (selectedSlot != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Selected Slot",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${selectedSlot!!.start} - ${selectedSlot!!.end}")
                        Text("₹${selectedSlot!!.price}", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showEquipmentBottomSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Equipment (${selectedEquipment.values.sum()} items)")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total: ₹$totalPrice",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Button(
                            onClick = {
                                // TODO: Create booking and navigate to order confirmation
                                navigator.navigate(Route.OrderConfirmation.create("booking123"))
                            }
                        ) {
                            Text("Confirm Booking")
                        }
                    }
                }
            }
        }
    }

    // Equipment Bottom Sheet
    if (showEquipmentBottomSheet) {
        EquipmentBottomSheet(
            equipment = mockEquipment,
            selectedEquipment = selectedEquipment,
            onEquipmentChanged = { selectedEquipment = it },
            onDismiss = { showEquipmentBottomSheet = false }
        )
    }
}

@Composable
fun DateChip(
    dateText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = dateText,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selected = isSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotCard(
    slot: MockSlot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = when {
                slot.isBooked -> MaterialTheme.colorScheme.errorContainer
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${slot.start} - ${slot.end}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (slot.isBooked) "Booked" else "₹${slot.price}",
                fontSize = 10.sp,
                color = if (slot.isBooked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentBottomSheet(
    equipment: List<MockEquipment>,
    selectedEquipment: Map<String, Int>,
    onEquipmentChanged: (Map<String, Int>) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Add Equipment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(equipment) { item ->
                    val quantity = selectedEquipment[item.id] ?: 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "₹${item.pricePerHour}/hour",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${item.availableQuantity} available",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (quantity > 0) {
                                            val updated = selectedEquipment.toMutableMap()
                                            if (quantity == 1) {
                                                updated.remove(item.id)
                                            } else {
                                                updated[item.id] = quantity - 1
                                            }
                                            onEquipmentChanged(updated)
                                        }
                                    },
                                    enabled = quantity > 0
                                ) {
                                    Text("−") // Using text minus instead of Icon
                                }

                                Text(
                                    text = quantity.toString(),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                IconButton(
                                    onClick = {
                                        if (quantity < item.availableQuantity) {
                                            val updated = selectedEquipment.toMutableMap()
                                            updated[item.id] = quantity + 1
                                            onEquipmentChanged(updated)
                                        }
                                    },
                                    enabled = quantity < item.availableQuantity
                                ) {
                                    Text("+") // Using text plus instead of Icon
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class MockSlot(
    val start: String,
    val end: String,
    val isBooked: Boolean,
    val price: Int
)

data class MockEquipment(
    val id: String,
    val name: String,
    val pricePerHour: Int,
    val availableQuantity: Int
)
