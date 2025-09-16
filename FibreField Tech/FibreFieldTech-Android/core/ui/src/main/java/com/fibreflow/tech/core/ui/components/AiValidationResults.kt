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
 * AI Validation Results Component
 *
 * A high-tech AI validation display with:
 * - Real-time analysis results
 * - Confidence score visualization
 * - Detailed validation metrics
 * - Improvement recommendations
 * - Validation history
 * - Full accessibility support
 * - Performance optimized
 *
 * @param validationState Overall validation state
 * @param modifier The modifier to be applied to the component
 * @param confidenceScore AI confidence score (0.0f to 1.0f)
 * @param validationType Type of validation performed
 * @param errorMessage Error message if validation failed
 * @param recommendations List of improvement recommendations
 * @param validationMetrics Detailed validation metrics
 * @param multipleResults List of multiple validation results
 * @param validationHistory Historical validation results
 * @param viewMode Display mode ("summary" or "detailed")
 * @param aiModelInfo AI model information
 * @param confidenceTrend Confidence score trend over time
 * @param onRevalidate Handler for revalidation request
 */
@Composable
fun AiValidationResults(
    validationState: String,
    modifier: Modifier = Modifier,
    confidenceScore: Float? = null,
    validationType: String? = null,
    errorMessage: String? = null,
    recommendations: List<String>? = null,
    validationMetrics: Map<String, Float>? = null,
    multipleResults: List<Map<String, Any>>? = null,
    validationHistory: List<Map<String, Any>>? = null,
    viewMode: String = "detailed",
    aiModelInfo: Map<String, String>? = null,
    confidenceTrend: List<Float>? = null,
    onRevalidate: (() -> Unit)? = null
) {
    val extendedColors = LocalExtendedColors.current

    GlassCard(
        modifier = modifier,
        accentColor = getValidationStateColor(validationState, extendedColors)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with overall status
            ValidationHeader(
                validationState = validationState,
                confidenceScore = confidenceScore,
                validationType = validationType,
                extendedColors = extendedColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confidence score visualization
            confidenceScore?.let { score ->
                ConfidenceScoreVisualization(
                    score = score,
                    extendedColors = extendedColors
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Error message
            errorMessage?.let { message ->
                ErrorMessageCard(
                    message = message,
                    extendedColors = extendedColors
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Validation metrics
            if (viewMode == "detailed") {
                validationMetrics?.let { metrics ->
                    ValidationMetricsSection(
                        metrics = metrics,
                        extendedColors = extendedColors
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Recommendations
                recommendations?.let { recs ->
                    RecommendationsSection(
                        recommendations = recs,
                        extendedColors = extendedColors
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // AI model info
                aiModelInfo?.let { info ->
                    AiModelInfoSection(
                        modelInfo = info,
                        extendedColors = extendedColors
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Multiple results
            multipleResults?.let { results ->
                MultipleResultsSection(
                    results = results,
                    extendedColors = extendedColors
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Validation history
            validationHistory?.let { history ->
                ValidationHistorySection(
                    history = history,
                    extendedColors = extendedColors
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Revalidate button
            onRevalidate?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onRevalidate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = extendedColors.aiActive,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Revalidate")
                    }
                }
            }
        }
    }
}

/**
 * Validation Header Component
 */
@Composable
fun ValidationHeader(
    validationState: String,
    confidenceScore: Float?,
    validationType: String?,
    extendedColors: ExtendedColors
) {
    val stateInfo = getValidationStateInfo(validationState, extendedColors)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Status icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(stateInfo.color.copy(alpha = 0.2f))
                .border(
                    width = 2.dp,
                    color = stateInfo.color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = stateInfo.icon,
                contentDescription = null,
                tint = stateInfo.color,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stateInfo.label,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            validationType?.let { type ->
                Text(
                    text = type.replaceFirstChar { it.uppercase() }.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            confidenceScore?.let { score ->
                Text(
                    text = "Confidence: ${(score * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = stateInfo.color
                )
            }
        }
    }
}

/**
 * Confidence Score Visualization Component
 */
@Composable
fun ConfidenceScoreVisualization(
    score: Float,
    extendedColors: ExtendedColors
) {
    Column {
        Text(
            text = "AI Confidence Score",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = score,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = getConfidenceColor(score, extendedColors),
            trackColor = extendedColors.glassBorder
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Score breakdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0%",
                style = MaterialTheme.typography.caption,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = "${(score * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = getConfidenceColor(score, extendedColors),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "100%",
                style = MaterialTheme.typography.caption,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Error Message Card Component
 */
@Composable
fun ErrorMessageCard(
    message: String,
    extendedColors: ExtendedColors
) {
    GlassCard(
        accentColor = extendedColors.aiError
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = extendedColors.aiError,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Validation Metrics Section Component
 */
@Composable
fun ValidationMetricsSection(
    metrics: Map<String, Float>,
    extendedColors: ExtendedColors
) {
    Column {
        Text(
            text = "Detailed Metrics",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(metrics.toList()) { (metric, value) ->
                ValidationMetricItem(
                    metric = metric,
                    value = value,
                    extendedColors = extendedColors
                )
            }
        }
    }
}

/**
 * Validation Metric Item Component
 */
@Composable
fun ValidationMetricItem(
    metric: String,
    value: Float,
    extendedColors: ExtendedColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = metric.replaceFirstChar { it.uppercase() }.replace("_", " "),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mini progress bar
            LinearProgressIndicator(
                progress = value,
                modifier = Modifier
                    .width(60.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = getConfidenceColor(value, extendedColors),
                trackColor = extendedColors.glassBorder
            )

            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = getConfidenceColor(value, extendedColors),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Recommendations Section Component
 */
@Composable
fun RecommendationsSection(
    recommendations: List<String>,
    extendedColors: ExtendedColors
) {
    Column {
        Text(
            text = "Recommendations",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recommendations) { recommendation ->
                RecommendationItem(
                    recommendation = recommendation,
                    extendedColors = extendedColors
                )
            }
        }
    }
}

/**
 * Recommendation Item Component
 */
@Composable
fun RecommendationItem(
    recommendation: String,
    extendedColors: ExtendedColors
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = extendedColors.aiWarning,
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = recommendation,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * AI Model Info Section Component
 */
@Composable
fun AiModelInfoSection(
    modelInfo: Map<String, String>,
    extendedColors: ExtendedColors
) {
    Column {
        Text(
            text = "AI Model Information",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(modelInfo.toList()) { (key, value) ->
                ModelInfoItem(
                    key = key,
                    value = value,
                    extendedColors = extendedColors
                )
            }
        }
    }
}

/**
 * Model Info Item Component
 */
@Composable
fun ModelInfoItem(
    key: String,
    value: String,
    extendedColors: ExtendedColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = key.replaceFirstChar { it.uppercase() }.replace("_", " "),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = extendedColors.aiActive,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Multiple Results Section Component
 */
@Composable
fun MultipleResultsSection(
    results: List<Map<String, Any>>,
    extendedColors: ExtendedColors
) {
    Column {
        Text(
            text = "Multiple Validation Results",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(results) { result ->
                MultipleResultItem(
                    result = result,
                    extendedColors = extendedColors
                )
            }
        }
    }
}

/**
 * Multiple Result Item Component
 */
@Composable
fun MultipleResultItem(
    result: Map<String, Any>,
    extendedColors: ExtendedColors
) {
    val type = result["type"] as? String ?: "Unknown"
    val state = result["state"] as? String ?: "unknown"
    val confidence = result["confidence"] as? Float ?: 0f

    GlassCard(
        accentColor = getValidationStateColor(state, extendedColors)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = type.replaceFirstChar { it.uppercase() }.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Text(
                    text = state.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.caption,
                    color = getValidationStateColor(state, extendedColors)
                )
            }

            Text(
                text = "${(confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = getConfidenceColor(confidence, extendedColors),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Validation History Section Component
 */
@Composable
fun ValidationHistorySection(
    history: List<Map<String, Any>>,
    extendedColors: ExtendedColors
) {
    Column {
        Text(
            text = "Validation History",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { entry ->
                HistoryItem(
                    entry = entry,
                    extendedColors = extendedColors
                )
            }
        }
    }
}

/**
 * History Item Component
 */
@Composable
fun HistoryItem(
    entry: Map<String, Any>,
    extendedColors: ExtendedColors
) {
    val timestamp = entry["timestamp"] as? String ?: "Unknown"
    val result = entry["result"] as? String ?: "unknown"
    val confidence = entry["confidence"] as? Float ?: 0f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )

            Text(
                text = result.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.caption,
                color = getValidationStateColor(result, extendedColors)
            )
        }

        Text(
            text = "${(confidence * 100).toInt()}%",
            style = MaterialTheme.typography.caption,
            color = getConfidenceColor(confidence, extendedColors)
        )
    }
}

/**
 * Get validation state color
 */
private fun getValidationStateColor(state: String, colors: ExtendedColors): Color {
    return when (state.lowercase()) {
        "success", "excellent", "good" -> colors.aiSuccess
        "warning", "needs_improvement" -> colors.aiWarning
        "error", "invalid" -> colors.aiError
        "processing", "analyzing" -> colors.aiActive
        else -> colors.aiNeutral
    }
}

/**
 * Get confidence score color
 */
private fun getConfidenceColor(score: Float, colors: ExtendedColors): Color {
    return when {
        score >= 0.9f -> colors.aiSuccess
        score >= 0.7f -> colors.aiSuccess.copy(alpha = 0.8f)
        score >= 0.5f -> colors.aiWarning
        else -> colors.aiError
    }
}

/**
 * Get validation state information
 */
private fun getValidationStateInfo(state: String, colors: ExtendedColors): ValidationStateInfo {
    return when (state.lowercase()) {
        "success", "excellent" -> ValidationStateInfo(
            label = "Excellent",
            icon = Icons.Default.CheckCircle
        )
        "good" -> ValidationStateInfo(
            label = "Good",
            icon = Icons.Default.ThumbUp
        )
        "warning", "needs_improvement" -> ValidationStateInfo(
            label = "Needs Improvement",
            icon = Icons.Default.Warning
        )
        "error", "invalid" -> ValidationStateInfo(
            label = "Invalid",
            icon = Icons.Default.Error
        )
        "processing", "analyzing" -> ValidationStateInfo(
            label = "Processing",
            icon = Icons.Default.Pending
        )
        else -> ValidationStateInfo(
            label = "Unknown",
            icon = Icons.Default.Help
        )
    }
}

/**
 * Validation state information data class
 */
private data class ValidationStateInfo(
    val label: String,
    val icon: ImageVector
)