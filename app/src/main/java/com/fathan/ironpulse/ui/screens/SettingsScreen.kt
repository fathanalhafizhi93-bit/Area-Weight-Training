package com.fathan.ironpulse.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.ClockFormat
import com.fathan.ironpulse.model.CustomRingtone
import com.fathan.ironpulse.model.DayTimeMode
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.PerformancePreferences
import com.fathan.ironpulse.model.PerformancePresetId
import com.fathan.ironpulse.model.RingtoneStorageMode
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.components.ConfirmDeleteDialog
import com.fathan.ironpulse.ui.components.IronPulseMotion
import com.fathan.ironpulse.ui.components.fluidModalEntrance
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.theme.CarbonTwillShaderCache
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.MaterialYouColorEngine
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.IronPulseUiState
import kotlinx.coroutines.flow.StateFlow
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
    onPickCustomRingtone: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    var activeSubTab by remember { mutableStateOf(SettingsSubTab.PROFILE) }
    var appearanceSection by remember { mutableStateOf(AppearanceSection.THEMES) }
    var confirmSignOut by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 36.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Compact Header + Segmented Settings Sub-Nav
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .ironGlassSurface(tokens)
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
                            animationSpec = androidx.compose.animation.core.tween(if (noAnim) 0 else 180),
                            label = "settingsTabBg"
                        )
                        val pillFg by animateColorAsState(
                            targetValue = if (selected) tokens.onAccentColor else tokens.mutedTextColor,
                            animationSpec = androidx.compose.animation.core.tween(if (noAnim) 0 else 180),
                            label = "settingsTabFg"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .tactilePress(pressedScale = 0.94f, disabledByPerf = noAnim)
                                .clip(RoundedCornerShape(10.dp))
                                .background(pillBg)
                                .clickable { activeSubTab = tab }
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

        item {
            AnimatedContent(
                targetState = activeSubTab,
                transitionSpec = {
                    if (noAnim) {
                        ContentTransform(EnterTransition.None, ExitTransition.None)
                    } else {
                        val forward = targetState.ordinal >= initialState.ordinal
                        (slideInHorizontally(animationSpec = IronPulseMotion.TabSlideSpring) { w ->
                            if (forward) (w * 0.10f).toInt() else -(w * 0.10f).toInt()
                        } + fadeIn(IronPulseMotion.ShortFadeSpec)) togetherWith
                            (slideOutHorizontally(animationSpec = IronPulseMotion.TabSlideSpring) { w ->
                                if (forward) -(w * 0.08f).toInt() else (w * 0.08f).toInt()
                            } + fadeOut(IronPulseMotion.ShortFadeSpec))
                    }
                },
                label = "settingsSubTabContent"
            ) { currentSubTab ->
                when (currentSubTab) {
                    SettingsSubTab.PROFILE -> ProfileSettingsSection(
                        state = state,
                        onSaveName = { viewModel.updateProfile(displayName = it) },
                        onRequestSignOut = { confirmSignOut = true }
                    )

                    SettingsSubTab.APPEARANCE -> AppearanceSettingsSection(
                        state = state,
                        section = appearanceSection,
                        onSelectSection = { appearanceSection = it },
                        viewModel = viewModel
                    )

                    SettingsSubTab.ALARM -> AlarmAndRingtoneSettingsSection(
                        state = state,
                        viewModel = viewModel,
                        onRequestNotificationPermission = onRequestNotificationPermission,
                        onOpenExactAlarmSettings = onOpenExactAlarmSettings,
                        onPickCustomRingtone = onPickCustomRingtone
                    )

                    SettingsSubTab.SYSTEM -> SystemSettingsSection(
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
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
private fun ProfileSettingsSection(
    state: IronPulseUiState,
    onSaveName: (String) -> Unit,
    onRequestSignOut: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val profile = state.profile
    val rawName = profile?.displayName?.takeIf { it.isNotBlank() }
        ?: (profile?.email ?: state.userEmail)?.substringBefore("@")
        ?: lang.t("Atlet", "Athlete")
    val avatarInitial = rawName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "A"
    var nameDraft by remember(profile?.displayName) { mutableStateOf(profile?.displayName ?: "") }
    var savedFeedback by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Athlete Identity & Cloud Sync Hero Card
        Column(
            modifier = Modifier
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

        // Display Name Edit Card
        Column(
            modifier = Modifier
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

        // Session / Sign Out Card
        Column(
            modifier = Modifier
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AppearanceSettingsSection(
    state: IronPulseUiState,
    section: AppearanceSection,
    onSelectSection: (AppearanceSection) -> Unit,
    viewModel: GymViewModel
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val prefs = state.appPrefs
    var showCustomHexDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Nested switcher: Tema & Warna vs Performa
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple(AppearanceSection.THEMES, lang.t("Tema & Warna", "Themes & Colors"), Icons.Rounded.Palette),
                Triple(AppearanceSection.PERFORMANCE, lang.t("Performa", "Performance"), Icons.Rounded.Speed)
            ).forEach { (sec, label, icon) ->
                val active = section == sec
                val bgColor by animateColorAsState(
                    targetValue = if (active) tokens.accentColor.copy(alpha = 0.16f) else tokens.palette.surfaceContainerLow,
                    animationSpec = androidx.compose.animation.core.tween(if (noAnim) 0 else 180),
                    label = "appSecBg"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (active) tokens.accentColor else tokens.surfaceBorderColor,
                    animationSpec = androidx.compose.animation.core.tween(if (noAnim) 0 else 180),
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

        AnimatedContent(
            targetState = section,
            transitionSpec = {
                if (noAnim) {
                    ContentTransform(EnterTransition.None, ExitTransition.None)
                } else {
                    val forward = targetState.ordinal >= initialState.ordinal
                    (slideInHorizontally(animationSpec = IronPulseMotion.TabSlideSpring) { w ->
                        if (forward) (w * 0.10f).toInt() else -(w * 0.10f).toInt()
                    } + fadeIn(IronPulseMotion.ShortFadeSpec)) togetherWith
                        (slideOutHorizontally(animationSpec = IronPulseMotion.TabSlideSpring) { w ->
                            if (forward) -(w * 0.08f).toInt() else (w * 0.08f).toInt()
                        } + fadeOut(IronPulseMotion.ShortFadeSpec))
                }
            },
            label = "appearanceSubSection"
        ) { currentSection ->
            if (currentSection == AppearanceSection.THEMES) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 6 Themes Grid (2 columns via chunked rows)
                    Column(
                        modifier = Modifier
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
                                text = lang.t("Gaya Antarmuka (6 Tema)", "Interface Style (6 Themes)"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.palette.onSurface
                            )
                            TextButton(onClick = { viewModel.resetThemeCustomization() }) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(lang.t("Reset", "Reset"), fontSize = 12.sp, color = tokens.accentColor)
                            }
                        }

                        UiTheme.entries.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                pair.forEach { theme ->
                                    ThemePreviewCard(
                                        theme = theme,
                                        selected = prefs.uiTheme == theme,
                                        lang = lang,
                                        accentColor = tokens.accentColor,
                                        onSelect = { viewModel.setUiTheme(theme) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    // Accent Color Swatches + Custom Hex + Surface Tint + Live Tonal Strip
                    Column(
                        modifier = Modifier
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
                                text = prefs.accentColorHex,
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
                                val active = prefs.accentColorHex.equals(opt.hex, ignoreCase = true)
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
                                        .clickable { viewModel.setAccentColor(opt.hex) },
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
                                onClick = { showCustomHexDialog = true },
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

                        // Material Surface Tint
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
                            TextButton(onClick = { viewModel.setMaterialTintColor(null) }) {
                                Text(
                                    text = lang.t("Bawaan Tema", "Theme Default"),
                                    fontSize = 11.5.sp,
                                    color = if (prefs.materialTintColorHex == null) tokens.accentColor else tokens.palette.onSurfaceVariant
                                )
                            }
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GymModelHelpers.MATERIAL_TINT_PRESETS.forEach { opt ->
                                val color = MaterialYouColorEngine.parseColor(opt.hex)
                                val active = prefs.materialTintColorHex.equals(opt.hex, ignoreCase = true)
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
                                        .clickable { viewModel.setMaterialTintColor(opt.hex) },
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

                        // Live Material You Tonal Palette Strip
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

                    // Day Time Phase Override
                    Column(
                        modifier = Modifier
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
                                val active = prefs.todayThemeMode == mode
                                Surface(
                                    onClick = { viewModel.setTodayThemeMode(mode) },
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
            } else {
                // Performance Profiles & 4 Independent Switches
                val activePreset: PerformancePresetId? = when (prefs.performance) {
                    PerformancePreferences() -> PerformancePresetId.BALANCED
                    PerformancePreferences(noBlur = true, noAnimations = false, noAmbientGlow = true, staticMascot = false) -> PerformancePresetId.BATTERY
                    PerformancePreferences(noBlur = true, noAnimations = true, noAmbientGlow = true, staticMascot = true) -> PerformancePresetId.ULTRA
                    else -> null
                }

                Column(
                    modifier = Modifier
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
                                onClick = { viewModel.applyPerformancePreset(preset) },
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
                        title = lang.t("Tanpa Efek Blur", "Disable Blur"),
                        subtitle = lang.t("Mengurangi beban GPU di perangkat hemat daya.", "Reduces GPU cost on battery-saver devices."),
                        checked = prefs.performance.noBlur,
                        onCheckedChange = { viewModel.setPerformance(noBlur = it) }
                    )
                    PerformanceSwitchRow(
                        title = lang.t("Tanpa Animasi Gerak", "Disable Animations"),
                        subtitle = lang.t("Transisi layar instan tanpa efek denyut.", "Instant screen transitions without pulse animations."),
                        checked = prefs.performance.noAnimations,
                        onCheckedChange = { viewModel.setPerformance(noAnimations = it) }
                    )
                    PerformanceSwitchRow(
                        title = lang.t("Tanpa Cahaya Ambient", "Disable Ambient Glow"),
                        subtitle = lang.t("Latar belakang solid bersih tanpa sorot radial.", "Clean solid background without radial glows."),
                        checked = prefs.performance.noAmbientGlow,
                        onCheckedChange = { viewModel.setPerformance(noAmbientGlow = it) }
                    )
                    PerformanceSwitchRow(
                        title = lang.t("Maskot Statis", "Static Mascot"),
                        subtitle = lang.t("Hentikan gerakan mengambang & kedipan maskot Pulse.", "Pause Pulse mascot floating & blinking motion."),
                        checked = prefs.performance.staticMascot,
                        onCheckedChange = { viewModel.setPerformance(staticMascot = it) }
                    )
                }
            }
        }
    }

    if (showCustomHexDialog) {
        CustomHexAccentDialog(
            initialHex = prefs.accentColorHex,
            lang = lang,
            onApply = { hex ->
                viewModel.setAccentColor(hex)
                showCustomHexDialog = false
            },
            onDismiss = { showCustomHexDialog = false }
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
    val previewAccent = if (theme == UiTheme.UBUNTU_YARU) Color(0xFFE95420) else accentColor

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) previewAccent.copy(alpha = 0.14f) else tokens.controlBg,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) previewAccent else tokens.surfaceBorderColor
        ),
        modifier = modifier.tactilePress(pressedScale = 0.96f, disabledByPerf = tokens.performance.noAnimations)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Miniature Theme Visual Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(if (theme == UiTheme.CARBON_FORGE) 6.dp else 10.dp))
                    .background(
                        when (theme) {
                            UiTheme.MATERIAL_YOU -> Color(0xFF1D1B22)
                            UiTheme.OBSIDIAN -> Color(0xFF12151C)
                            UiTheme.MIDNIGHT_FROSTED -> Color(0xFF152036)
                            UiTheme.CARBON_FORGE -> Color(0xFF101318)
                            UiTheme.AEROGLASS -> Color(0xFF13293D)
                            UiTheme.UBUNTU_YARU -> Color(0xFF2C2C2C)
                        }
                    )
                    .border(
                        1.dp,
                        when (theme) {
                            UiTheme.AEROGLASS -> Color(0x70BAE6FD)
                            UiTheme.MIDNIGHT_FROSTED -> Color.White.copy(alpha = 0.25f)
                            else -> Color.White.copy(alpha = 0.14f)
                        },
                        RoundedCornerShape(if (theme == UiTheme.CARBON_FORGE) 6.dp else 10.dp)
                    )
            ) {
                if (theme == UiTheme.CARBON_FORGE) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithCache {
                                val strokePx = 1.dp.toPx()
                                val cellPx = (2.dp.toPx()).roundToInt().coerceIn(4, 8)
                                val twillBrush = CarbonTwillShaderCache.getBrush(cellPx = cellPx, subtle = false)
                                val sheen = Brush.verticalGradient(
                                    0.0f to Color.White.copy(alpha = 0.07f),
                                    0.45f to Color.Transparent,
                                    1.0f to Color.Black.copy(alpha = 0.24f)
                                )
                                val rim = Color.White.copy(alpha = 0.18f)
                                onDrawBehind {
                                    drawRect(twillBrush)
                                    drawRect(sheen)
                                    drawLine(
                                        color = rim,
                                        start = Offset(0f, strokePx * 0.5f),
                                        end = Offset(size.width, strokePx * 0.5f),
                                        strokeWidth = strokePx
                                    )
                                }
                            }
                    )
                } else if (theme == UiTheme.AEROGLASS) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.26f), Color.Transparent)
                                )
                            )
                    )
                }

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
                                .width(42.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.72f))
                        )
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.30f))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(width = 24.dp, height = 14.dp)
                            .clip(RoundedCornerShape(if (theme == UiTheme.MATERIAL_YOU || theme == UiTheme.MIDNIGHT_FROSTED) 999.dp else 4.dp))
                            .background(previewAccent)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lang.t(theme.titleId, theme.titleEn),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (selected) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = previewAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = lang.t(theme.subtitleId, theme.subtitleEn),
                fontSize = 10.5.sp,
                lineHeight = 14.sp,
                color = tokens.palette.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
