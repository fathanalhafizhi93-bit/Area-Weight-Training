package com.fathan.ironpulse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import kotlin.math.cos
import kotlin.math.sin

enum class IronPulseIcon {
    TODAY,
    SCHEDULE,
    HISTORY,
    EXERCISES,
    SETTINGS,
    INFO,
    PLAY,
    PAUSE,
    STOP,
    ADD,
    REMOVE,
    CHECK,
    CHECK_CIRCLE,
    CLOSE,
    REFRESH,
    EDIT,
    DELETE,
    BACK,
    FORWARD,
    CHEVRON_LEFT,
    CHEVRON_RIGHT,
    EXPAND_MORE,
    EXPAND_LESS,
    SEARCH,
    TIMER,
    ALARM,
    FOCUS_HUD,
    FLAME,
    PALETTE,
    SPEED,
    LOCK,
    PERSON,
    LOGOUT,
    VOLUME,
    MUSIC,
    NOTIFICATIONS,
    NOTIFICATIONS_OFF,
    CLOUD_DONE,
    CLOUD_OFF,
    WARNING,
    LIBRARY_ADD,
    BOOKMARK,
    SPARKLE,
    SCHOOL,
    SNOOZE,
    MORE_VERT
}

/**
 * Full custom per-theme vector/Canvas icon engine replacing generic Material Icons.
 * Every icon adapts its silhouette, stroke geometry, gloss/bevel/reticle accents,
 * and surface language to match the active OS theme:
 * - Material You: Expressive two-tone organic rounded shapes with tonal wash fill.
 * - Carbon Forge: Machined steel geometric strokes with mitered edges & forged bevel glints.
 * - Ubuntu Yaru: Clean GNOME desktop round-line icons with subtle Yaru accent node.
 * - AMOLED Stealth: Precision thin tactical HUD wireframe with corner reticle pips.
 * - Frutiger Aero: Glossy Y2K aqua/gel 3D icons with upper crystal sheen & specular glint.
 */
@Composable
fun ThemedIcon(
    icon: IronPulseIcon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    size: Dp = 20.dp,
    themeOverride: UiTheme? = null,
    filledBadge: Boolean = false
) {
    val tokens = LocalIronPulseTokens.current
    val theme = themeOverride ?: tokens.uiTheme
    val accent = tokens.accentColor

    val semMod = if (contentDescription != null) {
        modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        modifier
    }

    Canvas(
        modifier = semMod
            .size(size)
            .testTag("themed_icon_${theme.wireValue}_${icon.name.lowercase()}")
    ) {
        val style = resolveThemeIconStyle(theme, tint, accent, this.size.width)
        val showBadge = filledBadge && this.size.width >= 18.dp.toPx()
        drawThemeIconUnderlay(theme, style, showBadge)
        if (showBadge) {
            scale(scale = 0.78f, pivot = center) {
                drawSemanticGlyph(icon, theme, style)
            }
        } else {
            drawSemanticGlyph(icon, theme, style)
        }
        drawThemeIconOverlay(theme, style, showBadge)
    }
}

private data class ThemeIconStyle(
    val primary: Color,
    val secondary: Color,
    val fillWash: Color,
    val highlight: Color,
    val strokeWidth: Float,
    val cap: StrokeCap,
    val join: StrokeJoin,
    val cornerScale: Float
)

private fun resolveThemeIconStyle(
    theme: UiTheme,
    tint: Color,
    accent: Color,
    widthPx: Float
): ThemeIconStyle = when (theme) {
    UiTheme.MATERIAL_YOU -> ThemeIconStyle(
        primary = tint,
        secondary = accent.copy(alpha = 0.85f),
        fillWash = tint.copy(alpha = 0.16f),
        highlight = Color.White.copy(alpha = 0.35f),
        strokeWidth = widthPx * 0.088f,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
        cornerScale = 0.26f
    )
    UiTheme.CARBON_FORGE -> ThemeIconStyle(
        primary = tint,
        secondary = Color(0xFFCBD5E1),
        fillWash = tint.copy(alpha = 0.14f),
        highlight = Color.White.copy(alpha = 0.55f),
        strokeWidth = widthPx * 0.095f,
        cap = StrokeCap.Square,
        join = StrokeJoin.Miter,
        cornerScale = 0.08f
    )
    UiTheme.UBUNTU_YARU -> ThemeIconStyle(
        primary = tint,
        secondary = Color(0xFFE95420),
        fillWash = tint.copy(alpha = 0.12f),
        highlight = Color(0xFFF6F5F4).copy(alpha = 0.45f),
        strokeWidth = widthPx * 0.084f,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
        cornerScale = 0.18f
    )
    UiTheme.AMOLED_STEALTH -> ThemeIconStyle(
        primary = tint,
        secondary = accent,
        fillWash = tint.copy(alpha = 0.10f),
        highlight = Color.White.copy(alpha = 0.85f),
        strokeWidth = widthPx * 0.074f,
        cap = StrokeCap.Square,
        join = StrokeJoin.Miter,
        cornerScale = 0.04f
    )
    UiTheme.FRUTIGER_AERO -> ThemeIconStyle(
        primary = tint,
        secondary = Color(0xFF0077C2),
        fillWash = tint.copy(alpha = 0.18f),
        highlight = Color.White.copy(alpha = 0.85f),
        strokeWidth = widthPx * 0.090f,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
        cornerScale = 0.30f
    )
    UiTheme.XMATERIAL_YOU -> ThemeIconStyle(
        primary = tint,
        secondary = accent,
        fillWash = tint.copy(alpha = 0.11f),
        highlight = Color.White.copy(alpha = 0.60f),
        strokeWidth = widthPx * 0.080f,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
        cornerScale = 0.20f
    )
    UiTheme.ANDROID_NOUGAT -> ThemeIconStyle(
        primary = tint,
        secondary = Color(0xFF00BFA5),
        fillWash = tint.copy(alpha = 0.14f),
        highlight = Color(0xFFB2DFDB),
        strokeWidth = widthPx * 0.084f,
        cap = StrokeCap.Square,
        join = StrokeJoin.Miter,
        cornerScale = 0.10f
    )
}

