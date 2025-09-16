package com.fibreflow.tech.core.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Network Status Bar Component
 *
 * A high-tech status bar showing network connectivity with:
 * - Real-time connectivity status
 * - Signal strength visualization
 * - Network type indicators
 * - Offline/online state management
 * - Performance optimized
 * - Full accessibility support
 *
 * @param networkStatus Current network status ("excellent", "good", "poor", "offline", "connecting")
 * @param modifier The modifier to be applied to the status bar
 * @param networkType Type of network ("WiFi", "5G", "4G", etc.)
 * @param signalStrength Signal strength (0-5)
 * @param isCompact Whether to show compact view
 * @param showDetailedInfo Whether to show detailed information
 * @param onClick Optional click handler
 * @param onRefresh Optional refresh handler
 */
@Composable
fun NetworkStatusBar(
    networkStatus: String,
    modifier: Modifier = Modifier,
    networkType: String? = null,
    signalStrength: Int? = null,
    isCompact: Boolean = false,
    showDetailedInfo: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null
) {
    val extendedColors = LocalExtendedColors.current

    // Get network status information
    val statusInfo = getNetworkStatusInfo(networkStatus, extendedColors)

    GlassCard(
        modifier = modifier,
        accentColor = statusInfo.color,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Network status icon and info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status icon
                Icon(
                    imageVector = statusInfo.icon,
                    contentDescription = null,
                    tint = statusInfo.color,
                    modifier = Modifier.size(20.dp)
                )

                if (!isCompact) {
                    Column {
                        Text(
                            text = statusInfo.label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = Color.White
                        )

                        networkType?.let { type ->
                            Text(
                                text = type,
                                style = MaterialTheme.typography.caption,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Signal strength indicator
            signalStrength?.let { strength ->
                SignalStrengthIndicator(
                    strength = strength,
                    color = statusInfo.color,
                    isCompact = isCompact
                )
            }

            // Refresh button
            onRefresh?.let { refresh ->
                IconButton(
                    onClick = refresh,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                        contentDescription = "Refresh network status",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Detailed information section
        if (showDetailedInfo && !isCompact) {
            Spacer(modifier = Modifier.height(8.dp))
            NetworkDetailedInfo(
                networkStatus = networkStatus,
                networkType = networkType,
                signalStrength = signalStrength,
                extendedColors = extendedColors
            )
        }
    }
}

/**
 * Signal Strength Indicator Component
 */
@Composable
fun SignalStrengthIndicator(
    strength: Int,
    color: Color,
    isCompact: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(5) { index ->
            val isActive = index < strength
            val height = when (index) {
                0, 1 -> if (isCompact) 4.dp else 6.dp
                2, 3 -> if (isCompact) 6.dp else 8.dp
                else -> if (isCompact) 8.dp else 10.dp
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(height)
                    .clip(CircleShape)
                    .background(
                        color = if (isActive) color else color.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

/**
 * Network Detailed Information Component
 */
@Composable
fun NetworkDetailedInfo(
    networkStatus: String,
    networkType: String? = null,
    signalStrength: Int? = null,
    extendedColors: ExtendedColors = LocalExtendedColors.current
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Connection quality
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Connection Quality",
                style = MaterialTheme.typography.caption,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = networkStatus.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.caption,
                color = getNetworkStatusInfo(networkStatus, extendedColors).color
            )
        }

        // Network type
        networkType?.let { type ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Network Type",
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = type,
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Signal strength
        signalStrength?.let { strength ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Signal Strength",
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "$strength/5",
                    style = MaterialTheme.typography.caption,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Enhanced Network Status Bar with real-time updates
 */
@Composable
fun NetworkStatusBarEnhanced(
    networkStatus: String,
    modifier: Modifier = Modifier,
    networkType: String? = null,
    signalStrength: Int? = null,
    networkSpeed: String? = null,
    latency: String? = null,
    dataUsage: String? = null,
    securityStatus: String? = null,
    onRefresh: (() -> Unit)? = null
) {
    val extendedColors = LocalExtendedColors.current
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(networkStatus) {
        isRefreshing = false
    }

    GlassCard(
        modifier = modifier,
        accentColor = getNetworkStatusInfo(networkStatus, extendedColors).color
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NetworkStatusBar(
                    networkStatus = networkStatus,
                    networkType = networkType,
                    signalStrength = signalStrength,
                    isCompact = true,
                    onRefresh = {
                        isRefreshing = true
                        onRefresh?.invoke()
                    }
                )

                // Refresh animation
                if (isRefreshing) {
                    androidx.compose.animation.core.AnimatedVisibility(
                        visible = isRefreshing,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = getNetworkStatusInfo(networkStatus, extendedColors).color,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            // Detailed information
            Spacer(modifier = Modifier.height(12.dp))

            // Network metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                networkSpeed?.let { speed ->
                    NetworkMetricItem(
                        label = "Speed",
                        value = speed,
                        icon = androidx.compose.material.icons.Icons.Default.Speed
                    )
                }

                latency?.let { lat ->
                    NetworkMetricItem(
                        label = "Latency",
                        value = lat,
                        icon = androidx.compose.material.icons.Icons.Default.Timer
                    )
                }

                dataUsage?.let { usage ->
                    NetworkMetricItem(
                        label = "Data",
                        value = usage,
                        icon = androidx.compose.material.icons.Icons.Default.DataUsage
                    )
                }
            }

            // Security status
            securityStatus?.let { security ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Security,
                            contentDescription = null,
                            tint = if (security == "secured") extendedColors.aiSuccess else extendedColors.aiError,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = security.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.caption,
                            color = if (security == "secured") extendedColors.aiSuccess else extendedColors.aiError
                        )
                    }
                }
            }
        }
    }
}

/**
 * Network Metric Item Component
 */
@Composable
fun NetworkMetricItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.caption.copy(
                fontWeight = FontWeight.Medium
            ),
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

/**
 * Get network status information based on current status
 */
private fun getNetworkStatusInfo(
    status: String,
    colors: ExtendedColors
): NetworkStatusInfo {
    return when (status.lowercase()) {
        "excellent" -> NetworkStatusInfo(
            color = colors.networkGood,
            label = "Excellent",
            icon = androidx.compose.material.icons.Icons.Default.SignalCellular4G
        )
        "good" -> NetworkStatusInfo(
            color = colors.networkGood.copy(alpha = 0.8f),
            label = "Good",
            icon = androidx.compose.material.icons.Icons.Default.SignalCellular3G
        )
        "poor" -> NetworkStatusInfo(
            color = colors.networkPoor,
            label = "Poor",
            icon = androidx.compose.material.icons.Icons.Default.SignalCellular1G
        )
        "offline" -> NetworkStatusInfo(
            color = colors.networkOffline,
            label = "Offline",
            icon = androidx.compose.material.icons.Icons.Default.SignalCellularConnectedNoInternet0G
        )
        "connecting" -> NetworkStatusInfo(
            color = colors.aiWarning,
            label = "Connecting",
            icon = androidx.compose.material.icons.Icons.Default.SignalCellularConnectedNoInternet0G
        )
        else -> NetworkStatusInfo(
            color = colors.aiNeutral,
            label = "Unknown",
            icon = androidx.compose.material.icons.Icons.Default.Help
        )
    }
}

/**
 * Network status information data class
 */
private data class NetworkStatusInfo(
    val color: Color,
    val label: String,
    val icon: ImageVector
)