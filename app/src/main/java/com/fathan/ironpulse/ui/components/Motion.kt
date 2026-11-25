package com.fathan.ironpulse.ui.components

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
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
        stiffness = 520f
    )

    val MorphSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.78f,
        stiffness = 440f
    )

    val ReturnMorphSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.80f,
        stiffness = 240f
    )

    val OrbSnapSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.72f,
        stiffness = 380f
    )

    val BubbleSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.76f,
        stiffness = 480f
    )

    val BubbleSizeSpring: SpringSpec<IntSize> = spring(
        dampingRatio = 0.80f,
        stiffness = 460f
    )

    val FluidCardSpring: SpringSpec<IntSize> = spring(
        dampingRatio = 0.82f,
        stiffness = 440f
    )

    val TabSlideSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.86f,
        stiffness = 520f
    )

    val ModalEntranceSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.78f,
        stiffness = 480f
    )

    val IndicatorDpSpring: SpringSpec<Dp> = spring(
        dampingRatio = 0.75f,
        stiffness = 420f
    )

    val IndicatorFloatSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.76f,
        stiffness = 440f
    )

    val ShortFadeSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 180)
}

/**
 * Zero-recomposition GPU-only fluid spring entrance for modal cards and dialogs.
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

private class FluidModalEntranceNode : Modifier.Node(), LayoutModifierNode {
    private val scaleAnim = Animatable(0.93f)
    private val alphaAnim = Animatable(0f)

    override fun onAttach() {
        coroutineScope.launch {
            launch { scaleAnim.animateTo(1f, IronPulseMotion.ModalEntranceSpring) }
            launch { alphaAnim.animateTo(1f, IronPulseMotion.ShortFadeSpec) }
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                val s = scaleAnim.value
                scaleX = s
                scaleY = s
                alpha = alphaAnim.value
                transformOrigin = TransformOrigin.Center
            }
        }
    }
}

/**
 * Zero-allocation, zero-recomposition Modifier.Node implementation of tactile spring press.
 * Uses PointerEventPass.Initial so it never intercepts clicks or vertical scrolling, and drives
 * GPU scale strictly inside placeWithLayer without idle coroutines when untouched.
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
        val target = if (next) pressedScale else 1f
        animJob?.cancel()
        animJob = coroutineScope.launch {
            scaleAnim.animateTo(target, IronPulseMotion.PressSpring)
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
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                val s = scaleAnim.value
                scaleX = s
                scaleY = s
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
