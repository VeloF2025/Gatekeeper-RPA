plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.fibreflow.tech"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fibreflow.tech"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // AI/ML configuration
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }

        // Placeholder values for build
        resValue("string", "maps_api_key", "AIzaSyBK0W8r3k6k5v5k5k5k5k5k5k5k5k5k5k")
        buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyBK0W8r3k6k5v5k5k5k5k5k5k5k5k5k5k\"")
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true

            // Debug AI/ML settings
            buildConfigField("String", "AI_MODEL_VERSION", "\"phi-3.5-mini-debug\"")
            buildConfigField("boolean", "ENABLE_AI_OPTIMIZATIONS", "false")
            buildConfigField("String", "API_BASE_URL", "\"https://dev-api.fibreflow.tech\"")
            buildConfigField("boolean", "ENABLE_CRASHLYTICS", "false")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
            buildConfigField("boolean", "ENABLE_DETAILED_LOGGING", "true")
            buildConfigField("String", "ENVIRONMENT", "\"development\"")
        }

        create("staging") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".staging"
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = false

            // Staging configuration
            buildConfigField("String", "AI_MODEL_VERSION", "\"phi-3.5-mini-staging\"")
            buildConfigField("boolean", "ENABLE_AI_OPTIMIZATIONS", "true")
            buildConfigField("String", "API_BASE_URL", "\"https://staging-api.fibreflow.tech\"")
            buildConfigField("boolean", "ENABLE_CRASHLYTICS", "true")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
            buildConfigField("boolean", "ENABLE_DETAILED_LOGGING", "true")
            buildConfigField("String", "ENVIRONMENT", "\"staging\"")
            buildConfigField("boolean", "ENABLE_TESTING_FEATURES", "true")

            matchingFallbacks += listOf("release")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // AI/ML optimizations for release
            buildConfigField("String", "AI_MODEL_VERSION", "\"phi-3.5-mini-3.8b\"")
            buildConfigField("boolean", "ENABLE_AI_OPTIMIZATIONS", "true")
            buildConfigField("String", "API_BASE_URL", "\"https://api.fibreflow.tech\"")
            buildConfigField("boolean", "ENABLE_CRASHLYTICS", "true")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
            buildConfigField("boolean", "ENABLE_DETAILED_LOGGING", "false")
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
            buildConfigField("boolean", "ENABLE_PERFORMANCE_MONITORING", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.addAll(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.camera.core.ExperimentalGetImage",
                "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi"
            )
        }
    }

    signingConfigs {
        create("release") {
            // These should be set via environment variables or secure properties
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore/fibreflow.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "store123"
            keyAlias = System.getenv("KEY_ALIAS") ?: "fibreflow"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "key123"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    flavorDimensions += listOf("environment", "tier")

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "FibreField Dev")
            buildConfigField("boolean", "IS_DEV_BUILD", "true")
        }

        create("stg") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            resValue("string", "app_name", "FibreField Staging")
            buildConfigField("boolean", "IS_DEV_BUILD", "false")
        }

        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "FibreField")
            buildConfigField("boolean", "IS_DEV_BUILD", "false")
        }

        create("standard") {
            dimension = "tier"
            resValue("string", "app_tier", "Standard")
            buildConfigField("boolean", "IS_ENTERPRISE", "false")
        }

        create("enterprise") {
            dimension = "tier"
            applicationIdSuffix = ".enterprise"
            versionNameSuffix = "-enterprise"
            resValue("string", "app_tier", "Enterprise")
            buildConfigField("boolean", "IS_ENTERPRISE", "true")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "**/attach_hotspot_windows.dll"
            excludes += "**/libopenh264.so"
            excludes += "**/libmediapipe_jni.so"

            // TensorFlow Lite specific excludes
            pickFirsts += "**/libc++_shared.so"
            pickFirsts += "**/libtensorflowlite_jni.so"
        }
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:design"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))

    // Domain modules
    implementation(project(":domain:installation"))

    // Feature modules
    implementation(project(":feature:installation"))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Navigation
    implementation(libs.bundles.navigation)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.bundles.room)
    kapt(libs.room.compiler)
    implementation(libs.androidx.room.paging)

    // Camera
    implementation(libs.bundles.camera)

    // ML Kit - Temporarily disabled for build
    // implementation(libs.bundles.mlkit)

    // TensorFlow Lite - Temporarily disabled for build
    // implementation(libs.bundles.tensorflow)
    // implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.2")
    // implementation("com.google.android.gms:play-services-tflite-gpu:16.1.0")

    // Network
    implementation(libs.bundles.network)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.work.testing)

    // DataStore
    implementation(libs.bundles.datastore)

    // Biometric
    implementation(libs.androidx.biometric)

    // Location
    implementation(libs.androidx.location)

    // Maps
    implementation(libs.osmdroid.android)
    // implementation(libs.osmdroid.mapsforge) // Disabled due to SVG conflicts

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.messaging)

    // Coroutines
    implementation(libs.bundles.coroutines)
    testImplementation(libs.kotlinx.coroutines.test)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Security
    implementation(libs.androidx.security.crypto)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.bundles.testing.common)
    testImplementation(libs.bundles.testing.android)
    androidTestImplementation(libs.bundles.testing.common)
    androidTestImplementation(libs.bundles.testing.android)
    androidTestImplementation(libs.bundles.testing.compose)

    // Additional testing dependencies
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.androidx.benchmark.junit4)
    androidTestImplementation(libs.androidx.test.uiautomator)

    // Memory monitoring
    debugImplementation(libs.leakcanary.android)
}

