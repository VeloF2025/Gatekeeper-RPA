import * as dotenv from 'dotenv';

// Load environment variables
dotenv.config();

const config = {
  schema: './src/database/schemas/index.ts',
  out: './drizzle',
  dialect: 'postgresql',
  dbCredentials: {
    url: process.env.DATABASE_URL || 'postgresql://postgres:password@localhost:5432/gatekeeper_rpa',
  },
  verbose: true,
  strict: true,
};

export default config;