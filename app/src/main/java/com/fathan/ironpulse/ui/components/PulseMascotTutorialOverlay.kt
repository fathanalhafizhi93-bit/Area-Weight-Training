package com.fathan.ironpulse.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.MascotMood
import com.fathan.ironpulse.model.TimePhasePose
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.viewmodel.AppTab
import com.fathan.ironpulse.ui.viewmodel.MascotEyeState
import com.fathan.ironpulse.ui.viewmodel.MascotUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

enum class TutorialSpotlightTarget {
    TODAY_REST_TIMER,
    SCHEDULE_PROGRAM_STRIP,
    EXERCISES_HEADER,
    HISTORY_CALENDAR,
    SETTINGS_HEADER
}

class TutorialSpotlightRegistry {
    val bounds = mutableStateMapOf<TutorialSpotlightTarget, Rect>()
    var active: Boolean = false

    fun update(target: TutorialSpotlightTarget, rect: Rect) {
        if (!active) return
        if (rect.width <= 4f || rect.height <= 4f) return
        val existing = bounds[target]
        if (existing == null ||
            kotlin.math.abs(existing.left - rect.left) > 1.5f ||
            kotlin.math.abs(existing.top - rect.top) > 1.5f ||
            kotlin.math.abs(existing.width - rect.width) > 1.5f ||
            kotlin.math.abs(existing.height - rect.height) > 1.5f
        ) {
            bounds[target] = rect
        }
    }
}

val LocalTutorialSpotlightRegistry = staticCompositionLocalOf<TutorialSpotlightRegistry?> { null }

fun Modifier.tutorialSpotlightTarget(target: TutorialSpotlightTarget): Modifier = composed {
    val registry = LocalTutorialSpotlightRegistry.current
    if (registry == null) {
        this
    } else {
        this.onGloballyPositioned { coords ->
            if (registry.active && coords.isAttached) {
                registry.update(target, coords.boundsInRoot())
            }
        }
    }
}

@Immutable
data class PulseTutorialStepSpec(
    val id: String,
    val tab: AppTab,
    val target: TutorialSpotlightTarget,
    val pose: TimePhasePose,
    val urgentMood: MascotMood? = null,
    val chirpVariant: String,
    val labelId: String,
    val labelEn: String,
    val bubbleId: String,
    val bubbleEn: String
)

