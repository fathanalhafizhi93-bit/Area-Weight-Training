package com.fathan.ironpulse.model

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

enum class MedalTier(
    val wireValue: String,
    val thresholdDays: Int,
    val titleId: String,
    val titleEn: String,
    val shortId: String,
    val shortEn: String
) {
    NONE(
        wireValue = "none",
        thresholdDays = 0,
        titleId = "Belum Ada Medali",
        titleEn = "No Medal Yet",
        shortId = "Proses",
        shortEn = "Progress"
    ),
    BRONZE(
        wireValue = "bronze",
        thresholdDays = 12,
        titleId = "Medali Perunggu",
        titleEn = "Bronze Medal",
        shortId = "Perunggu",
        shortEn = "Bronze"
    ),
    SILVER(
        wireValue = "silver",
        thresholdDays = 16,
        titleId = "Medali Perak",
        titleEn = "Silver Medal",
        shortId = "Perak",
        shortEn = "Silver"
    ),
    GOLD(
        wireValue = "gold",
        thresholdDays = 20,
        titleId = "Medali Emas",
        titleEn = "Gold Medal",
        shortId = "Emas",
        shortEn = "Gold"
    );

    fun title(lang: AppLanguage): String = if (lang == AppLanguage.EN) titleEn else titleId
    fun shortLabel(lang: AppLanguage): String = if (lang == AppLanguage.EN) shortEn else shortId

    fun nextTier(): MedalTier? = when (this) {
        NONE -> BRONZE
        BRONZE -> SILVER
        SILVER -> GOLD
        GOLD -> null
    }

    companion object {
        fun fromVerifiedDays(days: Int): MedalTier = when {
            days >= GOLD.thresholdDays -> GOLD
            days >= SILVER.thresholdDays -> SILVER
            days >= BRONZE.thresholdDays -> BRONZE
            else -> NONE
        }

        fun fromWire(raw: String?): MedalTier =
            entries.firstOrNull { it.wireValue == raw } ?: NONE
    }
}

enum class VerificationRejectReason {
    NO_COMPLETED_SETS,
    INSUFFICIENT_VOLUME,
    INSUFFICIENT_DURATION,
    SERVER_DATE_MISMATCH
}

@Immutable
data class SessionVerificationResult(
    val isVerified: Boolean,
    val verifiedExercises: Int,
    val verifiedSets: Int,
    val totalCompletedSets: Int,
    val fastTappedSets: Int,
    val verifiedTimerSeconds: Int,
    val effectiveDurationSeconds: Int,
    val serverDateValid: Boolean,
    val rejectReason: VerificationRejectReason?
) {
    fun statusDescription(lang: AppLanguage): String = when {
        isVerified -> lang.t(
            "Terverifikasi Medali · $verifiedSets set sah · ${effectiveDurationSeconds / 60} mnt",
            "Medal Verified · $verifiedSets valid sets · ${effectiveDurationSeconds / 60} min"
        )
        rejectReason == VerificationRejectReason.SERVER_DATE_MISMATCH -> lang.t(
            "Tanggal log tidak cocok dengan waktu server",
            "Log date does not match server clock"
        )
        rejectReason == VerificationRejectReason.NO_COMPLETED_SETS -> lang.t(
            "Belum ada set selesai",
            "No completed sets yet"
        )
        rejectReason == VerificationRejectReason.INSUFFICIENT_VOLUME -> lang.t(
            "Catatan singkat ($verifiedExercises/2 gerakan, $verifiedSets/6 set sah)",
            "Short log ($verifiedExercises/2 exercises, $verifiedSets/6 valid sets)"
        )
        else -> {
            val mins = effectiveDurationSeconds / 60
            lang.t(
                "Catatan singkat (durasi ${mins}/15 mnt minimal)",
                "Short log (${mins}/15 min minimum duration)"
            )
        }
    }
}

@Immutable
data class MonthlyMedalSummary(
    val monthKey: String,
    val verifiedDays: Int,
    val shortLogDays: Int,
    val tier: MedalTier,
    val nextTier: MedalTier?,
    val daysToNextTier: Int,
    val dayVerifications: Map<String, SessionVerificationResult> = emptyMap()
)

@Immutable
data class MedalUnlockEvent(
    val monthKey: String,
    val tier: MedalTier,
    val verifiedDays: Int,
    val isPreview: Boolean = false
)

