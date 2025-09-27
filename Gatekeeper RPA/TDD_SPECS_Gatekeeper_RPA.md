# Documentation-Driven Test Development (DGTS) Specifications
# Gatekeeper RPA System

**Based on PRP v2.0**
**Created**: 2025-09-23
**Purpose**: Test specifications derived from PRP requirements

---

## 1. Test Strategy Overview

### 1.1 DGTS Implementation Workflow

```
Phase 1: Requirements Analysis (Week 1-2)
├── Parse PRD requirements
├── Extract testable acceptance criteria
├── Create test specifications
└── Define validation matrices

Phase 2: Test Creation (Week 2-4)
├── Write unit tests first
├── Implement integration tests
├── Create E2E test scenarios
└── Define performance benchmarks

Phase 3: Implementation (Week 3-8)
├── Code to pass tests
├── Validate coverage >95%
├── Anti-gaming validation
└── Quality gate checks

Phase 4: Validation (Week 8-16)
├── Continuous testing
├── Performance monitoring
├── Security validation
└── Compliance verification
```

### 1.2 Test Coverage Matrix

| Module | Unit Tests | Integration Tests | E2E Tests | Security Tests | Performance Tests |
|--------|------------|------------------|-----------|----------------|------------------|
| WhatsApp Gateway | 95% | 90% | 85% | 100% | 90% |
| Ticket Management | 95% | 90% | 85% | 100% | 90% |
| Audit Engine | 95% | 90% | 85% | 100% | 90% |
| RPA Service | 95% | 90% | 85% | 100% | 90% |
| Database Layer | 95% | 90% | N/A | 100% | 90% |
| Authentication | 95% | 90% | 85% | 100% | 90% |
| API Endpoints | 95% | 90% | 85% | 100% | 90% |

---

## 2. WhatsApp Gateway Test Specifications

### 2.1 Unit Tests

#### Message Processing Tests
```typescript
// whatsapp.service.spec.ts
describe('WhatsAppService', () => {
  let service: WhatsAppService;
  let mockQueue: Queue;

  beforeEach(() => {
    service = new WhatsAppService(mockQueue);
  });

  describe('extractDRNumber', () => {
    it('should extract DR number from simple message', () => {
      const message = 'Please check DR1854443';
      const result = service.extractDRNumber(message);
      expect(result).toBe('DR1854443');
    });

    it('should extract DR number from complex message', () => {
      const message = 'Hi, can you verify DR1854443 for me? Thanks!';
      const result = service.extractDRNumber(message);
      expect(result).toBe('DR1854443');
    });

    it('should return null for invalid DR format', () => {
      const message = 'Please check ABC123';
      const result = service.extractDRNumber(message);
      expect(result).toBeNull();
    });

    it('should return null for empty message', () => {
      const result = service.extractDRNumber('');
      expect(result).toBeNull();
    });
  });

  describe('createAuditTicket', () => {
    it('should create ticket with valid data', async () => {
      const ticketData = {
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        messageContent: 'DR1854443',
        messageTimestamp: new Date()
      };

      const ticket = await service.createAuditTicket(ticketData);

      expect(ticket).toMatchObject({
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        status: 'pending'
      });
      expect(ticket.ticketNumber).toMatch(/^TICKET-\d{8}-\d{4}$/);
    });

    it('should validate phone number format', async () => {
      const invalidData = {
        drNumber: 'DR1854443',
        technicianNumber: '123456',
        messageContent: 'DR1854443',
        messageTimestamp: new Date()
      };

      await expect(service.createAuditTicket(invalidData))
        .rejects.toThrow('Invalid phone number format');
    });
  });

  describe('handleMessage', () => {
    it('should queue audit task for valid DR number', async () => {
      const message = {
        from: '+27821234567',
        body: 'DR1854443',
        timestamp: new Date()
      };

      await service.handleMessage(message);

      expect(mockQueue.add).toHaveBeenCalledWith(
        'process-audit',
        expect.objectContaining({
          ticketId: expect.any(String),
          priority: 'normal'
        })
      );
    });

    it('should send help message for invalid DR', async () => {
      const message = {
        from: '+27821234567',
        body: 'Hello',
        timestamp: new Date()
      };

      await service.handleMessage(message);

      expect(mockQueue.add).not.toHaveBeenCalled();
    });
  });
});
```

#### Rate Limiting Tests
```typescript
// rate-limiter.spec.ts
describe('RateLimiter', () => {
  let limiter: RateLimiter;

  beforeEach(() => {
    limiter = new RateLimiter({
      points: 10,
      duration: 60
    });
  });

  it('should allow requests within limit', async () => {
    const key = '+27821234567';

    for (let i = 0; i < 10; i++) {
      await expect(limiter.consume(key)).resolves.not.toThrow();
    }
  });

  it('should block requests over limit', async () => {
    const key = '+27821234567';

    // Use up all points
    for (let i = 0; i < 10; i++) {
      await limiter.consume(key);
    }

    // Next request should be blocked
    await expect(limiter.consume(key)).rejects.toThrow();
  });

  it('should reset after duration', async () => {
    const key = '+27821234567';

    // Use up all points
    for (let i = 0; i < 10; i++) {
      await limiter.consume(key);
    }

    // Wait for reset
    await new Promise(resolve => setTimeout(resolve, 61000));

    // Should allow requests again
    await expect(limiter.consume(key)).resolves.not.toThrow();
  });
});
```

### 2.2 Integration Tests

#### WhatsApp API Integration Tests
```typescript
// whatsapp.integration.spec.ts
describe('WhatsApp Integration', () => {
  let testServer: TestServer;
  let whatiTicketAPI: WhatiTicketAPI;

  beforeAll(async () => {
    testServer = new TestServer();
    await testServer.start();
    whatiTicketAPI = new WhatiTicketAPI({
      baseUrl: testServer.url,
      apiKey: 'test-api-key'
    });
  });

  afterAll(async () => {
    await testServer.stop();
  });

  it('should send message via WhatiTicket API', async () => {
    testServer.mockPOST('/api/messages', 200, { success: true });

    const result = await whatiTicketAPI.sendMessage({
      to: '+27821234567',
      message: 'Test message',
      ticketId: '123'
    });

    expect(result.success).toBe(true);
    expect(testServer.lastRequest).toMatchObject({
      method: 'POST',
      path: '/api/messages',
      body: {
        to: '+27821234567',
        message: 'Test message',
        ticketId: '123'
      }
    });
  });

  it('should handle API errors', async () => {
    testServer.mockPOST('/api/messages', 500, { error: 'Server error' });

    await expect(whatiTicketAPI.sendMessage({
      to: '+27821234567',
      message: 'Test message'
    })).rejects.toThrow('Server error');
  });
});
```

