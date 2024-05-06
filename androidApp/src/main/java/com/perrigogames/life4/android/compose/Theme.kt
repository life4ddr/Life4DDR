package com.perrigogames.life4.android.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.perrigogames.life4.enums.LadderRankClass

private val LightColors =
    lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )

private val DarkColors =
    darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        errorContainer = md_theme_dark_errorContainer,
        onError = md_theme_dark_onError,
        onErrorContainer = md_theme_dark_onErrorContainer,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        outline = md_theme_dark_outline,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inverseSurface = md_theme_dark_inverseSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )

private val CopperLightColors =
    lightColorScheme(
        primary = light_Copper,
        onPrimary = light_onCopper,
        primaryContainer = light_CopperContainer,
        onPrimaryContainer = light_onCopperContainer,
    )
private val CopperDarkColors =
    darkColorScheme(
        primary = dark_Copper,
        onPrimary = dark_onCopper,
        primaryContainer = dark_CopperContainer,
        onPrimaryContainer = dark_onCopperContainer,
    )
private val BronzeLightColors =
    lightColorScheme(
        primary = light_Bronze,
        onPrimary = light_onBronze,
        primaryContainer = light_BronzeContainer,
        onPrimaryContainer = light_onBronzeContainer,
    )
private val BronzeDarkColors =
    darkColorScheme(
        primary = dark_Bronze,
        onPrimary = dark_onBronze,
        primaryContainer = dark_BronzeContainer,
        onPrimaryContainer = dark_onBronzeContainer,
    )
private val SilverLightColors =
    lightColorScheme(
        primary = light_Silver,
        onPrimary = light_onSilver,
        primaryContainer = light_SilverContainer,
        onPrimaryContainer = light_onSilverContainer,
    )
private val SilverDarkColors =
    darkColorScheme(
        primary = dark_Silver,
        onPrimary = dark_onSilver,
        primaryContainer = dark_SilverContainer,
        onPrimaryContainer = dark_onSilverContainer,
    )
private val GoldLightColors =
    lightColorScheme(
        primary = light_Gold,
        onPrimary = light_onGold,
        primaryContainer = light_GoldContainer,
        onPrimaryContainer = light_onGoldContainer,
    )
private val GoldDarkColors =
    darkColorScheme(
        primary = dark_Gold,
        onPrimary = dark_onGold,
        primaryContainer = dark_GoldContainer,
        onPrimaryContainer = dark_onGoldContainer,
    )
private val PlatinumLightColors =
    lightColorScheme(
        primary = light_Platinum,
        onPrimary = light_onPlatinum,
        primaryContainer = light_PlatinumContainer,
        onPrimaryContainer = light_onPlatinumContainer,
    )
private val PlatinumDarkColors =
    darkColorScheme(
        primary = dark_Platinum,
        onPrimary = dark_onPlatinum,
        primaryContainer = dark_PlatinumContainer,
        onPrimaryContainer = dark_onPlatinumContainer,
    )
private val DiamondLightColors =
    lightColorScheme(
        primary = light_Diamond,
        onPrimary = light_onDiamond,
        primaryContainer = light_DiamondContainer,
        onPrimaryContainer = light_onDiamondContainer,
    )
private val DiamondDarkColors =
    darkColorScheme(
        primary = dark_Diamond,
        onPrimary = dark_onDiamond,
        primaryContainer = dark_DiamondContainer,
        onPrimaryContainer = dark_onDiamondContainer,
    )
private val CobaltLightColors =
    lightColorScheme(
        primary = light_Cobalt,
        onPrimary = light_onCobalt,
        primaryContainer = light_CobaltContainer,
        onPrimaryContainer = light_onCobaltContainer,
    )
private val CobaltDarkColors =
    darkColorScheme(
        primary = dark_Cobalt,
        onPrimary = dark_onCobalt,
        primaryContainer = dark_CobaltContainer,
        onPrimaryContainer = dark_onCobaltContainer,
    )
private val PearlLightColors =
    lightColorScheme(
        primary = light_Pearl,
        onPrimary = light_onPearl,
        primaryContainer = light_PearlContainer,
        onPrimaryContainer = light_onPearlContainer,
    )
