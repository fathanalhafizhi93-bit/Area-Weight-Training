package com.fathan.ironpulse

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.down
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.moveTo
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.MedalTier
import com.fathan.ironpulse.model.MedalUnlockEvent
import com.fathan.ironpulse.model.MonthlyMedalEvaluator
import com.fathan.ironpulse.model.MuscleGroup
import com.fathan.ironpulse.model.PerformancePreferences
import com.fathan.ironpulse.model.PerformancePresetId
import com.fathan.ironpulse.model.RingtoneStorageMode
import com.fathan.ironpulse.model.SavedCustomSchedule
import com.fathan.ironpulse.model.ScheduleLibraryState
import com.fathan.ironpulse.model.ScheduleProgramMerger
import com.fathan.ironpulse.model.ThemeAnimationStyle
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.model.UserProfile
import com.fathan.ironpulse.model.VerificationRejectReason
import com.fathan.ironpulse.model.WorkoutIntegrityEvaluator
import com.fathan.ironpulse.model.WorkoutLog
import com.fathan.ironpulse.model.WorkoutLogEntry
import com.fathan.ironpulse.model.WorkoutSetLog
import com.fathan.ironpulse.data.MascotDialogueContext
import com.fathan.ironpulse.data.MascotDialogues
import com.fathan.ironpulse.timer.RestTimerService
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.MascotPersonality
import com.fathan.ironpulse.model.TimePhasePose
import com.fathan.ironpulse.ui.components.AboutDialog
import com.fathan.ironpulse.ui.components.AmbientPhaseBackdrop
import com.fathan.ironpulse.ui.components.ConfirmDeleteDialog
import com.fathan.ironpulse.ui.components.FloatingMascotDock
import com.fathan.ironpulse.ui.components.IronPulseBottomNav
import com.fathan.ironpulse.ui.components.IronPulseMotion
import com.fathan.ironpulse.ui.components.IronPulseTopBar
import com.fathan.ironpulse.ui.components.LiveFluidClockSlot
import com.fathan.ironpulse.ui.components.LocalThemeMotionSpec
import com.fathan.ironpulse.ui.components.MedalUnlockCelebrationOverlay
import com.fathan.ironpulse.ui.components.MotionStyleCatalog
import com.fathan.ironpulse.ui.components.OrbBoundsHolder
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
import com.fathan.ironpulse.ui.components.staggeredCardEntrance
import com.fathan.ironpulse.ui.screens.AccentAndTintSettingsCard
import com.fathan.ironpulse.ui.screens.AuthScreen
import com.fathan.ironpulse.ui.screens.ClockCornerOrbPalette
import com.fathan.ironpulse.ui.screens.ExerciseLibraryFullScreen
import com.fathan.ironpulse.ui.screens.ExercisesScreen
import com.fathan.ironpulse.ui.screens.HeroAnimatedAtmosphereLayer
import com.fathan.ironpulse.ui.screens.HistoryScreen
import com.fathan.ironpulse.ui.screens.SettingsCategory
import com.fathan.ironpulse.ui.screens.SettingsScreen
import com.fathan.ironpulse.ui.screens.TodayScreen
import com.fathan.ironpulse.ui.screens.WorkoutSessionSummaryCard
import com.fathan.ironpulse.ui.screens.resolveClockCornerOrbPalette
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseOutlinedButton
import com.fathan.ironpulse.ui.theme.IronPulseTheme
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
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
                        onLogoClick = {},
                        onOpenSettings = {},
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
    fun uiTheme_hasSixThemesIncludingXMaterialYouAndLegacyFallback() {
        assertEquals(6, UiTheme.entries.size)
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("midnight-frosted"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("aeroglass"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("obsidian"))
        assertEquals(UiTheme.MATERIAL_YOU, UiTheme.fromWire("unknown-theme"))
        assertEquals(UiTheme.CARBON_FORGE, UiTheme.fromWire("carbon-forge"))
        assertEquals(UiTheme.UBUNTU_YARU, UiTheme.fromWire("ubuntu-yaru"))
        assertEquals(UiTheme.AMOLED_STEALTH, UiTheme.fromWire("amoled-stealth"))
        assertEquals(UiTheme.FRUTIGER_AERO, UiTheme.fromWire("frutiger-aero"))
        assertEquals(UiTheme.XMATERIAL_YOU, UiTheme.fromWire("xmaterial-you"))
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
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
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
        composeRule.onNodeWithText("Hai, Fathan! Aku Pulse").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("pulse_tutorial_start_hint").performScrollTo().assertIsDisplayed()

        // Tap Pulse mascot on welcome stage
        composeRule.onNodeWithTag("pulse_tutorial_mascot").performScrollTo().performClick()
        assertEquals(1, mascotTappedCount)

        // Switch language to English and back to Indonesian without advancing step
        composeRule.onNodeWithTag("pulse_tutorial_lang_en").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Hi, Fathan! I'm Pulse").performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithTag("pulse_tutorial_lang_id").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Hai, Fathan! Aku Pulse").performScrollTo().assertIsDisplayed()

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
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
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
    fun allSixThemes_renderUniqueBrandLockupsEmblemsAndPhaseLogos() {
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
    fun allSixOsThemes_provideUniqueBundledFontPacksAndBadges() {
        val packs = UiTheme.entries.map { resolveThemeFontPack(it) }
        assertEquals(6, packs.size)
        assertEquals(
            "Every theme must have a unique bundled display font",
            6,
            packs.map { it.displayFont }.distinct().size
        )
        assertEquals(
            "Every theme must have a unique font display name",
            6,
            packs.map { it.fontDisplayName }.distinct().size
        )
        assertEquals(
            "Every theme must have a unique OS badge label",
            6,
            packs.map { it.osBadgeLabel }.distinct().size
        )
    }

    @Test
    fun allSixOsThemes_renderDistinctChromeBackdropsCustomIconsAndMascotOrbs() {
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
                            onLogoClick = {},
                            onOpenSettings = {},
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
            composeRule.onNodeWithTag("topbar_settings_button").assertIsDisplayed()
            composeRule.onNodeWithTag("topbar_test_alarm_button").assertIsDisplayed()
            composeRule.onNodeWithContentDescription("Tentang IronPulse").assertDoesNotExist()
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
                            onLogoClick = {},
                            onOpenSettings = {},
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
    fun pulseMascot_allSixThemesHaveDistinctHarmoniousPalettesAcrossDayPhases() {
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

        // Every one of the 6 OS themes must adapt Pulse's palette distinctly across Morning, Afternoon, and Night
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
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
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
    fun themedDialogs_composeCleanlyAcrossAllSixThemes() {
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

    @Test
    fun xMaterialYouTheme_switchesLightDayAndCosmicNightLocksExclusivePalettesAndSyncsPreviewStrip() {
        fun linearChannel(c: Float): Double {
            val d = c.toDouble()
            return if (d <= 0.03928) d / 12.92 else Math.pow((d + 0.055) / 1.055, 2.4)
        }
        fun wcagContrast(foreground: Color, background: Color): Double {
            val lumFg = 0.2126 * linearChannel(foreground.red) +
                0.7152 * linearChannel(foreground.green) +
                0.0722 * linearChannel(foreground.blue)
            val lumBg = 0.2126 * linearChannel(background.red) +
                0.7152 * linearChannel(background.green) +
                0.0722 * linearChannel(background.blue)
            val lighter = maxOf(lumFg, lumBg)
            val darker = minOf(lumFg, lumBg)
            return (lighter + 0.05) / (darker + 0.05)
        }

        val restFlow = MutableStateFlow(RestTimerDisplayState())
        var phaseSetter: ((DayTimePhase) -> Unit)? = null
        var isLightTheme = false
        var isXMaterialNight = false
        var accentLocked = false
        var accentColor: Color? = null
        var onSurfaceColor: Color? = null
        var accentTextColor: Color? = null
        var canvasBg: Color? = null
        var cardShape: Shape? = null
        var subtleCardShape: Shape? = null
        var buttonShape: Shape? = null
        var inputShape: Shape? = null
        var cardRadius = 0.dp
        var buttonRadius = 0.dp
        var inputRadius = 0.dp
        var buttonHasGloss = true
        var surfaceBorderColor: Color? = null
        val selectedModes = mutableListOf<DayTimeMode>()

        composeRule.setContent {
            var activePhase by remember { mutableStateOf(DayTimePhase.MORNING) }
            var activeMode by remember { mutableStateOf(DayTimeMode.AUTO) }
            phaseSetter = { activePhase = it }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.XMATERIAL_YOU,
                    accentColorHex = "#C8FF2E",
                    materialTintColorHex = "#24192E",
                    lang = AppLanguage.ID,
                    todayThemeMode = activeMode,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                ),
                timePhase = activePhase
            ) {
                val t = LocalIronPulseTokens.current
                isLightTheme = t.isLightTheme
                isXMaterialNight = t.isXMaterialNight
                accentLocked = t.accentLocked
                accentColor = t.accentColor
                onSurfaceColor = t.palette.onSurface
                accentTextColor = t.accentTextColor
                canvasBg = t.canvasBg
                cardShape = t.cardShape
                subtleCardShape = t.subtleCardShape
                buttonShape = t.buttonShape
                inputShape = t.inputShape
                cardRadius = t.cardRadius
                buttonRadius = t.buttonRadius
                inputRadius = t.inputRadius
                buttonHasGloss = t.buttonHasGloss
                surfaceBorderColor = t.surfaceBorderColor

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
                            onLogoClick = {},
                            onOpenSettings = {},
                            onTestAlarm = {}
                        )
                        ThemeBrandLockup(theme = UiTheme.XMATERIAL_YOU, accentColor = t.accentColor)
                        AccentAndTintSettingsCard(
                            accentColorHex = t.accentColor.let { MaterialYouColorEngine.colorToHex(it) },
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

        // Phase 1: Morning Dark Obsidian — Fajar Emas (Champagne Gold #DFBE84 + Obsidian #060608)
        composeRule.waitForIdle()
        assertFalse("XMaterial You Morning must be rich dark obsidian theme", isLightTheme)
        assertFalse("XMaterial You Morning must not set isXMaterialNight", isXMaterialNight)
        assertTrue("XMaterial You must lock exclusive phase palettes", accentLocked)
        assertEquals(28.dp, cardRadius)
        assertEquals(
            "XMaterial You cards must use symmetrical soft 28dp rounded corners",
            RoundedCornerShape(28.dp),
            cardShape
        )
        assertEquals(
            "XMaterial You subtle cards must use soft 22dp rounded corners",
            RoundedCornerShape(22.dp),
            subtleCardShape
        )
        assertEquals(999.dp, buttonRadius)
        assertEquals(
            "XMaterial You buttons must use full capsule pill shapes",
            RoundedCornerShape(999.dp),
            buttonShape
        )
        assertEquals(18.dp, inputRadius)
        assertEquals(
            "XMaterial You inputs must use symmetrical 18dp rounded corners",
            RoundedCornerShape(18.dp),
            inputShape
        )
        assertFalse("XMaterial You must use clean satin finish without Aero gloss", buttonHasGloss)
        assertTrue("XMaterial You border must remain fine and understated", surfaceBorderColor!!.alpha <= 0.35f)
        assertEquals(Color(0xFFDFBE84), accentColor)
        assertEquals(Color(0xFF060608), canvasBg)
        assertTrue(wcagContrast(onSurfaceColor!!, Color(0xFF0E0F13)) >= 4.5)
        assertTrue(wcagContrast(accentTextColor!!, Color(0xFF0E0F13)) >= 4.5)
        composeRule.onNodeWithTag("os_backdrop_xmaterial-you").assertIsDisplayed()
        composeRule.onNodeWithTag("accent_locked_banner").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Palet Obsidian XMaterial You Terkunci").assertIsDisplayed()
        composeRule.onNodeWithTag("xmaterial_phase_preview_strip").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Fajar Emas").assertIsDisplayed()
        composeRule.onNodeWithTag("accent_pick_color_button").assertDoesNotExist()

        // Phase 2: Afternoon Dark Obsidian — Siang Platinum (Platinum Jade #8CC5C2 + Obsidian #060608)
        composeRule.runOnUiThread { phaseSetter?.invoke(DayTimePhase.AFTERNOON) }
        composeRule.waitForIdle()
        assertFalse("XMaterial You Afternoon must be rich dark obsidian theme", isLightTheme)
        assertFalse(isXMaterialNight)
        assertEquals(Color(0xFF8CC5C2), accentColor)
        assertEquals(Color(0xFF060608), canvasBg)
        assertTrue(wcagContrast(onSurfaceColor!!, Color(0xFF0E0F13)) >= 4.5)
        assertTrue(wcagContrast(accentTextColor!!, Color(0xFF0E0F13)) >= 4.5)
        composeRule.onNodeWithText("Siang Platinum").performScrollTo().assertIsDisplayed()

        // Phase 3: Night Dark Obsidian — Malam Moonstone (Moonstone #B0A4E3 + Obsidian #060608)
        composeRule.runOnUiThread { phaseSetter?.invoke(DayTimePhase.NIGHT) }
        composeRule.waitForIdle()
        assertFalse("XMaterial You Night must be rich dark obsidian theme", isLightTheme)
        assertTrue("XMaterial You Night must enable isXMaterialNight", isXMaterialNight)
        assertTrue(accentLocked)
        assertEquals(Color(0xFFB0A4E3), accentColor)
        assertEquals(Color(0xFF060608), canvasBg)
        assertTrue(wcagContrast(onSurfaceColor!!, Color(0xFF0E0F13)) >= 4.5)
        assertTrue(wcagContrast(accentTextColor!!, Color(0xFF0E0F13)) >= 4.5)
        composeRule.onNodeWithText("Malam Moonstone").performScrollTo().assertIsDisplayed()

        // Verify all 4 XMaterial You quick phase buttons trigger expected callbacks
        composeRule.onNodeWithTag("xmaterial_phase_btn_morning").performScrollTo().performClick()
        composeRule.onNodeWithTag("xmaterial_phase_btn_afternoon").performScrollTo().performClick()
        composeRule.onNodeWithTag("xmaterial_phase_btn_night").performScrollTo().performClick()
        composeRule.onNodeWithTag("xmaterial_phase_btn_auto").performScrollTo().performClick()
        composeRule.waitForIdle()
        assertEquals(
            listOf(DayTimeMode.MORNING, DayTimeMode.AFTERNOON, DayTimeMode.NIGHT, DayTimeMode.AUTO),
            selectedModes
        )
    }

    @Test
    fun clockCardTopRightOrb_rendersAndAdaptsPalettesAcrossAllDayPhasesAndThemes() {
        var themeSetter: ((UiTheme) -> Unit)? = null
        var phaseSetter: ((DayTimePhase) -> Unit)? = null
        val capturedOrbs = linkedMapOf<Pair<UiTheme, DayTimePhase>, ClockCornerOrbPalette>()

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            var activePhase by remember { mutableStateOf(DayTimePhase.MORNING) }
            themeSetter = { activeTheme = it }
            phaseSetter = { activePhase = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = false, noAmbientGlow = false)
                ),
                timePhase = activePhase
            ) {
                val t = LocalIronPulseTokens.current
                capturedOrbs[activeTheme to activePhase] = resolveClockCornerOrbPalette(t)

                CompositionLocalProvider(LocalTabActive provides true) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        HeroAnimatedAtmosphereLayer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                                .matchParentSize(),
                            noAnim = false
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("14:32:08")
                        }
                    }
                }
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            DayTimePhase.entries.forEach { phase ->
                composeRule.runOnUiThread { phaseSetter?.invoke(phase) }
                composeRule.waitForIdle()
                composeRule.onNodeWithTag("hero_atmosphere_layer").assertIsDisplayed()
            }
        }

        UiTheme.entries.forEach { theme ->
            val morning = capturedOrbs[theme to DayTimePhase.MORNING]
            val afternoon = capturedOrbs[theme to DayTimePhase.AFTERNOON]
            val night = capturedOrbs[theme to DayTimePhase.NIGHT]
            assertNotNull("Missing morning orb palette for $theme", morning)
            assertNotNull("Missing afternoon orb palette for $theme", afternoon)
            assertNotNull("Missing night orb palette for $theme", night)
            assertEquals(
                "Clock card top-right orb must produce 3 distinct phase palettes for $theme",
                3,
                setOf(morning, afternoon, night).size
            )
            listOf(morning!!, afternoon!!, night!!).forEach { orb ->
                assertTrue(
                    "Expected soft 30-35% core alpha in $theme, was ${orb.orbCoreCenter.alpha}",
                    orb.orbCoreCenter.alpha in 0.30f..0.38f
                )
            }
        }
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun settingsHub_rendersOrderedVerticalListAndNavigatesToAboutSubpageWithBack() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(context)
        }
        val vmStore = ViewModelStore()
        try {
            val viewModel = ViewModelProvider(
                vmStore,
                ViewModelProvider.AndroidViewModelFactory.getInstance(context)
            )[GymViewModel::class.java]
            val uiState = IronPulseUiState(
                authReady = true,
                userUid = "u_settings",
                userDisplayName = "Fathan",
                userEmail = "fathan@ironpulse.app",
                appPrefs = AppPreferences(
                    uiTheme = UiTheme.XMATERIAL_YOU,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            )
            var hubBackCalls = 0

            composeRule.setContent {
                IronPulseTheme(prefs = uiState.appPrefs) {
                    CompositionLocalProvider(LocalTabActive provides true) {
                        SettingsScreen(
                            state = uiState,
                            viewModel = viewModel,
                            contentPadding = PaddingValues(0.dp),
                            onBack = { hubBackCalls += 1 },
                            onRequestNotificationPermission = {},
                            onOpenExactAlarmSettings = {},
                            onPickCustomRingtone = {}
                        )
                    }
                }
            }

            composeRule.waitForIdle()
            // Verify exact category order in top-to-bottom hub list
            assertEquals(
                listOf(
                    SettingsCategory.PROFILE,
                    SettingsCategory.THEME,
                    SettingsCategory.CONFIGURATION,
                    SettingsCategory.ALARM,
                    SettingsCategory.SYSTEM,
                    SettingsCategory.ABOUT
                ),
                SettingsCategory.entries
            )
            SettingsCategory.entries.forEach { cat ->
                composeRule.onNodeWithTag("settings_list").performScrollToNode(hasTestTag(cat.testTag))
                composeRule.onNodeWithTag(cat.testTag).assertIsDisplayed()
            }

            // Open About full sub-page inside Settings
            composeRule.onNodeWithTag("settings_list").performScrollToNode(hasTestTag(SettingsCategory.ABOUT.testTag))
            composeRule.onNodeWithTag(SettingsCategory.ABOUT.testTag).performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithText("About IronPulse").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("v0.0.1 alpha").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("Fathan, and Artificial intelligence").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("fathan.").performScrollTo().assertIsDisplayed()

            // Press subpage Back button -> returns to 6-item vertical hub list
            composeRule.onNodeWithTag("settings_subpage_back_button").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("settings_list").performScrollToNode(hasTestTag(SettingsCategory.PROFILE.testTag))
            composeRule.onNodeWithTag(SettingsCategory.PROFILE.testTag).assertIsDisplayed()
            composeRule.onNodeWithTag("settings_hub_back_button").performClick()
            assertEquals(1, hubBackCalls)
        } finally {
            vmStore.clear()
        }
    }

    @Test
    fun uiTheme_exclusiveSplitAndVisualOnlyDescriptionsHaveNoTechnicalJargon() {
        val standard = UiTheme.entries.filter { !it.isExclusive }
        val exclusive = UiTheme.entries.filter { it.isExclusive }

        assertEquals(
            listOf(UiTheme.MATERIAL_YOU, UiTheme.CARBON_FORGE, UiTheme.AMOLED_STEALTH),
            standard
        )
        assertEquals(
            listOf(UiTheme.UBUNTU_YARU, UiTheme.FRUTIGER_AERO, UiTheme.XMATERIAL_YOU),
            exclusive
        )

        val bannedTokens = listOf("120 fps", "shader", "gtk4", "baterai", "battery", "terkunci", "locked", "ringan", "lightweight", "mikro-dot")
        UiTheme.entries.forEach { theme ->
            assertTrue("subtitleId blank for $theme", theme.subtitleId.isNotBlank())
            assertTrue("subtitleEn blank for $theme", theme.subtitleEn.isNotBlank())
            val combined = "${theme.subtitleId} ${theme.subtitleEn}".lowercase(Locale.ROOT)
            bannedTokens.forEach { token ->
                assertFalse("Theme $theme description must not contain '$token': $combined", combined.contains(token))
            }
        }
    }

    @Test
    fun ubuntuYaruTheme_locksYaruOrangeAndAubergineAndPreservesSavedCustomColors() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val store = PreferencesStore(context)
        store.resetThemeCustomization()
        store.setUiTheme(UiTheme.MATERIAL_YOU)
        store.setAccentColor("#FF4D8D")
        store.setMaterialTintColor("#112922")

        // Switching to Ubuntu Yaru and back must NOT clobber the user's saved custom colors
        store.setUiTheme(UiTheme.UBUNTU_YARU)
        assertEquals(UiTheme.UBUNTU_YARU, store.state.value.uiTheme)
        assertEquals("#FF4D8D", store.state.value.accentColorHex)
        assertEquals("#112922", store.state.value.materialTintColorHex)

        store.setUiTheme(UiTheme.MATERIAL_YOU)
        assertEquals("#FF4D8D", store.state.value.accentColorHex)
        assertEquals("#112922", store.state.value.materialTintColorHex)
        store.resetThemeCustomization()

        var resolvedAccentLocked = false
        var resolvedAccent: Color? = null
        var resolvedTint: Color? = null
        var resolvedCanvasBg: Color? = null
        var resolvedSecondaryBg: Color? = null

        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.UBUNTU_YARU,
                    accentColorHex = "#FF4D8D",
                    materialTintColorHex = "#112922",
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true)
                )
            ) {
                val t = LocalIronPulseTokens.current
                resolvedAccentLocked = t.accentLocked
                resolvedAccent = t.accentColor
                resolvedTint = t.effectiveTintColor
                resolvedCanvasBg = t.canvasBg
                resolvedSecondaryBg = t.canvasSecondaryBg

                Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    AccentAndTintSettingsCard(
                        accentColorHex = "#FF4D8D",
                        materialTintColorHex = "#112922",
                        onSelectAccent = {},
                        onOpenAccentPicker = {},
                        onOpenTintPicker = {},
                        onSelectMaterialTint = {}
                    )
                }
            }
        }

        composeRule.waitForIdle()
        assertTrue("Ubuntu Yaru must lock accent & aubergine customization", resolvedAccentLocked)
        assertEquals(Color(0xFFE95420), resolvedAccent)
        assertEquals(Color(0xFF2E182A), resolvedTint)
        assertEquals(Color(0xFF1F1C21), resolvedCanvasBg)
        assertEquals(Color(0xFF381630), resolvedSecondaryBg)

        composeRule.onNodeWithTag("accent_locked_banner").assertIsDisplayed()
        composeRule.onNodeWithText("Jingga Yaru & Aubergine Terkunci").assertIsDisplayed()
        composeRule.onNodeWithTag("accent_pick_color_button").assertDoesNotExist()
        composeRule.onNodeWithTag("tint_pick_color_button").assertDoesNotExist()
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun settingsThemeSubpage_groupsStandardAndExclusiveThemesWithoutPerCardBadges() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(context)
        }
        val vmStore = ViewModelStore()
        try {
            val viewModel = ViewModelProvider(
                vmStore,
                ViewModelProvider.AndroidViewModelFactory.getInstance(context)
            )[GymViewModel::class.java]
            val uiState = IronPulseUiState(
                authReady = true,
                userUid = "u_theme_groups",
                appPrefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            )

            composeRule.setContent {
                IronPulseTheme(prefs = uiState.appPrefs) {
                    CompositionLocalProvider(LocalTabActive provides true) {
                        SettingsScreen(
                            state = uiState,
                            viewModel = viewModel,
                            contentPadding = PaddingValues(0.dp),
                            onBack = {},
                            onRequestNotificationPermission = {},
                            onOpenExactAlarmSettings = {},
                            onPickCustomRingtone = {}
                        )
                    }
                }
            }

            composeRule.waitForIdle()
            composeRule.onNodeWithTag(SettingsCategory.THEME.testTag).performClick()
            composeRule.waitForIdle()

            // Both groups rendered cleanly inside ThemesGridCard on modern phone viewport
            composeRule.onNodeWithTag("themes_group_standard").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("Tema Standar").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("theme_card_material-you").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("theme_card_carbon-forge").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("theme_card_amoled-stealth").performScrollTo().assertIsDisplayed()

            composeRule.onNodeWithTag("themes_group_exclusive").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithText("Tema Eksklusif").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("theme_card_ubuntu-yaru").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("theme_card_frutiger-aero").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("theme_card_xmaterial-you").performScrollTo().assertIsDisplayed()

            // Ensure separation is by group ONLY (no extra "Eksklusif" pills on individual cards)
            composeRule.onAllNodesWithText("Eksklusif").fetchSemanticsNodes().let { nodes ->
                assertEquals(0, nodes.size)
            }
        } finally {
            vmStore.clear()
        }
    }

    @Test
    fun frutigerAeroTheme_rendersGlossyButtonsTonalControlsAndSubtleCardsCleanly() {
        var phaseSetter: ((DayTimePhase) -> Unit)? = null
        var primaryClicks = 0
        var secondaryClicks = 0
        var tonalClicks = 0
        var buttonHasGloss = false

        composeRule.setContent {
            var activePhase by remember { mutableStateOf(DayTimePhase.MORNING) }
            phaseSetter = { activePhase = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.FRUTIGER_AERO,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                ),
                timePhase = activePhase
            ) {
                val t = LocalIronPulseTokens.current
                buttonHasGloss = t.buttonHasGloss
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .ironGlassSurface(t, subtle = false)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .ironGlassSurface(t, subtle = true)
                            .padding(12.dp)
                    ) {
                        Text("Subtle Aero Glass Panel", color = t.palette.onSurface)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IronPulseButton(
                            onClick = { primaryClicks++ },
                            shape = t.buttonShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = t.accentColor,
                                contentColor = t.onAccentColor
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("aero_primary_gloss_btn")
                        ) {
                            Text("Primary Gel")
                        }
                        IronPulseOutlinedButton(
                            onClick = { secondaryClicks++ },
                            shape = t.buttonShape,
                            border = BorderStroke(1.dp, t.surfaceBorderColor),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("aero_outlined_gloss_btn")
                        ) {
                            Text("Frosted Gel", color = t.palette.onSurface)
                        }
                    }
                    Surface(
                        onClick = { tonalClicks++ },
                        shape = RoundedCornerShape(999.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, t.surfaceBorderColor),
                        modifier = Modifier
                            .ironTonalControl(
                                tokens = t,
                                shape = RoundedCornerShape(999.dp),
                                baseColor = t.controlBg,
                                isAccent = false
                            )
                            .testTag("aero_tonal_pill")
                    ) {
                        Text(
                            text = "Tonal Aqua Pill",
                            color = t.palette.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        assertTrue("Frutiger Aero must keep buttonHasGloss enabled", buttonHasGloss)
        composeRule.onNodeWithText("Subtle Aero Glass Panel").assertIsDisplayed()
        composeRule.onNodeWithTag("aero_primary_gloss_btn").performClick()
        composeRule.onNodeWithTag("aero_outlined_gloss_btn").performClick()
        composeRule.onNodeWithTag("aero_tonal_pill").performClick()
        composeRule.waitForIdle()

        // Verify Night Aero twilight gloss renders & responds cleanly as well
        composeRule.runOnUiThread { phaseSetter?.invoke(DayTimePhase.NIGHT) }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Subtle Aero Glass Panel").assertIsDisplayed()
        composeRule.onNodeWithTag("aero_primary_gloss_btn").performClick()
        composeRule.onNodeWithTag("aero_outlined_gloss_btn").performClick()
        composeRule.onNodeWithTag("aero_tonal_pill").performClick()
        composeRule.waitForIdle()

        assertEquals(2, primaryClicks)
        assertEquals(2, secondaryClicks)
        assertEquals(2, tonalClicks)
    }

    private fun buildTestWorkoutLog(
        dateKey: String,
        exerciseCount: Int = 2,
        setsPerExercise: Int = 3,
        durationSeconds: Int = 960,
        medalVerifiedSets: Boolean = true,
        serverCreatedAtMs: Long = 0L
    ): WorkoutLog {
        val entries = (1..exerciseCount).map { idx ->
            WorkoutLogEntry(
                exerciseId = "ex_$idx",
                name = "Exercise $idx",
                muscleGroup = MuscleGroup.DADA,
                trackingMode = ExerciseTrackingMode.REPS,
                usesWeight = true,
                sets = (1..setsPerExercise).map { s ->
                    WorkoutSetLog(
                        setNumber = s,
                        weightKg = 40.0,
                        reps = 10,
                        completed = true,
                        medalVerified = medalVerifiedSets
                    )
                }
            )
        }
        return WorkoutLog(
            userId = "u1",
            dateKey = dateKey,
            dayKey = DayKey.MON,
            focusTitle = "Upper Body",
            entries = entries,
            completed = true,
            activeDurationSeconds = durationSeconds,
            serverCreatedAtMs = serverCreatedAtMs
        )
    }

    @Test
    fun workoutIntegrityEvaluator_blocksFastTapTimeTravelAndShortSessions() {
        // 1. Spam-tapping sets (<20s / over bypass limit) -> not verified even with 15+ min
        val spammedLog = buildTestWorkoutLog(
            dateKey = "2026-11-10",
            exerciseCount = 2,
            setsPerExercise = 3,
            durationSeconds = 1200,
            medalVerifiedSets = false
        )
        val spammedRes = WorkoutIntegrityEvaluator.evaluate(spammedLog)
        assertFalse(spammedRes.isVerified)
        assertEquals(VerificationRejectReason.INSUFFICIENT_VOLUME, spammedRes.rejectReason)
        assertEquals(6, spammedRes.fastTappedSets)

        // 2. Real volume but rushed under 15 minutes -> rejected
        val rushedLog = buildTestWorkoutLog(
            dateKey = "2026-11-10",
            exerciseCount = 2,
            setsPerExercise = 3,
            durationSeconds = 420,
            medalVerifiedSets = true
        )
        val rushedRes = WorkoutIntegrityEvaluator.evaluate(rushedLog)
        assertFalse(rushedRes.isVerified)
        assertEquals(VerificationRejectReason.INSUFFICIENT_DURATION, rushedRes.rejectReason)

        // 3. Backdated device clock vs Firestore server createdAt -> rejected
        val cal = java.util.Calendar.getInstance().apply { set(2026, 10, 20, 12, 0, 0) }
        val spoofedLog = buildTestWorkoutLog(
            dateKey = "2026-11-01",
            exerciseCount = 2,
            setsPerExercise = 3,
            durationSeconds = 1200,
            medalVerifiedSets = true,
            serverCreatedAtMs = cal.timeInMillis
        )
        val spoofedRes = WorkoutIntegrityEvaluator.evaluate(spoofedLog)
        assertFalse(spoofedRes.isVerified)
        assertEquals(VerificationRejectReason.SERVER_DATE_MISMATCH, spoofedRes.rejectReason)

        // 4. Legit >= 2 exercises, >= 6 paced sets, >= 15 min -> verified
        val validLog = buildTestWorkoutLog(
            dateKey = "2026-11-20",
            exerciseCount = 2,
            setsPerExercise = 3,
            durationSeconds = 960,
            medalVerifiedSets = true,
            serverCreatedAtMs = cal.timeInMillis
        )
        assertTrue(WorkoutIntegrityEvaluator.evaluate(validLog).isVerified)

        // 5. Legit 15+ min timer cardio session -> verified
        val cardioLog = WorkoutLog(
            userId = "u1",
            dateKey = "2026-11-21",
            dayKey = DayKey.SAT,
            focusTitle = "Cardio",
            entries = listOf(
                WorkoutLogEntry(
                    exerciseId = "treadmill_run",
                    name = "Treadmill",
                    muscleGroup = MuscleGroup.KARDIO,
                    trackingMode = ExerciseTrackingMode.TIMER,
                    usesWeight = false,
                    sets = listOf(
                        WorkoutSetLog(
                            setNumber = 1,
                            weightKg = 0.0,
                            reps = 0,
                            durationSeconds = 950,
                            completed = true,
                            medalVerified = true
                        )
                    )
                )
            ),
            completed = true,
            activeDurationSeconds = 950
        )
        assertTrue(WorkoutIntegrityEvaluator.evaluate(cardioLog).isVerified)
    }

    @Test
    fun monthlyMedalEvaluator_computesBronze12Silver16Gold20AndArchive() {
        val novLogs = (1..12).map { d ->
            buildTestWorkoutLog(String.format(java.util.Locale.US, "2026-11-%02d", d))
        } + listOf(
            // Add a short/unverified log on day 13 — must NOT count as verified day
            buildTestWorkoutLog("2026-11-13", durationSeconds = 120)
        )
        val novSummary = MonthlyMedalEvaluator.summarizeMonth("2026-11", novLogs)
        assertEquals(12, novSummary.verifiedDays)
        assertEquals(1, novSummary.shortLogDays)
        assertEquals(MedalTier.BRONZE, novSummary.tier)
        assertEquals(MedalTier.SILVER, novSummary.nextTier)
        assertEquals(4, novSummary.daysToNextTier)

        val silverLogs = (1..16).map { d ->
            buildTestWorkoutLog(String.format(java.util.Locale.US, "2026-10-%02d", d))
        }
        assertEquals(MedalTier.SILVER, MonthlyMedalEvaluator.summarizeMonth("2026-10", silverLogs).tier)

        val goldLogs = (1..20).map { d ->
            buildTestWorkoutLog(String.format(java.util.Locale.US, "2026-09-%02d", d))
        }
        val goldSummary = MonthlyMedalEvaluator.summarizeMonth("2026-09", goldLogs)
        assertEquals(MedalTier.GOLD, goldSummary.tier)
        assertEquals(null, goldSummary.nextTier)

        val archive = MonthlyMedalEvaluator.earnedMonthlyMedalsArchive(novLogs + silverLogs + goldLogs)
        assertEquals(listOf("2026-11", "2026-10", "2026-09"), archive.map { it.monthKey })
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun historyScreen_displaysTopMonthlyMedalShowcaseAndCelebrationOverlayFlowsToHistory() {
        val silverLogs = (1..16).map { d ->
            buildTestWorkoutLog(String.format(java.util.Locale.US, "2026-11-%02d", d))
        }
        val summary = MonthlyMedalEvaluator.summarizeMonth("2026-11", silverLogs)
        var flightTriggeredSource: Offset? = null
        val eyeFlow = MutableStateFlow(MascotEyeState())

        composeRule.setContent {
            var showUnlock by remember { mutableStateOf(true) }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                CompositionLocalProvider(LocalTabActive provides true) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HistoryScreen(
                            state = IronPulseUiState(
                                userUid = "u1",
                                authReady = true,
                                historyLoading = false,
                                todayDateKey = "2026-11-16",
                                workoutHistory = silverLogs,
                                currentMonthMedal = summary
                            ),
                            contentPadding = PaddingValues(0.dp),
                            onStartTodayWorkout = {}
                        )
                        if (showUnlock) {
                            MedalUnlockCelebrationOverlay(
                                event = MedalUnlockEvent(
                                    monthKey = "2026-11",
                                    tier = MedalTier.SILVER,
                                    verifiedDays = 16
                                ),
                                mascot = MascotUiState(urgentMood = MascotMood.CELEBRATE),
                                mascotEyeFlow = eyeFlow,
                                onStartFlightToHistory = { src ->
                                    flightTriggeredSource = src
                                    showUnlock = false
                                }
                            )
                        }
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("medal_unlock_celebration_overlay").assertIsDisplayed()
        composeRule.onNode(
            hasText("Medali Perak") and hasAnyAncestor(hasTestTag("medal_unlock_celebration_overlay")),
            useUnmergedTree = true
        ).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("medal_save_to_history_button").performScrollTo().performClick()
        composeRule.waitForIdle()

        assertNotNull(flightTriggeredSource)
        composeRule.onNodeWithTag("medal_unlock_celebration_overlay").assertDoesNotExist()
        composeRule.onNodeWithTag("history_monthly_medal_card").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("history_medal_emblem_slot").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun preferencesStore_persistsMascotPersonalityAndDefaultsToPartner() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val store = PreferencesStore(context)
        store.setMascotPersonality(MascotPersonality.PARTNER)
        assertEquals(MascotPersonality.PARTNER, store.state.value.mascotPersonality)

        store.setMascotPersonality(MascotPersonality.COACH)
        assertEquals(MascotPersonality.COACH, store.state.value.mascotPersonality)
        assertEquals(MascotPersonality.COACH, PreferencesStore(context).state.value.mascotPersonality)

        // Restore default for test isolation
        store.setMascotPersonality(MascotPersonality.PARTNER)
        assertEquals(MascotPersonality.PARTNER, PreferencesStore(context).state.value.mascotPersonality)
    }

    @Test
    fun pulseMascot_twoPersonalitiesHaveDistinctExpandedMorningAfternoonNightDialogues() {
        DayTimePhase.entries.forEach { phase ->
            val partnerLines = MascotDialogues.buildMascotSpeechPool(
                MascotDialogueContext(
                    lang = AppLanguage.ID,
                    mood = MascotMood.READY,
                    timePhase = phase,
                    activeTimePose = TimePhasePose.FLEX,
                    n = ", Fathan",
                    completedSets = 2,
                    totalSets = 6,
                    progressPct = 33,
                    dayLabel = "Senin",
                    personality = MascotPersonality.PARTNER
                )
            )
            val coachLines = MascotDialogues.buildMascotSpeechPool(
                MascotDialogueContext(
                    lang = AppLanguage.ID,
                    mood = MascotMood.READY,
                    timePhase = phase,
                    activeTimePose = TimePhasePose.SALUTE,
                    n = ", Fathan",
                    completedSets = 2,
                    totalSets = 6,
                    progressPct = 33,
                    dayLabel = "Senin",
                    personality = MascotPersonality.COACH
                )
            )

            assertTrue("Expected >= 12 Partner dialogues in $phase, got ${partnerLines.size}", partnerLines.size >= 12)
            assertTrue("Expected >= 12 Coach dialogues in $phase, got ${coachLines.size}", coachLines.size >= 12)
            assertEquals(partnerLines.size, partnerLines.map { it.id }.distinct().size)
            assertEquals(coachLines.size, coachLines.map { it.id }.distinct().size)

            val partnerTitles = partnerLines.map { it.headline }.toSet()
            val coachTitles = coachLines.map { it.headline }.toSet()
            assertTrue("Partner and Coach headlines in $phase must stay distinct", partnerTitles.intersect(coachTitles).isEmpty())
        }

        val partnerPet = MascotDialogues.buildPettingPool(AppLanguage.EN, ", Alex", MascotPersonality.PARTNER)
        val coachPet = MascotDialogues.buildPettingPool(AppLanguage.EN, ", Alex", MascotPersonality.COACH)
        assertTrue(partnerPet.isNotEmpty() && coachPet.isNotEmpty())
        assertTrue(partnerPet.map { it.headline }.toSet().intersect(coachPet.map { it.headline }.toSet()).isEmpty())

        val partnerCombo = MascotDialogues.buildComboPokePool(AppLanguage.ID, ", Raka", 4, MascotPersonality.PARTNER)
        val coachCombo = MascotDialogues.buildComboPokePool(AppLanguage.ID, ", Raka", 4, MascotPersonality.COACH)
        assertTrue(partnerCombo.map { it.headline }.toSet().intersect(coachCombo.map { it.headline }.toSet()).isEmpty())

        val partnerTip = MascotDialogues.buildQuickTipPool(AppLanguage.ID, ", Raka", MascotPersonality.PARTNER)
        val coachTip = MascotDialogues.buildQuickTipPool(AppLanguage.ID, ", Raka", MascotPersonality.COACH)
        assertTrue(partnerTip.map { it.headline }.toSet().intersect(coachTip.map { it.headline }.toSet()).isEmpty())
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun settingsConfigurationSubpage_selectsMascotPersonalityAndPersistsChoice() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        if (com.google.firebase.FirebaseApp.getApps(context).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(context)
        }
        PreferencesStore(context).setMascotPersonality(MascotPersonality.PARTNER)
        val vmStore = ViewModelStore()
        try {
            val viewModel = ViewModelProvider(
                vmStore,
                ViewModelProvider.AndroidViewModelFactory.getInstance(context)
            )[GymViewModel::class.java]

            composeRule.setContent {
                val vmState by viewModel.uiState.collectAsStateWithLifecycle()
                IronPulseTheme(
                    prefs = vmState.appPrefs.copy(
                        performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                    )
                ) {
                    CompositionLocalProvider(LocalTabActive provides true) {
                        SettingsScreen(
                            state = vmState,
                            viewModel = viewModel,
                            contentPadding = PaddingValues(0.dp),
                            onBack = {},
                            onRequestNotificationPermission = {},
                            onOpenExactAlarmSettings = {},
                            onPickCustomRingtone = {}
                        )
                    }
                }
            }

            composeRule.waitForIdle()
            composeRule.onNodeWithTag(SettingsCategory.CONFIGURATION.testTag).performClick()
            composeRule.waitForIdle()

            composeRule.onNodeWithTag("mascot_personality_card").assertIsDisplayed()
            composeRule.onNodeWithTag("personality_card_partner").assertIsDisplayed()
            composeRule.onNodeWithTag("personality_card_coach").assertIsDisplayed()

            composeRule.onNodeWithTag("personality_card_coach").performClick()
            composeRule.waitForIdle()
            assertEquals(MascotPersonality.COACH, viewModel.uiState.value.appPrefs.mascotPersonality)

            composeRule.onNodeWithTag("personality_card_partner").performClick()
            composeRule.waitForIdle()
            assertEquals(MascotPersonality.PARTNER, viewModel.uiState.value.appPrefs.mascotPersonality)
        } finally {
            PreferencesStore(context).setMascotPersonality(MascotPersonality.PARTNER)
            vmStore.clear()
        }
    }

    @Test
    fun todayMascot_rendersCoachAndPartnerAcrossAllThemesWithoutQuickButtons() {
        var themeSetter: ((UiTheme) -> Unit)? = null
        var personalitySetter: ((MascotPersonality) -> Unit)? = null
        val eyeFlow = MutableStateFlow(MascotEyeState(glanceX = 1.2f, headTiltDeg = 2.0f))

        composeRule.setContent {
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            var activePersonality by remember { mutableStateOf(MascotPersonality.COACH) }
            themeSetter = { activeTheme = it }
            personalitySetter = { activePersonality = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    mascotPersonality = activePersonality,
                    performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                )
            ) {
                TodayMascot(
                    mascot = MascotUiState(
                        personality = activePersonality,
                        activeTimePose = TimePhasePose.SALUTE
                    ),
                    eyeStateFlow = eyeFlow,
                    voiceEnabled = false,
                    onTapMascot = {},
                    onPetMascot = {},
                    onReplayVoice = {},
                    modifier = Modifier.testTag("today_mascot_card")
                )
            }
        }

        composeRule.waitForIdle()
        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("mascot_orb_${theme.wireValue}", useUnmergedTree = true).assertIsDisplayed()
            composeRule.onNodeWithText("COACH · FOKUS").assertIsDisplayed()
            composeRule.onNodeWithTag("mascot_quick_hype").assertDoesNotExist()
            composeRule.onNodeWithTag("personality_card_coach").assertDoesNotExist()
        }

        composeRule.runOnUiThread { personalitySetter?.invoke(MascotPersonality.PARTNER) }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("COACH · FOKUS").assertDoesNotExist()
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun coachRoundEyes_personalitySwitchSpinAndCurvedReturnFlight_animateCleanly() {
        val eyeFlow = MutableStateFlow(MascotEyeState(blinkProgress = 0f, glanceX = 1.0f, glanceY = 0.5f))
        var shouldFloatSetter: ((Boolean) -> Unit)? = null
        var personalitySetter: ((MascotPersonality) -> Unit)? = null
        var poseSetter: ((TimePhasePose) -> Unit)? = null
        var morphStateRef: androidx.compose.runtime.State<Float>? = null
        var landingTicks = 0

        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.setContent {
                var shouldFloat by remember { mutableStateOf(true) }
                var activePersonality by remember { mutableStateOf(MascotPersonality.COACH) }
                var activePose by remember { mutableStateOf(TimePhasePose.SALUTE) }
                var switchTick by remember { mutableIntStateOf(0) }
                var landingTick by remember { mutableIntStateOf(0) }
                val homeBoundsHolder = remember {
                    OrbBoundsHolder().apply {
                        rect = androidx.compose.ui.geometry.Rect(32f, 220f, 132f, 320f)
                    }
                }
                shouldFloatSetter = { shouldFloat = it }
                personalitySetter = { next ->
                    if (next != activePersonality) {
                        activePersonality = next
                        switchTick += 1
                    }
                }
                poseSetter = { activePose = it }

                var prevShouldFloat by remember { mutableStateOf(shouldFloat) }
                var isReturning by remember { mutableStateOf(false) }
                val resolvedReturning = if (prevShouldFloat != shouldFloat) {
                    prevShouldFloat && !shouldFloat
                } else {
                    isReturning
                }
                LaunchedEffect(shouldFloat) {
                    isReturning = resolvedReturning
                    prevShouldFloat = shouldFloat
                }

                val morphProgressState = animateFloatAsState(
                    targetValue = if (shouldFloat) 1f else 0f,
                    animationSpec = if (resolvedReturning) IronPulseMotion.ReturnMorphSpring else IronPulseMotion.MorphSpring,
                    finishedListener = { finalVal ->
                        if (!shouldFloat && isReturning && finalVal <= 0.02f) {
                            isReturning = false
                            landingTick += 1
                            landingTicks = landingTick
                        }
                    },
                    label = "testMorph"
                )
                morphStateRef = morphProgressState

                IronPulseTheme(
                    prefs = AppPreferences(
                        uiTheme = UiTheme.CARBON_FORGE,
                        lang = AppLanguage.ID,
                        mascotPersonality = activePersonality,
                        performance = PerformancePreferences(noAnimations = false, staticMascot = false)
                    )
                ) {
                    CompositionLocalProvider(LocalTabActive provides true) {
                        val mascot = MascotUiState(
                            personality = activePersonality,
                            activeTimePose = activePose
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            TodayMascot(
                                mascot = mascot,
                                eyeStateFlow = eyeFlow,
                                voiceEnabled = false,
                                orbVisibleAlpha = { if (shouldFloat || morphProgressState.value > 0.02f) 0f else 1f },
                                homeOrbBoundsHolder = homeBoundsHolder,
                                landingPulseTick = landingTick,
                                onTapMascot = {},
                                onReplayVoice = {},
                                modifier = Modifier
                                    .padding(16.dp)
                                    .testTag("today_mascot_card")
                            )
                            MascotOrbFace(
                                mascot = mascot,
                                eyeStateFlow = eyeFlow,
                                size = 54.dp,
                                animActive = true,
                                switchPulseKey = switchTick
                            )
                            FloatingMascotDock(
                                mascot = mascot,
                                eyeStateFlow = eyeFlow,
                                anchorSideRight = true,
                                anchorYRatio = 0.62f,
                                shouldFloat = shouldFloat,
                                morphProgressState = morphProgressState,
                                homeOrbBoundsHolder = homeBoundsHolder,
                                animActive = true,
                                onTapOrb = {},
                                onSaveAnchor = { _, _ -> }
                            )
                        }
                    }
                }
            }

            composeRule.mainClock.advanceTimeBy(64)
            composeRule.onNodeWithTag("today_mascot_card").assertIsDisplayed()

            // Exercise Coach round eyes across open, blink, beast, smirk, and night poses
            listOf(TimePhasePose.SALUTE, TimePhasePose.BEAST, TimePhasePose.SMIRK, TimePhasePose.MOONLIT).forEach { pose ->
                composeRule.runOnUiThread { poseSetter?.invoke(pose) }
                composeRule.mainClock.advanceTimeBy(48)
            }
            eyeFlow.value = MascotEyeState(blinkProgress = 1f)
            composeRule.mainClock.advanceTimeBy(64)
            eyeFlow.value = MascotEyeState(blinkProgress = 0f, glanceX = 1.3f)

            // Switch personality Partner <-> Coach (360° spin + accent aura burst)
            composeRule.runOnUiThread { personalitySetter?.invoke(MascotPersonality.PARTNER) }
            composeRule.mainClock.advanceTimeBy(240)
            composeRule.runOnUiThread { personalitySetter?.invoke(MascotPersonality.COACH) }
            composeRule.mainClock.advanceTimeBy(600)

            // Trigger return-to-origin curved banked flight and verify landing settle completes
            composeRule.runOnUiThread { shouldFloatSetter?.invoke(false) }
            composeRule.mainClock.advanceTimeBy(180)
            val midMorph = morphStateRef!!.value
            assertTrue("Expected mascot mid-return flight (0 < morph < 1), got $midMorph", midMorph > 0.02f && midMorph < 0.99f)

            composeRule.mainClock.advanceTimeBy(1200)
            val settledMorph = morphStateRef!!.value
            assertTrue("Expected return morph to settle home, got $settledMorph", settledMorph <= 0.02f)
            assertTrue("Expected landing cushion tick to trigger on touchdown", landingTicks >= 1)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun exerciseLibraryFullScreen_filtersBatchSelectsAndCustomModalRemainsDialog() {
        val ctx = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vmStore = ViewModelStore()
        try {
            val vm = ViewModelProvider(
                vmStore,
                ViewModelProvider.AndroidViewModelFactory.getInstance(ctx)
            )[GymViewModel::class.java]
            var addedBatchNames by mutableStateOf<List<String>>(emptyList())

            composeRule.setContent {
                var showLibrary by remember { mutableStateOf(false) }
                val uiState = IronPulseUiState(
                    authReady = true,
                    userUid = "u_ex_lib",
                    selectedExerciseDayKey = DayKey.MON,
                    schedules = GymPresets.createDefaultSchedules("u_ex_lib")
                )
                IronPulseTheme(
                    prefs = AppPreferences(
                        uiTheme = UiTheme.CARBON_FORGE,
                        lang = AppLanguage.ID,
                        performance = PerformancePreferences(noAnimations = false, staticMascot = false)
                    )
                ) {
                    CompositionLocalProvider(LocalTabActive provides true) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ExercisesScreen(
                                state = uiState,
                                viewModel = vm,
                                contentPadding = PaddingValues(0.dp),
                                onOpenLibrary = { showLibrary = true }
                            )
                            if (showLibrary) {
                                ExerciseLibraryFullScreen(
                                    lang = AppLanguage.ID,
                                    selectedDayLabel = "Senin",
                                    existingNames = emptySet(),
                                    remainingSlots = 20,
                                    onAddSingle = {},
                                    onAddBatch = { batch ->
                                        addedBatchNames = batch.map { it.name }
                                        showLibrary = false
                                    },
                                    onDismiss = { showLibrary = false }
                                )
                            }
                        }
                    }
                }
            }

            composeRule.waitForIdle()
            // Open full-screen exercise library
            composeRule.onNodeWithTag("open_exercise_library_button").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("exercise_library_fullscreen").assertIsDisplayed()
            composeRule.onNodeWithTag("exercise_library_back_button").assertIsDisplayed()

            // Search, select movement in multi-select mode, and confirm batch add
            composeRule.onNodeWithTag("exercise_library_search").performTextInput("Barbell Bench")
            composeRule.waitForIdle()
            composeRule.onNode(
                hasAnyAncestor(hasTestTag("exercise_library_fullscreen")) and
                    hasText("Barbell Bench Press") and
                    hasClickAction()
            ).performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("exercise_library_confirm_button").assertIsDisplayed().performClick()
            composeRule.waitForIdle()

            assertTrue("Expected Barbell Bench Press added via batch confirm, got $addedBatchNames", addedBatchNames.any { it.contains("Barbell Bench Press") })
            composeRule.onNodeWithTag("exercise_library_fullscreen").assertDoesNotExist()

            // Verify custom movement creation still opens modal dialog (not full-screen library)
            composeRule.onNodeWithTag("open_custom_exercise_button").performClick()
            composeRule.waitForIdle()
            composeRule.onNodeWithText("Buat Gerakan Kustom").assertIsDisplayed()
            composeRule.onNodeWithTag("exercise_library_fullscreen").assertDoesNotExist()
        } finally {
            vmStore.clear()
        }
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun appWideAnimations_staggeredRevealAndTactileButtons_workWithAndWithoutPerfDisable() {
        var tabActiveSetter: ((Boolean) -> Unit)? = null
        var themeSetter: ((UiTheme) -> Unit)? = null
        var noAnimSetter: ((Boolean) -> Unit)? = null
        var primaryClicks = 0
        var secondaryClicks = 0

        composeRule.setContent {
            var tabActive by remember { mutableStateOf(false) }
            var theme by remember { mutableStateOf(UiTheme.CARBON_FORGE) }
            var noAnim by remember { mutableStateOf(false) }
            tabActiveSetter = { tabActive = it }
            themeSetter = { theme = it }
            noAnimSetter = { noAnim = it }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = theme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(noAnimations = noAnim, staticMascot = noAnim)
                )
            ) {
                CompositionLocalProvider(LocalTabActive provides tabActive) {
                    val active = LocalTabActive.current
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .staggeredCardEntrance(0, active = active, disabledByPerf = noAnim)
                                .testTag("stagger_card_0")
                        ) {
                            Text("Card Zero")
                        }
                        IronPulseButton(
                            onClick = { primaryClicks += 1 },
                            modifier = Modifier
                                .staggeredCardEntrance(1, active = active, disabledByPerf = noAnim)
                                .testTag("tactile_primary_btn")
                        ) {
                            Text("Primary Action")
                        }
                        IronPulseOutlinedButton(
                            onClick = { secondaryClicks += 1 },
                            modifier = Modifier
                                .staggeredCardEntrance(2, active = active, disabledByPerf = noAnim)
                                .testTag("tactile_secondary_btn")
                        ) {
                            Text("Secondary Action")
                        }
                    }
                }
            }
        }

        // Activate pre-warmed tab and trigger staggered reveals
        composeRule.runOnUiThread { tabActiveSetter?.invoke(true) }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("stagger_card_0").assertIsDisplayed()
        composeRule.onNodeWithTag("tactile_primary_btn").assertIsDisplayed().performClick()
        composeRule.onNodeWithTag("tactile_secondary_btn").assertIsDisplayed().performClick()
        assertEquals(1, primaryClicks)
        assertEquals(1, secondaryClicks)

        // Verify Frutiger Aero + noAnimations performance bypass
        composeRule.runOnUiThread {
            themeSetter?.invoke(UiTheme.FRUTIGER_AERO)
            noAnimSetter?.invoke(true)
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tactile_primary_btn").performClick()
        composeRule.onNodeWithTag("tactile_secondary_btn").performClick()
        assertEquals(2, primaryClicks)
        assertEquals(2, secondaryClicks)
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun frameZeroMotion_staggeredRevealAndTransitions_startAtFrameZeroWithoutDispatchLag() {
        val restFlow = MutableStateFlow(RestTimerDisplayState())
        var activeTabSetter: ((AppTab) -> Unit)? = null
        var shouldFloatSetter: ((Boolean) -> Unit)? = null
        var scheduleWeightRef: androidx.compose.runtime.State<Float>? = null
        var morphStateRef: androidx.compose.runtime.State<Float>? = null

        composeRule.mainClock.autoAdvance = false
        try {
            composeRule.setContent {
                var activeTab by remember { mutableStateOf(AppTab.TODAY) }
                var shouldFloat by remember { mutableStateOf(false) }
                activeTabSetter = { activeTab = it }
                shouldFloatSetter = { shouldFloat = it }

                val tabTransition = updateTransition(
                    targetState = activeTab,
                    label = "testTabTransition"
                )
                val scheduleWeight = tabTransition.animateFloat(
                    transitionSpec = { IronPulseMotion.TabSlideSpring },
                    label = "testScheduleWeight"
                ) { tab -> if (tab == AppTab.SCHEDULE) 1f else 0f }
                scheduleWeightRef = scheduleWeight

                val morphTransition = updateTransition(
                    targetState = shouldFloat,
                    label = "testMorphTransition"
                )
                val morphProgress = morphTransition.animateFloat(
                    transitionSpec = {
                        if (initialState && !targetState) IronPulseMotion.ReturnMorphSpring else IronPulseMotion.MorphSpring
                    },
                    label = "testMorphProgress"
                ) { floating -> if (floating) 1f else 0f }
                morphStateRef = morphProgress

                IronPulseTheme(
                    prefs = AppPreferences(
                        uiTheme = UiTheme.CARBON_FORGE,
                        lang = AppLanguage.ID,
                        performance = PerformancePreferences(noAnimations = false, staticMascot = false)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .staggeredCardEntrance(index = 4, active = true, disabledByPerf = false)
                                .testTag("frame0_stagger_card_4")
                        ) {
                            Text("Stagger Card 4")
                        }
                        IronPulseBottomNav(
                            activeTab = activeTab,
                            restTimerFlow = restFlow,
                            onSelectTab = {}
                        )
                    }
                }
            }

            composeRule.mainClock.advanceTimeBy(32)
            composeRule.onNodeWithTag("frame0_stagger_card_4").assertIsDisplayed()

            // Trigger tab switch and mascot float-out synchronously
            composeRule.runOnUiThread {
                activeTabSetter?.invoke(AppTab.SCHEDULE)
                shouldFloatSetter?.invoke(true)
                androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
            }
            composeRule.mainClock.advanceTimeBy(48)
            val earlyTabWeight = scheduleWeightRef!!.value
            val earlyMorphOut = morphStateRef!!.value
            assertTrue("Expected Frame-0 tab transition progress > 0.01f within 48ms, got $earlyTabWeight", earlyTabWeight > 0.01f)
            assertTrue("Expected Frame-0 mascot morph-out > 0.01f within 48ms, got $earlyMorphOut", earlyMorphOut > 0.01f)

            // Let float settle near 1f, then trigger return flight and confirm immediate departure
            composeRule.mainClock.advanceTimeBy(960)
            composeRule.waitForIdle()
            assertTrue("Expected mascot morph settled near 1f, got ${morphStateRef!!.value}", morphStateRef!!.value > 0.95f)

            composeRule.runOnUiThread {
                shouldFloatSetter?.invoke(false)
                androidx.compose.runtime.snapshots.Snapshot.sendApplyNotifications()
            }
            composeRule.mainClock.advanceTimeBy(64)
            val earlyReturnMorph = morphStateRef!!.value
            assertTrue("Expected mascot return flight to begin immediately (< 0.99f within 64ms), got $earlyReturnMorph", earlyReturnMorph < 0.99f)

            composeRule.mainClock.advanceTimeBy(1200)
            assertTrue("Expected mascot return flight to settle near 0f, got ${morphStateRef!!.value}", morphStateRef!!.value <= 0.02f)
        } finally {
            composeRule.mainClock.autoAdvance = true
        }
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun allSixThemes_withShadersAndSettingsPreviewCards_renderCleanlyAcrossPhasesAndGlowToggles() {
        val ctx = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vmStore = ViewModelStore()
        try {
            val viewModel = ViewModelProvider(
                vmStore,
                ViewModelProvider.AndroidViewModelFactory.getInstance(ctx)
            )[GymViewModel::class.java]
            val restFlow = MutableStateFlow(RestTimerDisplayState())
            var themeSetter: ((UiTheme) -> Unit)? = null
            var phaseSetter: ((DayTimePhase) -> Unit)? = null
            var glowOffSetter: ((Boolean) -> Unit)? = null

            composeRule.setContent {
                var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
                var activePhase by remember { mutableStateOf(DayTimePhase.MORNING) }
                var noAmbientGlow by remember { mutableStateOf(false) }
                themeSetter = { activeTheme = it }
                phaseSetter = { activePhase = it }
                glowOffSetter = { noAmbientGlow = it }

                val prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID,
                    performance = PerformancePreferences(
                        noAnimations = true,
                        noAmbientGlow = noAmbientGlow,
                        staticMascot = true
                    )
                )
                val uiState = IronPulseUiState(
                    authReady = true,
                    userUid = "u_theme_craft",
                    appPrefs = prefs
                )

                IronPulseTheme(prefs = prefs, timePhase = activePhase) {
                    val tokens = LocalIronPulseTokens.current
                    CompositionLocalProvider(LocalTabActive provides true) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AmbientPhaseBackdrop()
                            Column(modifier = Modifier.fillMaxSize()) {
                                IronPulseTopBar(
                                    activeTab = AppTab.SETTINGS,
                                    onLogoClick = {},
                                    onOpenSettings = {},
                                    onTestAlarm = {}
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .ironGlassSurface(tokens)
                                            .padding(10.dp)
                                            .testTag("theme_primary_surface")
                                    ) {
                                        Text("Primary Card", color = tokens.palette.onSurface)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .ironGlassSurface(tokens, subtle = true)
                                            .padding(10.dp)
                                            .testTag("theme_subtle_surface")
                                    ) {
                                        Text("Subtle Card", color = tokens.palette.onSurface)
                                    }
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SettingsScreen(
                                        state = uiState,
                                        viewModel = viewModel,
                                        contentPadding = PaddingValues(0.dp),
                                        onBack = {},
                                        onRequestNotificationPermission = {},
                                        onOpenExactAlarmSettings = {},
                                        onPickCustomRingtone = {},
                                        activeCategory = SettingsCategory.THEME
                                    )
                                }
                                IronPulseBottomNav(
                                    activeTab = AppTab.TODAY,
                                    restTimerFlow = restFlow,
                                    onSelectTab = {}
                                )
                            }
                        }
                    }
                }
            }

            composeRule.waitForIdle()
            listOf(
                Triple(UiTheme.MATERIAL_YOU, DayTimePhase.MORNING, false),
                Triple(UiTheme.UBUNTU_YARU, DayTimePhase.AFTERNOON, false),
                Triple(UiTheme.CARBON_FORGE, DayTimePhase.MORNING, false),
                Triple(UiTheme.AMOLED_STEALTH, DayTimePhase.NIGHT, false),
                Triple(UiTheme.FRUTIGER_AERO, DayTimePhase.MORNING, false),
                Triple(UiTheme.FRUTIGER_AERO, DayTimePhase.NIGHT, false),
                Triple(UiTheme.XMATERIAL_YOU, DayTimePhase.NIGHT, false),
                Triple(UiTheme.XMATERIAL_YOU, DayTimePhase.MORNING, true),
                Triple(UiTheme.AMOLED_STEALTH, DayTimePhase.NIGHT, true)
            ).forEach { (theme, phase, noGlow) ->
                composeRule.runOnUiThread {
                    themeSetter?.invoke(theme)
                    phaseSetter?.invoke(phase)
                    glowOffSetter?.invoke(noGlow)
                }
                composeRule.waitForIdle()
                composeRule.onNodeWithTag("os_backdrop_${theme.wireValue}").assertIsDisplayed()
                composeRule.onNodeWithTag("os_top_bar_${theme.wireValue}").assertIsDisplayed()
                composeRule.onNodeWithTag("os_bottom_nav_${theme.wireValue}").assertIsDisplayed()
                composeRule.onNodeWithTag("theme_primary_surface").assertIsDisplayed()
                composeRule.onNodeWithTag("theme_subtle_surface").assertIsDisplayed()
                UiTheme.entries.forEach { cardTheme ->
                    composeRule.onNodeWithTag("theme_card_${cardTheme.wireValue}").performScrollTo().assertIsDisplayed()
                }
            }
        } finally {
            vmStore.clear()
        }
    }

    @Test
    fun allTenThemeAnimationStyles_resolveDistinctMotionSpecsAndPersistAcrossRestarts() {
        assertEquals(10, ThemeAnimationStyle.entries.size)
        assertEquals(10, ThemeAnimationStyle.entries.map { it.wireValue }.toSet().size)

        val resolvedSpecs = ThemeAnimationStyle.entries.map { MotionStyleCatalog.resolve(it) }
        assertEquals(10, resolvedSpecs.map { it.style }.toSet().size)
        assertEquals(IronPulseMotion.ReturnMorphSpring, MotionStyleCatalog.resolve(ThemeAnimationStyle.FLUID_SILK).mascotReturnSpring)

        val ctx = ApplicationProvider.getApplicationContext<android.app.Application>()
        val store = PreferencesStore(ctx)
        ThemeAnimationStyle.entries.forEach { style ->
            store.setThemeAnimationStyle(style)
            assertEquals(style, store.state.value.themeAnimationStyle)
            val freshStore = PreferencesStore(ctx)
            assertEquals(style, freshStore.state.value.themeAnimationStyle)
        }
        store.setThemeAnimationStyle(ThemeAnimationStyle.FLUID_SILK)
    }

    @Test
    @Config(qualifiers = "w412dp-h915dp-xxhdpi")
    fun settingsThemeSubpage_displaysAllTenAnimationStyleCardsAndUpdatesSelection() {
        val ctx = ApplicationProvider.getApplicationContext<android.app.Application>()
        if (com.google.firebase.FirebaseApp.getApps(ctx).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(ctx)
        }
        val vmStore = ViewModelStore()
        try {
            val viewModel = ViewModelProvider(
                vmStore,
                ViewModelProvider.AndroidViewModelFactory.getInstance(ctx)
            )[GymViewModel::class.java]
            viewModel.setThemeAnimationStyle(ThemeAnimationStyle.FLUID_SILK)

            composeRule.setContent {
                val vmState by viewModel.uiState.collectAsStateWithLifecycle()
                val uiState = remember(vmState.appPrefs) {
                    vmState.copy(
                        authReady = true,
                        userUid = "u_anim_styles",
                        appPrefs = vmState.appPrefs.copy(
                            performance = PerformancePreferences(noAnimations = true, staticMascot = true)
                        )
                    )
                }
                IronPulseTheme(prefs = uiState.appPrefs) {
                    val spec = LocalThemeMotionSpec.current
                    assertEquals(vmState.appPrefs.themeAnimationStyle, spec.style)
                    CompositionLocalProvider(LocalTabActive provides true) {
                        SettingsScreen(
                            state = uiState,
                            viewModel = viewModel,
                            contentPadding = PaddingValues(0.dp),
                            onBack = {},
                            onRequestNotificationPermission = {},
                            onOpenExactAlarmSettings = {},
                            onPickCustomRingtone = {},
                            activeCategory = SettingsCategory.THEME
                        )
                    }
                }
            }

            composeRule.waitForIdle()
            composeRule.onNodeWithTag("theme_animation_style_card").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("anim_style_live_preview").performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithTag("anim_styles_grid").performScrollTo().assertIsDisplayed()

            ThemeAnimationStyle.entries.forEach { style ->
                composeRule.onNodeWithTag("anim_style_card_${style.wireValue}")
                    .performScrollTo()
                    .assertIsDisplayed()
            }

            composeRule.onNodeWithTag("anim_style_card_velocity-flip")
                .performScrollTo()
                .performClick()
            composeRule.waitForIdle()
            assertEquals(ThemeAnimationStyle.VELOCITY_FLIP, viewModel.uiState.value.appPrefs.themeAnimationStyle)

            composeRule.onNodeWithTag("anim_style_card_aero-crystal")
                .performScrollTo()
                .performClick()
            composeRule.waitForIdle()
            assertEquals(ThemeAnimationStyle.AERO_CRYSTAL_GLIDE, viewModel.uiState.value.appPrefs.themeAnimationStyle)

            viewModel.setThemeAnimationStyle(ThemeAnimationStyle.FLUID_SILK)
        } finally {
            vmStore.clear()
        }
    }
}
