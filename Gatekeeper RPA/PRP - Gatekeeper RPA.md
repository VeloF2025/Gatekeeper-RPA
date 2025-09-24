# Project Requirements Plan (PRP) - Gatekeeper RPA System

**Project**: Gatekeeper RPA - WhatsApp Ticketing & Audit System
**Version**: v2.0
**Date**: 2025-09-23
**Owner**: Velocity Fibre / FibreFlow
**Development Methodology**: Documentation-Driven Test Development (DGTS)

---

## 1. Project Architecture

### 1.1 System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Gatekeeper RPA System                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐   │
│  │   WhatsApp     │  │   Project       │  │   Audit         │   │
│  │   Gateway      │  │   Management    │  │   Automation    │   │
│  │                 │  │                 │  │                 │   │
│  │ • WhatiTicket   │  │ • Ticket        │  │ • RPA Engine    │   │
│  │   API           │  │   Routing       │  │ • 1Map          │   │
│  │ • Multi-Account │  │ • Assignment    │  │   Integration   │   │
│  │ • Message       │  │ • SLA           │  │ • Photo         │   │
│  │   Processing    │  │   Management    │  │   Analysis      │   │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘   │
│           │                     │                     │         │
│           └─────────────────────┼─────────────────────┘         │
│                                 │                               │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │              Zero Trust Security Layer                        │ │
│  │                                                             │ │
│  │ • Identity & Access Management                             │ │
│  │ • Encrypted Credential Storage                              │ │
│  │ • Audit Logging & Monitoring                               │ │
│  │ • Network Segmentation                                     │ │
│  │ • Continuous Authentication                                 │ │
│  └─────────────────────────────────────────────────────────────┘ │
│                                 │                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐   │
│  │   Database      │  │   Monitoring    │  │   Integration   │   │
│  │   Layer         │  │   & Analytics   │  │   Layer         │   │
│  │                 │  │                 │  │                 │   │
│  │ • Neon          │  │ • Real-time     │  │ • FibreFlow     │   │
│  │   PostgreSQL    │  │   Dashboards    │  │   API           │   │
│  │ • Project       │  │ • Performance   │  │ • Webhooks      │   │
│  │   Isolation     │  │   Metrics       │  │ • Event Bus     │   │
│  │ • Automatic     │  │ • Alerting      │  │ • Third-Party   │   │
│  │   Backups       │  │   System        │  │   Services      │   │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 Zero Trust Architecture Principles

1. **Verify Explicitly**: Always authenticate and authorize based on all available data points
2. **Least Privilege Access**: Grant minimum necessary access for tasks
3. **Assume Breach**: Design with the assumption that the network is compromised

#### Security Implementation
- **Authentication**: Multi-factor authentication for all access points
- **Authorization**: Role-based access control (RBAC) with granular permissions
- **Encryption**: AES-256 encryption for data at rest, TLS 1.3 for data in transit
- **Credential Management**: Hashicorp Vault integration for secure credential storage
- **Audit Trails**: Immutable logging of all system activities
- **Network Security**: Micro-segmentation with firewall rules between services

### 1.3 Microservice Design Patterns

#### Service Architecture
```yaml
services:
  # Core Services
  - name: whatsapp-gateway
    purpose: WhatsApp message processing
    tech: Node.js + Express + WhatiTicket API
    scale: 3 instances
    endpoints: /api/whatsapp/*

  - name: ticket-manager
    purpose: Ticket lifecycle management
    tech: Next.js API Routes
    scale: 3 instances
    endpoints: /api/tickets/*

  - name: audit-engine
    purpose: Audit workflow orchestration
    tech: Next.js API Routes
    scale: 3 instances
    endpoints: /api/audit/*

  - name: rpa-service
    purpose: 1Map automation
    tech: Node.js + Playwright
    scale: 8 workers
    endpoints: /api/rpa/*

  # Support Services
  - name: database-service
    purpose: Data persistence
    tech: Neon PostgreSQL + Drizzle ORM
    scale: Managed service

  - name: queue-service
    purpose: Message queuing
    tech: Redis + Bull Queue
    scale: 2 instances

  - name: auth-service
    purpose: Authentication & authorization
    tech: Next.js + Clerk
    scale: 2 instances
```

#### Communication Patterns
- **Synchronous**: REST APIs for real-time operations
- **Asynchronous**: Message queues for background processing
- **Event-Driven**: Webhooks for system notifications
- **GraphQL**: For complex data queries (analytics dashboard)

### 1.4 Performance & Scalability Design

#### Scalability Requirements
- **Horizontal Scaling**: Auto-scale based on load metrics
- **Load Balancing**: Round-robin with health checks
- **Caching Strategy**: Redis for session data, CDN for static assets
- **Database Optimization**: Read replicas, connection pooling, indexing

#### Performance Targets
- **Response Time**: <200ms for API calls, <1s for WhatsApp responses
- **Throughput**: 4,000+ tickets/day
- **Uptime**: 99.9% availability
- **Concurrent Users**: 200+ technicians

---

## 2. Development Methodology

### 2.1 DGTS (Documentation-Driven Test Development) Workflow

#### DGTS Implementation Process
```
1. Parse PRD Requirements
   ↓
2. Create Test Specifications
   ↓
3. Write Tests First
   ↓
4. Implement Code
   ↓
5. Validate Coverage
   ↓
6. Prevent Gaming (DGTS)
```

