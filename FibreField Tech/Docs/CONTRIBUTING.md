# Contributing to FibreField Technician

Welcome to the FibreField Technician project! We're excited to have you contribute to building the next-generation AI-powered fiber optic installation assistant. This guide will help you get started and ensure your contributions align with our project standards.

## 🌟 Ways to Contribute

- **🐛 Bug Reports**: Report issues and help us improve quality
- **✨ Feature Requests**: Suggest new features and enhancements
- **💻 Code Contributions**: Implement features, fix bugs, improve performance
- **📚 Documentation**: Improve guides, add examples, fix typos
- **🧪 Testing**: Write tests, perform quality assurance, field testing
- **🎨 Design**: UI/UX improvements, accessibility enhancements
- **🤖 AI/ML**: Model improvements, training data, accuracy enhancements

## 🚀 Getting Started

### Prerequisites

Before contributing, ensure you have:

- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **JDK**: Java 17 or higher
- **Git**: Latest version
- **Android SDK**: API 24+ (Android 7.0)
- **Device/Emulator**: For testing your changes

### Development Setup

1. **Fork the Repository**
```bash
# Fork on GitHub, then clone your fork
git clone https://github.com/YOUR_USERNAME/fibrefield-technician.git
cd fibrefield-technician
```

2. **Set Up Development Environment**
```bash
# Copy environment template
cp .env.example .env

# Install Git hooks
./scripts/install-git-hooks.sh

# Build the project
./gradlew build
```

3. **Run the Application**
```bash
# Install debug build
./gradlew installDebug

# Run tests to ensure everything works
./gradlew test
```

## 📋 Development Workflow

### Branch Strategy

We use **Git Flow** with the following branches:

```
main        ← Production-ready code
  ↑
develop     ← Integration branch for features  
  ↑
feature/*   ← Individual feature development
release/*   ← Release preparation
hotfix/*    ← Emergency production fixes
```

### Creating a Feature Branch

```bash
# Start from develop
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feature/your-feature-name

# Push branch to origin
git push -u origin feature/your-feature-name
```

### Branch Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| **Feature** | `feature/description` | `feature/ai-photo-validation` |
| **Bug Fix** | `bugfix/description` | `bugfix/crash-on-photo-capture` |
| **Hotfix** | `hotfix/description` | `hotfix/critical-sync-issue` |
| **Documentation** | `docs/description` | `docs/update-api-guide` |
| **Performance** | `perf/description` | `perf/optimize-model-loading` |
| **Refactor** | `refactor/description` | `refactor/repository-layer` |

## 💻 Code Standards

### Kotlin Coding Style

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with these additions:

#### File Organization

```kotlin
// 1. Package declaration
package com.fibreflow.technician.feature.installation

// 2. Imports (grouped and alphabetically sorted)
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.fibreflow.technician.core.common.Result
import com.fibreflow.technician.domain.installation.Installation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// 3. Top-level declarations
private const val MAX_RETRIES = 3

// 4. Class declaration
@AndroidEntryPoint
class InstallationFragment : Fragment() {
    // Implementation
}
```

#### Naming Conventions

```kotlin
// Classes: PascalCase
class InstallationRepository
class PhotoValidationResult

// Functions: camelCase with descriptive verbs
fun validatePhoto()
fun startInstallation()
fun updateDropStatus()

// Variables: camelCase
val dropNumber = "DROP001"
val isValidationComplete = true
val photoValidationResults = listOf<ValidationResult>()

// Constants: UPPER_SNAKE_CASE
const val MAX_PHOTO_SIZE = 10_000_000
const val DEFAULT_TIMEOUT_MS = 30_000

// Compose functions: PascalCase
@Composable
fun InstallationWorkflowScreen()

@Composable  
fun PhotoCaptureButton()
```

#### Documentation Standards

```kotlin
/**
 * Validates installation photos using AI models and quality assessment.
 *
 * This function performs comprehensive photo validation including:
 * - Image quality analysis (brightness, sharpness, resolution)
 * - Equipment detection using computer vision models  
 * - ONT light status validation for activation photos
 * - Text recognition for barcode and serial number extraction
 *
 * @param bitmap The photo bitmap to validate
 * @param photoType The type of installation photo (ONT_ACTIVE, CABLE_SPAN, etc.)
 * @param metadata Additional context like drop number and capture location
 * @return ValidationResult with success/failure status and detailed feedback
 *
 * @throws ModelNotLoadedException if required AI models are not available
 * @throws InvalidPhotoTypeException if photoType is not supported
 *
 * @sample
 * ```kotlin
 * val result = visionPipeline.validatePhoto(
 *     bitmap = capturedBitmap,
 *     photoType = PhotoType.ONT_ACTIVE,
 *     metadata = PhotoMetadata(dropNumber = "DROP001")
 * )
 * 
 * when (result) {
 *     is ValidationResult.Success -> showSuccess(result.confidence)
 *     is ValidationResult.Failed -> showError(result.issues)
 * }
 * ```
 */
suspend fun validatePhoto(
    bitmap: Bitmap,
    photoType: PhotoType,
    metadata: PhotoMetadata
): ValidationResult
```

