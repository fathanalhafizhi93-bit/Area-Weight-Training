package com.fathan.ironpulse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.fathan.ironpulse.data.AppPreferences
import com.fathan.ironpulse.data.FirestoreGymRepository
import com.fathan.ironpulse.data.GymCachedSnapshot
import com.fathan.ironpulse.data.GymPresets
import com.fathan.ironpulse.data.LocalGymCacheStore
import com.fathan.ironpulse.data.PreferencesStore
import com.fathan.ironpulse.data.ProgramTemplatePresets
import com.fathan.ironpulse.data.ScheduleProfileStore
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.MemoryEagerGcSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.CustomRingtone
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.MuscleGroup
import com.fathan.ironpulse.model.PerformancePreferences
import com.fathan.ironpulse.model.PerformancePresetId
import com.fathan.ironpulse.model.RingtoneStorageMode
import com.fathan.ironpulse.model.SavedCustomSchedule
import com.fathan.ironpulse.model.ScheduleLibraryState
import com.fathan.ironpulse.model.ScheduleProgramMerger
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.model.UserProfile
import com.fathan.ironpulse.model.WorkoutLog
import com.fathan.ironpulse.model.WorkoutLogEntry
import com.fathan.ironpulse.model.WorkoutSetLog
import com.fathan.ironpulse.timer.RestTimerService
import com.fathan.ironpulse.ui.components.AboutDialog
import com.fathan.ironpulse.ui.components.AmbientPhaseBackdrop
import com.fathan.ironpulse.ui.components.IronPulseBottomNav
import com.fathan.ironpulse.ui.components.IronPulseTopBar
import com.fathan.ironpulse.ui.components.LiveFluidClockSlot
import com.fathan.ironpulse.ui.components.MascotOrbFace
import com.fathan.ironpulse.ui.components.PhaseLogoEmblem
import com.fathan.ironpulse.ui.components.ScheduleTopProgramStrip
import com.fathan.ironpulse.ui.screens.AuthScreen
import com.fathan.ironpulse.ui.screens.HeroAnimatedAtmosphereLayer
import com.fathan.ironpulse.ui.theme.IronPulseTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import com.fathan.ironpulse.ui.viewmodel.RestTimerDisplayState
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StartupSmokeTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun authScreen_composesWithMascotGreetingAndSignInButton() {
        val eyeFlow = MutableStateFlow(MascotEyeState())
        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID
                )
            ) {
                AuthScreen(
                    authLoading = false,
                    authError = null,
                    onSelectLang = {},
                    onSignInClick = {},
                    mascot = MascotUiState(),
                    mascotEyeFlow = eyeFlow
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("auth_mascot_greeting_card").assertIsDisplayed()
        composeRule.onNodeWithTag("auth_mascot_orb", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithText("Halo! Saya Pulse").assertIsDisplayed()
        composeRule.onNodeWithTag("auth_mascot_greeting_card").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Siap Bangun Kebiasaan!").assertIsDisplayed()
        composeRule.onNodeWithTag("google_sign_in_button")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Test
    fun chromeAndAllThreeThemes_composeWithoutBlurAndRenderCleanly() {
        val restFlow = MutableStateFlow(RestTimerDisplayState())
        var themeSetter: ((UiTheme) -> Unit)? = null

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            themeSetter = { activeTheme = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID
                )
            ) {
                val tokens = LocalIronPulseTokens.current
                Box(modifier = Modifier.fillMaxSize()) {
                    AmbientPhaseBackdrop()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .ironGlassSurface(tokens)
                            .padding(16.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Set 1", modifier = Modifier.testTag("smoke_chip_1"))
                            Text("Set 2")
                        }
                    }
                    IronPulseTopBar(
                        activeTab = AppTab.TODAY,
                        aboutOpen = false,
                        onLogoClick = {},
                        onOpenSettings = {},
                        onOpenAbout = {},
                        onTestAlarm = {}
                    )
                    IronPulseBottomNav(
                        activeTab = AppTab.TODAY,
                        restTimerFlow = restFlow,
                        onSelectTab = {}
                    )
                }
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()
        }

        composeRule.onNodeWithTag("smoke_chip_1").assertIsDisplayed()
    }

    @Test
    fun phaseLogoEmblemAndHeroAtmosphere_composeAcrossAllDayPhases() {
        var phaseSetter: ((DayTimePhase) -> Unit)? = null
        composeRule.setContent {
            var phase by remember { mutableStateOf(DayTimePhase.MORNING) }
            phaseSetter = { phase = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.CARBON_FORGE,
                    lang = AppLanguage.ID
                ),
                timePhase = phase
            ) {
                CompositionLocalProvider(LocalTabActive provides true) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        HeroAnimatedAtmosphereLayer(
                            modifier = Modifier.fillMaxWidth().padding(0.dp),
                            noAnim = false
                        )
                        PhaseLogoEmblem(
                            timePhase = phase,
                            modifier = Modifier.testTag("phase_logo_emblem_smoke")
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        DayTimePhase.entries.forEach { phase ->
            composeRule.runOnUiThread { phaseSetter?.invoke(phase) }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("phase_logo_emblem_smoke").assertIsDisplayed()
        }
    }

    @Test
    fun renderIsolatedClockAndMascot_toggleTabActiveCleanly() {
        val clockFlow = MutableStateFlow(1_730_000_000L)
        val eyeFlow = MutableStateFlow(MascotEyeState())
        var tabActiveSetter: ((Boolean) -> Unit)? = null

        composeRule.setContent {
            var tabActive by remember { mutableStateOf(true) }
            tabActiveSetter = { tabActive = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID
                )
            ) {
                CompositionLocalProvider(LocalTabActive provides tabActive) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LiveFluidClockSlot(
                            clockSecondsFlow = clockFlow,
                            clockFormat = ClockFormat.H24,
                            lang = AppLanguage.ID
                        )
                        MascotOrbFace(
                            mascot = MascotUiState(),
                            eyeStateFlow = eyeFlow,
                            modifier = Modifier.testTag("mascot_orb_smoke")
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("mascot_orb_smoke").assertIsDisplayed()

        composeRule.runOnUiThread {
            tabActiveSetter?.invoke(false)
            clockFlow.value = 1_730_000_001L
            eyeFlow.value = MascotEyeState(isBlinking = true)
        }
        composeRule.waitForIdle()

        composeRule.runOnUiThread {
            tabActiveSetter?.invoke(true)
            clockFlow.value = 1_730_000_002L
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("mascot_orb_smoke").assertIsDisplayed()
    }

    @Test
    fun aboutDialog_rendersVersionAlphaAndCleanCredits() {
        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID
                )
            ) {
                AboutDialog(onDismiss = {})
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("About IronPulse").assertIsDisplayed()
        composeRule.onNodeWithText("v0.0.1 alpha").assertIsDisplayed()
        composeRule.onNodeWithText("scripter").assertIsDisplayed()
        composeRule.onNodeWithText("Fathan, and Artificial intelligence").assertIsDisplayed()
        composeRule.onNodeWithText("concept").assertIsDisplayed()
        composeRule.onNodeWithText("fathan.").assertIsDisplayed()
        composeRule.onNodeWithText("Tentang Aplikasi").assertIsDisplayed()
        composeRule.onNodeWithText("Hello world").assertDoesNotExist()
    }

    @Test
    fun uiTheme_hasThreeLightweightThemesAndLegacyWireFallback() {
        assertEquals(3, UiTheme.entries.size)
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("midnight-frosted"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("aeroglass"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("obsidian"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("unknown-theme"))
        assertEquals(UiTheme.CARBON_FORGE, UiTheme.fromWire("carbon-forge"))
        assertEquals(UiTheme.UBUNTU_YARU, UiTheme.fromWire("ubuntu-yaru"))
    }

    @Test
    fun preferencesStore_persistsPerformanceFlagsAndPresetsWithoutBlur() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val store = PreferencesStore(context)

        store.applyPerformancePreset(PerformancePresetId.ULTRA)
        assertEquals(
            PerformancePreferences(noAnimations = true, noAmbientGlow = true, staticMascot = true),
            store.state.value.performance
        )

        store.applyPerformancePreset(PerformancePresetId.BATTERY)
        assertEquals(
            PerformancePreferences(noAnimations = false, noAmbientGlow = true, staticMascot = false),
            store.state.value.performance
        )

        store.applyPerformancePreset(PerformancePresetId.BALANCED)
        assertEquals(
            PerformancePreferences(noAnimations = false, noAmbientGlow = false, staticMascot = false),
            store.state.value.performance
        )

        store.setPerformance(noAnimations = true, staticMascot = true)
        assertTrue(store.state.value.performance.noAnimations)
        assertFalse(store.state.value.performance.noAmbientGlow)
        assertTrue(store.state.value.performance.staticMascot)

        store.applyPerformancePreset(PerformancePresetId.BALANCED)
    }

    @Test
    fun restTimerService_stopClearsActiveState() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        RestTimerService.stop(context)
        assertFalse(RestTimerService.state.value.isActive)
    }

    @Test
    fun exerciseLibrary_hasExpandedUniqueMovementsAcrossAllGroups() {
        val lib = GymPresets.EXERCISE_LIBRARY
        assertTrue("Expected at least 120 exercises, was ${lib.size}", lib.size >= 120)

        val uniqueNames = lib.map { it.name.trim().lowercase(Locale.ROOT) }.toSet()
        assertEquals("Exercise names must be unique", lib.size, uniqueNames.size)

        MuscleGroup.entries.forEach { group ->
            val count = lib.count { it.muscleGroup == group }
            assertTrue("Expected at least 12 exercises in $group, found $count", count >= 12)
        }
    }

    @Test
    fun localGymCacheStore_savesAndHydratesSnapshotInstantly() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val cacheStore = LocalGymCacheStore(context)
        val uid = "cache_test_user_001"
        cacheStore.clearUser(uid)

        val defaults = GymPresets.createDefaultSchedules(uid)
        val sampleLog = WorkoutLog(
            userId = uid,
            dateKey = "2026-11-21",
            dayKey = DayKey.SAT,
            focusTitle = "Full Body",
            entries = listOf(
                WorkoutLogEntry(
                    exerciseId = "ex_1",
                    name = "Bench Press",
                    muscleGroup = MuscleGroup.DADA,
                    sets = listOf(WorkoutSetLog(setNumber = 1, weightKg = 60.0, reps = 10, completed = true))
                )
            ),
            completed = true
        )
        val snapshot = GymCachedSnapshot(
            userInitialized = true,
            profile = UserProfile(userId = uid, displayName = "Fathan", email = "fathan@example.com"),
            schedules = defaults,
            customRingtones = listOf(
                CustomRingtone(
                    id = "tone_1",
                    userId = uid,
                    name = "Bell",
                    mimeType = "audio/mpeg",
                    sizeBytes = 1024L,
                    durationSeconds = 5,
                    storageMode = RingtoneStorageMode.CLOUD
                )
            ),
            todayDateKey = "2026-11-21",
            todayLog = sampleLog,
            workoutHistory = listOf(sampleLog),
            weeklyStreak = 4
        )

        cacheStore.saveSnapshot(uid, snapshot)
        val loaded = cacheStore.loadSnapshot(uid)
        assertNotNull(loaded)
        assertEquals(true, loaded!!.userInitialized)
        assertEquals("Fathan", loaded.profile?.displayName)
        assertEquals(7, loaded.schedules.size)
        assertEquals(1, loaded.customRingtones.size)
        assertEquals("2026-11-21", loaded.todayDateKey)
        assertEquals(1, loaded.workoutHistory.size)
        assertEquals(4, loaded.weeklyStreak)

        cacheStore.clearUser(uid)
        assertNull(cacheStore.loadSnapshot(uid))
    }

    @Test
    fun authScreen_displaysSignInErrorMessageWhenAuthErrorProvided() {
        val eyeFlow = MutableStateFlow(MascotEyeState())
        val errorMessage = "Pemilihan akun Google terhenti. Pastikan koneksi & Google Play Services aktif."
        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID
                )
            ) {
                AuthScreen(
                    authLoading = false,
                    authError = errorMessage,
                    onSelectLang = {},
                    onSignInClick = {},
                    mascot = MascotUiState(),
                    mascotEyeFlow = eyeFlow
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithText(errorMessage)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun firestoreRepository_usesMemoryOnlyCacheWithoutDiskPersistence() {
        val settings = FirestoreGymRepository.buildMemoryOnlySettings()
        val cacheSettings = settings.cacheSettings
        assertTrue(
            "Expected FirestoreGymRepository to configure MemoryCacheSettings",
            cacheSettings is MemoryCacheSettings
        )
        assertFalse(cacheSettings is PersistentCacheSettings)
        val memorySettings = cacheSettings as MemoryCacheSettings
        assertTrue(
            "Expected eager memory GC to minimize Firestore RAM footprint",
            memorySettings.garbageCollectorSettings is MemoryEagerGcSettings
        )
    }

    @Test
    fun officialProgramTemplates_areValidAndRespectFirestoreRules() {
        val templates = ProgramTemplatePresets.OFFICIAL_TEMPLATES
        assertEquals(3, templates.size)
        val libraryNames = GymPresets.EXERCISE_LIBRARY.map { it.name }.toSet()
        val timeRegex = Regex("^([01]\\d|2[0-3]):([0-5]\\d)$")

        templates.forEach { tmpl ->
            val map = tmpl.buildSchedules("test_uid")
            assertEquals("Template ${tmpl.id} must define all 7 days", 7, map.size)
            val gymDays = map.values.count { it.isGymDay }
            assertEquals("Template ${tmpl.id} gym day count mismatch", tmpl.daysPerWeek, gymDays)
            map.values.forEach { sched ->
                assertTrue(sched.focusTitle.length in 1..80)
                assertTrue(timeRegex.matches(sched.alarmTime))
                assertTrue(sched.exercises.size <= 30)
                if (!sched.isGymDay) {
                    assertFalse("Rest day alarm must be disabled", sched.alarmEnabled)
                }
                sched.exercises.forEach { ex ->
                    assertTrue(
                        "Exercise '${ex.name}' in ${tmpl.id} must exist in EXERCISE_LIBRARY",
                        ex.name in libraryNames
                    )
                }
            }
        }
    }

    @Test
    fun scheduleProgramMerger_preservesUserAlarmsOnGymDaysAndDisablesOnRestDays() {
        val uid = "merge_uid"
        val existing = GymPresets.createDefaultSchedules(uid).mapValues { (_, sched) ->
            sched.copy(alarmTime = "18:15", ringtoneId = "custom_tone_99", alarmEnabled = true)
        }
        val pplUl = ProgramTemplatePresets.findById(ProgramTemplatePresets.ID_PPL_UPPER_LOWER)!!
            .buildSchedules(uid)

        val merged = ScheduleProgramMerger.mergeWithExistingAlarms(
            target = pplUl,
            existing = existing,
            preserveAlarmsAndRingtones = true,
            userId = uid
        )

        assertEquals("18:15", merged[DayKey.MON]?.alarmTime)
        assertEquals("custom_tone_99", merged[DayKey.MON]?.ringtoneId)
        assertTrue(merged[DayKey.MON]?.alarmEnabled == true)
        // Thursday is a rest day in PPL x Upper-Lower -> alarm must be auto-disabled
        assertFalse(merged[DayKey.THU]?.isGymDay == true)
        assertFalse(merged[DayKey.THU]?.alarmEnabled == true)
    }

    @Test
    fun scheduleProfileStore_persistsSavedSchedulesAndActiveSelectionPerUid() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val store = ScheduleProfileStore(context)
        val uid = "profile_store_test_uid"
        store.clearUser(uid)

        val customSchedules = GymPresets.createDefaultSchedules(uid)
        val saved = SavedCustomSchedule(
            id = "custom_1",
            name = "Jadwal Pribadi",
            isAutoBackup = false,
            updatedAtMs = 1_730_000_000_000L,
            schedules = customSchedules
        )
        store.save(
            uid,
            ScheduleLibraryState(
                savedSchedules = listOf(saved),
                activeProfileId = "custom_1",
                activeProfileName = "Jadwal Pribadi"
            )
        )

        val loaded = store.load(uid)
        assertEquals(1, loaded.savedSchedules.size)
        assertEquals("Jadwal Pribadi", loaded.savedSchedules.first().name)
        assertEquals(7, loaded.savedSchedules.first().schedules.size)
        assertEquals("custom_1", loaded.activeProfileId)
        assertEquals("Jadwal Pribadi", loaded.activeProfileName)

        store.clearUser(uid)
        assertTrue(store.load(uid).savedSchedules.isEmpty())
    }

    @Test
    fun scheduleTopProgramStrip_displaysSavedAndOfficialTemplatesAndHandlesClicks() {
        var clickedOfficialId: String? = null
        var clickedSavedId: String? = null
        var saveDialogOpened = false

        val savedBackup = SavedCustomSchedule(
            id = ScheduleProfileStore.AUTO_BACKUP_ID,
            name = "Jadwal Sebelumnya",
            isAutoBackup = true,
            schedules = GymPresets.createDefaultSchedules("u1")
        )

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID
                )
            ) {
                ScheduleTopProgramStrip(
                    savedSchedules = listOf(savedBackup),
                    officialTemplates = ProgramTemplatePresets.OFFICIAL_TEMPLATES,
                    activeProfileId = ProgramTemplatePresets.ID_PPL_UPPER_LOWER,
                    applyingSchedule = false,
                    onOpenSaveDialog = { saveDialogOpened = true },
                    onSelectSaved = { clickedSavedId = it.id },
                    onSelectOfficial = { clickedOfficialId = it.id }
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("schedule_top_program_strip").assertIsDisplayed()
        composeRule.onNodeWithTag("schedule_card_${ScheduleProfileStore.AUTO_BACKUP_ID}")
            .assertIsDisplayed()
            .performClick()
        assertEquals(ScheduleProfileStore.AUTO_BACKUP_ID, clickedSavedId)

        composeRule.onNodeWithTag("schedule_card_${ProgramTemplatePresets.ID_PPL_UPPER_LOWER}")
            .assertIsDisplayed()
            .performClick()
        assertEquals(ProgramTemplatePresets.ID_PPL_UPPER_LOWER, clickedOfficialId)

        composeRule.onNodeWithTag("save_custom_schedule_button")
            .assertIsDisplayed()
            .performClick()
        assertTrue(saveDialogOpened)
    }
}
