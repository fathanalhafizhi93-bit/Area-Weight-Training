package com.fathan.ironpulse.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.fathan.ironpulse.model.UiTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

@Immutable
data class MaterialYouPalette(
    val seed: String,
    val primaryHex: String,
    val onPrimaryHex: String,
    val primaryContainerHex: String,
    val onPrimaryContainerHex: String,
    val secondaryHex: String,
    val onSecondaryHex: String,
    val secondaryContainerHex: String,
    val onSecondaryContainerHex: String,
    val tertiaryHex: String,
    val onTertiaryHex: String,
    val tertiaryContainerHex: String,
    val onTertiaryContainerHex: String,
    val surfaceHex: String,
    val surfaceContainerLowestHex: String,
    val surfaceContainerLowHex: String,
    val surfaceContainerHex: String,
    val surfaceContainerHighHex: String,
    val surfaceContainerHighestHex: String,
    val onSurfaceHex: String,
    val onSurfaceVariantHex: String,
    val outlineHex: String,
    val outlineVariantHex: String,
    // Compose Colors
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val surface: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color
)

object MaterialYouColorEngine {

    const val DEFAULT_ACCENT_COLOR = "#C8FF2E"
    const val YARU_ORANGE_ACCENT = "#E95420"
    const val AMOLED_STEALTH_CYAN_ACCENT = "#00E5FF"
    const val AERO_SKY_ACCENT = "#0099E5"
    const val XMATERIAL_MORNING_ACCENT = "#DFBE84"
    const val XMATERIAL_AFTERNOON_ACCENT = "#8CC5C2"
    const val XMATERIAL_NIGHT_ACCENT = "#B0A4E3"
    const val CLI_PHOSPHOR_ACCENT = "#00FF66"

    fun xmaterialAccentForPhase(phase: com.fathan.ironpulse.model.DayTimePhase): String = when (phase) {
        com.fathan.ironpulse.model.DayTimePhase.MORNING -> XMATERIAL_MORNING_ACCENT
        com.fathan.ironpulse.model.DayTimePhase.AFTERNOON -> XMATERIAL_AFTERNOON_ACCENT
        com.fathan.ironpulse.model.DayTimePhase.NIGHT -> XMATERIAL_NIGHT_ACCENT
    }

    fun defaultTintForTheme(theme: UiTheme): String = when (theme) {
        UiTheme.MATERIAL_YOU -> "#1C1B22"
        UiTheme.CARBON_FORGE -> "#12151B"
        UiTheme.UBUNTU_YARU -> "#2E182A"
        UiTheme.AMOLED_STEALTH -> "#07080B"
        UiTheme.FRUTIGER_AERO -> "#DDF5FF"
        UiTheme.XMATERIAL_YOU -> "#0E0F13"
        UiTheme.TERMINAL_CLI -> "#050806"
    }

    fun normalizeHexColor(hex: String?, fallback: String = DEFAULT_ACCENT_COLOR): String {
        val cleaned = (hex ?: "").trim()
        return if (Regex("^#[0-9A-Fa-f]{6}$").matches(cleaned)) {
            cleaned.uppercase()
        } else {
            fallback.uppercase()
        }
    }

    fun parseColor(hex: String, fallback: Color = Color(0xFFC8FF2E)): Color {
        val valid = normalizeHexColor(hex, "")
        if (valid.isEmpty()) return fallback
        return Color(
            red = valid.substring(1, 3).toInt(16),
            green = valid.substring(3, 5).toInt(16),
            blue = valid.substring(5, 7).toInt(16)
        )
    }

    fun colorToHsv(color: Color): FloatArray {
        val r = color.red.coerceIn(0f, 1f)
        val g = color.green.coerceIn(0f, 1f)
        val b = color.blue.coerceIn(0f, 1f)
        val maxC = max(r, max(g, b))
        val minC = min(r, min(g, b))
        val delta = maxC - minC

        val hue = when {
            delta < 1e-5f -> 0f
            maxC == r -> 60f * (((g - b) / delta) % 6f)
            maxC == g -> 60f * (((b - r) / delta) + 2f)
            else -> 60f * (((r - g) / delta) + 4f)
        }.let { if (it < 0f) it + 360f else it }

        val sat = if (maxC <= 1e-5f) 0f else delta / maxC
        return floatArrayOf(hue.coerceIn(0f, 360f), sat.coerceIn(0f, 1f), maxC.coerceIn(0f, 1f))
    }

