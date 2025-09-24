/**
 * Docker Compose Helper for Integration Tests
 * Provides utilities for managing Docker Compose environments
 */

import { exec } from 'child_process';
import { promisify } from 'util';
import * as fs from 'fs';
import * as path from 'path';

const execAsync = promisify(exec);

export class DockerComposeHelper {
  private composeFile: string;

  constructor(composeFile = 'tests/docker/docker-compose.test.yml') {
    this.composeFile = composeFile;
  }

  async up(profile?: string) {
    const command = profile
      ? `docker-compose -f ${this.composeFile} --profile ${profile} up -d`
      : `docker-compose -f ${this.composeFile} up -d`;

    try {
      await execAsync(command);
      console.log(`Docker Compose stack started with profile: ${profile || 'default'}`);
    } catch (error) {
      console.error('Failed to start Docker Compose stack:', error);
      throw error;
    }
  }

  async down() {
    try {
      await execAsync(`docker-compose -f ${this.composeFile} down -v --remove-orphans`);
      console.log('Docker Compose stack stopped');
    } catch (error) {
      console.error('Failed to stop Docker Compose stack:', error);
      throw error;
    }
  }

  async getServices(): Promise<string[]> {
    try {
      const result = await execAsync(`docker-compose -f ${this.composeFile} ps --services`);
      return result.stdout.trim().split('\n').filter(service => service);
    } catch (error) {
      console.error('Failed to get services:', error);
      throw error;
    }
  }

  async getHealthStatus(): Promise<Record<string, string>> {
    try {
      const result = await execAsync(`docker-compose -f ${this.composeFile} ps`);
      const lines = result.stdout.split('\n').filter(line => line.trim());

      // Skip header line
      const services = lines.slice(1);
      const healthStatus: Record<string, string> = {};

      services.forEach(line => {
        const parts = line.split(/\s+/);
        const serviceName = parts[0];
        const status = parts[4] || 'unknown';
        healthStatus[serviceName] = status;
      });

      return healthStatus;
    } catch (error) {
      console.error('Failed to get health status:', error);
      throw error;
    }
  }

  async isPortAccessible(service: string, port: number): Promise<boolean> {
    try {
      const result = await execAsync(`docker-compose -f ${this.composeFile} port ${service} ${port}`);
      return result.stdout.includes(`0.0.0.0:${port}`);
    } catch (error) {
      return false;
    }
  }

  async getLogs(service?: string): Promise<string> {
    const command = service
      ? `docker-compose -f ${this.composeFile} logs ${service}`
      : `docker-compose -f ${this.composeFile} logs`;

    try {
      const result = await execAsync(command);
      return result.stdout;
    } catch (error) {
      console.error('Failed to get logs:', error);
      throw error;
    }
  }

  async executeCommand(service: string, command: string): Promise<string> {
    try {
      const result = await execAsync(`docker-compose -f ${this.composeFile} exec ${service} ${command}`);
      return result.stdout;
    } catch (error) {
      console.error(`Failed to execute command in service ${service}:`, error);
      throw error;
    }
  }

  async getContainerId(service: string): Promise<string> {
    try {
      const result = await execAsync(`docker-compose -f ${this.composeFile} ps -q ${service}`);
      return result.stdout.trim();
    } catch (error) {
      console.error(`Failed to get container ID for service ${service}:`, error);
      throw error;
    }
  }

  async copyToContainer(service: string, sourcePath: string, destPath: string): Promise<void> {
    try {
      const containerId = await this.getContainerId(service);
      await execAsync(`docker cp ${sourcePath} ${containerId}:${destPath}`);
    } catch (error) {
      console.error(`Failed to copy to container ${service}:`, error);
      throw error;
    }
  }

  async copyFromContainer(service: string, sourcePath: string, destPath: string): Promise<void> {
    try {
      const containerId = await this.getContainerId(service);
      await execAsync(`docker cp ${containerId}:${sourcePath} ${destPath}`);
    } catch (error) {
      console.error(`Failed to copy from container ${service}:`, error);
      throw error;
    }
  }

  async waitForService(service: string, timeout: number = 30000): Promise<void> {
    const startTime = Date.now();

    return new Promise((resolve, reject) => {
      const checkHealth = async () => {
        try {
          const healthStatus = await this.getHealthStatus();
          if (healthStatus[service] === 'healthy') {
            resolve();
          } else if (Date.now() - startTime > timeout) {
            reject(new Error(`Service ${service} did not become healthy within ${timeout}ms`));
          } else {
            setTimeout(checkHealth, 1000);
          }
        } catch (error) {
          if (Date.now() - startTime > timeout) {
            reject(new Error(`Service ${service} did not become healthy within ${timeout}ms`));
          } else {
            setTimeout(checkHealth, 1000);
          }
        }
      };

      checkHealth();
    });
  }

  async scaleService(service: string, replicas: number): Promise<void> {
    try {
      await execAsync(`docker-compose -f ${this.composeFile} up -d --scale ${service}=${replicas}`);
    } catch (error) {
      console.error(`Failed to scale service ${service}:`, error);
      throw error;
    }
  }

  async getNetworkInfo(): Promise<any> {
    try {
      const result = await execAsync(`docker-compose -f ${this.composeFile} config`);
      const config = JSON.parse(result.stdout);
      return config.networks || {};
    } catch (error) {
      console.error('Failed to get network info:', error);
      throw error;
    }
  }
}