#### DGTS Validation System
- **Agent Validation**: All agents must run validation before development
- **Anti-Gaming**: Detect and block test/result manipulation
- **Behavior Monitoring**: Real-time tracking of agent actions
- **Quality Gates**: Enforce >95% test coverage

#### Quality Assurance Protocols
1. **Pre-Development Checks**
   - Validate test specifications exist
   - Check anti-gaming system status
   - Verify requirements alignment

2. **During Development**
   - Monitor for gaming patterns
   - Track test creation progress
   - Validate code coverage

3. **Post-Development**
   - Run comprehensive test suite
   - Validate against requirements
   - Check for gaming violations

### 2.2 Documentation-Driven Development

#### Document Hierarchy
1. **PRD** (Product Requirements Document) - Business requirements
2. **PRP** (Project Requirements Plan) - Technical specifications
3. **ADR** (Architecture Decision Records) - Design decisions
4. **Test Specs** - Testable acceptance criteria

#### Test Creation Timeline from PRD

**Week 1-2: Foundation Tests**
- WhatsApp integration tests
- Database schema validation
- Authentication system tests
- Basic API endpoint tests

**Week 3-4: Core Feature Tests**
- Ticket management workflow tests
- RPA automation tests
- Audit processing tests
- Response generation tests

**Week 5-6: Integration Tests**
- End-to-end WhatsApp to 1Map flow
- Error handling and recovery
- Performance and load tests
- Security penetration tests

**Week 7-8: Advanced Tests**
- Multi-project scalability tests
- Concurrency and race condition tests
- Disaster recovery tests
- Compliance validation tests

### 2.3 Quality Gates and Validation Processes

#### Quality Gate Checklist
- [ ] Zero TypeScript compilation errors
- [ ] Zero ESLint warnings
- [ ] Zero console.log statements
- [ ] >95% test coverage
- [ ] All security tests passing
- [ ] Performance benchmarks met
- [ ] No gaming patterns detected

#### Validation Processes
1. **Automated Validation**
   - Pre-commit hooks
   - CI/CD pipeline checks
   - Anti-gaming system scans

2. **Manual Validation**
   - Code reviews
   - Architecture reviews
   - Security audits

3. **Continuous Monitoring**
   - Production metrics
   - Error tracking
   - User feedback

---

## 3. Implementation Timeline

### 3.1 16-Week Detailed Roadmap

#### Phase 1: Foundation Setup (Weeks 1-4)
**Week 1: Environment & Infrastructure**
- [ ] Setup development environment
- [ ] Configure Docker containers
- [ ] Initialize database schema
- [ ] Setup CI/CD pipeline
- [ ] Create base project structure

**Week 2: WhatsApp Integration**
- [ ] WhatiTicket API integration
- [ ] WhatsApp Business API setup
- [ ] Message parsing implementation
- [ ] Basic ticket creation
- [ ] Authentication system

**Week 3: Database & Core Services**
- [ ] Complete database schema implementation
- [ ] Core API services development
- [ ] Ticket management system
- [ ] User management integration
- [ ] Basic audit workflow

**Week 4: RPA Foundation**
- [ ] Playwright setup for automation
- [ ] 1Map login automation
- [ ] Basic data extraction
- [ ] Error handling framework
- [ ] End-to-end testing setup

#### Phase 2: Core Features (Weeks 5-8)
**Week 5: Advanced RPA Automation**
- [ ] Complete 1Map navigation automation
- [ ] Photo extraction implementation
- [ ] Metadata parsing
- [ ] Screenshot capture for audit trail
- [ ] RPA worker scaling

**Week 6: Audit Engine**
- [ ] Compliance checking algorithm
- [ ] Photo validation rules
- [ ] Audit result generation
- [ ] Scoring system implementation
- [ ] Missing photo detection

**Week 7: Response System**
- [ ] WhatsApp response formatting
- [ ] Success/failure message templates
- [ ] Ticket reference integration
- [ ] Follow-up question handling
- [ ] Message delivery tracking

**Week 8: Ticket Management**
- [ ] Complete ticket lifecycle
- [ ] Assignment system
- [ ] Priority handling
- [ ] SLA monitoring
- [ ] Performance metrics

#### Phase 3: Enhancement & Testing (Weeks 9-12)
**Week 9: Pilot Preparation**
- [ ] Pilot environment setup
- [ ] Test data preparation
- [ ] Performance optimization
- [ ] Monitoring configuration
- [ ] Documentation completion

**Week 10: Pilot Program**
- [ ] Onboard 50-100 test installations
- [ ] Real-world testing
- [ ] Bug tracking and fixing
- [ ] Performance monitoring
- [ ] User feedback collection

**Week 11: Feature Refinement**
- [ ] Implement pilot feedback
- [ ] Bug fixes and improvements
- [ ] Enhanced error handling
- [ ] Additional features
- [ ] Performance tuning

**Week 12: Advanced Features**
- [ ] Multi-project support
- [ ] Advanced analytics
- [ ] Reporting dashboard
- [ ] Admin interface
- [ ] API documentation

#### Phase 4: Production & Scaling (Weeks 13-16)
**Week 13: Production Deployment**
- [ ] Production environment setup
- [ ] Data migration
- [ ] Go-live preparation
- [ ] Training materials
- [ ] Support procedures

**Week 14: Full Operations**
- [ ] Scale to 2,000+ tickets/day
- [ ] Monitor performance
- [ ] Optimize bottlenecks
- [ ] User support
- [ ] Continuous improvement

