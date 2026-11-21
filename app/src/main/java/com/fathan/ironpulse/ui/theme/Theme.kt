package com.fathan.ironpulse.ui.theme

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.data.AppPreferences
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.PerformancePreferences
import com.fathan.ironpulse.model.UiTheme
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Immutable
data class PhaseThemeTokens(
    val phase: DayTimePhase,
    val accentHex: String,
    val accentBrightHex: String,
    val accent: Color,
    val accentBright: Color,
    val heroStart: Color,
    val heroMid: Color,
    val heroEnd: Color,
    val heroBorder: Color,
    val headerBorder: Color,
    val glowPrimary: Color,
    val glowSecondary: Color
)

fun resolvePhaseThemeTokens(phase: DayTimePhase): PhaseThemeTokens = when (phase) {
    DayTimePhase.MORNING -> PhaseThemeTokens(
        phase = phase,
        accentHex = "#F59E0B",
        accentBrightHex = "#FBBF24",
        accent = Color(0xFFF59E0B),
        accentBright = Color(0xFFFBBF24),
        heroStart = Color(0xCC2A1B08),
        heroMid = Color(0xB8181410),
        heroEnd = Color(0xD612151C),
        heroBorder = Color(0x52FBBF24),
        headerBorder = Color(0x38F59E0B),
        glowPrimary = Color(0x28FBBF24),
        glowSecondary = Color(0x1AEAB308)
    )
    DayTimePhase.AFTERNOON -> PhaseThemeTokens(
        phase = phase,
        accentHex = "#38BDF8",
        accentBrightHex = "#7DD3FC",
        accent = Color(0xFF38BDF8),
        accentBright = Color(0xFF7DD3FC),
        heroStart = Color(0xCC0D2337),
        heroMid = Color(0xB8121B28),
        heroEnd = Color(0xD612151C),
        heroBorder = Color(0x477DD3FC),
        headerBorder = Color(0x3338BDF8),
        glowPrimary = Color(0x2438BDF8),
        glowSecondary = Color(0x160EA5E9)
    )
    DayTimePhase.NIGHT -> PhaseThemeTokens(
        phase = phase,
        accentHex = "#818CF8",
        accentBrightHex = "#A5B4FC",
        accent = Color(0xFF818CF8),
        accentBright = Color(0xFFA5B4FC),
        heroStart = Color(0xD6161432),
        heroMid = Color(0xC2121321),
        heroEnd = Color(0xDE0E1017),
        heroBorder = Color(0x47A5B4FC),
        headerBorder = Color(0x33818CF8),
        glowPrimary = Color(0x26818CF8),
        glowSecondary = Color(0x186366F1)
    )
}

@Immutable
data class IronPulseThemeTokens(
    val uiTheme: UiTheme,
    val palette: MaterialYouPalette,
    val accentColor: Color,
    val onAccentColor: Color,
    val effectiveTintColor: Color,
    val canvasBg: Color,
    val canvasSecondaryBg: Color,
    val wellBg: Color,
    val controlBg: Color,
    val subtleChipBg: Color,
    val mutedTextColor: Color,
    val cardTopHighlight: Color,
    val surfaceBorderColor: Color,
    val subtleBorderColor: Color,
    val cardRadius: Dp,
    val buttonRadius: Dp,
    val inputRadius: Dp,
    val bubbleRadius: Dp,
    val heroScrimAlpha: Float,
    val buttonHasGloss: Boolean,
    val timePhase: DayTimePhase,
    val phaseTokens: PhaseThemeTokens,
    val performance: PerformancePreferences,
    val lang: AppLanguage,
    val clockFormat: ClockFormat
)

private val DefaultPalette = MaterialYouColorEngine.generateMaterialYouPalette(
    MaterialYouColorEngine.DEFAULT_ACCENT_COLOR,
    null
)

