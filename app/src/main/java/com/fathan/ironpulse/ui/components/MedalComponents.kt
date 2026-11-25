package com.fathan.ironpulse.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.MedalTier
import com.fathan.ironpulse.model.MedalUnlockEvent
import com.fathan.ironpulse.model.MonthlyMedalSummary
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private data class MedalVisualPalette(
    val faceLight: Color,
    val faceMid: Color,
    val faceDeep: Color,
    val rimHighlight: Color,
    val rimShadow: Color,
    val ribbonTop: Color,
    val ribbonBottom: Color,
    val crestTint: Color,
    val auraGlow: Color
)

private fun resolveMedalPalette(tier: MedalTier): MedalVisualPalette = when (tier) {
    MedalTier.BRONZE -> MedalVisualPalette(
        faceLight = Color(0xFFFAD0AE),
        faceMid = Color(0xFFCD7F32),
        faceDeep = Color(0xFF7B3B13),
        rimHighlight = Color(0xFFFFE5CC),
        rimShadow = Color(0xFF5D2A0C),
        ribbonTop = Color(0xFFF59E0B),
        ribbonBottom = Color(0xFFB45309),
        crestTint = Color(0xFFFFEDD5),
        auraGlow = Color(0xFFF59E0B)
    )
    MedalTier.SILVER -> MedalVisualPalette(
        faceLight = Color(0xFFFFFFFF),
        faceMid = Color(0xFFCBD5E1),
        faceDeep = Color(0xFF64748B),
        rimHighlight = Color(0xFFF8FAFC),
        rimShadow = Color(0xFF334155),
        ribbonTop = Color(0xFF38BDF8),
        ribbonBottom = Color(0xFF1D4ED8),
        crestTint = Color(0xFFFFFFFF),
        auraGlow = Color(0xFF7DD3FC)
    )
    MedalTier.GOLD -> MedalVisualPalette(
        faceLight = Color(0xFFFEF9C3),
        faceMid = Color(0xFFFACC15),
        faceDeep = Color(0xFFA16207),
        rimHighlight = Color(0xFFFFFFFF),
        rimShadow = Color(0xFF713F12),
        ribbonTop = Color(0xFFFB7185),
        ribbonBottom = Color(0xFFBE123C),
        crestTint = Color(0xFFFFFBEB),
        auraGlow = Color(0xFFFDE047)
    )
    MedalTier.NONE -> MedalVisualPalette(
        faceLight = Color(0xFF64748B),
        faceMid = Color(0xFF475569),
        faceDeep = Color(0xFF1E293B),
        rimHighlight = Color(0xFF94A3B8),
        rimShadow = Color(0xFF0F172A),
        ribbonTop = Color(0xFF475569),
        ribbonBottom = Color(0xFF334155),
        crestTint = Color(0xFF94A3B8),
        auraGlow = Color(0xFF64748B)
    )
}

