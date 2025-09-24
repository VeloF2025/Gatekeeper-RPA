/**
 * Docker Security Tests
 * Comprehensive security validation for Docker containers
 */

import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { createContainer, getContainerLogs, stopContainer } from '../helpers/docker-helper';
import { SecurityValidator } from '../helpers/security-validator';

describe('Docker Security Tests', () => {
  let nextjsContainer: any;
  let rpaContainer: any;
  let queueContainer: any;

  beforeAll(async () => {
    // Start containers for testing
    nextjsContainer = await createContainer('gatekeeper-rpa-nextjs:latest', {
      environment: {
        NODE_ENV: 'test',
        JWT_SECRET: 'test-secret'
      },
      ports: {
        '3000': '3000'
      }
    });

    rpaContainer = await createContainer('gatekeeper-rpa-rpa:latest', {
      environment: {
        NODE_ENV: 'test'
      },
      ports: {
        '8081': '8081'
      }
    });

    queueContainer = await createContainer('gatekeeper-rpa-queue:latest', {
      environment: {
        NODE_ENV: 'test'
      },
      ports: {
        '8082': '8082'
      }
    });
  });

  afterAll(async () => {
    // Clean up containers
    await stopContainer(nextjsContainer);
    await stopContainer(rpaContainer);
    await stopContainer(queueContainer);
  });

  describe('Container Security Configuration', () => {
    it('should run containers as non-root user', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsUser = await securityValidator.getContainerUser(nextjsContainer);
      const rpaUser = await securityValidator.getContainerUser(rpaContainer);
      const queueUser = await securityValidator.getContainerUser(queueContainer);

      expect(nextjsUser).not.toBe('root');
      expect(rpaUser).not.toBe('root');
      expect(queueUser).not.toBe('root');
    });

    it('should not have shell access', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsShell = await securityValidator.hasShellAccess(nextjsContainer);
      const rpaShell = await securityValidator.hasShellAccess(rpaContainer);
      const queueShell = await securityValidator.hasShellAccess(queueContainer);

      expect(nextjsShell).toBe(false);
      expect(rpaShell).toBe(false);
      expect(queueShell).toBe(false);
    });

    it('should have proper file permissions', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsPerms = await securityValidator.checkFilePermissions(nextjsContainer, '/app');
      const rpaPerms = await securityValidator.checkFilePermissions(rpaContainer, '/app');
      const queuePerms = await securityValidator.checkFilePermissions(queueContainer, '/app');

      expect(nextjsPerms).toBe('secure');
      expect(rpaPerms).toBe('secure');
      expect(queuePerms).toBe('secure');
    });

    it('should not have sensitive environment variables', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsEnv = await securityValidator.getEnvironmentVariables(nextjsContainer);
      const rpaEnv = await securityValidator.getEnvironmentVariables(rpaContainer);
      const queueEnv = await securityValidator.getEnvironmentVariables(queueContainer);

      const sensitiveVars = ['PASSWORD', 'SECRET', 'KEY', 'TOKEN'];

      sensitiveVars.forEach(varName => {
        expect(nextjsEnv[varName]).toBeUndefined();
        expect(rpaEnv[varName]).toBeUndefined();
        expect(queueEnv[varName]).toBeUndefined();
      });
    });
  });

  describe('Network Security', () => {
    it('should only expose necessary ports', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsPorts = await securityValidator.getExposedPorts(nextjsContainer);
      const rpaPorts = await securityValidator.getExposedPorts(rpaContainer);
      const queuePorts = await securityValidator.getExposedPorts(queueContainer);

      expect(nextjsPorts).toContain('3000');
      expect(rpaPorts).toContain('8081');
      expect(queuePorts).toContain('8082');

      // Should not expose unnecessary ports
      expect(nextjsPorts).not.toContain('22');
      expect(rpaPorts).not.toContain('22');
      expect(queuePorts).not.toContain('22');
    });

    it('should have proper network isolation', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsNetwork = await securityValidator.getNetworkMode(nextjsContainer);
      const rpaNetwork = await securityValidator.getNetworkMode(rpaContainer);
      const queueNetwork = await securityValidator.getNetworkMode(queueContainer);

      expect(nextjsNetwork).toBe('bridge');
      expect(rpaNetwork).toBe('bridge');
      expect(queueNetwork).toBe('bridge');
    });
  });

  describe('Image Security', () => {
    it('should use official base images', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsImage = await securityValidator.getBaseImage(nextjsContainer);
      const rpaImage = await securityValidator.getBaseImage(rpaContainer);
      const queueImage = await securityValidator.getBaseImage(queueContainer);

      expect(nextjsImage).toMatch(/node:.*alpine/);
      expect(rpaImage).toMatch(/node:.*alpine/);
      expect(queueImage).toMatch(/node:.*alpine/);
    });

    it('should not have known vulnerabilities', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsVulns = await securityValidator.scanVulnerabilities(nextjsContainer);
      const rpaVulns = await securityValidator.scanVulnerabilities(rpaContainer);
      const queueVulns = await securityValidator.scanVulnerabilities(queueContainer);

      expect(nextjsVulns.critical).toBe(0);
      expect(rpaVulns.critical).toBe(0);
      expect(queueVulns.critical).toBe(0);

      expect(nextjsVulns.high).toBeLessThan(5);
      expect(rpaVulns.high).toBeLessThan(5);
      expect(queueVulns.high).toBeLessThan(5);
    });
  });

  describe('Runtime Security', () => {
    it('should have health checks configured', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsHealth = await securityValidator.hasHealthCheck(nextjsContainer);
      const rpaHealth = await securityValidator.hasHealthCheck(rpaContainer);
      const queueHealth = await securityValidator.hasHealthCheck(queueContainer);

      expect(nextjsHealth).toBe(true);
      expect(rpaHealth).toBe(true);
      expect(queueHealth).toBe(true);
    });

    it('should have proper resource limits', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsLimits = await securityValidator.getResourceLimits(nextjsContainer);
      const rpaLimits = await securityValidator.getResourceLimits(rpaContainer);
      const queueLimits = await securityValidator.getResourceLimits(queueContainer);

      expect(nextjsLimits.memory).toBeDefined();
      expect(rpaLimits.memory).toBeDefined();
      expect(queueLimits.memory).toBeDefined();

      expect(nextjsLimits.cpu).toBeDefined();
      expect(rpaLimits.cpu).toBeDefined();
      expect(queueLimits.cpu).toBeDefined();
    });

    it('should not run privileged containers', async () => {
      const securityValidator = new SecurityValidator();

      const nextjsPrivileged = await securityValidator.isPrivileged(nextjsContainer);
      const rpaPrivileged = await securityValidator.isPrivileged(rpaContainer);
      const queuePrivileged = await securityValidator.isPrivileged(queueContainer);

      expect(nextjsPrivileged).toBe(false);
      expect(rpaPrivileged).toBe(false);
      expect(queuePrivileged).toBe(false);
    });
  });

  describe('Application Security', () => {
    it('should enforce security headers', async () => {
      const response = await fetch('http://localhost:3000');
      const headers = response.headers;

      expect(headers.get('X-Content-Type-Options')).toBe('nosniff');
      expect(headers.get('X-Frame-Options')).toBe('DENY');
      expect(headers.get('X-XSS-Protection')).toBe('1; mode=block');
      expect(headers.get('Strict-Transport-Security')).toBeDefined();
    });

    it('should not leak sensitive information in responses', async () => {
      const response = await fetch('http://localhost:3000/api/health');
      const data = await response.json();

      expect(data).not.toHaveProperty('password');
      expect(data).not.toHaveProperty('secret');
      expect(data).not.toHaveProperty('key');
    });

    it('should validate input properly', async () => {
      const maliciousInput = {
        username: '<script>alert("xss")</script>',
        password: 'test123'
      };

      const response = await fetch('http://localhost:3000/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(maliciousInput)
      });

      expect(response.status).toBe(400);
    });

    it('should enforce rate limiting', async () => {
      const requests = [];
      const numRequests = 110; // Exceed rate limit of 100

      for (let i = 0; i < numRequests; i++) {
        requests.push(fetch('http://localhost:3000/api/health'));
      }

      const responses = await Promise.all(requests);
      const rateLimited = responses.some(response => response.status === 429);

      expect(rateLimited).toBe(true);
    });
  });

  describe('Logging and Monitoring', () => {
    it('should have structured logging', async () => {
      const logs = await getContainerLogs(nextjsContainer);

      const structuredLogs = logs.filter(log => {
        try {
          JSON.parse(log);
          return true;
        } catch {
          return false;
        }
      });

      expect(structuredLogs.length).toBeGreaterThan(0);
    });

    it('should log security events', async () => {
      const logs = await getContainerLogs(nextjsContainer);
      const securityLogs = logs.filter(log => log.includes('security') || log.includes('auth'));

      expect(securityLogs.length).toBeGreaterThan(0);
    });

    it('should not log sensitive information', async () => {
      const logs = await getContainerLogs(nextjsContainer);
      const sensitiveInfo = logs.filter(log =>
        log.includes('password') ||
        log.includes('secret') ||
        log.includes('token') ||
        log.includes('key')
      );

      expect(sensitiveInfo.length).toBe(0);
    });
  });
});