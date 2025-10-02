package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route
import com.creed.android.vm.LoginViewModel
import com.creed.android.vm.LoginState
import com.creed.android.vm.LoginEffect
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    navigator: Navigator
) {
    val viewModel: LoginViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    // Handle state changes for navigation
    LaunchedEffect(state) {
        when (state) {
            is LoginState.OtpVerified -> {
                // Navigate after successful verification
                navigator.navigate(Route.ProfileCompletion)
            }
            else -> { /* Handle other states */ }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Creed",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Sports ground booking made easy",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        when (val currentState = state) {
            is LoginState.Idle, is LoginState.Loading -> {
                // Phone number input
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            phoneNumber = it
                        }
                    },
                    label = { Text("Phone Number") },
                    placeholder = { Text("Enter 10-digit mobile number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("+91 ") },
                    enabled = currentState !is LoginState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (phoneNumber.length == 10) {
                            viewModel.sendOtp(phoneNumber)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = currentState !is LoginState.Loading && phoneNumber.length == 10
                ) {
                    if (currentState is LoginState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Send OTP")
                    }
                }
            }

            is LoginState.OtpSent -> {
                // OTP input
                Text(
                    text = "Enter the 6-digit OTP sent to +91 ${currentState.phone}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            otp = it
                        }
                    },
                    label = { Text("OTP") },
                    placeholder = { Text("Enter 6-digit OTP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otp.length == 6) {
                            viewModel.verifyOtp(otp)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otp.length == 6
                ) {
                    Text("Verify OTP")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        viewModel.resetToPhoneInput()
                        phoneNumber = ""
                        otp = ""
                    }
                ) {
                    Text("Change Phone Number")
                }
            }

            is LoginState.Error -> {
                // Show error and allow retry
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                            phoneNumber = it
                            viewModel.clearError()
                        }
                    },
                    label = { Text("Phone Number") },
                    placeholder = { Text("Enter 10-digit mobile number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    prefix = { Text("+91 ") },
                    isError = true
                )

                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (phoneNumber.length == 10) {
                            viewModel.sendOtp(phoneNumber)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = phoneNumber.length == 10
                ) {
                    Text("Retry Send OTP")
                }
            }

            is LoginState.OtpVerified -> {
                // Show success state briefly before navigation
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("OTP Verified Successfully!")
                    Text("Setting up your account...")
                }
            }
        }
    }
}