private fun AlarmAndRingtoneSettingsSection(
    state: IronPulseUiState,
    viewModel: GymViewModel,
    onRequestNotificationPermission: () -> Unit,
    onOpenExactAlarmSettings: () -> Unit,
    onPickCustomRingtone: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val profile = state.profile
    val volumeFloat = (profile?.volume ?: 0.85).toFloat().coerceIn(0.1f, 1.0f)
    val snoozeMinutes = profile?.snoozeMinutes ?: 5
    val autoStartRest = profile?.autoStartRestTimer ?: true
    val notificationsEnabled = profile?.notificationsEnabled ?: true
    val defaultAlarmId = profile?.defaultRingtoneId ?: "builtin_athletic_bell"
    val restRingtoneId = profile?.restRingtoneId ?: "builtin_kettlebell_chime"

    var pendingDeleteTone by remember { mutableStateOf<CustomRingtone?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Card 1: Audio Volume, Snooze & Rest Automation
        Column(
            modifier = Modifier
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
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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

        // Card 2: Tactile Feedback & Mascot Voice
        Column(
            modifier = Modifier
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

        // Android Background Alarm Diagnostics Card
        Column(
            modifier = Modifier
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

        // Ringtone Manager Card (Built-in 5 + Custom Upload + Per-Use)
        Column(
            modifier = Modifier
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
                enabled = !state.uploadingRingtone,
                shape = RoundedCornerShape(tokens.buttonRadius),
                border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                if (state.uploadingRingtone) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = tokens.accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(lang.t("Mengimpor Audio...", "Importing Audio..."), color = tokens.accentColor, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Rounded.UploadFile, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(lang.t("Pilih File Audio dari HP", "Pick Audio File from Phone"), color = tokens.accentColor, fontWeight = FontWeight.Bold)
                }
            }

            if (state.customRingtones.isNotEmpty()) {
                Text(
                    text = lang.t("Nada Kustom Saya (${state.customRingtones.size})", "My Custom Ringtones (${state.customRingtones.size})"),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurfaceVariant
                )

                state.customRingtones.forEach { tone ->
                    val isPreviewing = state.previewingRingtoneId == tone.id
                    val isAlarmDefault = defaultAlarmId == tone.id
                    val isRestDefault = restRingtoneId == tone.id
                    val sizeKb = (tone.sizeBytes / 1024L).coerceAtLeast(1L)

                    Column(
                        modifier = Modifier
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
                                onClick = { viewModel.previewRingtone(tone.id) },
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
                                onClick = { pendingDeleteTone = tone },
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
                                onClick = { viewModel.updateProfile(defaultRingtoneId = tone.id) },
                                modifier = Modifier.weight(1f)
                            )
                            RingtoneAssignChip(
                                label = lang.t("Nada Istirahat", "Rest Chime"),
                                active = isRestDefault,
                                onClick = { viewModel.updateProfile(restRingtoneId = tone.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            Text(
                text = lang.t("Nada Bawaan IronPulse (5 Synth)", "Built-in Synth Ringtones (5)"),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurfaceVariant
            )

            GymModelHelpers.BUILTIN_RINGTONES.forEach { spec ->
                val isPreviewing = state.previewingRingtoneId == spec.id
                val isAlarmDefault = defaultAlarmId == spec.id
                val isRestDefault = restRingtoneId == spec.id

                Column(
                    modifier = Modifier
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
                            onClick = { viewModel.previewRingtone(spec.id) },
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
                            onClick = { viewModel.updateProfile(defaultRingtoneId = spec.id) },
                            modifier = Modifier.weight(1f)
                        )
                        RingtoneAssignChip(
                            label = lang.t("Nada Istirahat", "Rest Chime"),
                            active = isRestDefault,
                            onClick = { viewModel.updateProfile(restRingtoneId = spec.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
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
    val clockSeconds by clockSecondsFlow.collectAsStateWithLifecycle()
    val livePreviewTime = remember(clockSeconds, clockFormat, lang) {
        GymModelHelpers.formatAlarmTime(
            GymModelHelpers.formatHHMM(Date(clockSeconds * 1000L)),
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
private fun SystemSettingsSection(
    state: IronPulseUiState,
    viewModel: GymViewModel
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val prefs = state.appPrefs

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Language Picker
        Column(
            modifier = Modifier
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
                    val active = prefs.lang == option
                    Surface(
                        onClick = { viewModel.setLang(option) },
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

        // Clock Format Picker
        Column(
            modifier = Modifier
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
                    clockSecondsFlow = viewModel.clockSecondsFlow,
                    clockFormat = prefs.clockFormat,
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
                    val active = prefs.clockFormat == fmt
                    Surface(
                        onClick = { viewModel.setClockFormat(fmt) },
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

        // Tutorial & About Action Rows
        Column(
            modifier = Modifier
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
                title = lang.t("Panduan Fitur IronPulse", "Feature Walkthrough"),
                subtitle = lang.t("Pelajari 5 alur utama jadwal, set, dan maskot.", "Review the 5 core flows: schedule, sets & mascot."),
                onClick = { viewModel.openTutorial(startWithLang = false) }
            )

            SystemActionRow(
                icon = Icons.Rounded.Info,
                title = lang.t("Tentang IronPulse & Kredit", "About IronPulse & Credits"),
                subtitle = lang.t("Versi aplikasi, informasi rilis & kredit pengembang.", "App version, release notes & developer credits."),
                onClick = { viewModel.openAboutDialog() }
            )
        }
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
