package com.fathan.ironpulse.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.fathan.ironpulse.model.MedalTier
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.components.ActiveAlarmOverlay
import com.fathan.ironpulse.ui.components.AmbientPhaseBackdrop
import com.fathan.ironpulse.ui.components.CooldownAlertModal
import com.fathan.ironpulse.ui.components.FloatingMascotDock
import com.fathan.ironpulse.ui.components.FlyingMedalOverlay
import com.fathan.ironpulse.ui.components.IronPulseBottomNav
import com.fathan.ironpulse.ui.components.IronPulseIcon
import com.fathan.ironpulse.ui.components.IronPulseMotion
import com.fathan.ironpulse.ui.components.IronPulseTopBar
import com.fathan.ironpulse.ui.components.LocalThemeMotionSpec
import com.fathan.ironpulse.ui.components.LocalTutorialSpotlightRegistry
import com.fathan.ironpulse.ui.components.MedalUnlockCelebrationOverlay
import com.fathan.ironpulse.ui.components.NamePromptModal
import com.fathan.ironpulse.ui.components.OrbBoundsHolder
import com.fathan.ironpulse.ui.components.PulseMascotTutorialOverlay
import com.fathan.ironpulse.ui.components.ThemedIcon
import com.fathan.ironpulse.ui.components.TutorialSpotlightRegistry
import com.fathan.ironpulse.ui.screens.AuthScreen
import com.fathan.ironpulse.ui.screens.ExerciseLibraryFullScreen
import com.fathan.ironpulse.ui.screens.ExercisesScreen
import com.fathan.ironpulse.ui.screens.HistoryScreen
import com.fathan.ironpulse.ui.screens.ScheduleScreen
import com.fathan.ironpulse.ui.screens.SettingsCategory
import com.fathan.ironpulse.ui.screens.SettingsScreen
import com.fathan.ironpulse.ui.screens.TodayScreen
import com.fathan.ironpulse.ui.screens.WorkoutScreen
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalScrollActiveState
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.LocalTabSettled
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

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
    var activeSettingsCategory by remember { mutableStateOf<SettingsCategory?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.snackbarEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val navigateToTab: (AppTab) -> Unit = { target ->
        if (target == AppTab.SETTINGS) {
            if (state.activeTab != AppTab.SETTINGS) {
                tabBeforeSettings = state.activeTab
                activeSettingsCategory = null
            }
            viewModel.selectTab(AppTab.SETTINGS)
        } else {
            viewModel.selectTab(target)
        }
    }

    BackHandler(enabled = state.isSignedIn && state.activeTab != AppTab.TODAY) {
        if (state.activeTab == AppTab.SETTINGS) {
            if (activeSettingsCategory != null) {
                activeSettingsCategory = null
            } else {
                val fallback = if (tabBeforeSettings == AppTab.SETTINGS) AppTab.TODAY else tabBeforeSettings
                viewModel.selectTab(fallback)
            }
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
                        (listOf("HUB") + SettingsCategory.entries.map { it.name }).associateWith { LazyListState() }
                    }
                    val homeOrbBoundsHolder = remember { OrbBoundsHolder() }
                    val spotlightRegistry = remember { TutorialSpotlightRegistry() }
                    spotlightRegistry.active = state.tutorialOpen
                    val tourScrollScope = rememberCoroutineScope()
                    var medalSlotTargetCenter by remember { mutableStateOf(Offset.Zero) }
                    var flyingMedalState by remember { mutableStateOf<Pair<MedalTier, Offset>?>(null) }
                    var showExerciseLibraryScreen by remember { mutableStateOf(false) }

                    LaunchedEffect(state.activeTab) {
                        if (state.activeTab != AppTab.EXERCISES) {
                            showExerciseLibraryScreen = false
                        }
                    }

                    val modalBlocking = state.showNamePrompt ||
                        state.tutorialOpen ||
                        state.cooldownAlertTarget != null ||
                        state.activeTestAlarm != null ||
                        state.pendingMedalUnlock != null ||
                        flyingMedalState != null ||
                        showExerciseLibraryScreen

                    val showFloatingMascotState = remember(
                        state.activeTab,
                        state.showNamePrompt,
                        state.tutorialOpen,
                        state.activeTestAlarm,
                        state.pendingMedalUnlock,
                        flyingMedalState,
                        showExerciseLibraryScreen,
                        todayListState
                    ) {
                        derivedStateOf {
                            val hiddenByModal = state.showNamePrompt ||
                                state.tutorialOpen ||
                                state.activeTestAlarm != null ||
                                state.pendingMedalUnlock != null ||
                                flyingMedalState != null ||
                                showExerciseLibraryScreen
                            if (hiddenByModal) {
                                false
                            } else if (state.activeTab != AppTab.TODAY) {
                                true
                            } else {
                                todayListState.firstVisibleItemIndex > 0 || todayListState.firstVisibleItemScrollOffset > 460
                            }
                        }
                    }

                    val morphProgressSource = remember { MorphProgressSource() }
                    val homeLandingTickState = remember { mutableIntStateOf(0) }

                    val activeScrollState = when (state.activeTab) {
                        AppTab.TODAY -> todayListState
                        AppTab.WORKOUT -> workoutListState
                        AppTab.SCHEDULE -> scheduleListState
                        AppTab.HISTORY -> historyListState
                        AppTab.EXERCISES -> exercisesListState
                        AppTab.SETTINGS -> settingsSubTabListStates[activeSettingsCategory?.name ?: "HUB"]
                    }
                    val isActiveListScrollingState = remember(activeScrollState) {
                        derivedStateOf { activeScrollState?.isScrollInProgress == true }
                    }

                    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    val topBarContentHeight = 60.dp
                    val bottomBarContentHeight = when {
                        state.activeTab == AppTab.SETTINGS -> 0.dp
                        tokens.uiTheme == UiTheme.UBUNTU_YARU -> 80.dp
                        else -> 68.dp
                    }
                    val innerPadding = PaddingValues(
                        top = statusBarTop + topBarContentHeight,
                        bottom = navBarBottom + bottomBarContentHeight
                    )

                    CompositionLocalProvider(
                        LocalTutorialSpotlightRegistry provides spotlightRegistry,
                        LocalScrollActiveState provides isActiveListScrollingState
                    ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // LAYER 1: Ambient gradient backdrop + active/outgoing tab stage
                        Box(modifier = Modifier.fillMaxSize()) {
                            AmbientPhaseBackdrop()

                            PersistedTabStage(
                                activeTab = state.activeTab,
                                state = state,
                                noAnim = noAnim,
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
                                                if (state.tutorialOpen || showFloatingMascotState.value || morphProgressSource.state.value > 0.02f) 0f else 1f
                                            },
                                            homeOrbAnimActive = state.activeTab == AppTab.TODAY && !modalBlocking,
                                            homeOrbAnimActiveProvider = {
                                                state.activeTab == AppTab.TODAY &&
                                                    !showFloatingMascotState.value &&
                                                    !modalBlocking
                                            },
                                            homeOrbBoundsHolder = homeOrbBoundsHolder,
                                            homeLandingTick = 0,
                                            homeLandingTickState = homeLandingTickState,
                                            onTapMascot = { viewModel.tapMascot() },
                                            onPetMascot = { viewModel.petMascot(true) },
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
                                            onOpenHistory = { navigateToTab(AppTab.HISTORY) },
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
                                            listState = historyListState,
                                            landingBurstTick = state.medalLandingBurstTick,
                                            onMedalSlotPositioned = { medalSlotTargetCenter = it }
                                        )

                                        AppTab.EXERCISES -> ExercisesScreen(
                                            state = tabState,
                                            viewModel = viewModel,
                                            contentPadding = innerPadding,
                                            listState = exercisesListState,
                                            onOpenLibrary = { showExerciseLibraryScreen = true }
                                        )

                                        AppTab.SETTINGS -> SettingsScreen(
                                            state = tabState,
                                            viewModel = viewModel,
                                            contentPadding = innerPadding,
                                            onBack = {
                                                if (activeSettingsCategory != null) {
                                                    activeSettingsCategory = null
                                                } else {
                                                    val fallback = if (tabBeforeSettings == AppTab.SETTINGS) AppTab.TODAY else tabBeforeSettings
                                                    viewModel.selectTab(fallback)
                                                }
                                            },
                                            onRequestNotificationPermission = onRequestNotificationPermission,
                                            onOpenExactAlarmSettings = onOpenExactAlarmSettings,
                                            onPickCustomRingtone = onPickCustomRingtone,
                                            activeCategory = activeSettingsCategory,
                                            onSelectCategory = { activeSettingsCategory = it },
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
                                onLogoClick = { navigateToTab(AppTab.TODAY) },
                                onOpenSettings = {
                                    if (state.activeTab == AppTab.SETTINGS) {
                                        activeSettingsCategory = null
                                        val fallback = if (tabBeforeSettings == AppTab.SETTINGS) AppTab.TODAY else tabBeforeSettings
                                        viewModel.selectTab(fallback)
                                    } else {
                                        navigateToTab(AppTab.SETTINGS)
                                    }
                                },
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
                                            ThemedIcon(
                                                icon = IronPulseIcon.CLOUD_OFF,
                                                contentDescription = null,
                                                tint = Color(0xFFFBBF24),
                                                size = 16.dp
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
                                            ThemedIcon(
                                                icon = IronPulseIcon.WARNING,
                                                contentDescription = null,
                                                tint = Color(0xFFFCA5A5),
                                                size = 16.dp
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
                                                ThemedIcon(IronPulseIcon.REFRESH, contentDescription = null, tint = Color.White, size = 14.dp)
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
                            visible = state.activeTab != AppTab.SETTINGS && !showExerciseLibraryScreen,
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

                        // LAYER 4: Draggable Floating Mascot Companion Orb (isolated from root recomposition)
                        MascotDockCoordinator(
                            mascot = state.mascot,
                            eyeStateFlow = viewModel.mascotEyeFlow,
                            anchorSideRight = state.appPrefs.floatingOrbSideRight,
                            anchorYRatio = state.appPrefs.floatingOrbYRatio,
                            showFloatingMascotState = showFloatingMascotState,
                            noAnim = noAnim,
                            modalBlocking = modalBlocking,
                            homeOrbBoundsHolder = homeOrbBoundsHolder,
                            morphProgressSource = morphProgressSource,
                            homeLandingTickState = homeLandingTickState,
                            onTapOrb = { viewModel.tapMascot() },
                            onPetOrb = { viewModel.petMascot(true) },
                            onSaveAnchor = { sideRight, yRatio -> viewModel.setFloatingOrbAnchor(sideRight, yRatio) }
                        )

                        // LAYER 5: Full-Screen Routes & In-Window Modals
                        val rootMotionSpec = LocalThemeMotionSpec.current
                        AnimatedVisibility(
                            visible = showExerciseLibraryScreen,
                            enter = if (noAnim) {
                                EnterTransition.None
                            } else {
                                slideInVertically(tween(rootMotionSpec.modalFadeMs + 80)) { it / 7 } +
                                    scaleIn(initialScale = rootMotionSpec.modalStartScale.coerceAtLeast(0.955f), animationSpec = rootMotionSpec.modalSpring) +
                                    fadeIn(tween(rootMotionSpec.modalFadeMs))
                            },
                            exit = if (noAnim) {
                                ExitTransition.None
                            } else {
                                slideOutVertically(tween(rootMotionSpec.modalFadeMs + 10)) { it / 9 } +
                                    scaleOut(targetScale = 0.975f, animationSpec = tween(rootMotionSpec.modalFadeMs)) +
                                    fadeOut(tween((rootMotionSpec.modalFadeMs - 20).coerceAtLeast(100)))
                            }
                        ) {
                            val selectedDay = state.selectedExerciseDayKey
                            val dayExercises = state.schedules[selectedDay]?.exercises ?: emptyList()
                            ExerciseLibraryFullScreen(
                                lang = lang,
                                selectedDayLabel = selectedDay.label(lang),
                                existingNames = dayExercises.map { it.name.lowercase(Locale.ROOT) }.toSet(),
                                remainingSlots = (30 - dayExercises.size).coerceAtLeast(0),
                                onAddSingle = { tmpl ->
                                    viewModel.addTemplatesToDay(selectedDay, listOf(tmpl))
                                },
                                onAddBatch = { tmpls ->
                                    viewModel.addTemplatesToDay(selectedDay, tmpls)
                                    showExerciseLibraryScreen = false
                                },
                                onDismiss = { showExerciseLibraryScreen = false }
                            )
                        }

                        if (state.showNamePrompt) {
                            NamePromptModal(
                                initialName = state.profile?.displayName ?: state.userDisplayName ?: "",
                                mascot = state.mascot,
                                onSaveName = { viewModel.confirmDisplayNameOnboarding(it) }
                            )
                        }

                        if (!state.showNamePrompt && state.tutorialOpen) {
                            PulseMascotTutorialOverlay(
                                userName = state.profile?.displayName?.takeIf { it.isNotBlank() }
                                    ?: state.userDisplayName
                                    ?: "",
                                baseMascot = state.mascot,
                                mascotEyeFlow = viewModel.mascotEyeFlow,
                                startAtWelcomeLanguageStep = state.tutorialStartWithLang,
                                homeOrbBoundsHolder = homeOrbBoundsHolder,
                                spotlightRegistry = spotlightRegistry,
                                onSelectLanguage = { viewModel.setLang(it) },
                                onNavigateToStepTab = { tab ->
                                    navigateToTab(tab)
                                    tourScrollScope.launch {
                                        when (tab) {
                                            AppTab.TODAY -> todayListState.scrollToItem(0)
                                            AppTab.SCHEDULE -> scheduleListState.scrollToItem(0)
                                            AppTab.EXERCISES -> exercisesListState.scrollToItem(0)
                                            AppTab.HISTORY -> historyListState.scrollToItem(0)
                                            AppTab.SETTINGS -> {
                                                activeSettingsCategory = null
                                                settingsSubTabListStates["HUB"]?.scrollToItem(0)
                                            }
                                            AppTab.WORKOUT -> workoutListState.scrollToItem(0)
                                        }
                                    }
                                },
                                onPlayChirp = { variant -> viewModel.playTutorialChirp(variant) },
                                onTapMascotFeedback = { viewModel.tapTutorialMascot() },
                                onComplete = { viewModel.closeTutorial() }
                            )
                        }

                        val cooldownTarget = state.cooldownAlertTarget
                        if (cooldownTarget != null) {
                            CooldownAlertModal(
                                cooldownSecondsFlow = viewModel.cooldownSecondsFlow,
                                exerciseName = cooldownTarget.exerciseName,
                                setNumber = cooldownTarget.setNumber,
                                bypassesRemaining = cooldownTarget.bypassesRemaining,
                                onBypass = { viewModel.bypassCooldownAndComplete() },
                                onDismiss = { viewModel.dismissCooldownAlert() }
                            )
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

                        // Monthly Medal Unlock Celebration + Flight to History Top Showcase
                        val unlockEvent = state.pendingMedalUnlock
                        if (unlockEvent != null && flyingMedalState == null) {
                            MedalUnlockCelebrationOverlay(
                                event = unlockEvent,
                                mascot = state.mascot,
                                mascotEyeFlow = viewModel.mascotEyeFlow,
                                onStartFlightToHistory = { sourceCenter ->
                                    if (noAnim || sourceCenter == Offset.Zero) {
                                        viewModel.confirmMedalUnlockAndFlyToHistory()
                                        navigateToTab(AppTab.HISTORY)
                                        tourScrollScope.launch {
                                            historyListState.scrollToItem(0)
                                            viewModel.triggerMedalLandingBurst()
                                        }
                                    } else {
                                        flyingMedalState = unlockEvent.tier to sourceCenter
                                        viewModel.confirmMedalUnlockAndFlyToHistory()
                                        navigateToTab(AppTab.HISTORY)
                                        tourScrollScope.launch {
                                            historyListState.scrollToItem(0)
                                        }
                                    }
                                }
                            )
                        }

                        val flight = flyingMedalState
                        if (flight != null) {
                            FlyingMedalOverlay(
                                tier = flight.first,
                                startCenter = flight.second,
                                targetCenter = medalSlotTargetCenter,
                                onFlightFinished = {
                                    flyingMedalState = null
                                    viewModel.triggerMedalLandingBurst()
                                }
                            )
                        }
                    }
                    }
                }
            }
        }
    }
}