### Architecture Guidelines

#### Clean Architecture Structure

```
app/
├── presentation/     # UI Layer (Activities, Fragments, Compose)
│   ├── ui/
│   ├── viewmodel/
│   └── navigation/
├── domain/          # Business Logic Layer
│   ├── entities/
│   ├── usecases/
│   └── repositories/
└── data/           # Data Layer
    ├── local/      # Room database, preferences
    ├── remote/     # API clients, networking
    └── repositories/ # Repository implementations
```

#### Dependency Injection with Hilt

```kotlin
// Module definition
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideDropRepository(
        dropDao: DropDao,
        dropApi: DropApi,
        syncManager: SyncManager
    ): DropRepository = DropRepositoryImpl(dropDao, dropApi, syncManager)
}

// Injection in classes
@AndroidEntryPoint
class InstallationFragment : Fragment() {
    
    @Inject
    lateinit var installationRepository: InstallationRepository
}

// ViewModel injection
@HiltViewModel
class InstallationViewModel @Inject constructor(
    private val installationRepository: InstallationRepository,
    private val visionService: VisionService
) : ViewModel()
```

#### Error Handling

```kotlin
// Use sealed classes for results
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Loading(val message: String = "") : Result<Nothing>()
}

// Repository error handling
class DropRepositoryImpl : DropRepository {
    
    override suspend fun getDropByNumber(dropNumber: String): Result<Drop> {
        return try {
            val drop = dropDao.getDropByNumber(dropNumber)
                ?: return Result.Error(DropNotFoundException(dropNumber))
            Result.Success(drop.toDomainModel())
        } catch (e: Exception) {
            Log.e("DropRepository", "Failed to get drop: $dropNumber", e)
            Result.Error(e)
        }
    }
}

// UI error handling
@Composable
fun InstallationScreen(viewModel: InstallationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val result = uiState.dropResult) {
        is Result.Loading -> LoadingIndicator(result.message)
        is Result.Success -> DropContent(result.data)
        is Result.Error -> ErrorMessage(
            message = result.exception.localizedMessage ?: "Unknown error",
            onRetry = { viewModel.retryLoadDrop() }
        )
    }
}
```

### Testing Requirements

#### Test Coverage Targets

- **Unit Tests**: >95% coverage for business logic
- **Integration Tests**: All repository and use case interactions
- **UI Tests**: Critical user flows and accessibility
- **End-to-End**: Complete installation workflows

#### Test Structure

```kotlin
class InstallationViewModelTest {
    
    // Arrange: Test setup
    @Before
    fun setup() {
        // Initialize mocks, test data
    }
    
    // Act & Assert: Test execution
    @Test
    fun `startInstallation success updates state correctly`() = runTest {
        // Given
        val dropNumber = "DROP001"
        coEvery { mockRepository.startInstallation(dropNumber) } returns Result.Success(testInstallation)
        
        // When
        viewModel.startInstallation(dropNumber)
        
        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.installation).isEqualTo(testInstallation)
            assertThat(state.isLoading).isFalse()
        }
    }
}
```

## 🎯 Pull Request Process

### Before Creating a PR

1. **✅ Code Quality Checks**
```bash
# Run all tests
./gradlew test

# Run lint checks  
./gradlew lint

# Check code coverage
./gradlew jacocoTestReport

# Format code
./gradlew ktlintFormat
```

2. **✅ Feature Validation**
- [ ] Feature works as designed
- [ ] No regressions in existing functionality
- [ ] Performance impact assessed
- [ ] Accessibility considered
- [ ] Documentation updated

### Creating a Pull Request

1. **Push your branch**
```bash
git push origin feature/your-feature-name
```

2. **Create PR on GitHub**
- Use our PR template (auto-populated)
- Target the `develop` branch
- Add descriptive title and detailed description
- Link related issues
- Add appropriate labels

### PR Template

```markdown
## Description
Brief description of changes and motivation.

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)  
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Refactoring

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed
- [ ] AI model accuracy validated (if applicable)

## Screenshots/Videos
Add screenshots or videos demonstrating the changes.

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No breaking changes or migration guide provided
```

### Review Process

1. **Automated Checks**: CI/CD pipeline runs automatically
2. **Code Review**: At least 2 reviewers required
3. **Testing**: QA team validates critical changes
4. **Approval**: Technical lead approval for architectural changes
5. **Merge**: Squash and merge after all checks pass