val LocalIronPulseTokens = staticCompositionLocalOf {
    IronPulseThemeTokens(
        uiTheme = UiTheme.MATERIAL_YOU,
        palette = DefaultPalette,
        accentColor = DefaultPalette.primary,
        onAccentColor = DefaultPalette.onPrimary,
        effectiveTintColor = MaterialYouColorEngine.parseColor("#1C1B22"),
        canvasBg = DefaultPalette.surface,
        canvasSecondaryBg = DefaultPalette.surfaceContainerLowest,
        wellBg = DefaultPalette.surfaceContainerLowest,
        controlBg = DefaultPalette.surfaceContainerHigh,
        subtleChipBg = DefaultPalette.surfaceContainerHigh,
        mutedTextColor = DefaultPalette.onSurfaceVariant,
        cardTopHighlight = Color.White.copy(alpha = 0.06f),
        surfaceBorderColor = DefaultPalette.outlineVariant,
        subtleBorderColor = DefaultPalette.outlineVariant.copy(alpha = 0.7f),
        cardRadius = 24.dp,
        buttonRadius = 999.dp,
        inputRadius = 14.dp,
        bubbleRadius = 20.dp,
        heroScrimAlpha = 0.68f,
        buttonHasGloss = false,
        timePhase = DayTimePhase.MORNING,
        phaseTokens = resolvePhaseThemeTokens(DayTimePhase.MORNING),
        performance = PerformancePreferences(),
        lang = AppLanguage.ID,
        clockFormat = ClockFormat.H24
    )
}

val LocalBackdropHazeState = staticCompositionLocalOf<HazeState?> { null }
val LocalChromeHazeState = staticCompositionLocalOf<HazeState?> { null }
val LocalBlurEnabled = compositionLocalOf { true }
val LocalTabActive = compositionLocalOf { true }

/**
 * Hardware-accelerated repeating GPU shader textures cached once per density/mode key.
 * Zero per-card CPU Path loops: Carbon 2x2 twill and Ubuntu Yaru warm matte stipple
 * render in a single GPU pass at 120 FPS.
 */
object ThemeMaterialShaderCache {
    private val carbonCache = ConcurrentHashMap<Long, ShaderBrush>()
    private val yaruCache = ConcurrentHashMap<Long, ShaderBrush>()

    fun getCarbonTwillBrush(cellPx: Int, subtle: Boolean = false): ShaderBrush {
        val safeCell = cellPx.coerceIn(3, 12)
        val key = (safeCell.toLong() shl 1) or (if (subtle) 1L else 0L)
        return carbonCache[key] ?: synchronized(carbonCache) {
            carbonCache[key] ?: buildCarbonTwillBrush(safeCell, subtle).also { carbonCache[key] = it }
        }
    }

    fun getYaruMatteGrainBrush(pixelScale: Int = 2, subtle: Boolean = false): ShaderBrush {
        val safeScale = pixelScale.coerceIn(1, 4)
        val key = (safeScale.toLong() shl 1) or (if (subtle) 1L else 0L)
        return yaruCache[key] ?: synchronized(yaruCache) {
            yaruCache[key] ?: buildYaruMatteGrainBrush(safeScale, subtle).also { yaruCache[key] = it }
        }
    }