private class MorphProgressSource {
    var state: State<Float> = mutableFloatStateOf(0f)
}

@Composable
private fun MascotDockCoordinator(
    mascot: MascotUiState,
    eyeStateFlow: StateFlow<MascotEyeState>,
    anchorSideRight: Boolean,
    anchorYRatio: Float,
    showFloatingMascotState: State<Boolean>,
    noAnim: Boolean,
    modalBlocking: Boolean,
    homeOrbBoundsHolder: OrbBoundsHolder,
    morphProgressSource: MorphProgressSource,
    homeLandingTickState: MutableIntState,
    onTapOrb: () -> Unit,
    onPetOrb: () -> Unit,
    onSaveAnchor: (sideRight: Boolean, yRatio: Float) -> Unit
) {
    val showFloatingMascot = showFloatingMascotState.value
    val prevFloatStateRef = remember { booleanArrayOf(showFloatingMascot) }
    var hadStartedReturn by remember { mutableStateOf(false) }

    SideEffect {
        val prev = prevFloatStateRef[0]
        if (prev != showFloatingMascot) {
            if (prev && !showFloatingMascot) {
                hadStartedReturn = true
            } else if (showFloatingMascot) {
                hadStartedReturn = false
            }
            prevFloatStateRef[0] = showFloatingMascot
        }
    }

    val morphTransition = updateTransition(
        targetState = showFloatingMascot,
        label = "mascotMorphTransition"
    )
    LaunchedEffect(
        morphTransition.currentState,
        morphTransition.targetState,
        morphTransition.isRunning,
        noAnim
    ) {
        if (!morphTransition.isRunning &&
            !morphTransition.currentState &&
            !morphTransition.targetState &&
            hadStartedReturn
        ) {
            hadStartedReturn = false
            if (!noAnim) {
                homeLandingTickState.intValue += 1
            }
        }
    }

    val motionSpec = LocalThemeMotionSpec.current
    val morphProgressState = morphTransition.animateFloat(
        transitionSpec = {
            when {
                noAnim -> snap()
                initialState && !targetState -> motionSpec.mascotReturnSpring
                else -> motionSpec.mascotMorphSpring
            }
        },
        label = "mascotMorphProgress"
    ) { floating ->
        if (floating) 1f else 0f
    }
    morphProgressSource.state = morphProgressState

    FloatingMascotDock(
        mascot = mascot,
        eyeStateFlow = eyeStateFlow,
        anchorSideRight = anchorSideRight,
        anchorYRatio = anchorYRatio,
        shouldFloat = showFloatingMascot,
        morphProgressState = morphProgressState,
        homeOrbBoundsHolder = homeOrbBoundsHolder,
        animActive = !modalBlocking,
        onTapOrb = onTapOrb,
        onPetOrb = onPetOrb,
        onSaveAnchor = onSaveAnchor
    )
}

