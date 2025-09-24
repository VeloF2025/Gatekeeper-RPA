# 1Map RPA-Only Integration Guide

## Overview
This document outlines the RPA-only approach for integrating with 1Map, as no API access is available. The system will use Playwright to automate web interactions for data extraction and audit processing.

## 🎯 RPA Strategy

### Key Challenges
- **No API Access**: All interactions must be through the web interface
- **Session Management**: Handle login sessions and authentication
- **Data Extraction**: Parse HTML and extract structured data
- **Reliability**: Handle UI changes and website downtime
- **Performance**: Optimize for speed and efficiency

### Solution Approach
- **Modular Design**: Break down RPA into reusable components
- **Robust Selectors**: Use multiple selector strategies
- **Error Handling**: Comprehensive retry and recovery mechanisms
- **Monitoring**: Track performance and detect issues
- **Fallback**: Alternative strategies for data extraction

## 🏗️ RPA Architecture

### Core Components

#### 1. Authentication Manager
```typescript
class AuthenticationManager {
  private browser: Browser;
  private page: Page;
  private credentials: Credentials;

  async login(): Promise<void> {
    try {
      // Launch browser with stealth mode
      this.browser = await chromium.launch({
        headless: true,
        args: [
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-dev-shm-usage',
          '--disable-gpu'
        ]
      });

      this.page = await this.browser.newPage();

      // Set user agent to avoid detection
      await this.page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36');

      // Navigate to login page
      await this.page.goto('https://1map.co.za/login');

      // Wait for page to load
      await this.page.waitForSelector('#username', { timeout: 30000 });

      // Enter credentials
      await this.page.fill('#username', this.credentials.username);
      await this.page.fill('#password', this.credentials.password);

      // Handle potential CAPTCHA
      await this.handleCaptcha();

      // Click login button
      await this.page.click('#login-button');

      // Wait for successful login
      await this.page.waitForURL('**/dashboard', { timeout: 30000 });

      // Verify login success
      const isLoggedIn = await this.page.locator('.user-profile').isVisible();
      if (!isLoggedIn) {
        throw new Error('Login verification failed');
      }

      // Save session cookies for future use
      await this.saveSession();

    } catch (error) {
      await this.handleLoginError(error);
    }
  }

  async handleCaptcha(): Promise<void> {
    // Check for CAPTCHA and handle accordingly
    const captchaVisible = await this.page.locator('#captcha-container').isVisible();
    if (captchaVisible) {
      // Log CAPTCHA presence for manual intervention
      console.log('CAPTCHA detected - manual intervention required');
      // Implement CAPTCHA solving service integration if available
    }
  }

  async saveSession(): Promise<void> {
    const cookies = await this.page.context().cookies();
    // Save cookies to secure storage for session persistence
  }
}
```

#### 2. Navigation Manager
```typescript
class NavigationManager {
  private page: Page;

  async navigateToHomeInstallations(): Promise<void> {
    try {
      // Wait for main navigation to load
      await this.page.waitForSelector('.main-nav', { timeout: 30000 });

      // Click on 1Map App menu
      await this.page.click('text=1Map App');

      // Wait for submenu to appear
      await this.page.waitForSelector('.submenu', { timeout: 10000 });

      // Click on Home Signups & Installations
      await this.page.click('text=Home Signups & Installations');

      // Wait for page to load
      await this.page.waitForURL('**/home-signups-installations', { timeout: 30000 });

      // Verify we're on the correct page
      const pageTitle = await this.page.locator('h1').textContent();
      if (!pageTitle?.includes('Home Signups')) {
        throw new Error('Navigation verification failed');
      }

    } catch (error) {
      await this.handleNavigationError(error);
    }
  }

  async searchDRNumber(drNumber: string): Promise<void> {
    try {
      // Wait for search interface to load
      await this.page.waitForSelector('#search-input', { timeout: 30000 });

      // Clear search input
      await this.page.fill('#search-input', '');

      // Enter DR number
      await this.page.fill('#search-input', drNumber);

      // Click search button
      await this.page.click('#search-button');

      // Wait for search results
      await this.page.waitForSelector('.search-results', { timeout: 30000 });

      // Verify search results contain the DR number
      const resultText = await this.page.locator('.search-results').textContent();
      if (!resultText?.includes(drNumber)) {
        throw new Error(`DR number ${drNumber} not found in search results`);
      }

    } catch (error) {
      await this.handleSearchError(error, drNumber);
    }
  }
}
```