    private fun buildCarbonTwillBrush(cell: Int, subtle: Boolean): ShaderBrush {
        val tileSize = cell * 4
        val bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        val hiPaint = Paint().apply {
            color = android.graphics.Color.argb(if (subtle) 12 else 18, 255, 255, 255)
            isAntiAlias = false
        }
        val midPaint = Paint().apply {
            color = android.graphics.Color.argb(if (subtle) 6 else 10, 210, 224, 244)
            isAntiAlias = false
        }
        val shPaint = Paint().apply {
            color = android.graphics.Color.argb(if (subtle) 58 else 78, 0, 0, 0)
            isAntiAlias = false
        }

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val phase = (col - row + 4) % 4
                val left = col * cell.toFloat()
                val top = row * cell.toFloat()
                val right = left + cell
                val bottom = top + cell
                when (phase) {
                    0 -> canvas.drawRect(left, top, right, bottom, hiPaint)
                    1 -> canvas.drawRect(left, top, right, bottom, midPaint)
                    2 -> canvas.drawRect(left, top, right, bottom, shPaint)
                }
            }
        }
        val imageShader = ImageShader(bitmap.asImageBitmap(), TileMode.Repeated, TileMode.Repeated)
        return ShaderBrush(imageShader)
    }

    private fun buildYaruMatteGrainBrush(scale: Int, subtle: Boolean): ShaderBrush {
        val grid = 20
        val tileSize = grid * scale
        val bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        val paint = Paint().apply { isAntiAlias = false }

        for (y in 0 until grid) {
            for (x in 0 until grid) {
                val h = ((x * 374761393) xor (y * 668265263)) and 0xFF
                if (h > 210) {
                    paint.color = android.graphics.Color.argb(if (subtle) 7 else 11, 255, 245, 238)
                } else if (h < 42) {
                    paint.color = android.graphics.Color.argb(if (subtle) 9 else 14, 10, 8, 8)
                } else {
                    continue
                }
                canvas.drawRect(
                    (x * scale).toFloat(),
                    (y * scale).toFloat(),
                    ((x + 1) * scale).toFloat(),
                    ((y + 1) * scale).toFloat(),
                    paint
                )
            }
        }
        val imageShader = ImageShader(bitmap.asImageBitmap(), TileMode.Repeated, TileMode.Repeated)
        return ShaderBrush(imageShader)
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
private fun Modifier.applyBackdropHaze(
    targetHazeState: HazeState?,
    tint: Color,
    permNoBlur: Boolean,
    blurEnabledNow: Boolean,
    blurRadius: Dp,
    noiseFactor: Float,
    fallbackAlpha: Float
): Modifier {
    if (permNoBlur || targetHazeState == null) return this
    val style = remember(tint, blurRadius, noiseFactor, fallbackAlpha) {
        HazeStyle(
            backgroundColor = Color.Transparent,
            tints = emptyList(),
            blurRadius = blurRadius,
            noiseFactor = noiseFactor,
            fallbackTint = HazeTint(tint.copy(alpha = fallbackAlpha))
        )
    }
    return this.hazeEffect(state = targetHazeState, style = style) {
        blurEnabled = blurEnabledNow
        inputScale = HazeInputScale.Auto
    }
}

@Composable
fun Modifier.ironGlassSurface(
    tokens: IronPulseThemeTokens,
    subtle: Boolean = false,
    borderOverride: Color? = null,
    radiusOverride: Dp? = null,
    blurOverChrome: Boolean = false,
    hazeStateOverride: HazeState? = null
): Modifier {
    val radius = radiusOverride ?: tokens.cardRadius
    val shape = remember(radius) { RoundedCornerShape(radius) }
    val tint = tokens.effectiveTintColor
    val borderColor = borderOverride ?: if (subtle) tokens.subtleBorderColor else tokens.surfaceBorderColor
    val globalBlurAllowed = LocalBlurEnabled.current
    val permNoBlur = tokens.performance.noBlur
    val noBlur = permNoBlur || !globalBlurAllowed
    val targetHazeState = hazeStateOverride ?: if (blurOverChrome) {
        LocalChromeHazeState.current ?: LocalBackdropHazeState.current
    } else {
        LocalBackdropHazeState.current
    }
    val useChromeLiveBlur = blurOverChrome && !permNoBlur && targetHazeState != null

    return when (tokens.uiTheme) {
        UiTheme.MATERIAL_YOU -> {
            val baseBg = if (subtle) tokens.palette.surfaceContainerLow else tokens.palette.surfaceContainer
            val useChromeBlur = blurOverChrome && !noBlur && targetHazeState != null
            val bg = if (useChromeBlur) baseBg.copy(alpha = 0.78f) else baseBg
            this
                .clip(shape)
                .then(
                    if (useChromeLiveBlur) {
                        Modifier.applyBackdropHaze(
                            targetHazeState = targetHazeState,
                            tint = tint,
                            permNoBlur = false,
                            blurEnabledNow = globalBlurAllowed,
                            blurRadius = 16.dp,
                            noiseFactor = 0f,
                            fallbackAlpha = 0.35f
                        )
                    } else {
                        Modifier
                    }
                )
                .background(bg, shape)
                .border(1.dp, borderColor, shape)
        }
        UiTheme.UBUNTU_YARU -> {
            val bg = if (subtle) Color(0xFF232323) else Color(0xFF2C2C2C)
            val outerBorder = borderOverride ?: Color(0xFF141414)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val grainScale = density.roundToInt().coerceIn(1, 3)
                    val matteGrain = ThemeMaterialShaderCache.getYaruMatteGrainBrush(
                        pixelScale = grainScale,
                        subtle = subtle
                    )
                    val topBevel = Color.White.copy(alpha = if (subtle) 0.05f else 0.09f)
                    val bottomShade = Color.Black.copy(alpha = 0.22f)
                    onDrawBehind {
                        drawRect(bg)
                        drawRect(matteGrain)
                        drawLine(
                            color = topBevel,
                            start = Offset(0f, strokePx * 1.2f),
                            end = Offset(size.width, strokePx * 1.2f),
                            strokeWidth = strokePx
                        )
                        drawLine(
                            color = bottomShade,
                            start = Offset(0f, size.height - strokePx * 0.5f),
                            end = Offset(size.width, size.height - strokePx * 0.5f),
                            strokeWidth = strokePx
                        )
                    }
                }
                .border(1.dp, outerBorder, shape)
        }
        UiTheme.CARBON_FORGE -> {
            val baseBg = tint.copy(alpha = if (subtle) 0.95f else 0.99f)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val cellPx = (2.2.dp.toPx()).roundToInt().coerceIn(4, 10)
                    val twillBrush = ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = cellPx, subtle = subtle)
                    val sheenBrush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = if (subtle) 0.03f else 0.065f),
                        0.36f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = if (subtle) 0.16f else 0.28f)
                    )
                    val rimColor = Color.White.copy(alpha = if (subtle) 0.10f else 0.18f)
                    val recessedBottom = Color.Black.copy(alpha = 0.42f)
                    onDrawBehind {
                        drawRect(baseBg)
                        drawRect(twillBrush)
                        drawRect(sheenBrush)
                        drawLine(
                            color = rimColor,
                            start = Offset(0f, strokePx * 0.5f),
                            end = Offset(size.width, strokePx * 0.5f),
                            strokeWidth = strokePx
                        )
                        drawLine(
                            color = recessedBottom,
                            start = Offset(0f, size.height - strokePx * 0.5f),
                            end = Offset(size.width, size.height - strokePx * 0.5f),
                            strokeWidth = strokePx
                        )
                    }
                }
                .border(1.dp, borderColor, shape)
        }
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
fun Modifier.ironGlassBar(
    tokens: IronPulseThemeTokens,
    shapeRadius: Dp = 0.dp,
    topHairlineColor: Color? = null,
    bottomHairlineColor: Color? = null,
    hazeStateOverride: HazeState? = null
): Modifier {
    val shape = remember(shapeRadius) { RoundedCornerShape(shapeRadius) }
    val tint = tokens.effectiveTintColor
    val globalBlurAllowed = LocalBlurEnabled.current
    val permNoBlur = tokens.performance.noBlur
    val noBlur = permNoBlur || !globalBlurAllowed
    val chromeHazeState = hazeStateOverride ?: LocalChromeHazeState.current
    val supportsBarBlur = tokens.uiTheme == UiTheme.MATERIAL_YOU
    val canGpuBlur = !noBlur && !permNoBlur && supportsBarBlur && chromeHazeState != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val barHazeModifier = if (!permNoBlur && supportsBarBlur && chromeHazeState != null) {
        val barHazeStyle = remember(tokens.uiTheme, tint) {
            HazeStyle(
                backgroundColor = Color.Transparent,
                tints = emptyList(),
                blurRadius = 16.dp,
                noiseFactor = 0f,
                fallbackTint = HazeTint(tint.copy(alpha = 0.30f))
            )
        }
        Modifier.hazeEffect(
            state = chromeHazeState,
            style = barHazeStyle
        ) {
            blurEnabled = globalBlurAllowed
            inputScale = HazeInputScale.Auto
        }
    } else {
        Modifier
    }

    return this
        .clip(shape)
        .then(barHazeModifier)
        .drawWithCache {
            val strokePx = 1.dp.toPx()
            val cellPx = (2.2.dp.toPx()).roundToInt().coerceIn(4, 10)
            val grainScale = density.roundToInt().coerceIn(1, 3)
            val primaryBrush = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> null
                UiTheme.UBUNTU_YARU -> ThemeMaterialShaderCache.getYaruMatteGrainBrush(grainScale, subtle = true)
                UiTheme.CARBON_FORGE -> ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = cellPx, subtle = true)
            }
            val solidBg = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> tokens.palette.surfaceContainerLow.copy(
                    alpha = if (noBlur) 1f else if (canGpuBlur) 0.72f else 0.94f
                )
                UiTheme.UBUNTU_YARU -> Color(0xFF242424)
                UiTheme.CARBON_FORGE -> tint.copy(alpha = 0.98f)
            }
            onDrawBehind {
                drawRect(solidBg)
                if (primaryBrush != null) {
                    drawRect(primaryBrush)
                }
                if (topHairlineColor != null) {
                    drawLine(
                        color = topHairlineColor,
                        start = Offset(0f, strokePx * 0.5f),
                        end = Offset(size.width, strokePx * 0.5f),
                        strokeWidth = strokePx
                    )
                }
                if (bottomHairlineColor != null) {
                    drawLine(
                        color = bottomHairlineColor,
                        start = Offset(0f, size.height - strokePx * 0.5f),
                        end = Offset(size.width, size.height - strokePx * 0.5f),
                        strokeWidth = strokePx
                    )
                }
            }
        }
}

