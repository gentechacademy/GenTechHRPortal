/**
 * API Testing Script for GenTech HR Portal
 * Tests all API endpoints defined in services/api.js
 */

const axios = require('axios');

// Base URL for testing (local development server)
const BASE_URL = process.env.API_URL || 'http://localhost:8081/api';

const api = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Track test results
const results = {
  passed: 0,
  failed: 0,
  endpoints: [],
};

async function testEndpoint(name, method, url, data = null, requiresAuth = false) {
  try {
    console.log(`Testing: ${method.toUpperCase()} ${url}`);
    
    let response;
    if (method === 'get') {
      response = await api.get(url);
    } else if (method === 'post') {
      response = await api.post(url, data);
    } else if (method === 'put') {
      response = await api.put(url, data);
    } else if (method === 'delete') {
      response = await api.delete(url);
    }
    
    results.passed++;
    results.endpoints.push({ name, status: 'PASS', url, statusCode: response.status });
    console.log(`  ✓ PASS (${response.status})`);
    return true;
  } catch (error) {
    results.failed++;
    const statusCode = error.response?.status || 'NETWORK_ERROR';
    const isAuthError = statusCode === 401 || statusCode === 403;
    
    // Auth errors are expected for protected endpoints without valid token
    if (isAuthError && requiresAuth) {
      console.log(`  ✓ PASS (Auth required - ${statusCode})`);
      results.passed++;
      results.endpoints[results.endpoints.length - 1].status = 'PASS (Auth Required)';
      return true;
    }
    
    results.endpoints.push({ name, status: 'FAIL', url, statusCode, error: error.message });
    console.log(`  ✗ FAIL (${statusCode}): ${error.message}`);
    return false;
  }
}

async function runTests() {
  console.log('='.repeat(60));
  console.log('GenTech HR Portal - API Endpoint Testing');
  console.log(`Base URL: ${BASE_URL}`);
  console.log('='.repeat(60));
  console.log();

  // Test Auth APIs
  console.log('--- Auth APIs ---');
  await testEndpoint('Login', 'post', '/auth/login', { email: 'test@test.com', password: 'test' });
  await testEndpoint('Setup', 'post', '/auth/setup');

  // Test Super Admin APIs
  console.log('\n--- Super Admin APIs ---');
  await testEndpoint('Get All Admins', 'get', '/superadmin/admins', null, true);
  await testEndpoint('Get All Companies', 'get', '/superadmin/companies', null, true);

  // Test Admin APIs
  console.log('\n--- Admin APIs ---');
  await testEndpoint('Get All Employees', 'get', '/admin/employees', null, true);
  await testEndpoint('Get My Company', 'get', '/admin/my-company', null, true);

  // Test Employee APIs
  console.log('\n--- Employee APIs ---');
  await testEndpoint('Get My Profile', 'get', '/employee/profile', null, true);
  await testEndpoint('Get My Projects', 'get', '/employee/my-projects', null, true);

  // Test Attendance APIs
  console.log('\n--- Attendance APIs ---');
  await testEndpoint('Get My Attendance', 'get', '/attendance/my-attendance', null, true);
  await testEndpoint('Get Today Attendance', 'get', '/attendance/today', null, true);
  await testEndpoint('Check In', 'post', '/attendance/checkin', { notes: 'Test' }, true);

  // Test Leave APIs
  console.log('\n--- Leave APIs ---');
  await testEndpoint('Get My Leaves', 'get', '/leaves/my-leaves', null, true);
  await testEndpoint('Apply for Leave', 'post', '/leaves/apply', { startDate: '2024-01-01', endDate: '2024-01-02' }, true);

  // Test Salary APIs
  console.log('\n--- Salary APIs ---');
  await testEndpoint('Get My Salary Slips', 'get', '/salary/my-slips', null, true);
  await testEndpoint('Get All Slips', 'get', '/salary/slips', null, true);

  // Test Bonus APIs
  console.log('\n--- Bonus APIs ---');
  await testEndpoint('Get My Bonus Requests', 'get', '/bonus/my-requests', null, true);
  await testEndpoint('Get My Bonuses', 'get', '/bonus/my-bonuses', null, true);

  // Test Profile Edit APIs
  console.log('\n--- Profile Edit APIs ---');
  await testEndpoint('Get My Requests', 'get', '/profile-edit/my-requests', null, true);

  // Test Manager Project APIs
  console.log('\n--- Manager Project APIs ---');
  await testEndpoint('Get My Projects (Manager)', 'get', '/manager/projects', null, true);

  // Test BGV APIs
  console.log('\n--- BGV APIs ---');
  await testEndpoint('Get My BGV Status', 'get', '/bgv/my-status', null, true);
  await testEndpoint('Get Company BGV Requests', 'get', '/bgv/company-requests', null, true);

  // Test HR Policy APIs
  console.log('\n--- HR Policy APIs ---');
  await testEndpoint('Get All Policies', 'get', '/policies', null, true);
  await testEndpoint('Get Active Policies', 'get', '/policies/active', null, true);
  await testEndpoint('Get My Pending Policies', 'get', '/policies/my-pending', null, true);

  // Test Resignation APIs
  console.log('\n--- Resignation APIs ---');
  await testEndpoint('Get My Resignation', 'get', '/resignation/my-requests', null, true);
  await testEndpoint('Get Pending for Manager', 'get', '/resignation/pending/manager', null, true);

  // Test Notification APIs
  console.log('\n--- Notification APIs ---');
  await testEndpoint('Get My Notifications', 'get', '/notifications', null, true);
  await testEndpoint('Get Unread Count', 'get', '/notifications/count', null, true);

  // Test Employee Exit APIs
  console.log('\n--- Employee Exit APIs ---');
  await testEndpoint('Get My Exit Details', 'get', '/employee/exit/my-exit', null, true);
  await testEndpoint('Get All Exit Employees', 'get', '/admin/exit/all', null, true);

  // Test Document APIs
  console.log('\n--- Document APIs ---');
  await testEndpoint('Get My Documents', 'get', '/employee/documents/my-documents', null, true);
  await testEndpoint('Get All Documents (Admin)', 'get', '/admin/documents/all', null, true);

  // Print Summary
  console.log('\n' + '='.repeat(60));
  console.log('TEST SUMMARY');
  console.log('='.repeat(60));
  console.log(`Total Endpoints Tested: ${results.passed + results.failed}`);
  console.log(`Passed: ${results.passed} ✓`);
  console.log(`Failed: ${results.failed} ✗`);
  console.log(`Success Rate: ${((results.passed / (results.passed + results.failed)) * 100).toFixed(1)}%`);
  console.log('='.repeat(60));

  // List failed endpoints
  if (results.failed > 0) {
    console.log('\nFailed Endpoints:');
    results.endpoints
      .filter(e => e.status === 'FAIL')
      .forEach(e => console.log(`  - ${e.name}: ${e.statusCode}`));
  }

  return results.failed === 0;
}

// Run tests
runTests()
  .then(success => {
    process.exit(success ? 0 : 1);
  })
  .catch(error => {
    console.error('Test execution failed:', error);
    process.exit(1);
  });
