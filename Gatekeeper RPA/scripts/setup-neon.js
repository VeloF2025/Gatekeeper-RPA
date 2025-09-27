/**
 * Neon PostgreSQL Setup Helper
 *
 * This script helps users set up their Neon PostgreSQL database connection.
 * Run with: node scripts/setup-neon.js
 */

const readline = require('readline');
const fs = require('fs');
const path = require('path');

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

console.log('\n🔌 Neon PostgreSQL Database Setup\n');
console.log('=====================================\n');
console.log('To get your Neon database connection string:');
console.log('1. Go to https://console.neon.tech/');
console.log('2. Create a new project or select an existing one');
console.log('3. Go to Dashboard > Connection Details');
console.log('4. Copy the connection string\n');

rl.question('Enter your Neon connection string: ', (connectionString) => {
  if (!connectionString) {
    console.log('\n❌ No connection string provided. Please try again.');
    rl.close();
    return;
  }

  // Validate connection string format
  if (!connectionString.startsWith('postgresql://') && !connectionString.startsWith('postgres://')) {
    console.log('\n❌ Invalid connection string format. It should start with "postgresql://" or "postgres://"');
    rl.close();
    return;
  }

  // Update .env file
  const envPath = path.join(__dirname, '../.env');
  let envContent = '';

  try {
    if (fs.existsSync(envPath)) {
      envContent = fs.readFileSync(envPath, 'utf8');
    }

    // Replace or add DATABASE_URL
    const dbUrlRegex = /^DATABASE_URL=.*$/m;
    if (dbUrlRegex.test(envContent)) {
      envContent = envContent.replace(dbUrlRegex, `DATABASE_URL=${connectionString}`);
    } else {
      envContent += `\nDATABASE_URL=${connectionString}`;
    }

    fs.writeFileSync(envPath, envContent);
    console.log('\n✅ DATABASE_URL updated successfully!');
    console.log('\n🚀 Next steps:');
    console.log('1. Run "npm run db:push" to push the schema to Neon');
    console.log('2. Run "npm run db:seed" to seed the database');
    console.log('3. Start your development server with "npm run dev"');

  } catch (error) {
    console.error('\n❌ Error updating .env file:', error.message);
  }

  rl.close();
});