package com.fathan.ironpulse.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Snooze
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.ActiveTestAlarmState
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NamePromptModal(
    initialName: String,
    mascot: MascotUiState,
    onSaveName: (String) -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    var nameInput by remember(initialName) { mutableStateOf(initialName) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    BackHandler { /* Name prompt requires entry */ }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth()
                .fluidModalEntrance(tokens.performance.noAnimations)
                .ironGlassSurface(
                    tokens = tokens,
                    borderOverride = tokens.accentColor.copy(alpha = 0.35f)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MascotOrbFace(mascot = mascot, size = 60.dp)
                Column {
                    Text(
                        text = lang.t("KENALAN DENGAN PULSE", "MEET PULSE"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = tokens.accentColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = lang.t("Siapa nama panggilanmu?", "What should I call you?"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = lang.t(
                    "Pulse akan menyapamu dengan nama ini dan memandumu mengenal IronPulse. Kamu bisa menggantinya kapan saja di Pengaturan.",
                    "Pulse will greet you by this name and guide you around IronPulse. You can change it anytime in Settings."
                ),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = tokens.palette.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = {
                    nameInput = it.take(100)
                    errorMsg = null
                },
                label = { Text(lang.t("Nama Panggilan", "Nickname / Display Name")) },
                placeholder = { Text(lang.t("Contoh: Fathan", "e.g. Alex")) },
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
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = errorMsg ?: "", fontSize = 12.sp, color = Color(0xFFFCA5A5))
            }

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    val cleaned = nameInput.trim()
                    if (cleaned.isEmpty()) {
                        errorMsg = lang.t("Masukkan nama panggilanmu terlebih dahulu.", "Please enter your name first.")
                    } else {
                        onSaveName(cleaned)
                    }
                },
                shape = RoundedCornerShape(tokens.buttonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentColor,
                    contentColor = tokens.onAccentColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(lang.t("Simpan & Lanjutkan", "Save & Continue"), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CooldownAlertModal(
    cooldownSecondsFlow: StateFlow<Int>,
    exerciseName: String,
    setNumber: Int,
    onBypass: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val cooldownRemaining by cooldownSecondsFlow.collectAsStateWithLifecycle()
    val progress = (cooldownRemaining.coerceIn(0, 30) / 30f)

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth()
                .fluidModalEntrance(tokens.performance.noAnimations)
                .ironGlassSurface(
                    tokens = tokens,
                    borderOverride = Color(0x73F59E0B)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Rounded.Warning, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                Text(
                    text = lang.t("PENGAMAN SALAH KLIK · 30 DETIK", "ANTI-MISCLICK GUARD · 30S"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.9.sp,
                    color = Color(0xFFFBBF24)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 7.dp.toPx()
                    drawArc(
                        color = Color.White.copy(alpha = 0.08f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(stroke)
                    )
                    drawArc(
                        color = Color(0xFFF59E0B),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$cooldownRemaining",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 40.sp,
                        color = tokens.accentColor
                    )
                    Text(
                        text = lang.t("d", "s"),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        color = tokens.palette.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = lang.t("Cegah Salah Klik Set!", "Hold On — Avoid Misclicks!"),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tokens.palette.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = lang.t(
                    "Jeda 30 detik mencegah klik ganda atau ketukan tidak sengaja pada \"$exerciseName\" Set #$setNumber. Gunakan Timer Istirahat untuk jeda antar set.",
                    "This 30s guard prevents accidental taps on \"$exerciseName\" Set #$setNumber. Use the Rest Timer for between-set recovery."
                ),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
                color = tokens.palette.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(lang.t("Tunggu ($cooldownRemaining d)", "Wait (${cooldownRemaining}s)"), color = tokens.palette.onSurface)
                }

                Button(
                    onClick = onBypass,
                    shape = RoundedCornerShape(tokens.buttonRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accentColor,
                        contentColor = tokens.onAccentColor
                    ),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(lang.t("Tetap Centang (Lewati)", "Confirm Anyway"), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val context = LocalContext.current
    val appVersionLabel = remember(context) {
        val raw = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty().ifBlank { "0.0.1-alpha" }
        "v${raw.replace('-', ' ')}"
    }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.56f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .fluidModalEntrance(tokens.performance.noAnimations)
                .ironGlassSurface(tokens)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "About IronPulse",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface
                    )
                    Text(
                        text = appVersionLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = tokens.accentColor
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.palette.onSurfaceVariant)
                }
            }

            // Credits Hero Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .ironGlassSurface(tokens, subtle = true, borderOverride = tokens.accentColor.copy(alpha = 0.3f))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = lang.t("KREDIT & PEMBUAT", "CREDITS & CREATORS"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.6.sp,
                    color = tokens.accentColor
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                AboutCreditRow(
                    label = "scripter",
                    value = "Fathan, and Artificial intelligence",
                    valueColor = tokens.palette.onSurface
                )
                AboutCreditRow(
                    label = "concept",
                    value = "fathan.",
                    valueColor = tokens.accentColor,
                    mono = true
                )
            }

            // About the App Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .ironGlassSurface(tokens, subtle = true)
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = lang.t("Tentang Aplikasi", "About the App"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurface
                )
                Text(
                    text = lang.t(
                        "IronPulse dirancang untuk membantu konsistensi jadwal latihan gym Anda setiap minggu, lengkap dengan alarm hari aktif, unggahan ringtone kustom, panduan daftar gerakan harian, catatan beban/set/repetisi, riwayat kalender sesi, serta timer istirahat antar set yang tersinkronisasi ke Cloud.",
                        "IronPulse is built to keep your weekly gym schedule consistent—featuring daily workout alarms, custom ringtones, daily movement plans, weight/set/rep logging, a workout history calendar, and cloud-synced rest timers."
                    ),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = tokens.palette.onSurfaceVariant
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Text(
                    text = listOf(
                        lang.t("Jadwal Mingguan", "Weekly Schedule"),
                        lang.t("Kalender Riwayat", "History Calendar"),
                        lang.t("Ringtone Kustom", "Custom Ringtones"),
                        lang.t("Alarm Background Android", "Android Background Alarms")
                    ).joinToString(" · "),
                    fontSize = 11.5.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AboutCreditRow(
    label: String,
    value: String,
    valueColor: Color,
    mono: Boolean = false
) {
    val tokens = LocalIronPulseTokens.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = tokens.palette.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (mono) FontFamily.Monospace else FontFamily.SansSerif,
            color = valueColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActiveAlarmOverlay(
    alarm: ActiveTestAlarmState,
    snoozeMinutes: Int,
    onStartWorkout: () -> Unit,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val infinite = rememberInfiniteTransition(label = "inAppAlarm")
    val pulse by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.09f,
        animationSpec = infiniteRepeatable(tween(750), RepeatMode.Reverse),
        label = "pulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(tokens.canvasBg.copy(alpha = 0.96f))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = lang.t("SIMULASI ALARM LATIHAN", "WORKOUT ALARM PREVIEW"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp,
                        color = tokens.accentColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = alarm.timeLabel,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 44.sp,
                        color = tokens.palette.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(144.dp)
                            .scale(if (tokens.performance.noAnimations) 1f else pulse)
                            .background(tokens.accentColor.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(98.dp)
                                .background(tokens.accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Alarm,
                                contentDescription = null,
                                tint = tokens.onAccentColor,
                                modifier = Modifier.size(46.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = alarm.focusTitle,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = tokens.palette.surfaceContainerHigh,
                        border = BorderStroke(1.dp, tokens.surfaceBorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = tokens.accentColor, modifier = Modifier.size(14.dp))
                            Text(alarm.ringtoneName, fontSize = 12.sp, color = tokens.palette.onSurface)
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onStartWorkout,
                        shape = RoundedCornerShape(tokens.buttonRadius),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentColor,
                            contentColor = tokens.onAccentColor
                        ),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Icon(Icons.Rounded.FitnessCenter, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(lang.t("Matikan & Mulai Latihan", "Dismiss & Start Workout"), fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onSnooze,
                        shape = RoundedCornerShape(tokens.buttonRadius),
                        border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Rounded.Snooze, contentDescription = null, tint = tokens.palette.onSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.EN) "Snooze $snoozeMinutes Min" else "Tunda $snoozeMinutes Menit",
                            color = tokens.palette.onSurface
                        )
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(lang.t("Tutup", "Dismiss"), color = tokens.palette.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
