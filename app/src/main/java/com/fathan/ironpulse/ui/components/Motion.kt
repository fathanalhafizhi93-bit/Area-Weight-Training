package com.fathan.ironpulse.ui.components

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.fathan.ironpulse.model.ThemeAnimationStyle
import com.fathan.ironpulse.ui.theme.LocalSuppressTabCardFade
import com.fathan.ironpulse.ui.theme.LocalTabSettled
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

@Immutable
object IronPulseMotion {
    val PressSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.68f,
        stiffness = 780f
    )

    val MorphSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.72f,
        stiffness = 560f
    )

    val ReturnMorphSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.78f,
        stiffness = 500f
    )

    val OrbSnapSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.72f,
        stiffness = 580f
    )

    val BubbleSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.72f,
        stiffness = 640f
    )

    val BubbleSizeSpring: SpringSpec<IntSize> = spring(
        dampingRatio = 0.78f,
        stiffness = 660f
    )

    val FluidCardSpring: SpringSpec<IntSize> = spring(
        dampingRatio = 0.78f,
        stiffness = 640f
    )

    val TabSlideSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.86f,
        stiffness = 780f
    )

    val ModalEntranceSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.78f,
        stiffness = 720f
    )

    val StaggerCardSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.80f,
        stiffness = 680f
    )

    val MascotBlinkSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.66f,
        stiffness = 740f
    )

    val MascotGazeSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.66f,
        stiffness = 280f
    )

    val MascotHeadTiltSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.66f,
        stiffness = 290f
    )

    val IndicatorDpSpring: SpringSpec<Dp> = spring(
        dampingRatio = 0.76f,
        stiffness = 680f
    )

    val IndicatorFloatSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.76f,
        stiffness = 700f
    )

    val ShortFadeSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 115, easing = FastOutSlowInEasing)
}

@Immutable
enum class TabChoreography {
    HORIZONTAL_GLIDE,
    VERTICAL_ELASTIC
}

@Immutable
data class ThemeMotionSpec(
    val style: ThemeAnimationStyle,
    val tabChoreography: TabChoreography,
    // 1. Tab transitions
    val tabSlideSpring: SpringSpec<Float>,
    val tabSlideFraction: Float,
    val tabEnterScale: Float,
    val tabExitScale: Float,
    val tabPerspectiveDeg: Float,
    // 2. Card entrance
    val cardRevealSpring: SpringSpec<Float>,
    val cardFadeMs: Int,
    val cardSlideYDp: Float,
    val cardSlideXDp: Float,
    val cardStartScale: Float,
    // 3. Tactile press
    val pressSpring: SpringSpec<Float>,
    val pressDepthFactor: Float,
    // 4. Bottom nav indicator & icon
    val navIndicatorSpring: SpringSpec<Float>,
    val navIconSpring: SpringSpec<Float>,
    val navActiveScale: Float,
    // 5. CTA sheen (exclusively for primary Start Workout CTA)
    val sheenPeakAlpha: Float,
    val sheenDurationMs: Int,
    // 6. Hero atmosphere & clock orbital ring
    val heroBreathMs: Int,
    val heroOrbitMs: Int,
    val heroAuraSwing: Float,
    // 7. Focus card surface rim highlight
    val surfaceRimAlpha: Float,
    // 8. Modal & dialog entrance
    val modalSpring: SpringSpec<Float>,
    val modalFadeMs: Int,
    val modalStartScale: Float,
    // 9. Backdrop cadence & ambient breathing
    val backdropCadenceScale: Float,
    val backdropBreathAlpha: Float,
    // 10. Mascot flight & progress fill
    val mascotMorphSpring: SpringSpec<Float>,
    val mascotReturnSpring: SpringSpec<Float>,
    val progressFillMs: Int
)

