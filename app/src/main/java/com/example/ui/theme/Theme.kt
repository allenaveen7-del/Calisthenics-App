package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val AthleticColorScheme = darkColorScheme(
    primary = AthleticCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF80F2FF),
    secondary = AthleticLime,
    onSecondary = Color(0xFF143800),
    secondaryContainer = Color(0xFF285400),
    onSecondaryContainer = Color(0xFFACFF66),
    tertiary = AthleticOrange,
    onTertiary = Color.White,
    background = AthleticBgDark,
    onBackground = AthleticTextPrimary,
    surface = AthleticSurfaceDark,
    onSurface = AthleticTextPrimary,
    surfaceVariant = AthleticSurfaceVariant,
    onSurfaceVariant = AthleticTextSecondary,
    outline = AthleticCardBorder,
    error = Color(0xFFFF5252)
)

private val StealthDarkColorScheme = darkColorScheme(
    primary = StealthAccent,
    onPrimary = Color(0xFF00354A),
    primaryContainer = Color(0xFF004D6A),
    onPrimaryContainer = Color(0xFFC2E8FF),
    secondary = Color(0xFFE2E8F0),
    onSecondary = Color(0xFF1E293B),
    tertiary = AthleticOrange,
    background = StealthBg,
    onBackground = Color(0xFFF1F5F9),
    surface = StealthSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = StealthCard,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    error = Color(0xFFFF5252)
)

private val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF0F172A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = AthleticOrange,
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCard,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = Color(0xFFBA1A1A)
)

@Composable
fun CalisthenicsCoachTheme(
    appThemeSetting: String = "athletic", // "athletic", "dark", "light", "system"
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val colorScheme = when (appThemeSetting.lowercase()) {
        "light" -> LightColorScheme
        "dark" -> StealthDarkColorScheme
        "system" -> if (systemDark) AthleticColorScheme else LightColorScheme
        else -> AthleticColorScheme // default signature athletic
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Keep MyApplicationTheme for backward compatibility if any preview or test references it
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CalisthenicsCoachTheme(appThemeSetting = if (darkTheme) "athletic" else "light", content = content)
}
