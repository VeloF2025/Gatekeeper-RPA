package com.fibreflow.tech.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fibreflow.tech.core.design.theme.ExtendedColors
import com.fibreflow.tech.core.design.theme.LocalExtendedColors
import kotlinx.coroutines.delay

/**
 * AI Status Indicator Component
 *
 * A high-tech status indicator showing AI model state with:
 * - Animated status transitions
 * - Neon accent colors
 * - Loading/ready/error/processing states
 * - Real-time updates
 * - Full accessibility support
 * - Performance optimized animations
 *
 * @param status Current AI status ("loading", "ready", "error", "processing")
 * @param modifier The modifier to be applied to the indicator
 * @param onClick Optional click handler
 * @param size Indicator size
 * @param showLabel Whether to show status label
 * @param animateTransitions Whether to animate status transitions
 */
@Composable
fun AiStatusIndicator(
    status: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    size: AiStatusSize = AiStatusSize.Medium,
    showLabel: Boolean = true,
    animateTransitions: Boolean = true
) {
    val extendedColors = LocalExtendedColors.current
    val infiniteTransition = rememberInfiniteTransition()

    // Determine status properties
    val statusInfo = getStatusInfo(status, extendedColors)

    // Animation for rotating/loading effect
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Pulsing animation for active states
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    val isPulsing = status == "processing" || status == "loading"

    Column(
        modifier = modifier
            .semantics {
                role = Role.Button
                contentDescription = "AI status: $status"
            }
            .then(
                onClick?.let {
                    Modifier.clickable(onClick = it)
                } ?: Modifier
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Status indicator with glow effect
        Box(
            modifier = Modifier
                .size(size.size.dp)
                .then(
                    if (isPulsing) {
                        Modifier
                            .size((size.size.dp * pulseScale))
                            .background(
                                color = statusInfo.color.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    } else {
                        Modifier
                    }
                )
        ) {
            // Main status circle
            Box(
                modifier = Modifier
                    .size(size.size.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                statusInfo.color.copy(alpha = 0.8f),
                                statusInfo.color.copy(alpha = 0.4f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = statusInfo.color,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Status icon
                if (status == "loading" || status == "processing") {
                    androidx.compose.material3.Icon(
                        painter = statusInfo.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(size.iconSize.dp)
                            .rotate(if (status == "loading") rotation else 0f)
                    )
                } else {
                    androidx.compose.material3.Icon(
                        painter = statusInfo.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(size.iconSize.dp)
                    )
                }
            }

            // Glow effect
            if (isPulsing) {
                Box(
                    modifier = Modifier
                        .size((size.size.dp * 1.5f))
                        .background(
                            color = statusInfo.color.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )
            }
        }

        // Status label
        if (showLabel) {
            Spacer(modifier = Modifier.height(size.spacing.dp))
            Text(
                text = statusInfo.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = size.fontSize.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Enhanced AI Status Indicator with additional features
 */
@Composable
fun AiStatusIndicatorEnhanced(
    status: String,
    modifier: Modifier = Modifier,
    confidence: Float? = null,
    lastUpdate: String? = null,
    onClick: (() -> Unit)? = null,
    size: AiStatusSize = AiStatusSize.Medium
) {
    GlassCard(
        modifier = modifier,
        accentColor = getStatusInfo(status, LocalExtendedColors.current).color,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AiStatusIndicator(
                status = status,
                size = size,
                showLabel = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = getStatusInfo(status, LocalExtendedColors.current).label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            confidence?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Confidence: ${(it * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            lastUpdate?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Updated: $it",
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * AI Status Size options
 */
enum class AiStatusSize(
    val size: Int,
    val iconSize: Int,
    val fontSize: Int,
    val spacing: Int
) {
    Small(size = 24, iconSize = 12, fontSize = 10, spacing = 4),
    Medium(size = 32, iconSize = 16, fontSize = 12, spacing = 6),
    Large(size = 48, iconSize = 24, fontSize = 14, spacing = 8),
    ExtraLarge(size = 64, iconSize = 32, fontSize = 16, spacing = 10)
}

/**
 * Get status information based on current status
 */
private fun getStatusInfo(
    status: String,
    colors: ExtendedColors
): StatusInfo {
    return when (status.lowercase()) {
        "loading" -> StatusInfo(
            color = colors.aiWarning,
            label = "AI Loading",
            icon = androidx.compose.material.icons.Icons.Default.Refresh
        )
        "ready" -> StatusInfo(
            color = colors.aiSuccess,
            label = "AI Ready",
            icon = androidx.compose.material.icons.Icons.Default.CheckCircle
        )
        "error" -> StatusInfo(
            color = colors.aiError,
            label = "AI Error",
            icon = androidx.compose.material.icons.Icons.Default.Error
        )
        "processing" -> StatusInfo(
            color = colors.aiActive,
            label = "Processing",
            icon = androidx.compose.material.icons.Icons.Default.Settings
        )
        else -> StatusInfo(
            color = colors.aiNeutral,
            label = "Unknown",
            icon = androidx.compose.material.icons.Icons.Default.Help
        )
    }
}

/**
 * Status information data class
 */
private data class StatusInfo(
    val color: Color,
    val label: String,
    val icon: Painter
)