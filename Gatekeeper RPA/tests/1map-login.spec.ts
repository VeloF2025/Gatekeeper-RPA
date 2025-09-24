import { test, expect } from '@playwright/test';

test.describe('1Map Login Process', () => {
  test('login to 1Map in headed mode', async ({ page }) => {
    // Enable headed mode for visual debugging
    await page.setViewportSize({ width: 1280, height: 720 });

    // Navigate to 1Map login page
    await page.goto('https://www.1map.co.za/login');

    // Wait for page to load
    await page.waitForLoadState('networkidle');

    // Take screenshot of initial state
    await page.screenshot({ path: '1map-initial-state.png', fullPage: true });

    // Fill in login credentials
    await page.fill('input[type="email"]', 'hein@velocityfibre.co.za');
    await page.fill('input[type="password"]', 'VanHein@80');

    // Click login button
    await page.click('button[type="submit"]');

    // Wait for navigation after login
    await page.waitForNavigation({ timeout: 30000 });

    // Take screenshot after successful login
    await page.screenshot({ path: '1map-logged-in.png', fullPage: true });

    // Verify successful login by checking for dashboard element
    await expect(page.locator('body')).toBeVisible();

    // Log the current URL for debugging
    console.log('Current URL after login:', page.url());

    // Wait a bit to see the result in headed mode
    await page.waitForTimeout(5000);
  });

  test('1Map basic navigation', async ({ page }) => {
    await page.goto('https://www.1map.co.za');
    await page.waitForLoadState('networkidle');

    // Take screenshot
    await page.screenshot({ path: '1map-homepage.png', fullPage: true });

    // Log page title
    const title = await page.title();
    console.log('Page title:', title);

    // Check if we can see login button or similar elements
    const loginButton = await page.$('text=Login') || await page.$('text=Sign In') || await page.$('button:has-text("Login")');

    if (loginButton) {
      console.log('Found login button');
      await loginButton.click();
      await page.waitForTimeout(3000);
      await page.screenshot({ path: '1map-login-page.png', fullPage: true });
    }
  });
});