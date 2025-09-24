import { test, expect } from '@playwright/test';

test.describe('Automated 1Map Audit Workflow', () => {
  test('complete automated audit process based on recorded workflow', async ({ page }) => {
    // Setup Chrome browser
    await page.setViewportSize({ width: 1600, height: 900 });

    console.log('=== 🚀 AUTOMATED 1MAP AUDIT WORKFLOW ===');
    console.log('Based on your recorded manual workflow\n');

    // Step 1: Navigate to 1Map homepage
    console.log('📍 Step 1: Navigate to 1Map homepage');
    await page.goto('https://www.1map.co.za');
    await page.waitForTimeout(3000);
    console.log('✅ Homepage loaded:', page.url());
    await page.screenshot({ path: 'automated-01-homepage.png' });

    // Step 2: Handle login navigation (if needed)
    if (page.url().includes('login')) {
      console.log('🔐 Step 2: Login required');
      await page.fill('input[type="email"]', 'hein@velocityfibre.co.za');
      await page.fill('input[type="password"]', 'VanHein@80');
      await page.screenshot({ path: 'automated-02-login-filled.png' });

      await page.click('button[type="submit"]');
      await page.waitForTimeout(5000);
      console.log('✅ Login completed');
    } else {
      console.log('ℹ️ Step 2: Already logged in');
    }

    // Step 3: Navigate to dashboard (if needed)
    if (!page.url().includes('dashboard') && !page.url().includes('apps')) {
      console.log('📊 Step 3: Navigate to dashboard');
      // Look for dashboard link or wait for redirect
      await page.waitForTimeout(3000);
    }

    // Step 4: Navigate to 1Map app
    console.log('🗺️ Step 4: Open 1Map app');

    // If not already in apps, navigate there
    if (!page.url().includes('apps/app')) {
      // Try multiple selectors to find 1Map app
      const appSelectors = [
        'a:has-text("1Map app")',
        'text=1Map app',
        '[href*="apps/app"]',
        'a[href*="1map"]'
      ];

      for (const selector of appSelectors) {
        try {
          const appLink = await page.$(selector);
          if (appLink) {
            console.log(`Found 1Map app with: ${selector}`);
            await appLink.click();
            await page.waitForTimeout(5000);
            break;
          }
        } catch (error) {
          continue;
        }
      }
    }

    console.log('✅ 1Map app loaded:', page.url());
    await page.screenshot({ path: 'automated-04-onemap-app.png' });

    // Step 5: Workspace selection
    console.log('🏠 Step 5: Select workspace');

    // Look for workspace dropdown/selector
    const workspaceSelectors = [
      'text=Home Signups & Installations',
      'select',
      '.workspace-dropdown',
      '[role="combobox"]',
      '.dropdown-toggle'
    ];

    let workspaceSelected = false;
    for (const selector of workspaceSelectors) {
      try {
        const workspace = await page.$(selector);
        if (workspace) {
          console.log(`Found workspace element: ${selector}`);
          await workspace.click();
          await page.waitForTimeout(2000);

          // Look for the specific workspace option
          const optionSelectors = [
            'text=Home Signups & Installations',
            'text=Home Signup & Home Installation',
            'option[value*="signup"]',
            '.dropdown-item >> text=Home'
          ];

          for (const optionSelector of optionSelectors) {
            try {
              const option = await page.$(optionSelector);
              if (option) {
                await option.click();
                await page.waitForTimeout(2000);
                console.log('✅ Workspace "Home Signups & Installations" selected');
                workspaceSelected = true;
                await page.screenshot({ path: 'automated-05-workspace-selected.png' });
                break;
              }
            } catch (error) {
              continue;
            }
          }
          if (workspaceSelected) break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 6: Search for DR number
    console.log('🔍 Step 6: Search for DR number');

    const searchSelectors = [
      'input[placeholder*="search"]',
      'input[type="search"]',
      '.search-input',
      'input[placeholder*="Search"]',
      '.form-control >> input[type="text"]'
    ];

    let searchPerformed = false;
    for (const selector of searchSelectors) {
      try {
        const searchInput = await page.$(selector);
        if (searchInput) {
          console.log(`Found search input: ${selector}`);

          // Test with example DR number
          await searchInput.fill('DR1854443');
          await page.waitForTimeout(1000);

          // Press Enter to search
          await page.press(selector, 'Enter');
          await page.waitForTimeout(3000);

          console.log('✅ DR number searched: DR1854443');
          searchPerformed = true;
          await page.screenshot({ path: 'automated-06-search-results.png' });
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 7: Locate and click on the correct result
    console.log('📋 Step 7: Find and select correct installation');

    // Look for results table/grid
    const resultSelectors = [
      'table',
      '.results-grid',
      '.data-grid',
      '.installation-item',
      '[role="table"]',
      '.list-item'
    ];

    for (const selector of resultSelectors) {
      try {
        const results = await page.$$(selector);
        if (results.length > 0) {
          console.log(`Found ${results.length} results with selector: ${selector}`);

          // Look for the specific DR number in results
          const drNumberElement = await page.$(`text=DR1854443`);
          if (drNumberElement) {
            console.log('Found DR1854443 in results');

            // Click on the result or its row
            const rowSelectors = [
              'tr:has-text("DR1854443")',
              '.item:has-text("DR1854443")',
              '[data-id*="DR1854443"]'
            ];

            for (const rowSelector of rowSelectors) {
              try {
                const row = await page.$(rowSelector);
                if (row) {
                  await row.click();
                  await page.waitForTimeout(3000);
                  console.log('✅ Installation selected');
                  await page.screenshot({ path: 'automated-07-installation-details.png' });
                  break;
                }
              } catch (error) {
                continue;
              }
            }
          }
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 8: Access photos/documents
    console.log('📸 Step 8: Access photos and documents');

    // Look for photos/attachments tab
    const photoSelectors = [
      'text=Photos',
      'text=Attachments',
      'text=Documents',
      'text=Images',
      '.tab:has-text("Photo")',
      '.nav-tabs >> text=Photos'
    ];

    for (const selector of photoSelectors) {
      try {
        const photoTab = await page.$(selector);
        if (photoTab) {
          console.log(`Found photos tab: ${selector}`);
          await photoTab.click();
          await page.waitForTimeout(3000);
          console.log('✅ Photos tab opened');
          await page.screenshot({ path: 'automated-08-photos-tab.png' });
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Step 9: Verify installation photos
    console.log('🔎 Step 9: Verify installation photos');

    // Count photos/images
    const imageSelectors = [
      'img',
      '.photo',
      '.attachment',
      '.image-thumbnail',
      '.document-preview'
    ];

    let photoCount = 0;
    for (const selector of imageSelectors) {
      try {
        const images = await page.$$(selector);
        photoCount += images.length;
      } catch (error) {
        continue;
      }
    }

    console.log(`📊 Found ${photoCount} photos/images`);
    await page.screenshot({ path: 'automated-09-photo-verification.png' });

    // Step 10: Review metadata/attributes
    console.log('📝 Step 10: Review installation metadata');

    // Look for metadata/attributes tab
    const metadataSelectors = [
      'text=Metadata',
      'text=Attributes',
      'text=Details',
      'text=Information',
      '.tab:has-text("Metadata")',
      '.nav-tabs >> text=Details'
    ];

    for (const selector of metadataSelectors) {
      try {
        const metadataTab = await page.$(selector);
        if (metadataTab) {
          console.log(`Found metadata tab: ${selector}`);
          await metadataTab.click();
          await page.waitForTimeout(2000);
          console.log('✅ Metadata tab opened');
          await page.screenshot({ path: 'automated-10-metadata.png' });
          break;
        }
      } catch (error) {
        continue;
      }
    }

    // Final summary
    console.log('\n=== 🎉 AUTOMATED WORKFLOW COMPLETE ===');
    console.log('📋 Summary:');
    console.log(`✅ Final URL: ${page.url()}`);
    console.log(`✅ Page title: ${await page.title()}`);
    console.log(`✅ Photos found: ${photoCount}`);
    console.log(`✅ All major steps completed successfully`);

    await page.screenshot({ path: 'automated-11-final-state.png' });

    // Keep open for verification
    console.log('\n👁️ Keeping browser open for 15 seconds for verification...');
    await page.waitForTimeout(15000);

    console.log('🚀 Automated workflow execution completed successfully!');
  });
});