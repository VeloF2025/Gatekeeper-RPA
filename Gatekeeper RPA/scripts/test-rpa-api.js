#!/usr/bin/env node

/**
 * RPA API Endpoints Test Script
 * Validates that all RPA API endpoints are working correctly
 */

const http = require('http');

const BASE_URL = 'http://localhost:3003';

// Test endpoints
const endpoints = [
  {
    method: 'GET',
    path: '/api/rpa/health',
    description: 'RPA Health Check'
  },
  {
    method: 'GET',
    path: '/api/rpa/stats',
    description: 'RPA Statistics'
  },
  {
    method: 'GET',
    path: '/api/rpa/jobs',
    description: 'RPA Jobs List'
  },
  {
    method: 'POST',
    path: '/api/rpa/jobs',
    description: 'RPA Job Submission',
    body: {
      ticketId: 'TEST-001',
      drNumber: 'DR1234567',
      options: {}
    }
  }
];

function makeRequest(endpoint) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: 'localhost',
      port: 3000,
      path: endpoint.path,
      method: endpoint.method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer test-token' // Mock auth
      }
    };

    const req = http.request(options, (res) => {
      let data = '';
      res.on('data', (chunk) => {
        data += chunk;
      });
      res.on('end', () => {
        resolve({
          statusCode: res.statusCode,
          headers: res.headers,
          body: data
        });
      });
    });

    req.on('error', (error) => {
      reject(error);
    });

    if (endpoint.body) {
      req.write(JSON.stringify(endpoint.body));
    }
    req.end();
  });
}

async function testEndpoints() {
  console.log('🧪 Testing RPA API Endpoints...\n');

  for (const endpoint of endpoints) {
    try {
      console.log(`Testing ${endpoint.description} (${endpoint.method} ${endpoint.path})...`);

      const response = await makeRequest(endpoint);

      if (response.statusCode >= 200 && response.statusCode < 300) {
        console.log(`✅ ${endpoint.description}: ${response.statusCode}`);

        try {
          const body = JSON.parse(response.body);
          if (body.success) {
            console.log(`   Success: ${body.message || 'OK'}`);
          } else {
            console.log(`   API Error: ${body.error || 'Unknown error'}`);
          }
        } catch (e) {
          console.log(`   Response: ${response.body.substring(0, 100)}...`);
        }
      } else {
        console.log(`❌ ${endpoint.description}: ${response.statusCode}`);
        console.log(`   Response: ${response.body.substring(0, 100)}...`);
      }
    } catch (error) {
      console.log(`❌ ${endpoint.description}: Connection failed`);
      console.log(`   Error: ${error.message}`);
    }
    console.log('');
  }

  console.log('🏁 RPA API Testing Complete');
}

// Check if dev server is running
function checkDevServer() {
  return new Promise((resolve) => {
    const req = http.request({
      hostname: 'localhost',
      port: 3000,
      path: '/',
      method: 'HEAD',
      timeout: 5000
    }, (res) => {
      resolve(true);
    });

    req.on('error', () => {
      resolve(false);
    });

    req.setTimeout(5000, () => {
      req.destroy();
      resolve(false);
    });

    req.end();
  });
}

async function main() {
  console.log('🚀 RPA API Endpoints Validation\n');

  const isRunning = await checkDevServer();

  if (!isRunning) {
    console.log('❌ Development server is not running on http://localhost:3000');
    console.log('Please start the development server with: npm run dev');
    process.exit(1);
  }

  console.log('✅ Development server is running\n');

  await testEndpoints();
}

if (require.main === module) {
  main().catch(console.error);
}

module.exports = { testEndpoints, checkDevServer };