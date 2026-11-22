package com.fathan.ironpulse.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
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
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.PerformancePreferences
import com.fathan.ironpulse.model.PerformancePresetId
import com.fathan.ironpulse.model.RingtoneStorageMode
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.components.ConfirmDeleteDialog
import com.fathan.ironpulse.ui.components.TutorialSpotlightTarget
import com.fathan.ironpulse.ui.components.fluidModalEntrance
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.components.tutorialSpotlightTarget
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.LocalTabActive
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine
import com.fathan.ironpulse.ui.theme.ThemeMaterialShaderCache
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private enum class SettingsSubTab {
    PROFILE,
    APPEARANCE,
    ALARM,
    SYSTEM
}

private enum class AppearanceSection {
    THEMES,
    PERFORMANCE
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
    subTabListStates: Map<String, LazyListState> = remember {
        SettingsSubTab.entries.associate { it.name to LazyListState() }
    }
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    var activeSubTab by remember { mutableStateOf(SettingsSubTab.PROFILE) }
    var appearanceSection by remember { mutableStateOf(AppearanceSection.THEMES) }
    var confirmSignOut by remember { mutableStateOf(false) }
    var showCustomHexDialog by remember { mutableStateOf(false) }
    var pendingDeleteTone by remember { mutableStateOf<CustomRingtone?>(null) }

    val fallbackListState = rememberLazyListState()
    val listState = subTabListStates[activeSubTab.name] ?: fallbackListState
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val headerHeightDp = with(density) { headerHeightPx.toDp() }