### Review Criteria

#### Code Quality
- [ ] **Readability**: Code is clear and self-documenting
- [ ] **Maintainability**: Easy to modify and extend
- [ ] **Performance**: No unnecessary overhead introduced
- [ ] **Security**: No security vulnerabilities
- [ ] **Testing**: Adequate test coverage

#### Architecture
- [ ] **Consistency**: Follows established patterns
- [ ] **Separation of Concerns**: Proper layer separation
- [ ] **SOLID Principles**: Well-designed interfaces
- [ ] **Error Handling**: Comprehensive error scenarios
- [ ] **Memory Management**: No leaks or excessive usage

## 🐛 Bug Reports

### Before Reporting a Bug

1. **Search existing issues** to avoid duplicates
2. **Test on latest version** to ensure issue persists
3. **Gather reproduction steps** and supporting evidence
4. **Check logs** for relevant error messages

### Bug Report Template

```markdown
## Bug Description
Clear and concise description of the bug.

## Steps to Reproduce
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior
What you expected to happen.

## Actual Behavior
What actually happened.

## Environment
- **Device**: Samsung Galaxy S23
- **Android Version**: Android 13
- **App Version**: 1.0.0 (Build 100)
- **Network**: WiFi/4G/Offline

## Screenshots/Videos
Add any visual evidence.

## Logs
```
Relevant log entries (remove sensitive data)
```

## Additional Context
Any other context about the problem.
```

## ✨ Feature Requests

### Feature Request Template

```markdown
## Feature Summary
Brief description of the feature.

## Problem Statement
What problem does this feature solve?

## Proposed Solution
Detailed description of the proposed feature.

## Alternative Solutions
Other approaches considered.

## User Impact
Who benefits and how?

## Technical Considerations
- Implementation complexity
- Performance implications
- Compatibility requirements
- AI/ML model changes needed

## Mockups/Wireframes
Visual representation if applicable.
```

## 🤖 AI/ML Contributions

### Model Improvements

We welcome contributions to improve our AI models:

#### Training Data Contributions
- **Photo datasets**: Quality installation photos with labels
- **Validation scenarios**: Edge cases and challenging conditions  
- **Performance data**: Real-world accuracy measurements

#### Model Enhancements
- **Accuracy improvements**: Better detection algorithms
- **Performance optimization**: Faster inference, smaller models
- **New capabilities**: Additional equipment detection, quality metrics

#### AI Testing
- **Benchmark datasets**: Standardized test cases
- **Accuracy validation**: Real-world testing results
- **Performance profiling**: Memory usage, inference time

### AI Development Guidelines

```kotlin
// Model interface consistency
interface VisionModel {
    suspend fun initialize(): Result<Unit>
    suspend fun process(input: Bitmap): Result<ModelOutput>
    fun release()
    fun getModelInfo(): ModelInfo
}

// Performance monitoring
class ModelPerformanceTracker {
    fun recordInference(modelName: String, timeMs: Long, accuracy: Float) {
        // Track performance metrics
    }
}

// Testing requirements
@Test
fun `model accuracy meets minimum threshold`() {
    val testDataset = loadTestDataset()
    val accuracyResults = testDataset.map { testCase ->
        model.process(testCase.input).accuracy
    }
    
    val averageAccuracy = accuracyResults.average()
    assertThat(averageAccuracy).isGreaterThan(0.95f) // 95% minimum
}
```

## 🎨 UI/UX Contributions

### Design System

We use Material Design 3 with custom components:

```kotlin
// Theme structure
@Composable
fun FibreFieldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FibreFieldTypography,
        content = content
    )
}

// Custom components
@Composable
fun FibreFieldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ButtonStyle = ButtonStyle.Primary
) {
    // Implementation
}
```

### Accessibility Guidelines

- **Content descriptions**: All interactive elements
- **Focus management**: Logical navigation order
- **Color contrast**: WCAG AA compliance
- **Text scaling**: Support dynamic text sizes
- **Voice guidance**: TalkBack compatibility

## 📚 Documentation Contributions

### Documentation Types

| Type | Location | Purpose |
|------|----------|---------|
| **API Docs** | `/docs/api/` | Backend API reference |
| **Architecture** | `/docs/architecture/` | Technical design docs |
| **User Guides** | `/docs/user/` | End-user documentation |
| **Developer Guides** | `/docs/developer/` | Development setup and guidelines |
| **Troubleshooting** | `/docs/troubleshooting/` | Common issues and solutions |

### Documentation Standards

- **Clear headings**: Use descriptive, hierarchical headings
- **Code examples**: Include working, tested code samples
- **Screenshots**: Visual aids for complex procedures
- **Links**: Reference related documentation
- **Updates**: Keep synchronized with code changes

