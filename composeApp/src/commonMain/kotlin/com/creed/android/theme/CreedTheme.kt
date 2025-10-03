package com.creed.android.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Purple gradient colors matching the provided background
private val PurpleGradientPalette = lightColorScheme(
    primary = Color(0xFF9C7FD1), // Medium purple
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8DDF5), // Light purple
    onPrimaryContainer = Color(0xFF4A2C7A), // Dark purple
    secondary = Color(0xFFB39DDB), // Light purple
    onSecondary = Color.White,
    tertiary = Color(0xFFD1C4E9), // Very light purple
    onTertiary = Color(0xFF512DA8), // Deep purple
    background = Color.Transparent, // Transparent to show gradient
    onBackground = Color(0xFF2D2D2D), // Dark gray for text
    surface = Color(0x80FFFFFF), // Semi-transparent white for cards
    onSurface = Color(0xFF2D2D2D), // Dark gray for text on surfaces
    surfaceVariant = Color(0x60FFFFFF), // More transparent white
    onSurfaceVariant = Color(0xFF666666), // Medium gray
    error = Color(0xFFE57373),
    onError = Color.White
)

private val DarkPurpleGradientPalette = darkColorScheme(
    primary = Color(0xFFB39DDB),
    onPrimary = Color(0xFF2D2D2D),
    primaryContainer = Color(0xFF512DA8),
    onPrimaryContainer = Color(0xFFE8DDF5),
    secondary = Color(0xFF9C7FD1),
    onSecondary = Color(0xFF2D2D2D),
    tertiary = Color(0xFFD1C4E9),
    onTertiary = Color(0xFF2D2D2D),
    background = Color.Transparent,
    onBackground = Color.White,
    surface = Color(0x40000000), // Semi-transparent black for cards
    onSurface = Color.White,
    surfaceVariant = Color(0x60000000),
    onSurfaceVariant = Color(0xFFCCCCCC),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

// Gradient brush matching the provided background with darker grayish overlay throughout
val AppGradientBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF8B7B9B), // Darker grayish-purple at very top
        Color(0xFF9B8BAB), // Slightly lighter grayish-purple
        Color(0xFFB39DDB), // Light purple
        Color(0xFFA58BCD), // Medium-light purple (softer than before)
        Color(0xFFC7B9E6), // Pale lavender (smooth bridge to gray)
        Color(0xFFBDBDBD), // Light gray
        Color(0xFFE0E0E0), // Very light gray
        Color(0xFFF5F5F5)  // Almost white at bottom
    ),
    startY = 0f,
    endY = Float.POSITIVE_INFINITY
)

@Composable
fun CreedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = AppGradientBrush)
    ) {
        content()
    }
}

@Composable
fun CreedTheme(
    darkTheme: Boolean = false,
    useDynamicColor: Boolean = false, // KMP doesn't support dynamic colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkPurpleGradientPalette
        else -> PurpleGradientPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = {
            CreedGradientBackground {
                content()
            }
        }
    )
}
