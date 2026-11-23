package com.fathan.ironpulse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine
import kotlin.math.cos
import kotlin.math.sin

fun resolvePreviewAccentForTheme(theme: UiTheme, activeAccent: Color): Color = when (theme) {
    UiTheme.FRUTIGER_AERO -> MaterialYouColorEngine.parseColor(MaterialYouColorEngine.AERO_SKY_ACCENT)
    UiTheme.UBUNTU_YARU -> MaterialYouColorEngine.parseColor(MaterialYouColorEngine.YARU_ORANGE_ACCENT)
    UiTheme.AMOLED_STEALTH -> if (activeAccent == MaterialYouColorEngine.parseColor(MaterialYouColorEngine.DEFAULT_ACCENT_COLOR)) {
        MaterialYouColorEngine.parseColor(MaterialYouColorEngine.AMOLED_STEALTH_CYAN_ACCENT)
    } else {
        activeAccent
    }
    UiTheme.MATERIAL_YOU, UiTheme.CARBON_FORGE -> activeAccent
}

@Composable
fun ThemeBrandEmblem(
    theme: UiTheme,
    accentOverride: Color? = null,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val accent = accentOverride ?: resolvePreviewAccentForTheme(theme, tokens.accentColor)

    Canvas(
        modifier = modifier
            .size(size)
            .testTag("theme_brand_emblem_${theme.wireValue}")
    ) {
        when (theme) {
            UiTheme.MATERIAL_YOU -> drawMaterialYouEmblem(accent)
            UiTheme.CARBON_FORGE -> drawCarbonForgeEmblem(accent)
            UiTheme.UBUNTU_YARU -> drawUbuntuYaruEmblem(accent)
            UiTheme.AMOLED_STEALTH -> drawAmoledStealthEmblem(accent)
            UiTheme.FRUTIGER_AERO -> drawFrutigerAeroEmblem()
        }
    }
}

private fun DrawScope.drawMaterialYouEmblem(accent: Color) {
    val w = size.width
    val h = size.height
    val corner = w * 0.34f

    // M3 Tonal Soft Squircle Container
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(accent.copy(alpha = 0.28f), accent.copy(alpha = 0.12f)),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Layered M3 inner tonal pill shape
    drawRoundRect(
        color = accent.copy(alpha = 0.22f),
        topLeft = Offset(w * 0.14f, h * 0.14f),
        size = Size(w * 0.72f, h * 0.72f),
        cornerRadius = CornerRadius(w * 0.24f, w * 0.24f)
    )
    // Crisp soft outer tonal stroke
    drawRoundRect(
        color = accent.copy(alpha = 0.75f),
        topLeft = Offset(w * 0.04f, h * 0.04f),
        size = Size(w * 0.92f, h * 0.92f),
        cornerRadius = CornerRadius(corner, corner),
        style = Stroke(width = w * 0.055f)
    )
    // Smooth organic pulse wave
    val pulse = Path().apply {
        moveTo(w * 0.22f, h * 0.56f)
        lineTo(w * 0.36f, h * 0.56f)
        lineTo(w * 0.45f, h * 0.31f)
        lineTo(w * 0.56f, h * 0.70f)
        lineTo(w * 0.65f, h * 0.48f)
        lineTo(w * 0.78f, h * 0.48f)
    }
    drawPath(
        path = pulse,
        color = accent,
        style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = w * 0.06f,
        center = Offset(w * 0.78f, h * 0.48f)
    )
}

