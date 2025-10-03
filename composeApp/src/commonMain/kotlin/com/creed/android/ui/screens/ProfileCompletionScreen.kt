package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import com.creed.android.vm.ProfileCompletionViewModel
import com.creed.android.vm.ProfileCompletionState
import org.koin.compose.koinInject

@Composable
fun ProfileCompletionScreen(navigator: Navigator) {
    val viewModel: ProfileCompletionViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }

    val genderOptions = listOf("male", "female", "other")

    // Handle navigation based on state changes
    LaunchedEffect(state) {
        when (state) {
            is ProfileCompletionState.Success -> {
                navigator.navigate(Route.Home)
            }
            else -> {
                // Handle other states in UI
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Complete Your Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Help us personalize your experience",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // First Name
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
                if (state is ProfileCompletionState.Error) {
                    viewModel.clearError()
                }
            },
            label = { Text("First Name") },
            placeholder = { Text("Enter your first name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is ProfileCompletionState.Loading,
            isError = state is ProfileCompletionState.Error && firstName.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Last Name
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
                if (state is ProfileCompletionState.Error) {
                    viewModel.clearError()
                }
            },
            label = { Text("Last Name") },
            placeholder = { Text("Enter your last name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is ProfileCompletionState.Loading,
            isError = state is ProfileCompletionState.Error && lastName.isBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Gender Selection
        Column {
            Text(
                text = "Gender",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            genderOptions.forEach { gender ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedGender == gender,
                            onClick = {
                                selectedGender = gender
                                if (state is ProfileCompletionState.Error) {
                                    viewModel.clearError()
                                }
                            }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedGender == gender,
                        onClick = {
                            selectedGender = gender
                            if (state is ProfileCompletionState.Error) {
                                viewModel.clearError()
                            }
                        },
                        enabled = state !is ProfileCompletionState.Loading
                    )
                    Text(
                        text = gender.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Date of Birth
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { input ->
                if (input.length <= 10) {
                    dateOfBirth = input
                    if (state is ProfileCompletionState.Error) {
                        viewModel.clearError()
                    }
                }
            },
            label = { Text("Date of Birth") },
            placeholder = { Text("YYYY-MM-DD (e.g., 1990-01-15)") },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is ProfileCompletionState.Loading,
            isError = state is ProfileCompletionState.Error && !isValidDateFormat(dateOfBirth),
            supportingText = {
                if (state is ProfileCompletionState.Error && !isValidDateFormat(dateOfBirth)) {
                    Text("Please enter a valid date in YYYY-MM-DD format")
                } else {
                    Text("Format: YYYY-MM-DD")
                }
            }
        )

        // Show error message if any
        if (state is ProfileCompletionState.Error) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = (state as ProfileCompletionState.Error).message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.submitProfile(
                    firstName = firstName,
                    lastName = lastName,
                    gender = selectedGender,
                    dateOfBirth = dateOfBirth
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is ProfileCompletionState.Loading &&
                firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                selectedGender.isNotBlank() &&
                isValidDateFormat(dateOfBirth)
        ) {
            when (state) {
                is ProfileCompletionState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completing...")
                }
                is ProfileCompletionState.Success -> {
                    Text("Profile Completed!")
                }
                else -> {
                    Text("Complete Profile")
                }
            }
        }
    }
}

private fun isValidDateFormat(date: String): Boolean {
    return try {
        val parts = date.split("-")
        if (parts.size != 3) return false

        val year = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val day = parts[2].toIntOrNull() ?: return false

        year in 1900..2010 && month in 1..12 && day in 1..31
    } catch (e: Exception) {
        false
    }
}