### 2.3 E2E Tests

#### Message Flow E2E Test
```typescript
// message-flow.e2e.spec.ts
describe('Message Flow E2E', () => {
  let app: Application;
  let browser: Browser;
  let page: Page;

  beforeAll(async () => {
    app = await startTestApp();
    browser = await chromium.launch();
  });

  afterAll(async () => {
    await browser.close();
    await app.close();
  });

  beforeEach(async () => {
    page = await browser.newPage();
  });

  it('should complete full message flow', async () => {
    // Send WhatsApp message
    await page.goto('http://localhost:3020/test/whatsapp');
    await page.fill('#phone', '+27821234567');
    await page.fill('#message', 'DR1854443');
    await page.click('#send');

    // Wait for processing
    await page.waitForSelector('#status:has-text("Processing")');

    // Check ticket creation
    const ticketsResponse = await page.request.get('http://localhost:3000/api/tickets');
    const tickets = await ticketsResponse.json();
    expect(tickets).toHaveLength(1);
    expect(tickets[0].drNumber).toBe('DR1854443');

    // Simulate RPA completion
    await page.request.post('http://localhost:3000/test/rpa/complete', {
      data: { ticketId: tickets[0].id }
    });

    // Verify response sent
    await page.waitForSelector('#status:has-text("Response sent")');

    const messages = await page.evaluate(() => {
      return window.testWhatsAppMessages;
    });
    expect(messages[messages.length - 1].content)
      .toMatch(/✅ DR1854443 verified/);
  });
});
```

---

## 3. Ticket Management Test Specifications

### 3.1 Unit Tests

#### Ticket Creation Tests
```typescript
// ticket.service.spec.ts
describe('TicketService', () => {
  let service: TicketService;
  let mockDb: Database;

  beforeEach(() => {
    mockDb = createMockDatabase();
    service = new TicketService(mockDb);
  });

  describe('createTicket', () => {
    it('should create ticket with all required fields', async () => {
      const ticketData = {
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443',
        priority: 'normal'
      };

      const ticket = await service.createTicket(ticketData);

      expect(ticket).toMatchObject({
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        status: 'pending',
        priority: 'normal'
      });
      expect(ticket.id).toBeDefined();
      expect(ticket.ticketNumber).toMatch(/^TICKET-\d{8}-\d{4}$/);
    });

    it('should validate required fields', async () => {
      const invalidData = {
        drNumber: '',
        technicianNumber: '+27821234567'
      };

      await expect(service.createTicket(invalidData))
        .rejects.toThrow('DR number is required');
    });

    it('should generate unique ticket numbers', async () => {
      const ticket1 = await service.createTicket({
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443'
      });

      const ticket2 = await service.createTicket({
        drNumber: 'DR1854444',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854444'
      });

      expect(ticket1.ticketNumber).not.toBe(ticket2.ticketNumber);
    });
  });

  describe('updateTicketStatus', () => {
    it('should update ticket status', async () => {
      const ticket = await service.createTicket({
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443'
      });

      const updated = await service.updateTicketStatus(ticket.id, 'in_progress');

      expect(updated.status).toBe('in_progress');
      expect(updated.updatedAt).not.toBe(ticket.updatedAt);
    });

    it('should throw error for invalid status', async () => {
      const ticket = await service.createTicket({
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443'
      });

      await expect(service.updateTicketStatus(ticket.id, 'invalid'))
        .rejects.toThrow('Invalid status');
    });
  });

  describe('assignTicket', () => {
    it('should assign ticket to user', async () => {
      const ticket = await service.createTicket({
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443'
      });

      const assigned = await service.assignTicket(ticket.id, 'auditor1');

      expect(assigned.assignedTo).toBe('auditor1');
      expect(assigned.status).toBe('assigned');
    });

    it('should create assignment record', async () => {
      const ticket = await service.createTicket({
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443'
      });

      await service.assignTicket(ticket.id, 'auditor1');

      const assignments = await mockDb.query(
        'SELECT * FROM ticket_assignments WHERE ticket_id = $1',
        [ticket.id]
      );
      expect(assignments).toHaveLength(1);
      expect(assignments[0].assigned_to).toBe('auditor1');
    });
  });
});
```

#### Ticket Query Tests
```typescript
// ticket-query.service.spec.ts
describe('TicketQueryService', () => {
  let service: TicketQueryService;
  let mockDb: Database;

  beforeEach(() => {
    mockDb = createMockDatabase();
    service = new TicketQueryService(mockDb);
  });

  describe('getTicketsByStatus', () => {
    it('should return tickets with specified status', async () => {
      // Create test tickets
      await createTestTicket(mockDb, { status: 'pending' });
      await createTestTicket(mockDb, { status: 'pending' });
      await createTestTicket(mockDb, { status: 'completed' });

      const tickets = await service.getTicketsByStatus('pending');

      expect(tickets).toHaveLength(2);
      tickets.forEach(ticket => {
        expect(ticket.status).toBe('pending');
      });
    });

    it('should return empty array for no matching tickets', async () => {
      await createTestTicket(mockDb, { status: 'completed' });

      const tickets = await service.getTicketsByStatus('pending');

      expect(tickets).toHaveLength(0);
    });
  });

  describe('getTicketsByTechnician', () => {
    it('should return tickets for specific technician', async () => {
      await createTestTicket(mockDb, { technicianNumber: '+27821234567' });
      await createTestTicket(mockDb, { technicianNumber: '+27821234567' });
      await createTestTicket(mockDb, { technicianNumber: '+27821234568' });

      const tickets = await service.getTicketsByTechnician('+27821234567');

      expect(tickets).toHaveLength(2);
      tickets.forEach(ticket => {
        expect(ticket.technicianNumber).toBe('+27821234567');
      });
    });
  });

  describe('getTicketMetrics', () => {
    it('should calculate correct metrics', async () => {
      const now = new Date();
      const yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

      await createTestTicket(mockDb, { status: 'completed', completedAt: now });
      await createTestTicket(mockDb, { status: 'completed', completedAt: now });
      await createTestTicket(mockDb, { status: 'pending' });

      const metrics = await service.getTicketMetrics();

      expect(metrics.total).toBe(3);
      expect(metrics.completed).toBe(2);
      expect(metrics.pending).toBe(1);
      expect(metrics.completionRate).toBeCloseTo(66.67, 2);
    });
  });
});
```

