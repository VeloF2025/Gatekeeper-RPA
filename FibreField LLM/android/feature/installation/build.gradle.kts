// 🟢 WORKING: Installation feature module with workflow and photo capture
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // alias(libs.plugins.hilt)  // temporarily disabled
    // alias(libs.plugins.kotlin.kapt)  // temporarily disabled
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.fibreflow.feature.installation"

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // Compose BOM for version management
    implementation(platform(libs.compose.bom))

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Hilt - temporarily disabled to resolve KAPT issues
    // implementation(libs.hilt.android)
    // kapt(libs.hilt.compiler)

    // Utilities
    implementation(libs.timber)

    // Camera
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    // Project dependencies
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:ai"))
    implementation(project(":core:location"))
    implementation(project(":core:authentication"))
    implementation(project(":domain:drops"))
    implementation(project(":domain:authentication"))

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.mockito.kotlin)
}

