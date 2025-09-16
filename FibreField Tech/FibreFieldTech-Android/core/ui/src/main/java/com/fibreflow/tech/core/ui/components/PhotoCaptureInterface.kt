package com.fibreflow.tech.core.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
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
 * Photo Capture Interface Component
 *
 * A high-tech 9-step photo capture workflow with:
 * - Step-by-step guided photography
 * - Real-time AI validation
 * - Camera integration
 * - Progress tracking
 * - Offline support
 * - Full accessibility
 * - Performance optimized
 *
 * @param currentStep Current step in the 9-step workflow
 * @param totalSteps Total number of steps (typically 9)
 * @param modifier The modifier to be applied to the interface
 * @param onNextStep Handler for moving to next step
 * @param onPreviousStep Handler for moving to previous step
 * @param onPhotoCapture Handler for photo capture
 * @param showPreview Whether to show real-time preview
 * @param validationState Current AI validation state
 * @param stepGuidance Step-specific guidance text
 * @param cameraPermissionState Camera permission state
 * @param isOffline Whether device is offline
 */
@Composable
fun PhotoCaptureInterface(
    currentStep: Int,
    totalSteps: Int = 9,
    modifier: Modifier = Modifier,
    onNextStep: (() -> Unit)? = null,
    onPreviousStep: (() -> Unit)? = null,
    onPhotoCapture: (() -> Unit)? = null,
    showPreview: Boolean = true,
    validationState: String? = null,
    stepGuidance: String? = null,
    cameraPermissionState: String = "granted",
    isOffline: Boolean = false,
    photoQuality: String? = null,
    cameraMode: String = "auto"
) {
    val extendedColors = LocalExtendedColors.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Camera preview background
        if (showPreview) {
            CameraPreviewArea(
                modifier = Modifier.fillMaxSize(),
                isOffline = isOffline,
                cameraPermissionState = cameraPermissionState
            )
        }

        // Main interface overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            // Header with progress
            PhotoCaptureHeader(
                currentStep = currentStep,
                totalSteps = totalSteps,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Main content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                // AI validation feedback
                validationState?.let { state ->
                    AiValidationFeedback(
                        validationState = state,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }

                // Step-specific guidance
                StepGuidanceArea(
                    step = currentStep,
                    guidance = stepGuidance,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }

            // Bottom controls
            PhotoCaptureControls(
                currentStep = currentStep,
                totalSteps = totalSteps,
                onNextStep = onNextStep,
                onPreviousStep = onPreviousStep,
                onPhotoCapture = onPhotoCapture,
                isOffline = isOffline,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Camera Preview Area Component
 */
@Composable
fun CameraPreviewArea(
    modifier: Modifier = Modifier,
    isOffline: Boolean = false,
    cameraPermissionState: String = "granted"
) {
    val extendedColors = LocalExtendedColors.current

    Box(
        modifier = modifier
            .background(Color.Black)
    ) {
        // Camera preview placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1F3A),
                            Color(0xFF0A0E27)
                        )
                    )
                )
        )

        // Permission state overlay
        when (cameraPermissionState) {
            "denied" -> PermissionRequiredOverlay("Camera permission required")
            "requesting" -> LoadingOverlay("Requesting camera permission...")
        }

        // Offline indicator
        if (isOffline) {
            OfflineIndicator(modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

/**
 * Photo Capture Header Component
 */
@Composable
fun PhotoCaptureHeader(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current

    GlassCard(
        modifier = modifier.padding(16.dp),
        accentColor = extendedColors.aiActive
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress text
            Text(
                text = "Step $currentStep of $totalSteps",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = currentStep.toFloat() / totalSteps,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = extendedColors.aiActive,
                trackColor = extendedColors.glassBorder
            )

            // Step title
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getStepTitle(currentStep),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

/**
 * Step Guidance Area Component
 */
@Composable
fun StepGuidanceArea(
    step: Int,
    guidance: String?,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current

    GlassCard(
        modifier = modifier,
        accentColor = extendedColors.aiActive
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = getStepIcon(step),
                contentDescription = null,
                tint = extendedColors.aiActive,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = guidance ?: getStepGuidance(step),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getStepDetails(step),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * Photo Capture Controls Component
 */
@Composable
fun PhotoCaptureControls(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    onNextStep: (() -> Unit)? = null,
    onPreviousStep: (() -> Unit)? = null,
    onPhotoCapture: (() -> Unit)? = null,
    isOffline: Boolean = false
) {
    val extendedColors = LocalExtendedColors.current

    GlassCard(
        modifier = modifier.padding(16.dp),
        accentColor = extendedColors.aiActive
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            if (currentStep > 1) {
                FloatingActionButton(
                    onClick = { onPreviousStep?.invoke() },
                    containerColor = extendedColors.glassSurface.copy(alpha = 0.5f),
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.NavigateBefore,
                        contentDescription = "Previous step"
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(56.dp))
            }

            // Capture button
            FloatingActionButton(
                onClick = { onPhotoCapture?.invoke() },
                containerColor = extendedColors.aiActive,
                contentColor = Color.Black,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Capture photo",
                    modifier = Modifier.size(36.dp)
                )
            }

            // Next/Skip button
            if (currentStep < totalSteps) {
                FloatingActionButton(
                    onClick = { onNextStep?.invoke() },
                    containerColor = extendedColors.aiSuccess,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.NavigateNext,
                        contentDescription = "Next step"
                    )
                }
            } else {
                FloatingActionButton(
                    onClick = { onNextStep?.invoke() },
                    containerColor = extendedColors.aiSuccess,
                    contentColor = Color.Black
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete"
                    )
                }
            }
        }

        // Offline warning
        if (isOffline) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = extendedColors.aiWarning,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Offline mode - photos will sync when online",
                    style = MaterialTheme.typography.caption,
                    color = extendedColors.aiWarning
                )
            }
        }
    }
}

/**
 * AI Validation Feedback Component
 */
@Composable
fun AiValidationFeedback(
    validationState: String,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalExtendedColors.current
    val validationInfo = getValidationInfo(validationState, extendedColors)

    GlassCard(
        modifier = modifier,
        accentColor = validationInfo.color
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = validationInfo.icon,
                contentDescription = null,
                tint = validationInfo.color,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = validationInfo.label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Permission Required Overlay Component
 */
@Composable
fun PermissionRequiredOverlay(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        GlassCard {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFFFF006E),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * Loading Overlay Component
 */
@Composable
fun LoadingOverlay(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF00D4FF),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

/**
 * Offline Indicator Component
 */
@Composable
fun OfflineIndicator(
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.padding(16.dp),
        accentColor = Color(0xFFFFAA00)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = Color(0xFFFFAA00),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Offline Mode",
                style = MaterialTheme.typography.caption,
                color = Color.White
            )
        }
    }
}

/**
 * Get step title based on current step
 */
private fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "Site Survey"
        2 -> "Cable Routing"
        3 -> "Fiber Termination"
        4 -> "Splice Verification"
        5 -> "Equipment Setup"
        6 -> "Signal Testing"
        7 -> "Quality Assurance"
        8 -> "Documentation"
        9 -> "Final Verification"
        else -> "Step $step"
    }
}

