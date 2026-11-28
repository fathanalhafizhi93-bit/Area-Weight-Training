package com.fathan.ironpulse.data

import android.content.Context
import com.fathan.ironpulse.model.CustomRingtone
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.DaySchedule
import com.fathan.ironpulse.model.ExerciseItem
import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.MuscleGroup
import com.fathan.ironpulse.model.RingtoneStorageMode
import com.fathan.ironpulse.model.TimerDirection
import com.fathan.ironpulse.model.UserProfile
import com.fathan.ironpulse.model.WorkoutLog
import com.fathan.ironpulse.model.WorkoutLogEntry
import com.fathan.ironpulse.model.WorkoutSetLog
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ConcurrentHashMap

data class GymCachedSnapshot(
    val userInitialized: Boolean = false,
    val cachedAtMs: Long = 0L,
    val profile: UserProfile? = null,
    val schedules: Map<DayKey, DaySchedule> = emptyMap(),
    val customRingtones: List<CustomRingtone> = emptyList(),
    val todayDateKey: String? = null,
    val todayLog: WorkoutLog? = null,
    val workoutHistory: List<WorkoutLog> = emptyList(),
    val weeklyStreak: Int = 0
)

/**
 * Instant disk-backed JSON snapshot store keyed per authenticated Firebase UID.
 * Hydrates ViewModel StateFlows on launch in milliseconds while Firestore syncs silently in the background.
 */
class LocalGymCacheStore(private val context: Context) {

    private val uidLocks = ConcurrentHashMap<String, Any>()

    private fun lockFor(uid: String): Any = uidLocks.computeIfAbsent(uid) { Any() }

    private fun cacheFile(uid: String): File {
        val safeUid = uid.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(80)
        return File(context.filesDir, "ironpulse_cache_v1_$safeUid.json")
    }

    fun loadSnapshot(uid: String): GymCachedSnapshot? {
        if (uid.isBlank()) return null
        return synchronized(lockFor(uid)) {
            runCatching {
                val file = cacheFile(uid)
                if (!file.exists() || !file.isFile) return@runCatching null
                val raw = file.readText(Charsets.UTF_8)
                if (raw.isBlank()) return@runCatching null
                decodeSnapshot(JSONObject(raw), uid)
            }.getOrNull()
        }
    }

    fun saveSnapshot(uid: String, snapshot: GymCachedSnapshot) {
        if (uid.isBlank()) return
        synchronized(lockFor(uid)) {
            runCatching {
                val target = cacheFile(uid)
                val parent = target.parentFile
                if (parent != null && !parent.exists()) parent.mkdirs()
                val tmp = File(parent, "${target.name}.tmp")
                val json = encodeSnapshot(snapshot.copy(cachedAtMs = System.currentTimeMillis())).toString()
                tmp.writeText(json, Charsets.UTF_8)
                if (!tmp.renameTo(target)) {
                    target.writeText(json, Charsets.UTF_8)
                    tmp.delete()
                }
            }
        }
    }

    fun updateSnapshot(
        uid: String,
        transform: (GymCachedSnapshot) -> GymCachedSnapshot
    ) {
        if (uid.isBlank()) return
        synchronized(lockFor(uid)) {
            val current = runCatching {
                val file = cacheFile(uid)
                if (file.exists() && file.isFile) {
                    val raw = file.readText(Charsets.UTF_8)
                    if (raw.isNotBlank()) decodeSnapshot(JSONObject(raw), uid) else null
                } else null
            }.getOrNull() ?: GymCachedSnapshot()

            val updated = transform(current).copy(cachedAtMs = System.currentTimeMillis())
            runCatching {
                val target = cacheFile(uid)
                val parent = target.parentFile
                if (parent != null && !parent.exists()) parent.mkdirs()
                val tmp = File(parent, "${target.name}.tmp")
                val json = encodeSnapshot(updated).toString()
                tmp.writeText(json, Charsets.UTF_8)
                if (!tmp.renameTo(target)) {
                    target.writeText(json, Charsets.UTF_8)
                    tmp.delete()
                }
            }
        }
    }

    fun clearUser(uid: String) {
        if (uid.isBlank()) return
        synchronized(lockFor(uid)) {
            runCatching {
                val f = cacheFile(uid)
                if (f.exists()) f.delete()
            }
        }
    }

