package com.fathan.ironpulse.model

import androidx.compose.runtime.Immutable

@Immutable
data class OfficialProgramTemplate(
    val id: String,
    val titleId: String,
    val titleEn: String,
    val subtitleId: String,
    val subtitleEn: String,
    val badgeId: String,
    val badgeEn: String,
    val daysPerWeek: Int,
    val isFeatured: Boolean = false,
    val buildSchedules: (userId: String) -> Map<DayKey, DaySchedule>
) {
    fun title(lang: AppLanguage): String = if (lang == AppLanguage.EN) titleEn else titleId
    fun subtitle(lang: AppLanguage): String = if (lang == AppLanguage.EN) subtitleEn else subtitleId
    fun badge(lang: AppLanguage): String = if (lang == AppLanguage.EN) badgeEn else badgeId
}

@Immutable
data class SavedCustomSchedule(
    val id: String,
    val name: String,
    val isAutoBackup: Boolean = false,
    val updatedAtMs: Long = 0L,
    val schedules: Map<DayKey, DaySchedule> = emptyMap()
) {
    val gymDaysCount: Int
        get() = DayKey.entries.count { schedules[it]?.isGymDay == true }
}

@Immutable
data class ScheduleLibraryState(
    val savedSchedules: List<SavedCustomSchedule> = emptyList(),
    val activeProfileId: String? = null,
    val activeProfileName: String? = null
)

object ScheduleProgramMerger {

    private val TIME_REGEX = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")

    fun mergeWithExistingAlarms(
        target: Map<DayKey, DaySchedule>,
        existing: Map<DayKey, DaySchedule>,
        preserveAlarmsAndRingtones: Boolean,
        userId: String
    ): Map<DayKey, DaySchedule> {
        val preferredAlarmTime = existing.values
            .firstOrNull { it.isGymDay && TIME_REGEX.matches(it.alarmTime) }
            ?.alarmTime
            ?: "06:30"
        val userHasAnyAlarmEnabled = existing.values.any { it.isGymDay && it.alarmEnabled }
        val preferredRingtoneId = existing.values
            .firstOrNull { it.isGymDay && it.ringtoneId.isNotBlank() }
            ?.ringtoneId
            ?: "default"

        return DayKey.entries.associateWith { day ->
            val rawTarget = target[day] ?: existing[day] ?: DaySchedule(
                userId = userId,
                dayKey = day,
                isGymDay = false,
                alarmEnabled = false,
                alarmTime = "06:30",
                focusTitle = "Rest Day",
                ringtoneId = "default",
                exercises = emptyList()
            )
            val prev = existing[day]

            val resolvedAlarmTime = if (preserveAlarmsAndRingtones) {
                when {
                    prev != null && TIME_REGEX.matches(prev.alarmTime) -> prev.alarmTime
                    TIME_REGEX.matches(preferredAlarmTime) -> preferredAlarmTime
                    TIME_REGEX.matches(rawTarget.alarmTime) -> rawTarget.alarmTime
                    else -> "06:30"
                }
            } else {
                if (TIME_REGEX.matches(rawTarget.alarmTime)) rawTarget.alarmTime else "06:30"
            }

            val resolvedAlarmEnabled = if (!rawTarget.isGymDay) {
                false
            } else if (preserveAlarmsAndRingtones) {
                when {
                    prev != null && prev.isGymDay -> prev.alarmEnabled
                    existing.isNotEmpty() -> userHasAnyAlarmEnabled
                    else -> rawTarget.alarmEnabled
                }
            } else {
                rawTarget.alarmEnabled
            }

            val resolvedRingtoneId = if (preserveAlarmsAndRingtones) {
                (prev?.ringtoneId?.takeIf { it.isNotBlank() } ?: preferredRingtoneId).take(128)
            } else {
                rawTarget.ringtoneId.trim().take(128).ifEmpty { "default" }
            }

            val safeFocus = rawTarget.focusTitle.trim().take(80).ifEmpty {
                if (rawTarget.isGymDay) "Sesi Latihan" else "Rest Day"
            }

            val safeExercises = rawTarget.exercises
                .take(30)
                .map { GymModelHelpers.sanitizeExerciseItem(it) }

            DaySchedule(
                userId = userId,
                dayKey = day,
                isGymDay = rawTarget.isGymDay,
                alarmEnabled = resolvedAlarmEnabled,
                alarmTime = resolvedAlarmTime,
                focusTitle = safeFocus,
                ringtoneId = resolvedRingtoneId,
                exercises = safeExercises
            )
        }
    }

    fun schedulesStructurallyEqual(
        a: Map<DayKey, DaySchedule>,
        b: Map<DayKey, DaySchedule>,
        ignoreAlarms: Boolean = false
    ): Boolean {
        if (a.isEmpty() || b.isEmpty()) return false
        return DayKey.entries.all { day ->
            val sa = a[day] ?: return@all false
            val sb = b[day] ?: return@all false
            if (sa.isGymDay != sb.isGymDay) return@all false
            if (sa.focusTitle.trim() != sb.focusTitle.trim()) return@all false
            if (!ignoreAlarms) {
                if (sa.alarmEnabled != sb.alarmEnabled) return@all false
                if (sa.alarmTime != sb.alarmTime) return@all false
                if (sa.ringtoneId != sb.ringtoneId) return@all false
            }
            if (sa.exercises.size != sb.exercises.size) return@all false
            sa.exercises.indices.all { idx ->
                val ea = sa.exercises[idx]
                val eb = sb.exercises[idx]
                ea.name == eb.name &&
                    ea.muscleGroup == eb.muscleGroup &&
                    ea.targetSets == eb.targetSets &&
                    ea.targetReps == eb.targetReps &&
                    ea.targetWeightKg == eb.targetWeightKg &&
                    ea.restSeconds == eb.restSeconds &&
                    ea.trackingMode == eb.trackingMode &&
                    ea.targetDurationSeconds == eb.targetDurationSeconds
            }
        }
    }
}
