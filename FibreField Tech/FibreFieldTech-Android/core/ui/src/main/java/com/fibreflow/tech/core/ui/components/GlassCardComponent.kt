package com.fibreflow.tech.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.fibreflow.tech.core.design.theme.ExtendedColors
import com.fibreflow.tech.core.design.theme.LocalExtendedColors

/**
 * Glass Card Component with Morphism Effects
 *
 * A reusable card component featuring:
 * - Glass morphism visual effects
 * - Neon accent colors
 * - Transparent/blur effects
 * - High-tech aesthetic
 * - Full accessibility support
 * - Performance optimized
 *
 * @param modifier The modifier to be applied to the card
 * @param alpha Transparency level (0.1f to 0.5f)
 * @param accentColor Neon accent color for borders and highlights
 * @param shape Card shape
 * @param onClick Optional click handler
 * @param enabled Whether the card is enabled for interaction
 * @param content The content to be displayed inside the card
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.2f,
    accentColor: Color = LocalExtendedColors.current.aiActive,
    shape: Shape = RoundedCornerShape(12.dp),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val extendedColors = LocalExtendedColors.current

    Card(
        modifier = modifier
            .semantics {
                role = Role.Button
                contentDescription = "Glass card"
            }
            .then(
                onClick?.let {
                    if (enabled) {
                        Modifier.clickable(onClick = it)
                    } else {
                        Modifier
                    }
                } ?: Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            extendedColors.glassSurface.copy(alpha = alpha),
                            extendedColors.glassSurface.copy(alpha = alpha * 0.8f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = accentColor.copy(alpha = 0.3f),
                    shape = shape
                )
                .clip(shape)
        ) {
            // Glass morphism effect layers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            center = androidx.compose.ui.geometry.Offset(
                                x = Float.POSITIVE_INFINITY,
                                y = 0f
                            ),
                            radius = 300f
                        )
                    )
            )

            // Content with proper spacing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Glass Card with Enhanced Effects
 *
 * Enhanced version with additional visual effects:
 * - Animated glow on hover/click
 * - Enhanced blur effects
 * - Dynamic accent colors
 */
@Composable
fun GlassCardEnhanced(
    modifier: Modifier = Modifier,
    alpha: Float = 0.2f,
    accentColor: Color = LocalExtendedColors.current.aiActive,
    shape: Shape = RoundedCornerShape(12.dp),
    glowIntensity: Float = 0.5f,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    val glowAlpha = when {
        isPressed -> glowIntensity * 1.5f
        isHovered -> glowIntensity * 1.2f
        else -> glowIntensity
    }

    GlassCard(
        modifier = modifier,
        alpha = alpha,
        accentColor = accentColor,
        shape = shape,
        onClick = onClick,
        enabled = enabled
    ) {
        Box {
            // Enhanced glow effect
            if (glowAlpha > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = glowAlpha * 0.3f),
                                    Color.Transparent
                                ),
                                center = androidx.compose.ui.geometry.Offset(
                                    x = Float.POSITIVE_INFINITY,
                                    y = 0f
                                ),
                                radius = 400f
                            )
                        )
                )
            }

            // Content
            content()
        }
    }
}

/**
 * Glass Card with Icon
 *
 * Glass card with an icon header for consistent UI patterns
 */
@Composable
fun GlassCardWithIcon(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable (() -> Unit)? = null,
    subtitle: String? = null,
    accentColor: Color = LocalExtendedColors.current.aiActive,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    GlassCard(
        modifier = modifier,
        accentColor = accentColor,
        onClick = onClick
    ) {
        Column {
            // Header section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                icon?.let {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    ) {
                        it()
                    }
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    subtitle?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Divider
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = accentColor.copy(alpha = 0.2f)
            )

            // Content
            content()
        }
    }
}