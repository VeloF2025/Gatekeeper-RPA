# FibreField Technician BOT - Complete Technical Architecture

## 1. System Overview & Architecture Philosophy

### 1.1 Architecture Principles
- **Clean Architecture**: Separation of concerns with distinct layers
- **MVVM Pattern**: Model-View-ViewModel for UI architecture
- **Repository Pattern**: Abstraction between data sources and business logic
- **Dependency Injection**: Using Hilt for IoC container
- **Offline-First**: Local database as single source of truth
- **Reactive Programming**: Kotlin Coroutines and Flow for async operations
- **Modular Design**: Feature-based modules for scalability

### 1.2 High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │
│  │   Compose   │ │ ViewModels  │ │ Navigation  │       │
│  │     UI      │ │   (MVVM)    │ │  Component  │       │
│  └─────────────┘ └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │
│  │  Use Cases  │ │   Entities  │ │ Repositories│       │
│  │             │ │             │ │ (Interface) │       │
│  └─────────────┘ └─────────────┘ └─────────────┘       │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                           │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │   Room   │ │ Retrofit │ │   Cache  │ │   Sync   │  │
│  │    DB    │ │   API    │ │  Manager │ │  Engine  │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
└─────────────────────────────────────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │   AI/ML  │ │  CameraX │ │ Location │ │ Security │  │
│  │  Models  │ │          │ │ Services │ │  Keystore│  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
└─────────────────────────────────────────────────────────┘
```

## 2. Complete Module Architecture

### 2.1 Project Structure

```
fibrefield-technician-android/
├── app/                                    # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fibreflow/technician/
│   │   │   │   ├── FibreFieldApplication.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── MainViewModel.kt
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
│
├── core/                                   # Core shared modules
│   ├── common/
│   │   ├── src/main/java/com/fibreflow/core/common/
│   │   │   ├── constants/
│   │   │   │   ├── AppConstants.kt
│   │   │   │   ├── ErrorCodes.kt
│   │   │   │   └── PhotoTypes.kt
│   │   │   ├── extensions/
│   │   │   │   ├── BitmapExtensions.kt
│   │   │   │   ├── FlowExtensions.kt
│   │   │   │   └── StringExtensions.kt
│   │   │   ├── utils/
│   │   │   │   ├── DateTimeUtils.kt
│   │   │   │   ├── FileUtils.kt
│   │   │   │   └── PermissionUtils.kt
│   │   │   └── result/
│   │   │       └── Result.kt
│   │   └── build.gradle.kts
│   │
│   ├── database/
│   │   ├── src/main/java/com/fibreflow/core/database/
│   │   │   ├── FibreFieldDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── DropDao.kt
│   │   │   │   ├── InstallationDao.kt
│   │   │   │   ├── PhotoDao.kt
│   │   │   │   ├── ProjectDao.kt
│   │   │   │   ├── TechnicianDao.kt
│   │   │   │   ├── RemediationDao.kt
│   │   │   │   └── SyncQueueDao.kt
│   │   │   ├── entities/
│   │   │   │   ├── DropEntity.kt
│   │   │   │   ├── InstallationEntity.kt
│   │   │   │   ├── PhotoEntity.kt
│   │   │   │   ├── ProjectEntity.kt
│   │   │   │   ├── TechnicianEntity.kt
│   │   │   │   ├── RemediationEntity.kt
│   │   │   │   └── SyncQueueEntity.kt
│   │   │   ├── converters/
│   │   │   │   ├── DateConverters.kt
│   │   │   │   ├── LocationConverters.kt
│   │   │   │   └── StatusConverters.kt
│   │   │   └── migrations/
│   │   │       └── Migrations.kt
│   │   └── build.gradle.kts
│   │
│   ├── network/
│   │   ├── src/main/java/com/fibreflow/core/network/
│   │   │   ├── api/
│   │   │   │   ├── FibreFlowApi.kt
│   │   │   │   ├── AuthApi.kt
│   │   │   │   ├── DropApi.kt
│   │   │   │   ├── InstallationApi.kt
│   │   │   │   └── SyncApi.kt
│   │   │   ├── models/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── interceptors/
│   │   │   │   ├── AuthInterceptor.kt
│   │   │   │   ├── LoggingInterceptor.kt
│   │   │   │   └── CertificatePinningInterceptor.kt
│   │   │   └── NetworkModule.kt
│   │   └── build.gradle.kts
│   │
│   ├── ai/
│   │   ├── src/main/java/com/fibreflow/core/ai/
│   │   │   ├── llm/
│   │   │   │   ├── LLMEngine.kt
│   │   │   │   ├── Phi35MiniEngine.kt
│   │   │   │   ├── ConversationManager.kt
│   │   │   │   └── PromptTemplates.kt
│   │   │   ├── vision/
│   │   │   │   ├── VisionPipeline.kt
│   │   │   │   ├── ONTLightDetector.kt
│   │   │   │   ├── YOLOv8Detector.kt
│   │   │   │   ├── PhotoQualityAnalyzer.kt
│   │   │   │   └── MLKitWrapper.kt
│   │   │   ├── speech/
│   │   │   │   ├── SpeechRecognizer.kt
│   │   │   │   └── TextToSpeech.kt
│   │   │   └── AIModule.kt
│   │   └── build.gradle.kts
│   │
│   └── design/
│       ├── src/main/java/com/fibreflow/core/design/
│       │   ├── theme/
│       │   │   ├── Color.kt
│       │   │   ├── Theme.kt
│       │   │   └── Typography.kt
│       │   ├── components/
│       │   │   ├── FibreFieldButton.kt
│       │   │   ├── FibreFieldCard.kt
│       │   │   ├── LoadingIndicator.kt
│       │   │   └── ErrorDialog.kt
│       │   └── animations/
│       │       └── Animations.kt
│       └── build.gradle.kts
│
├── domain/                                 # Business logic modules
│   ├── authentication/
│   │   ├── src/main/java/com/fibreflow/domain/auth/
│   │   │   ├── entities/
│   │   │   │   └── Technician.kt
│   │   │   ├── repositories/
│   │   │   │   └── AuthRepository.kt
│   │   │   └── usecases/
│   │   │       ├── LoginUseCase.kt
│   │   │       ├── LogoutUseCase.kt
│   │   │       └── RefreshTokenUseCase.kt
│   │   └── build.gradle.kts
│   │
│   ├── installation/
│   │   ├── src/main/java/com/fibreflow/domain/installation/
│   │   │   ├── entities/
│   │   │   │   ├── Installation.kt
│   │   │   │   └── Photo.kt
│   │   │   ├── repositories/
│   │   │   │   └── InstallationRepository.kt
│   │   │   └── usecases/
│   │   │       ├── StartInstallationUseCase.kt
│   │   │       ├── CapturePhotoUseCase.kt
│   │   │       └── SubmitInstallationUseCase.kt
│   │   └── build.gradle.kts
│   │
│   └── drops/
│       ├── src/main/java/com/fibreflow/domain/drops/
│       │   ├── entities/
│       │   │   └── Drop.kt
│       │   ├── repositories/
│       │   │   └── DropRepository.kt
│       │   └── usecases/
│       │       ├── GetAvailableDropsUseCase.kt
│       │       └── ValidateDropUseCase.kt
│       └── build.gradle.kts
│
├── feature/                                # Feature modules
│   ├── authentication/
│   │   ├── src/main/java/com/fibreflow/feature/auth/
│   │   │   ├── data/
│   │   │   │   ├── AuthRepositoryImpl.kt
│   │   │   │   └── datasources/
│   │   │   ├── presentation/
│   │   │   │   ├── login/
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   └── LoginViewModel.kt
│   │   │   │   └── biometric/
│   │   │   │       └── BiometricAuthManager.kt
│   │   │   └── di/
│   │   │       └── AuthModule.kt
│   │   └── build.gradle.kts
│   │
│   ├── installation/
│   │   ├── src/main/java/com/fibreflow/feature/installation/
│   │   │   ├── data/
│   │   │   │   └── InstallationRepositoryImpl.kt
│   │   │   ├── presentation/
│   │   │   │   ├── workflow/
│   │   │   │   │   ├── InstallationWorkflowScreen.kt
│   │   │   │   │   └── InstallationWorkflowViewModel.kt
│   │   │   │   ├── camera/
│   │   │   │   │   ├── CameraScreen.kt
│   │   │   │   │   └── CameraViewModel.kt
│   │   │   │   └── review/
│   │   │   │       ├── PhotoReviewScreen.kt
│   │   │   │       └── PhotoReviewViewModel.kt
│   │   │   └── di/
│   │   │       └── InstallationModule.kt
│   │   └── build.gradle.kts
│   │
│   ├── drops/
│   │   ├── src/main/java/com/fibreflow/feature/drops/
│   │   │   ├── data/
│   │   │   │   └── DropRepositoryImpl.kt
│   │   │   ├── presentation/
│   │   │   │   ├── list/
│   │   │   │   │   ├── DropListScreen.kt
│   │   │   │   │   └── DropListViewModel.kt
│   │   │   │   └── map/
│   │   │   │       ├── DropMapScreen.kt
│   │   │   │       └── DropMapViewModel.kt
│   │   │   └── di/
│   │   │       └── DropModule.kt
│   │   └── build.gradle.kts
│   │
│   ├── activation/
│   │   └── ...similar structure...
│   │
│   └── remediation/
│       └── ...similar structure...
│
├── infrastructure/                         # Infrastructure modules
│   ├── sync/
│   │   ├── src/main/java/com/fibreflow/infrastructure/sync/
│   │   │   ├── SyncManager.kt
│   │   │   ├── SyncWorker.kt
│   │   │   ├── ConflictResolver.kt
│   │   │   └── SyncModule.kt
│   │   └── build.gradle.kts
│   │
│   ├── location/
│   │   ├── src/main/java/com/fibreflow/infrastructure/location/
│   │   │   ├── LocationService.kt
│   │   │   ├── ProximityDetector.kt
│   │   │   └── LocationModule.kt
│   │   └── build.gradle.kts
│   │
│   └── security/
│       ├── src/main/java/com/fibreflow/infrastructure/security/
│       │   ├── SecureStorageManager.kt
│       │   ├── BiometricAuthenticator.kt
│       │   └── SecurityModule.kt
│       └── build.gradle.kts
│
├── gradle/
│   └── libs.versions.toml                 # Version catalog
├── build.gradle.kts                       # Root build file
└── settings.gradle.kts                    # Module inclusion
```

### 2.2 Dependency Graph

```kotlin
// settings.gradle.kts
include(
    ":app",
    // Core modules
    ":core:common",
    ":core:database",
    ":core:network",
    ":core:ai",
    ":core:design",
    // Domain modules
    ":domain:authentication",
    ":domain:installation",
    ":domain:drops",
    ":domain:activation",
    ":domain:remediation",
    // Feature modules
    ":feature:authentication",
    ":feature:installation",
    ":feature:drops",
    ":feature:activation",
    ":feature:remediation",
    // Infrastructure modules
    ":infrastructure:sync",
    ":infrastructure:location",
    ":infrastructure:security"
)
```

## 3. Data Layer Architecture

### 3.1 Room Database Schema

```kotlin
// Complete Database Entity Definitions