### 3.2 Integration Tests

#### Ticket Workflow Integration Test
```typescript
// ticket-workflow.integration.spec.ts
describe('Ticket Workflow Integration', () => {
  let app: TestApplication;
  let db: TestDatabase;

  beforeAll(async () => {
    db = new TestDatabase();
    await db.initialize();
    app = new TestApplication({ database: db });
    await app.start();
  });

  afterAll(async () => {
    await app.stop();
    await db.close();
  });

  it('should handle complete ticket lifecycle', async () => {
    // Create ticket via API
    const createResponse = await app.request.post('/api/tickets', {
      data: {
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443'
      }
    });

    expect(createResponse.status()).toBe(201);
    const ticket = await createResponse.json();

    // Assign ticket
    const assignResponse = await app.request.post(`/api/tickets/${ticket.id}/assign`, {
      data: { assignedTo: 'auditor1' }
    });

    expect(assignResponse.status()).toBe(200);
    const assignedTicket = await assignResponse.json();
    expect(assignedTicket.assignedTo).toBe('auditor1');

    // Update status to in progress
    const updateResponse = await app.request.put(`/api/tickets/${ticket.id}`, {
      data: { status: 'in_progress' }
    });

    expect(updateResponse.status()).toBe(200);

    // Complete ticket
    const completeResponse = await app.request.put(`/api/tickets/${ticket.id}`, {
      data: { status: 'completed' }
    });

    expect(completeResponse.status()).toBe(200);

    // Verify in database
    const dbTicket = await db.query(
      'SELECT * FROM tickets WHERE id = $1',
      [ticket.id]
    );
    expect(dbTicket[0].status).toBe('completed');
    expect(dbTicket[0].assigned_to).toBe('auditor1');
  });
});
```

---

## 4. Audit Engine Test Specifications

### 4.1 Unit Tests

#### Compliance Checking Tests
```typescript
// compliance.service.spec.ts
describe('ComplianceService', () => {
  let service: ComplianceService;

  beforeEach(() => {
    service = new ComplianceService();
  });

  describe('checkPhotoCompliance', () => {
    it('should pass compliance with all required photos', () => {
      const photos = [
        { type: 'trench', url: 'photo1.jpg' },
        { type: 'ONT', url: 'photo2.jpg' },
        { type: 'termination', url: 'photo3.jpg' },
        { type: 'property', url: 'photo4.jpg' }
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(true);
      expect(result.complianceScore).toBe(100);
      expect(result.missingPhotos).toHaveLength(0);
    });

    it('should fail compliance with missing photos', () => {
      const photos = [
        { type: 'trench', url: 'photo1.jpg' },
        { type: 'ONT', url: 'photo2.jpg' }
        // Missing termination and property photos
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(false);
      expect(result.complianceScore).toBe(50);
      expect(result.missingPhotos).toEqual(
        expect.arrayContaining(['termination', 'property'])
      );
    });

    it('should handle no photos', () => {
      const result = service.checkPhotoCompliance([]);

      expect(result.isCompliant).toBe(false);
      expect(result.complianceScore).toBe(0);
      expect(result.missingPhotos).toEqual(
        expect.arrayContaining(['trench', 'ONT', 'termination', 'property'])
      );
    });
  });

  describe('calculateComplianceScore', () => {
    it('should calculate score correctly', () => {
      const requiredPhotos = ['trench', 'ONT', 'termination', 'property'];
      const foundPhotos = ['trench', 'ONT'];

      const score = service.calculateComplianceScore(requiredPhotos, foundPhotos);

      expect(score).toBe(50);
    });

    it('should handle empty arrays', () => {
      const score = service.calculateComplianceScore([], []);

      expect(score).toBe(100); // No requirements means 100% compliant
    });
  });

  describe('validatePhotoType', () => {
    it('should validate known photo types', () => {
      expect(service.validatePhotoType('trench')).toBe(true);
      expect(service.validatePhotoType('ONT')).toBe(true);
      expect(service.validatePhotoType('termination')).toBe(true);
      expect(service.validatePhotoType('property')).toBe(true);
    });

    it('should reject unknown photo types', () => {
      expect(service.validatePhotoType('unknown')).toBe(false);
      expect(service.validatePhotoType('')).toBe(false);
      expect(service.validatePhotoType(null)).toBe(false);
    });
  });
});
```

#### Audit Result Generation Tests
```typescript
// audit-result.service.spec.ts
describe('AuditResultService', () => {
  let service: AuditResultService;

  beforeEach(() => {
    service = new AuditResultService();
  });

  describe('generateAuditResult', () => {
    it('should generate result with all fields', () => {
      const auditData = {
        ticketId: 'uuid-1',
        drNumber: 'DR1854443',
        propertyId: 'PROP-123',
        jobId: 'JOB-456',
        address: '123 Main St',
        status: 'Home Installation: Installed',
        photos: [
          { type: 'trench', url: 'photo1.jpg', uploadTime: '2025-09-23T10:00:00Z' },
          { type: 'ONT', url: 'photo2.jpg', uploadTime: '2025-09-23T10:05:00Z' }
        ],
        metadata: {
          lastModified: '2025-09-23T09:00:00Z',
          technician: 'John Doe'
        }
      };

      const result = service.generateAuditResult(auditData);

      expect(result).toMatchObject({
        ticketId: 'uuid-1',
        propertyId: 'PROP-123',
        jobId: 'JOB-456',
        address: '123 Main St',
        installationStatus: 'Home Installation: Installed',
        photosRequired: expect.arrayContaining(['trench', 'ONT', 'termination', 'property']),
        photosFound: ['trench', 'ONT'],
        photosMissing: expect.arrayContaining(['termination', 'property']),
        complianceScore: 50,
        auditDetails: expect.objectContaining({
          totalPhotos: 2,
          auditTimestamp: expect.any(String)
        })
      });
    });

    it('should include all photos in audit details', () => {
      const auditData = {
        ticketId: 'uuid-1',
        photos: [
          { type: 'trench', url: 'photo1.jpg', uploadTime: '2025-09-23T10:00:00Z' },
          { type: 'additional', url: 'photo2.jpg', uploadTime: '2025-09-23T10:05:00Z' }
        ],
        metadata: {}
      };

      const result = service.generateAuditResult(auditData);

      expect(result.auditDetails.photos).toHaveLength(2);
      expect(result.auditDetails.photos[0]).toMatchObject({
        type: 'trench',
        url: 'photo1.jpg'
      });
    });
  });
});
```

