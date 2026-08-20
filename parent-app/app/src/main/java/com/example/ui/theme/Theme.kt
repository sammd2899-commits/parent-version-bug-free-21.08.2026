package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TrustBluePrimary,
    onPrimary = OnTrustBluePrimary,
    primaryContainer = TrustBlueContainer,
    onPrimaryContainer = OnTrustBlueContainer,
    secondary = GrowthEmeraldSecondary,
    onSecondary = OnGrowthEmeraldSecondary,
    secondaryContainer = GrowthEmeraldContainer,
    onSecondaryContainer = OnGrowthEmeraldContainer,
    tertiary = GuardianTertiary,
    onTertiary = Color.White,
    tertiaryContainer = GuardianTertiaryContainer,
    background = GuardianBackground,
    onBackground = Color(0xFF0F172A),
    surface = GuardianSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = GuardianSurfaceVariant,
    onSurfaceVariant = Color(0xFF64748B),
    outline = GuardianOutline,
    outlineVariant = GuardianOutlineVariant,
    error = GuardianError,
    onError = OnGuardianError,
    errorContainer = GuardianErrorContainer,
    onErrorContainer = OnGuardianErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9CCAFF),
    onPrimary = Color(0xFF003358),
    primaryContainer = TrustBluePrimary,
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = Color(0xFF6CF8BB),
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF005236),
    onSecondaryContainer = Color(0xFF99F2BE),
    background = Color(0xFF191C20),
    onBackground = Color(0xFFE1E2E8),
    surface = Color(0xFF191C20),
    onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF414750),
    onSurfaceVariant = Color(0xFFC1C7D2)
)

@Composable
fun SentryKidsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

