package com.fathan.ironpulse.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.data.ProgramTemplatePresets
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.DaySchedule
import com.fathan.ironpulse.model.OfficialProgramTemplate
import com.fathan.ironpulse.model.SavedCustomSchedule
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface

sealed interface SchedulePreviewTarget {
    data class Saved(val profile: SavedCustomSchedule) : SchedulePreviewTarget
    data class Official(val template: OfficialProgramTemplate) : SchedulePreviewTarget
}

@Composable
fun ScheduleTopProgramStrip(
    savedSchedules: List<SavedCustomSchedule>,
    officialTemplates: List<OfficialProgramTemplate> = ProgramTemplatePresets.OFFICIAL_TEMPLATES,
    activeProfileId: String?,
    applyingSchedule: Boolean,
    onOpenSaveDialog: () -> Unit,
    onSelectSaved: (SavedCustomSchedule) -> Unit,
    onSelectOfficial: (OfficialProgramTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_top_program_strip")
            .ironGlassSurface(tokens)
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header Row: Section title + Quick Save button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = lang.t("JADWAL TERSIMPAN & RESMI", "SAVED & OFFICIAL SCHEDULES"),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.3.sp,
                    color = tokens.accentColor
                )
                Text(
                    text = lang.t(
                        "Ganti program atau kembali ke jadwal sebelumnya",
                        "Switch programs or return to a previous schedule"
                    ),
                    fontSize = 12.sp,
                    color = tokens.palette.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Surface(
                shape = RoundedCornerShape(tokens.buttonRadius),
                color = tokens.accentColor.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.42f)),
                modifier = Modifier
                    .testTag("save_custom_schedule_button")
                    .heightIn(min = 48.dp)
                    .tactilePress(enabled = !applyingSchedule, disabledByPerf = noAnim)
                    .clip(RoundedCornerShape(tokens.buttonRadius))
                    .clickable(enabled = !applyingSchedule) { onOpenSaveDialog() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ThemedIcon(
                        icon = IronPulseIcon.ADD,
                        contentDescription = lang.t("Simpan jadwal saat ini", "Save current schedule"),
                        tint = tokens.accentColor,
                        size = 16.dp
                    )
                    Text(
                        text = lang.t("Simpan", "Save"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.accentColor
                    )
                }
            }
        }

        // Horizontal strip of Saved Custom Schedules + Official Templates
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("schedule_program_strip_row")
        ) {
            items(savedSchedules, key = { "saved_${it.id}" }) { saved ->
                val isActive = activeProfileId == saved.id
                ScheduleStripCard(
                    title = saved.name,
                    subtitle = if (saved.isAutoBackup) {
                        lang.t("Cadangan otomatis", "Auto backup")
                    } else {
                        lang.t("Jadwal Kustom", "Custom Schedule")
                    },
                    badgeText = if (isActive) {
                        lang.t("AKTIF", "ACTIVE")
                    } else if (saved.isAutoBackup) {
                        lang.t("Sebelumnya", "Previous")
                    } else {
                        lang.t("Tersimpan", "Saved")
                    },
                    daysBadge = "${saved.gymDaysCount}/7 ${lang.t("Hari", "Days")}",
                    gymDayFlags = DayKey.entries.map { saved.schedules[it]?.isGymDay == true },
                    isActive = isActive,
                    isOfficial = false,
                    isAutoBackup = saved.isAutoBackup,
                    enabled = !applyingSchedule,
                    testTagName = "schedule_card_${saved.id}",
                    onClick = { onSelectSaved(saved) }
                )
            }

            items(officialTemplates, key = { "official_${it.id}" }) { template ->
                val isActive = activeProfileId == template.id
                val previewDays = remember(template.id) { template.buildSchedules("preview") }
                ScheduleStripCard(
                    title = template.title(lang),
                    subtitle = template.badge(lang),
                    badgeText = if (isActive) {
                        lang.t("AKTIF", "ACTIVE")
                    } else {
                        lang.t("Resmi", "Official")
                    },
                    daysBadge = "${template.daysPerWeek}/7 ${lang.t("Hari", "Days")}",
                    gymDayFlags = DayKey.entries.map { previewDays[it]?.isGymDay == true },
                    isActive = isActive,
                    isOfficial = true,
                    isAutoBackup = false,
                    enabled = !applyingSchedule,
                    testTagName = "schedule_card_${template.id}",
                    onClick = { onSelectOfficial(template) }
                )
            }
        }
    }
}