## 🔄 Release Process

### Version Strategy

We use [Semantic Versioning](https://semver.org/):

- **MAJOR**: Breaking changes requiring migration
- **MINOR**: New features, backwards compatible
- **PATCH**: Bug fixes, no new features

### Release Schedule

- **Major releases**: Quarterly (every 3 months)
- **Minor releases**: Monthly 
- **Patch releases**: As needed for critical fixes

### Release Checklist

#### Pre-Release
- [ ] All features complete and tested
- [ ] Documentation updated
- [ ] Performance benchmarks passed
- [ ] Security review completed
- [ ] AI model accuracy validated

#### Release
- [ ] Version number updated
- [ ] Release notes prepared
- [ ] Builds created and signed
- [ ] App stores updated
- [ ] Monitoring alerts configured

#### Post-Release
- [ ] Metrics monitored
- [ ] User feedback collected
- [ ] Issues triaged and prioritized
- [ ] Next release planning

## 🏆 Recognition

### Contributor Recognition

We recognize contributors through:

- **GitHub contributions**: Commit history and PR stats
- **Hall of Fame**: Top contributors highlighted in README
- **Release notes**: Notable contributions mentioned
- **Swag**: FibreFlow merchandise for significant contributions
- **Job opportunities**: Exceptional contributors may be invited to join the team

### Contribution Levels

| Level | Criteria | Benefits |
|-------|----------|----------|
| **Contributor** | 1+ merged PR | GitHub badge, thanks in release notes |
| **Regular Contributor** | 5+ PRs or major feature | FibreFlow sticker pack |
| **Core Contributor** | 10+ PRs, ongoing participation | T-shirt, early access to features |
| **Maintainer** | Long-term commitment, technical leadership | Hoodie, conference opportunities |

## 📞 Getting Help

### Communication Channels

| Channel | Purpose | Response Time |
|---------|---------|---------------|
| **GitHub Issues** | Bug reports, feature requests | 2-3 business days |
| **GitHub Discussions** | General questions, ideas | 1-2 business days |
| **Discord** | Real-time chat, collaboration | Minutes to hours |
| **Email** | Private matters, security issues | 24 hours |

### Support Contacts

- **Technical Questions**: dev-team@fibreflow.com
- **Security Issues**: security@fibreflow.com (GPG key available)
- **Partnership Inquiries**: partnerships@fibreflow.com
- **General Support**: support@fibreflow.com

### Office Hours

Our core team holds virtual office hours:
- **Time**: Every Friday 2-4 PM UTC
- **Platform**: Discord voice channel
- **Topics**: Architecture discussions, contribution guidance, Q&A

## 📖 Resources

### Learning Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Android Developer Guides](https://developer.android.com/guide)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Clean Architecture Guide](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [TensorFlow Lite Android](https://www.tensorflow.org/lite/android)

### Tools & Setup

- **IDE**: Android Studio with Kotlin plugin
- **Version Control**: Git with conventional commits
- **CI/CD**: GitHub Actions
- **Testing**: JUnit, Espresso, Compose Testing
- **Static Analysis**: Detekt, ktlint

### Code Examples

Check our [examples repository](https://github.com/fibreflow/examples) for:
- Sample implementations
- Best practices demonstrations  
- Common patterns and solutions
- Testing examples

## 🤝 Community Guidelines

### Code of Conduct

We are committed to providing a welcoming and inclusive experience for everyone. Please read our [Code of Conduct](CODE_OF_CONDUCT.md).

### Key Principles

- **Be respectful** and professional in all interactions
- **Be inclusive** and welcome contributors from all backgrounds
- **Be collaborative** and help others learn and grow
- **Be constructive** when providing feedback
- **Be patient** with new contributors

### Enforcement

Community guidelines are enforced by our moderation team. Violations may result in:
1. **Warning**: For minor violations
2. **Temporary suspension**: For repeat violations  
3. **Permanent ban**: For severe violations

Report violations to community@fibreflow.com.

---

## 🎉 Thank You!

Thank you for contributing to FibreField Technician! Your contributions help technicians worldwide provide better fiber optic installations with the power of AI assistance. Every contribution, no matter how small, makes a difference.

**Happy coding!** 🚀

---

## 📄 Legal

By contributing to this project, you agree to:

1. **License your contributions** under the same license as the project
2. **Grant necessary rights** for your contributions to be used
3. **Confirm you have authority** to make the contribution
4. **Follow all applicable laws** and regulations

See [LICENSE](LICENSE) for full legal terms.

---

**Contributing Guide Version**: 1.0  
**Last Updated**: March 2024  
**Next Review**: June 2024