**Week 15: ML Preparation**
- [ ] Data collection for ML
- [ ] ML architecture design
- [ ] Training pipeline setup
- [ ] Model evaluation framework
- [ ] Phase 2 requirements

**Week 16: Final Optimization**
- [ ] System hardening
- [ ] Documentation finalization
- [ ] Knowledge transfer
- [ ] Performance benchmarks
- [ ] Project retrospective

### 3.2 Critical Path Analysis

#### Critical Path Dependencies
1. **WhatsApp API Setup** → All messaging features
2. **1Map RPA Automation** → Core audit functionality
3. **Database Schema** → All data operations
4. **Integration Layer** → System communication

#### Resource Allocation
- **Development Team**: 4 developers (1 lead, 3 seniors)
- **QA Engineer**: 1 full-time
- **DevOps Engineer**: 1 part-time
- **Project Manager**: 1 full-time
- **UI/UX Designer**: 1 part-time

### 3.3 Risk Mitigation Strategies

#### Technical Risks
1. **RPA Fragility**
   - Risk: 1Map UI changes break automation
   - Mitigation: Modular selectors, monitoring, quick update process
   - Contingency: Manual review process

2. **WhatsApp API Limits**
   - Risk: Rate limiting prevents messaging
   - Mitigation: Message queuing, multiple accounts
   - Contingency: Email fallback system

3. **Database Performance**
   - Risk: Slow queries under load
   - Mitigation: Indexing, caching, read replicas
   - Contingency: Query optimization team

#### Business Risks
1. **User Adoption**
   - Risk: Technicians resist new system
   - Mitigation: Training, intuitive UI, incentives
   - Contingency: Phased rollout approach

2. **Data Privacy**
   - Risk: Sensitive data exposure
   - Mitigation: Encryption, access controls
   - Contingency: Security audit team

---

## 4. Technical Specifications

### 4.1 API Design with Security Endpoints

#### Core API Endpoints

```typescript
// WhatsApp Gateway API
/api/whatsapp/
  POST /webhook          - Receive WhatsApp messages
  POST /send             - Send WhatsApp response
  GET  /status          - Check service status

// Ticket Management API
/api/tickets/
  GET    /              - List tickets
  POST   /              - Create ticket
  GET    /:id           - Get ticket details
  PUT    /:id           - Update ticket
  DELETE /:id           - Delete ticket
  POST   /:id/assign    - Assign ticket
  POST   /:id/close     - Close ticket

// Audit API
/api/audit/
  POST   /process       - Start audit
  GET    /:id           - Get audit results
  POST   /:id/retry     - Retry failed audit
  GET    /metrics       - Audit metrics

// RPA API
/api/rpa/
  POST   /execute       - Run RPA workflow
  GET    /status        - Check RPA status
  POST   /screenshot    - Capture screenshot
  GET    /logs          - View RPA logs

// Admin API
/api/admin/
  GET    /users         - User management
  POST   /projects      - Project configuration
  GET    /reports       - Generate reports
  POST   /settings      - System settings
```

#### Security Endpoints
```typescript
/api/auth/
  POST   /login         - User login
  POST   /logout        - User logout
  POST   /refresh       - Refresh token
  GET    /profile       - User profile

/api/security/
  GET    /audit-log     - Security audit log
  POST   /scan          - Security scan
  GET    /vulnerabilities - Known vulnerabilities
```

#### API Security Implementation
- **Authentication**: JWT tokens with 15-minute expiry
- **Authorization**: Role-based access control
- **Rate Limiting**: 100 requests/minute per IP
- **Input Validation**: Sanitize all inputs
- **CORS**: Restricted domains only
- **Headers**: Security headers (HSTS, CSP, X-Frame-Options)

### 4.2 Database Schema Implementation

