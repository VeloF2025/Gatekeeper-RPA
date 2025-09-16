# FibreField Technician BOT - AI/ML Integration Architecture

## 1. Overview

This document details the complete AI/ML integration architecture for the FibreField Technician Android application, featuring:
- **Phi-3.5 Mini LLM** (3.8B parameters) for conversational guidance
- **Custom Computer Vision Models** for photo validation
- **Speech Recognition & Synthesis** for hands-free operation
- **On-device inference** for offline functionality

## 2. LLM Integration Architecture

### 2.1 Phi-3.5 Mini Implementation

```kotlin
// LLM Engine Interface
interface LLMEngine {
    suspend fun initialize(): Result<Unit>
    suspend fun generateResponse(
        prompt: String,
        context: ConversationContext? = null,
        temperature: Float = 0.3f,
        maxTokens: Int = 250
    ): Result<LLMResponse>
    suspend fun generateGuidance(
        step: InstallationStep,
        metadata: Map<String, Any>
    ): Result<String>
    suspend fun processCommand(input: String): Result<Command>
    fun release()
    fun getMemoryUsage(): Long
    fun isInitialized(): Boolean
}

// Phi-3.5 Mini Engine Implementation
@Singleton
class Phi35MiniEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: LLMConfig,
    private val performanceMonitor: PerformanceMonitor
) : LLMEngine {
    
    private var mlcChat: MLCChat? = null
    private val contextManager = ConversationContextManager()
    private val promptOptimizer = PromptOptimizer()
    
    // Model configuration
    private val modelConfig = ModelConfig(
        modelPath = "models/phi-3.5-mini-instruct-q4_k_m.gguf",
        contextLength = 8192,
        batchSize = 512,
        numThreads = 4,
        useGPU = true,
        gpuLayers = 24 // Number of layers to offload to GPU
    )
    
    override suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            
            // Initialize MLC runtime
            MLCEngine.init(
                context,
                MLCEngine.Config.builder()
                    .setNumThreads(modelConfig.numThreads)
                    .setUseGPU(modelConfig.useGPU)
                    .setGPULayers(modelConfig.gpuLayers)
                    .setCacheDir(context.cacheDir)
                    .build()
            )
            
            // Load model with optimizations
            val modelFile = loadModelFile()
            mlcChat = MLCChat.fromFile(
                modelFile,
                MLCChatConfig.builder()
                    .setMaxGenLength(config.maxTokens)
                    .setTemperature(config.temperature)
                    .setTopP(config.topP)
                    .setRepetitionPenalty(1.1f)
                    .setContextLength(modelConfig.contextLength)
                    .setBatchSize(modelConfig.batchSize)
                    .build()
            )
            
            // Warm up model
            warmUpModel()
            
            val loadTime = System.currentTimeMillis() - startTime
            performanceMonitor.recordModelLoadTime("phi-3.5-mini", loadTime)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Phi35MiniEngine", "Failed to initialize", e)
            Result.failure(e)
        }
    }
    
    override suspend fun generateResponse(
        prompt: String,
        context: ConversationContext?,
        temperature: Float,
        maxTokens: Int
    ): Result<LLMResponse> = withContext(Dispatchers.Default) {
        try {
            val chat = mlcChat ?: return Result.failure(
                IllegalStateException("LLM not initialized")
            )
            
            val startTime = System.currentTimeMillis()
            
            // Optimize prompt
            val optimizedPrompt = promptOptimizer.optimize(prompt, context)
            
            // Build full conversation
            val conversation = buildConversation(optimizedPrompt, context)
            
            // Generate response with streaming
            val responseBuilder = StringBuilder()
            var tokenCount = 0
            
            chat.generateStreaming(
                conversation,
                MLCGenerateConfig.builder()
                    .setMaxNewTokens(maxTokens)
                    .setTemperature(temperature)
                    .build()
            ) { token ->
                responseBuilder.append(token)
                tokenCount++
                
                // Early stopping conditions
                if (tokenCount >= maxTokens || 
                    responseBuilder.contains("<|end|>") ||
                    responseBuilder.contains("</s>")) {
                    return@generateStreaming false
                }
                true
            }
            
            val response = responseBuilder.toString()
                .replace("<|end|>", "")
                .replace("</s>", "")
                .trim()
            
            // Update context
            contextManager.addTurn(prompt, response)
            
            val inferenceTime = System.currentTimeMillis() - startTime
            performanceMonitor.recordInferenceTime("phi-3.5-mini", inferenceTime)
            
            Result.success(
                LLMResponse(
                    text = response,
                    tokensGenerated = tokenCount,
                    inferenceTimeMs = inferenceTime,
                    confidence = calculateConfidence(response)
                )
            )
        } catch (e: Exception) {
            Log.e("Phi35MiniEngine", "Generation failed", e)
            Result.failure(e)
        }
    }
    
    override suspend fun generateGuidance(
        step: InstallationStep,
        metadata: Map<String, Any>
    ): Result<String> {
        val prompt = GuidancePromptBuilder.build(step, metadata)
        val response = generateResponse(
            prompt = prompt,
            temperature = 0.3f, // Lower temperature for consistent guidance
            maxTokens = 150
        )
        return response.map { it.text }
    }
    
    private fun buildConversation(
        prompt: String,
        context: ConversationContext?
    ): String {
        return buildString {
            // System prompt
            appendLine("<|system|>")
            appendLine(SYSTEM_PROMPT)
            appendLine("<|end|>")
            
            // Add context if available
            context?.let {
                // Include relevant context
                it.installationContext?.let { install ->
                    appendLine("<|system|>")
                    appendLine("Current installation: Drop ${install.dropNumber}")
                    appendLine("Step: ${install.currentStep}")
                    appendLine("Photos taken: ${install.photosTaken}/9")
                    appendLine("<|end|>")
                }
                
                // Include recent conversation
                it.previousTurns.takeLast(3).forEach { turn ->
                    appendLine("<|user|>")
                    appendLine(turn.userInput)
                    appendLine("<|end|>")
                    appendLine("<|assistant|>")
                    appendLine(turn.assistantResponse)
                    appendLine("<|end|>")
                }
            }
            
            // Current prompt
            appendLine("<|user|>")
            appendLine(prompt)
            appendLine("<|end|>")
            appendLine("<|assistant|>")
        }
    }
    
    private suspend fun warmUpModel() {
        generateResponse(
            "Hello",
            temperature = 0.1f,
            maxTokens = 5
        )
    }
    
    private suspend fun loadModelFile(): File = withContext(Dispatchers.IO) {
        val modelFile = File(context.filesDir, modelConfig.modelPath)
        
        if (!modelFile.exists()) {
            // Copy from assets or download
            context.assets.open(modelConfig.modelPath).use { input ->
                modelFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        
        modelFile
    }
    
    companion object {
        private const val SYSTEM_PROMPT = """
You are a helpful fiber optic installation assistant guiding technicians through the installation process. 
Your responses should be:
- Clear and concise (max 2-3 sentences)
- Safety-focused
- Step-by-step when needed
- Encouraging and supportive

Key responsibilities:
1. Guide through 9-step photo documentation
2. Validate installation quality
3. Troubleshoot issues
4. Ensure safety compliance
5. Provide real-time feedback

Always prioritize safety and quality over speed.
"""
    }
}

// LLM Response Model
data class LLMResponse(
    val text: String,
    val tokensGenerated: Int,
    val inferenceTimeMs: Long,
    val confidence: Float
)

// Conversation Context Management
class ConversationContextManager {
    private val maxTokens = 4096
    private val conversationHistory = mutableListOf<ConversationTurn>()
    private var currentTokenCount = 0
    
    fun addTurn(userInput: String, assistantResponse: String) {
        val turn = ConversationTurn(
            userInput = userInput,
            assistantResponse = assistantResponse,
            timestamp = System.currentTimeMillis()
        )
        
        val turnTokens = estimateTokens(turn)
        
        // Manage context window
        while (currentTokenCount + turnTokens > maxTokens && conversationHistory.isNotEmpty()) {
            val removed = conversationHistory.removeFirst()
            currentTokenCount -= estimateTokens(removed)
        }
        
        conversationHistory.add(turn)
        currentTokenCount += turnTokens
    }
    
    fun getContext(): ConversationContext {
        return ConversationContext(
            previousTurns = conversationHistory.toList(),
            tokenCount = currentTokenCount
        )
    }
    
    private fun estimateTokens(turn: ConversationTurn): Int {
        // Rough estimation: 1 token ≈ 4 characters
        return (turn.userInput.length + turn.assistantResponse.length) / 4
    }
}
```

