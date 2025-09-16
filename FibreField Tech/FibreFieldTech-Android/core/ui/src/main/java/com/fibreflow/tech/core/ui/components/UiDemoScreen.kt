package com.fibreflow.tech.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fibreflow.tech.core.design.theme.FibreFieldTheme
import kotlinx.coroutines.delay

/**
 * UI Demo Screen - High-Tech Component Showcase
 *
 * This screen demonstrates all the high-tech UI components:
 * - AI Status Indicator with live updates
 * - Network Status Bar with real-time monitoring
 * - Photo Capture Interface with 9-step workflow
 * - Glass Card Components with morphism effects
 * - Installation Progress Tracker
 * - AI Validation Results
 * - Location Mapping Component
 * - Biometric Auth Dialog
 *
 * Features:
 * - Real-time state updates
 * - Interactive demonstrations
 * - Performance monitoring
 * - Accessibility compliance
 * - Responsive design
 */
@Composable
fun UiDemoScreen() {
    var aiStatus by remember { mutableStateOf("loading") }
    var networkStatus by remember { mutableStateOf("excellent") }
    var signalStrength by remember { mutableStateOf(5) }
    var currentStep by remember { mutableStateOf(1) }
    var validationState by remember { mutableStateOf("processing") }
    var confidence by remember { mutableStateOf(0.75f) }

    // Simulate real-time updates
    LaunchedEffect(Unit) {
        // AI status simulation
        while (true) {
            delay(3000)
            aiStatus = listOf("loading", "processing", "ready", "error").random()
            confidence = (0.6f..0.98f).random()
        }
    }

    LaunchedEffect(Unit) {
        // Network status simulation
        while (true) {
            delay(5000)
            networkStatus = listOf("excellent", "good", "poor", "offline").random()
            signalStrength = (1..5).random()
        }
    }

    FibreFieldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "FibreField Tech UI Components",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF00D4FF)
                    )
                }

                // AI Status Indicators
                item {
                    Text(
                        text = "AI Status Indicators",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("loading", "ready", "error", "processing").forEach { status ->
                            AiStatusIndicator(
                                status = status,
                                size = AiStatusSize.Medium,
                                onClick = { aiStatus = status }
                            )
                        }
                    }
                }

                item {
                    AiStatusIndicatorEnhanced(
                        status = aiStatus,
                        confidence = confidence,
                        lastUpdate = "Just now",
                        onClick = { /* Show details */ }
                    )
                }

                // Network Status Bar
                item {
                    Text(
                        text = "Network Status",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    NetworkStatusBar(
                        networkStatus = networkStatus,
                        networkType = "5G",
                        signalStrength = signalStrength,
                        showDetailedInfo = true,
                        onRefresh = { /* Refresh network */ }
                    )
                }

                // Photo Capture Interface
                item {
                    Text(
                        text = "Photo Capture Interface",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    PhotoCaptureInterface(
                        currentStep = currentStep,
                        totalSteps = 9,
                        validationState = validationState,
                        onNextStep = { if (currentStep < 9) currentStep++ },
                        onPreviousStep = { if (currentStep > 1) currentStep-- },
                        onPhotoCapture = { /* Capture photo */ },
                        isOffline = networkStatus == "offline"
                    )
                }

                // Glass Card Examples
                item {
                    Text(
                        text = "Glass Card Components",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        GlassCard(
                            modifier = Modifier.weight(1f),
                            accentColor = Color(0xFF00D4FF)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "AI Processing",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                CircularProgressIndicator(
                                    color = Color(0xFF00D4FF)
                                )
                            }
                        }

                        GlassCard(
                            modifier = Modifier.weight(1f),
                            accentColor = Color(0xFF00FF88)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Success",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00FF88),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Installation Progress Tracker
                item {
                    Text(
                        text = "Installation Progress",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    InstallationProgressTracker(
                        currentStep = currentStep,
                        totalSteps = 9,
                        stepStatuses = (1..9).map { step ->
                            when {
                                step < currentStep -> "completed"
                                step == currentStep -> "in_progress"
                                else -> "pending"
                            }
                        }
                    )
                }

                // AI Validation Results
                item {
                    Text(
                        text = "AI Validation Results",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    AiValidationResults(
                        validationState = when {
                            confidence > 0.9f -> "success"
                            confidence > 0.7f -> "warning"
                            else -> "error"
                        },
                        confidenceScore = confidence,
                        validationType = "cable_alignment",
                        recommendations = when {
                            confidence > 0.9f -> listOf("Excellent cable alignment detected")
                            confidence > 0.7f -> listOf("Minor adjustments recommended for optimal alignment")
                            else -> listOf("Significant alignment issues detected", "Please reposition cable")
                        }
                    )
                }

                // Interactive Controls
                item {
                    Text(
                        text = "Interactive Controls",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Step controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { if (currentStep > 1) currentStep-- },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00D4FF),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Previous Step")
                            }

                            Button(
                                onClick = { if (currentStep < 9) currentStep++ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00FF88),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Next Step")
                            }
                        }

                        // Status controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { networkStatus = "excellent" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00FF88),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Good Network")
                            }

                            Button(
                                onClick = { networkStatus = "offline" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF3B30),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Offline")
                            }
                        }
                    }
                }

                // Performance metrics
                item {
                    Text(
                        text = "System Performance",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    GlassCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PerformanceMetricItem(
                                label = "AI Processing Time",
                                value = "< 100ms",
                                status = "excellent"
                            )
                            PerformanceMetricItem(
                                label = "Network Latency",
                                value = "12ms",
                                status = "good"
                            )
                            PerformanceMetricItem(
                                label = "Memory Usage",
                                value = "45MB",
                                status = "good"
                            )
                            PerformanceMetricItem(
                                label = "Battery Impact",
                                value = "2%/hr",
                                status = "excellent"
                            )
                        }
                    }
                }

                // Accessibility information
                item {
                    Text(
                        text = "Accessibility Features",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                item {
                    GlassCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccessibilityFeatureItem(
                                title = "Screen Reader Support",
                                description = "Full TalkBack and VoiceOver compatibility",
                                status = "enabled"
                            )
                            AccessibilityFeatureItem(
                                title = "Color Contrast",
                                description = "WCAG 2.1 AA compliant (4.5:1 ratio)",
                                status = "compliant"
                            )
                            AccessibilityFeatureItem(
                                title = "Touch Targets",
                                description = "48dp minimum touch targets",
                                status = "compliant"
                            )
                            AccessibilityFeatureItem(
                                title = "Dynamic Type",
                                description = "Supports system font scaling",
                                status = "enabled"
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Performance Metric Item Component
 */
@Composable
fun PerformanceMetricItem(
    label: String,
    value: String,
    status: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = when (status) {
                    "excellent" -> Color(0xFF00FF88)
                    "good" -> Color(0xFF00D4FF)
                    else -> Color(0xFFFFAA00)
                }
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = when (status) {
                            "excellent" -> Color(0xFF00FF88)
                            "good" -> Color(0xFF00D4FF)
                            else -> Color(0xFFFFAA00)
                        },
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
}

/**
 * Accessibility Feature Item Component
 */
@Composable
fun AccessibilityFeatureItem(
    title: String,
    description: String,
    status: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = when (status) {
                "enabled", "compliant" -> androidx.compose.material.icons.Icons.Default.CheckCircle
                else -> androidx.compose.material.icons.Icons.Default.Info
            },
            contentDescription = null,
            tint = when (status) {
                "enabled", "compliant" -> Color(0xFF00FF88)
                else -> Color(0xFF00D4FF)
            },
            modifier = Modifier.size(20.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}