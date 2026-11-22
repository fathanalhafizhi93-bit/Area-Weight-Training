package com.fathan.ironpulse.data

import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.DaySchedule
import com.fathan.ironpulse.model.ExerciseItem
import com.fathan.ironpulse.model.OfficialProgramTemplate

object ProgramTemplatePresets {

    const val ID_PPL_UPPER_LOWER = "official_ppl_upper_lower"
    const val ID_PPL_6_DAYS = "official_ppl_6d"
    const val ID_UPPER_LOWER_4_DAYS = "official_upper_lower_4d"

    private val libraryByName: Map<String, PresetExerciseTemplate> by lazy {
        GymPresets.EXERCISE_LIBRARY.associateBy { it.name }
    }

    private fun presetEx(name: String, prefix: String, idx: Int): ExerciseItem {
        val tmpl = requireNotNull(libraryByName[name]) {
            "Exercise '$name' not found in GymPresets.EXERCISE_LIBRARY"
        }
        return tmpl.toExerciseItem("${prefix}_$idx")
    }

    val OFFICIAL_TEMPLATES: List<OfficialProgramTemplate> = listOf(
        OfficialProgramTemplate(
            id = ID_PPL_UPPER_LOWER,
            titleId = "PPL × Upper-Lower",
            titleEn = "PPL × Upper-Lower",
            subtitleId = "Split hybrid 5 hari: Push, Pull, Legs lalu Upper & Lower dengan 2 hari pemulihan.",
            subtitleEn = "5-day hybrid split: Push, Pull, Legs followed by Upper & Lower with 2 recovery days.",
            badgeId = "5 Hari · Hybrid",
            badgeEn = "5 Days · Hybrid",
            daysPerWeek = 5,
            isFeatured = true,
            buildSchedules = ::buildPplUpperLowerSchedules
        ),
        OfficialProgramTemplate(
            id = ID_PPL_6_DAYS,
            titleId = "Push-Pull-Legs",
            titleEn = "Push-Pull-Legs",
            subtitleId = "Split 6 hari volume tinggi (2× rotasi Push, Pull, Legs) dengan Minggu istirahat total.",
            subtitleEn = "High-volume 6-day split (2× Push, Pull, Legs rotation) with Sunday full rest.",
            badgeId = "6 Hari · Volume",
            badgeEn = "6 Days · Volume",
            daysPerWeek = 6,
            isFeatured = false,
            buildSchedules = ::buildPplSixDaySchedules
        ),
        OfficialProgramTemplate(
            id = ID_UPPER_LOWER_4_DAYS,
            titleId = "Upper-Lower",
            titleEn = "Upper-Lower",
            subtitleId = "Split 4 hari seimbang (2× Tubuh Atas & 2× Tubuh Bawah) ideal untuk progres stabil.",
            subtitleEn = "Balanced 4-day split (2× Upper & 2× Lower) ideal for steady strength progression.",
            badgeId = "4 Hari · Seimbang",
            badgeEn = "4 Days · Balanced",
            daysPerWeek = 4,
            isFeatured = false,
            buildSchedules = ::buildUpperLowerFourDaySchedules
        )
    )

    fun findById(id: String?): OfficialProgramTemplate? =
        OFFICIAL_TEMPLATES.firstOrNull { it.id == id }

