import { test, expect } from '@playwright/test';

test('Direct 1Map Login Test', async ({ page }) => {
  // Setup Chrome browser
  await page.setViewportSize({ width: 1400, height: 900 });

  console.log('=== Starting Direct 1Map Login ===');

  // Navigate to 1Map
  await page.goto('https://www.1map.co.za');

  // Wait a reasonable time for page to load
  await page.waitForTimeout(5000);

  console.log('Page loaded, current URL:', page.url());
  await page.screenshot({ path: 'direct-01-page.png' });

  // Check if login form exists
  const emailInput = await page.$('input[type="email"]');
  if (emailInput) {
    console.log('📝 Found login form - entering credentials...');

    // Enter credentials
    await emailInput.fill('hein@velocityfibre.co.za');
    await page.fill('input[type="password"]', 'VanHein@80');

    console.log('✓ Credentials entered');
    await page.screenshot({ path: 'direct-02-credentials.png' });

    // Find and click login button
    const loginButton = await page.$('button[type="submit"], button:has-text("Sign in"), button:has-text("Login")');
    if (loginButton) {
      console.log('🔐 Clicking login button...');
      await loginButton.click();

      // Wait for navigation
      await page.waitForTimeout(10000);

      console.log('✓ Login completed');
      console.log('Final URL:', page.url());
      await page.screenshot({ path: 'direct-03-logged-in.png' });
    } else {
      console.log('❌ Login button not found');
    }
  } else {
    console.log('ℹ️ Login form not found - might already be logged in');
    await page.screenshot({ path: 'direct-already-logged-in.png' });
  }

  // Keep browser open for observation
  console.log('👁️ Keeping browser open for 15 seconds...');
  await page.waitForTimeout(15000);

  console.log('✅ Login test completed!');
});