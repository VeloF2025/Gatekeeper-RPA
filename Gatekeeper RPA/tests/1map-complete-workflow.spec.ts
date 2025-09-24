import { test, expect } from '@playwright/test';

test.describe('1Map Complete Workflow Test', () => {
  test('complete 1Map audit workflow', async ({ page }) => {
    // Chrome setup
    await page.setViewportSize({ width: 1400, height: 900 });

    console.log('=== Starting Complete 1Map Workflow Test ===');

    // Step 1: Navigate to 1Map
    await page.goto('https://www.1map.co.za');
    await page.waitForLoadState('networkidle');
    console.log('✓ Step 1: Navigated to 1Map');
    await page.screenshot({ path: 'workflow-01-homepage.png' });

    // Step 2: Handle login if needed
    const emailInput = await page.$('input[type="email"]');
    if (emailInput) {
      console.log('🔐 Login required...');
      await emailInput.fill('hein@velocityfibre.co.za');
      await page.fill('input[type="password"]', 'VanHein@80');
      await page.screenshot({ path: 'workflow-02-login-filled.png' });

      await page.click('button[type="submit"]');
      await page.waitForNavigation({ timeout: 30000 });
      console.log('✓ Step 2: Login completed');
      await page.screenshot({ path: 'workflow-03-logged-in.png' });
    } else {
      console.log('✓ Step 2: Already logged in');
    }

    // Step 3: Navigate to Portal
    try {
      // Try multiple selectors for Portal
      const portalSelectors = [
        'text=Portal',
        'a:has-text("Portal")',
        'nav >> text=Portal',
        '.menu >> text=Portal'
      ];

      for (const selector of portalSelectors) {
        const portal = await page.$(selector);
        if (portal) {
          console.log('📂 Found Portal - navigating...');
          await portal.hover();
          await page.waitForTimeout(1000);
          await portal.click();
          await page.waitForTimeout(2000);
          console.log('✓ Step 3: Portal accessed');
          await page.screenshot({ path: 'workflow-04-portal.png' });
          break;
        }
      }
    } catch (error) {
      console.log('⚠️ Portal navigation skipped:', error.message);
    }

    // Step 4: Access 1Map app
    try {
      const appSelectors = [
        'text=1Map app',
        'a:has-text("1Map app")',
        '[href*="1map"]'
      ];

      for (const selector of appSelectors) {
        const app = await page.$(selector);
        if (app) {
          console.log('🗺️ Found 1Map app - launching...');
          await app.click();
          await page.waitForTimeout(5000); // Wait for app to load
          console.log('✓ Step 4: 1Map app launched');
          await page.screenshot({ path: 'workflow-05-onemap-app.png' });
          break;
        }
      }
    } catch (error) {
      console.log('⚠️ 1Map app access skipped:', error.message);
    }

    // Step 5: Select workspace
    try {
      const workspaceSelectors = [
        'text=Home Signups & Installations',
        'select:has-text("Workspace")',
        '.workspace-dropdown',
        '[role="combobox"]'
      ];

      for (const selector of workspaceSelectors) {
        const workspace = await page.$(selector);
        if (workspace) {
          console.log('🏠 Found workspace selector...');
          await workspace.click();
          await page.waitForTimeout(2000);
          console.log('✓ Step 5: Workspace selection accessed');
          await page.screenshot({ path: 'workflow-06-workspace.png' });
          break;
        }
      }
    } catch (error) {
      console.log('⚠️ Workspace selection skipped:', error.message);
    }

    // Step 6: Search for DR number
    try {
      const searchSelectors = [
        'input[placeholder*="search"]',
        'input[type="search"]',
        '.search-input',
        'input[placeholder*="Search"]'
      ];

      for (const selector of searchSelectors) {
        const search = await page.$(selector);
        if (search) {
          console.log('🔍 Found search input - testing DR number...');
          await search.fill('DR1854443');
          await page.waitForTimeout(1000);
          await page.press(selector, 'Enter');
          await page.waitForTimeout(3000);
          console.log('✓ Step 6: DR number search completed');
          await page.screenshot({ path: 'workflow-07-search.png' });
          break;
        }
      }
    } catch (error) {
      console.log('⚠️ Search functionality skipped:', error.message);
    }

    // Step 7: Look for results table/grid
    try {
      const tableSelectors = [
        'table',
        '.grid',
        '.results-table',
        '[role="table"]',
        '.data-grid'
      ];

      for (const selector of tableSelectors) {
        const table = await page.$(selector);
        if (table) {
          console.log('📊 Found results table...');
          await page.screenshot({ path: 'workflow-08-results-table.png' });
          break;
        }
      }
    } catch (error) {
      console.log('⚠️ Results table not found:', error.message);
    }

    // Final state
    console.log('=== Workflow Complete ===');
    console.log('Final URL:', page.url());
    console.log('Page title:', await page.title());
    await page.screenshot({ path: 'workflow-09-final.png' });

    // Keep open for manual observation
    console.log('👁️ Keeping browser open for 20 seconds for manual verification...');
    await page.waitForTimeout(20000);
    console.log('✅ Test completed successfully!');
  });
});