object WorkoutIntegrityEvaluator {
    const val MIN_EXERCISES = 2
    const val MIN_VERIFIED_SETS = 6
    const val MIN_SESSION_DURATION_SECONDS = 15 * 60 // 15 minutes
    const val MIN_CARDIO_TIMER_SECONDS = 15 * 60   // 15 minutes
    const val MAX_COOLDOWN_BYPASSES_PER_SESSION = 2
    const val SET_COOLDOWN_SECONDS = 30
    const val FAST_TAP_MIN_GAP_SECONDS = 20

    private val dateKeyParser = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
        }
    }

    fun evaluate(
        log: WorkoutLog,
        liveElapsedSecondsOverride: Int = 0
    ): SessionVerificationResult {
        var totalCompletedSets = 0
        var verifiedSets = 0
        var fastTappedSets = 0
        var verifiedTimerSeconds = 0
        val verifiedExerciseIds = mutableSetOf<String>()

        var minCompletedEpoch = Long.MAX_VALUE
        var maxCompletedEpoch = 0L

        for (entry in log.entries) {
            for (s in entry.sets) {
                if (!s.completed) continue
                totalCompletedSets++
                if (s.completedAtEpochMs > 0L) {
                    if (s.completedAtEpochMs < minCompletedEpoch) minCompletedEpoch = s.completedAtEpochMs
                    if (s.completedAtEpochMs > maxCompletedEpoch) maxCompletedEpoch = s.completedAtEpochMs
                }
                if (s.medalVerified) {
                    verifiedSets++
                    verifiedExerciseIds += entry.exerciseId
                    if (entry.trackingMode == ExerciseTrackingMode.TIMER) {
                        verifiedTimerSeconds += s.durationSeconds.coerceAtLeast(0)
                    }
                } else {
                    fastTappedSets++
                }
            }
        }

        if (totalCompletedSets == 0) {
            return SessionVerificationResult(
                isVerified = false,
                verifiedExercises = 0,
                verifiedSets = 0,
                totalCompletedSets = 0,
                fastTappedSets = 0,
                verifiedTimerSeconds = 0,
                effectiveDurationSeconds = 0,
                serverDateValid = true,
                rejectReason = VerificationRejectReason.NO_COMPLETED_SETS
            )
        }

        // Compute most tamper-resistant session duration:
        // 1) Monotonic elapsed seconds persisted or ticked live
        // 2) Cross-checked server span if timestamps exist
        // 3) Client set-to-set epoch span fallback (only if within a single realistic window)
        val serverSpanSec = if (log.serverCreatedAtMs > 0L && log.serverUpdatedAtMs > log.serverCreatedAtMs) {
            ((log.serverUpdatedAtMs - log.serverCreatedAtMs) / 1000L).toInt().coerceIn(0, 8 * 3600)
        } else 0
        val epochSpanSec = if (minCompletedEpoch < Long.MAX_VALUE && maxCompletedEpoch > minCompletedEpoch) {
            ((maxCompletedEpoch - minCompletedEpoch) / 1000L).toInt().coerceIn(0, 8 * 3600)
        } else 0

        val effectiveDurationSec = maxOf(
            log.activeDurationSeconds.coerceAtLeast(0),
            liveElapsedSecondsOverride.coerceAtLeast(0),
            serverSpanSec,
            epochSpanSec
        )

        val serverDateValid = isServerDateConsistent(log.dateKey, log.serverCreatedAtMs)
        val volumeMet = (verifiedExerciseIds.size >= MIN_EXERCISES && verifiedSets >= MIN_VERIFIED_SETS) ||
            (verifiedTimerSeconds >= MIN_CARDIO_TIMER_SECONDS && verifiedSets >= 1)
        val durationMet = effectiveDurationSec >= MIN_SESSION_DURATION_SECONDS

        val rejectReason = when {
            !serverDateValid -> VerificationRejectReason.SERVER_DATE_MISMATCH
            !volumeMet -> VerificationRejectReason.INSUFFICIENT_VOLUME
            !durationMet -> VerificationRejectReason.INSUFFICIENT_DURATION
            else -> null
        }

        return SessionVerificationResult(
            isVerified = rejectReason == null,
            verifiedExercises = verifiedExerciseIds.size,
            verifiedSets = verifiedSets,
            totalCompletedSets = totalCompletedSets,
            fastTappedSets = fastTappedSets,
            verifiedTimerSeconds = verifiedTimerSeconds,
            effectiveDurationSeconds = effectiveDurationSec,
            serverDateValid = serverDateValid,
            rejectReason = rejectReason
        )
    }

    /**
     * Prevents device clock time-travel farming: dateKey (yyyy-MM-dd) must fall within ±1 calendar day
     * of Firestore server createdAt timestamp (allowing global UTC timezone differences).
     */
    fun isServerDateConsistent(dateKey: String, serverCreatedAtMs: Long): Boolean {
        if (serverCreatedAtMs <= 0L) return true
        val sdf = dateKeyParser.get() ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
        }
        val parsedLocal = runCatching { sdf.parse(dateKey) }.getOrNull() ?: return false
        val localEpochDay = localCalendarEpochDay(parsedLocal)
        val serverEpochDay = localCalendarEpochDay(Date(serverCreatedAtMs))
        return abs(localEpochDay - serverEpochDay) <= 1L
    }

    private fun localCalendarEpochDay(date: Date): Long {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis / 86_400_000L
    }
}

