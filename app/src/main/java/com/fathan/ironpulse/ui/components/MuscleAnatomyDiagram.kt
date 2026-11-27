package com.fathan.ironpulse.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fathan.ironpulse.model.AppLanguage
import com.fathan.ironpulse.model.BodyViewSide
import com.fathan.ironpulse.model.ExerciseTrackingMode
import com.fathan.ironpulse.model.GymModelHelpers
import com.fathan.ironpulse.model.MuscleAnatomyResolver
import com.fathan.ironpulse.model.MuscleAnatomySpec
import com.fathan.ironpulse.model.MuscleGroup
import com.fathan.ironpulse.model.MuscleRegion
import com.fathan.ironpulse.model.TimerDirection
import com.fathan.ironpulse.ui.theme.IronPulseButton
import com.fathan.ironpulse.ui.theme.IronPulseOutlinedButton
import com.fathan.ironpulse.ui.theme.IronPulseTonalSurface
import com.fathan.ironpulse.ui.theme.LocalIronPulseTokens
import com.fathan.ironpulse.ui.theme.ironGlassSurface
import com.fathan.ironpulse.ui.theme.ironTonalControl

/**
 * Normalized 100 x 176 human figure silhouettes and 20 symmetric muscle region paths.
 * Cached once per process for zero-allocation 60-120fps scrolling inside LazyColumn lists.
 */
private object AnatomyVectorCache {
    const val FIG_W = 100f
    const val FIG_H = 176f