### 2.2 Guidance Prompt Templates

```kotlin
// Guidance Prompt Builder
object GuidancePromptBuilder {
    
    fun build(step: InstallationStep, metadata: Map<String, Any>): String {
        return when (step) {
            InstallationStep.DROP_VALIDATION -> buildDropValidationPrompt(metadata)
            InstallationStep.CABLE_SPAN -> buildCableSpanPrompt(metadata)
            InstallationStep.HOME_ENTRY -> buildHomeEntryPrompt(metadata)
            InstallationStep.ONT_BARCODE -> buildBarcodePrompt(metadata)
            InstallationStep.ONT_INSTALLATION -> buildONTInstallationPrompt(metadata)
            InstallationStep.ONT_ACTIVE -> buildONTActivePrompt(metadata)
            InstallationStep.ROUTER_CONNECTION -> buildRouterPrompt(metadata)
            InstallationStep.SPEED_TEST -> buildSpeedTestPrompt(metadata)
            InstallationStep.CLEANUP -> buildCleanupPrompt(metadata)
            InstallationStep.COMPLETION -> buildCompletionPrompt(metadata)
        }
    }
    
    private fun buildDropValidationPrompt(metadata: Map<String, Any>): String {
        val dropNumber = metadata["dropNumber"] as String
        val address = metadata["address"] as String
        val distance = metadata["distance"] as? Float ?: 0f
        
        return """
        The technician is at drop $dropNumber.
        Address: $address
        Distance from drop: ${distance}m
        
        Provide guidance for verifying they are at the correct location and starting the installation.
        """.trimIndent()
    }
    
    private fun buildONTActivePrompt(metadata: Map<String, Any>): String {
        val dropNumber = metadata["dropNumber"] as String
        val retakeCount = metadata["retakeCount"] as? Int ?: 0
        
        return """
        Guide the technician to capture the ONT activation photo for drop $dropNumber.
        This is attempt ${retakeCount + 1}.
        
        Requirements:
        - All 4 lights must be green (Power, LOS, PON, LAN)
        - Drop number label must be visible
        - ONT serial number should be readable
        
        Provide clear instructions for capturing a valid photo.
        """.trimIndent()
    }
    
    private fun buildTroubleshootingPrompt(issue: String, context: Map<String, Any>): String {
        return """
        The technician encountered an issue: $issue
        
        Context:
        - Step: ${context["currentStep"]}
        - Drop: ${context["dropNumber"]}
        - Previous attempts: ${context["attempts"]}
        
        Provide troubleshooting steps to resolve this issue.
        """.trimIndent()
    }
}

// Dynamic Prompt Optimization
class PromptOptimizer {
    
    fun optimize(prompt: String, context: ConversationContext?): String {
        var optimized = prompt
        
        // Remove redundant information if in context
        context?.installationContext?.let { install ->
            optimized = optimized
                .replace("drop ${install.dropNumber}", "this drop")
                .replace("Drop ${install.dropNumber}", "This drop")
        }
        
        // Compress whitespace
        optimized = optimized.replace(Regex("\\s+"), " ").trim()
        
        // Add context markers for better understanding
        if (!optimized.contains("?") && !optimized.contains(".")) {
            optimized = "$optimized?"
        }
        
        return optimized
    }
}
```

