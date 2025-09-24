import type { Config } from 'drizzle-kit';
import * as dotenv from 'dotenv';

// Load environment variables
dotenv.config();

const config: Config = {
  schema: './src/database/schemas/index.ts',
  out: './drizzle',
  dialect: 'postgresql',
  dbCredentials: {
    url: process.env.DATABASE_URL || 'postgresql://postgres:password@localhost:5432/gatekeeper_rpa',
    ssl: process.env.DATABASE_SSL === 'true' ? { rejectUnauthorized: false } : undefined,
  },
  verbose: true,
  strict: true,
  casing: 'snake_case',
  breakpoints: true,
};

export default config;