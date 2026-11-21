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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
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
            .drawWithCache {
                val canvasColor = tokens.canvasBg
                val baseGradient = when (tokens.uiTheme) {
                    UiTheme.UBUNTU_YARU -> Brush.verticalGradient(
                        0.0f to tokens.canvasSecondaryBg,
                        0.45f to tokens.canvasBg,
                        1.0f to Color(0xFF171717)
                    )
                    UiTheme.AEROGLASS -> Brush.verticalGradient(
                        0.0f to tokens.canvasBg,
                        0.55f to tokens.canvasSecondaryBg,
                        1.0f to Color(0xFF030B14)
                    )
                    UiTheme.MIDNIGHT_FROSTED -> Brush.verticalGradient(
                        0.0f to tokens.canvasBg,
                        0.60f to tokens.canvasSecondaryBg,
                        1.0f to Color(0xFF04060E)
                    )
                    UiTheme.CARBON_FORGE -> Brush.radialGradient(
                        colors = listOf(tokens.canvasSecondaryBg, tokens.canvasBg),
                        center = Offset(size.width * 0.5f, size.height * 0.25f),
                        radius = size.height.coerceAtLeast(800f) * 0.85f
                    )
                    else -> null
                }
                val topGlow = when {
                    suppressGlow -> null
                    tokens.uiTheme == UiTheme.CARBON_FORGE -> null
                    tokens.uiTheme == UiTheme.UBUNTU_YARU -> Brush.radialGradient(
                        colors = listOf(Color(0x28E95420), Color.Transparent),
                        center = Offset(size.width * 0.14f, size.height * 0.06f),
                        radius = size.width.coerceAtLeast(500f) * 0.92f
                    )
                    tokens.uiTheme == UiTheme.MATERIAL_YOU -> Brush.radialGradient(
                        colors = listOf(tokens.accentColor.copy(alpha = 0.09f), Color.Transparent),
                        center = Offset(size.width * 0.5f, 0f),
                        radius = size.width.coerceAtLeast(600f) * 0.95f
                    )
                    tokens.uiTheme == UiTheme.AEROGLASS -> Brush.radialGradient(
                        colors = listOf(Color(0x4C38BDF8), Color.Transparent),
                        center = Offset(size.width * 0.86f, size.height * 0.07f),
                        radius = size.width.coerceAtLeast(500f) * 0.95f
                    )
                    tokens.uiTheme == UiTheme.MIDNIGHT_FROSTED -> Brush.radialGradient(
                        colors = listOf(Color(0x3D818CF8), Color.Transparent),
                        center = Offset(size.width * 0.82f, size.height * 0.08f),
                        radius = size.width.coerceAtLeast(500f) * 0.92f
                    )
                    else -> Brush.radialGradient(
                        colors = listOf(tokens.phaseTokens.glowPrimary, Color.Transparent),
                        center = Offset(size.width * 0.84f, size.height * 0.08f),
                        radius = size.width.coerceAtLeast(500f) * 0.9f
                    )
                }
                val bottomGlow = when {
                    suppressGlow ||
                        tokens.uiTheme == UiTheme.MATERIAL_YOU ||
                        tokens.uiTheme == UiTheme.CARBON_FORGE ||
                        tokens.uiTheme == UiTheme.UBUNTU_YARU -> null
                    tokens.uiTheme == UiTheme.MIDNIGHT_FROSTED -> Brush.radialGradient(
                        colors = listOf(tokens.accentColor.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width * 0.16f, size.height * 0.80f),
                        radius = size.width.coerceAtLeast(450f) * 0.90f
                    )
                    tokens.uiTheme == UiTheme.AEROGLASS -> Brush.radialGradient(
                        colors = listOf(Color(0x340284C7), Color.Transparent),
                        center = Offset(size.width * 0.14f, size.height * 0.82f),
                        radius = size.width.coerceAtLeast(450f) * 0.88f
                    )
                    else -> Brush.radialGradient(
                        colors = listOf(tokens.phaseTokens.glowSecondary, Color.Transparent),
                        center = Offset(size.width * 0.12f, size.height * 0.82f),
                        radius = size.width.coerceAtLeast(450f) * 0.85f
                    )
                }
                onDrawBehind {
                    drawRect(canvasColor)
                    if (baseGradient != null) drawRect(baseGradient)
                    if (topGlow != null) drawRect(topGlow)
                    if (bottomGlow != null) drawRect(bottomGlow)
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
    val isMaterialYou = tokens.uiTheme == UiTheme.MATERIAL_YOU
    val isFrosted = tokens.uiTheme == UiTheme.MIDNIGHT_FROSTED
    val isAero = tokens.uiTheme == UiTheme.AEROGLASS
    val iconShape = RoundedCornerShape(
        when {
            isFrosted || isMaterialYou -> 999.dp
            isAero || tokens.uiTheme == UiTheme.OBSIDIAN -> 12.dp
            tokens.uiTheme == UiTheme.UBUNTU_YARU -> 10.dp
            else -> 8.dp
        }
    )
    val headerBorder = when (tokens.uiTheme) {
        UiTheme.AEROGLASS -> Color(0x667DD3FC)
        UiTheme.MATERIAL_YOU -> tokens.palette.outlineVariant.copy(alpha = 0.75f)
        UiTheme.MIDNIGHT_FROSTED -> Color.White.copy(alpha = 0.22f)
        UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.14f)
        UiTheme.UBUNTU_YARU -> Color(0xFF141414)
        UiTheme.OBSIDIAN -> tokens.phaseTokens.headerBorder
    }

    val settingsActive = activeTab == AppTab.SETTINGS && !aboutOpen
    val metallicIronBrush = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFB5BDC9))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .ironGlassBar(tokens = tokens, bottomHairlineColor = headerBorder)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Dual-tone brand wordmark: Metallic gradient "Iron" + glowing accent "Pulse"
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            brush = metallicIronBrush,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Iron")
                    }
                    withStyle(
                        SpanStyle(
                            color = tokens.accentColor,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("Pulse")
                    }
                },
                fontSize = 23.sp,
                letterSpacing = (-0.6).sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .tactilePress(disabledByPerf = tokens.performance.noAnimations)
                    .clickable(onClick = onLogoClick)
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TopBarIconButton(
                    icon = Icons.Rounded.Settings,
                    contentDescription = lang.t("Buka pengaturan", "Open settings"),
                    active = settingsActive,
                    shape = iconShape,
                    onClick = onOpenSettings
                )
                TopBarIconButton(
                    icon = Icons.Rounded.Info,
                    contentDescription = lang.t("Tentang IronPulse", "About IronPulse"),
                    active = aboutOpen,
                    shape = iconShape,
                    onClick = onOpenAbout
                )

                val ctaShape = RoundedCornerShape(tokens.buttonRadius)
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .tactilePress(disabledByPerf = tokens.performance.noAnimations)
                        .clip(ctaShape)
                        .background(tokens.accentColor, ctaShape)
                        .then(
                            if (isAero) {
                                Modifier.background(
                                    Brush.verticalGradient(
                                        0.0f to Color.White.copy(alpha = 0.34f),
                                        0.48f to Color.White.copy(alpha = 0.08f),
                                        0.50f to Color.Transparent,
                                        1.0f to Color.Black.copy(alpha = 0.14f)
                                    ),
                                    ctaShape
                                )
                            } else Modifier
                        )
                        .clickable(onClick = onTestAlarm)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lang.t("Tes Alarm", "Test Alarm"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.onAccentColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    shape: RoundedCornerShape,
    onClick: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val isMaterialYou = tokens.uiTheme == UiTheme.MATERIAL_YOU
    val isAero = tokens.uiTheme == UiTheme.AEROGLASS
    val isFrosted = tokens.uiTheme == UiTheme.MIDNIGHT_FROSTED
    val containerColor = when {
        active && isMaterialYou -> tokens.palette.secondaryContainer
        active -> tokens.accentColor.copy(alpha = 0.18f)
        isMaterialYou -> tokens.palette.surfaceContainerHigh
        tokens.uiTheme == UiTheme.UBUNTU_YARU -> tokens.controlBg
        isFrosted -> Color.White.copy(alpha = 0.09f)
        isAero -> Color(0x2838BDF8)
        else -> tokens.controlBg
    }
    val borderColor = when {
        active && isMaterialYou -> tokens.palette.secondaryContainer
        active -> tokens.accentColor.copy(alpha = 0.65f)
        isMaterialYou -> tokens.palette.outlineVariant
        isAero -> Color(0x66BAE6FD)
        isFrosted -> Color.White.copy(alpha = 0.24f)
        tokens.uiTheme == UiTheme.UBUNTU_YARU -> Color(0xFF141414)
        tokens.uiTheme == UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.18f)
        else -> Color.White.copy(alpha = 0.14f)
    }
    val tint = when {
        active && isMaterialYou -> tokens.palette.onSecondaryContainer
        active -> tokens.accentColor
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
            modifier = when {
                isAero -> Modifier.background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.28f),
                        0.5f to Color.Transparent
                    )
                )
                tokens.uiTheme == UiTheme.UBUNTU_YARU || tokens.uiTheme == UiTheme.CARBON_FORGE -> Modifier.background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.08f),
                        0.45f to Color.Transparent
                    )
                )
                else -> Modifier
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(18.dp)
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
    val isFrosted = tokens.uiTheme == UiTheme.MIDNIGHT_FROSTED
    val isMaterialYou = tokens.uiTheme == UiTheme.MATERIAL_YOU
    val isAero = tokens.uiTheme == UiTheme.AEROGLASS
    val isCarbon = tokens.uiTheme == UiTheme.CARBON_FORGE
    val isYaru = tokens.uiTheme == UiTheme.UBUNTU_YARU

    val items = remember(lang) {
        listOf(
            Triple(AppTab.TODAY, lang.t("Hari Ini", "Today"), Icons.Rounded.PlayCircle),
            Triple(AppTab.SCHEDULE, lang.t("Jadwal", "Schedule"), Icons.Rounded.CalendarMonth),
            Triple(AppTab.HISTORY, lang.t("Riwayat", "History"), Icons.Rounded.History),
            Triple(AppTab.EXERCISES, lang.t("Gerakan", "Exercises"), Icons.Rounded.FitnessCenter)
        )
    }

    val activeIdx = when (activeTab) {
        AppTab.TODAY, AppTab.WORKOUT -> 0
        AppTab.SCHEDULE -> 1
        AppTab.HISTORY -> 2
        AppTab.EXERCISES -> 3
        AppTab.SETTINGS -> -1
    }

    val outerPadding = if (isFrosted) {
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 14.dp, end = 14.dp, bottom = 10.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    val topBorder = when (tokens.uiTheme) {
        UiTheme.AEROGLASS -> Color(0x6BB9E6FE)
        UiTheme.MATERIAL_YOU -> tokens.palette.outlineVariant.copy(alpha = 0.75f)
        UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.16f)
        UiTheme.UBUNTU_YARU -> Color(0xFF141414)
        UiTheme.OBSIDIAN -> tokens.phaseTokens.headerBorder
        UiTheme.MIDNIGHT_FROSTED -> Color.White.copy(alpha = 0.28f)
    }

    val frostedDockShape = remember { RoundedCornerShape(28.dp) }
    val dockModifier = if (isFrosted) {
        Modifier
            .fillMaxWidth()
            .ironGlassBar(tokens, shapeRadius = 28.dp)
            .border(1.dp, topBorder, frostedDockShape)
    } else {
        Modifier
            .fillMaxWidth()
            .ironGlassBar(tokens, topHairlineColor = topBorder)
            .navigationBarsPadding()
    }

    Box(modifier = outerPadding) {
        BoxWithConstraints(
            modifier = dockModifier
                .height(if (isFrosted) 62.dp else 64.dp)
                .padding(horizontal = if (isFrosted || isAero || isYaru) 6.dp else 4.dp)
        ) {
            val slotWidth = maxWidth / items.size
            val slotWidthPx = with(LocalDensity.current) { slotWidth.toPx() }

            // Zero-relayout gliding GPU spring indicator behind active tab
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
                        .padding(vertical = if (isFrosted || isAero || isYaru) 5.dp else 0.dp, horizontal = 3.dp),
                    contentAlignment = if (isYaru) Alignment.BottomCenter else Alignment.TopCenter
                ) {
                    when {
                        isFrosted -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.22f),
                                                Color.White.copy(alpha = 0.11f)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.34f), RoundedCornerShape(22.dp))
                            )
                        }
                        isAero -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x3438BDF8))
                                    .background(
                                        Brush.verticalGradient(
                                            0.0f to Color.White.copy(alpha = 0.30f),
                                            0.48f to Color.White.copy(alpha = 0.06f),
                                            0.5f to Color.Transparent,
                                            1.0f to Color(0xFF0EA5E9).copy(alpha = 0.16f)
                                        )
                                    )
                                    .border(1.dp, Color(0x75B8E6FE), RoundedCornerShape(12.dp))
                            )
                        }
                        isYaru -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.07f))
                                    .border(1.dp, Color.White.copy(alpha = 0.09f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 2.dp)
                                        .width(24.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(tokens.accentColor)
                                )
                            }
                        }
                        isMaterialYou -> {
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp)
                                    .width(58.dp)
                                    .height(30.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(tokens.palette.secondaryContainer)
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .width(if (isCarbon) 42.dp else 34.dp)
                                    .height(if (isCarbon) 3.dp else 2.5.dp)
                                    .background(
                                        tokens.accentColor,
                                        if (isCarbon) RoundedCornerShape(0.dp) else RoundedCornerShape(999.dp)
                                    )
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { idx, (tab, label, icon) ->
                    val isActive = idx == activeIdx

                    val itemContentColor by animateColorAsState(
                        targetValue = when {
                            isActive && isMaterialYou -> tokens.palette.onSurface
                            isActive -> tokens.accentColor
                            else -> tokens.mutedTextColor
                        },
                        animationSpec = tween(if (tokens.performance.noAnimations) 0 else 180),
                        label = "navTint"
                    )
                    val iconScaleState = animateFloatAsState(
                        targetValue = if (isActive && !tokens.performance.noAnimations) 1.09f else 1f,
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
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(58.dp)
                                    .height(30.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isActive && isMaterialYou) {
                                        tokens.palette.onSecondaryContainer
                                    } else {
                                        itemContentColor
                                    },
                                    modifier = Modifier
                                        .size(21.dp)
                                        .graphicsLayer {
                                            val s = iconScaleState.value
                                            scaleX = s
                                            scaleY = s
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            if (tab == AppTab.TODAY && activeTab != AppTab.WORKOUT) {
                                BottomNavTodayLabel(
                                    defaultLabel = label,
                                    isActive = isActive,
                                    defaultColor = itemContentColor,
                                    accentColor = tokens.accentColor,
                                    restTimerFlow = restTimerFlow
                                )
                            } else {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
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
    val labelFlow = remember(restTimerFlow) {
        restTimerFlow
            .map { if (it.isActive) GymModelHelpers.formatRestDuration(it.remainingSeconds) else null }
            .distinctUntilChanged()
    }
    val compactRestText by labelFlow.collectAsStateWithLifecycle(initialValue = null)
    val showCompactRest = compactRestText != null
    Text(
        text = compactRestText ?: defaultLabel,
        fontSize = 11.sp,
        fontFamily = if (showCompactRest) FontFamily.Monospace else FontFamily.SansSerif,
        fontWeight = if (showCompactRest || isActive) FontWeight.Bold else FontWeight.Medium,
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
    val accent = tokens.phaseTokens.accentBright
    val cornerRadius = when (tokens.uiTheme) {
        UiTheme.MATERIAL_YOU, UiTheme.MIDNIGHT_FROSTED -> 18.dp
        UiTheme.CARBON_FORGE -> 9.dp
        UiTheme.UBUNTU_YARU -> 11.dp
        else -> 14.dp
    }

    Box(
        modifier = modifier
            .size(54.dp)
            .ironGlassSurface(
                tokens = tokens,
                subtle = true,
                borderOverride = accent.copy(alpha = 0.45f),
                radiusOverride = cornerRadius
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(30.dp)) {
            val w = size.width
            val h = size.height
            when (timePhase) {
                DayTimePhase.MORNING -> {
                    val cx = w * 0.5f
                    val cy = h * 0.62f
                    drawCircle(
                        color = accent,
                        radius = w * 0.22f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2.4.dp.toPx())
                    )
                    drawLine(
                        color = accent,
                        start = Offset(w * 0.14f, cy + h * 0.18f),
                        end = Offset(w * 0.86f, cy + h * 0.18f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    listOf(
                        Offset(cx, h * 0.14f) to Offset(cx, h * 0.28f),
                        Offset(w * 0.22f, h * 0.28f) to Offset(w * 0.31f, h * 0.37f),
                        Offset(w * 0.78f, h * 0.28f) to Offset(w * 0.69f, h * 0.37f)
                    ).forEach { (s, e) ->
                        drawLine(accent, s, e, strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    }
                }
                DayTimePhase.AFTERNOON -> {
                    val cx = w * 0.5f
                    val cy = h * 0.5f
                    drawCircle(
                        color = accent,
                        radius = w * 0.21f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2.4.dp.toPx())
                    )
                    for (i in 0 until 8) {
                        val angle = Math.toRadians((i * 45).toDouble())
                        val r1 = w * 0.31f
                        val r2 = w * 0.43f
                        drawLine(
                            color = accent,
                            start = Offset(
                                (cx + r1 * kotlin.math.cos(angle)).toFloat(),
                                (cy + r1 * kotlin.math.sin(angle)).toFloat()
                            ),
                            end = Offset(
                                (cx + r2 * kotlin.math.cos(angle)).toFloat(),
                                (cy + r2 * kotlin.math.sin(angle)).toFloat()
                            ),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                DayTimePhase.NIGHT -> {
                    val p = Path().apply {
                        moveTo(w * 0.62f, h * 0.18f)
                        cubicTo(w * 0.35f, h * 0.20f, w * 0.24f, h * 0.52f, w * 0.40f, h * 0.74f)
                        cubicTo(w * 0.52f, h * 0.88f, w * 0.74f, h * 0.82f, w * 0.82f, h * 0.68f)
                        cubicTo(w * 0.60f, h * 0.74f, w * 0.44f, h * 0.56f, w * 0.48f, h * 0.36f)
                        cubicTo(w * 0.50f, h * 0.27f, w * 0.56f, h * 0.22f, w * 0.62f, h * 0.18f)
                        close()
                    }
                    drawPath(p, color = accent)
                    drawCircle(accent, radius = 2.dp.toPx(), center = Offset(w * 0.74f, h * 0.28f))
                }
            }
        }
    }
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
    val periodPillBg = remember(tokens.phaseTokens.accent) { tokens.phaseTokens.accent.copy(alpha = 0.16f) }
    val periodPillBorder = remember(accentBright) { BorderStroke(1.dp, accentBright.copy(alpha = 0.35f)) }
    val colonMainColor = remember { Color(0xFFF8FAFC).copy(alpha = 0.65f) }
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
        val mainDigitStyle = remember(clockFontSize) {
            TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                fontSize = clockFontSize,
                lineHeight = clockLineHeight,
                letterSpacing = (-0.8).sp,
                fontFeatureSettings = "tnum",
                color = Color(0xFFF8FAFC)
            )
        }
        val secondsDigitStyle = remember(clockFontSize, accentBright) {
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
                            fontFamily = FontFamily.Monospace,
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
                    fontFamily = FontFamily.Monospace,
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
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "-",
                    tint = tokens.palette.onSurface,
                    modifier = Modifier.size(16.dp)
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
                            color = tokens.accentColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(tokens.accentColor),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = valueText,
                        fontFamily = FontFamily.Monospace,
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
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "+",
                    tint = tokens.accentColor,
                    modifier = Modifier.size(16.dp)
                )
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
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = tokens.palette.surfaceContainerHigh,
        titleContentColor = tokens.palette.onSurface,
        textContentColor = tokens.palette.onSurfaceVariant,
        icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = Color(0xFFF87171)
            )
        },
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(tokens.buttonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White
                ),
                modifier = Modifier.tactilePress(disabledByPerf = tokens.performance.noAnimations)
            ) {
                Text(confirmLabel, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(tokens.buttonRadius),
                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                modifier = Modifier.tactilePress(disabledByPerf = tokens.performance.noAnimations)
            ) {
                Text(cancelLabel, color = tokens.palette.onSurface)
            }
        }
    )
}