## 3. Computer Vision Pipeline

### 3.1 Vision Model Architecture

```kotlin
// Vision Validation Pipeline
@Singleton
class VisionValidationPipeline @Inject constructor(
    private val ontDetector: ONTLightDetector,
    private val equipmentDetector: EquipmentDetector,
    private val qualityAnalyzer: PhotoQualityAnalyzer,
    private val textRecognizer: TextRecognizer,
    private val performanceMonitor: PerformanceMonitor
) {
    
    suspend fun validatePhoto(
        bitmap: Bitmap,
        photoType: PhotoType,
        metadata: PhotoMetadata
    ): PhotoValidationResult = coroutineScope {
        
        val startTime = System.currentTimeMillis()
        
        // Run quality analysis first (fast, can reject early)
        val quality = qualityAnalyzer.analyze(bitmap)
        if (!quality.isAcceptable) {
            return@coroutineScope PhotoValidationResult.QualityIssue(
                issues = quality.issues,
                suggestions = quality.suggestions
            )
        }
        
        // Type-specific validation
        val result = when (photoType) {
            PhotoType.ONT_ACTIVE -> validateONTActive(bitmap, metadata)
            PhotoType.BARCODE -> validateBarcode(bitmap)
            PhotoType.CABLE_SPAN -> validateCableSpan(bitmap, metadata)
            PhotoType.HOME_ENTRY -> validateHomeEntry(bitmap)
            PhotoType.ROUTER_CONNECTED -> validateRouter(bitmap)
            PhotoType.SPEED_TEST -> validateSpeedTest(bitmap)
            else -> validateGeneric(bitmap, photoType)
        }
        
        val processingTime = System.currentTimeMillis() - startTime
        performanceMonitor.recordPhotoValidation(photoType, processingTime)
        
        result
    }
    
    private suspend fun validateONTActive(
        bitmap: Bitmap,
        metadata: PhotoMetadata
    ): PhotoValidationResult = coroutineScope {
        
        // Parallel detection
        val lightsDeferred = async { ontDetector.detectLights(bitmap) }
        val equipmentDeferred = async { equipmentDetector.detect(bitmap) }
        val textDeferred = async { textRecognizer.recognize(bitmap) }
        
        val lights = lightsDeferred.await()
        val equipment = equipmentDeferred.await()
        val text = textDeferred.await()
        
        // Validation logic
        val issues = mutableListOf<ValidationIssue>()
        
        // Check all 4 lights
        if (!lights.powerLight.isOn) {
            issues.add(ValidationIssue.POWER_LIGHT_OFF)
        }
        if (!lights.losLight.isOn) {
            issues.add(ValidationIssue.LOS_LIGHT_OFF)
        }
        if (!lights.ponLight.isOn) {
            issues.add(ValidationIssue.PON_LIGHT_OFF)
        }
        if (!lights.lanLight.isOn) {
            issues.add(ValidationIssue.LAN_LIGHT_OFF)
        }
        
        // Check ONT detection
        val ontDetected = equipment.any { 
            it.label == "ONT" && it.confidence > 0.7f 
        }
        if (!ontDetected) {
            issues.add(ValidationIssue.ONT_NOT_VISIBLE)
        }
        
        // Check drop number visibility
        val dropNumberVisible = text.any { 
            it.text.contains(metadata.dropNumber, ignoreCase = true) 
        }
        if (!dropNumberVisible) {
            issues.add(ValidationIssue.DROP_NUMBER_NOT_VISIBLE)
        }
        
        return@coroutineScope if (issues.isEmpty()) {
            PhotoValidationResult.Success(
                confidence = minOf(
                    lights.overallConfidence,
                    equipment.maxOfOrNull { it.confidence } ?: 0f
                ),
                metadata = ValidationMetadata(
                    lightsDetected = lights.toMap(),
                    equipmentDetected = equipment.map { it.label },
                    textDetected = text.map { it.text },
                    dropNumberVerified = dropNumberVisible
                )
            )
        } else {
            PhotoValidationResult.Failed(
                issues = issues,
                canRetry = true,
                allowManualOverride = issues.none { it.isCritical },
                suggestions = generateSuggestions(issues)
            )
        }
    }
}

// ONT Light Detector
@Singleton
class ONTLightDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private lateinit var interpreter: Interpreter
    private val inputSize = 224
    private val modelFile = "ont_light_detector_v3.tflite"
    
    suspend fun initialize() = withContext(Dispatchers.IO) {
        val modelBuffer = loadModelFile()
        
        val options = Interpreter.Options().apply {
            setNumThreads(2)
            setUseNNAPI(true)
            setAllowFp16PrecisionForFp32(true)
            setAllowBufferHandleOutput(false)
        }
        
        // Add GPU delegate if available
        try {
            val gpuDelegate = GpuDelegate()
            options.addDelegate(gpuDelegate)
        } catch (e: Exception) {
            Log.w("ONTDetector", "GPU delegate not available", e)
        }
        
        interpreter = Interpreter(modelBuffer, options)
    }
    
    suspend fun detectLights(bitmap: Bitmap): ONTLightDetection = 
        withContext(Dispatchers.Default) {
            
        // Preprocess image
        val input = preprocessImage(bitmap)
        
        // Prepare output buffers
        val lightStates = Array(1) { FloatArray(8) } // 4 lights × 2 states
        val boundingBoxes = Array(1) { Array(4) { FloatArray(4) } } // 4 lights × 4 coords
        
        val outputs = mapOf(
            0 to lightStates,
            1 to boundingBoxes
        )
        
        // Run inference
        interpreter.runForMultipleInputsOutputs(arrayOf(input), outputs)
        
        // Parse results
        ONTLightDetection(
            powerLight = LightStatus(
                isOn = lightStates[0][0] > lightStates[0][1],
                confidence = maxOf(lightStates[0][0], lightStates[0][1]),
                boundingBox = parseBoundingBox(boundingBoxes[0][0])
            ),
            losLight = LightStatus(
                isOn = lightStates[0][2] > lightStates[0][3],
                confidence = maxOf(lightStates[0][2], lightStates[0][3]),
                boundingBox = parseBoundingBox(boundingBoxes[0][1])
            ),
            ponLight = LightStatus(
                isOn = lightStates[0][4] > lightStates[0][5],
                confidence = maxOf(lightStates[0][4], lightStates[0][5]),
                boundingBox = parseBoundingBox(boundingBoxes[0][2])
            ),
            lanLight = LightStatus(
                isOn = lightStates[0][6] > lightStates[0][7],
                confidence = maxOf(lightStates[0][6], lightStates[0][7]),
                boundingBox = parseBoundingBox(boundingBoxes[0][3])
            ),
            overallConfidence = lightStates[0].max()
        )
    }
    
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        // Resize to model input size
        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        
        // Convert to ByteBuffer
        val buffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(inputSize * inputSize)
        resized.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        
        // Normalize to [-1, 1]
        for (pixel in pixels) {
            val r = ((pixel shr 16 and 0xFF) - 127.5f) / 127.5f
            val g = ((pixel shr 8 and 0xFF) - 127.5f) / 127.5f
            val b = ((pixel and 0xFF) - 127.5f) / 127.5f
            
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        
        buffer.rewind()
        return buffer
    }
    
    private fun parseBoundingBox(coords: FloatArray): BoundingBox {
        return BoundingBox(
            left = coords[0],
            top = coords[1],
            right = coords[2],
            bottom = coords[3]
        )
    }
}

// Equipment Detector using YOLOv8 Nano
@Singleton
class EquipmentDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private lateinit var interpreter: Interpreter
    private val inputSize = 320
    private val modelFile = "yolov8n_equipment.tflite"
    
    // Class labels
    private val labels = listOf(
        "ONT", "Router", "Cable", "Connector", 
        "Splitter", "UPS", "Tool", "Ladder"
    )
    
    suspend fun detect(bitmap: Bitmap): List<Detection> = 
        withContext(Dispatchers.Default) {
            
        val input = preprocessImage(bitmap)
        
        // YOLOv8 output: [1, 8400, 85] 
        // 8400 predictions, 85 values each (x,y,w,h,conf,80 classes)
        val output = Array(1) { Array(8400) { FloatArray(13) } } // Adjusted for our classes
        
        interpreter.run(input, output)
        
        // Parse detections
        val detections = mutableListOf<Detection>()
        
        for (i in 0 until 8400) {
            val prediction = output[0][i]
            val confidence = prediction[4]
            
            if (confidence > 0.5f) {
                // Get class with highest probability
                var maxClassProb = 0f
                var classId = -1
                
                for (j in 5 until 13) {
                    if (prediction[j] > maxClassProb) {
                        maxClassProb = prediction[j]
                        classId = j - 5
                    }
                }
                
                if (maxClassProb > 0.5f && classId >= 0) {
                    detections.add(
                        Detection(
                            label = labels[classId],
                            confidence = confidence * maxClassProb,
                            boundingBox = BoundingBox(
                                left = prediction[0] - prediction[2] / 2,
                                top = prediction[1] - prediction[3] / 2,
                                right = prediction[0] + prediction[2] / 2,
                                bottom = prediction[1] + prediction[3] / 2
                            )
                        )
                    )
                }
            }
        }
        
        // Non-maximum suppression
        nonMaxSuppression(detections, 0.45f)
    }
    
    private fun nonMaxSuppression(
        detections: MutableList<Detection>,
        iouThreshold: Float
    ): List<Detection> {
        if (detections.isEmpty()) return detections
        
        // Sort by confidence
        detections.sortByDescending { it.confidence }
        
        val selected = mutableListOf<Detection>()
        val active = detections.toMutableList()
        
        while (active.isNotEmpty()) {
            val best = active.removeAt(0)
            selected.add(best)
            
            active.removeAll { detection ->
                calculateIoU(best.boundingBox, detection.boundingBox) > iouThreshold
            }
        }
        
        return selected
    }
    
    private fun calculateIoU(box1: BoundingBox, box2: BoundingBox): Float {
        val x1 = maxOf(box1.left, box2.left)
        val y1 = maxOf(box1.top, box2.top)
        val x2 = minOf(box1.right, box2.right)
        val y2 = minOf(box1.bottom, box2.bottom)
        
        val intersection = maxOf(0f, x2 - x1) * maxOf(0f, y2 - y1)
        val area1 = (box1.right - box1.left) * (box1.bottom - box1.top)
        val area2 = (box2.right - box2.left) * (box2.bottom - box2.top)
        val union = area1 + area2 - intersection
        
        return if (union > 0) intersection / union else 0f
    }
}
```

