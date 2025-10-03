package com.creed.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import com.creed.android.vm.ProfileViewModel
import com.creed.android.vm.ProfileState
import com.creed.android.vm.ProfileEffect
import com.creed.domain.models.User
import com.creed.domain.models.ProfileUpdateRequest
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigator: Navigator,
    viewModel: ProfileViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    var showEditBottomSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Load profile data when screen is first opened
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    when (state) {
        ProfileState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProfileState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${(state as ProfileState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadProfile() }) {
                        Text("Retry")
                    }
                }
            }
        }

        is ProfileState.Success -> {
            ProfileContent(
                user = (state as ProfileState.Success).user,
                navigator = navigator,
                onEditProfile = { showEditBottomSheet = true },
                onLogout = { showLogoutDialog = true }
            )
        }

        ProfileState.Idle -> {
            // Loading will be triggered by LaunchedEffect
        }
    }

    // Edit Profile Bottom Sheet
    if (showEditBottomSheet && state is ProfileState.Success) {
        EditProfileBottomSheet(
            user = (state as ProfileState.Success).user,
            onDismiss = { showEditBottomSheet = false },
            onSave = { updatedUser ->
                viewModel.updateProfile(updatedUser)
                showEditBottomSheet = false
            }
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // Clear storage and navigate to login
                        navigator.navigate(Route.Login, true)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Yes, Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileContent(
    user: User,
    navigator: Navigator,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with initials
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = buildString {
                        user.firstName?.firstOrNull()?.let { append(it) }
                        user.lastName?.firstOrNull()?.let { append(it) }
                    }.ifEmpty { "U" }

                    Text(
                        text = initials,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}".trim().ifEmpty { "User" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                if (!user.email.isNullOrEmpty()) {
                    Text(
                        text = user.email!!,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (!user.phoneNumber.isNullOrEmpty()) {
                    Text(
                        text = user.phoneNumber!!,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onEditProfile
                ) {
                    Text("✏️")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile")
                }
            }
        }

        // Settings Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                )

                ProfileMenuItem(
                    emoji = "🔔",
                    title = "Notifications",
                    onClick = { /* TODO: Navigate to notifications settings */ }
                )

                ProfileMenuItem(
                    emoji = "📍",
                    title = "Location Settings",
                    onClick = { /* TODO: Navigate to location settings */ }
                )

                ProfileMenuItem(
                    emoji = "💳",
                    title = "Payment Methods",
                    onClick = { /* TODO: Navigate to payment methods */ }
                )

                ProfileMenuItem(
                    emoji = "📊",
                    title = "Booking History",
                    onClick = { /* TODO: Navigate to detailed booking history */ }
                )
            }
        }

        // About Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "About",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                )

                ProfileMenuItem(
                    emoji = "📄",
                    title = "Terms & Conditions",
                    onClick = { /* TODO: Navigate to terms */ }
                )

                ProfileMenuItem(
                    emoji = "🔒",
                    title = "Privacy Policy",
                    onClick = { /* TODO: Navigate to privacy policy */ }
                )

                ProfileMenuItem(
                    emoji = "ℹ️",
                    title = "About Creed",
                    onClick = { /* TODO: Navigate to about page */ }
                )

                ProfileMenuItem(
                    emoji = "❓",
                    title = "Help & Support",
                    onClick = { /* TODO: Navigate to help */ }
                )
            }
        }

        // Logout Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("🚪")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    emoji: String,
    title: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 16.dp)
            )

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "▶",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    user: User,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var firstName by remember { mutableStateOf(user.firstName.orEmpty()) }
    var lastName by remember { mutableStateOf(user.lastName.orEmpty()) }
    var email by remember { mutableStateOf(user.email.orEmpty()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Edit Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        onSave(user.copy(
                            firstName = firstName,
                            lastName = lastName,
                            email = email
                        ))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