### 4.2 Integration Tests

#### Audit Workflow Integration Test
```typescript
// audit-workflow.integration.spec.ts
describe('Audit Workflow Integration', () => {
  let app: TestApplication;
  let db: TestDatabase;
  let mockRPA: MockRPAService;

  beforeAll(async () => {
    db = new TestDatabase();
    await db.initialize();
    mockRPA = new MockRPAService();
    app = new TestApplication({
      database: db,
      rpaService: mockRPA
    });
    await app.start();
  });

  afterAll(async () => {
    await app.stop();
    await db.close();
  });

  it('should process audit from start to finish', async () => {
    // Create ticket
    const ticketResponse = await app.request.post('/api/tickets', {
      data: {
        drNumber: 'DR1854443',
        technicianNumber: '+27821234567',
        technicianName: 'John Doe',
        messageContent: 'DR1854443'
      }
    });

    const ticket = await ticketResponse.json();

    // Start audit
    const auditResponse = await app.request.post('/api/audit/process', {
      data: { ticketId: ticket.id }
    });

    expect(auditResponse.status()).toBe(202);

    // Mock RPA completion
    const rpaData = {
      propertyId: 'PROP-123',
      jobId: 'JOB-456',
      address: '123 Main St',
      status: 'Home Installation: Installed',
      photos: [
        { type: 'trench', url: 'photo1.jpg' },
        { type: 'ONT', url: 'photo2.jpg' },
        { type: 'termination', url: 'photo3.jpg' },
        { type: 'property', url: 'photo4.jpg' }
      ]
    };

    await mockRPA.completeAudit(ticket.id, rpaData);

    // Wait for processing
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Verify audit result
    const auditResult = await app.request.get(`/api/audit/${ticket.id}`);
    const result = await auditResult.json();

    expect(result.complianceScore).toBe(100);
    expect(result.photosMissing).toHaveLength(0);

    // Verify ticket status updated
    const updatedTicket = await app.request.get(`/api/tickets/${ticket.id}`);
    const ticketData = await updatedTicket.json();

    expect(ticketData.status).toBe('completed');
    expect(ticketData.completedAt).toBeDefined();
  });
});
```

---

## 5. RPA Service Test Specifications

### 5.1 Unit Tests

#### 1Map Login Tests
```typescript
// rpa-login.service.spec.ts
describe('RPALoginService', () => {
  let service: RPALoginService;
  let mockBrowser: MockBrowser;

  beforeEach(() => {
    mockBrowser = new MockBrowser();
    service = new RPALoginService(mockBrowser);
  });

  describe('login', () => {
    it('should login successfully with valid credentials', async () => {
      // Mock browser behavior
      mockBrowser.mockGoto('https://1map.example.com/login');
      mockBrowser.mockFill('#username', 'testuser');
      mockBrowser.mockFill('#password', 'testpass');
      mockBrowser.mockClick('#login-button');
      mockBrowser.mockWaitForNavigation();
      mockBrowser.mockUrl('https://1map.example.com/dashboard');

      await service.login('testuser', 'testpass');

      expect(mockBrowser.goto).toHaveBeenCalledWith('https://1map.example.com/login');
      expect(mockBrowser.fill).toHaveBeenCalledWith('#username', 'testuser');
      expect(mockBrowser.fill).toHaveBeenCalledWith('#password', 'testpass');
    });

    it('should handle login failure', async () => {
      mockBrowser.mockGoto('https://1map.example.com/login');
      mockBrowser.mockFill('#username', 'testuser');
      mockBrowser.mockFill('#password', 'wrongpass');
      mockBrowser.mockClick('#login-button');
      mockBrowser.mockWaitForSelector('#login-error', { state: 'visible' });

      await expect(service.login('testuser', 'wrongpass'))
        .rejects.toThrow('Login failed');
    });

    it('should handle MFA when required', async () => {
      mockBrowser.mockGoto('https://1map.example.com/login');
      mockBrowser.mockFill('#username', 'testuser');
      mockBrowser.mockFill('#password', 'testpass');
      mockBrowser.mockClick('#login-button');
      mockBrowser.mockWaitForSelector('#mfa-code', { state: 'visible' });
      mockBrowser.mockFill('#mfa-code', '123456');
      mockBrowser.mockClick('#verify-button');
      mockBrowser.mockWaitForNavigation();

      await service.login('testuser', 'testpass', '123456');

      expect(mockBrowser.fill).toHaveBeenCalledWith('#mfa-code', '123456');
    });
  });
});
```

#### Data Extraction Tests
```typescript
// rpa-extraction.service.spec.ts
describe('RPAExtractionService', () => {
  let service: RPAExtractionService;
  let mockPage: MockPage;

  beforeEach(() => {
    mockPage = new MockPage();
    service = new RPAExtractionService(mockPage);
  });

  describe('extractInstallationData', () => {
    it('should extract all installation data', async () => {
      // Mock page content
      mockPage.mockSelectorText('.property-id', 'PROP-123');
      mockPage.mockSelectorText('.job-id', 'JOB-456');
      mockPage.mockSelectorText('.address', '123 Main St, City');
      mockPage.mockSelectorText('.status', 'Home Installation: Installed');
      mockPage.mockSelectorText('.last-modified', '2025-09-23 10:00:00');

      const data = await service.extractInstallationData();

      expect(data).toMatchObject({
        propertyId: 'PROP-123',
        jobId: 'JOB-456',
        address: '123 Main St, City',
        status: 'Home Installation: Installed',
        lastModified: '2025-09-23 10:00:00'
      });
    });

    it('should handle missing data', async () => {
      mockPage.mockSelectorText('.property-id', 'PROP-123');
      // Job ID missing
      mockPage.mockSelectorText('.address', '123 Main St, City');

      const data = await service.extractInstallationData();

      expect(data.propertyId).toBe('PROP-123');
      expect(data.jobId).toBeUndefined();
      expect(data.address).toBe('123 Main St, City');
    });
  });

  describe('extractPhotos', () => {
    it('should extract all photos with metadata', async () => {
      const photoElements = [
        {
          type: 'trench',
          url: 'https://1map.example.com/photos/1.jpg',
          uploadTime: '2025-09-23T10:00:00Z',
          fileSize: '1024000'
        },
        {
          type: 'ONT',
          url: 'https://1map.example.com/photos/2.jpg',
          uploadTime: '2025-09-23T10:05:00Z',
          fileSize: '2048000'
        }
      ];

      mockPage.mockQuerySelectorAll('.photo-container', photoElements);

      const photos = await service.extractPhotos();

      expect(photos).toHaveLength(2);
      expect(photos[0]).toMatchObject({
        type: 'trench',
        url: 'https://1map.example.com/photos/1.jpg',
        uploadTime: '2025-09-23T10:00:00Z',
        fileSize: 1024000
      });
    });

    it('should handle no photos', async () => {
      mockPage.mockQuerySelectorAll('.photo-container', []);

      const photos = await service.extractPhotos();

      expect(photos).toHaveLength(0);
    });
  });
});
```