object MonthlyMedalEvaluator {

    fun summarizeMonth(
        monthKey: String,
        logs: List<WorkoutLog>,
        todayDateKey: String = "",
        liveTodayElapsedSec: Int = 0
    ): MonthlyMedalSummary {
        val monthLogs = logs
            .filter { it.dateKey.startsWith(monthKey) }
            .distinctBy { it.dateKey }

        // Anti-clock-jump deduplication: if two different dateKeys share a serverCreatedAt day
        // within 8 hours of each other while having different dateKeys, keep the closest matching one.
        val verifications = LinkedHashMap<String, SessionVerificationResult>(monthLogs.size)
        val seenServerSlots = mutableSetOf<Long>()

        for (log in monthLogs.sortedBy { it.dateKey }) {
            val overrideDur = if (log.dateKey == todayDateKey) liveTodayElapsedSec else 0
            var result = WorkoutIntegrityEvaluator.evaluate(log, liveElapsedSecondsOverride = overrideDur)
            if (result.isVerified && log.serverCreatedAtMs > 0L) {
                val serverBucket8h = log.serverCreatedAtMs / (8L * 3600_000L)
                // Only reject duplicate if another dateKey already claimed this exact 8h server window
                // and this log's dateKey doesn't match server local date
                val exactServerDay = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(log.serverCreatedAtMs))
                if (log.dateKey != exactServerDay && !seenServerSlots.add(serverBucket8h)) {
                    result = result.copy(
                        isVerified = false,
                        serverDateValid = false,
                        rejectReason = VerificationRejectReason.SERVER_DATE_MISMATCH
                    )
                } else {
                    seenServerSlots.add(serverBucket8h)
                }
            }
            verifications[log.dateKey] = result
        }

        val verifiedDays = verifications.values.count { it.isVerified }
        val shortLogDays = verifications.values.count { !it.isVerified && it.totalCompletedSets > 0 }
        val tier = MedalTier.fromVerifiedDays(verifiedDays)
        val nextTier = tier.nextTier()
        val daysToNext = if (nextTier != null) (nextTier.thresholdDays - verifiedDays).coerceAtLeast(0) else 0

        return MonthlyMedalSummary(
            monthKey = monthKey,
            verifiedDays = verifiedDays,
            shortLogDays = shortLogDays,
            tier = tier,
            nextTier = nextTier,
            daysToNextTier = daysToNext,
            dayVerifications = verifications
        )
    }

    fun earnedMonthlyMedalsArchive(
        logs: List<WorkoutLog>,
        todayDateKey: String = "",
        liveTodayElapsedSec: Int = 0
    ): List<MonthlyMedalSummary> {
        val monthKeys = logs
            .mapNotNull { log -> log.dateKey.takeIf { it.length >= 7 }?.substring(0, 7) }
            .distinct()
            .sortedDescending()

        return monthKeys.mapNotNull { monthKey ->
            val summary = summarizeMonth(
                monthKey = monthKey,
                logs = logs,
                todayDateKey = todayDateKey,
                liveTodayElapsedSec = liveTodayElapsedSec
            )
            summary.takeIf { it.tier != MedalTier.NONE }
        }
    }
}
