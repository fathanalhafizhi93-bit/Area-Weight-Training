package com.fathan.ironpulse.model

import androidx.compose.runtime.Immutable
import java.util.Locale

enum class BodyViewSide {
    ANTERIOR,
    POSTERIOR
}

enum class MuscleRegion(
    val labelId: String,
    val labelEn: String,
    val side: BodyViewSide
) {
    // Anterior (Depan)
    UPPER_CHEST("Dada Atas", "Upper Chest", BodyViewSide.ANTERIOR),
    MID_CHEST("Dada Tengah & Bawah", "Mid & Lower Chest", BodyViewSide.ANTERIOR),
    FRONT_DELTS("Bahu Depan", "Front Delts", BodyViewSide.ANTERIOR),
    SIDE_DELTS("Bahu Samping", "Side Delts", BodyViewSide.ANTERIOR),
    BICEPS("Bisep", "Biceps", BodyViewSide.ANTERIOR),
    FOREARMS("Lengan Bawah", "Forearms", BodyViewSide.ANTERIOR),
    ABS("Perut (Abs)", "Abs", BodyViewSide.ANTERIOR),
    OBLIQUES("Perut Samping", "Obliques", BodyViewSide.ANTERIOR),
    QUADS("Paha Depan (Quads)", "Quadriceps", BodyViewSide.ANTERIOR),
    ADDUCTORS("Paha Dalam & Pinggul", "Adductors & Hips", BodyViewSide.ANTERIOR),
    TIBIALIS("Tulang Kering", "Tibialis", BodyViewSide.ANTERIOR),

    // Posterior (Belakang)
    TRAPS("Trapezius", "Traps", BodyViewSide.POSTERIOR),
    REAR_DELTS("Bahu Belakang", "Rear Delts", BodyViewSide.POSTERIOR),
    LATS("Sayap Punggung (Lats)", "Lats", BodyViewSide.POSTERIOR),
    MID_BACK("Punggung Tengah", "Mid Back", BodyViewSide.POSTERIOR),
    LOWER_BACK("Punggung Bawah", "Lower Back", BodyViewSide.POSTERIOR),
    TRICEPS("Trisep", "Triceps", BodyViewSide.POSTERIOR),
    GLUTES("Bokong (Glutes)", "Glutes", BodyViewSide.POSTERIOR),
    HAMSTRINGS("Paha Belakang", "Hamstrings", BodyViewSide.POSTERIOR),
    CALVES("Betis (Calves)", "Calves", BodyViewSide.POSTERIOR);

    fun label(lang: AppLanguage): String = if (lang == AppLanguage.EN) labelEn else labelId
}

@Immutable
data class MuscleAnatomySpec(
    val primary: List<MuscleRegion>,
    val secondary: List<MuscleRegion> = emptyList()
) {
    val hasAnterior: Boolean
        get() = primary.any { it.side == BodyViewSide.ANTERIOR } ||
            secondary.any { it.side == BodyViewSide.ANTERIOR }

    val hasPosterior: Boolean
        get() = primary.any { it.side == BodyViewSide.POSTERIOR } ||
            secondary.any { it.side == BodyViewSide.POSTERIOR }

    fun searchableKeywords(lang: AppLanguage): String =
        (primary + secondary).distinct().joinToString(" ") { it.label(lang) }
}

/**
 * Deterministic biomechanical resolver that maps every library & custom exercise
 * to exact primary and secondary anatomical regions across front and back body views.
 */
object MuscleAnatomyResolver {

    fun resolve(
        name: String,
        muscleGroup: MuscleGroup,
        equipment: String = ""
    ): MuscleAnatomySpec {
        val n = name.lowercase(Locale.ROOT)
        val eq = equipment.lowercase(Locale.ROOT)

        return when (muscleGroup) {
            MuscleGroup.DADA -> resolveChest(n)
            MuscleGroup.PUNGGUNG -> resolveBack(n)
            MuscleGroup.BAHU -> resolveShoulders(n)
            MuscleGroup.KAKI -> resolveLegs(n, eq)
            MuscleGroup.LENGAN -> resolveArms(n)
            MuscleGroup.CORE -> resolveCore(n)
            MuscleGroup.KARDIO -> resolveCardio(n, eq)
        }
    }

