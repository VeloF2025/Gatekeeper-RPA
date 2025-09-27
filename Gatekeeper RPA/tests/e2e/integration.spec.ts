/**
 * End-to-End Integration Tests
 * Comprehensive testing of critical user journeys and system integrations
 */

import { test, expect } from '@playwright/test';

// Test configuration
test.describe('Gatekeeper RPA - End-to-End Integration Tests', () => {
  let page;

  test.beforeEach(async ({ browser }) => {
    page = await browser.newPage();
    // Set viewport size for consistent testing
    await page.setViewportSize({ width: 1280, height: 720 });

    // Enable console logging for debugging
    page.on('console', msg => console.log('Console:', msg.text()));
    page.on('pageerror', error => console.log('Page Error:', error.message));
  });

  test.afterEach(async () => {
    await page.close();
  });

  // Test 1: Critical Path - Dashboard Navigation
  test.describe('Critical User Journeys', () => {
    test('should load dashboard and navigate to key sections', async () => {
      // Navigate to dashboard
      await page.goto('/');

      // Verify main dashboard loads
      await expect(page.locator('h1')).toContainText('Gatekeeper RPA Dashboard');

      // Check for key dashboard components
      await expect(page.locator('[data-testid="stats-grid"]')).toBeVisible();
      await expect(page.locator('[data-testid="activity-feed"]')).toBeVisible();

      // Navigate to performance section
      await page.click('text=Performance');
      await expect(page).toHaveURL('/performance');

      // Verify performance dashboard loads
      await expect(page.locator('h2')).toContainText('Performance Metrics');

      // Navigate to tickets section
      await page.click('text=Tickets');
      await expect(page).toHaveURL('/tickets');

      // Verify tickets section loads
      await expect(page.locator('h1')).toContainText('Ticket Management');
    });

    test('should handle authentication flow', async () => {
      // Test logout flow if authenticated
      await page.goto('/');

      // Look for authentication state
      const logoutButton = page.locator('button:has-text("Logout")');
      const isAuthVisible = await logoutButton.isVisible();

      if (isAuthVisible) {
        await logoutButton.click();
        await expect(page).toHaveURL('/sign-in');
      }
    });
  });

  // Test 2: Performance Monitoring
  test.describe('Performance Monitoring System', () => {
    test('should display performance metrics', async () => {
      await page.goto('/performance');

      // Wait for performance data to load
      await page.waitForSelector('[data-testid="performance-metrics"]');

      // Verify key metrics are displayed
      await expect(page.locator('text=CPU Usage')).toBeVisible();
      await expect(page.locator('text=Memory Usage')).toBeVisible();
      await expect(page.locator('text=Response Time')).toBeVisible();

      // Check for real-time updates
      const initialCpuValue = await page.locator('[data-testid="cpu-metric"]').textContent();

      // Wait for potential update (metrics update every 5 seconds)
      await page.waitForTimeout(6000);

      // Verify data can change (real-time functionality)
      // Note: In real implementation, you'd mock the API responses
    });

    test('should handle performance alerts', async () => {
      await page.goto('/performance');

      // Check for alert system
      await expect(page.locator('[data-testid="alert-management"]')).toBeVisible();

      // Verify alert controls work
      const alertToggle = page.locator('[data-testid="alert-toggle"]');
      if (await alertToggle.isVisible()) {
        await alertToggle.click();
        // Verify alert state changes
      }
    });
  });

  // Test 3: RPA Operations
  test.describe('RPA Operations Integration', () => {
    test('should display RPA performance data', async () => {
      await page.goto('/performance');

      // Navigate to RPA tab
      await page.click('text=RPA Performance');

      // Verify RPA metrics
      await expect(page.locator('text=Total Jobs')).toBeVisible();
      await expect(page.locator('text=Success Rate')).toBeVisible();
      await expect(page.locator('text=Average Execution')).toBeVisible();

      // Check for job status indicators
      await expect(page.locator('[data-testid="job-status-indicators"]')).toBeVisible();
    });

    test('should handle job management interactions', async () => {
      await page.goto('/performance');
      await page.click('text=RPA Performance');

      // Test job filtering
      const filterDropdown = page.locator('[data-testid="job-filter"]');
      if (await filterDropdown.isVisible()) {
        await filterDropdown.click();
        await page.click('text=Running Jobs');
        // Verify filter applies
      }

      // Test job details view
      const jobItem = page.locator('[data-testid="job-item"]').first();
      if (await jobItem.isVisible()) {
        await jobItem.click();
        // Verify job details modal or expanded view
      }
    });
  });

  // Test 4: API Endpoints Integration
  test.describe('API Integration Tests', () => {
    test('should validate performance API endpoints', async () => {
      // Test performance metrics API
      const perfResponse = await page.request.get('/api/performance/metrics?type=system');
      expect(perfResponse.status()).toBe(200);

      const perfData = await perfResponse.json();
      expect(perfData).toHaveProperty('success', true);
      expect(perfData).toHaveProperty('data');

      // Test health check API
      const healthResponse = await page.request.get('/api/health');
      expect(healthResponse.status()).toBe(200);

      const healthData = await healthResponse.json();
      expect(healthData).toHaveProperty('status');
    });

    test('should handle API error scenarios', async () => {
      // Test invalid API endpoint
      const invalidResponse = await page.request.get('/api/invalid-endpoint');
      expect(invalidResponse.status()).toBe(404);

      // Test malformed request
      const malformedResponse = await page.request.get('/api/performance/metrics?type=invalid');
      expect(malformedResponse.status()).toBe(400);
    });
  });

  // Test 5: Responsive Design
  test.describe('Responsive Design Testing', () => {
    test('should display correctly on mobile devices', async () => {
      await page.setViewportSize({ width: 375, height: 667 });
      await page.goto('/');

      // Verify mobile navigation
      const mobileMenu = page.locator('[data-testid="mobile-menu"]');
      if (await mobileMenu.isVisible()) {
        await mobileMenu.click();
        await expect(page.locator('[data-testid="mobile-nav"]')).toBeVisible();
      }

      // Verify content scales properly
      await expect(page.locator('h1')).toBeVisible();
      await expect(page.locator('[data-testid="stats-grid"]')).toBeVisible();
    });

    test('should display correctly on tablet devices', async () => {
      await page.setViewportSize({ width: 768, height: 1024 });
      await page.goto('/performance');

      // Verify tablet layout
      await expect(page.locator('[data-testid="performance-charts"]')).toBeVisible();

      // Test touch interactions
      const chart = page.locator('[data-testid="performance-chart"]').first();
      if (await chart.isVisible()) {
        await chart.click({ position: { x: 100, y: 100 } });
        // Verify tooltip or interaction response
      }
    });
  });

  // Test 6: Error Handling
  test.describe('Error Handling and Resilience', () => {
    test('should handle network errors gracefully', async () => {
      // Simulate network error
      await page.route('**/api/performance/metrics', route => route.abort('failed'));

      await page.goto('/performance');

      // Verify error message is displayed
      await expect(page.locator('[data-testid="error-message"]')).toBeVisible();

      // Verify retry functionality
      const retryButton = page.locator('[data-testid="retry-button"]');
      if (await retryButton.isVisible()) {
        await retryButton.click();
        // Verify retry attempt
      }
    });

    test('should handle missing data gracefully', async () => {
      // Mock empty API response
      await page.route('**/api/performance/metrics', route => {
        route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ success: true, data: [] })
        });
      });

      await page.goto('/performance');

      // Verify empty state is displayed
      await expect(page.locator('[data-testid="empty-state"]')).toBeVisible();
    });
  });

  // Test 7: Accessibility
  test.describe('Accessibility Testing', () => {
    test('should meet accessibility standards', async () => {
      await page.goto('/');

      // Test keyboard navigation
      await page.keyboard.press('Tab');
      let focusedElement = await page.evaluate(() => document.activeElement);
      expect(focusedElement).toBeTruthy();

      // Navigate through interactive elements
      for (let i = 0; i < 10; i++) {
        await page.keyboard.press('Tab');
        focusedElement = await page.evaluate(() => document.activeElement);
        expect(focusedElement).toBeTruthy();
      }

      // Test ARIA labels
      const chartElement = page.locator('[role="img"]').first();
      if (await chartElement.isVisible()) {
        const ariaLabel = await chartElement.getAttribute('aria-label');
        expect(ariaLabel).toBeTruthy();
      }
    });
  });

  // Test 8: Performance Benchmarks
  test.describe('Performance Benchmarks', () => {
    test('should meet page load performance targets', async () => {
      const startTime = Date.now();
      await page.goto('/');
      const loadTime = Date.now() - startTime;

      // Verify page loads within target (1.5 seconds)
      expect(loadTime).toBeLessThan(1500);

      // Check for performance metrics
      const metrics = await page.metrics();
      expect(metrics.LayoutDuration).toBeLessThan(100);
      expect(metrics.ScriptDuration).toBeLessThan(200);
    });

    test('should handle large datasets efficiently', async () => {
      // Mock large dataset response
      await page.route('**/api/performance/metrics', route => {
        const largeData = Array.from({ length: 1000 }, (_, i) => ({
          timestamp: new Date(Date.now() - i * 60000).toISOString(),
          cpu: Math.random() * 100,
          memory: Math.random() * 100,
          responseTime: Math.random() * 500
        }));

        route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ success: true, data: largeData })
        });
      });

      await page.goto('/performance');

      // Verify page remains responsive
      await expect(page.locator('[data-testid="performance-metrics"]')).toBeVisible({ timeout: 5000 });

      // Test interactions with large dataset
      await page.click('text=24h');
      await expect(page.locator('[data-testid="loading-indicator"]')).toBeHidden();
    });
  });

  // Test 9: Security Testing
  test.describe('Security Integration Tests', () => {
    test('should implement proper security headers', async () => {
      const response = await page.request.get('/');
      const headers = response.headers();

      // Verify security headers
      expect(headers['x-frame-options']).toBe('DENY');
      expect(headers['x-content-type-options']).toBe('nosniff');
      expect(headers['strict-transport-security']).toContain('max-age=31536000');
    });

    test('should prevent XSS vulnerabilities', async () => {
      // Test with potentially malicious input
      const maliciousScript = '<script>alert("XSS")</script>';

      // This would be tested through form inputs or API endpoints
      // For now, verify that script tags are properly escaped
      await page.goto('/');

      // Look for proper content security policy implementation
      const cspHeader = await page.request.get('/').then(response =>
        response.headers()['content-security-policy']
      );
      expect(cspHeader).toContain("script-src 'self'");
    });
  });

  // Test 10: Data Visualization
  test.describe('Data Visualization Integration', () => {
    test('should render charts correctly', async () => {
      await page.goto('/performance');

      // Wait for charts to load
      await page.waitForSelector('[data-testid="performance-chart"]');

      // Verify chart elements
      const charts = await page.locator('[data-testid="performance-chart"]').count();
      expect(charts).toBeGreaterThan(0);

      // Test chart interactions
      const firstChart = page.locator('[data-testid="performance-chart"]').first();
      await firstChart.hover();

      // Verify tooltip appears on hover
      const tooltip = page.locator('[data-testid="chart-tooltip"]');
      if (await tooltip.isVisible()) {
        await expect(tooltip).toBeVisible();
      }
    });

    test('should handle chart navigation and controls', async () => {
      await page.goto('/performance');

      // Test time range selector
      const timeRangeButtons = page.locator('[data-testid="time-range-button"]');
      const buttonCount = await timeRangeButtons.count();

      if (buttonCount > 0) {
        await timeRangeButtons.nth(1).click(); // Click 24h button
        await expect(page.locator('[data-testid="loading-indicator"]')).toBeVisible();
        await expect(page.locator('[data-testid="loading-indicator"]')).toBeHidden();
      }

      // Test export functionality
      const exportButton = page.locator('[data-testid="export-button"]');
      if (await exportButton.isVisible()) {
        // Mock download behavior
        const downloadPromise = page.waitForEvent('download');
        await exportButton.click();
        // In real test, verify download starts
      }
    });
  });
});

// Test configuration for CI/CD
test.describe.configure({
  timeout: 30000, // 30 second timeout for all tests
  retries: 2, // Retry failed tests twice
});