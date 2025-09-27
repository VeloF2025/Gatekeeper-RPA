import { database } from '../src/database/database.ts';
import { readFileSync } from 'fs';
import { join, dirname } from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

async function runMigration() {
  console.log('🚀 Running database migration...');

  try {
    // Read the migration file
    const migrationPath = join(__dirname, '../drizzle/0000_flaky_serpent_society.sql');
    const migrationSQL = readFileSync(migrationPath, 'utf8');

    console.log('📝 Migration file loaded successfully');

    // Execute the migration
    await database.query(migrationSQL);

    console.log('✅ Database migration completed successfully!');

    // Test the connection
    const isHealthy = await database.healthCheck();
    if (isHealthy) {
      console.log('✅ Database connection is healthy');
    } else {
      console.log('❌ Database connection is not healthy');
    }

    await database.close();

  } catch (error) {
    console.error('❌ Migration failed:', error);
    process.exit(1);
  }
}

runMigration();