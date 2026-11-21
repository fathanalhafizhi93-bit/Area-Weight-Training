package com.fathan.ironpulse.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.R
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.MascotSpeechLine
import com.fathan.ironpulse.model.TimePhasePose
import com.fathan.ironpulse.ui.components.MascotOrbFace
import com.fathan.ironpulse.ui.components.rememberScaledResourceBitmap
import com.fathan.ironpulse.ui.components.tactilePress
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
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
    val backdropBitmap = rememberScaledResourceBitmap(R.drawable.gym_arena_backdrop, 1024)
    var mascotTapCount by remember { mutableIntStateOf(0) }

    val loginPoses = remember {
        listOf(
            TimePhasePose.SALUTE,
            TimePhasePose.PERK,
            TimePhasePose.WINK,
            TimePhasePose.SPARKLE
        )
    }
    val greetingPair = remember(lang, mascotTapCount) {
        val idx = mascotTapCount % 3
        when (idx) {
            0 -> lang.t("Halo! Saya Pulse", "Hey! I'm Pulse") to lang.t(
                "Masuk dengan akun Google agar jadwal latihan, ringtone, dan progresmu aman di Cloud.",
                "Sign in with Google so your workout schedule, ringtones, and progress stay safely synced."
            )
            1 -> lang.t("Siap Bangun Kebiasaan!", "Ready to Build Consistency!") to lang.t(
                "Alarm harian tepat waktu, timer istirahat otomatis, dan panduan 120+ gerakan menunggumu.",
                "On-time workout alarms, auto rest timers, and a 120+ exercise library are ready for you."
            )
            else -> lang.t("Ketuk Aku Lagi Kapan Saja!", "Tap Me Anytime!") to lang.t(
                "Aku akan menemani dan menyemangatimu setiap set latihan di tab Hari Ini.",
                "I'll cheer you on through every set once you sign in."
            )
        }
    }

    val displayMascot = remember(mascot, tokens.timePhase, mascotTapCount, greetingPair) {
        (mascot ?: MascotUiState()).copy(
            mood = MascotMood.READY,
            timePhase = tokens.timePhase,
            urgentMood = null,
            activeTimePose = loginPoses[mascotTapCount % loginPoses.size],
            phaseColorHex = tokens.phaseTokens.accentBrightHex,
            activeLine = MascotSpeechLine(
                id = "auth_welcome_$mascotTapCount",
                headline = greetingPair.first,
                subline = greetingPair.second
            )
        )
    }

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
                    .alpha(0.42f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            tokens.canvasBg.copy(alpha = 0.72f),
                            tokens.canvasBg.copy(alpha = 0.82f),
                            tokens.canvasBg.copy(alpha = 0.96f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            tokens.canvasBg.copy(alpha = 0.92f),
                            tokens.canvasBg.copy(alpha = 0.72f),
                            tokens.canvasBg.copy(alpha = 0.45f)
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top header: IronPulse + ID/EN toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFFF4F5F7), fontWeight = FontWeight.ExtraBold)) {
                            append("Iron")
                        }
                        withStyle(SpanStyle(color = tokens.accentColor, fontWeight = FontWeight.ExtraBold)) {
                            append("Pulse")
                        }
                    },
                    fontSize = 24.sp,
                    letterSpacing = (-0.5).sp
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.wellBg.copy(alpha = 0.85f),
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) tokens.accentColor else Color.Transparent)
                                    .clickable { onSelectLang(option) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) tokens.onAccentColor else tokens.mutedTextColor
                                )
                            }
                        }
                    }
                }
            }

            // Main Hero Content + Welcoming IronPulse Mascot
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Mascot Welcome Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_mascot_greeting_card")
                        .tactilePress(pressedScale = 0.985f, disabledByPerf = noAnim)
                        .ironGlassSurface(
                            tokens = tokens,
                            subtle = true,
                            borderOverride = tokens.phaseTokens.accent.copy(alpha = 0.36f),
                            radiusOverride = 18.dp
                        )
                        .clickable { mascotTapCount += 1 }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    MascotOrbFace(
                        mascot = displayMascot,
                        eyeStateFlow = mascotEyeFlow,
                        size = 58.dp,
                        animActive = !noAnim,
                        modifier = Modifier.testTag("auth_mascot_orb")
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = displayMascot.activeLine.headline,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tokens.phaseTokens.accentBright
                        )
                        Text(
                            text = displayMascot.activeLine.subline,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFFD5DCE6)
                        )
                    }
                }

                Text(
                    text = lang.t(
                        "GYM REMINDER & PELACAK LATIHAN",
                        "GYM REMINDER & WORKOUT TRACKER"
                    ),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = tokens.accentColor
                )

                Text(
                    text = lang.t(
                        "Disiplin Jadwal Latihan, Ringtone Pilihan Sendiri, dan Catatan Progres Nyata.",
                        "Disciplined Training Schedules, Custom Ringtones, and Real Set Tracking."
                    ),
                    fontSize = 34.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp,
                    color = Color(0xFFF4F5F7)
                )

                Text(
                    text = lang.t(
                        "Atur hari gym Anda dari Senin hingga Minggu, unggah ringtone pengingat favorit Anda, pantau daftar gerakan hari ini, catat beban dan repetisi tiap set, serta jalankan timer istirahat otomatis dalam satu ruang kerja.",
                        "Configure your weekly training days Monday through Sunday, upload custom reminder ringtones, track today's exercises with weight and reps, and run automatic rest timers in one focused workspace."
                    ),
                    fontSize = 14.5.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFFB5BCC9)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val highlights = listOf(
                        lang.t("Jadwal Hari Kustom", "Custom Weekly Days"),
                        lang.t("Upload MP3/WAV Ringtone", "Custom MP3/WAV Ringtones"),
                        lang.t("Catatan Set & Beban", "Set & Load Logging"),
                        lang.t("Timer Istirahat", "Rest Timer")
                    )
                    highlights.forEachIndexed { idx, item ->
                        Text(
                            text = if (idx == 0) item else "·  $item",
                            fontSize = 13.sp,
                            color = tokens.mutedTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onSignInClick,
                    enabled = !authLoading,
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentColor,
                        contentColor = tokens.onAccentColor
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                    modifier = Modifier
                        .testTag("google_sign_in_button")
                        .height(52.dp)
                        .tactilePress(enabled = !authLoading, disabledByPerf = noAnim)
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
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = lang.t("Masuk dengan Akun Google", "Sign in with Google"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                if (!authError.isNullOrBlank()) {
                    Text(
                        text = authError,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF87171)
                    )
                }
            }

            // Quiet Footer
            Text(
                text = lang.t(
                    "Data jadwal latihan, daftar gerakan, dan catatan set tersimpan aman di akun Cloud Anda.",
                    "Your schedules, exercise routines, and workout logs are securely synced to your Cloud account."
                ),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}