private fun DrawScope.drawCarbonForgeEmblem(accent: Color) {
    val w = size.width
    val h = size.height

    // Chamfered Forged Hexagon Shield
    val hex = Path().apply {
        moveTo(w * 0.50f, h * 0.05f)
        lineTo(w * 0.90f, h * 0.27f)
        lineTo(w * 0.90f, h * 0.73f)
        lineTo(w * 0.50f, h * 0.95f)
        lineTo(w * 0.10f, h * 0.73f)
        lineTo(w * 0.10f, h * 0.27f)
        close()
    }
    drawPath(
        path = hex,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF232833), Color(0xFF101318), Color(0xFF1A202B)),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )
    )
    // Diagonal carbon-steel specular slash
    val sheen = Path().apply {
        moveTo(w * 0.50f, h * 0.07f)
        lineTo(w * 0.88f, h * 0.28f)
        lineTo(w * 0.12f, h * 0.72f)
        lineTo(w * 0.12f, h * 0.28f)
        close()
    }
    drawPath(
        path = sheen,
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent)
        )
    )
    drawPath(
        path = hex,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE2E8F0), Color(0xFF64748B), accent),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        ),
        style = Stroke(width = w * 0.065f, join = StrokeJoin.Miter)
    )
    // Sharp forged lightning-anvil core
    val bolt = Path().apply {
        moveTo(w * 0.55f, h * 0.22f)
        lineTo(w * 0.34f, h * 0.53f)
        lineTo(w * 0.49f, h * 0.53f)
        lineTo(w * 0.44f, h * 0.78f)
        lineTo(w * 0.67f, h * 0.46f)
        lineTo(w * 0.52f, h * 0.46f)
        close()
    }
    drawPath(path = bolt, color = accent)
}

private fun DrawScope.drawUbuntuYaruEmblem(accent: Color) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val cy = h * 0.5f
    val outerR = w * 0.46f

    // Warm dark GTK4 matte medallion
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF3A3538), Color(0xFF221E22))
        ),
        radius = outerR,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = accent.copy(alpha = 0.85f),
        radius = outerR,
        center = Offset(cx, cy),
        style = Stroke(width = w * 0.055f)
    )

    // 3-node Ubuntu-inspired orbital ring
    val ringR = w * 0.255f
    drawCircle(
        color = Color(0xFFF6F5F4).copy(alpha = 0.90f),
        radius = ringR,
        center = Offset(cx, cy),
        style = Stroke(width = w * 0.068f)
    )
    val nodeAngles = listOf(-90.0, 30.0, 150.0)
    nodeAngles.forEach { deg ->
        val rad = Math.toRadians(deg)
        val nx = cx + (ringR * cos(rad)).toFloat()
        val ny = cy + (ringR * sin(rad)).toFloat()
        drawCircle(color = Color(0xFF242024), radius = w * 0.115f, center = Offset(nx, ny))
        drawCircle(color = accent, radius = w * 0.078f, center = Offset(nx, ny))
    }
    // Warm center core dot
    drawCircle(
        color = accent.copy(alpha = 0.95f),
        radius = w * 0.075f,
        center = Offset(cx, cy)
    )
}

