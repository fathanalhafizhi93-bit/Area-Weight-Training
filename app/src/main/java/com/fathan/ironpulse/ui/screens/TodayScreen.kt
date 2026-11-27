package com.fathan.ironpulse.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathan.ironpulse.R
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.DaySchedule
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.components.GpuSheenOverlay
import com.fathan.ironpulse.ui.components.IronPulseIcon
import com.fathan.ironpulse.ui.components.LiveFluidClockSlot
import com.fathan.ironpulse.ui.components.LocalThemeMotionSpec
import com.fathan.ironpulse.ui.components.OrbBoundsHolder
import com.fathan.ironpulse.ui.components.PhaseLogoEmblem
import com.fathan.ironpulse.ui.components.ThemedIcon
import com.fathan.ironpulse.ui.components.TodayMascot
import com.fathan.ironpulse.ui.components.TutorialSpotlightTarget
import com.fathan.ironpulse.ui.components.tutorialSpotlightTarget
import com.fathan.ironpulse.ui.components.rememberScaledResourceBitmap
import com.fathan.ironpulse.ui.components.staggeredCardEntrance
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseOutlinedButton
import com.fathan.ironpulse.ui.theme.IronPulseThemeTokens
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.RestTimerDisplayState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodayScreen(
    state: IronPulseUiState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    clockSecondsFlow: StateFlow<Long>,
    restTimerFlow: StateFlow<RestTimerDisplayState>,
    mascotEyeFlow: StateFlow<MascotEyeState>,
    homeOrbAlphaProvider: () -> Float = { 1f },
    homeOrbAnimActive: Boolean = true,
    homeOrbAnimActiveProvider: (() -> Boolean)? = null,
    homeOrbBoundsHolder: OrbBoundsHolder? = null,
    homeLandingTick: Int = 0,
    homeLandingTickState: State<Int>? = null,
    onTapMascot: () -> Unit,
    onPetMascot: () -> Unit = {},
    onReplayMascotVoice: () -> Unit,
    onStartRestPreset: (Int, String?) -> Unit,
    onToggleRestPauseOrStart: () -> Unit,
    onAdjustRest: (Int) -> Unit,
    onResetRest: () -> Unit,
    onOpenWorkout: (String?) -> Unit,
    onOpenSchedule: () -> Unit,
    onOpenExercisesForDay: (DayKey) -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val tabActive = LocalTabActive.current
    val secUnit = lang.t("d", "s")
    val activeSchedule = state.schedules[state.todayDayKey]
    val dayLabel = state.todayDayKey.label(lang)
    val todayEntries = state.effectiveTodayEntries
    val specById = remember(activeSchedule?.exercises) {
        activeSchedule?.exercises?.associateBy { it.id } ?: emptyMap()
    }
    val heroBitmap = rememberScaledResourceBitmap(R.drawable.gym_arena_backdrop, 1024)

    val summary = remember(todayEntries, specById) {
        var total = 0
        var done = 0
        var volume = 0.0
        var timedSecs = 0
        for (entry in todayEntries) {
            val spec = specById[entry.exerciseId]
            val mode = spec?.trackingMode ?: entry.trackingMode
            val usesWeight = GymModelHelpers.resolveUsesWeight(
                usesWeight = entry.usesWeight,
                trackingMode = mode,
                targetWeightKg = spec?.targetWeightKg ?: entry.sets.firstOrNull()?.weightKg ?: 0.0,
                equipment = spec?.equipment
            )
            for (s in entry.sets) {
                total += 1
                if (s.completed) {
                    done += 1
                    if (mode == ExerciseTrackingMode.TIMER) {
                        timedSecs += s.durationSeconds.coerceAtLeast(0)
                    } else if (usesWeight) {
                        volume += s.weightKg.coerceAtLeast(0.0) * s.reps.coerceAtLeast(0)
                    }
                }
            }
        }
        val pct = if (total > 0) (done * 100) / total else 0
        TodayProgressStats(total, done, volume.toInt(), timedSecs, pct)
    }

    val startBtnLabel = when {
        summary.totalSets > 0 && summary.completedSets == summary.totalSets ->
            lang.t("Lihat / Ubah Set Latihan", "Review Workout Sets")
        summary.completedSets > 0 ->
            lang.t("Lanjutkan Latihan", "Continue Workout")
        else ->
            lang.t("Mulai Latihan", "Start Workout")
    }

    val numberFormat = remember(lang) {
        NumberFormat.getIntegerInstance(if (lang == AppLanguage.EN) Locale.US else Locale.forLanguageTag("id-ID"))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = contentPadding.calculateTopPadding() + 14.dp,
                bottom = contentPadding.calculateBottomPadding() + 92.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
        ) {
        // 1. COMPACT HERO BANNER CARD
        item(key = "hero_banner", contentType = "hero") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
                    .ironGlassSurface(tokens, borderOverride = tokens.phaseTokens.heroBorder)
            ) {
                if (heroBitmap != null) {
                    Image(
                        bitmap = heroBitmap,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alpha = if (tokens.isLightTheme) 0.05f else 0.12f,
                        modifier = Modifier.matchParentSize()
                    )
                }

                HeroAnimatedAtmosphereLayer(
                    modifier = Modifier.matchParentSize(),
                    noAnim = noAnim
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Zone 1: Phase Emblem + Clock + Phase / Alarm Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PhaseLogoEmblem(timePhase = state.timePhase)
                        Column(modifier = Modifier.weight(1f)) {
                            LiveFluidClockSlot(
                                clockSecondsFlow = clockSecondsFlow,
                                clockFormat = tokens.clockFormat,
                                lang = lang
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val alarmText = if (activeSchedule?.isGymDay == true) {
                                val fmt = GymModelHelpers.formatAlarmTime(activeSchedule.alarmTime, tokens.clockFormat, lang)
                                if (activeSchedule.alarmEnabled) {
                                    "${lang.t("Alarm", "Alarm")} $fmt"
                                } else {
                                    "$fmt (${lang.t("Mati", "Off")})"
                                }
                            } else {
                                lang.t("Hari Istirahat", "Rest Day")
                            }
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = state.timePhase.label(lang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.phaseTokens.accentBright
                                )
                                Text(text = "·", fontSize = 12.5.sp, color = tokens.mutedTextColor)
                                Text(
                                    text = alarmText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = tokens.palette.onSurfaceVariant
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = tokens.subtleBorderColor)

                    // Zone 2: Date Lead-In + Dominant Focus Title + Compact Next Alarm Line
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        TodayLongDateLabel(
                            clockSecondsFlow = clockSecondsFlow,
                            lang = lang,
                            accentColor = tokens.accentColor
                        )
                        Text(
                            text = if (activeSchedule?.isGymDay == true) {
                                activeSchedule.focusTitle
                            } else {
                                "$dayLabel — ${lang.t("Hari Istirahat", "Rest Day")}"
                            },
                            fontSize = 25.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.4).sp,
                            color = tokens.palette.onSurface
                        )
                    }

                    NextAlarmStatusBar(
                        schedules = state.schedules,
                        clockSecondsFlow = clockSecondsFlow,
                        clockFormat = tokens.clockFormat,
                        lang = lang
                    )

                    // Daily Progress Metrics + Animated Bar
                    if (activeSchedule?.isGymDay == true && activeSchedule.exercises.isNotEmpty()) {
                        val barMotionSpec = LocalThemeMotionSpec.current
                        val animatedBarState = animateFloatAsState(
                            targetValue = (summary.progressPct / 100f).coerceIn(0f, 1f),
                            animationSpec = if (noAnim) tween(0) else tween(barMotionSpec.progressFillMs, easing = FastOutSlowInEasing),
                            label = "todayProgressBar"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${summary.completedSets}/${summary.totalSets} ${lang.t("Set", "Sets")}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = tokens.palette.onSurfaceVariant
                                    )
                                    Text("·", fontSize = 12.sp, color = tokens.mutedTextColor)
                                    Text(
                                        text = "${numberFormat.format(summary.totalVolumeKg)} kg",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = tokens.palette.onSurfaceVariant
                                    )
                                    if (summary.totalTimedSeconds > 0) {
                                        Text("·", fontSize = 12.sp, color = tokens.mutedTextColor)
                                        Text(
                                            text = GymModelHelpers.formatCompactDuration(summary.totalTimedSeconds, lang),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            color = if (tokens.isLightTheme) Color(0xFF0284C7) else Color(0xFF38BDF8)
                                        )
                                    }
                                }
                                Text(
                                    text = "${summary.progressPct}%",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.accentColor
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        if (tokens.isLightTheme) Color(0xFF0077B6).copy(alpha = 0.14f)
                                        else Color.White.copy(alpha = 0.1f)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(7.dp)
                                        .graphicsLayer {
                                            scaleX = animatedBarState.value
                                            transformOrigin = TransformOrigin(0f, 0.5f)
                                        }
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(tokens.accentColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. INTERACTIVE PULSE COMPANION CARD (Frame-0 layout so mascot return flight never waits)
        item(key = "today_mascot_card", contentType = "mascot") {
            TodayMascot(
                mascot = state.mascot,
                eyeStateFlow = mascotEyeFlow,
                voiceEnabled = state.appPrefs.mascotVoiceEnabled,
                orbVisibleAlpha = homeOrbAlphaProvider,
                orbAnimActive = homeOrbAnimActive,
                orbAnimActiveProvider = homeOrbAnimActiveProvider,
                hideForTutorial = state.tutorialOpen,
                homeOrbBoundsHolder = homeOrbBoundsHolder,
                landingPulseTick = homeLandingTick,
                landingPulseTickState = homeLandingTickState,
                onTapMascot = onTapMascot,
                onPetMascot = onPetMascot,
                onReplayVoice = onReplayMascotVoice,
                modifier = Modifier.testTag("today_mascot_card")
            )
        }

        // 3. ELEVATED PRIMARY START WORKOUT CTA
        if (activeSchedule?.isGymDay == true && todayEntries.isNotEmpty()) {
            item(key = "start_workout_cta", contentType = "cta") {
                val ctaRadius = (tokens.cardRadius * 0.78f).coerceAtLeast(10.dp)
                val ctaShape = if (tokens.isXMaterial) {
                    RoundedCornerShape(24.dp)
                } else {
                    RoundedCornerShape(ctaRadius)
                }
                val isAero = tokens.uiTheme == UiTheme.FRUTIGER_AERO
                IronPulseTonalSurface(
                    onClick = { onOpenWorkout(null) },
                    shape = ctaShape,
                    baseColor = tokens.accentColor,
                    isAccent = true,
                    contentColor = tokens.onAccentColor,
                    border = if (isAero) {
                        BorderStroke(
                            1.dp,
                            if (tokens.isAeroNight) Color(0x99BAE6FD) else Color.White.copy(alpha = 0.80f)
                        )
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(2, active = tabActive, disabledByPerf = noAnim)
                        .testTag("start_workout_cta")
                ) {
                    val ctaMotionSpec = LocalThemeMotionSpec.current
                    GpuSheenOverlay(
                        active = tabActive && !noAnim && !tokens.performance.noAmbientGlow,
                        tint = if (tokens.uiTheme == UiTheme.AMOLED_STEALTH) tokens.accentColor else Color.White,
                        peakAlpha = ctaMotionSpec.sheenPeakAlpha,
                        durationMs = ctaMotionSpec.sheenDurationMs,
                        modifier = Modifier.matchParentSize()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            ThemedIcon(
                                icon = IronPulseIcon.EXERCISES,
                                contentDescription = null,
                                tint = tokens.onAccentColor,
                                size = 19.dp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = startBtnLabel,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = tokens.onAccentColor
                            )
                            Text(
                                text = lang.t("Catat set, beban & repetisi", "Log sets, load & reps"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.onAccentColor.copy(alpha = 0.82f)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${summary.completedSets}/${summary.totalSets}",
                                fontFamily = tokens.fontPack.monoFont,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = tokens.onAccentColor
                            )
                            ThemedIcon(
                                icon = IronPulseIcon.FORWARD,
                                contentDescription = null,
                                tint = tokens.onAccentColor,
                                size = 18.dp
                            )
                        }
                    }
                }
            }
        }

        // 4. COMPACT STANDALONE REST TIMER CARD
        item(key = "rest_timer_card", contentType = "rest_timer") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredCardEntrance(3, active = tabActive, disabledByPerf = noAnim)
                    .tutorialSpotlightTarget(TutorialSpotlightTarget.TODAY_REST_TIMER)
            ) {
                RestTimerHeroCard(
                    restTimerFlow = restTimerFlow,
                    secUnit = secUnit,
                    onStartRestPreset = onStartRestPreset,
                    onToggleRestPauseOrStart = onToggleRestPauseOrStart,
                    onAdjustRest = onAdjustRest,
                    onResetRest = onResetRest
                )
            }
        }

        // 4. REST DAY / EMPTY / FLATTENED RECYCLABLE EXERCISE OVERVIEW
        if (activeSchedule != null && !activeSchedule.isGymDay) {
            item(key = "rest_day_card", contentType = "rest_day") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(4, active = tabActive, disabledByPerf = noAnim)
                        .ironGlassSurface(tokens)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemedIcon(
                        icon = IronPulseIcon.TIMER,
                        contentDescription = null,
                        tint = tokens.mutedTextColor,
                        size = 36.dp,
                        filledBadge = true
                    )
                    Text(
                        text = lang.t(
                            "$dayLabel Dijadwalkan Sebagai Hari Istirahat",
                            "$dayLabel is Scheduled as a Rest Day"
                        ),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = lang.t(
                            "Pemulihan otot sama pentingnya dengan latihan beban. Jika ingin berlatih hari ini, aktifkan hari $dayLabel di tab Jadwal.",
                            "Muscle recovery matters just as much as lifting. If you want to train today, enable $dayLabel in the Schedule tab."
                        ),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = tokens.mutedTextColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    IronPulseButton(
                        onClick = onOpenSchedule,
                        shape = tokens.buttonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentColor,
                            contentColor = tokens.onAccentColor
                        )
                    ) {
                        Text(lang.t("Buka Tab Jadwal", "Open Schedule Tab"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (activeSchedule != null && activeSchedule.exercises.isEmpty()) {
            item(key = "empty_exercises_card", contentType = "empty") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(4, active = tabActive, disabledByPerf = noAnim)
                        .ironGlassSurface(tokens)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemedIcon(
                        icon = IronPulseIcon.EXERCISES,
                        contentDescription = null,
                        tint = tokens.mutedTextColor,
                        size = 36.dp,
                        filledBadge = true
                    )
                    Text(
                        text = lang.t("Belum Ada Gerakan untuk $dayLabel", "No Exercises Yet for $dayLabel"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = lang.t(
                            "Tambahkan gerakan untuk hari ini dari tab Gerakan.",
                            "Add movements for today from the Exercises tab."
                        ),
                        fontSize = 13.sp,
                        color = tokens.mutedTextColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    IronPulseButton(
                        onClick = { onOpenExercisesForDay(state.todayDayKey) },
                        shape = tokens.buttonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentColor,
                            contentColor = tokens.onAccentColor
                        )
                    ) {
                        Text(lang.t("+ Tambah Gerakan Sekarang", "+ Add Exercise Now"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (todayEntries.isNotEmpty()) {
            item(key = "overview_header", contentType = "header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(4, active = tabActive, disabledByPerf = noAnim)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lang.t("Ringkasan Gerakan Hari Ini", "Today's Exercise Overview"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.palette.onSurface
                        )
                        Text(
                            text = "${todayEntries.size} ${lang.t("gerakan terjadwal", "scheduled exercises")} · ${lang.t("Ketuk untuk catat set", "Tap to log sets")}",
                            fontSize = 12.sp,
                            color = tokens.mutedTextColor
                        )
                    }
                }
            }

            itemsIndexed(
                items = todayEntries,
                key = { _, e -> e.exerciseId },
                contentType = { _, _ -> "exercise_row" }
            ) { idx, entry ->
                val spec = specById[entry.exerciseId]
                val mode = spec?.trackingMode ?: entry.trackingMode
                val targetDur = GymModelHelpers.resolveTargetDurationSeconds(
                    spec?.targetDurationSeconds ?: entry.targetDurationSeconds,
                    spec?.targetReps
                )
                val doneCount = entry.sets.count { it.completed }
                val allDone = entry.sets.isNotEmpty() && doneCount == entry.sets.size
                val targetBadge = if (spec != null) {
                    if (mode == ExerciseTrackingMode.TIMER) {
                        "${spec.targetSets}×${GymModelHelpers.formatCompactDuration(targetDur, lang)}"
                    } else {
                        "${spec.targetSets}×${spec.targetReps}"
                    }
                } else {
                    "${entry.sets.size} set"
                }

                val itemOrderLabel = remember(idx) { "${(idx + 1).toString().padStart(2, '0')}." }
                val itemSubtitle = remember(entry.muscleGroup, spec?.equipment, targetBadge, lang) {
                    listOfNotNull(
                        entry.muscleGroup.label(lang),
                        spec?.equipment?.takeIf { it.isNotBlank() },
                        targetBadge
                    ).joinToString(" · ")
                }
                val itemBorderOverride = remember(allDone) { if (allDone) Color(0x6616A34A) else null }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (idx < 3) {
                                Modifier.staggeredCardEntrance(idx + 5, active = tabActive, disabledByPerf = noAnim)
                            } else {
                                Modifier
                            }
                        )
                        .tactilePress(pressedScale = 0.98f, disabledByPerf = noAnim)
                        .ironGlassSurface(
                            tokens = tokens,
                            subtle = !allDone,
                            borderOverride = itemBorderOverride,
                            radiusOverride = 16.dp
                        )
                        .clickable { onOpenWorkout(entry.exerciseId) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = itemOrderLabel,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.accentColor
                            )
                            Text(
                                text = entry.name,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.palette.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = itemSubtitle,
                            fontSize = 12.sp,
                            color = tokens.mutedTextColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val doneGreen = if (tokens.isLightTheme) Color(0xFF15803D) else Color(0xFF4ADE80)
                        Text(
                            text = "$doneCount/${entry.sets.size}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (allDone) doneGreen else tokens.palette.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .ironTonalControl(
                                    tokens = tokens,
                                    shape = CircleShape,
                                    baseColor = if (allDone) Color(0x3316A34A) else tokens.subtleChipBg,
                                    isAccent = allDone
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ThemedIcon(
                                icon = if (allDone) IronPulseIcon.CHECK else IronPulseIcon.FORWARD,
                                contentDescription = null,
                                tint = if (allDone) doneGreen else tokens.mutedTextColor,
                                size = 14.dp
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
private fun TodayLongDateLabel(
    clockSecondsFlow: StateFlow<Long>,
    lang: AppLanguage,
    accentColor: Color
) {
    val tabActive = LocalTabActive.current
    val minuteBucketFlow = remember(clockSecondsFlow) {
        clockSecondsFlow.map { it / 60L }.distinctUntilChanged()
    }
    val minuteBucket = if (tabActive) {
        minuteBucketFlow.collectAsStateWithLifecycle(initialValue = clockSecondsFlow.value / 60L).value
    } else {
        remember(clockSecondsFlow) { clockSecondsFlow.value / 60L }
    }
    val formatted = remember(minuteBucket, lang) {
        GymModelHelpers.formatLongTodayDate(Date(minuteBucket * 60_000L), lang)
    }
    Text(
        text = formatted,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = accentColor
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NextAlarmStatusBar(
    schedules: Map<DayKey, DaySchedule>,
    clockSecondsFlow: StateFlow<Long>,
    clockFormat: ClockFormat,
    lang: AppLanguage
) {
    val tokens = LocalIronPulseTokens.current
    val tabActive = LocalTabActive.current
    val halfMinuteFlow = remember(clockSecondsFlow) {
        clockSecondsFlow.map { it / 30L }.distinctUntilChanged()
    }
    val halfMinuteBucket = if (tabActive) {
        halfMinuteFlow.collectAsStateWithLifecycle(initialValue = clockSecondsFlow.value / 30L).value
    } else {
        remember(clockSecondsFlow) { clockSecondsFlow.value / 30L }
    }
    val nextAlarm = remember(schedules, halfMinuteBucket, lang) {
        GymModelHelpers.computeNextAlarm(schedules, lang)
    }
    val nextCountdown = remember(nextAlarm, halfMinuteBucket, lang) {
        nextAlarm?.let { formatNextCountdown(it.triggerAtMillis, halfMinuteBucket * 30_000L, lang) } ?: ""
    }

    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemedIcon(
            icon = IronPulseIcon.NOTIFICATIONS,
            contentDescription = null,
            tint = tokens.accentColor,
            size = 15.dp,
            modifier = Modifier.padding(top = 2.dp)
        )
        if (nextAlarm != null) {
            val alarmFmt = GymModelHelpers.formatAlarmTime(nextAlarm.alarmTime, clockFormat, lang)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = lang.t("Alarm Berikutnya:", "Next Alarm:"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.mutedTextColor
                )
                Text(
                    text = nextAlarm.dayLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.palette.onSurface
                )
                Text("·", fontSize = 12.sp, color = tokens.mutedTextColor)
                Text(
                    text = alarmFmt,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.accentColor
                )
                Text("·", fontSize = 12.sp, color = tokens.mutedTextColor)
                Text(
                    text = nextCountdown,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = lang.t(
                    "Belum ada alarm aktif. Aktifkan di tab Jadwal.",
                    "No active alarms yet. Enable one in the Schedule tab."
                ),
                fontSize = 12.sp,
                color = tokens.mutedTextColor
            )
        }
    }
}

@Composable
private fun RestTimerHeroCard(
    restTimerFlow: StateFlow<RestTimerDisplayState>,
    secUnit: String,
    onStartRestPreset: (Int, String?) -> Unit,
    onToggleRestPauseOrStart: () -> Unit,
    onAdjustRest: (Int) -> Unit,
    onResetRest: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val rest by restTimerFlow.collectAsStateWithLifecycle()

    val restRunning = rest.isActive
    val restRemaining = rest.remainingSeconds
    val restTotal = rest.totalSeconds.coerceAtLeast(1)
    val restLabel = rest.exerciseName
    val targetProgress = (restRemaining.toFloat() / restTotal.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = if (noAnim) tween(0) else tween(260),
        label = "restArc"
    )

    val restBorder = when {
        restRunning -> tokens.accentColor.copy(alpha = 0.52f)
        restRemaining == 0 -> Color(0x8016A34A)
        else -> null
    }

    val buttonShape = tokens.buttonShape
    val presetChipShape = remember { RoundedCornerShape(10.dp) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens, borderOverride = restBorder)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .graphicsLayer { },
                contentAlignment = Alignment.Center
            ) {
                val ringColor = if (restRemaining == 0) Color(0xFF16A34A) else tokens.accentColor
                val ringTrack = if (tokens.isLightTheme) Color(0xFF0077B6).copy(alpha = 0.14f) else Color.White.copy(alpha = 0.08f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 5.5.dp.toPx()
                    drawArc(
                        color = ringTrack,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(stroke)
                    )
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = GymModelHelpers.formatRestDuration(restRemaining),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = tokens.palette.onSurface
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lang.t("Timer Istirahat", "Rest Timer"),
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = restLabel.ifBlank {
                        lang.t("Waktu pemulihan antar set", "Between-set recovery")
                    },
                    fontSize = 12.sp,
                    color = tokens.mutedTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        restRemaining == 0 -> lang.t(
                            "Istirahat selesai — lanjutkan set berikutnya",
                            "Rest complete — start next set"
                        )
                        restRunning -> lang.t("Sedang berjalan", "Running")
                        else -> lang.t("Siap dimulai", "Ready to start")
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        restRemaining == 0 -> if (tokens.isLightTheme) Color(0xFF15803D) else Color(0xFF4ADE80)
                        restRunning -> tokens.accentColor
                        else -> tokens.palette.onSurfaceVariant
                    }
                )
            }
        }

        // 4 Preset Chips (45s / 60s / 90s / 120s)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(45, 60, 90, 120).forEach { sec ->
                val activePreset = restTotal == sec && restRunning
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .tactilePress(pressedScale = 0.94f, disabledByPerf = noAnim)
                        .ironTonalControl(
                            tokens = tokens,
                            shape = presetChipShape,
                            baseColor = if (activePreset) tokens.accentColor.copy(alpha = 0.20f) else tokens.wellBg,
                            isAccent = activePreset
                        )
                        .border(
                            1.dp,
                            if (activePreset) tokens.accentColor else tokens.subtleBorderColor,
                            presetChipShape
                        )
                        .clickable { onStartRestPreset(sec, restLabel.ifBlank { null }) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$sec$secUnit",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (activePreset) tokens.accentTextColor else tokens.palette.onSurfaceVariant
                    )
                }
            }
        }

        // Controls Row: -15s | Start/Pause | +15s | Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IronPulseOutlinedButton(
                onClick = { onAdjustRest(-15) },
                shape = buttonShape,
                border = BorderStroke(1.dp, tokens.subtleBorderColor),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("-15$secUnit", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = tokens.palette.onSurface)
            }

            IronPulseButton(
                onClick = onToggleRestPauseOrStart,
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentColor,
                    contentColor = tokens.onAccentColor
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            ) {
                ThemedIcon(
                    icon = if (restRunning) IronPulseIcon.PAUSE else IronPulseIcon.PLAY,
                    contentDescription = null,
                    tint = tokens.onAccentColor,
                    size = 18.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        restRunning -> lang.t("Jeda", "Pause")
                        restRemaining > 0 -> lang.t("Mulai", "Start")
                        else -> lang.t("Ulangi", "Restart")
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            IronPulseOutlinedButton(
                onClick = { onAdjustRest(15) },
                shape = buttonShape,
                border = BorderStroke(1.dp, tokens.subtleBorderColor),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text("+15$secUnit", fontFamily = tokens.fontPack.monoFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = tokens.palette.onSurface)
            }

            IronPulseTonalSurface(
                onClick = onResetRest,
                shape = buttonShape,
                baseColor = tokens.controlBg,
                border = BorderStroke(1.dp, tokens.subtleBorderColor),
                visualModifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ThemedIcon(
                        icon = IronPulseIcon.REFRESH,
                        contentDescription = lang.t("Reset timer", "Reset timer"),
                        tint = tokens.mutedTextColor,
                        size = 18.dp
                    )
                }
            }
        }
    }
}

private data class TodayProgressStats(
    val totalSets: Int,
    val completedSets: Int,
    val totalVolumeKg: Int,
    val totalTimedSeconds: Int,
    val progressPct: Int
)

private fun formatNextCountdown(targetMillis: Long, nowMillis: Long, lang: AppLanguage): String {
    val diffMins = ((targetMillis - nowMillis).coerceAtLeast(0L) / 60_000L).toInt()
    val days = diffMins / (60 * 24)
    val hours = (diffMins % (60 * 24)) / 60
    val mins = diffMins % 60
    return if (lang == AppLanguage.EN) {
        when {
            days > 0 -> "in ${days}d ${hours}h"
            hours > 0 -> "in ${hours}h ${mins}m"
            else -> "in ${mins}m"
        }
    } else {
        when {
            days > 0 -> "dalam ${days}h ${hours}j"
            hours > 0 -> "dalam ${hours}j ${mins}m"
            else -> "dalam ${mins}m"
        }
    }
}

internal data class ClockCornerOrbPalette(
    val orbCoreCenter: Color,
    val orbCoreMid: Color,
    val ringBright: Color,
    val ringAccent: Color,
    val ringRest: Color
)

internal fun resolveClockCornerOrbPalette(tokens: IronPulseThemeTokens): ClockCornerOrbPalette {
    val phase = tokens.timePhase
    return if (tokens.isLightTheme) {
        when (phase) {
            DayTimePhase.MORNING -> ClockCornerOrbPalette(
                orbCoreCenter = if (tokens.isXMaterial) Color(0xFFF43F5E).copy(alpha = 0.34f) else Color(0xFFF59E0B).copy(alpha = 0.34f),
                orbCoreMid = Color(0xFFF59E0B).copy(alpha = 0.17f),
                ringBright = if (tokens.isXMaterial) Color(0xFFE11D48).copy(alpha = 0.38f) else Color(0xFFD97706).copy(alpha = 0.38f),
                ringAccent = Color(0xFFF59E0B).copy(alpha = 0.22f),
                ringRest = Color(0xFFD97706).copy(alpha = 0.10f)
            )
            DayTimePhase.AFTERNOON -> ClockCornerOrbPalette(
                orbCoreCenter = Color(0xFF0284C7).copy(alpha = 0.32f),
                orbCoreMid = if (tokens.isXMaterial) Color(0xFF10B981).copy(alpha = 0.16f) else Color(0xFF38BDF8).copy(alpha = 0.17f),
                ringBright = Color(0xFF0284C7).copy(alpha = 0.37f),
                ringAccent = if (tokens.isXMaterial) Color(0xFF059669).copy(alpha = 0.22f) else Color(0xFF0EA5E9).copy(alpha = 0.22f),
                ringRest = Color(0xFF0284C7).copy(alpha = 0.10f)
            )
            DayTimePhase.NIGHT -> ClockCornerOrbPalette(
                orbCoreCenter = Color(0xFF4F46E5).copy(alpha = 0.32f),
                orbCoreMid = Color(0xFF38BDF8).copy(alpha = 0.16f),
                ringBright = Color(0xFF4338CA).copy(alpha = 0.36f),
                ringAccent = Color(0xFF0284C7).copy(alpha = 0.22f),
                ringRest = Color(0xFF4338CA).copy(alpha = 0.10f)
            )
        }
    } else {
        when (phase) {
            DayTimePhase.MORNING -> if (tokens.isXMaterial) {
                ClockCornerOrbPalette(
                    orbCoreCenter = Color(0xFFDFBE84).copy(alpha = 0.32f),
                    orbCoreMid = Color(0xFFF3E0BE).copy(alpha = 0.16f),
                    ringBright = Color(0xFFF3E0BE).copy(alpha = 0.36f),
                    ringAccent = Color(0xFFDFBE84).copy(alpha = 0.22f),
                    ringRest = Color(0xFFDFBE84).copy(alpha = 0.10f)
                )
            } else {
                ClockCornerOrbPalette(
                    orbCoreCenter = Color(0xFFFBBF24).copy(alpha = 0.34f),
                    orbCoreMid = Color(0xFFF97316).copy(alpha = 0.17f),
                    ringBright = Color(0xFFFDE68A).copy(alpha = 0.38f),
                    ringAccent = Color(0xFFF59E0B).copy(alpha = 0.23f),
                    ringRest = Color(0xFFFBBF24).copy(alpha = 0.10f)
                )
            }
            DayTimePhase.AFTERNOON -> if (tokens.isXMaterial) {
                ClockCornerOrbPalette(
                    orbCoreCenter = Color(0xFF8CC5C2).copy(alpha = 0.32f),
                    orbCoreMid = Color(0xFFC6E6E4).copy(alpha = 0.16f),
                    ringBright = Color(0xFFC6E6E4).copy(alpha = 0.36f),
                    ringAccent = Color(0xFF8CC5C2).copy(alpha = 0.22f),
                    ringRest = Color(0xFF8CC5C2).copy(alpha = 0.10f)
                )
            } else {
                ClockCornerOrbPalette(
                    orbCoreCenter = Color(0xFF38BDF8).copy(alpha = 0.33f),
                    orbCoreMid = Color(0xFF0EA5E9).copy(alpha = 0.16f),
                    ringBright = Color(0xFFBAE6FD).copy(alpha = 0.38f),
                    ringAccent = Color(0xFF38BDF8).copy(alpha = 0.22f),
                    ringRest = Color(0xFF38BDF8).copy(alpha = 0.10f)
                )
            }
            DayTimePhase.NIGHT -> ClockCornerOrbPalette(
                orbCoreCenter = when {
                    tokens.isXMaterial || tokens.isXMaterialNight -> Color(0xFFB0A4E3).copy(alpha = 0.32f)
                    tokens.isAeroNight -> Color(0xFF38BDF8).copy(alpha = 0.33f)
                    else -> Color(0xFF818CF8).copy(alpha = 0.34f)
                },
                orbCoreMid = when {
                    tokens.isXMaterial || tokens.isXMaterialNight -> Color(0xFFDDD6FA).copy(alpha = 0.16f)
                    tokens.isAeroNight -> Color(0xFF2DD4BF).copy(alpha = 0.16f)
                    else -> Color(0xFF6366F1).copy(alpha = 0.17f)
                },
                ringBright = when {
                    tokens.isXMaterial || tokens.isXMaterialNight -> Color(0xFFDDD6FA).copy(alpha = 0.36f)
                    tokens.isAeroNight -> Color(0xFF7DD3FC).copy(alpha = 0.38f)
                    else -> Color(0xFFC7D2FE).copy(alpha = 0.38f)
                },
                ringAccent = when {
                    tokens.isXMaterial || tokens.isXMaterialNight -> Color(0xFFB0A4E3).copy(alpha = 0.22f)
                    else -> Color(0xFF818CF8).copy(alpha = 0.22f)
                },
                ringRest = if (tokens.isXMaterial || tokens.isXMaterialNight) {
                    Color(0xFFB0A4E3).copy(alpha = 0.10f)
                } else {
                    Color(0xFF818CF8).copy(alpha = 0.10f)
                }
            )
        }
    }
}

@Composable
internal fun HeroAnimatedAtmosphereLayer(
    modifier: Modifier = Modifier,
    noAnim: Boolean
) {
    val tokens = LocalIronPulseTokens.current
    val motionSpec = LocalThemeMotionSpec.current
    val tabActive = LocalTabActive.current
    val animate = !noAnim && tabActive
    val noAmbientGlow = tokens.performance.noAmbientGlow

    val breatheState: State<Float>
    val ringAngleState: State<Float>
    if (animate) {
        val inf = rememberInfiniteTransition(label = "heroAtmosphere")
        breatheState = inf.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = motionSpec.heroBreathMs, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathe"
        )
        ringAngleState = inf.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = motionSpec.heroOrbitMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "clockCornerRingRotate"
        )
    } else {
        breatheState = remember { mutableFloatStateOf(0.5f) }
        ringAngleState = remember { mutableFloatStateOf(42f) }
    }

    val scrimAlpha = tokens.heroScrimAlpha
    val heroStart = tokens.phaseTokens.heroStart.copy(alpha = tokens.phaseTokens.heroStart.alpha * scrimAlpha)
    val heroMid = tokens.phaseTokens.heroMid.copy(alpha = tokens.phaseTokens.heroMid.alpha * scrimAlpha)
    val heroEnd = tokens.phaseTokens.heroEnd.copy(alpha = tokens.phaseTokens.heroEnd.alpha * scrimAlpha)
    val glowColor = tokens.phaseTokens.glowPrimary
    val accentBright = tokens.phaseTokens.accentBright
    val orbPalette = remember(tokens.timePhase, tokens.isLightTheme, tokens.isXMaterial, tokens.isXMaterialNight, tokens.isAeroNight) {
        resolveClockCornerOrbPalette(tokens)
    }

    Box(modifier = modifier.testTag("hero_atmosphere_layer")) {
        // Static cached background + core orb + inner rim (never invalidated during animation or scroll)
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .drawWithCache {
                    val bgGradient = Brush.linearGradient(colors = listOf(heroStart, heroMid, heroEnd))
                    if (size.width <= 2f || size.height <= 2f) {
                        return@drawWithCache onDrawBehind { drawRect(bgGradient) }
                    }
                    val center = Offset(size.width, 0f)
                    val orbRadius = (size.minDimension * 0.68f)
                        .coerceIn(136.dp.toPx(), 200.dp.toPx())
                        .coerceAtLeast(1f)
                    val innerRimStrokePx = 0.85.dp.toPx()
                    val orbBrush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.0f to orbPalette.orbCoreCenter,
                            0.55f to orbPalette.orbCoreMid,
                            1.0f to Color.Transparent
                        ),
                        center = center,
                        radius = orbRadius
                    )
                    val rimColor = orbPalette.ringRest.copy(
                        alpha = (orbPalette.ringRest.alpha * 1.4f).coerceAtMost(0.18f)
                    )
                    onDrawBehind {
                        drawRect(bgGradient)
                        drawCircle(brush = orbBrush, radius = orbRadius, center = center)
                        drawCircle(
                            color = rimColor,
                            radius = orbRadius * 0.94f,
                            center = center,
                            style = Stroke(width = innerRimStrokePx)
                        )
                    }
                }
        )

        // GPU-scaled breathing ambient aura (only matrix scale updates, zero Canvas redraw)
        if (!noAmbientGlow) {
            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        val swing = motionSpec.heroAuraSwing
                        val pulseScale = (1f - swing * 0.45f) + swing * breatheState.value
                        scaleX = pulseScale
                        scaleY = pulseScale
                        transformOrigin = TransformOrigin(1f, 0f)
                    }
                    .drawWithCache {
                        if (size.width <= 2f || size.height <= 2f) {
                            return@drawWithCache onDrawBehind {}
                        }
                        val center = Offset(size.width, 0f)
                        val orbRadius = (size.minDimension * 0.68f)
                            .coerceIn(136.dp.toPx(), 200.dp.toPx())
                            .coerceAtLeast(1f)
                        val auraRadius = (orbRadius * 1.30f).coerceAtLeast(1f)
                        val auraBrush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.14f),
                                accentBright.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = auraRadius
                        )
                        onDrawBehind {
                            drawCircle(brush = auraBrush, radius = auraRadius, center = center)
                        }
                    }
            )
        }

        // GPU-rotated luminous ring + symmetric orbital light beads (zero Canvas redraw)
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    rotationZ = ringAngleState.value
                    transformOrigin = TransformOrigin(1f, 0f)
                }
                .drawWithCache {
                    if (size.width <= 2f || size.height <= 2f) {
                        return@drawWithCache onDrawBehind {}
                    }
                    val cx = size.width
                    val cy = 0f
                    val center = Offset(cx, cy)
                    val orbRadius = (size.minDimension * 0.68f)
                        .coerceIn(136.dp.toPx(), 200.dp.toPx())
                        .coerceAtLeast(1f)
                    val ringRadius = (orbRadius + 12.dp.toPx()).coerceAtLeast(1f)
                    val ringStrokePx = 1.75.dp.toPx()
                    val nodeRadiusPx = 2.4.dp.toPx()
                    val ringSweepBrush = Brush.sweepGradient(
                        0.00f to orbPalette.ringBright,
                        0.14f to orbPalette.ringAccent,
                        0.28f to orbPalette.ringRest,
                        0.40f to orbPalette.ringAccent,
                        0.50f to orbPalette.ringBright,
                        0.64f to orbPalette.ringAccent,
                        0.78f to orbPalette.ringRest,
                        0.90f to orbPalette.ringAccent,
                        1.00f to orbPalette.ringBright,
                        center = center
                    )
                    val bead1 = Offset(cx + ringRadius, cy)
                    val bead2 = Offset(cx - ringRadius, cy)
                    onDrawBehind {
                        drawCircle(
                            brush = ringSweepBrush,
                            radius = ringRadius,
                            center = center,
                            style = Stroke(width = ringStrokePx, cap = StrokeCap.Round)
                        )
                        drawCircle(color = orbPalette.ringBright, radius = nodeRadiusPx, center = bead1)
                        drawCircle(color = orbPalette.ringBright, radius = nodeRadiusPx, center = bead2)
                    }
                }
        )
    }
}