    val itemAnimMod: Modifier = Modifier

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = contentPadding.calculateTopPadding() + headerHeightDp + 22.dp,
                bottom = contentPadding.calculateBottomPadding() + 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 640.dp)
                .fillMaxSize()
        ) {
            when (activeSubTab) {
                SettingsSubTab.PROFILE -> {
                    item(key = "profile_hero", contentType = "profile_hero") {
                        ProfileHeroCard(
                            state = state,
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    item(key = "profile_name", contentType = "profile_name") {
                        ProfileDisplayNameCard(
                            state = state,
                            onSaveName = { viewModel.updateProfile(displayName = it) },
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    item(key = "profile_signout", contentType = "profile_signout") {
                        ProfileSignOutCard(
                            onRequestSignOut = { confirmSignOut = true },
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                }

                SettingsSubTab.APPEARANCE -> {
                    item(key = "appearance_switcher", contentType = "appearance_switcher") {
                        AppearanceSectionSwitcher(
                            section = appearanceSection,
                            onSelectSection = { appearanceSection = it },
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    if (appearanceSection == AppearanceSection.THEMES) {
                        item(key = "themes_grid", contentType = "themes_grid") {
                            ThemesGridCard(
                                selectedTheme = state.appPrefs.uiTheme,
                                onSelectTheme = { viewModel.setUiTheme(it) },
                                onResetTheme = { viewModel.resetThemeCustomization() },
                                modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                            )
                        }
                        item(key = "accent_tint", contentType = "accent_tint") {
                            AccentAndTintSettingsCard(
                                accentColorHex = state.appPrefs.accentColorHex,
                                materialTintColorHex = state.appPrefs.materialTintColorHex,
                                onSelectAccent = { viewModel.setAccentColor(it) },
                                onOpenCustomHex = { showCustomHexDialog = true },
                                onSelectMaterialTint = { viewModel.setMaterialTintColor(it) },
                                modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                            )
                        }
                        item(key = "day_phase_mood", contentType = "day_phase_mood") {
                            TodayPhaseMoodCard(
                                selectedMode = state.appPrefs.todayThemeMode,
                                onSelectMode = { viewModel.setTodayThemeMode(it) },
                                modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                            )
                        }
                    } else {
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
                                modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                            )
                        }
                    }
                }

                SettingsSubTab.ALARM -> {
                    item(key = "alarm_audio", contentType = "alarm_audio") {
                        AlarmAudioSettingsCard(
                            state = state,
                            viewModel = viewModel,
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    item(key = "alarm_feedback", contentType = "alarm_feedback") {
                        AlarmFeedbackSettingsCard(
                            state = state,
                            viewModel = viewModel,
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    item(key = "alarm_diagnostics", contentType = "alarm_diagnostics") {
                        AlarmDiagnosticsCard(
                            state = state,
                            viewModel = viewModel,
                            onRequestNotificationPermission = onRequestNotificationPermission,
                            onOpenExactAlarmSettings = onOpenExactAlarmSettings,
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    item(key = "ringtone_header", contentType = "ringtone_header") {
                        RingtoneManagerHeaderCard(
                            uploading = state.uploadingRingtone,
                            onPickCustomRingtone = onPickCustomRingtone,
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
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
                        items(
                            items = state.customRingtones,
                            key = { "custom_tone_${it.id}" },
                            contentType = { "ringtone_row" }
                        ) { tone ->
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
                                modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
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
                    items(
                        items = GymModelHelpers.BUILTIN_RINGTONES,
                        key = { "builtin_tone_${it.id}" },
                        contentType = { "ringtone_row" }
                    ) { spec ->
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
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                }

                SettingsSubTab.SYSTEM -> {
                    item(key = "system_language", contentType = "system_language") {
                        SystemLanguageCard(
                            selectedLang = state.appPrefs.lang,
                            onSelectLang = { viewModel.setLang(it) },
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    item(key = "system_clock", contentType = "system_clock") {
                        SystemClockFormatCard(
                            clockFormat = state.appPrefs.clockFormat,
                            clockSecondsFlow = viewModel.clockSecondsFlow,
                            onSelectFormat = { viewModel.setClockFormat(it) },
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                    item(key = "system_guide_about", contentType = "system_guide_about") {
                        SystemGuideAndAboutCard(
                            onOpenTutorial = { viewModel.openTutorial(startWithLang = false) },
                            onOpenAbout = { viewModel.openAboutDialog() },
                            modifier = if (noAnim) itemAnimMod else Modifier.animateItem()
                        )
                    }
                }
            }
        }

        // Pinned Settings Header & Sub-Tab Switcher
        SettingsPinnedHeader(
            activeSubTab = activeSubTab,
            onSelectSubTab = { activeSubTab = it },
            onBack = onBack,
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

    if (showCustomHexDialog) {
        CustomHexAccentDialog(
            initialHex = state.appPrefs.accentColorHex,
            lang = lang,
            onApply = { hex ->
                viewModel.setAccentColor(hex)
                showCustomHexDialog = false
            },
            onDismiss = { showCustomHexDialog = false }
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

@Composable
private fun SettingsPinnedHeader(
    activeSubTab: SettingsSubTab,
    onSelectSubTab: (SettingsSubTab) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens = tokens)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(tokens.buttonRadius),
                color = tokens.controlBg,
                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                modifier = Modifier.tactilePress(disabledByPerf = noAnim)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = lang.t("Kembali", "Back"),
                        tint = tokens.palette.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = lang.t("Kembali", "Back"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurface
                    )
                }
            }

            Text(
                text = lang.t("Pengaturan", "Settings"),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tokens.palette.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = tokens.accentColor.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.28f))
            ) {
                Text(
                    text = lang.t("ATUR", "SETUP"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = tokens.accentColor,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                )
            }
        }

        // 4-Tab Segmented Sub-Nav with fluid color crossfade
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(tokens.wellBg)
                .border(1.dp, tokens.subtleBorderColor, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                Triple(SettingsSubTab.PROFILE, lang.t("Profil", "Profile"), Icons.Rounded.Person),
                Triple(SettingsSubTab.APPEARANCE, lang.t("Tampilan", "Themes"), Icons.Rounded.Palette),
                Triple(SettingsSubTab.ALARM, lang.t("Alarm", "Alarm"), Icons.Rounded.Alarm),
                Triple(SettingsSubTab.SYSTEM, lang.t("Sistem", "System"), Icons.Rounded.Settings)
            ).forEach { (tab, label, icon) ->
                val selected = activeSubTab == tab
                val pillBg by animateColorAsState(
                    targetValue = if (selected) tokens.accentColor else Color.Transparent,
                    animationSpec = tween(if (noAnim) 0 else 180),
                    label = "settingsTabBg"
                )
                val pillFg by animateColorAsState(
                    targetValue = if (selected) tokens.onAccentColor else tokens.mutedTextColor,
                    animationSpec = tween(if (noAnim) 0 else 180),
                    label = "settingsTabFg"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .tactilePress(pressedScale = 0.94f, disabledByPerf = noAnim)
                        .clip(RoundedCornerShape(10.dp))
                        .background(pillBg)
                        .clickable { onSelectSubTab(tab) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = pillFg,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = pillFg,
                            maxLines = 1
                        )
                    }
                }
            }
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

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (state.isOffline) Color(0x26FBBF24) else Color(0x1E34D399),
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
                    Icon(
                        imageVector = if (state.isOffline) Icons.Rounded.CloudOff else Icons.Rounded.CloudDone,
                        contentDescription = null,
                        tint = if (state.isOffline) Color(0xFFFBBF24) else Color(0xFF6EE7B7),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (state.isOffline) lang.t("Offline", "Offline") else lang.t("Sinkron", "Synced"),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isOffline) Color(0xFFFBBF24) else Color(0xFF6EE7B7)
                    )
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

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
            shape = RoundedCornerShape(tokens.inputRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = tokens.accentColor,
                unfocusedBorderColor = tokens.surfaceBorderColor,
                focusedTextColor = tokens.palette.onSurface,
                unfocusedTextColor = tokens.palette.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val cleaned = nameDraft.trim()
                if (cleaned.isNotEmpty()) {
                    onSaveName(cleaned)
                    savedFeedback = true
                }
            },
            enabled = nameDraft.trim().isNotEmpty(),
            shape = RoundedCornerShape(tokens.buttonRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.accentColor,
                contentColor = tokens.onAccentColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .tactilePress(enabled = nameDraft.trim().isNotEmpty(), disabledByPerf = noAnim)
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
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
        OutlinedButton(
            onClick = onRequestSignOut,
            shape = RoundedCornerShape(tokens.buttonRadius),
            border = BorderStroke(1.dp, Color(0x66F87171)),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .tactilePress(disabledByPerf = noAnim)
        ) {
            Icon(Icons.Rounded.Logout, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = lang.t("Keluar Akun", "Sign Out"),
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF87171)
            )
        }
    }
}

@Composable
private fun AppearanceSectionSwitcher(
    section: AppearanceSection,
    onSelectSection: (AppearanceSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            Triple(AppearanceSection.THEMES, lang.t("Tema & Warna", "Themes & Colors"), Icons.Rounded.Palette),
            Triple(AppearanceSection.PERFORMANCE, lang.t("Performa", "Performance"), Icons.Rounded.Speed)
        ).forEach { (sec, label, icon) ->
            val active = section == sec
            val bgColor by animateColorAsState(
                targetValue = if (active) tokens.accentColor.copy(alpha = 0.16f) else tokens.palette.surfaceContainerLow,
                animationSpec = tween(if (noAnim) 0 else 180),
                label = "appSecBg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (active) tokens.accentColor else tokens.surfaceBorderColor,
                animationSpec = tween(if (noAnim) 0 else 180),
                label = "appSecBorder"
            )
            Surface(
                onClick = { onSelectSection(sec) },
                shape = RoundedCornerShape(tokens.buttonRadius),
                color = bgColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .weight(1f)
                    .tactilePress(pressedScale = 0.96f, disabledByPerf = noAnim)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (active) tokens.accentColor else tokens.palette.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) tokens.accentColor else tokens.palette.onSurface
                    )
                }
            }
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
                    text = lang.t("Gaya Antarmuka (3 Tema)", "Interface Style (3 Themes)"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = lang.t(
                        "Tema ringan & cepat tanpa efek berat.",
                        "Lightweight, fast themes built for smooth performance."
                    ),
                    fontSize = 11.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
            TextButton(onClick = onResetTheme) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(lang.t("Reset", "Reset"), fontSize = 12.sp, color = tokens.accentColor)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            UiTheme.entries.forEach { theme ->
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccentAndTintSettingsCard(
    accentColorHex: String,
    materialTintColorHex: String?,
    onSelectAccent: (String) -> Unit,
    onOpenCustomHex: () -> Unit,
    onSelectMaterialTint: (String?) -> Unit,
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
            Text(
                text = accentColorHex,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.accentColor
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GymModelHelpers.ACCENT_COLOR_PRESETS.forEach { opt ->
                val color = MaterialYouColorEngine.parseColor(opt.hex)
                val active = accentColorHex.equals(opt.hex, ignoreCase = true)
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
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = opt.labelEn,
                            tint = Color(0xFF0A0B0E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Surface(
                onClick = onOpenCustomHex,
                shape = RoundedCornerShape(999.dp),
                color = tokens.palette.surfaceContainerLow,
                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                modifier = Modifier.tactilePress(pressedScale = 0.94f, disabledByPerf = noAnim)
            ) {
                Text(
                    text = lang.t("# Kustom", "# Custom"),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                )
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = lang.t("Nuansa Permukaan (Material Tint)", "Material Surface Tint"),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
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
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = opt.labelEn,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        Text(
            text = lang.t("Palet Tonal Material You Aktif", "Live Material You Tonal Palette"),
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(swatchColor)
                            .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(8.dp))
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
            listOf(
                Triple(DayTimeMode.AUTO, lang.t("Auto", "Auto"), Color(0xFF34D399)),
                Triple(DayTimeMode.MORNING, lang.t("Pagi", "Morning"), Color(0xFFFBBF24)),
                Triple(DayTimeMode.AFTERNOON, lang.t("Siang", "Day"), Color(0xFF38BDF8)),
                Triple(DayTimeMode.NIGHT, lang.t("Malam", "Night"), Color(0xFFA5B4FC))
            ).forEach { (mode, label, dot) ->
                val active = selectedMode == mode
                Surface(
                    onClick = { onSelectMode(mode) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (active) dot.copy(alpha = 0.16f) else tokens.palette.surfaceContainerLow,
                    border = BorderStroke(1.dp, if (active) dot else tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .tactilePress(pressedScale = 0.95f, disabledByPerf = noAnim)
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
            Text(
                text = lang.t("Profil Performa Cepat", "Quick Performance Profiles"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
            if (activePreset == null) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = tokens.subtleChipBg,
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
                Surface(
                    onClick = { onApplyPreset(preset) },
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    color = if (isSelected) tokens.accentColor.copy(alpha = 0.16f) else tokens.palette.surfaceContainerLow,
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) tokens.accentColor else tokens.surfaceBorderColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .tactilePress(pressedScale = 0.95f, disabledByPerf = noAnim)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = tokens.accentColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) tokens.accentColor else tokens.palette.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        PerformanceSwitchRow(
            title = lang.t("Tanpa Animasi Gerak", "Disable Animations"),
            subtitle = lang.t("Transisi layar instan tanpa efek denyut.", "Instant screen transitions without pulse animations."),
            checked = perf.noAnimations,
            onCheckedChange = { onUpdatePerf(it, null, null) }
        )
        PerformanceSwitchRow(
            title = lang.t("Tanpa Cahaya Ambient", "Disable Ambient Glow"),
            subtitle = lang.t("Latar belakang solid bersih tanpa sorot radial.", "Clean solid background without radial glows."),
            checked = perf.noAmbientGlow,
            onCheckedChange = { onUpdatePerf(null, it, null) }
        )
        PerformanceSwitchRow(
            title = lang.t("Maskot Statis", "Static Mascot"),
            subtitle = lang.t("Hentikan gerakan mengambang & kedipan maskot Pulse.", "Pause Pulse mascot floating & blinking motion."),
            checked = perf.staticMascot,
            onCheckedChange = { onUpdatePerf(null, null, it) }
        )
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
    val previewAccent = when (theme) {
        UiTheme.UBUNTU_YARU -> Color(0xFFE95420)
        UiTheme.AMOLED_STEALTH -> if (accentColor == MaterialYouColorEngine.parseColor(MaterialYouColorEngine.DEFAULT_ACCENT_COLOR)) {
            Color(0xFF00E5FF)
        } else {
            accentColor
        }
        else -> accentColor
    }

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) previewAccent.copy(alpha = 0.14f) else tokens.controlBg,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) previewAccent else tokens.surfaceBorderColor
        ),
        modifier = modifier.tactilePress(pressedScale = 0.97f, disabledByPerf = tokens.performance.noAnimations)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val previewShape = RoundedCornerShape(
                when (theme) {
                    UiTheme.MATERIAL_YOU -> 14.dp
                    UiTheme.CARBON_FORGE -> 6.dp
                    UiTheme.UBUNTU_YARU -> 7.dp
                    UiTheme.AMOLED_STEALTH -> 10.dp
                }
            )
            Box(
                modifier = Modifier
                    .width(92.dp)
                    .height(54.dp)
                    .clip(previewShape)
                    .drawWithCache {
                        val strokePx = 1.dp.toPx()
                        val cellPx = (2.dp.toPx()).roundToInt().coerceIn(4, 8)
                        val grainScale = density.roundToInt().coerceIn(1, 3)
                        val matrixCellPx = (3.dp.toPx()).roundToInt().coerceIn(5, 10)
                        val baseBg = when (theme) {
                            UiTheme.MATERIAL_YOU -> Color(0xFF23202A)
                            UiTheme.CARBON_FORGE -> Color(0xFF101318)
                            UiTheme.UBUNTU_YARU -> Color(0xFF2C2C2C)
                            UiTheme.AMOLED_STEALTH -> Color(0xFF000000)
                        }
                        val shaderBrush = when (theme) {
                            UiTheme.CARBON_FORGE -> ThemeMaterialShaderCache.getCarbonTwillBrush(cellPx = cellPx, subtle = false)
                            UiTheme.UBUNTU_YARU -> ThemeMaterialShaderCache.getYaruMatteGrainBrush(pixelScale = grainScale, subtle = false)
                            UiTheme.AMOLED_STEALTH -> ThemeMaterialShaderCache.getAmoledStealthMatrixBrush(cellPx = matrixCellPx, subtle = false)
                            UiTheme.MATERIAL_YOU -> null
                        }
                        val finishBrush = when (theme) {
                            UiTheme.MATERIAL_YOU -> Brush.verticalGradient(
                                0.0f to previewAccent.copy(alpha = 0.08f),
                                1.0f to Color.Transparent
                            )
                            UiTheme.CARBON_FORGE -> Brush.verticalGradient(
                                0.0f to Color.White.copy(alpha = 0.07f),
                                0.45f to Color.Transparent,
                                1.0f to Color.Black.copy(alpha = 0.24f)
                            )
                            UiTheme.UBUNTU_YARU -> Brush.horizontalGradient(
                                listOf(Color(0x28E95420), Color.Transparent)
                            )
                            UiTheme.AMOLED_STEALTH -> Brush.verticalGradient(
                                0.0f to previewAccent.copy(alpha = 0.12f),
                                0.35f to Color.Transparent
                            )
                        }
                        val topRim = when (theme) {
                            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.20f)
                            UiTheme.UBUNTU_YARU -> Color.White.copy(alpha = 0.09f)
                            UiTheme.AMOLED_STEALTH -> previewAccent.copy(alpha = 0.45f)
                            UiTheme.MATERIAL_YOU -> Color.Transparent
                        }
                        onDrawBehind {
                            drawRect(baseBg)
                            if (shaderBrush != null) drawRect(shaderBrush)
                            drawRect(finishBrush)
                            if (topRim != Color.Transparent) {
                                drawLine(
                                    color = topRim,
                                    start = Offset(0f, strokePx * 0.5f),
                                    end = Offset(size.width, strokePx * 0.5f),
                                    strokeWidth = strokePx
                                )
                            }
                        }
                    }
                    .border(
                        1.dp,
                        when (theme) {
                            UiTheme.CARBON_FORGE -> Color.White.copy(alpha = 0.18f)
                            UiTheme.UBUNTU_YARU -> Color(0xFF141414)
                            UiTheme.AMOLED_STEALTH -> Color.White.copy(alpha = 0.16f)
                            UiTheme.MATERIAL_YOU -> previewAccent.copy(alpha = 0.28f)
                        },
                        previewShape
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.76f))
                        )
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.32f))
                        )
                    }
                    val pillShape = RoundedCornerShape(
                        when (theme) {
                            UiTheme.MATERIAL_YOU -> 999.dp
                            UiTheme.CARBON_FORGE -> 3.dp
                            UiTheme.UBUNTU_YARU -> 5.dp
                            UiTheme.AMOLED_STEALTH -> 6.dp
                        }
                    )
                    Box(
                        modifier = Modifier
                            .size(width = 22.dp, height = 14.dp)
                            .clip(pillShape)
                            .background(previewAccent)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = lang.t(theme.titleId, theme.titleEn),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = lang.t(theme.subtitleId, theme.subtitleEn),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = tokens.palette.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = lang.t("Dipilih", "Selected"),
                    tint = previewAccent,
                    modifier = Modifier.size(20.dp)
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
    onCheckedChange: (Boolean) -> Unit
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
            )
        )
    }
}

@Composable
private fun CustomHexAccentDialog(
    initialHex: String,
    lang: AppLanguage,
    onApply: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    var hexInput by remember(initialHex) { mutableStateOf(initialHex) }
    val valid = remember(hexInput) { Regex("^#[0-9A-Fa-f]{6}$").matches(hexInput.trim()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = tokens.palette.surfaceContainerHigh,
        modifier = Modifier.fluidModalEntrance(disabledByPerf = tokens.performance.noAnimations),
        title = {
            Text(
                text = lang.t("Warna Aksen Kustom (Hex)", "Custom Accent Hex"),
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { hexInput = it.uppercase(Locale.US).take(7) },
                    label = { Text("#RRGGBB") },
                    singleLine = true,
                    shape = RoundedCornerShape(tokens.inputRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.accentColor,
                        unfocusedBorderColor = tokens.surfaceBorderColor,
                        focusedTextColor = tokens.palette.onSurface,
                        unfocusedTextColor = tokens.palette.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (valid) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialYouColorEngine.parseColor(hexInput))
                        )
                        Text(lang.t("Pratinjau warna valid", "Valid color preview"), fontSize = 12.sp, color = tokens.palette.onSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(hexInput.trim()) },
                enabled = valid,
                shape = RoundedCornerShape(tokens.buttonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentColor,
                    contentColor = tokens.onAccentColor
                )
            ) {
                Text(lang.t("Terapkan", "Apply"), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(lang.t("Batal", "Cancel"), color = tokens.palette.onSurfaceVariant)
            }
        }
    )
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
                Icon(Icons.Rounded.VolumeUp, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(18.dp))
                Text(
                    text = lang.t("Volume & Pengatur Waktu", "Volume & Timing"),
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
            }
            Text(
                text = "${(volumeFloat * 100).toInt()}%",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = tokens.accentColor
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

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

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
                Surface(
                    onClick = { viewModel.updateProfile(snoozeMinutes = mins) },
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    color = if (active) tokens.accentColor else tokens.palette.surfaceContainerLow,
                    border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .tactilePress(pressedScale = 0.95f, disabledByPerf = noAnim)
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

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

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
            Icon(Icons.Rounded.Vibration, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(18.dp))
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
                Surface(
                    onClick = { viewModel.testHapticPattern() },
                    shape = RoundedCornerShape(999.dp),
                    color = tokens.controlBg,
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                    modifier = Modifier.tactilePress(pressedScale = 0.92f, disabledByPerf = noAnim)
                ) {
                    Text(
                        text = lang.t("Uji", "Test"),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.accentColor,
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

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

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
            Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(18.dp))
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
                    color = if (state.hasNotificationPermission) Color(0xFF34D399) else Color(0xFFFBBF24)
                )
            }
            if (!state.hasNotificationPermission) {
                Button(
                    onClick = onRequestNotificationPermission,
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentColor,
                        contentColor = tokens.onAccentColor
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(lang.t("Izinkan", "Allow"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
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
                    color = if (state.canScheduleExactAlarms) Color(0xFF34D399) else Color(0xFFFBBF24)
                )
            }
            OutlinedButton(
                onClick = onOpenExactAlarmSettings,
                shape = RoundedCornerShape(tokens.buttonRadius),
                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(lang.t("Atur", "Manage"), fontSize = 12.sp, color = tokens.palette.onSurface)
            }
        }

        Button(
            onClick = { viewModel.schedule5SecondNativeTestAlarm() },
            shape = RoundedCornerShape(tokens.buttonRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.accentColor,
                contentColor = tokens.onAccentColor
            ),
            modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
            Icon(Icons.Rounded.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
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
            Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(18.dp))
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

        OutlinedButton(
            onClick = onPickCustomRingtone,
            enabled = !uploading,
            shape = RoundedCornerShape(tokens.buttonRadius),
            border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.55f)),
            modifier = Modifier.fillMaxWidth().height(46.dp)
        ) {
            if (uploading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = tokens.accentColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(lang.t("Mengimpor Audio...", "Importing Audio..."), color = tokens.accentColor, fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Rounded.UploadFile, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(lang.t("Pilih File Audio dari HP", "Pick Audio File from Phone"), color = tokens.accentColor, fontWeight = FontWeight.Bold)
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
            IconButton(
                onClick = onPreview,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tokens.accentColor.copy(alpha = 0.16f))
            ) {
                Icon(
                    imageVector = if (isPreviewing) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    contentDescription = "Preview",
                    tint = tokens.accentColor,
                    modifier = Modifier.size(18.dp)
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
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete",
                    tint = Color(0xFFF87171),
                    modifier = Modifier.size(18.dp)
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
            IconButton(
                onClick = onPreview,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(tokens.accentColor.copy(alpha = 0.16f))
            ) {
                Icon(
                    imageVector = if (isPreviewing) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    contentDescription = "Preview",
                    tint = tokens.accentColor,
                    modifier = Modifier.size(18.dp)
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
                        color = tokens.accentColor
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
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (active) tokens.accentColor.copy(alpha = 0.16f) else tokens.wellBg,
        border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
        modifier = modifier.tactilePress(pressedScale = 0.95f, disabledByPerf = tokens.performance.noAnimations)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (active) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (active) tokens.accentColor else tokens.mutedTextColor,
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
private fun SystemLanguageCard(
    selectedLang: AppLanguage,
    onSelectLang: (AppLanguage) -> Unit,
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
            text = lang.t("Bahasa Aplikasi", "Application Language"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = tokens.palette.onSurface
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppLanguage.entries.forEach { option ->
                val active = selectedLang == option
                Surface(
                    onClick = { onSelectLang(option) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (active) tokens.accentColor.copy(alpha = 0.16f) else tokens.palette.surfaceContainerLow,
                    border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .tactilePress(pressedScale = 0.95f, disabledByPerf = noAnim)
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
                            color = if (active) tokens.accentColor else tokens.palette.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (active) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemClockFormatCard(
    clockFormat: ClockFormat,
    clockSecondsFlow: StateFlow<Long>,
    onSelectFormat: (ClockFormat) -> Unit,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = lang.t("Format Jam", "Clock Format"),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
            LiveClockPreviewBadge(
                clockSecondsFlow = clockSecondsFlow,
                clockFormat = clockFormat,
                lang = lang,
                accentColor = tokens.accentColor
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
                Surface(
                    onClick = { onSelectFormat(fmt) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (active) tokens.accentColor.copy(alpha = 0.16f) else tokens.palette.surfaceContainerLow,
                    border = BorderStroke(1.dp, if (active) tokens.accentColor else tokens.surfaceBorderColor),
                    modifier = Modifier
                        .weight(1f)
                        .tactilePress(pressedScale = 0.95f, disabledByPerf = noAnim)
                ) {
                    Text(
                        text = label,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) tokens.accentColor else tokens.palette.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemGuideAndAboutCard(
    onOpenTutorial: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironGlassSurface(tokens)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = lang.t("Panduan & Informasi", "Guide & Information"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = tokens.palette.onSurface
        )

        SystemActionRow(
            icon = Icons.Rounded.School,
            title = lang.t("Tur Interaktif Bersama Pulse", "Interactive Tour with Pulse"),
            subtitle = lang.t("Pulse memandumu keliling 5 tab, template jadwal & alarm.", "Let Pulse walk you through all 5 tabs, templates & alarms."),
            onClick = onOpenTutorial
        )

        SystemActionRow(
            icon = Icons.Rounded.Info,
            title = lang.t("Tentang IronPulse & Kredit", "About IronPulse & Credits"),
            subtitle = lang.t("Versi aplikasi, informasi rilis & kredit pengembang.", "App version, release notes & developer credits."),
            onClick = onOpenAbout
        )
    }
}

@Composable
private fun SystemActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = tokens.wellBg,
        border = BorderStroke(1.dp, tokens.subtleBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .tactilePress(pressedScale = 0.97f, disabledByPerf = tokens.performance.noAnimations)
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
                    .clip(RoundedCornerShape(10.dp))
                    .background(tokens.accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tokens.accentColor,
                    modifier = Modifier.size(18.dp)
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
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = tokens.mutedTextColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