private fun DrawScope.drawAmoledStealthEmblem(accent: Color) {
    val w = size.width
    val h = size.height

    // True pitch-black OLED base
    drawRoundRect(
        color = Color(0xFF000000),
        cornerRadius = CornerRadius(w * 0.22f, w * 0.22f)
    )

    // Tactical 4-corner micro brackets
    val pad = w * 0.08f
    val arm = w * 0.18f
    val bracketStroke = Stroke(width = w * 0.052f, cap = StrokeCap.Square)
    val bracketColor = accent.copy(alpha = 0.55f)
    // Top-left
    drawLine(bracketColor, Offset(pad, pad + arm), Offset(pad, pad), bracketStroke.width)
    drawLine(bracketColor, Offset(pad, pad), Offset(pad + arm, pad), bracketStroke.width)
    // Top-right
    drawLine(bracketColor, Offset(w - pad - arm, pad), Offset(w - pad, pad), bracketStroke.width)
    drawLine(bracketColor, Offset(w - pad, pad), Offset(w - pad, pad + arm), bracketStroke.width)
    // Bottom-left
    drawLine(bracketColor, Offset(pad, h - pad - arm), Offset(pad, h - pad), bracketStroke.width)
    drawLine(bracketColor, Offset(pad, h - pad), Offset(pad + arm, h - pad), bracketStroke.width)
    // Bottom-right
    drawLine(bracketColor, Offset(w - pad - arm, h - pad), Offset(w - pad, h - pad), bracketStroke.width)
    drawLine(bracketColor, Offset(w - pad, h - pad - arm), Offset(w - pad, h - pad), bracketStroke.width)

    // Crisp wireframe diamond reticle
    val diamond = Path().apply {
        moveTo(w * 0.50f, h * 0.13f)
        lineTo(w * 0.87f, h * 0.50f)
        lineTo(w * 0.50f, h * 0.87f)
        lineTo(w * 0.13f, h * 0.50f)
        close()
    }
    drawPath(
        path = diamond,
        color = accent,
        style = Stroke(width = w * 0.05f, join = StrokeJoin.Miter)
    )

    // Ultra-crisp wireframe telemetry pulse
    val wave = Path().apply {
        moveTo(w * 0.25f, h * 0.50f)
        lineTo(w * 0.39f, h * 0.50f)
        lineTo(w * 0.46f, h * 0.32f)
        lineTo(w * 0.54f, h * 0.68f)
        lineTo(w * 0.61f, h * 0.50f)
        lineTo(w * 0.75f, h * 0.50f)
    }
    drawPath(
        path = wave,
        color = Color.White,
        style = Stroke(width = w * 0.055f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawFrutigerAeroEmblem() {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val cy = h * 0.5f
    val r = w * 0.46f

    // 3D Glossy Aqua Glass Sphere Base
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFF7CE8FF),
                0.52f to Color(0xFF00A8F0),
                0.86f to Color(0xFF0068B5),
                1.0f to Color(0xFF004C8C)
            ),
            center = Offset(cx * 0.92f, cy * 0.80f),
            radius = r * 1.15f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
    // Fresh Aero leaf-green lower inner glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x994ADE80), Color.Transparent),
            center = Offset(cx * 1.05f, cy * 1.48f),
            radius = r * 0.85f
        ),
        radius = r,
        center = Offset(cx, cy)
    )
    // Iconic Y2K Curved Upper Gel Specular Dome
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.88f), Color.White.copy(alpha = 0.08f))
        ),
        topLeft = Offset(cx - r * 0.68f, cy - r * 0.88f),
        size = Size(r * 1.36f, r * 0.82f)
    )
    // Luminous White + Mint Aero Pulse Wave inside glass orb
    val pulse = Path().apply {
        moveTo(w * 0.22f, h * 0.55f)
        lineTo(w * 0.37f, h * 0.55f)
        lineTo(w * 0.46f, h * 0.34f)
        lineTo(w * 0.55f, h * 0.69f)
        lineTo(w * 0.64f, h * 0.49f)
        lineTo(w * 0.78f, h * 0.49f)
    }
    drawPath(
        path = pulse,
        color = Color(0xFF86EFAC).copy(alpha = 0.65f),
        style = Stroke(width = w * 0.13f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = pulse,
        color = Color.White,
        style = Stroke(width = w * 0.072f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    // Tiny glossy bubble glint
    drawCircle(
        color = Color.White.copy(alpha = 0.92f),
        radius = w * 0.055f,
        center = Offset(w * 0.72f, h * 0.29f)
    )
    // Crystal white outer glass rim
    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = w * 0.048f)
    )
}