### 3.2 Photo Quality Analysis

```kotlin
// Photo Quality Analyzer
@Singleton
class PhotoQualityAnalyzer @Inject constructor() {
    
    data class QualityResult(
        val isAcceptable: Boolean,
        val overallScore: Float,
        val issues: List<QualityIssue>,
        val suggestions: List<String>,
        val metrics: QualityMetrics
    )
    
    data class QualityMetrics(
        val brightness: Float,
        val contrast: Float,
        val sharpness: Float,
        val noise: Float,
        val resolution: Pair<Int, Int>
    )
    
    suspend fun analyze(bitmap: Bitmap): QualityResult = 
        withContext(Dispatchers.Default) {
            
        val issues = mutableListOf<QualityIssue>()
        val suggestions = mutableListOf<String>()
        
        // Calculate metrics
        val metrics = calculateMetrics(bitmap)
        
        // Check brightness
        when {
            metrics.brightness < 0.2f -> {
                issues.add(QualityIssue.TOO_DARK)
                suggestions.add("Image is too dark. Use flash or find better lighting.")
            }
            metrics.brightness > 0.85f -> {
                issues.add(QualityIssue.OVEREXPOSED)
                suggestions.add("Image is overexposed. Reduce lighting or disable flash.")
            }
        }
        
        // Check blur/sharpness
        if (metrics.sharpness < 0.3f) {
            issues.add(QualityIssue.BLURRY)
            suggestions.add("Image is blurry. Hold the device steady and ensure focus.")
        }
        
        // Check resolution
        val (width, height) = metrics.resolution
        if (width < 1280 || height < 720) {
            issues.add(QualityIssue.LOW_RESOLUTION)
            suggestions.add("Image resolution too low. Ensure camera is set to high quality.")
        }
        
        // Check noise
        if (metrics.noise > 0.7f) {
            issues.add(QualityIssue.NOISY)
            suggestions.add("Image has too much noise. Improve lighting conditions.")
        }
        
        // Calculate overall score
        val overallScore = calculateOverallScore(metrics)
        val isAcceptable = issues.none { it.isCritical } && overallScore > 0.6f
        
        QualityResult(
            isAcceptable = isAcceptable,
            overallScore = overallScore,
            issues = issues,
            suggestions = suggestions,
            metrics = metrics
        )
    }
    
    private fun calculateMetrics(bitmap: Bitmap): QualityMetrics {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        
        // Calculate brightness (average luminance)
        var totalLuminance = 0.0
        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0
            val g = (pixel shr 8 and 0xFF) / 255.0
            val b = (pixel and 0xFF) / 255.0
            totalLuminance += 0.299 * r + 0.587 * g + 0.114 * b
        }
        val brightness = (totalLuminance / pixels.size).toFloat()
        
        // Calculate contrast (standard deviation of luminance)
        var sumSquaredDiff = 0.0
        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0
            val g = (pixel shr 8 and 0xFF) / 255.0
            val b = (pixel and 0xFF) / 255.0
            val luminance = 0.299 * r + 0.587 * g + 0.114 * b
            sumSquaredDiff += (luminance - brightness) * (luminance - brightness)
        }
        val contrast = sqrt(sumSquaredDiff / pixels.size).toFloat()
        
        // Calculate sharpness using Laplacian
        val sharpness = calculateLaplacianVariance(bitmap)
        
        // Estimate noise
        val noise = estimateNoise(bitmap)
        
        return QualityMetrics(
            brightness = brightness,
            contrast = contrast,
            sharpness = sharpness,
            noise = noise,
            resolution = Pair(bitmap.width, bitmap.height)
        )
    }
    
    private fun calculateLaplacianVariance(bitmap: Bitmap): Float {
        // Simplified Laplacian for sharpness detection
        val grayscale = toGrayscale(bitmap)
        val laplacian = applyLaplacian(grayscale)
        return calculateVariance(laplacian)
    }
    
    private fun estimateNoise(bitmap: Bitmap): Float {
        // Simplified noise estimation
        val grayscale = toGrayscale(bitmap)
        val denoised = applyMedianFilter(grayscale)
        
        var totalDiff = 0.0
        for (i in grayscale.indices) {
            totalDiff += abs(grayscale[i] - denoised[i])
        }
        
        return (totalDiff / grayscale.size / 255.0).toFloat()
    }
    
    private fun calculateOverallScore(metrics: QualityMetrics): Float {
        val brightnessScore = when {
            metrics.brightness < 0.2f || metrics.brightness > 0.85f -> 0f
            metrics.brightness < 0.3f || metrics.brightness > 0.75f -> 0.5f
            else -> 1f
        }
        
        val contrastScore = when {
            metrics.contrast < 0.1f -> 0f
            metrics.contrast < 0.2f -> 0.5f
            else -> 1f
        }
        
        val sharpnessScore = when {
            metrics.sharpness < 0.2f -> 0f
            metrics.sharpness < 0.3f -> 0.5f
            else -> 1f
        }
        
        val noiseScore = when {
            metrics.noise > 0.7f -> 0f
            metrics.noise > 0.5f -> 0.5f
            else -> 1f
        }
        
        return (brightnessScore * 0.25f + 
                contrastScore * 0.25f + 
                sharpnessScore * 0.35f + 
                noiseScore * 0.15f)
    }
}

enum class QualityIssue(val isCritical: Boolean) {
    TOO_DARK(false),
    OVEREXPOSED(false),
    BLURRY(true),
    LOW_RESOLUTION(true),
    NOISY(false),
    OUT_OF_FOCUS(true),
    MOTION_BLUR(true)
}
```

