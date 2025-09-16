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
 * Test cases for Glass Card Component
 *
 * Validates:
 * - Glass morphism visual effects
 * - Transparency and blur effects
 * - Neon accent integration
 * - Accessibility compliance
 * - Performance optimization
 * - Reusable card component
 */
class GlassCardComponentTest : BaseComponentTest() {

    @Test
    fun `should display glass card correctly`() {
        setContentWithTheme {
            GlassCard(
                modifier = Modifier.testTag("glass_card")
            ) {
                Text("Glass Card Content")
            }
        }

        validateAccessibility("glass_card")
        validatePerformance("glass_card")
    }

    @Test
    fun `should handle different glass intensities`() {
        val alphas = listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f)

        alphas.forEach { alpha ->
            setContentWithTheme {
                GlassCard(
                    alpha = alpha,
                    modifier = Modifier.testTag("glass_alpha_$alpha")
                ) {
                    Text("Glass Content $alpha")
                }
            }

            validateAccessibility("glass_alpha_$alpha")
        }
    }

    @Test
    fun `should integrate with neon accent colors`() {
        TestData.neonColors.forEach { color ->
            setContentWithTheme {
                GlassCard(
                    accentColor = color,
                    modifier = Modifier.testTag("glass_neon_${color.hashCode()}")
                ) {
                    Text("Neon Accent Card")
                }
            }

            validateAccessibility("glass_neon_${color.hashCode()}")
        }
    }

    @Test
    fun `should be accessible for screen readers`() {
        setContentWithTheme {
            GlassCard(
                modifier = Modifier.testTag("glass_accessibility")
            ) {
                Text("Accessible Glass Card")
            }
        }

        validateAccessibility(
            testTag = "glass_accessibility",
            expectedRole = Role.Button,
            expectedContentDescription = "Glass card with content"
        )
    }

    @Test
    fun `should work in both light and dark themes`() {
        validateThemeCompatibility("glass_theme") {
            GlassCard(
                modifier = Modifier.testTag("glass_theme")
            ) {
                Text("Theme Test")
            }
        }
    }

    @Test
    fun `should be responsive across different screen sizes`() {
        validateResponsiveness("glass_responsive") { modifier ->
            GlassCard(
                modifier = modifier.testTag("glass_responsive")
            ) {
                Text("Responsive Glass Card")
            }
        }
    }

    @Test
    fun `should maintain performance with multiple glass cards`() {
        setContentWithTheme {
            Column(modifier = Modifier.testTag("glass_multiple")) {
                repeat(10) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    ) {
                        Text("Card $it")
                    }
                }
            }
        }

        validatePerformance("glass_multiple", maxTimeMs = 100) // Allow more time for multiple cards
    }
}