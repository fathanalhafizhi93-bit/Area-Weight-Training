package com.fathan.ironpulse.ui.theme

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
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
import com.fathan.ironpulse.model.ThemeAnimationStyle
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.components.LocalThemeMotionSpec
import com.fathan.ironpulse.ui.components.MotionStyleCatalog
import com.fathan.ironpulse.ui.components.tactilePress

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

fun resolveXMaterialPhaseTokens(phase: DayTimePhase): PhaseThemeTokens = when (phase) {
    DayTimePhase.MORNING -> PhaseThemeTokens(
        phase = phase,
        accentHex = "#DFBE84",
        accentBrightHex = "#F3E0BE",
        accent = Color(0xFFDFBE84),
        accentBright = Color(0xFFF3E0BE),
        heroStart = Color(0xFA121318),
        heroMid = Color(0xF60C0D11),
        heroEnd = Color(0xFA08090C),
        heroBorder = Color(0x4ADFBE84),
        headerBorder = Color(0x2CDFBE84),
        glowPrimary = Color(0x1EDFBE84),
        glowSecondary = Color(0x12F3E0BE)
    )
    DayTimePhase.AFTERNOON -> PhaseThemeTokens(
        phase = phase,
        accentHex = "#8CC5C2",
        accentBrightHex = "#C6E6E4",
        accent = Color(0xFF8CC5C2),
        accentBright = Color(0xFFC6E6E4),
        heroStart = Color(0xFA111317),
        heroMid = Color(0xF60C0D11),
        heroEnd = Color(0xFA08090C),
        heroBorder = Color(0x4A8CC5C2),
        headerBorder = Color(0x2C8CC5C2),
        glowPrimary = Color(0x1E8CC5C2),
        glowSecondary = Color(0x12C6E6E4)
    )
    DayTimePhase.NIGHT -> PhaseThemeTokens(
        phase = phase,
        accentHex = "#B0A4E3",
        accentBrightHex = "#DDD6FA",
        accent = Color(0xFFB0A4E3),
        accentBright = Color(0xFFDDD6FA),
        heroStart = Color(0xFA121218),
        heroMid = Color(0xF60C0D11),
        heroEnd = Color(0xFA08090C),
        heroBorder = Color(0x4AB0A4E3),
        headerBorder = Color(0x2CB0A4E3),
        glowPrimary = Color(0x1EB0A4E3),
        glowSecondary = Color(0x12DDD6FA)
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
    val cardShape: Shape = RoundedCornerShape(cardRadius),
    val subtleCardShape: Shape = RoundedCornerShape(cardRadius),
    val buttonShape: Shape = RoundedCornerShape(buttonRadius),
    val inputShape: Shape = RoundedCornerShape(inputRadius),
    val heroScrimAlpha: Float,
    val buttonHasGloss: Boolean,
    val isLightTheme: Boolean = false,
    val isAeroNight: Boolean = false,
    val isXMaterial: Boolean = false,
    val isXMaterialNight: Boolean = false,
    val accentLocked: Boolean = false,
    val fontPack: ThemeFontPack = resolveThemeFontPack(UiTheme.MATERIAL_YOU),
    val timePhase: DayTimePhase,
    val phaseTokens: PhaseThemeTokens,
    val performance: PerformancePreferences,
    val lang: AppLanguage,
    val clockFormat: ClockFormat,
    val animationStyle: ThemeAnimationStyle = ThemeAnimationStyle.FLUID_SILK
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
val LocalTabSettled = compositionLocalOf { true }
val LocalScrollActiveState = staticCompositionLocalOf<State<Boolean>> { mutableStateOf(false) }

/**
 * Hardware-accelerated repeating GPU shader textures cached once per density/mode key.
 * Zero per-card CPU Path loops: Carbon 2x2 twill and Ubuntu Yaru warm matte stipple
 * render in a single GPU pass at 120 FPS.
 */
object ThemeMaterialShaderCache {
    private val carbonCache = ConcurrentHashMap<Long, ShaderBrush>()
    private val yaruCache = ConcurrentHashMap<Long, ShaderBrush>()
    private val stealthCache = ConcurrentHashMap<Long, ShaderBrush>()
    private val prismaticCache = ConcurrentHashMap<Long, ShaderBrush>()

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

    fun getPrismaticFacetBrush(cellPx: Int = 28, isNight: Boolean = false): ShaderBrush {
        val safeCell = cellPx.coerceIn(16, 48)
        val key = (safeCell.toLong() shl 1) or (if (isNight) 1L else 0L)
        return prismaticCache[key] ?: synchronized(prismaticCache) {
            prismaticCache[key] ?: buildPrismaticFacetBrush(safeCell, isNight).also { prismaticCache[key] = it }
        }
    }

    private fun buildCarbonTwillBrush(cell: Int, subtle: Boolean): ShaderBrush {
        val period = cell * 4
        val tileSize = period * 2
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

        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val phase = ((col % 4) - (row % 4) + 4) % 4
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
        val period = cell * 2
        val tileSize = period * 2
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
        for (py in 0 until 2) {
            for (px in 0 until 2) {
                val ox = (px * period).toFloat()
                val oy = (py * period).toFloat()
                canvas.drawRect(ox, oy, ox + dotSize, oy + dotSize, dotPaint)
                canvas.drawRect(ox + cell, oy + cell, ox + cell + dotSize, oy + cell + dotSize, faintDotPaint)
            }
        }
        val imageShader = ImageShader(bitmap.asImageBitmap(), TileMode.Repeated, TileMode.Repeated)
        return ShaderBrush(imageShader)
    }

    private fun buildPrismaticFacetBrush(cell: Int, isNight: Boolean): ShaderBrush {
        val tileSize = cell * 2
        val bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888)
        val canvas = AndroidCanvas(bitmap)
        val primaryLine = Paint().apply {
            color = if (isNight) {
                android.graphics.Color.argb(20, 167, 139, 250)
            } else {
                android.graphics.Color.argb(24, 255, 255, 255)
            }
            strokeWidth = 1.25f
            isAntiAlias = true
        }
        val spectralLine = Paint().apply {
            color = if (isNight) {
                android.graphics.Color.argb(14, 34, 211, 238)
            } else {
                android.graphics.Color.argb(11, 56, 189, 248)
            }
            strokeWidth = 1f
            isAntiAlias = true
        }
        val s = tileSize.toFloat()
        val half = s * 0.5f
        // Diagonal faceted prism mesh rendered once into a GPU repeating tile
        canvas.drawLine(0f, half, half, 0f, primaryLine)
        canvas.drawLine(half, s, s, half, primaryLine)
        canvas.drawLine(0f, half, half, s, spectralLine)
        canvas.drawLine(half, 0f, s, half, spectralLine)
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
    val shape = remember(tokens.uiTheme, subtle, radiusOverride, tokens.cardShape, tokens.subtleCardShape) {
        when (tokens.uiTheme) {
            UiTheme.CARBON_FORGE -> CutCornerShape(radius.coerceAtMost(8.dp))
            UiTheme.AMOLED_STEALTH -> RoundedCornerShape(radius.coerceAtMost(6.dp))
            UiTheme.XMATERIAL_YOU -> if (radiusOverride != null) {
                RoundedCornerShape(radiusOverride)
            } else if (subtle) {
                tokens.subtleCardShape
            } else {
                tokens.cardShape
            }
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
                    val topSheen = if (!subtle) {
                        Brush.verticalGradient(
                            0.0f to tokens.palette.primary.copy(alpha = 0.085f),
                            0.42f to Color.Transparent
                        )
                    } else {
                        Brush.verticalGradient(
                            0.0f to tokens.palette.primary.copy(alpha = 0.03f),
                            0.35f to Color.Transparent
                        )
                    }
                    val cornerWarmth = if (!subtle && !tokens.performance.noAmbientGlow) {
                        Brush.radialGradient(
                            colors = listOf(
                                tokens.palette.tertiary.copy(alpha = 0.065f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.92f, size.height * 0.92f),
                            radius = size.maxDimension.coerceAtLeast(160f) * 0.72f
                        )
                    } else null
                    val innerLip = Color.White.copy(alpha = if (subtle) 0.05f else 0.105f)
                    val bottomEdge = tokens.palette.secondary.copy(alpha = if (subtle) 0.03f else 0.075f)
                    onDrawBehind {
                        drawRect(baseBg)
                        drawRect(topSheen)
                        if (cornerWarmth != null) drawRect(cornerWarmth)
                        if (size.width > 32.dp.toPx()) {
                            drawLine(
                                color = innerLip,
                                start = Offset(strokePx * 2f, strokePx * 1.1f),
                                end = Offset(size.width - strokePx * 2f, strokePx * 1.1f),
                                strokeWidth = strokePx * 0.9f
                            )
                            if (!subtle) {
                                drawLine(
                                    color = bottomEdge,
                                    start = Offset(strokePx * 3f, size.height - strokePx * 1.1f),
                                    end = Offset(size.width - strokePx * 3f, size.height - strokePx * 1.1f),
                                    strokeWidth = strokePx * 0.8f
                                )
                            }
                        }
                    }
                }
                .border(1.dp, borderColor, shape)
        }
        UiTheme.UBUNTU_YARU -> {
            // Authentic Ubuntu GNOME Libadwaita surface — warm aubergine-slate depth, matte stipple grain & glowing Yaru stripe
            val outerBorder = borderOverride ?: if (subtle) Color(0xFF171419) else Color(0xFF110F13)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val grainScale = density.roundToInt().coerceIn(1, 3)
                    val yaruGrain = ThemeMaterialShaderCache.getYaruMatteGrainBrush(
                        pixelScale = grainScale,
                        subtle = subtle
                    )
                    val baseFill = Brush.verticalGradient(
                        0.0f to if (subtle) Color(0xFF252128) else Color(0xFF332B36),
                        0.45f to if (subtle) Color(0xFF201D22) else Color(0xFF2A262D),
                        1.0f to if (subtle) Color(0xFF1B181E) else Color(0xFF231F26)
                    )
                    val headerSheen = Brush.verticalGradient(
                        0.0f to Color(0xFF5D274D).copy(alpha = if (subtle) 0.12f else 0.28f),
                        0.34f to Color.Transparent
                    )
                    val innerRim = Color.White.copy(alpha = if (subtle) 0.05f else 0.11f)
                    val bottomAubergineRim = Color(0xFFE95420).copy(alpha = if (subtle) 0.04f else 0.10f)
                    val yaruStripe = Color(0xFFE95420).copy(alpha = 0.90f)
                    val yaruGlow = Color(0xFFE95420).copy(alpha = 0.18f)
                    val stripeStart = 14.dp.toPx()
                    val stripeLen = (size.width * 0.16f).coerceIn(22.dp.toPx(), 54.dp.toPx())
                    onDrawBehind {
                        drawRect(baseFill)
                        drawRect(yaruGrain)
                        drawRect(headerSheen)
                        drawLine(
                            color = innerRim,
                            start = Offset(strokePx * 1.5f, strokePx * 1.0f),
                            end = Offset(size.width - strokePx * 1.5f, strokePx * 1.0f),
                            strokeWidth = strokePx * 0.85f
                        )
                        if (!subtle && size.width > 120.dp.toPx()) {
                            drawLine(
                                color = yaruGlow,
                                start = Offset(stripeStart - 2.dp.toPx(), strokePx * 2.2f),
                                end = Offset(stripeStart + stripeLen + 2.dp.toPx(), strokePx * 2.2f),
                                strokeWidth = strokePx * 4.5f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = yaruStripe,
                                start = Offset(stripeStart, strokePx * 1.15f),
                                end = Offset(stripeStart + stripeLen, strokePx * 1.15f),
                                strokeWidth = strokePx * 2.2f,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = bottomAubergineRim,
                                start = Offset(stripeStart, size.height - strokePx * 1.1f),
                                end = Offset(size.width - stripeStart, size.height - strokePx * 1.1f),
                                strokeWidth = strokePx * 0.8f
                            )
                        }
                    }
                }
                .border(1.dp, outerBorder, shape)
        }
        UiTheme.CARBON_FORGE -> {
            // Forged Carbon & CNC Machined Steel Plate — GPU 2x2 twill weave, brushed metallic bevel & crisp accent bar
            val baseBg = tint.copy(alpha = if (subtle) 0.96f else 1f)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val cellPx = (2.dp.toPx()).roundToInt().coerceIn(4, 8)
                    val carbonTwill = ThemeMaterialShaderCache.getCarbonTwillBrush(
                        cellPx = cellPx,
                        subtle = subtle
                    )
                    val sheenBrush = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = if (subtle) 0.045f else 0.095f),
                        0.26f to Color.Transparent,
                        0.72f to Color.Transparent,
                        1.0f to Color.Black.copy(alpha = if (subtle) 0.20f else 0.32f)
                    )
                    val rimColor = Color(0xFFE2E8F0).copy(alpha = if (subtle) 0.14f else 0.28f)
                    val chamferShadow = Color.Black.copy(alpha = 0.42f)
                    val accentBar = tokens.accentColor.copy(alpha = 0.94f)
                    val accentHalo = tokens.accentColor.copy(alpha = 0.20f)
                    onDrawBehind {
                        drawRect(baseBg)
                        drawRect(carbonTwill)
                        drawRect(sheenBrush)
                        drawLine(
                            color = rimColor,
                            start = Offset(0f, strokePx * 0.7f),
                            end = Offset(size.width, strokePx * 0.7f),
                            strokeWidth = strokePx
                        )
                        drawLine(
                            color = chamferShadow,
                            start = Offset(0f, strokePx * 1.8f),
                            end = Offset(size.width, strokePx * 1.8f),
                            strokeWidth = strokePx * 0.7f
                        )
                        if (!subtle && size.height > 44.dp.toPx()) {
                            val barH = (size.height - 20.dp.toPx()).coerceAtLeast(10f)
                            drawRect(
                                color = accentHalo,
                                topLeft = Offset(0f, 8.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(5.5.dp.toPx(), barH + 4.dp.toPx())
                            )
                            drawRect(
                                color = accentBar,
                                topLeft = Offset(0f, 10.dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(2.6.dp.toPx(), barH)
                            )
                        }
                    }
                }
                .border(1.dp, borderColor, shape)
        }
        UiTheme.AMOLED_STEALTH -> {
            // Pure Black Tactical HUD Module — OLED-first deep black, subtle perimeter matrix & precision corner reticles
            val baseBg = if (subtle) Color(0xFF020304) else Color(0xFF040609)
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val matrixCellPx = (3.dp.toPx()).roundToInt().coerceIn(5, 10)
                    val matrixBrush = if (!tokens.performance.noAmbientGlow) {
                        ThemeMaterialShaderCache.getAmoledStealthMatrixBrush(
                            cellPx = matrixCellPx,
                            subtle = true
                        )
                    } else null
                    val topAura = if (!subtle && !tokens.performance.noAmbientGlow) {
                        Brush.verticalGradient(
                            0.0f to (borderOverride ?: tokens.accentColor).copy(alpha = 0.075f),
                            0.28f to Color.Transparent
                        )
                    } else null
                    val bracketStroke = (if (subtle) 1.0.dp else 1.5.dp).toPx()
                    val arm = (if (subtle) 4.dp else 7.5.dp).toPx()
                    val activeAccent = borderOverride ?: tokens.accentColor
                    val bracketColor = activeAccent.copy(alpha = if (subtle) 0.48f else 0.88f)
                    val scanLineColor = activeAccent.copy(alpha = 0.28f)
                    onDrawBehind {
                        drawRect(baseBg)
                        if (matrixBrush != null) drawRect(matrixBrush)
                        if (topAura != null) drawRect(topAura)
                        if (size.width > 80.dp.toPx() && size.height > 36.dp.toPx()) {
                            val p = strokePx * 1.15f
                            if (!subtle) {
                                drawLine(bracketColor, Offset(p, p), Offset(p + arm, p), bracketStroke)
                                drawLine(bracketColor, Offset(p, p), Offset(p, p + arm), bracketStroke)

                                drawLine(bracketColor, Offset(size.width - p, p), Offset(size.width - p - arm, p), bracketStroke)
                                drawLine(bracketColor, Offset(size.width - p, p), Offset(size.width - p, p + arm), bracketStroke)

                                drawLine(bracketColor, Offset(p, size.height - p), Offset(p + arm, size.height - p), bracketStroke)
                                drawLine(bracketColor, Offset(p, size.height - p), Offset(p, size.height - p - arm), bracketStroke)

                                drawLine(bracketColor, Offset(size.width - p, size.height - p), Offset(size.width - p - arm, size.height - p), bracketStroke)
                                drawLine(bracketColor, Offset(size.width - p, size.height - p), Offset(size.width - p, size.height - p - arm), bracketStroke)

                                if (size.width > 160.dp.toPx()) {
                                    drawLine(
                                        color = scanLineColor,
                                        start = Offset(size.width * 0.34f, p),
                                        end = Offset(size.width * 0.66f, p),
                                        strokeWidth = strokePx * 0.9f
                                    )
                                    drawLine(
                                        color = scanLineColor.copy(alpha = 0.18f),
                                        start = Offset(size.width * 0.40f, size.height - p),
                                        end = Offset(size.width * 0.60f, size.height - p),
                                        strokeWidth = strokePx * 0.8f
                                    )
                                }
                            } else {
                                drawLine(bracketColor, Offset(p, p), Offset(p + arm, p), bracketStroke)
                                drawLine(bracketColor, Offset(p, p), Offset(p, p + arm), bracketStroke)
                                drawLine(bracketColor, Offset(size.width - p, size.height - p), Offset(size.width - p - arm, size.height - p), bracketStroke)
                                drawLine(bracketColor, Offset(size.width - p, size.height - p), Offset(size.width - p, size.height - p - arm), bracketStroke)
                            }
                        }
                    }
                }
                .border(0.9.dp, borderColor.copy(alpha = if (subtle) 0.42f else 0.64f), shape)
        }
        UiTheme.FRUTIGER_AERO -> {
            if (tokens.isAeroNight) {
                // Twilight Dark Aero Crystal — 2-zone wet reflection + frosted inner crystal bevel
                val twilightBorder = borderOverride ?: if (subtle) Color(0x7A5BC6FA) else Color(0xB85BC6FA)
                this
                    .clip(shape)
                    .drawWithCache {
                        val strokePx = 1.dp.toPx()
                        val baseFill = Brush.verticalGradient(
                            0.0f to if (subtle) Color(0xEE214274) else Color(0xF5274E86),
                            0.47f to if (subtle) Color(0xEB18335B) else Color(0xF01C3C6A),
                            0.51f to if (subtle) Color(0xEA142B4D) else Color(0xEE163159),
                            1.0f to if (subtle) Color(0xED13304F) else Color(0xF4163A59)
                        )
                        val wetGloss = Brush.verticalGradient(
                            0.0f to Color(0xFFE0F4FF).copy(alpha = if (subtle) 0.18f else 0.29f),
                            0.46f to Color(0xFFBAE6FD).copy(alpha = if (subtle) 0.05f else 0.09f),
                            0.48f to Color.Transparent,
                            0.78f to Color.Transparent,
                            1.0f to Color(0xFF7DD3FC).copy(alpha = if (subtle) 0.08f else 0.14f)
                        )
                        val crystalTopLine = Color(0xFFE0F4FF).copy(alpha = if (subtle) 0.38f else 0.64f)
                        val crystalBottomRim = Color(0xFF5EEAD4).copy(alpha = if (subtle) 0.14f else 0.24f)
                        onDrawBehind {
                            drawRect(baseFill)
                            drawRect(wetGloss)
                            drawLine(
                                color = crystalTopLine,
                                start = Offset(strokePx * 2.2f, strokePx * 1.25f),
                                end = Offset(size.width - strokePx * 2.2f, strokePx * 1.25f),
                                strokeWidth = strokePx * 1.1f
                            )
                            drawLine(
                                color = crystalBottomRim,
                                start = Offset(strokePx * 2.6f, size.height - strokePx * 1.25f),
                                end = Offset(size.width - strokePx * 2.6f, size.height - strokePx * 1.25f),
                                strokeWidth = strokePx * 0.85f
                            )
                        }
                    }
                    .border(1.dp, twilightBorder, shape)
            } else {
                // Crisp Frosted Aqua Crystal Window — 2-zone Y2K wet reflection + radiant inner crystal bevel
                val aeroOuterBorder = borderOverride ?: if (subtle) Color(0x880082C8) else Color(0xC20082C8)
                this
                    .clip(shape)
                    .drawWithCache {
                        val strokePx = 1.dp.toPx()
                        val baseFill = Brush.verticalGradient(
                            0.0f to if (subtle) Color(0xFBFFFFFF) else Color(0xFEFFFFFF),
                            0.47f to if (subtle) Color(0xF4ECF9FF) else Color(0xF8F0FAFF),
                            0.51f to if (subtle) Color(0xF0DFF4FF) else Color(0xF5E3F6FF),
                            1.0f to if (subtle) Color(0xF2E9FCF3) else Color(0xF7EAFFF5)
                        )
                        val wetGloss = Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = if (subtle) 0.58f else 0.78f),
                            0.46f to Color.White.copy(alpha = if (subtle) 0.16f else 0.25f),
                            0.48f to Color.Transparent,
                            0.78f to Color.Transparent,
                            1.0f to Color.White.copy(alpha = if (subtle) 0.18f else 0.28f)
                        )
                        val crystalTopLine = Color.White.copy(alpha = if (subtle) 0.88f else 0.98f)
                        val innerAquaRim = Color(0xFF7DD3FC).copy(alpha = if (subtle) 0.22f else 0.34f)
                        onDrawBehind {
                            drawRect(baseFill)
                            drawRect(wetGloss)
                            drawLine(
                                color = crystalTopLine,
                                start = Offset(strokePx * 2.2f, strokePx * 1.25f),
                                end = Offset(size.width - strokePx * 2.2f, strokePx * 1.25f),
                                strokeWidth = strokePx * 1.2f
                            )
                            drawLine(
                                color = innerAquaRim,
                                start = Offset(strokePx * 2.6f, size.height - strokePx * 1.25f),
                                end = Offset(size.width - strokePx * 2.6f, size.height - strokePx * 1.25f),
                                strokeWidth = strokePx * 0.9f
                            )
                        }
                    }
                    .border(1.dp, aeroOuterBorder, shape)
            }
        }
        UiTheme.XMATERIAL_YOU -> {
            // Luxury Dark Obsidian Glass — prismatic facet refraction, deep obsidian body & brushed phase-metal rim
            val phaseMetal = tokens.phaseTokens.accent
            val phaseBright = tokens.phaseTokens.accentBright
            val obsidianRimBrush = if (borderOverride != null) {
                Brush.verticalGradient(
                    0.0f to borderOverride,
                    1.0f to borderOverride.copy(alpha = 0.65f)
                )
            } else {
                Brush.verticalGradient(
                    0.0f to if (subtle) phaseMetal.copy(alpha = 0.24f) else phaseBright.copy(alpha = 0.42f),
                    0.45f to if (subtle) Color(0xFF272934) else phaseMetal.copy(alpha = 0.22f),
                    1.0f to Color(0xFF1E2029)
                )
            }
            this
                .clip(shape)
                .drawWithCache {
                    val strokePx = 1.dp.toPx()
                    val facetCellPx = (14.dp.toPx()).roundToInt().coerceIn(20, 44)
                    val facetBrush = if (!subtle && !tokens.performance.noAmbientGlow) {
                        ThemeMaterialShaderCache.getPrismaticFacetBrush(
                            cellPx = facetCellPx,
                            isNight = tokens.isXMaterialNight
                        )
                    } else null
                    val baseFill = Brush.verticalGradient(
                        0.0f to if (subtle) Color(0xF60E0F14) else Color(0xFA14151D),
                        0.55f to if (subtle) Color(0xF60A0B0F) else Color(0xFA0C0D12),
                        1.0f to if (subtle) Color(0xF808090C) else Color(0xFC08090D)
                    )
                    val topSatin = Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = if (subtle) 0.020f else 0.045f),
                        0.34f to phaseBright.copy(alpha = if (subtle) 0.010f else 0.022f),
                        0.72f to Color.Transparent
                    )
                    val jewelCornerGlow = if (!subtle && !tokens.performance.noAmbientGlow) {
                        Brush.radialGradient(
                            colors = listOf(phaseBright.copy(alpha = 0.065f), Color.Transparent),
                            center = Offset(size.width * 0.12f, 0f),
                            radius = size.width.coerceAtLeast(180f) * 0.65f
                        )
                    } else null
                    val topHairline = if (subtle) {
                        phaseBright.copy(alpha = 0.14f)
                    } else {
                        phaseBright.copy(alpha = 0.30f)
                    }
                    onDrawBehind {
                        drawRect(baseFill)
                        if (facetBrush != null) drawRect(facetBrush, alpha = 0.42f)
                        drawRect(topSatin)
                        if (jewelCornerGlow != null) drawRect(jewelCornerGlow)
                        if (size.width > 36.dp.toPx()) {
                            drawLine(
                                color = topHairline,
                                start = Offset(strokePx * 2f, strokePx * 0.95f),
                                end = Offset(size.width - strokePx * 2f, strokePx * 0.95f),
                                strokeWidth = strokePx * 0.9f
                            )
                        }
                    }
                }
                .border(if (subtle) 0.9.dp else 1.dp, obsidianRimBrush, shape)
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
            val cellPx = (2.dp.toPx()).roundToInt().coerceIn(4, 8)
            val grainScale = density.roundToInt().coerceIn(1, 3)
            val matrixCellPx = (3.dp.toPx()).roundToInt().coerceIn(5, 10)
            val facetCellPx = (14.dp.toPx()).roundToInt().coerceIn(20, 44)
            val shaderBrush = when (tokens.uiTheme) {
                UiTheme.CARBON_FORGE -> ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = cellPx, subtle = true)
                UiTheme.UBUNTU_YARU -> ThemeMaterialShaderCache.getYaruMatteGrainBrush(pixelScale = grainScale, subtle = true)
                UiTheme.AMOLED_STEALTH -> if (!tokens.performance.noAmbientGlow) {
                    ThemeMaterialShaderCache.getAmoledStealthMatrixBrush(cellPx = matrixCellPx, subtle = true)
                } else null
                UiTheme.XMATERIAL_YOU -> if (!tokens.performance.noAmbientGlow) {
                    ThemeMaterialShaderCache.getPrismaticFacetBrush(cellPx = facetCellPx, isNight = tokens.isXMaterialNight)
                } else null
                UiTheme.MATERIAL_YOU, UiTheme.FRUTIGER_AERO -> null
            }
            val primaryBrush = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> Brush.verticalGradient(
                    0.0f to tokens.palette.primary.copy(alpha = 0.06f),
                    0.65f to tokens.palette.tertiary.copy(alpha = 0.02f),
                    1.0f to Color.Transparent
                )
                UiTheme.UBUNTU_YARU -> Brush.verticalGradient(
                    0.0f to Color(0xFF3A1E32).copy(alpha = 0.88f),
                    1.0f to Color(0xFF231F25)
                )
                UiTheme.CARBON_FORGE -> Brush.verticalGradient(
                    0.0f to Color.White.copy(alpha = 0.06f),
                    0.5f to Color.Transparent,
                    1.0f to Color.Black.copy(alpha = 0.22f)
                )
                UiTheme.AMOLED_STEALTH -> if (!tokens.performance.noAmbientGlow) {
                    Brush.verticalGradient(
                        0.0f to tokens.accentColor.copy(alpha = 0.045f),
                        0.45f to Color.Transparent
                    )
                } else null
                UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) {
                    Brush.verticalGradient(
                        0.0f to Color(0xF628508A),
                        0.47f to Color(0xF41C3C6C),
                        0.51f to Color(0xF2152E54),
                        1.0f to Color(0xF6133252)
                    )
                } else {
                    Brush.verticalGradient(
                        0.0f to Color(0xFEFFFFFF),
                        0.47f to Color(0xF6EAF8FF),
                        0.51f to Color(0xF4DCF3FF),
                        1.0f to Color(0xF7E8FDF3)
                    )
                }
                UiTheme.XMATERIAL_YOU -> Brush.verticalGradient(
                    0.0f to Color(0xFA0C0D13),
                    0.55f to Color(0xF80A0B10),
                    1.0f to Color(0xF80E1016)
                )
            }
            val xMaterialSheen = if (tokens.uiTheme == UiTheme.XMATERIAL_YOU) {
                Brush.verticalGradient(
                    0.0f to tokens.phaseTokens.accentBright.copy(alpha = 0.04f),
                    0.5f to Color.Transparent
                )
            } else null
            val aeroGlossOverlay = if (tokens.uiTheme == UiTheme.FRUTIGER_AERO) {
                Brush.verticalGradient(
                    0.0f to if (tokens.isAeroNight) Color(0xFFE0F4FF).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.72f),
                    0.46f to if (tokens.isAeroNight) Color(0xFFBAE6FD).copy(alpha = 0.07f) else Color.White.copy(alpha = 0.20f),
                    0.48f to Color.Transparent,
                    0.80f to Color.Transparent,
                    1.0f to if (tokens.isAeroNight) Color(0xFF7DD3FC).copy(alpha = 0.12f) else Color.White.copy(alpha = 0.24f)
                )
            } else null
            val solidBg = when (tokens.uiTheme) {
                UiTheme.MATERIAL_YOU -> tokens.palette.surfaceContainerLow
                UiTheme.UBUNTU_YARU -> Color(0xFF221E24)
                UiTheme.CARBON_FORGE -> tint
                UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
                UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) Color(0xF4162E52) else Color(0xF6F2FBFF)
                UiTheme.XMATERIAL_YOU -> Color(0xFA090A0E)
            }
            onDrawBehind {
                drawRect(solidBg)
                if (shaderBrush != null) {
                    val alpha = if (tokens.uiTheme == UiTheme.XMATERIAL_YOU) 0.32f else 0.85f
                    drawRect(shaderBrush, alpha = alpha)
                }
                if (primaryBrush != null) drawRect(primaryBrush)
                if (xMaterialSheen != null) drawRect(xMaterialSheen)
                if (aeroGlossOverlay != null) drawRect(aeroGlossOverlay)
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

@Composable
fun Modifier.ironTonalControl(
    tokens: IronPulseThemeTokens,
    shape: Shape,
    baseColor: Color,
    isAccent: Boolean = false,
    glossAlphaMultiplier: Float = 1f
): Modifier {
    if (tokens.uiTheme != UiTheme.FRUTIGER_AERO) {
        return this.clip(shape).background(baseColor, shape)
    }
    if (!isAccent && baseColor.alpha < 0.02f) {
        return this.clip(shape).background(baseColor, shape)
    }

    val isNight = tokens.isAeroNight
    val isPrimarySkyAccent = isAccent && baseColor == tokens.accentColor
    val alpha = baseColor.alpha.coerceIn(0.05f, 1f)
    val opaqueBase = baseColor.copy(alpha = 1f)

    val topStop: Color
    val upperMidStop: Color
    val lowerMidStop: Color
    val bottomStop: Color
    if (isPrimarySkyAccent) {
        if (isNight) {
            topStop = Color(0xFF67D2FF)
            upperMidStop = Color(0xFF009AE6)
            lowerMidStop = Color(0xFF0077C2)
            bottomStop = Color(0xFF005A99)
        } else {
            topStop = Color(0xFF6ED5FF)
            upperMidStop = Color(0xFF0096E6)
            lowerMidStop = Color(0xFF0078C4)
            bottomStop = Color(0xFF005D9E)
        }
    } else if (isAccent && alpha >= 0.72f) {
        topStop = lerp(opaqueBase, Color.White, 0.36f).copy(alpha = alpha)
        upperMidStop = baseColor
        lowerMidStop = lerp(opaqueBase, Color.Black, 0.14f).copy(alpha = alpha)
        bottomStop = lerp(opaqueBase, Color.Black, 0.24f).copy(alpha = alpha)
    } else if (isAccent) {
        topStop = lerp(opaqueBase, Color.White, 0.56f).copy(alpha = (alpha * 1.15f).coerceAtMost(0.95f))
        upperMidStop = baseColor
        lowerMidStop = lerp(opaqueBase, Color(0xFFC5EBFF), 0.28f).copy(alpha = alpha)
        bottomStop = lerp(opaqueBase, Color(0xFFD9FAEB), 0.24f).copy(alpha = alpha)
    } else if (isNight) {
        topStop = lerp(opaqueBase, Color(0xFF93E2FF), 0.20f).copy(alpha = alpha)
        upperMidStop = baseColor
        lowerMidStop = lerp(opaqueBase, Color(0xFF08162E), 0.24f).copy(alpha = alpha)
        bottomStop = lerp(opaqueBase, Color(0xFF0C2A45), 0.16f).copy(alpha = alpha)
    } else {
        topStop = lerp(opaqueBase, Color.White, 0.65f).copy(alpha = alpha)
        upperMidStop = baseColor
        lowerMidStop = lerp(opaqueBase, Color(0xFFC5EBFF), 0.42f).copy(alpha = alpha)
        bottomStop = lerp(opaqueBase, Color(0xFFD9FAEB), 0.36f).copy(alpha = alpha)
    }

    val topSheenAlpha = (when {
        isAccent -> 0.42f
        isNight -> 0.22f
        else -> 0.58f
    } * glossAlphaMultiplier).coerceIn(0f, 0.85f)
    val midSheenAlpha = (when {
        isAccent -> 0.10f
        isNight -> 0.05f
        else -> 0.14f
    } * glossAlphaMultiplier).coerceIn(0f, 0.45f)
    val bounceAlpha = (when {
        isAccent -> 0.12f
        isNight -> 0.09f
        else -> 0.18f
    } * glossAlphaMultiplier).coerceIn(0f, 0.40f)
    val specularColor = if (isNight && !isAccent) {
        Color(0xFFBAE6FD).copy(alpha = (0.48f * glossAlphaMultiplier).coerceIn(0f, 0.85f))
    } else {
        Color.White.copy(alpha = ((if (isAccent) 0.78f else 0.90f) * glossAlphaMultiplier).coerceIn(0f, 0.96f))
    }

    return this
        .clip(shape)
        .drawWithCache {
            val strokePx = 1.dp.toPx()
            val gradient = Brush.verticalGradient(
                0.0f to topStop,
                0.46f to upperMidStop,
                0.50f to lowerMidStop,
                1.0f to bottomStop
            )
            val gloss = Brush.verticalGradient(
                0.0f to Color.White.copy(alpha = topSheenAlpha),
                0.46f to Color.White.copy(alpha = midSheenAlpha),
                0.48f to Color.Transparent,
                0.78f to Color.Transparent,
                1.0f to (if (isNight && !isAccent) Color(0xFF7DD3FC) else Color.White).copy(alpha = bounceAlpha)
            )
            onDrawBehind {
                drawRect(gradient)
                drawRect(gloss)
                if (size.width > 12.dp.toPx()) {
                    drawLine(
                        color = specularColor,
                        start = Offset(strokePx * 1.4f, strokePx * 1.0f),
                        end = Offset(size.width - strokePx * 1.4f, strokePx * 1.0f),
                        strokeWidth = strokePx * 0.9f
                    )
                }
            }
        }
}

/**
 * Canonical tonal/glossy surface for IronPulse buttons, pills, badges, and cards.
 * Keeps touch-target expansion (`minimumInteractiveComponentSize`) strictly on an outer transparent
 * wrapper while drawing shape clipping, tonal/glossy fill, border, and ripple on the exact same
 * inner visual box—preventing fill from ever spilling outside borders in any theme.
 */
@Composable
fun IronPulseTonalSurface(
    shape: Shape,
    baseColor: Color,
    modifier: Modifier = Modifier,
    visualModifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isAccent: Boolean = false,
    glossAlphaMultiplier: Float = 1f,
    border: BorderStroke? = null,
    contentColor: Color = Color.Unspecified,
    pressedScale: Float = 0.96f,
    enableTactilePress: Boolean = onClick != null,
    minTouchTarget: Boolean = onClick != null,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val interactionSource = remember { MutableInteractionSource() }
    val resolvedContentColor = when {
        contentColor != Color.Unspecified -> contentColor
        isAccent -> tokens.onAccentColor
        else -> tokens.palette.onSurface
    }
    val interactiveOuterModifier = if (onClick != null) {
        Modifier
            .then(
                if (enableTactilePress) {
                    Modifier.tactilePress(
                        enabled = enabled,
                        pressedScale = pressedScale,
                        disabledByPerf = tokens.performance.noAnimations
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .then(
                if (minTouchTarget) {
                    Modifier.layout { measurable, constraints ->
                        // Measure visual child with exact caller constraints (not inflated to 48.dp)
                        val placeable = measurable.measure(constraints)
                        val minTouchPx = 48.dp.roundToPx()
                        val w = maxOf(placeable.width, minTouchPx)
                            .coerceIn(constraints.minWidth, maxOf(constraints.maxWidth, minTouchPx))
                        val h = maxOf(placeable.height, minTouchPx)
                            .coerceIn(constraints.minHeight, maxOf(constraints.maxHeight, minTouchPx))
                        layout(w, h) {
                            placeable.placeRelative(
                                (w - placeable.width) / 2,
                                (h - placeable.height) / 2
                            )
                        }
                    }
                } else {
                    Modifier
                }
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier.then(interactiveOuterModifier),
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true
    ) {
        Box(
            modifier = visualModifier
                .clip(shape)
                .ironTonalControl(
                    tokens = tokens,
                    shape = shape,
                    baseColor = baseColor,
                    isAccent = isAccent,
                    glossAlphaMultiplier = glossAlphaMultiplier
                )
                .then(if (border != null) Modifier.border(border, shape) else Modifier)
                .then(if (onClick != null) Modifier.indication(interactionSource, ripple()) else Modifier),
            contentAlignment = contentAlignment,
            propagateMinConstraints = true
        ) {
            CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
                content()
            }
        }
    }
}

@Composable
fun IronPulseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = LocalIronPulseTokens.current.buttonShape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    if (tokens.uiTheme != UiTheme.FRUTIGER_AERO) {
        val themedBorder = border ?: when (tokens.uiTheme) {
            UiTheme.CARBON_FORGE -> BorderStroke(1.dp, Color.White.copy(alpha = 0.24f))
            UiTheme.AMOLED_STEALTH -> BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.58f))
            UiTheme.XMATERIAL_YOU -> BorderStroke(0.95.dp, tokens.phaseTokens.accentBright.copy(alpha = 0.44f))
            UiTheme.UBUNTU_YARU -> BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
            else -> null
        }
        Button(
            onClick = onClick,
            modifier = modifier.tactilePress(
                enabled = enabled,
                pressedScale = 0.962f,
                disabledByPerf = tokens.performance.noAnimations
            ),
            enabled = enabled,
            shape = shape,
            colors = colors,
            border = themedBorder,
            contentPadding = contentPadding,
            content = content
        )
        return
    }
    val effectiveContainerColor = if (enabled) colors.containerColor else colors.disabledContainerColor
    val effectiveContentColor = if (enabled) colors.contentColor else colors.disabledContentColor
    val isTransparent = effectiveContainerColor.alpha < 0.02f
    val isVibrant = !isTransparent &&
        effectiveContainerColor != tokens.controlBg &&
        effectiveContainerColor != tokens.wellBg &&
        effectiveContainerColor != tokens.subtleChipBg
    val effectiveBorder = border ?: if (!isTransparent) {
        BorderStroke(
            1.dp,
            if (tokens.isAeroNight) Color(0x99BAE6FD) else Color.White.copy(alpha = 0.80f)
        )
    } else null

    IronPulseTonalSurface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        baseColor = effectiveContainerColor,
        isAccent = isVibrant,
        glossAlphaMultiplier = if (enabled) 1f else 0.45f,
        border = effectiveBorder,
        contentColor = effectiveContentColor,
        pressedScale = 0.962f,
        enableTactilePress = true,
        modifier = modifier,
        visualModifier = Modifier.defaultMinSize(
            minWidth = ButtonDefaults.MinWidth,
            minHeight = ButtonDefaults.MinHeight
        )
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun IronPulseOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = LocalIronPulseTokens.current.buttonShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    if (tokens.uiTheme != UiTheme.FRUTIGER_AERO) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.tactilePress(
                enabled = enabled,
                pressedScale = 0.962f,
                disabledByPerf = tokens.performance.noAnimations
            ),
            enabled = enabled,
            shape = shape,
            colors = colors,
            border = border,
            contentPadding = contentPadding,
            content = content
        )
        return
    }
    val rawContainer = if (enabled) colors.containerColor else colors.disabledContainerColor
    val baseGlassColor = if (rawContainer.alpha < 0.02f) {
        val frosted = if (tokens.isAeroNight) Color(0xCC1E3D6B) else Color(0xF0FFFFFF)
        if (enabled) frosted else frosted.copy(alpha = 0.55f)
    } else {
        rawContainer
    }
    val effectiveContentColor = if (enabled) {
        if (colors.contentColor == Color.Unspecified) tokens.palette.onSurface else colors.contentColor
    } else {
        colors.disabledContentColor
    }
    val effectiveBorder = border ?: BorderStroke(1.dp, tokens.surfaceBorderColor)

    IronPulseTonalSurface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        baseColor = baseGlassColor,
        isAccent = false,
        glossAlphaMultiplier = if (enabled) 1f else 0.45f,
        border = effectiveBorder,
        contentColor = effectiveContentColor,
        pressedScale = 0.962f,
        enableTactilePress = true,
        modifier = modifier,
        visualModifier = Modifier.defaultMinSize(
            minWidth = ButtonDefaults.MinWidth,
            minHeight = ButtonDefaults.MinHeight
        )
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
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
    val isXMaterial = uiTheme == UiTheme.XMATERIAL_YOU
    val isXMaterialNight = isXMaterial && timePhase == DayTimePhase.NIGHT
    val isYaru = uiTheme == UiTheme.UBUNTU_YARU
    // Reimagined XMaterial You is Rich Expressive Dark across ALL 3 phases (Morning, Afternoon, Night)
    val isLightTheme = isLightAero
    val isDefaultAccent = prefs.accentColorHex.equals(
        MaterialYouColorEngine.DEFAULT_ACCENT_COLOR,
        ignoreCase = true
    )
    val effectiveAccentHex = when {
        // Frutiger Aero locks accent permanently to signature Aero Sky Blue (day & twilight night)
        isAero -> MaterialYouColorEngine.AERO_SKY_ACCENT
        // XMaterial You locks an exclusive phase palette per Morning/Afternoon/Night
        isXMaterial -> MaterialYouColorEngine.xmaterialAccentForPhase(timePhase)
        // Ubuntu Yaru locks accent and surface permanently to Yaru Orange & Aubergine
        isYaru -> MaterialYouColorEngine.YARU_ORANGE_ACCENT
        uiTheme == UiTheme.AMOLED_STEALTH && isDefaultAccent -> MaterialYouColorEngine.AMOLED_STEALTH_CYAN_ACCENT
        else -> prefs.accentColorHex
    }

    val effectiveTintOverride = when {
        isYaru -> MaterialYouColorEngine.defaultTintForTheme(UiTheme.UBUNTU_YARU)
        isAero || isXMaterial -> null
        else -> prefs.materialTintColorHex
    }
    val effectiveTintHex = effectiveTintOverride ?: when {
        isAeroNight -> "#162A52"
        isXMaterial -> "#0E0F13"
        else -> MaterialYouColorEngine.defaultTintForTheme(uiTheme)
    }

    val palette = remember(effectiveAccentHex, effectiveTintOverride, uiTheme, isLightAero, isAeroNight, isXMaterial, timePhase) {
        MaterialYouColorEngine.generateMaterialYouPalette(
            seedHex = effectiveAccentHex,
            surfaceTintOverrideHex = effectiveTintOverride,
            isLight = isLightAero,
            isAeroNight = isAeroNight,
            xMaterialPhase = if (isXMaterial) timePhase else null
        )
    }

    val phaseTokens = remember(timePhase, isLightAero, isAeroNight, isXMaterial) {
        when {
            isXMaterial -> resolveXMaterialPhaseTokens(timePhase)
            isAeroNight -> {
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
            }
            else -> resolvePhaseThemeTokens(timePhase, isLight = isLightAero)
        }
    }

    val fontPack = remember(uiTheme) { resolveThemeFontPack(uiTheme) }
    val themeTypography = remember(uiTheme) { buildIronPulseTypography(uiTheme) }

    val tokens = remember(
        uiTheme,
        palette,
        effectiveTintHex,
        timePhase,
        phaseTokens,
        prefs,
        isAero,
        isAeroNight,
        isXMaterial,
        isXMaterialNight,
        isLightTheme,
        fontPack
    ) {
        val canvasBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surface
            UiTheme.UBUNTU_YARU -> Color(0xFF1F1C21)
            UiTheme.CARBON_FORGE -> Color(0xFF07080B)
            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xFF162A52) else Color(0xFF7FD9FF)
            UiTheme.XMATERIAL_YOU -> Color(0xFF060608)
        }
        val canvasSecondaryBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF381630) // iconic Ubuntu aubergine desktop warmth
            UiTheme.CARBON_FORGE -> Color(0xFF12161E)
            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xFF0F3D3E) else Color(0xFFDFFCE9)
            UiTheme.XMATERIAL_YOU -> Color(0xFF0A0B0F)
        }
        val borderColor = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant
            UiTheme.CARBON_FORGE -> Color(0xFFCBD5E1).copy(alpha = 0.24f)
            UiTheme.UBUNTU_YARU -> Color(0xFF141216)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.32f)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0x995BC6FA) else Color(0xAA0099E5)
            UiTheme.XMATERIAL_YOU -> phaseTokens.accent.copy(alpha = 0.28f)
        }
        val subtleBorder = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.outlineVariant.copy(alpha = 0.65f)
            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.14f)
            UiTheme.UBUNTU_YARU -> Color(0xFF18151A)
            UiTheme.AMOLED_STEALTH -> palette.primary.copy(alpha = 0.18f)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0x555BC6FA) else Color(0x660088CC)
            UiTheme.XMATERIAL_YOU -> Color(0xFF262832)
        }
        val cardRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 22.dp
            UiTheme.CARBON_FORGE -> 8.dp
            UiTheme.UBUNTU_YARU -> 12.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.FRUTIGER_AERO -> 18.dp
            UiTheme.XMATERIAL_YOU -> 28.dp
        }
        val buttonRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 999.dp
            UiTheme.CARBON_FORGE -> 6.dp
            UiTheme.UBUNTU_YARU -> 10.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.FRUTIGER_AERO -> 999.dp
            UiTheme.XMATERIAL_YOU -> 999.dp
        }
        val inputRadius = when (uiTheme) {
            UiTheme.CARBON_FORGE -> 6.dp
            UiTheme.UBUNTU_YARU -> 10.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.MATERIAL_YOU -> 14.dp
            UiTheme.FRUTIGER_AERO -> 14.dp
            UiTheme.XMATERIAL_YOU -> 18.dp
        }
        val bubbleRadius = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> 20.dp
            UiTheme.CARBON_FORGE -> 8.dp
            UiTheme.UBUNTU_YARU -> 12.dp
            UiTheme.AMOLED_STEALTH -> 6.dp
            UiTheme.FRUTIGER_AERO -> 18.dp
            UiTheme.XMATERIAL_YOU -> 24.dp
        }
        val cardShape: Shape = when (uiTheme) {
            UiTheme.CARBON_FORGE -> CutCornerShape(8.dp)
            UiTheme.AMOLED_STEALTH -> RoundedCornerShape(6.dp)
            UiTheme.XMATERIAL_YOU -> RoundedCornerShape(28.dp)
            else -> RoundedCornerShape(cardRadius)
        }
        val subtleCardShape: Shape = when (uiTheme) {
            UiTheme.CARBON_FORGE -> CutCornerShape(8.dp)
            UiTheme.AMOLED_STEALTH -> RoundedCornerShape(6.dp)
            UiTheme.XMATERIAL_YOU -> RoundedCornerShape(22.dp)
            else -> RoundedCornerShape(cardRadius)
        }
        val buttonShape: Shape = when (uiTheme) {
            UiTheme.CARBON_FORGE -> CutCornerShape(6.dp)
            UiTheme.XMATERIAL_YOU -> RoundedCornerShape(999.dp)
            else -> RoundedCornerShape(buttonRadius)
        }
        val inputShape: Shape = when (uiTheme) {
            UiTheme.CARBON_FORGE -> CutCornerShape(6.dp)
            UiTheme.XMATERIAL_YOU -> RoundedCornerShape(18.dp)
            else -> RoundedCornerShape(inputRadius)
        }
        val heroScrimAlpha = when (uiTheme) {
            UiTheme.CARBON_FORGE -> 0.42f
            UiTheme.UBUNTU_YARU -> 0.52f
            UiTheme.AMOLED_STEALTH -> 0.80f
            UiTheme.MATERIAL_YOU -> 0.62f
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) 0.28f else 0.12f
            UiTheme.XMATERIAL_YOU -> 0.44f
        }
        val wellBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerLowest
            UiTheme.UBUNTU_YARU -> Color(0xFF211E23)
            UiTheme.CARBON_FORGE -> Color(0xFF0A0C10)
            UiTheme.AMOLED_STEALTH -> Color(0xFF020305)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xE0112442) else Color(0xF0EAF7FF)
            UiTheme.XMATERIAL_YOU -> Color(0xFF07080B)
        }
        val controlBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh
            UiTheme.UBUNTU_YARU -> Color(0xFF343037)
            UiTheme.CARBON_FORGE -> Color(0xFF171C26)
            UiTheme.AMOLED_STEALTH -> Color(0xFF0B0D12)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xEB1E3C66) else Color(0xFAFFFFFF)
            UiTheme.XMATERIAL_YOU -> Color(0xFF14161D)
        }
        val subtleChipBg = when (uiTheme) {
            UiTheme.MATERIAL_YOU -> palette.surfaceContainerHigh.copy(alpha = 0.78f)
            UiTheme.UBUNTU_YARU -> Color(0xFF322D34)
            UiTheme.CARBON_FORGE -> Color(0xFF141923)
            UiTheme.AMOLED_STEALTH -> Color(0xFF080A0E)
            UiTheme.FRUTIGER_AERO -> if (isAeroNight) Color(0xDC1B3860) else Color(0xE8DCF2FF)
            UiTheme.XMATERIAL_YOU -> Color(0xFF181A22)
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
            UiTheme.XMATERIAL_YOU -> phaseTokens.accentBright.copy(alpha = 0.34f)
        }
        val accentTextColor = when {
            isLightAero -> Color(0xFF005F9E)
            isAeroNight -> Color(0xFF7DD3FC)
            isXMaterial && timePhase == DayTimePhase.MORNING -> Color(0xFFEBCEA0)
            isXMaterial && timePhase == DayTimePhase.AFTERNOON -> Color(0xFFA7D8D5)
            isXMaterial -> Color(0xFFC5BBE9)
            else -> palette.primary
        }
        val dividerColor = when {
            isLightAero -> Color(0x280B4B75)
            isAeroNight -> Color(0x367DD3FC)
            isXMaterial -> Color.White.copy(alpha = 0.07f)
            else -> Color.White.copy(alpha = 0.09f)
        }
        val successColor = if (isLightTheme) Color(0xFF15803D) else Color(0xFF4ADE80)
        val warningColor = if (isLightTheme) Color(0xFFB45309) else Color(0xFFFBBF24)
        val dangerColor = if (isLightTheme) Color(0xFFB91C1C) else Color(0xFFF87171)
        val infoColor = if (isLightTheme) Color(0xFF0369A1) else Color(0xFF38BDF8)

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
            cardShape = cardShape,
            subtleCardShape = subtleCardShape,
            buttonShape = buttonShape,
            inputShape = inputShape,
            heroScrimAlpha = heroScrimAlpha,
            buttonHasGloss = isAero,
            isLightTheme = isLightTheme,
            isAeroNight = isAeroNight,
            isXMaterial = isXMaterial,
            isXMaterialNight = isXMaterialNight,
            accentLocked = isYaru || isAero || isXMaterial,
            fontPack = fontPack,
            timePhase = timePhase,
            phaseTokens = phaseTokens,
            performance = prefs.performance,
            lang = prefs.lang,
            clockFormat = prefs.clockFormat,
            animationStyle = prefs.themeAnimationStyle
        )
    }

    val motionSpec = remember(prefs.themeAnimationStyle) {
        MotionStyleCatalog.resolve(prefs.themeAnimationStyle)
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

    CompositionLocalProvider(
        LocalIronPulseTokens provides tokens,
        LocalThemeMotionSpec provides motionSpec
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = themeTypography,
            content = content
        )
    }
}