    private fun encodeSnapshot(snap: GymCachedSnapshot): JSONObject = JSONObject().apply {
        put("userInitialized", snap.userInitialized)
        put("cachedAtMs", snap.cachedAtMs)
        put("weeklyStreak", snap.weeklyStreak)
        if (snap.todayDateKey != null) {
            put("todayDateKey", snap.todayDateKey)
        }
        snap.profile?.let { put("profile", encodeProfile(it)) }
        if (snap.schedules.isNotEmpty()) {
            put("schedules", JSONArray().apply {
                snap.schedules.values.forEach { put(encodeSchedule(it)) }
            })
        }
        if (snap.customRingtones.isNotEmpty()) {
            put("customRingtones", JSONArray().apply {
                snap.customRingtones.forEach { put(encodeRingtone(it)) }
            })
        }
        snap.todayLog?.let { put("todayLog", encodeWorkoutLog(it)) }
        if (snap.workoutHistory.isNotEmpty()) {
            put("workoutHistory", JSONArray().apply {
                snap.workoutHistory.forEach { put(encodeWorkoutLog(it)) }
            })
        }
    }

    private fun decodeSnapshot(root: JSONObject, uid: String): GymCachedSnapshot {
        val profile = root.optJSONObject("profile")?.let { decodeProfile(it, uid) }
        val schedulesArr = root.optJSONArray("schedules")
        val schedules = mutableMapOf<DayKey, DaySchedule>()
        if (schedulesArr != null) {
            for (i in 0 until schedulesArr.length()) {
                val obj = schedulesArr.optJSONObject(i) ?: continue
                val sched = decodeSchedule(obj, uid)
                schedules[sched.dayKey] = sched
            }
        }
        val tonesArr = root.optJSONArray("customRingtones")
        val tones = mutableListOf<CustomRingtone>()
        if (tonesArr != null) {
            for (i in 0 until tonesArr.length()) {
                val obj = tonesArr.optJSONObject(i) ?: continue
                tones += decodeRingtone(obj, uid)
            }
        }
        val todayLog = root.optJSONObject("todayLog")?.let { decodeWorkoutLog(it, uid) }
        val histArr = root.optJSONArray("workoutHistory")
        val history = mutableListOf<WorkoutLog>()
        if (histArr != null) {
            for (i in 0 until histArr.length()) {
                val obj = histArr.optJSONObject(i) ?: continue
                decodeWorkoutLog(obj, uid)?.let { history += it }
            }
        }
        return GymCachedSnapshot(
            userInitialized = root.optBoolean("userInitialized", false),
            cachedAtMs = root.optLong("cachedAtMs", 0L),
            profile = profile,
            schedules = schedules,
            customRingtones = tones,
            todayDateKey = root.optString("todayDateKey").takeIf { it.isNotBlank() },
            todayLog = todayLog,
            workoutHistory = history,
            weeklyStreak = root.optInt("weeklyStreak", 0)
        )
    }

    private fun encodeProfile(p: UserProfile): JSONObject = JSONObject().apply {
        put("userId", p.userId)
        put("displayName", p.displayName)
        put("email", p.email)
        put("defaultRingtoneId", p.defaultRingtoneId)
        put("restRingtoneId", p.restRingtoneId)
        put("volume", p.volume)
        put("notificationsEnabled", p.notificationsEnabled)
        put("snoozeMinutes", p.snoozeMinutes)
        put("autoStartRestTimer", p.autoStartRestTimer)
    }

    private fun decodeProfile(o: JSONObject, uid: String): UserProfile = UserProfile(
        userId = o.optString("userId", uid).ifBlank { uid },
        displayName = o.optString("displayName", "Atlet RepMate").ifBlank { "Atlet RepMate" },
        email = o.optString("email", ""),
        defaultRingtoneId = o.optString("defaultRingtoneId", "builtin_athletic_bell").ifBlank { "builtin_athletic_bell" },
        restRingtoneId = o.optString("restRingtoneId", "builtin_kettlebell_chime").ifBlank { "builtin_kettlebell_chime" },
        volume = o.optDouble("volume", 0.85).coerceIn(0.0, 1.0),
        notificationsEnabled = o.optBoolean("notificationsEnabled", false),
        snoozeMinutes = o.optInt("snoozeMinutes", 5).coerceIn(1, 60),
        autoStartRestTimer = o.optBoolean("autoStartRestTimer", true)
    )

