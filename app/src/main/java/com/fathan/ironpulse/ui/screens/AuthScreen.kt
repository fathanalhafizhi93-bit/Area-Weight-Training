package com.fathan.ironpulse.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.R
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.MascotExpression
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.MascotSpeechLine
import com.fathan.ironpulse.model.TimePhasePose
import com.fathan.ironpulse.model.UiTheme
import com.fathan.ironpulse.ui.components.IronPulseIcon
import com.fathan.ironpulse.ui.components.MascotOrbFace
import com.fathan.ironpulse.ui.components.ThemeBrandLockup
import com.fathan.ironpulse.ui.components.ThemedIcon
import com.fathan.ironpulse.ui.components.rememberScaledResourceBitmap
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl
import com.fathan.ironpulse.ui.viewmodel.GymViewModel
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    authLoading: Boolean,
    authError: String?,
    onSelectLang: (AppLanguage) -> Unit,
    onSignInClick: () -> Unit,
    mascot: MascotUiState? = null,
    mascotEyeFlow: StateFlow<MascotEyeState>? = null
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val isCli = tokens.uiTheme == UiTheme.TERMINAL_CLI
    val backdropBitmap = rememberScaledResourceBitmap(R.drawable.gym_arena_backdrop, 1024)

    var mascotTapCount by remember { mutableIntStateOf(0) }
    var isCoolingDown by remember { mutableStateOf(false) }

    LaunchedEffect(mascotTapCount) {
        if (mascotTapCount > 0) {
            isCoolingDown = true
            delay(GymViewModel.MASCOT_SPEECH_COOLDOWN_MS)
            isCoolingDown = false
        }
    }

    val loginPoses = remember {
        listOf(
            TimePhasePose.SALUTE,
            TimePhasePose.PERK,
            TimePhasePose.WINK,
            TimePhasePose.SPARKLE
        )
    }
    val loginExpressions = remember {
        listOf(
            MascotExpression.EXCITED,
            MascotExpression.PLAYFUL,
            MascotExpression.PROUD,
            MascotExpression.ENERGETIC
        )
    }

    val greetingLine = remember(lang, mascotTapCount) {
        when (mascotTapCount % 3) {
            0 -> MascotSpeechLine(
                id = "auth_welcome_0",
                headline = lang.t("Halo! Saya Pulse", "Hey! I'm Pulse"),
                subline = lang.t(
                    "Masuk dengan Google agar jadwal, alarm ringtone, dan medali bulananmu aman di Cloud.",
                    "Sign in with Google to keep your workouts, custom alarms, and monthly medals synced."
                ),
                expression = loginExpressions[mascotTapCount % loginExpressions.size]
            )
            1 -> MascotSpeechLine(
                id = "auth_welcome_1",
                headline = lang.t("Siap Bangun Kebiasaan!", "Ready to Build Consistency!"),
                subline = lang.t(
                    "Panduan 300+ gerakan anatomi, timer istirahat otomatis, dan medali bulanan menunggumu.",
                    "300+ anatomical exercises, automatic rest timers, and monthly medals are waiting for you."
                ),
                expression = loginExpressions[mascotTapCount % loginExpressions.size]
            )
            else -> MascotSpeechLine(
                id = "auth_welcome_2",
                headline = lang.t("Fokus Satu Ketukan!", "Focused From Day One!"),
                subline = lang.t(
                    "Atur jadwal gym, pilih ringtone alarm favorit, dan raih targetmu setiap minggu.",
                    "Set your training schedule, pick your alarm ringtone, and hit every weekly goal."
                ),
                expression = loginExpressions[mascotTapCount % loginExpressions.size]
            )
        }
    }

    // Smooth fade-out -> fade-in dialogue transition (no typewriter or cursor)
    var displayedGreeting by remember { mutableStateOf(greetingLine) }
    var lastRenderedTapCount by remember { mutableIntStateOf(0) }
    var lastRenderedLang by remember { mutableStateOf(lang) }
    val greetingAlpha = remember { Animatable(1f) }
    var isTalkingGreeting by remember { mutableStateOf(false) }

    LaunchedEffect(greetingLine, mascotTapCount, lang, noAnim) {
        val tapChanged = mascotTapCount != lastRenderedTapCount
        val langChanged = lang != lastRenderedLang
        if (!tapChanged && !langChanged) {
            displayedGreeting = greetingLine
            greetingAlpha.snapTo(1f)
            isTalkingGreeting = false
            return@LaunchedEffect
        }
        if (noAnim || langChanged) {
            displayedGreeting = greetingLine
            greetingAlpha.snapTo(1f)
            lastRenderedTapCount = mascotTapCount
            lastRenderedLang = lang
            isTalkingGreeting = false
            return@LaunchedEffect
        }
        isTalkingGreeting = true
        greetingAlpha.animateTo(0f, tween(durationMillis = 110, easing = FastOutSlowInEasing))
        displayedGreeting = greetingLine
        greetingAlpha.animateTo(1f, tween(durationMillis = 150, easing = FastOutSlowInEasing))
        lastRenderedTapCount = mascotTapCount
        lastRenderedLang = lang
        delay(380L)
        isTalkingGreeting = false
    }

    val displayMascot = remember(mascot, tokens.timePhase, mascotTapCount, displayedGreeting) {
        (mascot ?: MascotUiState()).copy(
            mood = MascotMood.READY,
            timePhase = tokens.timePhase,
            urgentMood = null,
            activeTimePose = loginPoses[mascotTapCount % loginPoses.size],
            phaseColorHex = tokens.phaseTokens.accentBrightHex,
            activeLine = displayedGreeting
        )
    }

    val chipShape = if (isCli) RectangleShape else RoundedCornerShape(999.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.canvasBg)
    ) {
        if (backdropBitmap != null) {
            Image(
                bitmap = backdropBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (tokens.isLightTheme) 0.10f else 0.34f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.canvasBg.copy(alpha = 0.78f),
                            tokens.canvasBg.copy(alpha = 0.88f),
                            tokens.canvasBg.copy(alpha = 0.97f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top header: IronPulse + ID/EN toggle (no version badge on login)
            Row(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemeBrandLockup(
                    theme = tokens.uiTheme,
                    accentColor = tokens.accentColor,
                    emblemSize = 30.dp,
                    fontSize = 21.sp
                )

                val toggleContainerShape = if (isCli) RectangleShape else RoundedCornerShape(12.dp)
                val toggleOptionShape = if (isCli) RectangleShape else RoundedCornerShape(8.dp)
                IronPulseTonalSurface(
                    shape = toggleContainerShape,
                    baseColor = tokens.wellBg.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(AppLanguage.ID to "ID", AppLanguage.EN to "EN").forEach { (option, label) ->
                            val selected = lang == option
                            Box(
                                modifier = Modifier
                                    .testTag("lang_toggle_${option.wireValue}")
                                    .tactilePress(pressedScale = 0.94f, disabledByPerf = noAnim)
                                    .ironTonalControl(
                                        tokens = tokens,
                                        shape = toggleOptionShape,
                                        baseColor = if (selected) tokens.accentColor else Color.Transparent,
                                        isAccent = selected
                                    )
                                    .clickable { onSelectLang(option) }
                                    .padding(horizontal = 11.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) tokens.onAccentColor else tokens.mutedTextColor
                                )
                            }
                        }
                    }
                }
            }

            // Minimalist Focused Hero Stage + Mascot + 3 Feature Pills + Google Sign-In CTA
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Live Mascot Hero Stage
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_mascot_greeting_card")
                        .tactilePress(pressedScale = 0.985f, disabledByPerf = noAnim)
                        .ironGlassSurface(
                            tokens = tokens,
                            subtle = true,
                            borderOverride = tokens.phaseTokens.accent.copy(alpha = 0.42f),
                            radiusOverride = if (isCli) 0.dp else 22.dp
                        )
                        .clickable {
                            if (!isCoolingDown) {
                                mascotTapCount += 1
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MascotOrbFace(
                        mascot = displayMascot,
                        eyeStateFlow = mascotEyeFlow,
                        size = 82.dp,
                        animActive = !noAnim,
                        isTalking = isTalkingGreeting && !noAnim,
                        modifier = Modifier.testTag("auth_mascot_orb")
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer { alpha = greetingAlpha.value },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = displayedGreeting.headline,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tokens.phaseTokens.accentBright,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = displayedGreeting.subline,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            color = tokens.palette.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text = if (isCoolingDown) {
                            lang.t("• • jeda sejenak", "• • pausing")
                        } else {
                            lang.t("Ketuk Pulse untuk sapaan lain", "Tap Pulse to say hi")
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCoolingDown) {
                            tokens.phaseTokens.accentBright.copy(alpha = 0.85f)
                        } else {
                            tokens.mutedTextColor
                        },
                        modifier = Modifier.testTag("auth_mascot_cooldown_hint")
                    )
                }

                Text(
                    text = lang.t(
                        "Latihan Terjadwal.\nProgres Terukur.",
                        "Scheduled Training.\nMeasured Progress."
                    ),
                    fontSize = 28.sp,
                    lineHeight = 33.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp,
                    color = tokens.palette.onSurface,
                    textAlign = TextAlign.Center
                )

                // 3 Concise Feature Highlights (300+ Anatomy, Medals, Alarm)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val highlights = listOf(
                        IronPulseIcon.EXERCISES to lang.t("300+ Gerakan & Anatomi", "300+ Moves & Anatomy"),
                        IronPulseIcon.SPARKLE to lang.t("Medali Bulanan", "Monthly Medals"),
                        IronPulseIcon.ALARM to lang.t("Alarm & Ringtone", "Alarm & Ringtone")
                    )
                    highlights.forEach { (icon, label) ->
                        IronPulseTonalSurface(
                            shape = chipShape,
                            baseColor = tokens.wellBg.copy(alpha = 0.90f),
                            border = BorderStroke(1.dp, tokens.subtleBorderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ThemedIcon(
                                    icon = icon,
                                    contentDescription = null,
                                    tint = tokens.accentTextColor,
                                    size = 14.dp
                                )
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = tokens.palette.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                IronPulseButton(
                    onClick = onSignInClick,
                    enabled = !authLoading,
                    shape = tokens.buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentColor,
                        contentColor = tokens.onAccentColor
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("google_sign_in_button")
                ) {
                    if (authLoading) {
                        CircularProgressIndicator(
                            color = tokens.onAccentColor,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = lang.t("Menghubungkan Akun...", "Connecting Account..."),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        ThemedIcon(
                            icon = IronPulseIcon.PERSON,
                            contentDescription = null,
                            size = 20.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = lang.t("Masuk dengan Akun Google", "Sign in with Google"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.5.sp
                        )
                    }
                }

                if (!authError.isNullOrBlank()) {
                    IronPulseTonalSurface(
                        shape = tokens.subtleCardShape,
                        baseColor = Color(0x22F87171),
                        border = BorderStroke(1.dp, Color(0x88F87171)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = authError,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFCA5A5),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            // Quiet Cloud Footer
            Text(
                text = lang.t(
                    "Jadwal latihan, catatan set, dan medali tersinkron aman di akun Cloud Anda.",
                    "Schedules, workout logs, and medals stay securely synced to your Cloud account."
                ),
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = tokens.mutedTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 460.dp)
            )
        }
    }
}
