# Architecture Decision Records (ADR) - Gatekeeper RPA System

**Version**: 1.0
**Date**: 2025-09-23

## Table of Contents
1. [ADR-001: Technology Stack Selection](#adr-001-technology-stack-selection)
2. [ADR-002: Database Architecture](#adr-002-database-architecture)
3. [ADR-003: RPA-Only Integration Approach](#adr-003-rpa-only-integration-approach)
4. [ADR-004: WhatsApp Integration Strategy](#adr-004-whatsapp-integration-strategy)
5. [ADR-005: Deployment Architecture](#adr-005-deployment-architecture)
6. [ADR-006: Security Architecture](#adr-006-security-architecture)
7. [ADR-007: Performance and Scalability](#adr-007-performance-and-scalability)
8. [ADR-008: Error Handling and Recovery](#adr-008-error-handling-and-recovery)
9. [ADR-009: Monitoring and Observability](#adr-009-monitoring-and-observability)
10. [ADR-010: Future-Proofing and Extensibility](#adr-010-future-proofing-and-extensibility)

---

## ADR-001: Technology Stack Selection

### Status
**Accepted**

### Context
We need to select a technology stack for the Gatekeeper RPA system that meets the following requirements:
- Enterprise-grade reliability and performance
- Integration with WhatsApp and external systems
- RPA automation capabilities
- Multi-project support
- Scalability to handle 4,000+ tickets/day

### Decision
We have selected the following technology stack:

**Backend/API Framework**
- **Next.js 14+ with App Router**: Modern, full-stack framework with excellent TypeScript support, built-in API routes, and Vercel optimization
- **TypeScript (Strict Mode)**: Type safety and better developer experience with no 'any' types allowed

**Database**
- **Neon PostgreSQL**: Serverless Postgres with automatic scaling, branching, and excellent TypeScript integration
- **Drizzle ORM**: TypeScript-first ORM with type safety and excellent migration system

**RPA Automation**
- **Playwright**: Modern browser automation with better reliability, features, and TypeScript support compared to Puppeteer
- **Browser Pool Management**: Custom implementation for handling multiple concurrent automation sessions

**WhatsApp Integration**
- **WhatiTicket Community Platform**: Proven WhatsApp Business API integration with ticketing capabilities
- **Webhook-based Architecture**: Real-time message processing without polling

**Infrastructure**
- **Docker & Docker Compose**: Containerization for consistent deployment and development environments
- **Redis + Bull**: Queue system for async processing and job management
- **Prometheus + Grafana**: Monitoring and observability stack

**Testing**
- **Vitest**: Fast unit and integration testing
- **Playwright Test**: E2E testing with the same framework as RPA
- **K6**: Performance and load testing

### Consequences
**Benefits**
- Modern, maintainable codebase with full type safety
- Excellent developer experience with TypeScript
- Scalable architecture that can handle growth
- Comprehensive testing capabilities
- Production-ready from day one

**Drawbacks**
- Learning curve for team members new to Next.js/TypeScript
- Additional complexity with multiple services
- Docker requirement for development environment

**Alternatives Considered**
1. **Node.js + Express**: More flexible but requires more setup
2. **Python + FastAPI**: Excellent for RPA but less mature for web apps
3. **Java Spring**: Enterprise-grade but slower development

### Decision Date
2025-09-23

---

## ADR-002: Database Architecture

### Status
**Accepted**

### Context
The system needs to store audit data, ticket information, and support multi-project isolation while maintaining high performance and data integrity.

### Decision
**Database Structure**
- **Primary Database**: Single PostgreSQL instance with schema-based project isolation
- **Connection Pooling**: PgBouncer or built-in pooling for high concurrency
- **Indexing Strategy**: Optimized indexes for common query patterns
- **JSONB Usage**: For flexible photo metadata and audit details

**Schema Design Principles**
1. **Normalization**: Core tables normalized to reduce redundancy
2. **JSONB**: Semi-structured data for photos and audit details
3. **Audit Trail**: Complete workflow event logging
4. **Soft Deletes**: No data deletion, only status updates
5. **Timestamps**: Created/updated timestamps on all records

**Key Design Decisions**
```sql
-- Project isolation through schema
CREATE SCHEMA project1;
CREATE SCHEMA project2;

-- Each project has identical tables
-- Central registry for project management
CREATE TABLE projects (
  id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  schema_name VARCHAR(50) UNIQUE NOT NULL,
  config JSONB,
  created_at TIMESTAMP DEFAULT NOW()
);
```

**Migration Strategy**
- Drizzle ORM for version-controlled migrations
- Automatic migration application on deployment
- Backup before major schema changes
- Rollforward-only approach (no down migrations)

### Consequences
**Benefits**
- Clear data isolation between projects
- Flexible schema for evolving requirements
- Excellent query performance with proper indexing
- Complete audit trail for compliance

**Drawbacks**
- Schema management complexity for multi-tenant setup
- JSONB querying requires specific PostgreSQL knowledge
- Migration planning required for schema changes

---

## ADR-003: RPA-Only Integration Approach

### Status
**Accepted**

### Context
1Map does not provide API access for installation data. We need to extract data through the web interface only.

### Decision
**RPA-First Architecture**
- **Playwright Automation**: Modern browser automation with reliable selectors
- **Modular Design**: Break RPA into reusable components
- **Robust Selectors**: Multiple selector strategies for resilience
- **Session Management**: Persistent login sessions with cookie handling
- **Error Recovery**: Comprehensive retry and fallback mechanisms

**Key Components**
1. **Authentication Manager**: Handle login, MFA, and session persistence
2. **Navigation Manager**: Reliable page navigation with verification
3. **Data Extraction Manager**: Multi-strategy data extraction
4. **Error Handler**: Exponential backoff and recovery procedures
5. **Browser Pool**: Manage multiple browser instances

**RPA Workflow**
```typescript
class RPAWorkflow {
  async executeAudit(drNumber: string): Promise<AuditData> {
    // 1. Get browser from pool
    const browser = await browserPool.get();

    // 2. Login with session reuse
    await authManager.ensureLoggedIn(browser);

    // 3. Navigate and search
    await navManager.navigateToInstallations(browser);
    await navManager.searchDRNumber(browser, drNumber);

    // 4. Extract data with retries
    const data = await errorHandler.withRetry(
      () => extractor.extractAllData(browser),
      'Data Extraction'
    );

    // 5. Return browser to pool
    browserPool.release(browser);

    return data;
  }
}
```

**Mitigation Strategies**
- **Selector Redundancy**: Multiple selectors for each element
- **Screenshot Verification**: Visual confirmation of actions
- **Performance Monitoring**: Track extraction times
- **Change Detection**: Alert on UI changes
- **Manual Override**: Quick human intervention capability

### Consequences
**Benefits**
- Complete access to all 1Map data
- Resilient to minor UI changes
- Scalable through browser pooling
- Comprehensive error handling

**Drawbacks**
- Dependent on 1Map UI stability
- Requires ongoing maintenance
- Slower than API access
- Browser resource intensive

---

## ADR-004: WhatsApp Integration Strategy

### Status
**Accepted**

### Context
Need enterprise-grade WhatsApp integration with message processing, ticketing, and response capabilities.

### Decision
**WhatiTicket + Custom Integration**
- **WhatiTicket Platform**: Proven WhatsApp Business API integration
- **Custom Audit Extension**: Build audit-specific workflows on top
- **Event-Driven Architecture**: Webhook-based real-time processing
- **Message Queue**: Redis-based queue for async processing

**Integration Architecture**
```
WhatsApp → WhatiTicket → Webhook → Our System → Process → Response
    ↑                                                ↓
Technician                                        WhatsApp
```

**Key Components**
1. **Webhook Handler**: Receive messages from WhatiTicket
2. **Message Parser**: Extract DR numbers and commands
3. **Ticket Router**: Route to appropriate workflow
4. **Response Generator**: Format and send responses
5. **Media Handler**: Process photos and attachments

**Message Processing Flow**
```typescript
class WhatsAppService {
  async handleWebhook(message: WhatsAppMessage): Promise<void> {
    // 1. Validate and parse message
    const validated = this.validateMessage(message);

    // 2. Extract DR number
    const drNumber = this.extractDRNumber(validated.body);

    if (drNumber) {
      // 3. Create audit ticket
      const ticket = await this.createAuditTicket({
        drNumber,
        technicianNumber: validated.from,
        message: validated.body
      });

      // 4. Queue for processing
      await auditQueue.add('process-audit', { ticketId: ticket.id });
    }
  }
}
```

**Multi-Project Support**
- Separate WhatiTicket instances per project
- Shared core processing logic
- Project-specific configurations and templates

### Consequences
**Benefits**
- Reliable WhatsApp delivery
- Enterprise-grade features
- Scalable architecture
- Separation of concerns

**Drawbacks**
- Additional integration complexity
- WhatiTicket dependency
- Cost considerations for multiple projects

---

## ADR-005: Deployment Architecture

### Status
**Accepted**

### Context
Need deployment strategy that supports development, staging, and production environments with scalability and reliability.

### Decision
**Docker-First Architecture**
- **Containerization**: All services in Docker containers
- **Docker Compose**: Orchestration for simple deployments
- **Environment Parity**: Identical environments across stages
- **Scalability**: Horizontal scaling through service replication

**Environment Strategy**
```
Development (Local)
├── Docker Compose
├── Local volumes
└── Hot reload

Staging (Cloud)
├── Docker Compose
├── Cloud storage
└── Monitoring

Production (Cloud)
├── Docker Compose (Phase 1)
├── Kubernetes (Phase 2)
├── Load balancer
└── High availability
```

**Service Architecture**
```yaml
services:
  # Core Application
  backend:
    image: gatekeeper/backend
    deploy:
      replicas: 3
      resources:
        limits: cpus: 2, memory: 2G

  rpa-workers:
    image: gatekeeper/rpa
    deploy:
      replicas: 8
      resources:
        limits: cpus: 4, memory: 4G

  # Infrastructure
  postgres:
    image: postgres:13
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes

  # Monitoring
  prometheus:
    image: prom/prometheus
  grafana:
    image: grafana/grafana
```

**Deployment Strategy**
1. **Blue-Green Deployment**: Zero-downtime updates
2. **Health Checks**: Automated service validation
3. **Rollback Capability**: Quick recovery on failure
4. **Configuration Management**: Environment-specific configs

### Consequences
**Benefits**
- Consistent environments
- Easy scaling
- Simplified dependencies
- Production parity

**Drawbacks**
- Docker learning curve
- Resource overhead
- Complex networking setup

---

## ADR-006: Security Architecture

### Status
**Accepted**

### Context
Handling sensitive installation data, technician information, and providing enterprise-grade security.

### Decision
**Zero-Trust Security Model**
- **Authentication**: Multi-factor authentication where possible
- **Authorization**: Role-based access control (RBAC)
- **Encryption**: End-to-end encryption for sensitive data
- **Audit Logging**: Complete activity tracking

**Security Layers**
1. **Network Security**
   - Firewall rules
   - SSL/TLS termination
   - VPN access for internal services

2. **Application Security**
   - Input validation and sanitization
   - SQL injection protection
   - XSS prevention
   - CSRF protection
   - Rate limiting

3. **Data Security**
   - Encryption at rest (Postgres pgcrypto)
   - Encryption in transit (TLS 1.3)
   - Secure credential storage
   - Data masking in logs

4. **Identity Security**
   - JWT with short expiration
   - Refresh token rotation
   - Session management
   - Password policies

**Implementation Details**
```typescript
// Security middleware stack
app.use(helmet());
app.use(cors({
  origin: allowedOrigins,
  credentials: true
}));
app.use(rateLimiter);
app.use(csurf());
app.use(inputValidator);

// RBAC middleware
const requireRole = (roles: string[]) => {
  return (req: Request, res: Response, next: NextFunction) => {
    const userRole = req.user?.role;
    if (!roles.includes(userRole)) {
      return res.status(403).json({ error: 'Insufficient permissions' });
    }
    next();
  };
};
```

**Security Monitoring**
- Failed login attempt tracking
- Unusual activity detection
- Security event logging
- Regular security audits

### Consequences
**Benefits**
- Comprehensive security coverage
- Compliance with data protection laws
- Audit trail for all activities
- Defense in depth

**Drawbacks**
- Implementation complexity
- Performance overhead
- User experience friction

---

## ADR-007: Performance and Scalability

### Status
**Accepted**

### Context
System must handle 4,000+ tickets/day with sub-30 second processing times.

### Decision
**Scalable Microservices Architecture**
- **Horizontal Scaling**: Stateless services that can be replicated
- **Caching Strategy**: Multi-level caching for performance
- **Database Optimization**: Query optimization and indexing
- **Async Processing**: Non-blocking operations where possible

**Performance Targets**
- **WhatsApp Response**: <1 second
- **Audit Processing**: <30 seconds end-to-end
- **API Response**: <200ms p95
- **Database Query**: <100ms p95
- **Uptime**: 99.9%

**Scaling Strategies**
1. **Application Layer**
   - Load balancer with least-conn routing
   - Auto-scaling based on CPU/memory
   - Circuit breakers for external services
   - Request batching and optimization

2. **Database Layer**
   - Read replicas for reporting
   - Connection pooling
   - Query optimization
   - Index strategy review

3. **RPA Layer**
   - Browser pool management
   - Concurrent audit processing
   - Resource limits and timeouts
   - Graceful degradation

**Implementation Example**
```typescript
// Browser pool for RPA scaling
class BrowserPool {
  private pool: Browser[] = [];
  private maxPoolSize = 10;

  async getBrowser(): Promise<Browser> {
    if (this.pool.length > 0) {
      return this.pool.pop();
    }

    if (this.activeCount < this.maxPoolSize) {
      return await this.createBrowser();
    }

    // Wait for available browser
    return this.waitForBrowser();
  }

  async releaseBrowser(browser: Browser): Promise<void> {
    // Reset browser state
    await this.resetBrowser(browser);
    this.pool.push(browser);
  }
}
```

**Monitoring and Optimization**
- Real-time performance metrics
- Automatic scaling triggers
- Query performance analysis
- Resource utilization monitoring

### Consequences
**Benefits**
- Handles high traffic volumes
- Maintains performance under load
- Cost-effective scaling
- Better user experience

**Drawbacks**
- Increased infrastructure complexity
- Higher resource requirements
- Monitoring overhead

---

## ADR-008: Error Handling and Recovery

### Status
**Accepted**

### Context
RPA automation and external integrations introduce various failure scenarios that need graceful handling.

### Decision
**Comprehensive Error Handling Strategy**
- **Circuit Breaker Pattern**: Prevent cascading failures
- **Exponential Backoff**: Intelligent retry logic
- **Dead Letter Queue**: Failed message handling
- **Automated Recovery**: Self-healing where possible

**Error Categories and Handling**
1. **Transient Errors**
   - Network timeouts
   - Temporary service unavailability
   - Rate limiting
   - **Strategy**: Retry with exponential backoff

2. **Permanent Errors**
   - Invalid credentials
   - Missing data
   - Business rule violations
   - **Strategy**: Fail fast with clear error messages

3. **System Errors**
   - Database failures
   - Out of memory
   - Browser crashes
   - **Strategy**: Alert and restart services

**Implementation Framework**
```typescript
class ErrorHandler {
  async withRetry<T>(
    operation: () => Promise<T>,
    context: string,
    maxRetries = 3
  ): Promise<T> {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        return await operation();
      } catch (error) {
        if (attempt === maxRetries || !isTransientError(error)) {
          await this.logFinalError(context, error);
          throw error;
        }

        const delay = Math.min(1000 * 2 ** attempt, 30000);
        await this.sleep(delay);
      }
    }
  }

  async handleRPACrash(session: RPASession): Promise<void> {
    // 1. Log the crash
    await this.logEvent('rpa_crash', { sessionId: session.id });

    // 2. Take screenshot for debugging
    await session.takeScreenshot('crash');

    // 3. Cleanup resources
    await session.cleanup();

    // 4. Requeue the audit
    await auditQueue.add('process-audit', {
      ticketId: session.ticketId,
      priority: 'high'
    });
  }
}
```

**Monitoring and Alerting**
- Error rate tracking
- Failure pattern detection
- Automatic escalation
- Health check integration

### Consequences
**Benefits**
- Improved system reliability
- Better user experience during failures
- Faster problem resolution
- Comprehensive error tracking

**Drawbacks**
- Implementation complexity
- Potential for retry storms
- Additional monitoring overhead

---

## ADR-009: Monitoring and Observability

### Status
**Accepted**

### Context
Complex distributed system needs comprehensive monitoring to ensure reliability and performance.

### Decision
**Observability-First Design**
- **Metrics Collection**: Comprehensive metrics across all services
- **Structured Logging**: JSON logs with correlation IDs
- **Distributed Tracing**: Request tracking across services
- **Real-time Dashboards**: Operational visibility

**Monitoring Stack**
1. **Metrics**: Prometheus + Grafana
   - Application metrics (request rates, response times)
   - Business metrics (tickets processed, compliance rates)
   - System metrics (CPU, memory, disk)
   - Custom metrics (RPA success rates, browser pool size)

2. **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
   - Structured JSON logging
   - Log aggregation and search
   - Error log analysis
   - Audit trail logging

3. **Tracing**: OpenTelemetry
   - Request correlation IDs
   - Distributed transaction tracking
   - Performance bottleneck identification
   - Service dependency mapping

**Key Metrics to Monitor**
```yaml
# Application Metrics
http_requests_total:
  labels: [method, endpoint, status]

http_request_duration_seconds:
  labels: [method, endpoint]
  buckets: [0.1, 0.5, 1, 2, 5]

# Business Metrics
audit_tickets_total:
  labels: [status, project]

audit_processing_time_seconds:
  buckets: [10, 30, 60, 300]

# RPA Metrics
rpa_operations_total:
  labels: [operation, status]

rpa_browser_pool_size:
  type: gauge

# Database Metrics
database_connections_active:
  type: gauge

database_query_duration_seconds:
  labels: [query_type]
```

**Alerting Strategy**
- **Critical**: System down, data loss, security breach
- **Warning**: High error rates, performance degradation
- **Info**: Resource utilization approaching limits
- **Clear**: Automated resolution notifications

**Implementation Example**
```typescript
// Metrics collection
const auditCounter = new Counter({
  name: 'audit_operations_total',
  labelNames: ['operation', 'status']
});

const auditTimer = new Histogram({
  name: 'audit_processing_time_seconds',
  buckets: [10, 30, 60, 300]
});

class AuditService {
  async processAudit(ticket: Ticket): Promise<AuditResult> {
    const endTimer = auditTimer.startTimer();

    try {
      const result = await this.executeAudit(ticket);
      auditCounter.labels({ operation: 'process', status: 'success' }).inc();
      endTimer();
      return result;
    } catch (error) {
      auditCounter.labels({ operation: 'process', status: 'error' }).inc();
      endTimer();
      throw error;
    }
  }
}
```

### Consequences
**Benefits**
- Proactive issue detection
- Faster troubleshooting
- Performance optimization insights
- Compliance reporting capabilities

**Drawbacks**
- Resource overhead for monitoring
- Complex setup and maintenance
- Storage requirements for logs/metrics

---

## ADR-010: Future-Proofing and Extensibility

### Status
**Accepted**

### Context
System must evolve to support ML integration, FibreFlow integration, and scaling to multiple FNO projects.

### Decision
**Modular, Extensible Architecture**
- **Plugin System**: For adding new audit types
- **Event-Driven Design**: Loose coupling between components
- **API-First**: All functionality exposed through APIs
- **Configuration-Driven**: Behavior through configuration, not code

**Extensibility Strategies**
1. **ML Integration Preparation**
   - Store photo metadata in analyzable format
   - Design audit results to accommodate ML scores
   - Implement feature extraction pipeline
   - Create ML model interface

2. **Multi-FNO Support**
   - Tenant isolation architecture
   - Configurable audit rules
   - White-label capabilities
   - Project-specific workflows

3. **FibreFlow Integration**
   - Shared database schema
   - Common authentication system
   - API compatibility
   - Modular component design

**Plugin Architecture Example**
```typescript
// Plugin interface for audit types
interface AuditPlugin {
  name: string;
  version: string;

  // Plugin lifecycle
  initialize(config: PluginConfig): Promise<void>;

  // Audit processing
  process(data: AuditData): Promise<AuditResult>;

  // Validation
  validate(data: AuditData): ValidationResult;

  // UI components (optional)
  getUIComponents?: () => ReactComponent;
}

// Plugin registry
class PluginRegistry {
  private plugins: Map<string, AuditPlugin> = new Map();

  register(plugin: AuditPlugin): void {
    this.plugins.set(plugin.name, plugin);
  }

  async processWithPlugin(
    pluginName: string,
    data: AuditData
  ): Promise<AuditResult> {
    const plugin = this.plugins.get(pluginName);
    if (!plugin) {
      throw new Error(`Plugin ${pluginName} not found`);
    }

    return await plugin.process(data);
  }
}
```

**API Design Principles**
- Versioned APIs (/api/v1/, /api/v2/)
- Backward compatibility
- Rate limiting and quotas
- Comprehensive documentation (OpenAPI)
- SDK for integrations

**Database Extensibility**
```sql
-- Flexible configuration storage
CREATE TABLE system_config (
  id UUID PRIMARY KEY,
  config_key VARCHAR(100) UNIQUE NOT NULL,
  config_value JSONB NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);

-- Future-proof audit results
ALTER TABLE audit_results
ADD COLUMN IF NOT EXISTS ml_data JSONB,
ADD COLUMN IF NOT EXISTS plugin_results JSONB,
ADD COLUMN IF NOT EXISTS future_extensions JSONB;
```

### Consequences
**Benefits**
- Easy to add new features
- Support for multiple customers
- Reduced technical debt
- Faster innovation cycle

**Drawbacks**
- Initial complexity overhead
- Performance considerations
- Testing complexity increases

---

## Summary of Decisions

This ADR collection provides a comprehensive architectural foundation for the Gatekeeper RPA system. The decisions reflect:

1. **Modern Technology Stack**: Next.js, TypeScript, and modern tooling
2. **Scalable Architecture**: Microservices with horizontal scaling
3. **Enterprise Security**: Zero-trust model with comprehensive protection
4. **Operational Excellence**: Monitoring, logging, and observability first
5. **Future-Ready Design**: Extensible for ML integration and multi-tenant support

These architectural decisions will guide the implementation team and ensure the system meets all requirements while being maintainable and extensible for future needs.

**Document Version**: 1.0
**Next Review**: After Sprint 2 completion
**Approval**: Architecture Team