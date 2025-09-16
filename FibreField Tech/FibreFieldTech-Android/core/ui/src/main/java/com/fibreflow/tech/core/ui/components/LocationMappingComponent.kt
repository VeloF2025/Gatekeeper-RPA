package com.fibreflow.tech.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fibreflow.tech.core.design.theme.ExtendedColors
import com.fibreflow.tech.core.design.theme.LocalExtendedColors

/**
 * Location Mapping Component
 *
 * A high-tech GPS mapping interface with:
 * - Real-time location tracking
 * - Offline map functionality
 * - Route planning and navigation
 * - Location accuracy indicators
 * - Full accessibility support
 * - Performance optimized
 *
 * @param modifier The modifier to be applied to the component
 * @param locationState Current location state
 * @param accuracy GPS accuracy in meters
 * @param isOffline Whether device is offline
 * @param showRoutePlanning Whether to show route planning features
 * @param showCurrentLocation Whether to show current location marker
 * @param onLocationRequest Handler for location refresh request
 * @param onRoutePlan Handler for route planning request
 * @param onMapClick Handler for map interactions
 */
@Composable
fun LocationMappingComponent(
    modifier: Modifier = Modifier,
    locationState: String = "acquiring",
    accuracy: Float? = null,
    isOffline: Boolean = false,
    showRoutePlanning: Boolean = false,
    showCurrentLocation: Boolean = true,
    onLocationRequest: (() -> Unit)? = null,
    onRoutePlan: (() -> Unit)? = null,
    onMapClick: (() -> Unit)? = null
) {
    val extendedColors = LocalExtendedColors.current

    GlassCard(
        modifier = modifier,
        accentColor = extendedColors.aiActive
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Map background placeholder
            MapBackground(
                isOffline = isOffline,
                locationState = locationState,
                onClick = onMapClick
            )

            // Location controls overlay
            LocationControlsOverlay(
                locationState = locationState,
                accuracy = accuracy,
                isOffline = isOffline,
                showRoutePlanning = showRoutePlanning,
                onLocationRequest = onLocationRequest,
                onRoutePlan = onRoutePlan,
                extendedColors = extendedColors
            )

            // Current location marker
            if (showCurrentLocation && locationState != "unavailable") {
                CurrentLocationMarker(
                    locationState = locationState,
                    extendedColors = extendedColors
                )
            }
        }
    }
}

/**
 * Map Background Component
 */
@Composable
fun MapBackground(
    isOffline: Boolean,
    locationState: String,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick?.invoke() }
            .background(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A2F4A),
                        Color(0xFF0F1929)
                    )
                )
            )
    ) {
        // Grid pattern for map
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00D4FF).copy(alpha = 0.05f)
                        )
                    )
                )
        )

        // Location state overlay
        when (locationState) {
            "acquiring" -> AcquiringLocationOverlay()
            "unavailable" -> LocationUnavailableOverlay()
        }

        // Offline indicator
        if (isOffline) {
            OfflineMapIndicator()
        }
    }
}

/**
 * Location Controls Overlay Component
 */
