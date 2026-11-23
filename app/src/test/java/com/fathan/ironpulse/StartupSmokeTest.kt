package com.fathan.ironpulse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
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
import com.fathan.ironpulse.model.DayTimeMode
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.GymModelHelpers
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
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.TimePhasePose
import com.fathan.ironpulse.ui.components.AboutDialog
import com.fathan.ironpulse.ui.components.AmbientPhaseBackdrop
import com.fathan.ironpulse.ui.components.ConfirmDeleteDialog
import com.fathan.ironpulse.ui.components.IronPulseBottomNav
import com.fathan.ironpulse.ui.components.IronPulseTopBar
import com.fathan.ironpulse.ui.components.LiveFluidClockSlot
import com.fathan.ironpulse.ui.components.PulseMascotTutorialOverlay
import com.fathan.ironpulse.ui.components.MascotOrbFace
import com.fathan.ironpulse.ui.components.PhaseLogoEmblem
import com.fathan.ironpulse.ui.components.SaveCustomScheduleDialog
import com.fathan.ironpulse.ui.components.ScheduleTopProgramStrip
import com.fathan.ironpulse.ui.components.ThemeBrandLockup
import com.fathan.ironpulse.ui.components.TodayMascot
import com.fathan.ironpulse.ui.components.TutorialSpotlightRegistry
import com.fathan.ironpulse.ui.components.VisualColorPickerDialog
import com.fathan.ironpulse.ui.components.VisualColorPickerMode
import com.fathan.ironpulse.ui.components.isClosedEyeExpression
import com.fathan.ironpulse.ui.components.resolveMascotOrbPalette
import com.fathan.ironpulse.ui.screens.AccentAndTintSettingsCard
import com.fathan.ironpulse.ui.screens.AuthScreen
import com.fathan.ironpulse.ui.screens.HeroAnimatedAtmosphereLayer
import com.fathan.ironpulse.ui.screens.TodayScreen
import com.fathan.ironpulse.ui.screens.WorkoutSessionSummaryCard
import com.fathan.ironpulse.ui.theme.IronPulseTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.resolveThemeFontPack
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotReaction
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
            eyeFlow.value = MascotEyeState(blinkProgress = 1f)
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
    fun uiTheme_hasFiveThemesIncludingFrutigerAeroAndLegacyFallback() {
        assertEquals(5, UiTheme.entries.size)
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("midnight-frosted"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("aeroglass"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("obsidian"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("unknown-theme"))
        assertEquals(UiTheme.CARBON_FORGE, UiTheme.fromWire("carbon-forge"))
        assertEquals(UiTheme.UBUNTU_YARU, UiTheme.fromWire("ubuntu-yaru"))
        assertEquals(UiTheme.AMOLED_STEALTH, UiTheme.fromWire("amoled-stealth"))
        assertEquals(UiTheme.FRUTIGER_AERO, UiTheme.fromWire("frutiger-aero"))
    }

    @Test
    fun amoledStealthTheme_usesPitchBlackCanvasStealthCyanDefaultAndSupportsCustomAccent() {
        var defaultCanvasBg: Color? = null
        var defaultAccent: Color? = null
        var customAccent: Color? = null

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.AMOLED_STEALTH,
                    accentColorHex = "#C8FF2E"
                )
            ) {
                val t = LocalIronPulseTokens.current
                defaultCanvasBg = t.canvasBg
                defaultAccent = t.accentColor
            }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.AMOLED_STEALTH,
                    accentColorHex = "#FF4D8D"
                )
            ) {
                customAccent = LocalIronPulseTokens.current.accentColor
            }
        }

        composeRule.waitForIdle()
        assertEquals(Color(0xFF000000), defaultCanvasBg)
        assertEquals(Color(0xFF00E5FF), defaultAccent)
        assertEquals(Color(0xFFFF4D8D), customAccent)
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

    @Test
    fun pulseMascotTutorial_greetsUserSelectsLanguageAndToursAllFiveTabs() {
        val visitedTabs = mutableListOf<AppTab>()
        val chirpsPlayed = mutableListOf<String>()
        var mascotTappedCount = 0
        var completed = false
        val spotlightRegistry = TutorialSpotlightRegistry().apply { active = true }

        composeRule.setContent {
            var activeLang by remember { mutableStateOf(AppLanguage.ID) }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.AMOLED_STEALTH,
                    lang = activeLang,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                PulseMascotTutorialOverlay(
                    userName = "Fathan Pratama",
                    baseMascot = MascotUiState(),
                    mascotEyeFlow = null,
                    startAtWelcomeLanguageStep = true,
                    spotlightRegistry = spotlightRegistry,
                    onSelectLanguage = { activeLang = it },
                    onNavigateToStepTab = { visitedTabs.add(it) },
                    onPlayChirp = { chirpsPlayed.add(it) },
                    onTapMascotFeedback = { mascotTappedCount += 1 },
                    onComplete = { completed = true }
                )
            }
        }

        composeRule.waitForIdle()
        // Welcome bubble stage greets by first name and allows selecting language
        composeRule.onNodeWithTag("pulse_tutorial_welcome_card").assertIsDisplayed()
        composeRule.onNodeWithText("Hai, Fathan! Aku Pulse").assertIsDisplayed()
        composeRule.onNodeWithTag("pulse_tutorial_start_hint").assertIsDisplayed()

        // Tap Pulse mascot on welcome stage
        composeRule.onNodeWithTag("pulse_tutorial_mascot").performClick()
        assertEquals(1, mascotTappedCount)

        // Switch language to English and back to Indonesian without advancing step
        composeRule.onNodeWithTag("pulse_tutorial_lang_en").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Hi, Fathan! I'm Pulse").assertIsDisplayed()

        composeRule.onNodeWithTag("pulse_tutorial_lang_id").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Hai, Fathan! Aku Pulse").assertIsDisplayed()

        val tapScreen = {
            composeRule.onNodeWithTag("pulse_tutorial_tap_area")
                .performTouchInput { click(Offset(14f, 14f)) }
            composeRule.waitForIdle()
        }

        // Tap anywhere on screen -> Step 1: Today tab Rest Timer spotlight
        tapScreen()
        assertEquals(AppTab.TODAY, visitedTabs.last())
        composeRule.onNodeWithTag("pulse_tutorial_bubble_dock").assertIsDisplayed()
        composeRule.onNodeWithText("Tab Hari Ini · Timer Istirahat").assertIsDisplayed()

        // Step 2: Schedule tab (Template Program PPL Upper-Lower + Saved)
        tapScreen()
        assertEquals(AppTab.SCHEDULE, visitedTabs.last())
        composeRule.onNodeWithText("Tab Jadwal · Template & Tersimpan").assertIsDisplayed()

        // Step 3: Exercises tab
        tapScreen()
        assertEquals(AppTab.EXERCISES, visitedTabs.last())

        // Step 4: History tab
        tapScreen()
        assertEquals(AppTab.HISTORY, visitedTabs.last())

        // Step 5: Settings finale
        tapScreen()
        assertEquals(AppTab.SETTINGS, visitedTabs.last())

        // Final tap returns Pulse back to Today tab and finishes tutorial
        tapScreen()
        assertEquals("Expected tutorial to return to Today tab on finish", AppTab.TODAY, visitedTabs.last())
        assertTrue("Expected tutorial onComplete to fire after final tap", completed)
        assertTrue("Expected soft chirps to play during tour", chirpsPlayed.contains("celebrate"))
    }

    @Test
    fun pulseMascotTutorial_skipShowsPulseConfirmationAndSupportsResumeOrSkip() {
        val visitedTabs = mutableListOf<AppTab>()
        var completed = false

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                PulseMascotTutorialOverlay(
                    userName = "Raka",
                    baseMascot = MascotUiState(),
                    mascotEyeFlow = null,
                    startAtWelcomeLanguageStep = false,
                    onSelectLanguage = {},
                    onNavigateToStepTab = { visitedTabs.add(it) },
                    onPlayChirp = {},
                    onTapMascotFeedback = {},
                    onComplete = { completed = true }
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pulse_tutorial_bubble_dock").assertIsDisplayed()

        // Advance to Schedule tab before testing skip return-to-Today
        composeRule.onNodeWithTag("pulse_tutorial_tap_area")
            .performTouchInput { click(Offset(14f, 14f)) }
        composeRule.waitForIdle()
        assertEquals(AppTab.SCHEDULE, visitedTabs.last())

        // Tap Skip -> Pulse shows confirmation bubble card
        composeRule.onNodeWithTag("pulse_tutorial_skip_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pulse_tutorial_skip_confirm_card").assertIsDisplayed()
        assertFalse(completed)

        // Resume tour returns to bubble dock
        composeRule.onNodeWithTag("pulse_tutorial_resume_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pulse_tutorial_bubble_dock").assertIsDisplayed()
        assertFalse(completed)

        // Confirm skip returns to Today tab and finishes tutorial
        composeRule.onNodeWithTag("pulse_tutorial_skip_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("pulse_tutorial_confirm_skip_button").performClick()
        composeRule.waitForIdle()
        assertEquals(AppTab.TODAY, visitedTabs.last())
        assertTrue(completed)
    }

    @Test
    fun materialYouColorEngine_hsvRoundTripAndFriendlyNames() {
        val sampleColor = Color(0xFF00E5FF)
        val hsv = MaterialYouColorEngine.colorToHsv(sampleColor)
        assertEquals(186f, hsv[0], 2f)
        assertEquals(1f, hsv[1], 0.02f)
        assertEquals(1f, hsv[2], 0.02f)
        assertEquals("#00E5FF", MaterialYouColorEngine.colorToHex(Color.hsv(hsv[0], hsv[1], hsv[2])))

        val presetName = MaterialYouColorEngine.friendlyColorName(
            hex = "#C8FF2E",
            presets = GymModelHelpers.ACCENT_COLOR_PRESETS,
            lang = AppLanguage.ID
        )
        assertEquals("Hijau Limau", presetName)

        val customName = MaterialYouColorEngine.friendlyColorName(
            hex = "#4F46E5",
            presets = GymModelHelpers.ACCENT_COLOR_PRESETS,
            lang = AppLanguage.ID
        )
        assertFalse(customName.contains("#"))
        assertTrue(customName.contains("Kustom"))
    }

    @Test
    fun visualColorPicker_hidesHexCodesAndAppliesVisualAccentAndTint() {
        var appliedAccentHex: String? = null
        var appliedTintHex: String? = null

        composeRule.setContent {
            var activeMode by remember { mutableStateOf<VisualColorPickerMode?>(null) }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    accentColorHex = "#C8FF2E",
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true)
                )
            ) {
                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AccentAndTintSettingsCard(
                        accentColorHex = appliedAccentHex ?: "#C8FF2E",
                        materialTintColorHex = appliedTintHex,
                        onSelectAccent = { appliedAccentHex = it },
                        onOpenAccentPicker = { activeMode = VisualColorPickerMode.ACCENT },
                        onOpenTintPicker = { activeMode = VisualColorPickerMode.SURFACE_TINT },
                        onSelectMaterialTint = { appliedTintHex = it }
                    )
                }

                val mode = activeMode
                if (mode != null) {
                    VisualColorPickerDialog(
                        mode = mode,
                        initialHex = if (mode == VisualColorPickerMode.ACCENT) "#C8FF2E" else "#1C1B22",
                        quickPresets = if (mode == VisualColorPickerMode.ACCENT) {
                            GymModelHelpers.ACCENT_COLOR_PRESETS
                        } else {
                            GymModelHelpers.MATERIAL_TINT_PRESETS
                        },
                        lang = AppLanguage.ID,
                        onApplyHex = { chosen ->
                            if (mode == VisualColorPickerMode.ACCENT) {
                                appliedAccentHex = chosen
                            } else {
                                appliedTintHex = chosen
                            }
                            activeMode = null
                        },
                        onDismiss = { activeMode = null }
                    )
                }
            }
        }

        composeRule.waitForIdle()
        // Zero raw HEX codes or "# Kustom" in settings UI
        composeRule.onNodeWithText("#C8FF2E").assertDoesNotExist()
        composeRule.onNodeWithText("# Kustom").assertDoesNotExist()
        composeRule.onNodeWithTag("accent_active_visual_badge").assertIsDisplayed()
        composeRule.onNodeWithText("Hijau Limau").assertIsDisplayed()

        // Open visual Accent Color Picker
        composeRule.onNodeWithTag("accent_pick_color_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("visual_color_picker_dialog").assertIsDisplayed()
        composeRule.onNodeWithTag("color_picker_sv_canvas").assertIsDisplayed()
        composeRule.onNodeWithTag("color_picker_hue_bar").assertIsDisplayed()
        composeRule.onNodeWithTag("color_picker_live_preview").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("#RRGGBB").assertDoesNotExist()

        // Drag across hue spectrum bar & SV canvas visually, then Apply
        composeRule.onNodeWithTag("color_picker_hue_bar").performScrollTo().performTouchInput {
            click(Offset(width * 0.55f, height * 0.5f))
        }
        composeRule.onNodeWithTag("color_picker_sv_canvas").performScrollTo().performTouchInput {
            click(Offset(width * 0.78f, height * 0.25f))
        }
        composeRule.onNodeWithTag("color_picker_apply_button").performClick()
        composeRule.waitForIdle()
        assertNotNull(appliedAccentHex)

        // Open visual Material Tint picker and apply
        composeRule.onNodeWithTag("tint_pick_color_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("visual_color_picker_dialog").assertIsDisplayed()
        composeRule.onNodeWithTag("color_picker_apply_button").performClick()
        composeRule.waitForIdle()
        assertNotNull(appliedTintHex)
    }

    @Test
    fun pulseMascot_omitsQuickActionButtonsAndSupportsTapAndPetting() {
        var tappedCount = 0
        var pettedCount = 0
        val eyeFlow = MutableStateFlow(
            MascotEyeState(
                blinkProgress = 0.85f,
                glanceX = 1.6f,
                headTiltDeg = 4.2f,
                reactionKind = MascotReaction.COMBO_EXCITED,
                reactionTick = 1,
                isPetting = false,
                comboCount = 3
            )
        )

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.AMOLED_STEALTH,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true)
                )
            ) {
                TodayMascot(
                    mascot = MascotUiState(),
                    eyeStateFlow = eyeFlow,
                    voiceEnabled = false,
                    onTapMascot = { tappedCount += 1 },
                    onPetMascot = { pettedCount += 1 },
                    onReplayVoice = {},
                    modifier = Modifier.testTag("today_mascot_card")
                )
            }
        }

        composeRule.waitForIdle()
        // Quick action pills (Hype, Tip, Pose) must be completely removed
        composeRule.onNodeWithTag("mascot_quick_hype").assertDoesNotExist()
        composeRule.onNodeWithTag("mascot_quick_tip").assertDoesNotExist()
        composeRule.onNodeWithTag("mascot_quick_pose").assertDoesNotExist()
        composeRule.onNodeWithText("Semangati").assertDoesNotExist()
        composeRule.onNodeWithText("Tips").assertDoesNotExist()
        composeRule.onNodeWithText("Gaya").assertDoesNotExist()

        composeRule.onNodeWithTag("today_mascot_card").performClick()
        assertEquals(1, tappedCount)

        composeRule.onNodeWithTag("today_mascot_card").performTouchInput { longClick() }
        composeRule.waitForIdle()
        assertEquals(1, pettedCount)
    }

    @Test
    fun pulseMascot_closedEyeStillnessAndDayNightAutoBehaviorRules() {
        // Every closed-eye pose & mood must lock glance & tilt off
        val closedPoses = listOf(
            TimePhasePose.SLEEP,
            TimePhasePose.ZEN,
            TimePhasePose.COZY,
            TimePhasePose.DREAM,
            TimePhasePose.TEA,
            TimePhasePose.SUNRISE,
            TimePhasePose.MUSIC,
            TimePhasePose.SWEAT,
            TimePhasePose.RECHARGE,
            TimePhasePose.YAWN
        )
        for (pose in closedPoses) {
            val mascot = MascotUiState(urgentMood = null, activeTimePose = pose)
            assertTrue("Expected $pose to count as closed-eye", GymViewModel.isMascotClosedEyeState(mascot, MascotEyeState()))
            assertTrue("Expected UI $pose to lock closed-eye stillness", isClosedEyeExpression(mascot, isPetting = false))
        }

        val closedMoods = listOf(
            MascotMood.REST,
            MascotMood.CLAP,
            MascotMood.CELEBRATE,
            MascotMood.MILESTONE
        )
        for (mood in closedMoods) {
            val mascot = MascotUiState(urgentMood = mood, activeTimePose = TimePhasePose.FLEX)
            assertTrue("Expected mood $mood to count as closed-eye", GymViewModel.isMascotClosedEyeState(mascot, MascotEyeState()))
            assertTrue("Expected UI mood $mood to lock closed-eye stillness", isClosedEyeExpression(mascot, isPetting = false))
        }

        // Open-eye daytime poses allow glance/tilt
        val openPoses = listOf(TimePhasePose.FLEX, TimePhasePose.BEAST, TimePhasePose.CURIOUS, TimePhasePose.COFFEE)
        for (pose in openPoses) {
            val mascot = MascotUiState(urgentMood = null, activeTimePose = pose)
            assertFalse("Expected $pose to allow open-eye glance", GymViewModel.isMascotClosedEyeState(mascot, MascotEyeState()))
            assertFalse("Expected UI $pose to allow open-eye glance", isClosedEyeExpression(mascot, isPetting = false))
            // Petting overrides any pose into closed-eye cozy state
            assertTrue(GymViewModel.isMascotClosedEyeState(mascot, MascotEyeState(isPetting = true)))
            assertTrue(isClosedEyeExpression(mascot, isPetting = true))
        }

        // Automatic Tip triggers only in Morning & Afternoon (never Night)
        assertTrue(GymViewModel.shouldTriggerAutoDaytimeTip(DayTimePhase.MORNING, null))
        assertTrue(GymViewModel.shouldTriggerAutoDaytimeTip(DayTimePhase.AFTERNOON, null))
        assertFalse(GymViewModel.shouldTriggerAutoDaytimeTip(DayTimePhase.NIGHT, null))
        assertFalse(GymViewModel.shouldTriggerAutoDaytimeTip(DayTimePhase.MORNING, MascotMood.TIMER_ACTIVE))

        // Nighttime sleep chat gap is much longer (rare) than daytime
        assertTrue(GymViewModel.mascotAutoChatIntervalMs(DayTimePhase.NIGHT) >= 240_000L)
        assertTrue(GymViewModel.mascotAutoChatIntervalMs(DayTimePhase.MORNING) < GymViewModel.mascotAutoChatIntervalMs(DayTimePhase.NIGHT))
    }

    @Test
    fun preferencesStore_persistsWorkoutFocusHudEnabled() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val store = PreferencesStore(context)
        store.setWorkoutFocusHudEnabled(false)
        assertFalse(store.state.value.workoutFocusHudEnabled)

        store.setWorkoutFocusHudEnabled(true)
        assertTrue(store.state.value.workoutFocusHudEnabled)

        val reloaded = PreferencesStore(context)
        assertTrue(reloaded.state.value.workoutFocusHudEnabled)

        store.setWorkoutFocusHudEnabled(false)
        assertFalse(PreferencesStore(context).state.value.workoutFocusHudEnabled)
    }

    @Test
    fun gymFocusHudStage_switchesExercisesAdjustsMetricsAndControlsRestRing() {
        val restFlow = MutableStateFlow(RestTimerDisplayState())
        val timersFlow = MutableStateFlow(emptyMap<String, com.fathan.ironpulse.ui.viewmodel.ExerciseTimerUiState>())
        val cooldownFlow = MutableStateFlow(0)

        val entries = listOf(
            WorkoutLogEntry(
                exerciseId = "ex_bench",
                name = "Bench Press",
                muscleGroup = MuscleGroup.DADA,
                usesWeight = true,
                sets = listOf(
                    WorkoutSetLog(setNumber = 1, weightKg = 60.0, reps = 10, completed = false),
                    WorkoutSetLog(setNumber = 2, weightKg = 60.0, reps = 10, completed = false)
                )
            ),
            WorkoutLogEntry(
                exerciseId = "ex_plank",
                name = "Plank Hold",
                muscleGroup = MuscleGroup.CORE,
                trackingMode = com.fathan.ironpulse.model.ExerciseTrackingMode.TIMER,
                usesWeight = false,
                targetDurationSeconds = 45,
                sets = listOf(
                    WorkoutSetLog(setNumber = 1, weightKg = 0.0, reps = 1, durationSeconds = 45, completed = false)
                )
            )
        )
        val specs = listOf(
            com.fathan.ironpulse.model.ExerciseItem(
                id = "ex_bench",
                name = "Bench Press",
                muscleGroup = MuscleGroup.DADA,
                targetSets = 2,
                targetReps = "8-10",
                targetWeightKg = 60.0,
                restSeconds = 90,
                equipment = "Barbell",
                notes = "Kunci bahu ke belakang"
            ),
            com.fathan.ironpulse.model.ExerciseItem(
                id = "ex_plank",
                name = "Plank Hold",
                muscleGroup = MuscleGroup.CORE,
                equipment = "Bodyweight",
                targetSets = 1,
                targetReps = "45d",
                targetWeightKg = 0.0,
                restSeconds = 45,
                notes = "Pertahankan pinggul sejajar",
                trackingMode = com.fathan.ironpulse.model.ExerciseTrackingMode.TIMER,
                usesWeight = false,
                targetDurationSeconds = 45
            )
        )

        var activeIdx by mutableStateOf(0)
        var currentEntries by mutableStateOf(entries)
        var completedTriggerCount = 0
        val adjustDeltas = mutableListOf<Int>()
        var restToggledCount = 0
        var restSkippedCount = 0
        var restDismissed by mutableStateOf(false)

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.AMOLED_STEALTH,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true)
                )
            ) {
                Box(modifier = Modifier.verticalScroll(rememberScrollState()).padding(12.dp)) {
                    val entry = currentEntries[activeIdx]
                    com.fathan.ironpulse.ui.screens.GymFocusHudStage(
                        index = activeIdx,
                        totalExercises = currentEntries.size,
                        entry = entry,
                        spec = specs[activeIdx],
                        notesExpanded = false,
                        onToggleNotes = {},
                        restBarDismissed = restDismissed,
                        restTimerFlow = restFlow,
                        exerciseTimersFlow = timersFlow,
                        cooldownSecondsFlow = cooldownFlow,
                        onPrevExercise = { if (activeIdx > 0) activeIdx -= 1 },
                        onNextExercise = { if (activeIdx < currentEntries.lastIndex) activeIdx += 1 },
                        onStartRest = { secs ->
                            restDismissed = false
                            restFlow.value = RestTimerDisplayState(
                                isActive = true,
                                remainingSeconds = secs,
                                totalSeconds = secs,
                                exerciseName = entry.name
                            )
                        },
                        onAdjustRest = { delta -> adjustDeltas.add(delta) },
                        onToggleRestPause = { restToggledCount += 1 },
                        onDismissRest = {
                            restDismissed = true
                            restSkippedCount += 1
                        },
                        onUndismissRest = { restDismissed = false },
                        onToggleSetComplete = { setNum ->
                            completedTriggerCount += 1
                            currentEntries = currentEntries.map { e ->
                                if (e.exerciseId == entry.exerciseId) {
                                    e.copy(sets = e.sets.map { s ->
                                        if (s.setNumber == setNum) s.copy(completed = !s.completed) else s
                                    })
                                } else e
                            }
                        },
                        onUpdateSetMetrics = { setNum, w, r ->
                            currentEntries = currentEntries.map { e ->
                                if (e.exerciseId == entry.exerciseId) {
                                    e.copy(sets = e.sets.map { s ->
                                        if (s.setNumber == setNum) s.copy(weightKg = w, reps = r) else s
                                    })
                                } else e
                            }
                        },
                        onUpdateSetDuration = { _, _ -> },
                        onToggleExerciseTimer = {},
                        onResetExerciseTimer = {},
                        onSelectExerciseTimerSet = {},
                        onChangeTimerDirection = {},
                        onSaveExerciseTimer = {},
                        onAddSet = {},
                        onRemoveSet = {},
                        secUnit = "d"
                    )
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("gym_focus_hud_stage").assertIsDisplayed()
        composeRule.onNodeWithTag("hud_exercise_title").assertIsDisplayed()
        composeRule.onNodeWithText("Bench Press").assertIsDisplayed()
        composeRule.onNodeWithTag("hud_rest_idle_strip").assertIsDisplayed()

        // Adjust weight (+2.5 kg) and reps (+1) via giant steppers
        composeRule.onNodeWithTag("hud_weight_inc").performScrollTo().performClick()
        composeRule.onNodeWithTag("hud_reps_inc").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("62.5").assertIsDisplayed()
        composeRule.onNodeWithText("11").assertIsDisplayed()

        // Complete Set #1 via oversized full-width CTA
        composeRule.onNodeWithTag("hud_complete_set_button").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(1, completedTriggerCount)

        // Start Rest Timer via recommended preset pill (90d) -> large countdown ring appears with -15d / +15d
        composeRule.onNodeWithTag("hud_rest_preset_90").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("hud_rest_ring_card").assertIsDisplayed()
        composeRule.onNodeWithTag("hud_rest_ring_visual").assertIsDisplayed()
        composeRule.onNodeWithText("01:30").assertIsDisplayed()

        composeRule.onNodeWithTag("hud_rest_minus_15").performScrollTo().performClick()
        composeRule.onNodeWithTag("hud_rest_plus_15").performScrollTo().performClick()
        composeRule.onNodeWithTag("hud_rest_toggle").performScrollTo().performClick()
        assertEquals(listOf(-15, 15), adjustDeltas)
        assertEquals(1, restToggledCount)

        composeRule.onNodeWithTag("hud_rest_skip").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(1, restSkippedCount)
        composeRule.onNodeWithTag("hud_rest_idle_strip").performScrollTo().assertIsDisplayed()

        // Switch to Next Exercise (Plank Hold - Timer Mode) and verify timer clock + Prev back
        composeRule.onNodeWithTag("hud_next_exercise").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Plank Hold").assertIsDisplayed()
        composeRule.onNodeWithTag("hud_exercise_timer_clock").performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithTag("hud_prev_exercise").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Bench Press").assertIsDisplayed()
    }

    @Test
    fun frutigerAeroTheme_usesLightSchemeLocksBlueAccentAndShowsLockedBanner() {
        var resolvedIsLight = false
        var resolvedAccentLocked = false
        var resolvedAccent: Color? = null
        var resolvedOnSurface: Color? = null

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.FRUTIGER_AERO,
                    accentColorHex = "#FF4D8D",
                    materialTintColorHex = "#24192E",
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true)
                )
            ) {
                val t = LocalIronPulseTokens.current
                resolvedIsLight = t.isLightTheme
                resolvedAccentLocked = t.accentLocked
                resolvedAccent = t.accentColor
                resolvedOnSurface = t.palette.onSurface

                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AccentAndTintSettingsCard(
                        accentColorHex = "#FF4D8D",
                        materialTintColorHex = "#24192E",
                        onSelectAccent = {},
                        onOpenAccentPicker = {},
                        onOpenTintPicker = {},
                        onSelectMaterialTint = {}
                    )
                }
            }
        }

        composeRule.waitForIdle()
        assertTrue("Frutiger Aero must use light theme tokens", resolvedIsLight)
        assertTrue("Frutiger Aero must lock accent customization", resolvedAccentLocked)
        assertEquals(
            "Frutiger Aero accent must stay permanently locked to Aero Sky Blue",
            Color(0xFF0099E5),
            resolvedAccent
        )
        assertEquals(
            "Frutiger Aero must use deep oceanic dark text on bright glass",
            Color(0xFF071E36),
            resolvedOnSurface
        )
        composeRule.onNodeWithTag("accent_locked_banner").assertIsDisplayed()
        composeRule.onNodeWithText("Biru Aero Terkunci").assertIsDisplayed()
        composeRule.onNodeWithTag("accent_pick_color_button").assertDoesNotExist()
        composeRule.onNodeWithTag("tint_pick_color_button").assertDoesNotExist()
    }

    @Test
    fun allFiveThemes_renderUniqueBrandLockupsEmblemsAndPhaseLogos() {
        var themeSetter: ((UiTheme) -> Unit)? = null
        var phaseSetter: ((DayTimePhase) -> Unit)? = null

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            var activePhase by remember { mutableStateOf(DayTimePhase.MORNING) }
            themeSetter = { activeTheme = it }
            phaseSetter = { activePhase = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true)
                ),
                timePhase = activePhase
            ) {
                val t = LocalIronPulseTokens.current
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThemeBrandLockup(
                        theme = t.uiTheme,
                        accentColor = t.accentColor
                    )
                    PhaseLogoEmblem(timePhase = activePhase)
                }
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            DayTimePhase.entries.forEach { phase ->
                composeRule.runOnUiThread { phaseSetter?.invoke(phase) }
                composeRule.waitForIdle()
                composeRule.onNodeWithTag("theme_brand_lockup_${theme.wireValue}").assertIsDisplayed()
                composeRule.onNodeWithTag("theme_brand_emblem_${theme.wireValue}", useUnmergedTree = true).assertIsDisplayed()
                composeRule.onNodeWithTag("theme_brand_wordmark_${theme.wireValue}", useUnmergedTree = true).assertIsDisplayed()
                composeRule.onNodeWithTag("phase_emblem_${theme.wireValue}_${phase.name.lowercase(Locale.US)}").assertIsDisplayed()
            }
        }
    }

    @Test
    fun allFiveOsThemes_provideUniqueBundledFontPacksAndBadges() {
        val packs = UiTheme.entries.map { resolveThemeFontPack(it) }
        assertEquals(5, packs.size)
        assertEquals(
            "Every theme must have a unique bundled display font",
            5,
            packs.map { it.displayFont }.distinct().size
        )
        assertEquals(
            "Every theme must have a unique font display name",
            5,
            packs.map { it.fontDisplayName }.distinct().size
        )
        assertEquals(
            "Every theme must have a unique OS badge label",
            5,
            packs.map { it.osBadgeLabel }.distinct().size
        )
    }

    @Test
    fun allFiveOsThemes_renderDistinctChromeBackdropsCustomIconsAndMascotOrbs() {
        var themeSetter: ((UiTheme) -> Unit)? = null
        val restFlow = MutableStateFlow(RestTimerDisplayState())

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            themeSetter = { activeTheme = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                ),
                timePhase = DayTimePhase.MORNING
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AmbientPhaseBackdrop()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        IronPulseTopBar(
                            activeTab = AppTab.TODAY,
                            aboutOpen = false,
                            onLogoClick = {},
                            onOpenSettings = {},
                            onOpenAbout = {},
                            onTestAlarm = {}
                        )
                        MascotOrbFace(
                            mascot = MascotUiState(),
                            size = 64.dp
                        )
                        IronPulseBottomNav(
                            activeTab = AppTab.TODAY,
                            restTimerFlow = restFlow,
                            onSelectTab = {}
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()

            composeRule.onNodeWithTag("os_backdrop_${theme.wireValue}").assertIsDisplayed()
            composeRule.onNodeWithTag("os_top_bar_${theme.wireValue}").assertIsDisplayed()
            composeRule.onNodeWithTag("os_bottom_nav_${theme.wireValue}").assertIsDisplayed()
            composeRule.onNodeWithTag("mascot_orb_${theme.wireValue}", useUnmergedTree = true).assertIsDisplayed()
            composeRule.onNodeWithTag("themed_icon_${theme.wireValue}_settings", useUnmergedTree = true).assertIsDisplayed()
            composeRule.onNodeWithTag("themed_icon_${theme.wireValue}_today", useUnmergedTree = true).assertIsDisplayed()
            composeRule.onNodeWithTag("themed_icon_${theme.wireValue}_schedule", useUnmergedTree = true).assertIsDisplayed()
            composeRule.onNodeWithTag("themed_icon_${theme.wireValue}_history", useUnmergedTree = true).assertIsDisplayed()
            composeRule.onNodeWithTag("themed_icon_${theme.wireValue}_exercises", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun frutigerAeroAndAllThemes_exposeHighContrastSemanticTokensAndReadableLightAccents() {
        fun linearChannel(c: Float): Double {
            val d = c.toDouble()
            return if (d <= 0.03928) d / 12.92 else Math.pow((d + 0.055) / 1.055, 2.4)
        }
        fun wcagContrastOnAeroLight(color: Color): Double {
            val lumInk = 0.2126 * linearChannel(color.red) +
                0.7152 * linearChannel(color.green) +
                0.0722 * linearChannel(color.blue)
            val aeroGlass = Color(0xFFF5FBFF)
            val lumBg = 0.2126 * linearChannel(aeroGlass.red) +
                0.7152 * linearChannel(aeroGlass.green) +
                0.0722 * linearChannel(aeroGlass.blue)
            return (lumBg + 0.05) / (lumInk + 0.05)
        }

        // Bright neon accents must darken to WCAG AA (>= 4.6:1) on light Aero surfaces
        val limeLightInk = MaterialYouColorEngine.readableAccentOnLight(Color(0xFFC8FF2E))
        val cyanLightInk = MaterialYouColorEngine.readableAccentOnLight(Color(0xFF00E5FF))
        val limeRatio = wcagContrastOnAeroLight(limeLightInk)
        val cyanRatio = wcagContrastOnAeroLight(cyanLightInk)
        assertTrue("Expected lime ink WCAG contrast >= 4.6 on Aero light glass, was $limeRatio", limeRatio >= 4.6)
        assertTrue("Expected cyan ink WCAG contrast >= 4.6 on Aero light glass, was $cyanRatio", cyanRatio >= 4.6)

        var aeroAccentLocked = false
        var aeroAccentColor: Color? = null
        var aeroAccentTextColor: Color? = null
        var aeroWarningColor: Color? = null
        var aeroDangerColor: Color? = null
        var aeroSuccessColor: Color? = null
        var aeroDividerColor: Color? = null

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.FRUTIGER_AERO,
                    accentColorHex = "#C8FF2E",
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                val t = LocalIronPulseTokens.current
                aeroAccentLocked = t.accentLocked
                aeroAccentColor = t.accentColor
                aeroAccentTextColor = t.accentTextColor
                aeroWarningColor = t.warningColor
                aeroDangerColor = t.dangerColor
                aeroSuccessColor = t.successColor
                aeroDividerColor = t.dividerColor
            }
        }

        composeRule.waitForIdle()
        assertTrue("Frutiger Aero accent must remain locked", aeroAccentLocked)
        assertEquals(Color(0xFF0099E5), aeroAccentColor)
        assertEquals(Color(0xFF005F9E), aeroAccentTextColor)
        assertEquals(Color(0xFFB45309), aeroWarningColor)
        assertEquals(Color(0xFFB91C1C), aeroDangerColor)
        assertEquals(Color(0xFF15803D), aeroSuccessColor)
        assertTrue(wcagContrastOnAeroLight(aeroAccentTextColor!!) >= 4.5)
        assertTrue(wcagContrastOnAeroLight(aeroWarningColor!!) >= 4.5)
        assertTrue(wcagContrastOnAeroLight(aeroDangerColor!!) >= 4.5)
        assertTrue(wcagContrastOnAeroLight(aeroSuccessColor!!) >= 4.5)
        assertNotNull(aeroDividerColor)
        assertTrue("Frutiger Aero divider must be tinted dark-sky, not white", aeroDividerColor!!.red < 0.2f)
    }

    @Test
    fun frutigerAeroNightPhase_switchesToSoftTwilightDarkAeroAndSyncsPreviewStrip() {
        fun linearChannel(c: Float): Double {
            val d = c.toDouble()
            return if (d <= 0.03928) d / 12.92 else Math.pow((d + 0.055) / 1.055, 2.4)
        }
        fun wcagContrastOnTwilightAeroGlass(foreground: Color): Double {
            val lumFg = 0.2126 * linearChannel(foreground.red) +
                0.7152 * linearChannel(foreground.green) +
                0.0722 * linearChannel(foreground.blue)
            val darkAeroGlass = Color(0xFF183259)
            val lumBg = 0.2126 * linearChannel(darkAeroGlass.red) +
                0.7152 * linearChannel(darkAeroGlass.green) +
                0.0722 * linearChannel(darkAeroGlass.blue)
            val lighter = maxOf(lumFg, lumBg)
            val darker = minOf(lumFg, lumBg)
            return (lighter + 0.05) / (darker + 0.05)
        }

        val restFlow = MutableStateFlow(RestTimerDisplayState())
        var isLightTheme = true
        var isAeroNight = false
        var accentLocked = false
        var accentColor: Color? = null
        var onSurfaceColor: Color? = null
        var accentTextColor: Color? = null
        var canvasBg: Color? = null
        val selectedModes = mutableListOf<DayTimeMode>()

        composeRule.setContent {
            var activeMode by remember { mutableStateOf(DayTimeMode.AUTO) }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.FRUTIGER_AERO,
                    accentColorHex = "#C8FF2E",
                    lang = AppLanguage.ID,
                    todayThemeMode = activeMode,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                ),
                timePhase = DayTimePhase.NIGHT
            ) {
                val t = LocalIronPulseTokens.current
                isLightTheme = t.isLightTheme
                isAeroNight = t.isAeroNight
                accentLocked = t.accentLocked
                accentColor = t.accentColor
                onSurfaceColor = t.palette.onSurface
                accentTextColor = t.accentTextColor
                canvasBg = t.canvasBg

                Box(modifier = Modifier.fillMaxSize()) {
                    AmbientPhaseBackdrop()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IronPulseTopBar(
                            activeTab = AppTab.SETTINGS,
                            aboutOpen = false,
                            onLogoClick = {},
                            onOpenSettings = {},
                            onOpenAbout = {},
                            onTestAlarm = {}
                        )
                        ThemeBrandLockup(theme = UiTheme.FRUTIGER_AERO, accentColor = t.accentColor)
                        AccentAndTintSettingsCard(
                            accentColorHex = "#0099E5",
                            materialTintColorHex = null,
                            onSelectAccent = {},
                            onOpenAccentPicker = {},
                            onOpenTintPicker = {},
                            onSelectMaterialTint = {},
                            selectedDayTimeMode = activeMode,
                            onSelectDayTimeMode = { mode ->
                                activeMode = mode
                                selectedModes.add(mode)
                            }
                        )
                        IronPulseBottomNav(
                            activeTab = AppTab.SETTINGS,
                            restTimerFlow = restFlow,
                            onSelectTab = {}
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        assertFalse("Frutiger Aero Night must switch off light theme flag", isLightTheme)
        assertTrue("Frutiger Aero Night must activate isAeroNight", isAeroNight)
        assertTrue("Frutiger Aero Night must keep accent permanently locked", accentLocked)
        assertEquals(Color(0xFF0099E5), accentColor)
        assertEquals(Color(0xFFF0F8FF), onSurfaceColor)
        assertEquals(Color(0xFF7DD3FC), accentTextColor)
        assertEquals(Color(0xFF162A52), canvasBg)
        assertTrue(
            "Ice-white onSurface must exceed WCAG AA on twilight Aero glass",
            wcagContrastOnTwilightAeroGlass(onSurfaceColor!!) >= 4.5
        )
        assertTrue(
            "Sky-blue accentTextColor must exceed WCAG AA on twilight Aero glass",
            wcagContrastOnTwilightAeroGlass(accentTextColor!!) >= 4.5
        )

        composeRule.onNodeWithTag("os_backdrop_frutiger-aero").assertIsDisplayed()
        composeRule.onNodeWithTag("accent_locked_banner").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("aero_phase_preview_strip").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Senja Malam").assertIsDisplayed()

        composeRule.onNodeWithTag("aero_phase_btn_night").performScrollTo().performClick()
        composeRule.onNodeWithTag("aero_phase_btn_day").performScrollTo().performClick()
        composeRule.onNodeWithTag("aero_phase_btn_auto").performScrollTo().performClick()
        composeRule.waitForIdle()

        assertEquals(
            listOf(DayTimeMode.NIGHT, DayTimeMode.AFTERNOON, DayTimeMode.AUTO),
            selectedModes
        )
    }

    @Test
    fun pulseMascot_allFiveThemesHaveDistinctHarmoniousPalettesAcrossDayPhases() {
        var themeSetter: ((UiTheme) -> Unit)? = null
        var phaseSetter: ((DayTimePhase) -> Unit)? = null
        val capturedPalettes = linkedMapOf<Pair<UiTheme, DayTimePhase>, List<Color>>()

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            var activePhase by remember { mutableStateOf(DayTimePhase.MORNING) }
            themeSetter = { activeTheme = it }
            phaseSetter = { activePhase = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                ),
                timePhase = activePhase
            ) {
                val tokens = LocalIronPulseTokens.current
                val orbPalette = resolveMascotOrbPalette(
                    theme = tokens.uiTheme,
                    phase = tokens.timePhase,
                    accentColor = tokens.accentColor,
                    palette = tokens.palette,
                    isLightTheme = tokens.isLightTheme
                )
                capturedPalettes[activeTheme to activePhase] = listOf(
                    orbPalette.coreStart,
                    orbPalette.coreEnd,
                    orbPalette.lowerBounce,
                    orbPalette.haloPrimary,
                    orbPalette.expressionAccent
                )

                Box(modifier = Modifier.padding(16.dp)) {
                    MascotOrbFace(
                        mascot = MascotUiState(),
                        size = 72.dp
                    )
                }
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            DayTimePhase.entries.forEach { phase ->
                composeRule.runOnUiThread { phaseSetter?.invoke(phase) }
                composeRule.waitForIdle()
                composeRule.onNodeWithTag("mascot_orb_${theme.wireValue}", useUnmergedTree = true)
                    .assertIsDisplayed()
            }
        }

        // Every one of the 5 OS themes must adapt Pulse's palette distinctly across Morning, Afternoon, and Night
        UiTheme.entries.forEach { theme ->
            val morning = capturedPalettes[theme to DayTimePhase.MORNING]
            val afternoon = capturedPalettes[theme to DayTimePhase.AFTERNOON]
            val night = capturedPalettes[theme to DayTimePhase.NIGHT]
            assertNotNull("Missing morning mascot palette for $theme", morning)
            assertNotNull("Missing afternoon mascot palette for $theme", afternoon)
            assertNotNull("Missing night mascot palette for $theme", night)
            assertEquals(
                "Theme $theme must produce 3 distinct harmonious Pulse palettes for Morning, Afternoon, and Night",
                3,
                setOf(morning, afternoon, night).size
            )
        }
    }

    @Test
    fun todayScreen_elevatesStartWorkoutCtaAboveRestTimerAndMascotAcrossAllThemes() {
        val defaultSchedules = GymPresets.createDefaultSchedules("u_today").toMutableMap()
        val mondayExercises = GymPresets.createDefaultSchedules("u_today")[DayKey.MON]?.exercises ?: emptyList()
        val activeDay = DayKey.MON
        val activeSched = (defaultSchedules[activeDay] ?: return).copy(
            isGymDay = true,
            focusTitle = "Push Day",
            exercises = mondayExercises.ifEmpty {
                listOf(
                    com.fathan.ironpulse.model.ExerciseItem(
                        id = "ex_bench",
                        name = "Barbell Bench Press",
                        muscleGroup = MuscleGroup.DADA,
                        equipment = "Barbell",
                        targetSets = 3,
                        targetReps = "10",
                        targetWeightKg = 60.0,
                        restSeconds = 90,
                        notes = ""
                    )
                )
            }
        )
        defaultSchedules[activeDay] = activeSched
        val entries = activeSched.exercises.map { ex ->
            WorkoutLogEntry(
                exerciseId = ex.id,
                name = ex.name,
                muscleGroup = ex.muscleGroup,
                sets = listOf(WorkoutSetLog(setNumber = 1, weightKg = 60.0, reps = 10, completed = false))
            )
        }
        val uiState = IronPulseUiState(
            authReady = true,
            userUid = "u_today",
            dataLoading = false,
            historyLoading = false,
            schedules = defaultSchedules,
            todayDayKey = activeDay,
            effectiveTodayEntries = entries
        )
        val clockFlow = MutableStateFlow(1_730_000_000L)
        val restFlow = MutableStateFlow(RestTimerDisplayState())
        val eyeFlow = MutableStateFlow(MascotEyeState())
        var themeSetter: ((UiTheme) -> Unit)? = null
        var openWorkoutCalls = 0

        val listState = androidx.compose.foundation.lazy.LazyListState()

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            themeSetter = { activeTheme = it }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                CompositionLocalProvider(LocalTabActive provides true) {
                    TodayScreen(
                        state = uiState,
                        listState = listState,
                        contentPadding = PaddingValues(0.dp),
                        clockSecondsFlow = clockFlow,
                        restTimerFlow = restFlow,
                        mascotEyeFlow = eyeFlow,
                        onTapMascot = {},
                        onReplayMascotVoice = {},
                        onStartRestPreset = { _, _ -> },
                        onToggleRestPauseOrStart = {},
                        onAdjustRest = {},
                        onResetRest = {},
                        onOpenWorkout = { openWorkoutCalls += 1 },
                        onOpenSchedule = {},
                        onOpenExercisesForDay = {}
                    )
                }
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("today_mascot_card").assertIsDisplayed()
            composeRule.onNodeWithTag("start_workout_cta").assertIsDisplayed()
            val visibleKeys = listState.layoutInfo.visibleItemsInfo.map { it.key }
            assertEquals("hero_banner", visibleKeys.getOrNull(0))
            assertEquals("today_mascot_card", visibleKeys.getOrNull(1))
            assertEquals("start_workout_cta", visibleKeys.getOrNull(2))
            val mascotItem = listState.layoutInfo.visibleItemsInfo[1]
            val ctaItem = listState.layoutInfo.visibleItemsInfo[2]
            assertTrue(
                "Expected TodayMascot offset (${mascotItem.offset}) right below Hero and above Start Workout CTA (${ctaItem.offset}) in $theme",
                mascotItem.offset < ctaItem.offset
            )
            val restItem = listState.layoutInfo.visibleItemsInfo.firstOrNull { it.key == "rest_timer_card" }
            if (restItem != null) {
                assertTrue(
                    "Expected Start Workout CTA offset (${ctaItem.offset}) above Rest Timer (${restItem.offset}) in $theme",
                    ctaItem.offset < restItem.offset
                )
            }
        }

        composeRule.onNodeWithTag("start_workout_cta").performClick()
        assertEquals(1, openWorkoutCalls)
    }

    @Test
    fun preferencesStore_keepScreenOnWorkoutDefaultsTrueAndPersistsToggle() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val store = PreferencesStore(context)
        assertTrue(store.state.value.keepScreenOnWorkout)

        store.setKeepScreenOnWorkout(false)
        assertFalse(store.state.value.keepScreenOnWorkout)
        assertFalse(PreferencesStore(context).state.value.keepScreenOnWorkout)

        store.setKeepScreenOnWorkout(true)
        assertTrue(PreferencesStore(context).state.value.keepScreenOnWorkout)
    }

    @Test
    fun themedDialogs_composeCleanlyAcrossAllFiveThemes() {
        var themeSetter: ((UiTheme) -> Unit)? = null
        var showConfirm by mutableStateOf(true)
        var showSave by mutableStateOf(false)

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            themeSetter = { activeTheme = it }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                if (showConfirm) {
                    ConfirmDeleteDialog(
                        title = "Hapus Gerakan?",
                        message = "Konfirmasi penghapusan gerakan.",
                        confirmLabel = "Hapus",
                        cancelLabel = "Batal",
                        onConfirm = {},
                        onDismiss = {}
                    )
                }
                if (showSave) {
                    SaveCustomScheduleDialog(
                        initialName = "",
                        isRename = false,
                        onDismiss = {},
                        onConfirm = {}
                    )
                }
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("confirm_delete_dialog").assertIsDisplayed()
        }

        composeRule.runOnUiThread {
            showConfirm = false
            showSave = true
        }
        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("save_custom_schedule_dialog").assertIsDisplayed()
        }
    }

    @Test
    fun workoutSessionSummaryCard_rendersStatsAndActionsAcrossAllThemes() {
        var themeSetter: ((UiTheme) -> Unit)? = null
        var historyClicks = 0
        var todayClicks = 0

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            themeSetter = { activeTheme = it }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                WorkoutSessionSummaryCard(
                    focusTitle = "Push Day · Dada & Bahu",
                    doneExercises = 4,
                    totalExercises = 4,
                    completedSets = 12,
                    totalVolumeKg = 3450.0,
                    totalTimedSeconds = 0,
                    onOpenHistory = { historyClicks += 1 },
                    onBackToToday = { todayClicks += 1 }
                )
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("workout_session_summary_card").assertIsDisplayed()
            composeRule.onNodeWithText("SESI SELESAI!").assertIsDisplayed()
            composeRule.onNodeWithText("3450 kg").assertIsDisplayed()
        }

        composeRule.onNodeWithTag("summary_open_history_button").performClick()
        composeRule.onNodeWithTag("summary_back_today_button").performClick()
        assertEquals(1, historyClicks)
        assertEquals(1, todayClicks)
    }
}



