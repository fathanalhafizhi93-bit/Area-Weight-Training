package com.fathan.ironpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathan.ironpulse.model.ExerciseItem
import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.TimerDirection
import com.fathan.ironpulse.model.WorkoutLogEntry
import com.fathan.ironpulse.model.WorkoutSetLog
import com.fathan.ironpulse.ui.components.IronPulseIcon
import com.fathan.ironpulse.ui.components.IronPulseMotion
import com.fathan.ironpulse.ui.components.ThemedIcon
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseOutlinedButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.viewmodel.ExerciseTimerUiState
import com.fathan.ironpulse.ui.viewmodel.RestTimerDisplayState
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GymFocusHudStage(
    index: Int,
    totalExercises: Int,
    entry: WorkoutLogEntry,
    spec: ExerciseItem?,
    notesExpanded: Boolean,
    onToggleNotes: () -> Unit,
    restBarDismissed: Boolean,
    restTimerFlow: StateFlow<RestTimerDisplayState>,
    exerciseTimersFlow: StateFlow<Map<String, ExerciseTimerUiState>>,
    cooldownSecondsFlow: StateFlow<Int>,
    onPrevExercise: () -> Unit,
    onNextExercise: () -> Unit,
    onStartRest: (Int) -> Unit,
    onAdjustRest: (Int) -> Unit,
    onToggleRestPause: () -> Unit,
    onDismissRest: () -> Unit,
    onUndismissRest: () -> Unit,
    onToggleSetComplete: (Int) -> Unit,
    onUpdateSetMetrics: (Int, Double, Int) -> Unit,
    onUpdateSetDuration: (Int, Int) -> Unit,
    onToggleExerciseTimer: (Int) -> Unit,
    onResetExerciseTimer: () -> Unit,
    onSelectExerciseTimerSet: (Int) -> Unit,
    onChangeTimerDirection: (TimerDirection) -> Unit,
    onSaveExerciseTimer: () -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    secUnit: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    val mode = spec?.trackingMode ?: entry.trackingMode
    val usesWeight = GymModelHelpers.resolveUsesWeight(
        usesWeight = entry.usesWeight,
        trackingMode = mode,
        targetWeightKg = spec?.targetWeightKg ?: entry.sets.firstOrNull()?.weightKg ?: 0.0,
        equipment = spec?.equipment
    )
    val defaultTargetDur = GymModelHelpers.resolveTargetDurationSeconds(
        spec?.targetDurationSeconds ?: entry.targetDurationSeconds,
        spec?.targetReps
    )
    val doneCount = entry.sets.count { it.completed }
    val allDone = entry.sets.isNotEmpty() && doneCount == entry.sets.size
    val firstIncompleteSet = entry.sets.firstOrNull { !it.completed }?.setNumber
        ?: entry.sets.firstOrNull()?.setNumber ?: 1

    var selectedSetNumber by remember(entry.exerciseId) {
        mutableIntStateOf(firstIncompleteSet)
    }
    var lastAutoTargetSet by remember(entry.exerciseId) {
        mutableIntStateOf(firstIncompleteSet)
    }
    LaunchedEffect(entry.exerciseId, firstIncompleteSet, entry.sets.size) {
        if (entry.sets.none { it.setNumber == selectedSetNumber }) {
            selectedSetNumber = firstIncompleteSet
        } else if (firstIncompleteSet != lastAutoTargetSet) {
            selectedSetNumber = firstIncompleteSet
        }
        lastAutoTargetSet = firstIncompleteSet
    }

    val activeSet = entry.sets.firstOrNull { it.setNumber == selectedSetNumber }
        ?: entry.sets.firstOrNull()
        ?: WorkoutSetLog(setNumber = 1, weightKg = 0.0, reps = 10, completed = false)

    val cardBorder = if (allDone) Color(0x8016A34A) else tokens.accentColor.copy(alpha = 0.32f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gym_focus_hud_stage"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. SINGLE-EXERCISE HUD HEADER + ONE-HANDED PREV / NEXT STEPPER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .ironGlassSurface(tokens, borderOverride = cardBorder)
                .then(if (noAnim) Modifier else Modifier.animateContentSize(IronPulseMotion.FluidCardSpring))
                .padding(16.dp)
                .testTag("hud_exercise_header_card"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val hasPrev = index > 0 && totalExercises > 1
                IronPulseTonalSurface(
                    onClick = onPrevExercise,
                    enabled = hasPrev,
                    shape = tokens.buttonShape,
                    baseColor = if (hasPrev) tokens.controlBg else tokens.wellBg.copy(alpha = 0.5f),
                    glossAlphaMultiplier = if (hasPrev) 1f else 0.45f,
                    border = BorderStroke(
                        1.dp,
                        if (hasPrev) tokens.surfaceBorderColor else tokens.subtleBorderColor.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("hud_prev_exercise")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        ThemedIcon(
                            icon = IronPulseIcon.CHEVRON_LEFT,
                            contentDescription = lang.t("Latihan sebelumnya", "Previous exercise"),
                            tint = if (hasPrev) tokens.palette.onSurface else tokens.mutedTextColor.copy(alpha = 0.45f),
                            size = 24.dp
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${(index + 1).toString().padStart(2, '0')} / ${totalExercises.toString().padStart(2, '0')} · ${entry.muscleGroup.label(lang)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.accentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$doneCount/${entry.sets.size} ${lang.t("Set Selesai", "Sets Done")}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (allDone) Color(0xFF4ADE80) else tokens.mutedTextColor
                    )
                }

                val hasNext = index < totalExercises - 1 && totalExercises > 1
                IronPulseTonalSurface(
                    onClick = onNextExercise,
                    enabled = hasNext,
                    shape = tokens.buttonShape,
                    baseColor = if (hasNext) tokens.controlBg else tokens.wellBg.copy(alpha = 0.5f),
                    glossAlphaMultiplier = if (hasNext) 1f else 0.45f,
                    border = BorderStroke(
                        1.dp,
                        if (hasNext) tokens.surfaceBorderColor else tokens.subtleBorderColor.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("hud_next_exercise")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        ThemedIcon(
                            icon = IronPulseIcon.CHEVRON_RIGHT,
                            contentDescription = lang.t("Latihan berikutnya", "Next exercise"),
                            tint = if (hasNext) tokens.palette.onSurface else tokens.mutedTextColor.copy(alpha = 0.45f),
                            size = 24.dp
                        )
                    }
                }
            }

            Text(
                text = entry.name,
                fontSize = 26.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tokens.palette.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hud_exercise_title")
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (spec != null) {
                    HudMetaChip(
                        text = if (mode == ExerciseTrackingMode.TIMER) {
                            "${lang.t("Target", "Target")} ${spec.targetSets}×${GymModelHelpers.formatCompactDuration(defaultTargetDur, lang)}"
                        } else {
                            "${lang.t("Target", "Target")} ${spec.targetSets}×${spec.targetReps}"
                        }
                    )
                }
                if (!spec?.equipment.isNullOrBlank()) {
                    HudMetaChip(text = spec?.equipment ?: "")
                }
                if (!usesWeight) {
                    HudMetaChip(text = lang.t("Tanpa Beban", "Bodyweight"))
                }
                if (!spec?.notes.isNullOrBlank()) {
                    IronPulseTonalSurface(
                        onClick = onToggleNotes,
                        shape = RoundedCornerShape(999.dp),
                        baseColor = tokens.controlBg,
                        border = BorderStroke(1.dp, tokens.subtleBorderColor),
                        modifier = Modifier.testTag("hud_notes_toggle"),
                        visualModifier = Modifier.heightIn(min = 34.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = lang.t("Teknik", "Form"),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.palette.onSurface
                            )
                            ThemedIcon(
                                icon = if (notesExpanded) IronPulseIcon.EXPAND_LESS else IronPulseIcon.EXPAND_MORE,
                                contentDescription = null,
                                tint = tokens.mutedTextColor,
                                size = 16.dp
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = notesExpanded && !spec?.notes.isNullOrBlank(),
                enter = if (noAnim) EnterTransition.None else expandVertically(IronPulseMotion.FluidCardSpring) + fadeIn(tween(180)),
                exit = if (noAnim) ExitTransition.None else shrinkVertically(IronPulseMotion.FluidCardSpring) + fadeOut(tween(140))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(tokens.wellBg)
                        .border(1.dp, tokens.subtleBorderColor, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = spec?.notes ?: "",
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = tokens.palette.onSurface.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // 2. LARGE REST TIMER COUNTDOWN RING / QUICK START STRIP (ISOLATED 1Hz FLOW)
        HudRestTimerRingCard(
            restTimerFlow = restTimerFlow,
            restBarDismissed = restBarDismissed,
            recommendedRestSeconds = spec?.restSeconds ?: 60,
            secUnit = secUnit,
            onStartRest = onStartRest,
            onAdjustRest = onAdjustRest,
            onToggleRestPause = onToggleRestPause,
            onDismissRest = onDismissRest,
            onUndismissRest = onUndismissRest
        )

        // 3. INTERACTIVE SET PILLS + ADD / REMOVE SET
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hud_set_selector_row"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entry.sets, key = { it.setNumber }) { s ->
                    val isSelected = s.setNumber == activeSet.setNumber
                    val bgColor = when {
                        isSelected && s.completed -> Color(0xFF16A34A)
                        isSelected -> tokens.accentColor
                        s.completed -> Color(0x2616A34A)
                        else -> tokens.controlBg
                    }
                    val borderColor = when {
                        isSelected && s.completed -> Color(0xFF4ADE80)
                        isSelected -> tokens.accentColor
                        s.completed -> Color(0x6616A34A)
                        else -> tokens.surfaceBorderColor
                    }
                    val labelColor = when {
                        isSelected && s.completed -> Color.White
                        isSelected -> tokens.onAccentColor
                        s.completed -> Color(0xFF4ADE80)
                        else -> tokens.palette.onSurface
                    }

                    IronPulseTonalSurface(
                        onClick = {
                            selectedSetNumber = s.setNumber
                            if (mode == ExerciseTrackingMode.TIMER) {
                                onSelectExerciseTimerSet(s.setNumber)
                            }
                        },
                        shape = tokens.buttonShape,
                        baseColor = bgColor,
                        isAccent = isSelected,
                        pressedScale = 0.94f,
                        border = BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("hud_set_pill_${s.setNumber}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "S${s.setNumber}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = labelColor
                            )
                            if (s.completed) {
                                ThemedIcon(
                                    icon = IronPulseIcon.CHECK_CIRCLE,
                                    contentDescription = lang.t("Selesai", "Done"),
                                    tint = labelColor,
                                    size = 16.dp
                                )
                            }
                        }
                    }
                }
            }

            IronPulseTonalSurface(
                onClick = onRemoveSet,
                enabled = entry.sets.size > 1,
                shape = tokens.buttonShape,
                baseColor = if (entry.sets.size > 1) tokens.controlBg else tokens.wellBg.copy(alpha = 0.5f),
                glossAlphaMultiplier = if (entry.sets.size > 1) 1f else 0.45f,
                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("hud_remove_set")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ThemedIcon(
                        icon = IronPulseIcon.REMOVE,
                        contentDescription = lang.t("Kurangi Set", "Remove Set"),
                        tint = if (entry.sets.size > 1) tokens.palette.onSurface else tokens.mutedTextColor.copy(alpha = 0.4f),
                        size = 20.dp
                    )
                }
            }

            IronPulseTonalSurface(
                onClick = onAddSet,
                enabled = entry.sets.size < 12,
                shape = tokens.buttonShape,
                baseColor = if (entry.sets.size < 12) tokens.controlBg else tokens.wellBg.copy(alpha = 0.5f),
                glossAlphaMultiplier = if (entry.sets.size < 12) 1f else 0.45f,
                border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.5f)),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("hud_add_set")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    ThemedIcon(
                        icon = IronPulseIcon.ADD,
                        contentDescription = lang.t("Tambah Set", "Add Set"),
                        tint = tokens.accentColor,
                        size = 20.dp
                    )
                }
            }
        }

        // 4. ACTIVE SET HERO CARD WITH OVERSIZED ONE-HANDED CONTROLS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .ironGlassSurface(
                    tokens = tokens,
                    borderOverride = if (activeSet.completed) Color(0x8016A34A) else tokens.accentColor.copy(alpha = 0.45f)
                )
                .padding(16.dp)
                .testTag("hud_active_set_card"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SET #${activeSet.setNumber}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (activeSet.completed) Color(0xFF4ADE80) else tokens.accentColor
                    )
                    Text(
                        text = "/ ${entry.sets.size}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = tokens.mutedTextColor
                    )
                }

                IronPulseTonalSurface(
                    shape = RoundedCornerShape(999.dp),
                    baseColor = if (activeSet.completed) Color(0x2616A34A) else tokens.accentColor.copy(alpha = 0.14f),
                    border = BorderStroke(
                        1.dp,
                        if (activeSet.completed) Color(0x6616A34A) else tokens.accentColor.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = if (activeSet.completed) {
                            lang.t("Selesai", "Completed")
                        } else if (activeSet.setNumber == firstIncompleteSet) {
                            lang.t("Set Aktif", "Active Set")
                        } else {
                            lang.t("Berikutnya", "Queued")
                        },
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeSet.completed) Color(0xFF4ADE80) else tokens.accentTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (mode == ExerciseTrackingMode.TIMER) {
                HudExerciseTimerStage(
                    entry = entry,
                    spec = spec,
                    activeSet = activeSet,
                    defaultTargetDur = defaultTargetDur,
                    exerciseTimersFlow = exerciseTimersFlow,
                    onToggleExerciseTimer = onToggleExerciseTimer,
                    onResetExerciseTimer = onResetExerciseTimer,
                    onChangeTimerDirection = onChangeTimerDirection,
                    onSaveExerciseTimer = onSaveExerciseTimer
                )

                val rowDur = if (activeSet.durationSeconds > 0) activeSet.durationSeconds else defaultTargetDur
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (usesWeight) {
                        HudGiantStepper(
                            label = "${lang.t("Beban", "Weight")} (kg)",
                            valueText = GymModelHelpers.formatWeight(activeSet.weightKg),
                            decrementLabel = "-2.5",
                            incrementLabel = "+2.5",
                            onDecrement = {
                                val next = ((activeSet.weightKg - 2.5) * 10.0).roundToInt() / 10.0
                                onUpdateSetMetrics(activeSet.setNumber, next.coerceAtLeast(0.0), activeSet.reps)
                            },
                            onIncrement = {
                                val next = ((activeSet.weightKg + 2.5) * 10.0).roundToInt() / 10.0
                                onUpdateSetMetrics(activeSet.setNumber, next.coerceAtMost(999.0), activeSet.reps)
                            },
                            onDirectEdit = { raw ->
                                val parsed = raw.replace(',', '.').toDoubleOrNull()
                                if (parsed != null) {
                                    onUpdateSetMetrics(activeSet.setNumber, parsed.coerceIn(0.0, 999.0), activeSet.reps)
                                }
                            },
                            cardTag = "hud_weight_stepper",
                            decTag = "hud_weight_dec",
                            incTag = "hud_weight_inc",
                            valueTag = "hud_weight_value",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HudGiantStepper(
                        label = lang.t("Durasi Target", "Target Duration"),
                        valueText = GymModelHelpers.formatTimerClock(rowDur),
                        decrementLabel = "-10$secUnit",
                        incrementLabel = "+10$secUnit",
                        onDecrement = {
                            onUpdateSetDuration(activeSet.setNumber, (rowDur - 10).coerceAtLeast(5))
                        },
                        onIncrement = {
                            onUpdateSetDuration(activeSet.setNumber, (rowDur + 10).coerceAtMost(10800))
                        },
                        cardTag = "hud_duration_stepper",
                        decTag = "hud_duration_dec",
                        incTag = "hud_duration_inc",
                        valueTag = "hud_duration_value",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (usesWeight) {
                        HudGiantStepper(
                            label = "${lang.t("Beban", "Weight")} (kg)",
                            valueText = GymModelHelpers.formatWeight(activeSet.weightKg),
                            decrementLabel = "-2.5",
                            incrementLabel = "+2.5",
                            onDecrement = {
                                val next = ((activeSet.weightKg - 2.5) * 10.0).roundToInt() / 10.0
                                onUpdateSetMetrics(activeSet.setNumber, next.coerceAtLeast(0.0), activeSet.reps)
                            },
                            onIncrement = {
                                val next = ((activeSet.weightKg + 2.5) * 10.0).roundToInt() / 10.0
                                onUpdateSetMetrics(activeSet.setNumber, next.coerceAtMost(999.0), activeSet.reps)
                            },
                            onDirectEdit = { raw ->
                                val parsed = raw.replace(',', '.').toDoubleOrNull()
                                if (parsed != null) {
                                    onUpdateSetMetrics(activeSet.setNumber, parsed.coerceIn(0.0, 999.0), activeSet.reps)
                                }
                            },
                            cardTag = "hud_weight_stepper",
                            decTag = "hud_weight_dec",
                            incTag = "hud_weight_inc",
                            valueTag = "hud_weight_value",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HudGiantStepper(
                        label = "${lang.t("Repetisi", "Reps")}",
                        valueText = activeSet.reps.toString(),
                        decrementLabel = "-1",
                        incrementLabel = "+1",
                        onDecrement = {
                            onUpdateSetMetrics(activeSet.setNumber, activeSet.weightKg, (activeSet.reps - 1).coerceAtLeast(0))
                        },
                        onIncrement = {
                            onUpdateSetMetrics(activeSet.setNumber, activeSet.weightKg, (activeSet.reps + 1).coerceAtMost(999))
                        },
                        onDirectEdit = { raw ->
                            val parsed = raw.toIntOrNull()
                            if (parsed != null) {
                                onUpdateSetMetrics(activeSet.setNumber, activeSet.weightKg, parsed.coerceIn(0, 999))
                            }
                        },
                        cardTag = "hud_reps_stepper",
                        decTag = "hud_reps_dec",
                        incTag = "hud_reps_inc",
                        valueTag = "hud_reps_value",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HudCompleteSetButton(
                setNumber = activeSet.setNumber,
                completed = activeSet.completed,
                cooldownSecondsFlow = cooldownSecondsFlow,
                secUnit = secUnit,
                onClick = { onToggleSetComplete(activeSet.setNumber) }
            )
        }
    }
}

@Composable
private fun HudMetaChip(text: String) {
    val tokens = LocalIronPulseTokens.current
    IronPulseTonalSurface(
        shape = RoundedCornerShape(999.dp),
        baseColor = tokens.wellBg,
        border = BorderStroke(1.dp, tokens.subtleBorderColor),
        visualModifier = Modifier.heightIn(min = 34.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = tokens.palette.onSurface.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HudRestTimerRingCard(
    restTimerFlow: StateFlow<RestTimerDisplayState>,
    restBarDismissed: Boolean,
    recommendedRestSeconds: Int,
    secUnit: String,
    onStartRest: (Int) -> Unit,
    onAdjustRest: (Int) -> Unit,
    onToggleRestPause: () -> Unit,
    onDismissRest: () -> Unit,
    onUndismissRest: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val rest by restTimerFlow.collectAsStateWithLifecycle()

    LaunchedEffect(rest.isActive) {
        if (rest.isActive) onUndismissRest()
    }

    val restRunning = rest.isActive
    val restRemaining = rest.remainingSeconds
    val restTotal = rest.totalSeconds.coerceAtLeast(1)
    val showLargeRing = !restBarDismissed && (restRunning || (restRemaining in 1 until restTotal))

    if (showLargeRing) {
        val ringColor = if (restRemaining == 0) Color(0xFF4ADE80) else tokens.accentColor
        val frac by animateFloatAsState(
            targetValue = (restRemaining.toFloat() / restTotal.toFloat()).coerceIn(0f, 1f),
            animationSpec = if (noAnim) tween(0) else tween(220),
            label = "hudRestCountdownArc"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .ironGlassSurface(
                    tokens = tokens,
                    borderOverride = ringColor.copy(alpha = 0.65f)
                )
                .padding(16.dp)
                .testTag("hud_rest_ring_card"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    ThemedIcon(
                        icon = IronPulseIcon.TIMER,
                        contentDescription = null,
                        tint = ringColor,
                        size = 18.dp
                    )
                    Text(
                        text = rest.exerciseName.ifBlank { lang.t("Waktu Istirahat", "Rest Timer") },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IronPulseTonalSurface(
                    shape = RoundedCornerShape(999.dp),
                    baseColor = ringColor.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, ringColor.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = when {
                            restRemaining == 0 -> lang.t("Selesai!", "Done!")
                            restRunning -> lang.t("Berjalan", "Active")
                            else -> lang.t("Dijeda", "Paused")
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ringColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Large Circular Countdown Ring
            Box(
                modifier = Modifier
                    .size(168.dp)
                    .testTag("hud_rest_ring_visual"),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokePx = 12.dp.toPx()
                    drawArc(
                        color = tokens.dividerColor,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokePx)
                    )
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = 360f * frac,
                        useCenter = false,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = GymModelHelpers.formatRestDuration(restRemaining),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (restRemaining == 0) tokens.successColor else tokens.palette.onSurface,
                        modifier = Modifier.testTag("hud_rest_time_text")
                    )
                    Text(
                        text = "/ ${GymModelHelpers.formatRestDuration(restTotal)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = tokens.mutedTextColor
                    )
                }
            }

            // Big Quick Adjustment & Pause Controls: [-15d] [Jeda/Lanjut] [+15d]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IronPulseTonalSurface(
                    onClick = { onAdjustRest(-15) },
                    shape = tokens.buttonShape,
                    baseColor = tokens.controlBg,
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("hud_rest_minus_15")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "-15$secUnit",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tokens.palette.onSurface
                        )
                    }
                }

                IronPulseButton(
                    onClick = onToggleRestPause,
                    shape = tokens.buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ringColor,
                        contentColor = if (restRemaining == 0) Color(0xFF0A0B0E) else tokens.onAccentColor
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .weight(1.25f)
                        .height(50.dp)
                        .tactilePress(disabledByPerf = noAnim)
                        .testTag("hud_rest_toggle")
                ) {
                    ThemedIcon(
                        icon = if (restRunning) IronPulseIcon.PAUSE else IronPulseIcon.PLAY,
                        contentDescription = null,
                        size = 20.dp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (restRunning) lang.t("Jeda", "Pause") else lang.t("Lanjut", "Resume"),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }

                IronPulseTonalSurface(
                    onClick = { onAdjustRest(15) },
                    shape = tokens.buttonShape,
                    baseColor = tokens.controlBg,
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("hud_rest_plus_15")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+15$secUnit",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tokens.palette.onSurface
                        )
                    }
                }
            }

            IronPulseOutlinedButton(
                onClick = onDismissRest,
                shape = tokens.buttonShape,
                border = BorderStroke(1.dp, tokens.subtleBorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .tactilePress(disabledByPerf = noAnim)
                    .testTag("hud_rest_skip")
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.CLOSE,
                    contentDescription = null,
                    tint = tokens.mutedTextColor,
                    size = 18.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = lang.t("Lewati Istirahat", "Skip Rest"),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
            }
        }
    } else {
        // Idle Quick Rest Trigger Strip
        val presets = remember(recommendedRestSeconds) {
            listOf(recommendedRestSeconds, 45, 60, 90, 120).distinct()
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .ironGlassSurface(tokens)
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("hud_rest_idle_strip"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.TIMER,
                    contentDescription = null,
                    tint = tokens.accentColor,
                    size = 16.dp
                )
                Text(
                    text = lang.t("Mulai Istirahat Cepat", "Quick Rest Timer"),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                presets.forEach { secs ->
                    val isRecommended = secs == recommendedRestSeconds
                    IronPulseTonalSurface(
                        onClick = { onStartRest(secs) },
                        shape = tokens.buttonShape,
                        baseColor = if (isRecommended) tokens.accentColor.copy(alpha = 0.16f) else tokens.controlBg,
                        isAccent = isRecommended,
                        border = BorderStroke(
                            1.dp,
                            if (isRecommended) tokens.accentColor.copy(alpha = 0.5f) else tokens.surfaceBorderColor
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("hud_rest_preset_$secs")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$secs$secUnit",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isRecommended) tokens.accentTextColor else tokens.palette.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HudGiantStepper(
    label: String,
    valueText: String,
    decrementLabel: String,
    incrementLabel: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onDirectEdit: ((String) -> Unit)? = null,
    cardTag: String,
    decTag: String,
    incTag: String,
    valueTag: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val noAnim = tokens.performance.noAnimations
    val focusManager = LocalFocusManager.current
    var editing by remember { mutableStateOf(false) }
    var draft by remember(valueText) { mutableStateOf(valueText) }

    Column(
        modifier = modifier
            .ironTonalControl(
                tokens = tokens,
                shape = RoundedCornerShape(16.dp),
                baseColor = tokens.wellBg,
                isAccent = false
            )
            .border(1.dp, tokens.surfaceBorderColor, RoundedCornerShape(16.dp))
            .padding(12.dp)
            .testTag(cardTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = tokens.mutedTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = onDirectEdit != null) {
                    draft = valueText
                    editing = true
                }
                .padding(horizontal = 4.dp),
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            editing = false
                            focusManager.clearFocus()
                        }
                    ),
                    textStyle = TextStyle(
                        color = tokens.accentColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(tokens.accentColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("${valueTag}_input")
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = valueText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface,
                        maxLines = 1,
                        modifier = Modifier.testTag(valueTag)
                    )
                    if (onDirectEdit != null) {
                        ThemedIcon(
                            icon = IronPulseIcon.EDIT,
                            contentDescription = null,
                            tint = tokens.mutedTextColor.copy(alpha = 0.65f),
                            size = 14.dp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IronPulseTonalSurface(
                onClick = {
                    editing = false
                    onDecrement()
                },
                shape = tokens.buttonShape,
                baseColor = tokens.controlBg,
                pressedScale = 0.92f,
                border = BorderStroke(1.dp, tokens.subtleBorderColor),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag(decTag)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = decrementLabel,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface
                    )
                }
            }

            IronPulseTonalSurface(
                onClick = {
                    editing = false
                    onIncrement()
                },
                shape = tokens.buttonShape,
                baseColor = tokens.accentColor.copy(alpha = 0.18f),
                pressedScale = 0.92f,
                border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.45f)),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag(incTag)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = incrementLabel,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.accentTextColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HudExerciseTimerStage(
    entry: WorkoutLogEntry,
    spec: ExerciseItem?,
    activeSet: WorkoutSetLog,
    defaultTargetDur: Int,
    exerciseTimersFlow: StateFlow<Map<String, ExerciseTimerUiState>>,
    onToggleExerciseTimer: (Int) -> Unit,
    onResetExerciseTimer: () -> Unit,
    onChangeTimerDirection: (TimerDirection) -> Unit,
    onSaveExerciseTimer: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    val timerMap by exerciseTimersFlow.collectAsStateWithLifecycle()
    val timerState = timerMap[entry.exerciseId]
    val timerRunning = timerState?.running == true
    val timerDirection = timerState?.direction ?: spec?.timerDirection ?: entry.timerDirection
    val activeTargetSeconds = (activeSet.durationSeconds.takeIf { it > 0 } ?: defaultTargetDur).coerceAtLeast(5)
    val elapsedSeconds = timerState?.elapsedSeconds ?: 0
    val displayClockSeconds = if (timerDirection == TimerDirection.COUNTDOWN) {
        (activeTargetSeconds - elapsedSeconds).coerceAtLeast(0)
    } else {
        elapsedSeconds
    }
    val timerProgress = if (activeTargetSeconds > 0) {
        (elapsedSeconds.toFloat() / activeTargetSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .ironTonalControl(
                tokens = tokens,
                shape = RoundedCornerShape(16.dp),
                baseColor = if (timerRunning) Color(0xB30C1924) else tokens.wellBg,
                isAccent = false
            )
            .border(
                1.dp,
                if (timerRunning) Color(0x8C38BDF8) else tokens.subtleBorderColor,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .ironTonalControl(
                    tokens = tokens,
                    shape = RoundedCornerShape(999.dp),
                    baseColor = tokens.controlBg,
                    isAccent = false
                )
                .border(1.dp, tokens.subtleBorderColor, RoundedCornerShape(999.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                TimerDirection.COUNTDOWN to lang.t("Hitung Mundur", "Countdown"),
                TimerDirection.COUNTUP to lang.t("Hitung Maju", "Count-Up")
            ).forEach { (dir, label) ->
                val active = timerDirection == dir
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp)
                        .tactilePress(pressedScale = 0.96f, disabledByPerf = noAnim)
                        .then(
                            if (active) {
                                Modifier.ironTonalControl(
                                    tokens = tokens,
                                    shape = RoundedCornerShape(999.dp),
                                    baseColor = Color(0xFF38BDF8),
                                    isAccent = true
                                )
                            } else {
                                Modifier.clip(RoundedCornerShape(999.dp))
                            }
                        )
                        .clickable { onChangeTimerDirection(dir) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color(0xFF0A0B0E) else tokens.mutedTextColor
                    )
                }
            }
        }

        Text(
            text = GymModelHelpers.formatTimerClock(displayClockSeconds),
            fontFamily = FontFamily.Monospace,
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (timerRunning) tokens.infoColor else tokens.palette.onSurface,
            modifier = Modifier.testTag("hud_exercise_timer_clock")
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(tokens.dividerColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .graphicsLayer {
                        scaleX = timerProgress
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (timerDirection == TimerDirection.COUNTDOWN) Color(0xFF38BDF8) else tokens.accentColor)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IronPulseButton(
                onClick = { onToggleExerciseTimer(activeSet.setNumber) },
                shape = tokens.buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerRunning) Color(0xFFF59E0B) else Color(0xFF38BDF8),
                    contentColor = Color(0xFF0A0B0E)
                ),
                modifier = Modifier
                    .weight(1.4f)
                    .height(52.dp)
                    .tactilePress(disabledByPerf = noAnim)
                    .testTag("hud_exercise_timer_toggle")
            ) {
                ThemedIcon(
                    icon = if (timerRunning) IronPulseIcon.PAUSE else IronPulseIcon.PLAY,
                    contentDescription = null,
                    size = 20.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        timerRunning -> lang.t("Jeda", "Pause")
                        elapsedSeconds > 0 -> lang.t("Lanjut", "Resume")
                        else -> lang.t("Mulai Timer", "Start")
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }

            IronPulseOutlinedButton(
                onClick = onResetExerciseTimer,
                shape = tokens.buttonShape,
                border = BorderStroke(1.dp, tokens.subtleBorderColor),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .tactilePress(disabledByPerf = noAnim)
                    .testTag("hud_exercise_timer_reset")
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.REFRESH,
                    contentDescription = null,
                    tint = tokens.palette.onSurface,
                    size = 18.dp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(lang.t("Reset", "Reset"), fontSize = 13.sp, color = tokens.palette.onSurface)
            }
        }

        if (elapsedSeconds > 0 || timerRunning) {
            IronPulseButton(
                onClick = onSaveExerciseTimer,
                shape = tokens.buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF16A34A),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .tactilePress(disabledByPerf = noAnim)
                    .testTag("hud_exercise_timer_save")
            ) {
                ThemedIcon(IronPulseIcon.CHECK, contentDescription = null, tint = Color.White, size = 18.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = lang.t("Simpan Set #${activeSet.setNumber}", "Save Set #${activeSet.setNumber}"),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.5.sp
                )
            }
        }
    }
}

@Composable
private fun HudCompleteSetButton(
    setNumber: Int,
    completed: Boolean,
    cooldownSecondsFlow: StateFlow<Int>,
    secUnit: String,
    onClick: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val cooldownRemaining = if (!completed) {
        cooldownSecondsFlow.collectAsStateWithLifecycle().value
    } else {
        0
    }
    val isBlockedByCooldown = !completed && cooldownRemaining > 0

    IronPulseTonalSurface(
        onClick = onClick,
        shape = tokens.buttonShape,
        baseColor = when {
            completed -> Color(0x2616A34A)
            isBlockedByCooldown -> tokens.wellBg
            else -> tokens.accentColor
        },
        isAccent = !completed && !isBlockedByCooldown,
        pressedScale = 0.965f,
        border = BorderStroke(
            1.5.dp,
            when {
                completed -> Color(0x8016A34A)
                isBlockedByCooldown -> Color(0x8038BDF8)
                else -> tokens.accentColor
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("hud_complete_set_button")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isBlockedByCooldown) {
                ThemedIcon(
                    icon = IronPulseIcon.TIMER,
                    contentDescription = null,
                    tint = tokens.infoColor,
                    size = 20.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lang.t("Tunggu $cooldownRemaining$secUnit", "Wait $cooldownRemaining$secUnit"),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.infoColor
                )
            } else {
                ThemedIcon(
                    icon = if (completed) IronPulseIcon.CHECK_CIRCLE else IronPulseIcon.CHECK,
                    contentDescription = null,
                    tint = if (completed) tokens.successColor else tokens.onAccentColor,
                    size = 22.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (completed) {
                        lang.t("Set #$setNumber Selesai · Batal", "Set #$setNumber Done · Undo")
                    } else {
                        lang.t("Selesaikan Set #$setNumber", "Complete Set #$setNumber")
                    },
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (completed) tokens.successColor else tokens.onAccentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