## 4. Speech Integration

### 4.1 Speech Recognition & Synthesis

```kotlin
// Speech Service Implementation
@Singleton
class SpeechService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val llmEngine: LLMEngine
) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private val recognitionResults = MutableSharedFlow<String>()
    
    suspend fun initialize() {
        initializeSpeechRecognition()
        initializeTextToSpeech()
    }
    
    private fun initializeSpeechRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w("SpeechService", "Speech recognition not available")
            return
        }
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle) {
                    val matches = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )
                    matches?.firstOrNull()?.let { text ->
                        GlobalScope.launch {
                            recognitionResults.emit(text)
                            processVoiceCommand(text)
                        }
                    }
                }
                
                override fun onError(error: Int) {
                    Log.e("SpeechService", "Recognition error: $error")
                }
                
                // Other callbacks...
                override fun onReadyForSpeech(params: Bundle) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle) {}
                override fun onEvent(eventType: Int, params: Bundle) {}
            })
        }
    }
    
    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.apply {
                    language = Locale.US
                    setSpeechRate(1.0f)
                    setPitch(1.0f)
                }
            }
        }
    }
    
    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        speechRecognizer?.startListening(intent)
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
    }
    
    fun speak(text: String, priority: Int = TextToSpeech.QUEUE_FLUSH) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech?.speak(text, priority, null, UUID.randomUUID().toString())
        } else {
            @Suppress("DEPRECATION")
            textToSpeech?.speak(text, priority, null)
        }
    }
    
    private suspend fun processVoiceCommand(command: String) {
        // Process common commands
        when {
            command.contains("take photo", ignoreCase = true) -> {
                // Trigger photo capture
                speak("Ready to capture photo")
            }
            command.contains("help", ignoreCase = true) -> {
                // Get contextual help from LLM
                val response = llmEngine.generateResponse(
                    "The technician needs help with: $command"
                )
                response.onSuccess { speak(it.text) }
            }
            command.contains("next step", ignoreCase = true) -> {
                speak("Moving to the next step")
            }
            command.contains("previous", ignoreCase = true) -> {
                speak("Going back to previous step")
            }
            else -> {
                // Send to LLM for interpretation
                val response = llmEngine.processCommand(command)
                response.onSuccess { 
                    handleCommand(it)
                }
            }
        }
    }
    
    private fun handleCommand(command: Command) {
        when (command.type) {
            CommandType.NAVIGATION -> {
                // Handle navigation command
            }
            CommandType.CAPTURE -> {
                // Handle capture command
            }
            CommandType.VALIDATION -> {
                // Handle validation command
            }
            CommandType.INFORMATION -> {
                speak(command.response)
            }
        }
    }
    
    fun release() {
        speechRecognizer?.destroy()
        textToSpeech?.shutdown()
    }
}

// Voice Command Models
data class Command(
    val type: CommandType,
    val action: String,
    val parameters: Map<String, Any>,
    val response: String
)

enum class CommandType {
    NAVIGATION,
    CAPTURE,
    VALIDATION,
    INFORMATION,
    TROUBLESHOOTING
}
```

