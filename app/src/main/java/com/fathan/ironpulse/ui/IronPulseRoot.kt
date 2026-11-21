package com.fathan.ironpulse.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fathan.ironpulse.ui.components.AboutDialog
import com.fathan.ironpulse.ui.components.ActiveAlarmOverlay
import com.fathan.ironpulse.ui.components.AmbientPhaseBackdrop
import com.fathan.ironpulse.ui.components.CooldownAlertModal
import com.fathan.ironpulse.ui.components.FeatureTutorialModal
import com.fathan.ironpulse.ui.components.FloatingMascotDock
import com.fathan.ironpulse.ui.components.IronPulseBottomNav
import com.fathan.ironpulse.ui.components.IronPulseMotion
import com.fathan.ironpulse.ui.components.IronPulseTopBar
import com.fathan.ironpulse.ui.components.NamePromptModal
import com.fathan.ironpulse.ui.components.OrbBoundsHolder
import com.fathan.ironpulse.ui.screens.AuthScreen
import com.fathan.ironpulse.ui.screens.ExercisesScreen
import com.fathan.ironpulse.ui.screens.HistoryScreen
import com.fathan.ironpulse.ui.screens.ScheduleScreen
import com.fathan.ironpulse.ui.screens.SettingsScreen
import com.fathan.ironpulse.ui.screens.TodayScreen
import com.fathan.ironpulse.ui.screens.WorkoutScreen
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import kotlinx.coroutines.delay