#### Enhanced Schema with DGTS Validation
```sql
-- Core tables with validation constraints
CREATE TABLE tickets (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_number VARCHAR(50) UNIQUE NOT NULL
    CONSTRAINT valid_ticket_format CHECK (ticket_number ~ '^TICKET-\d{8}-\d{4}$'),
  dr_number VARCHAR(50) NOT NULL
    CONSTRAINT valid_dr_format CHECK (dr_number ~ '^DR\d{7}$'),
  technician_number VARCHAR(20) NOT NULL
    CONSTRAINT valid_phone CHECK (technician_number ~ '^\+\d{10,15}$'),
  technician_name VARCHAR(100) NOT NULL,
  message_content TEXT NOT NULL,
  message_timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  status VARCHAR(20) DEFAULT 'pending'
    CONSTRAINT valid_status CHECK (status IN ('pending', 'in_progress', 'completed', 'failed', 'closed')),
  priority VARCHAR(10) DEFAULT 'normal'
    CONSTRAINT valid_priority CHECK (priority IN ('low', 'normal', 'high', 'urgent')),
  assigned_to VARCHAR(100),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  completed_at TIMESTAMP WITH TIME ZONE,
  whatsapp_message_id VARCHAR(100),
  external_ticket_id VARCHAR(50),
  external_system VARCHAR(20) DEFAULT 'whatsticket',
  sync_status VARCHAR(20) DEFAULT 'pending'
    CONSTRAINT valid_sync CHECK (sync_status IN ('pending', 'synced', 'failed'))
);

-- Audit results with comprehensive validation
CREATE TABLE audit_results (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_id UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
  property_id VARCHAR(50),
  job_id VARCHAR(50),
  address TEXT,
  installation_status VARCHAR(50),
  last_modified TIMESTAMP WITH TIME ZONE,
  photos_required JSONB NOT NULL DEFAULT '[]',
  photos_found JSONB NOT NULL DEFAULT '[]',
  photos_missing JSONB NOT NULL DEFAULT '[]',
  compliance_score DECIMAL(5,2)
    CONSTRAINT valid_score CHECK (compliance_score BETWEEN 0 AND 100),
  audit_details JSONB DEFAULT '{}',
  ml_confidence_score DECIMAL(5,2),
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Performance tracking for DGTS
CREATE TABLE performance_metrics (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  metric_name VARCHAR(100) NOT NULL,
  metric_value DECIMAL(10,2) NOT NULL,
  metric_unit VARCHAR(20) NOT NULL,
  timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
  context JSONB DEFAULT '{}'
);

-- Anti-gaming validation logs
CREATE TABLE dgts_validation_logs (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  agent_name VARCHAR(100) NOT NULL,
  task_description TEXT NOT NULL,
  validation_type VARCHAR(50) NOT NULL,
  validation_result VARCHAR(20) NOT NULL,
  confidence_score DECIMAL(5,2),
  gaming_score DECIMAL(5,2) DEFAULT 0,
  details JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

#### Database Optimization
```sql
-- Indexes for performance
CREATE INDEX idx_tickets_status_created ON tickets(status, created_at);
CREATE INDEX idx_tickets_dr_number ON tickets(dr_number);
CREATE INDEX idx_tickets_technician ON tickets(technician_number);
CREATE INDEX idx_audit_results_ticket_id ON audit_results(ticket_id);
CREATE INDEX idx_audit_results_score ON audit_results(compliance_score);
CREATE INDEX idx_performance_metrics_timestamp ON performance_metrics(timestamp);

-- Partitioning for large tables
CREATE TABLE tickets_2025 PARTITION OF tickets
  FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
```

### 4.3 RPA Automation Design

#### Playwright Automation Architecture
```typescript
class RPAAuditEngine {
  private browser: Browser;
  private page: Page;
  private context: BrowserContext;
  private screenshots: Screenshot[] = [];

  async initialize(): Promise<void> {
    // Launch browser with security settings
    this.browser = await chromium.launch({
      headless: process.env.NODE_ENV === 'production',
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage'
      ]
    });

    // Create isolated context
    this.context = await this.browser.newContext({
      viewport: { width: 1920, height: 1080 },
      userAgent: 'Gatekeeper-RPA/1.0'
    });

    this.page = await this.context.newPage();

    // Setup interception for audit trail
    await this.page.route('**/*', route => {
      this.captureRequest(route.request());
      route.continue();
    });
  }

  async executeAudit(drNumber: string): Promise<AuditResult> {
    try {
      // Login with secure credentials
      await this.loginTo1Map();

      // Navigate to installations
      await this.navigateToInstallations();

      // Search for DR number
      const searchData = await this.searchDRNumber(drNumber);

      // Validate installation status
      if (searchData.status !== 'Home Installation: Installed') {
        throw new Error(`Invalid installation status: ${searchData.status}`);
      }

      // Extract metadata
      const metadata = await this.extractMetadata();

      // Extract photos
      const photos = await this.extractPhotos();

      // Generate audit result
      const result = await this.generateAuditResult({
        drNumber,
        propertyId: searchData.propertyId,
        jobId: searchData.jobId,
        address: searchData.address,
        status: searchData.status,
        photos,
        metadata
      });

      return result;

    } catch (error) {
      await this.captureError(error);
      throw error;
    } finally {
      await this.cleanup();
    }
  }

  private async loginTo1Map(): Promise<void> {
    // Secure login with credential rotation
    const credentials = await this.getCredentials();

    await this.page.goto('https://1map.example.com/login');
    await this.page.fill('#username', credentials.username);
    await this.page.fill('#password', credentials.password);

    // Handle MFA if required
    if (await this.page.isVisible('#mfa-code')) {
      const mfaCode = await this.getMFACode();
      await this.page.fill('#mfa-code', mfaCode);
    }

    await this.page.click('#login-button');
    await this.page.waitForNavigation({ waitUntil: 'networkidle' });

    // Verify successful login
    if (await this.page.isVisible('#login-error')) {
      throw new Error('Login failed');
    }
  }

  private async extractPhotos(): Promise<Photo[]> {
    const photos: Photo[] = [];

    // Find all photo elements
    const photoElements = await this.page.$$('.photo-container');

    for (const element of photoElements) {
      const photo = await element.evaluate(el => ({
        url: el.querySelector('img')?.src,
        type: el.dataset.photoType,
        uploadTime: el.dataset.uploadTime,
        fileSize: el.dataset.fileSize
      }));

      if (photo.url) {
        // Download photo for audit
        const downloadedPhoto = await this.downloadPhoto(photo.url);
        photos.push(downloadedPhoto);
      }
    }

    return photos;
  }

  private async generateAuditResult(data: AuditData): Promise<AuditResult> {
    // Compliance checking rules
    const requiredPhotos = [
      'trench',
      'ONT',
      'termination',
      'property'
    ];

    const foundPhotos = data.photos.map(p => p.type);
    const missingPhotos = requiredPhotos.filter(type => !foundPhotos.includes(type));

    // Calculate compliance score
    const complianceScore = Math.round(
      (foundPhotos.length / requiredPhotos.length) * 100
    );

    return {
      ticketId: data.ticketId,
      propertyId: data.propertyId,
      jobId: data.jobId,
      address: data.address,
      installationStatus: data.status,
      photosRequired: requiredPhotos,
      photosFound: foundPhotos,
      photosMissing: missingPhotos,
      complianceScore,
      auditDetails: {
        totalPhotos: data.photos.length,
        averageQuality: this.calculateAverageQuality(data.photos),
        auditTimestamp: new Date().toISOString()
      },
      screenshots: this.screenshots
    };
  }
}
```

### 4.4 WhatsApp Integration Architecture

#### WhatiTicket Integration Layer
```typescript
class WhatsAppIntegration {
  private whatiTicketAPI: WhatiTicketAPI;
  private messageQueue: Queue;
  private rateLimiter: RateLimiter;

