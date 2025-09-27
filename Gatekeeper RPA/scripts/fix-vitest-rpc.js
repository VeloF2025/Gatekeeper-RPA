#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

// Fix Vitest RPC issues permanently
console.log('🔧 Fixing Vitest RPC configuration...');

const vitestConfigPath = path.join(process.cwd(), 'vitest.config.ts');

if (fs.existsSync(vitestConfigPath)) {
  let config = fs.readFileSync(vitestConfigPath, 'utf8');

  // Add RPC stability configuration if not already present
  if (!config.includes('poolOptions')) {
    config = config.replace(
      /test: \{/,
      `test: {
    // Prevent RPC connection issues
    pool: 'forks',
    poolOptions: {
      forks: {
        singleFork: true,
        isolate: false
      }
    },
    maxWorkers: 1,
    minWorkers: 1,`
    );

    fs.writeFileSync(vitestConfigPath, config);
    console.log('✅ Updated vitest.config.ts with RPC stability settings');
  }
}

// Clear cache directories
const cacheDirs = ['.vitest', 'node_modules/.vite', 'node_modules/.cache'];
cacheDirs.forEach(dir => {
  const fullPath = path.join(process.cwd(), dir);
  if (fs.existsSync(fullPath)) {
    fs.rmSync(fullPath, { recursive: true, force: true });
    console.log(`🗑️  Cleared cache: ${dir}`);
  }
});

console.log('✅ Vitest RPC fix complete!');
console.log('📝 Run "npm run test:unit" to test');