object MotionStyleCatalog {
    private val specs: Map<ThemeAnimationStyle, ThemeMotionSpec> = mapOf(
        ThemeAnimationStyle.SILK_GLIDE to ThemeMotionSpec(
            style = ThemeAnimationStyle.SILK_GLIDE,
            tabChoreography = TabChoreography.HORIZONTAL_GLIDE,
            tabSlideSpring = spring(dampingRatio = 0.88f, stiffness = 740f),
            tabSlideFraction = 1.0f,
            tabEnterScale = 0.985f,
            tabExitScale = 0.98f,
            tabPerspectiveDeg = 0f,
            cardRevealSpring = IronPulseMotion.StaggerCardSpring,
            cardFadeMs = 115,
            cardSlideYDp = 16f,
            cardSlideXDp = 0f,
            cardStartScale = 0.97f,
            pressSpring = IronPulseMotion.PressSpring,
            pressDepthFactor = 1.0f,
            navIndicatorSpring = IronPulseMotion.IndicatorFloatSpring,
            navIconSpring = IronPulseMotion.PressSpring,
            navActiveScale = 1.07f,
            sheenPeakAlpha = 0.54f,
            sheenDurationMs = 2100,
            heroBreathMs = 3400,
            heroOrbitMs = 11500,
            heroAuraSwing = 0.08f,
            surfaceRimAlpha = 0.18f,
            modalSpring = IronPulseMotion.ModalEntranceSpring,
            modalFadeMs = 110,
            modalStartScale = 0.94f,
            backdropCadenceScale = 0.95f,
            backdropBreathAlpha = 0.08f,
            mascotMorphSpring = IronPulseMotion.MorphSpring,
            mascotReturnSpring = IronPulseMotion.ReturnMorphSpring,
            progressFillMs = 190
        ),
        ThemeAnimationStyle.ELASTIC_POP to ThemeMotionSpec(
            style = ThemeAnimationStyle.ELASTIC_POP,
            tabChoreography = TabChoreography.VERTICAL_ELASTIC,
            tabSlideSpring = spring(dampingRatio = 0.68f, stiffness = 640f),
            tabSlideFraction = 1.0f,
            tabEnterScale = 0.94f,
            tabExitScale = 0.94f,
            tabPerspectiveDeg = 0f,
            cardRevealSpring = spring(dampingRatio = 0.58f, stiffness = 640f),
            cardFadeMs = 115,
            cardSlideYDp = 22f,
            cardSlideXDp = 0f,
            cardStartScale = 0.93f,
            pressSpring = spring(dampingRatio = 0.48f, stiffness = 760f),
            pressDepthFactor = 1.24f,
            navIndicatorSpring = spring(dampingRatio = 0.56f, stiffness = 660f),
            navIconSpring = spring(dampingRatio = 0.50f, stiffness = 720f),
            navActiveScale = 1.16f,
            sheenPeakAlpha = 0.58f,
            sheenDurationMs = 1950,
            heroBreathMs = 3000,
            heroOrbitMs = 9800,
            heroAuraSwing = 0.11f,
            surfaceRimAlpha = 0.24f,
            modalSpring = spring(dampingRatio = 0.56f, stiffness = 660f),
            modalFadeMs = 105,
            modalStartScale = 0.88f,
            backdropCadenceScale = 0.88f,
            backdropBreathAlpha = 0.095f,
            mascotMorphSpring = spring(dampingRatio = 0.56f, stiffness = 580f),
            mascotReturnSpring = spring(dampingRatio = 0.60f, stiffness = 520f),
            progressFillMs = 200
        )
    )

    fun resolve(style: ThemeAnimationStyle): ThemeMotionSpec =
        specs[style] ?: specs.getValue(ThemeAnimationStyle.SILK_GLIDE)
}

val LocalThemeMotionSpec = staticCompositionLocalOf {
    MotionStyleCatalog.resolve(ThemeAnimationStyle.SILK_GLIDE)
}

/**
 * Pure GPU-layer sheen sweep rendered once via [drawWithCache] and translated exclusively in
 * [graphicsLayer] so active CTAs shimmer clearly at 60–120 FPS even while scrolling.
 */
@Composable
fun GpuSheenOverlay(
    active: Boolean,
    tint: Color = Color.White,
    peakAlpha: Float = LocalThemeMotionSpec.current.sheenPeakAlpha,
    durationMs: Int = LocalThemeMotionSpec.current.sheenDurationMs,
    modifier: Modifier = Modifier
) {
    val shouldSweep = active &&
        peakAlpha > 0.005f &&
        LocalTabSettled.current
    if (!shouldSweep) return

    val infinite = rememberInfiniteTransition(label = "gpuSheenInfinite")
    val safeDuration = durationMs.coerceIn(1400, 6000)
    val sweepStartMs = (safeDuration * 0.36f).toInt()
    val progress = infinite.animateFloat(
        initialValue = -0.55f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = safeDuration
                -0.55f at 0 using LinearEasing
                -0.55f at sweepStartMs using FastOutSlowInEasing
                1.55f at safeDuration
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "gpuSheenProgress"
    )

    Box(modifier = modifier.clipToBounds()) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = (progress.value - 0.5f) * size.width * 1.18f
                }
                .drawWithCache {
                    if (size.width <= 2f || size.height <= 2f) {
                        return@drawWithCache onDrawBehind {}
                    }
                    val bandWidth = (size.width * 0.48f).coerceAtLeast(48f)
                    val cx = size.width * 0.5f
                    val sweepBrush = Brush.linearGradient(
                        0.0f to Color.Transparent,
                        0.26f to tint.copy(alpha = (peakAlpha * 0.42f).coerceIn(0f, 1f)),
                        0.50f to tint.copy(alpha = peakAlpha.coerceIn(0f, 1f)),
                        0.74f to tint.copy(alpha = (peakAlpha * 0.42f).coerceIn(0f, 1f)),
                        1.0f to Color.Transparent,
                        start = Offset(cx - bandWidth * 0.56f, 0f),
                        end = Offset(cx + bandWidth * 0.56f, size.height)
                    )
                    onDrawBehind {
                        drawRect(sweepBrush)
                    }
                }
        )
    }
}

