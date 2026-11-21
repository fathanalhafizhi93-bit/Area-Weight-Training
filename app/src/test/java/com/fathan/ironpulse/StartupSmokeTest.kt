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
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.fathan.ironpulse.data.AppPreferences
import com.fathan.ironpulse.data.GymCachedSnapshot
import com.fathan.ironpulse.data.GymPresets
import com.fathan.ironpulse.data.LocalGymCacheStore
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.CustomRingtone
import com.fathan.ironpulse.model.DayKey
import com.fathan.ironpulse.model.MuscleGroup
import com.fathan.ironpulse.model.RingtoneStorageMode
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.model.UserProfile
import com.fathan.ironpulse.model.WorkoutLog
import com.fathan.ironpulse.model.WorkoutLogEntry
import com.fathan.ironpulse.model.WorkoutSetLog
import com.fathan.ironpulse.ui.components.AboutDialog
import com.fathan.ironpulse.ui.components.IronPulseBottomNav
import com.fathan.ironpulse.ui.components.IronPulseTopBar
import com.fathan.ironpulse.ui.components.LaunchIntroOverlay
import com.fathan.ironpulse.ui.components.LiveFluidClockSlot
import com.fathan.ironpulse.ui.components.MascotOrbFace
import com.fathan.ironpulse.ui.screens.AuthScreen
import com.fathan.ironpulse.ui.theme.IronPulseTheme
import com.fathan.ironpulse.ui.theme.LocalBackdropHazeState
import com.fathan.ironpulse.ui.theme.LocalBlurEnabled
import com.fathan.ironpulse.ui.theme.LocalChromeHazeState
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import com.fathan.ironpulse.ui.viewmodel.RestTimerDisplayState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
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
    fun authScreen_composesWithFlowRowAndRendersSignInButton() {
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
                    onSignInClick = {}
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag("google_sign_in_button").assertIsDisplayed()
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Test
    fun hazeChromeAndAllThemes_composePastSplashBlurTransitionWithoutCrash() {
        val restFlow = MutableStateFlow(RestTimerDisplayState())
        var blurStateSetter: ((Boolean) -> Unit)? = null
        var themeSetter: ((UiTheme) -> Unit)? = null

        composeRule.setContent {
            var blurEnabled by remember { mutableStateOf(false) }
            var activeTheme by remember { mutableStateOf(UiTheme.MATERIAL_YOU) }
            blurStateSetter = { blurEnabled = it }
            themeSetter = { activeTheme = it }

            val backdropHaze = remember { HazeState() }
            val chromeHaze = remember { HazeState() }

            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = activeTheme,
                    lang = AppLanguage.ID
                )
            ) {
                CompositionLocalProvider(
                    LocalBackdropHazeState provides backdropHaze,
                    LocalChromeHazeState provides chromeHaze,
                    LocalBlurEnabled provides blurEnabled
                ) {
                    val tokens = LocalIronPulseTokens.current
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(chromeHaze)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .hazeSource(backdropHaze)
                            )
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
        }

        composeRule.waitForIdle()
        composeRule.runOnUiThread { blurStateSetter?.invoke(true) }
        composeRule.waitForIdle()

        UiTheme.entries.forEach { theme ->
            composeRule.runOnUiThread { themeSetter?.invoke(theme) }
            composeRule.waitForIdle()
        }

        composeRule.onNodeWithTag("smoke_chip_1").assertIsDisplayed()
    }

    @Test
    fun launchIntroOverlay_rendersSplashLogoWithoutCrash() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID
                )
            ) {
                LaunchIntroOverlay(onDismiss = {})
            }
        }
        composeRule.mainClock.advanceTimeBy(200L)
        composeRule.waitForIdle()
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

        // Suspend background tab rendering
        composeRule.runOnUiThread {
            tabActiveSetter?.invoke(false)
            clockFlow.value = 1_730_000_001L
            eyeFlow.value = MascotEyeState(isBlinking = true)
        }
        composeRule.waitForIdle()

        // Resume active tab rendering
        composeRule.runOnUiThread {
            tabActiveSetter?.invoke(true)
            clockFlow.value = 1_730_000_002L
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("mascot_orb_smoke").assertIsDisplayed()
    }

    @Test
    fun aboutDialog_rendersVersionOneBetaLabel() {
        composeRule.setContent {
            val chromeHaze = remember { HazeState() }
            IronPulseTheme(
                prefs = AppPreferences(
                    uiTheme = UiTheme.MATERIAL_YOU,
                    lang = AppLanguage.ID
                )
            ) {
                CompositionLocalProvider(LocalChromeHazeState provides chromeHaze) {
                    AboutDialog(onDismiss = {})
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("About IronPulse").assertIsDisplayed()
        composeRule.onNodeWithText("v1.0.0 beta").assertIsDisplayed()
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
}
