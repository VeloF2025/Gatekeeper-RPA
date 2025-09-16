package com.fibreflow.tech.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.junit.Test
import org.junit.Assert.*

/**
 * Test cases for Biometric Auth Dialog Component
 *
 * Validates:
 * - Fingerprint and face ID authentication UI
 * - Modern authentication interface
 * - Error handling and fallback options
 * - Accessibility compliance
 * - Security best practices
 */
class BiometricAuthDialogTest : BaseComponentTest() {

    @Test
    fun `should display biometric auth dialog correctly`() {
        setContentWithTheme {
            BiometricAuthDialog(
                onDismiss = {},
                onAuthSuccess = {},
                modifier = Modifier.testTag("biometric_dialog")
            )
        }

        validateAccessibility("biometric_dialog")
        validatePerformance("biometric_dialog")
    }

    @Test
    fun `should handle different biometric types`() {
        val biometricTypes = listOf("fingerprint", "face", "iris", "none")

        biometricTypes.forEach { type ->
            setContentWithTheme {
                BiometricAuthDialog(
                    biometricType = type,
                    onDismiss = {},
                    onAuthSuccess = {},
                    modifier = Modifier.testTag("biometric_type_$type")
                )
            }

            validateAccessibility("biometric_type_$type")
        }
    }

    @Test
    fun `should show authentication states`() {
        val authStates = listOf("ready", "processing", "success", "error", "cancelled")

        authStates.forEach { state ->
            setContentWithTheme {
                BiometricAuthDialog(
                    authState = state,
                    onDismiss = {},
                    onAuthSuccess = {},
                    modifier = Modifier.testTag("biometric_state_$state")
                )
            }

            validateAccessibility("biometric_state_$state")
        }
    }

    @Test
    fun `should be accessible for screen readers`() {
        setContentWithTheme {
            BiometricAuthDialog(
                biometricType = "fingerprint",
                authState = "ready",
                onDismiss = {},
                onAuthSuccess = {},
                modifier = Modifier.testTag("biometric_accessibility")
            )
        }

        validateAccessibility(
            testTag = "biometric_accessibility",
            expectedRole = Role.Dialog,
            expectedContentDescription = "Biometric authentication dialog"
        )
    }

    @Test
    fun `should work in both light and dark themes`() {
        validateThemeCompatibility("biometric_theme") {
            BiometricAuthDialog(
                onDismiss = {},
                onAuthSuccess = {},
                modifier = Modifier.testTag("biometric_theme")
            )
        }
    }
}