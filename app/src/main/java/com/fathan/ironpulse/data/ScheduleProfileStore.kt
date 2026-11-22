package com.fathan.ironpulse.data

import android.content.Context
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.DaySchedule
import com.fathan.ironpulse.model.ExerciseItem
import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.MuscleGroup
import com.fathan.ironpulse.model.SavedCustomSchedule
import com.fathan.ironpulse.model.ScheduleLibraryState
import com.fathan.ironpulse.model.TimerDirection
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Lightweight per-UID disk store for saved Custom Schedules and active program metadata.
 * Keeps Firestore lean (no extra collections or cache overhead) while preserving
 * user-saved custom schedule slots for effortless switching back and forth.
 */
class ScheduleProfileStore(private val context: Context) {

    private val uidLocks = ConcurrentHashMap<String, Any>()

    private fun lockFor(uid: String): Any = uidLocks.computeIfAbsent(uid) { Any() }

    private fun storeFile(uid: String): File {
        val safeUid = uid.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(80)
        return File(context.filesDir, "ironpulse_schedule_profiles_v1_$safeUid.json")
    }

    fun load(uid: String): ScheduleLibraryState {
        if (uid.isBlank()) return ScheduleLibraryState()
        return synchronized(lockFor(uid)) {
            runCatching {
                val file = storeFile(uid)
                if (!file.exists() || !file.isFile) return@runCatching ScheduleLibraryState()
                val raw = file.readText(Charsets.UTF_8)
                if (raw.isBlank()) return@runCatching ScheduleLibraryState()
                decodeLibrary(JSONObject(raw), uid)
            }.getOrDefault(ScheduleLibraryState())
        }
    }

    fun save(uid: String, state: ScheduleLibraryState) {
        if (uid.isBlank()) return
        synchronized(lockFor(uid)) {
            runCatching {
                val target = storeFile(uid)
                val parent = target.parentFile
                if (parent != null && !parent.exists()) parent.mkdirs()
                val tmp = File(parent, "${target.name}.tmp")
                val json = encodeLibrary(state).toString()
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
                val f = storeFile(uid)
                if (f.exists()) f.delete()
            }
        }
    }

    private fun encodeLibrary(lib: ScheduleLibraryState): JSONObject = JSONObject().apply {
        if (!lib.activeProfileId.isNullOrBlank()) {
            put("activeProfileId", lib.activeProfileId)
        }
        if (!lib.activeProfileName.isNullOrBlank()) {
            put("activeProfileName", lib.activeProfileName)
        }
        put("savedSchedules", JSONArray().apply {
            lib.savedSchedules.take(MAX_SAVED_PROFILES).forEach { profile ->
                put(encodeProfile(profile))
            }
        })
    }

    private fun decodeLibrary(root: JSONObject, uid: String): ScheduleLibraryState {
        val arr = root.optJSONArray("savedSchedules")
        val list = mutableListOf<SavedCustomSchedule>()
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val parsed = decodeProfile(obj, uid)
                if (parsed.schedules.isNotEmpty()) {
                    list += parsed
                }
            }
        }
        return ScheduleLibraryState(
            savedSchedules = list,
            activeProfileId = root.optString("activeProfileId").takeIf { it.isNotBlank() },
            activeProfileName = root.optString("activeProfileName").takeIf { it.isNotBlank() }
        )
    }

    private fun encodeProfile(p: SavedCustomSchedule): JSONObject = JSONObject().apply {
        put("id", p.id)
        put("name", p.name)
        put("isAutoBackup", p.isAutoBackup)
        put("updatedAtMs", p.updatedAtMs)
        put("schedules", JSONArray().apply {
            DayKey.entries.forEach { day ->
                p.schedules[day]?.let { put(encodeSchedule(it)) }
            }
        })
    }

    private fun decodeProfile(o: JSONObject, uid: String): SavedCustomSchedule {
        val schedArr = o.optJSONArray("schedules")
        val map = mutableMapOf<DayKey, DaySchedule>()
        if (schedArr != null) {
            for (i in 0 until schedArr.length()) {
                val so = schedArr.optJSONObject(i) ?: continue
                val sched = decodeSchedule(so, uid)
                map[sched.dayKey] = sched
            }
        }
        return SavedCustomSchedule(
            id = o.optString("id").ifBlank { "custom_${System.currentTimeMillis()}" },
            name = o.optString("name", "Jadwal Kustom").trim().take(48).ifEmpty { "Jadwal Kustom" },
            isAutoBackup = o.optBoolean("isAutoBackup", false),
            updatedAtMs = o.optLong("updatedAtMs", 0L),
            schedules = map
        )
    }

    private fun encodeSchedule(s: DaySchedule): JSONObject = JSONObject().apply {
        put("userId", s.userId)
        put("dayKey", s.dayKey.key)
        put("isGymDay", s.isGymDay)
        put("alarmEnabled", s.alarmEnabled)
        put("alarmTime", s.alarmTime)
        put("focusTitle", s.focusTitle)
        put("ringtoneId", s.ringtoneId)
        put("exercises", JSONArray().apply {
            s.exercises.take(30).forEach { put(encodeExercise(it)) }
        })
    }

    private fun decodeSchedule(o: JSONObject, uid: String): DaySchedule {
        val exArr = o.optJSONArray("exercises")
        val exercises = mutableListOf<ExerciseItem>()
        if (exArr != null) {
            for (i in 0 until exArr.length()) {
                val eo = exArr.optJSONObject(i) ?: continue
                exercises += decodeExercise(eo, i)
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

    companion object {
        const val AUTO_BACKUP_ID = "custom_previous_backup"
        const val MAX_SAVED_PROFILES = 12
    }
}