@Composable
fun ThemeBrandWordmark(
    theme: UiTheme,
    accentColor: Color,
    fontSize: TextUnit = 21.sp,
    modifier: Modifier = Modifier
) {
    val metallicBrush = remember {
        Brush.verticalGradient(colors = listOf(Color(0xFFFFFFFF), Color(0xFFAEB8C6)))
    }
    val aeroOceanBrush = remember {
        Brush.verticalGradient(colors = listOf(Color(0xFF062A4D), Color(0xFF005A99)))
    }
    val aeroAquaLeafBrush = remember {
        Brush.horizontalGradient(colors = listOf(Color(0xFF006EB8), Color(0xFF0284A8), Color(0xFF15803D)))
    }

    val fontPack = remember(theme) { com.fathan.ironpulse.ui.theme.resolveThemeFontPack(theme) }
    val text = when (theme) {
        UiTheme.MATERIAL_YOU -> buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xFFF4F1F8), fontFamily = fontPack.displayFont, fontWeight = FontWeight.Bold)) {
                append("Iron")
            }
            withStyle(SpanStyle(color = accentColor, fontFamily = fontPack.displayFont, fontWeight = FontWeight.ExtraBold)) {
                append("Pulse")
            }
        }
        UiTheme.CARBON_FORGE -> buildAnnotatedString {
            withStyle(SpanStyle(brush = metallicBrush, fontFamily = fontPack.displayFont, fontWeight = FontWeight.Black, letterSpacing = 1.0.sp)) {
                append("IRON")
            }
            withStyle(SpanStyle(color = accentColor, fontFamily = fontPack.displayFont, fontWeight = FontWeight.Black, letterSpacing = 1.0.sp)) {
                append("PULSE")
            }
        }
        UiTheme.UBUNTU_YARU -> buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xFFF6F5F4), fontFamily = fontPack.displayFont, fontWeight = FontWeight.Medium, letterSpacing = (-0.2).sp)) {
                append("iron")
            }
            withStyle(SpanStyle(color = accentColor, fontFamily = fontPack.displayFont, fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp)) {
                append("pulse")
            }
        }
        UiTheme.AMOLED_STEALTH -> buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = Color(0xFFFFFFFF),
                    fontFamily = fontPack.displayFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.25.sp
                )
            ) {
                append("IRON")
            }
            withStyle(
                SpanStyle(
                    color = accentColor,
                    fontFamily = fontPack.displayFont,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.25.sp
                )
            ) {
                append("_PULSE")
            }
        }
        UiTheme.FRUTIGER_AERO -> buildAnnotatedString {
            withStyle(
                SpanStyle(
                    brush = aeroOceanBrush,
                    fontFamily = fontPack.displayFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = (-0.4).sp
                )
            ) {
                append("Iron")
            }
            withStyle(
                SpanStyle(
                    brush = aeroAquaLeafBrush,
                    fontFamily = fontPack.displayFont,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.3).sp
                )
            ) {
                append("Pulse")
            }
        }
    }

    val effectiveSize = when (theme) {
        UiTheme.CARBON_FORGE -> (fontSize.value * 0.90f).sp
        UiTheme.AMOLED_STEALTH -> (fontSize.value * 0.84f).sp
        else -> fontSize
    }

    Text(
        text = text,
        fontSize = effectiveSize,
        fontFamily = fontPack.displayFont,
        maxLines = 1,
        modifier = modifier.testTag("theme_brand_wordmark_${theme.wireValue}")
    )
}

@Composable
fun ThemeBrandLockup(
    theme: UiTheme,
    accentColor: Color,
    emblemSize: Dp = 30.dp,
    fontSize: TextUnit = 21.sp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val baseMod = modifier
        .clip(RoundedCornerShape(10.dp))
        .then(
            if (onClick != null) {
                Modifier
                    .tactilePress(disabledByPerf = tokens.performance.noAnimations)
                    .clickable(onClick = onClick)
            } else {
                Modifier
            }
        )
        .padding(vertical = 3.dp, horizontal = 2.dp)
        .testTag("theme_brand_lockup_${theme.wireValue}")

    Row(
        modifier = baseMod,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeBrandEmblem(theme = theme, accentOverride = accentColor, size = emblemSize)
        ThemeBrandWordmark(theme = theme, accentColor = accentColor, fontSize = fontSize)
    }
}

