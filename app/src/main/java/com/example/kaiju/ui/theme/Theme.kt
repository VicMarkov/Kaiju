package com.example.kaiju.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VerdeBosqueClaro,
    onPrimary = VerdeBosqueHondo,
    primaryContainer = VerdeBosqueMedio,
    onPrimaryContainer = VerdeNiebla,
    secondary = AmbarClaro,
    onSecondary = AmbarHondo,
    secondaryContainer = AmbarFondo,
    onSecondaryContainer = AmbarNiebla,
    tertiary = TealClaro,
    onTertiary = TealHondo,
    tertiaryContainer = TealFondo,
    onTertiaryContainer = TealNiebla,
    background = NeutroOscuroFondo,
    onBackground = NeutroOscuroTexto,
    surface = NeutroOscuroSuperficie,
    onSurface = NeutroOscuroTexto,
    surfaceVariant = NeutroOscuroVariant,
    onSurfaceVariant = NeutroOscuroVariantTexto,
    surfaceContainerLowest = Color(0xFF0B0F0B),
    surfaceContainerLow = Color(0xFF121712),
    surfaceContainer = Color(0xFF161B16),
    surfaceContainerHigh = Color(0xFF1B211B),
    surfaceContainerHighest = NeutroOscuroVariant,
    surfaceDim = NeutroOscuroFondo,
    surfaceBright = Color(0xFF363A35),
    outline = NeutroOscuroBorde,
    outlineVariant = NeutroOscuroBordeSuave,
    error = RojoErrorOscuro,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    inverseSurface = NeutroOscuroTexto,
    inverseOnSurface = Color(0xFF2F312F),
    inversePrimary = VerdeBosque
)

private val LightColorScheme = lightColorScheme(
    primary = VerdeBosque,
    onPrimary = NeutroClaroSuperficie,
    primaryContainer = VerdeNiebla,
    onPrimaryContainer = VerdeBosqueHondo,
    secondary = Ambar,
    onSecondary = NeutroClaroSuperficie,
    secondaryContainer = AmbarNiebla,
    onSecondaryContainer = AmbarHondo,
    tertiary = Teal,
    onTertiary = NeutroClaroSuperficie,
    tertiaryContainer = TealNiebla,
    onTertiaryContainer = TealHondo,
    background = NeutroClaroFondo,
    onBackground = NeutroClaroTexto,
    surface = NeutroClaroSuperficie,
    onSurface = NeutroClaroTexto,
    surfaceVariant = NeutroClaroVariant,
    onSurfaceVariant = NeutroClaroVariantTexto,
    surfaceContainerLowest = NeutroClaroSuperficie,
    surfaceContainerLow = Color(0xFFF5F9F4),
    surfaceContainer = Color(0xFFEFF4EE),
    surfaceContainerHigh = Color(0xFFE9EFE8),
    surfaceContainerHighest = Color(0xFFE3E9E2),
    surfaceDim = Color(0xFFEFF3EE),
    surfaceBright = NeutroClaroSuperficie,
    outline = NeutroClaroBorde,
    outlineVariant = NeutroClaroBordeSuave,
    error = RojoError,
    onError = NeutroClaroSuperficie,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410002),
    inverseSurface = NeutroClaroTexto,
    inverseOnSurface = Color(0xFFF1F4EF),
    inversePrimary = VerdeBosqueClaro
)

@Composable
fun KaijuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalStockColores provides if (darkTheme) StockOscuro else StockClaro) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = KaijuShapes,
            content = content
        )
    }
}
