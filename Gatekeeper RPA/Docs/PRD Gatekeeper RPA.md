📑 Product Requirements Document (PRD)

Project: Gatekeeper RPA - WhatsApp Ticketing & Audit System
Version: v2.0
Date: 2025-09-23
Owner: Velocity Fibre / FibreFlow

1. Executive Summary

Technicians currently upload photos for home installations to 1Map, while sharing DR numbers (drop references) via WhatsApp. This leads to inconsistent quality checks, missing evidence, and delays in approval.

This project delivers a comprehensive WhatsApp ticketing and audit system that:

Receives DR numbers via WhatsApp through a WhatiTicket-powered ticketing platform.

Creates automated audit tickets with proper queuing, assignment, and tracking.

Triggers an RPA workflow (via Playwright) to log into 1Map, extract the installation record, and fetch uploaded photos & metadata.

Stores audit results in a structured Neon database with complete ticket lifecycle management.

Responds back to the technician in WhatsApp with either approval or a list of missing/incorrect photos through the integrated ticketing system.

Provides comprehensive ticket management, reporting, and analytics for audit operations.

Lays the foundation for Phase 2 ML auditing, where an AI model scores photo compliance automatically.

The system will integrate seamlessly into FibreFlow with enterprise-grade ticketing capabilities.

2. Objectives & Goals

Short-term:

Implement enterprise-grade WhatsApp ticketing with WhatiTicket integration.

Ensure every home install has complete, verifiable photo evidence.

Provide near-real-time feedback to technicians directly in WhatsApp.

Reduce manual back-office workload through automated ticket management.

Establish comprehensive audit ticket lifecycle tracking.

Medium-term:

Introduce ML auditing (Phase 2).

Enhanced ticketing analytics and reporting capabilities.

Multi-technician team management and workload distribution.

Advanced audit workflow automation with conditional logic.

Long-term:

Integrate dashboards, reporting, and contractor KPIs in FibreFlow (Phase 3).

Create scalable audit automation usable across multiple FNO projects.

Enterprise-level ticketing system with SLA management and escalation.

Mobile application integration for field technicians.

3. Key Requirements
Functional

✅ WhatsApp Ticketing System Integration
- Integrate WhatiTicket community platform for enterprise WhatsApp ticketing
- Support multi-user WhatsApp account management
- Automated ticket creation from WhatsApp messages
- Ticket queuing, assignment, and status tracking
- Multi-media support (photos, documents, voice notes)

✅ Audit Workflow Automation
- Receive DR number from technician via WhatsApp
- Create automated audit ticket with proper categorization
- Log in to 1Map via RPA (Playwright)
- Navigate: Portal → 1Map App → Home Signups & Installations
- Search for DR number, confirm status = Home Installation: Installed
- Extract metadata (Property ID, Job ID, Address, Status, Last Modified, etc.)
- Download or link all attached photos
- Perform photo compliance audit against checklist
- Store results in Neon (Postgres) with ticket lifecycle tracking

✅ Intelligent Response System
- Send WhatsApp response through integrated ticketing system
- Success: "✅ DR1854443 verified. All required photos present. Ticket #12345"
- Failure: "⚠️ DR1854443 missing trench photo. Please re-upload. Ticket #12345"
- Include ticket reference numbers for tracking
- Support for follow-up questions and ticket updates

✅ Ticket Management & Analytics
- Complete ticket lifecycle management (Created → In Progress → Completed → Closed)
- Technician assignment and workload distribution
- Priority handling and SLA management
- Performance analytics and reporting
- Audit trail for all ticket operations

Non-Functional

⚡ Performance: End-to-end check within 30s, WhatsApp response <1s
🔒 Security: Role-based access control, encrypted credential storage, audit logging
📊 Scalability: Handle 4,000+ tickets/day across 10+ projects (200+ technicians)
🧩 Extensibility: ML photo audit module pluggable without redesign
🎯 Reliability: 99.9% uptime, automatic failover, message delivery guarantee
📈 Monitoring: Real-time dashboards, performance metrics, alerting system
🔄 Integration: RESTful APIs, webhooks, event-driven architecture
🌐 Multi-Project: Support 10+ projects with 20+ technicians each
🚀 High Volume: Concurrent processing across multiple WhatsApp numbers