@Entity(tableName = "drops")
data class DropEntity(
    @PrimaryKey
    @ColumnInfo(name = "drop_number")
    val dropNumber: String,
    
    @ColumnInfo(name = "project_id")
    val projectId: Int,
    
    @Embedded
    val location: LocationData,
    
    @ColumnInfo(name = "address")
    val address: String,
    
    @ColumnInfo(name = "status")
    val status: DropStatus,
    
    @ColumnInfo(name = "customer_name")
    val customerName: String?,
    
    @ColumnInfo(name = "assigned_technician_id")
    val assignedTechnicianId: String?,
    
    @ColumnInfo(name = "installation_date")
    val installationDate: Date?,
    
    @ColumnInfo(name = "activation_status")
    val activationStatus: ActivationStatus,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date,
    
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    
    @ColumnInfo(name = "last_sync_attempt")
    val lastSyncAttempt: Date? = null
)

@Entity(
    tableName = "installations",
    foreignKeys = [
        ForeignKey(
            entity = DropEntity::class,
            parentColumns = ["drop_number"],
            childColumns = ["drop_number"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TechnicianEntity::class,
            parentColumns = ["technician_id"],
            childColumns = ["technician_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["drop_number"]),
        Index(value = ["technician_id"]),
        Index(value = ["status"])
    ]
)
data class InstallationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "installation_id")
    val installationId: Long = 0,
    
    @ColumnInfo(name = "drop_number")
    val dropNumber: String,
    
    @ColumnInfo(name = "technician_id")
    val technicianId: String,
    
    @ColumnInfo(name = "start_time")
    val startTime: Date,
    
    @ColumnInfo(name = "end_time")
    val endTime: Date? = null,
    
    @ColumnInfo(name = "status")
    val status: InstallationStatus,
    
    @ColumnInfo(name = "ont_serial")
    val ontSerial: String? = null,
    
    @ColumnInfo(name = "router_serial")
    val routerSerial: String? = null,
    
    @ColumnInfo(name = "cable_length")
    val cableLength: Float? = null,
    
    @ColumnInfo(name = "speed_test_download")
    val speedTestDownload: Float? = null,
    
    @ColumnInfo(name = "speed_test_upload")
    val speedTestUpload: Float? = null,
    
    @ColumnInfo(name = "customer_signature")
    val customerSignature: String? = null,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "submission_time")
    val submissionTime: Date? = null,
    
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = InstallationEntity::class,
            parentColumns = ["installation_id"],
            childColumns = ["installation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["installation_id"]),
        Index(value = ["photo_type"])
    ]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "photo_id")
    val photoId: Long = 0,
    
    @ColumnInfo(name = "installation_id")
    val installationId: Long,
    
    @ColumnInfo(name = "photo_type")
    val photoType: PhotoType,
    
    @ColumnInfo(name = "file_path")
    val filePath: String,
    
    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String? = null,
    
    @ColumnInfo(name = "capture_time")
    val captureTime: Date,
    
    @Embedded
    val location: LocationData? = null,
    
    @ColumnInfo(name = "validation_status")
    val validationStatus: ValidationStatus,
    
    @ColumnInfo(name = "validation_confidence")
    val validationConfidence: Float? = null,
    
    @ColumnInfo(name = "validation_issues")
    val validationIssues: String? = null, // JSON array
    
    @ColumnInfo(name = "manual_override")
    val manualOverride: Boolean = false,
    
    @ColumnInfo(name = "override_reason")
    val overrideReason: String? = null,
    
    @ColumnInfo(name = "ai_metadata")
    val aiMetadata: String? = null, // JSON object
    
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    
    @ColumnInfo(name = "upload_url")
    val uploadUrl: String? = null
)