@Composable
private fun ScheduleStripCard(
    title: String,
    subtitle: String,
    badgeText: String,
    daysBadge: String,
    gymDayFlags: List<Boolean>,
    isActive: Boolean,
    isOfficial: Boolean,
    isAutoBackup: Boolean,
    enabled: Boolean,
    testTagName: String,
    onClick: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    val borderCol = when {
        isActive -> tokens.accentColor
        isOfficial -> tokens.surfaceBorderColor
        else -> tokens.phaseTokens.accent.copy(alpha = 0.42f)
    }
    val containerCol = when {
        isActive -> tokens.accentColor.copy(alpha = 0.14f)
        else -> tokens.wellBg
    }

    val dayInitials = remember(lang) {
        if (lang == AppLanguage.EN) listOf("M", "T", "W", "T", "F", "S", "S")
        else listOf("S", "S", "R", "K", "J", "S", "M")
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerCol,
        border = BorderStroke(if (isActive) 1.5.dp else 1.dp, borderCol),
        modifier = Modifier
            .width(172.dp)
            .testTag(testTagName)
            .tactilePress(enabled = enabled, pressedScale = 0.97f, disabledByPerf = noAnim)
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = enabled) { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = when {
                        isActive -> tokens.accentColor
                        isOfficial -> tokens.accentColor.copy(alpha = 0.14f)
                        else -> tokens.phaseTokens.accent.copy(alpha = 0.16f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        ThemedIcon(
                            icon = when {
                                isActive -> IronPulseIcon.CHECK
                                isOfficial -> IronPulseIcon.SPARKLE
                                isAutoBackup -> IronPulseIcon.HISTORY
                                else -> IronPulseIcon.BOOKMARK
                            },
                            contentDescription = null,
                            tint = when {
                                isActive -> tokens.onAccentColor
                                isOfficial -> tokens.accentColor
                                else -> tokens.phaseTokens.accentBright
                            },
                            size = 11.dp
                        )
                        Text(
                            text = badgeText,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when {
                                isActive -> tokens.onAccentColor
                                isOfficial -> tokens.accentColor
                                else -> tokens.phaseTokens.accentBright
                            }
                        )
                    }
                }

                Text(
                    text = daysBadge,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.mutedTextColor
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = tokens.palette.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 7-day visual mini indicator (Mon..Sun)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                dayInitials.forEachIndexed { idx, letter ->
                    val isGym = gymDayFlags.getOrNull(idx) == true
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (isGym) {
                                    if (isActive) tokens.accentColor else tokens.accentColor.copy(alpha = 0.24f)
                                } else {
                                    tokens.palette.surfaceContainerHigh.copy(alpha = 0.65f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isGym) {
                                if (isActive) tokens.onAccentColor else tokens.accentColor
                            } else {
                                tokens.mutedTextColor.copy(alpha = 0.7f)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SaveCustomScheduleDialog(
    initialName: String = "",
    isRename: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    var nameInput by remember(initialName) { mutableStateOf(initialName) }
    val suggestions = remember(lang) {
        if (lang == AppLanguage.EN) {
            listOf("My Custom Split", "Cutting 5 Days", "Strength Focus")
        } else {
            listOf("Jadwal Kustom Saya", "Program Cutting", "Fokus Kekuatan")
        }
    }

    val noAnim = tokens.performance.noAnimations

    ThemedIronDialog(
        onDismissRequest = onDismiss,
        maxWidth = 440.dp,
        modifier = Modifier.testTag("save_custom_schedule_dialog"),
        title = {
            Text(
                text = if (isRename) {
                    lang.t("Ganti Nama Jadwal", "Rename Schedule")
                } else {
                    lang.t("Simpan Jadwal Saat Ini", "Save Current Schedule")
                },
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tokens.palette.onSurface
            )
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
                    Text(lang.t("Batal", "Cancel"), color = tokens.palette.onSurface)
                }
                Button(
                    onClick = {
                        val cleaned = nameInput.trim().ifEmpty {
                            lang.t("Jadwal Kustom", "Custom Schedule")
                        }
                        onConfirm(cleaned)
                    },
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentColor,
                        contentColor = tokens.onAccentColor
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .heightIn(min = 48.dp)
                        .tactilePress(disabledByPerf = noAnim)
                        .testTag("confirm_save_custom_schedule_button")
                ) {
                    Text(
                        text = lang.t("Simpan", "Save"),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (isRename) {
                    lang.t(
                        "Masukkan nama baru untuk jadwal tersimpan ini.",
                        "Enter a new name for this saved schedule."
                    )
                } else {
                    lang.t(
                        "Simpan seluruh jadwal Senin–Minggu beserta alarm & gerakannya agar Anda bisa kembali kapan saja.",
                        "Save all Monday–Sunday days, alarms, and exercises so you can switch back anytime."
                    )
                },
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = tokens.palette.onSurfaceVariant
            )

            OutlinedTextField(
                value = nameInput,
                onValueChange = { if (it.length <= 48) nameInput = it },
                singleLine = true,
                placeholder = {
                    Text(lang.t("Contoh: Jadwal Kustom Saya", "e.g. My Custom Split"))
                },
                shape = RoundedCornerShape(tokens.inputRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = tokens.accentColor,
                    unfocusedBorderColor = tokens.surfaceBorderColor,
                    focusedTextColor = tokens.palette.onSurface,
                    unfocusedTextColor = tokens.palette.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_schedule_name_input")
            )

            if (!isRename) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = tokens.wellBg,
                            border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { nameInput = preset }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tokens.palette.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleProgramPreviewSheet(
    target: SchedulePreviewTarget,
    isCurrentlyActive: Boolean,
    applying: Boolean,
    onDismiss: () -> Unit,
    onApply: (preserveAlarmsAndRingtones: Boolean) -> Unit,
    onRequestRename: ((SavedCustomSchedule) -> Unit)? = null,
    onDeleteSaved: ((SavedCustomSchedule) -> Unit)? = null
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isOfficial = target is SchedulePreviewTarget.Official
    var preserveAlarms by remember(target) { mutableStateOf(isOfficial) }

    val title = when (target) {
        is SchedulePreviewTarget.Official -> target.template.title(lang)
        is SchedulePreviewTarget.Saved -> target.profile.name
    }
    val subtitle = when (target) {
        is SchedulePreviewTarget.Official -> target.template.subtitle(lang)
        is SchedulePreviewTarget.Saved -> if (target.profile.isAutoBackup) {
            lang.t(
                "Cadangan otomatis dari jadwal Anda sebelumnya. Anda bisa langsung kembali ke jadwal ini kapan saja.",
                "Automatic backup of your previous schedule. You can restore it anytime."
            )
        } else {
            lang.t(
                "Jadwal kustom tersimpan milik Anda beserta konfigurasi gerakan dan alarmnya.",
                "Your saved custom schedule including its exercise routine and alarm setup."
            )
        }
    }
    val previewMap: Map<DayKey, DaySchedule> = remember(target) {
        when (target) {
            is SchedulePreviewTarget.Official -> target.template.buildSchedules("preview")
            is SchedulePreviewTarget.Saved -> target.profile.schedules
        }
    }
    val gymCount = remember(previewMap) {
        DayKey.entries.count { previewMap[it]?.isGymDay == true }
    }

    val sheetShape = when (tokens.uiTheme) {
        UiTheme.CARBON_FORGE -> CutCornerShape(topStart = 12.dp, topEnd = 12.dp)
        UiTheme.AMOLED_STEALTH -> RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        UiTheme.UBUNTU_YARU -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        UiTheme.FRUTIGER_AERO, UiTheme.MATERIAL_YOU -> RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    }
    val sheetBg = when (tokens.uiTheme) {
        UiTheme.AMOLED_STEALTH -> Color(0xFF05070B)
        UiTheme.UBUNTU_YARU -> Color(0xFF2B272D)
        UiTheme.CARBON_FORGE -> tokens.effectiveTintColor
        UiTheme.FRUTIGER_AERO -> if (tokens.isAeroNight) Color(0xFF14274E) else Color(0xFFF2FAFF)
        UiTheme.MATERIAL_YOU -> tokens.palette.surfaceContainerHigh
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = sheetShape,
        containerColor = sheetBg,
        contentColor = tokens.palette.onSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (tokens.uiTheme == UiTheme.UBUNTU_YARU) Color(0xFFE95420).copy(alpha = 0.85f)
                        else tokens.accentColor.copy(alpha = 0.65f)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = tokens.accentColor.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.36f))
                ) {
                    Text(
                        text = when (target) {
                            is SchedulePreviewTarget.Official -> "${lang.t("PROGRAM RESMI", "OFFICIAL PROGRAM")} · $gymCount/7"
                            is SchedulePreviewTarget.Saved -> "${lang.t("JADWAL TERSIMPAN", "SAVED SCHEDULE")} · $gymCount/7"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = tokens.accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (target is SchedulePreviewTarget.Saved) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (onRequestRename != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = tokens.wellBg,
                                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("rename_custom_schedule_button")
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(enabled = !applying) { onRequestRename(target.profile) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    ThemedIcon(
                                        icon = IronPulseIcon.EDIT,
                                        contentDescription = lang.t("Ganti nama jadwal", "Rename schedule"),
                                        tint = tokens.palette.onSurfaceVariant,
                                        size = 18.dp
                                    )
                                }
                            }
                        }
                        if (onDeleteSaved != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.32f)),
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("delete_custom_schedule_button")
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable(enabled = !applying) { onDeleteSaved(target.profile) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    ThemedIcon(
                                        icon = IronPulseIcon.DELETE,
                                        contentDescription = lang.t("Hapus jadwal tersimpan", "Delete saved schedule"),
                                        tint = tokens.dangerColor,
                                        size = 18.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }

            // 7-day preview list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DayKey.entries.forEach { day ->
                    val sched = previewMap[day]
                    val isGym = sched?.isGymDay == true
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isGym) tokens.wellBg else tokens.palette.surfaceContainerLow.copy(alpha = 0.65f),
                        border = BorderStroke(
                            1.dp,
                            if (isGym) tokens.surfaceBorderColor else tokens.surfaceBorderColor.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isGym) tokens.accentColor.copy(alpha = 0.16f) else tokens.wellBg
                                    ) {
                                        Text(
                                            text = day.shortLabel(lang),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isGym) tokens.accentColor else tokens.mutedTextColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = sched?.focusTitle ?: lang.t("Rest Day", "Rest Day"),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGym) tokens.palette.onSurface else tokens.mutedTextColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = if (isGym) {
                                        "${sched?.exercises?.size ?: 0} ${lang.t("gerakan", "ex")}"
                                    } else {
                                        lang.t("Istirahat", "Rest")
                                    },
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isGym) tokens.accentColor else tokens.mutedTextColor
                                )
                            }

                            if (isGym && !sched?.exercises.isNullOrEmpty()) {
                                val summary = sched!!.exercises.joinToString(" · ") { it.name }
                                Text(
                                    text = summary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = tokens.palette.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = tokens.surfaceBorderColor)

            // Option toggle: preserve existing alarms & ringtones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(tokens.wellBg)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.NOTIFICATIONS,
                    contentDescription = null,
                    tint = tokens.accentColor,
                    size = 20.dp
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = lang.t(
                            "Pertahankan jam alarm & ringtone saat ini",
                            "Keep my current alarm times & ringtones"
                        ),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface
                    )
                    Text(
                        text = if (preserveAlarms) {
                            lang.t(
                                "Jam alarm lama tetap dipakai; hari istirahat otomatis nonaktif.",
                                "Current alarm times stay intact; rest days auto-disable."
                            )
                        } else {
                            lang.t(
                                "Gunakan jam alarm bawaan dari jadwal yang dipilih ini.",
                                "Use the alarm times stored with this selected schedule."
                            )
                        },
                        fontSize = 11.sp,
                        color = tokens.palette.onSurfaceVariant
                    )
                }
                Switch(
                    checked = preserveAlarms,
                    onCheckedChange = { preserveAlarms = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = tokens.onAccentColor,
                        checkedTrackColor = tokens.accentColor
                    ),
                    modifier = Modifier.testTag("preserve_alarms_toggle")
                )
            }

            if (!isCurrentlyActive) {
                Text(
                    text = lang.t(
                        "Jadwal aktif Anda saat ini akan diamankan otomatis di strip paling atas agar mudah kembali kapan saja.",
                        "Your current active schedule is automatically backed up in the top strip so you can switch back anytime."
                    ),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = tokens.mutedTextColor
                )
            }

            // Apply CTA
            Button(
                onClick = { onApply(preserveAlarms) },
                enabled = !applying,
                shape = RoundedCornerShape(tokens.buttonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentColor,
                    contentColor = tokens.onAccentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("apply_schedule_program_button")
            ) {
                if (applying) {
                    CircularProgressIndicator(
                        color = tokens.onAccentColor,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lang.t("Menerapkan Jadwal...", "Applying Schedule..."),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = if (isCurrentlyActive) {
                            lang.t("Terapkan Ulang Jadwal Ini", "Re-apply This Schedule")
                        } else {
                            lang.t("Gunakan Jadwal Ini", "Use This Schedule")
                        },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