private fun DrawScope.drawThemeIconUnderlay(
    theme: UiTheme,
    style: ThemeIconStyle,
    filledBadge: Boolean
) {
    val w = size.width
    val h = size.height
    when (theme) {
        UiTheme.FRUTIGER_AERO -> {
            if (filledBadge) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x99A5F3FC),
                            Color(0x660099E5),
                            Color(0x264ADE80)
                        ),
                        center = Offset(w * 0.42f, h * 0.35f),
                        radius = w * 0.56f
                    ),
                    radius = w * 0.48f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.78f),
                    radius = w * 0.47f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = Stroke(w * 0.045f)
                )
            }
        }
        UiTheme.AMOLED_STEALTH -> {
            if (filledBadge) {
                val p = w * 0.04f
                val arm = w * 0.18f
                val sw = w * 0.048f
                val c = style.primary.copy(alpha = 0.52f)
                drawLine(c, Offset(p, p), Offset(p + arm, p), sw)
                drawLine(c, Offset(p, p), Offset(p, p + arm), sw)
                drawLine(c, Offset(w - p, p), Offset(w - p - arm, p), sw)
                drawLine(c, Offset(w - p, p), Offset(w - p, p + arm), sw)
                drawLine(c, Offset(p, h - p), Offset(p + arm, h - p), sw)
                drawLine(c, Offset(p, h - p), Offset(p, h - p - arm), sw)
                drawLine(c, Offset(w - p, h - p), Offset(w - p - arm, h - p), sw)
                drawLine(c, Offset(w - p, h - p), Offset(w - p, h - p - arm), sw)
            }
        }
        UiTheme.CARBON_FORGE -> {
            if (filledBadge) {
                val cut = w * 0.16f
                val plate = Path().apply {
                    moveTo(cut, w * 0.05f)
                    lineTo(w - cut, w * 0.05f)
                    lineTo(w * 0.95f, cut)
                    lineTo(w * 0.95f, h - cut)
                    lineTo(w - cut, h * 0.95f)
                    lineTo(cut, h * 0.95f)
                    lineTo(w * 0.05f, h - cut)
                    lineTo(w * 0.05f, cut)
                    close()
                }
                drawPath(plate, style.fillWash)
                drawPath(plate, style.primary.copy(alpha = 0.38f), style = Stroke(w * 0.042f))
            }
        }
        UiTheme.UBUNTU_YARU -> {
            if (filledBadge) {
                drawRoundRect(
                    color = style.primary.copy(alpha = 0.12f),
                    topLeft = Offset(w * 0.05f, h * 0.05f),
                    size = Size(w * 0.90f, h * 0.90f),
                    cornerRadius = CornerRadius(w * 0.24f, w * 0.24f)
                )
            }
        }
        UiTheme.MATERIAL_YOU -> {
            if (filledBadge) {
                drawCircle(
                    color = style.fillWash,
                    radius = w * 0.46f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
            }
        }
        UiTheme.XMATERIAL_YOU -> {
            if (filledBadge) {
                val inset = w * 0.05f
                val bigR = w * 0.30f
                val smallR = w * 0.10f
                val obsidianSeal = Path().apply {
                    moveTo(inset + bigR, inset)
                    lineTo(w - inset - smallR, inset)
                    quadraticBezierTo(w - inset, inset, w - inset, inset + smallR)
                    lineTo(w - inset, h - inset - bigR)
                    quadraticBezierTo(w - inset, h - inset, w - inset - bigR, h - inset)
                    lineTo(inset + smallR, h - inset)
                    quadraticBezierTo(inset, h - inset, inset, h - inset - smallR)
                    lineTo(inset, inset + bigR)
                    quadraticBezierTo(inset, inset, inset + bigR, inset)
                    close()
                }
                drawPath(
                    path = obsidianSeal,
                    color = style.fillWash
                )
                drawPath(
                    path = obsidianSeal,
                    color = style.secondary.copy(alpha = 0.42f),
                    style = Stroke(w * 0.040f)
                )
            }
        }
        UiTheme.ANDROID_NOUGAT -> {
            if (filledBadge) {
                val r = w * 0.12f
                drawRoundRect(
                    color = Color(0xFF263238),
                    topLeft = Offset(w * 0.05f, h * 0.05f),
                    size = Size(w * 0.90f, h * 0.90f),
                    cornerRadius = CornerRadius(r, r)
                )
                drawRoundRect(
                    color = style.secondary.copy(alpha = 0.72f),
                    topLeft = Offset(w * 0.05f, h * 0.05f),
                    size = Size(w * 0.90f, h * 0.90f),
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(w * 0.048f, join = StrokeJoin.Round)
                )
            }
        }
    }
}

