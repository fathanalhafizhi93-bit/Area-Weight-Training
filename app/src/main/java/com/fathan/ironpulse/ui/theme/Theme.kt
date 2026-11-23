package com.fathan.ironpulse.ui.theme

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

fun resolvePhaseThemeTokens(phase: DayTimePhase, isLight: Boolean = false): PhaseThemeTokens = when (phase) {
    DayTimePhase.MORNING -> if (isLight) PhaseThemeTokens(
        phase = phase,
        accentHex = "#B45309",
        accentBrightHex = "#B45309",
        accent = Color(0xFFB45309),
        accentBright = Color(0xFFC2410C),
        heroStart = Color(0xCCFFF9E6),
        heroMid = Color(0xCCE8F7FF),
        heroEnd = Color(0xD8EEFDF3),
        heroBorder = Color(0x99D97706),
        headerBorder = Color(0x700088CC),
        glowPrimary = Color(0x36FBBF24),
        glowSecondary = Color(0x2838BDF8)
    ) else PhaseThemeTokens(
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
    DayTimePhase.AFTERNOON -> if (isLight) PhaseThemeTokens(
        phase = phase,
        accentHex = "#0369A1",
        accentBrightHex = "#005F9E",
        accent = Color(0xFF0369A1),
        accentBright = Color(0xFF005F9E),
        heroStart = Color(0xD6E3F6FF),
        heroMid = Color(0xDCEFFAFF),
        heroEnd = Color(0xE0ECFCF2),
        heroBorder = Color(0xA60088CC),
        headerBorder = Color(0x700088CC),
        glowPrimary = Color(0x3438BDF8),
        glowSecondary = Color(0x284ADE80)
    ) else PhaseThemeTokens(
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
    DayTimePhase.NIGHT -> if (isLight) PhaseThemeTokens(
        phase = phase,
        accentHex = "#1D4ED8",
        accentBrightHex = "#1E40AF",
        accent = Color(0xFF1D4ED8),
        accentBright = Color(0xFF1E40AF),
        heroStart = Color(0xD6E2ECFF),
        heroMid = Color(0xDCEAF6FF),
        heroEnd = Color(0xE0EFF9FF),
        heroBorder = Color(0x992563EB),
        headerBorder = Color(0x700088CC),
        glowPrimary = Color(0x3060A5FA),
        glowSecondary = Color(0x2438BDF8)
    ) else PhaseThemeTokens(
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
    val accentTextColor: Color = accentColor,
    val dividerColor: Color = Color.White.copy(alpha = 0.09f),
    val successColor: Color = Color(0xFF4ADE80),
    val warningColor: Color = Color(0xFFFBBF24),
    val dangerColor: Color = Color(0xFFF87171),
    val infoColor: Color = Color(0xFF38BDF8),
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
    val isLightTheme: Boolean = false,
    val isAeroNight: Boolean = false,
    val accentLocked: Boolean = false,
    val fontPack: ThemeFontPack = resolveThemeFontPack(UiTheme.MATERIAL_YOU),
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
        isLightTheme = false,
        accentLocked = false,
        timePhase = DayTimePhase.MORNING,
        phaseTokens = resolvePhaseThemeTokens(DayTimePhase.MORNING),
        performance = PerformancePreferences(),
        lang = AppLanguage.ID,
        clockFormat = ClockFormat.H24
    )
}

val LocalTabActive = compositionLocalOf { true }

/**
 * Hardware-accelerated repeating GPU shader textures cached once per density/mode key.
 * Zero per-card CPU Path loops: Carbon 2x2 twill and Ubuntu Yaru warm matte stipple
 * render in a single GPU pass at 120 FPS.
 */
object ThemeMaterialShaderCache {
    private val carbonCache = ConcurrentHashMap<Long, ShaderBrush>()
    private val yaruCache = ConcurrentHashMap<Long, ShaderBrush>()
    private val stealthCache = ConcurrentHashMap<Long, ShaderBrush>()

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

    fun getAmoledStealthMatrixBrush(cellPx: Int = 6, subtle: Boolean = false): ShaderBrush {
        val safeCell = cellPx.coerceIn(4, 14)
        val key = (safeCell.toLong() shl 1) or (if (subtle) 1L else 0L)
        return stealthCache[key] ?: synchronized(stealthCache) {
            stealthCache[key] ?: buildAmoledStealthMatrixBrush(safeCell, subtle).also { stealthCache[key] = it }
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

    private fun buildAmoledStealthMatrixBrush(cell: Int, subtle: Boolean): ShaderBrush {
        val tileSize = cell * 2
        val bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        val dotSize = (cell / 4f).coerceAtLeast(1.2f)
        val dotPaint = Paint().apply {
            color = android.graphics.Color.argb(if (subtle) 10 else 16, 224, 242, 255)
            isAntiAlias = false
        }
        val faintDotPaint = Paint().apply {
            color = android.graphics.Color.argb(if (subtle) 5 else 9, 255, 255, 255)
            isAntiAlias = false
        }
        // Staggered micro-dot stealth matrix: crisp OLED feel, near-zero pixel luminance
        canvas.drawRect(0f, 0f, dotSize, dotSize, dotPaint)
        canvas.drawRect(cell.toFloat(), cell.toFloat(), cell + dotSize, cell + dotSize, faintDotPaint)
        val imageShader = ImageShader(bitmap.asImageBitmap(), TileMode.Repeated, TileMode.Repeated)
        return ShaderBrush(imageShader)
    }
}

@Composable
fun Modifier.ironGlassSurface(
    tokens: IronPulseThemeTokens,
    subtle: Boolean = false,
    borderOverride: Color? = null,
    radiusOverride: Dp? = null
): Modifier {
    val radius = radiusOverride ?: tokens.cardRadius
    val shape = remember(tokens.uiTheme, radius) {
        when (tokens.uiTheme) {
            UiTheme.CARBON_FORGE -> CutCornerShape(radius.coerceAtMost(8.dp))
            UiTheme.AMOLED_STEALTH -> RoundedCornerShape(radius.coerceAtMost(6.dp))
            else -> RoundedCornerShape(radius)
        }
    }
    val tint = tokens.effectiveTintColor
    val borderColor = borderOverride ?: if (subtle) tokens.subtleBorderColor else tokens.surfaceBorderColor

    return when (tokens.uiTheme) {
        UiTheme.MATERIAL_YOU -> {
            val baseBg = if (subtle) tokens.palette.surfaceContainerLow else tokens.palette.surfaceContainer
            this
                .clip(shape)
                .drawWithCache {
                    val topSheen = if (!subtle) {
                        Brush.verticalGradient(
                            0.0f to tokens.palette.primary.copy(alpha = 0.055f),
                            0.35f to Color.Transparent
                        )
                    } else null
                    onDrawBehind {
                        drawRect(baseBg)
                        if (topSheen != null) drawRect(topSheen)
                    }
                }
                .border(1.dp, borderColor, shape)
        }
        UiTheme.UBUNTU_YARU -> {
            // Authentic Ubuntu GNOME Libadwaita card — clean matte slate + warm orange top-left accent on primary cards
            val bg = if (subtle) Color(0xFF211E23) else Color(0xFF2B272D)
            val outerBorder = borderOverride ?: if (subtle) Color(0xFF171419) else Color(0xFF121014)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val headerSheen = if (!subtle) {
                        Brush.verticalGradient(
                            0.0f to Color(0xFF42273B).copy(alpha = 0.20f),
                            0.24f to Color.Transparent
                        )
                    } else null
                    val yaruStripe = Color(0xFFE95420).copy(alpha = 0.78f)
                    val stripeStart = 14.dp.toPx()
                    val stripeLen = (size.width * 0.14f).coerceIn(20.dp.toPx(), 48.dp.toPx())
                    onDrawBehind {
                        drawRect(bg)
                        if (headerSheen != null) drawRect(headerSheen)
                        if (!subtle && size.width > 120.dp.toPx()) {
                            drawLine(
                                color = yaruStripe,
                                start = Offset(stripeStart, strokePx * 1.1f),
                                end = Offset(stripeStart + stripeLen, strokePx * 1.1f),
                                strokeWidth = strokePx * 2.0f
                            )
                        }
                    }
                }
                .border(1.dp, outerBorder, shape)
        }
        UiTheme.CARBON_FORGE -> {
            // Industrial Machined Steel Plate — clean chamfer + left accent bar on primary cards (no rivets/heavy twill)
            val baseBg = tint.copy(alpha = if (subtle) 0.94f else 1f)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val sheenBrush = if (!subtle) {
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.06f),
                            0.32f to Color.Transparent,
                            1.0f to Color.Black.copy(alpha = 0.24f)
                        )
                    } else null
                    val rimColor = Color(0xFFE2E8F0).copy(alpha = if (subtle) 0.08f else 0.18f)
                    val accentBar = tokens.accentColor.copy(alpha = 0.85f)
                    onDrawBehind {
                        drawRect(baseBg)
                        if (sheenBrush != null) drawRect(sheenBrush)
                        drawLine(
                            color = rimColor,
                            start = Offset(0f, strokePx * 0.6f),
                            end = Offset(size.width, strokePx * 0.6f),
                            strokeWidth = strokePx
                        )
                        if (!subtle && size.height > 44.dp.toPx()) {
                            drawRect(
                                color = accentBar,
                                topLeft = Offset(0f, 10.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(2.dp.toPx(), (size.height - 20.dp.toPx()).coerceAtLeast(10f))
                            )
                        }
                    }
                }
                .border(1.dp, borderColor, shape)
        }
        UiTheme.AMOLED_STEALTH -> {
            // Pure Black Tactical HUD Module — crisp corner brackets on primary cards only, zero matrix clutter
            val baseBg = if (subtle) Color(0xFF030406) else Color(0xFF05070A)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val bracketStroke = 1.4.dp.toPx()
                    val arm = 6.dp.toPx()
                    val bracketColor = (borderOverride ?: tokens.accentColor).copy(alpha = 0.78f)
                    onDrawBehind {
                        drawRect(baseBg)
                        if (!subtle && size.width > 96.dp.toPx() && size.height > 40.dp.toPx()) {
                            val p = strokePx * 1.1f
                            drawLine(bracketColor, Offset(p, p), Offset(p + arm, p), bracketStroke)
                            drawLine(bracketColor, Offset(p, p), Offset(p, p + arm), bracketStroke)

                            drawLine(bracketColor, Offset(size.width - p, p), Offset(size.width - p - arm, p), bracketStroke)
                            drawLine(bracketColor, Offset(size.width - p, p), Offset(size.width - p, p + arm), bracketStroke)

                            drawLine(bracketColor, Offset(p, size.height - p), Offset(p + arm, size.height - p), bracketStroke)
                            drawLine(bracketColor, Offset(p, size.height - p), Offset(p, size.height - p - arm), bracketStroke)

                            drawLine(bracketColor, Offset(size.width - p, size.height - p), Offset(size.width - p - arm, size.height - p), bracketStroke)
                            drawLine(bracketColor, Offset(size.width - p, size.height - p), Offset(size.width - p, size.height - p - arm), bracketStroke)
                        }
                    }
                }
                .border(0.85.dp, borderColor.copy(alpha = if (subtle) 0.40f else 0.58f), shape)
        }
        UiTheme.FRUTIGER_AERO -> {
            if (tokens.isAeroNight) {
                // Soft Twilight Dark Aero Glass — clean semi-dark indigo crystal with gentle top sheen
                val twilightBorder = borderOverride ?: if (subtle) Color(0x5E5BC6FA) else Color(0x965BC6FA)
                this
                    .clip(shape)
                    .drawWithCache {
                        val strokePx = 1.1.dp.toPx()
                        val baseFill = Brush.verticalGradient(
                            0.0f to if (subtle) Color(0xEA1C3760) else Color(0xEE203F6D),
                            0.55f to if (subtle) Color(0xEA162E52) else Color(0xEE183259),
                            1.0f to if (subtle) Color(0xEB132B45) else Color(0xF012324B)
                        )
                        val topGlossBand = if (!subtle) {
                            Brush.verticalGradient(
                                0.0f to Color(0xFFE0F4FF).copy(alpha = 0.18f),
                                0.28f to Color.Transparent
                            )
                        } else null
                        val crystalTopLine = Color(0xFFBAE6FD).copy(alpha = if (subtle) 0.20f else 0.40f)
                        onDrawBehind {
                            drawRect(baseFill)
                            if (topGlossBand != null) drawRect(topGlossBand)
                            drawLine(
                                color = crystalTopLine,
                                start = Offset(strokePx * 1.5f, strokePx * 1.4f),
                                end = Offset(size.width - strokePx * 1.5f, strokePx * 1.4f),
                                strokeWidth = strokePx
                            )
                        }
                    }
                    .border(1.dp, twilightBorder, shape)
            } else {
                // Crisp Frosted Aqua Crystal Window — clean high-contrast white-ice glass with gentle top sheen
                val aeroOuterBorder = borderOverride ?: if (subtle) Color(0x660082C8) else Color(0xA60082C8)
                this
                    .clip(shape)
                    .drawWithCache {
                        val strokePx = 1.1.dp.toPx()
                        val baseFill = Brush.verticalGradient(
                            0.0f to if (subtle) Color(0xF5FFFFFF) else Color(0xFAFFFFFF),
                            0.55f to if (subtle) Color(0xEEF0FAFF) else Color(0xF5EFFAFF),
                            1.0f to if (subtle) Color(0xEEF0FDF6) else Color(0xF5EBFDF4)
                        )
                        val topGlossBand = if (!subtle) {
                            Brush.verticalGradient(
                                0.0f to Color.White.copy(alpha = 0.62f),
                                0.28f to Color.Transparent
                            )
                        } else null
                        onDrawBehind {
                            drawRect(baseFill)
                            if (topGlossBand != null) drawRect(topGlossBand)
                            if (!subtle) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.90f),
                                    start = Offset(strokePx * 1.5f, strokePx * 1.5f),
                                    end = Offset(size.width - strokePx * 1.5f, strokePx * 1.5f),
                                    strokeWidth = strokePx
                                )
                            }
                        }
                    }
                    .border(1.dp, aeroOuterBorder, shape)
            }
        }
    }
}