  constructor() {
    this.whatiTicketAPI = new WhatiTicketAPI({
      baseUrl: process.env.WHATICKEET_API_URL,
      apiKey: process.env.WHATICKEET_API_KEY
    });

    this.messageQueue = new RedisQueue('whatsapp-messages');
    this.rateLimiter = new RateLimiter({
      points: 100, // requests
      duration: 60, // per minute
    });
  }

  async handleMessage(message: WhatsAppMessage): Promise<void> {
    try {
      // Rate limit check
      await this.rateLimiter.consume(message.from);

      // Parse DR number
      const drNumber = this.extractDRNumber(message.body);

      if (!drNumber) {
        await this.sendHelpMessage(message.from);
        return;
      }

      // Create audit ticket
      const ticket = await this.createAuditTicket({
        drNumber,
        technicianNumber: message.from,
        messageContent: message.body,
        messageTimestamp: message.timestamp
      });

      // Queue for processing
      await this.messageQueue.add('process-audit', {
        ticketId: ticket.id,
        priority: 'normal'
      });

    } catch (error) {
      await this.handleMessageError(error, message);
    }
  }

  async sendAuditResult(ticket: Ticket, result: AuditResult): Promise<void> {
    const message = this.formatResultMessage(result);

    await this.whatiTicketAPI.sendMessage({
      to: ticket.technicianNumber,
      message: message,
      ticketId: ticket.externalTicketId
    });

    // Log delivery
    await this.logMessageDelivery(ticket.id, message);
  }

  private formatResultMessage(result: AuditResult): string {
    const { drNumber, complianceScore, photosMissing, ticketNumber } = result;

    if (complianceScore === 100) {
      return `✅ DR${drNumber} verified. All required photos present. Ticket #${ticketNumber}`;
    } else {
      const missingList = photosMissing.join(', ');
      return `⚠️ DR${drNumber} missing ${missingList}. Please re-upload. Ticket #${ticketNumber}`;
    }
  }
}
```

---

## 5. Quality Assurance

### 5.1 DGTS Validation Process

#### Anti-Gaming System Implementation
```typescript
class DGTSValidator {
  private behaviorMonitor: BehaviorMonitor;
  private gamingDetector: GamingDetector;
  private scoreCalculator: GamingScoreCalculator;

  async validateAgentWork(agentName: string, task: string, changes: FileChange[]): Promise<ValidationResult> {
    // Initialize validation
    const validation: ValidationResult = {
      agentName,
      task,
      timestamp: new Date(),
      isValid: true,
      violations: [],
      gamingScore: 0
    };

    // Check for gaming patterns
    const gamingPatterns = await this.gamingDetector.detect(changes);
    if (gamingPatterns.length > 0) {
      validation.isValid = false;
      validation.violations.push(...gamingPatterns);
      validation.gamingScore = this.scoreCalculator.calculate(gamingPatterns);
    }

    // Validate test coverage
    const coverage = await this.validateTestCoverage(changes);
    if (coverage < 95) {
      validation.isValid = false;
      validation.violations.push({
        type: 'INSUFFICIENT_COVERAGE',
        message: `Test coverage ${coverage}% is below 95% threshold`
      });
    }

    // Check for console.log statements
    const consoleLogs = await this.detectConsoleLogs(changes);
    if (consoleLogs.length > 0) {
      validation.isValid = false;
      validation.violations.push({
        type: 'CONSOLE_LOG_VIOLATION',
        message: `Found ${consoleLogs.length} console.log statements`,
        files: consoleLogs
      });
    }

    // Log validation result
    await this.logValidation(validation);

    return validation;
  }

  private async detectGamingPatterns(changes: FileChange[]): Promise<GamingPattern[]> {
    const patterns: GamingPattern[] = [];

    for (const change of changes) {
      // Check for fake implementations
      if (change.content.includes('return "mock_data"')) {
        patterns.push({
          type: 'FAKE_IMPLEMENTATION',
          file: change.path,
          line: this.findLineNumber(change.content, 'return "mock_data"'),
          severity: 'critical'
        });
      }

      // Check for commented validation
      if (change.content.includes('// validation_required')) {
        patterns.push({
          type: 'COMMENTED_VALIDATION',
          file: change.path,
          line: this.findLineNumber(change.content, '// validation_required'),
          severity: 'high'
        });
      }

      // Check for always-true assertions
      if (change.content.includes('assert True')) {
        patterns.push({
          type: 'MEANINGLESS_TEST',
          file: change.path,
          line: this.findLineNumber(change.content, 'assert True'),
          severity: 'high'
        });
      }
    }

    return patterns;
  }
}
```

### 5.2 Test Coverage Requirements (>95%)

#### Test Categories and Coverage Targets

```yaml
test_categories:
  unit_tests:
    target_coverage: 95%
    frameworks:
      - Vitest (TypeScript)
      - pytest (Python)
    file_pattern: "*.spec.ts"

  integration_tests:
    target_coverage: 90%
    frameworks:
      - Playwright
      - Supertest
    file_pattern: "*.integration.ts"

  e2e_tests:
    target_coverage: 85%
    frameworks:
      - Playwright
      - Cypress
    file_pattern: "*.e2e.ts"

  security_tests:
    target_coverage: 100%
    frameworks:
      - OWASP ZAP
      - Jest Security
    file_pattern: "*.security.ts"

  performance_tests:
    target_coverage: 90%
    frameworks:
      - K6
      - Artillery
    file_pattern: "*.performance.ts"