private val IronPulseTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        fontFeatureSettings = "tnum"
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 25.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFeatureSettings = "tnum"
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontFeatureSettings = "tnum"
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontFeatureSettings = "tnum"
    )
)

@Composable
fun IronPulseTheme(
    prefs: AppPreferences = AppPreferences(),
    timePhase: DayTimePhase = DayTimePhase.MORNING,
    content: @Composable () -> Unit
) {
    val uiTheme = prefs.uiTheme
    val effectiveAccentHex = if (uiTheme == UiTheme.UBUNTU_YARU &&
        prefs.accentColorHex.equals(MaterialYouColorEngine.DEFAULT_ACCENT_COLOR, ignoreCase = true)
    ) {
        MaterialYouColorEngine.YARU_ORANGE_ACCENT
    } else {
        prefs.accentColorHex
    }

    val effectiveTintHex = prefs.materialTintColorHex
        ?: MaterialYouColorEngine.defaultTintForTheme(uiTheme)

    val palette = remember(effectiveAccentHex, prefs.materialTintColorHex, uiTheme) {
        MaterialYouColorEngine.generateMaterialYouPalette(
            seedHex = effectiveAccentHex,
            surfaceTintOverrideHex = prefs.materialTintColorHex
        )
    }

    val phaseTokens = remember(timePhase) { resolvePhaseThemeTokens(timePhase) }

    val tokens = remember(uiTheme, palette, effectiveTintHex, timePhase, phaseTokens, prefs) {
        val canvasBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surface
            UiTheme.UBUNTU_YARU -> Color(0xFF1C1C1C)
            UiTheme.CARBON_FORGE -> Color(0xFF060709)
        }
        val canvasSecondaryBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF261722) // subtle Ubuntu aubergine warmth at top
            UiTheme.CARBON_FORGE -> Color(0xFF0B0D11)
        }
        val borderColor = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.18f)
            UiTheme.UBUNTU_YARU -> Color(0xFF141414)
        }
        val subtleBorder = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant.copy(alpha = 0.65f)
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.12f)
            UiTheme.UBUNTU_YARU -> Color(0xFF181818)
        }
        val cardRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 24.dp
            UiTheme.CARBON_FORGE -> 10.dp
            UiTheme.UBUNTU_YARU -> 12.dp
        }
        val buttonRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 999.dp
            UiTheme.CARBON_FORGE -> 8.dp
            UiTheme.UBUNTU_YARU -> 10.dp
        }
        val inputRadius = when (uiTheme) {
            UiTheme.CARBON_FORGE -> 8.dp
            UiTheme.UBUNTU_YARU -> 10.dp
            UiTheme.MATERIAL_YOU -> 12.dp
        }
        val bubbleRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 20.dp
            UiTheme.CARBON_FORGE -> 10.dp
            UiTheme.UBUNTU_YARU -> 12.dp
        }
        val heroScrimAlpha = when (uiTheme) {
            UiTheme.CARBON_FORGE -> 0.42f
            UiTheme.UBUNTU_YARU -> 0.52f
            UiTheme.MATERIAL_YOU -> 0.62f
        }
        val wellBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF212121)
            UiTheme.CARBON_FORGE -> Color(0xFF090B0E)
        }
        val controlBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh
            UiTheme.UBUNTU_YARU -> Color(0xFF353535)
            UiTheme.CARBON_FORGE -> Color(0xFF161A22)
        }
        val subtleChipBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh.copy(alpha = 0.75f)
            UiTheme.UBUNTU_YARU -> Color(0xFF303030)
            UiTheme.CARBON_FORGE -> Color(0xFF131720)
        }
        val mutedTextColor = when (uiTheme) {
            UiTheme.UBUNTU_YARU -> Color(0xFFB5B4B0)
            else -> palette.onSurfaceVariant
        }
        val cardTopHighlight = when (uiTheme) {
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.18f)
            UiTheme.UBUNTU_YARU -> Color.White.copy(alpha = 0.08f)
            UiTheme.MATERIAL_YOU -> Color.White.copy(alpha = 0.03f)
        }
        IronPulseThemeTokens(
            uiTheme = uiTheme,
            palette = palette,
            accentColor = palette.primary,
            onAccentColor = palette.onPrimary,
            effectiveTintColor = MaterialYouColorEngine.parseColor(effectiveTintHex, Color(0xFF1C1B22)),
            canvasBg = canvasBg,
            canvasSecondaryBg = canvasSecondaryBg,
            wellBg = wellBg,
            controlBg = controlBg,
            subtleChipBg = subtleChipBg,
            mutedTextColor = mutedTextColor,
            cardTopHighlight = cardTopHighlight,
            surfaceBorderColor = borderColor,
            subtleBorderColor = subtleBorder,
            cardRadius = cardRadius,
            buttonRadius = buttonRadius,
            inputRadius = inputRadius,
            bubbleRadius = bubbleRadius,
            heroScrimAlpha = heroScrimAlpha,
            buttonHasGloss = false,
            timePhase = timePhase,
            phaseTokens = phaseTokens,
            performance = prefs.performance,
            lang = prefs.lang,
            clockFormat = prefs.clockFormat
        )
    }

    val colorScheme = remember(tokens) {
        darkColorScheme(
            primary = tokens.palette.primary,
            onPrimary = tokens.palette.onPrimary,
            primaryContainer = tokens.palette.primaryContainer,
            onPrimaryContainer = tokens.palette.onPrimaryContainer,
            secondary = tokens.palette.secondary,
            onSecondary = tokens.palette.onSecondary,
            secondaryContainer = tokens.palette.secondaryContainer,
            onSecondaryContainer = tokens.palette.onSecondaryContainer,
            tertiary = tokens.palette.tertiary,
            onTertiary = tokens.palette.onTertiary,
            tertiaryContainer = tokens.palette.tertiaryContainer,
            onTertiaryContainer = tokens.palette.onTertiaryContainer,
            background = tokens.canvasBg,
            onBackground = tokens.palette.onSurface,
            surface = tokens.canvasBg,
            onSurface = tokens.palette.onSurface,
            surfaceVariant = tokens.palette.surfaceContainerHigh,
            onSurfaceVariant = tokens.palette.onSurfaceVariant,
            surfaceContainerLowest = tokens.palette.surfaceContainerLowest,
            surfaceContainerLow = tokens.palette.surfaceContainerLow,
            surfaceContainer = tokens.palette.surfaceContainer,
            surfaceContainerHigh = tokens.palette.surfaceContainerHigh,
            surfaceContainerHighest = tokens.palette.surfaceContainerHighest,
            outline = tokens.palette.outline,
            outlineVariant = tokens.palette.outlineVariant,
            error = Color(0xFFF87171),
            onError = Color(0xFF2F0808)
        )
    }

    CompositionLocalProvider(LocalIronPulseTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = IronPulseTypography,
            content = content
        )
    }
}