4. System Architecture
flowchart TB
    Tech1[Project 1 Techs] -->|DR #| WT1[WhatsApp #1]
    Tech2[Project 2 Techs] -->|DR #| WT2[WhatsApp #2]
    TechN[Project N Techs] -->|DR #| WTN[WhatsApp #N]

    WT1 -->|Messages| WTP[WhatiTicket Platform]
    WT2 -->|Messages| WTP
    WTN -->|Messages| WTP

    WTP -->|Ticket Creation| PM[Project Manager]
    PM -->|Route to Project| P1[Project 1] & P2[Project 2] & PN[Project N]

    P1 -->|Audit Request| AW1[Audit Engine 1]
    P2 -->|Audit Request| AW2[Audit Engine 2]
    PN -->|Audit Request| AWN[Audit Engine N]

    AW1 -->|RPA Automation| RPA1[RPA Worker 1]
    AW2 -->|RPA Automation| RPA2[RPA Worker 2]
    AWN -->|RPA Automation| RPAN[RPA Worker N]

    RPA1 -->|Data Extraction| 1MAP[1Map Web Portal]
    RPA2 -->|Data Extraction| 1MAP
    RPAN -->|Data Extraction| 1MAP

    1MAP -->|Photos & Metadata| RPA1 & RPA2 & RPAN

    RPA1 -->|Audit Results| DB1[(Project 1 DB)]
    RPA2 -->|Audit Results| DB2[(Project 2 DB)]
    RPAN -->|Audit Results| DBN[(Project N DB)]

    DB1 -->|Results| AW1
    DB2 -->|Results| AW2
    DBN -->|Results| AWN

    AW1 -->|Response| PM
    AW2 -->|Response| PM
    AWN -->|Response| PM

    PM -->|WhatsApp Response| WTP
    WTP -->|Results| Tech1 & Tech2 & TechN

    subgraph "WhatsApp Layer"
        WT1 & WT2 & WTN
        WTP
    end

    subgraph "Project Management"
        PM
    end

    subgraph "Project 1"
        P1
        AW1
        RPA1
        DB1
    end

    subgraph "Project 2"
        P2
        AW2
        RPA2
        DB2
    end

    subgraph "Project N"
        PN
        AWN
        RPAN
        DBN
    end

5. Tech Stack

Frontend/API: Next.js (v14+ App Router)

Language: TypeScript (strict)

Database: Neon (serverless Postgres)

RPA: Playwright (preferred over Puppeteer)

Messaging: WhatiTicket Community Platform + WhatsApp Business API

Deployment: Vercel (API) + Worker (RPA) + WhatiTicket (WhatsApp Ticketing)

Monitoring: Real-time dashboards, performance metrics, alerting

Integration: FibreFlow app (same Next.js/Neon stack)

WhatiTicket Tech Stack:
- Backend: Node.js, Express, Sequelize ORM
- Frontend: React with Material UI
- Database: MySQL
- WhatsApp: whatsapp-web.js
- Deployment: Docker + Nginx + PM2

6. Database Schema (Enhanced)
tickets
Field	Type	Notes
id	UUID	Primary key
ticket_number	VARCHAR(50)	Unique ticket ID (TICKET-YYYYMMDD-XXXX)
dr_number	VARCHAR(50)	Drop reference number
technician_number	VARCHAR(20)	WhatsApp phone number
technician_name	VARCHAR(100)	Technician name
message_content	TEXT	Original WhatsApp message
message_timestamp	Timestamp	Message received time
status	ENUM	pending / in_progress / completed / failed / closed
priority	ENUM	low / normal / high / urgent
assigned_to	VARCHAR(100)	Assigned auditor/manager
created_at	Timestamp	Record creation time
updated_at	Timestamp	Last update time
completed_at	Timestamp	Completion time
whatsapp_message_id	VARCHAR(100)	WhatsApp message ID
external_ticket_id	VARCHAR(50)	WhatiTicket integration ID
external_system	VARCHAR(20)	Integration system name
sync_status	ENUM	pending / synced / failed

audit_results
Field	Type	Notes
id	UUID	Primary key
ticket_id	UUID	→ tickets.id
property_id	VARCHAR(50)	From 1Map
job_id	VARCHAR(50)	From 1Map
address	TEXT	From 1Map
installation_status	VARCHAR(50)	Installation status
last_modified	Timestamp	From 1Map
photos_required	JSONB	Expected photo types
photos_found	JSONB	Actual photos found
photos_missing	JSONB	Missing photos
compliance_score	DECIMAL(5,2)	0-100 score
audit_details	JSONB	Detailed audit results
ml_confidence_score	DECIMAL(5,2)	ML confidence (Phase 2)
created_at	Timestamp	Record creation time
updated_at	Timestamp	Last update time