// Additional entities for complete schema
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    @ColumnInfo(name = "project_id")
    val projectId: Int,
    
    @ColumnInfo(name = "project_name")
    val projectName: String,
    
    @ColumnInfo(name = "boundary_polygon")
    val boundaryPolygon: String, // GeoJSON
    
    @ColumnInfo(name = "total_drops")
    val totalDrops: Int,
    
    @ColumnInfo(name = "completed_drops")
    val completedDrops: Int = 0,
    
    @ColumnInfo(name = "active")
    val active: Boolean = true
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sync_id")
    val syncId: Long = 0,
    
    @ColumnInfo(name = "entity_type")
    val entityType: EntityType,
    
    @ColumnInfo(name = "entity_id")
    val entityId: String,
    
    @ColumnInfo(name = "operation")
    val operation: SyncOperation,
    
    @ColumnInfo(name = "payload")
    val payload: String, // JSON
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    
    @ColumnInfo(name = "retry_count")
    val retryCount: Int = 0,
    
    @ColumnInfo(name = "last_error")
    val lastError: String? = null
)
```

### 3.2 Repository Implementation

```kotlin
// Repository Pattern Implementation

interface DropRepository {
    suspend fun getAvailableDrops(projectId: Int): Flow<List<Drop>>
    suspend fun getDropByNumber(dropNumber: String): Drop?
    suspend fun validateDrop(dropNumber: String, location: Location): ValidationResult
    suspend fun updateDropStatus(dropNumber: String, status: DropStatus)
    suspend fun syncDrops(): SyncResult
}