/**
 * Zero-recomposition GPU-only staggered spring entrance for screen cards & list rows.
 * Waits while [active] is false (e.g. pre-warmed inactive tabs) and cascades in when [active] becomes true.
 * Automatically honors [LocalThemeMotionSpec].
 */
fun Modifier.staggeredCardEntrance(
    index: Int,
    active: Boolean = true,
    disabledByPerf: Boolean = false
): Modifier = if (disabledByPerf) {
    this
} else {
    this then StaggeredCardEntranceElement(index = index, active = active)
}

private data class StaggeredCardEntranceElement(
    val index: Int,
    val active: Boolean
) : ModifierNodeElement<StaggeredCardEntranceNode>() {
    override fun create(): StaggeredCardEntranceNode = StaggeredCardEntranceNode(index, active)
    override fun update(node: StaggeredCardEntranceNode) {
        node.updateState(index, active)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "staggeredCardEntrance"
        properties["index"] = index
        properties["active"] = active
    }
}

private class StaggeredCardEntranceNode(
    private var index: Int,
    private var active: Boolean
) : Modifier.Node(), LayoutModifierNode, CompositionLocalConsumerModifierNode {

    private val alphaAnim = Animatable(1f)
    private val offsetFractionAnim = Animatable(0f)
    private val scaleAnim = Animatable(1f)
    private var animJob: Job? = null
    private var revealedForActiveSession = false
    private var activeSlideYDp = 20f
    private var activeSlideXDp = 0f

    private fun snapRevealedNow() {
        animJob?.cancel()
        animJob = null
        revealedForActiveSession = true
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            alphaAnim.snapTo(1f)
            offsetFractionAnim.snapTo(0f)
            scaleAnim.snapTo(1f)
        }
    }

    private fun triggerReveal() {
        if (!isAttached) return
        if (currentValueOf(LocalSuppressTabCardFade)) {
            snapRevealedNow()
            return
        }
        val spec = currentValueOf(LocalThemeMotionSpec)
        activeSlideYDp = spec.cardSlideYDp
        activeSlideXDp = spec.cardSlideXDp
        animJob?.cancel()
        revealedForActiveSession = true
        animJob = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            alphaAnim.snapTo(0f)
            offsetFractionAnim.snapTo(1f)
            scaleAnim.snapTo(spec.cardStartScale)
            launch(start = CoroutineStart.UNDISPATCHED) {
                alphaAnim.animateTo(1f, tween(spec.cardFadeMs, easing = FastOutSlowInEasing))
            }
            launch(start = CoroutineStart.UNDISPATCHED) {
                offsetFractionAnim.animateTo(0f, spec.cardRevealSpring)
            }
            launch(start = CoroutineStart.UNDISPATCHED) {
                scaleAnim.animateTo(1f, spec.cardRevealSpring)
            }
        }
    }

    fun updateState(nextIndex: Int, nextActive: Boolean) {
        val suppressFade = if (isAttached) currentValueOf(LocalSuppressTabCardFade) else false
        val becameActive = !active && nextActive
        index = nextIndex
        active = nextActive
        if (suppressFade) {
            if (!revealedForActiveSession) {
                snapRevealedNow()
            }
            return
        }
        if (becameActive || (nextActive && !revealedForActiveSession)) {
            triggerReveal()
        } else if (!nextActive) {
            revealedForActiveSession = false
        }
    }

    override fun onAttach() {
        if (currentValueOf(LocalSuppressTabCardFade)) {
            snapRevealedNow()
            return
        }
        if (active && !revealedForActiveSession) {
            triggerReveal()
        } else if (!active) {
            coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                alphaAnim.snapTo(0f)
                offsetFractionAnim.snapTo(1f)
            }
        }
    }

    override fun onDetach() {
        animJob?.cancel()
        animJob = null
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        val a = alphaAnim.value.coerceIn(0f, 1f)
        val tFrac = offsetFractionAnim.value
        val s = scaleAnim.value
        val settled = !alphaAnim.isRunning &&
            !offsetFractionAnim.isRunning &&
            !scaleAnim.isRunning &&
            abs(a - 1f) < 0.002f &&
            abs(tFrac) < 0.002f &&
            abs(s - 1f) < 0.002f
        val density = if (settled) 1f else currentValueOf(LocalDensity).density
        val slideYPx = activeSlideYDp * density
        val slideXPx = activeSlideXDp * density
        return layout(placeable.width, placeable.height) {
            if (settled) {
                placeable.place(0, 0)
            } else {
                placeable.placeWithLayer(0, 0) {
                    alpha = a
                    translationY = tFrac * slideYPx
                    translationX = tFrac * slideXPx
                    scaleX = s
                    scaleY = s
                    transformOrigin = TransformOrigin(0.5f, 0.35f)
                }
            }
        }
    }
}

