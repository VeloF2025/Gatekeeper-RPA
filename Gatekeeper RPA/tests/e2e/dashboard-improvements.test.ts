/**
 * Dashboard Improvements Validation Test
 *
 * Comprehensive test to verify the dashboard modernization, charts, metrics,
 * and overall UI improvements in the Gatekeeper RPA system.
 */

import { test, expect } from '@playwright/test';

// Test configuration with headed mode for visual validation
test.use({
  headless: false,
  viewport: { width: 1280, height: 720 },
  launchOptions: {
    slowMo: 200,
  },
});

test.describe('Gatekeeper RPA - Dashboard Improvements Validation', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
  });

  test('DASHBOARD-001: Homepage Modern Design', async ({ page }) => {
    console.log('Testing homepage modern design...');

    // Check main heading and title
    await expect(page).toHaveTitle(/Gatekeeper RPA/);
    const mainHeading = page.locator('h1');
    await expect(mainHeading).toBeVisible();
    console.log('Main heading visible:', await mainHeading.isVisible());

    // Check for modern styling elements
    const bodyStyles = await page.evaluate(() => {
      const body = document.body;
      const computed = window.getComputedStyle(body);
      return {
        backgroundColor: computed.backgroundColor,
        backgroundImage: computed.backgroundImage,
        fontFamily: computed.fontFamily,
      };
    });
    console.log('Body background:', bodyStyles);

    // Take comprehensive screenshots
    await page.screenshot({ path: 'dashboard-modern-homepage.png', fullPage: true });

    // Check for gradient backgrounds and modern styling
    const hasGradient = bodyStyles.backgroundImage.includes('gradient');
    console.log('Has gradient background:', hasGradient);
  });

  test('DASHBOARD-002: Navigation and Menu Improvements', async ({ page }) => {
    console.log('Testing navigation improvements...');

    // Test navigation links
    const navLinks = page.locator('a[href]');
    const linkCount = await navLinks.count();
    console.log('Navigation links found:', linkCount);

    // Test dashboard navigation
    const dashboardLink = page.locator('a[href="/dashboard"], text=Dashboard');
    if (await dashboardLink.isVisible()) {
      await dashboardLink.click();
      await page.waitForLoadState('networkidle');
      await page.screenshot({ path: 'dashboard-page-navigation.png' });
      console.log('Dashboard navigation successful');

      // Navigate back to homepage
      await page.goto('/');
      await page.waitForLoadState('networkidle');
    }

    // Test tickets navigation
    const ticketsLink = page.locator('a[href="/tickets"], text=Tickets');
    if (await ticketsLink.isVisible()) {
      await ticketsLink.click();
      await page.waitForLoadState('networkidle');
      await page.screenshot({ path: 'tickets-page-navigation.png' });
      console.log('Tickets navigation successful');
    }
  });

  test('DASHBOARD-003: Metrics and Data Visualization', async ({ page }) => {
    console.log('Testing metrics and data visualization...');

    // Look for metric/statistic cards
    const statCards = page.locator('.stat, .metric, .card, [data-testid*="stat"]');
    const statCount = await statCards.count();
    console.log('Statistics cards found:', statCount);

    if (statCount > 0) {
      // Take screenshot of metrics section
      await statCards.first().screenshot({ path: 'dashboard-metrics-detail.png' });

      // Check for numbers/data in stat cards
      for (let i = 0; i < Math.min(statCount, 3); i++) {
        const card = statCards.nth(i);
        const cardText = await card.textContent();
        console.log(`Stat card ${i} content:`, cardText?.trim());
      }
    }

    // Look for charts or graphs
    const chartElements = page.locator('canvas, svg, .chart, .graph');
    const chartCount = await chartElements.count();
    console.log('Chart elements found:', chartCount);

    if (chartCount > 0) {
      await chartElements.first().screenshot({ path: 'dashboard-chart-detail.png' });
    }

    await page.screenshot({ path: 'dashboard-metrics-overview.png' });
  });

  test('DASHBOARD-004: Responsive Design Verification', async ({ page }) => {
    console.log('Testing responsive design across devices...');

    const viewports = [
      { width: 1920, height: 1080, name: 'Desktop' },
      { width: 768, height: 1024, name: 'Tablet' },
      { width: 375, height: 812, name: 'Mobile' }
    ];

    for (const viewport of viewports) {
      console.log(`Testing ${viewport.name} viewport...`);
      await page.setViewportSize(viewport);
      await page.waitForLoadState('networkidle');
      await page.waitForTimeout(500);

      // Check main elements are visible and properly laid out
      const mainHeading = page.locator('h1');
      const isVisible = await mainHeading.isVisible();
      console.log(`${viewport.name} - Main heading visible:`, isVisible);

      // Check navigation accessibility
      const navElements = page.locator('nav, a[href], button');
      const navCount = await navElements.count();
      console.log(`${viewport.name} - Navigation elements:`, navCount);

      await page.screenshot({ path: `dashboard-responsive-${viewport.name}.png`, fullPage: true });
    }
  });

  test('DASHBOARD-005: Interactive Elements and Micro-interactions', async ({ page }) => {
    console.log('Testing interactive elements and micro-interactions...');

    // Test button hover effects
    const buttons = page.locator('button, a[href]');
    const buttonCount = await buttons.count();
    console.log('Interactive elements found:', buttonCount);

    for (let i = 0; i < Math.min(buttonCount, 5); i++) {
      const button = buttons.nth(i);
      const buttonText = await button.textContent();

      try {
        // Test hover state
        await button.hover();
        await page.waitForTimeout(300);

        // Check for hover effects
        const beforeHover = await button.evaluate(el => {
          const styles = window.getComputedStyle(el);
          return {
            backgroundColor: styles.backgroundColor,
            transform: styles.transform,
            boxShadow: styles.boxShadow,
          };
        });

        console.log(`Button "${buttonText?.trim()}" hover effects:`, beforeHover);

        await page.screenshot({ path: `dashboard-button-hover-${i}.png` });
      } catch (error) {
        console.log(`Error testing button ${i}:`, error.message);
      }
    }

    // Test focus states for accessibility
    await page.keyboard.press('Tab');
    await page.waitForTimeout(300);

    const focusedElement = page.locator(':focus');
    const hasFocus = await focusedElement.isVisible();
    console.log('Focus state working:', hasFocus);

    if (hasFocus) {
      await focusedElement.screenshot({ path: 'dashboard-focus-state.png' });
    }
  });

  test('DASHBOARD-006: Loading States and Animations', async ({ page }) => {
    console.log('Testing loading states and animations...');

    // Test page reload with loading states
    await page.reload();

    // Look for loading indicators
    const loadingElements = page.locator('[data-testid="loading"], .loading, .skeleton, [aria-busy="true"]');
    const loadingCount = await loadingElements.count();
    console.log('Loading elements found:', loadingCount);

    // Wait for full load
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(1000);

    // Check for smooth transitions
    const transitionElements = page.locator('[style*="transition"], [class*="transition"]');
    const transitionCount = await transitionElements.count();
    console.log('Elements with transitions:', transitionCount);

    await page.screenshot({ path: 'dashboard-loading-complete.png' });
  });

  test('DASHBOARD-007: Performance Optimization', async ({ page }) => {
    console.log('Testing performance optimization...');

    // Collect performance metrics
    const metrics = await page.evaluate(() => {
      const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
      const paintEntries = performance.getEntriesByType('paint');

      return {
        domContentLoaded: navigation.domContentLoadedEventEnd - navigation.domContentLoadedEventStart,
        loadComplete: navigation.loadEventEnd - navigation.loadEventStart,
        firstPaint: paintEntries.find(p => p.name === 'first-paint')?.startTime || 0,
        firstContentfulPaint: paintEntries.find(p => p.name === 'first-contentful-paint')?.startTime || 0,
        totalResources: performance.getEntriesByType('resource').length,
      };
    });

    console.log('Performance metrics:', metrics);

    // Check for large resources that might affect performance
    const largeResources = await page.evaluate(() => {
      const resources = performance.getEntriesByType('resource');
      return resources
        .filter(r => (r as any).transferSize > 50000) // Larger than 50KB
        .map(r => ({ name: r.name, size: (r as any).transferSize }));
    });

    console.log('Large resources:', largeResources);

    await page.screenshot({ path: 'dashboard-performance-analysis.png' });
  });

  test('DASHBOARD-008: Theme and Color Scheme', async ({ page }) => {
    console.log('Testing theme and color scheme...');

    // Analyze color scheme
    const colorAnalysis = await page.evaluate(() => {
      const body = document.body;
      const computed = window.getComputedStyle(body);

      return {
        backgroundColor: computed.backgroundColor,
        color: computed.color,
        primaryColor: computed.getPropertyValue('--primary') || 'not set',
        secondaryColor: computed.getPropertyValue('--secondary') || 'not set',
        accentColor: computed.getPropertyValue('--accent') || 'not set',
      };
    });

    console.log('Color scheme analysis:', colorAnalysis);

    // Check for professional color usage
    const hasProfessionalColors = colorAnalysis.backgroundColor.includes('rgb') &&
      !colorAnalysis.backgroundColor.includes('255, 255, 255'); // Not pure white
    console.log('Uses professional colors:', hasProfessionalColors);

    // Check for CSS custom properties
    const cssVars = await page.evaluate(() => {
      const root = document.documentElement;
      const styles = window.getComputedStyle(root);
      const variables: Record<string, string> = {};

      for (let i = 0; i < styles.length; i++) {
        const prop = styles[i];
        if (prop.startsWith('--')) {
          variables[prop] = styles.getPropertyValue(prop);
        }
      }

      return variables;
    });

    console.log('CSS variables found:', Object.keys(cssVars).length);

    await page.screenshot({ path: 'dashboard-theme-analysis.png' });
  });

});