@Singleton
class DropRepositoryImpl @Inject constructor(
    private val dropDao: DropDao,
    private val dropApi: DropApi,
    private val syncManager: SyncManager,
    private val locationService: LocationService
) : DropRepository {
    
    override suspend fun getAvailableDrops(projectId: Int): Flow<List<Drop>> {
        return dropDao.getDropsByProjectAndStatus(projectId, DropStatus.AVAILABLE)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun getDropByNumber(dropNumber: String): Drop? {
        // Try local first
        val localDrop = dropDao.getDropByNumber(dropNumber)
        if (localDrop != null) {
            return localDrop.toDomainModel()
        }
        
        // If not found locally, try remote
        return try {
            val remoteDrop = dropApi.getDrop(dropNumber)
            dropDao.insert(remoteDrop.toEntity())
            remoteDrop.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun validateDrop(
        dropNumber: String, 
        location: Location
    ): ValidationResult {
        val drop = getDropByNumber(dropNumber) 
            ?: return ValidationResult.Error("Drop not found")
        
        // Check proximity
        val distance = locationService.calculateDistance(
            location,
            Location(drop.latitude, drop.longitude)
        )
        
        if (distance > 50) { // 50 meters threshold
            return ValidationResult.Warning(
                "You are ${distance}m away from the drop location"
            )
        }
        
        // Check status
        if (drop.status != DropStatus.AVAILABLE) {
            return ValidationResult.Error(
                "Drop is not available. Current status: ${drop.status}"
            )
        }
        
        return ValidationResult.Success
    }
    
    override suspend fun updateDropStatus(dropNumber: String, status: DropStatus) {
        dropDao.updateStatus(dropNumber, status)
        syncManager.queueSync(
            EntityType.DROP,
            dropNumber,
            SyncOperation.UPDATE
        )
    }
    
    override suspend fun syncDrops(): SyncResult {
        return syncManager.syncEntity(EntityType.DROP)
    }
}
```

## 4. AI/ML Integration Architecture

### 4.1 LLM Engine Implementation

```kotlin
// Phi-3.5 Mini Integration using MLC LLM

interface LLMEngine {
    suspend fun initialize(): Result<Unit>
    suspend fun generateResponse(prompt: String, context: ConversationContext? = null): Result<String>
    suspend fun generateGuidance(step: InstallationStep, metadata: Map<String, Any> = emptyMap()): Result<String>
    fun release()
}

@Singleton
class Phi35MiniEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: LLMConfig
) : LLMEngine {
    
    private var mlcChat: MLCChat? = null
    private val contextManager = ConversationContextManager()
    
    override suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Initialize MLC runtime
            MLCEngine.init(context)
            
            // Load Phi-3.5 Mini model
            val modelConfig = MLCChatConfig.fromAssets(
                context.assets,
                "models/phi-3.5-mini-instruct-q4_k_m"
            )
            
            mlcChat = MLCChat(modelConfig).apply {
                // Configure generation parameters
                setMaxGenLength(config.maxTokens)
                setTemperature(config.temperature)
                setTopP(config.topP)
                setPresencePenalty(config.presencePenalty)
                setFrequencyPenalty(config.frequencyPenalty)
            }
            
            // Warm up model
            mlcChat?.prefill(WARMUP_PROMPT)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generateResponse(
        prompt: String,
        context: ConversationContext?
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            val chat = mlcChat ?: return Result.failure(
                IllegalStateException("LLM not initialized")
            )
            
            // Build conversation with context
            val conversation = buildConversation(prompt, context)
            
            // Generate response
            val response = chat.generate(conversation)
            
            // Update context
            contextManager.addTurn(prompt, response)
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generateGuidance(
        step: InstallationStep,
        metadata: Map<String, Any>
    ): Result<String> {
        val prompt = PromptTemplates.getStepGuidance(step, metadata)
        return generateResponse(prompt)
    }
    
    private fun buildConversation(
        prompt: String,
        context: ConversationContext?
    ): String {
        return buildString {
            appendLine("<|system|>")
            appendLine(SYSTEM_PROMPT)
            appendLine("<|end|>")
            
            // Add context if available
            context?.let {
                it.previousTurns.takeLast(5).forEach { turn ->
                    appendLine("<|user|>")
                    appendLine(turn.userInput)
                    appendLine("<|end|>")
                    appendLine("<|assistant|>")
                    appendLine(turn.assistantResponse)
                    appendLine("<|end|>")
                }
            }
            
            appendLine("<|user|>")
            appendLine(prompt)
            appendLine("<|end|>")
            appendLine("<|assistant|>")
        }
    }
    
    companion object {
        private const val SYSTEM_PROMPT = """
            You are a helpful fiber optic installation assistant. 
            Provide clear, step-by-step guidance to technicians.
            Be concise but thorough. Safety is paramount.
            If you detect any safety issues, alert immediately.
        """
        
        private const val WARMUP_PROMPT = "Hello"
    }
}

// Prompt Templates
object PromptTemplates {
    fun getStepGuidance(step: InstallationStep, metadata: Map<String, Any>): String {
        return when (step) {
            InstallationStep.CABLE_SPAN -> """
                Guide the technician to capture the cable span photo.
                Requirements:
                - Show full cable span from pole to house
                - Ensure drop number label is visible
                - Check for any visible damage or sagging
                Current location: ${metadata["address"]}
            """.trimIndent()
            
            InstallationStep.ONT_ACTIVE -> """
                Guide the technician to capture the ONT activation photo.
                Requirements:
                - All 4 lights must be green (Power, LOS, PON, LAN)
                - Drop number label must be visible
                - Serial number should be readable
                Drop number: ${metadata["dropNumber"]}
            """.trimIndent()
            
            // ... other steps
            else -> "Proceed with the next step of the installation."
        }
    }
    
    fun getValidationFeedback(issues: List<ValidationIssue>): String {
        return buildString {
            appendLine("Photo validation failed. Issues detected:")
            issues.forEach { issue ->
                appendLine("- ${issue.getUserFriendlyMessage()}")
            }
            appendLine("\nPlease retake the photo addressing these issues.")
        }
    }
}
```

### 4.2 Computer Vision Pipeline

```kotlin
// Vision Validation Pipeline with Multiple Models

@Singleton
class VisionValidationPipeline @Inject constructor(
    private val mlKit: MLKitWrapper,
    private val ontDetector: ONTLightDetector,
    private val yoloDetector: YOLOv8Detector,
    private val qualityAnalyzer: PhotoQualityAnalyzer
) {
    
    suspend fun validatePhoto(
        bitmap: Bitmap,
        photoType: PhotoType,
        metadata: PhotoMetadata
    ): PhotoValidationResult = coroutineScope {
        
        // Run quality analysis in parallel
        val qualityDeferred = async { qualityAnalyzer.analyze(bitmap) }
        val ocrDeferred = async { mlKit.performOCR(bitmap) }
        
        // Wait for quality results
        val quality = qualityDeferred.await()
        if (!quality.isAcceptable) {
            return@coroutineScope PhotoValidationResult.QualityIssue(quality)
        }
        
        // Type-specific validation
        when (photoType) {
            PhotoType.ONT_ACTIVE -> validateONTActive(bitmap, metadata, ocrDeferred.await())
            PhotoType.BARCODE -> validateBarcode(bitmap, ocrDeferred.await())
            PhotoType.CABLE_SPAN -> validateCableSpan(bitmap, metadata)
            PhotoType.HOME_ENTRY -> validateHomeEntry(bitmap)
            PhotoType.ROUTER_CONNECTED -> validateRouterConnection(bitmap)
            PhotoType.SPEED_TEST -> validateSpeedTest(bitmap, ocrDeferred.await())
            PhotoType.CLEANUP -> validateCleanup(bitmap)
            PhotoType.CUSTOMER_SIGNATURE -> validateSignature(bitmap)
            PhotoType.COMPLETED_INSTALLATION -> validateCompleted(bitmap, metadata)
        }
    }
    
    private suspend fun validateONTActive(
        bitmap: Bitmap,
        metadata: PhotoMetadata,
        ocrText: List<TextBlock>
    ): PhotoValidationResult = coroutineScope {
        
        // Detect ONT lights
        val lightsDeferred = async { ontDetector.detectLights(bitmap) }
        
        // Detect equipment
        val equipmentDeferred = async { yoloDetector.detectEquipment(bitmap) }
        
        // Check for drop number in OCR
        val dropNumberVisible = ocrText.any { block ->
            block.text.contains(metadata.dropNumber, ignoreCase = true)
        }
        
        val lights = lightsDeferred.await()
        val equipment = equipmentDeferred.await()
        
        // Validate all requirements
        val issues = mutableListOf<ValidationIssue>()
        
        // Check lights
        if (!lights.powerLight) issues.add(ValidationIssue.POWER_LIGHT_OFF)
        if (!lights.losLight) issues.add(ValidationIssue.LOS_LIGHT_OFF)
        if (!lights.ponLight) issues.add(ValidationIssue.PON_LIGHT_OFF)
        if (!lights.lanLight) issues.add(ValidationIssue.LAN_LIGHT_OFF)
        
        // Check equipment detection
        if (!equipment.any { it.label == "ONT" && it.confidence > 0.7f }) {
            issues.add(ValidationIssue.ONT_NOT_DETECTED)
        }
        
        // Check drop number
        if (!dropNumberVisible) {
            issues.add(ValidationIssue.DROP_NUMBER_NOT_VISIBLE)
        }
        
        return@coroutineScope if (issues.isEmpty()) {
            PhotoValidationResult.Success(
                confidence = minOf(lights.confidence, equipment.maxOf { it.confidence }),
                metadata = mapOf(
                    "lights_detected" to lights.toMap(),
                    "equipment_detected" to equipment.map { it.label },
                    "drop_number_verified" to dropNumberVisible
                )
            )
        } else {
            PhotoValidationResult.Failed(
                issues = issues,
                allowManualOverride = true,
                suggestions = generateSuggestions(issues)
            )
        }
    }
}

// Custom ONT Light Detector
class ONTLightDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private lateinit var interpreter: Interpreter
    private val inputSize = 224
    
    suspend fun initialize() = withContext(Dispatchers.IO) {
        val modelBuffer = loadModelFile("ont_light_detector_v2.tflite")
        val options = Interpreter.Options().apply {
            setNumThreads(4)
            setUseNNAPI(true)
        }
        interpreter = Interpreter(modelBuffer, options)
    }
    
    suspend fun detectLights(bitmap: Bitmap): ONTLightResult = withContext(Dispatchers.Default) {
        // Preprocess image
        val input = preprocessImage(bitmap)
        
        // Prepare output
        val output = Array(1) { FloatArray(8) } // 4 lights × 2 states
        
        // Run inference
        interpreter.run(input, output)
        
        // Parse results
        ONTLightResult(
            powerLight = output[0][0] > output[0][1],
            losLight = output[0][2] > output[0][3],
            ponLight = output[0][4] > output[0][5],
            lanLight = output[0][6] > output[0][7],
            confidence = output[0].max()
        )
    }
    
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val buffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(inputSize * inputSize)
        resized.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        
        // Normalize pixels to [-1, 1]
        for (pixel in pixels) {
            val r = ((pixel shr 16 and 0xFF) - 127.5f) / 127.5f
            val g = ((pixel shr 8 and 0xFF) - 127.5f) / 127.5f
            val b = ((pixel and 0xFF) - 127.5f) / 127.5f
            
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        
        return buffer
    }
}
```

## 5. Offline-First Sync Strategy

### 5.1 Sync Architecture

```kotlin
// Comprehensive Sync Manager

@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager,
    private val syncQueueDao: SyncQueueDao,
    private val networkMonitor: NetworkMonitor,
    private val conflictResolver: ConflictResolver,
    private val syncApi: SyncApi
) {
    
    fun initializeSync() {
        // Schedule periodic sync
        schedulePeriodicSync()
        
        // Monitor network changes
        networkMonitor.networkState.onEach { state ->
            if (state == NetworkState.CONNECTED) {
                triggerImmediateSync()
            }
        }.launchIn(GlobalScope)
    }
    
    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30, TimeUnit.SECONDS
            )
            .addTag("sync")
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "periodic_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
    
    suspend fun performSync(): SyncResult = coroutineScope {
        try {
            // Get pending sync items
            val pendingItems = syncQueueDao.getPendingItems()
            
            if (pendingItems.isEmpty()) {
                return@coroutineScope SyncResult.NoItemsToSync
            }
            
            // Group by entity type for batch processing
            val groupedItems = pendingItems.groupBy { it.entityType }
            
            val results = groupedItems.map { (entityType, items) ->
                async {
                    syncEntityBatch(entityType, items)
                }
            }.awaitAll()
            
            // Aggregate results
            val totalSynced = results.sumOf { it.syncedCount }
            val totalFailed = results.sumOf { it.failedCount }
            val conflicts = results.flatMap { it.conflicts }
            
            // Handle conflicts
            if (conflicts.isNotEmpty()) {
                resolveConflicts(conflicts)
            }
            
            SyncResult.Success(
                syncedCount = totalSynced,
                failedCount = totalFailed,
                conflictsResolved = conflicts.size
            )
            
        } catch (e: Exception) {
            SyncResult.Error(e)
        }
    }
    
    private suspend fun syncEntityBatch(
        entityType: EntityType,
        items: List<SyncQueueEntity>
    ): BatchSyncResult {
        
        val request = when (entityType) {
            EntityType.DROP -> buildDropSyncRequest(items)
            EntityType.INSTALLATION -> buildInstallationSyncRequest(items)
            EntityType.PHOTO -> buildPhotoSyncRequest(items)
            EntityType.REMEDIATION -> buildRemediationSyncRequest(items)
        }
        
        return try {
            val response = syncApi.syncBatch(request)
            
            // Process successful syncs
            response.successful.forEach { id ->
                syncQueueDao.markSynced(id)
            }
            
            // Process failures
            response.failed.forEach { failure ->
                syncQueueDao.incrementRetryCount(failure.id)
                if (failure.retryable) {
                    scheduleRetry(failure.id, failure.retryAfter)
                }
            }
            
            BatchSyncResult(
                syncedCount = response.successful.size,
                failedCount = response.failed.size,
                conflicts = response.conflicts
            )
            
        } catch (e: Exception) {
            // Mark all items for retry
            items.forEach { item ->
                syncQueueDao.incrementRetryCount(item.syncId)
            }
            
            BatchSyncResult(
                syncedCount = 0,
                failedCount = items.size,
                conflicts = emptyList()
            )
        }
    }
    
    private suspend fun resolveConflicts(conflicts: List<SyncConflict>) {
        conflicts.forEach { conflict ->
            val resolution = conflictResolver.resolve(conflict)
            
            when (resolution) {
                ConflictResolution.USE_LOCAL -> {
                    // Re-queue local version
                    queueSync(
                        conflict.entityType,
                        conflict.entityId,
                        SyncOperation.UPDATE
                    )
                }
                ConflictResolution.USE_REMOTE -> {
                    // Update local with remote version
                    applyRemoteChanges(conflict.remoteData)
                }
                ConflictResolution.MERGE -> {
                    // Merge and re-queue
                    val merged = conflictResolver.merge(
                        conflict.localData,
                        conflict.remoteData
                    )
                    applyMergedChanges(merged)
                    queueSync(
                        conflict.entityType,
                        conflict.entityId,
                        SyncOperation.UPDATE
                    )
                }
            }
        }
    }
}