@Composable
fun ThemedPhaseLogoEmblem(
    timePhase: DayTimePhase,
    theme: UiTheme,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val accent = tokens.phaseTokens.accentBright
    val softAccent = tokens.phaseTokens.accent

    val containerShape = remember(theme) {
        when (theme) {
            UiTheme.MATERIAL_YOU -> RoundedCornerShape(18.dp)
            UiTheme.CARBON_FORGE -> RoundedCornerShape(8.dp)
            UiTheme.UBUNTU_YARU -> RoundedCornerShape(13.dp)
            UiTheme.AMOLED_STEALTH -> RoundedCornerShape(12.dp)
            UiTheme.FRUTIGER_AERO -> CircleShape
        }
    }

    val containerBg = when (theme) {
        UiTheme.MATERIAL_YOU -> softAccent.copy(alpha = 0.16f)
        UiTheme.CARBON_FORGE -> Color(0xFF141820)
        UiTheme.UBUNTU_YARU -> Color(0xFF2D272B)
        UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
        UiTheme.FRUTIGER_AERO -> Color.Transparent
    }
    val containerBorder = when (theme) {
        UiTheme.MATERIAL_YOU -> softAccent.copy(alpha = 0.25f)
        UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.22f)
        UiTheme.UBUNTU_YARU -> accent.copy(alpha = 0.45f)
        UiTheme.AMOLED_STEALTH -> accent.copy(alpha = 0.72f)
        UiTheme.FRUTIGER_AERO -> Color.White.copy(alpha = 0.90f)
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(containerShape)
            .background(containerBg, containerShape)
            .border(1.dp, containerBorder, containerShape)
            .testTag("phase_emblem_${theme.wireValue}_${timePhase.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(if (theme == UiTheme.FRUTIGER_AERO) 52.dp else 32.dp)) {
            when (theme) {
                UiTheme.MATERIAL_YOU -> drawMaterialYouPhase(timePhase, accent, softAccent)
                UiTheme.CARBON_FORGE -> drawCarbonForgePhase(timePhase, accent, softAccent)
                UiTheme.UBUNTU_YARU -> drawUbuntuYaruPhase(timePhase, accent, softAccent)
                UiTheme.AMOLED_STEALTH -> drawAmoledStealthPhase(timePhase, accent)
                UiTheme.FRUTIGER_AERO -> drawFrutigerAeroPhase(timePhase)
            }
        }
    }
}

private fun DrawScope.drawMaterialYouPhase(phase: DayTimePhase, accent: Color, soft: Color) {
    val w = size.width
    val h = size.height
    when (phase) {
        DayTimePhase.MORNING -> {
            val cx = w * 0.5f
            val hy = h * 0.72f
            val r = w * 0.27f
            drawArc(soft.copy(alpha = 0.35f), 180f, 180f, true, Offset(cx - r * 1.3f, hy - r * 1.3f), Size(r * 2.6f, r * 2.6f))
            drawArc(accent, 180f, 180f, true, Offset(cx - r, hy - r), Size(r * 2f, r * 2f))
            drawLine(soft, Offset(w * 0.14f, hy + 2.5.dp.toPx()), Offset(w * 0.86f, hy + 2.5.dp.toPx()), 3.dp.toPx(), cap = StrokeCap.Round)
        }
        DayTimePhase.AFTERNOON -> {
            val cx = w * 0.5f
            val cy = h * 0.5f
            // M3 scalloped sun petals
            for (i in 0 until 8) {
                val a = Math.toRadians((i * 45).toDouble())
                drawCircle(
                    color = soft.copy(alpha = 0.42f),
                    radius = w * 0.11f,
                    center = Offset((cx + w * 0.28f * cos(a)).toFloat(), (cy + w * 0.28f * sin(a)).toFloat())
                )
            }
            drawCircle(color = accent, radius = w * 0.24f, center = Offset(cx, cy))
        }
        DayTimePhase.NIGHT -> {
            drawCircle(color = soft.copy(alpha = 0.22f), radius = w * 0.38f, center = Offset(w * 0.48f, h * 0.52f))
            drawCrescentPath(accent)
            drawCircle(color = accent, radius = 2.4.dp.toPx(), center = Offset(w * 0.75f, h * 0.27f))
        }
    }
}