// Deployment tasks
tasks.register("deployDevelopment") {
    group = "deployment"
    description = "Deploy to development environment"
    dependsOn("assembleDevDebug")
    doLast {
        println("Deploying to development environment...")
        // Add deployment logic here
    }
}

tasks.register("deployStaging") {
    group = "deployment"
    description = "Deploy to staging environment"
    dependsOn("assembleStagingRelease")
    doLast {
        println("Deploying to staging environment...")
        // Add deployment logic here
    }
}

tasks.register("deployProduction") {
    group = "deployment"
    description = "Deploy to production environment"
    dependsOn("bundleProdRelease")
    doLast {
        println("Deploying to production environment...")
        // Add deployment logic here
    }
}

tasks.register("runAllTests") {
    group = "verification"
    description = "Run all tests including unit, integration, and UI tests"
    dependsOn("test")
    dependsOn("connectedCheck")
    dependsOn("connectedAndroidTest")
}

tasks.register("generateTestReport") {
    group = "reporting"
    description = "Generate comprehensive test coverage report"
    dependsOn("testDebugUnitTestCoverage")
    dependsOn("connectedCheck")
    doLast {
        println("Generating comprehensive test report...")
        // Add report generation logic here
    }
}

tasks.register("securityScan") {
    group = "security"
    description = "Run security vulnerability scan"
    dependsOn("assembleDebug")
    doLast {
        println("Running security scan...")
        // Add security scanning logic here
    }
}

tasks.register("performanceTest") {
    group = "verification"
    description = "Run performance tests"
    dependsOn("assembleDebug")
    doLast {
        println("Running performance tests...")
        // Add performance testing logic here
    }
}

// Custom build info task
tasks.register("buildInfo") {
    group = "build"
    description = "Display build information"
    doLast {
        println("=== Build Information ===")
        println("Application ID: ${android.defaultConfig.applicationId}")
        println("Version Code: ${android.defaultConfig.versionCode}")
        println("Version Name: ${android.defaultConfig.versionName}")
        println("Build Time: ${System.currentTimeMillis()}")
        println("Build Host: ${System.getProperty("user.name")}")
        println("Git Branch: ${providers.exec { commandLine("git", "branch", "--show-current") }.standardOutput.asText.get().trim()}")
        println("Git Commit: ${providers.exec { commandLine("git", "rev-parse", "HEAD") }.standardOutput.asText.get().trim()}")
    }
}

// Environment-specific tasks
tasks.register("developmentEnvironmentCheck") {
    group = "verification"
    description = "Check development environment configuration"
    doLast {
        println("Checking development environment...")
        // Add environment validation logic here
    }
}

tasks.register("stagingEnvironmentCheck") {
    group = "verification"
    description = "Check staging environment configuration"
    doLast {
        println("Checking staging environment...")
        // Add environment validation logic here
    }
}

tasks.register("productionEnvironmentCheck") {
    group = "verification"
    description = "Check production environment configuration"
    doLast {
        println("Checking production environment...")
        // Add environment validation logic here
    }
}