package com.fathan.ironpulse.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.ColorPresetOption
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseOutlinedButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine

enum class VisualColorPickerMode {
    ACCENT,
    SURFACE_TINT
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualColorPickerDialog(
    mode: VisualColorPickerMode,
    initialHex: String,
    quickPresets: List<ColorPresetOption>,
    lang: AppLanguage,
    onApplyHex: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val noAnim = tokens.performance.noAnimations

    val initialHsv = remember(initialHex) {
        MaterialYouColorEngine.colorToHsv(
            MaterialYouColorEngine.parseColor(initialHex, tokens.accentColor)
        )
    }
    var hue by remember(initialHex) { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember(initialHex) { mutableFloatStateOf(initialHsv[1]) }
    var value by remember(initialHex) { mutableFloatStateOf(initialHsv[2]) }

    val selectedColor = remember(hue, saturation, value) {
        Color.hsv(hue.coerceIn(0f, 360f), saturation.coerceIn(0f, 1f), value.coerceIn(0f, 1f))
    }
    val pureHueColor = remember(hue) {
        Color.hsv(hue.coerceIn(0f, 360f), 1f, 1f)
    }
    val onSelectedColor = remember(selectedColor) {
        if (selectedColor.luminance() > 0.44f) Color(0xFF0A0C10) else Color(0xFFF8FAFC)
    }

    val rainbowColors = remember {
        listOf(
            Color(0xFFFF0000),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF00FFFF),
            Color(0xFF0000FF),
            Color(0xFFFF00FF),
            Color(0xFFFF0000)
        )
    }

    ThemedIronDialog(
        onDismissRequest = onDismiss,
        maxWidth = 460.dp,
        borderOverride = selectedColor.copy(alpha = 0.45f),
        scrollable = true,
        modifier = Modifier.testTag("visual_color_picker_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                        .border(2.dp, Color.White.copy(alpha = 0.75f), CircleShape)
                )
                Text(
                    text = if (mode == VisualColorPickerMode.ACCENT) {
                        lang.t("Pilih Warna Aksen", "Choose Accent Color")
                    } else {
                        lang.t("Pilih Nuansa Permukaan", "Choose Surface Tint")
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = tokens.palette.onSurface
                )
            }
        },
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IronPulseOutlinedButton(
                    onClick = onDismiss,
                    shape = tokens.buttonShape,
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .tactilePress(disabledByPerf = noAnim)
                ) {
                    Text(lang.t("Batal", "Cancel"), color = tokens.palette.onSurface)
                }
                IronPulseButton(
                    onClick = {
                        val internalHex = MaterialYouColorEngine.colorToHex(selectedColor)
                        onApplyHex(internalHex)
                    },
                    shape = tokens.buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedColor,
                        contentColor = onSelectedColor
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .heightIn(min = 48.dp)
                        .tactilePress(disabledByPerf = noAnim)
                        .testTag("color_picker_apply_button")
                ) {
                    ThemedIcon(IronPulseIcon.CHECK, contentDescription = null, size = 16.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(lang.t("Terapkan", "Apply"), fontWeight = FontWeight.Bold)
                }
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = lang.t(
                        "Geser di kanvas & spektrum pelangi sampai pas di mata — tanpa kode warna.",
                        "Drag across the canvas & spectrum bar until it looks right — no codes needed."
                    ),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = tokens.palette.onSurfaceVariant
                )

                // 2D Saturation x Value Touch Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(14.dp))
                        .testTag("color_picker_sv_canvas")
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val w = size.width.toFloat().coerceAtLeast(1f)
                                val h = size.height.toFloat().coerceAtLeast(1f)
                                saturation = (down.position.x / w).coerceIn(0f, 1f)
                                value = (1f - (down.position.y / h)).coerceIn(0f, 1f)
                                down.consume()
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break
                                    if (!change.pressed) break
                                    saturation = (change.position.x / w).coerceIn(0f, 1f)
                                    value = (1f - (change.position.y / h)).coerceIn(0f, 1f)
                                    change.consume()
                                }
                            }
                        }
                ) {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White, pureHueColor)
                        )
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black)
                        )
                    )
                    val cx = (saturation * size.width).coerceIn(0f, size.width)
                    val cy = ((1f - value) * size.height).coerceIn(0f, size.height)
                    val center = Offset(cx, cy)
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.55f),
                        radius = 12.dp.toPx(),
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = selectedColor,
                        radius = 8.5.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 9.5.dp.toPx(),
                        center = center,
                        style = Stroke(width = 2.2.dp.toPx())
                    )
                }

                // Rainbow Hue Spectrum Slider
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(999.dp))
                        .testTag("color_picker_hue_bar")
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val w = size.width.toFloat().coerceAtLeast(1f)
                                hue = ((down.position.x / w).coerceIn(0f, 1f)) * 360f
                                down.consume()
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break
                                    if (!change.pressed) break
                                    hue = ((change.position.x / w).coerceIn(0f, 1f)) * 360f
                                    change.consume()
                                }
                            }
                        }
                ) {
                    drawRect(brush = Brush.horizontalGradient(colors = rainbowColors))
                    val thumbX = ((hue / 360f) * size.width).coerceIn(10.dp.toPx(), size.width - 10.dp.toPx())
                    val center = Offset(thumbX, size.height / 2f)
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.45f),
                        radius = 12.dp.toPx(),
                        center = center,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = pureHueColor,
                        radius = 9.5.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 10.dp.toPx(),
                        center = center,
                        style = Stroke(width = 2.4.dp.toPx())
                    )
                }

                // Quick Visual Reference Dots
                if (quickPresets.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("color_picker_quick_presets")
                    ) {
                        quickPresets.take(7).forEach { opt ->
                            val c = MaterialYouColorEngine.parseColor(opt.hex)
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .tactilePress(pressedScale = 0.88f, disabledByPerf = noAnim)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(1.2.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                                    .clickable {
                                        val hsv = MaterialYouColorEngine.colorToHsv(c)
                                        hue = hsv[0]
                                        saturation = hsv[1]
                                        value = hsv[2]
                                    }
                            )
                        }
                    }
                }

                // Live UI Preview Card (no hex/numbers)
                val previewSurface = if (mode == VisualColorPickerMode.SURFACE_TINT) {
                    selectedColor
                } else {
                    tokens.palette.surfaceContainerLow
                }
                val previewAccent = if (mode == VisualColorPickerMode.ACCENT) {
                    selectedColor
                } else {
                    tokens.accentColor
                }
                val previewOnAccent = if (mode == VisualColorPickerMode.ACCENT) {
                    onSelectedColor
                } else {
                    tokens.onAccentColor
                }
                val previewTextColor = if (previewSurface.luminance() > 0.48f) {
                    Color(0xFF0E1118)
                } else {
                    Color(0xFFF3F4F6)
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = previewSurface,
                    border = BorderStroke(1.dp, previewAccent.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("color_picker_live_preview")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(previewAccent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                ThemedIcon(
                                    icon = IronPulseIcon.EXERCISES,
                                    contentDescription = null,
                                    tint = previewAccent,
                                    size = 16.dp
                                )
                            }
                            Column {
                                Text(
                                    text = lang.t("Pratinjau Tampilan", "Live Preview"),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = previewTextColor
                                )
                                Text(
                                    text = lang.t("Terlihat nyata di kartu & tombol", "Matches your cards & buttons"),
                                    fontSize = 10.5.sp,
                                    color = previewTextColor.copy(alpha = 0.68f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = previewAccent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                ThemedIcon(
                                    icon = IronPulseIcon.SPARKLE,
                                    contentDescription = null,
                                    tint = previewOnAccent,
                                    size = 12.dp
                                )
                                Text(
                                    text = lang.t("Aktif", "Active"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = previewOnAccent
                                )
                            }
                        }
                    }
                }
            }
        }
}

@Composable
fun VisualPickColorPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val noAnim = tokens.performance.noAnimations
    val rainbowRing = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFFFF4D6D),
                Color(0xFFFBBF24),
                Color(0xFF34D399),
                Color(0xFF00E5FF),
                Color(0xFFA78BFA),
                Color(0xFFFF4D6D)
            )
        )
    }

    val pillShape = RoundedCornerShape(999.dp)
    IronPulseTonalSurface(
        onClick = onClick,
        shape = pillShape,
        baseColor = tokens.palette.surfaceContainerLow,
        pressedScale = 0.94f,
        border = BorderStroke(1.dp, tokens.surfaceBorderColor),
        modifier = modifier,
        visualModifier = Modifier.heightIn(min = 38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(rainbowRing)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(tokens.palette.surfaceContainerLow),
                contentAlignment = Alignment.Center
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.PALETTE,
                    contentDescription = null,
                    tint = Color.White,
                    size = 10.dp
                )
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        }
    }
}
