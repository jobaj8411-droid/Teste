package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraMotion
import com.example.model.VideoAspectRatio
import com.example.model.VideoStyle
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.StudioOutline
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPrimary

@Composable
fun CreditPill(
    credits: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E1B4B),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberTertiary.copy(alpha = 0.5f)),
        modifier = modifier
            .testTag("credit_pill_button")
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = "Credits",
                tint = AmberTertiary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$credits",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "credits",
                color = AmberTertiary,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    creditCost: Int? = null,
    testTag: String = "gradient_action_button"
) {
    val gradientBrush = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1), Color(0xFF06B6D4))
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(Color(0xFF334155), Color(0xFF1E293B))
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) Color.White else Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            if (creditCost != null && creditCost > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = AmberTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "-$creditCost",
                            color = AmberTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AspectRatioSelector(
    selectedRatio: VideoAspectRatio,
    onSelect: (VideoAspectRatio) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VideoAspectRatio.entries.forEach { ratio ->
            val isSelected = ratio == selectedRatio
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) VioletPrimary.copy(alpha = 0.2f) else StudioSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) VioletPrimary else StudioOutline
                ),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(ratio) }
                    .testTag("ratio_button_${ratio.name}")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                ) {
                    // Mini shape representing the aspect ratio
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(
                                when (ratio) {
                                    VideoAspectRatio.PORTRAIT_9_16 -> 24.dp
                                    VideoAspectRatio.LANDSCAPE_16_9 -> 12.dp
                                    VideoAspectRatio.SQUARE_1_1 -> 18.dp
                                    VideoAspectRatio.PORTRAIT_4_5 -> 20.dp
                                }
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isSelected) CyanSecondary else TextMuted,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ratio.label,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StyleChipsRow(
    selectedStyle: VideoStyle,
    onSelect: (VideoStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(VideoStyle.entries.toTypedArray()) { style ->
            val isSelected = style == selectedStyle
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) Color(style.colorHex).copy(alpha = 0.25f) else StudioSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) Color(style.colorHex) else StudioOutline
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelect(style) }
                    .testTag("style_chip_${style.name}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(style.colorHex))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = style.displayName,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CameraMotionGrid(
    selectedMotion: CameraMotion,
    onSelect: (CameraMotion) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val pairs = CameraMotion.entries.chunked(2)
        pairs.forEach { rowMotions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowMotions.forEach { motion ->
                    val isSelected = motion == selectedMotion
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) CyanSecondary.copy(alpha = 0.15f) else StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) CyanSecondary else StudioOutline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(motion) }
                            .testTag("camera_motion_${motion.name}")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = motion.displayName,
                                    color = if (isSelected) CyanSecondary else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = CyanSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = motion.description,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CinematicVideoPlayerCanvas(
    prompt: String,
    style: String,
    aspectRatio: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cinematic_anim")
    
    // Atmospheric pulsing glow
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Holographic camera scan line
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan"
    )

    // Optical rotation / camera drift angle
    val cameraAngle by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF070913))
            .border(1.dp, StudioOutline, RoundedCornerShape(16.dp))
    ) {
        // Dynamic procedural rendering canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Deep background gradient
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4C1D95).copy(alpha = glowAlpha * 0.7f),
                        Color(0xFF1E1B4B),
                        Color(0xFF030712)
                    ),
                    center = Offset(width * 0.5f, height * 0.45f),
                    radius = width.coerceAtLeast(height) * 0.8f
                )
            )

            // Dynamic grid lines simulating 3D perspective
            val horizon = height * 0.65f
            for (i in 0..12) {
                val startX = (width / 12) * i
                drawLine(
                    color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                    start = Offset(width * 0.5f, horizon),
                    end = Offset(startX, height),
                    strokeWidth = 1.2f
                )
            }

            // Moving horizontal grid lines
            for (step in 1..5) {
                val lineY = horizon + (height - horizon) * (step / 5f)
                drawLine(
                    color = Color(0xFF06B6D4).copy(alpha = 0.2f),
                    start = Offset(0f, lineY),
                    end = Offset(width, lineY),
                    strokeWidth = 1.2f
                )
            }

            // Focal energy orb / AI subject simulation
            val orbRadius = 55.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF06B6D4).copy(alpha = glowAlpha),
                        Color(0xFF8B5CF6).copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.5f, horizon * 0.6f),
                    radius = orbRadius * 1.5f
                ),
                radius = orbRadius * 1.5f,
                center = Offset(width * 0.5f, horizon * 0.6f)
            )

            // Cinematic laser scan line
            val currentScanY = height * scanLineY
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF06B6D4).copy(alpha = 0.6f),
                        Color(0xFF8B5CF6).copy(alpha = 0.9f),
                        Color(0xFF06B6D4).copy(alpha = 0.6f),
                        Color.Transparent
                    )
                ),
                start = Offset(0f, currentScanY),
                end = Offset(width, currentScanY),
                strokeWidth = 2f
            )

            // Film frame viewfinder markers
            val markerLength = 20.dp.toPx()
            val inset = 16.dp.toPx()
            // Top-left
            drawLine(Color.White.copy(alpha = 0.4f), Offset(inset, inset), Offset(inset + markerLength, inset), 2f)
            drawLine(Color.White.copy(alpha = 0.4f), Offset(inset, inset), Offset(inset, inset + markerLength), 2f)
            // Top-right
            drawLine(Color.White.copy(alpha = 0.4f), Offset(width - inset, inset), Offset(width - inset - markerLength, inset), 2f)
            drawLine(Color.White.copy(alpha = 0.4f), Offset(width - inset, inset), Offset(width - inset, inset + markerLength), 2f)
            // Bottom-left
            drawLine(Color.White.copy(alpha = 0.4f), Offset(inset, height - inset), Offset(inset + markerLength, height - inset), 2f)
            drawLine(Color.White.copy(alpha = 0.4f), Offset(inset, height - inset), Offset(inset, height - inset - markerLength), 2f)
            // Bottom-right
            drawLine(Color.White.copy(alpha = 0.4f), Offset(width - inset, height - inset), Offset(width - inset - markerLength, height - inset), 2f)
            drawLine(Color.White.copy(alpha = 0.4f), Offset(width - inset, height - inset), Offset(width - inset, height - inset - markerLength), 2f)
        }

        // Overlay video specs and live simulation tag
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) Color(0xFF10B981) else Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying) "AI RENDER • PLAYING" else "PAUSED",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "$style • $aspectRatio",
                        color = Color(0xFFCFFAFE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Lower prompt label
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = prompt,
                    color = Color(0xFFF1F5F9),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ActiveGenerationBanner(
    progress: Int,
    stage: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E1B4B),
        border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress / 100f },
                    color = CyanSecondary,
                    trackColor = StudioOutline,
                    modifier = Modifier.size(42.dp),
                    strokeWidth = 3.5.dp
                )
                Text(
                    text = "$progress%",
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Video Generation in Progress",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stage.ifBlank { "Processing video frames..." },
                    color = CyanSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
