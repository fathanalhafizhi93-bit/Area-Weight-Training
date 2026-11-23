package com.fathan.ironpulse.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.ironGlassBar
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.RestTimerDisplayState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val clock24Sdf = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.US)
}
private val clock12Sdf = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat = SimpleDateFormat("h:mm", Locale.US)
}
private val secSdf = object : ThreadLocal<SimpleDateFormat>() {
    override fun initialValue(): SimpleDateFormat = SimpleDateFormat("ss", Locale.US)
}

@Composable
fun AmbientPhaseBackdrop(modifier: Modifier = Modifier) {
    val tokens = LocalIronPulseTokens.current
    val suppressGlow = tokens.performance.noAmbientGlow

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("os_backdrop_${tokens.uiTheme.wireValue}")
            .drawWithCache {
                val canvasColor = tokens.canvasBg
                val strokePx = 1.dp.toPx()
                val baseGradient = when (tokens.uiTheme) {
                    UiTheme.UBUNTU_YARU -> Brush.verticalGradient(
                        0.0f to Color(0xFF47163B),
                        0.32f to Color(0xFF2D1628),
                        0.68f to Color(0xFF1E1B20),
                        1.0f to Color(0xFF151317)
                    )
                    UiTheme.CARBON_FORGE -> Brush.radialGradient(
                        colors = listOf(Color(0xFF171C26), Color(0xFF0A0C10), Color(0xFF050608)),
                        center = Offset(size.width * 0.5f, size.height * 0.18f),
                        radius = size.height.coerceAtLeast(800f) * 0.85f
                    )
                    UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) {
                        Brush.verticalGradient(
                            0.0f to Color(0xFF162A52),
                            0.34f to Color(0xFF1B3B6B),
                            0.70f to Color(0xFF124559),
                            1.0f to Color(0xFF0F3D3E)
                        )
                    } else {
                        Brush.verticalGradient(
                            0.0f to Color(0xFF66CCFF),
                            0.30f to Color(0xFFAEE9FF),
                            0.66f to Color(0xFFE4FAFF),
                            0.86f to Color(0xFFD7FDE6),
                            1.0f to Color(0xFFB3F4C9)
                        )
                    }
                    UiTheme.MATERIAL_YOU -> Brush.verticalGradient(
                        0.0f to tokens.palette.surfaceContainerLow,
                        0.45f to tokens.palette.surface,
                        1.0f to tokens.palette.surfaceContainerLowest
                    )
                    UiTheme.AMOLED_STEALTH -> null
                }
                val topGlow = when {
                    suppressGlow -> null
                    tokens.uiTheme == UiTheme.AMOLED_STEALTH -> null
                    tokens.uiTheme == UiTheme.CARBON_FORGE -> Brush.radialGradient(
                        colors = listOf(tokens.accentColor.copy(alpha = 0.09f), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.12f),
                        radius = size.width.coerceAtLeast(480f) * 0.78f
                    )
                    tokens.uiTheme == UiTheme.UBUNTU_YARU -> Brush.radialGradient(
                        colors = listOf(Color(0x3AE95420), Color(0x1A77216F), Color.Transparent),
                        center = Offset(size.width * 0.14f, size.height * 0.08f),
                        radius = size.width.coerceAtLeast(520f) * 0.96f
                    )
                    tokens.uiTheme == UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) {
                        Brush.radialGradient(
                            colors = listOf(Color(0x405BC6FA), Color(0x2038BDF8), Color.Transparent),
                            center = Offset(size.width * 0.18f, size.height * 0.07f),
                            radius = size.width.coerceAtLeast(520f) * 0.92f
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(Color(0xB8FFFFFF), Color(0x3C7CE8FF), Color.Transparent),
                            center = Offset(size.width * 0.18f, size.height * 0.05f),
                            radius = size.width.coerceAtLeast(520f) * 0.90f
                        )
                    }
                    else -> Brush.radialGradient(
                        colors = listOf(tokens.accentColor.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(size.width * 0.20f, size.height * 0.06f),
                        radius = size.width.coerceAtLeast(560f) * 0.88f
                    )
                }
                val secondaryGlow = when {
                    suppressGlow -> null
                    tokens.uiTheme == UiTheme.MATERIAL_YOU -> Brush.radialGradient(
                        colors = listOf(tokens.palette.tertiary.copy(alpha = 0.09f), Color.Transparent),
                        center = Offset(size.width * 0.86f, size.height * 0.72f),
                        radius = size.width.coerceAtLeast(520f) * 0.85f
                    )
                    tokens.uiTheme == UiTheme.UBUNTU_YARU -> Brush.radialGradient(
                        colors = listOf(Color(0x26E95420), Color.Transparent),
                        center = Offset(size.width * 0.88f, size.height * 0.84f),
                        radius = size.width.coerceAtLeast(460f) * 0.75f
                    )
                    tokens.uiTheme == UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) {
                        Brush.radialGradient(
                            colors = listOf(Color(0x362DD4BF), Color.Transparent),
                            center = Offset(size.width * 0.86f, size.height * 0.90f),
                            radius = size.width.coerceAtLeast(520f) * 0.88f
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(Color(0x484ADE80), Color.Transparent),
                            center = Offset(size.width * 0.86f, size.height * 0.92f),
                            radius = size.width.coerceAtLeast(520f) * 0.88f
                        )
                    }
                    else -> null
                }
                val carbonBgTwill = if (tokens.uiTheme == UiTheme.CARBON_FORGE) {
                    com.fathan.ironpulse.ui.theme.ThemeMaterialShaderCache.getCarbonTwillBrush(
                        cellPx = (3.dp.toPx()).toInt().coerceIn(5, 10),
                        subtle = true
                    )
                } else null

                val aeroHillBack = if (tokens.uiTheme == UiTheme.FRUTIGER_AERO) {
                    Path().apply {
                        moveTo(0f, size.height * 0.82f)
                        cubicTo(
                            size.width * 0.28f, size.height * 0.74f,
                            size.width * 0.64f, size.height * 0.87f,
                            size.width, size.height * 0.78f
                        )
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                } else null
                val aeroHillFront = if (tokens.uiTheme == UiTheme.FRUTIGER_AERO) {
                    Path().apply {
                        moveTo(0f, size.height * 0.89f)
                        cubicTo(
                            size.width * 0.35f, size.height * 0.82f,
                            size.width * 0.72f, size.height * 0.93f,
                            size.width, size.height * 0.85f
                        )
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                } else null
                val aeroHillBackBrush = if (tokens.isAeroNight) {
                    Brush.verticalGradient(
                        colors = listOf(Color(0x3D14B8A6), Color(0x280F766E)),
                        startY = size.height * 0.74f,
                        endY = size.height
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color(0x3D34D399), Color(0x2210B981)),
                        startY = size.height * 0.74f,
                        endY = size.height
                    )
                }
                val aeroHillFrontBrush = if (tokens.isAeroNight) {
                    Brush.verticalGradient(
                        colors = listOf(Color(0x452DD4BF), Color(0x330D9488)),
                        startY = size.height * 0.82f,
                        endY = size.height
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(Color(0x4A4ADE80), Color(0x3016A34A)),
                        startY = size.height * 0.82f,
                        endY = size.height
                    )
                }
                val yaruWave = if (tokens.uiTheme == UiTheme.UBUNTU_YARU && !suppressGlow) {
                    Path().apply {
                        moveTo(0f, size.height * 0.38f)
                        cubicTo(
                            size.width * 0.32f, size.height * 0.26f,
                            size.width * 0.68f, size.height * 0.48f,
                            size.width, size.height * 0.32f
                        )
                    }
                } else null

                val hudGridColor = tokens.accentColor.copy(alpha = 0.038f)
                val hudStepPx = 44.dp.toPx().coerceAtLeast(48f)

                onDrawBehind {
                    drawRect(canvasColor)
                    if (baseGradient != null) drawRect(baseGradient)
                    if (topGlow != null) drawRect(topGlow)
                    if (secondaryGlow != null) drawRect(secondaryGlow)

                    if (tokens.uiTheme == UiTheme.FRUTIGER_AERO) {
                        if (tokens.isAeroNight && !suppressGlow) {
                            val stars = listOf(
                                Triple(0.18f, 0.10f, 1.4f),
                                Triple(0.56f, 0.12f, 1.5f),
                                Triple(0.78f, 0.08f, 1.2f),
                                Triple(0.90f, 0.22f, 1.3f)
                            )
                            stars.forEach { (sx, sy, sr) ->
                                drawCircle(
                                    color = Color(0xFFDCF4FF).copy(alpha = 0.36f),
                                    radius = sr.dp.toPx(),
                                    center = Offset(size.width * sx, size.height * sy)
                                )
                            }
                        }
                        if (aeroHillBack != null) drawPath(aeroHillBack, aeroHillBackBrush)
                        if (aeroHillFront != null) drawPath(aeroHillFront, aeroHillFrontBrush)
                        if (!suppressGlow) {
                            val bubbles = if (tokens.isAeroNight) {
                                listOf(
                                    Triple(Offset(size.width * 0.86f, size.height * 0.14f), size.width * 0.11f, Color(0x187DD3FC)),
                                    Triple(Offset(size.width * 0.14f, size.height * 0.72f), size.width * 0.06f, Color(0x182DD4BF))
                                )
                            } else {
                                listOf(
                                    Triple(Offset(size.width * 0.86f, size.height * 0.14f), size.width * 0.11f, Color.White.copy(alpha = 0.18f)),
                                    Triple(Offset(size.width * 0.14f, size.height * 0.72f), size.width * 0.06f, Color(0x1E4ADE80))
                                )
                            }
                            val rimColor = if (tokens.isAeroNight) Color(0xFFBAE6FD).copy(alpha = 0.28f) else Color.White.copy(alpha = 0.40f)
                            val glintColor = if (tokens.isAeroNight) Color(0xFFE0F7FF).copy(alpha = 0.42f) else Color.White.copy(alpha = 0.56f)
                            bubbles.forEach { (center, rad, fill) ->
                                drawCircle(fill, rad, center)
                                drawCircle(
                                    color = rimColor,
                                    radius = rad,
                                    center = center,
                                    style = Stroke(strokePx)
                                )
                                drawCircle(
                                    color = glintColor,
                                    radius = rad * 0.15f,
                                    center = Offset(center.x - rad * 0.34f, center.y - rad * 0.34f)
                                )
                            }
                        }
                    }
                }
            }
    )
}