/**
 * Zero-recomposition GPU-only fluid spring entrance for modal cards and dialogs.
 * Automatically honors [LocalThemeMotionSpec].
 */
fun Modifier.fluidModalEntrance(
    disabledByPerf: Boolean = false
): Modifier = if (disabledByPerf) {
    this
} else {
    this then FluidModalEntranceElement
}

private data object FluidModalEntranceElement : ModifierNodeElement<FluidModalEntranceNode>() {
    override fun create(): FluidModalEntranceNode = FluidModalEntranceNode()
    override fun update(node: FluidModalEntranceNode) {}
    override fun InspectorInfo.inspectableProperties() {
        name = "fluidModalEntrance"
    }
}

private class FluidModalEntranceNode :
    Modifier.Node(),
    LayoutModifierNode,
    CompositionLocalConsumerModifierNode {
    private val scaleAnim = Animatable(0.93f)
    private val alphaAnim = Animatable(0f)

    override fun onAttach() {
        val spec = currentValueOf(LocalThemeMotionSpec)
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            scaleAnim.snapTo(spec.modalStartScale)
            alphaAnim.snapTo(0f)
            launch(start = CoroutineStart.UNDISPATCHED) {
                scaleAnim.animateTo(1f, spec.modalSpring)
            }
            launch(start = CoroutineStart.UNDISPATCHED) {
                alphaAnim.animateTo(1f, tween(spec.modalFadeMs, easing = FastOutSlowInEasing))
            }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        val s = scaleAnim.value
        val a = alphaAnim.value
        val settled = !scaleAnim.isRunning &&
            !alphaAnim.isRunning &&
            abs(s - 1f) < 0.001f &&
            abs(a - 1f) < 0.002f
        return layout(placeable.width, placeable.height) {
            if (settled) {
                placeable.place(0, 0)
            } else {
                placeable.placeWithLayer(0, 0) {
                    scaleX = s
                    scaleY = s
                    alpha = a
                    transformOrigin = TransformOrigin.Center
                }
            }
        }
    }
}

/**
 * Zero-allocation, zero-recomposition Modifier.Node implementation of tactile spring press.
 * Uses PointerEventPass.Initial so it never intercepts clicks or vertical scrolling, and drives
 * GPU scale strictly inside placeWithLayer using [LocalThemeMotionSpec].
 */
fun Modifier.tactilePress(
    enabled: Boolean = true,
    pressedScale: Float = 0.968f,
    disabledByPerf: Boolean = false
): Modifier = if (!enabled || disabledByPerf) {
    this
} else {
    this then TactilePressElement(pressedScale)
}

private data class TactilePressElement(
    val pressedScale: Float
) : ModifierNodeElement<TactilePressNode>() {
    override fun create(): TactilePressNode = TactilePressNode(pressedScale)
    override fun update(node: TactilePressNode) {
        node.pressedScale = pressedScale
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "tactilePress"
        properties["pressedScale"] = pressedScale
    }
}

