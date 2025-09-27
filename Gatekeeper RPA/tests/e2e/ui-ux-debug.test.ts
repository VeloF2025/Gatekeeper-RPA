/**
 * UI/UX Debugging Tests for Gatekeeper RPA System
 *
 * Comprehensive testing of user interface components, accessibility,
 * responsiveness, and user experience in headed mode for debugging.
 *
 * Created using Archon UI/UX optimization agent with NLNH principles.
 */

import { test, expect } from '@playwright/test';

// Test configuration with headed mode for debugging
test.use({
  headless: false,
  viewport: { width: 1280, height: 720 },
  // Slow down for debugging
  launchOptions: {
    slowMo: 100,
  },
});

test.describe('Gatekeeper RPA - UI/UX Debugging Suite', () => {

  test.beforeEach(async ({ page }) => {
    // Navigate to the application (adjust port if needed)
    await page.goto('http://localhost:3000');
    await page.waitForLoadState('networkidle');
  });

  test('DEBUG-001: Homepage Layout and Visual Structure', async ({ page }) => {
    // Test homepage loads correctly
    await expect(page).toHaveTitle(/Gatekeeper RPA/);

    // Check main heading
    const mainHeading = page.locator('h1');
    await expect(mainHeading).toBeVisible();
    await expect(mainHeading).toContainText('Gatekeeper RPA');

    // Check navigation elements
    const dashboardLink = page.locator('a[href="/dashboard"]');
    const ticketsLink = page.locator('a[href="/tickets"]');

    console.log('Dashboard link visible:', await dashboardLink.isVisible());
    console.log('Tickets link visible:', await ticketsLink.isVisible());

    // Check feature cards
    const featureCards = page.locator('.grid > .card');
    const cardCount = await featureCards.count();
    console.log('Feature cards found:', cardCount);

    // Verify cards have expected content
    if (cardCount > 0) {
      const firstCard = featureCards.first();
      const cardTitle = await firstCard.locator('.card-title').textContent();
      console.log('First card title:', cardTitle);
    }

    // Take screenshot for visual debugging
    await page.screenshot({ path: 'debug-homepage-layout.png', fullPage: true });
  });

  test('DEBUG-002: Component Accessibility Testing', async ({ page }) => {
    // Test accessibility basics
    await expect(page.locator('html')).toHaveAttribute('lang', 'en');

    // Check for skip to main content link (accessibility feature)
    const skipLink = page.locator('a[href="#main-content"]');
    console.log('Skip link exists:', await skipLink.isVisible());

    // Test button accessibility
    const buttons = page.locator('button');
    const buttonCount = await buttons.count();
    console.log('Buttons found:', buttonCount);

    // Test semantic HTML structure
    const mainElement = page.locator('main');
    const navElement = page.locator('nav');
    const headerElement = page.locator('header');

    console.log('Semantic elements - Main:', await mainElement.count(),
                'Nav:', await navElement.count(),
                'Header:', await headerElement.count());

    // Check for ARIA labels
    const elementsWithAria = page.locator('[aria-label], [aria-describedby], [aria-labelledby]');
    const ariaCount = await elementsWithAria.count();
    console.log('Elements with ARIA attributes:', ariaCount);

    await page.screenshot({ path: 'debug-accessibility.png', fullPage: true });
  });

  test('DEBUG-003: Responsive Design Testing', async ({ page }) => {
    // Test different viewport sizes
    const viewports = [
      { width: 1920, height: 1080, name: 'Desktop' },
      { width: 768, height: 1024, name: 'Tablet' },
      { width: 375, height: 812, name: 'Mobile' }
    ];

    for (const viewport of viewports) {
      await page.setViewportSize(viewport);
      await page.waitForLoadState('networkidle');

      // Test main elements are visible
      const mainHeading = page.locator('h1');
      const isVisible = await mainHeading.isVisible();
      const isInViewport = await mainHeading.isVisible();

      console.log(`${viewport.name} viewport - Heading visible:`, isVisible, 'in viewport:', isInViewport);

      // Test navigation
      const navButtons = page.locator('button, a[href]');
      const navCount = await navButtons.count();
      console.log(`${viewport.name} - Navigation elements:`, navCount);

      await page.screenshot({ path: `debug-responsive-${viewport.name}.png` });
    }
  });

  test('DEBUG-004: Interactive Elements and States', async ({ page }) => {
    // Test button hover states
    const buttons = page.locator('button');
    const buttonCount = await buttons.count();

    console.log('Testing button interactions for', buttonCount, 'buttons');

    for (let i = 0; i < Math.min(buttonCount, 5); i++) {
      const button = buttons.nth(i);
      const buttonText = await button.textContent();

      try {
        // Hover over button
        await button.hover();
        await page.waitForTimeout(200);

        // Check if button has hover effect (class change or style)
        const classNames = await button.getAttribute('class');
        console.log(`Button "${buttonText?.trim()}" hover - classes:`, classNames);

        await page.screenshot({ path: `debug-button-hover-${i}.png` });
      } catch (error) {
        console.log(`Error testing button ${i}:`, error.message);
      }
    }

    // Test focus states
    await page.keyboard.press('Tab');
    await page.waitForTimeout(300);

    const focusedElement = page.locator(':focus');
    const isFocused = await focusedElement.isVisible();
    console.log('Element focused after Tab:', isFocused);

    if (isFocused) {
      const focusedTag = await focusedElement.evaluate(el => el.tagName);
      console.log('Focused element tag:', focusedTag);
    }
  });

  test('DEBUG-005: Loading States and Error Handling', async ({ page }) => {
    // Test loading indicators
    await page.reload();

    // Look for loading spinners or skeleton loaders
    const loadingElements = page.locator('[data-testid="loading"], .loading, .skeleton, [aria-busy="true"]');
    const loadingCount = await loadingElements.count();
    console.log('Loading elements found:', loadingCount);

    // Wait for page to fully load
    await page.waitForLoadState('networkidle');

    // Test error states (if any)
    const errorElements = page.locator('[role="alert"], .error, .alert-error');
    const errorCount = await errorElements.count();
    console.log('Error elements found:', errorCount);

    if (errorCount > 0) {
      const firstError = await errorElements.first().textContent();
      console.log('First error message:', firstError);
    }

    // Test console errors
    page.on('console', msg => {
      if (msg.type() === 'error') {
        console.log('Console error:', msg.text());
      }
    });

    await page.screenshot({ path: 'debug-loading-states.png' });
  });

  test('DEBUG-006: System Status Components', async ({ page }) => {
    // Look for system status indicators
    const statusIndicators = page.locator('[data-testid*="status"], .status, .indicator');
    const statusCount = await statusIndicators.count();
    console.log('Status indicators found:', statusCount);

    // Test security indicators
    const securityElements = page.locator('[data-testid*="security"], .security, .badge');
    const securityCount = await securityElements.count();
    console.log('Security elements found:', securityCount);

    // Test metrics/statistics display
    const statElements = page.locator('[data-testid*="stat"], .stat, .metric');
    const statCount = await statElements.count();
    console.log('Statistics elements found:', statCount);

    // Check if system status is displayed
    if (statusCount > 0) {
      const firstStatus = await statusIndicators.first().textContent();
      console.log('Status indicator content:', firstStatus);
    }

    await page.screenshot({ path: 'debug-system-status.png' });
  });

  test('DEBUG-007: Navigation and Routing', async ({ page }) => {
    // Test navigation links
    const links = page.locator('a[href]');
    const linkCount = await links.count();

    console.log('Navigation links found:', linkCount);

    // Test first few navigation links
    for (let i = 0; i < Math.min(linkCount, 3); i++) {
      const link = links.nth(i);
      const href = await link.getAttribute('href');
      const linkText = await link.textContent();

      console.log(`Testing link: "${linkText}" -> ${href}`);

      if (href && !href.startsWith('http') && !href.startsWith('#')) {
        try {
          // Test navigation
          await Promise.all([
            page.waitForNavigation({ waitUntil: 'networkidle' }),
            link.click()
          ]);

          console.log(`Successfully navigated to: ${page.url()}`);

          // Take screenshot of destination
          await page.screenshot({ path: `debug-navigation-${href.replace('/', '-')}.png` });

          // Navigate back
          await page.goBack();
          await page.waitForLoadState('networkidle');
        } catch (error) {
          console.log(`Navigation error for ${href}:`, error.message);
        }
      }
    }
  });

  test('DEBUG-008: Console and Network Analysis', async ({ page }) => {
    // Monitor console logs
    const consoleLogs: string[] = [];
    page.on('console', msg => {
      consoleLogs.push(`[${msg.type()}] ${msg.text()}`);
    });

    // Monitor network requests
    const networkRequests: string[] = [];
    page.on('request', request => {
      networkRequests.push(`${request.method()} ${request.url()}`);
    });

    // Reload page and monitor
    await page.reload();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);

    console.log('Console logs collected:', consoleLogs.length);
    consoleLogs.slice(0, 10).forEach(log => console.log('  ', log));

    console.log('Network requests collected:', networkRequests.length);
    networkRequests.slice(0, 10).forEach(req => console.log('  ', req));

    // Check for failed requests
    page.on('requestfailed', request => {
      console.log('Failed request:', request.url(), request.failure());
    });

    await page.screenshot({ path: 'debug-console-network.png' });
  });

  test('DEBUG-009: Performance and Loading Metrics', async ({ page }) => {
    // Performance metrics
    const metrics = await page.evaluate(() => {
      const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
      return {
        domContentLoaded: navigation.domContentLoadedEventEnd - navigation.domContentLoadedEventStart,
        loadComplete: navigation.loadEventEnd - navigation.loadEventStart,
        firstPaint: performance.getEntriesByType('paint')[0]?.startTime || 0,
        resources: performance.getEntriesByType('resource').length,
      };
    });

    console.log('Performance metrics:', metrics);

    // Check for large resources
    const largeResources = await page.evaluate(() => {
      const resources = performance.getEntriesByType('resource');
      return resources
        .filter(r => r.transferSize > 100000) // Larger than 100KB
        .map(r => ({ name: r.name, size: r.transferSize }));
    });

    console.log('Large resources:', largeResources);

    await page.screenshot({ path: 'debug-performance.png' });
  });

  test('DEBUG-010: Theme and Styling Analysis', async ({ page }) => {
    // Test color scheme
    const bodyStyles = await page.evaluate(() => {
      const body = document.body;
      const computed = window.getComputedStyle(body);
      return {
        backgroundColor: computed.backgroundColor,
        color: computed.color,
        fontFamily: computed.fontFamily,
        fontSize: computed.fontSize,
      };
    });

    console.log('Body styles:', bodyStyles);

    // Test for theme toggle (if exists)
    const themeToggle = page.locator('[data-testid="theme-toggle"], .theme-toggle, button[aria-label*="theme"]');
    const hasThemeToggle = await themeToggle.isVisible();
    console.log('Theme toggle found:', hasThemeToggle);

    // Check CSS variables
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
    Object.entries(cssVars).slice(0, 10).forEach(([key, value]) => {
      console.log(`  ${key}: ${value}`);
    });

    await page.screenshot({ path: 'debug-theme-styling.png' });
  });

});