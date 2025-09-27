/**
 * Application Configuration
 *
 * Central configuration management for the Gatekeeper RPA system.
 * Handles environment variables and provides typed configuration access.
 */

import dotenv from 'dotenv';

// Load environment variables
dotenv.config();

interface DatabaseConfig {
  url: string;
  ssl: boolean;
  pool: {
    max: number;
    min: number;
    idle: number;
  };
}

interface AppConfig {
  database: DatabaseConfig;
  port: number;
  env: string;
}

const config: AppConfig = {
  database: {
    url: process.env.DATABASE_URL || 'postgresql://username:password@localhost:5432/gatekeeper_rpa',
    ssl: process.env.DATABASE_SSL === 'true',
    pool: {
      max: parseInt(process.env.DB_POOL_MAX || '20'),
      min: parseInt(process.env.DB_POOL_MIN || '5'),
      idle: parseInt(process.env.DB_POOL_IDLE || '30000'),
    },
  },
  port: parseInt(process.env.PORT || '3000'),
  env: process.env.NODE_ENV || 'development',
};

export default config;