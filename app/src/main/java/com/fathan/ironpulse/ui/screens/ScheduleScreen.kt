package com.fathan.ironpulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import com.fathan.ironpulse.ui.components.IronPulseMotion
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.data.ProgramTemplatePresets
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.CustomRingtone
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.DaySchedule
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.SavedCustomSchedule
import com.fathan.ironpulse.ui.components.IronPulseIcon
import com.fathan.ironpulse.ui.components.SaveCustomScheduleDialog
import com.fathan.ironpulse.ui.components.SchedulePreviewTarget
import com.fathan.ironpulse.ui.components.ScheduleProgramPreviewSheet
import com.fathan.ironpulse.ui.components.ScheduleTopProgramStrip
import com.fathan.ironpulse.ui.components.ThemedIcon
import com.fathan.ironpulse.ui.components.ThemedIronDialog
import com.fathan.ironpulse.ui.components.TutorialSpotlightTarget
import com.fathan.ironpulse.ui.components.tutorialSpotlightTarget
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseOutlinedButton
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    state: IronPulseUiState,
    viewModel: GymViewModel,
    contentPadding: PaddingValues,
    onOpenExercisesForDay: (DayKey) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val activeGymDaysCount = remember(state.schedules) {
        DayKey.entries.count { state.schedules[it]?.isGymDay == true }
    }
    val activeAlarmsCount = remember(state.schedules) {
        DayKey.entries.count { state.schedules[it]?.let { s -> s.isGymDay && s.alarmEnabled } == true }
    }

    var timePickerDay by remember { mutableStateOf<DayKey?>(null) }
    var editingFocusDay by remember { mutableStateOf<DayKey?>(null) }
    var showSaveScheduleDialog by remember { mutableStateOf(false) }
    var renamingCustomSchedule by remember { mutableStateOf<SavedCustomSchedule?>(null) }
    var previewTarget by remember { mutableStateOf<SchedulePreviewTarget?>(null) }

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
            // TOPMOST SECTION: Saved Custom Schedules & Official Program Switcher
            item(key = "schedule_program_strip", contentType = "program_strip") {
                ScheduleTopProgramStrip(
                    savedSchedules = state.savedCustomSchedules,
                    officialTemplates = ProgramTemplatePresets.OFFICIAL_TEMPLATES,
                    activeProfileId = state.activeScheduleProfileId,
                    applyingSchedule = state.applyingScheduleProfile,
                    onOpenSaveDialog = { showSaveScheduleDialog = true },
                    onSelectSaved = { saved ->
                        previewTarget = SchedulePreviewTarget.Saved(saved)
                    },
                    onSelectOfficial = { template ->
                        previewTarget = SchedulePreviewTarget.Official(template)
                    },
                    modifier = Modifier.tutorialSpotlightTarget(TutorialSpotlightTarget.SCHEDULE_PROGRAM_STRIP)
                )
            }

            // Weekly Overview Header
            item(key = "schedule_header", contentType = "header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .ironGlassSurface(tokens)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = lang.t("Jadwal & Alarm Mingguan", "Weekly Schedule & Alarms"),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.32f)),
                            modifier = Modifier.ironTonalControl(
                                tokens = tokens,
                                shape = RoundedCornerShape(999.dp),
                                baseColor = tokens.accentColor.copy(alpha = 0.14f),
                                isAccent = false
                            )
                        ) {
                            Text(
                                text = "$activeGymDaysCount/7 ${lang.t("Hari Gym", "Gym Days")} · $activeAlarmsCount ${lang.t("Alarm", "Alarms")}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.accentColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        if (!state.activeScheduleProfileName.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                                modifier = Modifier.ironTonalControl(
                                    tokens = tokens,
                                    shape = RoundedCornerShape(999.dp),
                                    baseColor = tokens.wellBg,
                                    isAccent = false
                                )
                            ) {
                                Text(
                                    text = state.activeScheduleProfileName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.palette.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            items(DayKey.entries, key = { it.key }, contentType = { "day_card" }) { day ->
                val sched = state.schedules[day] ?: return@items
                DayScheduleCard(
                    day = day,
                    sched = sched,
                    isToday = day == state.todayDayKey,
                    isEditingFocus = editingFocusDay == day,
                    defaultRingtoneId = state.profile?.defaultRingtoneId ?: "builtin_athletic_bell",
                    customRingtones = state.customRingtones,
                    onStartEditingFocus = { editingFocusDay = day },
                    onStopEditingFocus = { if (editingFocusDay == day) editingFocusDay = null },
                    onToggleGymDay = {
                        val nextGym = !sched.isGymDay
                        if (!nextGym && editingFocusDay == day) editingFocusDay = null
                        viewModel.updateScheduleFields(
                            dayKey = day,
                            isGymDay = nextGym,
                            alarmEnabled = nextGym
                        )
                    },
                    onOpenExercises = { onOpenExercisesForDay(day) },
                    onSaveFocusTitle = { nextTitle ->
                        viewModel.updateScheduleFields(dayKey = day, focusTitle = nextTitle)
                        editingFocusDay = null
                    },
                    onOpenTimePicker = { timePickerDay = day },
                    onToggleAlarmEnabled = {
                        viewModel.updateScheduleFields(dayKey = day, alarmEnabled = !sched.alarmEnabled)
                    },
                    onSelectRingtone = { nextId ->
                        viewModel.updateScheduleFields(dayKey = day, ringtoneId = nextId)
                    },
                    modifier = if (noAnim) Modifier else Modifier.animateItem()
                )
            }
        }
    }

    val editingDay = timePickerDay
    val editingSchedule = editingDay?.let { state.schedules[it] }
    if (editingDay != null && editingSchedule != null) {
        val parts = editingSchedule.alarmTime.split(":")
        val initHour = parts.getOrNull(0)?.toIntOrNull()?.coerceIn(0, 23) ?: 6
        val initMinute = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(0, 59) ?: 30
        val pickerState = rememberTimePickerState(
            initialHour = initHour,
            initialMinute = initMinute,
            is24Hour = tokens.clockFormat == ClockFormat.H24
        )

        ThemedIronDialog(
            onDismissRequest = { timePickerDay = null },
            maxWidth = 440.dp,
            scrollable = true,
            modifier = Modifier.testTag("schedule_time_picker_dialog"),
            title = {
                Text(
                    text = "${lang.t("Jam Alarm", "Alarm Time")} — ${editingDay.label(lang)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface
                )
            },
            actions = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IronPulseOutlinedButton(
                        onClick = { timePickerDay = null },
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
                            val hhmm = String.format(Locale.US, "%02d:%02d", pickerState.hour, pickerState.minute)
                            viewModel.updateScheduleFields(dayKey = editingDay, alarmTime = hhmm)
                            timePickerDay = null
                        },
                        shape = tokens.buttonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentColor,
                            contentColor = tokens.onAccentColor
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .heightIn(min = 48.dp)
                            .tactilePress(disabledByPerf = noAnim)
                    ) {
                        Text(lang.t("Simpan", "Save"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = pickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = tokens.wellBg,
                        clockDialSelectedContentColor = tokens.onAccentColor,
                        clockDialUnselectedContentColor = tokens.palette.onSurface,
                        selectorColor = tokens.accentColor,
                        containerColor = Color.Transparent,
                        periodSelectorBorderColor = tokens.surfaceBorderColor,
                        periodSelectorSelectedContainerColor = tokens.accentColor,
                        periodSelectorUnselectedContainerColor = tokens.controlBg,
                        periodSelectorSelectedContentColor = tokens.onAccentColor,
                        periodSelectorUnselectedContentColor = tokens.palette.onSurface,
                        timeSelectorSelectedContainerColor = tokens.accentColor.copy(alpha = 0.22f),
                        timeSelectorUnselectedContainerColor = tokens.wellBg,
                        timeSelectorSelectedContentColor = tokens.accentTextColor,
                        timeSelectorUnselectedContentColor = tokens.palette.onSurface
                    )
                )
            }
        }
    }

    if (showSaveScheduleDialog) {
        SaveCustomScheduleDialog(
            initialName = "",
            isRename = false,
            onDismiss = { showSaveScheduleDialog = false },
            onConfirm = { name ->
                viewModel.saveCurrentScheduleAsCustom(name)
                showSaveScheduleDialog = false
            }
        )
    }

    val targetToRename = renamingCustomSchedule
    if (targetToRename != null) {
        SaveCustomScheduleDialog(
            initialName = targetToRename.name,
            isRename = true,
            onDismiss = { renamingCustomSchedule = null },
            onConfirm = { newName ->
                viewModel.renameCustomSchedule(targetToRename.id, newName)
                renamingCustomSchedule = null
            }
        )
    }

    val activePreview = previewTarget
    if (activePreview != null) {
        val targetId = when (activePreview) {
            is SchedulePreviewTarget.Official -> activePreview.template.id
            is SchedulePreviewTarget.Saved -> activePreview.profile.id
        }
        ScheduleProgramPreviewSheet(
            target = activePreview,
            isCurrentlyActive = state.activeScheduleProfileId == targetId,
            applying = state.applyingScheduleProfile,
            onDismiss = { previewTarget = null },
            onApply = { preserveAlarms ->
                when (activePreview) {
                    is SchedulePreviewTarget.Official -> {
                        viewModel.applyOfficialProgramTemplate(
                            templateId = activePreview.template.id,
                            preserveAlarmsAndRingtones = preserveAlarms
                        )
                    }
                    is SchedulePreviewTarget.Saved -> {
                        viewModel.applySavedCustomSchedule(
                            profileId = activePreview.profile.id,
                            preserveAlarmsAndRingtones = preserveAlarms
                        )
                    }
                }
                previewTarget = null
            },
            onRequestRename = { saved ->
                previewTarget = null
                renamingCustomSchedule = saved
            },
            onDeleteSaved = { saved ->
                viewModel.deleteCustomSchedule(saved.id)
                previewTarget = null
            }
        )
    }
}

