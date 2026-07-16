package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  themeColorIndex: Int = 0,
  content: @Composable () -> Unit,
) {
  val primaryColor = if (darkTheme) ThemeColorsDark.getOrElse(themeColorIndex) { ThemeColorsDark[0] }
                     else ThemeColorsLight.getOrElse(themeColorIndex) { ThemeColorsLight[0] }

  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> darkColorScheme(
          primary = primaryColor,
          onPrimary = OnPrimaryColor,
          background = DarkBackground,
          surface = DarkSurface,
          onBackground = TextPrimaryDark,
          onSurface = TextPrimaryDark,
          error = ExpenseRed
      )
      else -> lightColorScheme(
          primary = primaryColor,
          background = LightBackground,
          surface = LightSurface,
          onPrimary = androidx.compose.ui.graphics.Color.White,
          onBackground = TextPrimaryLight,
          onSurface = TextPrimaryLight,
          error = ExpenseRed
      )
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