@Composable
fun IronPulseRoot(
    state: IronPulseUiState,
    viewModel: GymViewModel,
    onRequestGoogleSignIn: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onOpenExactAlarmSettings: () -> Unit,
    onPickCustomRingtone: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val snackbarHostState = remember { SnackbarHostState() }
    val saveableStateHolder = rememberSaveableStateHolder()
    var tabBeforeSettings by remember { mutableStateOf(AppTab.TODAY) }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val navigateToTab: (AppTab) -> Unit = { target ->
        if (target == AppTab.SETTINGS) {
            if (state.activeTab != AppTab.SETTINGS) {
                tabBeforeSettings = state.activeTab
            }
            viewModel.selectTab(AppTab.SETTINGS)
        } else {
            viewModel.selectTab(target)
        }
    }

    BackHandler(enabled = state.isSignedIn && state.activeTab != AppTab.TODAY) {
        if (state.activeTab == AppTab.SETTINGS) {
            val fallback = if (tabBeforeSettings == AppTab.SETTINGS) AppTab.TODAY else tabBeforeSettings
            viewModel.selectTab(fallback)
        } else {
            viewModel.selectTab(AppTab.TODAY)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !state.authReady -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(tokens.canvasBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = tokens.accentColor)
                        Text(
                            text = lang.t("Menyiapkan IronPulse...", "Preparing IronPulse..."),
                            fontSize = 13.sp,
                            color = tokens.mutedTextColor
                        )
                    }
                }
            }

            !state.isSignedIn -> {
                AuthScreen(
                    authLoading = state.authLoading,
                    authError = state.authError,
                    onSelectLang = { viewModel.setLang(it) },
                    onSignInClick = onRequestGoogleSignIn,
                    mascot = state.mascot,
                    mascotEyeFlow = viewModel.mascotEyeFlow
                )
            }

            else -> {
                key(state.userUid) {
                    val todayListState = rememberLazyListState()
                    val workoutListState = rememberLazyListState()
                    val scheduleListState = rememberLazyListState()
                    val historyListState = rememberLazyListState()
                    val exercisesListState = rememberLazyListState()
                    val settingsSubTabListStates = remember {
                        mapOf(
                            "PROFILE" to LazyListState(),
                            "APPEARANCE" to LazyListState(),
                            "ALARM" to LazyListState(),
                            "SYSTEM" to LazyListState()
                        )
                    }
                    val homeOrbBoundsHolder = remember { OrbBoundsHolder() }

                    val modalBlocking = state.showNamePrompt ||
                        state.tutorialOpen ||
                        state.cooldownAlertTarget != null ||
                        state.showAboutDialog ||
                        state.activeTestAlarm != null

                    val showFloatingMascot by remember(
                        state.activeTab,
                        state.showNamePrompt,
                        state.tutorialOpen,
                        state.activeTestAlarm,
                        todayListState
                    ) {
                        derivedStateOf {
                            val hiddenByModal = state.showNamePrompt ||
                                state.tutorialOpen ||
                                state.activeTestAlarm != null
                            if (hiddenByModal) {
                                false
                            } else if (state.activeTab != AppTab.TODAY) {
                                true
                            } else {
                                todayListState.firstVisibleItemIndex > 0 || todayListState.firstVisibleItemScrollOffset > 460
                            }
                        }
                    }

                    val morphProgressState = animateFloatAsState(
                        targetValue = if (showFloatingMascot) 1f else 0f,
                        animationSpec = if (noAnim) tween(0) else IronPulseMotion.MorphSpring,
                        label = "mascotMorphProgress"
                    )

                    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    val topBarContentHeight = 60.dp
                    val bottomBarContentHeight = if (state.activeTab == AppTab.SETTINGS) 0.dp else 64.dp
                    val innerPadding = PaddingValues(
                        top = statusBarTop + topBarContentHeight,
                        bottom = navBarBottom + bottomBarContentHeight
                    )

                    Box(modifier = Modifier.fillMaxSize()) {
                        // LAYER 1: Ambient gradient backdrop + cached multi-tab stage
                        Box(modifier = Modifier.fillMaxSize()) {
                            AmbientPhaseBackdrop()

                            PersistedTabStage(
                                activeTab = state.activeTab,
                                state = state,
                                noAnim = noAnim,
                                prewarmEnabled = !state.showNamePrompt && !state.tutorialOpen,
                                saveableStateHolder = saveableStateHolder,
                                renderTab = { tab, tabState ->
                                    when (tab) {
                                        AppTab.TODAY -> TodayScreen(
                                            state = tabState,
                                            listState = todayListState,
                                            contentPadding = innerPadding,
                                            clockSecondsFlow = viewModel.clockSecondsFlow,
                                            restTimerFlow = viewModel.restTimerDisplayFlow,
                                            mascotEyeFlow = viewModel.mascotEyeFlow,
                                            homeOrbAlphaProvider = {
                                                if (showFloatingMascot || morphProgressState.value > 0.02f) 0f else 1f
                                            },
                                            homeOrbAnimActive = state.activeTab == AppTab.TODAY &&
                                                !showFloatingMascot &&
                                                !modalBlocking,
                                            homeOrbBoundsHolder = homeOrbBoundsHolder,
                                            onTapMascot = { viewModel.tapMascot() },
                                            onReplayMascotVoice = { viewModel.replayMascotVoice() },
                                            onStartRestPreset = { secs, exName ->
                                                viewModel.startManualRestTimer(secs, exName ?: "")
                                            },
                                            onToggleRestPauseOrStart = { viewModel.toggleRestPauseOrStart() },
                                            onAdjustRest = { delta -> viewModel.adjustRestTimer(delta) },
                                            onResetRest = { viewModel.resetRestTimer() },
                                            onOpenWorkout = { exId -> viewModel.openWorkout(exId) },
                                            onOpenSchedule = { navigateToTab(AppTab.SCHEDULE) },
                                            onOpenExercisesForDay = { day -> viewModel.openExercisesForDay(day) }
                                        )

                                        AppTab.WORKOUT -> WorkoutScreen(
                                            state = tabState,
                                            viewModel = viewModel,
                                            contentPadding = innerPadding,
                                            onBackToToday = { viewModel.selectTab(AppTab.TODAY) },
                                            onOpenSchedule = { navigateToTab(AppTab.SCHEDULE) },
                                            onOpenExercisesForDay = { day -> viewModel.openExercisesForDay(day) },
                                            listState = workoutListState
                                        )

                                        AppTab.SCHEDULE -> ScheduleScreen(
                                            state = tabState,
                                            viewModel = viewModel,
                                            contentPadding = innerPadding,
                                            onOpenExercisesForDay = { day -> viewModel.openExercisesForDay(day) },
                                            listState = scheduleListState
                                        )

                                        AppTab.HISTORY -> HistoryScreen(
                                            state = tabState,
                                            contentPadding = innerPadding,
                                            onStartTodayWorkout = { viewModel.openWorkout(null) },
                                            listState = historyListState
                                        )

                                        AppTab.EXERCISES -> ExercisesScreen(
                                            state = tabState,
                                            viewModel = viewModel,
                                            contentPadding = innerPadding,
                                            listState = exercisesListState
                                        )

                                        AppTab.SETTINGS -> SettingsScreen(
                                            state = tabState,
                                            viewModel = viewModel,
                                            contentPadding = innerPadding,
                                            onBack = {
                                                val fallback = if (tabBeforeSettings == AppTab.SETTINGS) AppTab.TODAY else tabBeforeSettings
                                                viewModel.selectTab(fallback)
                                            },
                                            onRequestNotificationPermission = onRequestNotificationPermission,
                                            onOpenExactAlarmSettings = onOpenExactAlarmSettings,
                                            onPickCustomRingtone = onPickCustomRingtone,
                                            subTabListStates = settingsSubTabListStates
                                        )
                                    }
                                }
                            )
                        }

                        // LAYER 2: TopBar Chrome
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                        ) {
                            IronPulseTopBar(
                                activeTab = state.activeTab,
                                aboutOpen = state.showAboutDialog,
                                onLogoClick = { navigateToTab(AppTab.TODAY) },
                                onOpenSettings = {
                                    if (state.activeTab == AppTab.SETTINGS) {
                                        val fallback = if (tabBeforeSettings == AppTab.SETTINGS) AppTab.TODAY else tabBeforeSettings
                                        viewModel.selectTab(fallback)
                                    } else {
                                        navigateToTab(AppTab.SETTINGS)
                                    }
                                },
                                onOpenAbout = { viewModel.openAboutDialog() },
                                onTestAlarm = { viewModel.triggerInAppTestAlarm() }
                            )
                        }

                        // Floating Offline & Sync Error Banners anchored below TopBar
                        AnimatedVisibility(
                            visible = state.isOffline || !state.syncError.isNullOrBlank(),
                            enter = if (noAnim) EnterTransition.None else expandVertically() + fadeIn(tween(180)),
                            exit = if (noAnim) ExitTransition.None else shrinkVertically() + fadeOut(tween(150)),
                            modifier = Modifier
                                .widthIn(max = 640.dp)
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(top = innerPadding.calculateTopPadding() + 8.dp, start = 16.dp, end = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (state.isOffline) {
                                    Surface(
                                        color = Color(0xE61F180A),
                                        border = BorderStroke(1.dp, Color(0x88F59E0B)),
                                        shape = RoundedCornerShape(12.dp),
                                        shadowElevation = 8.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.CloudOff,
                                                contentDescription = null,
                                                tint = Color(0xFFFBBF24),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = lang.t(
                                                    "Mode Offline — catatan disimpan lokal & akan tersinkronisasi saat online.",
                                                    "Offline mode — logs save locally and sync once connected."
                                                ),
                                                fontSize = 11.5.sp,
                                                color = Color(0xFFFDE68A),
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }

                                val syncErr = state.syncError
                                if (!syncErr.isNullOrBlank()) {
                                    Surface(
                                        color = Color(0xE6220D0E),
                                        border = BorderStroke(1.dp, Color(0x88EF4444)),
                                        shape = RoundedCornerShape(12.dp),
                                        shadowElevation = 8.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Warning,
                                                contentDescription = null,
                                                tint = Color(0xFFFCA5A5),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = syncErr,
                                                fontSize = 11.5.sp,
                                                color = Color(0xFFFECACA),
                                                modifier = Modifier.weight(1f)
                                            )
                                            TextButton(
                                                onClick = { viewModel.retrySync() },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(lang.t("Coba Lagi", "Retry"), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // LAYER 3: Bottom Navigation Bar
                        AnimatedVisibility(
                            visible = state.activeTab != AppTab.SETTINGS,
                            enter = if (noAnim) EnterTransition.None else slideInVertically(tween(220)) { it } + fadeIn(tween(180)),
                            exit = if (noAnim) ExitTransition.None else slideOutVertically(tween(180)) { it } + fadeOut(tween(140)),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {
                            IronPulseBottomNav(
                                activeTab = state.activeTab,
                                restTimerFlow = viewModel.restTimerDisplayFlow,
                                onSelectTab = { navigateToTab(it) }
                            )
                        }

                        // Snackbar Host above bottom nav
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(bottom = bottomBarContentHeight + 8.dp)
                        )

                        // LAYER 4: Draggable Floating Mascot Companion Orb
                        FloatingMascotDock(
                            mascot = state.mascot,
                            eyeStateFlow = viewModel.mascotEyeFlow,
                            anchorSideRight = state.appPrefs.floatingOrbSideRight,
                            anchorYRatio = state.appPrefs.floatingOrbYRatio,
                            shouldFloat = showFloatingMascot,
                            morphProgressState = morphProgressState,
                            homeOrbBoundsHolder = homeOrbBoundsHolder,
                            animActive = !modalBlocking,
                            onTapOrb = { viewModel.tapMascot() },
                            onSaveAnchor = { sideRight, yRatio -> viewModel.setFloatingOrbAnchor(sideRight, yRatio) }
                        )

                        // LAYER 5: In-Window Modals
                        if (state.showNamePrompt) {
                            NamePromptModal(
                                initialName = state.profile?.displayName ?: state.userDisplayName ?: "",
                                mascot = state.mascot,
                                onSaveName = { viewModel.confirmDisplayNameOnboarding(it) }
                            )
                        }

                        if (!state.showNamePrompt && state.tutorialOpen) {
                            FeatureTutorialModal(
                                startAtLanguageStep = state.tutorialStartWithLang,
                                onSelectLanguage = { viewModel.setLang(it) },
                                onComplete = { viewModel.closeTutorial() },
                                onJumpToTab = { navigateToTab(it) }
                            )
                        }

                        val cooldownTarget = state.cooldownAlertTarget
                        if (cooldownTarget != null) {
                            CooldownAlertModal(
                                cooldownSecondsFlow = viewModel.cooldownSecondsFlow,
                                exerciseName = cooldownTarget.exerciseName,
                                setNumber = cooldownTarget.setNumber,
                                onBypass = { viewModel.bypassCooldownAndComplete() },
                                onDismiss = { viewModel.dismissCooldownAlert() }
                            )
                        }

                        if (state.showAboutDialog) {
                            AboutDialog(onDismiss = { viewModel.closeAboutDialog() })
                        }

                        // Full-Screen In-App Test Alarm Overlay
                        val testAlarm = state.activeTestAlarm
                        if (testAlarm != null) {
                            ActiveAlarmOverlay(
                                alarm = testAlarm,
                                snoozeMinutes = state.profile?.snoozeMinutes ?: 5,
                                onStartWorkout = { viewModel.dismissInAppTestAlarm(openWorkoutAfter = true) },
                                onSnooze = { viewModel.snoozeInAppTestAlarm() },
                                onDismiss = { viewModel.dismissInAppTestAlarm(openWorkoutAfter = false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Keeps visited & pre-warmed tabs alive in composition & scroll state with:
 * 1) Immediate post-frame idle pre-warming (zero splash wait).
 * 2) Inactive tab render freeze gate (hidden tabs keep cached state and skip background recompositions).
 * 3) Fluid GPU spring translation & alpha transitions (`IronPulseMotion.TabSlideSpring`).
 */
@Composable
private fun PersistedTabStage(
    activeTab: AppTab,
    state: IronPulseUiState,
    noAnim: Boolean,
    prewarmEnabled: Boolean,
    saveableStateHolder: SaveableStateHolder,
    renderTab: @Composable (AppTab, IronPulseUiState) -> Unit
) {
    var visitedTabs by remember { mutableStateOf(setOf(activeTab)) }
    var previousTab by remember { mutableStateOf<AppTab?>(null) }
    var transitionDir by remember { mutableIntStateOf(1) }
    val progress = remember { Animatable(1f) }

    // Pre-warm unvisited tabs smoothly in idle frames right after Frame 1 settles
    LaunchedEffect(prewarmEnabled) {
        if (!prewarmEnabled) return@LaunchedEffect
        withFrameNanos { }
        val preloadOrder = listOf(
            AppTab.SCHEDULE,
            AppTab.HISTORY,
            AppTab.EXERCISES,
            AppTab.WORKOUT,
            AppTab.SETTINGS
        )
        for (tab in preloadOrder) {
            if (tab !in visitedTabs) {
                visitedTabs = visitedTabs + tab
                withFrameNanos { }
                delay(16L)
            }
        }
    }

    LaunchedEffect(activeTab) {
        val needsFirstBuild = activeTab !in visitedTabs
        if (needsFirstBuild) {
            visitedTabs = visitedTabs + activeTab
        }
        val prev = previousTab
        if (prev != null && prev != activeTab && !noAnim) {
            transitionDir = if (activeTab.ordinal >= prev.ordinal) 1 else -1
            progress.snapTo(0f)
            if (needsFirstBuild) {
                withFrameNanos { }
            }
            progress.animateTo(1f, IronPulseMotion.TabSlideSpring)
        } else {
            progress.snapTo(1f)
        }
        previousTab = activeTab
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (visitedTabs.size > 1) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0.5f)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                event.changes.forEach { it.consume() }
                            }
                        }
                    }
            )
        }

        visitedTabs.forEach { tab ->
            key(tab) {
                val isCurrent = tab == activeTab
                var cachedTabState by remember { mutableStateOf(state) }
                if (isCurrent) {
                    cachedTabState = state
                }
                val z = if (isCurrent) 1f else 0f
                val mod = Modifier
                    .fillMaxSize()
                    .zIndex(z)
                    .graphicsLayer {
                        if (isCurrent) {
                            val p = progress.value
                            alpha = if (noAnim) 1f else p.coerceIn(0f, 1f)
                            translationX = if (noAnim) 0f else (1f - p) * transitionDir * (size.width * 0.055f)
                        } else {
                            alpha = 0f
                        }
                    }

                Box(modifier = mod) {
                    CompositionLocalProvider(LocalTabActive provides isCurrent) {
                        saveableStateHolder.SaveableStateProvider(key = tab) {
                            renderTab(tab, cachedTabState)
                        }
                    }
                }
            }
        }
    }
}
