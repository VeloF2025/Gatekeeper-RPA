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
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fibreflow.tech"
        minSdk = 24
        targetSdk = 35
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
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // AI/ML optimizations for release
            buildConfigField("String", "AI_MODEL_VERSION", "\"phi-3.5-mini-3.8b\"")
            buildConfigField("boolean", "ENABLE_AI_OPTIMIZATIONS", "true")
        }

        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true

            // Debug AI/ML settings
            buildConfigField("String", "AI_MODEL_VERSION", "\"phi-3.5-mini-debug\"")
            buildConfigField("boolean", "ENABLE_AI_OPTIMIZATIONS", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.camera.core.ExperimentalGetImage",
            "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi"
        )
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
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:ai"))
    implementation(project(":core:design"))

    // Domain modules
    implementation(project(":domain:authentication"))
    implementation(project(":domain:installation"))
    implementation(project(":domain:drops"))
    implementation(project(":domain:activation"))
    implementation(project(":domain:remediation"))

    // Feature modules
    implementation(project(":feature:authentication"))
    implementation(project(":feature:installation"))
    implementation(project(":feature:drops"))
    implementation(project(":feature:activation"))
    implementation(project(":feature:remediation"))

    // Infrastructure modules
    implementation(project(":infrastructure:sync"))
    implementation(project(":infrastructure:location"))
    implementation(project(":infrastructure:security"))

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

    // ML Kit
    implementation(libs.bundles.mlkit)

    // TensorFlow Lite
    implementation(libs.bundles.tensorflow)
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.4")
    implementation("com.google.android.gms:play-services-tflite-acceleration:16.2.0")

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
    implementation(libs.osmdroid.mapsforge)

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
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("org.robolectric:robolectric:4.12.1")
    androidTestImplementation("androidx.benchmark:benchmark-junit4:1.2.4")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    // Memory monitoring
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.13")
}