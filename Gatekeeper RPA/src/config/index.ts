import dotenv from 'dotenv';
import { AppConfig } from '@/types';

dotenv.config();

const config: AppConfig = {
  nodeEnv: process.env.NODE_ENV || 'development',
  port: parseInt(process.env.PORT || '3000', 10),
  apiVersion: process.env.API_VERSION || 'v1',

  database: {
    url: process.env.DATABASE_URL || 'postgresql://localhost:5432/gatekeeper_rpa',
    ssl: process.env.DATABASE_SSL === 'true',
    pool: {
      min: 2,
      max: 10,
      idle: 30000
    }
  },

  redis: {
    url: process.env.REDIS_URL || 'redis://localhost:6379/0',
    password: process.env.REDIS_PASSWORD,
    db: parseInt(process.env.REDIS_DB || '0', 10)
  },

  security: {
    jwtSecret: process.env.JWT_SECRET || 'default-secret-change-in-production',
    jwtExpiresIn: process.env.JWT_EXPIRES_IN || '15m',
    bcryptRounds: parseInt(process.env.BCRYPT_ROUNDS || '12', 10),
    enableHelmet: true,
    enableCORS: true,
    enableRateLimit: true,
    enableCSRF: true
  },

  whatsapp: {
    apiKey: process.env.WHATSAPP_API_KEY || '',
    baseUrl: process.env.WHATSAPP_BASE_URL || 'https://api.whatsapp.com/v1',
    webhookSecret: process.env.WHATSAPP_WEBHOOK_SECRET || ''
  },

  whatiTicket: {
    version: process.env.WHATITICKET_VERSION || 'latest',
    database: process.env.WHATITICKET_DATABASE || 'postgresql',
    redis: process.env.WHATITICKET_REDIS === 'true',
    encryptionKey: process.env.WHATITICKET_ENCRYPTION_KEY || '',
    jwtSecret: process.env.WHATITICKET_JWT_SECRET || '',
    webhookUrl: process.env.WHATITICKET_WEBHOOK_URL || '',
    verifyToken: process.env.WHATITICKET_VERIFY_TOKEN || ''
  },

  onemap: {
    baseUrl: process.env.ONEMAP_BASE_URL || 'https://1map.example.com',
    username: process.env.ONEMAP_USERNAME || '',
    password: process.env.ONEMAP_PASSWORD || '',
    timeout: 30000,
    retries: 3
  },

  rateLimit: {
    points: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS || '100', 10),
    duration: parseInt(process.env.RATE_LIMIT_WINDOW_MS || '900000', 10)
  },

  upload: {
    maxFileSize: parseInt(process.env.MAX_FILE_SIZE_MB || '10', 10) * 1024 * 1024,
    path: process.env.UPLOAD_PATH || './uploads'
  },

  logging: {
    level: process.env.LOG_LEVEL || 'info',
    file: process.env.LOG_FILE || './logs/app.log'
  },

  monitoring: {
    enabled: process.env.ENABLE_METRICS === 'true',
    port: parseInt(process.env.METRICS_PORT || '9090', 10)
  }
};

// Validate required configuration
const requiredEnvVars = [
  'JWT_SECRET',
  'DATABASE_URL',
  'WHATSAPP_API_KEY',
  'ONEMAP_USERNAME',
  'ONEMAP_PASSWORD',
  'WHATITICKET_ENCRYPTION_KEY',
  'WHATITICKET_JWT_SECRET',
  'WHATSAPP_PHONE_NUMBER_ID',
  'WHATSAPP_ACCESS_TOKEN',
  'WHATSAPP_BUSINESS_ACCOUNT_ID',
  'WHATSAPP_WEBHOOK_URL',
  'WHATSAPP_VERIFY_TOKEN'
];

if (config.nodeEnv === 'production') {
  requiredEnvVars.forEach(envVar => {
    if (!process.env[envVar]) {
      throw new Error(`Required environment variable ${envVar} is missing`);
    }
  });
}

export default config;