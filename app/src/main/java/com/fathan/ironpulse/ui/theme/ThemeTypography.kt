package com.fathan.ironpulse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.R
import com.fathan.ironpulse.model.UiTheme

val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans, FontWeight.ExtraBold)
)

val ChakraPetchFamily = FontFamily(
    Font(R.font.chakra_petch, FontWeight.Normal),
    Font(R.font.chakra_petch, FontWeight.Medium),
    Font(R.font.chakra_petch, FontWeight.SemiBold),
    Font(R.font.chakra_petch, FontWeight.Bold),
    Font(R.font.chakra_petch, FontWeight.ExtraBold)
)

val UbuntuFamily = FontFamily(
    Font(R.font.ubuntu, FontWeight.Normal),
    Font(R.font.ubuntu, FontWeight.Medium),
    Font(R.font.ubuntu, FontWeight.SemiBold),
    Font(R.font.ubuntu, FontWeight.Bold),
    Font(R.font.ubuntu, FontWeight.ExtraBold)
)

val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk, FontWeight.Normal),
    Font(R.font.space_grotesk, FontWeight.Medium),
    Font(R.font.space_grotesk, FontWeight.SemiBold),
    Font(R.font.space_grotesk, FontWeight.Bold),
    Font(R.font.space_grotesk, FontWeight.ExtraBold)
)

val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
    Font(R.font.jetbrains_mono, FontWeight.Medium),
    Font(R.font.jetbrains_mono, FontWeight.SemiBold),
    Font(R.font.jetbrains_mono, FontWeight.Bold),
    Font(R.font.jetbrains_mono, FontWeight.ExtraBold)
)

val NunitoFamily = FontFamily(
    Font(R.font.nunito, FontWeight.Normal),
    Font(R.font.nunito, FontWeight.Medium),
    Font(R.font.nunito, FontWeight.SemiBold),
    Font(R.font.nunito, FontWeight.Bold),
    Font(R.font.nunito, FontWeight.ExtraBold)
)

@Immutable
data class ThemeFontPack(
    val displayFont: FontFamily,
    val bodyFont: FontFamily,
    val monoFont: FontFamily,
    val fontDisplayName: String,
    val osBadgeLabel: String
)

fun resolveThemeFontPack(theme: UiTheme): ThemeFontPack = when (theme) {
    UiTheme.MATERIAL_YOU -> ThemeFontPack(
        displayFont = PlusJakartaSansFamily,
        bodyFont = PlusJakartaSansFamily,
        monoFont = JetBrainsMonoFamily,
        fontDisplayName = "Plus Jakarta Sans",
        osBadgeLabel = "M3 OS"
    )
    UiTheme.CARBON_FORGE -> ThemeFontPack(
        displayFont = ChakraPetchFamily,
        bodyFont = ChakraPetchFamily,
        monoFont = JetBrainsMonoFamily,
        fontDisplayName = "Chakra Petch",
        osBadgeLabel = "FORGE"
    )
    UiTheme.UBUNTU_YARU -> ThemeFontPack(
        displayFont = UbuntuFamily,
        bodyFont = UbuntuFamily,
        monoFont = JetBrainsMonoFamily,
        fontDisplayName = "Ubuntu",
        osBadgeLabel = "YARU"
    )
    UiTheme.AMOLED_STEALTH -> ThemeFontPack(
        displayFont = JetBrainsMonoFamily,
        bodyFont = SpaceGroteskFamily,
        monoFont = JetBrainsMonoFamily,
        fontDisplayName = "JetBrains Mono + Space",
        osBadgeLabel = "HUD"
    )
    UiTheme.FRUTIGER_AERO -> ThemeFontPack(
        displayFont = NunitoFamily,
        bodyFont = NunitoFamily,
        monoFont = JetBrainsMonoFamily,
        fontDisplayName = "Nunito Aero",
        osBadgeLabel = "AERO"
    )
    UiTheme.XMATERIAL_YOU -> ThemeFontPack(
        displayFont = SpaceGroteskFamily,
        bodyFont = PlusJakartaSansFamily,
        monoFont = JetBrainsMonoFamily,
        fontDisplayName = "Space Grotesk + Jakarta",
        osBadgeLabel = "X-M3"
    )
    UiTheme.TERMINAL_CLI -> ThemeFontPack(
        displayFont = JetBrainsMonoFamily,
        bodyFont = JetBrainsMonoFamily,
        monoFont = JetBrainsMonoFamily,
        fontDisplayName = "JetBrains Mono",
        osBadgeLabel = "CLI"
    )
}

fun buildIronPulseTypography(theme: UiTheme): Typography {
    val pack = resolveThemeFontPack(theme)
    val display = pack.displayFont
    val body = pack.bodyFont
    val displayTracking = when (theme) {
        UiTheme.CARBON_FORGE -> 0.5.sp
        UiTheme.AMOLED_STEALTH -> 0.4.sp
        UiTheme.TERMINAL_CLI -> 0.3.sp
        UiTheme.FRUTIGER_AERO -> (-0.2).sp
        UiTheme.XMATERIAL_YOU -> (-0.25).sp
        else -> 0.sp
    }
    val labelTracking = when (theme) {
        UiTheme.CARBON_FORGE -> 0.6.sp
        UiTheme.AMOLED_STEALTH -> 0.5.sp
        UiTheme.TERMINAL_CLI -> 0.6.sp
        UiTheme.XMATERIAL_YOU -> 0.25.sp
        else -> 0.1.sp
    }

    return Typography(
        displayLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 44.sp,
            lineHeight = 50.sp,
            letterSpacing = displayTracking,
            fontFeatureSettings = "tnum"
        ),
        displayMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            letterSpacing = displayTracking,
            fontFeatureSettings = "tnum"
        ),
        displaySmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 31.sp,
            lineHeight = 36.sp,
            letterSpacing = displayTracking,
            fontFeatureSettings = "tnum"
        ),
        headlineMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            lineHeight = 31.sp,
            letterSpacing = displayTracking
        ),
        headlineSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            lineHeight = 27.sp,
            letterSpacing = displayTracking
        ),
        titleLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            lineHeight = 25.sp,
            letterSpacing = displayTracking
        ),
        titleMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        ),
        titleSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 19.sp
        ),
        bodySmall = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),
        labelLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = labelTracking,
            fontFeatureSettings = "tnum"
        ),
        labelMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = labelTracking,
            fontFeatureSettings = "tnum"
        ),
        labelSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = labelTracking,
            fontFeatureSettings = "tnum"
        )
    )
}