#### 3. Data Extraction Manager
```typescript
class DataExtractionManager {
  private page: Page;

  async extractInstallationData(): Promise<InstallationData> {
    try {
      const data: InstallationData = {
        propertyId: '',
        jobId: '',
        address: '',
        status: '',
        lastModified: new Date(),
        photos: []
      };

      // Extract property ID
      data.propertyId = await this.extractField('property-id');

      // Extract job ID
      data.jobId = await this.extractField('job-id');

      // Extract address
      data.address = await this.extractAddress();

      // Extract status
      data.status = await this.extractStatus();

      // Extract last modified date
      data.lastModified = await this.extractLastModified();

      // Extract photos
      data.photos = await this.extractPhotos();

      return data;

    } catch (error) {
      throw new Error(`Data extraction failed: ${error.message}`);
    }
  }

  private async extractField(fieldId: string): Promise<string> {
    const selectors = [
      `#${fieldId}`,
      `[data-field="${fieldId}"]`,
      `.field-${fieldId}`,
      `label:has-text("${fieldId}") + input`,
      `th:has-text("${fieldId}") + td`
    ];

    for (const selector of selectors) {
      try {
        const element = await this.page.$(selector);
        if (element) {
          return await element.textContent() || '';
        }
      } catch (error) {
        // Continue to next selector
      }
    }

    throw new Error(`Field ${fieldId} not found with any selector`);
  }

  private async extractAddress(): Promise<string> {
    try {
      // Try multiple address selectors
      const addressSelectors = [
        '.address-field',
        '[data-field="address"]',
        'label:has-text("Address") + div',
        '.property-address'
      ];

      for (const selector of addressSelectors) {
        const address = await this.page.locator(selector).textContent();
        if (address && address.trim()) {
          return address.trim();
        }
      }

      throw new Error('Address not found');
    } catch (error) {
      throw new Error(`Address extraction failed: ${error.message}`);
    }
  }

  private async extractPhotos(): Promise<Photo[]> {
    const photos: Photo[] = [];

    try {
      // Wait for photos to load
      await this.page.waitForSelector('.photo-container', { timeout: 30000 });

      // Get all photo elements
      const photoElements = await this.page.$$('.photo-item');

      for (let i = 0; i < photoElements.length; i++) {
        const photoElement = photoElements[i];

        const photo: Photo = {
          id: `photo_${i}`,
          url: '',
          type: await this.extractPhotoType(photoElement),
          uploadDate: new Date(),
          fileSize: await this.extractFileSize(photoElement),
          format: await this.extractFileFormat(photoElement)
        };

        // Extract photo URL
        const imgElement = await photoElement.$('img');
        if (imgElement) {
          photo.url = await imgElement.getAttribute('src') || '';
        }

        // Download photo if needed
        if (photo.url) {
          photo.localPath = await this.downloadPhoto(photo);
        }

        photos.push(photo);
      }

      return photos;

    } catch (error) {
      console.error('Photo extraction failed:', error);
      return [];
    }
  }

  private async extractPhotoType(photoElement: ElementHandle): Promise<string> {
    // Extract photo type based on context, filename, or labels
    const typeText = await photoElement.$eval('.photo-label', el => el.textContent).catch(() => '');

    // Determine photo type based on text content
    if (typeText?.toLowerCase().includes('trench')) return 'trench';
    if (typeText?.toLowerCase().includes('ont')) return 'ont';
    if (typeText?.toLowerCase().includes('termination')) return 'termination';
    if (typeText?.toLowerCase().includes('property')) return 'property';

    return 'other';
  }

  private async downloadPhoto(photo: Photo): Promise<string> {
    try {
      // Download photo to local storage
      const response = await this.page.goto(photo.url);
      const buffer = await response.body();

      // Generate filename
      const filename = `${photo.id}_${Date.now()}.jpg`;
      const filepath = `/opt/whats-ticket/photos/${filename}`;

      // Save file
      await fs.writeFile(filepath, buffer);

      return filepath;
    } catch (error) {
      console.error(`Failed to download photo ${photo.id}:`, error);
      return '';
    }
  }
}
```

#### 4. Error Handler & Recovery Manager
```typescript
class ErrorHandler {
  private maxRetries = 3;
  private retryDelay = 5000; // 5 seconds

  async withRetry<T>(
    operation: () => Promise<T>,
    operationName: string,
    maxRetries?: number
  ): Promise<T> {
    const retries = maxRetries || this.maxRetries;

    for (let attempt = 1; attempt <= retries; attempt++) {
      try {
        return await operation();
      } catch (error) {
        if (attempt === retries) {
          await this.logFinalError(operationName, error);
          throw error;
        }

        console.log(`${operationName} failed (attempt ${attempt}/${retries}): ${error.message}`);
        console.log(`Retrying in ${this.retryDelay/1000} seconds...`);

        await this.delay(this.retryDelay);
        this.retryDelay = Math.min(this.retryDelay * 2, 30000); // Exponential backoff
      }
    }

    throw new Error('Max retries exceeded');
  }

