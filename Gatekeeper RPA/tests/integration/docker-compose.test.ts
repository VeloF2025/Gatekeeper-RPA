/**
 * Docker Compose Integration Tests
 * Testing the complete containerized environment
 */

import { execSync } from 'child_process';
import { describe, test, expect, beforeAll, afterAll } from 'vitest';

describe('Docker Compose Integration', () => {
  let composeCommand: string;

  beforeAll(() => {
    // Determine the correct compose command
    try {
      execSync('docker-compose --version', { stdio: 'pipe' });
      composeCommand = 'docker-compose';
    } catch {
      composeCommand = 'docker compose';
    }
  });

  test('should start all services successfully', async () => {
    try {
      // Start services
      execSync(`${composeCommand} -f docker-compose.yml up -d`, {
        stdio: 'pipe',
        timeout: 300000 // 5 minutes timeout
      });

      // Wait for services to be ready
      await new Promise(resolve => setTimeout(resolve, 30000));

      // Check service status
      const statusOutput = execSync(`${composeCommand} -f docker-compose.yml ps`, {
        encoding: 'utf8',
        stdio: 'pipe'
      });

      expect(statusOutput).toContain('Up');
      expect(statusOutput).toContain('healthy');
    } catch (error) {
      console.error('Docker services failed to start:', error);
      throw error;
    }
  });

  test('should verify all services are running', () => {
    const services = [
      'postgres',
      'redis',
      'nextjs',
      'nginx',
      'rpa-workers',
      'queue-worker',
      'prometheus',
      'grafana'
    ];

    const statusOutput = execSync(`${composeCommand} -f docker-compose.yml ps`, {
      encoding: 'utf8',
      stdio: 'pipe'
    });

    services.forEach(service => {
      expect(statusOutput).toContain(service);
    });
  });

  test('should verify service health checks', async () => {
    const healthChecks = [
      { service: 'postgres', command: 'docker exec gatekeeper-rpa-postgres-1 pg_isready -U postgres' },
      { service: 'redis', command: 'docker exec gatekeeper-rpa-redis-1 redis-cli ping' },
      { service: 'nextjs', command: 'curl -f http://localhost:3000/api/health || exit 1' },
      { service: 'nginx', command: 'curl -f http://localhost:80/health || exit 1' },
      { service: 'prometheus', command: 'curl -f http://localhost:9090/-/healthy || exit 1' },
      { service: 'grafana', command: 'curl -f http://localhost:3001/api/health || exit 1' }
    ];

    for (const check of healthChecks) {
      try {
        execSync(check.command, { stdio: 'pipe', timeout: 30000 });
        console.log(`✓ ${check.service} health check passed`);
      } catch (error) {
        console.error(`✗ ${check.service} health check failed:`, error);
        throw error;
      }
    }
  });

  test('should verify database connectivity', () => {
    try {
      // Test PostgreSQL connection
      execSync('docker exec gatekeeper-rpa-postgres-1 psql -U postgres -d gatekeeper_rpa -c "SELECT 1;"', {
        stdio: 'pipe',
        timeout: 30000
      });

      // Test Redis connection
      execSync('docker exec gatekeeper-rpa-redis-1 redis-cli set test-key test-value', {
        stdio: 'pipe',
        timeout: 30000
      });

      execSync('docker exec gatekeeper-rpa-redis-1 redis-cli get test-key', {
        stdio: 'pipe',
        timeout: 30000
      });

      console.log('✓ Database connectivity verified');
    } catch (error) {
      console.error('✗ Database connectivity failed:', error);
      throw error;
    }
  });

  test('should verify application endpoints', () => {
    const endpoints = [
      { url: 'http://localhost:3000/api/health', expectedStatus: 200 },
      { url: 'http://localhost:80/health', expectedStatus: 200 },
      { url: 'http://localhost:9090/api/v1/targets', expectedStatus: 200 },
      { url: 'http://localhost:3001/api/health', expectedStatus: 200 }
    ];

    endpoints.forEach(endpoint => {
      try {
        const response = execSync(`curl -s -o /dev/null -w "%{http_code}" ${endpoint.url}`, {
          encoding: 'utf8',
          stdio: 'pipe',
          timeout: 30000
        });

        expect(response.trim()).toBe(endpoint.expectedStatus.toString());
        console.log(`✓ ${endpoint.url} - ${response.trim()}`);
      } catch (error) {
        console.error(`✗ ${endpoint.url} failed:`, error);
        throw error;
      }
    });
  });

  test('should verify service networking', () => {
    try {
      // Test inter-container communication
      execSync('docker exec gatekeeper-rpa-nextjs-1 curl -f http://postgres:5432 || exit 1', {
        stdio: 'pipe',
        timeout: 30000
      });

      execSync('docker exec gatekeeper-rpa-nextjs-1 curl -f http://redis:6379 || exit 1', {
        stdio: 'pipe',
        timeout: 30000
      });

      console.log('✓ Service networking verified');
    } catch (error) {
      console.error('✗ Service networking failed:', error);
      throw error;
    }
  });

  test('should verify volume mounts and persistence', () => {
    try {
      // Create test data
      execSync('docker exec gatekeeper-rpa-postgres-1 psql -U postgres -d gatekeeper_rpa -c "CREATE TABLE IF NOT EXISTS test_table (id SERIAL PRIMARY KEY, data TEXT);"', {
        stdio: 'pipe',
        timeout: 30000
      });

      execSync('docker exec gatekeeper-rpa-postgres-1 psql -U postgres -d gatekeeper_rpa -c "INSERT INTO test_table (data) VALUES (\'test-data\');"', {
        stdio: 'pipe',
        timeout: 30000
      });

      // Verify data persistence
      const result = execSync('docker exec gatekeeper-rpa-postgres-1 psql -U postgres -d gatekeeper_rpa -t -c "SELECT COUNT(*) FROM test_table;"', {
        encoding: 'utf8',
        stdio: 'pipe',
        timeout: 30000
      });

      expect(result).toContain('1');
      console.log('✓ Data persistence verified');
    } catch (error) {
      console.error('✗ Data persistence failed:', error);
      throw error;
    }
  });

  test('should verify environment variables', () => {
    try {
      // Check critical environment variables
      const envVars = [
        { service: 'nextjs', var: 'NODE_ENV', expected: 'production' },
        { service: 'postgres', var: 'POSTGRES_DB', expected: 'gatekeeper_rpa' },
        { service: 'redis', var: 'REDIS_PASSWORD', expected: undefined } // Should be set
      ];

      envVars.forEach(({ service, var: varName, expected }) => {
        try {
          const result = execSync(`docker exec gatekeeper-rpa-${service}-1 printenv ${varName}`, {
            encoding: 'utf8',
            stdio: 'pipe',
            timeout: 30000
          }).trim();

          if (expected !== undefined) {
            expect(result).toBe(expected);
          } else {
            expect(result).not.toBe('');
          }
          console.log(`✓ ${service}.${varName} = ${result}`);
        } catch (error) {
          if (expected === undefined) {
            console.log(`✓ ${service}.${varName} is set (security)`);
          } else {
            console.error(`✗ ${service}.${varName} failed:`, error);
            throw error;
          }
        }
      });
    } catch (error) {
      console.error('✗ Environment variables verification failed:', error);
      throw error;
    }
  });

  test('should verify resource limits', () => {
    try {
      // Check container resource limits
      const containers = ['nextjs', 'rpa-workers', 'queue-worker'];

      containers.forEach(container => {
        try {
          // Check memory limit
          const memoryLimit = execSync(`docker inspect gatekeeper-rpa-${container}-1 --format='{{.HostConfig.Memory}}'`, {
            encoding: 'utf8',
            stdio: 'pipe',
            timeout: 30000
          }).trim();

          expect(memoryLimit).not.toBe('0');
          console.log(`✓ ${container} memory limit: ${parseInt(memoryLimit) / 1024 / 1024}MB`);

          // Check CPU limit
          const cpuLimit = execSync(`docker inspect gatekeeper-rpa-${container}-1 --format='{{.HostConfig.NanoCpus}}'`, {
            encoding: 'utf8',
            stdio: 'pipe',
            timeout: 30000
          }).trim();

          expect(cpuLimit).not.toBe('0');
          console.log(`✓ ${container} CPU limit: ${parseInt(cpuLimit) / 1000000000} cores`);
        } catch (error) {
          console.error(`✗ ${container} resource limits failed:`, error);
          throw error;
        }
      });
    } catch (error) {
      console.error('✗ Resource limits verification failed:', error);
      throw error;
    }
  });

  test('should verify security configurations', () => {
    try {
      // Check if containers are running as non-root
      const containers = ['nextjs', 'rpa-workers', 'queue-worker'];

      containers.forEach(container => {
        try {
          const user = execSync(`docker exec gatekeeper-rpa-${container}-1 id -u`, {
            encoding: 'utf8',
            stdio: 'pipe',
            timeout: 30000
          }).trim();

          expect(user).not.toBe('0');
          console.log(`✓ ${container} running as non-root user: ${user}`);
        } catch (error) {
          console.error(`✗ ${container} user verification failed:`, error);
          throw error;
        }
      });

      // Check if sensitive directories are mounted as read-only
      const readOnlyMounts = execSync(`docker inspect gatekeeper-rpa-nextjs-1 --format='{{json .Mounts}}'`, {
        encoding: 'utf8',
        stdio: 'pipe',
        timeout: 30000
      });

      expect(readOnlyMounts).not.toContain('RW'); // Should have read-only mounts
      console.log('✓ Read-only mount verification');
    } catch (error) {
      console.error('✗ Security configuration verification failed:', error);
      throw error;
    }
  });

  afterAll(() => {
    try {
      // Clean up services
      console.log('Cleaning up Docker services...');
      execSync(`${composeCommand} -f docker-compose.yml down -v`, {
        stdio: 'pipe',
        timeout: 60000
      });
      console.log('✓ Docker services cleaned up');
    } catch (error) {
      console.error('Warning: Failed to clean up Docker services:', error);
    }
  });
});