    private fun resolveChest(n: String): MuscleAnatomySpec = when {
        n.contains("pullover") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_CHEST, MuscleRegion.LATS),
            secondary = listOf(MuscleRegion.TRICEPS, MuscleRegion.ABS)
        )
        (n.contains("incline") || n.contains("low-to-high") || n.contains("clavicular") || n.contains("guillotine") || n.contains("landmine")) &&
            (n.contains("fly") || n.contains("pec deck")) -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.UPPER_CHEST),
            secondary = listOf(MuscleRegion.MID_CHEST, MuscleRegion.FRONT_DELTS)
        )
        n.contains("incline") || n.contains("low-to-high") || n.contains("reverse-grip") || n.contains("landmine") || n.contains("guillotine") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.UPPER_CHEST),
            secondary = listOf(MuscleRegion.MID_CHEST, MuscleRegion.FRONT_DELTS, MuscleRegion.TRICEPS)
        )
        n.contains("diamond") || n.contains("narrow") || n.contains("close-grip") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_CHEST, MuscleRegion.TRICEPS),
            secondary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.UPPER_CHEST)
        )
        n.contains("dip") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_CHEST),
            secondary = listOf(MuscleRegion.TRICEPS, MuscleRegion.FRONT_DELTS)
        )
        n.contains("fly") || n.contains("pec deck") || n.contains("crossover") || n.contains("squeeze") || n.contains("svend") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_CHEST),
            secondary = listOf(MuscleRegion.UPPER_CHEST, MuscleRegion.FRONT_DELTS)
        )
        n.contains("decline") || n.contains("high-to-low") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_CHEST),
            secondary = listOf(MuscleRegion.TRICEPS, MuscleRegion.FRONT_DELTS)
        )
        n.contains("push-up") || n.contains("pushup") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_CHEST),
            secondary = listOf(MuscleRegion.UPPER_CHEST, MuscleRegion.FRONT_DELTS, MuscleRegion.TRICEPS, MuscleRegion.ABS)
        )
        else -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_CHEST),
            secondary = listOf(MuscleRegion.UPPER_CHEST, MuscleRegion.FRONT_DELTS, MuscleRegion.TRICEPS)
        )
    }

    private fun resolveBack(n: String): MuscleAnatomySpec = when {
        n.contains("deadlift") || n.contains("rack pull") || n.contains("block pull") || n.contains("snatch-grip") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LOWER_BACK, MuscleRegion.LATS, MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.TRAPS, MuscleRegion.FOREARMS)
        )
        n.contains("hyperextension") || n.contains("back extension") || n.contains("superman") || n.contains("bird-dog") || n.contains("reverse hyper") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LOWER_BACK),
            secondary = listOf(MuscleRegion.GLUTES, MuscleRegion.HAMSTRINGS)
        )
        n.contains("straight-arm") || n.contains("pullover") || n.contains("lat prayer") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LATS),
            secondary = listOf(MuscleRegion.TRICEPS, MuscleRegion.ABS, MuscleRegion.MID_BACK)
        )
        n.contains("chin-up") || n.contains("reverse-grip") || n.contains("underhand") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LATS),
            secondary = listOf(MuscleRegion.BICEPS, MuscleRegion.MID_BACK, MuscleRegion.FOREARMS)
        )
        n.contains("pulldown") || n.contains("pull-up") || n.contains("muscle-up") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LATS),
            secondary = listOf(MuscleRegion.MID_BACK, MuscleRegion.BICEPS, MuscleRegion.REAR_DELTS, MuscleRegion.FOREARMS)
        )
        n.contains("kelso") || n.contains("scapular") || n.contains("retraction") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.MID_BACK, MuscleRegion.TRAPS),
            secondary = listOf(MuscleRegion.REAR_DELTS, MuscleRegion.FOREARMS)
        )
        n.contains("chest-supported") || n.contains("seal row") || n.contains("inverted row") || n.contains("machine row") || n.contains("low row") || n.contains("high row") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LATS, MuscleRegion.MID_BACK),
            secondary = listOf(MuscleRegion.REAR_DELTS, MuscleRegion.BICEPS, MuscleRegion.TRAPS)
        )
        n.contains("row") || n.contains("t-bar") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LATS, MuscleRegion.MID_BACK),
            secondary = listOf(MuscleRegion.REAR_DELTS, MuscleRegion.BICEPS, MuscleRegion.LOWER_BACK, MuscleRegion.FOREARMS)
        )
        else -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LATS, MuscleRegion.MID_BACK),
            secondary = listOf(MuscleRegion.REAR_DELTS, MuscleRegion.BICEPS)
        )
    }

    private fun resolveShoulders(n: String): MuscleAnatomySpec = when {
        n.contains("shrug") || n.contains("trap") && !n.contains("y-raise") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.TRAPS),
            secondary = listOf(MuscleRegion.FOREARMS, MuscleRegion.MID_BACK)
        )
        n.contains("rear delt") || n.contains("reverse pec") || n.contains("face pull") ||
            n.contains("pull-apart") || n.contains("reverse fly") || n.contains("y-raise") ||
            n.contains("external rotation") || n.contains("w-raise") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.REAR_DELTS),
            secondary = listOf(MuscleRegion.MID_BACK, MuscleRegion.TRAPS, MuscleRegion.SIDE_DELTS)
        )
        n.contains("lateral raise") || n.contains("lu raise") || n.contains("egyptian") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.SIDE_DELTS),
            secondary = listOf(MuscleRegion.TRAPS, MuscleRegion.FRONT_DELTS)
        )
        n.contains("upright row") || n.contains("high pull") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.SIDE_DELTS, MuscleRegion.TRAPS),
            secondary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.BICEPS, MuscleRegion.FOREARMS)
        )
        n.contains("front raise") || n.contains("bus driver") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.FRONT_DELTS),
            secondary = listOf(MuscleRegion.UPPER_CHEST, MuscleRegion.SIDE_DELTS)
        )
        n.contains("arnold") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.SIDE_DELTS),
            secondary = listOf(MuscleRegion.TRICEPS, MuscleRegion.TRAPS, MuscleRegion.UPPER_CHEST)
        )
        n.contains("press") || n.contains("push-up") || n.contains("handstand") || n.contains("jerk") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.SIDE_DELTS),
            secondary = listOf(MuscleRegion.TRICEPS, MuscleRegion.TRAPS, MuscleRegion.UPPER_CHEST)
        )
        else -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.SIDE_DELTS),
            secondary = listOf(MuscleRegion.REAR_DELTS, MuscleRegion.TRAPS)
        )
    }

    private fun resolveLegs(n: String, eq: String): MuscleAnatomySpec = when {
        n.contains("tibialis") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.TIBIALIS),
            secondary = listOf(MuscleRegion.CALVES)
        )
        n.contains("calf") || n.contains("calves") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.CALVES),
            secondary = if (n.contains("standing")) listOf(MuscleRegion.TIBIALIS) else emptyList()
        )
        n.contains("adduction") || n.contains("copenhagen") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.ADDUCTORS),
            secondary = listOf(MuscleRegion.OBLIQUES, MuscleRegion.QUADS)
        )
        n.contains("abduction") || n.contains("clamshell") || n.contains("monster walk") || n.contains("fire hydrant") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.ADDUCTORS)
        )
        n.contains("hip thrust") || n.contains("glute bridge") || n.contains("kickback") ||
            n.contains("pull-through") || n.contains("frog pump") || n.contains("kas") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.QUADS, MuscleRegion.ABS)
        )
        n.contains("rdl") || n.contains("romanian") || n.contains("stiff-leg") || n.contains("good morning") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.LOWER_BACK, MuscleRegion.FOREARMS)
        )
        n.contains("leg curl") || n.contains("hamstring") || n.contains("nordic") || n.contains("glute-ham") || n.contains("slider") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.HAMSTRINGS),
            secondary = listOf(MuscleRegion.GLUTES, MuscleRegion.CALVES)
        )
        n.contains("sumo") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.GLUTES, MuscleRegion.ADDUCTORS, MuscleRegion.QUADS),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.LOWER_BACK, MuscleRegion.TRAPS)
        )
        n.contains("leg extension") || n.contains("sissy") || n.contains("wall sit") || n.contains("spanish") || n.contains("cyclist") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS),
            secondary = listOf(MuscleRegion.ADDUCTORS, MuscleRegion.TIBIALIS)
        )
        n.contains("bulgarian") || n.contains("split squat") || n.contains("lunge") || n.contains("step-up") || n.contains("step up") || n.contains("pistol") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.ADDUCTORS, MuscleRegion.CALVES)
        )
        n.contains("front squat") || n.contains("goblet") || n.contains("zercher") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS),
            secondary = listOf(MuscleRegion.GLUTES, MuscleRegion.ABS, MuscleRegion.UPPER_CHEST)
        )
        n.contains("squat") || n.contains("leg press") || n.contains("thruster") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.ADDUCTORS, MuscleRegion.LOWER_BACK)
        )
        else -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.CALVES)
        )
    }

    private fun resolveArms(n: String): MuscleAnatomySpec = when {
        n.contains("wrist") || n.contains("finger") || n.contains("dead hang") || n.contains("pinch") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.FOREARMS),
            secondary = listOf(MuscleRegion.BICEPS)
        )
        n.contains("hammer") || n.contains("reverse-grip") || n.contains("reverse curl") || n.contains("zottman") || n.contains("pinwheel") || n.contains("cross-body") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.BICEPS, MuscleRegion.FOREARMS),
            secondary = listOf(MuscleRegion.FRONT_DELTS)
        )
        n.contains("curl") || n.contains("21s") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.BICEPS),
            secondary = listOf(MuscleRegion.FOREARMS)
        )
        n.contains("close-grip") || n.contains("jm press") || n.contains("dip") || n.contains("diamond") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.TRICEPS),
            secondary = listOf(MuscleRegion.MID_CHEST, MuscleRegion.FRONT_DELTS)
        )
        n.contains("tricep") || n.contains("pushdown") || n.contains("skull crusher") ||
            n.contains("extension") || n.contains("kickback") || n.contains("french press") ||
            n.contains("tate press") || n.contains("california") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.TRICEPS),
            secondary = listOf(MuscleRegion.FOREARMS, MuscleRegion.FRONT_DELTS)
        )
        else -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.BICEPS, MuscleRegion.TRICEPS),
            secondary = listOf(MuscleRegion.FOREARMS)
        )
    }

    private fun resolveCore(n: String): MuscleAnatomySpec = when {
        n.contains("side") || n.contains("woodchop") || n.contains("russian twist") ||
            n.contains("pallof") || n.contains("rotation") || n.contains("suitcase") ||
            n.contains("bicycle") || n.contains("wiper") || n.contains("landmine 180") ||
            n.contains("copenhagen") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.OBLIQUES, MuscleRegion.ABS),
            secondary = listOf(MuscleRegion.LOWER_BACK, MuscleRegion.FOREARMS)
        )
        n.contains("ab wheel") || n.contains("rollout") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.ABS),
            secondary = listOf(MuscleRegion.OBLIQUES, MuscleRegion.LATS, MuscleRegion.TRICEPS)
        )
        n.contains("bird-dog") || n.contains("vacuum") || n.contains("glute march") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.ABS, MuscleRegion.LOWER_BACK),
            secondary = listOf(MuscleRegion.GLUTES, MuscleRegion.OBLIQUES)
        )
        n.contains("leg raise") || n.contains("knee raise") || n.contains("toes-to-bar") || n.contains("l-sit") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.ABS),
            secondary = listOf(MuscleRegion.OBLIQUES, MuscleRegion.ADDUCTORS, MuscleRegion.FOREARMS)
        )
        else -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.ABS),
            secondary = listOf(MuscleRegion.OBLIQUES, MuscleRegion.LOWER_BACK)
        )
    }

    private fun resolveCardio(n: String, eq: String): MuscleAnatomySpec = when {
        n.contains("row") || n.contains("skierg") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.LATS, MuscleRegion.QUADS),
            secondary = listOf(MuscleRegion.REAR_DELTS, MuscleRegion.ABS, MuscleRegion.FOREARMS, MuscleRegion.HAMSTRINGS)
        )
        n.contains("battle rope") || n.contains("shadowbox") || n.contains("heavy bag") || n.contains("slam ball") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.ABS),
            secondary = listOf(MuscleRegion.FOREARMS, MuscleRegion.SIDE_DELTS, MuscleRegion.TRICEPS, MuscleRegion.LATS)
        )
        n.contains("swing") || n.contains("snatch") || n.contains("clean") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.GLUTES, MuscleRegion.HAMSTRINGS),
            secondary = listOf(MuscleRegion.LOWER_BACK, MuscleRegion.FRONT_DELTS, MuscleRegion.ABS, MuscleRegion.FOREARMS)
        )
        n.contains("farmers") || n.contains("carry") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.FOREARMS, MuscleRegion.TRAPS, MuscleRegion.QUADS),
            secondary = listOf(MuscleRegion.ABS, MuscleRegion.OBLIQUES, MuscleRegion.CALVES)
        )
        n.contains("sled") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.GLUTES, MuscleRegion.CALVES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.ABS, MuscleRegion.FRONT_DELTS)
        )
        n.contains("burpee") || n.contains("turkish") || n.contains("thruster") || n.contains("man maker") || n.contains("bear crawl") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.MID_CHEST, MuscleRegion.ABS),
            secondary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.TRICEPS, MuscleRegion.GLUTES)
        )
        n.contains("assault") || n.contains("air bike") || n.contains("elliptical") || n.contains("arc trainer") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.GLUTES),
            secondary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.HAMSTRINGS, MuscleRegion.CALVES, MuscleRegion.ABS)
        )
        n.contains("mountain climber") || n.contains("sprawl") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.ABS, MuscleRegion.QUADS),
            secondary = listOf(MuscleRegion.FRONT_DELTS, MuscleRegion.OBLIQUES, MuscleRegion.ADDUCTORS)
        )
        n.contains("jump") || n.contains("skip") || n.contains("shuttle") || n.contains("sprint") || n.contains("stair") -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.CALVES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.GLUTES, MuscleRegion.ABS)
        )
        else -> MuscleAnatomySpec(
            primary = listOf(MuscleRegion.QUADS, MuscleRegion.CALVES),
            secondary = listOf(MuscleRegion.HAMSTRINGS, MuscleRegion.GLUTES, MuscleRegion.ABS)
        )
    }
}
