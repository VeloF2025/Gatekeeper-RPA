import { test, expect } from '@playwright/test';

test.describe('1Map Operations Process', () => {
  test.beforeEach(async ({ page }) => {
    // Set up headed mode
    await page.setViewportSize({ width: 1280, height: 720 });
  });

  test('navigate to operations dashboard', async ({ page }) => {
    // Go to 1Map main page
    await page.goto('https://www.1map.co.za');
    await page.waitForLoadState('networkidle');

    console.log('Navigated to 1Map homepage');

    // Take initial screenshot
    await page.screenshot({ path: 'operations-1map-home.png', fullPage: true });

    // Look for operations-related menu items or buttons
    const operationsSelectors = [
      'text=Portal',
      'text=1Map app',
      'text=Operations',
      'text=Projects',
      'text=Dashboard',
      'text=Work Orders',
      'text=Tasks',
      'text=Maps',
      '.operations-menu',
      '[href*="operations"]',
      '[href*="projects"]'
    ];

    for (const selector of operationsSelectors) {
      try {
        const element = await page.$(selector);
        if (element) {
          console.log(`Found operations element: ${selector}`);
          await element.click();
          await page.waitForTimeout(2000);
          break;
        }
      } catch (error) {
        // Continue to next selector
      }
    }

    // Take screenshot after navigation
    await page.screenshot({ path: 'operations-navigation.png', fullPage: true });

    // Log current URL
    console.log('Current URL:', page.url());

    // Wait to observe in headed mode
    await page.waitForTimeout(3000);
  });

  test('search for specific operations or projects', async ({ page }) => {
    await page.goto('https://www.1map.co.za');
    await page.waitForLoadState('networkidle');

    console.log('Starting operations search test');

    // Look for search functionality
    const searchSelectors = [
      'input[placeholder*="Search"]',
      'input[type="search"]',
      '.search-input',
      '[aria-label*="search"]'
    ];

    for (const selector of searchSelectors) {
      try {
        const searchInput = await page.$(selector);
        if (searchInput) {
          console.log(`Found search input: ${selector}`);
          await searchInput.fill('DR1854443'); // Example DR number
          await page.waitForTimeout(1000);
          await page.press(selector, 'Enter');
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'operations-search-results.png', fullPage: true });
          break;
        }
      } catch (error) {
        // Continue to next selector
      }
    }

    // Wait to observe results
    await page.waitForTimeout(3000);
  });

  test('check for workspace selection', async ({ page }) => {
    await page.goto('https://www.1map.co.za');
    await page.waitForLoadState('networkidle');

    console.log('Checking workspace selection functionality');

    // First login if needed
    const emailInput = await page.$('input[type="email"]');
    if (emailInput) {
      await emailInput.fill('hein@velocityfibre.co.za');
      await page.fill('input[type="password"]', 'VanHein@80');
      await page.click('button[type="submit"]');
      await page.waitForNavigation({ timeout: 30000 });
    }

    // Look for Portal navigation
    const portalSelectors = [
      'text=Portal',
      'a:has-text("Portal")',
      '.portal-menu',
      '[href*="portal"]'
    ];

    for (const selector of portalSelectors) {
      try {
        const portalElement = await page.$(selector);
        if (portalElement) {
          console.log(`Found Portal element: ${selector}`);
          await portalElement.click();
          await page.waitForTimeout(2000);
          break;
        }
      } catch (error) {
        // Continue to next selector
      }
    }

    // Look for 1Map app link
    const oneMapAppSelectors = [
      'text=1Map app',
      'a:has-text("1Map app")',
      '.onemap-app',
      '[href*="1map"]'
    ];

    for (const selector of oneMapAppSelectors) {
      try {
        const appElement = await page.$(selector);
        if (appElement) {
          console.log(`Found 1Map app element: ${selector}`);
          await appElement.click();
          await page.waitForTimeout(3000);
          await page.screenshot({ path: 'onemap-app-loaded.png', fullPage: true });
          break;
        }
      } catch (error) {
        // Continue to next selector
      }
    }

    // Look for workspace dropdown
    const workspaceSelectors = [
      'text=Home Signups & Installations',
      'select:has-text("Workspace")',
      '.workspace-dropdown',
      '[aria-label*="workspace"]'
    ];

    for (const selector of workspaceSelectors) {
      try {
        const workspaceElement = await page.$(selector);
        if (workspaceElement) {
          console.log(`Found workspace element: ${selector}`);
          await workspaceElement.click();
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'workspace-selection.png', fullPage: true });
          break;
        }
      } catch (error) {
        // Continue to next selector
      }
    }

    // Wait to observe results
    await page.waitForTimeout(3000);
  });
});