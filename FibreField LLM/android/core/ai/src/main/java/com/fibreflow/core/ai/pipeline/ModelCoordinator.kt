package com.fibreflow.core.ai.pipeline

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.fibreflow.core.ai.inference.InferenceEngine
import com.fibreflow.core.ai.inference.InferenceType
import com.fibreflow.core.ai.inference.ModelType
import com.fibreflow.core.ai.llm.LLMManager
import com.fibreflow.core.ai.vision.BarcodeScanner
import com.fibreflow.core.ai.vision.ONTLightDetector
import com.fibreflow.core.ai.vision.PhotoQualityAnalyzer
import com.fibreflow.core.ai.vision.TextExtractor
import com.fibreflow.core.common.result.Result
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * AI Model Coordinator for FibreField
 *
 * Orchestrates multiple AI models for comprehensive photo validation:
 * - Photo quality analysis
 * - Barcode/text extraction
 * - ONT light detection
 * - LLM guidance generation
 *
 * Ensures proper sequencing, error handling, and performance optimization.
 */
@ViewModelScoped
class ModelCoordinator @Inject constructor(
    private val inferenceEngine: InferenceEngine,
    private val llmManager: LLMManager,
    private val photoQualityAnalyzer: PhotoQualityAnalyzer,
    private val barcodeScanner: BarcodeScanner,
    private val textExtractor: TextExtractor,
    private val ontLightDetector: ONTLightDetector
) {

    companion object {
        private const val TAG = "ModelCoordinator"
        private const val MAX_CONCURRENT_MODELS = 2
        private const val VALIDATION_TIMEOUT_MS = 10000L // 10 seconds
    }

    private val coordinatorScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /**
     * Execute complete photo validation pipeline
     * Coordinates all AI models for comprehensive analysis
     */
    suspend fun executePhotoValidationPipeline(
        photoData: ByteArray,
        stepContext: InstallationStepContext
    ): Result<PhotoValidationResult> = withContext(Dispatchers.Default) {
        try {
            Log.i(TAG, "Starting photo validation pipeline for step: ${stepContext.stepName}")

            // Convert ByteArray to Bitmap for vision processing
            val bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.size)
                ?: return@withContext Result.Error(IllegalArgumentException("Failed to decode image data"))

            val startTime = System.currentTimeMillis()

            // Phase 1: Parallel quality and content analysis
            val qualityAnalysis = async {
                inferenceEngine.executeInference(InferenceType.VISION_ANALYSIS) {
                    photoQualityAnalyzer.analyzeQuality(bitmap, stepContext.stepName)
                }
            }

            val barcodeAnalysis = async {
                inferenceEngine.executeInference(InferenceType.BARCODE_SCANNING) {
                    barcodeScanner.scanBarcode(bitmap, stepContext.stepName)
                }
            }

            val textAnalysis = async {
                inferenceEngine.executeInference(InferenceType.TEXT_RECOGNITION) {
                    textExtractor.extractText(bitmap)
                }
            }

            // Wait for parallel analyses to complete
            val qualityResult = qualityAnalysis.await()
            val barcodeResult = barcodeAnalysis.await()
            val textResult = textAnalysis.await()

            // Phase 2: Equipment-specific validation
            val equipmentValidation = when (stepContext.stepName) {
                "ONT Installation" -> validateONTInstallation(photoData, qualityResult, barcodeResult, textResult)
                "Cable Connection" -> validateCableConnection(photoData, qualityResult, barcodeResult)
                "Power Verification" -> validatePowerVerification(photoData, qualityResult, textResult)
                else -> validateGenericStep(photoData, qualityResult, barcodeResult, textResult)
            }

            val totalTime = System.currentTimeMillis() - startTime
            Log.i(TAG, "Photo validation pipeline completed in ${totalTime}ms")

            equipmentValidation

        } catch (e: Exception) {
            Log.e(TAG, "Photo validation pipeline failed", e)
            Result.Error(e)
        }
    }

    /**
     * Generate contextual guidance based on validation results
     */
    suspend fun generateContextualGuidance(
        validationResult: PhotoValidationResult,
        stepContext: InstallationStepContext
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            val prompt = buildGuidancePrompt(validationResult, stepContext)

            llmManager.generateInstallationGuidance(
                userQuery = prompt,
                installationContext = com.fibreflow.core.ai.llm.InstallationContext(
                    currentStep = stepContext.stepName,
                    equipmentType = stepContext.equipmentType,
                    location = stepContext.location
                )
            )

        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate contextual guidance", e)
            Result.Error(e)
        }
    }

    /**
     * Stream real-time validation feedback
     */
    fun streamValidationFeedback(
        photoData: ByteArray,
        stepContext: InstallationStepContext
    ): Flow<Result<ValidationFeedback>> = flow {
        try {
            // Start with quality analysis (fastest)
            emit(Result.Success(ValidationFeedback(
                stage = ValidationStage.QUALITY_CHECK,
                message = "Analyzing photo quality...",
                progress = 0.2f
            )))

            val bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.size)
                ?: run {
                    emit(Result.Success(ValidationFeedback(
                        stage = ValidationStage.QUALITY_CHECK,
                        message = "Failed to decode image",
                        progress = 0.2f,
                        issues = listOf("Invalid image data")
                    )))
                    return@flow
                }
            
            val qualityResult = inferenceEngine.executeInference(InferenceType.VISION_ANALYSIS) {
                photoQualityAnalyzer.analyzeQuality(bitmap, stepContext.stepName)
            }

            if (qualityResult is Result.Error) {
                emit(Result.Success(ValidationFeedback(
                    stage = ValidationStage.QUALITY_CHECK,
                    message = "Quality analysis failed",
                    progress = 0.2f,
                    issues = listOf("Unable to analyze photo quality")
                )))
                return@flow
            }

            emit(Result.Success(ValidationFeedback(
                stage = ValidationStage.QUALITY_CHECK,
                message = "Photo quality acceptable",
                progress = 0.4f
            )))

            // Content analysis
            emit(Result.Success(ValidationFeedback(
                stage = ValidationStage.CONTENT_ANALYSIS,
                message = "Extracting text and barcodes...",
                progress = 0.6f
            )))

            val barcodeResult = inferenceEngine.executeInference(InferenceType.BARCODE_SCANNING) {
                barcodeScanner.scanMultipleBarcodes(bitmap)
            }

            val textResult = inferenceEngine.executeInference(InferenceType.TEXT_RECOGNITION) {
                textExtractor.extractText(bitmap)
            }

            emit(Result.Success(ValidationFeedback(
                stage = ValidationStage.CONTENT_ANALYSIS,
                message = "Content analysis complete",
                progress = 0.8f
            )))

            // Equipment-specific validation
            emit(Result.Success(ValidationFeedback(
                stage = ValidationStage.EQUIPMENT_VALIDATION,
                message = "Validating equipment installation...",
                progress = 0.9f
            )))

            val finalResult = executePhotoValidationPipeline(photoData, stepContext)

            when (finalResult) {
                is Result.Success -> {
                    val feedback = if (finalResult.data.isValid) {
                        ValidationFeedback(
                            stage = ValidationStage.COMPLETE,
                            message = "Validation successful!",
                            progress = 1.0f,
                            recommendations = finalResult.data.recommendations
                        )
                    } else {
                        ValidationFeedback(
                            stage = ValidationStage.COMPLETE,
                            message = "Validation failed",
                            progress = 1.0f,
                            issues = finalResult.data.issues,
                            recommendations = finalResult.data.recommendations
                        )
                    }
                    emit(Result.Success(feedback))
                }
                is Result.Error -> {
                    emit(Result.Success(ValidationFeedback(
                        stage = ValidationStage.COMPLETE,
                        message = "Validation error occurred",
                        progress = 1.0f,
                        issues = listOf("Validation process failed")
                    )))
                }
                else -> {
                    emit(Result.Success(ValidationFeedback(
                        stage = ValidationStage.COMPLETE,
                        message = "Validation status unknown",
                        progress = 1.0f,
                        issues = listOf("Unexpected validation result")
                    )))
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Streaming validation failed", e)
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.Default)

    /**
     * Preload models for faster validation
     */
    suspend fun preloadValidationModels(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Preloading validation models")

            inferenceEngine.preloadModels(listOf(
                ModelType.PHOTO_QUALITY_ANALYZER,
                ModelType.BARCODE_SCANNER,
                ModelType.TEXT_RECOGNIZER,
                ModelType.ONT_LIGHT_DETECTOR
            ))

            Log.i(TAG, "Validation models preloaded successfully")
            Result.Success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to preload validation models", e)
            Result.Error(e)
        }
    }

    /**
     * Optimize model usage based on device conditions
     */
    suspend fun optimizeForDeviceConditions(
        batteryLevel: Int,
        availableMemoryMB: Int,
        networkAvailable: Boolean
    ): Result<ModelOptimization> = withContext(Dispatchers.Default) {
        try {
            val inferenceOptimization = inferenceEngine.optimizeForConditions(
                batteryLevel = batteryLevel,
                availableMemoryMB = availableMemoryMB,
                thermalStatus = com.fibreflow.core.ai.inference.ThermalStatus.NORMAL
            )

            val optimization = ModelOptimization(
                enableStreaming = !inferenceOptimization.shouldDeferHeavyInference,
                maxConcurrentModels = inferenceOptimization.recommendedConcurrency,
                useSimplifiedModels = batteryLevel < 20,
                disableLLMGuidance = batteryLevel < 15 || !networkAvailable,
                optimizations = inferenceOptimization.optimizations.map { it.name }
            )

            Result.Success(optimization)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to optimize for device conditions", e)
            Result.Error(e)
        }
    }

    /**
     * Shutdown coordinator and cleanup resources
     */
    fun shutdown() {
        coordinatorScope.cancel()
        Log.i(TAG, "ModelCoordinator shutdown complete")
    }

    // Private validation methods for different installation steps

    private suspend fun validateONTInstallation(
        photoData: ByteArray,
        qualityResult: Result<com.fibreflow.core.ai.vision.PhotoQualityAnalysis>,
        barcodeResult: Result<com.fibreflow.core.ai.vision.BarcodeResult>,
        textResult: Result<com.fibreflow.core.ai.vision.TextExtractionResult>
    ): Result<PhotoValidationResult> = withContext(Dispatchers.Default) {
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        // Check quality
        if (qualityResult is Result.Success && !qualityResult.data.isAcceptable) {
            issues.add("Photo quality too low for ONT validation")
            recommendations.add("Retake photo with better lighting and focus")
        }

        // Check for ONT barcode
        if (barcodeResult is Result.Success) {
            val ontBarcode = if (barcodeResult is Result.Success) {
                barcodeResult.data.value?.contains("ONT") == true || barcodeResult.data.value?.contains("ONU") == true
            } else {
                false
            }
            if (ontBarcode == null) {
                issues.add("ONT barcode not found in photo")
                recommendations.add("Ensure ONT barcode is visible in the photo")
            }
        }

        // Check for serial number in text
        if (textResult is Result.Success) {
            val hasSerial = textResult.data.fullText.contains("SN:", ignoreCase = true) ||
                           textResult.data.fullText.matches(Regex(".*\\b[A-Z0-9]{8,}\\b.*")) // Serial number pattern
            if (!hasSerial) {
                issues.add("ONT serial number not detected")
                recommendations.add("Ensure ONT serial number label is visible")
            }
        }

        // ONT light detection - convert ByteArray to Bitmap first
        val bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.size)
        val lightResult = if (bitmap != null) {
            inferenceEngine.executeInference(InferenceType.LIGHT_DETECTION) {
                ontLightDetector.detectLights(bitmap)
            }
        } else {
            Result.Error(IllegalArgumentException("Failed to decode image for light detection"))
        }

        if (lightResult is Result.Success) {
            val lights = lightResult.data.detectedLights
            val powerLight = lights.find { it.lightType == com.fibreflow.core.ai.vision.LightType.POWER }
            if (powerLight == null || powerLight.detectedState != com.fibreflow.core.ai.vision.LightState.ON) {
                issues.add("ONT power light not detected or not illuminated")
                recommendations.add("Verify ONT is powered on and power light is green")
            }
        }

        Result.Success(PhotoValidationResult(
            isValid = issues.isEmpty(),
            confidence = calculateConfidence(issues.size),
            issues = issues,
            recommendations = recommendations,
            extractedData = extractONTData(barcodeResult, textResult)
        ))
    }

    private suspend fun validateCableConnection(
        photoData: ByteArray,
        qualityResult: Result<com.fibreflow.core.ai.vision.PhotoQualityAnalysis>,
        barcodeResult: Result<com.fibreflow.core.ai.vision.BarcodeResult>
    ): Result<PhotoValidationResult> {
        // Simplified cable validation - focus on barcode detection
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        if (barcodeResult is Result.Success && barcodeResult.data.value.isNullOrBlank()) {
            issues.add("No cable barcodes detected")
            recommendations.add("Ensure cable labels are visible in the photo")
        }

        return Result.Success(PhotoValidationResult(
            isValid = issues.isEmpty(),
            confidence = calculateConfidence(issues.size),
            issues = issues,
            recommendations = recommendations,
            extractedData = mapOf("cable_barcodes" to listOfNotNull((barcodeResult as? Result.Success)?.data?.value))
        ))
    }

    private suspend fun validatePowerVerification(
        photoData: ByteArray,
        qualityResult: Result<com.fibreflow.core.ai.vision.PhotoQualityAnalysis>,
        textResult: Result<com.fibreflow.core.ai.vision.TextExtractionResult>
    ): Result<PhotoValidationResult> {
        // Check for power meter readings
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        if (textResult is Result.Success) {
            val hasPowerReading = textResult.data.fullText.contains(Regex("\\b\\d+(\\.\\d+)?\\s*(dBm|dbm|dB)\\b")) ||
                                 textResult.data.fullText.contains("power", ignoreCase = true)

            if (!hasPowerReading) {
                issues.add("Power meter reading not detected")
                recommendations.add("Ensure power meter display is clearly visible")
            }
        }

        return Result.Success(PhotoValidationResult(
            isValid = issues.isEmpty(),
            confidence = calculateConfidence(issues.size),
            issues = issues,
            recommendations = recommendations,
            extractedData = extractPowerData(textResult) as Map<String, Any>
        ))
    }

    private suspend fun validateGenericStep(
        photoData: ByteArray,
        qualityResult: Result<com.fibreflow.core.ai.vision.PhotoQualityAnalysis>,
        barcodeResult: Result<com.fibreflow.core.ai.vision.BarcodeResult>,
        textResult: Result<com.fibreflow.core.ai.vision.TextExtractionResult>
    ): Result<PhotoValidationResult> {
        // Basic validation for unspecified steps
        val issues = mutableListOf<String>()

        if (qualityResult is Result.Success && !qualityResult.data.isAcceptable) {
            issues.add("Photo quality insufficient")
        }

        return Result.Success(PhotoValidationResult(
            isValid = issues.size <= 1, // Allow minor issues
            confidence = calculateConfidence(issues.size),
            issues = issues,
            recommendations = if (issues.isNotEmpty()) listOf("Review photo and retake if needed") else emptyList(),
            extractedData = emptyMap<String, Any>()
        ))
    }

    // Helper methods

    private fun calculateConfidence(issueCount: Int): Float {
        return when (issueCount) {
            0 -> 0.95f
            1 -> 0.80f
            2 -> 0.60f
            else -> 0.30f
        }
    }

    private fun extractONTData(
        barcodeResult: Result<com.fibreflow.core.ai.vision.BarcodeResult>,
        textResult: Result<com.fibreflow.core.ai.vision.TextExtractionResult>
    ): Map<String, Any> {
        val data = mutableMapOf<String, Any>()

        if (barcodeResult is Result.Success) {
            data["ont_barcodes"] = listOf(barcodeResult.data.value ?: "")
        }

        if (textResult is Result.Success) {
            data["extracted_text"] = textResult.data.fullText
        }

        return data
    }

    private fun extractPowerData(textResult: Result<com.fibreflow.core.ai.vision.TextExtractionResult>): Map<String, Any> {
        val data = mutableMapOf<String, Any>()

        if (textResult is Result.Success) {
            val powerReadings = textResult.data.fullText.split("\\s+".toRegex()).filter { text ->
                text.matches(Regex("\\b\\d+(\\.\\d+)?\\s*(dBm|dbm|dB)\\b"))
            }
            data["power_readings"] = powerReadings
        }

        return data
    }

    private fun buildGuidancePrompt(
        validationResult: PhotoValidationResult,
        stepContext: InstallationStepContext
    ): String {
        val issueSummary = if (validationResult.issues.isNotEmpty()) {
            "Issues found: ${validationResult.issues.joinToString(", ")}"
        } else {
            "No issues detected"
        }

        return """
            Installation step: ${stepContext.stepName}
            Equipment: ${stepContext.equipmentType}
            Validation result: $issueSummary

            Provide specific guidance to resolve any issues and complete this installation step safely.
            Include safety precautions and next steps.
        """.trimIndent()
    }
}

/**
 * Context for installation step validation
 */
data class InstallationStepContext(
    val stepName: String,
    val equipmentType: String,
    val location: String,
    val requiredValidations: List<String> = emptyList()
)

/**
 * Result of photo validation pipeline
 */
data class PhotoValidationResult(
    val isValid: Boolean,
    val confidence: Float,
    val issues: List<String>,
    val recommendations: List<String>,
    val extractedData: Map<String, Any>
)

/**
 * Real-time validation feedback
 */
data class ValidationFeedback(
    val stage: ValidationStage,
    val message: String,
    val progress: Float,
    val issues: List<String> = emptyList(),
    val recommendations: List<String> = emptyList()
)

/**
 * Validation pipeline stages
 */
enum class ValidationStage {
    QUALITY_CHECK,
    CONTENT_ANALYSIS,
    EQUIPMENT_VALIDATION,
    COMPLETE
}

/**
 * Model optimization recommendations
 */
data class ModelOptimization(
    val enableStreaming: Boolean,
    val maxConcurrentModels: Int,
    val useSimplifiedModels: Boolean,
    val disableLLMGuidance: Boolean,
    val optimizations: List<String>
)
