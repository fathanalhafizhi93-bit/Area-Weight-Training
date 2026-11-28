package com.fathan.ironpulse.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
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
import androidx.compose.material3.TextButton
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.LocalTabSettled
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
    landingBurstTick: Int = 0,
    yawDegrees: Float = 0f
) {
    val tokens = LocalIronPulseTokens.current
    val tabActive = LocalTabActive.current
    val tabSettled = LocalTabSettled.current
    val noAnim = tokens.performance.noAnimations
    val suppressGlow = tokens.performance.noAmbientGlow
    val runShine = shineEnabled && tier != MedalTier.NONE && !noAnim && tabActive && tabSettled

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
                animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "medalPulse"
        )
        rayAngleState = infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 15000, easing = LinearEasing),
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
            val center = Offset(w * 0.5f, h * 0.58f)
            val coinR = minDim * 0.36f
            val sweep = sweepState.value
            val pulse = pulseState.value
            val burst = burstAnim.value

            if (tier == MedalTier.NONE) {
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
                drawFacetedStarCrest(
                    center = center,
                    outerR = coinR * 0.44f,
                    innerR = coinR * 0.20f,
                    lightColor = tokens.mutedTextColor.copy(alpha = 0.35f),
                    shadowColor = tokens.mutedTextColor.copy(alpha = 0.20f)
                )
                return@Canvas
            }

            // 1. Soft aura & refined ambient rays behind the sculpted 3D coin
            if (!suppressGlow) {
                val auraR = coinR * (1.62f + pulse * 0.14f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            pal.auraGlow.copy(alpha = 0.30f + pulse * 0.12f),
                            pal.auraGlow.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = auraR
                    ),
                    radius = auraR,
                    center = center
                )

                rotate(degrees = rayAngleState.value, pivot = center) {
                    val rayCount = 8
                    for (i in 0 until rayCount) {
                        val angleRad = (i * (360f / rayCount)) * (PI / 180.0)
                        val rInner = coinR * 1.06f
                        val rOuter = coinR * (1.42f + (i % 2) * 0.16f)
                        val startPt = Offset(
                            center.x + (cos(angleRad) * rInner).toFloat(),
                            center.y + (sin(angleRad) * rInner).toFloat()
                        )
                        val endPt = Offset(
                            center.x + (cos(angleRad) * rOuter).toFloat(),
                            center.y + (sin(angleRad) * rOuter).toFloat()
                        )
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    pal.rimHighlight.copy(alpha = 0.24f + pulse * 0.12f),
                                    Color.Transparent
                                ),
                                start = startPt,
                                end = endPt
                            ),
                            start = startPt,
                            end = endPt,
                            strokeWidth = 1.6.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // Compute 3D yaw projection & extruded coin thickness
            val normYaw = ((yawDegrees % 360f) + 360f) % 360f
            val radYaw = normYaw * (PI / 180.0)
            val cosYaw = cos(radYaw).toFloat()
            val sinYaw = sin(radYaw).toFloat()
            val faceScaleX = kotlin.math.abs(cosYaw).coerceAtLeast(0.055f)
            val isFrontFacing = cosYaw >= 0f
            val maxDepthPx = coinR * 0.24f
            val visibleDepthPx = maxDepthPx * kotlin.math.abs(sinYaw)
            // Direction of the hidden/back side relative to center along X
            val edgeDir = if (sinYaw >= 0f) -1f else 1f
            val frontShiftX = -edgeDir * (visibleDepthPx * 0.5f)
            val backShiftX = edgeDir * (visibleDepthPx * 0.5f)

            // 2. Layered V-Fold Championship Ribbon & Metallic Clasp Bar
            val claspCenterY = center.y - coinR * 0.96f
            val ribbonTopY = center.y - coinR * 1.54f
            val ribbonFoldY = center.y - coinR * 0.94f

            // Back under-fold ribbon wing for stacked 3D depth
            val backRibbon = Path().apply {
                moveTo(center.x - coinR * 0.62f, ribbonTopY + coinR * 0.06f)
                lineTo(center.x + coinR * 0.62f, ribbonTopY + coinR * 0.06f)
                lineTo(center.x + coinR * 0.34f, ribbonFoldY + coinR * 0.08f)
                lineTo(center.x - coinR * 0.34f, ribbonFoldY + coinR * 0.08f)
                close()
            }
            drawPath(backRibbon, pal.ribbonBottom.copy(alpha = 0.72f))

            val ribbonLeft = Path().apply {
                moveTo(center.x - coinR * 0.58f, ribbonTopY)
                lineTo(center.x - coinR * 0.10f, ribbonTopY)
                lineTo(center.x + coinR * 0.14f, ribbonFoldY)
                lineTo(center.x - coinR * 0.30f, ribbonFoldY)
                close()
            }
            val ribbonRight = Path().apply {
                moveTo(center.x + coinR * 0.10f, ribbonTopY)
                lineTo(center.x + coinR * 0.58f, ribbonTopY)
                lineTo(center.x + coinR * 0.30f, ribbonFoldY)
                lineTo(center.x - coinR * 0.14f, ribbonFoldY)
                close()
            }
            val ribbonLeftBrush = Brush.verticalGradient(
                colors = listOf(pal.ribbonTop, pal.ribbonBottom),
                startY = ribbonTopY,
                endY = ribbonFoldY
            )
            val ribbonRightBrush = Brush.verticalGradient(
                colors = listOf(pal.ribbonTop.copy(alpha = 0.96f), pal.ribbonBottom),
                startY = ribbonTopY,
                endY = ribbonFoldY
            )
            drawPath(ribbonLeft, ribbonLeftBrush)
            // Subtle crisp gold/ivory center stripe on left fold
            drawLine(
                color = pal.rimHighlight.copy(alpha = 0.48f),
                start = Offset(center.x - coinR * 0.34f, ribbonTopY + coinR * 0.03f),
                end = Offset(center.x - coinR * 0.08f, ribbonFoldY - coinR * 0.03f),
                strokeWidth = (coinR * 0.07f).coerceAtLeast(1.dp.toPx()),
                cap = StrokeCap.Butt
            )
            // Fold overlap shadow before drawing right wing over left wing
            drawLine(
                color = Color.Black.copy(alpha = 0.28f),
                start = Offset(center.x + coinR * 0.08f, ribbonTopY),
                end = Offset(center.x - coinR * 0.15f, ribbonFoldY),
                strokeWidth = (coinR * 0.09f).coerceAtLeast(1.2.dp.toPx())
            )
            drawPath(ribbonRight, ribbonRightBrush)
            drawLine(
                color = pal.rimHighlight.copy(alpha = 0.42f),
                start = Offset(center.x + coinR * 0.34f, ribbonTopY + coinR * 0.03f),
                end = Offset(center.x + coinR * 0.08f, ribbonFoldY - coinR * 0.03f),
                strokeWidth = (coinR * 0.07f).coerceAtLeast(1.dp.toPx()),
                cap = StrokeCap.Butt
            )

            // Sculpted horizontal metallic ribbon bar + jump ring connecting coin
            val barW = coinR * 0.56f
            val barH = coinR * 0.15f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(pal.rimHighlight, pal.faceMid, pal.rimShadow),
                    startY = ribbonFoldY - barH * 0.5f,
                    endY = ribbonFoldY + barH * 0.5f
                ),
                topLeft = Offset(center.x - barW * 0.5f, ribbonFoldY - barH * 0.5f),
                size = Size(barW, barH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barH * 0.4f, barH * 0.4f)
            )
            drawCircle(
                color = pal.rimShadow,
                radius = coinR * 0.13f,
                center = Offset(center.x + frontShiftX * 0.45f, claspCenterY + coinR * 0.04f),
                style = Stroke(width = (coinR * 0.065f).coerceAtLeast(1.4.dp.toPx()))
            )
            drawCircle(
                color = pal.rimHighlight,
                radius = coinR * 0.13f,
                center = Offset(center.x + frontShiftX * 0.45f, claspCenterY + coinR * 0.02f),
                style = Stroke(width = (coinR * 0.045f).coerceAtLeast(1.1.dp.toPx()))
            )

            // 3. Soft Ground/Drop Shadow behind sculpted coin
            val rx = coinR * faceScaleX
            val ry = coinR
            drawOval(
                color = Color.Black.copy(alpha = 0.24f),
                topLeft = Offset(center.x - rx * 1.03f, center.y - ry * 0.94f + coinR * 0.08f),
                size = Size(rx * 2.06f, ry * 2.02f)
            )

            // 4. True 3D Extruded Metallic Rim when coin turns sideways
            if (visibleDepthPx > 0.6f) {
                val backCenterX = center.x + backShiftX
                val frontCenterX = center.x + frontShiftX
                // Draw back rim oval
                drawOval(
                    color = pal.rimShadow,
                    topLeft = Offset(backCenterX - rx, center.y - ry),
                    size = Size(rx * 2f, ry * 2f)
                )
                // Side cylinder band bridging top-to-bottom tangent of back & front ovals
                val rimBand = Path().apply {
                    moveTo(backCenterX, center.y - ry)
                    lineTo(frontCenterX, center.y - ry)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            left = frontCenterX - rx,
                            top = center.y - ry,
                            right = frontCenterX + rx,
                            bottom = center.y + ry
                        ),
                        startAngleDegrees = -90f,
                        sweepAngleDegrees = if (edgeDir > 0f) -180f else 180f,
                        forceMoveTo = false
                    )
                    lineTo(backCenterX, center.y + ry)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(
                            left = backCenterX - rx,
                            top = center.y - ry,
                            right = backCenterX + rx,
                            bottom = center.y + ry
                        ),
                        startAngleDegrees = 90f,
                        sweepAngleDegrees = if (edgeDir > 0f) 180f else -180f,
                        forceMoveTo = false
                    )
                    close()
                }
                drawPath(
                    path = rimBand,
                    brush = Brush.verticalGradient(
                        0.0f to pal.rimShadow,
                        0.22f to pal.faceMid,
                        0.46f to pal.rimHighlight,
                        0.74f to pal.faceDeep,
                        1.0f to pal.rimShadow,
                        startY = center.y - ry,
                        endY = center.y + ry
                    )
                )
                // Milled edge grooves on the exposed 3D rim thickness
                val grooveCount = 9
                for (g in 1 until grooveCount) {
                    val fracY = (g / grooveCount.toFloat()) * 2f - 1f
                    val gy = center.y + fracY * ry * 0.90f
                    val edgeOffset = kotlin.math.sqrt((1f - fracY * fracY).coerceAtLeast(0f)) * rx * (-edgeDir)
                    val gxStart = backCenterX + edgeOffset
                    val gxEnd = frontCenterX + edgeOffset
                    drawLine(
                        color = pal.rimShadow.copy(alpha = 0.52f),
                        start = Offset(gxStart, gy),
                        end = Offset(gxEnd, gy),
                        strokeWidth = (coinR * 0.032f).coerceAtLeast(0.8.dp.toPx())
                    )
                }
            }

            // 5. Perspective-Scaled Coin Face (Front Sculpted or Reverse Minted)
            withTransform({
                translate(left = frontShiftX, top = 0f)
                scale(scaleX = faceScaleX, scaleY = 1f, pivot = center)
            }) {
                // Outer beveled metallic body
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(pal.rimHighlight, pal.faceMid, pal.rimShadow),
                        start = Offset(center.x - coinR, center.y - coinR),
                        end = Offset(center.x + coinR, center.y + coinR)
                    ),
                    radius = coinR,
                    center = center
                )

                // Reeded / milled rim tick ring around outer bezel
                val tickCount = 36
                val outerTickR = coinR * 0.97f
                val innerTickR = coinR * 0.88f
                for (t in 0 until tickCount) {
                    val ang = (t * (360.0 / tickCount)) * (PI / 180.0)
                    val tickCol = if (t % 2 == 0) pal.rimHighlight.copy(alpha = 0.55f) else pal.rimShadow.copy(alpha = 0.42f)
                    drawLine(
                        color = tickCol,
                        start = Offset(
                            center.x + (cos(ang) * innerTickR).toFloat(),
                            center.y + (sin(ang) * innerTickR).toFloat()
                        ),
                        end = Offset(
                            center.x + (cos(ang) * outerTickR).toFloat(),
                            center.y + (sin(ang) * outerTickR).toFloat()
                        ),
                        strokeWidth = (coinR * 0.030f).coerceAtLeast(0.7.dp.toPx()),
                        cap = StrokeCap.Round
                    )
                }

                // Inner metallic rim ridge & recessed dish
                val innerR = coinR * 0.78f
                drawCircle(
                    color = pal.rimHighlight.copy(alpha = 0.65f),
                    radius = coinR * 0.84f,
                    center = center,
                    style = Stroke(width = (coinR * 0.042f).coerceAtLeast(1.dp.toPx()))
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(pal.faceLight, pal.faceMid, pal.faceDeep),
                        center = Offset(center.x - innerR * 0.28f, center.y - innerR * 0.30f),
                        radius = innerR * 1.48f
                    ),
                    radius = innerR,
                    center = center
                )
                // Inner dish shadow groove
                drawCircle(
                    color = pal.rimShadow.copy(alpha = 0.58f),
                    radius = innerR,
                    center = center,
                    style = Stroke(width = (coinR * 0.045f).coerceAtLeast(1.1.dp.toPx()))
                )

                if (isFrontFacing) {
                    // Sculpted Symmetrical Laurel Wreath framing the inner well
                    drawLaurelWreath(
                        center = center,
                        radius = innerR * 0.76f,
                        leafSize = innerR * 0.145f,
                        lightColor = pal.crestTint.copy(alpha = 0.88f),
                        shadowColor = pal.rimShadow.copy(alpha = 0.44f)
                    )

                    // Tier-specific sculpted centerpiece
                    when (tier) {
                        MedalTier.GOLD -> {
                            drawCrownCrest(
                                center = Offset(center.x, center.y - innerR * 0.42f),
                                width = innerR * 0.44f,
                                height = innerR * 0.22f,
                                lightColor = pal.crestTint,
                                shadowColor = pal.rimShadow.copy(alpha = 0.45f)
                            )
                            drawFacetedStarCrest(
                                center = Offset(center.x, center.y + innerR * 0.07f),
                                outerR = innerR * 0.42f,
                                innerR = innerR * 0.19f,
                                lightColor = pal.crestTint,
                                shadowColor = pal.faceDeep.copy(alpha = 0.72f)
                            )
                        }
                        MedalTier.SILVER -> {
                            drawFacetedStarCrest(
                                center = Offset(center.x, center.y - innerR * 0.02f),
                                outerR = innerR * 0.46f,
                                innerR = innerR * 0.21f,
                                lightColor = pal.crestTint,
                                shadowColor = pal.faceDeep.copy(alpha = 0.72f)
                            )
                            drawCircle(
                                color = pal.rimHighlight.copy(alpha = 0.55f),
                                radius = innerR * 0.52f,
                                center = center,
                                style = Stroke(width = (coinR * 0.025f).coerceAtLeast(0.7.dp.toPx()))
                            )
                        }
                        MedalTier.BRONZE -> {
                            drawFacetedStarCrest(
                                center = Offset(center.x, center.y - innerR * 0.01f),
                                outerR = innerR * 0.44f,
                                innerR = innerR * 0.20f,
                                lightColor = pal.crestTint,
                                shadowColor = pal.faceDeep.copy(alpha = 0.76f)
                            )
                        }
                        MedalTier.NONE -> Unit
                    }
                } else {
                    // Reverse Minted Face (visible mid-rotation when cosYaw < 0): concentric ring + embossed "VL" crest
                    drawCircle(
                        color = pal.crestTint.copy(alpha = 0.42f),
                        radius = innerR * 0.68f,
                        center = center,
                        style = Stroke(width = (coinR * 0.032f).coerceAtLeast(0.8.dp.toPx()))
                    )
                    // Reverse engraved "VL" chevron monogram
                    val vlStroke = Stroke(
                        width = (innerR * 0.16f).coerceAtLeast(1.8.dp.toPx()),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                    val vlPath = Path().apply {
                        moveTo(center.x - innerR * 0.36f, center.y - innerR * 0.24f)
                        lineTo(center.x - innerR * 0.10f, center.y + innerR * 0.24f)
                        lineTo(center.x + innerR * 0.14f, center.y - innerR * 0.24f)
                        lineTo(center.x + innerR * 0.14f, center.y + innerR * 0.22f)
                        lineTo(center.x + innerR * 0.38f, center.y + innerR * 0.22f)
                    }
                    drawPath(vlPath, pal.rimShadow.copy(alpha = 0.52f), style = vlStroke)
                    drawPath(vlPath, pal.crestTint.copy(alpha = 0.90f), style = Stroke(
                        width = (innerR * 0.12f).coerceAtLeast(1.4.dp.toPx()),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ))
                }

                // 6. Clipped Diagonal Specular Shine Sweep scaled naturally by surface angle
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
                    val angleShineFactor = (0.18f + 0.82f * faceScaleX).coerceIn(0.18f, 1f)
                    clipPath(coinClip) {
                        val bandX = (center.x - coinR * 1.3f) + (coinR * 2.6f * sweep)
                        rotate(degrees = 26f, pivot = center) {
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.56f * angleShineFactor),
                                        Color.Transparent
                                    ),
                                    startX = bandX - coinR * 0.26f,
                                    endX = bandX + coinR * 0.26f
                                ),
                                topLeft = Offset(bandX - coinR * 0.26f, center.y - coinR * 1.5f),
                                size = Size(coinR * 0.52f, coinR * 3.0f)
                            )
                        }
                    }
                }
            }

            // 7. Twinkling rim sparkles (anchored gently outside rotated silhouette)
            if (runShine && !suppressGlow) {
                val sparkleAlpha = (0.35f + pulse * 0.65f).coerceIn(0f, 1f)
                drawFourPointSparkle(
                    center = Offset(center.x + coinR * 0.80f * faceScaleX + frontShiftX, center.y - coinR * 0.64f),
                    radius = coinR * (0.13f + pulse * 0.07f),
                    color = Color.White.copy(alpha = sparkleAlpha)
                )
                drawFourPointSparkle(
                    center = Offset(center.x - coinR * 0.82f * faceScaleX + frontShiftX, center.y + coinR * 0.58f),
                    radius = coinR * (0.10f + (1f - pulse) * 0.06f),
                    color = pal.rimHighlight.copy(alpha = 0.35f + (1f - pulse) * 0.55f)
                )
            }

            // 8. Landing Arrival Shockwave + Sparkle Burst
            if (burst > 0f) {
                val burstAlpha = (1f - burst).coerceIn(0f, 1f)
                val shockR = coinR * (1.0f + burst * 0.95f)
                drawCircle(
                    color = pal.auraGlow.copy(alpha = burstAlpha * 0.85f),
                    radius = shockR,
                    center = center,
                    style = Stroke(width = (3.4f * (1f - burst * 0.6f)).dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = burstAlpha * 0.65f),
                    radius = coinR * (0.95f + burst * 0.55f),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
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

private fun DrawScope.drawLaurelWreath(
    center: Offset,
    radius: Float,
    leafSize: Float,
    lightColor: Color,
    shadowColor: Color
) {
    val pairs = 6
    listOf(-1f, 1f).forEach { sideSign ->
        for (i in 0 until pairs) {
            // Arc from bottom (+68 deg) toward upper side (-52 deg)
            val baseDeg = 68f - i * 22f
            val rad = (if (sideSign < 0f) 180f - baseDeg else baseDeg) * (PI / 180.0)
            val anchor = Offset(
                center.x + (cos(rad) * radius).toFloat(),
                center.y + (sin(rad) * radius).toFloat()
            )
            // Outer and inner leaf angled along wreath curvature
            listOf(-26f, 22f).forEach { offsetDeg ->
                val leafAngle = (if (sideSign < 0f) 180f - baseDeg - 90f else baseDeg + 90f) + offsetDeg * sideSign
                val leafPath = Path().apply {
                    moveTo(anchor.x, anchor.y)
                    val tipRad = (leafAngle - 16f) * (PI / 180.0)
                    val midLeft = (leafAngle - 42f) * (PI / 180.0)
                    val midRight = (leafAngle + 14f) * (PI / 180.0)
                    val tip = Offset(
                        anchor.x + (cos(tipRad) * leafSize * 1.35f).toFloat(),
                        anchor.y + (sin(tipRad) * leafSize * 1.35f).toFloat()
                    )
                    val c1 = Offset(
                        anchor.x + (cos(midLeft) * leafSize * 0.75f).toFloat(),
                        anchor.y + (sin(midLeft) * leafSize * 0.75f).toFloat()
                    )
                    val c2 = Offset(
                        anchor.x + (cos(midRight) * leafSize * 0.75f).toFloat(),
                        anchor.y + (sin(midRight) * leafSize * 0.75f).toFloat()
                    )
                    quadraticBezierTo(c1.x, c1.y, tip.x, tip.y)
                    quadraticBezierTo(c2.x, c2.y, anchor.x, anchor.y)
                    close()
                }
                drawPath(leafPath, shadowColor)
                withTransform({ translate(left = 0f, top = -leafSize * 0.08f) }) {
                    drawPath(leafPath, lightColor)
                }
            }
        }
    }
}

private fun DrawScope.drawCrownCrest(
    center: Offset,
    width: Float,
    height: Float,
    lightColor: Color,
    shadowColor: Color
) {
    val left = center.x - width * 0.5f
    val right = center.x + width * 0.5f
    val bottom = center.y + height * 0.5f
    val top = center.y - height * 0.5f
    val crown = Path().apply {
        moveTo(left + width * 0.12f, bottom)
        lineTo(left, top + height * 0.22f)
        lineTo(center.x - width * 0.22f, center.y + height * 0.06f)
        lineTo(center.x, top)
        lineTo(center.x + width * 0.22f, center.y + height * 0.06f)
        lineTo(right, top + height * 0.22f)
        lineTo(right - width * 0.12f, bottom)
        close()
    }
    withTransform({ translate(left = 0f, top = height * 0.10f) }) {
        drawPath(crown, shadowColor)
    }
    drawPath(crown, lightColor)
}

private fun DrawScope.drawFacetedStarCrest(
    center: Offset,
    outerR: Float,
    innerR: Float,
    lightColor: Color,
    shadowColor: Color
) {
    // Drop shadow behind star
    val shadowPath = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = (-90.0 + i * 36.0) * (PI / 180.0)
        val x = center.x + (cos(angle) * r).toFloat()
        val y = center.y + outerR * 0.06f + (sin(angle) * r).toFloat()
        if (i == 0) shadowPath.moveTo(x, y) else shadowPath.lineTo(x, y)
    }
    shadowPath.close()
    drawPath(shadowPath, shadowColor.copy(alpha = 0.55f))

    // 10 beveled triangular facets meeting at the star center
    for (i in 0 until 10) {
        val r1 = if (i % 2 == 0) outerR else innerR
        val r2 = if ((i + 1) % 2 == 0) outerR else innerR
        val a1 = (-90.0 + i * 36.0) * (PI / 180.0)
        val a2 = (-90.0 + (i + 1) * 36.0) * (PI / 180.0)
        val p1 = Offset(center.x + (cos(a1) * r1).toFloat(), center.y + (sin(a1) * r1).toFloat())
        val p2 = Offset(center.x + (cos(a2) * r2).toFloat(), center.y + (sin(a2) * r2).toFloat())
        val facet = Path().apply {
            moveTo(center.x, center.y)
            lineTo(p1.x, p1.y)
            lineTo(p2.x, p2.y)
            close()
        }
        drawPath(facet, if (i % 2 == 0) lightColor else shadowColor.copy(alpha = 0.62f))
    }
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
    onStartFlightToHistory: (sourceCenter: Offset) -> Unit,
    onDismissPreview: (() -> Unit)? = null
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val pal = remember(event.tier) { resolveMedalPalette(event.tier) }
    val sourceOffsetHolder = remember { mutableListOf(Offset.Zero) }

    val medalPopScale = remember { Animatable(if (noAnim) 1f else 0.36f) }
    val medalSpinY = remember { Animatable(0f) }
    val shockwaveProgress = remember { Animatable(if (noAnim) 1f else 0f) }
    val showcaseFade = remember { Animatable(if (noAnim) 1f else 0f) }
    var remainingSeconds by remember(event) { mutableIntStateOf(5) }
    val hasTriggeredFlight = remember(event) { booleanArrayOf(false) }

    val triggerFlight = {
        if (!hasTriggeredFlight[0]) {
            hasTriggeredFlight[0] = true
            onStartFlightToHistory(sourceOffsetHolder.firstOrNull() ?: Offset.Zero)
        }
    }

    LaunchedEffect(event, noAnim) {
        remainingSeconds = 5
        if (!noAnim) {
            showcaseFade.snapTo(0f)
            medalPopScale.snapTo(0.36f)
            medalSpinY.snapTo(0f)
            shockwaveProgress.snapTo(0f)
            coroutineScope {
                launch {
                    showcaseFade.animateTo(1f, tween(durationMillis = 280, easing = FastOutSlowInEasing))
                }
                launch {
                    medalPopScale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = spring(
                            dampingRatio = 0.56f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
                launch {
                    shockwaveProgress.animateTo(1f, tween(durationMillis = 900, easing = FastOutSlowInEasing))
                }
                launch {
                    delay(260L)
                    medalSpinY.animateTo(
                        targetValue = 1080f,
                        animationSpec = tween(
                            durationMillis = 4150,
                            easing = CubicBezierEasing(0.18f, 0.88f, 0.24f, 1.0f)
                        )
                    )
                }
                launch {
                    for (sec in 5 downTo 1) {
                        remainingSeconds = sec
                        delay(1000L)
                    }
                }
            }
        } else {
            showcaseFade.snapTo(1f)
            medalPopScale.snapTo(1f)
            medalSpinY.snapTo(0f)
            shockwaveProgress.snapTo(1f)
            for (sec in 5 downTo 1) {
                remainingSeconds = sec
                delay(1000L)
            }
        }
        triggerFlight()
    }

    val ambientTick = if (!noAnim) {
        val inf = rememberInfiniteTransition(label = "medalShowcaseAmbient")
        inf.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "showcaseAmbientTick"
        )
    } else {
        remember { mutableFloatStateOf(0.25f) }
    }

    val celebrateMascot = remember(mascot) {
        mascot.copy(
            urgentMood = MascotMood.CELEBRATE,
            timePhase = DayTimePhase.AFTERNOON
        )
    }

    // Lock Back during the full 5-second showcase on normal unlocks; only preview allows closing early.
    BackHandler(enabled = true) {
        if (event.isPreview && onDismissPreview != null) {
            onDismissPreview()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = showcaseFade.value }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("medal_unlock_celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle radial spotlight backdrop + rotating light rays + shockwave + celebratory confetti
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.44f)
            val spotRadius = size.maxDimension * 0.68f
            drawRect(Color(0x9607090F))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        pal.auraGlow.copy(alpha = 0.28f),
                        pal.faceMid.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = spotRadius
                ),
                radius = spotRadius,
                center = center
            )

            val tick = ambientTick.value
            if (!tokens.performance.noAmbientGlow && !noAnim) {
                // Delicate rotating light beams behind the 3D extruded coin
                val rayCount = 12
                val baseAngle = tick * 360f
                val beamLen = 178.dp.toPx()
                for (r in 0 until rayCount) {
                    val deg = baseAngle + r * (360f / rayCount)
                    val rad = Math.toRadians(deg.toDouble())
                    val end = Offset(
                        x = center.x + (cos(rad) * beamLen).toFloat(),
                        y = center.y + (sin(rad) * beamLen).toFloat()
                    )
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                pal.auraGlow.copy(alpha = if (r % 2 == 0) 0.30f else 0.14f),
                                Color.Transparent
                            ),
                            start = center,
                            end = end
                        ),
                        start = center,
                        end = end,
                        strokeWidth = (if (r % 2 == 0) 6.dp else 3.dp).toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Expanding burst shockwave ring on impact
                val sw = shockwaveProgress.value
                if (sw in 0.01f..0.99f) {
                    drawCircle(
                        color = pal.rimHighlight.copy(alpha = (1f - sw) * 0.65f),
                        radius = 48.dp.toPx() + sw * 136.dp.toPx(),
                        center = center,
                        style = Stroke(width = ((1f - sw) * 5.dp.toPx()).coerceAtLeast(1f))
                    )
                }

                // Celebratory confetti & 4-point sparkles showering across viewport
                val particles = 38
                for (i in 0 until particles) {
                    val xFrac = ((i * 37 + 13) % 100) / 100f
                    val yBase = ((i * 23) % 100) / 100f
                    val yFrac = (yBase + tick * (1.05f + (i % 4) * 0.16f)) % 1f
                    val sway = sin((tick * 2f * PI + i * 0.75).toDouble()).toFloat() * 20.dp.toPx()
                    val pos = Offset(size.width * xFrac + sway, size.height * yFrac)
                    val col = when (i % 4) {
                        0 -> pal.auraGlow
                        1 -> Color.White
                        2 -> tokens.accentColor
                        else -> pal.rimHighlight
                    }.copy(alpha = 0.86f)
                    when (i % 3) {
                        0 -> drawFourPointSparkle(pos, (4.8f + (i % 3) * 1.8f).dp.toPx(), col)
                        1 -> rotate(degrees = (tick * 420f + i * 28f), pivot = pos) {
                            drawRect(
                                color = col,
                                topLeft = Offset(pos.x - 4.5.dp.toPx(), pos.y - 2.dp.toPx()),
                                size = Size(9.dp.toPx(), 4.dp.toPx())
                            )
                        }
                        else -> drawCircle(col, radius = (2.8f + (i % 2) * 1.6f).dp.toPx(), center = pos)
                    }
                }
            }
        }

        // Top-right close chip strictly in Settings preview mode
        if (event.isPreview && onDismissPreview != null) {
            IronPulseTonalSurface(
                onClick = onDismissPreview,
                shape = RoundedCornerShape(999.dp),
                baseColor = Color(0xCC12151C),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.24f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .testTag("medal_preview_close_button")
            ) {
                Text(
                    text = lang.t("Tutup", "Close"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        // Center-stage floating typography + 3D spinning medal + cheering Pulse mascot (zero dialog card)
        Column(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(pal.auraGlow.copy(alpha = 0.18f))
                        .border(1.dp, pal.auraGlow.copy(alpha = 0.65f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (event.isPreview) {
                            lang.t("PRATINJAU MEDALI · ${formatShortMonthLabel(event.monthKey, lang)}", "MEDAL PREVIEW · ${formatShortMonthLabel(event.monthKey, lang)}")
                        } else {
                            lang.t("MEDALI BARU · ${formatShortMonthLabel(event.monthKey, lang)}", "NEW MEDAL · ${formatShortMonthLabel(event.monthKey, lang)}")
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.9.sp,
                        color = pal.rimHighlight
                    )
                }

                Text(
                    text = event.tier.title(lang),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = lang.t(
                        "${event.verifiedDays} hari gym sah bulan ini",
                        "${event.verifiedDays} verified gym days this month"
                    ),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center
                )
            }

            // Center extruded 3D spinning coin tracked for exact curved flight origin
            Box(
                modifier = Modifier
                    .size(168.dp)
                    .graphicsLayer {
                        scaleX = medalPopScale.value
                        scaleY = medalPopScale.value
                    }
                    .onGloballyPositioned { coords ->
                        val b = coords.boundsInRoot()
                        sourceOffsetHolder.clear()
                        sourceOffsetHolder.add(Offset(b.center.x, b.center.y))
                    }
                    .testTag("medal_3d_spinning_emblem"),
                contentAlignment = Alignment.Center
            ) {
                ProceduralMedalEmblem(
                    tier = event.tier,
                    size = 148.dp,
                    shineEnabled = true,
                    yawDegrees = medalSpinY.value
                )
            }

            // Cheering Pulse mascot + live 5-second countdown indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xB811151E))
                    .border(1.dp, pal.auraGlow.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                MascotOrbFace(
                    mascot = celebrateMascot,
                    eyeStateFlow = mascotEyeFlow,
                    size = 48.dp,
                    animActive = !noAnim
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = lang.t("Pulse ikut bersorak!", "Pulse is cheering!"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = lang.t(
                            "Meluncur ke Riwayat dalam ${remainingSeconds}d...",
                            "Flying to History in ${remainingSeconds}s..."
                        ),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = pal.rimHighlight,
                        modifier = Modifier.testTag("medal_countdown_text")
                    )
                }
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
    val startSizeDp = 124.dp
    val endSizeDp = 86.dp
    val progress = remember { Animatable(0f) }

    LaunchedEffect(startCenter, targetCenter) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 720, easing = FastOutSlowInEasing)
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
    val arcLiftPx = with(density) { 68.dp.toPx() } * sin(p * PI).toFloat()
    val curX = startCenter.x + (resolvedTarget.x - startCenter.x) * p
    val curY = startCenter.y + (resolvedTarget.y - startCenter.y) * p - arcLiftPx

    Box(modifier = Modifier.fillMaxSize()) {
        // Soft sparkle trail behind flying medal
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pal = resolveMedalPalette(tier)
            for (i in 1..5) {
                val tp = (p - i * 0.055f).coerceIn(0f, 1f)
                val tLift = 68.dp.toPx() * sin(tp * PI).toFloat()
                val tx = startCenter.x + (resolvedTarget.x - startCenter.x) * tp
                val ty = startCenter.y + (resolvedTarget.y - startCenter.y) * tp - tLift
                drawFourPointSparkle(
                    center = Offset(tx, ty),
                    radius = (12f - i * 1.8f).dp.toPx(),
                    color = pal.auraGlow.copy(alpha = (0.78f - i * 0.14f) * (1f - p * 0.35f))
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