@Composable
fun IronPulseTopBar(
    activeTab: AppTab,
    aboutOpen: Boolean,
    onLogoClick: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onTestAlarm: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val theme = tokens.uiTheme
    val isAero = theme == UiTheme.FRUTIGER_AERO
    val isYaru = theme == UiTheme.UBUNTU_YARU
    val isCarbon = theme == UiTheme.CARBON_FORGE
    val isStealth = theme == UiTheme.AMOLED_STEALTH

    val iconShape = remember(theme) {
        when (theme) {
            UiTheme.MATERIAL_YOU -> RoundedCornerShape(999.dp)
            UiTheme.UBUNTU_YARU -> RoundedCornerShape(9.dp)
            UiTheme.CARBON_FORGE -> CutCornerShape(6.dp)
            UiTheme.AMOLED_STEALTH -> RoundedCornerShape(4.dp)
            UiTheme.FRUTIGER_AERO -> CircleShape
        }
    }
    val headerBorder = when (theme) {
        UiTheme.MATERIAL_YOU -> tokens.palette.outlineVariant.copy(alpha = 0.75f)
        UiTheme.CARBON_FORGE -> tokens.accentColor.copy(alpha = 0.45f)
        UiTheme.UBUNTU_YARU -> Color(0xFF141216)
        UiTheme.AMOLED_STEALTH -> tokens.accentColor.copy(alpha = 0.45f)
        UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) Color(0x885BC6FA) else Color(0x990099E5)
    }

    val settingsActive = activeTab == AppTab.SETTINGS && !aboutOpen

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("os_top_bar_${theme.wireValue}")
            .ironGlassBar(tokens = tokens, bottomHairlineColor = headerBorder)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                ThemeBrandLockup(
                    theme = theme,
                    accentColor = tokens.accentColor,
                    emblemSize = 26.dp,
                    fontSize = 18.5.sp,
                    onClick = onLogoClick
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TopBarIconButton(
                    icon = IronPulseIcon.SETTINGS,
                    contentDescription = lang.t("Buka pengaturan", "Open settings"),
                    active = settingsActive,
                    shape = iconShape,
                    onClick = onOpenSettings
                )
                TopBarIconButton(
                    icon = IronPulseIcon.INFO,
                    contentDescription = lang.t("Tentang IronPulse", "About IronPulse"),
                    active = aboutOpen,
                    shape = iconShape,
                    onClick = onOpenAbout
                )

                val ctaShape = remember(theme, tokens.buttonRadius) {
                    if (isCarbon) CutCornerShape(6.dp) else RoundedCornerShape(tokens.buttonRadius)
                }
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .tactilePress(disabledByPerf = tokens.performance.noAnimations)
                        .clip(ctaShape)
                        .then(
                            when {
                                isAero -> Modifier
                                    .background(
                                        Brush.verticalGradient(
                                            0.0f to Color(0xFF39B9F5),
                                            0.46f to Color(0xFF0088D4),
                                            0.52f to Color(0xFF0072B8),
                                            1.0f to Color(0xFF005B99)
                                        ),
                                        ctaShape
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.78f), ctaShape)
                                isStealth -> Modifier
                                    .background(Color(0xFF000000), ctaShape)
                                    .border(1.1.dp, tokens.accentColor, ctaShape)
                                isCarbon -> Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(tokens.accentColor, tokens.accentColor.copy(alpha = 0.84f))
                                        ),
                                        ctaShape
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.30f), ctaShape)
                                else -> Modifier.background(tokens.accentColor, ctaShape)
                            }
                        )
                        .clickable(onClick = onTestAlarm)
                        .padding(horizontal = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        ThemedIcon(
                            icon = IronPulseIcon.ALARM,
                            contentDescription = null,
                            tint = if (isStealth) tokens.accentColor else tokens.onAccentColor,
                            size = 14.dp
                        )
                        Text(
                            text = lang.t("Tes Alarm", "Test Alarm"),
                            fontSize = 11.sp,
                            fontFamily = tokens.fontPack.displayFont,
                            fontWeight = FontWeight.Bold,
                            color = if (isStealth) tokens.accentColor else tokens.onAccentColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: IronPulseIcon,
    contentDescription: String,
    active: Boolean,
    shape: androidx.compose.ui.graphics.Shape,
    onClick: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val isMaterialYou = tokens.uiTheme == UiTheme.MATERIAL_YOU
    val isAero = tokens.uiTheme == UiTheme.FRUTIGER_AERO
    val isAeroNight = tokens.isAeroNight
    val isStealth = tokens.uiTheme == UiTheme.AMOLED_STEALTH
    val containerColor = when {
        active && isMaterialYou -> tokens.palette.secondaryContainer
        active && isAero -> if (isAeroNight) Color(0xFF0099E5) else Color(0xFF0084CE)
        active && isStealth -> tokens.accentColor.copy(alpha = 0.16f)
        active -> tokens.accentColor.copy(alpha = 0.22f)
        isAero -> if (isAeroNight) Color(0xD91E3D6B) else Color(0xF5FFFFFF)
        isStealth -> Color(0xFF05070A)
        isMaterialYou -> tokens.palette.surfaceContainerHigh
        else -> tokens.controlBg
    }
    val borderColor = when {
        active && isMaterialYou -> tokens.palette.secondaryContainer
        active && isAero -> if (isAeroNight) Color(0xFF93E2FF) else Color(0xFF005F9E)
        active -> tokens.accentColor
        isAero -> if (isAeroNight) Color(0x885BC6FA) else Color(0x990082C8)
        isStealth -> tokens.accentColor.copy(alpha = 0.42f)
        isMaterialYou -> tokens.palette.outlineVariant
        tokens.uiTheme == UiTheme.UBUNTU_YARU -> Color(0xFF141216)
        else -> Color.White.copy(alpha = 0.18f)
    }
    val tint = when {
        active && isMaterialYou -> tokens.palette.onSecondaryContainer
        active && isAero -> Color.White
        active -> tokens.accentColor
        isAero -> if (isAeroNight) Color(0xFFE0F2FE) else Color(0xFF073B66)
        else -> tokens.palette.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .size(38.dp)
            .tactilePress(disabledByPerf = tokens.performance.noAnimations)
    ) {
        Box(
            modifier = when (tokens.uiTheme) {
                UiTheme.FRUTIGER_AERO -> Modifier.background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(
                            alpha = when {
                                isAeroNight && active -> 0.30f
                                isAeroNight -> 0.20f
                                active -> 0.34f
                                else -> 0.72f
                            }
                        ),
                        0.45f to Color.Transparent
                    )
                )
                UiTheme.UBUNTU_YARU, UiTheme.CARBON_FORGE -> Modifier.background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.09f),
                        0.45f to Color.Transparent
                    )
                )
                else -> Modifier
            },
            contentAlignment = Alignment.Center
        ) {
            ThemedIcon(
                icon = icon,
                contentDescription = contentDescription,
                tint = tint,
                size = 19.dp,
                filledBadge = false
            )
        }
    }
}