## 5. Model Management & Optimization

### 5.1 Model Lifecycle Management

```kotlin
// Model Manager
@Singleton
class ModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val memoryManager: MemoryManager,
    private val performanceMonitor: PerformanceMonitor
) {
    
    private val loadedModels = mutableMapOf<ModelType, Any>()
    private val modelSizes = mapOf(
        ModelType.LLM to 2_000_000_000L, // 2GB
        ModelType.ONT_DETECTOR to 30_000_000L, // 30MB
        ModelType.EQUIPMENT_DETECTOR to 20_000_000L, // 20MB
        ModelType.QUALITY_ANALYZER to 15_000_000L, // 15MB
        ModelType.TEXT_RECOGNIZER to 10_000_000L // 10MB (ML Kit)
    )
    
    suspend fun loadModel(type: ModelType): Result<Any> = withContext(Dispatchers.IO) {
        try {
            // Check if already loaded
            loadedModels[type]?.let {
                return@withContext Result.success(it)
            }
            
            // Check memory availability
            val requiredMemory = modelSizes[type] ?: 0L
            if (!memoryManager.canAllocate(requiredMemory)) {
                // Try to free memory
                freeMemoryForModel(requiredMemory)
                
                if (!memoryManager.canAllocate(requiredMemory)) {
                    return@withContext Result.failure(
                        InsufficientMemoryException(requiredMemory)
                    )
                }
            }
            
            // Load model
            val model = when (type) {
                ModelType.LLM -> loadLLM()
                ModelType.ONT_DETECTOR -> loadONTDetector()
                ModelType.EQUIPMENT_DETECTOR -> loadEquipmentDetector()
                ModelType.QUALITY_ANALYZER -> loadQualityAnalyzer()
                ModelType.TEXT_RECOGNIZER -> loadTextRecognizer()
            }
            
            loadedModels[type] = model
            performanceMonitor.recordModelLoad(type)
            
            Result.success(model)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun freeMemoryForModel(requiredMemory: Long) {
        // Unload models based on priority
        val priority = listOf(
            ModelType.QUALITY_ANALYZER,
            ModelType.TEXT_RECOGNIZER,
            ModelType.EQUIPMENT_DETECTOR,
            ModelType.ONT_DETECTOR
            // Never unload LLM
        )
        
        var freedMemory = 0L
        for (modelType in priority) {
            if (freedMemory >= requiredMemory) break
            
            loadedModels[modelType]?.let {
                unloadModel(modelType)
                freedMemory += modelSizes[modelType] ?: 0L
            }
        }
    }
    
    fun unloadModel(type: ModelType) {
        loadedModels.remove(type)?.let { model ->
            when (model) {
                is Interpreter -> model.close()
                is MLCChat -> model.release()
                // Handle other model types
            }
            System.gc()
        }
    }
    
    fun getLoadedModels(): Set<ModelType> {
        return loadedModels.keys
    }
    
    fun getTotalMemoryUsage(): Long {
        return loadedModels.keys.sumOf { modelSizes[it] ?: 0L }
    }
}

enum class ModelType {
    LLM,
    ONT_DETECTOR,
    EQUIPMENT_DETECTOR,
    QUALITY_ANALYZER,
    TEXT_RECOGNIZER
}
```