    fun colorToHex(color: Color): String {
        val ri = (color.red.coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
        val gi = (color.green.coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
        val bi = (color.blue.coerceIn(0f, 1f) * 255f).roundToInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", ri, gi, bi)
    }

    fun friendlyColorName(
        hex: String,
        presets: List<com.fathan.ironpulse.model.ColorPresetOption>,
        lang: com.fathan.ironpulse.model.AppLanguage
    ): String {
        val matched = presets.firstOrNull { it.hex.equals(hex, ignoreCase = true) }
        if (matched != null) return lang.t(matched.labelId, matched.labelEn)

        val hsv = colorToHsv(parseColor(hex))
        val h = hsv[0]
        val s = hsv[1]
        val v = hsv[2]
        if (v < 0.12f) return lang.t("Hitam Pekat", "Deep Onyx")
        if (s < 0.14f) return lang.t("Abu Netral", "Neutral Slate")

        val family = when {
            h < 18f || h >= 342f -> lang.t("Merah", "Crimson")
            h < 42f -> lang.t("Oranye", "Amber")
            h < 68f -> lang.t("Kuning", "Gold")
            h < 155f -> lang.t("Hijau", "Emerald")
            h < 195f -> lang.t("Cyan", "Cyan")
            h < 252f -> lang.t("Biru", "Azure")
            h < 292f -> lang.t("Ungu", "Violet")
            else -> lang.t("Pink", "Rose")
        }
        return "$family · ${lang.t("Kustom", "Custom")}"
    }

    private fun hexToRgb(hex: String): Triple<Int, Int, Int> {
        val valid = normalizeHexColor(hex)
        return Triple(
            valid.substring(1, 3).toInt(16),
            valid.substring(3, 5).toInt(16),
            valid.substring(5, 7).toInt(16)
        )
    }

    private fun rgbToHex(r: Double, g: Double, b: Double): String {
        val ri = r.roundToInt().coerceIn(0, 255)
        val gi = g.roundToInt().coerceIn(0, 255)
        val bi = b.roundToInt().coerceIn(0, 255)
        return String.format("#%02X%02X%02X", ri, gi, bi)
    }

    private fun rgbToHsl(r: Int, g: Int, b: Int): Triple<Double, Double, Double> {
        val rn = r / 255.0
        val gn = g / 255.0
        val bn = b / 255.0
        val maxC = max(rn, max(gn, bn))
        val minC = min(rn, min(gn, bn))
        val delta = maxC - minC

        var h = 0.0
        var s = 0.0
        val l = (maxC + minC) / 2.0

        if (delta != 0.0) {
            s = delta / (1.0 - abs(2.0 * l - 1.0))
            h = when (maxC) {
                rn -> 60.0 * (((gn - bn) / delta) % 6.0)
                gn -> 60.0 * ((bn - rn) / delta + 2.0)
                else -> 60.0 * ((rn - gn) / delta + 4.0)
            }
        }
        if (h < 0.0) h += 360.0
        return Triple(h, s * 100.0, l * 100.0)
    }

    private fun hslToHex(h: Double, s: Double, l: Double): String {
        val hue = ((h % 360.0) + 360.0) % 360.0
        val sat = s.coerceIn(0.0, 100.0) / 100.0
        val lig = l.coerceIn(0.0, 100.0) / 100.0

        val c = (1.0 - abs(2.0 * lig - 1.0)) * sat
        val x = c * (1.0 - abs(((hue / 60.0) % 2.0) - 1.0))
        val m = lig - c / 2.0

        val (r, g, b) = when {
            hue < 60.0 -> Triple(c, x, 0.0)
            hue < 120.0 -> Triple(x, c, 0.0)
            hue < 180.0 -> Triple(0.0, c, x)
            hue < 240.0 -> Triple(0.0, x, c)
            hue < 300.0 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }
        return rgbToHex((r + m) * 255.0, (g + m) * 255.0, (b + m) * 255.0)
    }

    private fun relativeLuminance(hex: String): Double {
        val (r, g, b) = hexToRgb(hex)
        fun toLinear(channel: Int): Double {
            val s = channel / 255.0
            return if (s <= 0.03928) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * toLinear(r) + 0.7152 * toLinear(g) + 0.0722 * toLinear(b)
    }

    private fun contrastRatio(hexA: String, hexB: String): Double {
        val lumA = relativeLuminance(hexA)
        val lumB = relativeLuminance(hexB)
        val lighter = max(lumA, lumB)
        val darker = min(lumA, lumB)
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun computeTonalOnColor(bgHex: String, hue: Double, seedSat: Double): String {
        val darkCandidate = hslToHex(hue, (seedSat * 0.35).coerceIn(6.0, 32.0), 8.5)
        val lightCandidate = hslToHex(hue, (seedSat * 0.25).coerceIn(6.0, 26.0), 96.0)
        val darkRatio = contrastRatio(bgHex, darkCandidate)
        val lightRatio = contrastRatio(bgHex, lightCandidate)
        return if (darkRatio >= lightRatio) {
            if (darkRatio >= 4.5) darkCandidate else "#08090C"
        } else {
            if (lightRatio >= 4.5) lightCandidate else "#FFFFFF"
        }
    }

    /**
     * Ensures any theme accent (including bright neon lime or cyan) remains crisp and
     * WCAG AA readable when rendered as text, icons, or badges on light Frutiger Aero surfaces.
     */
    fun readableAccentOnLight(color: Color): Color {
        val hex = colorToHex(color)
        if (contrastRatio(hex, "#F5FBFF") >= 4.6) return color
        val (r, g, b) = hexToRgb(hex)
        val (h, s, l) = rgbToHsl(r, g, b)
        val targetSat = s.coerceIn(42.0, 88.0)
        var targetLight = min(l, 38.0)
        while (targetLight > 18.0 && contrastRatio(hslToHex(h, targetSat, targetLight), "#F5FBFF") < 5.0) {
            targetLight -= 3.0
        }
        return parseColor(hslToHex(h, targetSat, targetLight), Color(0xFF005F9E))
    }

    fun generateMaterialYouPalette(
        seedHex: String,
        surfaceTintOverrideHex: String? = null,
        isLight: Boolean = false,
        isAeroNight: Boolean = false,
        xMaterialPhase: com.fathan.ironpulse.model.DayTimePhase? = null,
        isCli: Boolean = false
    ): MaterialYouPalette {
        if (isCli) {
            return buildTerminalCliPalette()
        }
        if (xMaterialPhase != null) {
            return buildXMaterialYouPhasePalette(xMaterialPhase)
        }
        if (isAeroNight) {
            return buildFrutigerAeroTwilightPalette(normalizeHexColor(seedHex, AERO_SKY_ACCENT))
        }
        if (isLight) {
            return buildFrutigerAeroLightPalette(normalizeHexColor(seedHex, AERO_SKY_ACCENT))
        }
        val seed = normalizeHexColor(seedHex, DEFAULT_ACCENT_COLOR)
        val (r, g, b) = hexToRgb(seed)
        val (seedH, seedS, _) = rgbToHsl(r, g, b)

        var surfaceH = seedH
        var surfaceSatBoost = seedS
        if (!surfaceTintOverrideHex.isNullOrBlank() && Regex("^#[0-9A-Fa-f]{6}$").matches(surfaceTintOverrideHex)) {
            val (or, og, ob) = hexToRgb(surfaceTintOverrideHex)
            val (oh, os, _) = rgbToHsl(or, og, ob)
            if (os >= 6.0) {
                surfaceH = oh
                surfaceSatBoost = max(os, 18.0)
            }
        }

        val isNeutralSeed = seedS < 8.0
        val primaryHex = seed
        val onPrimaryHex = computeTonalOnColor(primaryHex, seedH, seedS)

        val primaryContainerHex = hslToHex(
            seedH,
            if (isNeutralSeed) 6.0 else (seedS * 0.52).coerceIn(22.0, 54.0),
            22.0
        )
        val onPrimaryContainerHex = hslToHex(
            seedH,
            if (isNeutralSeed) 8.0 else (seedS * 0.78).coerceIn(36.0, 90.0),
            90.0
        )

        val secondaryHex = hslToHex(
            seedH,
            if (isNeutralSeed) 6.0 else (seedS * 0.34).coerceIn(14.0, 34.0),
            78.0
        )
        val onSecondaryHex = computeTonalOnColor(secondaryHex, seedH, seedS * 0.4)
        val secondaryContainerHex = hslToHex(
            seedH,
            if (isNeutralSeed) 8.0 else (seedS * 0.34).coerceIn(16.0, 36.0),
            24.0
        )
        val onSecondaryContainerHex = hslToHex(
            seedH,
            if (isNeutralSeed) 10.0 else (seedS * 0.56).coerceIn(24.0, 74.0),
            91.0
        )

        val tertiaryH = (seedH + 55.0) % 360.0
        val tertiaryHex = hslToHex(
            tertiaryH,
            if (isNeutralSeed) 10.0 else (seedS * 0.56).coerceIn(26.0, 66.0),
            78.0
        )
        val onTertiaryHex = computeTonalOnColor(tertiaryHex, tertiaryH, seedS * 0.5)
        val tertiaryContainerHex = hslToHex(
            tertiaryH,
            if (isNeutralSeed) 10.0 else (seedS * 0.42).coerceIn(18.0, 42.0),
            23.0
        )
        val onTertiaryContainerHex = hslToHex(
            tertiaryH,
            if (isNeutralSeed) 10.0 else (seedS * 0.62).coerceIn(28.0, 78.0),
            90.0
        )

        val neutralSat = if (surfaceSatBoost < 8.0) 3.0 else (surfaceSatBoost * 0.14).coerceIn(7.0, 15.0)
        val surfaceHex = hslToHex(surfaceH, neutralSat, 6.8)
        val surfaceContainerLowestHex = hslToHex(surfaceH, neutralSat, 4.6)
        val surfaceContainerLowHex = hslToHex(surfaceH, (neutralSat + 1.0).coerceIn(3.0, 17.0), 9.6)
        val surfaceContainerHex = hslToHex(surfaceH, (neutralSat + 2.0).coerceIn(4.0, 18.0), 12.2)
        val surfaceContainerHighHex = hslToHex(surfaceH, (neutralSat + 3.0).coerceIn(4.0, 20.0), 15.6)
        val surfaceContainerHighestHex = hslToHex(surfaceH, (neutralSat + 4.0).coerceIn(5.0, 22.0), 19.6)

        val onSurfaceHex = hslToHex(surfaceH, (neutralSat * 0.9).coerceIn(2.0, 14.0), 93.5)
        val onSurfaceVariantHex = hslToHex(surfaceH, (neutralSat + 3.0).coerceIn(4.0, 18.0), 74.0)
        val outlineHex = hslToHex(surfaceH, (neutralSat + 5.0).coerceIn(5.0, 20.0), 47.0)
        val outlineVariantHex = hslToHex(surfaceH, (neutralSat + 4.0).coerceIn(5.0, 19.0), 25.0)

        return MaterialYouPalette(
            seed = seed,
            primaryHex = primaryHex,
            onPrimaryHex = onPrimaryHex,
            primaryContainerHex = primaryContainerHex,
            onPrimaryContainerHex = onPrimaryContainerHex,
            secondaryHex = secondaryHex,
            onSecondaryHex = onSecondaryHex,
            secondaryContainerHex = secondaryContainerHex,
            onSecondaryContainerHex = onSecondaryContainerHex,
            tertiaryHex = tertiaryHex,
            onTertiaryHex = onTertiaryHex,
            tertiaryContainerHex = tertiaryContainerHex,
            onTertiaryContainerHex = onTertiaryContainerHex,
            surfaceHex = surfaceHex,
            surfaceContainerLowestHex = surfaceContainerLowestHex,
            surfaceContainerLowHex = surfaceContainerLowHex,
            surfaceContainerHex = surfaceContainerHex,
            surfaceContainerHighHex = surfaceContainerHighHex,
            surfaceContainerHighestHex = surfaceContainerHighestHex,
            onSurfaceHex = onSurfaceHex,
            onSurfaceVariantHex = onSurfaceVariantHex,
            outlineHex = outlineHex,
            outlineVariantHex = outlineVariantHex,
            primary = parseColor(primaryHex),
            onPrimary = parseColor(onPrimaryHex),
            primaryContainer = parseColor(primaryContainerHex),
            onPrimaryContainer = parseColor(onPrimaryContainerHex),
            secondary = parseColor(secondaryHex),
            onSecondary = parseColor(onSecondaryHex),
            secondaryContainer = parseColor(secondaryContainerHex),
            onSecondaryContainer = parseColor(onSecondaryContainerHex),
            tertiary = parseColor(tertiaryHex),
            onTertiary = parseColor(onTertiaryHex),
            tertiaryContainer = parseColor(tertiaryContainerHex),
            onTertiaryContainer = parseColor(onTertiaryContainerHex),
            surface = parseColor(surfaceHex),
            surfaceContainerLowest = parseColor(surfaceContainerLowestHex),
            surfaceContainerLow = parseColor(surfaceContainerLowHex),
            surfaceContainer = parseColor(surfaceContainerHex),
            surfaceContainerHigh = parseColor(surfaceContainerHighHex),
            surfaceContainerHighest = parseColor(surfaceContainerHighestHex),
            onSurface = parseColor(onSurfaceHex),
            onSurfaceVariant = parseColor(onSurfaceVariantHex),
            outline = parseColor(outlineHex),
            outlineVariant = parseColor(outlineVariantHex)
        )
    }

    private fun buildFrutigerAeroLightPalette(primaryHex: String): MaterialYouPalette {
        val onPrimaryHex = "#FFFFFF"
        val primaryContainerHex = "#CBEFFF"
        val onPrimaryContainerHex = "#022D4A"
        val secondaryHex = "#0F9D58"
        val onSecondaryHex = "#FFFFFF"
        val secondaryContainerHex = "#D2F6E2"
        val onSecondaryContainerHex = "#063C21"
        val tertiaryHex = "#0369A1"
        val onTertiaryHex = "#FFFFFF"
        val tertiaryContainerHex = "#D6F1FF"
        val onTertiaryContainerHex = "#042E4A"

        val surfaceHex = "#DFF4FF"
        val surfaceContainerLowestHex = "#FFFFFF"
        val surfaceContainerLowHex = "#F5FBFF"
        val surfaceContainerHex = "#ECF8FF"
        val surfaceContainerHighHex = "#DCEFFF"
        val surfaceContainerHighestHex = "#C8E6FF"
        val onSurfaceHex = "#071E36"
        val onSurfaceVariantHex = "#24496A"
        val outlineHex = "#4C87B2"
        val outlineVariantHex = "#8EC6E8"

        return MaterialYouPalette(
            seed = primaryHex,
            primaryHex = primaryHex,
            onPrimaryHex = onPrimaryHex,
            primaryContainerHex = primaryContainerHex,
            onPrimaryContainerHex = onPrimaryContainerHex,
            secondaryHex = secondaryHex,
            onSecondaryHex = onSecondaryHex,
            secondaryContainerHex = secondaryContainerHex,
            onSecondaryContainerHex = onSecondaryContainerHex,
            tertiaryHex = tertiaryHex,
            onTertiaryHex = onTertiaryHex,
            tertiaryContainerHex = tertiaryContainerHex,
            onTertiaryContainerHex = onTertiaryContainerHex,
            surfaceHex = surfaceHex,
            surfaceContainerLowestHex = surfaceContainerLowestHex,
            surfaceContainerLowHex = surfaceContainerLowHex,
            surfaceContainerHex = surfaceContainerHex,
            surfaceContainerHighHex = surfaceContainerHighHex,
            surfaceContainerHighestHex = surfaceContainerHighestHex,
            onSurfaceHex = onSurfaceHex,
            onSurfaceVariantHex = onSurfaceVariantHex,
            outlineHex = outlineHex,
            outlineVariantHex = outlineVariantHex,
            primary = parseColor(primaryHex),
            onPrimary = parseColor(onPrimaryHex),
            primaryContainer = parseColor(primaryContainerHex),
            onPrimaryContainer = parseColor(onPrimaryContainerHex),
            secondary = parseColor(secondaryHex),
            onSecondary = parseColor(onSecondaryHex),
            secondaryContainer = parseColor(secondaryContainerHex),
            onSecondaryContainer = parseColor(onSecondaryContainerHex),
            tertiary = parseColor(tertiaryHex),
            onTertiary = parseColor(onTertiaryHex),
            tertiaryContainer = parseColor(tertiaryContainerHex),
            onTertiaryContainer = parseColor(onTertiaryContainerHex),
            surface = parseColor(surfaceHex),
            surfaceContainerLowest = parseColor(surfaceContainerLowestHex),
            surfaceContainerLow = parseColor(surfaceContainerLowHex),
            surfaceContainer = parseColor(surfaceContainerHex),
            surfaceContainerHigh = parseColor(surfaceContainerHighHex),
            surfaceContainerHighest = parseColor(surfaceContainerHighestHex),
            onSurface = parseColor(onSurfaceHex),
            onSurfaceVariant = parseColor(onSurfaceVariantHex),
            outline = parseColor(outlineHex),
            outlineVariant = parseColor(outlineVariantHex)
        )
    }

    /**
     * Soft Twilight Dark Frutiger Aero ("Aero Senja Lembut") palette:
     * medium twilight indigo-ocean glass, fresh aqua-teal highlights, and crisp ice-white ink.
     */
    private fun buildFrutigerAeroTwilightPalette(primaryHex: String): MaterialYouPalette {
        val onPrimaryHex = "#FFFFFF"
        val primaryContainerHex = "#1A4B7C"
        val onPrimaryContainerHex = "#E0F4FF"
        val secondaryHex = "#34D399"
        val onSecondaryHex = "#042C1C"
        val secondaryContainerHex = "#134747"
        val onSecondaryContainerHex = "#C6FBEA"
        val tertiaryHex = "#38BDF8"
        val onTertiaryHex = "#04253B"
        val tertiaryContainerHex = "#194469"
        val onTertiaryContainerHex = "#D8F2FF"

        val surfaceHex = "#162A52"
        val surfaceContainerLowestHex = "#102140"
        val surfaceContainerLowHex = "#162C50"
        val surfaceContainerHex = "#1B355E"
        val surfaceContainerHighHex = "#224170"
        val surfaceContainerHighestHex = "#2A4E82"
        val onSurfaceHex = "#F0F8FF"
        val onSurfaceVariantHex = "#B4D3EB"
        val outlineHex = "#5AA9D9"
        val outlineVariantHex = "#336796"

        return MaterialYouPalette(
            seed = primaryHex,
            primaryHex = primaryHex,
            onPrimaryHex = onPrimaryHex,
            primaryContainerHex = primaryContainerHex,
            onPrimaryContainerHex = onPrimaryContainerHex,
            secondaryHex = secondaryHex,
            onSecondaryHex = onSecondaryHex,
            secondaryContainerHex = secondaryContainerHex,
            onSecondaryContainerHex = onSecondaryContainerHex,
            tertiaryHex = tertiaryHex,
            onTertiaryHex = onTertiaryHex,
            tertiaryContainerHex = tertiaryContainerHex,
            onTertiaryContainerHex = onTertiaryContainerHex,
            surfaceHex = surfaceHex,
            surfaceContainerLowestHex = surfaceContainerLowestHex,
            surfaceContainerLowHex = surfaceContainerLowHex,
            surfaceContainerHex = surfaceContainerHex,
            surfaceContainerHighHex = surfaceContainerHighHex,
            surfaceContainerHighestHex = surfaceContainerHighestHex,
            onSurfaceHex = onSurfaceHex,
            onSurfaceVariantHex = onSurfaceVariantHex,
            outlineHex = outlineHex,
            outlineVariantHex = outlineVariantHex,
            primary = parseColor(primaryHex),
            onPrimary = parseColor(onPrimaryHex),
            primaryContainer = parseColor(primaryContainerHex),
            onPrimaryContainer = parseColor(onPrimaryContainerHex),
            secondary = parseColor(secondaryHex),
            onSecondary = parseColor(onSecondaryHex),
            secondaryContainer = parseColor(secondaryContainerHex),
            onSecondaryContainer = parseColor(onSecondaryContainerHex),
            tertiary = parseColor(tertiaryHex),
            onTertiary = parseColor(onTertiaryHex),
            tertiaryContainer = parseColor(tertiaryContainerHex),
            onTertiaryContainer = parseColor(onTertiaryContainerHex),
            surface = parseColor(surfaceHex),
            surfaceContainerLowest = parseColor(surfaceContainerLowestHex),
            surfaceContainerLow = parseColor(surfaceContainerLowHex),
            surfaceContainer = parseColor(surfaceContainerHex),
            surfaceContainerHigh = parseColor(surfaceContainerHighHex),
            surfaceContainerHighest = parseColor(surfaceContainerHighestHex),
            onSurface = parseColor(onSurfaceHex),
            onSurfaceVariant = parseColor(onSurfaceVariantHex),
            outline = parseColor(outlineHex),
            outlineVariant = parseColor(outlineVariantHex)
        )
    }

    /**
     * Quiet-Luxury Dark Obsidian XMaterial You 3-phase system:
     * Deep volcanic neutral obsidian surfaces (#060608 / #0E0F13) paired with refined mineral & precious-metal phase accents:
     * - MORNING ("Fajar Emas / Obsidian Dawn"): Brushed Champagne Gold (#DFBE84) & Warm Ivory.
     * - AFTERNOON ("Siang Platinum / Obsidian Day"): Smoked Glacier Platinum-Jade (#8CC5C2) & Mineral Mist.
     * - NIGHT ("Malam Moonstone / Obsidian Night"): Muted Moonstone Amethyst (#B0A4E3) & Lunar Silver.
     */
    private fun buildXMaterialYouPhasePalette(
        phase: com.fathan.ironpulse.model.DayTimePhase
    ): MaterialYouPalette = when (phase) {
        com.fathan.ironpulse.model.DayTimePhase.MORNING -> buildFixedPalette(
            seed = XMATERIAL_MORNING_ACCENT,
            primaryHex = XMATERIAL_MORNING_ACCENT,
            onPrimaryHex = "#1B1406",
            primaryContainerHex = "#241D12",
            onPrimaryContainerHex = "#F3E0BE",
            secondaryHex = "#C79F62",
            onSecondaryHex = "#1A1205",
            secondaryContainerHex = "#1D1811",
            onSecondaryContainerHex = "#E9D3AF",
            tertiaryHex = "#E9D3AF",
            onTertiaryHex = "#1B150A",
            tertiaryContainerHex = "#1A1713",
            onTertiaryContainerHex = "#F5ECDC",
            surfaceHex = "#060608",
            surfaceContainerLowestHex = "#040406",
            surfaceContainerLowHex = "#090A0D",
            surfaceContainerHex = "#0E0F13",
            surfaceContainerHighHex = "#15171E",
            surfaceContainerHighestHex = "#1D2029",
            onSurfaceHex = "#F5F3EF",
            onSurfaceVariantHex = "#9D9A93",
            outlineHex = "#DFBE84",
            outlineVariantHex = "#25262E"
        )
        com.fathan.ironpulse.model.DayTimePhase.AFTERNOON -> buildFixedPalette(
            seed = XMATERIAL_AFTERNOON_ACCENT,
            primaryHex = XMATERIAL_AFTERNOON_ACCENT,
            onPrimaryHex = "#071A1A",
            primaryContainerHex = "#112121",
            onPrimaryContainerHex = "#D8F0EE",
            secondaryHex = "#70ABA8",
            onSecondaryHex = "#051716",
            secondaryContainerHex = "#101B1B",
            onSecondaryContainerHex = "#C6E6E4",
            tertiaryHex = "#A6C4CC",
            onTertiaryHex = "#09171B",
            tertiaryContainerHex = "#121A1D",
            onTertiaryContainerHex = "#E0EEF2",
            surfaceHex = "#060608",
            surfaceContainerLowestHex = "#040406",
            surfaceContainerLowHex = "#090A0D",
            surfaceContainerHex = "#0E0F13",
            surfaceContainerHighHex = "#15171E",
            surfaceContainerHighestHex = "#1D2029",
            onSurfaceHex = "#F4F5F5",
            onSurfaceVariantHex = "#9A9FA1",
            outlineHex = "#8CC5C2",
            outlineVariantHex = "#23262B"
        )
        com.fathan.ironpulse.model.DayTimePhase.NIGHT -> buildFixedPalette(
            seed = XMATERIAL_NIGHT_ACCENT,
            primaryHex = XMATERIAL_NIGHT_ACCENT,
            onPrimaryHex = "#141028",
            primaryContainerHex = "#19172B",
            onPrimaryContainerHex = "#EAE5FF",
            secondaryHex = "#8D82C4",
            onSecondaryHex = "#110E22",
            secondaryContainerHex = "#151422",
            onSecondaryContainerHex = "#DDD6FA",
            tertiaryHex = "#A9B8E6",
            onTertiaryHex = "#0E1326",
            tertiaryContainerHex = "#141724",
            onTertiaryContainerHex = "#E4EAFA",
            surfaceHex = "#060608",
            surfaceContainerLowestHex = "#040406",
            surfaceContainerLowHex = "#090A0D",
            surfaceContainerHex = "#0E0F13",
            surfaceContainerHighHex = "#15171E",
            surfaceContainerHighestHex = "#1D2029",
            onSurfaceHex = "#F4F3F7",
            onSurfaceVariantHex = "#9B9AA5",
            outlineHex = "#B0A4E3",
            outlineVariantHex = "#242530"
        )
    }

    private fun buildTerminalCliPalette(): MaterialYouPalette = buildFixedPalette(
        seed = CLI_PHOSPHOR_ACCENT,
        primaryHex = CLI_PHOSPHOR_ACCENT,
        onPrimaryHex = "#031A0A",
        primaryContainerHex = "#0E2917",
        onPrimaryContainerHex = "#9BFFBE",
        secondaryHex = "#4ADE80",
        onSecondaryHex = "#041C0B",
        secondaryContainerHex = "#0B2113",
        onSecondaryContainerHex = "#7FFFA9",
        tertiaryHex = "#FFB000",
        onTertiaryHex = "#1E1400",
        tertiaryContainerHex = "#241B06",
        onTertiaryContainerHex = "#FFE08A",
        surfaceHex = "#050806",
        surfaceContainerLowestHex = "#030504",
        surfaceContainerLowHex = "#070C09",
        surfaceContainerHex = "#0A100C",
        surfaceContainerHighHex = "#0F1812",
        surfaceContainerHighestHex = "#152119",
        onSurfaceHex = "#E6F9EC",
        onSurfaceVariantHex = "#7CA88B",
        outlineHex = "#00FF66",
        outlineVariantHex = "#1D3B27"
    )

    private fun buildFixedPalette(
        seed: String,
        primaryHex: String,
        onPrimaryHex: String,
        primaryContainerHex: String,
        onPrimaryContainerHex: String,
        secondaryHex: String,
        onSecondaryHex: String,
        secondaryContainerHex: String,
        onSecondaryContainerHex: String,
        tertiaryHex: String,
        onTertiaryHex: String,
        tertiaryContainerHex: String,
        onTertiaryContainerHex: String,
        surfaceHex: String,
        surfaceContainerLowestHex: String,
        surfaceContainerLowHex: String,
        surfaceContainerHex: String,
        surfaceContainerHighHex: String,
        surfaceContainerHighestHex: String,
        onSurfaceHex: String,
        onSurfaceVariantHex: String,
        outlineHex: String,
        outlineVariantHex: String
    ): MaterialYouPalette = MaterialYouPalette(
        seed = seed,
        primaryHex = primaryHex,
        onPrimaryHex = onPrimaryHex,
        primaryContainerHex = primaryContainerHex,
        onPrimaryContainerHex = onPrimaryContainerHex,
        secondaryHex = secondaryHex,
        onSecondaryHex = onSecondaryHex,
        secondaryContainerHex = secondaryContainerHex,
        onSecondaryContainerHex = onSecondaryContainerHex,
        tertiaryHex = tertiaryHex,
        onTertiaryHex = onTertiaryHex,
        tertiaryContainerHex = tertiaryContainerHex,
        onTertiaryContainerHex = onTertiaryContainerHex,
        surfaceHex = surfaceHex,
        surfaceContainerLowestHex = surfaceContainerLowestHex,
        surfaceContainerLowHex = surfaceContainerLowHex,
        surfaceContainerHex = surfaceContainerHex,
        surfaceContainerHighHex = surfaceContainerHighHex,
        surfaceContainerHighestHex = surfaceContainerHighestHex,
        onSurfaceHex = onSurfaceHex,
        onSurfaceVariantHex = onSurfaceVariantHex,
        outlineHex = outlineHex,
        outlineVariantHex = outlineVariantHex,
        primary = parseColor(primaryHex),
        onPrimary = parseColor(onPrimaryHex),
        primaryContainer = parseColor(primaryContainerHex),
        onPrimaryContainer = parseColor(onPrimaryContainerHex),
        secondary = parseColor(secondaryHex),
        onSecondary = parseColor(onSecondaryHex),
        secondaryContainer = parseColor(secondaryContainerHex),
        onSecondaryContainer = parseColor(onSecondaryContainerHex),
        tertiary = parseColor(tertiaryHex),
        onTertiary = parseColor(onTertiaryHex),
        tertiaryContainer = parseColor(tertiaryContainerHex),
        onTertiaryContainer = parseColor(onTertiaryContainerHex),
        surface = parseColor(surfaceHex),
        surfaceContainerLowest = parseColor(surfaceContainerLowestHex),
        surfaceContainerLow = parseColor(surfaceContainerLowHex),
        surfaceContainer = parseColor(surfaceContainerHex),
        surfaceContainerHigh = parseColor(surfaceContainerHighHex),
        surfaceContainerHighest = parseColor(surfaceContainerHighestHex),
        onSurface = parseColor(onSurfaceHex),
        onSurfaceVariant = parseColor(onSurfaceVariantHex),
        outline = parseColor(outlineHex),
        outlineVariant = parseColor(outlineVariantHex)
    )
}