val PULSE_TUTORIAL_STEPS: List<PulseTutorialStepSpec> = listOf(
    PulseTutorialStepSpec(
        id = "today",
        tab = AppTab.TODAY,
        target = TutorialSpotlightTarget.TODAY_REST_TIMER,
        pose = TimePhasePose.PERK,
        urgentMood = null,
        chirpVariant = "ready",
        labelId = "Tab Hari Ini · Timer Istirahat",
        labelEn = "Today Tab · Rest Timer",
        bubbleId = "Ini Timer Istirahat cepat di Tab Hari Ini! Selesai satu set, ketuk preset di kotak ini biar jeda istirahatmu tetap presisi — plus ada pengaman salah klik 30 detik.",
        bubbleEn = "Here's your quick Rest Timer! Finish a set and tap a preset right here to keep recovery crisp — backed by a 30s anti-misclick guard."
    ),
    PulseTutorialStepSpec(
        id = "schedule",
        tab = AppTab.SCHEDULE,
        target = TutorialSpotlightTarget.SCHEDULE_PROGRAM_STRIP,
        pose = TimePhasePose.SALUTE,
        urgentMood = null,
        chirpVariant = "ready",
        labelId = "Tab Jadwal · Template & Tersimpan",
        labelEn = "Schedule Tab · Templates & Saved",
        bubbleId = "Lihat strip paling atas ini! Kamu bisa pakai Template Resmi PPL × Upper-Lower atau tekan + Simpan buat jadwal kustom. Jadwal lamamu otomatis aman di \"Jadwal Sebelumnya\"!",
        bubbleEn = "Look at this top strip! Pick an official PPL × Upper-Lower template or tap + Save for custom splits. Your old plan stays safely backed up in \"Previous Schedule\"!"
    ),
    PulseTutorialStepSpec(
        id = "exercises",
        tab = AppTab.EXERCISES,
        target = TutorialSpotlightTarget.EXERCISES_HEADER,
        pose = TimePhasePose.FLEX,
        urgentMood = null,
        chirpVariant = "clap",
        labelId = "Tab Gerakan · Pustaka 120+",
        labelEn = "Exercises Tab · 120+ Library",
        bubbleId = "Di sini markas daftar gerakanmu! Pilih hari latihan lalu buka Pustaka untuk mencentang banyak gerakan sekaligus, atau buat gerakan kustom dengan target set & beban.",
        bubbleEn = "Here's your exercise arsenal! Pick a weekday, open the Library to batch-add movements, or craft custom lifts with your own sets and weights."
    ),
    PulseTutorialStepSpec(
        id = "history",
        tab = AppTab.HISTORY,
        target = TutorialSpotlightTarget.HISTORY_CALENDAR,
        pose = TimePhasePose.SHADES,
        urgentMood = null,
        chirpVariant = "ready",
        labelId = "Tab Riwayat · Kalender Konsistensi",
        labelEn = "History Tab · Consistency Calendar",
        bubbleId = "Semua keringatmu tercatat di kalender ini! Ketuk tanggal mana pun untuk buka catatan beban & set sebelumnya — semuanya tersinkron otomatis ke Cloud.",
        bubbleEn = "Every workout is tracked right on this calendar! Tap any date to inspect logged sets and weights — all safely synced to your Cloud account."
    ),
    PulseTutorialStepSpec(
        id = "settings",
        tab = AppTab.SETTINGS,
        target = TutorialSpotlightTarget.SETTINGS_HEADER,
        pose = TimePhasePose.MUSIC,
        urgentMood = MascotMood.CELEBRATE,
        chirpVariant = "celebrate",
        labelId = "Pengaturan · Tema & Alarm",
        labelEn = "Settings · Themes & Alarms",
        bubbleId = "Terakhir! Di sini kamu bisa ganti tema (termasuk AMOLED Stealth!), unggah ringtone MP3/WAV, dan uji alarm. Aku terbang balik ke posku dulu ya — siap latihan!",
        bubbleEn = "Last stop! Switch themes (including AMOLED Stealth!), upload custom MP3/WAV ringtones, and test alarms here. I'm flying back to my post — let's train!"
    )
)