    private fun buildPplUpperLowerSchedules(userId: String): Map<DayKey, DaySchedule> = mapOf(
        DayKey.MON to DaySchedule(
            userId = userId,
            dayKey = DayKey.MON,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Push — Dada, Bahu & Trisep",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Barbell Bench Press", "pplul_mon", 0),
                presetEx("Incline Dumbbell Press", "pplul_mon", 1),
                presetEx("Overhead Barbell Press", "pplul_mon", 2),
                presetEx("Dumbbell Lateral Raise", "pplul_mon", 3),
                presetEx("Cable Chest Fly", "pplul_mon", 4),
                presetEx("Tricep Rope Pushdown", "pplul_mon", 5)
            )
        ),
        DayKey.TUE to DaySchedule(
            userId = userId,
            dayKey = DayKey.TUE,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Pull — Punggung & Bisep",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Pull-Up", "pplul_tue", 0),
                presetEx("Barbell Bent-Over Row", "pplul_tue", 1),
                presetEx("Lat Pulldown", "pplul_tue", 2),
                presetEx("Seated Cable Row", "pplul_tue", 3),
                presetEx("Face Pull", "pplul_tue", 4),
                presetEx("EZ-Bar Bicep Curl", "pplul_tue", 5)
            )
        ),
        DayKey.WED to DaySchedule(
            userId = userId,
            dayKey = DayKey.WED,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Legs — Kaki & Glutes",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Barbell Back Squat", "pplul_wed", 0),
                presetEx("Romanian Deadlift (RDL)", "pplul_wed", 1),
                presetEx("Leg Press", "pplul_wed", 2),
                presetEx("Lying Leg Curl Machine", "pplul_wed", 3),
                presetEx("Standing Calf Raise", "pplul_wed", 4),
                presetEx("Hanging Leg Raise", "pplul_wed", 5)
            )
        ),
        DayKey.THU to DaySchedule(
            userId = userId,
            dayKey = DayKey.THU,
            isGymDay = false,
            alarmEnabled = false,
            alarmTime = "07:00",
            focusTitle = "Rest Day — Pemulihan Aktif",
            ringtoneId = "default",
            exercises = emptyList()
        ),
        DayKey.FRI to DaySchedule(
            userId = userId,
            dayKey = DayKey.FRI,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Upper Body — Dada, Punggung & Lengan",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Incline Barbell Bench Press", "pplul_fri", 0),
                presetEx("Single-Arm Dumbbell Row", "pplul_fri", 1),
                presetEx("Seated Dumbbell Shoulder Press", "pplul_fri", 2),
                presetEx("Close-Grip Neutral Lat Pulldown", "pplul_fri", 3),
                presetEx("Cable Lateral Raise", "pplul_fri", 4),
                presetEx("Hammer Curl", "pplul_fri", 5),
                presetEx("Overhead Cable Tricep Extension", "pplul_fri", 6)
            )
        ),
        DayKey.SAT to DaySchedule(
            userId = userId,
            dayKey = DayKey.SAT,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "08:00",
            focusTitle = "Lower Body — Kaki, Glutes & Core",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Hack Squat Machine", "pplul_sat", 0),
                presetEx("Barbell Hip Thrust", "pplul_sat", 1),
                presetEx("Bulgarian Split Squat", "pplul_sat", 2),
                presetEx("Seated Leg Curl Machine", "pplul_sat", 3),
                presetEx("Seated Calf Raise", "pplul_sat", 4),
                presetEx("Cable Rope Crunch", "pplul_sat", 5)
            )
        ),
        DayKey.SUN to DaySchedule(
            userId = userId,
            dayKey = DayKey.SUN,
            isGymDay = false,
            alarmEnabled = false,
            alarmTime = "08:00",
            focusTitle = "Rest Day — Istirahat Total",
            ringtoneId = "default",
            exercises = emptyList()
        )
    )

    private fun buildPplSixDaySchedules(userId: String): Map<DayKey, DaySchedule> = mapOf(
        DayKey.MON to DaySchedule(
            userId = userId,
            dayKey = DayKey.MON,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Push A — Dada & Bahu Depan",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Barbell Bench Press", "ppl6_mon", 0),
                presetEx("Incline Dumbbell Press", "ppl6_mon", 1),
                presetEx("Overhead Barbell Press", "ppl6_mon", 2),
                presetEx("Dumbbell Lateral Raise", "ppl6_mon", 3),
                presetEx("Tricep Rope Pushdown", "ppl6_mon", 4)
            )
        ),
        DayKey.TUE to DaySchedule(
            userId = userId,
            dayKey = DayKey.TUE,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Pull A — Punggung & Bisep",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Pull-Up", "ppl6_tue", 0),
                presetEx("Barbell Bent-Over Row", "ppl6_tue", 1),
                presetEx("Seated Cable Row", "ppl6_tue", 2),
                presetEx("Face Pull", "ppl6_tue", 3),
                presetEx("EZ-Bar Bicep Curl", "ppl6_tue", 4)
            )
        ),
        DayKey.WED to DaySchedule(
            userId = userId,
            dayKey = DayKey.WED,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Legs A — Fokus Paha Depan",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Barbell Back Squat", "ppl6_wed", 0),
                presetEx("Leg Press", "ppl6_wed", 1),
                presetEx("Leg Extension Machine", "ppl6_wed", 2),
                presetEx("Lying Leg Curl Machine", "ppl6_wed", 3),
                presetEx("Standing Calf Raise", "ppl6_wed", 4)
            )
        ),
        DayKey.THU to DaySchedule(
            userId = userId,
            dayKey = DayKey.THU,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Push B — Dada Atas & Bahu",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Incline Barbell Bench Press", "ppl6_thu", 0),
                presetEx("Flat Dumbbell Bench Press", "ppl6_thu", 1),
                presetEx("Seated Dumbbell Shoulder Press", "ppl6_thu", 2),
                presetEx("Cable Lateral Raise", "ppl6_thu", 3),
                presetEx("EZ-Bar Skull Crusher", "ppl6_thu", 4)
            )
        ),
        DayKey.FRI to DaySchedule(
            userId = userId,
            dayKey = DayKey.FRI,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Pull B — Lebar Punggung & Lengan",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Lat Pulldown", "ppl6_fri", 0),
                presetEx("Chest-Supported T-Bar Row", "ppl6_fri", 1),
                presetEx("Single-Arm Dumbbell Row", "ppl6_fri", 2),
                presetEx("Reverse Pec Deck Rear Delt Fly", "ppl6_fri", 3),
                presetEx("Hammer Curl", "ppl6_fri", 4)
            )
        ),
        DayKey.SAT to DaySchedule(
            userId = userId,
            dayKey = DayKey.SAT,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "08:00",
            focusTitle = "Legs B — Hamstring, Glutes & Core",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Romanian Deadlift (RDL)", "ppl6_sat", 0),
                presetEx("Bulgarian Split Squat", "ppl6_sat", 1),
                presetEx("Barbell Hip Thrust", "ppl6_sat", 2),
                presetEx("Seated Leg Curl Machine", "ppl6_sat", 3),
                presetEx("Seated Calf Raise", "ppl6_sat", 4),
                presetEx("Hanging Leg Raise", "ppl6_sat", 5)
            )
        ),
        DayKey.SUN to DaySchedule(
            userId = userId,
            dayKey = DayKey.SUN,
            isGymDay = false,
            alarmEnabled = false,
            alarmTime = "08:00",
            focusTitle = "Rest Day — Istirahat Total",
            ringtoneId = "default",
            exercises = emptyList()
        )
    )

    private fun buildUpperLowerFourDaySchedules(userId: String): Map<DayKey, DaySchedule> = mapOf(
        DayKey.MON to DaySchedule(
            userId = userId,
            dayKey = DayKey.MON,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Upper A — Kekuatan Tubuh Atas",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Barbell Bench Press", "ul4_mon", 0),
                presetEx("Barbell Bent-Over Row", "ul4_mon", 1),
                presetEx("Overhead Barbell Press", "ul4_mon", 2),
                presetEx("Lat Pulldown", "ul4_mon", 3),
                presetEx("EZ-Bar Bicep Curl", "ul4_mon", 4),
                presetEx("Tricep Rope Pushdown", "ul4_mon", 5)
            )
        ),
        DayKey.TUE to DaySchedule(
            userId = userId,
            dayKey = DayKey.TUE,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Lower A — Fokus Squat & Core",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Barbell Back Squat", "ul4_tue", 0),
                presetEx("Romanian Deadlift (RDL)", "ul4_tue", 1),
                presetEx("Leg Press", "ul4_tue", 2),
                presetEx("Standing Calf Raise", "ul4_tue", 3),
                presetEx("Plank", "ul4_tue", 4)
            )
        ),
        DayKey.WED to DaySchedule(
            userId = userId,
            dayKey = DayKey.WED,
            isGymDay = false,
            alarmEnabled = false,
            alarmTime = "07:00",
            focusTitle = "Rest Day — Pemulihan",
            ringtoneId = "default",
            exercises = emptyList()
        ),
        DayKey.THU to DaySchedule(
            userId = userId,
            dayKey = DayKey.THU,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Upper B — Hipertrofi Tubuh Atas",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Incline Dumbbell Press", "ul4_thu", 0),
                presetEx("Seated Cable Row", "ul4_thu", 1),
                presetEx("Seated Dumbbell Shoulder Press", "ul4_thu", 2),
                presetEx("Cable Chest Fly", "ul4_thu", 3),
                presetEx("Dumbbell Lateral Raise", "ul4_thu", 4),
                presetEx("Hammer Curl", "ul4_thu", 5)
            )
        ),
        DayKey.FRI to DaySchedule(
            userId = userId,
            dayKey = DayKey.FRI,
            isGymDay = true,
            alarmEnabled = true,
            alarmTime = "06:30",
            focusTitle = "Lower B — Hamstring & Glutes",
            ringtoneId = "default",
            exercises = listOf(
                presetEx("Hack Squat Machine", "ul4_fri", 0),
                presetEx("Barbell Hip Thrust", "ul4_fri", 1),
                presetEx("Bulgarian Split Squat", "ul4_fri", 2),
                presetEx("Seated Leg Curl Machine", "ul4_fri", 3),
                presetEx("Seated Calf Raise", "ul4_fri", 4),
                presetEx("Hanging Leg Raise", "ul4_fri", 5)
            )
        ),
        DayKey.SAT to DaySchedule(
            userId = userId,
            dayKey = DayKey.SAT,
            isGymDay = false,
            alarmEnabled = false,
            alarmTime = "08:00",
            focusTitle = "Rest Day — Pemulihan Aktif",
            ringtoneId = "default",
            exercises = emptyList()
        ),
        DayKey.SUN to DaySchedule(
            userId = userId,
            dayKey = DayKey.SUN,
            isGymDay = false,
            alarmEnabled = false,
            alarmTime = "08:00",
            focusTitle = "Rest Day — Istirahat Total",
            ringtoneId = "default",
            exercises = emptyList()
        )
    )
}
