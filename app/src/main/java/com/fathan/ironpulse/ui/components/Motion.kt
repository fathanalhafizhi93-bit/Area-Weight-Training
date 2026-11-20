package com.fathan.ironpulse.ui.components

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

@Immutable
object IronPulseMotion {
    val PressSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.72f,
        stiffness = Spring.StiffnessMedium
    )

    val MorphSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.82f,
        stiffness = 380f
    )

    val OrbSnapSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.74f,
        stiffness = 340f
    )

    val BubbleSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.80f,
        stiffness = 440f
    )

    val BubbleSizeSpring: SpringSpec<IntSize> = spring(
        dampingRatio = 0.84f,
        stiffness = 420f
    )

    val FluidCardSpring: SpringSpec<IntSize> = spring(
        dampingRatio = 0.86f,
        stiffness = 400f
    )

    val TabSlideSpring: SpringSpec<IntOffset> = spring(
        dampingRatio = 0.88f,
        stiffness = 420f
    )

    val ModalEntranceSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.82f,
        stiffness = 440f
    )

    val IndicatorDpSpring: SpringSpec<Dp> = spring(
        dampingRatio = 0.78f,
        stiffness = Spring.StiffnessMediumLow
    )

    val ShortFadeSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 200)
}

/**
 * GPU-only fluid spring entrance for modal cards and dialogs.
 */
fun Modifier.fluidModalEntrance(
    disabledByPerf: Boolean = false
): Modifier = composed {
    if (disabledByPerf) return@composed this
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val scale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.93f,
        animationSpec = IronPulseMotion.ModalEntranceSpring,
        label = "modalScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = IronPulseMotion.ShortFadeSpec,
        label = "modalAlpha"
    )
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    }
}

/**
 * GPU-only tactile spring press scale applied on PointerEventPass.Initial so
 * it works seamlessly on Buttons, Surfaces, Cards, and clickables without
 * intercepting click handlers or blocking vertical scroll gestures.
 */
fun Modifier.tactilePress(
    enabled: Boolean = true,
    pressedScale: Float = 0.968f,
    disabledByPerf: Boolean = false
): Modifier = composed {
    if (!enabled || disabledByPerf) return@composed this

    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = IronPulseMotion.PressSpring,
        label = "tactileScale"
    )

    this
        .pointerInput(enabled) {
            val touchSlop = viewConfiguration.touchSlop
            var downX = 0f
            var downY = 0f
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    when (event.type) {
                        PointerEventType.Press -> {
                            val first = event.changes.firstOrNull()
                            if (first != null) {
                                downX = first.position.x
                                downY = first.position.y
                                pressed = true
                            }
                        }
                        PointerEventType.Move -> {
                            if (pressed) {
                                val first = event.changes.firstOrNull()
                                if (first == null ||
                                    abs(first.position.x - downX) > touchSlop ||
                                    abs(first.position.y - downY) > touchSlop
                                ) {
                                    pressed = false
                                }
                            }
                        }
                        PointerEventType.Release, PointerEventType.Exit -> {
                            pressed = false
                        }
                        else -> {
                            if (!event.changes.any { it.pressed }) {
                                pressed = false
                            }
                        }
                    }
                }
            }
        }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
}

private object ScaledBackdropCache {
    val bitmaps = ConcurrentHashMap<Long, ImageBitmap>()
}

/**
 * Decodes and downsamples large drawable backdrops once on Dispatchers.IO and keeps
 * an in-memory cache so navigating between tabs / screens never stalls the UI thread.
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