audit_photos
Field	Type	Notes
id	UUID	Primary key
audit_result_id	UUID	→ audit_results.id
photo_url	TEXT	Link to photo
photo_type	ENUM	trench / ONT / termination / property / other
photo_category	ENUM	required / optional / additional
file_size	INT	File size in bytes
file_format	VARCHAR(10)	Image format (jpg, png, etc.)
upload_timestamp	Timestamp	Photo upload time
compliance_status	ENUM	compliant / non_compliant / needs_review
compliance_notes	TEXT	Compliance details

ticket_assignments
Field	Type	Notes
id	UUID	Primary key
ticket_id	UUID	→ tickets.id
assigned_to	VARCHAR(100)	Assigned user
assigned_by	VARCHAR(100)	Who assigned
assigned_at	Timestamp	Assignment time
status	ENUM	assigned / in_progress / completed
notes	TEXT	Assignment notes

ticket_metrics
Field	Type	Notes
id	UUID	Primary key
ticket_id	UUID	→ tickets.id
metric_type	VARCHAR(50)	Metric type
metric_value	DECIMAL(10,2)	Metric value
metric_unit	VARCHAR(20)	Unit of measurement
recorded_at	Timestamp	When recorded

workflow_events
Field	Type	Notes
id	UUID	Primary key
ticket_id	UUID	→ tickets.id
event_type	VARCHAR(50)	Event type
event_data	JSONB	Event details
event_timestamp	Timestamp	Event time
event_source	VARCHAR(50)	Source system
7. Enhanced Workflow

## Ticket Lifecycle Management

### Phase 1: WhatsApp Ticket Creation
1. **Trigger**: Technician sends DR number via WhatsApp message
2. **WhatiTicket Processing**:
   - Message received by WhatiTicket platform
   - Automatic ticket creation with DR number detection
   - Ticket categorization as "Audit Request"
   - Initial ticket status: "Pending"

### Phase 2: Audit Assignment
3. **Ticket Routing**:
   - Automatic assignment to audit workflow
   - Priority determination based on technician/team
   - SLA timer starts (30-minute target)
   - Assignment notification to relevant stakeholders

### Phase 3: RPA Automation
4. **RPA Script Execution**:
   - Logs into 1Map (Playwright) with secure credentials
   - Navigates → "Home Signups & Installations"
   - Searches for DR number
   - Confirms installation status = "Home Installation: Installed"
   - Extracts metadata (Property ID, Job ID, Address, Status, Last Modified)
   - Downloads or links all attached photos
   - Screenshots taken for audit trail

### Phase 4: Audit Processing
5. **Automated Audit Engine**:
   - Photo validation against required checklist
   - Categorization by photo type (trench, ONT, termination, property)
   - Compliance scoring algorithm
   - Missing photo identification
   - ML confidence scoring (Phase 2)

### Phase 5: Results Storage
6. **Database Persistence**:
   - Ticket status update to "In Progress" → "Completed"
   - Audit results stored in comprehensive database schema
   - Photo metadata and compliance details saved
   - Performance metrics recorded
   - Workflow events logged for audit trail

### Phase 6: Response Generation
7. **Intelligent Response System**:
   - Generate formatted response based on audit results
   - Success: "✅ DR1854443 verified. All required photos present. Ticket #12345"
   - Failure: "⚠️ DR1854443 missing trench photo. Please re-upload. Ticket #12345"
   - Include ticket reference for tracking
   - Support for follow-up questions

### Phase 7: WhatsApp Response
8. **Automated Communication**:
   - Response sent through WhatiTicket platform
   - Message formatted for WhatsApp delivery
   - Delivery confirmation and tracking
   - Read receipt monitoring
   - Response time recording

### Phase 8: Ticket Resolution
9. **Ticket Lifecycle Completion**:
   - Ticket status updated to "Completed" or "Requires Action"
   - Performance metrics calculated and stored
   - Technician performance updated
   - Reports generated for management
   - Archive for historical analysis

## Workflow Status Flow
```
Created → Assigned → In Progress → Audit Complete → Response Sent → Completed
    ↓          ↓           ↓              ↓              ↓          ↓
 WhatsApp   Auto-Assign   RPA Running    Results       WhatsApp    Archive
 Message   to Workflow    Data Extract   Generated     Response    & Report
```

## Error Handling & Recovery
- **RPA Failures**: Automatic retry with exponential backoff
- **WhatsApp Issues**: Message queuing and retry logic
- **Database Errors**: Transaction rollback and error logging
- **System Outages**: Graceful degradation and notification
- **Data Validation**: Input sanitization and format checking