@Composable
fun Modifier.ironGlassBar(
    tokens: IronPulseThemeTokens,
    shapeRadius: Dp = 0.dp,
    topHairlineColor: Color? = null,
    bottomHairlineColor: Color? = null
): Modifier {
    val shape = remember(shapeRadius) { RoundedCornerShape(shapeRadius) }
    val tint = tokens.effectiveTintColor

    return this
        .clip(shape)
        .drawWithCache {
            val strokePx = 1.dp.toPx()
            val primaryBrush = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> Brush.verticalGradient(
                    0.0f to tokens.palette.primary.copy(alpha = 0.04f),
                    1.0f to Color.Transparent
                )
                UiTheme.UBUNTU_YARU -> Brush.verticalGradient(
                    0.0f to Color(0xFF331D2D).copy(alpha = 0.85f),
                    1.0f to Color(0xFF232025)
                )
                UiTheme.CARBON_FORGE -> Brush.verticalGradient(
                    0.0f to Color.White.copy(alpha = 0.04f),
                    1.0f to Color.Black.copy(alpha = 0.16f)
                )
                UiTheme.AMOLED_STEALTH -> null
                UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) {
                    Brush.verticalGradient(
                        0.0f to Color(0xF2214271),
                        0.50f to Color(0xF2173157),
                        1.0f to Color(0xF4122B47)
                    )
                } else {
                    Brush.verticalGradient(
                        0.0f to Color(0xFAFFFFFF),
                        0.50f to Color(0xF2E5F6FF),
                        1.0f to Color(0xF4E8FCF2)
                    )
                }
            }
            val solidBg = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> tokens.palette.surfaceContainerLow
                UiTheme.UBUNTU_YARU -> Color(0xFF221E24)
                UiTheme.CARBON_FORGE -> tint
                UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
                UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) Color(0xF4162E52) else Color(0xF5F2FBFF)
            }
            val barAccentHighlight = when (tokens.uiTheme) {
                UiTheme.CARBON_FORGE -> tokens.accentColor.copy(alpha = 0.55f)
                UiTheme.UBUNTU_YARU -> Color(0xFFE95420).copy(alpha = 0.45f)
                UiTheme.AMOLED_STEALTH -> tokens.accentColor.copy(alpha = 0.36f)
                UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) Color(0xFF7DD3FC).copy(alpha = 0.48f) else Color.White.copy(alpha = 0.90f)
                else -> null
            }
            onDrawBehind {
                drawRect(solidBg)
                if (primaryBrush != null) drawRect(primaryBrush)
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
                if (barAccentHighlight != null && topHairlineColor == null) {
                    drawLine(
                        color = barAccentHighlight,
                        start = Offset(0f, strokePx * 0.7f),
                        end = Offset(size.width, strokePx * 0.7f),
                        strokeWidth = strokePx * 1.2f
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
    val isAero = uiTheme == UiTheme.FRUTIGER_AERO
    val isAeroNight = isAero && timePhase == DayTimePhase.NIGHT
    val isLightAero = isAero && !isAeroNight
    val isDefaultAccent = prefs.accentColorHex.equals(
        MaterialYouColorEngine.DEFAULT_ACCENT_COLOR,
        ignoreCase = true
    )
    val effectiveAccentHex = when {
        // Frutiger Aero locks accent permanently to signature Aero Sky Blue (day & twilight night)
        isAero -> MaterialYouColorEngine.AERO_SKY_ACCENT
        uiTheme == UiTheme.UBUNTU_YARU && isDefaultAccent -> MaterialYouColorEngine.YARU_ORANGE_ACCENT
        uiTheme == UiTheme.AMOLED_STEALTH && isDefaultAccent -> MaterialYouColorEngine.AMOLED_STEALTH_CYAN_ACCENT
        else -> prefs.accentColorHex
    }

    val effectiveTintOverride = if (isAero) null else prefs.materialTintColorHex
    val effectiveTintHex = effectiveTintOverride
        ?: if (isAeroNight) "#162A52" else MaterialYouColorEngine.defaultTintForTheme(uiTheme)

    val palette = remember(effectiveAccentHex, effectiveTintOverride, uiTheme, isLightAero, isAeroNight) {
        MaterialYouColorEngine.generateMaterialYouPalette(
            seedHex = effectiveAccentHex,
            surfaceTintOverrideHex = effectiveTintOverride,
            isLight = isLightAero,
            isAeroNight = isAeroNight
        )
    }

    val phaseTokens = remember(timePhase, isLightAero, isAeroNight) {
        if (isAeroNight) {
            // Soft Twilight Aero Night hero tokens — indigo-aqua glass with gentle aurora accents
            PhaseThemeTokens(
                phase = DayTimePhase.NIGHT,
                accentHex = "#38BDF8",
                accentBrightHex = "#7DD3FC",
                accent = Color(0xFF38BDF8),
                accentBright = Color(0xFF7DD3FC),
                heroStart = Color(0xDE1C3A6B),
                heroMid = Color(0xDC16335A),
                heroEnd = Color(0xE011344A),
                heroBorder = Color(0x995BC6FA),
                headerBorder = Color(0x8838BDF8),
                glowPrimary = Color(0x3838BDF8),
                glowSecondary = Color(0x2E2DD4BF)
            )
        } else {
            resolvePhaseThemeTokens(timePhase, isLight = isLightAero)
        }
    }

    val fontPack = remember(uiTheme) { resolveThemeFontPack(uiTheme) }
    val themeTypography = remember(uiTheme) { buildIronPulseTypography(uiTheme) }

    val tokens = remember(uiTheme, palette, effectiveTintHex, timePhase, phaseTokens, prefs, isAero, isAeroNight, isLightAero, fontPack) {
        val canvasBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surface
            UiTheme.UBUNTU_YARU -> Color(0xFF1F1C21)
            UiTheme.CARBON_FORGE -> Color(0xFF07080B)
            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xFF162A52) else Color(0xFF7FD9FF)
        }
        val canvasSecondaryBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF381630) // iconic Ubuntu aubergine desktop warmth
            UiTheme.CARBON_FORGE -> Color(0xFF12161E)
            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xFF0F3D3E) else Color(0xFFDFFCE9)
        }
        val borderColor = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant
            UiTheme.CARBON_FORGE -> Color(0xFFCBD5E1).copy(alpha = 0.24f)
            UiTheme.UBUNTU_YARU -> Color(0xFF141216)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.32f)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0x995BC6FA) else Color(0xAA0099E5)
        }
        val subtleBorder = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant.copy(alpha = 0.65f)
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.14f)
            UiTheme.UBUNTU_YARU -> Color(0xFF18151A)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.18f)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0x555BC6FA) else Color(0x660088CC)
        }
        val cardRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 22.dp
            UiTheme.CARBON_FORGE -> 8.dp
            UiTheme.UBUNTU_YARU -> 12.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.FRUTIGER_AERO -> 18.dp
        }
        val buttonRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 999.dp
            UiTheme.CARBON_FORGE -> 6.dp
            UiTheme.UBUNTU_YARU -> 10.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.FRUTIGER_AERO -> 999.dp
        }
        val inputRadius = when (uiTheme) {
            UiTheme.CARBON_FORGE -> 6.dp
            UiTheme.UBUNTU_YARU -> 10.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.MATERIAL_YOU -> 14.dp
            UiTheme.FRUTIGER_AERO -> 14.dp
        }
        val bubbleRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 20.dp
            UiTheme.CARBON_FORGE -> 8.dp
            UiTheme.UBUNTU_YARU -> 12.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.FRUTIGER_AERO -> 18.dp
        }
        val heroScrimAlpha = when (uiTheme) {
            UiTheme.CARBON_FORGE -> 0.42f
            UiTheme.UBUNTU_YARU -> 0.52f
            UiTheme.AMOLED_STEALTH -> 0.80f
            UiTheme.MATERIAL_YOU -> 0.62f
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) 0.28f else 0.12f
        }
        val wellBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF211E23)
            UiTheme.CARBON_FORGE -> Color(0xFF0A0C10)
            UiTheme.AMOLED_STEALTH -> Color(0xFF020305)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xE0112442) else Color(0xF0EAF7FF)
        }
        val controlBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh
            UiTheme.UBUNTU_YARU -> Color(0xFF343037)
            UiTheme.CARBON_FORGE -> Color(0xFF171C26)
            UiTheme.AMOLED_STEALTH -> Color(0xFF0B0D12)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xEB1E3C66) else Color(0xFAFFFFFF)
        }
        val subtleChipBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh.copy(alpha = 0.78f)
            UiTheme.UBUNTU_YARU -> Color(0xFF322D34)
            UiTheme.CARBON_FORGE -> Color(0xFF141923)
            UiTheme.AMOLED_STEALTH -> Color(0xFF080A0E)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xDC1B3860) else Color(0xE8DCF2FF)
        }
        val mutedTextColor = when (uiTheme) {
            UiTheme.UBUNTU_YARU -> Color(0xFFBEB9B6)
            UiTheme.AMOLED_STEALTH -> Color(0xFF94A3B8)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xFFB4D3EB) else Color(0xFF24496A)
            else -> palette.onSurfaceVariant
        }
        val cardTopHighlight = when (uiTheme) {
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.22f)
            UiTheme.UBUNTU_YARU -> Color.White.copy(alpha = 0.10f)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.42f)
            UiTheme.MATERIAL_YOU -> Color.White.copy(alpha = 0.05f)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xFFBAE6FD).copy(alpha = 0.48f) else Color.White.copy(alpha = 0.94f)
        }
        val accentTextColor = when {
            isLightAero -> Color(0xFF005F9E)
            isAeroNight -> Color(0xFF7DD3FC)
            else -> palette.primary
        }
        val dividerColor = when {
            isLightAero -> Color(0x280B4B75)
            isAeroNight -> Color(0x367DD3FC)
            else -> Color.White.copy(alpha = 0.09f)
        }
        val successColor = if (isLightAero) Color(0xFF15803D) else Color(0xFF4ADE80)
        val warningColor = if (isLightAero) Color(0xFFB45309) else Color(0xFFFBBF24)
        val dangerColor = if (isLightAero) Color(0xFFB91C1C) else Color(0xFFF87171)
        val infoColor = if (isLightAero) Color(0xFF0369A1) else Color(0xFF38BDF8)

        IronPulseThemeTokens(
            uiTheme = uiTheme,
            palette = palette,
            accentColor = palette.primary,
            onAccentColor = palette.onPrimary,
            accentTextColor = accentTextColor,
            dividerColor = dividerColor,
            successColor = successColor,
            warningColor = warningColor,
            dangerColor = dangerColor,
            infoColor = infoColor,
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
            buttonHasGloss = isAero,
            isLightTheme = isLightAero,
            isAeroNight = isAeroNight,
            accentLocked = isAero,
            fontPack = fontPack,
            timePhase = timePhase,
            phaseTokens = phaseTokens,
            performance = prefs.performance,
            lang = prefs.lang,
            clockFormat = prefs.clockFormat
        )
    }

    val colorScheme = remember(tokens) {
        if (tokens.isLightTheme) {
            lightColorScheme(
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
                error = Color(0xFFDC2626),
                onError = Color.White
            )
        } else {
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
    }

    CompositionLocalProvider(LocalIronPulseTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = themeTypography,
            content = content
        )
    }
}
