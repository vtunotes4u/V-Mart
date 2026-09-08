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

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFE5E7EB),
    onPrimary = Color(0xFF111827),
    primaryContainer = Color(0xFF374151),
    onPrimaryContainer = Color(0xFFF9FAFB),
    secondary = Color(0xFFD1D5DB),
    onSecondary = Color(0xFF111827),
    secondaryContainer = Color(0xFF374151),
    onSecondaryContainer = Color(0xFFF9FAFB),
    tertiary = MintTertiary,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFF334155),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF111827),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF4F5F7),
    onPrimaryContainer = Color(0xFF111827),
    secondary = Color(0xFF24292F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF4F5F7),
    onSecondaryContainer = Color(0xFF111827),
    tertiary = AvatarDarkGreen,
    tertiaryContainer = Color(0xFFE6F4EA),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF4F5F7),
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Color(0xFFE5E7EB)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Defaulting to clean crisp white theme matching user request
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