// Conflict Resolution Strategy
class ConflictResolver @Inject constructor(
    private val database: FibreFieldDatabase
) {
    
    fun resolve(conflict: SyncConflict): ConflictResolution {
        return when (conflict.type) {
            ConflictType.DROP_STATUS -> {
                // Server wins for status updates
                ConflictResolution.USE_REMOTE
            }
            ConflictType.PHOTO_VALIDATION -> {
                // Local wins if manually overridden
                if (conflict.localData.get("manual_override") == true) {
                    ConflictResolution.USE_LOCAL
                } else {
                    ConflictResolution.USE_REMOTE
                }
            }
            ConflictType.INSTALLATION_DATA -> {
                // Merge installation data
                ConflictResolution.MERGE
            }
            else -> ConflictResolution.USE_REMOTE
        }
    }
    
    fun merge(local: JsonObject, remote: JsonObject): JsonObject {
        // Implement field-by-field merge logic
        return JsonObject().apply {
            // Use latest timestamp
            val localTime = local.get("updated_at").asLong
            val remoteTime = remote.get("updated_at").asLong
            
            if (localTime > remoteTime) {
                // Local is newer, but incorporate server-only fields
                addAll(local)
                remote.entrySet().forEach { (key, value) ->
                    if (!has(key)) {
                        add(key, value)
                    }
                }
            } else {
                // Remote is newer, but preserve local-only fields
                addAll(remote)
                local.entrySet().forEach { (key, value) ->
                    if (!has(key) && !isServerOnlyField(key)) {
                        add(key, value)
                    }
                }
            }
        }
    }
}
```

## 6. Security Implementation

### 6.1 Multi-Layer Security Architecture

```kotlin
// Comprehensive Security Implementation

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    
    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage {
        return SecureStorageImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideBiometricAuthenticator(
        @ApplicationContext context: Context
    ): BiometricAuthenticator {
        return BiometricAuthenticatorImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideCryptoManager(): CryptoManager {
        return CryptoManagerImpl()
    }
}

// Secure Storage with Android Keystore
class SecureStorageImpl(private val context: Context) : SecureStorage {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .setUserAuthenticationRequired(false)
        .build()
    
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    override suspend fun saveCredentials(credentials: Credentials) {
        encryptedPrefs.edit {
            putString("username", credentials.username)
            putString("password", credentials.password.encrypt())
            putString("token", credentials.token)
            putLong("token_expiry", credentials.tokenExpiry)
            putString("refresh_token", credentials.refreshToken)
        }
    }
    
    override suspend fun getCredentials(): Credentials? {
        return try {
            Credentials(
                username = encryptedPrefs.getString("username", null) ?: return null,
                password = encryptedPrefs.getString("password", null)?.decrypt() ?: return null,
                token = encryptedPrefs.getString("token", null),
                tokenExpiry = encryptedPrefs.getLong("token_expiry", 0L),
                refreshToken = encryptedPrefs.getString("refresh_token", null)
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearCredentials() {
        encryptedPrefs.edit {
            clear()
        }
    }
}

// Biometric Authentication
class BiometricAuthenticatorImpl(
    private val context: Context
) : BiometricAuthenticator {
    
    private val executor = ContextCompat.getMainExecutor(context)
    private val biometricPrompt by lazy {
        BiometricPrompt(
            context as FragmentActivity,
            executor,
            authenticationCallback
        )
    }
    
    private val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Authenticate to FibreField")
        .setSubtitle("Use your biometric credential")
        .setNegativeButtonText("Use password")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()
    
    override suspend fun authenticate(): BiometricResult = suspendCancellableCoroutine { cont ->
        authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                cont.resume(BiometricResult.Success)
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                cont.resume(BiometricResult.Error(errorCode, errString.toString()))
            }
            
            override fun onAuthenticationFailed() {
                cont.resume(BiometricResult.Failed)
            }
        }
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    override fun isAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
}

// Network Security
class NetworkSecurityInterceptor @Inject constructor(
    private val cryptoManager: CryptoManager,
    private val secureStorage: SecureStorage
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Add security headers
        val secureRequest = originalRequest.newBuilder().apply {
            // Add authentication token
            runBlocking {
                secureStorage.getCredentials()?.token?.let { token ->
                    header("Authorization", "Bearer $token")
                }
            }
            
            // Add request signature
            val timestamp = System.currentTimeMillis()
            val nonce = UUID.randomUUID().toString()
            val signature = generateSignature(originalRequest, timestamp, nonce)
            
            header("X-Timestamp", timestamp.toString())
            header("X-Nonce", nonce)
            header("X-Signature", signature)
            
            // Certificate pinning
            if (originalRequest.url.host == "api.fibreflow.com") {
                header("X-Certificate-Pin", BuildConfig.CERTIFICATE_PIN)
            }
        }.build()
        
        return chain.proceed(secureRequest)
    }
    
    private fun generateSignature(
        request: Request,
        timestamp: Long,
        nonce: String
    ): String {
        val data = buildString {
            append(request.method)
            append(request.url.encodedPath)
            append(timestamp)
            append(nonce)
            request.body?.let { body ->
                val buffer = Buffer()
                body.writeTo(buffer)
                append(buffer.readUtf8())
            }
        }
        
        return cryptoManager.hmacSha256(data, BuildConfig.API_SECRET)
    }
}
```

## 7. Performance Optimization

### 7.1 Memory Management Strategy

```kotlin
// Advanced Memory Management

@Singleton
class MemoryManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val performanceMonitor: PerformanceMonitor
) {
    
    private val modelCache = LruCache<ModelType, Any>(3)
    private val bitmapCache = LruCache<String, Bitmap>(10)
    private val memoryThreshold = 100 * 1024 * 1024 // 100MB
    
    fun monitorMemory() {
        // Register for memory warnings
        context.registerComponentCallbacks(object : ComponentCallbacks2 {
            override fun onTrimMemory(level: Int) {
                when (level) {
                    ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                        // App in background
                        trimMemory(MemoryTrimLevel.MODERATE)
                    }
                    ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> {
                        // Memory low while running
                        trimMemory(MemoryTrimLevel.AGGRESSIVE)
                    }
                    ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                        // Critical memory
                        trimMemory(MemoryTrimLevel.CRITICAL)
                    }
                }
            }
            
            override fun onConfigurationChanged(newConfig: Configuration) {}
            override fun onLowMemory() {
                trimMemory(MemoryTrimLevel.CRITICAL)
            }
        })
    }
    
    private fun trimMemory(level: MemoryTrimLevel) {
        when (level) {
            MemoryTrimLevel.MODERATE -> {
                // Clear bitmap cache
                bitmapCache.evictAll()
            }
            MemoryTrimLevel.AGGRESSIVE -> {
                // Clear bitmap cache and reduce model cache
                bitmapCache.evictAll()
                modelCache.resize(1)
            }
            MemoryTrimLevel.CRITICAL -> {
                // Clear all caches except essential
                bitmapCache.evictAll()
                modelCache.evictAll()
                System.gc()
            }
        }
    }
    
    suspend fun <T> loadModel(
        type: ModelType,
        loader: suspend () -> T
    ): T? {
        // Check available memory
        val availableMemory = getAvailableMemory()
        val requiredMemory = getModelMemoryRequirement(type)
        
        if (availableMemory < requiredMemory + memoryThreshold) {
            // Try to free memory
            freeMemory(requiredMemory)
            
            // Re-check
            if (getAvailableMemory() < requiredMemory + memoryThreshold) {
                performanceMonitor.logMemoryPressure(type)
                return null
            }
        }
        
        // Load from cache or create new
        @Suppress("UNCHECKED_CAST")
        return modelCache.get(type) as? T ?: loader().also {
            modelCache.put(type, it as Any)
        }
    }
    
    private fun getAvailableMemory(): Long {
        val runtime = Runtime.getRuntime()
        val used = runtime.totalMemory() - runtime.freeMemory()
        return runtime.maxMemory() - used
    }
    
    private fun getModelMemoryRequirement(type: ModelType): Long {
        return when (type) {
            ModelType.LLM -> 2L * 1024 * 1024 * 1024 // 2GB
            ModelType.ONT_DETECTOR -> 30L * 1024 * 1024 // 30MB
            ModelType.YOLO -> 20L * 1024 * 1024 // 20MB
            ModelType.QUALITY -> 15L * 1024 * 1024 // 15MB
        }
    }
}