private class TactilePressNode(
    var pressedScale: Float
) : Modifier.Node(),
    PointerInputModifierNode,
    LayoutModifierNode,
    CompositionLocalConsumerModifierNode {

    private val scaleAnim = Animatable(1f)
    private var animJob: Job? = null
    private var isPressed = false
    private var downX = 0f
    private var downY = 0f

    private fun setPressed(next: Boolean) {
        if (isPressed == next) return
        isPressed = next
        if (!isAttached) return
        val spec = currentValueOf(LocalThemeMotionSpec)
        val depth = (1f - pressedScale) * spec.pressDepthFactor
        val target = if (next) (1f - depth).coerceIn(0.84f, 0.992f) else 1f
        animJob?.cancel()
        animJob = coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            scaleAnim.animateTo(target, spec.pressSpring)
        }
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        if (pass != PointerEventPass.Initial) return
        when (pointerEvent.type) {
            PointerEventType.Press -> {
                val first = pointerEvent.changes.firstOrNull()
                if (first != null) {
                    downX = first.position.x
                    downY = first.position.y
                    setPressed(true)
                }
            }
            PointerEventType.Move -> {
                if (isPressed) {
                    val touchSlop = currentValueOf(LocalViewConfiguration).touchSlop
                    val first = pointerEvent.changes.firstOrNull()
                    if (first == null ||
                        abs(first.position.x - downX) > touchSlop ||
                        abs(first.position.y - downY) > touchSlop
                    ) {
                        setPressed(false)
                    }
                }
            }
            PointerEventType.Release, PointerEventType.Exit -> {
                setPressed(false)
            }
            else -> {
                if (!pointerEvent.changes.any { it.pressed }) {
                    setPressed(false)
                }
            }
        }
    }

    override fun onCancelPointerInput() {
        setPressed(false)
    }

    override fun onDetach() {
        isPressed = false
        animJob?.cancel()
        animJob = null
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        val s = scaleAnim.value
        val settled = !scaleAnim.isRunning && abs(s - 1f) < 0.001f
        return layout(placeable.width, placeable.height) {
            if (settled) {
                placeable.place(0, 0)
            } else {
                placeable.placeWithLayer(0, 0) {
                    scaleX = s
                    scaleY = s
                }
            }
        }
    }
}

object ScaledBackdropCache {
    val bitmaps = ConcurrentHashMap<Long, ImageBitmap>()

    private fun keyFor(@DrawableRes resId: Int, targetMaxDimPx: Int): Long =
        (resId.toLong() shl 20) or targetMaxDimPx.toLong()

    fun getCached(@DrawableRes resId: Int, targetMaxDimPx: Int = 1024): ImageBitmap? =
        bitmaps[keyFor(resId, targetMaxDimPx)]

    fun preload(context: Context, @DrawableRes resId: Int, targetMaxDimPx: Int = 1024) {
        val key = keyFor(resId, targetMaxDimPx)
        if (bitmaps.containsKey(key)) return
        decodeSampledBitmap(context.resources, resId, targetMaxDimPx)?.let { img ->
            bitmaps.putIfAbsent(key, img)
        }
    }
}

/**
 * Decodes and downsamples large drawable backdrops once on Dispatchers.IO and keeps
 * a process-wide memory cache so cold start & tab switches render on Frame 1 without pop-in.
 */
@Composable
fun rememberScaledResourceBitmap(
    @DrawableRes resId: Int,
    targetMaxDimPx: Int = 1024
): ImageBitmap? {
    val context = LocalContext.current
    val cacheKey = (resId.toLong() shl 20) or targetMaxDimPx.toLong()
    val cached = ScaledBackdropCache.bitmaps[cacheKey]
    if (cached != null) return cached

    val bitmapState = produceState<ImageBitmap?>(initialValue = null, resId, targetMaxDimPx) {
        value = ScaledBackdropCache.bitmaps[cacheKey] ?: withContext(Dispatchers.IO) {
            decodeSampledBitmap(context.resources, resId, targetMaxDimPx)?.also { img ->
                ScaledBackdropCache.bitmaps[cacheKey] = img
            }
        }
    }
    return bitmapState.value
}

private fun decodeSampledBitmap(
    res: Resources,
    @DrawableRes resId: Int,
    targetMaxDimPx: Int
): ImageBitmap? {
    return runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(res, resId, bounds)
        var inSampleSize = 1
        val maxSide = maxOf(bounds.outWidth, bounds.outHeight)
        if (maxSide > targetMaxDimPx) {
            while (maxSide / (inSampleSize * 2) >= targetMaxDimPx) {
                inSampleSize *= 2
            }
        }
        val decodeOpts = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
        }
        BitmapFactory.decodeResource(res, resId, decodeOpts)?.asImageBitmap()
    }.getOrNull()
}