  private async delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  private async logFinalError(operationName: string, error: Error): Promise<void> {
    console.error(`Final failure for ${operationName}:`, error);

    // Log to monitoring system
    await this.monitoringService.logError({
      operation: operationName,
      error: error.message,
      timestamp: new Date(),
      stack: error.stack
    });
  }

  async handleBrowserCrash(): Promise<void> {
    // Handle browser crashes and restart
    console.log('Browser crash detected - restarting...');

    try {
      // Close existing browser if possible
      if (this.browser) {
        await this.browser.close();
      }

      // Restart browser with new session
      await this.initializeBrowser();
      await this.login();

    } catch (error) {
      console.error('Failed to recover from browser crash:', error);
      throw error;
    }
  }
}
```

## 🔄 Complete Audit Workflow

### Main Audit Process
```typescript
class AuditProcess {
  private authManager: AuthenticationManager;
  private navManager: NavigationManager;
  private dataExtractor: DataExtractionManager;
  private errorHandler: ErrorHandler;

  async runAudit(drNumber: string): Promise<AuditResult> {
    try {
      console.log(`Starting audit for DR number: ${drNumber}`);

      // Step 1: Initialize session
      await this.errorHandler.withRetry(
        () => this.authManager.login(),
        'Login to 1Map'
      );

      // Step 2: Navigate to home installations
      await this.errorHandler.withRetry(
        () => this.navManager.navigateToHomeInstallations(),
        'Navigate to Home Installations'
      );

      // Step 3: Search for DR number
      await this.errorHandler.withRetry(
        () => this.navManager.searchDRNumber(drNumber),
        'Search DR Number',
        5 // More retries for search operations
      );

      // Step 4: Extract installation data
      const installationData = await this.errorHandler.withRetry(
        () => this.dataExtractor.extractInstallationData(),
        'Extract Installation Data'
      );

      // Step 5: Perform audit
      const auditResult = await this.performAudit(installationData);

      // Step 6: Save results
      await this.saveAuditResults(drNumber, auditResult);

      // Step 7: Take screenshot for audit trail
      await this.takeScreenshot(drNumber);

      console.log(`Audit completed successfully for DR: ${drNumber}`);
      return auditResult;

    } catch (error) {
      console.error(`Audit failed for DR ${drNumber}:`, error);

      // Handle audit failure
      await this.handleAuditFailure(drNumber, error);

      throw error;
    } finally {
      // Cleanup
      await this.cleanup();
    }
  }

  private async performAudit(data: InstallationData): Promise<AuditResult> {
    const requiredPhotos = ['trench', 'ont', 'termination', 'property'];
    const foundPhotos = data.photos.map(p => p.type);
    const missingPhotos = requiredPhotos.filter(type => !foundPhotos.includes(type));

    return {
      drNumber: data.jobId,
      propertyId: data.propertyId,
      address: data.address,
      installationStatus: data.status,
      auditDate: new Date(),
      photosRequired: requiredPhotos,
      photosFound: foundPhotos,
      photosMissing: missingPhotos,
      complianceScore: this.calculateComplianceScore(requiredPhotos, foundPhotos),
      auditDetails: {
        totalPhotos: data.photos.length,
        photoTypes: data.photos.map(p => p.type),
        lastModified: data.lastModified,
        auditMethod: 'rpa_extraction'
      }
    };
  }

  private calculateComplianceScore(required: string[], found: string[]): number {
    if (required.length === 0) return 100;

    const foundCount = found.filter(type => required.includes(type)).length;
    return Math.round((foundCount / required.length) * 100);
  }

  private async takeScreenshot(drNumber: string): Promise<void> {
    try {
      const screenshotPath = `/opt/whats-ticket/screenshots/audit_${drNumber}_${Date.now()}.png`;
      await this.authManager.page.screenshot({ path: screenshotPath });
      console.log(`Screenshot saved: ${screenshotPath}`);
    } catch (error) {
      console.error('Failed to take screenshot:', error);
    }
  }

  private async cleanup(): Promise<void> {
    try {
      if (this.authManager && this.authManager.browser) {
        await this.authManager.browser.close();
      }
    } catch (error) {
      console.error('Cleanup failed:', error);
    }
  }
}
```

## 🎯 Performance Optimization

### Browser Pool Management
```typescript
class BrowserPool {
  private pool: Browser[] = [];
  private maxPoolSize = 5;
  private activeBrowsers = 0;

