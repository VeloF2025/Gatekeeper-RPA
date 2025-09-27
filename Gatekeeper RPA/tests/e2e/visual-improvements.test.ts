/**
 * Focused Visual Validation Test for Gatekeeper RPA System
 *
 * Quick visual validation test to capture the improved UI/UX styling.
 */

import { test, expect } from '@playwright/test';

// Test configuration with headed mode for visual validation
test.use({
  headless: false,
  viewport: { width: 1280, height: 720 },
  launchOptions: {
    slowMo: 200,
  },
});

test.describe('Gatekeeper RPA - Visual Improvements Validation', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:3000');
    await page.waitForLoadState('networkidle');
  });

  test('VISUAL-001: Homepage Professional Styling', async ({ page }) => {
    // Take comprehensive screenshots of the improved homepage
    await page.screenshot({ path: 'improved-homepage-full.png', fullPage: true });

    // Focus on key sections
    const headerSection = page.locator('section').first();
    await headerSection.screenshot({ path: 'improved-header-section.png' });

    const featureCards = page.locator('.grid > .card').first();
    await featureCards.screenshot({ path: 'improved-feature-cards.png' });

    const statsSection = page.locator('section').nth(2);
    await statsSection.screenshot({ path: 'improved-stats-section.png' });

    // Test button hover effects
    const dashboardButton = page.locator('text=📊Dashboard');
    await dashboardButton.hover();
    await page.waitForTimeout(300);
    await dashboardButton.screenshot({ path: 'improved-button-hover.png' });
  });

  test('VISUAL-002: Responsive Design Validation', async ({ page }) => {
    const viewports = [
      { width: 1920, height: 1080, name: 'Desktop' },
      { width: 768, height: 1024, name: 'Tablet' },
      { width: 375, height: 812, name: 'Mobile' }
    ];

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(500);
      await page.screenshot({ path: `improved-responsive-${viewport.name}.png`, fullPage: true });
    }
  });

  test('VISUAL-003: Interactive Elements Validation', async ({ page }) => {
    // Test navigation to other pages
    const dashboardLink = page.locator('text=📊Dashboard');
    await dashboardLink.click();
    await page.waitForLoadState('networkidle');
    await page.screenshot({ path: 'improved-dashboard-page.png' });

    // Navigate back
    await page.goto('http://localhost:3000');
    await page.waitForLoadState('networkidle');

    const ticketsLink = page.locator('text=🎫View Tickets');
    await ticketsLink.click();
    await page.waitForLoadState('networkidle');
    await page.screenshot({ path: 'improved-tickets-page.png' });
  });

});