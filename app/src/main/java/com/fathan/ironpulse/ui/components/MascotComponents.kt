package com.fathan.ironpulse.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.PI
import kotlin.math.sin
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.TimePhasePose
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotReaction
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrbBoundsHolder {
    var rect: Rect? = null
}

private class DockOriginHolder {
    var origin: Offset = Offset.Zero
}

internal fun isClosedEyeExpression(mascot: MascotUiState, isPetting: Boolean): Boolean {
    if (isPetting) return true
    val urgent = mascot.urgentMood
    if (urgent != null) {
        return urgent == MascotMood.REST ||
            urgent == MascotMood.CLAP ||
            urgent == MascotMood.CELEBRATE ||
            urgent == MascotMood.MILESTONE
    }
    val pose = mascot.activeTimePose
    return pose == TimePhasePose.SLEEP ||
        pose == TimePhasePose.ZEN ||
        pose == TimePhasePose.COZY ||
        pose == TimePhasePose.DREAM ||
        pose == TimePhasePose.TEA ||
        pose == TimePhasePose.SUNRISE ||
        pose == TimePhasePose.MUSIC ||
        pose == TimePhasePose.SWEAT ||
        pose == TimePhasePose.RECHARGE ||
        pose == TimePhasePose.YAWN
}

@Composable
fun MascotOrbFace(
    mascot: MascotUiState,
    eyeStateFlow: StateFlow<MascotEyeState>? = null,
    size: Dp = 64.dp,
    animActive: Boolean = true,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val tabActive = LocalTabActive.current
    val staticMode = tokens.performance.staticMascot || tokens.performance.noAnimations
    val runAnim = !staticMode && animActive && tabActive
    val eyeState = if (eyeStateFlow != null && runAnim) {
        eyeStateFlow.collectAsStateWithLifecycle().value
    } else {
        remember { MascotEyeState() }
    }
    val phaseColor = remember(mascot.phaseColorHex) {
        MaterialYouColorEngine.parseColor(mascot.phaseColorHex, Color(0xFFFBBF24))
    }
    val ringBorderColor = remember(phaseColor, staticMode) {
        phaseColor.copy(alpha = if (staticMode) 0.45f else 0.55f)
    }

    val urgent = mascot.urgentMood
    val pose = mascot.activeTimePose
    val isCelebration = urgent == MascotMood.CLAP || urgent == MascotMood.CELEBRATE || urgent == MascotMood.MILESTONE
    val isCalmEyes = urgent == MascotMood.REST
    val isFocusedEyes = urgent == MascotMood.TIMER_ACTIVE

    val isSleepPose = urgent == null && pose == TimePhasePose.SLEEP
    val isYawnPose = urgent == null && pose == TimePhasePose.YAWN
    val isTeaPose = urgent == null && pose == TimePhasePose.TEA
    val isCoffeePose = urgent == null && pose == TimePhasePose.COFFEE
    val isStretchPose = urgent == null && pose == TimePhasePose.STRETCH
    val isPerkPose = urgent == null && pose == TimePhasePose.PERK
    val isWinkPose = urgent == null && pose == TimePhasePose.WINK
    val isSunrisePose = urgent == null && pose == TimePhasePose.SUNRISE
    val isProteinPose = urgent == null && pose == TimePhasePose.PROTEIN
    val isSalutePose = urgent == null && pose == TimePhasePose.SALUTE
    val isSparklePose = urgent == null && pose == TimePhasePose.SPARKLE

    val isHydratePose = urgent == null && pose == TimePhasePose.HYDRATE
    val isShadesPose = urgent == null && pose == TimePhasePose.SHADES
    val isFlexPose = urgent == null && pose == TimePhasePose.FLEX
    val isMusicPose = urgent == null && pose == TimePhasePose.MUSIC
    val isCuriousPose = urgent == null && pose == TimePhasePose.CURIOUS
    val isBeastPose = urgent == null && pose == TimePhasePose.BEAST
    val isSweatPose = urgent == null && pose == TimePhasePose.SWEAT
    val isSmirkPose = urgent == null && pose == TimePhasePose.SMIRK
    val isWhistlePose = urgent == null && pose == TimePhasePose.WHISTLE

    val isNightChillPose = urgent == null && pose == TimePhasePose.NIGHT_CHILL
    val isStargazePose = urgent == null && pose == TimePhasePose.STARGAZE
    val isZenPose = urgent == null && pose == TimePhasePose.ZEN
    val isCozyPose = urgent == null && pose == TimePhasePose.COZY
    val isDreamPose = urgent == null && pose == TimePhasePose.DREAM
    val isMoonlitPose = urgent == null && pose == TimePhasePose.MOONLIT
    val isRechargePose = urgent == null && pose == TimePhasePose.RECHARGE

    val isClosedCalmPose = isSleepPose || isZenPose || isCozyPose || isDreamPose
    val isClosedEyeExpression = isClosedEyeExpression(mascot, eyeState.isPetting)
    val density = LocalDensity.current

    val staticOne = remember { mutableFloatStateOf(1f) }
    val staticZero = remember { mutableFloatStateOf(0f) }

    val ringScaleState: State<Float>
    val bobOffsetDpState: State<Float>
    if (runAnim) {
        val infinite = rememberInfiniteTransition(label = "orbInfinite")
        ringScaleState = infinite.animateFloat(
            initialValue = 0.96f,
            targetValue = if (isCelebration || eyeState.tapBounce) 1.24f else 1.14f,
            animationSpec = infiniteRepeatable(
                animation = tween(if (isCelebration) 1100 else 2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ringScale"
        )
        bobOffsetDpState = infinite.animateFloat(
            initialValue = -2f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(if (isMusicPose || isWhistlePose || isProteinPose) 1400 else 2300, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bobOffset"
        )
    } else {
        ringScaleState = staticOne
        bobOffsetDpState = staticZero
    }

    val tapScaleState = if (runAnim) {
        animateFloatAsState(
            targetValue = if (eyeState.tapBounce) 1.11f else 1f,
            animationSpec = IronPulseMotion.OrbSnapSpring,
            label = "tapScale"
        )
    } else {
        staticOne
    }

    // Natural squash-and-stretch impulse on tap / combo / pet
    val squashX = remember { Animatable(1f) }
    val squashY = remember { Animatable(1f) }
    LaunchedEffect(eyeState.reactionTick, runAnim) {
        if (!runAnim || eyeState.reactionTick == 0) {
            squashX.snapTo(1f)
            squashY.snapTo(1f)
            return@LaunchedEffect
        }
        val intense = eyeState.reactionKind == MascotReaction.COMBO_EXCITED
        val wide = if (intense) 1.15f else 1.09f
        val flat = if (intense) 0.87f else 0.92f
        val tall = if (intense) 1.09f else 1.05f
        val slim = if (intense) 0.93f else 0.96f
        launch {
            squashX.snapTo(wide)
            squashX.animateTo(slim, tween(110, easing = FastOutSlowInEasing))
            squashX.animateTo(1f, spring(dampingRatio = 0.46f, stiffness = 420f))
        }
        launch {
            squashY.snapTo(flat)
            squashY.animateTo(tall, tween(110, easing = FastOutSlowInEasing))
            squashY.animateTo(1f, spring(dampingRatio = 0.46f, stiffness = 420f))
        }
    }

    val headTiltState = if (runAnim) {
        animateFloatAsState(
            targetValue = if (isClosedEyeExpression) 0f else eyeState.headTiltDeg,
            animationSpec = spring(dampingRatio = 0.60f, stiffness = 240f),
            label = "headTilt"
        )
    } else {
        staticZero
    }

    val smoothBlinkState = if (runAnim) {
        animateFloatAsState(
            targetValue = if (isClosedEyeExpression) 0f else eyeState.blinkProgress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 48),
            label = "smoothBlink"
        )
    } else {
        staticZero
    }

    val glanceXState = if (runAnim) {
        animateFloatAsState(
            targetValue = if (isClosedEyeExpression || isSparklePose) 0f else eyeState.glanceX,
            animationSpec = tween(280, easing = FastOutSlowInEasing),
            label = "glanceX"
        )
    } else {
        staticZero
    }
    val glanceYState = if (runAnim) {
        animateFloatAsState(
            targetValue = if (isClosedEyeExpression || isSparklePose) 0f else eyeState.glanceY,
            animationSpec = tween(280, easing = FastOutSlowInEasing),
            label = "glanceY"
        )
    } else {
        staticZero
    }

    val pathA = remember { Path() }
    val pathB = remember { Path() }

    val theme = tokens.uiTheme
    val accent = tokens.accentColor

    Box(
        modifier = modifier
            .size(size)
            .testTag("mascot_orb_${theme.wireValue}")
            .graphicsLayer { },
        contentAlignment = Alignment.Center
    ) {
        // OS-distinct outer breathing halo / bezel ring
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val s = ringScaleState.value
                    scaleX = s
                    scaleY = s
                }
        ) {
            val cx = this.size.width * 0.5f
            val cy = this.size.height * 0.5f
            val r = this.size.minDimension * 0.48f
            val sw = 2.dp.toPx()
            when (theme) {
                UiTheme.FRUTIGER_AERO -> {
                    // Luminous Y2K crystal water bubble aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x557CE8FF), Color(0x334ADE80), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = r * 1.06f
                        ),
                        radius = r,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.88f),
                        radius = r * 0.96f,
                        center = Offset(cx, cy),
                        style = Stroke(sw * 0.9f)
                    )
                }
                UiTheme.CARBON_FORGE -> {
                    // Brushed titanium mechanical bezel ring with 4 cardinal machined ticks
                    drawCircle(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFFE2E8F0), Color(0xFF475569), accent)
                        ),
                        radius = r * 0.97f,
                        center = Offset(cx, cy),
                        style = Stroke(sw * 1.15f)
                    )
                    for (deg in listOf(0.0, 90.0, 180.0, 270.0)) {
                        val rad = Math.toRadians(deg)
                        val c = kotlin.math.cos(rad).toFloat()
                        val s = kotlin.math.sin(rad).toFloat()
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(cx + r * 0.84f * c, cy + r * 0.84f * s),
                            end = Offset(cx + r * 1.01f * c, cy + r * 1.01f * s),
                            strokeWidth = sw * 1.1f,
                            cap = StrokeCap.Square
                        )
                    }
                }
                UiTheme.UBUNTU_YARU -> {
                    // Ubuntu orbital ring with 3 signature Yaru orange nodes
                    drawCircle(
                        color = Color(0xFFE95420).copy(alpha = 0.65f),
                        radius = r * 0.96f,
                        center = Offset(cx, cy),
                        style = Stroke(sw * 0.95f)
                    )
                    for (deg in listOf(-90.0, 30.0, 150.0)) {
                        val rad = Math.toRadians(deg)
                        val nx = cx + (r * 0.96f * kotlin.math.cos(rad)).toFloat()
                        val ny = cy + (r * 0.96f * kotlin.math.sin(rad)).toFloat()
                        drawCircle(Color(0xFF231D22), sw * 1.6f, Offset(nx, ny))
                        drawCircle(Color(0xFFE95420), sw * 1.15f, Offset(nx, ny))
                    }
                }
                UiTheme.AMOLED_STEALTH -> {
                    // Tactical HUD segmented holographic scan arcs + reticle pips
                    val hudColor = accent.copy(alpha = 0.82f)
                    for (start in listOf(-32f, 58f, 148f, 238f)) {
                        drawArc(
                            color = hudColor,
                            startAngle = start,
                            sweepAngle = 54f,
                            useCenter = false,
                            topLeft = Offset(cx - r * 0.97f, cy - r * 0.97f),
                            size = Size(r * 1.94f, r * 1.94f),
                            style = Stroke(sw * 0.85f, cap = StrokeCap.Square)
                        )
                    }
                }
                UiTheme.MATERIAL_YOU -> {
                    // Expressive M3 tonal breathing aura ring
                    drawCircle(
                        color = ringBorderColor,
                        radius = r * 0.96f,
                        center = Offset(cx, cy),
                        style = Stroke(sw)
                    )
                }
            }
        }

        // Core OS-styled circular orb chassis (GPU graphicsLayer: bob + squash/stretch + natural head tilt)
        Box(
            modifier = Modifier
                .size(size * 0.875f)
                .graphicsLayer {
                    translationY = bobOffsetDpState.value * density.density
                    val ts = tapScaleState.value
                    scaleX = ts * squashX.value
                    scaleY = ts * squashY.value
                    rotationZ = headTiltState.value
                }
                .clip(CircleShape)
                .drawBehind {
                    val cx = this.size.width * 0.5f
                    val cy = this.size.height * 0.5f
                    val r = this.size.minDimension * 0.5f
                    when (theme) {
                        UiTheme.FRUTIGER_AERO -> {
                            // 3D Glossy Aqua Crystal Water-Droplet Orb (deep ocean center keeps white eyes crisp!)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colorStops = arrayOf(
                                        0.0f to Color(0xFF0B6BB8),
                                        0.58f to Color(0xFF004C8C),
                                        0.92f to Color(0xFF003262),
                                        1.0f to Color(0xFF00244A)
                                    ),
                                    center = Offset(cx * 0.92f, cy * 0.82f),
                                    radius = r * 1.12f
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                            // Fresh Aero mint-aqua lower refraction bounce
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0x8834D399), Color.Transparent),
                                    center = Offset(cx, cy * 1.62f),
                                    radius = r * 0.85f
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                            // Iconic Y2K glossy white gel upper dome reflection
                            drawOval(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.62f), Color.Transparent)
                                ),
                                topLeft = Offset(cx - r * 0.68f, cy - r * 0.88f),
                                size = Size(r * 1.36f, r * 0.58f)
                            )
                        }
                        UiTheme.CARBON_FORGE -> {
                            // Anodized forged titanium core with diagonal metallic sheen
                            drawCircle(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF232936), Color(0xFF101319), Color(0xFF080A0E)),
                                    start = Offset(0f, 0f),
                                    end = Offset(this.size.width, this.size.height)
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(accent.copy(alpha = 0.22f), Color.Transparent),
                                    center = Offset(cx, cy * 1.45f),
                                    radius = r * 0.9f
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                        }
                        UiTheme.UBUNTU_YARU -> {
                            // Warm aubergine-to-charcoal GNOME matte orb
                            drawCircle(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF3E1C35), Color(0xFF261F25), Color(0xFF1A171B))
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                        }
                        UiTheme.AMOLED_STEALTH -> {
                            // Pitch-black tactical stealth visor
                            drawCircle(Color(0xFF000000), r, Offset(cx, cy))
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(accent.copy(alpha = 0.14f), Color.Transparent),
                                    center = Offset(cx, cy),
                                    radius = r
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                        }
                        UiTheme.MATERIAL_YOU -> {
                            // Expressive M3 tonal frosted core
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        tokens.palette.surfaceContainerHighest,
                                        Color(0xF5141824)
                                    ),
                                    center = Offset(cx * 0.78f, cy * 0.68f),
                                    radius = r * 1.15f
                                ),
                                radius = r,
                                center = Offset(cx, cy)
                            )
                        }
                    }
                }
                .border(
                    width = if (theme == UiTheme.FRUTIGER_AERO) 2.2.dp else 2.dp,
                    color = when (theme) {
                        UiTheme.FRUTIGER_AERO -> Color.White.copy(alpha = 0.92f)
                        UiTheme.UBUNTU_YARU -> Color(0xFFE95420)
                        UiTheme.AMOLED_STEALTH -> accent
                        else -> phaseColor
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.72f)) {
                val u = this.size.width / 64f
                fun px(v: Float) = v * u
                fun pt(x: Float, y: Float) = Offset(px(x), px(y))

                // Headphones for Music pose
                if (isMusicPose) {
                    pathA.reset()
                    pathA.moveTo(px(11f), px(31f))
                    pathA.cubicTo(px(11f), px(18.5f), px(20.5f), px(10.5f), px(32f), px(10.5f))
                    pathA.cubicTo(px(43.5f), px(10.5f), px(53f), px(18.5f), px(53f), px(31f))
                    drawPath(pathA, phaseColor, style = Stroke(px(2.6f), cap = StrokeCap.Round))
                    drawRoundRect(phaseColor, pt(7.5f, 26.5f), Size(px(5.5f), px(11f)), CornerRadius(px(2.7f)))
                    drawRoundRect(phaseColor, pt(51f, 26.5f), Size(px(5.5f), px(11f)), CornerRadius(px(2.7f)))
                }

                // Cozy Blanket
                if (isCozyPose) {
                    pathA.reset()
                    pathA.moveTo(px(13f), px(47.5f))
                    pathA.cubicTo(px(20f), px(53f), px(44f), px(53f), px(51f), px(47.5f))
                    drawPath(pathA, phaseColor.copy(alpha = 0.85f), style = Stroke(px(4.5f), cap = StrokeCap.Round))
                }

                // Eyebrows
                when {
                    isBeastPose -> {
                        drawLine(phaseColor, pt(16.5f, 20f), pt(26.2f, 24.4f), px(2.7f), cap = StrokeCap.Round)
                        drawLine(phaseColor, pt(47.5f, 20f), pt(37.8f, 24.4f), px(2.7f), cap = StrokeCap.Round)
                    }
                    isFocusedEyes || isFlexPose || isSalutePose -> {
                        drawLine(Color(0xFFD1D5DB), pt(17.5f, 21.5f), pt(25.5f, 23.8f), px(2.3f), cap = StrokeCap.Round)
                        drawLine(Color(0xFFD1D5DB), pt(46.5f, 21.5f), pt(38.5f, 23.8f), px(2.3f), cap = StrokeCap.Round)
                    }
                    isSmirkPose -> {
                        drawLine(Color(0xFFD1D5DB), pt(17.5f, 22.8f), pt(25.5f, 22.8f), px(2.1f), cap = StrokeCap.Round)
                        pathA.reset()
                        pathA.moveTo(px(38.5f), px(20.6f))
                        pathA.cubicTo(px(41f), px(18.2f), px(44.5f), px(18.4f), px(46.8f), px(20.8f))
                        drawPath(pathA, Color(0xFFD1D5DB), style = Stroke(px(2.2f), cap = StrokeCap.Round))
                    }
                    isCuriousPose -> {
                        pathA.reset()
                        pathA.moveTo(px(18f), px(22.5f))
                        pathA.cubicTo(px(20f), px(20.5f), px(23f), px(20.5f), px(25f), px(22.5f))
                        pathB.reset()
                        pathB.moveTo(px(39f), px(20f))
                        pathB.cubicTo(px(41f), px(17.8f), px(44f), px(17.8f), px(46f), px(20f))
                        drawPath(pathA, Color(0xFFD1D5DB), style = Stroke(px(2f), cap = StrokeCap.Round))
                        drawPath(pathB, Color(0xFFD1D5DB), style = Stroke(px(2.1f), cap = StrokeCap.Round))
                    }
                    isWinkPose || isWhistlePose -> {
                        pathA.reset()
                        pathA.moveTo(px(18f), px(21.5f))
                        pathA.cubicTo(px(20f), px(19.6f), px(23.5f), px(19.6f), px(25.5f), px(21.5f))
                        pathB.reset()
                        pathB.moveTo(px(39f), px(22.8f))
                        pathB.cubicTo(px(41f), px(21.5f), px(44f), px(21.5f), px(46f), px(22.8f))
                        drawPath(pathA, Color(0xFFD1D5DB), style = Stroke(px(2f), cap = StrokeCap.Round))
                        drawPath(pathB, Color(0xFFD1D5DB), style = Stroke(px(2f), cap = StrokeCap.Round))
                    }
                    isYawnPose || isSweatPose -> {
                        pathA.reset()
                        pathA.moveTo(px(18f), px(22f))
                        pathA.cubicTo(px(20f), px(20.5f), px(23.5f), px(21f), px(25f), px(22.5f))
                        pathB.reset()
                        pathB.moveTo(px(39f), px(22.5f))
                        pathB.cubicTo(px(40.5f), px(21f), px(44f), px(20.5f), px(46f), px(22f))
                        drawPath(pathA, Color(0xFF9CA3AF), style = Stroke(px(1.9f), cap = StrokeCap.Round))
                        drawPath(pathB, Color(0xFF9CA3AF), style = Stroke(px(1.9f), cap = StrokeCap.Round))
                    }
                }

                // Eyes (with glance offset strictly locked to zero when eyes are closed)
                val gx = if (isClosedEyeExpression) 0f else px(glanceXState.value)
                val gy = if (isClosedEyeExpression) 0f else px(glanceYState.value)
                fun gpt(x: Float, y: Float) = Offset(px(x) + gx, px(y) + gy)

                when {
                    isCelebration || isMusicPose || isSunrisePose || isSweatPose -> {
                        pathA.reset()
                        pathA.moveTo(px(18f) + gx, px(28f) + gy)
                        pathA.cubicTo(px(19.8f) + gx, px(24.4f) + gy, px(24.2f) + gx, px(24.4f) + gy, px(26f) + gx, px(28f) + gy)
                        pathB.reset()
                        pathB.moveTo(px(38f) + gx, px(28f) + gy)
                        pathB.cubicTo(px(39.8f) + gx, px(24.4f) + gy, px(44.2f) + gx, px(24.4f) + gy, px(46f) + gx, px(28f) + gy)
                        drawPath(pathA, Color(0xFFF4F5F7), style = Stroke(px(2.8f), cap = StrokeCap.Round))
                        drawPath(pathB, Color(0xFFF4F5F7), style = Stroke(px(2.8f), cap = StrokeCap.Round))
                    }
                    isSparklePose -> {
                        for (cx in listOf(22f, 42f)) {
                            pathA.reset()
                            pathA.moveTo(px(cx), px(23.2f))
                            pathA.lineTo(px(cx + 1.3f), px(26.2f))
                            pathA.lineTo(px(cx + 4.3f), px(27.5f))
                            pathA.lineTo(px(cx + 1.3f), px(28.8f))
                            pathA.lineTo(px(cx), px(31.8f))
                            pathA.lineTo(px(cx - 1.3f), px(28.8f))
                            pathA.lineTo(px(cx - 4.3f), px(27.5f))
                            pathA.lineTo(px(cx - 1.3f), px(26.2f))
                            pathA.close()
                            drawPath(pathA, Color(0xFFF4F5F7))
                            drawCircle(phaseColor, px(1.1f), pt(cx, 27.5f))
                        }
                    }
                    isBeastPose -> {
                        drawLine(Color(0xFFF4F5F7), gpt(18f, 26.5f), gpt(25.5f, 29f), px(2.4f), cap = StrokeCap.Round)
                        drawCircle(phaseColor, px(2.5f), gpt(22.5f, 28.2f))
                        drawLine(Color(0xFFF4F5F7), gpt(46f, 26.5f), gpt(38.5f, 29f), px(2.4f), cap = StrokeCap.Round)
                        drawCircle(phaseColor, px(2.5f), gpt(41.5f, 28.2f))
                    }
                    isSmirkPose -> {
                        drawLine(Color(0xFFF4F5F7), gpt(17.5f, 27.5f), gpt(26f, 27.5f), px(2.5f), cap = StrokeCap.Round)
                        drawCircle(Color(0xFFF4F5F7), px(1.9f), gpt(22f, 28.6f))
                        drawCircle(Color(0xFFF4F5F7), px(3.1f), gpt(42f, 27.2f))
                        drawCircle(phaseColor, px(1.0f), gpt(41f, 26.2f))
                    }
                    isWinkPose || isWhistlePose -> {
                        drawCircle(Color(0xFFF4F5F7), px(3.2f), gpt(22f, 27.5f))
                        drawCircle(phaseColor, px(1.05f), gpt(21f, 26.3f))
                        pathA.reset()
                        pathA.moveTo(px(38f) + gx, px(27.6f) + gy)
                        pathA.cubicTo(px(40f) + gx, px(24.8f) + gy, px(44f) + gx, px(24.8f) + gy, px(46f) + gx, px(27.6f) + gy)
                        drawPath(pathA, Color(0xFFF4F5F7), style = Stroke(px(2.7f), cap = StrokeCap.Round))
                    }
                    isShadesPose -> {
                        drawRoundRect(Color(0xFF0B0E14), gpt(14.5f, 23f), Size(px(14.5f), px(9.2f)), CornerRadius(px(3f)))
                        drawRoundRect(phaseColor, gpt(14.5f, 23f), Size(px(14.5f), px(9.2f)), CornerRadius(px(3f)), style = Stroke(px(2f)))
                        drawRoundRect(Color(0xFF0B0E14), gpt(35f, 23f), Size(px(14.5f), px(9.2f)), CornerRadius(px(3f)))
                        drawRoundRect(phaseColor, gpt(35f, 23f), Size(px(14.5f), px(9.2f)), CornerRadius(px(3f)), style = Stroke(px(2f)))
                        drawLine(phaseColor, gpt(29f, 26.5f), gpt(35f, 26.5f), px(2f), cap = StrokeCap.Round)
                        drawLine(Color.White.copy(alpha = 0.45f), gpt(17.5f, 25.5f), gpt(21f, 29.2f), px(1.5f), cap = StrokeCap.Round)
                        drawLine(Color.White.copy(alpha = 0.45f), gpt(38f, 25.5f), gpt(41.5f, 29.2f), px(1.5f), cap = StrokeCap.Round)
                    }
                    isSleepPose || isZenPose || isCozyPose || isDreamPose -> {
                        pathA.reset()
                        pathA.moveTo(px(17.5f) + gx, px(28.2f) + gy)
                        pathA.cubicTo(px(19.8f) + gx, px(31.2f) + gy, px(24.2f) + gx, px(31.2f) + gy, px(26.5f) + gx, px(28.2f) + gy)
                        pathB.reset()
                        pathB.moveTo(px(37.5f) + gx, px(28.2f) + gy)
                        pathB.cubicTo(px(39.8f) + gx, px(31.2f) + gy, px(44.2f) + gx, px(31.2f) + gy, px(46.5f) + gx, px(28.2f) + gy)
                        drawPath(pathA, Color(0xFFD1D5DB), style = Stroke(px(2.6f), cap = StrokeCap.Round))
                        drawPath(pathB, Color(0xFFD1D5DB), style = Stroke(px(2.6f), cap = StrokeCap.Round))
                    }
                    isYawnPose -> {
                        drawLine(Color(0xFFD1D5DB), gpt(17.5f, 27.8f), gpt(26.5f, 27.8f), px(2.5f), cap = StrokeCap.Round)
                        drawCircle(Color(0xFFF4F5F7), px(1.8f), gpt(22f, 29.4f))
                        pathA.reset()
                        pathA.moveTo(px(38f) + gx, px(28.4f) + gy)
                        pathA.cubicTo(px(40f) + gx, px(30.8f) + gy, px(44f) + gx, px(30.8f) + gy, px(46f) + gx, px(28.4f) + gy)
                        drawPath(pathA, Color(0xFFD1D5DB), style = Stroke(px(2.5f), cap = StrokeCap.Round))
                    }
                    isTeaPose -> {
                        pathA.reset()
                        pathA.moveTo(px(18f) + gx, px(28.2f) + gy)
                        pathA.cubicTo(px(19.8f) + gx, px(25.2f) + gy, px(24.2f) + gx, px(25.2f) + gy, px(26f) + gx, px(28.2f) + gy)
                        pathB.reset()
                        pathB.moveTo(px(38f) + gx, px(28.2f) + gy)
                        pathB.cubicTo(px(39.8f) + gx, px(25.2f) + gy, px(44.2f) + gx, px(25.2f) + gy, px(46f) + gx, px(28.2f) + gy)
                        drawPath(pathA, Color(0xFFF4F5F7), style = Stroke(px(2.6f), cap = StrokeCap.Round))
                        drawPath(pathB, Color(0xFFF4F5F7), style = Stroke(px(2.6f), cap = StrokeCap.Round))
                    }
                    isStargazePose || isMoonlitPose -> {
                        drawCircle(Color(0xFFF4F5F7), px(3.4f), gpt(22f, 27.2f))
                        drawCircle(Color(0xFFF4F5F7), px(3.4f), gpt(42f, 27.2f))
                        drawLine(phaseColor, gpt(22f, 25f), gpt(22f, 27.6f), px(1.25f), cap = StrokeCap.Round)
                        drawLine(phaseColor, gpt(20.7f, 26.3f), gpt(23.3f, 26.3f), px(1.25f), cap = StrokeCap.Round)
                        drawLine(phaseColor, gpt(42f, 25f), gpt(42f, 27.6f), px(1.25f), cap = StrokeCap.Round)
                        drawLine(phaseColor, gpt(40.7f, 26.3f), gpt(43.3f, 26.3f), px(1.25f), cap = StrokeCap.Round)
                    }
                    isCoffeePose || isProteinPose -> {
                        drawCircle(Color(0xFFF4F5F7), px(3.5f), gpt(22f, 27.2f))
                        drawCircle(Color(0xFFF4F5F7), px(3.5f), gpt(42f, 27.2f))
                        drawCircle(phaseColor, px(1.15f), gpt(21f, 25.9f))
                        drawCircle(phaseColor, px(1.15f), gpt(41f, 25.9f))
                    }
                    isCalmEyes || isRechargePose || eyeState.isPetting -> {
                        pathA.reset()
                        pathA.moveTo(px(18f) + gx, px(28.2f) + gy)
                        pathA.cubicTo(px(20f) + gx, px(30.8f) + gy, px(24f) + gx, px(30.8f) + gy, px(26f) + gx, px(28.2f) + gy)
                        pathB.reset()
                        pathB.moveTo(px(38f) + gx, px(28.2f) + gy)
                        pathB.cubicTo(px(40f) + gx, px(30.8f) + gy, px(44f) + gx, px(30.8f) + gy, px(46f) + gx, px(28.2f) + gy)
                        drawPath(pathA, Color(0xFFD1D5DB), style = Stroke(px(2.6f), cap = StrokeCap.Round))
                        drawPath(pathB, Color(0xFFD1D5DB), style = Stroke(px(2.6f), cap = StrokeCap.Round))
                    }
                    else -> {
                        val blinkT = smoothBlinkState.value
                        if (blinkT > 0.72f) {
                            pathA.reset()
                            pathA.moveTo(px(18f) + gx, px(27.8f) + gy)
                            pathA.cubicTo(px(20f) + gx, px(30.3f) + gy, px(24f) + gx, px(30.3f) + gy, px(26f) + gx, px(27.8f) + gy)
                            pathB.reset()
                            pathB.moveTo(px(38f) + gx, px(27.8f) + gy)
                            pathB.cubicTo(px(40f) + gx, px(30.3f) + gy, px(44f) + gx, px(30.3f) + gy, px(46f) + gx, px(27.8f) + gy)
                            drawPath(pathA, Color(0xFFD1D5DB), style = Stroke(px(2.5f), cap = StrokeCap.Round))
                            drawPath(pathB, Color(0xFFD1D5DB), style = Stroke(px(2.5f), cap = StrokeCap.Round))
                        } else {
                            val rBase = if (isPerkPose || isStretchPose || isFlexPose || isSalutePose) px(3.25f) else px(2.95f)
                            val openY = (1f - blinkT * 0.85f).coerceIn(0.18f, 1f)
                            val rH = rBase * openY
                            drawOval(
                                color = Color(0xFFF4F5F7),
                                topLeft = Offset(px(22f) + gx - rBase, px(27.5f) + gy - rH),
                                size = Size(rBase * 2f, rH * 2f)
                            )
                            drawOval(
                                color = Color(0xFFF4F5F7),
                                topLeft = Offset(px(42f) + gx - rBase, px(27.5f) + gy - rH),
                                size = Size(rBase * 2f, rH * 2f)
                            )
                            if (openY > 0.45f) {
                                drawCircle(phaseColor, px(0.95f), gpt(21f, 26.3f))
                                drawCircle(phaseColor, px(0.95f), gpt(41f, 26.3f))
                            }
                        }
                    }
                }

                // Cheek warmth + Cozy Petting Blush
                if (isCelebration || isTeaPose || isCoffeePose || isWinkPose || isStretchPose ||
                    isZenPose || isMusicPose || isSunrisePose || isSparklePose || isCozyPose ||
                    isDreamPose || isSweatPose || isPerkPose || eyeState.isPetting ||
                    eyeState.reactionKind == MascotReaction.COMBO_EXCITED
                ) {
                    val blushR = if (isCozyPose || isSparklePose || eyeState.isPetting) px(3.1f) else px(2.5f)
                    val blushAlpha = if (eyeState.isPetting) 0.44f else if (isCozyPose) 0.34f else 0.22f
                    val blushColor = if (eyeState.isPetting) Color(0xFFFB7185) else phaseColor
                    drawCircle(blushColor.copy(alpha = blushAlpha), blushR, pt(16.2f, 34f))
                    drawCircle(blushColor.copy(alpha = blushAlpha), blushR, pt(47.8f, 34f))
                }

                // Dynamic Mouth
                when {
                    isCelebration || isFlexPose || isBeastPose || isSparklePose -> {
                        pathA.reset()
                        pathA.moveTo(px(22.5f), px(36.5f))
                        pathA.cubicTo(px(25.5f), px(42.5f), px(38.5f), px(42.5f), px(41.5f), px(36.5f))
                        pathA.close()
                        drawPath(pathA, phaseColor.copy(alpha = 0.22f))
                        drawPath(pathA, phaseColor, style = Stroke(px(2.6f), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                    isWhistlePose -> {
                        drawCircle(phaseColor.copy(alpha = 0.2f), px(2.4f), pt(34f, 38.2f))
                        drawCircle(phaseColor, px(2.4f), pt(34f, 38.2f), style = Stroke(px(2.3f)))
                    }
                    isSmirkPose -> {
                        pathA.reset()
                        pathA.moveTo(px(25f), px(38.6f))
                        pathA.cubicTo(px(29.5f), px(40f), px(35.8f), px(39f), px(40.5f), px(35.4f))
                        drawPath(pathA, phaseColor, style = Stroke(px(2.6f), cap = StrokeCap.Round))
                    }
                    isYawnPose -> {
                        drawOval(phaseColor.copy(alpha = 0.18f), pt(28.9f, 35.2f), Size(px(6.2f), px(8f)))
                        drawOval(phaseColor, pt(28.9f, 35.2f), Size(px(6.2f), px(8f)), style = Stroke(px(2.3f)))
                    }
                    isSleepPose -> {
                        drawCircle(phaseColor.copy(alpha = 0.2f), px(2.1f), pt(32f, 38.5f))
                        drawCircle(phaseColor, px(2.1f), pt(32f, 38.5f), style = Stroke(px(2.1f)))
                    }
                    isCuriousPose || isStargazePose -> {
                        drawCircle(phaseColor, px(2.4f), pt(32f, 38.5f), style = Stroke(px(2.4f)))
                    }
                    isFocusedEyes -> {
                        drawLine(phaseColor, pt(25.5f, 38.5f), pt(38.5f, 38.5f), px(2.6f), cap = StrokeCap.Round)
                    }
                    isTeaPose || isCoffeePose || isProteinPose -> {
                        pathA.reset()
                        pathA.moveTo(px(24.5f), px(36.5f))
                        pathA.cubicTo(px(27.2f), px(39.5f), px(33.2f), px(39.5f), px(36f), px(36.5f))
                        drawPath(pathA, phaseColor, style = Stroke(px(2.5f), cap = StrokeCap.Round))
                    }
                    isShadesPose || isWinkPose || isSalutePose -> {
                        pathA.reset()
                        pathA.moveTo(px(24.5f), px(37.5f))
                        pathA.cubicTo(px(28.5f), px(40.8f), px(36f), px(40.2f), px(40f), px(36f))
                        drawPath(pathA, phaseColor, style = Stroke(px(2.7f), cap = StrokeCap.Round))
                    }
                    isNightChillPose || isZenPose || isCalmEyes || isCozyPose || isDreamPose || isMoonlitPose || isRechargePose -> {
                        pathA.reset()
                        pathA.moveTo(px(26.5f), px(37.5f))
                        pathA.cubicTo(px(29f), px(39.8f), px(35f), px(39.8f), px(37.5f), px(37.5f))
                        drawPath(pathA, phaseColor, style = Stroke(px(2.5f), cap = StrokeCap.Round))
                    }
                    else -> {
                        pathA.reset()
                        pathA.moveTo(px(23.5f), px(36.5f))
                        pathA.cubicTo(px(27f), px(40.8f), px(37f), px(40.8f), px(40.5f), px(36.5f))
                        drawPath(pathA, phaseColor, style = Stroke(px(2.8f), cap = StrokeCap.Round))
                    }
                }

                // Contextual Props
                if (isCelebration) {
                    drawLine(phaseColor, pt(11f, 18f), pt(15.5f, 20.5f), px(2.4f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(53f, 18f), pt(48.5f, 20.5f), px(2.4f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(32f, 9f), pt(32f, 14f), px(2.4f), cap = StrokeCap.Round)
                }
                if (isTeaPose) {
                    drawRoundRect(Color(0xFF1F2937), pt(41f, 38.5f), Size(px(11f), px(9f)), CornerRadius(px(2.5f)))
                    drawRoundRect(phaseColor, pt(41f, 38.5f), Size(px(11f), px(9f)), CornerRadius(px(2.5f)), style = Stroke(px(2f)))
                    drawLine(phaseColor, pt(44.8f, 35.5f), pt(44.8f, 31f), px(1.5f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(49.2f, 35.5f), pt(49.2f, 31f), px(1.5f), cap = StrokeCap.Round)
                }
                if (isCoffeePose) {
                    val amber = Color(0xFFFBBF24)
                    drawRoundRect(Color(0xFF291B12), pt(40.5f, 38f), Size(px(11.5f), px(9.5f)), CornerRadius(px(2.2f)))
                    drawRoundRect(amber, pt(40.5f, 38f), Size(px(11.5f), px(9.5f)), CornerRadius(px(2.2f)), style = Stroke(px(2f)))
                    drawLine(amber, pt(45.5f, 35f), pt(45.5f, 30f), px(1.7f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(14f, 14f), pt(14f, 18f), px(1.8f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(12f, 16f), pt(16f, 16f), px(1.8f), cap = StrokeCap.Round)
                }
                if (isSunrisePose) {
                    pathA.reset()
                    pathA.moveTo(px(24f), px(16.5f))
                    pathA.cubicTo(px(25.5f), px(12.2f), px(38.5f), px(12.2f), px(40f), px(16.5f))
                    drawPath(pathA, Color(0x59FBBF24))
                    drawPath(pathA, Color(0xFFFBBF24), style = Stroke(px(1.8f), cap = StrokeCap.Round))
                    drawLine(Color(0xFFFBBF24), pt(32f, 8f), pt(32f, 10.8f), px(1.8f), cap = StrokeCap.Round)
                }
                if (isProteinPose) {
                    drawRoundRect(Color(0xFF18202C), pt(42.5f, 36.5f), Size(px(9f), px(12f)), CornerRadius(px(2.2f)))
                    drawRoundRect(phaseColor, pt(42.5f, 36.5f), Size(px(9f), px(12f)), CornerRadius(px(2.2f)), style = Stroke(px(1.9f)))
                    drawLine(Color(0xFFF4F5F7), pt(41.5f, 36.5f), pt(52.5f, 36.5f), px(2.2f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(45f, 34f), pt(49f, 34f), px(2f), cap = StrokeCap.Round)
                }
                if (isSalutePose) {
                    drawLine(phaseColor, pt(44f, 15.5f), pt(48.5f, 11.5f), px(2.2f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(48.5f, 11.5f), pt(53f, 15.5f), px(2.2f), cap = StrokeCap.Round)
                }
                if (isSparklePose) {
                    drawLine(phaseColor, pt(14f, 13f), pt(14f, 18f), px(1.9f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(11.5f, 15.5f), pt(16.5f, 15.5f), px(1.9f), cap = StrokeCap.Round)
                    drawLine(Color(0xFFFDE68A), pt(50f, 13.5f), pt(50f, 18.5f), px(1.9f), cap = StrokeCap.Round)
                    drawLine(Color(0xFFFDE68A), pt(47.5f, 16f), pt(52.5f, 16f), px(1.9f), cap = StrokeCap.Round)
                }
                if (isBeastPose) {
                    pathA.reset()
                    pathA.moveTo(px(32f), px(7.5f))
                    pathA.cubicTo(px(34.8f), px(11f), px(36.2f), px(13.2f), px(35.4f), px(15.8f))
                    pathA.cubicTo(px(34.8f), px(17.5f), px(33.4f), px(18.4f), px(32f), px(18.4f))
                    pathA.cubicTo(px(30.6f), px(18.4f), px(29.2f), px(17.5f), px(28.6f), px(15.8f))
                    pathA.cubicTo(px(27.8f), px(13.2f), px(29.2f), px(11f), px(32f), px(7.5f))
                    pathA.close()
                    drawPath(pathA, phaseColor.copy(alpha = 0.9f))
                    drawPath(pathA, Color(0xFFFDE68A), style = Stroke(px(1.2f)))
                }
                if (isSweatPose || isHydratePose || isFocusedEyes) {
                    drawCircle(Color(0xFF38BDF8), px(3.2f), pt(50f, 20f))
                    drawCircle(Color(0xFFE0F2FE), px(1f), pt(49f, 18.8f))
                }
                if (isSmirkPose) {
                    drawLine(phaseColor, pt(48f, 13f), pt(48f, 23f), px(1.8f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(43f, 18f), pt(53f, 18f), px(1.8f), cap = StrokeCap.Round)
                }
                if (isWhistlePose || isMusicPose) {
                    drawLine(phaseColor, pt(48f, 18f), pt(48f, 11.5f), px(1.9f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(48f, 11.5f), pt(53.5f, 10.2f), px(1.9f), cap = StrokeCap.Round)
                    drawCircle(phaseColor, px(1.7f), pt(46.5f, 18.2f))
                }
                if (isDreamPose) {
                    drawCircle(phaseColor.copy(alpha = 0.7f), px(1.3f), pt(43.5f, 21f))
                    drawRoundRect(Color(0xFF1E293B), pt(43f, 10f), Size(px(11f), px(7f)), CornerRadius(px(3.5f)))
                    drawRoundRect(phaseColor, pt(43f, 10f), Size(px(11f), px(7f)), CornerRadius(px(3.5f)), style = Stroke(px(1.5f)))
                }
                if (isMoonlitPose || isYawnPose || isNightChillPose || isStargazePose) {
                    drawCircle(phaseColor, px(3.5f), pt(48f, 15.5f))
                    drawCircle(Color(0xFF121620), px(2.8f), pt(50f, 14f))
                    drawCircle(Color(0xFFE0E7FF), px(1.2f), pt(20f, 14f))
                }
                if (isRechargePose) {
                    drawRoundRect(phaseColor, pt(25f, 10f), Size(px(13f), px(6.8f)), CornerRadius(px(1.8f)), style = Stroke(px(1.6f)))
                    drawRect(phaseColor, pt(27f, 11.8f), Size(px(9f), px(3.2f)))
                }
                if (isWinkPose) {
                    drawLine(phaseColor, pt(49f, 14f), pt(49f, 19f), px(2f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(46.5f, 16.5f), pt(51.5f, 16.5f), px(2f), cap = StrokeCap.Round)
                }
                if (isFlexPose) {
                    drawLine(phaseColor, pt(48f, 11f), pt(44.5f, 17.5f), px(2.1f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(44.5f, 17.5f), pt(48.5f, 17.5f), px(2.1f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(48.5f, 17.5f), pt(45.5f, 23.5f), px(2.1f), cap = StrokeCap.Round)
                }
                if (isStretchPose) {
                    drawLine(phaseColor, pt(32f, 7.5f), pt(32f, 12.5f), px(2.3f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(21f, 10.5f), pt(24f, 14.5f), px(2.1f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(43f, 10.5f), pt(40f, 14.5f), px(2.1f), cap = StrokeCap.Round)
                }
                if (isSleepPose) {
                    drawLine(phaseColor, pt(44f, 17f), pt(50f, 17f), px(1.9f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(50f, 17f), pt(44f, 23f), px(1.9f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(44f, 23f), pt(50f, 23f), px(1.9f), cap = StrokeCap.Round)
                }
                if (isZenPose) {
                    drawCircle(phaseColor, px(3.2f), pt(32f, 14f), style = Stroke(px(1.7f)))
                }
                // Extra reaction micro-overlays for Combo Poke, Petting, and Quick Tips
                if (eyeState.reactionKind == MascotReaction.COMBO_EXCITED) {
                    drawLine(phaseColor, pt(12.5f, 13f), pt(12.5f, 18.5f), px(2f), cap = StrokeCap.Round)
                    drawLine(phaseColor, pt(9.8f, 15.8f), pt(15.2f, 15.8f), px(2f), cap = StrokeCap.Round)
                    drawLine(Color(0xFFFDE68A), pt(51.5f, 13f), pt(51.5f, 18.5f), px(2f), cap = StrokeCap.Round)
                    drawLine(Color(0xFFFDE68A), pt(48.8f, 15.8f), pt(54.2f, 15.8f), px(2f), cap = StrokeCap.Round)
                }
                if (eyeState.isPetting || eyeState.reactionKind == MascotReaction.PET_COZY) {
                    val warmHeart = Color(0xFFFB7185)
                    drawCircle(warmHeart, px(2.1f), pt(47.5f, 15.5f))
                    drawCircle(warmHeart, px(2.1f), pt(51f, 15.5f))
                    pathA.reset()
                    pathA.moveTo(px(45.5f), px(16.5f))
                    pathA.lineTo(px(49.25f), px(20.6f))
                    pathA.lineTo(px(53f), px(16.5f))
                    pathA.close()
                    drawPath(pathA, warmHeart)
                }
                if (eyeState.reactionKind == MascotReaction.TIP) {
                    drawCircle(Color(0xFFFBBF24), px(2.6f), pt(49.5f, 15.5f))
                    drawLine(Color(0xFFFDE68A), pt(49.5f, 10.5f), pt(49.5f, 12.2f), px(1.6f), cap = StrokeCap.Round)
                    drawLine(Color(0xFFFDE68A), pt(54f, 15.5f), pt(52.4f, 15.5f), px(1.6f), cap = StrokeCap.Round)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodayMascot(
    mascot: MascotUiState,
    eyeStateFlow: StateFlow<MascotEyeState>,
    voiceEnabled: Boolean,
    orbVisibleAlpha: () -> Float = { 1f },
    orbAnimActive: Boolean = true,
    hideForTutorial: Boolean = false,
    homeOrbBoundsHolder: OrbBoundsHolder? = null,
    onTapMascot: () -> Unit,
    onPetMascot: () -> Unit = {},
    onReplayVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val phaseColor = remember(mascot.phaseColorHex) {
        MaterialYouColorEngine.parseColor(mascot.phaseColorHex, tokens.phaseTokens.accentBright)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (hideForTutorial) 0f else 1f }
            .tactilePress(pressedScale = 0.985f, disabledByPerf = tokens.performance.noAnimations || hideForTutorial)
            .ironGlassSurface(
                tokens = tokens,
                subtle = true,
                borderOverride = phaseColor.copy(alpha = 0.28f),
                radiusOverride = tokens.bubbleRadius
            )
            .combinedClickable(
                enabled = !hideForTutorial,
                onClick = onTapMascot,
                onLongClick = onPetMascot
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .onGloballyPositioned { coords ->
                    if (coords.isAttached) {
                        val rect = coords.boundsInRoot()
                        if (rect.width > 10f) {
                            homeOrbBoundsHolder?.rect = rect
                        }
                    }
                }
                .graphicsLayer { alpha = orbVisibleAlpha() }
        ) {
            MascotOrbFace(
                mascot = mascot,
                eyeStateFlow = eyeStateFlow,
                size = 62.dp,
                animActive = orbAnimActive
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = mascot.activeLine,
                transitionSpec = {
                    if (tokens.performance.noAnimations) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(140))
                    }
                },
                label = "mascotLine"
            ) { line ->
                Column {
                    Text(
                        text = line.headline,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface,
                        lineHeight = 18.sp
                    )
                    if (line.subline.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = line.subline,
                            fontSize = 12.sp,
                            color = tokens.mutedTextColor,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        if (voiceEnabled) {
            IconButton(
                onClick = onReplayVoice,
                modifier = Modifier
                    .size(36.dp)
                    .tactilePress(disabledByPerf = tokens.performance.noAnimations)
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.VOLUME,
                    contentDescription = "Speak mascot line",
                    tint = phaseColor,
                    size = 18.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FloatingMascotDock(
    mascot: MascotUiState,
    eyeStateFlow: StateFlow<MascotEyeState>,
    anchorSideRight: Boolean,
    anchorYRatio: Float,
    shouldFloat: Boolean,
    morphProgressState: State<Float>,
    homeOrbBoundsHolder: OrbBoundsHolder,
    animActive: Boolean = true,
    onTapOrb: () -> Unit,
    onPetOrb: () -> Unit = {},
    onSaveAnchor: (sideRight: Boolean, yRatio: Float) -> Unit
) {
    val isDockActive by remember(shouldFloat) {
        derivedStateOf { shouldFloat || morphProgressState.value > 0.01f }
    }
    if (!isDockActive) return

    val tokens = LocalIronPulseTokens.current
    val noAnim = tokens.performance.noAnimations
    val density = LocalDensity.current
    val phaseColor = remember(mascot.phaseColorHex) {
        MaterialYouColorEngine.parseColor(mascot.phaseColorHex, tokens.phaseTokens.accentBright)
    }

    var bubbleOpen by remember { mutableStateOf(false) }
    val dockOriginHolder = remember { DockOriginHolder() }

    LaunchedEffect(shouldFloat) {
        if (!shouldFloat) bubbleOpen = false
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                if (coords.isAttached) {
                    dockOriginHolder.origin = coords.positionInRoot()
                }
            }
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val orbSizePx = with(density) { 56.dp.toPx() }
        val marginPx = with(density) { 14.dp.toPx() }
        val topSafePx = with(density) { 80.dp.toPx() }
        val bottomSafePx = with(density) { 110.dp.toPx() }
        val arcLiftPx = with(density) { 14.dp.toPx() }

        val targetX = if (anchorSideRight) (widthPx - orbSizePx - marginPx) else marginPx
        val targetY = (heightPx * anchorYRatio).coerceIn(topSafePx, (heightPx - orbSizePx - bottomSafePx).coerceAtLeast(topSafePx))

        var dragging by remember { mutableStateOf(false) }
        var dragX by remember(targetX, dragging) { mutableFloatStateOf(targetX) }
        var dragY by remember(targetY, dragging) { mutableFloatStateOf(targetY) }

        val edgeDisplayXState = animateFloatAsState(
            targetValue = if (dragging) dragX else targetX,
            animationSpec = if (dragging || noAnim) tween(0) else IronPulseMotion.OrbSnapSpring,
            label = "orbX"
        )
        val edgeDisplayYState = animateFloatAsState(
            targetValue = if (dragging) dragY else targetY,
            animationSpec = if (dragging || noAnim) tween(0) else IronPulseMotion.OrbSnapSpring,
            label = "orbY"
        )
        val dragScaleState = animateFloatAsState(
            targetValue = if (dragging && !noAnim) 1.09f else 1f,
            animationSpec = IronPulseMotion.OrbSnapSpring,
            label = "orbDragScale"
        )

        val bubbleW = with(density) { 252.dp.toPx() }
        var measuredBubbleH by remember { mutableIntStateOf(with(density) { 92.dp.toPx().toInt() }) }
        val bubbleVisible by remember(shouldFloat) {
            derivedStateOf { bubbleOpen && shouldFloat && !dragging && morphProgressState.value > 0.72f }
        }
        val orbInteractive by remember(shouldFloat) {
            derivedStateOf { shouldFloat && morphProgressState.value > 0.4f }
        }
        val bubbleOrigin = remember(anchorSideRight) {
            TransformOrigin(pivotFractionX = if (anchorSideRight) 1f else 0f, pivotFractionY = 0.5f)
        }

        fun computeOrbX(): Float {
            val edgeX = edgeDisplayXState.value
            if (dragging) return edgeX
            val bounds = homeOrbBoundsHolder.rect
            val hasBounds = bounds != null && !bounds.isEmpty && bounds.width > 10f
            val homeX = if (hasBounds) (bounds!!.left - dockOriginHolder.origin.x) else edgeX
            return lerp(homeX, edgeX, morphProgressState.value.coerceIn(0f, 1f))
        }

        fun computeOrbY(): Float {
            val edgeY = edgeDisplayYState.value
            if (dragging) return edgeY
            val t = morphProgressState.value.coerceIn(0f, 1f)
            val bounds = homeOrbBoundsHolder.rect
            val hasBounds = bounds != null && !bounds.isEmpty && bounds.width > 10f
            val homeY = if (hasBounds) (bounds!!.top - dockOriginHolder.origin.y) else edgeY
            val arcOffset = if (!hasBounds || noAnim) 0f else arcLiftPx * sin(t * PI).toFloat()
            return lerp(homeY, edgeY, t) - arcOffset
        }

        // Anchored, organic spring speech bubble next to floating orb
        AnimatedVisibility(
            visible = bubbleVisible,
            enter = if (noAnim) {
                EnterTransition.None
            } else {
                fadeIn(tween(200)) +
                    scaleIn(animationSpec = IronPulseMotion.BubbleSpring, initialScale = 0.65f, transformOrigin = bubbleOrigin) +
                    slideInHorizontally(tween(210)) { if (anchorSideRight) 18 else -18 }
            },
            exit = if (noAnim) {
                ExitTransition.None
            } else {
                fadeOut(tween(150)) +
                    scaleOut(animationSpec = tween(160), targetScale = 0.72f, transformOrigin = bubbleOrigin) +
                    slideOutHorizontally(tween(150)) { if (anchorSideRight) 16 else -16 }
            },
            modifier = Modifier.graphicsLayer {
                val orbX = computeOrbX()
                val orbY = computeOrbY()
                val bx = if (anchorSideRight) {
                    (orbX - bubbleW - 10f).coerceAtLeast(marginPx)
                } else {
                    (orbX + orbSizePx + 10f).coerceAtMost((widthPx - bubbleW - marginPx).coerceAtLeast(marginPx))
                }
                val centeredBy = orbY + (orbSizePx - measuredBubbleH.toFloat()) * 0.5f
                val maxBy = (heightPx - measuredBubbleH.toFloat() - bottomSafePx).coerceAtLeast(topSafePx)
                translationX = bx
                translationY = centeredBy.coerceIn(topSafePx, maxBy)
            }
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 200.dp, max = 252.dp)
                    .onSizeChanged { measuredBubbleH = it.height }
                    .ironGlassSurface(
                        tokens = tokens,
                        borderOverride = phaseColor.copy(alpha = 0.45f),
                        radiusOverride = tokens.bubbleRadius
                    )
                    .then(if (noAnim) Modifier else Modifier.animateContentSize(IronPulseMotion.BubbleSizeSpring))
                    .padding(13.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    AnimatedContent(
                        targetState = mascot.activeLine,
                        transitionSpec = {
                            if (noAnim) {
                                EnterTransition.None togetherWith ExitTransition.None
                            } else {
                                (fadeIn(tween(190))) togetherWith (fadeOut(tween(120)))
                            }
                        },
                        label = "floatingBubbleLine",
                        modifier = Modifier.weight(1f)
                    ) { line ->
                        Column {
                            Text(
                                text = line.headline,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.palette.onSurface,
                                lineHeight = 17.sp
                            )
                            if (line.subline.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = line.subline,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.5.sp,
                                    color = tokens.mutedTextColor
                                )
                            }
                        }
                    }
                    ThemedIcon(
                        icon = IronPulseIcon.CLOSE,
                        contentDescription = "Tutup",
                        tint = tokens.mutedTextColor,
                        size = 18.dp,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .clickable { bubbleOpen = false }
                    )
                }
            }
        }

        // Draggable Floating Orb (GPU translationX/Y + scale — 120 FPS morph & velocity snap)
        Box(
            modifier = Modifier
                .graphicsLayer {
                    val t = morphProgressState.value.coerceIn(0f, 1f)
                    val bounds = homeOrbBoundsHolder.rect
                    val hasBounds = bounds != null && !bounds.isEmpty && bounds.width > 10f
                    val homeScale = if (hasBounds) (bounds!!.width / orbSizePx).coerceIn(0.85f, 1.35f) else 0.72f
                    val scale = lerp(homeScale, dragScaleState.value, t)
                    translationX = computeOrbX()
                    translationY = computeOrbY()
                    scaleX = scale
                    scaleY = scale
                    alpha = if (hasBounds) 1f else t
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .size(56.dp)
                .tactilePress(pressedScale = 0.92f, disabledByPerf = noAnim || !shouldFloat)
                .then(
                    if (orbInteractive) {
                        Modifier
                            .pointerInput(widthPx, heightPx) {
                                val velocityTracker = VelocityTracker()
                                detectDragGestures(
                                    onDragStart = {
                                        dragging = true
                                        bubbleOpen = false
                                        velocityTracker.resetTracking()
                                    },
                                    onDragEnd = {
                                        dragging = false
                                        val velocity = velocityTracker.calculateVelocity()
                                        val projectedX = dragX + velocity.x * 0.16f + orbSizePx / 2f
                                        val projectedY = (dragY + velocity.y * 0.10f)
                                            .coerceIn(topSafePx, (heightPx - orbSizePx - bottomSafePx).coerceAtLeast(topSafePx))
                                        val snapRight = projectedX >= widthPx / 2f
                                        val yRatio = (projectedY / heightPx).coerceIn(0.14f, 0.82f)
                                        onSaveAnchor(snapRight, yRatio)
                                    },
                                    onDragCancel = { dragging = false },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                                        dragX = (dragX + dragAmount.x).coerceIn(marginPx, (widthPx - orbSizePx - marginPx).coerceAtLeast(marginPx))
                                        dragY = (dragY + dragAmount.y).coerceIn(topSafePx, (heightPx - orbSizePx - bottomSafePx).coerceAtLeast(topSafePx))
                                    }
                                )
                            }
                            .combinedClickable(
                                onClick = {
                                    onTapOrb()
                                    bubbleOpen = true
                                },
                                onLongClick = {
                                    onPetOrb()
                                    bubbleOpen = true
                                }
                            )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            MascotOrbFace(
                mascot = mascot,
                eyeStateFlow = eyeStateFlow,
                size = 54.dp,
                animActive = animActive
            )
        }
    }
}
