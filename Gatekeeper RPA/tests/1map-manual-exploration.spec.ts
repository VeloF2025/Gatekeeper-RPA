import { test, expect } from '@playwright/test';

test('Manual Chrome Exploration of 1Map Path', async ({ page }) => {
  // Setup Chrome browser
  await page.setViewportSize({ width: 1600, height: 900 });

  console.log('=== Starting Manual Chrome Exploration ===');
  console.log('This will open Chrome and keep it open for manual navigation');

  // Navigate to 1Map
  await page.goto('https://www.1map.co.za');
  await page.waitForTimeout(5000);

  console.log('📍 Starting at:', page.url());
  await page.screenshot({ path: 'manual-01-start.png' });

  // Check if login is needed
  const emailInput = await page.$('input[type="email"]');
  if (emailInput) {
    console.log('🔐 Auto-filling login credentials...');
    await emailInput.fill('hein@velocityfibre.co.za');
    await page.fill('input[type="password"]', 'VanHein@80');
    await page.screenshot({ path: 'manual-02-login-filled.png' });

    const loginButton = await page.$('button[type="submit"]');
    if (loginButton) {
      await loginButton.click();
      await page.waitForTimeout(10000);
      console.log('✅ Login completed');
      await page.screenshot({ path: 'manual-03-logged-in.png' });
    }
  } else {
    console.log('ℹ️ Already logged in or login form not found');
  }

  console.log('🌐 Current page:', page.url());
  console.log('📄 Page title:', await page.title());

  // Keep Chrome open for manual exploration
  console.log('👁️ Chrome browser is now open for manual navigation');
  console.log('📋 Please manually go through your process:');
  console.log('   1. Look for Portal menu');
  console.log('   2. Click on 1Map app');
  console.log('   3. Select workspace "Home Signups & Installations"');
  console.log('   4. Search for your specific DR number');
  console.log('   5. Navigate to the installation details');
  console.log('   6. Check the photos/documents section');
  console.log('');
  console.log('⏰ Browser will remain open for 5 minutes for manual exploration');

  // Take periodic screenshots during manual exploration
  let screenshotCount = 4;
  const screenshotInterval = setInterval(async () => {
    await page.screenshot({ path: `manual-exploration-${screenshotCount.toString().padStart(2, '0')}.png` });
    console.log(`📸 Screenshot ${screenshotCount} captured at:`, page.url());
    screenshotCount++;
  }, 30000); // Screenshot every 30 seconds

  // Keep browser open for 5 minutes (300 seconds)
  await page.waitForTimeout(300000);

  // Clear the interval
  clearInterval(screenshotInterval);

  // Final screenshot
  await page.screenshot({ path: 'manual-final-state.png' });
  console.log('🏁 Manual exploration session ended');
  console.log('📍 Final URL:', page.url());
});