// Battery-Aware Processing
@Singleton
class BatteryAwareProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val batteryManager: BatteryManager
) {
    
    enum class PowerProfile {
        HIGH_PERFORMANCE,
        BALANCED,
        POWER_SAVING
    }
    
    private var currentProfile = PowerProfile.BALANCED
    
    fun initialize() {
        // Monitor battery state
        val batteryStatusFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, batteryStatusFilter)
    }
    
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level * 100 / scale.toFloat()
            
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                           status == BatteryManager.BATTERY_STATUS_FULL
            
            updatePowerProfile(batteryPct, isCharging)
        }
    }
    
    private fun updatePowerProfile(batteryLevel: Float, isCharging: Boolean) {
        currentProfile = when {
            isCharging -> PowerProfile.HIGH_PERFORMANCE
            batteryLevel > 50 -> PowerProfile.BALANCED
            else -> PowerProfile.POWER_SAVING
        }
        
        applyPowerProfile()
    }
    
    private fun applyPowerProfile() {
        when (currentProfile) {
            PowerProfile.HIGH_PERFORMANCE -> {
                // Maximum performance
                ProcessingConfig.apply {
                    inferenceThreads = 4
                    useGPU = true
                    photoValidationMode = ValidationMode.FULL
                    syncFrequency = 5 // minutes
                }
            }
            PowerProfile.BALANCED -> {
                // Balanced performance
                ProcessingConfig.apply {
                    inferenceThreads = 2
                    useGPU = false
                    photoValidationMode = ValidationMode.ESSENTIAL
                    syncFrequency = 15 // minutes
                }
            }
            PowerProfile.POWER_SAVING -> {
                // Minimum power usage
                ProcessingConfig.apply {
                    inferenceThreads = 1
                    useGPU = false
                    photoValidationMode = ValidationMode.MANUAL
                    syncFrequency = 30 // minutes
                }
            }
        }
    }
}
```

## 8. Component Interfaces and Contracts

### 8.1 Core Domain Interfaces

```kotlin
// Domain Layer Contracts