    val detailSilhouetteStroke = Stroke(width = 1.6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val thumbSilhouetteStroke = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val detailSecondaryStroke = Stroke(width = 1.0f, join = StrokeJoin.Round)
    val thumbSecondaryStroke = Stroke(width = 1.4f, join = StrokeJoin.Round)
    val primaryHaloStroke = Stroke(width = 3.8f, join = StrokeJoin.Round)
    val primaryDetailStroke = Stroke(width = 0.9f, join = StrokeJoin.Round)

    private fun mirroredPolygon(vararg pts: Pair<Float, Float>): Path = Path().apply {
        // Left side
        pts.forEachIndexed { idx, (x, y) ->
            if (idx == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
        // Right side (mirrored across x = 50)
        pts.forEachIndexed { idx, (x, y) ->
            val mx = 100f - x
            if (idx == 0) moveTo(mx, y) else lineTo(mx, y)
        }
        close()
    }

    val bodySilhouette: Path = Path().apply {
        // Head
        addOval(Rect(42f, 4f, 58f, 23f))
        // Neck + Torso + Arms + Legs single smooth athletic figure
        moveTo(44.5f, 22f)
        lineTo(55.5f, 22f)
        lineTo(56.5f, 28.5f)
        // Right shoulder & outer arm
        quadraticTo(68f, 30f, 76f, 34f)
        quadraticTo(80f, 36f, 81f, 44f)
        lineTo(84.5f, 62f)
        lineTo(89.5f, 86f)
        lineTo(91f, 96f)
        quadraticTo(89f, 99f, 86.5f, 96f)
        lineTo(84.5f, 86f)
        lineTo(78.5f, 64f)
        lineTo(74f, 47f)
        // Right torso flank
        lineTo(71.5f, 52f)
        lineTo(67f, 74f)
        quadraticTo(71f, 85f, 71f, 96f)
        // Right outer leg
        lineTo(70f, 112f)
        lineTo(66.5f, 128f)
        quadraticTo(69.5f, 139f, 67f, 150f)
        lineTo(63.5f, 165f)
        lineTo(65.5f, 171f)
        lineTo(56.5f, 171f)
        lineTo(57.5f, 164f)
        lineTo(55.5f, 148f)
        lineTo(56f, 130f)
        lineTo(53.5f, 114f)
        lineTo(51.2f, 97.5f)
        // Crotch center
        lineTo(48.8f, 97.5f)
        // Left inner leg
        lineTo(46.5f, 114f)
        lineTo(44f, 130f)
        lineTo(44.5f, 148f)
        lineTo(42.5f, 164f)
        lineTo(43.5f, 171f)
        lineTo(34.5f, 171f)
        lineTo(36.5f, 165f)
        lineTo(33f, 150f)
        quadraticTo(30.5f, 139f, 33.5f, 128f)
        lineTo(30f, 112f)
        // Left outer hip & torso flank
        lineTo(29f, 96f)
        quadraticTo(29f, 85f, 33f, 74f)
        lineTo(28.5f, 52f)
        lineTo(26f, 47f)
        // Left inner arm to hand
        lineTo(21.5f, 64f)
        lineTo(15.5f, 86f)
        lineTo(13.5f, 96f)
        quadraticTo(11f, 99f, 9f, 96f)
        lineTo(10.5f, 86f)
        lineTo(15.5f, 62f)
        lineTo(19f, 44f)
        quadraticTo(20f, 36f, 24f, 34f)
        quadraticTo(32f, 30f, 43.5f, 28.5f)
        close()
    }

    val anteriorPaths: Map<MuscleRegion, Path> = mapOf(
        MuscleRegion.UPPER_CHEST to mirroredPolygon(
            32.5f to 33.5f, 48.5f to 35.5f, 48.5f to 41f, 31f to 39.5f
        ),
        MuscleRegion.MID_CHEST to mirroredPolygon(
            30.5f to 40f, 48.5f to 41.5f, 48.5f to 50.5f, 36f to 51.5f, 30f to 46.5f
        ),
        MuscleRegion.FRONT_DELTS to mirroredPolygon(
            23f to 34f, 31.5f to 34.5f, 30f to 44.5f, 22.5f to 44f
        ),
        MuscleRegion.SIDE_DELTS to mirroredPolygon(
            19.5f to 36f, 23f to 34f, 22.5f to 45.5f, 18.8f to 44.5f
        ),
        MuscleRegion.BICEPS to mirroredPolygon(
            20.5f to 46.5f, 26.5f to 47.5f, 24.5f to 61.5f, 18.5f to 60f
        ),
        MuscleRegion.FOREARMS to mirroredPolygon(
            16.5f to 63.5f, 21.8f to 65f, 16.5f to 83.5f, 12f to 82f
        ),
        MuscleRegion.ABS to Path().apply {
            // 3 upper-to-lower abdominal segments per side
            addRect(Rect(42.5f, 52.5f, 49f, 60f))
            addRect(Rect(51f, 52.5f, 57.5f, 60f))
            addRect(Rect(42.5f, 61.5f, 49f, 69f))
            addRect(Rect(51f, 61.5f, 57.5f, 69f))
            addRect(Rect(43f, 70.5f, 49f, 78.5f))
            addRect(Rect(51f, 70.5f, 57f, 78.5f))
            // Lower V segment
            moveTo(43.5f, 80f); lineTo(49f, 80f); lineTo(49f, 90f); lineTo(45.5f, 87f); close()
            moveTo(56.5f, 80f); lineTo(51f, 80f); lineTo(51f, 90f); lineTo(54.5f, 87f); close()
        },
        MuscleRegion.OBLIQUES to mirroredPolygon(
            32f to 53f, 40.5f to 54f, 41.5f to 77f, 34f to 74f
        ),
        MuscleRegion.ADDUCTORS to mirroredPolygon(
            42f to 96f, 48.2f to 98f, 46.2f to 118f, 41.5f to 116f
        ),
        MuscleRegion.QUADS to mirroredPolygon(
            31f to 95f, 41.5f to 96f, 43.2f to 124f, 33.5f to 125f, 30.5f to 111f
        ),
        MuscleRegion.TIBIALIS to mirroredPolygon(
            34.5f to 132f, 39.5f to 133f, 39f to 158f, 35.5f to 157f
        )
    )

    val posteriorPaths: Map<MuscleRegion, Path> = mapOf(
        MuscleRegion.TRAPS to Path().apply {
            moveTo(44.5f, 26.5f)
            lineTo(55.5f, 26.5f)
            lineTo(65f, 34f)
            lineTo(50f, 49f)
            lineTo(35f, 34f)
            close()
        },
        MuscleRegion.REAR_DELTS to mirroredPolygon(
            22.5f to 34.5f, 32f to 34.5f, 30.5f to 44.5f, 22.5f to 44.5f
        ),
        MuscleRegion.SIDE_DELTS to mirroredPolygon(
            19.5f to 36f, 23f to 34f, 22.5f to 45.5f, 18.8f to 44.5f
        ),
        MuscleRegion.LATS to mirroredPolygon(
            29.5f to 45.5f, 39f to 47f, 42.5f to 68f, 34.5f to 71f
        ),
        MuscleRegion.MID_BACK to mirroredPolygon(
            39.5f to 40f, 48.5f to 42f, 48.5f to 58f, 40f to 57f
        ),
        MuscleRegion.LOWER_BACK to mirroredPolygon(
            41f to 62f, 48.5f to 62f, 48.5f to 80f, 38.5f to 79f
        ),
        MuscleRegion.TRICEPS to mirroredPolygon(
            19.5f to 46f, 26.5f to 47f, 24.5f to 61.5f, 18f to 59.5f
        ),
        MuscleRegion.FOREARMS to mirroredPolygon(
            16.5f to 63.5f, 21.8f to 65f, 16.5f to 83.5f, 12f to 82f
        ),
        MuscleRegion.GLUTES to mirroredPolygon(
            31f to 81.5f, 48.5f to 82.5f, 48.5f to 97f, 31f to 98f
        ),
        MuscleRegion.HAMSTRINGS to mirroredPolygon(
            32f to 100.5f, 45.5f to 101f, 43.8f to 125f, 33.8f to 125f
        ),
        MuscleRegion.CALVES to mirroredPolygon(
            33.5f to 131f, 43.5f to 132f, 42.5f to 153f, 34.8f to 153f
        )
    )
}

private fun DrawScope.drawAnatomyFigure(
    side: BodyViewSide,
    anatomy: MuscleAnatomySpec,
    offsetX: Float,
    figureWidth: Float,
    figureHeight: Float,
    silhouetteFill: Color,
    silhouetteStroke: Color,
    primaryFill: Color,
    primaryGlow: Color,
    secondaryFill: Color,
    secondaryStroke: Color,
    detailed: Boolean
) {
    val scaleX = figureWidth / AnatomyVectorCache.FIG_W
    val scaleY = figureHeight / AnatomyVectorCache.FIG_H
    val s = minOf(scaleX, scaleY)
    val dx = offsetX + (figureWidth - AnatomyVectorCache.FIG_W * s) * 0.5f
    val dy = (figureHeight - AnatomyVectorCache.FIG_H * s) * 0.5f

    val regionMap = if (side == BodyViewSide.ANTERIOR) {
        AnatomyVectorCache.anteriorPaths
    } else {
        AnatomyVectorCache.posteriorPaths
    }

    withTransform({
        translate(left = dx, top = dy)
        scale(scaleX = s, scaleY = s, pivot = Offset.Zero)
    }) {
        // Base athletic silhouette
        drawPath(
            path = AnatomyVectorCache.bodySilhouette,
            color = silhouetteFill,
            style = Fill
        )
        drawPath(
            path = AnatomyVectorCache.bodySilhouette,
            color = silhouetteStroke,
            style = if (detailed) AnatomyVectorCache.detailSilhouetteStroke else AnatomyVectorCache.thumbSilhouetteStroke
        )

        // Subtle anatomy guide lines in detailed mode (collarbone / spine)
        if (detailed) {
            if (side == BodyViewSide.ANTERIOR) {
                drawLine(
                    color = silhouetteStroke.copy(alpha = 0.45f),
                    start = Offset(34f, 33f),
                    end = Offset(50f, 35.5f),
                    strokeWidth = 0.9f
                )
                drawLine(
                    color = silhouetteStroke.copy(alpha = 0.45f),
                    start = Offset(66f, 33f),
                    end = Offset(50f, 35.5f),
                    strokeWidth = 0.9f
                )
            } else {
                drawLine(
                    color = silhouetteStroke.copy(alpha = 0.35f),
                    start = Offset(50f, 30f),
                    end = Offset(50f, 82f),
                    strokeWidth = 1.0f
                )
            }
        }

        // Secondary muscles drawn underneath primary
        anatomy.secondary.forEach { region ->
            val path = regionMap[region] ?: return@forEach
            drawPath(path = path, color = secondaryFill, style = Fill)
            drawPath(
                path = path,
                color = secondaryStroke,
                style = if (detailed) AnatomyVectorCache.detailSecondaryStroke else AnatomyVectorCache.thumbSecondaryStroke
            )
        }

        // Primary muscles drawn on top with crisp border and soft halo
        anatomy.primary.forEach { region ->
            val path = regionMap[region] ?: return@forEach
            if (detailed) {
                drawPath(
                    path = path,
                    color = primaryGlow,
                    style = AnatomyVectorCache.primaryHaloStroke
                )
            }
            drawPath(path = path, color = primaryFill, style = Fill)
            if (detailed) {
                drawPath(
                    path = path,
                    color = primaryFill,
                    style = AnatomyVectorCache.primaryDetailStroke
                )
            }
        }
    }
}

/**
 * Compact dual front + back anatomical muscle diagram thumbnail for movement cards.
 */
@Composable
fun MuscleAnatomyThumbnail(
    anatomy: MuscleAnatomySpec,
    modifier: Modifier = Modifier,
    width: Dp = 58.dp,
    height: Dp = 50.dp
) {
    val tokens = LocalIronPulseTokens.current
    val shape = tokens.subtleCardShape
    val onSurfaceVar = tokens.palette.onSurfaceVariant
    val accent = tokens.accentColor
    val silhouetteFill = remember(onSurfaceVar) { onSurfaceVar.copy(alpha = 0.14f) }
    val silhouetteStroke = remember(onSurfaceVar) { onSurfaceVar.copy(alpha = 0.34f) }
    val primaryFill = remember(accent) { accent.copy(alpha = 0.96f) }
    val secondaryFill = remember(accent) { accent.copy(alpha = 0.34f) }
    val secondaryStroke = remember(accent) { accent.copy(alpha = 0.58f) }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .clip(shape)
            .background(tokens.wellBg)
            .border(1.dp, tokens.subtleBorderColor, shape)
            .padding(horizontal = 4.dp, vertical = 3.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gap = 3.dp.toPx()
            val eachW = (size.width - gap) * 0.5f
            drawAnatomyFigure(
                side = BodyViewSide.ANTERIOR,
                anatomy = anatomy,
                offsetX = 0f,
                figureWidth = eachW,
                figureHeight = size.height,
                silhouetteFill = silhouetteFill,
                silhouetteStroke = silhouetteStroke,
                primaryFill = primaryFill,
                primaryGlow = Color.Transparent,
                secondaryFill = secondaryFill,
                secondaryStroke = secondaryStroke,
                detailed = false
            )
            drawAnatomyFigure(
                side = BodyViewSide.POSTERIOR,
                anatomy = anatomy,
                offsetX = eachW + gap,
                figureWidth = eachW,
                figureHeight = size.height,
                silhouetteFill = silhouetteFill,
                silhouetteStroke = silhouetteStroke,
                primaryFill = primaryFill,
                primaryGlow = Color.Transparent,
                secondaryFill = secondaryFill,
                secondaryStroke = secondaryStroke,
                detailed = false
            )
        }
    }
}

/**
 * Large dual Front (Anterior) & Back (Posterior) anatomical diagram with legend.
 */
@Composable
fun MuscleAnatomyDualView(
    anatomy: MuscleAnatomySpec,
    lang: AppLanguage,
    modifier: Modifier = Modifier,
    diagramHeight: Dp = 212.dp
) {
    val tokens = LocalIronPulseTokens.current
    val onSurfaceVar = tokens.palette.onSurfaceVariant
    val accent = tokens.accentColor
    val silhouetteFill = remember(onSurfaceVar) { onSurfaceVar.copy(alpha = 0.13f) }
    val silhouetteStroke = remember(onSurfaceVar) { onSurfaceVar.copy(alpha = 0.36f) }
    val primaryFill = accent
    val primaryGlow = remember(accent) { accent.copy(alpha = 0.24f) }
    val secondaryFill = remember(accent) { accent.copy(alpha = 0.34f) }
    val secondaryStroke = remember(accent) { accent.copy(alpha = 0.62f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .ironTonalControl(
                tokens = tokens,
                shape = tokens.subtleCardShape,
                baseColor = tokens.wellBg,
                isAccent = false
            )
            .border(1.dp, tokens.subtleBorderColor, tokens.subtleCardShape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = lang.t("TAMPAK DEPAN", "FRONT VIEW"),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = if (anatomy.hasAnterior) tokens.accentTextColor else tokens.mutedTextColor
            )
            Text(
                text = lang.t("TAMPAK BELAKANG", "BACK VIEW"),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = if (anatomy.hasPosterior) tokens.accentTextColor else tokens.mutedTextColor
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(diagramHeight)
                .testTag("muscle_anatomy_canvas")
        ) {
            val gap = 16.dp.toPx()
            val eachW = (size.width - gap) * 0.5f

            // Subtle vertical divider between front and back
            drawLine(
                color = silhouetteStroke.copy(alpha = 0.22f),
                start = Offset(size.width * 0.5f, 8f),
                end = Offset(size.width * 0.5f, size.height - 8f),
                strokeWidth = 1.dp.toPx()
            )

            drawAnatomyFigure(
                side = BodyViewSide.ANTERIOR,
                anatomy = anatomy,
                offsetX = 0f,
                figureWidth = eachW,
                figureHeight = size.height,
                silhouetteFill = silhouetteFill,
                silhouetteStroke = silhouetteStroke,
                primaryFill = primaryFill,
                primaryGlow = primaryGlow,
                secondaryFill = secondaryFill,
                secondaryStroke = secondaryStroke,
                detailed = true
            )

            drawAnatomyFigure(
                side = BodyViewSide.POSTERIOR,
                anatomy = anatomy,
                offsetX = eachW + gap,
                figureWidth = eachW,
                figureHeight = size.height,
                silhouetteFill = silhouetteFill,
                silhouetteStroke = silhouetteStroke,
                primaryFill = primaryFill,
                primaryGlow = primaryGlow,
                secondaryFill = secondaryFill,
                secondaryStroke = secondaryStroke,
                detailed = true
            )
        }

        // Legend row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(primaryFill)
                )
                Text(
                    text = lang.t("Otot Utama", "Primary"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tokens.palette.onSurface
                )
            }
            Spacer(modifier = Modifier.width(18.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(secondaryFill)
                        .border(1.dp, secondaryStroke, CircleShape)
                )
                Text(
                    text = lang.t("Otot Pendukung", "Secondary"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = tokens.palette.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Full anatomy & movement detail modal shown when tapping an exercise card.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseAnatomyDetailDialog(
    name: String,
    muscleGroup: MuscleGroup,
    equipment: String,
    targetSets: Int,
    targetReps: String,
    targetWeightKg: Double,
    restSeconds: Int,
    notes: String,
    trackingMode: ExerciseTrackingMode,
    usesWeight: Boolean,
    timerDirection: TimerDirection,
    targetDurationSeconds: Int,
    lang: AppLanguage,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val tokens = LocalIronPulseTokens.current
    val anatomy = remember(name, muscleGroup, equipment) {
        MuscleAnatomyResolver.resolve(name, muscleGroup, equipment)
    }
    val isTimer = trackingMode == ExerciseTrackingMode.TIMER

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxWidth()
                .heightIn(max = 740.dp)
                .ironGlassSurface(tokens)
                .verticalScroll(rememberScrollState())
                .padding(18.dp)
                .testTag("exercise_anatomy_detail_dialog"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = name,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = tokens.palette.onSurface
                    )
                    Text(
                        text = "${muscleGroup.label(lang)} · $equipment",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = tokens.accentTextColor
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp)
                ) {
                    ThemedIcon(
                        icon = IronPulseIcon.CLOSE,
                        contentDescription = lang.t("Tutup", "Close"),
                        tint = tokens.palette.onSurfaceVariant,
                        size = 18.dp
                    )
                }
            }

            // Large Front & Back Muscle Anatomy Diagram
            MuscleAnatomyDualView(anatomy = anatomy, lang = lang)

            // Active Muscle Regions Breakdown
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = lang.t("Target Otot Utama", "Primary Target Muscles"),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = tokens.palette.onSurfaceVariant
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    anatomy.primary.forEach { region ->
                        Box(
                            modifier = Modifier
                                .clip(tokens.buttonShape)
                                .background(tokens.accentColor.copy(alpha = 0.16f))
                                .border(1.dp, tokens.accentColor.copy(alpha = 0.45f), tokens.buttonShape)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = region.label(lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = tokens.accentTextColor
                            )
                        }
                    }
                }

                if (anatomy.secondary.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = lang.t("Otot Pendukung", "Secondary Muscles"),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.palette.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        anatomy.secondary.forEach { region ->
                            Box(
                                modifier = Modifier
                                    .clip(tokens.buttonShape)
                                    .background(tokens.subtleChipBg)
                                    .border(1.dp, tokens.subtleBorderColor, tokens.buttonShape)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = region.label(lang),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = tokens.palette.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Target Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailMetricChip(
                    label = "SET",
                    value = "$targetSets",
                    modifier = Modifier.weight(1f)
                )
                DetailMetricChip(
                    label = if (isTimer) lang.t("DURASI", "TIME") else "REP",
                    value = if (isTimer) {
                        GymModelHelpers.formatCompactDuration(targetDurationSeconds, lang)
                    } else {
                        targetReps
                    },
                    modifier = Modifier.weight(1.2f)
                )
                if (usesWeight) {
                    DetailMetricChip(
                        label = lang.t("BEBAN", "LOAD"),
                        value = "${GymModelHelpers.formatWeight(targetWeightKg)} kg",
                        modifier = Modifier.weight(1.1f)
                    )
                }
                DetailMetricChip(
                    label = lang.t("ISTIRAHAT", "REST"),
                    value = "${restSeconds}${lang.t("d", "s")}",
                    modifier = Modifier.weight(1f)
                )
            }

            if (isTimer || !usesWeight) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isTimer) {
                        Text(
                            text = if (timerDirection == TimerDirection.COUNTUP) "Stopwatch" else "Countdown",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.infoColor
                        )
                    }
                    if (!usesWeight) {
                        Text(
                            text = lang.t("Tanpa Beban (Bodyweight)", "Bodyweight"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = tokens.successColor
                        )
                    }
                }
            }

            if (notes.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .ironTonalControl(
                            tokens = tokens,
                            shape = tokens.subtleCardShape,
                            baseColor = tokens.wellBg,
                            isAccent = false
                        )
                        .border(1.dp, tokens.subtleBorderColor, tokens.subtleCardShape)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = lang.t("Panduan Teknik", "Form Cues"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tokens.mutedTextColor
                    )
                    Text(
                        text = notes,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = tokens.palette.onSurface
                    )
                }
            }

            HorizontalDivider(color = tokens.dividerColor)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    IronPulseOutlinedButton(
                        onClick = onSecondaryAction,
                        shape = tokens.buttonShape,
                        border = BorderStroke(1.dp, tokens.surfaceBorderColor),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = secondaryActionLabel,
                            fontWeight = FontWeight.Bold,
                            color = tokens.palette.onSurface
                        )
                    }
                } else {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text(
                            text = lang.t("Tutup", "Close"),
                            fontWeight = FontWeight.SemiBold,
                            color = tokens.palette.onSurfaceVariant
                        )
                    }
                }

                if (primaryActionLabel != null && onPrimaryAction != null) {
                    IronPulseButton(
                        onClick = onPrimaryAction,
                        shape = tokens.buttonShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tokens.accentColor,
                            contentColor = tokens.onAccentColor
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = primaryActionLabel,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val tokens = LocalIronPulseTokens.current
    Column(
        modifier = modifier
            .ironTonalControl(
                tokens = tokens,
                shape = tokens.subtleCardShape,
                baseColor = tokens.wellBg,
                isAccent = false
            )
            .border(1.dp, tokens.subtleBorderColor, tokens.subtleCardShape)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            color = tokens.mutedTextColor
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = tokens.palette.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
