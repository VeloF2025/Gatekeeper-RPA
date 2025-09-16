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

/**
 * Installation Progress Tracker Component
 *
 * A high-tech progress tracking system with:
 * - Visual step progression
 * - AI-powered status updates
 * - Real-time synchronization
 * - Estimated completion time
 * - Error state handling
 * - Full accessibility support
 * - Performance optimized
 *
 * @param currentStep Current step in the installation process
 * @param totalSteps Total number of steps
 * @param modifier The modifier to be applied to the tracker
 * @param stepStatuses List of status for each step
 * @param stepTitles List of titles for each step
 * @param aiStatus AI processing status
 * @param estimatedTimeRemaining Estimated time remaining
 * @param animateProgress Whether to animate progress transitions
 * @param indicatorType Type of progress indicator
 * @param syncStatus Synchronization status
 * @param isOffline Whether device is offline
 * @param isComplete Whether installation is complete
 */
@Composable
fun InstallationProgressTracker(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    stepStatuses: List<String>? = null,
    stepTitles: List<String>? = null,
    aiStatus: String? = null,
    estimatedTimeRemaining: String? = null,
    animateProgress: Boolean = true,
    indicatorType: String = "timeline",
    syncStatus: String? = null,
    isOffline: Boolean = false,
    isComplete: Boolean = false,
    errorState: String? = null
) {
    val extendedColors = LocalExtendedColors.current

    GlassCard(
        modifier = modifier,
        accentColor = extendedColors.aiActive
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            ProgressHeader(
                currentStep = currentStep,
                totalSteps = totalSteps,
                estimatedTimeRemaining = estimatedTimeRemaining,
                isComplete = isComplete,
                extendedColors = extendedColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicator
            when (indicatorType) {
                "circular" -> CircularProgressIndicator(
                    progress = currentStep.toFloat() / totalSteps,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally),
                    color = extendedColors.aiActive,
                    strokeWidth = 8.dp
                )
                else -> TimelineProgressIndicator(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    stepStatuses = stepStatuses,
                    stepTitles = stepTitles,
                    animateProgress = animateProgress,
                    extendedColors = extendedColors
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI status and sync info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                aiStatus?.let { status ->
                    AiStatusMiniIndicator(
                        status = status,
                        extendedColors = extendedColors
                    )
                }

                syncStatus?.let { sync ->
                    SyncStatusIndicator(
                        status = sync,
                        extendedColors = extendedColors
                    )
                }
            }

            // Error state
            errorState?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                ErrorStateCard(
                    errorState = error,
                    extendedColors = extendedColors
                )
            }

            // Offline indicator
            if (isOffline) {
                Spacer(modifier = Modifier.height(8.dp))
                OfflineIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

/**
 * Progress Header Component
 */
@Composable
fun ProgressHeader(
    currentStep: Int,
    totalSteps: Int,
    estimatedTimeRemaining: String?,
    isComplete: Boolean,
    extendedColors: ExtendedColors
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isComplete) "Installation Complete" else "Installation Progress",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )

        estimatedTimeRemaining?.let { time ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estimated time: $time",
                style = MaterialTheme.typography.bodySmall,
                color = extendedColors.aiActive
            )
        }

        // Progress percentage
        val percentage = ((currentStep.toFloat() / totalSteps) * 100).toInt()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.headlineMedium,
            color = extendedColors.aiActive,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Timeline Progress Indicator Component
 */
@Composable
fun TimelineProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    stepStatuses: List<String>?,
    stepTitles: List<String>?,
    animateProgress: Boolean,
    extendedColors: ExtendedColors
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items((1..totalSteps).toList()) { step ->
            TimelineStepItem(
                step = step,
                currentStep = currentStep,
                status = stepStatuses?.getOrNull(step - 1) ?: getDefaultStepStatus(step, currentStep),
                title = stepTitles?.getOrNull(step - 1) ?: getDefaultStepTitle(step),
                isLast = step == totalSteps,
                animateProgress = animateProgress,
                extendedColors = extendedColors
            )
        }
    }
}

/**
 * Timeline Step Item Component
 */
@Composable
fun TimelineStepItem(
    step: Int,
    currentStep: Int,
    status: String,
    title: String,
    isLast: Boolean,
    animateProgress: Boolean,
    extendedColors: ExtendedColors
) {
    val statusInfo = getStepStatusInfo(status, extendedColors)
    val isActive = step == currentStep
    val isCompleted = step < currentStep

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step indicator
        Box(
            modifier = Modifier.size(40.dp)
        ) {
            // Connection line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            color = when {
                                isCompleted -> extendedColors.aiSuccess
                                isActive -> extendedColors.aiActive
                                else -> extendedColors.glassBorder
                            }
                        )
                )
            }

            // Step circle
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        color = statusInfo.color
                    )
                    .border(
                        width = 2.dp,
                        color = if (isActive) extendedColors.aiActive else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusInfo.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Step content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.8f)
            )

            Text(
                text = status.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.caption,
                color = statusInfo.color
            )
        }

        // Step number
        Text(
            text = step.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = if (isActive) extendedColors.aiActive else Color.White.copy(alpha = 0.6f)
        )
    }
}

/**
 * AI Status Mini Indicator Component
 */
