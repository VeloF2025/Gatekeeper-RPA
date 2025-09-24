import { test, expect } from '@playwright/test';

test.describe('1Map Chrome Visual Test', () => {
  test('visual exploration of 1Map interface', async ({ page }) => {
    // Set up Chrome with good viewport
    await page.setViewportSize({ width: 1400, height: 900 });

    console.log('Starting Chrome visual test...');

    // Navigate to 1Map
    await page.goto('https://www.1map.co.za');
    await page.waitForLoadState('networkidle');

    console.log('1Map homepage loaded');
    await page.screenshot({ path: 'chrome-01-homepage.png' });

    // Check if login form exists
    const emailInput = await page.$('input[type="email"]');
    if (emailInput) {
      console.log('Login form found - filling credentials...');

      // Fill login form
      await emailInput.fill('hein@velocityfibre.co.za');
      await page.fill('input[type="password"]', 'VanHein@80');
      await page.screenshot({ path: 'chrome-02-login-filled.png' });

      // Click login
      await page.click('button[type="submit"]');

      // Wait for navigation after login
      await page.waitForNavigation({ timeout: 30000 });
      console.log('Login successful');
      await page.screenshot({ path: 'chrome-03-logged-in.png' });

      // Look for Portal menu
      try {
        const portalLink = await page.$('a:has-text("Portal")');
        if (portalLink) {
          console.log('Found Portal link - hovering...');
          await portalLink.hover();
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'chrome-04-portal-hover.png' });

          // Click on Portal
          await portalLink.click();
          await page.waitForTimeout(3000);
          await page.screenshot({ path: 'chrome-05-portal-clicked.png' });
        }
      } catch (error) {
        console.log('Portal link not found or error:', error.message);
      }

      // Look for 1Map app
      try {
        const oneMapApp = await page.$('a:has-text("1Map app")');
        if (oneMapApp) {
          console.log('Found 1Map app - clicking...');
          await oneMapApp.click();
          await page.waitForTimeout(5000); // Wait longer for app to load
          await page.screenshot({ path: 'chrome-06-onemap-app.png' });
        }
      } catch (error) {
        console.log('1Map app not found or error:', error.message);
      }

      // Look for workspace dropdown
      try {
        const workspaceDropdown = await page.$('select, .dropdown, [role="combobox"]');
        if (workspaceDropdown) {
          console.log('Found workspace dropdown - clicking...');
          await workspaceDropdown.click();
          await page.waitForTimeout(2000);
          await page.screenshot({ path: 'chrome-07-workspace-dropdown.png' });
        }
      } catch (error) {
        console.log('Workspace dropdown not found:', error.message);
      }

      // Look for any search functionality
      try {
        const searchInput = await page.$('input[placeholder*="search"], input[type="search"], .search-input');
        if (searchInput) {
          console.log('Found search input - testing with DR number...');
          await searchInput.fill('DR1854443');
          await page.waitForTimeout(1000);
          await page.screenshot({ path: 'chrome-08-search-filled.png' });
        }
      } catch (error) {
        console.log('Search input not found:', error.message);
      }

      // Final state
      console.log('Final URL:', page.url());
      console.log('Page title:', await page.title());
      await page.screenshot({ path: 'chrome-09-final-state.png' });

      // Keep open for observation
      console.log('Keeping browser open for 15 seconds for manual observation...');
      await page.waitForTimeout(15000);

    } else {
      console.log('Login form not found - might already be logged in');
      await page.screenshot({ path: 'chrome-no-login-form.png' });
    }
  });
});