### 5.2 Integration Tests

#### RPA End-to-End Test
```typescript
// rpa-e2e.spec.ts
describe('RPA End-to-End', () => {
  let rpaService: RPAService;
  let mockBrowser: MockBrowser;
  let mockCredentialStore: CredentialStore;

  beforeAll(() => {
    mockBrowser = new MockBrowser();
    mockCredentialStore = new MockCredentialStore();
    rpaService = new RPAService(mockBrowser, mockCredentialStore);
  });

  describe('executeFullAudit', () => {
    it('should complete full audit workflow', async () => {
      // Setup mock browser workflow
      setupMockBrowserWorkflow(mockBrowser);

      const result = await rpaService.executeFullAudit('DR1854443');

      expect(result).toMatchObject({
        drNumber: 'DR1854443',
        propertyId: 'PROP-123',
        jobId: 'JOB-456',
        status: 'Home Installation: Installed',
        photos: expect.arrayContaining([
          expect.objectContaining({ type: 'trench' }),
          expect.objectContaining({ type: 'ONT' })
        ])
      });
      expect(result.screenshots).toHaveLength(3); // Login, search, results
    });

    it('should handle navigation errors', async () => {
      mockBrowser.mockGoto('https://1map.example.com/login');
      mockBrowser.mockThrow(new Error('Network error'));

      await expect(rpaService.executeFullAudit('DR1854443'))
        .rejects.toThrow('Network error');
    });

    it('should retry on temporary failures', async () => {
      let attempt = 0;
      mockBrowser.mockGoto(() => {
        attempt++;
        if (attempt === 1) {
          throw new Error('Temporary failure');
        }
        return Promise.resolve();
      });
      setupMockBrowserWorkflow(mockBrowser, { skipGoto: true });

      const result = await rpaService.executeFullAudit('DR1854443');

      expect(attempt).toBe(2);
      expect(result.drNumber).toBe('DR1854443');
    });
  });
});

function setupMockBrowserWorkflow(browser: MockBrowser, options: any = {}) {
  if (!options.skipGoto) {
    browser.mockGoto('https://1map.example.com/login');
  }
  browser.mockFill('#username', 'testuser');
  browser.mockFill('#password', 'testpass');
  browser.mockClick('#login-button');
  browser.mockWaitForNavigation();
  browser.mockUrl('https://1map.example.com/dashboard');
  browser.mockClick('#installations-link');
  browser.mockWaitForSelector('#search-input');
  browser.mockFill('#search-input', 'DR1854443');
  browser.mockClick('#search-button');
  browser.mockWaitForSelector('.installation-details');
  browser.mockScreenshot();
  browser.mockSelectorText('.property-id', 'PROP-123');
  browser.mockSelectorText('.job-id', 'JOB-456');
  browser.mockSelectorText('.status', 'Home Installation: Installed');
  browser.mockQuerySelectorAll('.photo-container', [
    { type: 'trench', url: 'photo1.jpg' },
    { type: 'ONT', url: 'photo2.jpg' }
  ]);
}
```

---

## 6. Performance Test Specifications

### 6.1 Load Tests

#### API Load Test
```typescript
// api-load.test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 100 },  // Ramp up
    { duration: '5m', target: 100 },  // Sustained load
    { duration: '2m', target: 500 },  // Ramp to peak
    { duration: '10m', target: 500 }, // Peak load
    { duration: '2m', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<200'],  // 95% of requests < 200ms
    http_req_failed: ['rate<0.01'],   // <1% error rate
    http_reqs: ['rate>100'],         // >100 requests/second
  },
};

const API_URL = 'http://localhost:3000';

export default function () {
  // Test ticket creation
  const ticketPayload = {
    drNumber: `DR${Math.floor(Math.random() * 10000000)}`,
    technicianNumber: `+2782${Math.floor(Math.random() * 10000000)}`,
    technicianName: 'Test Technician',
    messageContent: `DR${Math.floor(Math.random() * 10000000)}`
  };

  const createResponse = http.post(
    `${API_URL}/api/tickets`,
    JSON.stringify(ticketPayload),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(createResponse, {
    'ticket created': (r) => r.status === 201,
    'response time < 200ms': (r) => r.timings.duration < 200,
  });

  if (createResponse.status === 201) {
    const ticket = JSON.parse(createResponse.body);

    // Test ticket retrieval
    const getResponse = http.get(`${API_URL}/api/tickets/${ticket.id}`);

    check(getResponse, {
      'ticket retrieved': (r) => r.status === 200,
      'correct ticket': (r) => JSON.parse(r.body).id === ticket.id,
    });
  }

  sleep(0.1); // Small delay between requests
}
```

#### WhatsApp Processing Load Test
```typescript
// whatsapp-load.test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '1m', target: 50 },   // Simulate 50 concurrent technicians
    { duration: '5m', target: 50 },   // Sustained message flow
    { duration: '1m', target: 200 },  // Peak message flow
    { duration: '5m', target: 200 },  // Sustained peak
    { duration: '1m', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<100'],  // WhatsApp webhook should be fast
    http_req_failed: ['rate<0.005'],  // Very low error rate for messages
  },
};

export default function () {
  const messagePayload = {
    from: `+2782${Math.floor(Math.random() * 10000000)}`,
    body: `DR${Math.floor(Math.random() * 10000000)}`,
    timestamp: new Date().toISOString(),
    type: 'text'
  };

  const response = http.post(
    'http://localhost:3000/api/whatsapp/webhook',
    JSON.stringify(messagePayload),
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(response, {
    'message processed': (r) => r.status === 200,
    'processing time < 100ms': (r) => r.timings.duration < 100,
  });

  sleep(1); // Simulate 1 message per second per technician
}
```

