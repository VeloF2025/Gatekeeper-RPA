package com.fibreflow.tech.ui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fibreflow.tech.core.design.theme.FibreFieldTheme
import com.fibreflow.tech.core.design.theme.ExtendedColors
import com.fibreflow.tech.ui.components.*
import kotlinx.coroutines.delay

/**
 * High-Tech Demo Screen
 *
 * Showcase of all FibreField Tech UI components with real-time updates
 * and interactive demonstrations of the high-tech, futuristic design system.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighTechDemoScreen(
    onBack: () -> Unit = {}
) {
    var aiState by remember { mutableStateOf(AiState.LOADING) }
    var networkStatus by remember { mutableStateOf(NetworkStatus.OFFLINE) }
    var currentStep by remember { mutableIntStateOf(3) }
    var validationResult by remember { mutableStateOf<ValidationResult?>(null) }
    var showBiometric by remember { mutableStateOf(false) }

    // Simulate real-time updates
    LaunchedEffect(Unit) {
        // Simulate AI loading
        delay(2000)
        aiState = AiState.READY

        // Simulate network changes
        delay(1000)
        networkStatus = NetworkStatus.CONNECTED_4G

        // Simulate validation
        delay(1500)
        validationResult = ValidationResult(
            confidence = 0.94f,
            issues = listOf("ONT light detected", "Fiber alignment acceptable"),
            status = ValidationStatus.SUCCESS
        )
    }

    FibreFieldTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.foundation.background.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            ) {
                // Top App Bar with futuristic styling
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FibreField Tech Demo",
                            style = MaterialTheme.typography.headlineMedium,
                            color = ExtendedColors.aiActive,
                            fontWeight = FontWeight.Bold
                        )

                        // AI Status Indicator
                        AiStatusIndicator(
                            state = aiState,
                            modelInfo = "Phi-3.5 Mini (3.8B)"
                        )
                    }
                }

                // Network Status Bar
                NetworkStatusBar(
                    status = networkStatus,
                    signalStrength = 85,
                    latency = 42,
                    onRefresh = {
                        // Simulate network refresh
                        networkStatus = when (networkStatus) {
                            NetworkStatus.OFFLINE -> NetworkStatus.CONNECTED_4G
                            NetworkStatus.CONNECTED_4G -> NetworkStatus.CONNECTED_5G
                            NetworkStatus.CONNECTED_5G -> NetworkStatus.CONNECTED_WIFI
                            NetworkStatus.CONNECTED_WIFI -> NetworkStatus.OFFLINE
                        }
                    }
                )

                // Main content area
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Installation Progress Tracker
                    item {
                        InstallationProgressTracker(
                            currentStep = currentStep,
                            totalSteps = 9,
                            steps = listOf(
                                "Site Survey", "Equipment Prep", "Fiber Route",
                                "Conduit Install", "Fiber Pulling", "Splicing",
                                "ONT Mount", "Light Test", "Activation"
                            ),
                            onStepClick = { step ->
                                currentStep = step
                            }
                        )
                    }

                    // Photo Capture Interface
                    item {
                        PhotoCaptureInterface(
                            currentStep = currentStep,
                            onPhotoCaptured = { photoData ->
                                // Simulate AI validation
                                validationResult = ValidationResult(
                                    confidence = (0.7f..0.98f).random(),
                                    issues = listOf("Photo analysis complete"),
                                    status = if ((0.7f..0.98f).random() > 0.8f)
                                        ValidationStatus.SUCCESS
                                    else ValidationStatus.NEEDS_IMPROVEMENT
                                )
                            }
                        )
                    }

                    // AI Validation Results
                    validationResult?.let { result ->
                        item {
                            AiValidationResults(
                                result = result,
                                onRetry = {
                                    // Simulate re-validation
                                    validationResult = ValidationResult(
                                        confidence = (0.7f..0.98f).random(),
                                        issues = listOf("Re-analyzed with improved accuracy"),
                                        status = if ((0.7f..0.98f).random() > 0.8f)
                                            ValidationStatus.SUCCESS
                                        else ValidationStatus.NEEDS_IMPROVEMENT
                                    )
                                }
                            )
                        }
                    }

                    // Location Mapping Component
                    item {
                        LocationMappingComponent(
                            currentLocation = "123 Fiber St, Tech City",
                            coordinates = Pair(37.7749, -122.4194),
                            offlineMapAvailable = true,
                            onLocationUpdate = { location ->
                                // Handle location update
                            }
                        )
                    }

                    // Action buttons
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showBiometric = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ExtendedColors.aiActive,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Biometric Auth")
                            }

                            Button(
                                onClick = {
                                    // Simulate AI processing
                                    aiState = AiState.PROCESSING
                                    kotlinx.coroutines.GlobalScope.launch {
                                        delay(1500)
                                        aiState = AiState.READY
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ExtendedColors.aiSuccess,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text("AI Process")
                            }
                        }
                    }

                    // Demo info
                    item {
                        GlassCard {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Demo Features",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ExtendedColors.aiActive,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val features = listOf(
                                    "✨ Real-time AI status monitoring",
                                    "📶 Network connectivity tracking",
                                    "📷 9-step photo capture workflow",
                                    "🧠 AI-powered validation results",
                                    "📍 GPS mapping with offline support",
                                    "🔐 Biometric authentication",
                                    "🎨 High-tech glass morphism design",
                                    "♿ Full accessibility support"
                                )

                                features.forEach { feature ->
                                    Text(
                                        text = feature,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom navigation
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onBack) {
                            Text("Back")
                        }
                        TextButton(onClick = { /* Reset demo */ }) {
                            Text("Reset")
                        }
                        TextButton(onClick = { /* Show help */ }) {
                            Text("Help")
                        }
                    }
                }
            }
        }

        // Biometric Auth Dialog
        if (showBiometric) {
            BiometricAuthDialog(
                onAuthSuccess = {
                    showBiometric = false
                    // Handle successful authentication
                },
                onAuthFailed = {
                    // Handle failed authentication
                },
                onDismiss = {
                    showBiometric = false
                }
            )
        }
    }
}

// Data classes for demo
data class ValidationResult(
    val confidence: Float,
    val issues: List<String>,
    val status: ValidationStatus
)

enum class ValidationStatus {
    SUCCESS,
    NEEDS_IMPROVEMENT,
    FAILED
}