// Use Case Interfaces
interface UseCase<in P, out R> {
    suspend operator fun invoke(params: P): Result<R>
}

interface FlowUseCase<in P, out R> {
    operator fun invoke(params: P): Flow<R>
}

// Repository Interfaces
interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthToken>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(refreshToken: String): Result<AuthToken>
    suspend fun validateToken(token: String): Result<Boolean>
    suspend fun getBiometricCredentials(): Result<Credentials>
}

interface InstallationRepository {
    suspend fun createInstallation(drop: Drop): Result<Installation>
    suspend fun updateInstallation(installation: Installation): Result<Unit>
    suspend fun getInstallation(installationId: Long): Result<Installation>
    suspend fun submitInstallation(installationId: Long): Result<Unit>
    fun getActiveInstallations(): Flow<List<Installation>>
    suspend fun syncInstallations(): Result<SyncReport>
}

interface PhotoRepository {
    suspend fun savePhoto(photo: Photo): Result<Long>
    suspend fun validatePhoto(photoId: Long): Result<ValidationResult>
    suspend fun getPhotosForInstallation(installationId: Long): Result<List<Photo>>
    suspend fun uploadPhoto(photoId: Long): Result<String>
    suspend fun deletePhoto(photoId: Long): Result<Unit>
}

// Service Interfaces
interface LocationService {
    fun getCurrentLocation(): Flow<Location>
    suspend fun getLastKnownLocation(): Location?
    suspend fun calculateDistance(from: Location, to: Location): Float
    suspend fun isWithinBoundary(location: Location, boundary: Polygon): Boolean
}

interface ValidationService {
    suspend fun validateDrop(dropNumber: String, location: Location): ValidationResult
    suspend fun validatePhoto(bitmap: Bitmap, type: PhotoType): ValidationResult
    suspend fun validateInstallation(installation: Installation): ValidationResult
}

// AI Service Interfaces
interface LLMService {
    suspend fun initialize(): Result<Unit>
    suspend fun getGuidance(context: GuidanceContext): Result<String>
    suspend fun processVoiceCommand(audioData: ByteArray): Result<Command>
    fun speak(text: String)
    fun release()
}

interface VisionService {
    suspend fun analyzePhoto(bitmap: Bitmap, analysisType: AnalysisType): Result<AnalysisResult>
    suspend fun detectONTLights(bitmap: Bitmap): Result<ONTLightStatus>
    suspend fun detectEquipment(bitmap: Bitmap): Result<List<Equipment>>
    suspend fun assessQuality(bitmap: Bitmap): Result<QualityAssessment>
}
```

### 8.2 Data Transfer Objects

```kotlin
// Data Transfer Objects and Models

// Domain Models
data class Drop(
    val dropNumber: String,
    val projectId: Int,
    val location: Location,
    val address: String,
    val status: DropStatus,
    val customerName: String?,
    val assignedTechnician: String?,
    val metadata: Map<String, Any> = emptyMap()
)

data class Installation(
    val id: Long = 0,
    val dropNumber: String,
    val technicianId: String,
    val startTime: Instant,
    val endTime: Instant? = null,
    val status: InstallationStatus,
    val photos: List<Photo> = emptyList(),
    val ontSerial: String? = null,
    val routerSerial: String? = null,
    val speedTestResults: SpeedTestResult? = null,
    val customerSignature: String? = null,
    val notes: String? = null
)