### 6.2 Stress Tests

#### Database Stress Test
```typescript
// database-stress.test.js
import { Pool } from 'pg';
import { check } from 'k6';

const db = new Pool({
  host: __ENV.DB_HOST,
  port: __ENV.DB_PORT,
  user: __ENV.DB_USER,
  password: __ENV.DB_PASSWORD,
  database: __ENV.DB_NAME,
  max: 100, // Maximum number of connections
});

export let options = {
  stages: [
    { duration: '1m', target: 10 },   // Warm up
    { duration: '2m', target: 50 },   // Increase load
    { duration: '5m', target: 100 },  // Max connections
    { duration: '2m', target: 0 },    // Cool down
  ],
};

export default async function () {
  const client = await db.connect();

  try {
    // Insert test ticket
    const insertResult = await client.query(`
      INSERT INTO tickets (
        ticket_number, dr_number, technician_number,
        technician_name, message_content, status
      ) VALUES (
        'TICKET-20250923-' + floor(random() * 10000),
        'DR' + floor(random() * 10000000),
        '+2782' + floor(random() * 10000000),
        'Technician ' + floor(random() * 100),
        'Test message',
        'pending'
      ) RETURNING id
    `);

    // Query tickets
    const selectResult = await client.query(`
      SELECT * FROM tickets
      WHERE status = 'pending'
      LIMIT 10
    `);

    // Update ticket
    await client.query(`
      UPDATE tickets
      SET status = 'in_progress', updated_at = NOW()
      WHERE id = $1
    `, [insertResult.rows[0].id]);

    check(insertResult, {
      'ticket inserted': (r) => r.rows.length > 0,
    });

    check(selectResult, {
      'tickets queried': (r) => r.rows.length >= 0,
    });

  } finally {
    client.release();
  }
}
```

---

## 7. Security Test Specifications

### 7.1 Authentication Tests

#### JWT Token Tests
```typescript
// auth-jwt.spec.ts
describe('JWT Authentication', () => {
  let authService: AuthService;

  beforeEach(() => {
    authService = new AuthService();
  });

  describe('generateToken', () => {
    it('should generate valid JWT token', () => {
      const payload = { userId: '123', role: 'admin' };
      const token = authService.generateToken(payload);

      expect(token).toBeDefined();
      expect(typeof token).toBe('string');
    });

    it('should include correct claims', () => {
      const payload = { userId: '123', role: 'admin' };
      const token = authService.generateToken(payload);

      const decoded = authService.verifyToken(token);

      expect(decoded.userId).toBe('123');
      expect(decoded.role).toBe('admin');
      expect(decoded.iat).toBeDefined();
      expect(decoded.exp).toBeDefined();
    });

    it('should set appropriate expiration', () => {
      const payload = { userId: '123' };
      const token = authService.generateToken(payload, '15m');

      const decoded = authService.verifyToken(token);
      const expirationTime = decoded.exp * 1000; // Convert to milliseconds
      const expectedExpiration = Date.now() + 15 * 60 * 1000;

      expect(expirationTime).toBeCloseTo(expectedExpiration, 1000); // Within 1 second
    });
  });

  describe('verifyToken', () => {
    it('should verify valid token', () => {
      const payload = { userId: '123', role: 'admin' };
      const token = authService.generateToken(payload);

      const decoded = authService.verifyToken(token);

      expect(decoded).toMatchObject(payload);
    });

    it('should reject expired token', () => {
      const payload = { userId: '123' };
      const token = authService.generateToken(payload, '-1h'); // Expired

      expect(() => authService.verifyToken(token))
        .toThrow('Token expired');
    });

    it('should reject invalid token', () => {
      const invalidToken = 'invalid.token.here';

      expect(() => authService.verifyToken(invalidToken))
        .toThrow('Invalid token');
    });

    it('should reject tampered token', () => {
      const payload = { userId: '123', role: 'user' };
      const token = authService.generateToken(payload);

      // Tamper with token
      const tamperedToken = token.replace('user', 'admin');

      expect(() => authService.verifyToken(tamperedToken))
        .toThrow('Invalid signature');
    });
  });
});
```

### 7.2 Authorization Tests

#### Role-Based Access Control Tests
```typescript
// rbac.spec.ts
describe('Role-Based Access Control', () => {
  let rbacService: RBACService;

  beforeEach(() => {
    rbacService = new RBACService();
  });

  describe('canAccess', () => {
    it('should allow admin to access all resources', () => {
      const user = { id: '1', role: 'admin' };

      expect(rbacService.canAccess(user, '/api/tickets', 'GET')).toBe(true);
      expect(rbacService.canAccess(user, '/api/users', 'GET')).toBe(true);
      expect(rbacService.canAccess(user, '/api/admin', 'GET')).toBe(true);
    });

    it('should restrict auditor access', () => {
      const user = { id: '2', role: 'auditor' };

      expect(rbacService.canAccess(user, '/api/tickets', 'GET')).toBe(true);
      expect(rbacService.canAccess(user, '/api/audit', 'POST')).toBe(true);
      expect(rbacService.canAccess(user, '/api/admin', 'GET')).toBe(false);
      expect(rbacService.canAccess(user, '/api/users', 'GET')).toBe(false);
    });

    it('should restrict technician access', () => {
      const user = { id: '3', role: 'technician' };

      expect(rbacService.canAccess(user, '/api/tickets', 'POST')).toBe(true);
      expect(rbacService.canAccess(user, '/api/tickets/1', 'GET')).toBe(true);
      expect(rbacService.canAccess(user, '/api/audit', 'GET')).toBe(false);
      expect(rbacService.canAccess(user, '/api/users', 'GET')).toBe(false);
    });
  });

  describe('canModify', () => {
    it('should allow users to modify their own tickets', () => {
      const user = { id: '1', role: 'technician' };

      expect(rbacService.canModify(user, 'tickets', { technicianId: '1' })).toBe(true);
      expect(rbacService.canModify(user, 'tickets', { technicianId: '2' })).toBe(false);
    });

    it('should allow auditors to modify assigned tickets', () => {
      const user = { id: '2', role: 'auditor' };

      expect(rbacService.canModify(user, 'tickets', { assignedTo: '2' })).toBe(true);
      expect(rbacService.canModify(user, 'tickets', { assignedTo: '3' })).toBe(false);
    });

    it('should allow admins to modify any ticket', () => {
      const user = { id: '1', role: 'admin' };

      expect(rbacService.canModify(user, 'tickets', { technicianId: '2' })).toBe(true);
      expect(rbacService.canModify(user, 'tickets', { assignedTo: '3' })).toBe(true);
    });
  });
});
```