@Composable
fun ProceduralMedalEmblem(
    tier: MedalTier,
    modifier: Modifier = Modifier,
    size: Dp = 84.dp,
    shineEnabled: Boolean = true,
    landingBurstTick: Int = 0
) {
    val tokens = LocalIronPulseTokens.current
    val tabActive = LocalTabActive.current
    val noAnim = tokens.performance.noAnimations
    val suppressGlow = tokens.performance.noAmbientGlow
    val runShine = shineEnabled && tier != MedalTier.NONE && !noAnim && tabActive

    val staticSweep = remember { mutableFloatStateOf(0.35f) }
    val staticZero = remember { mutableFloatStateOf(0f) }

    val sweepState: State<Float>
    val pulseState: State<Float>
    val rayAngleState: State<Float>
    if (runShine) {
        val infinite = rememberInfiniteTransition(label = "medalShineInfinite")
        sweepState = infinite.animateFloat(
            initialValue = -0.35f,
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "medalSweep"
        )
        pulseState = infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "medalPulse"
        )
        rayAngleState = infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 14000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "medalRays"
        )
    } else {
        sweepState = staticSweep
        pulseState = staticZero
        rayAngleState = staticZero
    }

    val burstAnim = remember { Animatable(0f) }
    LaunchedEffect(landingBurstTick) {
        if (landingBurstTick > 0 && !noAnim) {
            burstAnim.snapTo(0.01f)
            burstAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 780, easing = FastOutSlowInEasing)
            )
            burstAnim.snapTo(0f)
        }
    }

    val pal = remember(tier) { resolveMedalPalette(tier) }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val minDim = minOf(w, h)
            val center = Offset(w * 0.5f, h * 0.56f)
            val coinR = minDim * 0.37f
            val sweep = sweepState.value
            val pulse = pulseState.value
            val burst = burstAnim.value

            if (tier == MedalTier.NONE) {
                // Locked/in-progress silhouette medal
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            tokens.accentColor.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = coinR * 1.45f
                    ),
                    radius = coinR * 1.45f,
                    center = center
                )
                drawCircle(
                    color = tokens.wellBg,
                    radius = coinR,
                    center = center
                )
                drawCircle(
                    color = tokens.subtleBorderColor.copy(alpha = 0.7f),
                    radius = coinR,
                    center = center,
                    style = Stroke(
                        width = 1.6.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    )
                )
                drawStarCrest(
                    center = center,
                    outerR = coinR * 0.44f,
                    innerR = coinR * 0.20f,
                    color = tokens.mutedTextColor.copy(alpha = 0.35f)
                )
                return@Canvas
            }

            // 1. Soft breathing glow aura behind medal
            if (!suppressGlow) {
                val auraR = coinR * (1.58f + pulse * 0.14f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            pal.auraGlow.copy(alpha = 0.34f + pulse * 0.14f),
                            pal.auraGlow.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = auraR
                    ),
                    radius = auraR,
                    center = center
                )

                // Rotating subtle light rays
                rotate(degrees = rayAngleState.value, pivot = center) {
                    val rayCount = 8
                    for (i in 0 until rayCount) {
                        val angleRad = (i * (360f / rayCount)) * (PI / 180.0)
                        val rInner = coinR * 1.04f
                        val rOuter = coinR * (1.42f + (i % 2) * 0.14f)
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    pal.rimHighlight.copy(alpha = 0.28f + pulse * 0.14f),
                                    Color.Transparent
                                ),
                                start = Offset(
                                    center.x + (cos(angleRad) * rInner).toFloat(),
                                    center.y + (sin(angleRad) * rInner).toFloat()
                                ),
                                end = Offset(
                                    center.x + (cos(angleRad) * rOuter).toFloat(),
                                    center.y + (sin(angleRad) * rOuter).toFloat()
                                )
                            ),
                            start = Offset(
                                center.x + (cos(angleRad) * rInner).toFloat(),
                                center.y + (sin(angleRad) * rInner).toFloat()
                            ),
                            end = Offset(
                                center.x + (cos(angleRad) * rOuter).toFloat(),
                                center.y + (sin(angleRad) * rOuter).toFloat()
                            ),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // 2. Top V-shaped Championship Ribbon Clasp
            val ribbonLeft = Path().apply {
                moveTo(center.x - coinR * 0.56f, center.y - coinR * 1.38f)
                lineTo(center.x - coinR * 0.12f, center.y - coinR * 1.38f)
                lineTo(center.x + coinR * 0.04f, center.y - coinR * 0.72f)
                lineTo(center.x - coinR * 0.28f, center.y - coinR * 0.72f)
                close()
            }
            val ribbonRight = Path().apply {
                moveTo(center.x + coinR * 0.12f, center.y - coinR * 1.38f)
                lineTo(center.x + coinR * 0.56f, center.y - coinR * 1.38f)
                lineTo(center.x + coinR * 0.28f, center.y - coinR * 0.72f)
                lineTo(center.x - coinR * 0.04f, center.y - coinR * 0.72f)
                close()
            }
            val ribbonBrush = Brush.verticalGradient(
                colors = listOf(pal.ribbonTop, pal.ribbonBottom),
                startY = center.y - coinR * 1.38f,
                endY = center.y - coinR * 0.72f
            )
            drawPath(ribbonLeft, ribbonBrush)
            drawPath(ribbonRight, ribbonBrush)

            // Metallic clasp ring connecting ribbon to coin
            drawCircle(
                color = pal.rimHighlight,
                radius = coinR * 0.15f,
                center = Offset(center.x, center.y - coinR * 0.98f),
                style = Stroke(width = 2.2.dp.toPx())
            )

            // 3. Outer Beveled Coin Body & Inner Metallic Disc
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(pal.rimHighlight, pal.faceMid, pal.rimShadow),
                    start = Offset(center.x - coinR, center.y - coinR),
                    end = Offset(center.x + coinR, center.y + coinR)
                ),
                radius = coinR,
                center = center
            )

            val innerR = coinR * 0.80f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(pal.faceLight, pal.faceMid, pal.faceDeep),
                    center = Offset(center.x - innerR * 0.28f, center.y - innerR * 0.30f),
                    radius = innerR * 1.45f
                ),
                radius = innerR,
                center = center
            )

            // Engraved concentric ring & rim highlight
            drawCircle(
                color = pal.rimShadow.copy(alpha = 0.45f),
                radius = innerR,
                center = center,
                style = Stroke(width = 1.4.dp.toPx())
            )
            drawCircle(
                color = pal.rimHighlight.copy(alpha = 0.55f),
                radius = coinR * 0.94f,
                center = center,
                style = Stroke(width = 1.1.dp.toPx())
            )

            // 4. Central Embossed Star + Crown/Barbell Emblem
            drawStarCrest(
                center = Offset(center.x, center.y + innerR * 0.03f),
                outerR = innerR * 0.56f,
                innerR = innerR * 0.26f,
                color = pal.rimShadow.copy(alpha = 0.35f)
            )
            drawStarCrest(
                center = Offset(center.x, center.y - innerR * 0.02f),
                outerR = innerR * 0.54f,
                innerR = innerR * 0.25f,
                color = pal.crestTint.copy(alpha = 0.94f)
            )

            // 5. Clipped Diagonal Specular Shine Sweep across Medal Face
            if (shineEnabled) {
                val coinClip = Path().apply {
                    addOval(
                        androidx.compose.ui.geometry.Rect(
                            left = center.x - coinR,
                            top = center.y - coinR,
                            right = center.x + coinR,
                            bottom = center.y + coinR
                        )
                    )
                }
                clipPath(coinClip) {
                    val bandX = (center.x - coinR * 1.3f) + (coinR * 2.6f * sweep)
                    rotate(degrees = 26f, pivot = center) {
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.68f),
                                    Color.Transparent
                                ),
                                startX = bandX - coinR * 0.28f,
                                endX = bandX + coinR * 0.28f
                            ),
                            topLeft = Offset(bandX - coinR * 0.28f, center.y - coinR * 1.5f),
                            size = Size(coinR * 0.56f, coinR * 3.0f)
                        )
                    }
                }
            }

            // 6. Twinkling rim sparkles
            if (runShine && !suppressGlow) {
                val sparkleAlpha = (0.35f + pulse * 0.65f).coerceIn(0f, 1f)
                drawFourPointSparkle(
                    center = Offset(center.x + coinR * 0.78f, center.y - coinR * 0.62f),
                    radius = coinR * (0.14f + pulse * 0.08f),
                    color = Color.White.copy(alpha = sparkleAlpha)
                )
                drawFourPointSparkle(
                    center = Offset(center.x - coinR * 0.82f, center.y + coinR * 0.56f),
                    radius = coinR * (0.11f + (1f - pulse) * 0.07f),
                    color = pal.rimHighlight.copy(alpha = 0.35f + (1f - pulse) * 0.55f)
                )
            }

            // 7. Landing Arrival Shockwave + Sparkle Burst
            if (burst > 0f) {
                val burstAlpha = (1f - burst).coerceIn(0f, 1f)
                val shockR = coinR * (1.0f + burst * 0.95f)
                drawCircle(
                    color = pal.auraGlow.copy(alpha = burstAlpha * 0.85f),
                    radius = shockR,
                    center = center,
                    style = Stroke(width = (3.6f * (1f - burst * 0.6f)).dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = burstAlpha * 0.65f),
                    radius = coinR * (0.95f + burst * 0.55f),
                    center = center,
                    style = Stroke(width = 1.6.dp.toPx())
                )
                val particleCount = 10
                for (i in 0 until particleCount) {
                    val ang = (i * (360f / particleCount) + 18f) * (PI / 180.0)
                    val dist = coinR * (0.85f + burst * 0.95f)
                    val pCenter = Offset(
                        center.x + (cos(ang) * dist).toFloat(),
                        center.y + (sin(ang) * dist).toFloat()
                    )
                    drawFourPointSparkle(
                        center = pCenter,
                        radius = coinR * 0.14f * (1f - burst * 0.5f),
                        color = (if (i % 2 == 0) Color.White else pal.auraGlow).copy(alpha = burstAlpha)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawStarCrest(
    center: Offset,
    outerR: Float,
    innerR: Float,
    color: Color
) {
    val path = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = (-90.0 + i * 36.0) * (PI / 180.0)
        val x = center.x + (cos(angle) * r).toFloat()
        val y = center.y + (sin(angle) * r).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawFourPointSparkle(
    center: Offset,
    radius: Float,
    color: Color
) {
    val inner = radius * 0.24f
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        lineTo(center.x + inner, center.y - inner)
        lineTo(center.x + radius, center.y)
        lineTo(center.x + inner, center.y + inner)
        lineTo(center.x, center.y + radius)
        lineTo(center.x - inner, center.y + inner)
        lineTo(center.x - radius, center.y)
        lineTo(center.x - inner, center.y - inner)
        close()
    }
    drawPath(path, color)
}

@Composable
fun MonthlyMedalShowcaseCard(
    summary: MonthlyMedalSummary,
    monthTitle: String,
    archiveMedals: List<MonthlyMedalSummary>,
    landingBurstTick: Int,
    onSelectArchiveMonth: (String) -> Unit,
    onMedalSlotPositioned: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val pal = remember(summary.tier) { resolveMedalPalette(summary.tier) }
    val borderAccent = if (summary.tier != MedalTier.NONE) {
        pal.auraGlow.copy(alpha = 0.48f)
    } else {
        tokens.surfaceBorderColor
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_monthly_medal_card")
            .ironGlassSurface(tokens, borderOverride = borderAccent)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coords ->
                        val b = coords.boundsInRoot()
                        onMedalSlotPositioned(Offset(b.center.x, b.center.y))
                    }
                    .testTag("history_medal_emblem_slot"),
                contentAlignment = Alignment.Center
            ) {
                ProceduralMedalEmblem(
                    tier = summary.tier,
                    size = 86.dp,
                    shineEnabled = true,
                    landingBurstTick = landingBurstTick
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = lang.t("MEDALI BULANAN", "MONTHLY MEDAL"),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        color = if (summary.tier != MedalTier.NONE) pal.auraGlow else tokens.accentTextColor
                    )
                    Text(
                        text = "· $monthTitle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.palette.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = summary.tier.title(lang),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface
                )

                Text(
                    text = when {
                        summary.nextTier != null -> lang.t(
                            "${summary.verifiedDays} hari sah · ${summary.daysToNextTier} hari lagi ke ${summary.nextTier.shortLabel(lang)}",
                            "${summary.verifiedDays} verified days · ${summary.daysToNextTier} more for ${summary.nextTier.shortLabel(lang)}"
                        )
                        else -> lang.t(
                            "${summary.verifiedDays} hari gym terverifikasi — Puncak Emas tercapai!",
                            "${summary.verifiedDays} verified gym days — Gold tier reached!"
                        )
                    },
                    fontSize = 12.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        }

        // Milestone Progress Track: 0 -> 12 (Perunggu) -> 16 (Perak) -> 20 (Emas)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val maxDays = MedalTier.GOLD.thresholdDays.toFloat()
            val progress = (summary.verifiedDays / maxDays).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(tokens.wellBg)
                    .border(1.dp, tokens.subtleBorderColor, RoundedCornerShape(999.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFCD7F32),
                                    Color(0xFFCBD5E1),
                                    Color(0xFFFACC15)
                                )
                            )
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(MedalTier.BRONZE, MedalTier.SILVER, MedalTier.GOLD).forEach { m ->
                    val reached = summary.verifiedDays >= m.thresholdDays
                    val mPal = resolveMedalPalette(m)
                    IronPulseTonalSurface(
                        shape = RoundedCornerShape(999.dp),
                        baseColor = if (reached) mPal.auraGlow.copy(alpha = 0.16f) else tokens.wellBg,
                        border = BorderStroke(
                            1.dp,
                            if (reached) mPal.auraGlow.copy(alpha = 0.55f) else tokens.subtleBorderColor
                        )
                    ) {
                        Text(
                            text = "${m.shortLabel(lang)} ${m.thresholdDays}×",
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (reached) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (reached) tokens.palette.onSurface else tokens.palette.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Anti-fake transparency hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .ironTonalControl(
                    tokens = tokens,
                    shape = RoundedCornerShape(10.dp),
                    baseColor = tokens.wellBg,
                    isAccent = false
                )
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemedIcon(
                icon = IronPulseIcon.CHECK_CIRCLE,
                contentDescription = null,
                tint = tokens.accentColor,
                size = 14.dp
            )
            Text(
                text = if (summary.shortLogDays > 0) {
                    lang.t(
                        "Syarat sah: ≥2 gerakan, ≥6 set terjeda & ≥15 mnt (${summary.shortLogDays} log singkat tidak dihitung)",
                        "Valid day: ≥2 exercises, ≥6 paced sets & ≥15m (${summary.shortLogDays} short logs excluded)"
                    )
                } else {
                    lang.t(
                        "Syarat sah: ≥2 gerakan, ≥6 set terjeda & ≥15 mnt per hari",
                        "Valid day: ≥2 exercises, ≥6 paced sets & ≥15 min/day"
                    )
                },
                fontSize = 11.sp,
                color = tokens.palette.onSurfaceVariant
            )
        }

        // Archive of earned monthly medals
        if (archiveMedals.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = lang.t("Koleksi Medali Bulanan", "Monthly Medal Collection"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 4.dp)
                ) {
                    items(archiveMedals, key = { it.monthKey }) { arch ->
                        val active = arch.monthKey == summary.monthKey
                        val aPal = resolveMedalPalette(arch.tier)
                        IronPulseTonalSurface(
                            onClick = { onSelectArchiveMonth(arch.monthKey) },
                            shape = RoundedCornerShape(12.dp),
                            baseColor = if (active) aPal.auraGlow.copy(alpha = 0.18f) else tokens.wellBg,
                            border = BorderStroke(
                                1.dp,
                                if (active) aPal.auraGlow.copy(alpha = 0.65f) else tokens.subtleBorderColor
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ProceduralMedalEmblem(
                                    tier = arch.tier,
                                    size = 24.dp,
                                    shineEnabled = false
                                )
                                Column {
                                    Text(
                                        text = formatShortMonthLabel(arch.monthKey, lang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = tokens.palette.onSurface
                                    )
                                    Text(
                                        text = "${arch.tier.shortLabel(lang)} (${arch.verifiedDays}×)",
                                        fontSize = 10.sp,
                                        color = tokens.palette.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedalUnlockCelebrationOverlay(
    event: MedalUnlockEvent,
    mascot: MascotUiState,
    mascotEyeFlow: StateFlow<MascotEyeState>,
    onStartFlightToHistory: (sourceCenter: Offset) -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val pal = remember(event.tier) { resolveMedalPalette(event.tier) }
    val sourceOffsetHolder = remember { mutableListOf(Offset.Zero) }

    val popScale = remember { Animatable(if (noAnim) 1f else 0.45f) }
    LaunchedEffect(event, noAnim) {
        if (!noAnim) {
            popScale.snapTo(0.45f)
            popScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    val confettiPhase = if (!noAnim) {
        val inf = rememberInfiniteTransition(label = "medalConfetti")
        inf.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "confettiTick"
        )
    } else {
        remember { mutableFloatStateOf(0.4f) }
    }

    val celebrateMascot = remember(mascot) {
        mascot.copy(
            urgentMood = MascotMood.CELEBRATE,
            timePhase = DayTimePhase.AFTERNOON
        )
    }

    val triggerSave = {
        onStartFlightToHistory(sourceOffsetHolder.firstOrNull() ?: Offset.Zero)
    }

    BackHandler(onBack = triggerSave)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.76f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
            .testTag("medal_unlock_celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Confetti & sparkle shower canvas behind card
        if (!tokens.performance.noAmbientGlow) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val p = confettiPhase.value
                val particles = 22
                for (i in 0 until particles) {
                    val xFrac = ((i * 37 + 13) % 100) / 100f
                    val yBase = ((i * 23) % 100) / 100f
                    val yFrac = (yBase + p) % 1f
                    val sway = sin((p * 2f * PI + i).toDouble()).toFloat() * 14.dp.toPx()
                    val pos = Offset(size.width * xFrac + sway, size.height * yFrac)
                    val col = when (i % 3) {
                        0 -> pal.auraGlow
                        1 -> Color.White
                        else -> tokens.accentColor
                    }.copy(alpha = 0.72f)
                    if (i % 2 == 0) {
                        drawFourPointSparkle(pos, (4.5f + (i % 3) * 2f).dp.toPx(), col)
                    } else {
                        drawCircle(col, radius = (3f + (i % 2) * 1.5f).dp.toPx(), center = pos)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .ironGlassSurface(tokens, borderOverride = pal.auraGlow.copy(alpha = 0.55f))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = lang.t("PENCAPAIAN BULANAN TERBUKA!", "MONTHLY MILESTONE UNLOCKED!"),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = pal.auraGlow
            )

            // Medal + Celebrating Mascot Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = popScale.value
                            scaleY = popScale.value
                        }
                        .onGloballyPositioned { coords ->
                            val b = coords.boundsInRoot()
                            sourceOffsetHolder.clear()
                            sourceOffsetHolder.add(Offset(b.center.x, b.center.y))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ProceduralMedalEmblem(
                        tier = event.tier,
                        size = 148.dp,
                        shineEnabled = true
                    )
                }

                // Pulse Mascot cheering beside the medal pedestal
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 18.dp, bottom = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MascotOrbFace(
                        mascot = celebrateMascot,
                        eyeStateFlow = mascotEyeFlow,
                        size = 68.dp,
                        animActive = true
                    )
                }
            }

            Text(
                text = event.tier.title(lang),
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tokens.palette.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = lang.t(
                    "Pulse merayakan ${event.verifiedDays} hari gym terverifikasi di ${formatShortMonthLabel(event.monthKey, lang)}! Medali ini siap disimpan ke tab Riwayat.",
                    "Pulse celebrates your ${event.verifiedDays} verified gym days in ${formatShortMonthLabel(event.monthKey, lang)}! Ready to dock in History."
                ),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = tokens.palette.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            IronPulseButton(
                onClick = triggerSave,
                shape = tokens.buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentColor,
                    contentColor = tokens.onAccentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("medal_save_to_history_button")
            ) {
                ThemedIcon(IronPulseIcon.HISTORY, contentDescription = null, tint = tokens.onAccentColor, size = 18.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lang.t("Simpan ke Tab Riwayat", "Save to History Tab"),
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun FlyingMedalOverlay(
    tier: MedalTier,
    startCenter: Offset,
    targetCenter: Offset,
    onFlightFinished: () -> Unit
) {
    val density = LocalDensity.current
    val startSizeDp = 148.dp
    val endSizeDp = 86.dp
    val progress = remember { Animatable(0f) }

    LaunchedEffect(startCenter, targetCenter) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 620, easing = FastOutSlowInEasing)
        )
        onFlightFinished()
    }

    val p = progress.value
    val curSizeDp = startSizeDp + (endSizeDp - startSizeDp) * p
    val curSizePx = with(density) { curSizeDp.toPx() }
    val resolvedTarget = if (targetCenter == Offset.Zero) {
        Offset(startCenter.x * 0.45f, with(density) { 175.dp.toPx() })
    } else {
        targetCenter
    }
    // Parabolic arc lift mid-flight
    val arcLiftPx = with(density) { 54.dp.toPx() } * sin(p * PI).toFloat()
    val curX = startCenter.x + (resolvedTarget.x - startCenter.x) * p
    val curY = startCenter.y + (resolvedTarget.y - startCenter.y) * p - arcLiftPx

    Box(modifier = Modifier.fillMaxSize()) {
        // Soft sparkle trail behind flying medal
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pal = resolveMedalPalette(tier)
            for (i in 1..4) {
                val tp = (p - i * 0.06f).coerceIn(0f, 1f)
                val tLift = 54.dp.toPx() * sin(tp * PI).toFloat()
                val tx = startCenter.x + (resolvedTarget.x - startCenter.x) * tp
                val ty = startCenter.y + (resolvedTarget.y - startCenter.y) * tp - tLift
                drawFourPointSparkle(
                    center = Offset(tx, ty),
                    radius = (11f - i * 2f).dp.toPx(),
                    color = pal.auraGlow.copy(alpha = (0.75f - i * 0.16f) * (1f - p * 0.4f))
                )
            }
        }

        ProceduralMedalEmblem(
            tier = tier,
            size = curSizeDp,
            shineEnabled = true,
            modifier = Modifier.offset {
                IntOffset(
                    x = (curX - curSizePx * 0.5f).roundToInt(),
                    y = (curY - curSizePx * 0.5f).roundToInt()
                )
            }
        )
    }
}

private fun formatShortMonthLabel(monthKey: String, lang: AppLanguage): String {
    val parts = monthKey.split("-")
    if (parts.size != 2) return monthKey
    val year = parts[0].toIntOrNull() ?: return monthKey
    val month = (parts[1].toIntOrNull() ?: return monthKey) - 1
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month.coerceIn(0, 11))
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val loc = if (lang == AppLanguage.EN) Locale.US else Locale.forLanguageTag("id-ID")
    return SimpleDateFormat("MMM yyyy", loc).format(cal.time)
}