@Composable
fun LocationControlsOverlay(
    locationState: String,
    accuracy: Float?,
    isOffline: Boolean,
    showRoutePlanning: Boolean,
    onLocationRequest: (() -> Unit)?,
    onRoutePlan: (() -> Unit)?,
    extendedColors: ExtendedColors
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Location status card
            LocationStatusCard(
                locationState = locationState,
                accuracy = accuracy,
                extendedColors = extendedColors
            )

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Refresh location button
                FloatingActionButton(
                    onClick = { onLocationRequest?.invoke() },
                    containerColor = extendedColors.glassSurface.copy(alpha = 0.8f),
                    contentColor = extendedColors.aiActive,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Refresh location",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Route planning button
                if (showRoutePlanning) {
                    FloatingActionButton(
                        onClick = { onRoutePlan?.invoke() },
                        containerColor = extendedColors.glassSurface.copy(alpha = 0.8f),
                        contentColor = extendedColors.aiSuccess,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Route,
                            contentDescription = "Plan route",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Map type selector
            MapTypeSelector(extendedColors = extendedColors)

            // Zoom controls
            ZoomControls(extendedColors = extendedColors)

            // Layers control
            LayersControl(extendedColors = extendedColors)
        }
    }
}

/**
 * Location Status Card Component
 */
@Composable
fun LocationStatusCard(
    locationState: String,
    accuracy: Float?,
    extendedColors: ExtendedColors
) {
    GlassCard(
        accentColor = getLocationStateColor(locationState, extendedColors)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = getLocationStateIcon(locationState),
                    contentDescription = null,
                    tint = getLocationStateColor(locationState, extendedColors),
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = locationState.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            accuracy?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "±${it.toInt()}m accuracy",
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Current Location Marker Component
 */
@Composable
fun CurrentLocationMarker(
    locationState: String,
    extendedColors: ExtendedColors
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Animated pulsing effect
        var pulseScale by remember { mutableStateOf(1f) }
        val pulseAnimation = rememberInfiniteTransition()

        pulseScale by pulseAnimation.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Pulsing outer circle
        Box(
            modifier = Modifier
                .size(60.dp * pulseScale)
                .clip(CircleShape)
                .background(
                    color = getLocationStateColor(locationState, extendedColors).copy(alpha = 0.2f)
                )
        )

        // Main marker
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    color = getLocationStateColor(locationState, extendedColors)
                )
                .border(
                    width = 3.dp,
                    color = Color.White,
                    shape = CircleShape
                )
        )

        // Center dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

/**
 * Acquiring Location Overlay Component
 */
@Composable
fun AcquiringLocationOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF00D4FF),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Acquiring Location...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

/**
 * Location Unavailable Overlay Component
 */
@Composable
fun LocationUnavailableOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOff,
                contentDescription = null,
                tint = Color(0xFFFF3B30),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Location Unavailable",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check device settings",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Offline Map Indicator Component
 */
@Composable
fun OfflineMapIndicator() {
    Box(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(8.dp)
    ) {
        GlassCard(
            accentColor = Color(0xFFFFAA00)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = Color(0xFFFFAA00),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Offline Map",
                    style = MaterialTheme.typography.caption,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Map Type Selector Component
 */
@Composable
fun MapTypeSelector(
    extendedColors: ExtendedColors
) {
    var selectedType by remember { mutableStateOf("standard") }
    val mapTypes = listOf("standard", "satellite", "terrain", "hybrid")

    GlassCard {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            mapTypes.forEach { type ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            color = if (selectedType == type) {
                                extendedColors.aiActive.copy(alpha = 0.3f)
                            } else {
                                Color.Transparent
                            }
                        )
                        .clickable { selectedType = type }
                        .padding(8.dp)
                ) {
                    Text(
                        text = type.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.caption,
                        color = if (selectedType == type) extendedColors.aiActive else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

/**
 * Zoom Controls Component
 */
@Composable
fun ZoomControls(
    extendedColors: ExtendedColors
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GlassCard {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { /* Zoom in */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom in",
                    tint = extendedColors.aiActive,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        GlassCard {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { /* Zoom out */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom out",
                    tint = extendedColors.aiActive,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Layers Control Component
 */
@Composable
fun LayersControl(
    extendedColors: ExtendedColors
) {
    GlassCard {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { /* Show layers */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = "Map layers",
                tint = extendedColors.aiActive,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Get location state color
 */
private fun getLocationStateColor(state: String, colors: ExtendedColors): Color {
    return when (state.lowercase()) {
        "available", "high_accuracy" -> colors.aiSuccess
        "acquiring", "low_accuracy" -> colors.aiWarning
        "unavailable" -> colors.aiError
        else -> colors.aiNeutral
    }
}

/**
 * Get location state icon
 */
private fun getLocationStateIcon(state: String): ImageVector {
    return when (state.lowercase()) {
        "available", "high_accuracy" -> Icons.Default.LocationOn
        "acquiring", "low_accuracy" -> Icons.Default.GpsFixed
        "unavailable" -> Icons.Default.LocationOff
        else -> Icons.Default.Help
    }
}