### 7.3 Input Validation Tests

#### SQL Injection Tests
```typescript
// sql-injection.spec.ts
describe('SQL Injection Protection', () => {
  let ticketService: TicketService;
  let mockDb: MockDatabase;

  beforeEach(() => {
    mockDb = new MockDatabase();
    ticketService = new TicketService(mockDb);
  });

  it('should escape SQL in search parameters', async () => {
    const maliciousQuery = "'; DROP TABLE tickets; --";

    await expect(ticketService.searchTickets(maliciousQuery))
      .resolves.not.toThrow();

    // Verify query was properly escaped
    const lastQuery = mockDb.getLastQuery();
    expect(lastQuery).not.toContain("DROP TABLE");
  });

  it('should handle OR 1=1 injection attempts', async () => {
    const maliciousInput = "1' OR '1'='1";

    const tickets = await ticketService.searchTickets(maliciousInput);

    // Should return no results or valid results, not all records
    expect(Array.isArray(tickets)).toBe(true);
  });

  it('should validate ticket ID format', async () => {
    const maliciousId = "123'; DROP TABLE tickets; --";

    await expect(ticketService.getTicket(maliciousId))
      .rejects.toThrow('Invalid ticket ID format');
  });
});
```

#### XSS Protection Tests
```typescript
// xss-protection.spec.ts
describe('XSS Protection', () => {
  let app: TestApplication;

  beforeAll(async () => {
    app = new TestApplication();
    await app.start();
  });

  afterAll(async () => {
    await app.stop();
  });

  it('should escape HTML in message content', async () => {
    const maliciousMessage = '<script>alert("xss")</script>DR1234567';

    const response = await app.request.post('/api/tickets', {
      data: {
        drNumber: 'DR1234567',
        technicianNumber: '+27821234567',
        technicianName: 'Test',
        messageContent: maliciousMessage
      }
    });

    const ticket = await response.json();

    // Script should be escaped, not executed
    expect(ticket.messageContent).not.toContain('<script>');
    expect(ticket.messageContent).toContain('&lt;script&gt;');
  });

  it('should sanitize HTML in response messages', async () => {
    const maliciousResponse = '<img src=x onerror=alert("xss")>';

    const response = await app.request.post('/api/test/response', {
      data: { message: maliciousResponse }
    });

    const result = await response.json();

    expect(result.message).not.toContain('onerror=');
    expect(result.message).toContain('&lt;img');
  });
});
```

---

## 8. DGTS Validation Tests

### 8.1 Anti-Gaming Tests

#### Gaming Detection Tests
```typescript
// anti-gaming.spec.ts
describe('Anti-Gaming System', () => {
  let gamingDetector: GamingDetector;
  let validator: DGTSValidator;

  beforeEach(() => {
    gamingDetector = new GamingDetector();
    validator = new DGTSValidator();
  });

  describe('detectFakeImplementations', () => {
    it('should detect hardcoded mock returns', () => {
      const fileChanges = [
        {
          path: 'audit.service.ts',
          content: `
            function calculateScore() {
              return "mock_data"; // Always returns mock
            }
          `
        }
      ];

      const patterns = gamingDetector.detect(fileChanges);

      expect(patterns).toContainEqual(
        expect.objectContaining({
          type: 'FAKE_IMPLEMENTATION',
          file: 'audit.service.ts',
          severity: 'critical'
        })
      );
    });

    it('should detect stub functions', () => {
      const fileChanges = [
        {
          path: 'rpa.service.ts',
          content: `
            async function extractPhotos() {
              // TODO: implement photo extraction
              return [];
            }
          `
        }
      ];

      const patterns = gamingDetector.detect(fileChanges);

      expect(patterns).toContainEqual(
        expect.objectContaining({
          type: 'STUB_FUNCTION',
          file: 'rpa.service.ts',
          severity: 'high'
        })
      );
    });
  });

  describe('detectTestGaming', () => {
    it('should detect always-true assertions', () => {
      const testFile = `
        test('should pass', () => {
          assert(true); // Always passes
        });
      `;

      const gaming = validator.validateTests(testFile);

      expect(gaming.hasGaming).toBe(true);
      expect(gaming.violations).toContainEqual(
        expect.objectContaining({
          type: 'ALWAYS_TRUE_ASSERTION'
        })
      );
    });

    it('should detect empty test implementations', () => {
      const testFile = `
        test('should do something', () => {
          // Empty test
        });
      `;

      const gaming = validator.validateTests(testFile);

      expect(gaming.hasGaming).toBe(true);
      expect(gaming.violations).toContainEqual(
        expect.objectContaining({
          type: 'EMPTY_TEST'
        })
      );
    });
  });

  describe('calculateGamingScore', () => {
    it('should calculate score based on violations', () => {
      const violations = [
        { type: 'FAKE_IMPLEMENTATION', weight: 1.0 },
        { type: 'COMMENTED_VALIDATION', weight: 0.8 },
        { type: 'STUB_FUNCTION', weight: 0.6 }
      ];

      const score = validator.calculateGamingScore(violations);

      expect(score).toBeGreaterThan(0.8); // High gaming score
    });

    it('should return 0 for no violations', () => {
      const score = validator.calculateGamingScore([]);

      expect(score).toBe(0);
    });
  });
});
```

#### Coverage Validation Tests
```typescript
// coverage-validation.spec.ts
describe('Coverage Validation', () => {
  let coverageValidator: CoverageValidator;

  beforeEach(() => {
    coverageValidator = new CoverageValidator();
  });

  describe('validateCoverage', () => {
    it('should pass with 95% coverage', () => {
      const coverage = {
        total: 1000,
        covered: 950,
        percentage: 95
      };

      const result = coverageValidator.validate(coverage);

      expect(result.passed).toBe(true);
      expect(result.percentage).toBe(95);
    });

    it('should fail with 94% coverage', () => {
      const coverage = {
        total: 1000,
        covered: 940,
        percentage: 94
      };

      const result = coverageValidator.validate(coverage);

      expect(result.passed).toBe(false);
      expect(result.message).toContain('94% is below 95% threshold');
    });

    it('should fail if uncovered lines contain critical paths', () => {
      const coverage = {
        total: 100,
        covered: 96,
        percentage: 96,
        uncoveredLines: [
          'if (error) throw new Error();',
          'return database.query(sql);'
        ]
      };

      const result = coverageValidator.validate(coverage);

      expect(result.passed).toBe(false);
      expect(result.message).toContain('Critical paths uncovered');
    });
  });

  describe('validateFileCoverage', () => {
    it('should require minimum coverage per file', () => {
      const fileCoverage = {
        'auth.service.ts': 90,
        'audit.service.ts': 98,
        'ticket.service.ts': 92
      };

      const result = coverageValidator.validateFiles(fileCoverage, 95);

      expect(result.passed).toBe(false);
      expect(result.failedFiles).toContain('auth.service.ts');
      expect(result.failedFiles).toContain('ticket.service.ts');
    });
  });
});
```

