package theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = ColorScheme(
    primary = Accent,
    onPrimary = Background,
    primaryContainer = AccentDim,
    onPrimaryContainer = TextPrimary,
    secondary = Surface2,
    onSecondary = TextPrimary,
    secondaryContainer = Surface2,
    onSecondaryContainer = TextPrimary,
    tertiary = Success,
    onTertiary = Background,
    tertiaryContainer = SuccessGlow,
    onTertiaryContainer = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Surface2,
    onSurfaceVariant = TextMuted,
    error = Error,
    onError = TextPrimary,
    errorContainer = ErrorGlow,
    onErrorContainer = TextPrimary,
    outline = CardBorder,
    outlineVariant = Surface2,
    scrim = Background.copy(alpha = 0.8f),
    inverseSurface = TextPrimary,
    inverseOnSurface = Background,
    inversePrimary = AccentDim,
    surfaceTint = Accent,
    surfaceBright = Surface,
    surfaceContainer = Surface,
    surfaceContainerHigh = Surface2,
    surfaceContainerHighest = Surface2,
    surfaceContainerLow = Surface,
    surfaceContainerLowest = Background,
    surfaceDim = Background
)

@Composable
fun TwentyFourTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    CompositionLocalProvider(
        LocalAppSpacing provides AppSpacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TwentyFourTypography,
            content = content
        )
    }
}
