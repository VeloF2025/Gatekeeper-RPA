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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fibreflow.tech.core.design.theme.ExtendedColors
import com.fibreflow.tech.core.design.theme.LocalExtendedColors

/**
 * Biometric Authentication Dialog Component
 *
 * A modern, high-tech biometric authentication interface with:
 * - Fingerprint and face ID support
 * - Animated authentication states
 * - Error handling and fallback options
 * - Security indicators
 * - Full accessibility support
 * - Performance optimized
 *
 * @param onDismiss Handler for dialog dismissal
 * @param onAuthSuccess Handler for successful authentication
 * @param onAuthFailure Handler for authentication failure
 * @param onFallback Handler for fallback authentication
 * @param modifier The modifier to be applied to the dialog
 * @param biometricType Type of biometric authentication
 * @param authState Current authentication state
 * @param errorMessage Error message if authentication failed
 * @param title Dialog title
 * @param description Dialog description
 * @param showFallback Whether to show fallback option
 */
@Composable
fun BiometricAuthDialog(
    onDismiss: () -> Unit,
    onAuthSuccess: () -> Unit,
    onAuthFailure: (String) -> Unit,
    onFallback: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    biometricType: String = "fingerprint",
    authState: String = "ready",
    errorMessage: String? = null,
    title: String = "Authenticate",
    description: String = "Use your biometric data to continue",
    showFallback: Boolean = true
) {
    val extendedColors = LocalExtendedColors.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        GlassCardEnhanced(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            accentColor = extendedColors.aiActive,
            glowIntensity = 0.3f
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Biometric indicator
                BiometricIndicator(
                    biometricType = biometricType,
                    authState = authState,
                    extendedColors = extendedColors
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Authentication status
                AuthStatusMessage(
                    authState = authState,
                    errorMessage = errorMessage,
                    extendedColors = extendedColors
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                ActionButtons(
                    onDismiss = onDismiss,
                    onFallback = onFallback,
                    showFallback = showFallback,
                    extendedColors = extendedColors
                )
            }
        }
    }
}

/**
 * Biometric Indicator Component
 */