  async getBrowser(): Promise<Browser> {
    if (this.pool.length > 0) {
      const browser = this.pool.pop();
      this.activeBrowsers++;
      return browser;
    }

    if (this.activeBrowsers < this.maxPoolSize) {
      this.activeBrowsers++;
      return await this.createBrowser();
    }

    // Wait for available browser
    return new Promise((resolve) => {
      const checkInterval = setInterval(() => {
        if (this.pool.length > 0) {
          clearInterval(checkInterval);
          const browser = this.pool.pop();
          this.activeBrowsers++;
          resolve(browser);
        }
      }, 1000);
    });
  }

  private async createBrowser(): Promise<Browser> {
    return await chromium.launch({
      headless: true,
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage',
        '--disable-gpu'
      ]
    });
  }

  async releaseBrowser(browser: Browser): Promise<void> {
    this.activeBrowsers--;
    this.pool.push(browser);
  }
}
```

### Session Management
```typescript
class SessionManager {
  private sessions: Map<string, Session> = new Map();
  private sessionTimeout = 30 * 60 * 1000; // 30 minutes

  async getSession(projectId: string): Promise<Session> {
    const session = this.sessions.get(projectId);

    if (session && !this.isSessionExpired(session)) {
      return session;
    }

    // Create new session
    const newSession = await this.createSession(projectId);
    this.sessions.set(projectId, newSession);
    return newSession;
  }

  private isSessionExpired(session: Session): boolean {
    return Date.now() - session.lastUsed > this.sessionTimeout;
  }

  private async createSession(projectId: string): Promise<Session> {
    const browser = await this.browserPool.getBrowser();
    const page = await browser.newPage();

    // Login with project-specific credentials
    await this.loginWithProjectCredentials(page, projectId);

    return {
      browser,
      page,
      projectId,
      lastUsed: Date.now()
    };
  }
}
```

## 📊 Monitoring & Analytics

### Performance Tracking
```typescript
class PerformanceMonitor {
  private metrics: Map<string, PerformanceMetric[]> = new Map();

  async recordMetric(operation: string, duration: number, success: boolean): Promise<void> {
    const metric: PerformanceMetric = {
      operation,
      duration,
      success,
      timestamp: new Date()
    };

    if (!this.metrics.has(operation)) {
      this.metrics.set(operation, []);
    }

    this.metrics.get(operation)?.push(metric);

    // Keep only last 1000 metrics
    const metrics = this.metrics.get(operation) || [];
    if (metrics.length > 1000) {
      this.metrics.set(operation, metrics.slice(-1000));
    }
  }

  getAveragePerformance(operation: string): number {
    const metrics = this.metrics.get(operation) || [];
    if (metrics.length === 0) return 0;

    const total = metrics.reduce((sum, m) => sum + m.duration, 0);
    return total / metrics.length;
  }

  getSuccessRate(operation: string): number {
    const metrics = this.metrics.get(operation) || [];
    if (metrics.length === 0) return 0;

    const successful = metrics.filter(m => m.success).length;
    return (successful / metrics.length) * 100;
  }
}
```

## 🔧 Configuration Management

### Environment Configuration
```typescript
interface RPAConfig {
  credentials: {
    username: string;
    password: string;
  };
  timeouts: {
    pageLoad: number;
    elementWait: number;
    operationTimeout: number;
  };
  retry: {
    maxRetries: number;
    initialDelay: number;
    maxDelay: number;
  };
  browser: {
    headless: boolean;
    poolSize: number;
    args: string[];
  };
  storage: {
    screenshotPath: string;
    photoPath: string;
    logPath: string;
  };
}

class ConfigManager {
  private config: RPAConfig;

  constructor() {
    this.config = this.loadConfig();
  }

  private loadConfig(): RPAConfig {
    return {
      credentials: {
        username: process.env['1MAP_USERNAME'] || '',
        password: process.env['1MAP_PASSWORD'] || ''
      },
      timeouts: {
        pageLoad: 30000,
        elementWait: 10000,
        operationTimeout: 60000
      },
      retry: {
        maxRetries: 3,
        initialDelay: 5000,
        maxDelay: 30000
      },
      browser: {
        headless: true,
        poolSize: 5,
        args: [
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-dev-shm-usage',
          '--disable-gpu'
        ]
      },
      storage: {
        screenshotPath: '/opt/whats-ticket/screenshots',
        photoPath: '/opt/whats-ticket/photos',
        logPath: '/opt/whats-ticket/logs'
      }
    };
  }

  getConfig(): RPAConfig {
    return this.config;
  }
}
```

This RPA-only approach provides a robust, scalable solution for 1Map integration without requiring API access, with comprehensive error handling, performance optimization, and monitoring capabilities.