    private fun encodeSchedule(s: DaySchedule): JSONObject = JSONObject().apply {
        put("userId", s.userId)
        put("dayKey", s.dayKey.key)
        put("isGymDay", s.isGymDay)
        put("alarmEnabled", s.alarmEnabled)
        put("alarmTime", s.alarmTime)
        put("focusTitle", s.focusTitle)
        put("ringtoneId", s.ringtoneId)
        put("exercises", JSONArray().apply {
            s.exercises.forEach { put(encodeExercise(it)) }
        })
    }

    private fun decodeSchedule(o: JSONObject, uid: String): DaySchedule {
        val exArr = o.optJSONArray("exercises")
        val exercises = mutableListOf<ExerciseItem>()
        if (exArr != null) {
            for (i in 0 until exArr.length()) {
                val obj = exArr.optJSONObject(i) ?: continue
                exercises += decodeExercise(obj, i)
            }
        }
        return DaySchedule(
            userId = o.optString("userId", uid).ifBlank { uid },
            dayKey = DayKey.fromKey(o.optString("dayKey")),
            isGymDay = o.optBoolean("isGymDay", false),
            alarmEnabled = o.optBoolean("alarmEnabled", false),
            alarmTime = o.optString("alarmTime", "06:30").ifBlank { "06:30" },
            focusTitle = o.optString("focusTitle", "Sesi Latihan").ifBlank { "Sesi Latihan" },
            ringtoneId = o.optString("ringtoneId", "default").ifBlank { "default" },
            exercises = exercises
        )
    }

    private fun encodeExercise(e: ExerciseItem): JSONObject = JSONObject().apply {
        put("id", e.id)
        put("name", e.name)
        put("muscleGroup", e.muscleGroup.labelId)
        put("equipment", e.equipment)
        put("targetSets", e.targetSets)
        put("targetReps", e.targetReps)
        put("targetWeightKg", e.targetWeightKg)
        put("restSeconds", e.restSeconds)
        put("notes", e.notes)
        put("trackingMode", e.trackingMode.wireValue)
        put("usesWeight", e.usesWeight)
        put("timerDirection", e.timerDirection.wireValue)
        put("targetDurationSeconds", e.targetDurationSeconds)
    }

    private fun decodeExercise(o: JSONObject, idx: Int): ExerciseItem = ExerciseItem(
        id = o.optString("id").ifBlank { "ex_$idx" },
        name = o.optString("name", "Gerakan").ifBlank { "Gerakan" },
        muscleGroup = MuscleGroup.fromLabel(o.optString("muscleGroup")),
        equipment = o.optString("equipment", "Bebas").ifBlank { "Bebas" },
        targetSets = o.optInt("targetSets", 3).coerceIn(1, 12),
        targetReps = o.optString("targetReps", "10").ifBlank { "10" },
        targetWeightKg = o.optDouble("targetWeightKg", 0.0),
        restSeconds = o.optInt("restSeconds", 60).coerceIn(15, 600),
        notes = o.optString("notes", ""),
        trackingMode = ExerciseTrackingMode.fromWire(o.optString("trackingMode")),
        usesWeight = o.optBoolean("usesWeight", true),
        timerDirection = TimerDirection.fromWire(o.optString("timerDirection")),
        targetDurationSeconds = o.optInt("targetDurationSeconds", 60).coerceIn(5, 10800)
    )

    private fun encodeRingtone(t: CustomRingtone): JSONObject = JSONObject().apply {
        put("id", t.id)
        put("userId", t.userId)
        put("name", t.name)
        put("mimeType", t.mimeType)
        put("sizeBytes", t.sizeBytes)
        put("durationSeconds", t.durationSeconds)
        put("storageMode", t.storageMode.wireValue)
        put("audioDataUrl", t.audioDataUrl)
    }

    private fun decodeRingtone(o: JSONObject, uid: String): CustomRingtone = CustomRingtone(
        id = o.optString("id"),
        userId = o.optString("userId", uid).ifBlank { uid },
        name = o.optString("name", "Ringtone Kustom").ifBlank { "Ringtone Kustom" },
        mimeType = o.optString("mimeType", "audio/mpeg"),
        sizeBytes = o.optLong("sizeBytes", 0L).coerceAtLeast(0L),
        durationSeconds = o.optInt("durationSeconds", 0).coerceIn(0, 3600),
        storageMode = RingtoneStorageMode.fromWire(o.optString("storageMode")),
        audioDataUrl = o.optString("audioDataUrl", "")
    )