private val PearlDarkColors =
    darkColorScheme(
        primary = dark_Pearl,
        onPrimary = dark_onPearl,
        primaryContainer = dark_PearlContainer,
        onPrimaryContainer = dark_onPearlContainer,
    )
private val AmethystLightColors =
    lightColorScheme(
        primary = light_Amethyst,
        onPrimary = light_onAmethyst,
        primaryContainer = light_AmethystContainer,
        onPrimaryContainer = light_onAmethystContainer,
    )
private val AmethystDarkColors =
    darkColorScheme(
        primary = dark_Amethyst,
        onPrimary = dark_onAmethyst,
        primaryContainer = dark_AmethystContainer,
        onPrimaryContainer = dark_onAmethystContainer,
    )
private val EmeraldLightColors =
    lightColorScheme(
        primary = light_Emerald,
        onPrimary = light_onEmerald,
        primaryContainer = light_EmeraldContainer,
        onPrimaryContainer = light_onEmeraldContainer,
    )
private val EmeraldDarkColors =
    darkColorScheme(
        primary = dark_Emerald,
        onPrimary = dark_onEmerald,
        primaryContainer = dark_EmeraldContainer,
        onPrimaryContainer = dark_onEmeraldContainer,
    )
private val OnyxLightColors =
    lightColorScheme(
        primary = light_Onyx,
        onPrimary = light_onOnyx,
        primaryContainer = light_OnyxContainer,
        onPrimaryContainer = light_onOnyxContainer,
    )
private val OnyxDarkColors =
    darkColorScheme(
        primary = dark_Onyx,
        onPrimary = dark_onOnyx,
        primaryContainer = dark_OnyxContainer,
        onPrimaryContainer = dark_onOnyxContainer,
    )

@Composable
fun primaryButtonColors() =
    ButtonDefaults.buttonColors(
        containerColor = seed,
        contentColor = Color.White,
    )

@Composable
fun LIFE4Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColors
            else -> LightColors
        }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.primary.toArgb()
            window.statusBarColor = seed.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

@Composable
fun LightDarkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    lightColors: ColorScheme,
    darkColors: ColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme =
            when {
                darkTheme -> darkColors
                else -> lightColors
            },
        content = content,
    )
}

@Composable
fun CopperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, CopperLightColors, CopperDarkColors, content)

@Composable
fun BronzeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, BronzeLightColors, BronzeDarkColors, content)

@Composable
fun SilverTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, SilverLightColors, SilverDarkColors, content)

@Composable
fun GoldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, GoldLightColors, GoldDarkColors, content)

@Composable
fun PlatinumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, PlatinumLightColors, PlatinumDarkColors, content)

@Composable
fun DiamondTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, DiamondLightColors, DiamondDarkColors, content)

@Composable
fun CobaltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, CobaltLightColors, CobaltDarkColors, content)

@Composable
fun PearlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, PearlLightColors, PearlDarkColors, content)

@Composable
fun AmethystTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, AmethystLightColors, AmethystDarkColors, content)

@Composable
fun EmeraldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, EmeraldLightColors, EmeraldDarkColors, content)

@Composable
fun OnyxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = LightDarkTheme(darkTheme, OnyxLightColors, OnyxDarkColors, content)

@Composable
fun LadderRankClassTheme(
    ladderRankClass: LadderRankClass,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = when (ladderRankClass) {
    LadderRankClass.COPPER -> CopperTheme(darkTheme, content)
    LadderRankClass.BRONZE -> BronzeTheme(darkTheme, content)
    LadderRankClass.SILVER -> SilverTheme(darkTheme, content)
    LadderRankClass.GOLD -> GoldTheme(darkTheme, content)
    LadderRankClass.PLATINUM -> PlatinumTheme(darkTheme, content)
    LadderRankClass.DIAMOND -> DiamondTheme(darkTheme, content)
    LadderRankClass.COBALT -> CobaltTheme(darkTheme, content)
    LadderRankClass.PEARL -> PearlTheme(darkTheme, content)
    LadderRankClass.AMETHYST -> AmethystTheme(darkTheme, content)
    LadderRankClass.EMERALD -> EmeraldTheme(darkTheme, content)
    LadderRankClass.ONYX -> OnyxTheme(darkTheme, content)
}