---

## 9. Test Execution Plan

### 9.1 Test Execution Order

```
Phase 1: Unit Tests (Week 1-4)
├── Run: npm run test:unit
├── Coverage: >95% required
├── Time: ~5 minutes
└── Fail on: Any test failure

Phase 2: Integration Tests (Week 3-6)
├── Run: npm run test:integration
├── Coverage: >90% required
├── Time: ~15 minutes
└── Dependencies: Unit tests passing

Phase 3: E2E Tests (Week 5-8)
├── Run: npm run test:e2e
├── Coverage: >85% required
├── Time: ~30 minutes
└── Dependencies: Integration tests passing

Phase 4: Security Tests (Week 7-10)
├── Run: npm run test:security
├── Coverage: 100% required
├── Time: ~45 minutes
└── Dependencies: E2E tests passing

Phase 5: Performance Tests (Week 9-12)
├── Run: npm run test:performance
├── Benchmarks: Must meet targets
├── Time: ~60 minutes
└── Dependencies: Security tests passing

Phase 6: DGTS Validation (Continuous)
├── Run: npm run test:dgts
├── Gaming Score: <0.3 required
├── Time: ~2 minutes
└── Run before every commit
```

### 9.2 Test Environment Setup

```bash
#!/bin/bash
# setup-test-env.sh

echo "Setting up test environment..."

# Install dependencies
npm ci
pip install -r requirements-test.txt

# Create test database
createdb gatekeeper_test
psql gatekeeper_test < schema.sql

# Set environment variables
export NODE_ENV=test
export DATABASE_URL=postgresql://user:pass@localhost:5432/gatekeeper_test
export REDIS_URL=redis://localhost:6379/1

# Start test services
docker-compose -f docker-compose.test.yml up -d

# Wait for services
echo "Waiting for services..."
sleep 10

# Run database migrations
npm run db:migrate

# Seed test data
npm run db:seed

echo "Test environment ready!"
```

### 9.3 Test CI/CD Pipeline

```yaml
# .github/workflows/test.yml
name: Test Pipeline

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        node-version: [18.x]

    steps:
      - uses: actions/checkout@v3

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: ${{ matrix.node-version }}

      - name: Install dependencies
        run: |
          npm ci
          npm install -g @playwright/test

      - name: Run DGTS validation
        run: |
          python scripts/dgts_validate.py
          npm run validate:no-gaming

      - name: Run unit tests
        run: |
          npm run test:unit
          npm run test:coverage

      - name: Run integration tests
        run: npm run test:integration
        env:
          NODE_ENV: test

      - name: Run E2E tests
        run: npm run test:e2e
        env:
          NODE_ENV: test

      - name: Run security tests
        run: |
          npm audit --audit-level moderate
          npm run test:security

      - name: Upload coverage
        uses: codecov/codecov-action@v3

  performance-test:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
      - uses: actions/checkout@v3

      - name: Setup performance test environment
        run: |
          npm ci
          npm install -g k6

      - name: Run load tests
        run: |
          k6 run tests/load/api-load.test.js
          k6 run tests/load/whatsapp-load.test.js

      - name: Generate performance report
        run: npm run test:report
```

---

## 10. Test Reporting and Metrics

### 10.1 Test Metrics Dashboard

#### Key Metrics to Track
```yaml
test_metrics:
  execution:
    total_tests: 1500
    passing_tests: 1485
    failing_tests: 0
    skipped_tests: 15
    execution_time: "12m 34s"

  coverage:
    overall_coverage: 96.5%
    unit_coverage: 97.2%
    integration_coverage: 94.8%
    e2e_coverage: 87.3%
    security_coverage: 100%

  quality:
    gaming_score: 0.12
    code_quality_score: 9.2/10
    security_vulnerabilities: 0
    performance_score: 8.8/10

  trends:
    coverage_trend: "+2.3%"
    test_execution_trend: "-15s"
    failure_rate_trend: "-0.5%"
```

### 10.2 Test Report Template

```markdown
# Test Execution Report

**Date**: 2025-09-23
**Commit**: abc1234
**Branch**: feature/whatsapp-integration

## Executive Summary
- ✅ All test suites passing
- ✅ Coverage at 96.5% (above 95% target)
- ✅ No security vulnerabilities
- ✅ Gaming score at 0.12 (below 0.3 threshold)

## Test Results

| Test Suite | Total | Passed | Failed | Skipped | Coverage |
|------------|-------|--------|--------|---------|----------|
| Unit Tests | 800 | 800 | 0 | 5 | 97.2% |
| Integration | 400 | 395 | 0 | 5 | 94.8% |
| E2E Tests | 200 | 190 | 0 | 10 | 87.3% |
| Security | 100 | 100 | 0 | 0 | 100% |
| **Total** | **1500** | **1485** | **0** | **20** | **96.5%** |

## Quality Metrics

### DGTS Validation
- Gaming Score: 0.12 (Good)
- No anti-patterns detected
- All validations passed

### Code Quality
- ESLint: 0 errors, 0 warnings
- TypeScript: 0 errors
- No console.log statements

### Performance
- API Response Time: 156ms (target: <200ms)
- Test Execution Time: 12m 34s
- Memory Usage: 512MB peak

## Recommendations
1. Continue current development practices
2. Focus on increasing E2E coverage
3. Monitor performance in production

## Next Steps
- Merge to develop branch
- Schedule integration testing
- Prepare for staging deployment
```

---

This comprehensive test specification document ensures that all requirements from the PRP are properly tested with >95% coverage while preventing gaming through the DGTS validation system. The tests are organized by module and type, with clear acceptance criteria derived from the PRD requirements.

**Document Version**: 1.0
**Created**: 2025-09-23
**Next Review**: After Phase 1 completion