    private fun encodeWorkoutLog(log: WorkoutLog): JSONObject = JSONObject().apply {
        put("userId", log.userId)
        put("dateKey", log.dateKey)
        put("dayKey", log.dayKey.key)
        put("focusTitle", log.focusTitle)
        put("completed", log.completed)
        put("activeDurationSeconds", log.activeDurationSeconds)
        put("firstSetElapsedMs", log.firstSetElapsedMs)
        put("serverCreatedAtMs", log.serverCreatedAtMs)
        put("serverUpdatedAtMs", log.serverUpdatedAtMs)
        put("entries", JSONArray().apply {
            log.entries.forEach { entry ->
                put(JSONObject().apply {
                    put("exerciseId", entry.exerciseId)
                    put("name", entry.name)
                    put("muscleGroup", entry.muscleGroup.labelId)
                    put("trackingMode", entry.trackingMode.wireValue)
                    put("usesWeight", entry.usesWeight)
                    put("timerDirection", entry.timerDirection.wireValue)
                    put("targetDurationSeconds", entry.targetDurationSeconds)
                    put("sets", JSONArray().apply {
                        entry.sets.forEach { s ->
                            put(JSONObject().apply {
                                put("setNumber", s.setNumber)
                                put("weightKg", s.weightKg)
                                put("reps", s.reps)
                                put("durationSeconds", s.durationSeconds)
                                put("completed", s.completed)
                                put("medalVerified", s.medalVerified)
                                put("completedAtEpochMs", s.completedAtEpochMs)
                            })
                        }
                    })
                })
            }
        })
    }

    private fun decodeWorkoutLog(o: JSONObject, uid: String): WorkoutLog? {
        val dateKey = o.optString("dateKey")
        if (!Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(dateKey)) return null
        val entriesArr = o.optJSONArray("entries")
        val entries = mutableListOf<WorkoutLogEntry>()
        if (entriesArr != null) {
            for (i in 0 until entriesArr.length()) {
                val eo = entriesArr.optJSONObject(i) ?: continue
                val setsArr = eo.optJSONArray("sets")
                val sets = mutableListOf<WorkoutSetLog>()
                if (setsArr != null) {
                    for (sIdx in 0 until setsArr.length()) {
                        val so = setsArr.optJSONObject(sIdx) ?: continue
                        sets += WorkoutSetLog(
                            setNumber = so.optInt("setNumber", sIdx + 1),
                            weightKg = so.optDouble("weightKg", 0.0),
                            reps = so.optInt("reps", 0),
                            durationSeconds = so.optInt("durationSeconds", 0),
                            completed = so.optBoolean("completed", false),
                            medalVerified = so.optBoolean("medalVerified", true),
                            completedAtEpochMs = so.optLong("completedAtEpochMs", 0L)
                        )
                    }
                }
                entries += WorkoutLogEntry(
                    exerciseId = eo.optString("exerciseId").ifBlank { "ex_$i" },
                    name = eo.optString("name", "Gerakan").ifBlank { "Gerakan" },
                    muscleGroup = MuscleGroup.fromLabel(eo.optString("muscleGroup")),
                    trackingMode = ExerciseTrackingMode.fromWire(eo.optString("trackingMode")),
                    usesWeight = eo.optBoolean("usesWeight", true),
                    timerDirection = TimerDirection.fromWire(eo.optString("timerDirection")),
                    targetDurationSeconds = eo.optInt("targetDurationSeconds", 60).coerceIn(5, 10800),
                    sets = sets
                )
            }
        }
        return WorkoutLog(
            userId = o.optString("userId", uid).ifBlank { uid },
            dateKey = dateKey,
            dayKey = DayKey.fromKey(o.optString("dayKey")),
            focusTitle = o.optString("focusTitle", "Latihan").ifBlank { "Latihan" },
            entries = entries,
            completed = o.optBoolean("completed", false),
            activeDurationSeconds = o.optInt("activeDurationSeconds", 0).coerceAtLeast(0),
            firstSetElapsedMs = o.optLong("firstSetElapsedMs", 0L).coerceAtLeast(0L),
            serverCreatedAtMs = o.optLong("serverCreatedAtMs", 0L).coerceAtLeast(0L),
            serverUpdatedAtMs = o.optLong("serverUpdatedAtMs", 0L).coerceAtLeast(0L)
        )
    }
}