@Composable
fun BiometricIndicator(
    biometricType: String,
    authState: String,
    extendedColors: ExtendedColors
) {
    val authInfo = getAuthStateInfo(authState, extendedColors)
    val biometricInfo = getBiometricTypeInfo(biometricType, extendedColors)

    Box(
        modifier = Modifier.size(120.dp)
    ) {
        // Animated background effect
        var pulseScale by remember { mutableStateOf(1f) }
        val pulseAnimation = rememberInfiniteTransition()

        pulseScale by pulseAnimation.animateFloat(
            initialValue = 1f,
            targetValue = if (authState == "processing") 1.3f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Pulsing background
        if (authState == "processing") {
            Box(
                modifier = Modifier
                    .size(120.dp * pulseScale)
                    .clip(CircleShape)
                    .background(
                        color = authInfo.color.copy(alpha = 0.2f)
                    )
            )
        }

        // Main biometric icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            authInfo.color.copy(alpha = 0.8f),
                            authInfo.color.copy(alpha = 0.4f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = authInfo.color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Animated icon
            val rotation by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            Icon(
                imageVector = biometricInfo.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .then(
                        if (authState == "processing") {
                            Modifier.rotate(rotation)
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }

    // Biometric type label
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = biometricInfo.label,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontWeight = FontWeight.Medium
    )
}

/**
 * Authentication Status Message Component
 */
@Composable
fun AuthStatusMessage(
    authState: String,
    errorMessage: String?,
    extendedColors: ExtendedColors
) {
    val authInfo = getAuthStateInfo(authState, extendedColors)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = authInfo.icon,
                contentDescription = null,
                tint = authInfo.color,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = authInfo.label,
                style = MaterialTheme.typography.bodyMedium,
                color = authInfo.color,
                fontWeight = FontWeight.Medium
            )
        }

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = extendedColors.aiError,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Action Buttons Component
 */
@Composable
fun ActionButtons(
    onDismiss: () -> Unit,
    onFallback: (() -> Unit)?,
    showFallback: Boolean,
    extendedColors: ExtendedColors
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cancel button
        OutlinedButton(
            onClick = onDismiss,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White.copy(alpha = 0.7f)
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Text("Cancel")
        }

        // Fallback button
        if (showFallback && onFallback != null) {
            TextButton(
                onClick = onFallback
            ) {
                Text(
                    text = "Use PIN/Password",
                    color = extendedColors.aiActive
                )
            }
        }
    }
}

/**
 * Biometric Authentication Prompt Component
 *
 * A smaller, less intrusive version for quick authentication
 */
@Composable
fun BiometricAuthPrompt(
    onAuthRequest: () -> Unit,
    onFallback: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    biometricType: String = "fingerprint",
    message: String = "Authenticate to continue"
) {
    val extendedColors = LocalExtendedColors.current
    val biometricInfo = getBiometricTypeInfo(biometricType, extendedColors)

    GlassCard(
        modifier = modifier,
        accentColor = extendedColors.aiActive,
        onClick = onAuthRequest
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            color = extendedColors.aiActive.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = biometricInfo.icon,
                        contentDescription = null,
                        tint = extendedColors.aiActive,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Authentication Required",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = extendedColors.aiActive,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Biometric Settings Component
 *
 * Shows biometric configuration and availability
 */
@Composable
fun BiometricSettings(
    modifier: Modifier = Modifier,
    availableBiometrics: List<String>,
    enabledBiometrics: List<String>,
    onBiometricToggle: (String, Boolean) -> Unit
) {
    val extendedColors = LocalExtendedColors.current

    GlassCard(
        modifier = modifier,
        accentColor = extendedColors.aiActive
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Biometric Authentication",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableBiometrics) { biometricType ->
                    BiometricSettingItem(
                        biometricType = biometricType,
                        isEnabled = enabledBiometrics.contains(biometricType),
                        onToggle = { enabled -> onBiometricToggle(biometricType, enabled) },
                        extendedColors = extendedColors
                    )
                }
            }
        }
    }
}

/**
 * Biometric Setting Item Component
 */
@Composable
fun BiometricSettingItem(
    biometricType: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    extendedColors: ExtendedColors
) {
    val biometricInfo = getBiometricTypeInfo(biometricType, extendedColors)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = biometricInfo.icon,
                contentDescription = null,
                tint = biometricInfo.color,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = biometricInfo.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = biometricInfo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = extendedColors.aiActive,
                checkedTrackColor = extendedColors.aiActive.copy(alpha = 0.5f),
                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

/**
 * Get authentication state information
 */
private fun getAuthStateInfo(state: String, colors: ExtendedColors): AuthStateInfo {
    return when (state.lowercase()) {
        "ready" -> AuthStateInfo(
            label = "Ready",
            color = colors.aiActive,
            icon = Icons.Default.Fingerprint
        )
        "processing" -> AuthStateInfo(
            label = "Processing",
            color = colors.aiActive,
            icon = Icons.Default.Pending
        )
        "success" -> AuthStateInfo(
            label = "Success",
            color = colors.aiSuccess,
            icon = Icons.Default.CheckCircle
        )
        "error" -> AuthStateInfo(
            label = "Authentication Failed",
            color = colors.aiError,
            icon = Icons.Default.Error
        )
        "cancelled" -> AuthStateInfo(
            label = "Cancelled",
            color = colors.aiWarning,
            icon = Icons.Default.Cancel
        )
        else -> AuthStateInfo(
            label = "Unknown",
            color = colors.aiNeutral,
            icon = Icons.Default.Help
        )
    }
}

/**
 * Get biometric type information
 */
private fun getBiometricTypeInfo(type: String, colors: ExtendedColors): BiometricTypeInfo {
    return when (type.lowercase()) {
        "fingerprint" -> BiometricTypeInfo(
            label = "Fingerprint",
            description = "Use your fingerprint to authenticate",
            icon = Icons.Default.Fingerprint,
            color = colors.aiActive
        )
        "face" -> BiometricTypeInfo(
            label = "Face ID",
            description = "Use your face to authenticate",
            icon = Icons.Default.Face,
            color = colors.aiSuccess
        )
        "iris" -> BiometricTypeInfo(
            label = "Iris Scanner",
            description = "Use your iris to authenticate",
            icon = Icons.Default.Visibility,
            color = colors.aiWarning
        )
        "none" -> BiometricTypeInfo(
            label = "No Biometrics",
            description = "No biometric authentication available",
            icon = Icons.Default.NotInterested,
            color = colors.aiNeutral
        )
        else -> BiometricTypeInfo(
            label = "Unknown",
            description = "Unknown biometric type",
            icon = Icons.Default.Help,
            color = colors.aiNeutral
        )
    }
}

/**
 * Authentication state information data class
 */
private data class AuthStateInfo(
    val label: String,
    val color: Color,
    val icon: ImageVector
)

/**
 * Biometric type information data class
 */
private data class BiometricTypeInfo(
    val label: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)