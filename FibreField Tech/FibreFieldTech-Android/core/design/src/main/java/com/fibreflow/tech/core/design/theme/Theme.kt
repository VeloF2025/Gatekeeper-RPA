package com.fibreflow.tech.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

/**
 * FibreField Tech Theme System
 *
 * High-tech, futuristic design system with:
 * - Dark theme optimized for field use
 * - Neon accent colors for AI/ML features
 * - Glass morphism effects
 * - Dynamic colors based on Material You
 */

// Primary color scheme - High-tech blue/cyan
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00D4FF),        // Neon cyan
    onPrimary = Color(0xFF000000),      // Black for contrast
    primaryContainer = Color(0xFF001F3F), // Deep blue
    onPrimaryContainer = Color(0xFF00D4FF),

    secondary = Color(0xFF00FF88),      // Electric green
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF003322),
    onSecondaryContainer = Color(0xFF00FF88),

    tertiary = Color(0xFFFF006E),       // Hot pink for alerts
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF3D0014),
    onTertiaryContainer = Color(0xFFFF006E),

    error = Color(0xFFFF3B30),           // Red for errors
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF3D0014),
    onErrorContainer = Color(0xFFFF3B30),

    background = Color(0xFF0A0E27),     // Deep space background
    onBackground = Color(0xFFE4E4E7),    // Light gray text
    surface = Color(0xFF1A1F3A),         // Dark surface
    onSurface = Color(0xFFE4E4E7),
    surfaceVariant = Color(0xFF252A45),  // Slightly lighter surface
    onSurfaceVariant = Color(0xFFC8C8D0),

    outline = Color(0xFF4A5068),         // Border colors
    outlineVariant = Color(0xFF2A3050),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE4E4E7),
    inverseOnSurface = Color(0xFF0A0E27),
    inversePrimary = Color(0xFF0066CC),
)

// Light theme variant (for outdoor use)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0066CC),        // Standard blue
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4E6FF),
    onPrimaryContainer = Color(0xFF001D35),

    secondary = Color(0xFF00CC66),      // Standard green
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCFFDD),
    onSecondaryContainer = Color(0xFF002111),

    tertiary = Color(0xFFCC0066),       // Standard pink
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFCCDD),
    onTertiaryContainer = Color(0xFF330814),

    error = Color(0xFFDC2626),           // Standard red
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFE6E6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF8F9FA),     // Light background
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE4E6E8),
    onSurfaceVariant = Color(0xFF4A4C50),

    outline = Color(0xFF7A7C80),
    outlineVariant = Color(0xFFCACCD0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2C2E30),
    inverseOnSurface = Color(0xFFF0F1F3),
    inversePrimary = Color(0xFF00D4FF),
)

// Extended colors for AI/ML features
object ExtendedColors {
    val aiActive = Color(0xFF00D4FF)        // AI processing
    val aiSuccess = Color(0xFF00FF88)       // AI success
    val aiWarning = Color(0xFFFFAA00)        // AI warning
    val aiError = Color(0xFFFF3B30)          // AI error
    val aiNeutral = Color(0xFF8B92A8)        // AI idle

    val networkGood = Color(0xFF00FF88)      // Good signal
    val networkPoor = Color(0xFFFFAA00)      // Poor signal
    val networkOffline = Color(0xFF8B92A8)   // Offline

    val batteryHigh = Color(0xFF00FF88)      // High battery
    val batteryMedium = Color(0xFFFFAA00)   // Medium battery
    val batteryLow = Color(0xFFFF3B30)       // Low battery

    val glassSurface = Color(0xFFFFFFFF.copy(alpha = 0.1f))  // Glass morphism
    val glassBorder = Color(0xFFFFFFFF.copy(alpha = 0.2f))    // Glass border
}

// Component shapes
object AppShapes {
    val small = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    val medium = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    val large = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val pill = androidx.compose.foundation.shape.RoundedCornerShape(50)
}

// Typography with high-tech font styles
object AppTypography {
    @Composable
    fun typography() = androidx.compose.material3.Typography(
        displayLarge = androidx.compose.material3.Typography().displayLarge.copy(
            fontWeight = FontWeight.Light,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = androidx.compose.material3.Typography().displayMedium.copy(
            fontWeight = FontWeight.Light,
            letterSpacing = (-0.25).sp
        ),
        displaySmall = androidx.compose.material3.Typography().displaySmall.copy(
            fontWeight = FontWeight.Normal
        ),
        headlineLarge = androidx.compose.material3.Typography().headlineLarge.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.25).sp
        ),
        headlineMedium = androidx.compose.material3.Typography().headlineMedium.copy(
            fontWeight = FontWeight.SemiBold
        ),
        headlineSmall = androidx.compose.material3.Typography().headlineSmall.copy(
            fontWeight = FontWeight.SemiBold
        ),
        titleLarge = androidx.compose.material3.Typography().titleLarge.copy(
            fontWeight = FontWeight.SemiBold
        ),
        titleMedium = androidx.compose.material3.Typography().titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (0.15).sp
        ),
        titleSmall = androidx.compose.material3.Typography().titleSmall.copy(
            fontWeight = FontWeight.Medium
        ),
        bodyLarge = androidx.compose.material3.Typography().bodyLarge.copy(
            fontWeight = FontWeight.Normal,
            letterSpacing = (0.5).sp
        ),
        bodyMedium = androidx.compose.material3.Typography().bodyMedium.copy(
            fontWeight = FontWeight.Normal,
            letterSpacing = (0.25).sp
        ),
        bodySmall = androidx.compose.material3.Typography().bodySmall.copy(
            fontWeight = FontWeight.Normal,
            letterSpacing = (0.4).sp
        ),
        labelLarge = androidx.compose.material3.Typography().labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (0.1).sp
        ),
        labelMedium = androidx.compose.material3.Typography().labelMedium.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = (0.5).sp
        ),
        labelSmall = androidx.compose.material3.Typography().labelSmall.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = (0.5).sp
        )
    )
}

@Composable
fun FibreFieldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val typography = AppTypography.typography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = AppShapes
    ) {
        CompositionLocalProvider(
            LocalExtendedColors provides ExtendedColors,
            LocalAppShapes provides AppShapes,
            content = content
        )
    }
}

// Local composition providers
val LocalExtendedColors = compositionLocalOf { ExtendedColors }
val LocalAppShapes = compositionLocalOf { AppShapes }