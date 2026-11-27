package com.fathan.ironpulse.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.BuiltinRingtoneSpec
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.CustomRingtone
import com.fathan.ironpulse.model.DayTimeMode
import com.fathan.ironpulse.model.DayTimePhase
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.MascotPersonality
import com.fathan.ironpulse.model.MedalTier
import com.fathan.ironpulse.model.PerformancePreferences
import com.fathan.ironpulse.model.PerformancePresetId
import com.fathan.ironpulse.model.RingtoneStorageMode
import com.fathan.ironpulse.model.ThemeAnimationStyle
import com.fathan.ironpulse.model.TimePhasePose
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.components.AboutContentCards
import com.fathan.ironpulse.ui.components.ConfirmDeleteDialog
import com.fathan.ironpulse.ui.components.IronPulseIcon
import com.fathan.ironpulse.ui.components.MascotOrbFace
import com.fathan.ironpulse.ui.components.MotionStyleCatalog
import com.fathan.ironpulse.ui.components.ProceduralMedalEmblem
import com.fathan.ironpulse.ui.components.TabChoreography
import com.fathan.ironpulse.ui.components.ThemeBrandEmblem
import com.fathan.ironpulse.ui.components.ThemedIcon
import com.fathan.ironpulse.ui.components.TutorialSpotlightTarget
import com.fathan.ironpulse.ui.components.VisualColorPickerDialog
import com.fathan.ironpulse.ui.components.VisualColorPickerMode
import com.fathan.ironpulse.ui.components.VisualPickColorPill
import com.fathan.ironpulse.ui.components.resolvePreviewAccentForTheme
import com.fathan.ironpulse.ui.components.IronPulseMotion
import com.fathan.ironpulse.ui.components.fluidModalEntrance
import com.fathan.ironpulse.ui.components.staggeredCardEntrance
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.components.tutorialSpotlightTarget
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseOutlinedButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine
import com.fathan.ironpulse.ui.theme.ThemeMaterialShaderCache
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.theme.resolveThemeFontPack
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class SettingsCategory(val testTag: String) {
    PROFILE("settings_menu_profile"),
    THEME("settings_menu_theme"),
    CONFIGURATION("settings_menu_configuration"),
    ALARM("settings_menu_alarm"),
    SYSTEM("settings_menu_system"),
    ABOUT("settings_menu_about")
}