/**
 * Active + Outgoing-Only tab stage that keeps zero hidden inactive screens live at alpha=0f
 * while preserving scroll & form state via hoisted [LazyListState]s and [SaveableStateHolder].
 */
@Composable
private fun PersistedTabStage(
    activeTab: AppTab,
    state: IronPulseUiState,
    noAnim: Boolean,
    saveableStateHolder: SaveableStateHolder,
    renderTab: @Composable (AppTab, IronPulseUiState) -> Unit
) {
    val motionSpec = LocalThemeMotionSpec.current
    val tabTransition = updateTransition(
        targetState = activeTab,
        label = "persistedTabStage"
    )
    val transitionRunning = !noAnim && tabTransition.isRunning
    val transitionDir = remember(tabTransition.currentState, tabTransition.targetState) {
        if (tabTransition.targetState.ordinal >= tabTransition.currentState.ordinal) 1 else -1
    }
    val visibleTabs = remember(activeTab, tabTransition.currentState, transitionRunning) {
        if (!transitionRunning || tabTransition.currentState == activeTab) {
            listOf(activeTab)
        } else {
            listOf(tabTransition.currentState, activeTab)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (transitionRunning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0.85f)
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

        visibleTabs.forEach { tab ->
            key(tab) {
                val isCurrent = tab == activeTab
                val tabWeightState = tabTransition.animateFloat(
                    transitionSpec = {
                        if (noAnim) snap() else motionSpec.tabSlideSpring
                    },
                    label = "tabWeight_${tab.name}"
                ) { stateTab ->
                    if (stateTab == tab) 1f else 0f
                }
                var cachedTabState by remember { mutableStateOf(state) }
                if (isCurrent) {
                    cachedTabState = state
                }
                val z = if (isCurrent) 1f else 0.7f
                val mod = Modifier
                    .fillMaxSize()
                    .zIndex(z)
                    .graphicsLayer {
                        if (noAnim || !transitionRunning) {
                            alpha = if (isCurrent) 1f else 0f
                            translationX = 0f
                            scaleX = 1f
                            scaleY = 1f
                            rotationY = 0f
                        } else {
                            val weight = tabWeightState.value
                            val persp = motionSpec.tabPerspectiveDeg
                            if (persp > 0.01f) {
                                cameraDistance = 14f * density
                            }
                            if (isCurrent) {
                                val clampedP = weight.coerceIn(0f, 1f)
                                alpha = clampedP
                                translationX = (1f - weight) * transitionDir * (size.width * motionSpec.tabSlideFraction)
                                val s = lerp(motionSpec.tabEnterScale, 1f, clampedP)
                                scaleX = s
                                scaleY = s
                                rotationY = if (persp > 0.01f) (1f - clampedP) * transitionDir * persp else 0f
                            } else {
                                val p = (1f - weight).coerceIn(0f, 1f)
                                alpha = (1f - p * 1.15f).coerceIn(0f, 1f)
                                translationX = -p * transitionDir * (size.width * (motionSpec.tabSlideFraction * 0.72f))
                                val s = lerp(1f, motionSpec.tabExitScale, p)
                                scaleX = s
                                scaleY = s
                                rotationY = if (persp > 0.01f) -p * transitionDir * (persp * 0.75f) else 0f
                            }
                        }
                    }

                Box(modifier = mod) {
                    CompositionLocalProvider(
                        LocalTabActive provides isCurrent,
                        LocalTabSettled provides (!transitionRunning)
                    ) {
                        saveableStateHolder.SaveableStateProvider(key = tab) {
                            renderTab(tab, cachedTabState)
                        }
                    }
                }
            }
        }
    }
}
