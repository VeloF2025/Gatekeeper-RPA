import { test, expect } from '@playwright/test';

test.describe('1Map Complete Audit Workflow', () => {
  test('complete audit workflow following documented steps', async ({ page }) => {
    // Setup Chrome browser for visual testing
    await page.setViewportSize({ width: 1400, height: 900 });

    console.log('=== Starting Complete 1Map Audit Workflow ===');

    // Step 1: Navigate to 1Map login page
    await page.goto('https://www.1map.co.za/login');
    await page.waitForTimeout(5000);

    console.log('✓ Step 0: At login page');
    console.log('Current URL:', page.url());
    await page.screenshot({ path: 'audit-01-login-page.png' });

    // Step 2: Sign in with credentials
    const emailInput = await page.$('input[type="email"]');
    if (emailInput) {
      console.log('🔐 Performing login...');
      await emailInput.fill('hein@velocityfibre.co.za');
      await page.fill('input[type="password"]', 'VanHein@80');
      await page.screenshot({ path: 'audit-02-credentials-filled.png' });

      // Click sign in button
      const signInButton = await page.$('button[type="submit"], button:has-text("Sign in"), button:has-text("Login")');
      if (signInButton) {
        await signInButton.click();
        await page.waitForTimeout(10000);
        console.log('✓ Step 1: Sign in completed');
        await page.screenshot({ path: 'audit-03-signed-in.png' });
      }
    }

    // Step 3: Open the 1Map app (via Portal navigation)
    console.log('📂 Looking for Portal navigation...');

    // Try different portal selectors
    const portalSelectors = [
      'text=Portal',
      'a >> text=Portal',
      '.navigation >> text=Portal',
      'nav >> text=Portal'
    ];

    let portalFound = false;
    for (const selector of portalSelectors) {
      try {
        const portal = await page.$(selector);
        if (portal) {
          console.log(`Found Portal with: ${selector}`);

          // Hover over Portal (as per documentation)
          await portal.hover();
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'audit-04-portal-hover.png' });

          // Click on Portal
          await portal.click();
          await page.waitForTimeout(3000);
          console.log('✓ Step 2: Portal clicked');
          await page.screenshot({ path: 'audit-05-portal-clicked.png' });
          portalFound = true;
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 4: Click "1Map app"
    console.log('🗺️ Looking for 1Map app link...');

    const appSelectors = [
      'text=1Map app',
      'a >> text=1Map app',
      '[href*="1map"]',
      '.app-link >> text=1Map'
    ];

    let appFound = false;
    for (const selector of appSelectors) {
      try {
        const appLink = await page.$(selector);
        if (appLink) {
          console.log(`Found 1Map app with: ${selector}`);
          await appLink.click();
          await page.waitForTimeout(5000);
          console.log('✓ Step 3: 1Map app opened');
          console.log('Current URL:', page.url());
          await page.screenshot({ path: 'audit-06-onemap-app-opened.png' });
          appFound = true;
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 5: Choose the correct workspace
    console.log('🏠 Looking for workspace selection...');

    const workspaceSelectors = [
      'text=Home Signups & Installations',
      'select',
      '.dropdown',
      '.workspace-selector',
      '[role="combobox"]'
    ];

    for (const selector of workspaceSelectors) {
      try {
        const workspace = await page.$(selector);
        if (workspace) {
          console.log(`Found workspace element with: ${selector}`);
          await workspace.click();
          await page.waitForTimeout(2000);

          // Look for "Home Signups & Installations" option
          const homeSignupOption = await page.$('text=Home Signups & Installations, text=Home Signup & Home Installation');
          if (homeSignupOption) {
            await homeSignupOption.click();
            await page.waitForTimeout(2000);
            console.log('✓ Step 4: Workspace selected');
            await page.screenshot({ path: 'audit-07-workspace-selected.png' });
          }
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 6: Search the active layer for the Drop
    console.log('🔍 Looking for search functionality...');

    const searchSelectors = [
      'input[placeholder*="search"]',
      'input[type="search"]',
      '.search-input',
      '.layer-search',
      'input[placeholder*="Search"]'
    ];

    for (const selector of searchSelectors) {
      try {
        const searchInput = await page.$(selector);
        if (searchInput) {
          console.log(`Found search input with: ${selector}`);
          await searchInput.fill('DR1854443'); // Example DR number
          await page.waitForTimeout(1000);
          await page.press(selector, 'Enter');
          await page.waitForTimeout(3000);
          console.log('✓ Step 5: DR number searched');
          await page.screenshot({ path: 'audit-08-dr-searched.png' });
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 7: Look for results table
    console.log('📊 Looking for results table...');

    const tableSelectors = [
      'table',
      '.results-table',
      '.data-grid',
      '.grid',
      '[role="table"]'
    ];

    for (const selector of tableSelectors) {
      try {
        const table = await page.$(selector);
        if (table) {
          console.log(`Found results table with: ${selector}`);
          await page.screenshot({ path: 'audit-09-results-table.png' });
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Final state
    console.log('=== Workflow Complete ===');
    console.log('Final URL:', page.url());
    console.log('Page title:', await page.title());
    await page.screenshot({ path: 'audit-10-final-state.png' });

    // Keep browser open for manual verification
    console.log('👁️ Keeping browser open for 30 seconds for manual observation...');
    await page.waitForTimeout(30000);

    console.log('✅ Complete audit workflow finished!');
  });
});