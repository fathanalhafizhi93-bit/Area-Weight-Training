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
                    val strokePx = 1.dp.toPx()
                    val topSheen = Brush.verticalGradient(
                        0.0f to tokens.palette.primary.copy(alpha = if (subtle) 0.03f else 0.07f),
                        0.38f to Color.Transparent
                    )
                    val cornerWash = if (!subtle) {
                        Brush.radialGradient(
                            colors = listOf(tokens.palette.tertiary.copy(alpha = 0.055f), Color.Transparent),
                            center = Offset(size.width * 0.92f, size.height * 0.08f),
                            radius = size.maxDimension.coerceAtLeast(220f) * 0.68f
                        )
                    } else null
                    val topHighlight = Color.White.copy(alpha = if (subtle) 0.03f else 0.07f)
                    onDrawBehind {
                        drawRect(baseBg)
                        drawRect(topSheen)
                        if (cornerWash != null) drawRect(cornerWash)
                        drawLine(
                            color = topHighlight,
                            start = Offset(0f, strokePx * 0.5f),
                            end = Offset(size.width, strokePx * 0.5f),
                            strokeWidth = strokePx
                        )
                    }
                }
                .border(1.dp, borderColor, shape)
        }
        UiTheme.UBUNTU_YARU -> {
            // Authentic Ubuntu GNOME Libadwaita desktop card — clean hierarchy for primary vs nested well
            val bg = if (subtle) Color(0xFF211E23) else Color(0xFF2B272D)
            val outerBorder = borderOverride ?: if (subtle) Color(0xFF171419) else Color(0xFF121014)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val grainScale = density.roundToInt().coerceIn(1, 3)
                    val matteGrain = ThemeMaterialShaderCache.getYaruMatteGrainBrush(
                        pixelScale = grainScale,
                        subtle = true
                    )
                    val headerSheen = if (!subtle) {
                        Brush.verticalGradient(
                            0.0f to Color(0xFF42273B).copy(alpha = 0.28f),
                            0.24f to Color.Transparent
                        )
                    } else null
                    val topBevel = Color.White.copy(alpha = if (subtle) 0.05f else 0.10f)
                    val yaruStripe = Color(0xFFE95420).copy(alpha = 0.80f)
                    val bottomShade = Color.Black.copy(alpha = 0.22f)
                    val stripeStart = 14.dp.toPx()
                    val stripeLen = (size.width * 0.16f).coerceIn(22.dp.toPx(), 56.dp.toPx())
                    onDrawBehind {
                        drawRect(bg)
                        drawRect(matteGrain)
                        if (headerSheen != null) drawRect(headerSheen)
                        drawLine(
                            color = topBevel,
                            start = Offset(0f, strokePx * 1.0f),
                            end = Offset(size.width, strokePx * 1.0f),
                            strokeWidth = strokePx
                        )
                        // Signature Ubuntu Yaru warm orange headerbar mark ONLY on primary cards
                        if (!subtle && size.width > 120.dp.toPx()) {
                            drawLine(
                                color = yaruStripe,
                                start = Offset(stripeStart, strokePx * 1.2f),
                                end = Offset(stripeStart + stripeLen, strokePx * 1.2f),
                                strokeWidth = strokePx * 2.0f
                            )
                        }
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
            // Industrial Machined Steel Plate — clean chamfer, accent bar & rivets ONLY on wide primary cards
            val baseBg = tint.copy(alpha = if (subtle) 0.94f else 1f)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val cellPx = (2.2.dp.toPx()).roundToInt().coerceIn(4, 10)
                    val twillBrush = ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = cellPx, subtle = true)
                    val sheenBrush = Brush.linearGradient(
                        0.0f to Color.White.copy(alpha = if (subtle) 0.04f else 0.09f),
                        0.32f to Color.Transparent,
                        0.74f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = if (subtle) 0.22f else 0.34f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width * 0.65f, size.height)
                    )
                    val rimColor = Color(0xFFE2E8F0).copy(alpha = if (subtle) 0.12f else 0.24f)
                    val accentBar = tokens.accentColor.copy(alpha = 0.88f)
                    val rivetOuter = Color(0xFF0A0C10)
                    val rivetInner = Color(0xFF94A3B8).copy(alpha = 0.60f)
                    val rInset = 6.dp.toPx()
                    val rRad = 1.6.dp.toPx()
                    val recessedBottom = Color.Black.copy(alpha = 0.48f)
                    onDrawBehind {
                        drawRect(baseBg)
                        drawRect(twillBrush)
                        drawRect(sheenBrush)
                        drawLine(
                            color = rimColor,
                            start = Offset(0f, strokePx * 0.6f),
                            end = Offset(size.width, strokePx * 0.6f),
                            strokeWidth = strokePx * 1.1f
                        )
                        if (!subtle && size.height > 44.dp.toPx()) {
                            drawRect(
                                color = accentBar,
                                topLeft = Offset(0f, 10.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(2.dp.toPx(), (size.height - 20.dp.toPx()).coerceAtLeast(10f))
                            )
                        }
                        drawLine(
                            color = recessedBottom,
                            start = Offset(0f, size.height - strokePx * 0.5f),
                            end = Offset(size.width, size.height - strokePx * 0.5f),
                            strokeWidth = strokePx
                        )
                        if (!subtle && size.width > 180.dp.toPx() && size.height > 64.dp.toPx()) {
                            val corners = listOf(
                                Offset(size.width - rInset, rInset),
                                Offset(size.width - rInset, size.height - rInset)
                            )
                            corners.forEach { pt ->
                                drawCircle(rivetOuter, rRad * 1.35f, pt)
                                drawCircle(rivetInner, rRad * 0.65f, pt)
                            }
                        }
                    }
                }
                .border(1.dp, borderColor, shape)
        }
        UiTheme.AMOLED_STEALTH -> {
            // Pure Black Tactical HUD Module — crisp 7dp corner brackets ONLY on primary cards (zero nested noise)
            val baseBg = if (subtle) Color(0xFF030406) else Color(0xFF05070A)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val bracketStroke = 1.4.dp.toPx()
                    val arm = 7.dp.toPx()
                    val matrixCellPx = (3.dp.toPx()).roundToInt().coerceIn(5, 12)
                    val stealthMatrix = ThemeMaterialShaderCache.getAmoledStealthMatrixBrush(
                        cellPx = matrixCellPx,
                        subtle = true
                    )
                    val bracketColor = (borderOverride ?: tokens.accentColor).copy(alpha = 0.82f)
                    val topAccentFade = if (!subtle) {
                        Brush.verticalGradient(
                            0.0f to tokens.accentColor.copy(alpha = 0.055f),
                            0.24f to Color.Transparent
                        )
                    } else null
                    onDrawBehind {
                        drawRect(baseBg)
                        drawRect(stealthMatrix)
                        if (topAccentFade != null) drawRect(topAccentFade)
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
                .border(0.85.dp, borderColor.copy(alpha = if (subtle) 0.45f else 0.62f), shape)
        }
        UiTheme.FRUTIGER_AERO -> {
            // Crisp Frosted Aqua Crystal Window — high-contrast white-ice glass with single clean border & subtle top sheen
            val aeroOuterBorder = borderOverride ?: if (subtle) Color(0x7A0082C8) else Color(0xB80082C8)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.2.dp.toPx()
                    val baseFill = Brush.verticalGradient(
                        0.0f to if (subtle) Color(0xF5FFFFFF) else Color(0xFAFFFFFF),
                        0.55f to if (subtle) Color(0xEEF0FAFF) else Color(0xF5EFFAFF),
                        1.0f to if (subtle) Color(0xEEF0FDF6) else Color(0xF5EBFDF4)
                    )
                    val topGlossBand = if (!subtle) {
                        Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.72f),
                            0.28f to Color.White.copy(alpha = 0.14f),
                            0.34f to Color.Transparent,
                            1.0f to Color.Transparent
                        )
                    } else null
                    val bottomBounce = if (!subtle) {
                        Brush.verticalGradient(
                            0.74f to Color.Transparent,
                            1.0f to Color(0xFF22C55E).copy(alpha = 0.08f)
                        )
                    } else null
                    val skyInnerGlow = Color(0xFF0088CC).copy(alpha = if (subtle) 0.14f else 0.24f)
                    onDrawBehind {
                        drawRect(baseFill)
                        if (topGlossBand != null) drawRect(topGlossBand)
                        if (bottomBounce != null) drawRect(bottomBounce)
                        // Single clean inner crystal white highlight inset below border
                        drawLine(
                            color = Color.White.copy(alpha = 0.94f),
                            start = Offset(strokePx * 1.5f, strokePx * 1.6f),
                            end = Offset(size.width - strokePx * 1.5f, strokePx * 1.6f),
                            strokeWidth = strokePx * 1.1f
                        )
                        drawLine(
                            color = skyInnerGlow,
                            start = Offset(0f, size.height - strokePx * 0.7f),
                            end = Offset(size.width, size.height - strokePx * 0.7f),
                            strokeWidth = strokePx * 0.9f
                        )
                    }
                }
                .border(1.dp, aeroOuterBorder, shape)
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
            val cellPx = (2.2.dp.toPx()).roundToInt().coerceIn(4, 10)
            val grainScale = density.roundToInt().coerceIn(1, 3)
            val matrixCellPx = (3.dp.toPx()).roundToInt().coerceIn(5, 12)
            val primaryBrush = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> Brush.verticalGradient(
                    0.0f to tokens.palette.primary.copy(alpha = 0.05f),
                    1.0f to Color.Transparent
                )
                UiTheme.UBUNTU_YARU -> Brush.verticalGradient(
                    0.0f to Color(0xFF3A1E32).copy(alpha = 0.85f),
                    1.0f to Color(0xFF242126)
                )
                UiTheme.CARBON_FORGE -> ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = cellPx, subtle = false)
                UiTheme.AMOLED_STEALTH -> ThemeMaterialShaderCache.getAmoledStealthMatrixBrush(cellPx = matrixCellPx, subtle = true)
                UiTheme.FRUTIGER_AERO -> Brush.verticalGradient(
                    0.0f to Color(0xFAFFFFFF),
                    0.50f to Color(0xF2E5F6FF),
                    1.0f to Color(0xF4E8FCF2)
                )
            }
            val solidBg = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> tokens.palette.surfaceContainerLow
                UiTheme.UBUNTU_YARU -> Color(0xFF221E24)
                UiTheme.CARBON_FORGE -> tint
                UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
                UiTheme.FRUTIGER_AERO -> Color(0xF5F2FBFF)
            }
            val barAccentHighlight = when (tokens.uiTheme) {
                UiTheme.CARBON_FORGE -> tokens.accentColor.copy(alpha = 0.65f)
                UiTheme.UBUNTU_YARU -> Color(0xFFE95420).copy(alpha = 0.55f)
                UiTheme.AMOLED_STEALTH -> tokens.accentColor.copy(alpha = 0.42f)
                UiTheme.FRUTIGER_AERO -> Color.White.copy(alpha = 0.95f)
                else -> null
            }
            onDrawBehind {
                drawRect(solidBg)
                drawRect(primaryBrush)
                if (tokens.uiTheme == UiTheme.UBUNTU_YARU) {
                    drawRect(ThemeMaterialShaderCache.getYaruMatteGrainBrush(grainScale, subtle = true))
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
                if (barAccentHighlight != null && topHairlineColor == null) {
                    drawLine(
                        color = barAccentHighlight,
                        start = Offset(0f, strokePx * 0.7f),
                        end = Offset(size.width, strokePx * 0.7f),
                        strokeWidth = strokePx * 1.4f
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
    val isDefaultAccent = prefs.accentColorHex.equals(
        MaterialYouColorEngine.DEFAULT_ACCENT_COLOR,
        ignoreCase = true
    )
    val effectiveAccentHex = when {
        // Frutiger Aero locks accent permanently to signature Aero Sky Blue
        isAero -> MaterialYouColorEngine.AERO_SKY_ACCENT
        uiTheme == UiTheme.UBUNTU_YARU && isDefaultAccent -> MaterialYouColorEngine.YARU_ORANGE_ACCENT
        uiTheme == UiTheme.AMOLED_STEALTH && isDefaultAccent -> MaterialYouColorEngine.AMOLED_STEALTH_CYAN_ACCENT
        else -> prefs.accentColorHex
    }

    val effectiveTintOverride = if (isAero) null else prefs.materialTintColorHex
    val effectiveTintHex = effectiveTintOverride
        ?: MaterialYouColorEngine.defaultTintForTheme(uiTheme)

    val palette = remember(effectiveAccentHex, effectiveTintOverride, uiTheme, isAero) {
        MaterialYouColorEngine.generateMaterialYouPalette(
            seedHex = effectiveAccentHex,
            surfaceTintOverrideHex = effectiveTintOverride,
            isLight = isAero
        )
    }

    val phaseTokens = remember(timePhase, isAero) { resolvePhaseThemeTokens(timePhase, isLight = isAero) }

    val fontPack = remember(uiTheme) { resolveThemeFontPack(uiTheme) }
    val themeTypography = remember(uiTheme) { buildIronPulseTypography(uiTheme) }

    val tokens = remember(uiTheme, palette, effectiveTintHex, timePhase, phaseTokens, prefs, isAero, fontPack) {
        val canvasBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surface
            UiTheme.UBUNTU_YARU -> Color(0xFF1F1C21)
            UiTheme.CARBON_FORGE -> Color(0xFF07080B)
            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
            UiTheme.FRUTIGER_AERO -> Color(0xFF7FD9FF)
        }
        val canvasSecondaryBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF381630) // iconic Ubuntu aubergine desktop warmth
            UiTheme.CARBON_FORGE -> Color(0xFF12161E)
            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
            UiTheme.FRUTIGER_AERO -> Color(0xFFDFFCE9) // fresh meadow glow at bottom
        }
        val borderColor = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant
            UiTheme.CARBON_FORGE -> Color(0xFFCBD5E1).copy(alpha = 0.24f)
            UiTheme.UBUNTU_YARU -> Color(0xFF141216)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.32f)
            UiTheme.FRUTIGER_AERO -> Color(0xAA0099E5)
        }
        val subtleBorder = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant.copy(alpha = 0.65f)
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.14f)
            UiTheme.UBUNTU_YARU -> Color(0xFF18151A)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.18f)
            UiTheme.FRUTIGER_AERO -> Color(0x660088CC)
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
            UiTheme.FRUTIGER_AERO -> 0.12f
        }
        val wellBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF211E23)
            UiTheme.CARBON_FORGE -> Color(0xFF0A0C10)
            UiTheme.AMOLED_STEALTH -> Color(0xFF020305)
            UiTheme.FRUTIGER_AERO -> Color(0xF0EAF7FF)
        }
        val controlBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh
            UiTheme.UBUNTU_YARU -> Color(0xFF343037)
            UiTheme.CARBON_FORGE -> Color(0xFF171C26)
            UiTheme.AMOLED_STEALTH -> Color(0xFF0B0D12)
            UiTheme.FRUTIGER_AERO -> Color(0xFAFFFFFF)
        }
        val subtleChipBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh.copy(alpha = 0.78f)
            UiTheme.UBUNTU_YARU -> Color(0xFF322D34)
            UiTheme.CARBON_FORGE -> Color(0xFF141923)
            UiTheme.AMOLED_STEALTH -> Color(0xFF080A0E)
            UiTheme.FRUTIGER_AERO -> Color(0xE8DCF2FF)
        }
        val mutedTextColor = when (uiTheme) {
            UiTheme.UBUNTU_YARU -> Color(0xFFBEB9B6)
            UiTheme.AMOLED_STEALTH -> Color(0xFF94A3B8)
            UiTheme.FRUTIGER_AERO -> Color(0xFF24496A)
            else -> palette.onSurfaceVariant
        }
        val cardTopHighlight = when (uiTheme) {
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.22f)
            UiTheme.UBUNTU_YARU -> Color.White.copy(alpha = 0.10f)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.42f)
            UiTheme.MATERIAL_YOU -> Color.White.copy(alpha = 0.05f)
            UiTheme.FRUTIGER_AERO -> Color.White.copy(alpha = 0.94f)
        }
        val accentTextColor = if (isAero) Color(0xFF005F9E) else palette.primary
        val dividerColor = if (isAero) Color(0x280B4B75) else Color.White.copy(alpha = 0.09f)
        val successColor = if (isAero) Color(0xFF15803D) else Color(0xFF4ADE80)
        val warningColor = if (isAero) Color(0xFFB45309) else Color(0xFFFBBF24)
        val dangerColor = if (isAero) Color(0xFFB91C1C) else Color(0xFFF87171)
        val infoColor = if (isAero) Color(0xFF0369A1) else Color(0xFF38BDF8)

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
            isLightTheme = isAero,
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