@Composable
fun AiStatusMiniIndicator(
    status: String,
    extendedColors: ExtendedColors
) {
    val statusInfo = getAiStatusInfo(status, extendedColors)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = statusInfo.icon,
            contentDescription = null,
            tint = statusInfo.color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = statusInfo.label,
            style = MaterialTheme.typography.caption,
            color = statusInfo.color
        )
    }
}

/**
 * Sync Status Indicator Component
 */
@Composable
fun SyncStatusIndicator(
    status: String,
    extendedColors: ExtendedColors
) {
    val syncInfo = getSyncStatusInfo(status, extendedColors)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = syncInfo.icon,
            contentDescription = null,
            tint = syncInfo.color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = syncInfo.label,
            style = MaterialTheme.typography.caption,
            color = syncInfo.color
        )
    }
}

/**
 * Error State Card Component
 */
@Composable
fun ErrorStateCard(
    errorState: String,
    extendedColors: ExtendedColors
) {
    val errorInfo = getErrorStateInfo(errorState, extendedColors)

    GlassCard(
        accentColor = extendedColors.aiError
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = errorInfo.icon,
                contentDescription = null,
                tint = extendedColors.aiError,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = errorInfo.label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

/**
 * Get default step status
 */
private fun getDefaultStepStatus(step: Int, currentStep: Int): String {
    return when {
        step < currentStep -> "completed"
        step == currentStep -> "in_progress"
        else -> "pending"
    }
}

/**
 * Get default step title
 */
private fun getDefaultStepTitle(step: Int): String {
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
 * Get step status information
 */
private fun getStepStatusInfo(
    status: String,
    colors: ExtendedColors
): StepStatusInfo {
    return when (status.lowercase()) {
        "completed" -> StepStatusInfo(
            color = colors.aiSuccess,
            icon = Icons.Default.CheckCircle
        )
        "in_progress" -> StepStatusInfo(
            color = colors.aiActive,
            icon = Icons.Default.Settings
        )
        "error" -> StepStatusInfo(
            color = colors.aiError,
            icon = Icons.Default.Error
        )
        "pending" -> StepStatusInfo(
            color = colors.aiNeutral,
            icon = Icons.Default.RadioButtonUnchecked
        )
        else -> StepStatusInfo(
            color = colors.aiNeutral,
            icon = Icons.Default.Help
        )
    }
}

/**
 * Get AI status information
 */
private fun getAiStatusInfo(
    status: String,
    colors: ExtendedColors
): AiStatusInfo {
    return when (status.lowercase()) {
        "analyzing" -> AiStatusInfo(
            color = colors.aiActive,
            label = "Analyzing",
            icon = Icons.Default.Search
        )
        "processing" -> AiStatusInfo(
            color = colors.aiActive,
            label = "Processing",
            icon = Icons.Default.Settings
        )
        "validating" -> AiStatusInfo(
            color = colors.aiWarning,
            label = "Validating",
            icon = Icons.Default.Verified
        )
        "complete" -> AiStatusInfo(
            color = colors.aiSuccess,
            label = "Complete",
            icon = Icons.Default.CheckCircle
        )
        else -> AiStatusInfo(
            color = colors.aiNeutral,
            label = "Idle",
            icon = Icons.Default.Info
        )
    }
}

/**
 * Get sync status information
 */
private fun getSyncStatusInfo(
    status: String,
    colors: ExtendedColors
): SyncStatusInfo {
    return when (status.lowercase()) {
        "synced" -> SyncStatusInfo(
            color = colors.aiSuccess,
            label = "Synced",
            icon = Icons.Default.Sync
        )
        "syncing" -> SyncStatusInfo(
            color = colors.aiActive,
            label = "Syncing",
            icon = Icons.Default.Sync
        )
        "pending_sync" -> SyncStatusInfo(
            color = colors.aiWarning,
            label = "Pending",
            icon = Icons.Default.Schedule
        )
        "sync_error" -> SyncStatusInfo(
            color = colors.aiError,
            label = "Sync Error",
            icon = Icons.Default.SyncProblem
        )
        else -> SyncStatusInfo(
            color = colors.aiNeutral,
            label = "Unknown",
            icon = Icons.Default.Help
        )
    }
}

/**
 * Get error state information
 */
private fun getErrorStateInfo(
    errorState: String,
    colors: ExtendedColors
): ErrorStateInfo {
    return when (errorState.lowercase()) {
        "network_error" -> ErrorStateInfo(
            label = "Network connection lost",
            icon = Icons.Default.WifiOff
        )
        "validation_error" -> ErrorStateInfo(
            label = "Validation failed",
            icon = Icons.Default.GppBad
        )
        "ai_processing_error" -> ErrorStateInfo(
            label = "AI processing error",
            icon = Icons.Default.Error
        )
        "storage_error" -> ErrorStateInfo(
            label = "Storage error",
            icon = Icons.Default.Storage
        )
        else -> ErrorStateInfo(
            label = "Unknown error",
            icon = Icons.Default.Error
        )
    }
}

/**
 * Data classes for status information
 */
private data class StepStatusInfo(
    val color: Color,
    val icon: ImageVector
)

private data class AiStatusInfo(
    val color: Color,
    val label: String,
    val icon: ImageVector
)

private data class SyncStatusInfo(
    val color: Color,
    val label: String,
    val icon: ImageVector
)

private data class ErrorStateInfo(
    val label: String,
    val icon: ImageVector
)