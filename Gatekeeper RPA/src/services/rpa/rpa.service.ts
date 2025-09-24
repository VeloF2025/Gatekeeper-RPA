import { chromium, Browser, Page, BrowserContext } from 'playwright';
import { v4 as uuidv4 } from 'uuid';
import { PhotoType, Photo, AuditContext, AuditStep } from '@/types';
import { logger, auditLogger, securityLogger } from '@/utils/logger';
import { InputSanitizer } from '@/utils/validation';
import config from '@/config';

export interface RPALoginCredentials {
  username: string;
  password: string;
  mfaCode?: string;
}

export interface InstallationData {
  propertyId: string;
  jobId: string;
  address: string;
  status: string;
  lastModified: string;
  photos: Photo[];
}

export interface RPAExecutionOptions {
  headless?: boolean;
  timeout?: number;
  screenshots?: boolean;
  retries?: number;
  slowMo?: number;
}

export interface RPAExecutionResult {
  success: boolean;
  data?: InstallationData;
  error?: string;
  screenshots?: string[];
  executionTime: number;
  steps: AuditStep[];
  errors: string[];
}

export class RPAService {
  private browser: Browser | null = null;
  private context: BrowserContext | null = null;
  private page: Page | null = null;
  private isInitialized: boolean = false;

  /**
   * Initialize browser with security settings
   */
  async initialize(options: RPAExecutionOptions = {}): Promise<void> {
    try {
      const launchOptions = {
        headless: options.headless ?? true,
        timeout: options.timeout ?? 30000,
        slowMo: options.slowMo ?? 0,
        args: [
          '--no-sandbox',
          '--disable-setuid-sandbox',
          '--disable-dev-shm-usage',
          '--disable-accelerated-2d-canvas',
          '--no-first-run',
          '--no-zygote',
          '--disable-gpu'
        ]
      };

      this.browser = await chromium.launch(launchOptions);

      // Create context with security settings
      this.context = await this.browser.newContext({
        viewport: { width: 1920, height: 1080 },
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
        javaScriptEnabled: true,
        ignoreHTTPSErrors: false, // Don't ignore HTTPS errors for security
        bypassCSP: false, // Don't bypass CSP for security
        extraHTTPHeaders: {
          'Accept-Language': 'en-US,en;q=0.9',
        }
      });

      // Setup page with security event listeners
      this.page = await this.context.newPage();

      // Setup page error handling
      this.page.on('pageerror', (error) => {
        logger.error('RPA page error', { error: error.message });
      });

      this.page.on('console', (msg) => {
        if (msg.type() === 'error') {
          logger.error('RPA console error', { text: msg.text() });
        }
      });

      // Setup request interception for security
      await this.page.route('**/*', (route) => {
        const request = route.request();
        const url = request.url();

        // Block non-essential resources for performance and security
        if (url.includes('google-analytics.com') ||
            url.includes('facebook.com') ||
            url.includes('doubleclick.net')) {
          route.abort();
          return;
        }

        // Continue with other requests
        route.continue();
      });

      this.isInitialized = true;

      logger.info('RPA service initialized successfully', {
        headless: launchOptions.headless,
        timeout: launchOptions.timeout
      });

    } catch (error) {
      logger.error('Failed to initialize RPA service', {
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }

  /**
   * Execute full audit workflow with comprehensive error handling
   */
  async executeFullAudit(
    drNumber: string,
    options: RPAExecutionOptions = {}
  ): Promise<RPAExecutionResult> {
    const startTime = Date.now();
    const screenshots: string[] = [];
    const steps: AuditStep[] = [];
    const errors: string[] = [];

    try {
      // Validate DR number
      if (!this.validateDRNumber(drNumber)) {
        throw new Error('Invalid DR number format');
      }

      // Initialize if not already done
      if (!this.isInitialized) {
        await this.initialize(options);
      }

      const context: AuditContext = {
        ticketId: `audit-${uuidv4()}`,
        userId: 'rpa-system',
        startTime: new Date(),
        steps,
        screenshots,
        errors: []
      };

      logger.info('Starting RPA audit execution', {
        drNumber,
        auditId: context.ticketId
      });

      // Execute audit steps with comprehensive error handling
      const result = await this.executeWithRetry(
        async () => this.performAuditSteps(drNumber, context, options),
        options.retries ?? 3
      );

      const executionTime = Date.now() - startTime;

      if (result.success) {
        logger.info('RPA audit completed successfully', {
          drNumber,
          executionTime,
          stepsCount: steps.length,
          photosCount: result.data?.photos.length || 0
        });

        // Log audit completion
        auditLogger.info('RPA audit completed', {
          auditId: context.ticketId,
          drNumber,
          executionTime,
          success: true,
          complianceScore: this.calculateComplianceScore(result.data?.photos || [])
        });
      } else {
        logger.error('RPA audit failed', {
          drNumber,
          executionTime,
          error: result.error
        });

        // Log audit failure
        auditLogger.error('RPA audit failed', {
          auditId: context.ticketId,
          drNumber,
          executionTime,
          error: result.error,
          success: false
        });
      }

      return {
        ...result,
        executionTime,
        steps,
        screenshots,
        errors: [...errors, ...context.errors]
      };

    } catch (error) {
      const executionTime = Date.now() - startTime;
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';

      logger.error('RPA audit execution failed', {
        drNumber,
        executionTime,
        error: errorMessage
      });

      return {
        success: false,
        error: errorMessage,
        executionTime,
        steps,
        screenshots,
        errors: [...errors, errorMessage]
      };
    }
  }

  /**
   * Perform audit steps with detailed tracking
   */
  private async performAuditSteps(
    drNumber: string,
    context: AuditContext,
    options: RPAExecutionOptions
  ): Promise<{ success: boolean; data?: InstallationData; error?: string }> {
    if (!this.page || !this.context) {
      throw new Error('RPA service not initialized');
    }

    const loginStep = this.createAuditStep('Login to 1Map');
    context.steps.push(loginStep);

    try {
      // Step 1: Login
      loginStep.status = 'in_progress';
      await this.loginTo1Map({
        username: config.onemap.username,
        password: config.onemap.password
      });
      loginStep.status = 'completed';

      // Take screenshot after login
      if (options.screenshots) {
        const screenshot = await this.takeScreenshot('after-login');
        if (screenshot) context.screenshots.push(screenshot);
      }

    } catch (error) {
      loginStep.status = 'failed';
      loginStep.error = error instanceof Error ? error.message : 'Login failed';
      throw error;
    }

    // Step 2: Navigate to installations
    const navigateStep = this.createAuditStep('Navigate to installations');
    context.steps.push(navigateStep);

    try {
      navigateStep.status = 'in_progress';
      await this.navigateToInstallations();
      navigateStep.status = 'completed';

    } catch (error) {
      navigateStep.status = 'failed';
      navigateStep.error = error instanceof Error ? error.message : 'Navigation failed';
      throw error;
    }

    // Step 3: Search for DR number
    const searchStep = this.createAuditStep('Search for DR number');
    context.steps.push(searchStep);

    try {
      searchStep.status = 'in_progress';
      await this.searchForInstallation(drNumber);
      searchStep.status = 'completed';

    } catch (error) {
      searchStep.status = 'failed';
      searchStep.error = error instanceof Error ? error.message : 'Search failed';
      throw error;
    }

    // Step 4: Extract installation data
    const extractStep = this.createAuditStep('Extract installation data');
    context.steps.push(extractStep);

    let installationData: InstallationData;
    try {
      extractStep.status = 'in_progress';
      installationData = await this.extractInstallationData();
      extractStep.status = 'completed';
      extractStep.result = installationData;

    } catch (error) {
      extractStep.status = 'failed';
      extractStep.error = error instanceof Error ? error.message : 'Data extraction failed';
      throw error;
    }

    // Step 5: Extract photos
    const photosStep = this.createAuditStep('Extract photos');
    context.steps.push(photosStep);

    try {
      photosStep.status = 'in_progress';
      installationData.photos = await this.extractPhotos();
      photosStep.status = 'completed';
      photosStep.result = { photoCount: installationData.photos.length };

    } catch (error) {
      photosStep.status = 'failed';
      photosStep.error = error instanceof Error ? error.message : 'Photo extraction failed';
      // Don't throw here - continue with available data
      context.errors.push(`Photo extraction warning: ${error instanceof Error ? error.message : error}`);
    }

    // Step 6: Validate extracted data
    const validationStep = this.createAuditStep('Validate extracted data');
    context.steps.push(validationStep);

    try {
      validationStep.status = 'in_progress';
      await this.validateExtractedData(installationData);
      validationStep.status = 'completed';
      validationStep.result = { valid: true };

    } catch (error) {
      validationStep.status = 'failed';
      validationStep.error = error instanceof Error ? error.message : 'Validation failed';
      context.errors.push(`Validation warning: ${error instanceof Error ? error.message : error}`);
    }

    // Take final screenshot
    if (options.screenshots) {
      const screenshot = await this.takeScreenshot('final-result');
      if (screenshot) context.screenshots.push(screenshot);
    }

    return {
      success: true,
      data: installationData
    };
  }

  /**
   * Login to 1Map with security validation
   */
  private async loginTo1Map(credentials: RPALoginCredentials): Promise<void> {
    if (!this.page) throw new Error('Page not initialized');

    try {
      // Navigate to login page
      await this.page.goto(config.onemap.baseUrl + '/login', {
        waitUntil: 'networkidle',
        timeout: config.onemap.timeout
      });

      // Verify login page loaded
      const loginForm = await this.page.waitForSelector('#login-form, form[action*="login"], .login-form', {
        timeout: 10000
      });

      if (!loginForm) {
        throw new Error('Login form not found');
      }

      // Fill login credentials with security validation
      await this.page.fill('#username, input[name="username"], input[type="text"]', credentials.username);
      await this.page.fill('#password, input[name="password"], input[type="password"]', credentials.password);

      // Security check: verify password field is masked
      const passwordField = await this.page.$('input[type="password"]');
      const passwordType = await passwordField?.getAttribute('type');
      if (passwordType !== 'password') {
        throw new Error('Security violation: password field not properly masked');
      }

      // Click login button
      await this.page.click('#login-button, button[type="submit"], .login-button');

      // Wait for navigation
      await this.page.waitForNavigation({
        waitUntil: 'networkidle',
        timeout: config.onemap.timeout
      });

      // Check for MFA if required
      if (credentials.mfaCode) {
        await this.handleMFA(credentials.mfaCode);
      }

      // Verify successful login
      const currentUrl = this.page.url();
      if (currentUrl.includes('/login') || currentUrl.includes('/error')) {
        throw new Error('Login failed - redirected to login or error page');
      }

      // Verify dashboard elements are present
      await this.page.waitForSelector('.dashboard, .main-content, #dashboard', {
        timeout: 10000
      });

      securityLogger.info('1Map login successful', {
        username: credentials.username,
        timestamp: new Date().toISOString()
      });

    } catch (error) {
      securityLogger.error('1Map login failed', {
        username: credentials.username,
        error: error instanceof Error ? error.message : error,
        timestamp: new Date().toISOString()
      });

      throw error;
    }
  }

  /**
   * Handle MFA authentication
   */
  private async handleMFA(mfaCode: string): Promise<void> {
    if (!this.page) throw new Error('Page not initialized');

    try {
      // Wait for MFA input
      const mfaInput = await this.page.waitForSelector('#mfa-code, #mfa, input[name="mfa"]', {
        timeout: 10000
      });

      if (!mfaInput) {
        throw new Error('MFA input not found');
      }

      // Fill MFA code
      await this.page.fill('#mfa-code, #mfa, input[name="mfa"]', mfaCode);

      // Submit MFA
      await this.page.click('#verify-button, #mfa-submit, button[type="submit"]');

      // Wait for navigation
      await this.page.waitForNavigation({
        waitUntil: 'networkidle',
        timeout: config.onemap.timeout
      });

      logger.info('MFA authentication completed');

    } catch (error) {
      logger.error('MFA authentication failed', {
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }

  /**
   * Navigate to installations page
   */
  private async navigateToInstallations(): Promise<void> {
    if (!this.page) throw new Error('Page not initialized');

    try {
      // Look for installations link
      const installationsLink = await this.page.waitForSelector(
        'a[href*="installation"], #installations-link, .installations-menu-item, nav a:has-text("Installations")',
        { timeout: 10000 }
      );

      if (!installationsLink) {
        throw new Error('Installations link not found');
      }

      // Click installations link
      await installationsLink.click();

      // Wait for page load
      await this.page.waitForSelector('.installations-table, .installation-list, #installations-container', {
        timeout: config.onemap.timeout
      });

      logger.info('Navigated to installations page');

    } catch (error) {
      logger.error('Failed to navigate to installations', {
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }

  /**
   * Search for installation by DR number
   */
  private async searchForInstallation(drNumber: string): Promise<void> {
    if (!this.page) throw new Error('Page not initialized');

    try {
      // Find search input
      const searchInput = await this.page.waitForSelector(
        '#search-input, input[name="search"], .search-field, [placeholder*="search"]',
        { timeout: 10000 }
      );

      if (!searchInput) {
        throw new Error('Search input not found');
      }

      // Clear and fill search input
      await searchInput.fill('');
      await searchInput.fill(drNumber);

      // Click search button
      const searchButton = await this.page.$('#search-button, button[type="submit"], .search-button');
      if (searchButton) {
        await searchButton.click();
      } else {
        // Press Enter if no button found
        await searchInput.press('Enter');
      }

      // Wait for search results
      await this.page.waitForSelector('.search-results, .installation-item, .result-row', {
        timeout: config.onemap.timeout
      });

      // Verify search results contain the DR number
      const results = await this.page.$$eval('.search-results, .installation-item, .result-row', (elements, drNum) => {
        return elements.some(el => el.textContent?.includes(drNum));
      }, drNumber);

      if (!results) {
        throw new Error(`No results found for DR number: ${drNumber}`);
      }

      // Click on the first result
      await this.page.click('.search-results .result-row:first-child, .installation-item:first-child');

      // Wait for installation details
      await this.page.waitForSelector('.installation-details, .detail-panel, #installation-details', {
        timeout: config.onemap.timeout
      });

      logger.info('Search completed successfully', { drNumber });

    } catch (error) {
      logger.error('Search failed', {
        error: error instanceof Error ? error.message : error,
        drNumber
      });

      throw error;
    }
  }

  /**
   * Extract installation data
   */
  private async extractInstallationData(): Promise<InstallationData> {
    if (!this.page) throw new Error('Page not initialized');

    try {
      // Extract property ID
      const propertyId = await this.page.$eval(
        '.property-id, [data-field="propertyId"], .property-field',
        el => el.textContent?.trim() || ''
      );

      // Extract job ID
      const jobId = await this.page.$eval(
        '.job-id, [data-field="jobId"], .job-field',
        el => el.textContent?.trim() || ''
      );

      // Extract address
      const address = await this.page.$eval(
        '.address, [data-field="address"], .address-field',
        el => el.textContent?.trim() || ''
      );

      // Extract status
      const status = await this.page.$eval(
        '.status, [data-field="status"], .status-field',
        el => el.textContent?.trim() || ''
      );

      // Extract last modified
      const lastModified = await this.page.$eval(
        '.last-modified, [data-field="lastModified"], .modified-field',
        el => el.textContent?.trim() || ''
      );

      // Validate extracted data
      if (!propertyId || !jobId || !address || !status) {
        throw new Error('Failed to extract required installation data');
      }

      const installationData: InstallationData = {
        propertyId: InputSanitizer.sanitizeString(propertyId),
        jobId: InputSanitizer.sanitizeString(jobId),
        address: InputSanitizer.sanitizeString(address),
        status: InputSanitizer.sanitizeString(status),
        lastModified: InputSanitizer.sanitizeString(lastModified),
        photos: []
      };

      logger.info('Installation data extracted successfully', {
        propertyId: installationData.propertyId,
        jobId: installationData.jobId,
        status: installationData.status
      });

      return installationData;

    } catch (error) {
      logger.error('Failed to extract installation data', {
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }

  /**
   * Extract photos from installation
   */
  private async extractPhotos(): Promise<Photo[]> {
    if (!this.page) throw new Error('Page not initialized');

    try {
      // Find photo containers
      const photoElements = await this.page.$$('.photo-container, .installation-photo, .photo-item');

      const photos: Photo[] = [];

      for (let i = 0; i < photoElements.length; i++) {
        const element = photoElements[i];

        try {
          // Extract photo type
          const type = await element.$eval('.photo-type, .type-label, [data-type]',
            el => el.textContent?.trim().toLowerCase() || 'additional'
          );

          // Extract photo URL
          const url = await element.$eval('img, .photo-img, [src]',
            el => el.getAttribute('src') || ''
          );

          // Extract upload time
          const uploadTimeText = await element.$eval('.upload-time, .date-label, .timestamp',
            el => el.textContent?.trim() || new Date().toISOString()
          );

          // Extract file size
          const fileSizeText = await element.$eval('.file-size, .size-label',
            el => el.textContent?.trim() || '0'
          );

          // Parse and validate data
          const photoType = this.parsePhotoType(type);
          const uploadTime = new Date(uploadTimeText);
          const fileSize = this.parseFileSize(fileSizeText);

          // Validate photo data
          if (!url || !photoType) {
            logger.warn('Skipping invalid photo', { type, url });
            continue;
          }

          const photo: Photo = {
            id: `photo-${uuidv4()}`,
            ticketId: '', // Will be set by caller
            type: photoType,
            url: url,
            uploadTime: uploadTime.toISOString(),
            fileSize: fileSize,
            metadata: {
              extractedAt: new Date().toISOString(),
              source: '1map-rpa'
            }
          };

          photos.push(photo);

        } catch (error) {
          logger.warn('Failed to extract photo data', {
            index: i,
            error: error instanceof Error ? error.message : error
          });
        }
      }

      logger.info('Photos extracted successfully', {
        totalPhotos: photos.length,
        photoTypes: photos.map(p => p.type)
      });

      return photos;

    } catch (error) {
      logger.error('Failed to extract photos', {
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }

  /**
   * Validate extracted data
   */
  private async validateExtractedData(data: InstallationData): Promise<void> {
    const errors: string[] = [];

    // Validate property ID format
    if (!data.propertyId || data.propertyId.length < 3) {
      errors.push('Invalid property ID format');
    }

    // Validate job ID format
    if (!data.jobId || data.jobId.length < 3) {
      errors.push('Invalid job ID format');
    }

    // Validate address
    if (!data.address || data.address.length < 5) {
      errors.push('Invalid address format');
    }

    // Validate status
    if (!data.status || data.status.length < 3) {
      errors.push('Invalid status format');
    }

    // Validate photos
    for (const photo of data.photos) {
      try {
        new URL(photo.url); // Validate URL format
      } catch {
        errors.push(`Invalid photo URL: ${photo.url}`);
      }
    }

    if (errors.length > 0) {
      throw new Error(`Data validation failed: ${errors.join(', ')}`);
    }

    logger.info('Data validation completed successfully');
  }

  /**
   * Execute with retry logic
   */
  private async executeWithRetry<T>(
    fn: () => Promise<T>,
    maxRetries: number
  ): Promise<T> {
    let lastError: Error;

    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        return await fn();
      } catch (error) {
        lastError = error instanceof Error ? error : new Error('Unknown error');

        if (attempt === maxRetries) {
          throw lastError;
        }

        logger.warn('Retrying RPA operation', {
          attempt,
          maxRetries,
          error: lastError.message
        });

        // Wait before retry
        await new Promise(resolve => setTimeout(resolve, 2000 * attempt));
      }
    }

    throw lastError!;
  }

  /**
   * Take screenshot with security considerations
   */
  private async takeScreenshot(name: string): Promise<string | null> {
    if (!this.page) return null;

    try {
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
      const filename = `screenshot-${name}-${timestamp}.png`;
      const path = `./screenshots/${filename}`;

      await this.page.screenshot({
        path,
        fullPage: false,
        type: 'png'
      });

      logger.debug('Screenshot taken', { filename });

      return path;

    } catch (error) {
      logger.warn('Failed to take screenshot', {
        name,
        error: error instanceof Error ? error.message : error
      });

      return null;
    }
  }

  /**
   * Helper methods
   */
  private validateDRNumber(drNumber: string): boolean {
    return /^DR\d{7}$/i.test(drNumber);
  }

  private parsePhotoType(type: string): PhotoType {
    const normalized = type.toLowerCase();

    if (normalized.includes('trench')) return PhotoType.TRENCH;
    if (normalized.includes('ont')) return PhotoType.ONT;
    if (normalized.includes('termination')) return PhotoType.TERMINATION;
    if (normalized.includes('property')) return PhotoType.PROPERTY;

    return PhotoType.ADDITIONAL;
  }

  private parseFileSize(sizeText: string): number {
    const match = sizeText.match(/(\d+(?:\.\d+)?)\s*(KB|MB|GB)?/i);
    if (!match) return 0;

    const size = parseFloat(match[1]);
    const unit = match[2]?.toUpperCase();

    switch (unit) {
      case 'KB': return size * 1024;
      case 'MB': return size * 1024 * 1024;
      case 'GB': return size * 1024 * 1024 * 1024;
      default: return size;
    }
  }

  private calculateComplianceScore(photos: Photo[]): number {
    const requiredTypes = [PhotoType.TRENCH, PhotoType.ONT, PhotoType.TERMINATION, PhotoType.PROPERTY];
    const foundTypes = new Set(photos.map(p => p.type));

    const foundRequiredCount = requiredTypes.filter(type => foundTypes.has(type)).length;
    return Math.round((foundRequiredCount / requiredTypes.length) * 100);
  }

  private createAuditStep(name: string): AuditStep {
    return {
      name,
      status: 'pending',
      startTime: new Date()
    };
  }

  /**
   * Cleanup resources
   */
  async cleanup(): Promise<void> {
    try {
      if (this.context) {
        await this.context.close();
        this.context = null;
      }

      if (this.browser) {
        await this.browser.close();
        this.browser = null;
      }

      this.page = null;
      this.isInitialized = false;

      logger.info('RPA service cleaned up successfully');

    } catch (error) {
      logger.error('Failed to cleanup RPA service', {
        error: error instanceof Error ? error.message : error
      });

      throw error;
    }
  }
}