```

#### Test Implementation Example
```typescript
// WhatsApp message processing test
describe('WhatsAppIntegration', () => {
  let integration: WhatsAppIntegration;

  beforeEach(() => {
    integration = new WhatsAppIntegration();
  });

  it('should extract DR number from message', () => {
    const message = {
      body: 'Please check DR1854443',
      from: '+27821234567',
      timestamp: new Date()
    };

    const drNumber = integration.extractDRNumber(message);

    expect(drNumber).toBe('DR1854443');
  });

  it('should create audit ticket for valid DR', async () => {
    const ticketData = {
      drNumber: 'DR1854443',
      technicianNumber: '+27821234567',
      messageContent: 'DR1854443',
      messageTimestamp: new Date()
    };

    const ticket = await integration.createAuditTicket(ticketData);

    expect(ticket).toMatchObject({
      drNumber: 'DR1854443',
      status: 'pending',
      technicianNumber: '+27821234567'
    });
    expect(ticket.ticketNumber).toMatch(/^TICKET-\d{8}-\d{4}$/);
  });

  it('should queue audit task after ticket creation', async () => {
    const messageQueue = mockMessageQueue();
    integration.setQueue(messageQueue);

    await integration.handleMessage(validMessage);

    expect(messageQueue.add).toHaveBeenCalledWith(
      'process-audit',
      expect.objectContaining({
        ticketId: expect.any(String),
        priority: 'normal'
      })
    );
  });
});

// RPA automation test
describe('RPAAuditEngine', () => {
  let rpa: RPAAuditEngine;

  beforeEach(async () => {
    rpa = new RPAAuditEngine();
    await rpa.initialize();
  });

  afterEach(async () => {
    await rpa.cleanup();
  });

  it('should login to 1Map successfully', async () => {
    await rpa.loginTo1Map();

    expect(rpa.page.url()).toContain('/dashboard');
    expect(await rpa.page.isVisible('#user-menu')).toBe(true);
  });

  it('should extract installation data', async () => {
    const data = await rpa.extractInstallationData('DR1854443');

    expect(data).toMatchObject({
      propertyId: expect.any(String),
      jobId: expect.any(String),
      status: 'Home Installation: Installed'
    });
  });

  it('should generate audit result with compliance score', async () => {
    const auditData = mockAuditData();
    const result = await rpa.generateAuditResult(auditData);

    expect(result.complianceScore).toBeGreaterThan(0);
    expect(result.complianceScore).toBeLessThanOrEqual(100);
    expect(result.photosMissing).toEqual(
      expect.arrayContaining(['trench'])
    );
  });
});
```

### 5.3 Performance Benchmarks

#### Performance Targets
```yaml
performance_targets:
  api_endpoints:
    avg_response_time: 200ms
    p95_response_time: 500ms
    p99_response_time: 1000ms
    throughput: 1000 req/s

  whatsapp_processing:
    message_to_ticket: 100ms
    audit_completion: 30s
    response_delivery: 1s

  database_operations:
    read_query: 50ms
    write_query: 100ms
    complex_query: 500ms

  rpa_automation:
    login_time: 10s
    data_extraction: 15s
    photo_processing: 5s
    total_audit: 30s

  system_resources:
    cpu_utilization: 70%
    memory_utilization: 80%
    disk_usage: 85%
    network_bandwidth: 1Gbps
```

#### Load Testing Strategy
```typescript
// K6 load test script
import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '2m', target: 100 },  // Ramp up
    { duration: '5m', target: 100 },  // Sustained load
    { duration: '2m', target: 500 },  // Ramp up
    { duration: '5m', target: 500 },  // Peak load
    { duration: '2m', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  // Test WhatsApp webhook endpoint
  let res = http.post('http://localhost:3000/api/whatsapp/webhook', {
    from: `+2782${Math.floor(Math.random() * 10000000)}`,
    body: `DR${Math.floor(Math.random() * 10000000)}`,
    timestamp: new Date().toISOString()
  });

  check(res, {
    'status was 200': (r) => r.status == 200,
    'response time < 200ms': (r) => r.timings.duration < 200,
  });

  // Test ticket endpoint
  res = http.get('http://localhost:3000/api/tickets');

  check(res, {
    'status was 200': (r) => r.status == 200,
    'tickets returned': (r) => JSON.parse(r.body).length >= 0,
  });

  sleep(1);
}
```

### 5.4 Security Validation Procedures

#### Security Testing Checklist
```yaml
security_tests:
  authentication:
    - JWT token validation
    - Session management
    - Password strength
    - MFA implementation
    - Rate limiting on auth

  authorization:
    - Role-based access control
    - Permission escalation
    - API key management
    - Service account security
    - Admin privilege segregation

  data_protection:
    - Encryption at rest (AES-256)
    - Encryption in transit (TLS 1.3)
    - Secure credential storage
    - Data masking in logs
    - Backup encryption

  api_security:
    - Input validation
    - SQL injection prevention
    - XSS protection
    - CSRF protection
    - API rate limiting

  infrastructure:
    - Network segmentation
    - Firewall rules
    - Intrusion detection
    - DDoS protection
    - Vulnerability scanning

  compliance:
    - GDPR compliance
    - Data retention policies
    - Audit trail completeness
    - Privacy impact assessment
    - Security documentation
