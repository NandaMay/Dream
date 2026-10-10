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

enum class AppThemeSetting {
  LIGHT_ROMANTIC,
  DARK_GOLD,
  SYSTEM
}

val RomanticLightColorScheme = lightColorScheme(
  primary = RomanticRose,
  onPrimary = Color.White,
  primaryContainer = RomanticBlush,
  onPrimaryContainer = MyanmarRedAccent,
  secondary = WarmGold,
  onSecondary = Color.Black,
  secondaryContainer = WarmGoldLight,
  onSecondaryContainer = WarmGoldDark,
  tertiary = MyanmarRedAccent,
  onTertiary = Color.White,
  background = RomanticCream,
  onBackground = RomanticDarkText,
  surface = RomanticSurface,
  onSurface = RomanticDarkText,
  surfaceVariant = RomanticBlush,
  onSurfaceVariant = RomanticMutedText,
  outline = RomanticBorder
)

val BlackAndGoldDarkColorScheme = darkColorScheme(
  primary = DarkGoldPrimary,
  onPrimary = Color.Black,
  primaryContainer = DarkSurfaceVariant,
  onPrimaryContainer = DarkGoldPrimary,
  secondary = DarkRoseAccent,
  onSecondary = Color.White,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = DarkRoseAccent,
  tertiary = WarmGold,
  onTertiary = Color.Black,
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkBorder
)

@Composable
fun OurDreamTheme(
  themeSetting: AppThemeSetting = AppThemeSetting.LIGHT_ROMANTIC,
  content: @Composable () -> Unit
) {
  val isDark = when (themeSetting) {
    AppThemeSetting.LIGHT_ROMANTIC -> false
    AppThemeSetting.DARK_GOLD -> true
    AppThemeSetting.SYSTEM -> isSystemInDarkTheme()
  }

  val colorScheme = if (isDark) {
    BlackAndGoldDarkColorScheme
  } else {
    RomanticLightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Backwards compatibility alias for template references
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  OurDreamTheme(
    themeSetting = if (darkTheme) AppThemeSetting.DARK_GOLD else AppThemeSetting.LIGHT_ROMANTIC,
    content = content
  )
}