private fun DrawScope.drawCarbonForgePhase(phase: DayTimePhase, accent: Color, soft: Color) {
    val w = size.width
    val h = size.height
    when (phase) {
        DayTimePhase.MORNING -> {
            // Precision horizon grid + sharp semi-hexagon rising sun
            val rise = Path().apply {
                moveTo(w * 0.22f, h * 0.66f)
                lineTo(w * 0.33f, h * 0.34f)
                lineTo(w * 0.67f, h * 0.34f)
                lineTo(w * 0.78f, h * 0.66f)
                close()
            }
            drawPath(rise, color = accent)
            drawLine(Color(0xFFE2E8F0), Offset(w * 0.10f, h * 0.72f), Offset(w * 0.90f, h * 0.72f), 2.2.dp.toPx())
            drawLine(soft.copy(alpha = 0.7f), Offset(w * 0.24f, h * 0.84f), Offset(w * 0.76f, h * 0.84f), 1.8.dp.toPx())
        }
        DayTimePhase.AFTERNOON -> {
            val cx = w * 0.5f
            val cy = h * 0.5f
            // Octagonal compass blade sun
            for (i in 0 until 8) {
                val a = Math.toRadians((i * 45).toDouble())
                val r1 = w * 0.28f
                val r2 = if (i % 2 == 0) w * 0.46f else w * 0.38f
                drawLine(
                    color = if (i % 2 == 0) accent else Color(0xFFCBD5E1),
                    start = Offset((cx + r1 * cos(a)).toFloat(), (cy + r1 * sin(a)).toFloat()),
                    end = Offset((cx + r2 * cos(a)).toFloat(), (cy + r2 * sin(a)).toFloat()),
                    strokeWidth = 2.2.dp.toPx()
                )
            }
            val core = Path().apply {
                moveTo(cx, cy - w * 0.21f)
                lineTo(cx + w * 0.21f, cy)
                lineTo(cx, cy + w * 0.21f)
                lineTo(cx - w * 0.21f, cy)
                close()
            }
            drawPath(core, color = accent)
        }
        DayTimePhase.NIGHT -> {
            // Faceted geometric angular moon
            val geoMoon = Path().apply {
                moveTo(w * 0.60f, h * 0.14f)
                lineTo(w * 0.28f, h * 0.30f)
                lineTo(w * 0.22f, h * 0.64f)
                lineTo(w * 0.50f, h * 0.86f)
                lineTo(w * 0.82f, h * 0.72f)
                lineTo(w * 0.54f, h * 0.64f)
                lineTo(w * 0.46f, h * 0.38f)
                close()
            }
            drawPath(geoMoon, color = accent)
            drawRect(Color(0xFFE2E8F0), topLeft = Offset(w * 0.70f, h * 0.22f), size = Size(4.dp.toPx(), 4.dp.toPx()))
        }
    }
}

