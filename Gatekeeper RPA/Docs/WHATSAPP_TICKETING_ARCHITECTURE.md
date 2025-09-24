# WhatsApp Ticketing System Integration Architecture

## Overview
This document outlines the integration architecture for incorporating WhatsApp ticketing capabilities into the Gatekeeper RPA audit system, leveraging the WhatiTicket community platform as a foundation.

## 🎯 Integration Strategy

### Phase 1: WhatiTicket Foundation
Utilize WhatiTicket's proven WhatsApp ticketing infrastructure as the base layer:
- **Multi-user WhatsApp management**
- **Ticket queuing and assignment**
- **Media support (photos, documents)**
- **Real-time messaging**

### Phase 2: Custom Audit Workflow
Extend WhatiTicket with specialized audit automation:
- **DR number processing**
- **RPA integration**
- **Audit result generation**
- **Automated compliance checking**

## 🏗️ Architecture Design

### Core Components

#### 1. WhatsApp Gateway Layer
```
┌─────────────────────────────────────────┐
│          WhatsApp Gateway              │
├─────────────────────────────────────────┤
│  • WhatiTicket WhatsApp API            │
│  • Message parsing & validation       │
│  • Multi-account management           │
│  • Media handling                     │
│  • Rate limiting & retry logic        │
└─────────────────────────────────────────┘
```

#### 2. Ticket Management System
```
┌─────────────────────────────────────────┐
│        Ticket Management               │
├─────────────────────────────────────────┤
│  • Ticket creation & assignment        │
│  • Queue management                   │
│  • Status tracking                    │
│  • Priority handling                  │
│  • SLA monitoring                     │
└─────────────────────────────────────────┘
```

#### 3. Audit Automation Engine
```
┌─────────────────────────────────────────┐
│        Audit Automation                │
├─────────────────────────────────────────┤
│  • DR number extraction                │
│  • 1Map RPA integration               │
│  • Photo compliance checking           │
│  • Audit result generation             │
│  • Response formatting                │
└─────────────────────────────────────────┘
```

#### 4. Integration Layer
```
┌─────────────────────────────────────────┐
│         Integration Layer              │
├─────────────────────────────────────────┤
│  • WhatiTicket API integration         │
│  • Custom audit workflows             │
│  • Database synchronization           │
│  • Event bus & messaging              │
│  • Error handling & recovery          │
└─────────────────────────────────────────┘
```

## 🔄 Data Flow Architecture

### Message Processing Flow
```
1. Technician sends DR number via WhatsApp
   ↓
2. WhatiTicket receives message and creates ticket
   ↓
3. Audit system detects DR number pattern
   ↓
4. Triggers RPA automation workflow
   ↓
5. 1Map data extraction & photo audit
   ↓
6. Generate audit results
   ↓
7. Update ticket status and send response
   ↓
8. Technician receives audit results
```

### Ticket Lifecycle
```
Created → DR Detected → In Progress → Audit Complete → Response Sent → Closed
    ↓          ↓           ↓              ↓              ↓          ↓
  WhatsApp → Pattern     RPA           Results       WhatsApp    Archive
  Message → Match       Running        Generated     Response    & Report
```

## 🔧 Technical Implementation

### Backend Services

#### WhatsApp Integration Service
```typescript
// Core WhatsApp integration using WhatiTicket patterns
class WhatsAppTicketingService {
  async handleMessage(message: WhatsAppMessage): Promise<void> {
    // Parse message for DR number
    const drNumber = this.extractDRNumber(message.body);

    if (drNumber) {
      // Create audit ticket
      const ticket = await this.createAuditTicket(drNumber, message.from);

      // Trigger audit workflow
      await this.auditService.processAudit(ticket);
    }
  }

  async sendResponse(ticket: AuditTicket, result: AuditResult): Promise<void> {
    const message = this.formatResponse(result);
    await this.whatsAppAPI.sendMessage(ticket.technicianNumber, message);
  }
}
```

#### Audit Workflow Service
```typescript
class AuditWorkflowService {
  async processAudit(ticket: AuditTicket): Promise<void> {
    try {
      // Update ticket status
      await this.updateTicketStatus(ticket.id, 'in_progress');

      // Run RPA automation
      const auditData = await this.rpaService.runAudit(ticket.drNumber);

      // Generate audit results
      const result = await this.generateAuditResult(auditData);

      // Store results
      await this.storeAuditResults(ticket.id, result);

      // Send response
      await this.whatsAppService.sendResponse(ticket, result);

      // Update ticket status
      await this.updateTicketStatus(ticket.id, 'completed');

    } catch (error) {
      await this.handleAuditError(ticket, error);
    }
  }
}
```

#### RPA Integration Service
```typescript
class RPAService {
  async runAudit(drNumber: string): Promise<AuditData> {
    // Launch browser and login to 1Map
    const browser = await playwright.chromium.launch();
    const page = await browser.newPage();

    // Navigate and search for DR
    await this.loginTo1Map(page);
    const searchData = await this.searchDRNumber(page, drNumber);

    // Extract photos and metadata
    const photos = await this.extractPhotos(page);
    const metadata = await this.extractMetadata(page);

    return {
      drNumber,
      propertyId: searchData.propertyId,
      jobId: searchData.jobId,
      address: searchData.address,
      status: searchData.status,
      photos: photos,
      metadata: metadata
    };
  }
}
```

### Database Schema Extensions

