import { test, expect } from '@playwright/test';

test.describe('1Map Simple Demo', () => {
  test('basic login and navigation demo', async ({ page }) => {
    // Set up headed mode with good visibility
    await page.setViewportSize({ width: 1400, height: 900 });

    console.log('Starting 1Map demo test...');

    // Go to 1Map
    await page.goto('https://www.1map.co.za');
    await page.waitForLoadState('networkidle');

    console.log('Page loaded successfully');
    await page.screenshot({ path: 'demo-01-homepage.png', fullPage: true });

    // Check if we need to login
    const emailInput = await page.$('input[type="email"]');
    if (emailInput) {
      console.log('Found login form - proceeding with login...');

      // Login
      await emailInput.fill('hein@velocityfibre.co.za');
      await page.fill('input[type="password"]', 'VanHein@80');
      await page.screenshot({ path: 'demo-02-login-filled.png', fullPage: true });

      // Click login
      await page.click('button[type="submit"]');

      // Wait for navigation
      await page.waitForNavigation({ timeout: 30000 });
      console.log('Login completed');
      await page.screenshot({ path: 'demo-03-after-login.png', fullPage: true });
    }

    // Look for Portal menu (try multiple selectors)
    const portalSelectors = [
      'text=Portal',
      'a:has-text("Portal")',
      '.portal-menu',
      '[href*="portal"]',
      'nav >> text=Portal'
    ];

    let portalFound = false;
    for (const selector of portalSelectors) {
      try {
        const portalElement = await page.$(selector);
        if (portalElement) {
          console.log(`Found Portal with selector: ${selector}`);

          // Try to hover first (as per documentation)
          await portalElement.hover();
          await page.waitForTimeout(1000);

          // Then click
          await portalElement.click();
          await page.waitForTimeout(2000);

          await page.screenshot({ path: 'demo-04-portal-clicked.png', fullPage: true });
          portalFound = true;
          break;
        }
      } catch (error) {
        console.log(`Selector ${selector} failed: ${error.message}`);
      }
    }

    if (!portalFound) {
      console.log('Portal not found, taking screenshot of current state...');
      await page.screenshot({ path: 'demo-04-no-portal.png', fullPage: true });
    }

    // Look for 1Map app link
    const appSelectors = [
      'text=1Map app',
      'a:has-text("1Map app")',
      '.onemap-app',
      '[href*="1map"]',
      'a >> text="1Map app"'
    ];

    let appFound = false;
    for (const selector of appSelectors) {
      try {
        const appElement = await page.$(selector);
        if (appElement) {
          console.log(`Found 1Map app with selector: ${selector}`);
          await appElement.click();
          await page.waitForTimeout(3000);

          await page.screenshot({ path: 'demo-05-onemap-app.png', fullPage: true });
          appFound = true;
          break;
        }
      } catch (error) {
        console.log(`App selector ${selector} failed: ${error.message}`);
      }
    }

    if (!appFound) {
      console.log('1Map app not found, checking current page state...');
      await page.screenshot({ path: 'demo-05-no-app.png', fullPage: true });
    }

    // Log final state
    console.log('Final URL:', page.url());
    console.log('Page title:', await page.title());

    // Final screenshot
    await page.screenshot({ path: 'demo-06-final-state.png', fullPage: true });

    // Keep browser open for a bit to observe
    console.log('Test completed - keeping browser open for 10 seconds for observation...');
    await page.waitForTimeout(10000);
  });
});