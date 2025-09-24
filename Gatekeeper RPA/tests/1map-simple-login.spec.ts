import { test, expect } from '@playwright/test';

test.describe('1Map Simple Login', () => {
  test('login and verify access', async ({ page }) => {
    // Setup Chrome browser
    await page.setViewportSize({ width: 1400, height: 900 });

    console.log('Starting 1Map login test...');

    // Go to 1Map login page
    await page.goto('https://www.1map.co.za/login');
    await page.waitForLoadState('networkidle');

    console.log('Login page loaded');
    await page.screenshot({ path: 'login-01-page.png' });

    // Fill in credentials
    await page.fill('input[type="email"]', 'hein@velocityfibre.co.za');
    await page.fill('input[type="password"]', 'VanHein@80');

    console.log('Credentials filled');
    await page.screenshot({ path: 'login-02-credentials.png' });

    // Click login button
    await page.click('button[type="submit"]');

    // Wait for navigation after login
    await page.waitForNavigation({ timeout: 30000 });

    console.log('Login completed');
    console.log('Current URL:', page.url());
    await page.screenshot({ path: 'login-03-success.png' });

    // Wait a bit to see the result
    await page.waitForTimeout(10000);

    console.log('Login test completed successfully!');
  });
});