#### Enhanced Ticket Table
```sql
CREATE TABLE tickets (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_number VARCHAR(50) UNIQUE NOT NULL,
  dr_number VARCHAR(50),
  technician_number VARCHAR(20),
  technician_name VARCHAR(100),
  message_content TEXT,
  message_timestamp TIMESTAMP,
  status VARCHAR(20) DEFAULT 'pending',
  priority VARCHAR(10) DEFAULT 'normal',
  assigned_to VARCHAR(100),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  completed_at TIMESTAMP,
  audit_result_id UUID REFERENCES audit_results(id),
  whatsapp_message_id VARCHAR(100),
  original_ticket_type VARCHAR(20) DEFAULT 'audit',
  -- WhatiTicket integration fields
  external_ticket_id VARCHAR(50),
  external_system VARCHAR(20),
  sync_status VARCHAR(20) DEFAULT 'pending'
);
```

#### Audit Results Table
```sql
CREATE TABLE audit_results (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_id UUID REFERENCES tickets(id),
  property_id VARCHAR(50),
  job_id VARCHAR(50),
  address TEXT,
  installation_status VARCHAR(50),
  last_modified TIMESTAMP,
  photos_required JSONB, -- Expected photo types
  photos_found JSONB,    -- Actual photos found
  photos_missing JSONB,  -- Missing photos
  compliance_score DECIMAL(5,2),
  audit_details JSONB,
  ml_confidence_score DECIMAL(5,2), -- For Phase 2 ML integration
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW()
);
```

## 🔌 Integration Points

### WhatiTicket API Integration
```typescript
class WhatiTicketIntegration {
  private baseUrl: string;
  private apiKey: string;

  async syncTickets(): Promise<void> {
    // Sync tickets from WhatiTicket
    const tickets = await this.getTickets();

    for (const ticket of tickets) {
      if (this.isAuditTicket(ticket)) {
        await this.processAuditTicket(ticket);
      }
    }
  }

  async updateTicketStatus(ticketId: string, status: string): Promise<void> {
    // Update ticket status in WhatiTicket
    await this.patch(`/tickets/${ticketId}`, { status });
  }

  async sendWhatsAppMessage(ticketId: string, message: string): Promise<void> {
    // Send message via WhatiTicket WhatsApp API
    await this.post(`/tickets/${ticketId}/messages`, {
      type: 'whatsapp',
      content: message
    });
  }
}
```

### Event-Driven Architecture
```typescript
// Event bus for system integration
class EventBus {
  emit(event: string, data: any): void {
    // Emit events to various services
    this.eventEmitter.emit(event, data);
  }
}

// Event listeners
eventBus.on('ticket.created', async (ticket) => {
  if (ticket.type === 'audit') {
    await auditService.processAudit(ticket);
  }
});

eventBus.on('audit.completed', async (result) => {
  await notificationService.sendAuditResult(result);
});
```

## 🚀 Deployment Architecture

### Service Components
```
┌─────────────────────────────────────────┐
│              Load Balancer             │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            API Gateway                  │
├─────────────────────────────────────────┤
│  • Route management                    │
│  • Authentication                      │
│  • Rate limiting                       │
│  • Request logging                     │
└─────────────────────────────────────────┘
                    ↓
┌─────────────┬─────────────┬─────────────┐
│   WhatsApp  │    Audit    │   Database  │
│   Service   │   Service   │   Service   │
│             │             │             │
│ • Message   │ • RPA       │ • Ticket    │
│   Handler   │   Engine    │   Storage   │
│ • Bot       │ • Photo     │ • Audit     │
│   Manager   │   Analysis  │   Results   │
│ • Media     │ • Result    │ • Reports   │
│   Handler   │   Generator │             │
└─────────────┴─────────────┴─────────────┘
                    ↓
┌─────────────────────────────────────────┐
│           External Services             │
├─────────────────────────────────────────┤
│  • WhatiTicket API                      │
│  • WhatsApp Business API               │
│  • 1Map Web Portal                      │
│  • Neon PostgreSQL                      │
└─────────────────────────────────────────┘
```

## 📊 Monitoring & Analytics

### Key Metrics
- **Ticket Processing Time**: End-to-end audit duration
- **WhatsApp Response Time**: Message response latency
- **Audit Success Rate**: Percentage of successful audits
- **Error Rate**: Failed audit attempts
- **System Uptime**: Service availability

### Alerting
- **Failed Audits**: Alert on consecutive failures
- **Slow Processing**: Alert on performance degradation
- **WhatsApp Issues**: Alert on messaging failures
- **Database Issues**: Alert on connection problems

## 🔒 Security Considerations

### Data Protection
- **Encrypted Storage**: Sensitive data encrypted at rest
- **Secure Communication**: HTTPS for all API calls
- **Access Control**: Role-based permissions
- **Audit Logging**: Complete audit trail

### WhatsApp Security
- **Business API Verification**: Verified WhatsApp Business account
- **Message Validation**: Input validation and sanitization
- **Rate Limiting**: Prevent spam and abuse
- **Content Filtering**: Block malicious content

## 📋 Implementation Roadmap

### Phase 1: WhatiTicket Integration (Weeks 1-2)
- [ ] Set up WhatiTicket instance
- [ ] Configure WhatsApp Business API
- [ ] Implement basic ticket synchronization
- [ ] Set up webhooks for real-time updates

### Phase 2: Audit Workflow (Weeks 3-4)
- [ ] Implement DR number detection
- [ ] Create RPA automation for 1Map
- [ ] Build audit result generation
- [ ] Integrate with existing database schema

### Phase 3: Enhanced Features (Weeks 5-6)
- [ ] Add media handling for photos
- [ ] Implement advanced compliance checking
- [ ] Create reporting and analytics
- [ ] Set up monitoring and alerting

### Phase 4: Optimization & Scaling (Weeks 7-8)
- [ ] Performance optimization
- [ ] Load testing
- [ ] Security hardening
- [ ] Documentation and training

This architecture provides a robust foundation for WhatsApp-based audit ticketing while maintaining flexibility for future enhancements and scalability.