private fun DrawScope.drawThemeIconOverlay(
    theme: UiTheme,
    style: ThemeIconStyle,
    filledBadge: Boolean
) {
    if (!filledBadge) return
    val w = size.width
    val h = size.height
    when (theme) {
        UiTheme.FRUTIGER_AERO -> {
            // Clean crystal upper arc glint only inside badged Aero glass spheres
            drawOval(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.52f), Color.Transparent)
                ),
                topLeft = Offset(w * 0.22f, h * 0.08f),
                size = Size(w * 0.56f, h * 0.24f)
            )
        }
        UiTheme.AMOLED_STEALTH -> {
            drawRect(
                color = style.primary.copy(alpha = 0.78f),
                topLeft = Offset(w * 0.83f, h * 0.09f),
                size = Size(w * 0.07f, h * 0.07f)
            )
        }
        UiTheme.CARBON_FORGE -> {
            drawLine(
                color = style.highlight.copy(alpha = 0.42f),
                start = Offset(w * 0.20f, h * 0.09f),
                end = Offset(w * 0.42f, h * 0.09f),
                strokeWidth = w * 0.036f,
                cap = StrokeCap.Square
            )
        }
        UiTheme.UBUNTU_YARU -> {
            drawCircle(
                color = Color(0xFFE95420),
                radius = w * 0.07f,
                center = Offset(w * 0.82f, h * 0.82f)
            )
        }
        UiTheme.MATERIAL_YOU -> Unit
        UiTheme.XMATERIAL_YOU -> {
            drawCircle(
                color = style.secondary.copy(alpha = 0.72f),
                radius = w * 0.042f,
                center = Offset(w * 0.78f, h * 0.20f)
            )
        }
        UiTheme.ANDROID_NOUGAT -> {
            drawLine(
                color = Color.White.copy(alpha = 0.24f),
                start = Offset(w * 0.14f, h * 0.08f),
                end = Offset(w * 0.86f, h * 0.08f),
                strokeWidth = w * 0.032f,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawSemanticGlyph(
    icon: IronPulseIcon,
    theme: UiTheme,
    style: ThemeIconStyle
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    val cy = h * 0.5f
    val stroke = Stroke(width = style.strokeWidth, cap = style.cap, join = style.join)
    val thinStroke = Stroke(width = style.strokeWidth * 0.82f, cap = style.cap, join = style.join)

    when (icon) {
        IronPulseIcon.TODAY -> {
            when (theme) {
                UiTheme.CARBON_FORGE -> {
                    // Hexagonal forged ignition play badge
                    val hex = Path().apply {
                        moveTo(cx, h * 0.12f)
                        lineTo(w * 0.85f, h * 0.31f)
                        lineTo(w * 0.85f, h * 0.69f)
                        lineTo(cx, h * 0.88f)
                        lineTo(w * 0.15f, h * 0.69f)
                        lineTo(w * 0.15f, h * 0.31f)
                        close()
                    }
                    drawPath(hex, style.fillWash)
                    drawPath(hex, style.primary, style = stroke)
                }
                UiTheme.AMOLED_STEALTH -> {
                    // Tactical reticle diamond + inner play chevron
                    val dia = Path().apply {
                        moveTo(cx, h * 0.10f)
                        lineTo(w * 0.90f, cy)
                        lineTo(cx, h * 0.90f)
                        lineTo(w * 0.10f, cy)
                        close()
                    }
                    drawPath(dia, style.primary, style = thinStroke)
                }
                UiTheme.ANDROID_NOUGAT -> {
                    // Android 7 Material crisp 4dp rounded card badge
                    drawRoundRect(
                        color = style.fillWash,
                        topLeft = Offset(w * 0.13f, h * 0.13f),
                        size = Size(w * 0.74f, h * 0.74f),
                        cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
                    )
                    drawRoundRect(
                        color = style.primary,
                        topLeft = Offset(w * 0.13f, h * 0.13f),
                        size = Size(w * 0.74f, h * 0.74f),
                        cornerRadius = CornerRadius(w * 0.12f, w * 0.12f),
                        style = stroke
                    )
                }
                else -> {
                    drawCircle(style.fillWash, w * 0.38f, Offset(cx, cy))
                    drawCircle(style.primary, w * 0.38f, Offset(cx, cy), style = stroke)
                }
            }
            val tri = Path().apply {
                moveTo(w * 0.42f, h * 0.34f)
                lineTo(w * 0.68f, cy)
                lineTo(w * 0.42f, h * 0.66f)
                close()
            }
            drawPath(tri, style.primary)
        }

        IronPulseIcon.SCHEDULE -> {
            val cr = w * style.cornerScale * 0.65f
            drawRoundRect(
                color = style.fillWash,
                topLeft = Offset(w * 0.15f, h * 0.24f),
                size = Size(w * 0.70f, h * 0.60f),
                cornerRadius = CornerRadius(cr, cr)
            )
            drawRoundRect(
                color = style.primary,
                topLeft = Offset(w * 0.15f, h * 0.24f),
                size = Size(w * 0.70f, h * 0.60f),
                cornerRadius = CornerRadius(cr, cr),
                style = stroke
            )
            drawLine(style.primary, Offset(w * 0.15f, h * 0.43f), Offset(w * 0.85f, h * 0.43f), style.strokeWidth * 0.85f)
            drawLine(style.primary, Offset(w * 0.34f, h * 0.14f), Offset(w * 0.34f, h * 0.30f), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.66f, h * 0.14f), Offset(w * 0.66f, h * 0.30f), style.strokeWidth, cap = style.cap)
            // Theme-distinct calendar day dots / blocks
            val dotPositions = listOf(0.34f to 0.58f, 0.50f to 0.58f, 0.66f to 0.58f, 0.34f to 0.72f, 0.50f to 0.72f)
            dotPositions.forEachIndexed { i, (dx, dy) ->
                val c = if (i == 2 && theme == UiTheme.UBUNTU_YARU) style.secondary else style.primary
                if (theme == UiTheme.CARBON_FORGE || theme == UiTheme.AMOLED_STEALTH) {
                    drawRect(c, Offset(w * dx - w * 0.04f, h * dy - h * 0.04f), Size(w * 0.08f, h * 0.08f))
                } else {
                    drawCircle(c, w * 0.045f, Offset(w * dx, h * dy))
                }
            }
        }

        IronPulseIcon.HISTORY -> {
            drawCircle(style.fillWash, w * 0.36f, Offset(cx, cy))
            drawArc(
                color = style.primary,
                startAngle = -140f,
                sweepAngle = 290f,
                useCenter = false,
                topLeft = Offset(w * 0.16f, h * 0.16f),
                size = Size(w * 0.68f, h * 0.68f),
                style = stroke
            )
            // Counter-clockwise arrow head
            val arrow = Path().apply {
                moveTo(w * 0.16f, h * 0.22f)
                lineTo(w * 0.16f, h * 0.41f)
                lineTo(w * 0.35f, h * 0.41f)
            }
            drawPath(arrow, style.primary, style = stroke)
            // Clock hands
            drawLine(style.primary, Offset(cx, cy), Offset(cx, h * 0.32f), style.strokeWidth * 0.9f, cap = style.cap)
            drawLine(style.primary, Offset(cx, cy), Offset(w * 0.64f, h * 0.58f), style.strokeWidth * 0.9f, cap = style.cap)
        }

        IronPulseIcon.EXERCISES -> {
            // Iconic dumbbell adapted per OS theme
            val cr = w * style.cornerScale * 0.5f
            drawLine(style.primary, Offset(w * 0.26f, cy), Offset(w * 0.74f, cy), style.strokeWidth * 1.15f, cap = style.cap)
            // Outer plates
            drawRoundRect(style.primary, Offset(w * 0.11f, h * 0.32f), Size(w * 0.12f, h * 0.36f), CornerRadius(cr, cr))
            drawRoundRect(style.primary, Offset(w * 0.77f, h * 0.32f), Size(w * 0.12f, h * 0.36f), CornerRadius(cr, cr))
            // Inner heavier plates
            drawRoundRect(style.fillWash, Offset(w * 0.22f, h * 0.22f), Size(w * 0.12f, h * 0.56f), CornerRadius(cr, cr))
            drawRoundRect(style.primary, Offset(w * 0.22f, h * 0.22f), Size(w * 0.12f, h * 0.56f), CornerRadius(cr, cr), style = thinStroke)
            drawRoundRect(style.fillWash, Offset(w * 0.66f, h * 0.22f), Size(w * 0.12f, h * 0.56f), CornerRadius(cr, cr))
            drawRoundRect(style.primary, Offset(w * 0.66f, h * 0.22f), Size(w * 0.12f, h * 0.56f), CornerRadius(cr, cr), style = thinStroke)
        }

        IronPulseIcon.SETTINGS -> {
            val teeth = if (theme == UiTheme.CARBON_FORGE || theme == UiTheme.AMOLED_STEALTH) 6 else 8
            val outerR = w * 0.40f
            val innerR = w * 0.28f
            for (i in 0 until teeth) {
                val a = Math.toRadians(i * (360.0 / teeth))
                drawLine(
                    color = style.primary,
                    start = Offset(cx + (innerR * cos(a)).toFloat(), cy + (innerR * sin(a)).toFloat()),
                    end = Offset(cx + (outerR * cos(a)).toFloat(), cy + (outerR * sin(a)).toFloat()),
                    strokeWidth = style.strokeWidth * 1.1f,
                    cap = style.cap
                )
            }
            drawCircle(style.fillWash, innerR, Offset(cx, cy))
            drawCircle(style.primary, innerR, Offset(cx, cy), style = stroke)
            drawCircle(style.primary, w * 0.10f, Offset(cx, cy), style = thinStroke)
        }

        IronPulseIcon.INFO -> {
            if (theme == UiTheme.CARBON_FORGE || theme == UiTheme.AMOLED_STEALTH) {
                val cr = w * style.cornerScale
                drawRoundRect(style.fillWash, Offset(w * 0.14f, h * 0.14f), Size(w * 0.72f, h * 0.72f), CornerRadius(cr, cr))
                drawRoundRect(style.primary, Offset(w * 0.14f, h * 0.14f), Size(w * 0.72f, h * 0.72f), CornerRadius(cr, cr), style = stroke)
            } else {
                drawCircle(style.fillWash, w * 0.38f, Offset(cx, cy))
                drawCircle(style.primary, w * 0.38f, Offset(cx, cy), style = stroke)
            }
            drawCircle(style.primary, w * 0.055f, Offset(cx, h * 0.33f))
            drawLine(style.primary, Offset(cx, h * 0.47f), Offset(cx, h * 0.69f), style.strokeWidth, cap = style.cap)
        }

        IronPulseIcon.PLAY -> {
            val tri = Path().apply {
                moveTo(w * 0.28f, h * 0.20f)
                lineTo(w * 0.80f, cy)
                lineTo(w * 0.28f, h * 0.80f)
                close()
            }
            if (theme == UiTheme.AMOLED_STEALTH) {
                drawPath(tri, style.fillWash)
                drawPath(tri, style.primary, style = stroke)
            } else {
                drawPath(tri, style.primary)
            }
        }

        IronPulseIcon.PAUSE -> {
            val cr = w * style.cornerScale * 0.45f
            drawRoundRect(style.primary, Offset(w * 0.24f, h * 0.20f), Size(w * 0.18f, h * 0.60f), CornerRadius(cr, cr))
            drawRoundRect(style.primary, Offset(w * 0.58f, h * 0.20f), Size(w * 0.18f, h * 0.60f), CornerRadius(cr, cr))
        }

        IronPulseIcon.STOP -> {
            val cr = w * style.cornerScale * 0.6f
            drawRoundRect(style.primary, Offset(w * 0.24f, h * 0.24f), Size(w * 0.52f, h * 0.52f), CornerRadius(cr, cr))
        }

        IronPulseIcon.ADD -> {
            drawLine(style.primary, Offset(cx, h * 0.20f), Offset(cx, h * 0.80f), style.strokeWidth * 1.15f, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.20f, cy), Offset(w * 0.80f, cy), style.strokeWidth * 1.15f, cap = style.cap)
        }

        IronPulseIcon.REMOVE -> {
            drawLine(style.primary, Offset(w * 0.20f, cy), Offset(w * 0.80f, cy), style.strokeWidth * 1.15f, cap = style.cap)
        }

        IronPulseIcon.CHECK -> {
            val p = Path().apply {
                moveTo(w * 0.18f, h * 0.52f)
                lineTo(w * 0.41f, h * 0.74f)
                lineTo(w * 0.82f, h * 0.27f)
            }
            drawPath(p, style.primary, style = Stroke(style.strokeWidth * 1.18f, cap = style.cap, join = style.join))
        }

        IronPulseIcon.CHECK_CIRCLE -> {
            if (theme == UiTheme.CARBON_FORGE || theme == UiTheme.AMOLED_STEALTH) {
                val cr = w * style.cornerScale
                drawRoundRect(style.fillWash, Offset(w * 0.13f, h * 0.13f), Size(w * 0.74f, h * 0.74f), CornerRadius(cr, cr))
                drawRoundRect(style.primary, Offset(w * 0.13f, h * 0.13f), Size(w * 0.74f, h * 0.74f), CornerRadius(cr, cr), style = stroke)
            } else {
                drawCircle(style.fillWash, w * 0.39f, Offset(cx, cy))
                drawCircle(style.primary, w * 0.39f, Offset(cx, cy), style = stroke)
            }
            val p = Path().apply {
                moveTo(w * 0.30f, h * 0.51f)
                lineTo(w * 0.45f, h * 0.66f)
                lineTo(w * 0.71f, h * 0.36f)
            }
            drawPath(p, style.primary, style = stroke)
        }

        IronPulseIcon.CLOSE -> {
            drawLine(style.primary, Offset(w * 0.24f, h * 0.24f), Offset(w * 0.76f, h * 0.76f), style.strokeWidth * 1.1f, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.76f, h * 0.24f), Offset(w * 0.24f, h * 0.76f), style.strokeWidth * 1.1f, cap = style.cap)
        }

        IronPulseIcon.REFRESH -> {
            drawArc(
                color = style.primary,
                startAngle = -55f,
                sweepAngle = 295f,
                useCenter = false,
                topLeft = Offset(w * 0.18f, h * 0.18f),
                size = Size(w * 0.64f, h * 0.64f),
                style = stroke
            )
            val head = Path().apply {
                moveTo(w * 0.64f, h * 0.15f)
                lineTo(w * 0.83f, h * 0.22f)
                lineTo(w * 0.73f, h * 0.40f)
            }
            drawPath(head, style.primary, style = stroke)
        }

        IronPulseIcon.EDIT -> {
            val pen = Path().apply {
                moveTo(w * 0.20f, h * 0.80f)
                lineTo(w * 0.26f, h * 0.58f)
                lineTo(w * 0.66f, h * 0.18f)
                lineTo(w * 0.82f, h * 0.34f)
                lineTo(w * 0.42f, h * 0.74f)
                close()
            }
            drawPath(pen, style.fillWash)
            drawPath(pen, style.primary, style = thinStroke)
            drawLine(style.primary, Offset(w * 0.56f, h * 0.28f), Offset(w * 0.72f, h * 0.44f), style.strokeWidth * 0.8f)
        }

        IronPulseIcon.DELETE -> {
            drawLine(style.primary, Offset(w * 0.18f, h * 0.26f), Offset(w * 0.82f, h * 0.26f), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.38f, h * 0.16f), Offset(w * 0.62f, h * 0.16f), style.strokeWidth, cap = style.cap)
            val bin = Path().apply {
                moveTo(w * 0.25f, h * 0.26f)
                lineTo(w * 0.30f, h * 0.82f)
                lineTo(w * 0.70f, h * 0.82f)
                lineTo(w * 0.75f, h * 0.26f)
            }
            drawPath(bin, style.fillWash)
            drawPath(bin, style.primary, style = thinStroke)
            drawLine(style.primary, Offset(w * 0.43f, h * 0.40f), Offset(w * 0.43f, h * 0.68f), style.strokeWidth * 0.75f, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.57f, h * 0.40f), Offset(w * 0.57f, h * 0.68f), style.strokeWidth * 0.75f, cap = style.cap)
        }

        IronPulseIcon.BACK -> {
            drawLine(style.primary, Offset(w * 0.22f, cy), Offset(w * 0.80f, cy), style.strokeWidth, cap = style.cap)
            val p = Path().apply {
                moveTo(w * 0.46f, h * 0.24f)
                lineTo(w * 0.20f, cy)
                lineTo(w * 0.46f, h * 0.76f)
            }
            drawPath(p, style.primary, style = stroke)
        }

        IronPulseIcon.FORWARD -> {
            drawLine(style.primary, Offset(w * 0.20f, cy), Offset(w * 0.78f, cy), style.strokeWidth, cap = style.cap)
            val p = Path().apply {
                moveTo(w * 0.54f, h * 0.24f)
                lineTo(w * 0.80f, cy)
                lineTo(w * 0.54f, h * 0.76f)
            }
            drawPath(p, style.primary, style = stroke)
        }

        IronPulseIcon.CHEVRON_LEFT -> {
            val p = Path().apply {
                moveTo(w * 0.62f, h * 0.22f)
                lineTo(w * 0.34f, cy)
                lineTo(w * 0.62f, h * 0.78f)
            }
            drawPath(p, style.primary, style = stroke)
        }

        IronPulseIcon.CHEVRON_RIGHT -> {
            val p = Path().apply {
                moveTo(w * 0.38f, h * 0.22f)
                lineTo(w * 0.66f, cy)
                lineTo(w * 0.38f, h * 0.78f)
            }
            drawPath(p, style.primary, style = stroke)
        }

        IronPulseIcon.EXPAND_MORE -> {
            val p = Path().apply {
                moveTo(w * 0.22f, h * 0.38f)
                lineTo(cx, h * 0.66f)
                lineTo(w * 0.78f, h * 0.38f)
            }
            drawPath(p, style.primary, style = stroke)
        }

        IronPulseIcon.EXPAND_LESS -> {
            val p = Path().apply {
                moveTo(w * 0.22f, h * 0.62f)
                lineTo(cx, h * 0.34f)
                lineTo(w * 0.78f, h * 0.62f)
            }
            drawPath(p, style.primary, style = stroke)
        }

        IronPulseIcon.SEARCH -> {
            drawCircle(style.fillWash, w * 0.27f, Offset(w * 0.43f, h * 0.43f))
            drawCircle(style.primary, w * 0.27f, Offset(w * 0.43f, h * 0.43f), style = stroke)
            drawLine(style.primary, Offset(w * 0.63f, h * 0.63f), Offset(w * 0.83f, h * 0.83f), style.strokeWidth * 1.15f, cap = style.cap)
        }

        IronPulseIcon.TIMER -> {
            drawLine(style.primary, Offset(w * 0.38f, h * 0.14f), Offset(w * 0.62f, h * 0.14f), style.strokeWidth, cap = style.cap)
            drawCircle(style.fillWash, w * 0.32f, Offset(cx, h * 0.56f))
            drawCircle(style.primary, w * 0.32f, Offset(cx, h * 0.56f), style = stroke)
            drawLine(style.primary, Offset(cx, h * 0.56f), Offset(w * 0.63f, h * 0.42f), style.strokeWidth * 0.95f, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.74f, h * 0.28f), Offset(w * 0.82f, h * 0.36f), style.strokeWidth * 0.85f, cap = style.cap)
        }

        IronPulseIcon.ALARM -> {
            drawCircle(style.fillWash, w * 0.31f, Offset(cx, h * 0.55f))
            drawCircle(style.primary, w * 0.31f, Offset(cx, h * 0.55f), style = stroke)
            drawLine(style.primary, Offset(w * 0.18f, h * 0.26f), Offset(w * 0.32f, h * 0.15f), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.82f, h * 0.26f), Offset(w * 0.68f, h * 0.15f), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(cx, h * 0.55f), Offset(cx, h * 0.38f), style.strokeWidth * 0.9f, cap = style.cap)
            drawLine(style.primary, Offset(cx, h * 0.55f), Offset(w * 0.62f, h * 0.62f), style.strokeWidth * 0.9f, cap = style.cap)
        }

        IronPulseIcon.FOCUS_HUD -> {
            val p = w * 0.15f
            val arm = w * 0.22f
            drawLine(style.primary, Offset(p, p), Offset(p + arm, p), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(p, p), Offset(p, p + arm), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w - p, p), Offset(w - p - arm, p), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w - p, p), Offset(w - p, p + arm), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(p, h - p), Offset(p + arm, h - p), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(p, h - p), Offset(p, h - p - arm), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w - p, h - p), Offset(w - p - arm, h - p), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w - p, h - p), Offset(w - p, h - p - arm), style.strokeWidth, cap = style.cap)
            drawCircle(style.fillWash, w * 0.16f, Offset(cx, cy))
            drawCircle(style.primary, w * 0.13f, Offset(cx, cy), style = thinStroke)
        }

        IronPulseIcon.FLAME -> {
            val flame = Path().apply {
                moveTo(cx, h * 0.12f)
                cubicTo(w * 0.78f, h * 0.36f, w * 0.84f, h * 0.66f, cx, h * 0.88f)
                cubicTo(w * 0.16f, h * 0.66f, w * 0.22f, h * 0.36f, cx, h * 0.12f)
                close()
            }
            drawPath(flame, style.fillWash)
            drawPath(flame, style.primary, style = stroke)
            drawCircle(style.primary, w * 0.08f, Offset(cx, h * 0.64f))
        }

        IronPulseIcon.PALETTE -> {
            drawCircle(style.fillWash, w * 0.37f, Offset(cx, cy))
            drawCircle(style.primary, w * 0.37f, Offset(cx, cy), style = stroke)
            drawCircle(style.primary, w * 0.06f, Offset(w * 0.36f, h * 0.38f))
            drawCircle(style.primary, w * 0.06f, Offset(w * 0.56f, h * 0.32f))
            drawCircle(style.primary, w * 0.06f, Offset(w * 0.66f, h * 0.50f))
            drawCircle(style.secondary, w * 0.075f, Offset(w * 0.44f, h * 0.64f))
        }

        IronPulseIcon.SPEED -> {
            drawArc(
                color = style.primary,
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = Offset(w * 0.14f, h * 0.20f),
                size = Size(w * 0.72f, h * 0.72f),
                style = stroke
            )
            drawLine(style.primary, Offset(cx, h * 0.62f), Offset(w * 0.72f, h * 0.36f), style.strokeWidth * 1.1f, cap = style.cap)
            drawCircle(style.primary, w * 0.07f, Offset(cx, h * 0.62f))
        }

        IronPulseIcon.LOCK -> {
            val cr = w * style.cornerScale * 0.5f
            drawArc(
                color = style.primary,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.31f, h * 0.15f),
                size = Size(w * 0.38f, h * 0.40f),
                style = stroke
            )
            drawRoundRect(style.fillWash, Offset(w * 0.20f, h * 0.42f), Size(w * 0.60f, h * 0.42f), CornerRadius(cr, cr))
            drawRoundRect(style.primary, Offset(w * 0.20f, h * 0.42f), Size(w * 0.60f, h * 0.42f), CornerRadius(cr, cr), style = stroke)
            drawCircle(style.primary, w * 0.06f, Offset(cx, h * 0.62f))
        }

        IronPulseIcon.PERSON -> {
            drawCircle(style.fillWash, w * 0.17f, Offset(cx, h * 0.31f))
            drawCircle(style.primary, w * 0.17f, Offset(cx, h * 0.31f), style = stroke)
            val body = Path().apply {
                moveTo(w * 0.18f, h * 0.82f)
                cubicTo(w * 0.18f, h * 0.58f, w * 0.82f, h * 0.58f, w * 0.82f, h * 0.82f)
            }
            drawPath(body, style.fillWash)
            drawPath(body, style.primary, style = stroke)
        }

        IronPulseIcon.LOGOUT -> {
            val door = Path().apply {
                moveTo(w * 0.48f, h * 0.18f)
                lineTo(w * 0.20f, h * 0.18f)
                lineTo(w * 0.20f, h * 0.82f)
                lineTo(w * 0.48f, h * 0.82f)
            }
            drawPath(door, style.primary, style = stroke)
            drawLine(style.primary, Offset(w * 0.42f, cy), Offset(w * 0.82f, cy), style.strokeWidth, cap = style.cap)
            val head = Path().apply {
                moveTo(w * 0.64f, h * 0.32f)
                lineTo(w * 0.82f, cy)
                lineTo(w * 0.64f, h * 0.68f)
            }
            drawPath(head, style.primary, style = stroke)
        }

        IronPulseIcon.VOLUME -> {
            val spk = Path().apply {
                moveTo(w * 0.16f, h * 0.40f)
                lineTo(w * 0.34f, h * 0.40f)
                lineTo(w * 0.54f, h * 0.22f)
                lineTo(w * 0.54f, h * 0.78f)
                lineTo(w * 0.34f, h * 0.60f)
                lineTo(w * 0.16f, h * 0.60f)
                close()
            }
            drawPath(spk, style.fillWash)
            drawPath(spk, style.primary, style = thinStroke)
            drawArc(style.primary, -45f, 90f, false, Offset(w * 0.46f, h * 0.30f), Size(w * 0.28f, h * 0.40f), style = thinStroke)
            drawArc(style.primary, -50f, 100f, false, Offset(w * 0.54f, h * 0.20f), Size(w * 0.34f, h * 0.60f), style = thinStroke)
        }

        IronPulseIcon.MUSIC -> {
            drawLine(style.primary, Offset(w * 0.46f, h * 0.20f), Offset(w * 0.46f, h * 0.70f), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.46f, h * 0.20f), Offset(w * 0.78f, h * 0.14f), style.strokeWidth, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.78f, h * 0.14f), Offset(w * 0.78f, h * 0.60f), style.strokeWidth, cap = style.cap)
            drawCircle(style.primary, w * 0.12f, Offset(w * 0.35f, h * 0.72f))
            drawCircle(style.primary, w * 0.11f, Offset(w * 0.68f, h * 0.62f))
        }

        IronPulseIcon.NOTIFICATIONS -> {
            val bell = Path().apply {
                moveTo(w * 0.22f, h * 0.68f)
                lineTo(w * 0.78f, h * 0.68f)
                lineTo(w * 0.72f, h * 0.56f)
                lineTo(w * 0.72f, h * 0.42f)
                cubicTo(w * 0.72f, h * 0.24f, w * 0.28f, h * 0.24f, w * 0.28f, h * 0.42f)
                lineTo(w * 0.28f, h * 0.56f)
                close()
            }
            drawPath(bell, style.fillWash)
            drawPath(bell, style.primary, style = stroke)
            drawCircle(style.primary, w * 0.07f, Offset(cx, h * 0.80f))
        }

        IronPulseIcon.NOTIFICATIONS_OFF -> {
            val bell = Path().apply {
                moveTo(w * 0.22f, h * 0.68f)
                lineTo(w * 0.78f, h * 0.68f)
                lineTo(w * 0.72f, h * 0.56f)
                lineTo(w * 0.72f, h * 0.42f)
                cubicTo(w * 0.72f, h * 0.24f, w * 0.28f, h * 0.24f, w * 0.28f, h * 0.42f)
                lineTo(w * 0.28f, h * 0.56f)
                close()
            }
            drawPath(bell, style.primary.copy(alpha = 0.55f), style = thinStroke)
            drawLine(style.primary, Offset(w * 0.18f, h * 0.18f), Offset(w * 0.82f, h * 0.82f), style.strokeWidth, cap = style.cap)
        }

        IronPulseIcon.CLOUD_DONE, IronPulseIcon.CLOUD_OFF -> {
            val cloud = Path().apply {
                moveTo(w * 0.26f, h * 0.72f)
                lineTo(w * 0.74f, h * 0.72f)
                cubicTo(w * 0.88f, h * 0.72f, w * 0.88f, h * 0.48f, w * 0.72f, h * 0.48f)
                cubicTo(w * 0.70f, h * 0.26f, w * 0.36f, h * 0.24f, w * 0.32f, h * 0.44f)
                cubicTo(w * 0.14f, h * 0.46f, w * 0.14f, h * 0.72f, w * 0.26f, h * 0.72f)
                close()
            }
            drawPath(cloud, style.fillWash)
            drawPath(cloud, style.primary, style = thinStroke)
            if (icon == IronPulseIcon.CLOUD_OFF) {
                drawLine(style.primary, Offset(w * 0.20f, h * 0.20f), Offset(w * 0.80f, h * 0.80f), style.strokeWidth, cap = style.cap)
            } else {
                val chk = Path().apply {
                    moveTo(w * 0.39f, h * 0.56f)
                    lineTo(w * 0.48f, h * 0.64f)
                    lineTo(w * 0.64f, h * 0.47f)
                }
                drawPath(chk, style.primary, style = thinStroke)
            }
        }

        IronPulseIcon.WARNING -> {
            val tri = Path().apply {
                moveTo(cx, h * 0.15f)
                lineTo(w * 0.87f, h * 0.82f)
                lineTo(w * 0.13f, h * 0.82f)
                close()
            }
            drawPath(tri, style.fillWash)
            drawPath(tri, style.primary, style = stroke)
            drawLine(style.primary, Offset(cx, h * 0.38f), Offset(cx, h * 0.58f), style.strokeWidth, cap = style.cap)
            drawCircle(style.primary, w * 0.05f, Offset(cx, h * 0.70f))
        }

        IronPulseIcon.LIBRARY_ADD -> {
            val cr = w * style.cornerScale * 0.5f
            drawRoundRect(style.fillWash, Offset(w * 0.24f, h * 0.16f), Size(w * 0.60f, h * 0.60f), CornerRadius(cr, cr))
            drawRoundRect(style.primary, Offset(w * 0.24f, h * 0.16f), Size(w * 0.60f, h * 0.60f), CornerRadius(cr, cr), style = thinStroke)
            drawLine(style.primary, Offset(w * 0.14f, h * 0.34f), Offset(w * 0.14f, h * 0.84f), style.strokeWidth * 0.85f, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.14f, h * 0.84f), Offset(w * 0.66f, h * 0.84f), style.strokeWidth * 0.85f, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.54f, h * 0.34f), Offset(w * 0.54f, h * 0.58f), style.strokeWidth * 0.9f, cap = style.cap)
            drawLine(style.primary, Offset(w * 0.42f, h * 0.46f), Offset(w * 0.66f, h * 0.46f), style.strokeWidth * 0.9f, cap = style.cap)
        }

        IronPulseIcon.BOOKMARK -> {
            val mark = Path().apply {
                moveTo(w * 0.26f, h * 0.16f)
                lineTo(w * 0.74f, h * 0.16f)
                lineTo(w * 0.74f, h * 0.84f)
                lineTo(cx, h * 0.66f)
                lineTo(w * 0.26f, h * 0.84f)
                close()
            }
            drawPath(mark, style.fillWash)
            drawPath(mark, style.primary, style = stroke)
        }

        IronPulseIcon.SPARKLE -> {
            val star = Path().apply {
                moveTo(cx, h * 0.12f)
                quadraticBezierTo(w * 0.54f, h * 0.46f, w * 0.88f, cy)
                quadraticBezierTo(w * 0.54f, h * 0.54f, cx, h * 0.88f)
                quadraticBezierTo(w * 0.46f, h * 0.54f, w * 0.12f, cy)
                quadraticBezierTo(w * 0.46f, h * 0.46f, cx, h * 0.12f)
                close()
            }
            drawPath(star, style.fillWash)
            drawPath(star, style.primary, style = thinStroke)
        }

        IronPulseIcon.SCHOOL -> {
            val cap = Path().apply {
                moveTo(cx, h * 0.20f)
                lineTo(w * 0.88f, h * 0.40f)
                lineTo(cx, h * 0.60f)
                lineTo(w * 0.12f, h * 0.40f)
                close()
            }
            drawPath(cap, style.fillWash)
            drawPath(cap, style.primary, style = thinStroke)
            val base = Path().apply {
                moveTo(w * 0.28f, h * 0.50f)
                lineTo(w * 0.28f, h * 0.70f)
                quadraticBezierTo(cx, h * 0.82f, w * 0.72f, h * 0.70f)
                lineTo(w * 0.72f, h * 0.50f)
            }
            drawPath(base, style.primary, style = thinStroke)
            drawLine(style.primary, Offset(w * 0.84f, h * 0.42f), Offset(w * 0.84f, h * 0.68f), style.strokeWidth * 0.8f, cap = style.cap)
        }

        IronPulseIcon.SNOOZE -> {
            val z1 = Path().apply {
                moveTo(w * 0.20f, h * 0.28f)
                lineTo(w * 0.50f, h * 0.28f)
                lineTo(w * 0.20f, h * 0.58f)
                lineTo(w * 0.50f, h * 0.58f)
            }
            val z2 = Path().apply {
                moveTo(w * 0.54f, h * 0.52f)
                lineTo(w * 0.80f, h * 0.52f)
                lineTo(w * 0.54f, h * 0.78f)
                lineTo(w * 0.80f, h * 0.78f)
            }
            drawPath(z1, style.primary, style = stroke)
            drawPath(z2, style.primary, style = thinStroke)
        }

        IronPulseIcon.MORE_VERT -> {
            val r = w * 0.085f
            drawCircle(style.primary, r, Offset(cx, h * 0.24f))
            drawCircle(style.primary, r, Offset(cx, cy))
            drawCircle(style.primary, r, Offset(cx, h * 0.76f))
        }
    }
}
