/**
 * Simple Navigation Test for Gatekeeper RPA System
 *
 * Tests basic navigation functionality to verify routes are working correctly.
 */

import { test, expect } from '@playwright/test';

test.use({
  headless: false,
  viewport: { width: 1280, height: 720 },
});

test.describe('Basic Navigation Tests', () => {
  test('should navigate between pages successfully', async ({ page }) => {
    // Test homepage
    await page.goto('http://localhost:3004');
    await expect(page).toHaveTitle(/Gatekeeper RPA/);
    await expect(page.locator('h1')).toContainText('Gatekeeper RPA');

    // Test dashboard navigation
    await page.click('a[href="/dashboard"]');
    await page.waitForLoadState('networkidle');
    await expect(page).toHaveURL(/\/dashboard$/);
    await expect(page.locator('h1')).toContainText('Dashboard');

    // Test tickets navigation
    await page.click('a[href="/tickets"]');
    await page.waitForLoadState('networkidle');
    await expect(page).toHaveURL(/\/tickets$/);
    await expect(page.locator('h1')).toContainText('Tickets');

    // Test back to home
    await page.click('a[href="/"]');
    await page.waitForLoadState('networkidle');
    await expect(page).toHaveURL(/\/$/);
    await expect(page.locator('h1')).toContainText('Gatekeeper RPA');
  });

  test('should show 404 for non-existent routes', async ({ page }) => {
    await page.goto('http://localhost:3004/non-existent-page');
    await expect(page.locator('body')).toContainText('404');
  });
});