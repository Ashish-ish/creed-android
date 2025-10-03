package com.creed.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creed.android.navigation.Navigator
import com.creed.android.navigation.Route

@Composable
fun LocationPermissionScreen(navigator: Navigator) {
    var isLoading by remember { mutableStateOf(false) }
    var showPermissionDeniedMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📍", // Using emoji instead of Icon
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "Enable Location Access",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "We need your location to find sports grounds near you and provide personalized recommendations.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (showPermissionDeniedMessage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Location permission denied. You can still search for grounds by city name, but nearby recommendations won't be available.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 14.sp
                )
            }
        }

        Button(
            onClick = {
                isLoading = true
                // TODO: Request location permissions using Moko-Geo or expect/actual wrapper
                // For now, simulate permission grant and navigate to main screen
                isLoading = false
                navigator.navigate(Route.MainScreen, true)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Allow Location Access")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                showPermissionDeniedMessage = true
                // Navigate to main screen even without location permission
                navigator.navigate(Route.MainScreen, true)
            }
        ) {
            Text("Skip for Now")
        }
    }
}
