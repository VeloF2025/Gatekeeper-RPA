# FibreField Tech Android - Archon Agents

## Overview

This directory contains project-specific AI agents specialized for the FibreField Tech Android application. These agents are tailored to the project's complex technology stack including AI/ML integration, computer vision, and offline-first architecture.

## Project Complexity: 9/10

- **Fiber optic installation technician app**
- **AI/ML capabilities with Phi-3.5 Mini LLM**
- **Computer vision for ONT light detection**
- **9-step photo capture workflow**
- **Offline-first architecture**
- **High-tech futuristic UI**

## Available Specialized Agents

### 1. Android Compose + AI Specialist
- **Command**: `@android-compose-ai`
- **Expertise**: Jetpack Compose UI with AI/ML integration
- **Use for**: UI components, AI-powered features, animations

### 2. ML & Computer Vision Architect
- **Command**: `@ml-cv-architect`
- **Expertise**: TensorFlow Lite, ONT detection, model optimization
- **Use for**: ML models, computer vision, AI inference

### 3. Offline-First Database Expert
- **Command**: `@offline-db-expert`
- **Expertise**: Room + SQLCipher, data synchronization
- **Use for**: Database design, offline features, sync logic

### 4. Camera Workflow Engineer
- **Command**: `@camera-workflow`
- **Expertise**: 9-step photo capture, CameraX integration
- **Use for**: Camera features, photo workflows, image processing

### 5. Material You High-Tech UI Designer
- **Command**: `@high-tech-ui`
- **Expertise**: Futuristic UI, Material You, accessibility
- **Use for**: Design system, UI components, theming

### 6. Android Performance Optimization Expert
- **Command**: `@android-perf`
- **Expertise**: Memory, battery, startup optimization
- **Use for**: Performance issues, memory leaks, battery drain

### 7. Biometric Security Architect
- **Command**: `@bio-security`
- **Expertise**: Authentication, encryption, security
- **Use for**: Security features, biometric auth, data protection

### 8. Android Testing Specialist
- **Command**: `@android-test`
- **Expertise**: Comprehensive testing strategies
- **Use for**: Test coverage, UI tests, performance testing

## Usage

### Basic Activation
```bash
@Archon <task>                    # Activate with auto-selected agent
@android-compose-ai <task>         # Activate specific agent
@ml-cv-architect <task>           # Activate ML specialist
```

### Agent Recommendation
```bash
@Archon recommend <task>           # Get recommended agent
```

### Examples
```bash
@android-compose-ai Create ONT detection UI component with real-time AI feedback
@ml-cv-architect Optimize Phi-3.5 model for ONT light detection under 100ms
@camera-workflow Implement 9-step photo capture with geotagging
@high-tech-ui Design futuristic dashboard with Material You dynamic colors
@android-perf Optimize app startup time to under 1.5 seconds
@bio-security Implement biometric authentication with fallback
```

## Quality Gates

All agents must enforce:
- **Zero Android compilation errors**
- **95%+ test coverage**
- **Zero lint warnings**
- **Memory usage <500MB peak**
- **Battery impact <15%/hour**
- **ML inference <100ms**
- **App startup <1.5s**

## Validation System

The project includes a validation system (`agent_validation.py`) that:

1. **Validates agent compatibility** with tasks
2. **Checks quality gates** before development
3. **Enforces project-specific rules**
4. **Blocks agents** with violations

### Running Validation
```bash
python .archon/agent_validation.py <agent> <task>
```

## Technology Stack

### Core
- Android SDK (API 24-35)
- Kotlin + Coroutines
- Jetpack Compose
- Material Design 3

### Architecture
- Clean Architecture
- MVVM pattern
- Hilt DI
- Room + SQLCipher

### AI/ML
- TensorFlow Lite
- Phi-3.5 Mini LLM (3.8B)
- ML Kit
- ONNX Runtime

### Features
- CameraX
- WorkManager
- Biometric Auth
- Offline Sync
- Firebase

## Project Structure

```
FibreFieldTech-Android/
├── app/                    # Main application
├── core/                   # Core modules
│   ├── common/            # Shared utilities
│   ├── database/          # Room database
│   ├── network/           # Network layer
│   ├── ai/                # AI/ML components
│   └── design/            # Design system
├── domain/                # Domain modules
│   ├── authentication/    # Auth domain
│   ├── installation/      # Installation logic
│   ├── drops/             # Drops feature
│   ├── activation/        # Activation logic
│   └── remediation/       # Remediation logic
├── feature/               # Feature modules
│   ├── authentication/    # Auth feature
│   ├── installation/      # Installation feature
│   ├── drops/             # Drops feature
│   ├── activation/        # Activation feature
│   └── remediation/       # Remediation feature
├── infrastructure/        # Infrastructure
│   ├── sync/              # Data sync
│   ├── location/          # Location services
│   └── security/          # Security
└── .archon/               # AI agent configuration
```

## Agent Guidelines

1. **Always validate** before starting development
2. **Follow Android architecture** best practices
3. **Prioritize offline-first** design
4. **Optimize for field conditions** (variable connectivity)
5. **Ensure AI features** are optional and user-controlled
6. **Implement proper error handling** for all operations
7. **Test thoroughly** on various device configurations

## Support

For issues or questions about the agent system:
1. Check the validation logs
2. Review agent configuration
3. Contact the system architect