@Composable
fun SettingsScreen(
    state: IronPulseUiState,
    viewModel: GymViewModel,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onOpenExactAlarmSettings: () -> Unit,
    onPickCustomRingtone: () -> Unit,
    activeCategory: SettingsCategory? = null,
    onSelectCategory: ((SettingsCategory?) -> Unit)? = null,
    subTabListStates: Map<String, LazyListState> = remember {
        (listOf("HUB") + SettingsCategory.entries.map { it.name }).associateWith { LazyListState() }
    }
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    var localCategory by remember { mutableStateOf(activeCategory) }
    val currentCategory = if (onSelectCategory != null) activeCategory else localCategory
    val selectCategory: (SettingsCategory?) -> Unit = { next ->
        if (onSelectCategory != null) {
            onSelectCategory(next)
        } else {
            localCategory = next
        }
    }

    val tabActive = LocalTabActive.current
    BackHandler(enabled = onSelectCategory == null && tabActive && currentCategory != null) {
        selectCategory(null)
    }

    var confirmSignOut by remember { mutableStateOf(false) }
    var activeColorPickerMode by remember { mutableStateOf<VisualColorPickerMode?>(null) }
    var pendingDeleteTone by remember { mutableStateOf<CustomRingtone?>(null) }

    val fallbackListState = rememberLazyListState()
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val headerHeightDp = with(density) { headerHeightPx.toDp() }

    val itemAnimMod: Modifier = Modifier

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentCategory,
            transitionSpec = {
                if (noAnim) {
                    EnterTransition.None togetherWith ExitTransition.None
                } else if (targetState != null) {
                    (slideInHorizontally(tween(240)) { it / 5 } + fadeIn(tween(200))) togetherWith
                        (slideOutHorizontally(tween(190)) { -it / 7 } + fadeOut(tween(160)))
                } else {
                    (slideInHorizontally(tween(240)) { -it / 5 } + fadeIn(tween(200))) togetherWith
                        (slideOutHorizontally(tween(190)) { it / 7 } + fadeOut(tween(160)))
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            label = "settingsCategoryTransition"
        ) { category ->
            val listState = subTabListStates[category?.name ?: "HUB"] ?: fallbackListState
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = contentPadding.calculateTopPadding() + headerHeightDp + 18.dp,
                    bottom = contentPadding.calculateBottomPadding() + 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("settings_list")
            ) {
                when (category) {
                    null -> {
                        val hubSections = listOf(
                            Triple(
                                "account_appearance",
                                lang.t("AKUN & TAMPILAN", "ACCOUNT & LOOK"),
                                listOf(SettingsCategory.PROFILE, SettingsCategory.THEME)
                            ),
                            Triple(
                                "training_experience",
                                lang.t("LATIHAN & PENGALAMAN", "TRAINING & EXPERIENCE"),
                                listOf(SettingsCategory.CONFIGURATION, SettingsCategory.ALARM)
                            ),
                            Triple(
                                "system_info",
                                lang.t("SISTEM & INFO", "SYSTEM & INFO"),
                                listOf(SettingsCategory.SYSTEM, SettingsCategory.ABOUT)
                            )
                        )
                        var staggerIdx = 0
                        hubSections.forEach { (sectionKey, sectionTitle, cats) ->
                            item(key = "hub_section_$sectionKey", contentType = "settings_hub_section") {
                                SettingsHubSectionHeader(
                                    title = sectionTitle,
                                    modifier = Modifier
                                        .padding(top = if (sectionKey == "account_appearance") 0.dp else 6.dp)
                                        .testTag("settings_section_$sectionKey")
                                )
                            }
                            itemsIndexed(
                                items = cats,
                                key = { _, it -> "settings_hub_${it.name}" },
                                contentType = { _, _ -> "settings_hub_row" }
                            ) { _, cat ->
                                val itemIdx = staggerIdx++
                                SettingsCategoryRow(
                                    category = cat,
                                    state = state,
                                    onClick = { selectCategory(cat) },
                                    modifier = Modifier
                                        .staggeredCardEntrance(itemIdx, active = tabActive, disabledByPerf = noAnim)
                                        .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                                )
                            }
                        }
                    }

                    SettingsCategory.PROFILE -> {
                        item(key = "profile_hero", contentType = "profile_hero") {
                            ProfileHeroCard(
                                state = state,
                                modifier = Modifier
                                    .staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "profile_name", contentType = "profile_name") {
                            ProfileDisplayNameCard(
                                state = state,
                                onSaveName = { viewModel.updateProfile(displayName = it) },
                                modifier = Modifier
                                    .staggeredCardEntrance(1, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "profile_signout", contentType = "profile_signout") {
                            ProfileSignOutCard(
                                onRequestSignOut = { confirmSignOut = true },
                                modifier = Modifier
                                    .staggeredCardEntrance(2, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                    }

                    SettingsCategory.THEME -> {
                        item(key = "themes_grid", contentType = "themes_grid") {
                            ThemesGridCard(
                                selectedTheme = state.appPrefs.uiTheme,
                                onSelectTheme = { viewModel.setUiTheme(it) },
                                onResetTheme = { viewModel.resetThemeCustomization() },
                                modifier = Modifier
                                    .staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "theme_animation_styles", contentType = "theme_animation_styles") {
                            ThemeAnimationStyleCard(
                                selectedStyle = state.appPrefs.themeAnimationStyle,
                                onSelectStyle = { viewModel.setThemeAnimationStyle(it) },
                                animationsEnabled = !state.appPrefs.performance.noAnimations,
                                onToggleAnimations = { enabled ->
                                    viewModel.setPerformance(noAnimations = !enabled)
                                },
                                onPreviewMedal = { tier -> viewModel.previewMedalUnlock(tier) },
                                modifier = Modifier
                                    .staggeredCardEntrance(1, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "accent_tint", contentType = "accent_tint") {
                            AccentAndTintSettingsCard(
                                accentColorHex = state.appPrefs.accentColorHex,
                                materialTintColorHex = state.appPrefs.materialTintColorHex,
                                onSelectAccent = { viewModel.setAccentColor(it) },
                                onOpenAccentPicker = { activeColorPickerMode = VisualColorPickerMode.ACCENT },
                                onOpenTintPicker = { activeColorPickerMode = VisualColorPickerMode.SURFACE_TINT },
                                onSelectMaterialTint = { viewModel.setMaterialTintColor(it) },
                                selectedDayTimeMode = state.appPrefs.todayThemeMode,
                                onSelectDayTimeMode = { viewModel.setTodayThemeMode(it) },
                                modifier = Modifier
                                    .staggeredCardEntrance(2, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "day_phase_mood", contentType = "day_phase_mood") {
                            TodayPhaseMoodCard(
                                selectedMode = state.appPrefs.todayThemeMode,
                                onSelectMode = { viewModel.setTodayThemeMode(it) },
                                modifier = Modifier
                                    .staggeredCardEntrance(3, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                    }

                    SettingsCategory.CONFIGURATION -> {
                        item(key = "mascot_personality_card", contentType = "mascot_personality_card") {
                            MascotPersonalitySettingsCard(
                                selected = state.appPrefs.mascotPersonality,
                                eyeStateFlow = viewModel.mascotEyeFlow,
                                onSelect = { viewModel.setMascotPersonality(it) },
                                modifier = Modifier
                                    .staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "performance_card", contentType = "performance_card") {
                            PerformanceSettingsCard(
                                perf = state.appPrefs.performance,
                                onApplyPreset = { viewModel.applyPerformancePreset(it) },
                                onUpdatePerf = { noAnimations, noAmbientGlow, staticMascot ->
                                    viewModel.setPerformance(
                                        noAnimations = noAnimations,
                                        noAmbientGlow = noAmbientGlow,
                                        staticMascot = staticMascot
                                    )
                                },
                                modifier = Modifier
                                    .staggeredCardEntrance(1, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "medal_animation_preview_card", contentType = "medal_animation_preview_card") {
                            MedalAnimationPreviewCard(
                                perf = state.appPrefs.performance,
                                onToggleAnimations = { enabled ->
                                    viewModel.setPerformance(noAnimations = !enabled)
                                },
                                onPreviewMedal = { tier -> viewModel.previewMedalUnlock(tier) },
                                modifier = Modifier
                                    .staggeredCardEntrance(2, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                    }

                    SettingsCategory.ALARM -> {
                        item(key = "alarm_audio", contentType = "alarm_audio") {
                            AlarmAudioSettingsCard(
                                state = state,
                                viewModel = viewModel,
                                modifier = Modifier
                                    .staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "alarm_feedback", contentType = "alarm_feedback") {
                            AlarmFeedbackSettingsCard(
                                state = state,
                                viewModel = viewModel,
                                modifier = Modifier
                                    .staggeredCardEntrance(1, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "alarm_diagnostics", contentType = "alarm_diagnostics") {
                            AlarmDiagnosticsCard(
                                state = state,
                                viewModel = viewModel,
                                onRequestNotificationPermission = onRequestNotificationPermission,
                                onOpenExactAlarmSettings = onOpenExactAlarmSettings,
                                modifier = Modifier
                                    .staggeredCardEntrance(2, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "ringtone_header", contentType = "ringtone_header") {
                            RingtoneManagerHeaderCard(
                                uploading = state.uploadingRingtone,
                                onPickCustomRingtone = onPickCustomRingtone,
                                modifier = Modifier
                                    .staggeredCardEntrance(3, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        if (state.customRingtones.isNotEmpty()) {
                            item(key = "custom_ringtones_label", contentType = "section_label") {
                                Text(
                                    text = lang.t(
                                        "Nada Kustom Saya (${state.customRingtones.size})",
                                        "My Custom Ringtones (${state.customRingtones.size})"
                                    ),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.palette.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            itemsIndexed(
                                items = state.customRingtones,
                                key = { _, it -> "custom_tone_${it.id}" },
                                contentType = { _, _ -> "ringtone_row" }
                            ) { idx, tone ->
                                val defaultAlarmId = state.profile?.defaultRingtoneId ?: "builtin_athletic_bell"
                                val restRingtoneId = state.profile?.restRingtoneId ?: "builtin_kettlebell_chime"
                                CustomRingtoneItemCard(
                                    tone = tone,
                                    isPreviewing = state.previewingRingtoneId == tone.id,
                                    isAlarmDefault = defaultAlarmId == tone.id,
                                    isRestDefault = restRingtoneId == tone.id,
                                    onPreview = { viewModel.previewRingtone(tone.id) },
                                    onDelete = { pendingDeleteTone = tone },
                                    onAssignAlarm = { viewModel.updateProfile(defaultRingtoneId = tone.id) },
                                    onAssignRest = { viewModel.updateProfile(restRingtoneId = tone.id) },
                                    modifier = Modifier
                                        .staggeredCardEntrance(idx + 4, active = tabActive, disabledByPerf = noAnim)
                                        .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                                )
                            }
                        }
                        item(key = "builtin_ringtones_label", contentType = "section_label") {
                            Text(
                                text = lang.t("Nada Bawaan IronPulse (5 Synth)", "Built-in Synth Ringtones (5)"),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.palette.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        itemsIndexed(
                            items = GymModelHelpers.BUILTIN_RINGTONES,
                            key = { _, it -> "builtin_tone_${it.id}" },
                            contentType = { _, _ -> "ringtone_row" }
                        ) { idx, spec ->
                            val defaultAlarmId = state.profile?.defaultRingtoneId ?: "builtin_athletic_bell"
                            val restRingtoneId = state.profile?.restRingtoneId ?: "builtin_kettlebell_chime"
                            BuiltinRingtoneItemCard(
                                spec = spec,
                                isPreviewing = state.previewingRingtoneId == spec.id,
                                isAlarmDefault = defaultAlarmId == spec.id,
                                isRestDefault = restRingtoneId == spec.id,
                                onPreview = { viewModel.previewRingtone(spec.id) },
                                onAssignAlarm = { viewModel.updateProfile(defaultRingtoneId = spec.id) },
                                onAssignRest = { viewModel.updateProfile(restRingtoneId = spec.id) },
                                modifier = Modifier
                                    .staggeredCardEntrance(idx + 4, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                    }

                    SettingsCategory.SYSTEM -> {
                        item(key = "system_localization_clock", contentType = "system_localization_clock") {
                            SystemLocalizationAndClockCard(
                                selectedLang = state.appPrefs.lang,
                                onSelectLang = { viewModel.setLang(it) },
                                clockFormat = state.appPrefs.clockFormat,
                                clockSecondsFlow = viewModel.clockSecondsFlow,
                                onSelectFormat = { viewModel.setClockFormat(it) },
                                modifier = Modifier
                                    .staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                        item(key = "system_display_guide", contentType = "system_display_guide") {
                            SystemDisplayAndGuideCard(
                                keepScreenOnWorkout = state.appPrefs.keepScreenOnWorkout,
                                onToggleKeepScreenOn = { viewModel.setKeepScreenOnWorkout(it) },
                                onOpenTutorial = { viewModel.openTutorial(startWithLang = false) },
                                modifier = Modifier
                                    .staggeredCardEntrance(1, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) itemAnimMod else Modifier.animateItem())
                            )
                        }
                    }

                    SettingsCategory.ABOUT -> {
                        item(key = "about_full_page", contentType = "about_full_page") {
                            AboutContentCards(
                                wrapHeaderInCard = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .staggeredCardEntrance(0, active = tabActive, disabledByPerf = noAnim)
                                    .then(if (noAnim) Modifier else Modifier.animateItem())
                            )
                        }
                    }
                }
            }
        }

        // Clean Pinned Settings Header (Main List or Focused Sub-Page with Back button)
        SettingsPinnedHeader(
            activeCategory = currentCategory,
            onBack = {
                if (currentCategory != null) {
                    selectCategory(null)
                } else {
                    onBack()
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 640.dp)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = contentPadding.calculateTopPadding() + 12.dp
                )
                .tutorialSpotlightTarget(TutorialSpotlightTarget.SETTINGS_HEADER)
                .onSizeChanged { headerHeightPx = it.height }
        )
    }

    val pickerMode = activeColorPickerMode
    if (pickerMode != null) {
        val isAccent = pickerMode == VisualColorPickerMode.ACCENT
        val startHex = if (isAccent) {
            MaterialYouColorEngine.colorToHex(tokens.accentColor)
        } else {
            state.appPrefs.materialTintColorHex
                ?: MaterialYouColorEngine.defaultTintForTheme(state.appPrefs.uiTheme)
        }
        VisualColorPickerDialog(
            mode = pickerMode,
            initialHex = startHex,
            quickPresets = if (isAccent) {
                GymModelHelpers.ACCENT_COLOR_PRESETS
            } else {
                GymModelHelpers.MATERIAL_TINT_PRESETS
            },
            lang = lang,
            onApplyHex = { chosenHex ->
                if (isAccent) {
                    viewModel.setAccentColor(chosenHex)
                } else {
                    viewModel.setMaterialTintColor(chosenHex)
                }
                activeColorPickerMode = null
            },
            onDismiss = { activeColorPickerMode = null }
        )
    }

    val toDelete = pendingDeleteTone
    if (toDelete != null) {
        ConfirmDeleteDialog(
            title = lang.t("Hapus Nada Dering?", "Delete Ringtone?"),
            message = lang.t(
                "Hapus nada kustom \"${toDelete.name}\"? Hari yang memakainya akan kembali ke nada bawaan.",
                "Remove custom ringtone \"${toDelete.name}\"? Days using it will revert to default."
            ),
            confirmLabel = lang.t("Hapus", "Delete"),
            cancelLabel = lang.t("Batal", "Cancel"),
            onConfirm = {
                viewModel.deleteCustomRingtone(toDelete.id)
                pendingDeleteTone = null
            },
            onDismiss = { pendingDeleteTone = null }
        )
    }

    if (confirmSignOut) {
        ConfirmDeleteDialog(
            title = lang.t("Keluar dari Akun?", "Sign Out?"),
            message = lang.t(
                "Data latihanmu tetap aman tersimpan di Cloud dan akan disinkronkan kembali saat kamu masuk lagi.",
                "Your workout data stays safely backed up in the Cloud and will resync when you sign back in."
            ),
            confirmLabel = lang.t("Keluar", "Sign Out"),
            cancelLabel = lang.t("Batal", "Cancel"),
            onConfirm = {
                confirmSignOut = false
                viewModel.signOut()
            },
            onDismiss = { confirmSignOut = false }
        )
    }
}

private fun settingsCategoryTitle(category: SettingsCategory, lang: AppLanguage): String =
    when (category) {
        SettingsCategory.PROFILE -> lang.t("Profil", "Profile")
        SettingsCategory.THEME -> lang.t("Tema", "Themes")
        SettingsCategory.CONFIGURATION -> lang.t("Konfigurasi", "Configuration")
        SettingsCategory.ALARM -> lang.t("Alarm", "Alarm")
        SettingsCategory.SYSTEM -> lang.t("Sistem", "System")
        SettingsCategory.ABOUT -> lang.t("Tentang", "About")
    }

@Composable
private fun SettingsHubSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(tokens.buttonShape)
                .background(tokens.accentColor)
        )
        Text(
            text = title,
            fontFamily = tokens.fontPack.monoFont,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = tokens.accentTextColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = tokens.subtleBorderColor
        )
    }
}

private fun settingsCategoryIcon(category: SettingsCategory): IronPulseIcon =
    when (category) {
        SettingsCategory.PROFILE -> IronPulseIcon.PERSON
        SettingsCategory.THEME -> IronPulseIcon.PALETTE
        SettingsCategory.CONFIGURATION -> IronPulseIcon.SPEED
        SettingsCategory.ALARM -> IronPulseIcon.ALARM
        SettingsCategory.SYSTEM -> IronPulseIcon.SETTINGS
        SettingsCategory.ABOUT -> IronPulseIcon.INFO
    }

@Composable
private fun SettingsCategoryRow(
    category: SettingsCategory,
    state: IronPulseUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val title = settingsCategoryTitle(category, lang)
    val icon = settingsCategoryIcon(category)
    val summary = remember(category, state, lang) {
        when (category) {
            SettingsCategory.PROFILE -> {
                val name = state.profile?.displayName?.takeIf { it.isNotBlank() }
                    ?: (state.profile?.email ?: state.userEmail)?.substringBefore("@")
                    ?: lang.t("Atlet", "Athlete")
                val syncTag = if (state.isOffline) lang.t("Offline", "Offline") else lang.t("Sinkron Cloud", "Cloud Synced")
                "$name · $syncTag"
            }
            SettingsCategory.THEME -> lang.t(state.appPrefs.uiTheme.titleId, state.appPrefs.uiTheme.titleEn)
            SettingsCategory.CONFIGURATION -> {
                val perf = state.appPrefs.performance
                val perfLabel = when (perf) {
                    PerformancePreferences() -> lang.t("Seimbang", "Balanced")
                    PerformancePreferences(noAnimations = false, noAmbientGlow = true, staticMascot = false) ->
                        lang.t("Hemat Daya", "Battery Saver")
                    PerformancePreferences(noAnimations = true, noAmbientGlow = true, staticMascot = true) ->
                        lang.t("Ultra Ringan", "Ultra Light")
                    else -> lang.t("Kustom", "Custom")
                }
                val personaLabel = state.appPrefs.mascotPersonality.label(lang)
                "$personaLabel · $perfLabel"
            }
            SettingsCategory.ALARM -> {
                val vol = ((state.profile?.volume ?: 0.85) * 100).toInt()
                val snooze = state.profile?.snoozeMinutes ?: 5
                lang.t("Volume $vol% · Tunda $snooze mnt", "Volume $vol% · Snooze ${snooze}m")
            }
            SettingsCategory.SYSTEM -> {
                val clockLabel = if (state.appPrefs.clockFormat == ClockFormat.H24) "24H" else "12H"
                "${state.appPrefs.lang.displayName} · $clockLabel"
            }
            SettingsCategory.ABOUT -> "IronPulse v0.0.1 alpha"
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 68.dp)
            .testTag(category.testTag)
            .tactilePress(pressedScale = 0.985f, disabledByPerf = noAnim)
            .ironGlassSurface(tokens)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .ironTonalControl(
                    tokens = tokens,
                    shape = tokens.buttonShape,
                    baseColor = tokens.accentColor.copy(alpha = if (tokens.isLightTheme) 0.14f else 0.14f),
                    isAccent = false
                )
                .border(1.dp, tokens.accentColor.copy(alpha = 0.28f), tokens.buttonShape),
            contentAlignment = Alignment.Center
        ) {
            ThemedIcon(
                icon = icon,
                contentDescription = null,
                tint = tokens.accentTextColor,
                size = 20.dp
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = summary,
                fontSize = 12.sp,
                color = tokens.mutedTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        ThemedIcon(
            icon = IronPulseIcon.CHEVRON_RIGHT,
            contentDescription = null,
            tint = tokens.mutedTextColor,
            size = 20.dp
        )
    }
}

@Composable
private fun SettingsPinnedHeader(
    activeCategory: SettingsCategory?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val headerTitle = if (activeCategory == null) {
        lang.t("Pengaturan", "Settings")
    } else {
        settingsCategoryTitle(activeCategory, lang)
    }
    val backButtonTag = if (activeCategory == null) "settings_hub_back_button" else "settings_subpage_back_button"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens = tokens)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IronPulseTonalSurface(
            onClick = onBack,
            shape = tokens.buttonShape,
            baseColor = tokens.controlBg,
            border = BorderStroke(1.dp, tokens.surfaceBorderColor),
            modifier = Modifier.testTag(backButtonTag),
            visualModifier = Modifier.defaultMinSize(minHeight = 40.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.BACK,
                    contentDescription = lang.t("Kembali", "Back"),
                    tint = tokens.palette.onSurface,
                    size = 16.dp
                )
                Text(
                    text = lang.t("Kembali", "Back"),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
            }
        }

        Text(
            text = headerTitle,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            color = tokens.palette.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IronPulseTonalSurface(
            shape = tokens.buttonShape,
            baseColor = tokens.accentColor.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.28f))
        ) {
            Text(
                text = if (activeCategory == null) tokens.fontPack.osBadgeLabel else lang.t("Pengaturan", "Settings"),
                fontFamily = if (activeCategory == null) tokens.fontPack.monoFont else tokens.fontPack.bodyFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = tokens.accentTextColor,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ProfileHeroCard(
    state: IronPulseUiState,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val profile = state.profile
    val rawName = profile?.displayName?.takeIf { it.isNotBlank() }
        ?: (profile?.email ?: state.userEmail)?.substringBefore("@")
        ?: lang.t("Atlet", "Athlete")
    val avatarInitial = rawName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "A"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(tokens.accentColor, tokens.palette.tertiary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarInitial,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.onAccentColor
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rawName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = profile?.email ?: state.userEmail ?: "-",
                    fontSize = 12.sp,
                    color = tokens.mutedTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val syncBadgeShape = tokens.buttonShape
            IronPulseTonalSurface(
                shape = syncBadgeShape,
                baseColor = if (state.isOffline) Color(0x26FBBF24) else Color(0x1E34D399),
                border = BorderStroke(
                    1.dp,
                    if (state.isOffline) Color(0x55FBBF24) else Color(0x4D34D399)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ThemedIcon(
                        icon = if (state.isOffline) IronPulseIcon.CLOUD_OFF else IronPulseIcon.CLOUD_DONE,
                        contentDescription = null,
                        tint = if (state.isOffline) tokens.warningColor else tokens.successColor,
                        size = 13.dp
                    )
                    Text(
                        text = if (state.isOffline) lang.t("Offline", "Offline") else lang.t("Sinkron", "Synced"),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isOffline) tokens.warningColor else tokens.successColor
                    )
                }
            }
        }

        HorizontalDivider(color = tokens.dividerColor)

        Text(
            text = if (state.isOffline) {
                lang.t("Mode Offline — perubahan otomatis disinkronkan saat koneksi kembali.", "Offline Mode — changes auto-sync once back online.")
            } else {
                lang.t("Sinkronisasi Cloud Firestore Real-time aktif untuk seluruh perangkatmu.", "Real-time Cloud Firestore sync active across your devices.")
            },
            fontSize = 11.5.sp,
            color = tokens.mutedTextColor
        )
    }
}

@Composable
private fun ProfileDisplayNameCard(
    state: IronPulseUiState,
    onSaveName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val profile = state.profile
    var nameDraft by remember(profile?.displayName) { mutableStateOf(profile?.displayName ?: "") }
    var savedFeedback by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = lang.t("Nama Panggilan Atlet", "Athlete Display Name"),
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
            Text(
                text = lang.t(
                    "Maskot Pulse menyapamu dengan nama ini di halaman Hari Ini.",
                    "The Pulse mascot greets you by this name on the Today screen."
                ),
                fontSize = 12.sp,
                color = tokens.mutedTextColor
            )
        }
        OutlinedTextField(
            value = nameDraft,
            onValueChange = {
                nameDraft = it.take(100)
                savedFeedback = false
            },
            label = { Text(lang.t("Nama Panggilan", "Display Name")) },
            singleLine = true,
            shape = tokens.inputShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = tokens.accentColor,
                unfocusedBorderColor = tokens.surfaceBorderColor,
                focusedTextColor = tokens.palette.onSurface,
                unfocusedTextColor = tokens.palette.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        IronPulseButton(
            onClick = {
                val cleaned = nameDraft.trim()
                if (cleaned.isNotEmpty()) {
                    onSaveName(cleaned)
                    savedFeedback = true
                }
            },
            enabled = nameDraft.trim().isNotEmpty(),
            shape = tokens.buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.accentColor,
                contentColor = tokens.onAccentColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            ThemedIcon(IronPulseIcon.CHECK, contentDescription = null, size = 18.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (savedFeedback) lang.t("Nama Tersimpan", "Name Saved") else lang.t("Simpan Nama", "Save Name"),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProfileSignOutCard(
    onRequestSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens, subtle = true)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        IronPulseOutlinedButton(
            onClick = onRequestSignOut,
            shape = tokens.buttonShape,
            border = BorderStroke(1.dp, tokens.dangerColor.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            ThemedIcon(IronPulseIcon.LOGOUT, contentDescription = null, tint = tokens.dangerColor, size = 18.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lang.t("Keluar Akun", "Sign Out"),
                fontWeight = FontWeight.Bold,
                color = tokens.dangerColor
            )
        }
    }
}

@Composable
private fun ThemesGridCard(
    selectedTheme: UiTheme,
    onSelectTheme: (UiTheme) -> Unit,
    onResetTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang

    val standardThemes = remember { UiTheme.entries.filter { !it.isExclusive } }
    val exclusiveThemes = remember { UiTheme.entries.filter { it.isExclusive } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lang.t(
                        "Gaya Antarmuka (${UiTheme.entries.size} Tema)",
                        "Interface Style (${UiTheme.entries.size} Themes)"
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = lang.t(
                        "Pilih gaya visual antarmuka yang paling cocok untukmu.",
                        "Choose the visual style that fits your training space."
                    ),
                    fontSize = 11.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
            TextButton(onClick = onResetTheme) {
                ThemedIcon(IronPulseIcon.REFRESH, contentDescription = null, tint = tokens.accentTextColor, size = 15.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(lang.t("Reset", "Reset"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tokens.accentTextColor)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("themes_group_standard"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = lang.t("Tema Standar", "Standard Themes"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp,
                    color = tokens.mutedTextColor
                )
                Text(
                    text = "${standardThemes.size}",
                    fontFamily = tokens.fontPack.monoFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.mutedTextColor
                )
            }
            standardThemes.forEach { theme ->
                ThemePreviewCard(
                    theme = theme,
                    selected = selectedTheme == theme,
                    lang = lang,
                    accentColor = tokens.accentColor,
                    onSelect = { onSelectTheme(theme) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider(color = tokens.subtleBorderColor)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("themes_group_exclusive"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = lang.t("Tema Eksklusif", "Exclusive Themes"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp,
                    color = tokens.accentTextColor
                )
                Text(
                    text = "${exclusiveThemes.size}",
                    fontFamily = tokens.fontPack.monoFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.accentTextColor
                )
            }
            exclusiveThemes.forEach { theme ->
                ThemePreviewCard(
                    theme = theme,
                    selected = selectedTheme == theme,
                    lang = lang,
                    accentColor = tokens.accentColor,
                    onSelect = { onSelectTheme(theme) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ThemeAnimationStyleCard(
    selectedStyle: ThemeAnimationStyle,
    onSelectStyle: (ThemeAnimationStyle) -> Unit,
    animationsEnabled: Boolean,
    onToggleAnimations: (Boolean) -> Unit,
    onPreviewMedal: (MedalTier) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val previewSpec = remember(selectedStyle) { MotionStyleCatalog.resolve(selectedStyle) }
    var replayTick by remember { mutableIntStateOf(0) }
    val demoProgress = remember { Animatable(1f) }
    val navStepIndex = remember(replayTick, selectedStyle) {
        (selectedStyle.ordinal + replayTick) % 3
    }
    val navDemoProgress = remember { Animatable(navStepIndex.toFloat()) }

    LaunchedEffect(selectedStyle, replayTick, noAnim) {
        if (noAnim) {
            demoProgress.snapTo(1f)
            navDemoProgress.snapTo(navStepIndex.toFloat())
        } else {
            demoProgress.snapTo(0f)
            demoProgress.animateTo(1f, previewSpec.tabSlideSpring)
        }
    }
    LaunchedEffect(navStepIndex, noAnim) {
        if (noAnim) {
            navDemoProgress.snapTo(navStepIndex.toFloat())
        } else {
            navDemoProgress.animateTo(navStepIndex.toFloat(), previewSpec.navIndicatorSpring)
        }
    }

    val stylePairs = remember { ThemeAnimationStyle.entries.chunked(2) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp)
            .testTag("theme_animation_style_card"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lang.t(
                        "Gaya Animasi Tema (5 Pilihan)",
                        "Theme Animation Style (5 Presets)"
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = lang.t(
                        "Pilih 1 dari 5 karakter gerak berbeda total untuk semua tema.",
                        "Pick 1 of 5 totally distinct motion choreographies across all themes."
                    ),
                    fontSize = 11.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
            if (selectedStyle != ThemeAnimationStyle.SILK_GLIDE) {
                TextButton(
                    onClick = {
                        onSelectStyle(ThemeAnimationStyle.SILK_GLIDE)
                        replayTick += 1
                    }
                ) {
                    ThemedIcon(IronPulseIcon.REFRESH, contentDescription = null, tint = tokens.accentTextColor, size = 15.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(lang.t("Default", "Default"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = tokens.accentTextColor)
                }
            }
        }

        // Quick UI Animation Switch right inside Theme -> Animation Style
        IronPulseTonalSurface(
            shape = tokens.subtleCardShape,
            baseColor = tokens.wellBg,
            border = BorderStroke(
                1.dp,
                if (animationsEnabled) tokens.accentColor.copy(alpha = 0.34f) else tokens.warningColor.copy(alpha = 0.42f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                PerformanceSwitchRow(
                    title = lang.t("Animasi UI Aktif", "UI Animations Enabled"),
                    subtitle = lang.t(
                        "Nyalakan atau jedakan gerak transisi, maskot & medali secara langsung.",
                        "Toggle screen transitions, mascot motion & medal celebrations live."
                    ),
                    checked = animationsEnabled,
                    onCheckedChange = onToggleAnimations,
                    switchModifier = Modifier.testTag("settings_theme_anim_quick_toggle")
                )
            }
        }

        if (noAnim) {
            IronPulseTonalSurface(
                shape = tokens.subtleCardShape,
                baseColor = tokens.warningColor.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, tokens.warningColor.copy(alpha = 0.42f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("anim_style_paused_notice")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemedIcon(IronPulseIcon.INFO, contentDescription = null, tint = tokens.warningColor, size = 16.dp)
                    Text(
                        text = lang.t(
                            "Mode Tanpa Animasi aktif — nyalakan sakelar Animasi UI Aktif di atas untuk melihat gaya gerak penuh.",
                            "Disable Animations is active — flip UI Animations Enabled above to preview full motion."
                        ),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = tokens.palette.onSurface
                    )
                }
            }
        }

        // Interactive GPU Live Motion Sandbox (distinct choreography preview, no moving sheen)
        IronPulseTonalSurface(
            onClick = { replayTick += 1 },
            shape = tokens.subtleCardShape,
            baseColor = tokens.wellBg,
            border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.32f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("anim_style_live_preview")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(tokens.accentColor)
                        )
                        Text(
                            text = selectedStyle.title(lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.palette.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IronPulseTonalSurface(
                        shape = tokens.buttonShape,
                        baseColor = tokens.accentColor.copy(alpha = 0.16f),
                        border = BorderStroke(0.85.dp, tokens.accentColor.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = lang.t("Ketuk Uji Gerak", "Tap to Replay"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.accentTextColor,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }

                // Animated demo row: mini page choreography card + gliding indicator rail
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(tokens.subtleCardShape)
                            .graphicsLayer {
                                val p = demoProgress.value
                                val inv = 1f - p
                                when (previewSpec.tabChoreography) {
                                    TabChoreography.HORIZONTAL_GLIDE -> {
                                        translationX = inv * 28.dp.toPx()
                                        translationY = 0f
                                        val s = 0.96f + 0.04f * p.coerceIn(0f, 1.04f)
                                        scaleX = s
                                        scaleY = s
                                        rotationY = 0f
                                    }
                                    TabChoreography.DEPTH_ZOOM -> {
                                        translationX = inv * 12.dp.toPx()
                                        translationY = 0f
                                        val s = 0.78f + 0.22f * p.coerceIn(0f, 1.05f)
                                        scaleX = s
                                        scaleY = s
                                        rotationY = 0f
                                    }
                                    TabChoreography.PERSPECTIVE_FLIP_3D -> {
                                        translationX = inv * 18.dp.toPx()
                                        translationY = 0f
                                        val s = 0.90f + 0.10f * p.coerceIn(0f, 1.04f)
                                        scaleX = s
                                        scaleY = s
                                        cameraDistance = 10f * density
                                        rotationY = inv * 32f
                                    }
                                    TabChoreography.VERTICAL_ELASTIC -> {
                                        translationX = 0f
                                        translationY = inv * 24.dp.toPx()
                                        scaleX = 0.92f + 0.08f * p.coerceIn(0f, 1.12f)
                                        scaleY = 1.06f - 0.06f * p.coerceIn(0f, 1.12f)
                                        rotationY = 0f
                                    }
                                    TabChoreography.INSTANT_SNAP -> {
                                        translationX = inv * 20.dp.toPx()
                                        translationY = 0f
                                        scaleX = 1f
                                        scaleY = 1f
                                        rotationY = 0f
                                    }
                                }
                            }
                            .background(tokens.controlBg)
                            .border(1.dp, tokens.surfaceBorderColor, tokens.subtleCardShape)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(tokens.buttonShape)
                                    .background(tokens.accentColor.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(tokens.accentColor)
                                )
                            }
                            Column {
                                Text(
                                    text = selectedStyle.tag(lang),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.palette.onSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = selectedStyle.subtitle(lang),
                                    fontSize = 9.5.sp,
                                    color = tokens.mutedTextColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Mini 3-slot gliding nav indicator preview
                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(tokens.controlBg)
                            .border(0.9.dp, tokens.subtleBorderColor, RoundedCornerShape(999.dp))
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    val slotPx = (84.dp - 6.dp).toPx() / 3f
                                    translationX = navDemoProgress.value * slotPx
                                }
                                .width(26.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(tokens.accentColor)
                        )
                    }
                }
            }
        }

        // 5 Selectable Animation Style Cards (2 + 2 + 1 full-width)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("anim_styles_grid"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stylePairs.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { style ->
                        val isSelected = style == selectedStyle
                        val cardColor = if (isSelected) {
                            lerp(tokens.controlBg, tokens.accentColor, if (tokens.isLightTheme) 0.16f else 0.14f)
                        } else {
                            tokens.controlBg
                        }
                        val cardBorder = if (isSelected) tokens.accentColor else tokens.subtleBorderColor
                        IronPulseTonalSurface(
                            onClick = {
                                onSelectStyle(style)
                                replayTick += 1
                            },
                            shape = tokens.subtleCardShape,
                            baseColor = cardColor,
                            pressedScale = 0.96f,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, cardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("anim_style_card_${style.wireValue}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 11.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = style.tag(lang),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) tokens.accentTextColor else tokens.mutedTextColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) tokens.accentColor else tokens.wellBg
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) tokens.accentColor else tokens.subtleBorderColor,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            ThemedIcon(
                                                icon = IronPulseIcon.CHECK,
                                                contentDescription = null,
                                                tint = tokens.onAccentColor,
                                                size = 10.dp
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = style.title(lang),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.palette.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = style.subtitle(lang),
                                    fontSize = 10.5.sp,
                                    lineHeight = 14.sp,
                                    color = tokens.mutedTextColor,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        IronPulseOutlinedButton(
            onClick = { onPreviewMedal(MedalTier.GOLD) },
            shape = tokens.buttonShape,
            border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .testTag("settings_theme_medal_preview_shortcut")
        ) {
            ThemedIcon(IronPulseIcon.SPARKLE, contentDescription = null, tint = tokens.accentTextColor, size = 16.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lang.t("Lihat Animasi Dapat Medali", "Preview Medal Unlock Animation"),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.accentTextColor
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccentAndTintSettingsCard(
    accentColorHex: String,
    materialTintColorHex: String?,
    onSelectAccent: (String) -> Unit,
    onOpenAccentPicker: () -> Unit,
    onOpenTintPicker: () -> Unit,
    onSelectMaterialTint: (String?) -> Unit,
    selectedDayTimeMode: DayTimeMode = DayTimeMode.AUTO,
    onSelectDayTimeMode: ((DayTimeMode) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val activeAccentHex = MaterialYouColorEngine.colorToHex(tokens.accentColor)
    val activeAccentName = remember(activeAccentHex, lang) {
        MaterialYouColorEngine.friendlyColorName(
            hex = activeAccentHex,
            presets = GymModelHelpers.ACCENT_COLOR_PRESETS,
            lang = lang
        )
    }
    val activeTintName = remember(materialTintColorHex, lang) {
        materialTintColorHex?.let {
            MaterialYouColorEngine.friendlyColorName(
                hex = it,
                presets = GymModelHelpers.MATERIAL_TINT_PRESETS,
                lang = lang
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = lang.t("Warna Aksen Utama", "Primary Accent Color"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
            IronPulseTonalSurface(
                shape = tokens.buttonShape,
                baseColor = tokens.accentColor.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.35f)),
                modifier = Modifier.testTag("accent_active_visual_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(tokens.accentColor)
                    )
                    Text(
                        text = activeAccentName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface
                    )
                }
            }
        }

        if (tokens.accentLocked) {
            val isXMaterialLocked = tokens.uiTheme == UiTheme.XMATERIAL_YOU
            val isYaruLocked = tokens.uiTheme == UiTheme.UBUNTU_YARU
            val isCliLocked = tokens.uiTheme == UiTheme.TERMINAL_CLI
            val lockBadgeShape = when {
                isXMaterialLocked -> CircleShape
                isYaruLocked -> RoundedCornerShape(10.dp)
                isCliLocked -> RoundedCornerShape(4.dp)
                else -> CircleShape
            }
            val lockBadgeBrush = when {
                isXMaterialLocked -> when (tokens.timePhase) {
                    DayTimePhase.MORNING -> Brush.linearGradient(
                        listOf(Color(0xFFDFBE84), Color(0xFFB39055))
                    )
                    DayTimePhase.AFTERNOON -> Brush.linearGradient(
                        listOf(Color(0xFF8CC5C2), Color(0xFF5D9693))
                    )
                    DayTimePhase.NIGHT -> Brush.linearGradient(
                        listOf(Color(0xFFB0A4E3), Color(0xFF7B6FB3))
                    )
                }
                isYaruLocked -> Brush.linearGradient(
                    listOf(Color(0xFFE95420), Color(0xFF77216F))
                )
                isCliLocked -> Brush.linearGradient(
                    listOf(Color(0xFF00FF66), Color(0xFF0B4F28))
                )
                else -> Brush.verticalGradient(
                    0.0f to Color(0xFF66DAFF),
                    0.5f to Color(0xFF0099E5),
                    1.0f to Color(0xFF006FB8)
                )
            }
            val lockedTitle = when {
                isXMaterialLocked -> lang.t("Palet Obsidian XMaterial You Terkunci", "XMaterial You Obsidian Palette Locked")
                isYaruLocked -> lang.t("Jingga Yaru & Aubergine Terkunci", "Yaru Orange & Aubergine Locked")
                isCliLocked -> lang.t("Palet Phosphor Terminal CLI Terkunci", "Terminal CLI Phosphor Palette Locked")
                else -> lang.t("Biru Aero Terkunci", "Aero Sky Blue Locked")
            }
            val lockedDesc = when {
                isXMaterialLocked -> lang.t(
                    "XMaterial You memadukan obsidian gelap yang tenang dengan aksen logam fase waktu (Fajar Emas, Siang Platinum, Malam Moonstone). Warna kustommu tetap aman untuk tema lain.",
                    "XMaterial You pairs calm dark obsidian surfaces with refined phase-metal accents (Obsidian Dawn, Obsidian Day, Obsidian Night). Your custom color stays saved for other themes."
                )
                isYaruLocked -> lang.t(
                    "Ubuntu Yaru mengunci aksen jingga Yaru dan permukaan aubergine matte agar karakter visualnya tetap utuh. Warna kustommu tetap aman untuk tema standar.",
                    "Ubuntu Yaru locks Yaru orange and warm aubergine matte surfaces to preserve its signature visual character. Your custom color stays saved for standard themes."
                )
                isCliLocked -> lang.t(
                    "Terminal CLI mengunci aksen hijau fosfor CRT (#00FF66) dan latar konsol gelap (#050806) agar nuansa terminal tetap autentik. Warna kustommu tetap aman untuk tema standar.",
                    "Terminal CLI locks classic CRT phosphor green (#00FF66) and dark console obsidian (#050806) to preserve its authentic terminal look. Your custom color stays saved for standard themes."
                )
                else -> lang.t(
                    "Tema Frutiger Aero mengunci warna aksen biru langit khas Y2K agar nuansa kaca glossy tetap serasi. Pilihan warna kustommu tetap aman untuk tema lainnya.",
                    "Frutiger Aero locks its signature Y2K sky-blue accent to keep the glossy glass look cohesive. Your custom color stays saved for other themes."
                )
            }

            val lockedBannerShape = when {
                isXMaterialLocked -> tokens.subtleCardShape
                isYaruLocked -> RoundedCornerShape(12.dp)
                isCliLocked -> RoundedCornerShape(4.dp)
                else -> RoundedCornerShape(14.dp)
            }
            val lockedBannerBg = when {
                isXMaterialLocked -> Color(0xFF101218)
                isYaruLocked -> Color(0xFF2B2029)
                isCliLocked -> Color(0xFF09100C)
                else -> tokens.accentColor.copy(alpha = 0.14f)
            }
            IronPulseTonalSurface(
                shape = lockedBannerShape,
                baseColor = lockedBannerBg,
                border = BorderStroke(
                    if (isXMaterialLocked) 0.9.dp else 1.dp,
                    tokens.accentColor.copy(alpha = 0.42f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("accent_locked_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(lockBadgeShape)
                            .background(lockBadgeBrush)
                            .border(
                                1.dp,
                                Color.White.copy(alpha = if (isXMaterialLocked || isYaruLocked || isCliLocked) 0.35f else 0.80f),
                                lockBadgeShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ThemedIcon(
                            icon = IronPulseIcon.LOCK,
                            contentDescription = lang.t("Warna aksen terkunci", "Accent color locked"),
                            tint = if (isXMaterialLocked || isCliLocked) tokens.onAccentColor else Color.White,
                            size = 18.dp
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = lockedTitle,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.palette.onSurface
                        )
                        Text(
                            text = lockedDesc,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp,
                            color = tokens.palette.onSurfaceVariant
                        )
                    }
                }
            }

            if (tokens.uiTheme == UiTheme.FRUTIGER_AERO) {
                val stripShape = RoundedCornerShape(14.dp)
                IronPulseTonalSurface(
                    shape = stripShape,
                    baseColor = tokens.wellBg,
                    border = BorderStroke(1.dp, tokens.subtleBorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("aero_phase_preview_strip")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.t("Fase Otomatis Frutiger Aero", "Frutiger Aero Auto Phase"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.palette.onSurface
                                )
                                Text(
                                    text = lang.t(
                                        "Otomatis beralih ke Aero Senja Lembut pukul 18:00–04:59.",
                                        "Automatically switches to Soft Twilight Aero from 18:00–04:59."
                                    ),
                                    fontSize = 11.sp,
                                    color = tokens.palette.onSurfaceVariant
                                )
                            }
                            IronPulseTonalSurface(
                                shape = RoundedCornerShape(999.dp),
                                baseColor = if (tokens.isAeroNight) Color(0x3338BDF8) else tokens.accentColor.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, tokens.accentTextColor.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = if (tokens.isAeroNight) {
                                        lang.t("Senja Malam", "Twilight Night")
                                    } else {
                                        lang.t("Langit Cerah", "Bright Sky")
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.accentTextColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val quickModes = listOf(
                                Triple(DayTimeMode.AUTO, lang.t("Auto", "Auto"), "aero_phase_btn_auto"),
                                Triple(DayTimeMode.AFTERNOON, lang.t("Siang", "Day"), "aero_phase_btn_day"),
                                Triple(DayTimeMode.NIGHT, lang.t("Malam", "Night"), "aero_phase_btn_night")
                            )
                            quickModes.forEach { (mode, label, tag) ->
                                val isActive = when (mode) {
                                    DayTimeMode.AUTO -> selectedDayTimeMode == DayTimeMode.AUTO
                                    DayTimeMode.AFTERNOON -> selectedDayTimeMode == DayTimeMode.AFTERNOON || selectedDayTimeMode == DayTimeMode.MORNING
                                    DayTimeMode.NIGHT -> selectedDayTimeMode == DayTimeMode.NIGHT
                                    else -> false
                                }
                                val pillShape = RoundedCornerShape(999.dp)
                                IronPulseTonalSurface(
                                    onClick = { onSelectDayTimeMode?.invoke(mode) },
                                    shape = pillShape,
                                    baseColor = if (isActive) tokens.accentColor else tokens.controlBg,
                                    isAccent = isActive,
                                    pressedScale = 0.95f,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isActive) {
                                            if (tokens.isAeroNight) Color(0xCCBAE6FD) else Color.White.copy(alpha = 0.85f)
                                        } else {
                                            tokens.surfaceBorderColor
                                        }
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(tag),
                                    visualModifier = Modifier.height(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) Color.White else tokens.palette.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (isXMaterialLocked) {
                IronPulseTonalSurface(
                    shape = tokens.subtleCardShape,
                    baseColor = tokens.wellBg,
                    border = BorderStroke(0.95.dp, tokens.accentColor.copy(alpha = 0.36f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("xmaterial_phase_preview_strip")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.t("Aura Fase Obsidian XMaterial You", "XMaterial You Obsidian Phase"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.palette.onSurface
                                )
                                Text(
                                    text = lang.t(
                                        "Obsidian gelap tenang dengan Fajar Emas, Siang Platinum & Malam Moonstone (18:00).",
                                        "Calm dark obsidian with Champagne Gold dawn, Platinum Jade day & Moonstone night (18:00)."
                                    ),
                                    fontSize = 11.sp,
                                    color = tokens.palette.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IronPulseTonalSurface(
                                shape = RoundedCornerShape(999.dp),
                                baseColor = lerp(tokens.wellBg, tokens.accentColor, 0.16f),
                                border = BorderStroke(0.9.dp, tokens.accentTextColor.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = when (tokens.timePhase) {
                                        DayTimePhase.MORNING -> lang.t("Fajar Emas", "Obsidian Dawn")
                                        DayTimePhase.AFTERNOON -> lang.t("Siang Platinum", "Obsidian Day")
                                        DayTimePhase.NIGHT -> lang.t("Malam Moonstone", "Obsidian Night")
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = tokens.accentTextColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val quickModes = listOf(
                                Triple(DayTimeMode.AUTO, lang.t("Auto", "Auto"), "xmaterial_phase_btn_auto"),
                                Triple(DayTimeMode.MORNING, lang.t("Pagi", "Dawn"), "xmaterial_phase_btn_morning"),
                                Triple(DayTimeMode.AFTERNOON, lang.t("Siang", "Day"), "xmaterial_phase_btn_afternoon"),
                                Triple(DayTimeMode.NIGHT, lang.t("Malam", "Night"), "xmaterial_phase_btn_night")
                            )
                            quickModes.forEach { (mode, label, tag) ->
                                val isActive = selectedDayTimeMode == mode
                                IronPulseTonalSurface(
                                    onClick = { onSelectDayTimeMode?.invoke(mode) },
                                    shape = tokens.buttonShape,
                                    baseColor = if (isActive) tokens.accentColor else tokens.controlBg,
                                    isAccent = isActive,
                                    pressedScale = 0.95f,
                                    border = BorderStroke(
                                        if (isActive) 1.dp else 0.9.dp,
                                        if (isActive) tokens.accentColor else tokens.surfaceBorderColor
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(tag),
                                    visualModifier = Modifier.height(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = label,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) tokens.onAccentColor else tokens.palette.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GymModelHelpers.ACCENT_COLOR_PRESETS.forEach { opt ->
                    val color = MaterialYouColorEngine.parseColor(opt.hex)
                    val active = activeAccentHex.equals(opt.hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .tactilePress(pressedScale = 0.90f, disabledByPerf = noAnim)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (active) 3.dp else 1.dp,
                                color = if (active) Color.White else Color.White.copy(alpha = 0.25f),
                                shape = CircleShape
                            )
                            .clickable { onSelectAccent(opt.hex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (active) {
                            ThemedIcon(
                                icon = IronPulseIcon.CHECK,
                                contentDescription = lang.t(opt.labelId, opt.labelEn),
                                tint = Color(0xFF0A0B0E),
                                size = 18.dp
                            )
                        }
                    }
                }

                VisualPickColorPill(
                    label = lang.t("Pilih Warna", "Pick Color"),
                    onClick = onOpenAccentPicker,
                    modifier = Modifier.testTag("accent_pick_color_button")
                )
            }

            HorizontalDivider(color = tokens.subtleBorderColor)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lang.t("Nuansa Permukaan (Material Tint)", "Material Surface Tint"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface
                    )
                    if (activeTintName != null) {
                        Text(
                            text = activeTintName,
                            fontSize = 11.sp,
                            color = tokens.palette.onSurfaceVariant
                        )
                    }
                }
                TextButton(onClick = { onSelectMaterialTint(null) }) {
                    Text(
                        text = lang.t("Bawaan Tema", "Theme Default"),
                        fontSize = 11.5.sp,
                        color = if (materialTintColorHex == null) tokens.accentColor else tokens.palette.onSurfaceVariant
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GymModelHelpers.MATERIAL_TINT_PRESETS.forEach { opt ->
                    val color = MaterialYouColorEngine.parseColor(opt.hex)
                    val active = materialTintColorHex.equals(opt.hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .tactilePress(pressedScale = 0.90f, disabledByPerf = noAnim)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color)
                            .border(
                                width = if (active) 2.5.dp else 1.dp,
                                color = if (active) tokens.accentColor else Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectMaterialTint(opt.hex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (active) {
                            ThemedIcon(
                                icon = IronPulseIcon.CHECK,
                                contentDescription = lang.t(opt.labelId, opt.labelEn),
                                tint = Color.White,
                                size = 16.dp
                            )
                        }
                    }
                }

                VisualPickColorPill(
                    label = lang.t("Pilih Warna", "Pick Color"),
                    onClick = onOpenTintPicker,
                    modifier = Modifier.testTag("tint_pick_color_button")
                )
            }
        }

        HorizontalDivider(color = tokens.subtleBorderColor)

        Text(
            text = if (tokens.uiTheme == UiTheme.MATERIAL_YOU) {
                lang.t("Palet Tonal Material You Aktif", "Live Material You Tonal Palette")
            } else {
                lang.t("Palet Tonal Tema Aktif", "Live Theme Tonal Palette")
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = tokens.palette.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Primary" to tokens.palette.primary,
                "PriCont" to tokens.palette.primaryContainer,
                "Second" to tokens.palette.secondary,
                "Tertiary" to tokens.palette.tertiary,
                "SurfHi" to tokens.palette.surfaceContainerHigh,
                "Outline" to tokens.palette.outlineVariant
            ).forEach { (label, swatchColor) ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .clip(tokens.buttonShape)
                            .background(swatchColor)
                            .border(1.dp, tokens.surfaceBorderColor, tokens.buttonShape)
                    )
                    Text(
                        text = label,
                        fontSize = 9.5.sp,
                        color = tokens.palette.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayPhaseMoodCard(
    selectedMode: DayTimeMode,
    onSelectMode: (DayTimeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = lang.t("Suasana Waktu Hari Ini", "Today Time Phase Mood"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = tokens.palette.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val nightDot = if (tokens.isLightTheme) Color(0xFF4338CA) else Color(0xFFA5B4FC)
            listOf(
                Triple(DayTimeMode.AUTO, lang.t("Auto", "Auto"), tokens.successColor),
                Triple(DayTimeMode.MORNING, lang.t("Pagi", "Morning"), tokens.warningColor),
                Triple(DayTimeMode.AFTERNOON, lang.t("Siang", "Day"), tokens.infoColor),
                Triple(DayTimeMode.NIGHT, lang.t("Malam", "Night"), nightDot)
            ).forEach { (mode, label, dot) ->
                val active = selectedMode == mode
                val moodShape = tokens.subtleCardShape
                val moodBg = if (active) {
                    lerp(tokens.controlBg, dot, if (tokens.isLightTheme) 0.16f else 0.18f)
                } else {
                    tokens.palette.surfaceContainerLow
                }
                IronPulseTonalSurface(
                    onClick = { onSelectMode(mode) },
                    shape = moodShape,
                    baseColor = moodBg,
                    isAccent = false,
                    pressedScale = 0.95f,
                    border = BorderStroke(1.dp, if (active) dot else tokens.surfaceBorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(dot)
                        )
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) dot else tokens.palette.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MascotPersonalitySettingsCard(
    selected: MascotPersonality,
    eyeStateFlow: StateFlow<MascotEyeState>?,
    onSelect: (MascotPersonality) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    var switchBurstTick by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mascot_personality_card")
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemedIcon(IronPulseIcon.SPARKLE, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
                Text(
                    text = lang.t("Kepribadian Maskot Pulse", "Pulse Mascot Personality"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
            }
            Text(
                text = lang.t(
                    "Pilih karakter suara, ekspresi mata, dan gaya dialog pendamping latihanmu.",
                    "Choose the voice, expressions, and dialogue style of your workout companion."
                ),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = tokens.mutedTextColor
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MascotPersonality.entries.forEach { personality ->
                val isSelected = selected == personality
                val previewPose = if (personality == MascotPersonality.COACH) {
                    TimePhasePose.SALUTE
                } else {
                    TimePhasePose.PERK
                }
                val previewMascot = remember(personality, previewPose) {
                    MascotUiState(
                        personality = personality,
                        activeTimePose = previewPose,
                        urgentMood = null
                    )
                }
                val cardBg by animateColorAsState(
                    targetValue = if (isSelected) {
                        lerp(tokens.controlBg, tokens.accentColor, if (tokens.isLightTheme) 0.14f else 0.16f)
                    } else {
                        tokens.palette.surfaceContainerLow
                    },
                    animationSpec = tween(if (noAnim) 0 else 220),
                    label = "personaCardBg"
                )
                val cardBorderColor by animateColorAsState(
                    targetValue = if (isSelected) tokens.accentColor else tokens.surfaceBorderColor,
                    animationSpec = tween(if (noAnim) 0 else 220),
                    label = "personaCardBorder"
                )

                IronPulseTonalSurface(
                    onClick = {
                        if (selected != personality) {
                            switchBurstTick += 1
                        }
                        onSelect(personality)
                    },
                    shape = tokens.cardShape,
                    baseColor = cardBg,
                    isAccent = false,
                    pressedScale = 0.96f,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = cardBorderColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("personality_card_${personality.wireValue}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            MascotOrbFace(
                                mascot = previewMascot,
                                eyeStateFlow = if (isSelected) eyeStateFlow else null,
                                size = 54.dp,
                                animActive = isSelected,
                                switchPulseKey = if (isSelected) switchBurstTick else 0
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(tokens.accentColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ThemedIcon(
                                        icon = IronPulseIcon.CHECK,
                                        contentDescription = lang.t("Aktif", "Active"),
                                        tint = tokens.onAccentColor,
                                        size = 13.dp
                                    )
                                }
                            }
                        }

                        Text(
                            text = personality.label(lang),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) tokens.accentTextColor else tokens.palette.onSurface,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = personality.tagline(lang),
                            fontSize = 11.sp,
                            lineHeight = 14.5.sp,
                            color = tokens.mutedTextColor,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceSettingsCard(
    perf: PerformancePreferences,
    onApplyPreset: (PerformancePresetId) -> Unit,
    onUpdatePerf: (Boolean?, Boolean?, Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    val activePreset: PerformancePresetId? = when (perf) {
        PerformancePreferences() -> PerformancePresetId.BALANCED
        PerformancePreferences(noAnimations = false, noAmbientGlow = true, staticMascot = false) -> PerformancePresetId.BATTERY
        PerformancePreferences(noAnimations = true, noAmbientGlow = true, staticMascot = true) -> PerformancePresetId.ULTRA
        else -> null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                ThemedIcon(IronPulseIcon.SPEED, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
                Text(
                    text = lang.t("Profil Performa Cepat", "Quick Performance Profiles"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
            }
            if (activePreset == null) {
                IronPulseTonalSurface(
                    shape = tokens.buttonShape,
                    baseColor = tokens.subtleChipBg,
                    border = BorderStroke(1.dp, tokens.subtleBorderColor)
                ) {
                    Text(
                        text = lang.t("Kustom", "Custom"),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.mutedTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                PerformancePresetId.BALANCED to lang.t("Seimbang", "Balanced"),
                PerformancePresetId.BATTERY to lang.t("Hemat Daya", "Battery"),
                PerformancePresetId.ULTRA to lang.t("Ultra Ringan", "Ultra Light")
            ).forEach { (preset, label) ->
                val isSelected = activePreset == preset
                IronPulseTonalSurface(
                    onClick = { onApplyPreset(preset) },
                    shape = tokens.buttonShape,
                    baseColor = if (isSelected) lerp(tokens.controlBg, tokens.accentColor, 0.16f) else tokens.palette.surfaceContainerLow,
                    isAccent = false,
                    pressedScale = 0.95f,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) tokens.accentColor else tokens.surfaceBorderColor
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isSelected) {
                            ThemedIcon(
                                icon = IronPulseIcon.CHECK,
                                contentDescription = null,
                                tint = tokens.accentTextColor,
                                size = 14.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) tokens.accentTextColor else tokens.palette.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = tokens.subtleBorderColor)

        PerformanceSwitchRow(
            title = lang.t("Tanpa Animasi Gerak", "Disable Animations"),
            subtitle = lang.t("Transisi layar instan tanpa efek denyut.", "Instant screen transitions without pulse animations."),
            checked = perf.noAnimations,
            onCheckedChange = { onUpdatePerf(it, null, null) },
            switchModifier = Modifier.testTag("settings_disable_animations_switch")
        )
        PerformanceSwitchRow(
            title = lang.t("Tanpa Cahaya Ambient", "Disable Ambient Glow"),
            subtitle = lang.t("Latar belakang solid bersih tanpa sorot radial.", "Clean solid background without radial glows."),
            checked = perf.noAmbientGlow,
            onCheckedChange = { onUpdatePerf(null, it, null) },
            switchModifier = Modifier.testTag("settings_disable_ambient_glow_switch")
        )
        PerformanceSwitchRow(
            title = lang.t("Maskot Statis", "Static Mascot"),
            subtitle = lang.t("Hentikan gerakan mengambang & kedipan maskot Pulse.", "Pause Pulse mascot floating & blinking motion."),
            checked = perf.staticMascot,
            onCheckedChange = { onUpdatePerf(null, null, it) },
            switchModifier = Modifier.testTag("settings_static_mascot_switch")
        )
    }
}

@Composable
private fun MedalAnimationPreviewCard(
    perf: PerformancePreferences,
    onToggleAnimations: (Boolean) -> Unit,
    onPreviewMedal: (MedalTier) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val animationsEnabled = !perf.noAnimations
    var selectedTier by remember { mutableStateOf(MedalTier.GOLD) }
    val tiers = remember { listOf(MedalTier.BRONZE, MedalTier.SILVER, MedalTier.GOLD) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp)
            .testTag("medal_animation_preview_card"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemedIcon(IronPulseIcon.SPARKLE, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lang.t("Toggle Animasi & Pratinjau Medali", "Animation Toggle & Medal Preview"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = lang.t(
                        "Nyalakan animasi lalu uji perayaan dapat medali & terbang ke Riwayat.",
                        "Toggle UI motion and test the medal unlock celebration & History flight."
                    ),
                    fontSize = 11.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        }

        IronPulseTonalSurface(
            shape = tokens.subtleCardShape,
            baseColor = tokens.wellBg,
            border = BorderStroke(
                1.dp,
                if (animationsEnabled) tokens.accentColor.copy(alpha = 0.34f) else tokens.warningColor.copy(alpha = 0.42f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                PerformanceSwitchRow(
                    title = lang.t("Animasi UI Aktif", "UI Animations Enabled"),
                    subtitle = lang.t(
                        "Aktifkan animasi layar, confetti & penerbangan medali.",
                        "Enable screen transitions, confetti & medal flight animations."
                    ),
                    checked = animationsEnabled,
                    onCheckedChange = onToggleAnimations,
                    switchModifier = Modifier.testTag("settings_anim_quick_toggle")
                )
            }
        }

        if (!animationsEnabled) {
            Text(
                text = lang.t(
                    "Catatan: Mode Tanpa Animasi sedang aktif — aktifkan sakelar di atas untuk melihat animasi penuh.",
                    "Note: Disable Animations is currently on — enable the toggle above to see full motion."
                ),
                fontSize = 11.sp,
                color = tokens.warningColor
            )
        }

        Text(
            text = lang.t("Pilih Tier Medali Uji", "Select Preview Medal Tier"),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = tokens.palette.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tiers.forEach { tier ->
                val isSelected = selectedTier == tier
                IronPulseTonalSurface(
                    onClick = { selectedTier = tier },
                    shape = tokens.subtleCardShape,
                    baseColor = if (isSelected) lerp(tokens.controlBg, tokens.accentColor, 0.16f) else tokens.wellBg,
                    pressedScale = 0.96f,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) tokens.accentColor else tokens.subtleBorderColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 56.dp)
                        .testTag("settings_medal_tier_${tier.wireValue}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ProceduralMedalEmblem(
                            tier = tier,
                            size = 28.dp,
                            shineEnabled = false
                        )
                        Text(
                            text = tier.shortLabel(lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) tokens.accentTextColor else tokens.palette.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = lang.t("${tier.thresholdDays} hr", "${tier.thresholdDays}d"),
                            fontFamily = tokens.fontPack.monoFont,
                            fontSize = 10.sp,
                            color = tokens.mutedTextColor
                        )
                    }
                }
            }
        }

        IronPulseButton(
            onClick = { onPreviewMedal(selectedTier) },
            shape = tokens.buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.accentColor,
                contentColor = tokens.onAccentColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("settings_medal_preview_button")
        ) {
            ThemedIcon(IronPulseIcon.PLAY, contentDescription = null, tint = tokens.onAccentColor, size = 18.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lang.t(
                    "Putar Animasi Medali ${selectedTier.shortLabel(lang)}",
                    "Preview ${selectedTier.shortLabel(lang)} Medal Animation"
                ),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun ThemePreviewCard(
    theme: UiTheme,
    selected: Boolean,
    lang: AppLanguage,
    accentColor: Color,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val previewAccent = resolvePreviewAccentForTheme(theme, accentColor, tokens.timePhase)
    val readableAccent = remember(previewAccent, tokens.isLightTheme) {
        if (tokens.isLightTheme) MaterialYouColorEngine.readableAccentOnLight(previewAccent) else previewAccent
    }
    val fontPack = remember(theme) { resolveThemeFontPack(theme) }
    val isAeroPreview = theme == UiTheme.FRUTIGER_AERO
    val isAeroNightPreview = isAeroPreview && tokens.timePhase == DayTimePhase.NIGHT
    val isXMaterialPreview = theme == UiTheme.XMATERIAL_YOU
    val isXMaterialMorningPreview = isXMaterialPreview && tokens.timePhase == DayTimePhase.MORNING
    val isXMaterialAfternoonPreview = isXMaterialPreview && tokens.timePhase == DayTimePhase.AFTERNOON
    val isXMaterialNightPreview = isXMaterialPreview && tokens.timePhase == DayTimePhase.NIGHT

    val cardShape = when (theme) {
        UiTheme.CARBON_FORGE -> CutCornerShape(8.dp)
        UiTheme.UBUNTU_YARU -> RoundedCornerShape(12.dp)
        UiTheme.AMOLED_STEALTH -> RoundedCornerShape(6.dp)
        UiTheme.TERMINAL_CLI -> RectangleShape
        UiTheme.FRUTIGER_AERO -> RoundedCornerShape(18.dp)
        UiTheme.XMATERIAL_YOU -> RoundedCornerShape(28.dp)
        UiTheme.MATERIAL_YOU -> RoundedCornerShape(18.dp)
    }

    val outerBaseColor = if (selected) {
        lerp(tokens.controlBg, readableAccent, if (tokens.isLightTheme) 0.16f else 0.14f)
    } else {
        tokens.controlBg
    }
    val outerBorderColor = if (selected) readableAccent else tokens.surfaceBorderColor
    IronPulseTonalSurface(
        onClick = onSelect,
        shape = cardShape,
        baseColor = outerBaseColor,
        isAccent = false,
        pressedScale = 0.97f,
        border = BorderStroke(
            width = if (selected) 1.7.dp else 1.dp,
            color = outerBorderColor
        ),
        visualModifier = Modifier.drawWithCache {
            val topHighlight = Brush.verticalGradient(
                0.0f to if (selected) readableAccent.copy(alpha = 0.16f) else Color.White.copy(alpha = if (tokens.isLightTheme) 0.22f else 0.05f),
                0.45f to Color.Transparent
            )
            val selectedBottomRim = if (selected) {
                Brush.verticalGradient(
                    0.70f to Color.Transparent,
                    1.0f to readableAccent.copy(alpha = 0.10f)
                )
            } else null
            onDrawBehind {
                drawRect(topHighlight)
                if (selectedBottomRim != null) {
                    drawRect(selectedBottomRim)
                }
            }
        },
        modifier = modifier.testTag("theme_card_${theme.wireValue}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val previewShape = when (theme) {
                UiTheme.MATERIAL_YOU -> RoundedCornerShape(14.dp)
                UiTheme.CARBON_FORGE -> CutCornerShape(6.dp)
                UiTheme.UBUNTU_YARU -> RoundedCornerShape(8.dp)
                UiTheme.AMOLED_STEALTH -> RoundedCornerShape(4.dp)
                UiTheme.TERMINAL_CLI -> RectangleShape
                UiTheme.FRUTIGER_AERO -> RoundedCornerShape(14.dp)
                UiTheme.XMATERIAL_YOU -> RoundedCornerShape(18.dp)
            }
            val xMaterialBorderBrush = remember(tokens.timePhase) {
                val metal = when (tokens.timePhase) {
                    DayTimePhase.MORNING -> Color(0xFFDFBE84)
                    DayTimePhase.AFTERNOON -> Color(0xFF8CC5C2)
                    DayTimePhase.NIGHT -> Color(0xFFB0A4E3)
                }
                Brush.verticalGradient(
                    0.0f to metal.copy(alpha = 0.54f),
                    0.5f to Color.White.copy(alpha = 0.14f),
                    1.0f to Color(0xFF23252F)
                )
            }
            Column(
                modifier = Modifier
                    .width(102.dp)
                    .height(66.dp)
                    .clip(previewShape)
                    .drawWithCache {
                        val cellPx = (2.dp.toPx()).roundToInt().coerceIn(4, 8)
                        val grainScale = density.roundToInt().coerceIn(1, 3)
                        val matrixCellPx = (3.dp.toPx()).roundToInt().coerceIn(5, 10)
                        val facetCellPx = (12.dp.toPx()).roundToInt().coerceIn(18, 36)
                        val cliLinePx = (1.5.dp.toPx()).roundToInt().coerceIn(3, 6)
                        val baseBg = when (theme) {
                            UiTheme.MATERIAL_YOU -> Color(0xFF221E2A)
                            UiTheme.CARBON_FORGE -> Color(0xFF0E1218)
                            UiTheme.UBUNTU_YARU -> Color(0xFF2D1729)
                            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
                            UiTheme.TERMINAL_CLI -> Color(0xFF030604)
                            UiTheme.FRUTIGER_AERO -> if (isAeroNightPreview) Color(0xFF162A52) else Color(0xFFD6F3FF)
                            UiTheme.XMATERIAL_YOU -> Color(0xFF0A0B10)
                        }
                        val shaderBrush = when (theme) {
                            UiTheme.CARBON_FORGE -> ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = cellPx, subtle = false)
                            UiTheme.UBUNTU_YARU -> ThemeMaterialShaderCache.getYaruMatteGrainBrush(pixelScale = grainScale, subtle = false)
                            UiTheme.AMOLED_STEALTH -> ThemeMaterialShaderCache.getAmoledStealthMatrixBrush(cellPx = matrixCellPx, subtle = false)
                            UiTheme.XMATERIAL_YOU -> ThemeMaterialShaderCache.getPrismaticFacetBrush(cellPx = facetCellPx, isNight = isXMaterialNightPreview)
                            UiTheme.TERMINAL_CLI -> ThemeMaterialShaderCache.getCliScanlineBrush(linePx = cliLinePx, subtle = false)
                            UiTheme.MATERIAL_YOU, UiTheme.FRUTIGER_AERO -> null
                        }
                        val finishBrush = when (theme) {
                            UiTheme.MATERIAL_YOU -> Brush.radialGradient(
                                colors = listOf(previewAccent.copy(alpha = 0.28f), Color.Transparent),
                                center = Offset(size.width * 0.85f, size.height * 0.20f),
                                radius = (size.width * 0.85f).coerceAtLeast(1f)
                            )
                            UiTheme.CARBON_FORGE -> Brush.verticalGradient(
                                0.0f to Color.White.copy(alpha = 0.09f),
                                0.45f to Color.Transparent,
                                1.0f to Color.Black.copy(alpha = 0.30f)
                            )
                            UiTheme.UBUNTU_YARU -> Brush.linearGradient(
                                colors = listOf(Color(0x52E95420), Color(0x3677216F), Color.Transparent),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, size.height)
                            )
                            UiTheme.AMOLED_STEALTH -> Brush.verticalGradient(
                                0.0f to previewAccent.copy(alpha = 0.14f),
                                0.45f to Color.Transparent
                            )
                            UiTheme.TERMINAL_CLI -> Brush.verticalGradient(
                                0.0f to Color(0xFF00FF66).copy(alpha = 0.12f),
                                0.50f to Color.Transparent,
                                1.0f to Color(0xFF00FF66).copy(alpha = 0.05f)
                            )
                            UiTheme.FRUTIGER_AERO -> if (isAeroNightPreview) {
                                Brush.linearGradient(
                                    0.0f to Color(0xFF0D2145),
                                    0.50f to Color(0xFF133A64),
                                    1.0f to Color(0xFF0B3D3C),
                                    start = Offset.Zero,
                                    end = Offset(size.width, size.height)
                                )
                            } else {
                                Brush.linearGradient(
                                    0.0f to Color(0xFF4AC7FA),
                                    0.55f to Color(0xFFC8F7EE),
                                    1.0f to Color(0xFF7BE8B8),
                                    start = Offset.Zero,
                                    end = Offset(size.width, size.height)
                                )
                            }
                            UiTheme.XMATERIAL_YOU -> {
                                val phaseMetal = when (tokens.timePhase) {
                                    DayTimePhase.MORNING -> Color(0xFFDFBE84)
                                    DayTimePhase.AFTERNOON -> Color(0xFF8CC5C2)
                                    DayTimePhase.NIGHT -> Color(0xFFB0A4E3)
                                }
                                Brush.verticalGradient(
                                    0.0f to phaseMetal.copy(alpha = 0.12f),
                                    0.45f to Color.Transparent,
                                    1.0f to Color(0xFF06070A)
                                )
                            }
                        }
                        val aeroFlareCenter = Offset(size.width * 0.78f, size.height * 0.24f)
                        val aeroFlareBrush = if (isAeroPreview) {
                            Brush.radialGradient(
                                colors = if (isAeroNightPreview) {
                                    listOf(Color(0x99BAE6FD), Color(0x3838BDF8), Color.Transparent)
                                } else {
                                    listOf(Color(0xE6FFFFFF), Color(0x557CE8FF), Color.Transparent)
                                },
                                center = aeroFlareCenter,
                                radius = size.width * 0.34f
                            )
                        } else null
                        val aeroGlossDome = if (isAeroPreview) {
                            Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width, size.height * 0.42f)
                                cubicTo(
                                    size.width * 0.66f, size.height * 0.56f,
                                    size.width * 0.34f, size.height * 0.36f,
                                    0f, size.height * 0.50f
                                )
                                close()
                            }
                        } else null
                        val xMatSheen = if (isXMaterialPreview) {
                            Brush.verticalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)
                            )
                        } else null
                        onDrawBehind {
                            drawRect(baseBg)
                            if (shaderBrush != null) {
                                val sAlpha = if (theme == UiTheme.XMATERIAL_YOU) 0.55f else 0.90f
                                drawRect(shaderBrush, alpha = sAlpha)
                            }
                            drawRect(finishBrush)
                            if (aeroFlareBrush != null) {
                                drawCircle(
                                    brush = aeroFlareBrush,
                                    radius = size.width * 0.34f,
                                    center = aeroFlareCenter
                                )
                                drawCircle(
                                    color = if (isAeroNightPreview) Color(0xFF7DD3FC).copy(alpha = 0.32f) else Color.White.copy(alpha = 0.48f),
                                    radius = size.width * 0.16f,
                                    center = aeroFlareCenter,
                                    style = Stroke(0.9.dp.toPx())
                                )
                                drawCircle(
                                    color = if (isAeroNightPreview) Color(0xFF5EEAD4).copy(alpha = 0.24f) else Color(0xFFA7F3D0).copy(alpha = 0.42f),
                                    radius = size.width * 0.075f,
                                    center = Offset(size.width * 0.46f, size.height * 0.54f)
                                )
                                drawCircle(
                                    color = if (isAeroNightPreview) Color(0xFF2DD4BF).copy(alpha = 0.20f) else Color.White.copy(alpha = 0.35f),
                                    radius = size.width * 0.095f,
                                    center = Offset(size.width * 0.22f, size.height * 0.74f)
                                )
                            }
                            if (aeroGlossDome != null) {
                                drawPath(
                                    path = aeroGlossDome,
                                    brush = Brush.verticalGradient(
                                        if (isAeroNightPreview) {
                                            listOf(Color(0x52E0F4FF), Color(0x16BAE6FD))
                                        } else {
                                            listOf(Color(0x92FFFFFF), Color(0x2CFFFFFF))
                                        }
                                    )
                                )
                            }
                            if (isXMaterialPreview && xMatSheen != null) {
                                drawRect(xMatSheen)
                            }
                            if (theme == UiTheme.AMOLED_STEALTH) {
                                val bLen = 5.5.dp.toPx()
                                val bPad = 2.dp.toPx()
                                val bColor = previewAccent.copy(alpha = 0.90f)
                                val bSw = 1.15.dp.toPx()
                                drawLine(bColor, Offset(bPad, bPad), Offset(bPad + bLen, bPad), bSw)
                                drawLine(bColor, Offset(bPad, bPad), Offset(bPad, bPad + bLen), bSw)
                                drawLine(bColor, Offset(size.width - bPad, bPad), Offset(size.width - bPad - bLen, bPad), bSw)
                                drawLine(bColor, Offset(size.width - bPad, bPad), Offset(size.width - bPad, bPad + bLen), bSw)
                                drawLine(bColor, Offset(bPad, size.height - bPad), Offset(bPad + bLen, size.height - bPad), bSw)
                                drawLine(bColor, Offset(bPad, size.height - bPad), Offset(bPad, size.height - bPad - bLen), bSw)
                                drawLine(bColor, Offset(size.width - bPad, size.height - bPad), Offset(size.width - bPad - bLen, size.height - bPad), bSw)
                                drawLine(bColor, Offset(size.width - bPad, size.height - bPad), Offset(size.width - bPad, size.height - bPad - bLen), bSw)
                            } else if (theme == UiTheme.CARBON_FORGE) {
                                drawRect(
                                    color = previewAccent,
                                    topLeft = Offset(0f, 14.dp.toPx()),
                                    size = androidx.compose.ui.geometry.Size(2.2.dp.toPx(), size.height - 20.dp.toPx())
                                )
                            } else if (theme == UiTheme.UBUNTU_YARU) {
                                drawLine(
                                    color = Color(0xFFE95420),
                                    start = Offset(size.width * 0.34f, size.height - 2.dp.toPx()),
                                    end = Offset(size.width * 0.66f, size.height - 2.dp.toPx()),
                                    strokeWidth = 2.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            } else if (theme == UiTheme.TERMINAL_CLI) {
                                val tLen = 5.dp.toPx()
                                val tPad = 2.dp.toPx()
                                val tColor = Color(0xFF00FF66)
                                val tSw = 1.25.dp.toPx()
                                drawLine(tColor, Offset(tPad, tPad), Offset(tPad + tLen, tPad), tSw)
                                drawLine(tColor, Offset(tPad, tPad), Offset(tPad, tPad + tLen), tSw)
                                drawLine(tColor, Offset(size.width - tPad, tPad), Offset(size.width - tPad - tLen, tPad), tSw)
                                drawLine(tColor, Offset(size.width - tPad, tPad), Offset(size.width - tPad, tPad + tLen), tSw)
                                drawLine(tColor, Offset(tPad, size.height - tPad), Offset(tPad + tLen, size.height - tPad), tSw)
                                drawLine(tColor, Offset(tPad, size.height - tPad), Offset(tPad, size.height - tPad - tLen), tSw)
                                drawLine(tColor, Offset(size.width - tPad, size.height - tPad), Offset(size.width - tPad - tLen, size.height - tPad), tSw)
                                drawLine(tColor, Offset(size.width - tPad, size.height - tPad), Offset(size.width - tPad, size.height - tPad - tLen), tSw)
                            }
                        }
                    }
                    .then(
                        if (isXMaterialPreview) {
                            Modifier.border(0.95.dp, xMaterialBorderBrush, previewShape)
                        } else {
                            Modifier.border(
                                1.dp,
                                when (theme) {
                                    UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.26f)
                                    UiTheme.UBUNTU_YARU -> Color(0xFF141414)
                                    UiTheme.AMOLED_STEALTH -> previewAccent.copy(alpha = 0.52f)
                                    UiTheme.TERMINAL_CLI -> Color(0xFF00FF66).copy(alpha = 0.78f)
                                    UiTheme.FRUTIGER_AERO -> if (isAeroNightPreview) Color(0xB45BC6FA) else Color(0xC20082C8)
                                    else -> previewAccent.copy(alpha = 0.36f)
                                },
                                previewShape
                            )
                        }
                    )
            ) {
                // Dedicated mini OS header strip — zero overlap with emblem/icons below
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(
                            when (theme) {
                                UiTheme.FRUTIGER_AERO -> if (isAeroNightPreview) Color(0xCC1E3D6B) else Color.White.copy(alpha = 0.66f)
                                UiTheme.XMATERIAL_YOU -> Color(0xFF101218)
                                UiTheme.UBUNTU_YARU -> Color(0xCC1E1920)
                                UiTheme.AMOLED_STEALTH -> Color(0xFF06080B)
                                UiTheme.TERMINAL_CLI -> Color(0xFF08110C)
                                UiTheme.CARBON_FORGE -> Color(0xCC181E28)
                                UiTheme.MATERIAL_YOU -> Color.White.copy(alpha = 0.08f)
                            }
                        )
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(3.5.dp)
                            .clip(if (theme == UiTheme.TERMINAL_CLI) RectangleShape else RoundedCornerShape(2.dp))
                            .background(
                                when {
                                    isAeroNightPreview -> Color(0xFF7DD3FC).copy(alpha = 0.85f)
                                    isAeroPreview -> Color(0xFF073B66).copy(alpha = 0.65f)
                                    isXMaterialMorningPreview -> Color(0xFFDFBE84).copy(alpha = 0.90f)
                                    isXMaterialAfternoonPreview -> Color(0xFF8CC5C2).copy(alpha = 0.90f)
                                    isXMaterialNightPreview -> Color(0xFFB0A4E3).copy(alpha = 0.90f)
                                    else -> previewAccent.copy(alpha = 0.85f)
                                }
                            )
                    )
                    when (theme) {
                        UiTheme.UBUNTU_YARU -> Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp)) {
                            Box(Modifier.size(3.5.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.35f)))
                            Box(Modifier.size(3.5.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.55f)))
                            Box(Modifier.size(3.5.dp).clip(CircleShape).background(Color(0xFFE95420)))
                        }
                        UiTheme.FRUTIGER_AERO -> Row(horizontalArrangement = Arrangement.spacedBy(2.5.dp)) {
                            Box(Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                            Box(Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF22C55E)))
                        }
                        UiTheme.XMATERIAL_YOU -> {
                            val dotMetal = when (tokens.timePhase) {
                                DayTimePhase.MORNING -> Color(0xFFDFBE84)
                                DayTimePhase.AFTERNOON -> Color(0xFF8CC5C2)
                                DayTimePhase.NIGHT -> Color(0xFFB0A4E3)
                            }
                            Box(Modifier.size(4.dp).clip(CircleShape).background(dotMetal))
                        }
                        UiTheme.AMOLED_STEALTH, UiTheme.TERMINAL_CLI -> Box(Modifier.size(4.dp).background(previewAccent))
                        else -> Box(Modifier.size(4.dp).clip(CircleShape).background(previewAccent.copy(alpha = 0.75f)))
                    }
                }
                HorizontalDivider(
                    thickness = 0.75.dp,
                    color = when {
                        isAeroNightPreview -> Color(0x667DD3FC)
                        isAeroPreview -> Color(0x550082C8)
                        isXMaterialPreview -> previewAccent.copy(alpha = 0.28f)
                        theme == UiTheme.TERMINAL_CLI -> Color(0xFF1F8A4C)
                        else -> Color.White.copy(alpha = 0.10f)
                    }
                )

                // Clean mini OS viewport body: emblem + 3 custom themed icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ThemeBrandEmblem(
                        theme = theme,
                        accentOverride = previewAccent,
                        size = 28.dp
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        isAeroNightPreview -> Color(0xFFE0F2FE).copy(alpha = 0.85f)
                                        isAeroPreview -> Color(0xFF072B4A).copy(alpha = 0.72f)
                                        isXMaterialPreview -> Color(0xFFF4F2EE).copy(alpha = 0.88f)
                                        else -> Color.White.copy(alpha = 0.75f)
                                    }
                                )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconTint = when {
                                isAeroNightPreview -> Color(0xFF38BDF8)
                                isAeroPreview -> Color(0xFF005F9E)
                                isXMaterialMorningPreview -> Color(0xFFDFBE84)
                                isXMaterialAfternoonPreview -> Color(0xFF8CC5C2)
                                isXMaterialNightPreview -> Color(0xFFB0A4E3)
                                else -> previewAccent
                            }
                            ThemedIcon(
                                icon = IronPulseIcon.TODAY,
                                contentDescription = null,
                                tint = iconTint,
                                size = 12.dp,
                                themeOverride = theme
                            )
                            ThemedIcon(
                                icon = IronPulseIcon.EXERCISES,
                                contentDescription = null,
                                tint = when {
                                    isAeroNightPreview -> Color(0xFFF0F8FF).copy(alpha = 0.90f)
                                    isAeroPreview -> Color(0xFF073257)
                                    isXMaterialPreview -> Color(0xFFF4F2EE).copy(alpha = 0.92f)
                                    else -> Color.White.copy(alpha = 0.85f)
                                },
                                size = 12.dp,
                                themeOverride = theme
                            )
                            ThemedIcon(
                                icon = IronPulseIcon.TIMER,
                                contentDescription = null,
                                tint = iconTint,
                                size = 12.dp,
                                themeOverride = theme
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = lang.t(theme.titleId, theme.titleEn),
                        fontFamily = fontPack.displayFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    IronPulseTonalSurface(
                        shape = tokens.buttonShape,
                        baseColor = readableAccent.copy(alpha = if (tokens.isLightTheme) 0.14f else 0.16f),
                        border = BorderStroke(1.dp, readableAccent.copy(alpha = if (tokens.isLightTheme) 0.42f else 0.36f))
                    ) {
                        Text(
                            text = fontPack.osBadgeLabel,
                            fontFamily = fontPack.monoFont,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = readableAccent,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = fontPack.fontDisplayName,
                    fontFamily = fontPack.bodyFont,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = readableAccent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = lang.t(theme.subtitleId, theme.subtitleEn),
                    fontSize = 11.sp,
                    lineHeight = 14.5.sp,
                    color = tokens.palette.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (selected) {
                ThemedIcon(
                    icon = IronPulseIcon.CHECK_CIRCLE,
                    contentDescription = lang.t("Dipilih", "Selected"),
                    tint = readableAccent,
                    size = 20.dp,
                    themeOverride = theme
                )
            }
        }
    }
}

@Composable
private fun PerformanceSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchModifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Text(text = title, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = tokens.palette.onSurface)
            Text(text = subtitle, fontSize = 11.5.sp, color = tokens.palette.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = tokens.onAccentColor,
                checkedTrackColor = tokens.accentColor
            ),
            modifier = switchModifier
        )
    }
}

@Composable
private fun AlarmAudioSettingsCard(
    state: IronPulseUiState,
    viewModel: GymViewModel,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val profile = state.profile
    val volumeFloat = (profile?.volume ?: 0.85).toFloat().coerceIn(0.1f, 1.0f)
    val snoozeMinutes = profile?.snoozeMinutes ?: 5
    val autoStartRest = profile?.autoStartRestTimer ?: true

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemedIcon(IronPulseIcon.VOLUME, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
                Text(
                    text = lang.t("Volume & Pengatur Waktu", "Volume & Timing"),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
            }
            Text(
                text = "${(volumeFloat * 100).toInt()}%",
                fontFamily = tokens.fontPack.monoFont,
                fontWeight = FontWeight.Bold,
                color = tokens.accentTextColor
            )
        }

        Slider(
            value = volumeFloat,
            onValueChange = { viewModel.updateProfile(volume = it.toDouble()) },
            valueRange = 0.1f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = tokens.accentColor,
                activeTrackColor = tokens.accentColor
            )
        )

        HorizontalDivider(color = tokens.dividerColor)

        Text(
            text = lang.t("Durasi Tunda (Snooze)", "Snooze Duration"),
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = tokens.palette.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(5, 10, 15).forEach { mins ->
                val active = snoozeMinutes == mins
                IronPulseTonalSurface(
                    onClick = { viewModel.updateProfile(snoozeMinutes = mins) },
                    shape = tokens.buttonShape,
                    baseColor = if (active) tokens.accentColor else tokens.palette.surfaceContainerLow,
                    isAccent = active,
                    pressedScale = 0.95f,
                    border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (lang == AppLanguage.EN) "$mins Min" else "$mins Menit",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) tokens.onAccentColor else tokens.palette.onSurface,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        HorizontalDivider(color = tokens.dividerColor)

        PerformanceSwitchRow(
            title = lang.t("Timer Istirahat Otomatis", "Auto-Start Rest Timer"),
            subtitle = lang.t("Mulai hitung istirahat otomatis tiap kali set dicentang.", "Start rest countdown automatically on set completion."),
            checked = autoStartRest,
            onCheckedChange = { viewModel.updateProfile(autoStartRestTimer = it) }
        )
    }
}

@Composable
private fun AlarmFeedbackSettingsCard(
    state: IronPulseUiState,
    viewModel: GymViewModel,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemedIcon(IronPulseIcon.SPARKLE, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
            Text(
                text = lang.t("Umpan Balik & Suara", "Feedback & Voice"),
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = lang.t("Haptik Taktil", "Tactile Haptics"),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = lang.t("Getaran saat set selesai & timer.", "Pulses on set check & timers."),
                    fontSize = 11.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IronPulseTonalSurface(
                    onClick = { viewModel.testHapticPattern() },
                    shape = tokens.buttonShape,
                    baseColor = tokens.controlBg,
                    pressedScale = 0.92f,
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor)
                ) {
                    Text(
                        text = lang.t("Uji", "Test"),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.accentTextColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Switch(
                    checked = state.appPrefs.hapticsEnabled,
                    onCheckedChange = { viewModel.setHapticsEnabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = tokens.onAccentColor,
                        checkedTrackColor = tokens.accentColor
                    )
                )
            }
        }

        HorizontalDivider(color = tokens.dividerColor)

        PerformanceSwitchRow(
            title = lang.t("Suara Maskot Pulse (TTS)", "Pulse Mascot Voice (TTS)"),
            subtitle = lang.t("Bacakan dialog penyemangat secara otomatis.", "Speak motivational companion lines aloud."),
            checked = state.appPrefs.mascotVoiceEnabled,
            onCheckedChange = { viewModel.setMascotVoiceEnabled(it) }
        )
    }
}

@Composable
private fun AlarmDiagnosticsCard(
    state: IronPulseUiState,
    viewModel: GymViewModel,
    onRequestNotificationPermission: () -> Unit,
    onOpenExactAlarmSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val notificationsEnabled = state.profile?.notificationsEnabled ?: true

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens, borderOverride = tokens.accentColor.copy(alpha = 0.35f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemedIcon(IronPulseIcon.NOTIFICATIONS, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
            Text(
                text = lang.t("Diagnostik Alarm Android", "Android Alarm Diagnostics"),
                fontSize = 15.5.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        }

        PerformanceSwitchRow(
            title = lang.t("Aktifkan Alarm Terjadwal", "Enable Scheduled Alarms"),
            subtitle = lang.t("Sinkronkan alarm harian ke Android AlarmManager.", "Sync weekly schedule to native Android AlarmManager."),
            checked = notificationsEnabled,
            onCheckedChange = { enabled ->
                if (enabled && !state.hasNotificationPermission) {
                    onRequestNotificationPermission()
                } else {
                    viewModel.updateProfile(notificationsEnabled = enabled)
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .ironGlassSurface(tokens, subtle = true)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lang.t("Izin Notifikasi", "Notification Permission"),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = if (state.hasNotificationPermission) lang.t("Diizinkan", "Granted") else lang.t("Belum diizinkan", "Missing"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (state.hasNotificationPermission) tokens.successColor else tokens.warningColor
                )
            }
            if (!state.hasNotificationPermission) {
                IronPulseButton(
                    onClick = onRequestNotificationPermission,
                    shape = tokens.buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentColor,
                        contentColor = tokens.onAccentColor
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(lang.t("Izinkan", "Allow"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                ThemedIcon(IronPulseIcon.CHECK_CIRCLE, contentDescription = null, tint = tokens.successColor, size = 20.dp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .ironGlassSurface(tokens, subtle = true)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lang.t("Exact Alarm Android", "Android Exact Alarm"),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = if (state.canScheduleExactAlarms) lang.t("Aktif (Presisi)", "Active (Exact)") else lang.t("Perlu izin sistem", "Needs system permission"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (state.canScheduleExactAlarms) tokens.successColor else tokens.warningColor
                )
            }
            IronPulseOutlinedButton(
                onClick = onOpenExactAlarmSettings,
                shape = tokens.buttonShape,
                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(lang.t("Atur", "Manage"), fontSize = 12.sp, color = tokens.palette.onSurface)
            }
        }

        IronPulseButton(
            onClick = { viewModel.schedule5SecondNativeTestAlarm() },
            shape = tokens.buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.accentColor,
                contentColor = tokens.onAccentColor
            ),
            modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
            ThemedIcon(IronPulseIcon.ALARM, contentDescription = null, size = 18.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(lang.t("Uji Alarm Latar Belakang (5 Detik)", "Test Background Alarm (5s)"), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RingtoneManagerHeaderCard(
    uploading: Boolean,
    onPickCustomRingtone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemedIcon(IronPulseIcon.MUSIC, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
            Text(
                text = lang.t("Pengelola Nada Dering", "Ringtone Manager"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        }
        Text(
            text = lang.t(
                "Pilih nada bawaan atau unggah file MP3/WAV/OGG/M4A dari penyimpanan HP untuk Alarm Gym maupun Nada Timer Istirahat.",
                "Pick built-in synth tones or upload MP3/WAV/OGG/M4A audio from your phone for Gym Alarms and Rest Timers."
            ),
            fontSize = 12.5.sp,
            color = tokens.palette.onSurfaceVariant
        )

        IronPulseOutlinedButton(
            onClick = onPickCustomRingtone,
            enabled = !uploading,
            shape = tokens.buttonShape,
            border = BorderStroke(1.dp, tokens.accentTextColor.copy(alpha = 0.55f)),
            modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
            if (uploading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = tokens.accentTextColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(lang.t("Mengimpor Audio...", "Importing Audio..."), color = tokens.accentTextColor, fontWeight = FontWeight.Bold)
            } else {
                ThemedIcon(IronPulseIcon.ADD, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(lang.t("Pilih File Audio dari HP", "Pick Audio File from Phone"), color = tokens.accentTextColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CustomRingtoneItemCard(
    tone: CustomRingtone,
    isPreviewing: Boolean,
    isAlarmDefault: Boolean,
    isRestDefault: Boolean,
    onPreview: () -> Unit,
    onDelete: () -> Unit,
    onAssignAlarm: () -> Unit,
    onAssignRest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val sizeKb = (tone.sizeBytes / 1024L).coerceAtLeast(1L)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens, subtle = true)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IronPulseTonalSurface(
                onClick = onPreview,
                shape = CircleShape,
                baseColor = tokens.accentColor.copy(alpha = 0.18f),
                isAccent = isPreviewing,
                visualModifier = Modifier.size(36.dp)
            ) {
                ThemedIcon(
                    icon = if (isPreviewing) IronPulseIcon.STOP else IronPulseIcon.PLAY,
                    contentDescription = "Preview",
                    tint = tokens.accentTextColor,
                    size = 18.dp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tone.name,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${sizeKb} KB · ${if (tone.storageMode == RingtoneStorageMode.CLOUD) "Cloud Sync" else lang.t("Lokal HP", "Local File")}",
                    fontSize = 11.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                ThemedIcon(
                    icon = IronPulseIcon.DELETE,
                    contentDescription = "Delete",
                    tint = tokens.dangerColor,
                    size = 18.dp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RingtoneAssignChip(
                label = lang.t("Alarm Gym", "Gym Alarm"),
                active = isAlarmDefault,
                onClick = onAssignAlarm,
                modifier = Modifier.weight(1f)
            )
            RingtoneAssignChip(
                label = lang.t("Nada Istirahat", "Rest Chime"),
                active = isRestDefault,
                onClick = onAssignRest,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BuiltinRingtoneItemCard(
    spec: BuiltinRingtoneSpec,
    isPreviewing: Boolean,
    isAlarmDefault: Boolean,
    isRestDefault: Boolean,
    onPreview: () -> Unit,
    onAssignAlarm: () -> Unit,
    onAssignRest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens, subtle = true)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IronPulseTonalSurface(
                onClick = onPreview,
                shape = CircleShape,
                baseColor = tokens.accentColor.copy(alpha = 0.18f),
                isAccent = isPreviewing,
                visualModifier = Modifier.size(36.dp)
            ) {
                ThemedIcon(
                    icon = if (isPreviewing) IronPulseIcon.STOP else IronPulseIcon.PLAY,
                    contentDescription = "Preview",
                    tint = tokens.accentTextColor,
                    size = 18.dp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = spec.name,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface
                    )
                    Text(
                        text = lang.t(spec.patternLabelId, spec.patternLabelEn),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tokens.accentTextColor
                    )
                }
                Text(
                    text = lang.t(spec.descriptionId, spec.descriptionEn),
                    fontSize = 11.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RingtoneAssignChip(
                label = lang.t("Alarm Gym", "Gym Alarm"),
                active = isAlarmDefault,
                onClick = onAssignAlarm,
                modifier = Modifier.weight(1f)
            )
            RingtoneAssignChip(
                label = lang.t("Nada Istirahat", "Rest Chime"),
                active = isRestDefault,
                onClick = onAssignRest,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RingtoneAssignChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val chipShape = tokens.buttonShape
    IronPulseTonalSurface(
        onClick = onClick,
        shape = chipShape,
        baseColor = if (active) lerp(tokens.wellBg, tokens.accentColor, 0.16f) else tokens.wellBg,
        isAccent = false,
        pressedScale = 0.95f,
        border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (active) {
                ThemedIcon(IronPulseIcon.CHECK, contentDescription = null, tint = tokens.accentTextColor, size = 14.dp)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (active) tokens.accentTextColor else tokens.mutedTextColor,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LiveClockPreviewBadge(
    clockSecondsFlow: StateFlow<Long>,
    clockFormat: ClockFormat,
    lang: AppLanguage,
    accentColor: Color
) {
    val tabActive = LocalTabActive.current
    val minuteBucketFlow = remember(clockSecondsFlow) {
        clockSecondsFlow.map { it / 60L }.distinctUntilChanged()
    }
    val minuteBucket = if (tabActive) {
        minuteBucketFlow.collectAsStateWithLifecycle(initialValue = clockSecondsFlow.value / 60L).value
    } else {
        remember(clockSecondsFlow) { clockSecondsFlow.value / 60L }
    }
    val livePreviewTime = remember(minuteBucket, clockFormat, lang) {
        GymModelHelpers.formatAlarmTime(
            GymModelHelpers.formatHHMM(Date(minuteBucket * 60_000L)),
            clockFormat,
            lang
        )
    }
    Text(
        text = livePreviewTime,
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = accentColor
    )
}

@Composable
private fun SystemLocalizationAndClockCard(
    selectedLang: AppLanguage,
    onSelectLang: (AppLanguage) -> Unit,
    clockFormat: ClockFormat,
    clockSecondsFlow: StateFlow<Long>,
    onSelectFormat: (ClockFormat) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("system_localization_clock_card")
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemedIcon(IronPulseIcon.SETTINGS, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
            Text(
                text = lang.t("Bahasa & Format Jam", "Language & Clock Format"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = lang.t("Bahasa Aplikasi", "Application Language"),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = tokens.palette.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLanguage.entries.forEach { option ->
                    val active = selectedLang == option
                    IronPulseTonalSurface(
                        onClick = { onSelectLang(option) },
                        shape = tokens.subtleCardShape,
                        baseColor = if (active) tokens.accentColor.copy(alpha = 0.18f) else tokens.palette.surfaceContainerLow,
                        isAccent = active,
                        pressedScale = 0.95f,
                        border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option.displayName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) tokens.accentTextColor else tokens.palette.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (active) {
                                ThemedIcon(IronPulseIcon.CHECK_CIRCLE, contentDescription = null, tint = tokens.accentTextColor, size = 16.dp)
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = tokens.dividerColor)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lang.t("Format Jam", "Clock Format"),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.palette.onSurface
                )
                LiveClockPreviewBadge(
                    clockSecondsFlow = clockSecondsFlow,
                    clockFormat = clockFormat,
                    lang = lang,
                    accentColor = tokens.accentTextColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    ClockFormat.H24 to lang.t("24 Jam (18:30)", "24-Hour (18:30)"),
                    ClockFormat.H12 to lang.t("12 Jam (6:30 Sore)", "12-Hour (6:30 PM)")
                ).forEach { (fmt, label) ->
                    val active = clockFormat == fmt
                    IronPulseTonalSurface(
                        onClick = { onSelectFormat(fmt) },
                        shape = tokens.subtleCardShape,
                        baseColor = if (active) tokens.accentColor.copy(alpha = 0.18f) else tokens.palette.surfaceContainerLow,
                        isAccent = active,
                        pressedScale = 0.95f,
                        border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) tokens.accentTextColor else tokens.palette.onSurface,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemDisplayAndGuideCard(
    keepScreenOnWorkout: Boolean,
    onToggleKeepScreenOn: (Boolean) -> Unit,
    onOpenTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("system_display_guide_card")
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemedIcon(IronPulseIcon.SCHOOL, contentDescription = null, tint = tokens.accentTextColor, size = 18.dp)
            Text(
                text = lang.t("Layar & Panduan", "Display & Guide"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        }

        PerformanceSwitchRow(
            title = lang.t("Layar Tetap Menyala di Tab Latihan", "Keep Screen On in Workout"),
            subtitle = lang.t(
                "Cegah layar meredup atau mati otomatis selama kamu berada di tab Latihan.",
                "Prevent the screen from dimming or locking while you are in the Workout tab."
            ),
            checked = keepScreenOnWorkout,
            onCheckedChange = onToggleKeepScreenOn,
            switchModifier = Modifier.testTag("settings_keep_screen_on_switch")
        )

        HorizontalDivider(color = tokens.dividerColor)

        SystemActionRow(
            icon = IronPulseIcon.SCHOOL,
            title = lang.t("Tur Interaktif Bersama Pulse", "Interactive Tour with Pulse"),
            subtitle = lang.t("Pulse memandumu keliling 5 tab, template jadwal & alarm.", "Let Pulse walk you through all 5 tabs, templates & alarms."),
            onClick = onOpenTutorial
        )
    }
}

@Composable
private fun SystemActionRow(
    icon: IronPulseIcon,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val rowShape = tokens.subtleCardShape
    IronPulseTonalSurface(
        onClick = onClick,
        shape = rowShape,
        baseColor = tokens.wellBg,
        pressedScale = 0.97f,
        border = BorderStroke(1.dp, tokens.subtleBorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .ironTonalControl(
                        tokens = tokens,
                        shape = tokens.buttonShape,
                        baseColor = tokens.accentColor.copy(alpha = 0.16f),
                        isAccent = false
                    ),
                contentAlignment = Alignment.Center
            ) {
                ThemedIcon(
                    icon = icon,
                    contentDescription = null,
                    tint = tokens.accentTextColor,
                    size = 18.dp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = tokens.mutedTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ThemedIcon(
                icon = IronPulseIcon.CHEVRON_RIGHT,
                contentDescription = null,
                tint = tokens.mutedTextColor,
                size = 20.dp
            )
        }
    }
}