data class Photo(
    val id: Long = 0,
    val installationId: Long,
    val type: PhotoType,
    val filePath: String,
    val thumbnailPath: String? = null,
    val captureTime: Instant,
    val location: Location? = null,
    val validationResult: ValidationResult? = null,
    val metadata: PhotoMetadata
)

// Enums
enum class DropStatus {
    AVAILABLE,
    IN_PROGRESS,
    PENDING_ACTIVATION,
    ACTIVATED,
    FAILED,
    REMEDIATION_REQUIRED
}

enum class PhotoType {
    CABLE_SPAN,
    HOME_ENTRY,
    ONT_BARCODE,
    ONT_ACTIVE,
    ROUTER_CONNECTED,
    SPEED_TEST,
    CLEANUP,
    CUSTOMER_SIGNATURE,
    COMPLETED_INSTALLATION
}

enum class ValidationStatus {
    PENDING,
    VALIDATING,
    PASSED,
    FAILED,
    MANUALLY_OVERRIDDEN
}

// Result Types
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Warning(val message: String) : ValidationResult()
    data class Error(val message: String, val canOverride: Boolean = false) : ValidationResult()
    data class Failed(
        val issues: List<ValidationIssue>,
        val allowManualOverride: Boolean = true
    ) : ValidationResult()
}

sealed class SyncResult {
    object NoItemsToSync : SyncResult()
    data class Success(
        val syncedCount: Int,
        val failedCount: Int,
        val conflictsResolved: Int
    ) : SyncResult()
    data class PartialSuccess(
        val syncedCount: Int,
        val failedCount: Int,
        val pendingCount: Int
    ) : SyncResult()
    data class Error(val exception: Exception) : SyncResult()
}
```

## 9. Build Configuration

### 9.1 Gradle Configuration

```kotlin
// app/build.gradle.kts
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.fibreflow.technician"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.fibreflow.technician"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        vectorDrawables {
            useSupportLibrary = true
        }
        
        buildConfigField("String", "API_BASE_URL", "\"https://api.fibreflow.com/v1/\"")
        buildConfigField("String", "CERTIFICATE_PIN", "\"sha256/XXXXXXXXXX\"")
    }
    
    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("String", "API_BASE_URL", "\"https://dev-api.fibreflow.com/v1/\"")
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    
    // ML/AI
    implementation("ai.mlc:mlc-llm-android:0.15.0")
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    
    // Maps
    implementation("org.osmdroid:osmdroid-android:6.1.17")
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.biometric:biometric:1.1.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

## 10. Testing Strategy

### 10.1 Test Architecture

```kotlin
// Unit Test Examples
class InstallationWorkflowEngineTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val coroutineRule = MainCoroutineRule()
    
    private lateinit var engine: InstallationWorkflowEngine
    private val mockDropRepository = mockk<DropRepository>()
    private val mockPhotoValidator = mockk<PhotoValidator>()
    private val mockLLMService = mockk<LLMService>()
    
    @Before
    fun setup() {
        engine = InstallationWorkflowEngine(
            mockDropRepository,
            mockPhotoValidator,
            mockLLMService
        )
    }
    
    @Test
    fun `start installation validates drop and transitions to photo capture`() = runTest {
        // Given
        val dropNumber = "DROP001"
        val drop = Drop(
            dropNumber = dropNumber,
            status = DropStatus.AVAILABLE,
            location = Location(-33.9249, 18.4241),
            address = "123 Test St"
        )
        
        coEvery { mockDropRepository.validateDrop(dropNumber, any()) } returns 
            ValidationResult.Success
        coEvery { mockDropRepository.getDropByNumber(dropNumber) } returns drop
        coEvery { mockLLMService.speak(any()) } just Runs
        
        // When
        engine.startInstallation(dropNumber)
        
        // Then
        val state = engine.state.value
        assertTrue(state is InstallationState.PhotoCapture)
        assertEquals(1, (state as InstallationState.PhotoCapture).step)
        coVerify { mockLLMService.speak(any()) }
    }
}

// Integration Test
@MediumTest
@HiltAndroidTest
class PhotoValidationIntegrationTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var visionPipeline: VisionValidationPipeline
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun validateONTPhotoWithAllLightsOn() = runTest {
        // Load test image
        val bitmap = loadTestBitmap("ont_all_lights_on.jpg")
        val metadata = PhotoMetadata(
            dropNumber = "DROP001",
            timestamp = Instant.now()
        )
        
        // Validate
        val result = visionPipeline.validatePhoto(
            bitmap,
            PhotoType.ONT_ACTIVE,
            metadata
        )
        
        // Assert
        assertTrue(result is PhotoValidationResult.Success)
        assertTrue(result.confidence > 0.9f)
    }
}

// UI Test
@LargeTest
@HiltAndroidTest
class InstallationFlowE2ETest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun completeInstallationFlow() {
        // Login
        composeTestRule.onNodeWithTag("username_field")
            .performTextInput("technician1")
        composeTestRule.onNodeWithTag("password_field")
            .performTextInput("password123")
        composeTestRule.onNodeWithTag("login_button")
            .performClick()
        
        // Select drop
        composeTestRule.onNodeWithText("DROP001")
            .performClick()
        
        // Start installation
        composeTestRule.onNodeWithTag("start_installation")
            .performClick()
        
        // Capture photos (simplified)
        repeat(9) {
            composeTestRule.onNodeWithTag("capture_photo")
                .performClick()
            composeTestRule.onNodeWithTag("confirm_photo")
                .performClick()
        }
        
        // Submit
        composeTestRule.onNodeWithTag("submit_installation")
            .performClick()
        
        // Verify completion
        composeTestRule.onNodeWithText("Installation Completed")
            .assertIsDisplayed()
    }
}
```

## Conclusion

This comprehensive technical architecture provides a complete blueprint for building the FibreField Technician Android application with:

1. **Clean Architecture**: Clear separation of concerns with well-defined layers
2. **Modular Design**: Feature-based modules for scalability and maintainability
3. **Offline-First**: Robust sync strategy with conflict resolution
4. **AI/ML Integration**: Phi-3.5 Mini LLM with computer vision pipeline
5. **Security**: Multi-layer security with encryption and biometric auth
6. **Performance**: Memory management and battery-aware processing
7. **Testing**: Comprehensive testing strategy with unit, integration, and UI tests

The architecture is designed to be:
- **Scalable**: Can grow with new features
- **Maintainable**: Clear structure and separation
- **Testable**: >90% test coverage achievable
- **Performant**: Optimized for mobile constraints
- **Secure**: Defense in depth approach

This architecture serves as the foundation for building a production-ready Android application that meets all requirements specified in the PRD.