package com.fathan.ironpulse.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.MonthlyMedalEvaluator
import com.fathan.ironpulse.model.VerificationRejectReason
import com.fathan.ironpulse.model.WorkoutIntegrityEvaluator
import com.fathan.ironpulse.model.WorkoutLog
import com.fathan.ironpulse.ui.components.IronPulseIcon
import com.fathan.ironpulse.ui.components.MonthlyMedalShowcaseCard
import com.fathan.ironpulse.ui.components.ThemedIcon
import com.fathan.ironpulse.ui.components.TutorialSpotlightTarget
import com.fathan.ironpulse.ui.components.staggeredCardEntrance
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.components.tutorialSpotlightTarget
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class CalendarDayCell(
    val dayOfMonth: Int,
    val dateKey: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    state: IronPulseUiState,
    contentPadding: PaddingValues,
    onStartTodayWorkout: () -> Unit,
    listState: LazyListState = rememberLazyListState(),
    landingBurstTick: Int = 0,
    onMedalSlotPositioned: (Offset) -> Unit = {}
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val tabActive = LocalTabActive.current
    val todayCal = remember(state.todayDateKey) { parseDateKeyToCalendar(state.todayDateKey) }
    val currentYear = todayCal.get(Calendar.YEAR)
    val currentMonth = todayCal.get(Calendar.MONTH)

    var viewYear by remember(currentYear, currentMonth) { mutableIntStateOf(currentYear) }
    var viewMonth by remember(currentYear, currentMonth) { mutableIntStateOf(currentMonth) }
    var selectedDateKey by remember(state.todayDateKey) { mutableStateOf(state.todayDateKey) }

    val mergedLogs = remember(state.workoutHistory, state.todayLog) {
        val byDate = LinkedHashMap<String, WorkoutLog>(state.workoutHistory.size + 1)
        state.workoutHistory.forEach { byDate[it.dateKey] = it }
        val today = state.todayLog
        if (today != null) {
            val existing = byDate[today.dateKey]
            val todayDone = today.entries.sumOf { e -> e.sets.count { it.completed } }
            val existingDone = existing?.entries?.sumOf { e -> e.sets.count { it.completed } } ?: -1
            if (existing == null || todayDone >= existingDone) {
                byDate[today.dateKey] = today
            }
        }
        byDate.values.toList()
    }

    val logsByDate = remember(mergedLogs) {
        mergedLogs.associateBy { it.dateKey }
    }

    val monthPrefix = remember(viewYear, viewMonth) {
        String.format(Locale.US, "%04d-%02d", viewYear, viewMonth + 1)
    }

    val completedSessions = remember(mergedLogs) {
        mergedLogs.count { log ->
            log.completed || log.entries.any { entry -> entry.sets.any { it.completed } }
        }
    }
    val weeklyStreak = state.weeklyStreak
    val monthlyVolumeKg = remember(mergedLogs, monthPrefix) {
        mergedLogs
            .filter { it.dateKey.startsWith(monthPrefix) }
            .sumOf { log ->
                log.entries.filter { it.usesWeight && it.trackingMode == ExerciseTrackingMode.REPS }
                    .sumOf { entry ->
                        entry.sets.filter { it.completed }.sumOf { it.weightKg * it.reps }
                    }
            }
    }
    val formattedMonthlyVolume = remember(monthlyVolumeKg) {
        when {
            monthlyVolumeKg >= 1000.0 -> String.format(Locale.US, "%.1ft", monthlyVolumeKg / 1000.0)
            else -> "${NumberFormat.getNumberInstance(Locale.US).format(monthlyVolumeKg.toInt())} kg"
        }
    }

    val monthTitle = remember(viewYear, viewMonth, lang) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, viewYear)
            set(Calendar.MONTH, viewMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val loc = if (lang == AppLanguage.EN) Locale.US else Locale.forLanguageTag("id-ID")
        SimpleDateFormat("MMMM yyyy", loc).format(cal.time)
    }

    val viewedMonthMedal = remember(
        monthPrefix,
        mergedLogs,
        state.currentMonthMedal,
        state.todayDateKey,
        state.liveSessionElapsedSeconds
    ) {
        val vmMedal = state.currentMonthMedal
        if (vmMedal != null && vmMedal.monthKey == monthPrefix) {
            vmMedal
        } else {
            MonthlyMedalEvaluator.summarizeMonth(
                monthKey = monthPrefix,
                logs = mergedLogs,
                todayDateKey = state.todayDateKey,
                liveTodayElapsedSec = state.liveSessionElapsedSeconds
            )
        }
    }

    val archiveMedals = remember(mergedLogs, state.todayDateKey, state.liveSessionElapsedSeconds) {
        MonthlyMedalEvaluator.earnedMonthlyMedalsArchive(
            logs = mergedLogs,
            todayDateKey = state.todayDateKey,
            liveTodayElapsedSec = state.liveSessionElapsedSeconds
        )
    }

    val calendarCells = remember(viewYear, viewMonth) {
        buildMonthGrid(viewYear, viewMonth)
    }

    val selectedLog: WorkoutLog? = logsByDate[selectedDateKey]
    val isViewingCurrentMonth = viewYear == currentYear && viewMonth == currentMonth

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
        // 0. Top Monthly Medal Showcase (always at very top of History tab)
        item(key = "history_monthly_medal", contentType = "monthly_medal") {
            MonthlyMedalShowcaseCard(
                summary = viewedMonthMedal,
                monthTitle = monthTitle,
                archiveMedals = archiveMedals,
                landingBurstTick = landingBurstTick,
                onSelectArchiveMonth = { ym ->
                    val parts = ym.split("-")
                    val y = parts.getOrNull(0)?.toIntOrNull()
                    val m = parts.getOrNull(1)?.toIntOrNull()?.minus(1)
                    if (y != null && m != null && m in 0..11) {
                        viewYear = y
                        viewMonth = m
                    }
                },
                onMedalSlotPositioned = onMedalSlotPositioned,
                modifier = Modifier.staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
            )
        }

        // Header Card
        item(key = "history_header", contentType = "header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggeredCardEntrance(1, active = tabActive, disabledByPerf = noAnim)
                    .ironGlassSurface(tokens)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = lang.t("Riwayat & Kalender Latihan", "History & Workout Calendar"),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = lang.t(
                        "Ketuk tanggal untuk melihat set, beban & durasi.",
                        "Tap a date to inspect sets, load & duration."
                    ),
                    fontSize = 12.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        }

        // Loading skeleton vs KPI & Calendar
        if (state.historyLoading && state.workoutHistory.isEmpty()) {
            item(key = "history_loading", contentType = "loading") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(2, active = tabActive, disabledByPerf = noAnim)
                        .ironGlassSurface(tokens)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .ironTonalControl(
                                    tokens = tokens,
                                    shape = RoundedCornerShape(12.dp),
                                    baseColor = tokens.subtleChipBg,
                                    isAccent = false
                                )
                        )
                    }
                }
            }
        } else {
            // 3 KPI Summary Cards
            item(key = "history_kpis", contentType = "kpis") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(2, active = tabActive, disabledByPerf = noAnim),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HistoryKpiCard(
                        icon = IronPulseIcon.CHECK_CIRCLE,
                        label = lang.t("Sesi Selesai", "Sessions"),
                        value = "$completedSessions",
                        accent = tokens.accentTextColor,
                        modifier = Modifier.weight(1f)
                    )
                    HistoryKpiCard(
                        icon = IronPulseIcon.FLAME,
                        label = lang.t("Streak Mgg", "Wk Streak"),
                        value = "$weeklyStreak${lang.t("m", "w")}",
                        accent = tokens.warningColor,
                        modifier = Modifier.weight(1f)
                    )
                    HistoryKpiCard(
                        icon = IronPulseIcon.EXERCISES,
                        label = lang.t("Vol. Bulan", "Mo. Vol"),
                        value = formattedMonthlyVolume,
                        accent = tokens.infoColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Interactive Month Calendar Card
            item(key = "history_calendar", contentType = "calendar") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(3, active = tabActive, disabledByPerf = noAnim)
                        .tutorialSpotlightTarget(TutorialSpotlightTarget.HISTORY_CALENDAR)
                        .ironGlassSurface(tokens)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (viewMonth == 0) {
                                    viewMonth = 11
                                    viewYear -= 1
                                } else {
                                    viewMonth -= 1
                                }
                            },
                            modifier = Modifier.tactilePress(disabledByPerf = noAnim)
                        ) {
                            ThemedIcon(IronPulseIcon.CHEVRON_LEFT, contentDescription = "Prev", tint = tokens.palette.onSurface, size = 20.dp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = monthTitle,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = tokens.palette.onSurface
                            )
                            if (!isViewingCurrentMonth) {
                                Text(
                                    text = lang.t("Kembali ke Bulan Ini", "Back to This Month"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.accentTextColor,
                                    modifier = Modifier
                                        .tactilePress(disabledByPerf = noAnim)
                                        .clip(RoundedCornerShape(999.dp))
                                        .clickable {
                                            viewYear = currentYear
                                            viewMonth = currentMonth
                                            selectedDateKey = state.todayDateKey
                                        }
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (viewMonth == 11) {
                                    viewMonth = 0
                                    viewYear += 1
                                } else {
                                    viewMonth += 1
                                }
                            },
                            modifier = Modifier.tactilePress(disabledByPerf = noAnim)
                        ) {
                            ThemedIcon(IronPulseIcon.CHEVRON_RIGHT, contentDescription = "Next", tint = tokens.palette.onSurface, size = 20.dp)
                        }
                    }

                    // Weekday Headers (Mon..Sun)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DayKey.entries.forEach { dk ->
                            Text(
                                text = dk.shortLabel(lang),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.palette.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Calendar Grid (Chunked in rows of 7)
                    val rows = calendarCells.chunked(7)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        rows.forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                week.forEach { cell ->
                                    if (cell == null) {
                                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    } else {
                                        val log = logsByDate[cell.dateKey]
                                        val doneSets = log?.entries?.sumOf { e -> e.sets.count { it.completed } } ?: 0
                                        val dayVerif = viewedMonthMedal.dayVerifications[cell.dateKey]
                                        val isMedalVerified = dayVerif?.isVerified == true
                                        val isShortLog = log != null && !isMedalVerified && doneSets > 0
                                        val isToday = cell.dateKey == state.todayDateKey
                                        val isSelected = cell.dateKey == selectedDateKey

                                        val bg = when {
                                            isMedalVerified -> tokens.accentColor.copy(alpha = if (tokens.isLightTheme) 0.16f else 0.22f)
                                            isShortLog -> tokens.infoColor.copy(alpha = 0.16f)
                                            isToday -> tokens.subtleChipBg
                                            else -> tokens.wellBg
                                        }
                                        val border = when {
                                            isSelected -> tokens.accentColor
                                            isToday -> tokens.palette.onSurface.copy(alpha = 0.45f)
                                            isMedalVerified -> tokens.accentColor.copy(alpha = 0.52f)
                                            isShortLog -> tokens.infoColor.copy(alpha = 0.48f)
                                            else -> tokens.subtleBorderColor
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .tactilePress(pressedScale = 0.90f, disabledByPerf = noAnim)
                                                .ironTonalControl(
                                                    tokens = tokens,
                                                    shape = RoundedCornerShape(10.dp),
                                                    baseColor = bg,
                                                    isAccent = isMedalVerified
                                                )
                                                .border(if (isSelected) 2.dp else 1.dp, border, RoundedCornerShape(10.dp))
                                                .clickable { selectedDateKey = cell.dateKey },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "${cell.dayOfMonth}",
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected || isToday || isMedalVerified) FontWeight.ExtraBold else FontWeight.Medium,
                                                    color = when {
                                                        isMedalVerified -> tokens.accentTextColor
                                                        isShortLog -> tokens.infoColor
                                                        else -> tokens.palette.onSurface
                                                    }
                                                )
                                                if (isMedalVerified || isShortLog) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .size(5.dp)
                                                            .clip(CircleShape)
                                                            .background(if (isMedalVerified) tokens.accentColor else tokens.infoColor)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Legend Row
                    HorizontalDivider(color = tokens.dividerColor)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CalendarLegendItem(tokens.accentTextColor, lang.t("Sah Medali", "Medal Verified"))
                        CalendarLegendItem(tokens.infoColor, lang.t("Log Singkat", "Short Log"))
                        CalendarLegendItem(tokens.palette.onSurface.copy(alpha = 0.55f), lang.t("Hari Ini", "Today"))
                    }
                }
            }

            // Selected Date Session Detail
            item(key = "history_detail", contentType = "detail") {
                val readableDate = remember(selectedDateKey, lang) {
                    GymModelHelpers.formatReadableDate(selectedDateKey, lang)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredCardEntrance(4, active = tabActive, disabledByPerf = noAnim)
                        .ironGlassSurface(tokens)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            ThemedIcon(
                                icon = IronPulseIcon.SCHEDULE,
                                contentDescription = null,
                                tint = tokens.accentColor,
                                size = 18.dp
                            )
                            Text(
                                text = readableDate,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.palette.onSurface
                            )
                        }

                        if (selectedLog != null) {
                            val totalSets = selectedLog.entries.sumOf { it.sets.size }
                            val doneSets = selectedLog.entries.sumOf { e -> e.sets.count { it.completed } }
                            val full = selectedLog.completed || (totalSets > 0 && doneSets == totalSets)
                            IronPulseTonalSurface(
                                shape = RoundedCornerShape(999.dp),
                                baseColor = if (full) tokens.accentColor.copy(alpha = 0.16f) else tokens.infoColor.copy(alpha = 0.16f),
                                border = BorderStroke(
                                    1.dp,
                                    if (full) tokens.accentColor.copy(alpha = 0.45f) else tokens.infoColor.copy(alpha = 0.45f)
                                )
                            ) {
                                Text(
                                    text = if (full) lang.t("Selesai Penuh", "Completed") else "$doneSets/$totalSets set",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (full) tokens.accentTextColor else tokens.infoColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (selectedLog == null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .ironGlassSurface(tokens, subtle = true)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = lang.t(
                                    "Tidak ada catatan latihan pada tanggal ini.",
                                    "No workout session recorded for this date."
                                ),
                                fontSize = 13.sp,
                                color = tokens.palette.onSurfaceVariant
                            )
                            if (selectedDateKey == state.todayDateKey) {
                                IronPulseButton(
                                    onClick = onStartTodayWorkout,
                                    shape = tokens.buttonShape,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = tokens.accentColor,
                                        contentColor = tokens.onAccentColor
                                    )
                                ) {
                                    ThemedIcon(IronPulseIcon.PLAY, contentDescription = null, tint = tokens.onAccentColor, size = 18.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = lang.t("Mulai Catat Latihan Hari Ini", "Start Logging Today"),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        val totalVolume = selectedLog.entries
                            .filter { it.usesWeight && it.trackingMode == ExerciseTrackingMode.REPS }
                            .sumOf { entry -> entry.sets.filter { it.completed }.sumOf { it.weightKg * it.reps } }
                        val totalTimedSeconds = selectedLog.entries
                            .filter { it.trackingMode == ExerciseTrackingMode.TIMER }
                            .sumOf { entry -> entry.sets.filter { it.completed }.sumOf { it.durationSeconds } }
                        val doneSets = selectedLog.entries.sumOf { e -> e.sets.count { it.completed } }
                        val totalSets = selectedLog.entries.sumOf { it.sets.size }

                        val selectedVerif = remember(
                            selectedLog,
                            selectedDateKey,
                            viewedMonthMedal,
                            state.todayDateKey,
                            state.liveSessionElapsedSeconds
                        ) {
                            viewedMonthMedal.dayVerifications[selectedDateKey]
                                ?: WorkoutIntegrityEvaluator.evaluate(
                                    log = selectedLog,
                                    liveElapsedSecondsOverride = if (selectedDateKey == state.todayDateKey) state.liveSessionElapsedSeconds else 0
                                )
                        }

                        Text(
                            text = selectedLog.focusTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tokens.palette.onSurface
                        )

                        // Session Verification Transparency Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .ironTonalControl(
                                    tokens = tokens,
                                    shape = RoundedCornerShape(10.dp),
                                    baseColor = if (selectedVerif.isVerified) {
                                        tokens.accentColor.copy(alpha = 0.14f)
                                    } else {
                                        tokens.infoColor.copy(alpha = 0.12f)
                                    },
                                    isAccent = selectedVerif.isVerified
                                )
                                .border(
                                    1.dp,
                                    if (selectedVerif.isVerified) tokens.accentColor.copy(alpha = 0.45f) else tokens.infoColor.copy(alpha = 0.40f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemedIcon(
                                icon = if (selectedVerif.isVerified) IronPulseIcon.CHECK_CIRCLE else IronPulseIcon.INFO,
                                contentDescription = null,
                                tint = if (selectedVerif.isVerified) tokens.accentTextColor else tokens.infoColor,
                                size = 15.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedVerif.statusDescription(lang),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedVerif.isVerified) tokens.accentTextColor else tokens.palette.onSurface
                                )
                                if (selectedVerif.fastTappedSets > 0) {
                                    Text(
                                        text = lang.t(
                                            "${selectedVerif.fastTappedSets} set terlalu cepat (<20 dtk / lewat batas bypass) tidak dihitung ke medali.",
                                            "${selectedVerif.fastTappedSets} fast-tapped sets (<20s / over bypass limit) excluded from medal."
                                        ),
                                        fontSize = 10.5.sp,
                                        color = tokens.palette.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SessionStatPill(
                                icon = IronPulseIcon.CHECK_CIRCLE,
                                text = "$doneSets/$totalSets set",
                                modifier = Modifier.weight(1f)
                            )
                            if (totalVolume > 0) {
                                SessionStatPill(
                                    icon = IronPulseIcon.EXERCISES,
                                    text = "${NumberFormat.getNumberInstance(Locale.US).format(totalVolume.toInt())} kg",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (totalTimedSeconds > 0) {
                                SessionStatPill(
                                    icon = IronPulseIcon.TIMER,
                                    text = GymModelHelpers.formatCompactDuration(totalTimedSeconds, lang),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            selectedLog.entries.forEach { entry ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .ironGlassSurface(tokens, subtle = true)
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = entry.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.palette.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = entry.muscleGroup.label(lang),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.accentTextColor
                                        )
                                    }

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        entry.sets.forEach { s ->
                                            val label = when {
                                                !s.completed -> "S${s.setNumber}: ${lang.t("Belum", "Pending")}"
                                                entry.trackingMode == ExerciseTrackingMode.TIMER ->
                                                    "S${s.setNumber}: ${GymModelHelpers.formatCompactDuration(s.durationSeconds, lang)}"
                                                entry.usesWeight ->
                                                    "S${s.setNumber}: ${GymModelHelpers.formatWeight(s.weightKg)}kg × ${s.reps}"
                                                else ->
                                                    "S${s.setNumber}: ${s.reps} rep"
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .ironTonalControl(
                                                        tokens = tokens,
                                                        shape = RoundedCornerShape(8.dp),
                                                        baseColor = if (s.completed) tokens.accentColor.copy(alpha = 0.14f) else tokens.wellBg,
                                                        isAccent = false
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (s.completed) tokens.accentColor.copy(alpha = 0.45f)
                                                        else tokens.subtleBorderColor,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (s.completed) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (s.completed) tokens.accentTextColor else tokens.palette.onSurfaceVariant
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
    }
    }
}

@Composable
private fun HistoryKpiCard(
    icon: IronPulseIcon,
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    Column(
        modifier = modifier
            .ironGlassSurface(tokens)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ThemedIcon(icon = icon, contentDescription = null, tint = accent, size = 20.dp, filledBadge = true)
        Text(
            text = value,
            fontFamily = tokens.fontPack.monoFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = tokens.palette.onSurface
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = tokens.palette.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun CalendarLegendItem(
    color: Color,
    label: String
) {
    val tokens = LocalIronPulseTokens.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 11.sp, color = tokens.palette.onSurfaceVariant)
    }
}

@Composable
private fun SessionStatPill(
    icon: IronPulseIcon,
    text: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    Row(
        modifier = modifier
            .ironTonalControl(
                tokens = tokens,
                shape = RoundedCornerShape(10.dp),
                baseColor = tokens.wellBg,
                isAccent = false
            )
            .border(1.dp, tokens.subtleBorderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ThemedIcon(icon = icon, contentDescription = null, tint = tokens.accentColor, size = 14.dp)
        Text(
            text = text,
            fontFamily = tokens.fontPack.monoFont,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = tokens.palette.onSurface
        )
    }
}

private fun buildMonthGrid(year: Int, month: Int): List<CalendarDayCell?> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDow = cal.get(Calendar.DAY_OF_WEEK) // Sun=1, Mon=2 .. Sat=7
    val leadingBlanks = if (firstDow == Calendar.SUNDAY) 6 else firstDow - Calendar.MONDAY

    val cells = mutableListOf<CalendarDayCell?>()
    repeat(leadingBlanks) { cells.add(null) }
    for (day in 1..daysInMonth) {
        val dateKey = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
        cells.add(CalendarDayCell(day, dateKey))
    }
    while (cells.size % 7 != 0) {
        cells.add(null)
    }
    return cells
}

private fun parseDateKeyToCalendar(dateKey: String): Calendar {
    val parts = dateKey.trim().split("-")
    val year = parts.getOrNull(0)?.toIntOrNull()
    val monthOneBased = parts.getOrNull(1)?.toIntOrNull()
    val day = parts.getOrNull(2)?.toIntOrNull()
    return Calendar.getInstance().apply {
        if (year != null && monthOneBased != null && monthOneBased in 1..12 && day != null && day in 1..31) {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthOneBased - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }
    }
}