@Composable
private fun DayScheduleCard(
    day: DayKey,
    sched: DaySchedule,
    isToday: Boolean,
    isEditingFocus: Boolean,
    defaultRingtoneId: String,
    customRingtones: List<CustomRingtone>,
    onStartEditingFocus: () -> Unit,
    onStopEditingFocus: () -> Unit,
    onToggleGymDay: () -> Unit,
    onOpenExercises: () -> Unit,
    onSaveFocusTitle: (String) -> Unit,
    onOpenTimePicker: () -> Unit,
    onToggleAlarmEnabled: () -> Unit,
    onSelectRingtone: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    var focusDraft by remember(sched.focusTitle, isEditingFocus) { mutableStateOf(sched.focusTitle) }
    var ringtoneMenuOpen by remember { mutableStateOf(false) }

    val toneDisplayName = remember(sched.ringtoneId, defaultRingtoneId, customRingtones, lang) {
        if (sched.ringtoneId == "default" || sched.ringtoneId.isBlank()) {
            lang.t("Ikuti Ringtone Utama", "Use Default Ringtone")
        } else {
            GymModelHelpers.resolveRingtoneName(sched.ringtoneId, defaultRingtoneId, customRingtones).second
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens, subtle = !sched.isGymDay)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Day Title + Today tag + Gym Day Toggle + Exercises link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = day.label(lang),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sched.isGymDay) tokens.palette.onSurface else tokens.palette.onSurfaceVariant
                    )
                    if (isToday) {
                        Text(
                            text = "· ${lang.t("Hari Ini", "Today")}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.accentColor
                        )
                    }
                }
                Text(
                    text = if (sched.isGymDay) {
                        "${sched.exercises.size} ${lang.t("gerakan terjadwal", "scheduled exercises")}"
                    } else {
                        lang.t("Hari istirahat · alarm nonaktif", "Rest day · alarm off")
                    },
                    fontSize = 12.sp,
                    color = tokens.mutedTextColor
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onToggleGymDay,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Transparent,
                    border = if (sched.isGymDay) null else BorderStroke(1.dp, tokens.subtleBorderColor),
                    modifier = Modifier
                        .tactilePress(disabledByPerf = noAnim)
                        .ironTonalControl(
                            tokens = tokens,
                            shape = RoundedCornerShape(10.dp),
                            baseColor = if (sched.isGymDay) tokens.accentColor else tokens.controlBg,
                            isAccent = sched.isGymDay
                        )
                ) {
                    Text(
                        text = if (sched.isGymDay) lang.t("Hari Gym", "Gym Day") else "Rest Day",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (sched.isGymDay) tokens.onAccentColor else tokens.mutedTextColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }

                if (sched.isGymDay) {
                    Surface(
                        onClick = onOpenExercises,
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, tokens.subtleBorderColor),
                        modifier = Modifier
                            .tactilePress(disabledByPerf = noAnim)
                            .ironTonalControl(
                                tokens = tokens,
                                shape = RoundedCornerShape(10.dp),
                                baseColor = tokens.controlBg,
                                isAccent = false
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(start = 10.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = lang.t("Gerakan", "Exercises"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.palette.onSurface
                            )
                            ThemedIcon(
                                icon = IronPulseIcon.CHEVRON_RIGHT,
                                contentDescription = null,
                                tint = tokens.mutedTextColor,
                                size = 16.dp
                            )
                        }
                    }
                }
            }
        }

        // Row 2: Active Gym Day Details
        AnimatedVisibility(
            visible = sched.isGymDay,
            enter = if (noAnim) EnterTransition.None else expandVertically(IronPulseMotion.FluidCardSpring) + fadeIn(tween(180)),
            exit = if (noAnim) ExitTransition.None else shrinkVertically(IronPulseMotion.FluidCardSpring) + fadeOut(tween(140))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HorizontalDivider(color = tokens.subtleBorderColor)

            // Lightweight Tap-to-Edit Focus Title (only mounts OutlinedTextField when editing)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = lang.t("Fokus Latihan", "Workout Focus"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = tokens.mutedTextColor
                )
                if (isEditingFocus) {
                    OutlinedTextField(
                        value = focusDraft,
                        onValueChange = { focusDraft = it.take(80) },
                        singleLine = true,
                        placeholder = {
                            Text(lang.t("Misal: Push — Dada & Bahu", "e.g. Push — Chest & Shoulders"))
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val trimmed = focusDraft.trim()
                                if (trimmed.isNotEmpty()) {
                                    IconButton(onClick = { onSaveFocusTitle(trimmed) }) {
                                        ThemedIcon(IronPulseIcon.CHECK, contentDescription = null, tint = tokens.accentColor, size = 18.dp)
                                    }
                                }
                                IconButton(onClick = onStopEditingFocus) {
                                    ThemedIcon(IronPulseIcon.CLOSE, contentDescription = null, tint = tokens.mutedTextColor, size = 18.dp)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            val trimmed = focusDraft.trim()
                            if (trimmed.isNotEmpty() && trimmed != sched.focusTitle) {
                                onSaveFocusTitle(trimmed)
                            } else {
                                onStopEditingFocus()
                            }
                        }),
                        shape = tokens.inputShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = tokens.accentColor,
                            unfocusedBorderColor = tokens.subtleBorderColor,
                            focusedTextColor = tokens.palette.onSurface,
                            unfocusedTextColor = tokens.palette.onSurface,
                            focusedContainerColor = tokens.wellBg,
                            unfocusedContainerColor = tokens.wellBg
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Surface(
                        onClick = onStartEditingFocus,
                        shape = tokens.inputShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, tokens.subtleBorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .tactilePress(pressedScale = 0.985f, disabledByPerf = noAnim)
                            .ironTonalControl(
                                tokens = tokens,
                                shape = tokens.inputShape,
                                baseColor = tokens.wellBg,
                                isAccent = false
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = sched.focusTitle.ifBlank {
                                    lang.t("Ketuk untuk atur fokus latihan...", "Tap to set workout focus...")
                                },
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (sched.focusTitle.isBlank()) tokens.mutedTextColor else tokens.palette.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ThemedIcon(
                                icon = IronPulseIcon.EDIT,
                                contentDescription = null,
                                tint = tokens.accentColor,
                                size = 16.dp
                            )
                        }
                    }
                }
            }

            // Alarm Time + Bell Toggle & Ringtone Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Time + Bell
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = lang.t("Jam Alarm", "Alarm Time"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.mutedTextColor
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = onOpenTimePicker,
                            shape = tokens.inputShape,
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, tokens.subtleBorderColor),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .tactilePress(disabledByPerf = noAnim)
                                .ironTonalControl(
                                    tokens = tokens,
                                    shape = tokens.inputShape,
                                    baseColor = tokens.wellBg,
                                    isAccent = false
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ThemedIcon(
                                    icon = IronPulseIcon.TIMER,
                                    contentDescription = null,
                                    tint = tokens.accentColor,
                                    size = 16.dp
                                )
                                Text(
                                    text = GymModelHelpers.formatAlarmTime(sched.alarmTime, tokens.clockFormat, lang),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.palette.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            onClick = onToggleAlarmEnabled,
                            shape = tokens.inputShape,
                            color = Color.Transparent,
                            border = BorderStroke(
                                1.dp,
                                if (sched.alarmEnabled) tokens.accentColor.copy(alpha = 0.5f) else tokens.subtleBorderColor
                            ),
                            modifier = Modifier
                                .size(46.dp)
                                .tactilePress(disabledByPerf = noAnim)
                                .ironTonalControl(
                                    tokens = tokens,
                                    shape = tokens.inputShape,
                                    baseColor = if (sched.alarmEnabled) tokens.accentColor.copy(alpha = 0.16f) else tokens.controlBg,
                                    isAccent = sched.alarmEnabled
                                )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                ThemedIcon(
                                    icon = if (sched.alarmEnabled) {
                                        IronPulseIcon.NOTIFICATIONS
                                    } else {
                                        IronPulseIcon.NOTIFICATIONS_OFF
                                    },
                                    contentDescription = null,
                                    tint = if (sched.alarmEnabled) tokens.accentColor else tokens.mutedTextColor,
                                    size = 18.dp
                                )
                            }
                        }
                    }
                }

                // Per-Day Ringtone Picker
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = lang.t("Nada Alarm", "Alarm Tone"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.mutedTextColor
                    )
                    Box {
                        Surface(
                            onClick = { ringtoneMenuOpen = true },
                            shape = tokens.inputShape,
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, tokens.subtleBorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .tactilePress(disabledByPerf = noAnim)
                                .ironTonalControl(
                                    tokens = tokens,
                                    shape = tokens.inputShape,
                                    baseColor = tokens.wellBg,
                                    isAccent = false
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = toneDisplayName,
                                    fontSize = 12.5.sp,
                                    color = tokens.palette.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                ThemedIcon(
                                    icon = IronPulseIcon.EXPAND_MORE,
                                    contentDescription = null,
                                    tint = tokens.mutedTextColor,
                                    size = 18.dp
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = ringtoneMenuOpen,
                            onDismissRequest = { ringtoneMenuOpen = false },
                            containerColor = tokens.palette.surfaceContainerHigh
                        ) {
                            DropdownMenuItem(
                                text = { Text(lang.t("Ikuti Ringtone Utama", "Use Default Ringtone")) },
                                onClick = {
                                    onSelectRingtone("default")
                                    ringtoneMenuOpen = false
                                }
                            )
                            HorizontalDivider()
                            GymModelHelpers.BUILTIN_RINGTONES.forEach { rt ->
                                DropdownMenuItem(
                                    text = { Text(rt.name) },
                                    onClick = {
                                        onSelectRingtone(rt.id)
                                        ringtoneMenuOpen = false
                                    }
                                )
                            }
                            if (customRingtones.isNotEmpty()) {
                                HorizontalDivider()
                                customRingtones.forEach { rt ->
                                    DropdownMenuItem(
                                        text = { Text(rt.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                        onClick = {
                                            onSelectRingtone(rt.id)
                                            ringtoneMenuOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            }
        }
    }
}