```

#### OWASP Security Scan
```bash
# Run OWASP ZAP scan
zap-baseline.py -t https://api.gatekeeper.example.com -r zap_report.html

# Check for common vulnerabilities
npm audit --audit-level moderate
snyk test

# Run security tests
npm run test:security
```

---

## 6. Deployment Strategy

### 6.1 Docker Deployment Architecture

#### Container Orchestration
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  # API Gateway
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./docker/nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./docker/nginx/ssl:/etc/nginx/ssl
    depends_on:
      - backend
      - frontend
    networks:
      - gatekeeper-network

  # Backend API
  backend:
    build:
      context: .
      dockerfile: docker/backend.Dockerfile
    environment:
      - NODE_ENV=production
      - DATABASE_URL=${DATABASE_URL}
      - REDIS_URL=${REDIS_URL}
      - JWT_SECRET=${JWT_SECRET}
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: '2.0'
          memory: 2G
        reservations:
          cpus: '1.0'
          memory: 1G
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    networks:
      - gatekeeper-network

  # RPA Workers
  rpa-workers:
    build:
      context: .
      dockerfile: docker/rpa.Dockerfile
    deploy:
      replicas: 8
      resources:
        limits:
          cpus: '4.0'
          memory: 4G
        reservations:
          cpus: '2.0'
          memory: 2G
    environment:
      - NODE_ENV=production
      - 1MAP_USERNAME=${1MAP_USERNAME}
      - 1MAP_PASSWORD=${1MAP_PASSWORD}
    volumes:
      - rpa-screenshots:/app/screenshots
    networks:
      - gatekeeper-network

  # Queue Worker
  queue-worker:
    build:
      context: .
      dockerfile: docker/queue.Dockerfile
    deploy:
      replicas: 4
    environment:
      - REDIS_URL=${REDIS_URL}
    depends_on:
      - redis
    networks:
      - gatekeeper-network

  # Frontend
  frontend:
    build:
      context: .
      dockerfile: docker/frontend.Dockerfile
    environment:
      - NEXT_PUBLIC_API_URL=${API_URL}
    networks:
      - gatekeeper-network

  # Database
  postgres:
    image: postgres:14-alpine
    environment:
      - POSTGRES_DB=${DB_NAME}
      - POSTGRES_USER=${DB_USER}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./docker/scripts/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    networks:
      - gatekeeper-network

  # Cache
  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis-data:/data
    ports:
      - "6379:6379"
    networks:
      - gatekeeper-network

  # Monitoring
  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
    networks:
      - gatekeeper-network

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}
    volumes:
      - grafana-data:/var/lib/grafana
    networks:
      - gatekeeper-network

volumes:
  postgres-data:
  redis-data:
  rpa-screenshots:
  grafana-data:

networks:
  gatekeeper-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

### 6.2 CI/CD Pipeline Design

#### GitHub Actions Workflow
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

env:
  NODE_VERSION: '18.x'
  PYTHON_VERSION: '3.10'

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        test-type: [unit, integration, e2e, security]

    steps:
      - uses: actions/checkout@v3

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: ${{ env.NODE_VERSION }}

      - name: Setup Python
        uses: actions/setup-python@v4
        with:
          python-version: ${{ env.PYTHON_VERSION }}

      - name: Install dependencies
        run: |
          npm ci
          pip install -r requirements.txt

      - name: Run DGTS validation
        run: |
          python scripts/dgts_validator.py validate

      - name: Run tests
        run: |
          npm run test:${{ matrix.test-type }}

      - name: Upload coverage
        uses: codecov/codecov-action@v3

  build:
    needs: test
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2

      - name: Login to Container Registry
        uses: docker/login-action@v2
        with:
          registry: ${{ secrets.REGISTRY_URL }}
          username: ${{ secrets.REGISTRY_USERNAME }}
          password: ${{ secrets.REGISTRY_PASSWORD }}

      - name: Build and push images
        run: |
          docker-compose -f docker-compose.prod.yml build
          docker-compose -f docker-compose.prod.yml push

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
      - uses: actions/checkout@v3

      - name: Deploy to production
        run: |
          ssh deploy@server << 'EOF'
            cd /opt/gatekeeper-rpa
            git pull origin main
            docker-compose -f docker-compose.prod.yml up -d
            docker system prune -f
          EOF

      - name: Run smoke tests
        run: |
          npm run test:smoke

      - name: Notify deployment
        uses: 8398a7/action-slack@v3
        with:
          status: ${{ job.status }}
          channel: '#deployments'
```

### 6.3 Monitoring and Alerting