@Composable
fun PulseMascotTutorialOverlay(
    userName: String,
    baseMascot: MascotUiState,
    mascotEyeFlow: StateFlow<MascotEyeState>?,
    startAtWelcomeLanguageStep: Boolean,
    homeOrbBoundsHolder: OrbBoundsHolder? = null,
    spotlightRegistry: TutorialSpotlightRegistry? = null,
    onSelectLanguage: (AppLanguage) -> Unit,
    onNavigateToStepTab: (AppTab) -> Unit,
    onPlayChirp: (String) -> Unit,
    onTapMascotFeedback: () -> Unit,
    onComplete: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    val noAnim = tokens.performance.noAnimations
    val density = LocalDensity.current

    var stepIndex by remember(startAtWelcomeLanguageStep) {
        mutableIntStateOf(if (startAtWelcomeLanguageStep) -1 else 0)
    }
    var confirmSkip by remember { mutableStateOf(false) }
    var isReturningHome by remember { mutableStateOf(false) }
    var tapBounceActive by remember { mutableStateOf(false) }
    var lastFloatingOrbCenter by remember { mutableStateOf<Offset?>(null) }

    val returnFlightProgress = remember { Animatable(0f) }

    val friendlyName = remember(userName, lang) {
        val firstWord = userName.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
        firstWord.ifEmpty { lang.t("Juara", "Champ") }
    }

    LaunchedEffect(tapBounceActive) {
        if (tapBounceActive) {
            delay(220L)
            tapBounceActive = false
        }
    }

    // Drive tab navigation + soft chirp when step changes
    LaunchedEffect(stepIndex, isReturningHome) {
        if (isReturningHome) return@LaunchedEffect
        if (stepIndex == -1) {
            onNavigateToStepTab(AppTab.TODAY)
            onPlayChirp("ready")
        } else {
            val step = PULSE_TUTORIAL_STEPS[stepIndex.coerceIn(0, PULSE_TUTORIAL_STEPS.lastIndex)]
            onNavigateToStepTab(step.tab)
            onPlayChirp(step.chirpVariant)
        }
    }

    // Return-to-home flight back into TodayScreen's exact original Pulse orb slot
    LaunchedEffect(isReturningHome) {
        if (!isReturningHome) return@LaunchedEffect
        confirmSkip = false
        onNavigateToStepTab(AppTab.TODAY)
        onPlayChirp("celebrate")
        if (noAnim) {
            onComplete()
            return@LaunchedEffect
        }
        // Let TodayScreen re-attach & measure homeOrbBoundsHolder.rect
        withFrameNanos { }
        withFrameNanos { }
        returnFlightProgress.snapTo(0f)
        returnFlightProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )
        onComplete()
    }

    val advanceStep: () -> Unit = {
        if (!isReturningHome && !confirmSkip) {
            when {
                stepIndex == -1 -> stepIndex = 0
                stepIndex < PULSE_TUTORIAL_STEPS.lastIndex -> stepIndex += 1
                else -> isReturningHome = true
            }
        }
    }

    val pulseScale by animateFloatAsState(
        targetValue = if (tapBounceActive) 1.14f else 1f,
        animationSpec = if (noAnim) tween(0) else spring(dampingRatio = 0.45f, stiffness = 420f),
        label = "pulseCoachBounce"
    )

    val tutorialMascot = remember(baseMascot, stepIndex, confirmSkip, isReturningHome) {
        when {
            isReturningHome -> baseMascot.copy(
                mood = MascotMood.CELEBRATE,
                urgentMood = MascotMood.CELEBRATE,
                activeTimePose = TimePhasePose.SPARKLE
            )
            confirmSkip -> baseMascot.copy(
                mood = MascotMood.READY,
                urgentMood = null,
                activeTimePose = TimePhasePose.CURIOUS
            )
            stepIndex == -1 -> baseMascot.copy(
                mood = MascotMood.WELCOME,
                urgentMood = null,
                activeTimePose = TimePhasePose.SPARKLE
            )
            else -> {
                val step = PULSE_TUTORIAL_STEPS[stepIndex.coerceIn(0, PULSE_TUTORIAL_STEPS.lastIndex)]
                baseMascot.copy(
                    mood = step.urgentMood ?: MascotMood.READY,
                    urgentMood = step.urgentMood,
                    activeTimePose = step.pose
                )
            }
        }
    }

    val handleTapPulse: () -> Unit = {
        tapBounceActive = true
        onTapMascotFeedback()
    }

    BackHandler {
        if (isReturningHome) {
            // Let flight finish
        } else if (confirmSkip) {
            confirmSkip = false
        } else if (stepIndex > 0) {
            stepIndex -= 1
        } else if (stepIndex == 0 && startAtWelcomeLanguageStep) {
            stepIndex = -1
        } else {
            confirmSkip = true
        }
    }

    val pulseHintAlpha: Float = if (noAnim) {
        0.92f
    } else {
        val inf = rememberInfiniteTransition(label = "tapHintPulse")
        val a by inf.animateFloat(
            initialValue = 0.55f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "tapHintAlpha"
        )
        a
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("pulse_tutorial_overlay")
    ) {
        val rootWidthPx = with(density) { maxWidth.toPx() }.coerceAtLeast(1f)
        val rootHeightPx = with(density) { maxHeight.toPx() }.coerceAtLeast(1f)

        val currentStep = if (stepIndex in PULSE_TUTORIAL_STEPS.indices) {
            PULSE_TUTORIAL_STEPS[stepIndex]
        } else {
            null
        }

        // Compute target spotlight cutout rect (live measured bounds or sensible fallback)
        val rawSpotlightRect: Rect? = if (currentStep != null && !confirmSkip && !isReturningHome) {
            val measured = spotlightRegistry?.bounds?.get(currentStep.target)
            if (measured != null && measured.width > 20f && measured.height > 20f) {
                val padPx = with(density) { 6.dp.toPx() }
                Rect(
                    left = (measured.left - padPx).coerceAtLeast(with(density) { 8.dp.toPx() }),
                    top = (measured.top - padPx).coerceAtLeast(with(density) { 8.dp.toPx() }),
                    right = (measured.right + padPx).coerceAtMost(rootWidthPx - with(density) { 8.dp.toPx() }),
                    bottom = (measured.bottom + padPx).coerceAtMost(rootHeightPx - with(density) { 8.dp.toPx() })
                )
            } else {
                // Clean fallback spotlight zone before first measurement pass completes
                val padX = with(density) { 16.dp.toPx() }
                val fallbackTop = when (currentStep.target) {
                    TutorialSpotlightTarget.TODAY_REST_TIMER -> rootHeightPx * 0.42f
                    TutorialSpotlightTarget.SCHEDULE_PROGRAM_STRIP -> rootHeightPx * 0.14f
                    TutorialSpotlightTarget.EXERCISES_HEADER -> rootHeightPx * 0.14f
                    TutorialSpotlightTarget.HISTORY_CALENDAR -> rootHeightPx * 0.34f
                    TutorialSpotlightTarget.SETTINGS_HEADER -> rootHeightPx * 0.12f
                }
                val fallbackHeight = when (currentStep.target) {
                    TutorialSpotlightTarget.HISTORY_CALENDAR -> rootHeightPx * 0.34f
                    TutorialSpotlightTarget.SETTINGS_HEADER -> rootHeightPx * 0.15f
                    else -> rootHeightPx * 0.22f
                }
                Rect(padX, fallbackTop, rootWidthPx - padX, (fallbackTop + fallbackHeight).coerceAtMost(rootHeightPx * 0.82f))
            }
        } else {
            null
        }

        val animSpotLeft by animateFloatAsState(
            targetValue = rawSpotlightRect?.left ?: (rootWidthPx * 0.5f),
            animationSpec = if (noAnim) tween(0) else IronPulseMotion.MorphSpring,
            label = "spotLeft"
        )
        val animSpotTop by animateFloatAsState(
            targetValue = rawSpotlightRect?.top ?: (rootHeightPx * 0.5f),
            animationSpec = if (noAnim) tween(0) else IronPulseMotion.MorphSpring,
            label = "spotTop"
        )
        val animSpotRight by animateFloatAsState(
            targetValue = rawSpotlightRect?.right ?: (rootWidthPx * 0.5f),
            animationSpec = if (noAnim) tween(0) else IronPulseMotion.MorphSpring,
            label = "spotRight"
        )
        val animSpotBottom by animateFloatAsState(
            targetValue = rawSpotlightRect?.bottom ?: (rootHeightPx * 0.5f),
            animationSpec = if (noAnim) tween(0) else IronPulseMotion.MorphSpring,
            label = "spotBottom"
        )

        val scrimAlpha = if (isReturningHome) {
            (1f - returnFlightProgress.value).coerceIn(0f, 1f) * 0.68f
        } else {
            0.72f
        }

        // LAYER 1: Real Offscreen Spotlight Cutout Scrim + Tap-Anywhere-To-Continue handler
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .pointerInput(stepIndex, confirmSkip, isReturningHome) {
                    detectTapGestures {
                        if (!confirmSkip && !isReturningHome) {
                            advanceStep()
                        }
                    }
                }
                .testTag("pulse_tutorial_tap_area")
        ) {
            drawRect(Color.Black.copy(alpha = scrimAlpha))

            if (rawSpotlightRect != null && !isReturningHome) {
                val w = (animSpotRight - animSpotLeft).coerceAtLeast(0f)
                val h = (animSpotBottom - animSpotTop).coerceAtLeast(0f)
                if (w > 4f && h > 4f) {
                    val cornerPx = 18.dp.toPx()
                    val topLeft = Offset(animSpotLeft, animSpotTop)
                    val rectSize = Size(w, h)
                    // Punch clean clear window directly over the live UI element
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = topLeft,
                        size = rectSize,
                        cornerRadius = CornerRadius(cornerPx, cornerPx),
                        blendMode = BlendMode.Clear
                    )
                    // Soft outer glow halo
                    drawRoundRect(
                        color = tokens.accentColor.copy(alpha = 0.28f * pulseHintAlpha),
                        topLeft = Offset(topLeft.x - 3.dp.toPx(), topLeft.y - 3.dp.toPx()),
                        size = Size(rectSize.width + 6.dp.toPx(), rectSize.height + 6.dp.toPx()),
                        cornerRadius = CornerRadius(cornerPx + 3.dp.toPx(), cornerPx + 3.dp.toPx()),
                        style = Stroke(width = 4.dp.toPx())
                    )
                    // Crisp inner accent spotlight frame
                    drawRoundRect(
                        color = tokens.accentColor.copy(alpha = 0.92f),
                        topLeft = topLeft,
                        size = rectSize,
                        cornerRadius = CornerRadius(cornerPx, cornerPx),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        // LAYER 2: Interactive Foreground Content
        when {
            isReturningHome -> {
                // Single-entity Pulse flying smoothly back into TodayScreen's home orb slot
                val startCenter = lastFloatingOrbCenter ?: Offset(rootWidthPx * 0.22f, rootHeightPx * 0.65f)
                val homeRect = homeOrbBoundsHolder?.rect
                val endCenter = if (homeRect != null && homeRect.width > 10f) {
                    homeRect.center
                } else {
                    Offset(
                        x = with(density) { 66.dp.toPx() },
                        y = with(density) { 260.dp.toPx() }
                    )
                }
                val t = returnFlightProgress.value.coerceIn(0f, 1f)
                val orbSizeDp = 62.dp
                val orbHalfPx = with(density) { (orbSizeDp / 2).toPx() }
                val currentX = startCenter.x + (endCenter.x - startCenter.x) * t
                val currentY = startCenter.y + (endCenter.y - startCenter.y) * t

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = (currentX - orbHalfPx).roundToInt(),
                                y = (currentY - orbHalfPx).roundToInt()
                            )
                        }
                        .size(orbSizeDp)
                        .testTag("pulse_tutorial_returning_orb")
                ) {
                    MascotOrbFace(
                        mascot = tutorialMascot,
                        eyeStateFlow = mascotEyeFlow,
                        size = orbSizeDp,
                        animActive = true
                    )
                }
            }

            confirmSkip -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PulseSkipBubbleCard(
                        mascot = tutorialMascot,
                        mascotEyeFlow = mascotEyeFlow,
                        pulseScale = pulseScale,
                        onTapPulse = handleTapPulse,
                        onConfirmSkip = { isReturningHome = true },
                        onResume = { confirmSkip = false }
                    )
                }
            }

            stepIndex == -1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    // Compact Skip pill at top-right
                    SkipTutorialPill(
                        onClick = { confirmSkip = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp, end = 14.dp)
                    )

                    // Center Welcome Bubble Chat + Hero Pulse + Language Selector Pills
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .widthIn(max = 440.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .testTag("pulse_tutorial_welcome_card"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    if (coords.isAttached) {
                                        lastFloatingOrbCenter = coords.boundsInRoot().center
                                    }
                                }
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                                .clip(CircleShape)
                                .clickable(onClick = handleTapPulse)
                                .testTag("pulse_tutorial_mascot")
                        ) {
                            MascotOrbFace(
                                mascot = tutorialMascot,
                                eyeStateFlow = mascotEyeFlow,
                                size = 78.dp,
                                animActive = true
                            )
                        }

                        // Speech bubble with upward tail pointing to Pulse
                        PulseSpeechBubble(
                            tailSide = BubbleTailSide.TOP,
                            badgeText = lang.t("PULSE · PARTNER LATIHAN", "PULSE · TRAINING PARTNER"),
                            stepCounter = null,
                            headline = lang.t("Hai, $friendlyName! Aku Pulse", "Hi, $friendlyName! I'm Pulse"),
                            body = lang.t(
                                "Selamat datang di IronPulse! Pilih bahasamu di bawah, lalu ketuk layar di mana saja biar aku tunjukin semua fitur penting secara langsung.",
                                "Welcome to IronPulse! Pick your language below, then tap anywhere on screen and I'll point out every key feature live."
                            ),
                            tapHint = null
                        )

                        // Language pills (do not advance step when tapped)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                Triple(AppLanguage.ID, "Indonesia", "pulse_tutorial_lang_id"),
                                Triple(AppLanguage.EN, "English", "pulse_tutorial_lang_en")
                            ).forEach { (option, label, tag) ->
                                val selected = lang == option
                                Surface(
                                    onClick = {
                                        onSelectLanguage(option)
                                        onPlayChirp("ready")
                                    },
                                    shape = RoundedCornerShape(999.dp),
                                    color = if (selected) {
                                        tokens.accentColor.copy(alpha = 0.22f)
                                    } else {
                                        tokens.palette.surfaceContainer.copy(alpha = 0.92f)
                                    },
                                    border = BorderStroke(
                                        width = if (selected) 1.5.dp else 1.dp,
                                        color = if (selected) tokens.accentColor else tokens.surfaceBorderColor
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .testTag(tag)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (selected) {
                                            ThemedIcon(
                                                icon = IronPulseIcon.CHECK_CIRCLE,
                                                contentDescription = null,
                                                tint = tokens.accentColor,
                                                size = 16.dp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = label,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = tokens.palette.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Pulsing tap-anywhere hint
                        TapScreenContinuePill(
                            text = lang.t("Ketuk layar untuk mulai tur", "Tap anywhere to start tour"),
                            alpha = pulseHintAlpha,
                            modifier = Modifier.testTag("pulse_tutorial_start_hint")
                        )
                    }
                }
            }

            currentStep != null -> {
                val placeAboveSpotlight = rawSpotlightRect != null &&
                    rawSpotlightRect.center.y > rootHeightPx * 0.50f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    // Compact Skip pill at top-right
                    SkipTutorialPill(
                        onClick = { confirmSkip = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 14.dp)
                    )

                    // Floating Pulse + Bubble Chat anchored smartly above or below the spotlight cutout
                    val alignment = if (placeAboveSpotlight) Alignment.TopCenter else Alignment.BottomCenter
                    val extraTopPad = if (placeAboveSpotlight) 56.dp else 12.dp
                    val extraBottomPad = if (placeAboveSpotlight) 12.dp else 18.dp

                    Row(
                        modifier = Modifier
                            .align(alignment)
                            .widthIn(max = 520.dp)
                            .fillMaxWidth()
                            .padding(
                                start = 14.dp,
                                end = 14.dp,
                                top = extraTopPad,
                                bottom = extraBottomPad
                            )
                            .testTag("pulse_tutorial_bubble_dock"),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Living Pulse mascot orb right beside the chat bubble
                        Box(
                            modifier = Modifier
                                .padding(bottom = 6.dp)
                                .onGloballyPositioned { coords ->
                                    if (coords.isAttached) {
                                        lastFloatingOrbCenter = coords.boundsInRoot().center
                                    }
                                }
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                                .clip(CircleShape)
                                .clickable(onClick = handleTapPulse)
                                .testTag("pulse_tutorial_mascot")
                        ) {
                            MascotOrbFace(
                                mascot = tutorialMascot,
                                eyeStateFlow = mascotEyeFlow,
                                size = 62.dp,
                                animActive = true
                            )
                        }

                        // Speech bubble pointing left toward Pulse
                        Box(modifier = Modifier.weight(1f)) {
                            AnimatedContent(
                                targetState = currentStep,
                                transitionSpec = {
                                    if (noAnim) {
                                        fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                                    } else {
                                        fadeIn(tween(180)) togetherWith fadeOut(tween(120))
                                    }
                                },
                                label = "pulseBubbleStep"
                            ) { step ->
                                val isLast = stepIndex >= PULSE_TUTORIAL_STEPS.lastIndex
                                PulseSpeechBubble(
                                    tailSide = BubbleTailSide.LEFT,
                                    badgeText = lang.t(step.labelId, step.labelEn),
                                    stepCounter = "${stepIndex + 1}/${PULSE_TUTORIAL_STEPS.size}",
                                    headline = null,
                                    body = lang.t(step.bubbleId, step.bubbleEn),
                                    tapHint = if (isLast) {
                                        lang.t("Ketuk layar untuk selesai & kembali", "Tap screen to finish & return")
                                    } else {
                                        lang.t("Ketuk layar untuk lanjut", "Tap anywhere to continue")
                                    },
                                    tapHintAlpha = pulseHintAlpha
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class BubbleTailSide { LEFT, TOP }

@Composable
private fun PulseSpeechBubble(
    tailSide: BubbleTailSide,
    badgeText: String,
    stepCounter: String?,
    headline: String?,
    body: String,
    tapHint: String?,
    tapHintAlpha: Float = 1f
) {
    val tokens = LocalIronPulseTokens.current
    val bubbleFill = tokens.palette.surfaceContainer.copy(alpha = 0.96f)
    val bubbleBorder = tokens.accentColor.copy(alpha = 0.55f)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (tailSide == BubbleTailSide.TOP) {
            Canvas(modifier = Modifier.size(width = 18.dp, height = 9.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.5f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path, color = bubbleFill)
                drawLine(bubbleBorder, Offset(size.width * 0.5f, 0f), Offset(0f, size.height), strokeWidth = 1.2.dp.toPx())
                drawLine(bubbleBorder, Offset(size.width * 0.5f, 0f), Offset(size.width, size.height), strokeWidth = 1.2.dp.toPx())
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (tailSide == BubbleTailSide.LEFT) {
                Canvas(
                    modifier = Modifier
                        .padding(bottom = 22.dp)
                        .size(width = 10.dp, height = 16.dp)
                ) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.5f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path, color = bubbleFill)
                    drawLine(bubbleBorder, Offset(0f, size.height * 0.5f), Offset(size.width, 0f), strokeWidth = 1.2.dp.toPx())
                    drawLine(bubbleBorder, Offset(0f, size.height * 0.5f), Offset(size.width, size.height), strokeWidth = 1.2.dp.toPx())
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f, fill = tailSide == BubbleTailSide.LEFT)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(bubbleFill)
                    .border(1.2.dp, bubbleBorder, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        ThemedIcon(
                            icon = IronPulseIcon.SPARKLE,
                            contentDescription = null,
                            tint = tokens.accentColor,
                            size = 13.dp
                        )
                        Text(
                            text = badgeText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.accentColor
                        )
                    }
                    if (stepCounter != null) {
                        Text(
                            text = stepCounter,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.palette.onSurfaceVariant
                        )
                    }
                }

                if (headline != null) {
                    Text(
                        text = headline,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface,
                        lineHeight = 22.sp
                    )
                }

                Text(
                    text = body,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = tokens.palette.onSurface
                )

                if (tapHint != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.graphicsLayer { alpha = tapHintAlpha }
                    ) {
                        ThemedIcon(
                            icon = IronPulseIcon.FOCUS_HUD,
                            contentDescription = null,
                            tint = tokens.accentColor,
                            size = 14.dp
                        )
                        Text(
                            text = tapHint,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.accentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TapScreenContinuePill(
    text: String,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = tokens.accentColor.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, tokens.accentColor.copy(alpha = 0.45f)),
        modifier = modifier.graphicsLayer { this.alpha = alpha }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            ThemedIcon(
                icon = IronPulseIcon.FOCUS_HUD,
                contentDescription = null,
                tint = tokens.accentColor,
                size = 16.dp
            )
            Text(
                text = text,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = tokens.palette.onSurface
            )
        }
    }
}

@Composable
private fun SkipTutorialPill(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        color = tokens.palette.surfaceContainer.copy(alpha = 0.90f),
        border = BorderStroke(1.dp, tokens.surfaceBorderColor),
        modifier = modifier
            .heightIn(min = 48.dp)
            .testTag("pulse_tutorial_skip_button")
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = lang.t("Lewati", "Skip"),
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = tokens.palette.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PulseSkipBubbleCard(
    mascot: MascotUiState,
    mascotEyeFlow: StateFlow<MascotEyeState>?,
    pulseScale: Float,
    onTapPulse: () -> Unit,
    onConfirmSkip: () -> Unit,
    onResume: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val lang = tokens.lang

    Column(
        modifier = Modifier
            .testTag("pulse_tutorial_skip_confirm_card")
            .widthIn(max = 420.dp)
            .fillMaxWidth()
            .ironGlassSurface(
                tokens = tokens,
                borderOverride = tokens.accentColor.copy(alpha = 0.42f)
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .clip(CircleShape)
                    .clickable(onClick = onTapPulse)
            ) {
                MascotOrbFace(
                    mascot = mascot,
                    eyeStateFlow = mascotEyeFlow,
                    size = 62.dp,
                    animActive = true
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lang.t("Lewati Tur Bareng Pulse?", "Skip Pulse's Tour?"),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tokens.palette.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lang.t(
                        "Tenang, aku balik dulu ke Tab Hari Ini. Kapan saja butuh, panggil aku lagi lewat Pengaturan -> Sistem.",
                        "No worries, I'll head back to the Today tab. Summon me anytime from Settings -> System."
                    ),
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onConfirmSkip,
                shape = RoundedCornerShape(tokens.buttonRadius),
                border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pulse_tutorial_confirm_skip_button")
            ) {
                Text(
                    text = lang.t("Ya, Lewati", "Yes, Skip"),
                    color = tokens.palette.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Button(
                onClick = onResume,
                shape = RoundedCornerShape(tokens.buttonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.accentColor,
                    contentColor = tokens.onAccentColor
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pulse_tutorial_resume_button")
            ) {
                Text(
                    text = lang.t("Lanjut Tur", "Resume"),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