@Composable
fun IronPulseBottomNav(
    activeTab: AppTab,
    restTimerFlow: StateFlow<RestTimerDisplayState>,
    onSelectTab: (AppTab) -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val theme = tokens.uiTheme
    val isMaterialYou = theme == UiTheme.MATERIAL_YOU
    val isCarbon = theme == UiTheme.CARBON_FORGE
    val isYaru = theme == UiTheme.UBUNTU_YARU
    val isStealth = theme == UiTheme.AMOLED_STEALTH
    val isAero = theme == UiTheme.FRUTIGER_AERO

    val items = remember(lang) {
        listOf(
            Triple(AppTab.TODAY, lang.t("Hari Ini", "Today"), IronPulseIcon.TODAY),
            Triple(AppTab.SCHEDULE, lang.t("Jadwal", "Schedule"), IronPulseIcon.SCHEDULE),
            Triple(AppTab.HISTORY, lang.t("Riwayat", "History"), IronPulseIcon.HISTORY),
            Triple(AppTab.EXERCISES, lang.t("Gerakan", "Exercises"), IronPulseIcon.EXERCISES)
        )
    }

    val activeIdx = when (activeTab) {
        AppTab.TODAY, AppTab.WORKOUT -> 0
        AppTab.SCHEDULE -> 1
        AppTab.HISTORY -> 2
        AppTab.EXERCISES -> 3
        AppTab.SETTINGS -> -1
    }

    val topBorder = when (theme) {
        UiTheme.MATERIAL_YOU -> tokens.palette.outlineVariant.copy(alpha = 0.75f)
        UiTheme.CARBON_FORGE -> tokens.accentColor.copy(alpha = 0.45f)
        UiTheme.UBUNTU_YARU -> Color(0xFF141216)
        UiTheme.AMOLED_STEALTH -> tokens.accentColor.copy(alpha = 0.48f)
        UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) Color(0x885BC6FA) else Color(0xAA0099E5)
    }

    // Outer wrapper differs per OS: Ubuntu Dock floats subtly, Aero has glossy taskbar rail,
    // Carbon Forge has cockpit rocker bar, Stealth has pure black HUD rail, Material You has expressive M3 bar.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("os_bottom_nav_${theme.wireValue}")
            .then(
                if (isYaru) {
                    Modifier
                        .background(Color(0xEC161318))
                        .navigationBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                } else {
                    Modifier
                        .ironGlassBar(tokens, topHairlineColor = topBorder)
                        .navigationBarsPadding()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .then(
                    if (isYaru) {
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2B252C))
                            .border(1.dp, Color.White.copy(alpha = 0.11f), RoundedCornerShape(16.dp))
                    } else {
                        Modifier
                    }
                )
                .height(68.dp)
                .padding(horizontal = if (isYaru || isAero) 8.dp else 4.dp)
        ) {
            val slotWidth = maxWidth / items.size
            val slotWidthPx = with(LocalDensity.current) { slotWidth.toPx() }

            // Exact-aligned gliding GPU spring indicator customized per OS
            if (activeIdx >= 0) {
                val indicatorIndexState = animateFloatAsState(
                    targetValue = activeIdx.toFloat(),
                    animationSpec = if (tokens.performance.noAnimations) {
                        tween(0)
                    } else {
                        IronPulseMotion.IndicatorFloatSpring
                    },
                    label = "navIndicatorIndex"
                )

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = indicatorIndexState.value * slotWidthPx
                        }
                        .width(slotWidth)
                        .fillMaxHeight()
                        .padding(vertical = if (isYaru || isCarbon) 5.dp else 0.dp, horizontal = 3.dp),
                    contentAlignment = when {
                        isYaru -> Alignment.BottomCenter
                        isStealth -> Alignment.Center
                        else -> Alignment.TopCenter
                    }
                ) {
                    when {
                        isYaru -> {
                            // Ubuntu Dock tile highlight + clean bottom orange pill dash with generous label clearance
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(Color.White.copy(alpha = 0.085f))
                                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(11.dp)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 2.dp)
                                        .width(18.dp)
                                        .height(2.5.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color(0xFFE95420))
                                )
                            }
                        }
                        isAero -> {
                            // Vista / Aqua Glossy Crystal Pill centered precisely on the 32dp icon stage
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .width(62.dp)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        if (tokens.isAeroNight) {
                                            Brush.verticalGradient(
                                                0.0f to Color(0xFF4CC9FF),
                                                0.46f to Color(0xFF008FE0),
                                                0.52f to Color(0xFF006EB8),
                                                1.0f to Color(0xFF00528C)
                                            )
                                        } else {
                                            Brush.verticalGradient(
                                                0.0f to Color(0xFF56C8FF),
                                                0.46f to Color(0xFF008FD9),
                                                0.52f to Color(0xFF0076BD),
                                                1.0f to Color(0xFF005C99)
                                            )
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (tokens.isAeroNight) Color(0xCCE0F4FF) else Color.White.copy(alpha = 0.85f),
                                        RoundedCornerShape(999.dp)
                                    )
                            )
                        }
                        isCarbon -> {
                            // Industrial Rocker Plate + sleek top LED bar safely clear of icon
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CutCornerShape(6.dp))
                                    .background(tokens.accentColor.copy(alpha = 0.12f))
                                    .border(1.dp, tokens.accentColor.copy(alpha = 0.42f), CutCornerShape(6.dp)),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(26.dp)
                                        .height(2.5.dp)
                                        .background(tokens.accentColor)
                                )
                            }
                        }
                        isStealth -> {
                            // Crisp single-layer Tactical HUD corner brackets around active slot
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 6.dp, horizontal = 5.dp)
                            ) {
                                val arm = 7.dp.toPx()
                                val sw = 1.4.dp.toPx()
                                val c = tokens.accentColor
                                drawLine(c, Offset(0f, 0f), Offset(arm, 0f), sw)
                                drawLine(c, Offset(0f, 0f), Offset(0f, arm), sw)
                                drawLine(c, Offset(size.width, 0f), Offset(size.width - arm, 0f), sw)
                                drawLine(c, Offset(size.width, 0f), Offset(size.width, arm), sw)
                                drawLine(c, Offset(0f, size.height), Offset(arm, size.height), sw)
                                drawLine(c, Offset(0f, size.height), Offset(0f, size.height - arm), sw)
                                drawLine(c, Offset(size.width, size.height), Offset(size.width - arm, size.height), sw)
                                drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - arm), sw)
                            }
                        }
                        isMaterialYou -> {
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .width(62.dp)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(tokens.palette.secondaryContainer)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) {
                items.forEachIndexed { idx, (tab, label, icon) ->
                    val isActive = idx == activeIdx

                    val itemContentColor by animateColorAsState(
                        targetValue = when {
                            isActive && isMaterialYou -> tokens.palette.onSurface
                            isActive && isAero -> if (tokens.isAeroNight) Color(0xFF7DD3FC) else Color(0xFF004B80)
                            isActive -> tokens.accentColor
                            isAero -> if (tokens.isAeroNight) Color(0xFFB4D3EB) else Color(0xFF1E496E)
                            else -> tokens.mutedTextColor
                        },
                        animationSpec = tween(if (tokens.performance.noAnimations) 0 else 180),
                        label = "navTint"
                    )
                    val iconScaleState = animateFloatAsState(
                        targetValue = if (isActive && !tokens.performance.noAnimations) 1.07f else 1f,
                        animationSpec = IronPulseMotion.PressSpring,
                        label = "navIconScale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .tactilePress(pressedScale = 0.94f, disabledByPerf = tokens.performance.noAnimations)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onSelectTab(tab) },
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ThemedIcon(
                                    icon = icon,
                                    contentDescription = label,
                                    tint = when {
                                        isActive && isMaterialYou -> tokens.palette.onSecondaryContainer
                                        isActive && isAero -> Color.White
                                        else -> itemContentColor
                                    },
                                    size = 21.dp,
                                    filledBadge = false,
                                    modifier = Modifier.graphicsLayer {
                                        val s = iconScaleState.value
                                        scaleX = s
                                        scaleY = s
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            if (tab == AppTab.TODAY && activeTab != AppTab.WORKOUT) {
                                BottomNavTodayLabel(
                                    defaultLabel = label,
                                    isActive = isActive,
                                    defaultColor = itemContentColor,
                                    accentColor = tokens.accentTextColor,
                                    restTimerFlow = restTimerFlow
                                )
                            } else {
                                Text(
                                    text = if (isStealth) label.uppercase() else label,
                                    fontSize = if (isStealth) 9.5.sp else 11.sp,
                                    fontFamily = tokens.fontPack.displayFont,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    letterSpacing = if (isStealth || isCarbon) 0.4.sp else 0.sp,
                                    color = itemContentColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavTodayLabel(
    defaultLabel: String,
    isActive: Boolean,
    defaultColor: Color,
    accentColor: Color,
    restTimerFlow: StateFlow<RestTimerDisplayState>
) {
    val tokens = LocalIronPulseTokens.current
    val isStealth = tokens.uiTheme == UiTheme.AMOLED_STEALTH
    val labelFlow = remember(restTimerFlow) {
        restTimerFlow
            .map { if (it.isActive) GymModelHelpers.formatRestDuration(it.remainingSeconds) else null }
            .distinctUntilChanged()
    }
    val compactRestText by labelFlow.collectAsStateWithLifecycle(initialValue = null)
    val showCompactRest = compactRestText != null
    val renderedText = compactRestText ?: if (isStealth) defaultLabel.uppercase() else defaultLabel
    Text(
        text = renderedText,
        fontSize = if (isStealth) 10.sp else 11.sp,
        fontFamily = if (showCompactRest) tokens.fontPack.monoFont else tokens.fontPack.displayFont,
        fontWeight = if (showCompactRest || isActive) FontWeight.Bold else FontWeight.Medium,
        letterSpacing = if (isStealth || tokens.uiTheme == UiTheme.CARBON_FORGE) 0.5.sp else 0.sp,
        color = if (showCompactRest) accentColor else defaultColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PhaseLogoEmblem(
    timePhase: DayTimePhase,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    ThemedPhaseLogoEmblem(
        timePhase = timePhase,
        theme = tokens.uiTheme,
        modifier = modifier
    )
}

/**
 * Isolated per-digit fluid clock that collects [clockSecondsFlow] directly so parent screens
 * do NOT recompose every second, matching JS FluidClockDisplay's per-digit vertical pop.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveFluidClockSlot(
    clockSecondsFlow: StateFlow<Long>,
    clockFormat: ClockFormat,
    lang: AppLanguage
) {
    val tabActive = LocalTabActive.current
    val clockSeconds = if (tabActive) {
        clockSecondsFlow.collectAsStateWithLifecycle().value
    } else {
        remember(clockSecondsFlow) { clockSecondsFlow.value }
    }
    val tokens = LocalIronPulseTokens.current
    val noAnim = tokens.performance.noAnimations

    val date = remember(clockSeconds) { Date(clockSeconds * 1000L) }
    val hhmm = remember(clockSeconds, clockFormat) {
        val sdf = if (clockFormat == ClockFormat.H12) {
            clock12Sdf.get() ?: SimpleDateFormat("h:mm", Locale.US)
        } else {
            clock24Sdf.get() ?: SimpleDateFormat("HH:mm", Locale.US)
        }
        sdf.format(date)
    }
    val ss = remember(clockSeconds) {
        (secSdf.get() ?: SimpleDateFormat("ss", Locale.US)).format(date)
    }
    val minuteBucket = clockSeconds / 60L
    val periodLabel = remember(minuteBucket, clockFormat, lang) {
        if (clockFormat == ClockFormat.H24) {
            ""
        } else {
            val cal = Calendar.getInstance().apply { timeInMillis = clockSeconds * 1000L }
            val h = cal.get(Calendar.HOUR_OF_DAY)
            if (lang == AppLanguage.ID) {
                when {
                    h < 5 -> "Dini hari"
                    h < 11 -> "Pagi"
                    h < 15 -> "Siang"
                    h < 19 -> "Sore"
                    else -> "Malam"
                }
            } else {
                if (h >= 12) "PM" else "AM"
            }
        }
    }

    val accentBright = tokens.phaseTokens.accentBright
    val clockMainColor = tokens.palette.onSurface
    val periodPillBg = remember(tokens.phaseTokens.accent) { tokens.phaseTokens.accent.copy(alpha = 0.16f) }
    val periodPillBorder = remember(accentBright) { BorderStroke(1.dp, accentBright.copy(alpha = 0.35f)) }
    val colonMainColor = remember(clockMainColor) { clockMainColor.copy(alpha = 0.65f) }
    val colonSecColor = remember(accentBright) { accentBright.copy(alpha = 0.72f) }
    val periodPillShape = remember { RoundedCornerShape(999.dp) }

    // Isolated GPU RenderNode for 1Hz digit transitions so parent hero card doesn't re-record
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { }
    ) {
        // Uniform display scale for HH, MM, and SS so seconds match hours/minutes cleanly without wrapping
        val clockFontSize = when {
            maxWidth < 192.dp -> 28.sp
            maxWidth < 222.dp -> 32.sp
            maxWidth < 252.dp -> 35.sp
            else -> 38.sp
        }
        val clockLineHeight = clockFontSize * 1.08f
        val mainDigitStyle = remember(clockFontSize, clockMainColor, tokens.fontPack.monoFont) {
            TextStyle(
                fontFamily = tokens.fontPack.monoFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = clockFontSize,
                lineHeight = clockLineHeight,
                letterSpacing = (-0.8).sp,
                fontFeatureSettings = "tnum",
                color = clockMainColor
            )
        }
        val secondsDigitStyle = remember(clockFontSize, accentBright, clockMainColor, tokens.fontPack.monoFont) {
            mainDigitStyle.copy(color = accentBright)
        }

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (idx in hhmm.indices) {
                    val ch = hhmm[idx]
                    if (ch == ':') {
                        Text(
                            text = ":",
                            fontFamily = tokens.fontPack.monoFont,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = clockFontSize,
                            lineHeight = clockLineHeight,
                            color = colonMainColor,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    } else {
                        ClockDigitCell(
                            digit = ch,
                            noAnimations = noAnim,
                            style = mainDigitStyle
                        )
                    }
                }

                Text(
                    text = ":",
                    fontFamily = tokens.fontPack.monoFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = clockFontSize,
                    lineHeight = clockLineHeight,
                    color = colonSecColor,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                for (idx in ss.indices) {
                    ClockDigitCell(
                        digit = ss[idx],
                        noAnimations = noAnim,
                        style = secondsDigitStyle
                    )
                }
            }

            if (periodLabel.isNotEmpty()) {
                Surface(
                    shape = periodPillShape,
                    color = periodPillBg,
                    border = periodPillBorder,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = periodLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentBright,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockDigitCell(
    digit: Char,
    noAnimations: Boolean,
    style: TextStyle
) {
    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            if (noAnimations) {
                EnterTransition.None togetherWith ExitTransition.None
            } else {
                (slideInVertically(tween(170)) { -it / 2 } + fadeIn(tween(150))) togetherWith
                    (slideOutVertically(tween(130)) { it / 2 } + fadeOut(tween(120)))
            }
        },
        label = "clockDigitCell"
    ) { ch ->
        Text(text = ch.toString(), style = style)
    }
}

@Composable
fun TactileStepperControl(
    valueText: String,
    label: String? = null,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onDirectEdit: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    var editing by remember { mutableStateOf(false) }
    var draft by remember(valueText) { mutableStateOf(valueText) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (!label.isNullOrBlank()) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = tokens.mutedTextColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Row(
            modifier = Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(tokens.inputRadius))
                .background(tokens.wellBg, RoundedCornerShape(tokens.inputRadius))
                .border(1.dp, tokens.subtleBorderColor, RoundedCornerShape(tokens.inputRadius))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .tactilePress(pressedScale = 0.88f, disabledByPerf = tokens.performance.noAnimations)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tokens.controlBg)
                    .clickable(onClick = onDecrement),
                contentAlignment = Alignment.Center
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.REMOVE,
                    contentDescription = "-",
                    tint = tokens.palette.onSurface,
                    size = 16.dp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = onDirectEdit != null) {
                        draft = valueText
                        editing = true
                    },
                contentAlignment = Alignment.Center
            ) {
                if (editing && onDirectEdit != null) {
                    BasicTextField(
                        value = draft,
                        onValueChange = { next ->
                            draft = next
                            onDirectEdit(next)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            color = tokens.accentTextColor,
                            fontFamily = tokens.fontPack.monoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(tokens.accentTextColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = valueText,
                        fontFamily = tokens.fontPack.monoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = tokens.palette.onSurface,
                        maxLines = 1
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .tactilePress(pressedScale = 0.88f, disabledByPerf = tokens.performance.noAnimations)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tokens.controlBg)
                    .clickable(onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.ADD,
                    contentDescription = "+",
                    tint = tokens.accentTextColor,
                    size = 16.dp
                )
            }
        }
    }
}

@Composable
fun ThemedIronDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    maxWidth: Dp = 460.dp,
    borderOverride: Color? = null,
    scrollable: Boolean = false,
    title: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val noAnim = tokens.performance.noAnimations

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = modifier
                    .widthIn(max = maxWidth)
                    .fillMaxWidth()
                    .fluidModalEntrance(disabledByPerf = noAnim)
                    .ironGlassSurface(
                        tokens = tokens,
                        borderOverride = borderOverride
                    )
                    .heightIn(max = 680.dp)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (title != null) {
                    title()
                }
                if (scrollable) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        content()
                    }
                } else {
                    content()
                }
                if (actions != null) {
                    actions()
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val noAnim = tokens.performance.noAnimations

    ThemedIronDialog(
        onDismissRequest = onDismiss,
        maxWidth = 420.dp,
        borderOverride = tokens.dangerColor.copy(alpha = 0.45f),
        modifier = Modifier.testTag("confirm_delete_dialog"),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tokens.dangerColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    ThemedIcon(
                        icon = IronPulseIcon.WARNING,
                        contentDescription = null,
                        tint = tokens.dangerColor,
                        size = 20.dp
                    )
                }
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .tactilePress(disabledByPerf = noAnim)
                ) {
                    Text(cancelLabel, fontWeight = FontWeight.SemiBold, color = tokens.palette.onSurface)
                }
                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .tactilePress(disabledByPerf = noAnim)
                ) {
                    Text(confirmLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = tokens.palette.onSurfaceVariant
        )
    }
}
