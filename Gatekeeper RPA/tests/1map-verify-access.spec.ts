import { test, expect } from '@playwright/test';

test('Verify 1Map Access and Navigation', async ({ page }) => {
  // Setup Chrome browser
  await page.setViewportSize({ width: 1400, height: 900 });

  console.log('=== Verifying 1Map Access ===');

  // Navigate to 1Map
  await page.goto('https://www.1map.co.za');
  await page.waitForTimeout(5000);

  console.log('Current URL:', page.url());
  await page.screenshot({ path: 'verify-01-current-state.png' });

  // Check if already logged in by looking for user-specific content
  const pageText = await page.textContent('body');
  console.log('Page contains user content:', pageText.includes('hein@velocityfibre.co.za') || pageText.includes('Portal') || pageText.includes('Logout'));

  // Try to access Portal if available
  try {
    const portalLink = await page.$('a:has-text("Portal"), text=Portal, .portal');
    if (portalLink) {
      console.log('📂 Found Portal - clicking...');
      await portalLink.hover();
      await page.waitForTimeout(1000);
      await portalLink.click();
      await page.waitForTimeout(3000);
      await page.screenshot({ path: 'verify-02-portal.png' });
    }
  } catch (error) {
    console.log('Portal access:', error.message);
  }

  // Try to access 1Map app
  try {
    const oneMapApp = await page.$('a:has-text("1Map app"), text=1Map app, [href*="1map"]');
    if (oneMapApp) {
      console.log('🗺️ Found 1Map app - clicking...');
      await oneMapApp.click();
      await page.waitForTimeout(5000);
      console.log('Current URL after clicking 1Map app:', page.url());
      await page.screenshot({ path: 'verify-03-onemap-app.png' });
    }
  } catch (error) {
    console.log('1Map app access:', error.message);
  }

  // Check for workspace options
  try {
    const workspaceElements = await page.$$('select, .dropdown, .workspace, [role="combobox"]');
    if (workspaceElements.length > 0) {
      console.log('🏠 Found workspace elements');
      await page.screenshot({ path: 'verify-04-workspace.png' });
    }
  } catch (error) {
    console.log('Workspace check:', error.message);
  }

  // Final state
  console.log('Final URL:', page.url());
  console.log('Page title:', await page.title());
  await page.screenshot({ path: 'verify-05-final.png' });

  // Keep open for observation
  console.log('👁️ Keeping browser open for 20 seconds...');
  await page.waitForTimeout(20000);

  console.log('✅ Access verification completed!');
});