/**
 * Get step guidance text
 */
private fun getStepGuidance(step: Int): String {
    return when (step) {
        1 -> "Position the camera to capture the entire work area"
        2 -> "Focus on the cable routing path and connections"
        3 -> "Capture a clear image of the fiber termination point"
        4 -> "Document the splice location and quality"
        5 -> "Show all installed equipment and connections"
        6 -> "Capture signal testing equipment and readings"
        7 -> "Document quality assurance checks and results"
        8 -> "Show all completed documentation and forms"
        9 -> "Final verification and client handover photo"
        else -> "Follow the on-screen instructions"
    }
}

/**
 * Get step details
 */
private fun getStepDetails(step: Int): String {
    return when (step) {
        1 -> "Ensure good lighting and capture the entire work area"
        2 -> "Focus on cable paths, conduits, and connection points"
        3 -> "Get close-up of connector faces and termination quality"
        4 -> "Show splice protection and alignment"
        5 -> "Document all equipment models and serial numbers"
        6 -> "Capture signal test equipment and readings"
        7 -> "Show test results and quality verification"
        8 -> "Include all paperwork and completion forms"
        9 -> "Final site overview and customer acceptance"
        else -> "Ensure the photo meets AI validation requirements"
    }
}

/**
 * Get step icon
 */
private fun getStepIcon(step: Int): androidx.compose.ui.graphics.vector.ImageVector {
    return when (step) {
        1 -> Icons.Default.LocationOn
        2 -> Icons.Default.Cable
        3 -> Icons.Default.SettingsInputComponent
        4 -> Icons.Default.MergeType
        5 -> Icons.Default.Router
        6 -> Icons.Default.NetworkCheck
        7 -> Icons.Default.Verified
        8 -> Icons.Default.Description
        9 -> Icons.Default.CheckCircle
        else -> Icons.Default.Help
    }
}

/**
 * Get validation information
 */
private fun getValidationInfo(
    state: String,
    colors: ExtendedColors
): ValidationInfo {
    return when (state.lowercase()) {
        "excellent" -> ValidationInfo(
            color = colors.aiSuccess,
            label = "Excellent",
            icon = Icons.Default.CheckCircle
        )
        "good" -> ValidationInfo(
            color = colors.aiSuccess.copy(alpha = 0.8f),
            label = "Good",
            icon = Icons.Default.ThumbUp
        )
        "needs_improvement" -> ValidationInfo(
            color = colors.aiWarning,
            label = "Needs Improvement",
            icon = Icons.Default.Warning
        )
        "invalid" -> ValidationInfo(
            color = colors.aiError,
            label = "Invalid",
            icon = Icons.Default.Error
        )
        else -> ValidationInfo(
            color = colors.aiNeutral,
            label = "Processing",
            icon = Icons.Default.Pending
        )
    }
}

/**
 * Validation information data class
 */
private data class ValidationInfo(
    val color: Color,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)