#### Monitoring Stack Configuration
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'gatekeeper-backend'
    static_configs:
      - targets: ['backend:8080']
    metrics_path: '/metrics'
    scrape_interval: 10s

  - job_name: 'gatekeeper-rpa'
    static_configs:
      - targets: ['rpa-workers:8081']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres:5432']

  - job_name: 'redis'
    static_configs:
      - targets: ['redis:6379']

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

# alerts.yml
groups:
  - name: gatekeeper-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }} errors per second"

      - alert: SlowResponseTime
        expr: histogram_quantile(0.95, http_request_duration_seconds_bucket) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Slow response time detected"
          description: "95th percentile response time is {{ $value }}s"

      - alert: LowAvailableWorkers
        expr: up{job="rpa-workers"} < 6
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Low number of available RPA workers"
          description: "Only {{ $value }} RPA workers are available"

      - alert: DatabaseConnectionsHigh
        expr: pg_stat_database_numbackends / pg_settings_max_connections * 100 > 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High database connection usage"
          description: "Database connection usage is at {{ $value }}%"
```

### 6.4 Backup and Recovery Procedures

#### Backup Strategy
```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups/gatekeeper"
S3_BUCKET="s3://gatekeeper-backups"

# Create backup directory
mkdir -p $BACKUP_DIR

# Database backup
docker exec postgres pg_dump -U $DB_USER -d $DB_NAME > $BACKUP_DIR/db_$DATE.sql

# Redis backup
docker exec redis redis-cli --rdb $BACKUP_DIR/redis_$DATE.rdb

# Config backup
tar -czf $BACKUP_DIR/config_$DATE.tar.gz \
  docker/ \
  .env \
  docker-compose*.yml

# Upload to S3
aws s3 cp $BACKUP_DIR/ $S3_BUCKET/$DATE/ --recursive

# Clean old backups (keep 30 days)
find $BACKUP_DIR/ -type f -mtime +30 -delete
aws s3 ls $S3_BUCKET/ | while read -r line; do
  createDate=$(echo $line | awk '{print $1" "$2}')
  createDate=$(date -d "$createDate" +%s)
  olderThan=$(date -d "30 days ago" +%s)
  if [[ $createDate -lt $olderThan ]]; then
    fileName=$(echo $line | awk '{print $4}')
    aws s3 rm "$S3_BUCKET/$fileName"
  fi
done
```

#### Recovery Procedures
```bash
#!/bin/bash
# restore.sh

BACKUP_DATE=$1
BACKUP_DIR="/opt/backups/gatekeeper"
S3_BUCKET="s3://gatekeeper-backups"

# Stop services
docker-compose down

# Download backup from S3
aws s3 cp $S3_BUCKET/$BACKUP_DATE/ $BACKUP_DIR/restore/ --recursive

# Restore database
docker exec -i postgres psql -U $DB_USER -d $DB_NAME < $BACKUP_DIR/restore/db_$BACKUP_DATE.sql

# Restore Redis
docker cp $BACKUP_DIR/restore/redis_$BACKUP_DATE.rdb redis:/data/dump.rdb
docker restart redis

# Restore config
tar -xzf $BACKUP_DIR/restore/config_$BACKUP_DATE.tar.gz

# Start services
docker-compose up -d

# Verify restore
docker-compose exec backend npm run test:smoke
```

---

## 7. Project Governance

### 7.1 DGTS Compliance Requirements

#### Anti-Gaming Enforcement
1. **Pre-Development Validation**
   - All agents must run validation before coding
   - Test specifications must exist from documentation
   - Gaming score must be <0.3 to proceed

2. **Development Monitoring**
   - Real-time monitoring of all file changes
   - Automatic detection of gaming patterns
   - Immediate blocking of violating agents

3. **Post-Development Validation**
   - Comprehensive test coverage verification
   - Code quality assessment
   - Gaming score final calculation

### 7.2 Success Metrics

#### Key Performance Indicators
- **System Performance**: 99.9% uptime, <200ms response time
- **Audit Processing**: 4,000+ tickets/day processed
- **Quality Gates**: 100% compliance with DGTS principles
- **Test Coverage**: >95% across all modules
- **Security**: Zero critical vulnerabilities
- **User Satisfaction**: >90% technician satisfaction

#### Business Outcomes
- **Efficiency**: 50% reduction in manual audit time
- **Accuracy**: 95%+ audit accuracy rate
- **Cost**: 30% reduction in operational costs
- **Scalability**: Support for 10+ concurrent projects

### 7.3 Maintenance and Evolution

#### Phase 2 Preparation (ML Integration)
- Data collection pipeline for training
- ML model architecture design
- Integration points for Phase 2 features

#### Long-term Evolution
- Horizontal scaling architecture
- Multi-tenant capability
- Advanced analytics features
- Mobile application support

---

## 8. Conclusion

This Project Requirements Plan provides a comprehensive blueprint for implementing the Gatekeeper RPA system using Documentation-Driven Test Development principles. The plan ensures:

1. **Zero Trust Security**: Comprehensive security measures at all layers
2. **Scalable Architecture**: Microservice design for horizontal scaling
3. **Quality Assurance**: DGTS validation prevents gaming and ensures quality
4. **Production Ready**: Complete deployment and monitoring strategy
5. **Future Proof**: Foundation for ML integration and advanced features

The implementation follows strict quality gates and validation processes to ensure a robust, secure, and maintainable system that meets all business requirements while preventing anti-patterns and gaming behaviors.

---

**Document Version**: 2.0
**Last Updated**: 2025-09-23
**Next Review**: 2025-10-23