private fun DrawScope.drawUbuntuYaruPhase(phase: DayTimePhase, accent: Color, soft: Color) {
    val w = size.width
    val h = size.height
    when (phase) {
        DayTimePhase.MORNING -> {
            val cx = w * 0.5f
            val hy = h * 0.70f
            val r = w * 0.24f
            drawArc(accent, 180f, 180f, false, Offset(cx - r, hy - r), Size(r * 2f, r * 2f), style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
            drawLine(Color(0xFFF6F5F4), Offset(w * 0.16f, hy + 2.dp.toPx()), Offset(w * 0.84f, hy + 2.dp.toPx()), 2.6.dp.toPx(), cap = StrokeCap.Round)
            listOf(-45.0, 0.0, 45.0).forEach { deg ->
                val rad = Math.toRadians(deg - 90.0)
                drawCircle(soft, 2.2.dp.toPx(), Offset((cx + w * 0.38f * cos(rad)).toFloat(), (hy + w * 0.38f * sin(rad)).toFloat()))
            }
        }
        DayTimePhase.AFTERNOON -> {
            val cx = w * 0.5f
            val cy = h * 0.5f
            drawCircle(accent, w * 0.18f, Offset(cx, cy))
            drawCircle(Color(0xFFF6F5F4).copy(alpha = 0.85f), w * 0.31f, Offset(cx, cy), style = Stroke(2.2.dp.toPx()))
            listOf(-90.0, 30.0, 150.0).forEach { deg ->
                val rad = Math.toRadians(deg)
                drawCircle(accent, 2.6.dp.toPx(), Offset((cx + w * 0.31f * cos(rad)).toFloat(), (cy + w * 0.31f * sin(rad)).toFloat()))
            }
        }
        DayTimePhase.NIGHT -> {
            drawCrescentPath(accent)
            drawCircle(Color(0xFFF6F5F4), 2.2.dp.toPx(), Offset(w * 0.74f, h * 0.28f))
            drawCircle(soft, 1.5.dp.toPx(), Offset(w * 0.80f, h * 0.44f))
        }
    }
}

private fun DrawScope.drawAmoledStealthPhase(phase: DayTimePhase, accent: Color) {
    val w = size.width
    val h = size.height
    val wire = Stroke(width = 1.9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (phase) {
        DayTimePhase.MORNING -> {
            val cx = w * 0.5f
            val hy = h * 0.72f
            val r = w * 0.25f
            drawArc(accent, 180f, 180f, false, Offset(cx - r, hy - r), Size(r * 2f, r * 2f), style = wire)
            drawLine(Color.White.copy(alpha = 0.85f), Offset(w * 0.14f, hy), Offset(w * 0.86f, hy), 1.8.dp.toPx(), cap = StrokeCap.Round)
            drawLine(accent, Offset(cx, h * 0.18f), Offset(cx, h * 0.30f), 1.8.dp.toPx(), cap = StrokeCap.Round)
        }
        DayTimePhase.AFTERNOON -> {
            val cx = w * 0.5f
            val cy = h * 0.5f
            drawCircle(accent, w * 0.20f, Offset(cx, cy), style = wire)
            for (i in 0 until 4) {
                val a = Math.toRadians((i * 90).toDouble())
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset((cx + w * 0.29f * cos(a)).toFloat(), (cy + w * 0.29f * sin(a)).toFloat()),
                    end = Offset((cx + w * 0.43f * cos(a)).toFloat(), (cy + w * 0.43f * sin(a)).toFloat()),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
        DayTimePhase.NIGHT -> {
            val moonPath = buildCrescentPath()
            drawPath(moonPath, color = accent, style = wire)
            drawLine(Color.White, Offset(w * 0.74f, h * 0.20f), Offset(w * 0.74f, h * 0.34f), 1.5.dp.toPx(), cap = StrokeCap.Round)
            drawLine(Color.White, Offset(w * 0.67f, h * 0.27f), Offset(w * 0.81f, h * 0.27f), 1.5.dp.toPx(), cap = StrokeCap.Round)
        }
    }
}

private fun DrawScope.drawFrutigerAeroPhase(phase: DayTimePhase) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val cy = h * 0.5f
    val r = w * 0.48f

    // 3D Glossy Sky Weather Orb per phase
    val sphereBrush = when (phase) {
        DayTimePhase.MORNING -> Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFFFFF4B8),
                0.48f to Color(0xFF4CC9F0),
                0.88f to Color(0xFF0088D1),
                1.0f to Color(0xFF005F99)
            ),
            center = Offset(cx * 0.85f, cy * 0.78f),
            radius = r * 1.15f
        )
        DayTimePhase.AFTERNOON -> Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFFA6F2FF),
                0.50f to Color(0xFF00A8F0),
                0.88f to Color(0xFF0072C6),
                1.0f to Color(0xFF004F91)
            ),
            center = Offset(cx * 0.85f, cy * 0.75f),
            radius = r * 1.15f
        )
        DayTimePhase.NIGHT -> Brush.radialGradient(
            colorStops = arrayOf(
                0.0f to Color(0xFF60A5FA),
                0.55f to Color(0xFF1D4ED8),
                0.90f to Color(0xFF1E3A8A),
                1.0f to Color(0xFF0F172A)
            ),
            center = Offset(cx * 0.88f, cy * 0.76f),
            radius = r * 1.15f
        )
    }
    drawCircle(brush = sphereBrush, radius = r, center = Offset(cx, cy))

    // Lower fresh green/aqua wave bounce inside glass orb
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                if (phase == DayTimePhase.NIGHT) Color(0x6638BDF8) else Color(0x884ADE80),
                Color.Transparent
            ),
            center = Offset(cx, cy * 1.55f),
            radius = r * 0.85f
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // Phase icon inside glossy sphere
    when (phase) {
        DayTimePhase.MORNING -> {
            drawCircle(Color(0xFFFFE066), w * 0.16f, Offset(cx, h * 0.56f))
            val wave = Path().apply {
                moveTo(w * 0.14f, h * 0.67f)
                quadraticBezierTo(w * 0.34f, h * 0.59f, w * 0.52f, h * 0.66f)
                quadraticBezierTo(w * 0.70f, h * 0.73f, w * 0.86f, h * 0.64f)
            }
            drawPath(wave, Color.White.copy(alpha = 0.92f), style = Stroke(2.4.dp.toPx(), cap = StrokeCap.Round))
        }
        DayTimePhase.AFTERNOON -> {
            drawCircle(Color(0xFFFFF59D), w * 0.17f, Offset(cx, cy))
            drawCircle(Color.White.copy(alpha = 0.55f), w * 0.25f, Offset(cx, cy), style = Stroke(1.8.dp.toPx()))
            drawCircle(Color.White.copy(alpha = 0.72f), w * 0.06f, Offset(w * 0.72f, h * 0.68f))
        }
        DayTimePhase.NIGHT -> {
            val moon = Path().apply {
                moveTo(w * 0.56f, h * 0.24f)
                cubicTo(w * 0.35f, h * 0.26f, w * 0.26f, h * 0.52f, w * 0.40f, h * 0.70f)
                cubicTo(w * 0.51f, h * 0.82f, w * 0.71f, h * 0.78f, w * 0.77f, h * 0.64f)
                cubicTo(w * 0.58f, h * 0.69f, w * 0.46f, h * 0.55f, w * 0.49f, h * 0.39f)
                close()
            }
            drawPath(moon, Color(0xFFFEF08A))
            drawCircle(Color.White, 2.2.dp.toPx(), Offset(w * 0.70f, h * 0.34f))
        }
    }

    // Classic Y2K top glossy gel arc reflection
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.82f), Color.White.copy(alpha = 0.06f))
        ),
        topLeft = Offset(cx - r * 0.66f, cy - r * 0.86f),
        size = Size(r * 1.32f, r * 0.76f)
    )
}

private fun DrawScope.buildCrescentPath(): Path {
    val w = size.width
    val h = size.height
    return Path().apply {
        moveTo(w * 0.58f, h * 0.16f)
        cubicTo(w * 0.31f, h * 0.18f, w * 0.20f, h * 0.52f, w * 0.38f, h * 0.75f)
        cubicTo(w * 0.52f, h * 0.90f, w * 0.77f, h * 0.84f, w * 0.84f, h * 0.67f)
        cubicTo(w * 0.61f, h * 0.73f, w * 0.45f, h * 0.55f, w * 0.49f, h * 0.35f)
        cubicTo(w * 0.51f, h * 0.26f, w * 0.55f, h * 0.20f, w * 0.58f, h * 0.16f)
        close()
    }
}

private fun DrawScope.drawCrescentPath(color: Color) {
    drawPath(buildCrescentPath(), color = color)
}