8. Future Enhancements

## Phase 2: ML Audit Agent & Advanced Features
- **ML Photo Classification**: Train model on historical installs (pass/fail photos)
- **Automated Quality Assessment**: Trench depth, termination quality, ONT placement
- **ML Confidence Scoring**: Save confidence scores in ml_confidence_score field
- **Predictive Analytics**: Identify at-risk installations before audit
- **Photo Enhancement**: Automatic photo quality improvement and validation

## Phase 3: FibreFlow Integration & Enterprise Features
- **Advanced Dashboards**: Real-time audit metrics and performance indicators
- **Multi-Tenant Architecture**: Support for multiple FNO projects and clients
- **Advanced RBAC**: Granular role-based access control with delegation
- **SLA Management**: Service level agreements with automated escalation
- **Mobile App Integration**: Native mobile applications for field technicians

## Phase 4: Scaling & Optimization
- **Horizontal Scaling**: Multi-region deployment and load balancing
- **Advanced Analytics**: Business intelligence and predictive modeling
- **IoT Integration**: Smart device integration for automated data collection
- **Blockchain Integration**: Immutable audit trails and verification
- **AI-Powered Insights**: Automated recommendation engine for process improvement

9. Implementation Roadmap

## Phase 1: Foundation & WhatsApp Integration (Weeks 1-4)
- **Week 1-2**: WhatiTicket setup and WhatsApp Business API configuration
- **Week 2-3**: Next.js API development and database schema implementation
- **Week 3-4**: Basic RPA automation and ticket management workflow
- **Week 4**: End-to-end testing with WhatiTicket integration

## Phase 2: Core Features (Weeks 5-8)
- **Week 5-6**: Advanced RPA automation with error handling
- **Week 6-7**: Comprehensive audit workflow and compliance engine
- **Week 7-8**: Ticket management system with assignment and analytics
- **Week 8**: Performance optimization and load testing

## Phase 3: Pilot & Enhancement (Weeks 9-12)
- **Week 9-10**: Pilot program with 50-100 installations
- **Week 10-11**: Feature refinement based on pilot feedback
- **Week 11-12**: Enhanced reporting and dashboard capabilities
- **Week 12**: Documentation and knowledge transfer

## Phase 4: Production & Scaling (Weeks 13-16)
- **Week 13-14**: Production deployment and monitoring setup
- **Week 14-15**: Scaling to full operations (2,000+ tickets/day)
- **Week 15-16**: Advanced features and ML preparation
- **Week 16**: Final optimization and go-live

## Future Phases
- **Q4 2025**: ML photo audit and predictive analytics
- **Q1 2026**: FibreFlow integration and enterprise features
- **Q2 2026**: Mobile app deployment and advanced scaling
- **Q3 2026**: IoT integration and advanced AI features

10. Risks & Mitigations

## Technical Risks
- **RPA Fragility**: UI changes in 1Map could break scripts
  - *Mitigation*: Modular Playwright selectors, monitoring, and quick update process
- **WhatsApp API Limitations**: Rate limiting and policy changes
  - *Mitigation*: Message queuing, retry logic, and multiple account support
- **Database Performance**: High volume of audit data and photos
  - *Mitigation*: Proper indexing, partitioning, and caching strategies
- **Integration Complexity**: Multiple systems integration challenges
  - *Mitigation*: API-first design, comprehensive testing, and fallback mechanisms

## Business Risks
- **User Adoption**: Technician resistance to new workflow
  - *Mitigation*: Training programs, intuitive interface, and gradual rollout
- **Data Privacy**: Sensitive installation data and photos
  - *Mitigation*: Encryption, access controls, and compliance with data protection laws
- **Vendor Dependencies**: Third-party service reliability
  - *Mitigation*: Multiple vendor options, in-house capabilities, and SLAs
- **Scalability**: Rapid growth beyond initial capacity
  - *Mitigation*: Cloud-native architecture, auto-scaling, and performance monitoring

## Operational Risks
- **System Downtime**: Critical service interruption
  - *Mitigation*: High availability setup, failover mechanisms, and disaster recovery
- **Security Breaches**: Unauthorized access to audit data
  - *Mitigation*: Security audits, penetration testing, and zero-trust architecture
- **Performance Degradation**: Slow response times under load
  - *Mitigation*: Performance monitoring, auto-scaling, and optimization
- **Data Loss**: Loss of critical audit records
  - *Mitigation*: Regular backups, disaster recovery, and data validation