## 6. Performance Monitoring

### 6.1 AI Performance Tracking

```kotlin
// Performance Monitor
@Singleton
class AIPerformanceMonitor @Inject constructor(
    private val analytics: AnalyticsService
) {
    
    private val metrics = mutableMapOf<String, MutableList<Long>>()
    
    fun recordInferenceTime(model: String, timeMs: Long) {
        metrics.getOrPut("${model}_inference") { mutableListOf() }.add(timeMs)
        
        // Log slow inference
        if (timeMs > getThreshold(model)) {
            Log.w("AIPerf", "$model inference slow: ${timeMs}ms")
        }
        
        // Send to analytics
        analytics.logEvent("ai_inference", mapOf(
            "model" to model,
            "time_ms" to timeMs
        ))
    }
    
    fun recordModelLoadTime(model: String, timeMs: Long) {
        metrics.getOrPut("${model}_load") { mutableListOf() }.add(timeMs)
    }
    
    fun recordPhotoValidation(type: PhotoType, timeMs: Long) {
        metrics.getOrPut("validation_${type}") { mutableListOf() }.add(timeMs)
    }
    
    fun getAverageInferenceTime(model: String): Double {
        return metrics["${model}_inference"]?.average() ?: 0.0
    }
    
    fun getPerformanceReport(): PerformanceReport {
        return PerformanceReport(
            llmAverageInference = getAverageInferenceTime("phi-3.5-mini"),
            visionAverageProcessing = metrics.filter { 
                it.key.startsWith("validation_") 
            }.values.flatten().average(),
            totalInferences = metrics.values.sumOf { it.size },
            slowInferences = metrics.values.flatten().count { it > 1000 }
        )
    }
    
    private fun getThreshold(model: String): Long {
        return when (model) {
            "phi-3.5-mini" -> 2000 // 2 seconds
            "ont_detector" -> 500 // 500ms
            "equipment_detector" -> 750 // 750ms
            else -> 1000 // 1 second default
        }
    }
}

data class PerformanceReport(
    val llmAverageInference: Double,
    val visionAverageProcessing: Double,
    val totalInferences: Int,
    val slowInferences: Int
)
```

## Conclusion

This comprehensive AI/ML integration architecture provides:

1. **Phi-3.5 Mini LLM**: Complete integration with context management and optimization
2. **Computer Vision Pipeline**: Multi-model validation with ONT detection, equipment recognition, and quality analysis
3. **Speech Integration**: Voice commands and text-to-speech for hands-free operation
4. **Model Management**: Lifecycle management with memory optimization
5. **Performance Monitoring**: Comprehensive tracking and optimization

The architecture ensures:
- **Offline Capability**: All models run on-device
- **Performance**: Optimized inference with GPU acceleration
- **Memory Efficiency**: Dynamic model loading/unloading
- **Accuracy**: Multiple validation layers with confidence scoring
- **User Experience**: Natural language interaction with voice support