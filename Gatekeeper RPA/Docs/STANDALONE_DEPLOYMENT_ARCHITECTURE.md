# Standalone Deployment Architecture (Docker)

## Overview
This document outlines the standalone deployment architecture for the Gatekeeper RPA system, designed to operate independently without FibreFlow integration initially.

## 🏗️ Architecture Overview

### System Components
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Standalone System                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐           │
│  │   WhatsApp      │  │   Audit         │  │   Database      │           │
│  │   Ticketing     │  │   Automation    │  │   Layer         │           │
│  │   Layer         │  │   Layer         │  │                 │           │
│  │                 │  │                 │  │                 │           │
│  │ • WhatiTicket   │  │ • RPA Workers   │  │ • Neon          │           │
│  │ • Multi-Project │  │ • Playwright    │  │ • Project DBs   │           │
│  │ • Message Queue │  │ • Error Recovery│  │ • Audit Results │           │
│  │ • Media Handler │  │ • Performance   │  │ • Analytics     │           │
│  │                 │  │   Monitoring    │  │                 │           │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘           │
│           │                       │                       │                 │
│           └───────────────────────┼───────────────────────┘                 │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                          Management Layer                              │ │
│  │                                                                     │ │
│  │ • Monitoring & Alerting      • Logging & Auditing                     │ │
│  │ • Performance Analytics     • Security & Access Control                │ │
│  │ • Configuration Management  • Backup & Recovery                        │ │
│  │ • User Management           • API Gateway                             │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                           │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 🐳 Docker-First Deployment Architecture

### Quick Start with Docker
The recommended approach is to use Docker for deployment. The complete setup is available in the `docker/` directory.

```bash
# Clone the WhatiTicket repository
git clone https://github.com/canove/whaticket-community.git whatsticket-gatekeeper
cd whatsticket-gatekeeper

# Copy Docker configuration
cp -r ../Gatekeeper\ RPA/docker/* .

# Run setup script
chmod +x docker/scripts/setup.sh
./docker/scripts/setup.sh

# Edit environment configuration
nano docker/.env

# Start all services
docker-compose up -d

# Run health check
./docker/scripts/health-check.sh
```

### Infrastructure Requirements

#### Single Server Deployment (Recommended for starting)
```yaml
# Server Specifications
server:
  cpu: 8 cores
  memory: 16GB RAM
  storage: 500GB SSD
  network: 1Gbps
  os: Ubuntu 20.04 LTS

# Docker Requirements
docker:
  version: 20.10+
  compose: v2.0+

# Resource Allocation per Service
services:
  backend: 2 CPU, 1GB RAM (2 replicas)
  frontend: 1 CPU, 512MB RAM
  rpa_workers: 2 CPU, 2GB RAM (4 replicas)
  mysql: 2 CPU, 4GB RAM
  redis: 1 CPU, 1GB RAM
  nginx: 1 CPU, 512MB RAM
  monitoring: 1 CPU, 2GB RAM
```

#### Multi-Server Production Deployment
```yaml
# Application Servers (3 nodes for High Availability)
app_servers:
  cpu: 8 cores
  memory: 16GB RAM
  storage: 200GB SSD
  network: 1Gbps
  count: 3

# Database Server
database_server:
  cpu: 4 cores
  memory: 32GB RAM
  storage: 500GB SSD
  network: 1Gbps
  count: 1

# Monitoring Server
monitoring_server:
  cpu: 2 cores
  memory: 8GB RAM
  storage: 200GB SSD
  network: 1Gbps
  count: 1
```

### Docker Network Architecture
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Docker Network                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐           │
│  │   WhatiTicket   │  │   Audit RPA     │  │   Database      │           │
│  │   Backend       │  │   Workers       │  │   Cluster       │           │
│  │                 │  │                 │  │                 │           │
│  │ • Node.js       │  │ • Node.js       │  │ • MySQL         │           │
│  │ • Express       │  │ • Playwright    │  │ • Redis         │           │
│  │ • Sequelize     │  │ • Browser Pool  │  │ • Project DBs   │           │
│  │                 │  │                 │  │                 │           │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘           │
│           │                       │                       │                 │
│           └───────────────────────┼───────────────────────┘                 │
│                                   │                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                      Supporting Services                                │ │
│  │                                                                     │ │
│  │ • Nginx (Reverse Proxy)    • Redis (Queue)     • Monitoring        │ │
│  │ • SSL/TLS Management      • File Storage      • Logging          │ │
│  │ • Load Balancing          • Backup System     • Security         │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                           │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Docker Services Overview
```yaml
services:
  # Core Application
  backend:          # WhatiTicket Node.js backend
  frontend:         # React frontend
  rpa-workers:      # Playwright automation workers
  queue-worker:     # Message queue processor

  # Infrastructure
  mysql:           # Primary database
  redis:           # Cache and message broker
  nginx:           # Reverse proxy and load balancer

  # Project Isolation
  project1-db:     # Project-specific database
  project2-db:     # Project-specific database

  # Monitoring
  prometheus:       # Metrics collection
  grafana:         # Visualization dashboards
  fluentd:         # Log aggregation

  # Maintenance
  backup:          # Automated backup service
```

## 🔧 Component Details

### 1. WhatsApp Ticketing Layer
```yaml
services:
  whatsticket_backend:
    image: node:18-alpine
    environment:
      NODE_ENV: production
      DB_HOST: neon-db-host
      DB_PORT: 5432
      DB_NAME: whatsticket
      REDIS_HOST: redis
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"
    volumes:
      - ./backend:/app
      - /opt/whats-ticket/uploads:/app/uploads
    depends_on:
      - postgres
      - redis
    deploy:
      replicas: 2
      resources:
        limits:
          cpus: '1.0'
          memory: 1G

  whatsticket_frontend:
    image: nginx:alpine
    ports:
      - "3000:80"
    volumes:
      - ./frontend/dist:/usr/share/nginx/html
    depends_on:
      - whatsticket_backend

  whatsapp_webhook:
    image: node:18-alpine
    environment:
      WEBHOOK_URL: https://api.whatsapp.company.com/webhook
      SECRET_TOKEN: ${WEBHOOK_SECRET}
    ports:
      - "8081:8080"
    volumes:
      - ./webhook:/app
```

### 2. Audit Automation Layer
```yaml
services:
  rpa_worker_pool:
    image: node:18-alpine
    environment:
      NODE_ENV: production
      BROWSER_POOL_SIZE: 10
      MAX_CONCURRENT_AUDITS: 5
      1MAP_USERNAME: ${1MAP_USERNAME}
      1MAP_PASSWORD: ${1MAP_PASSWORD}
    volumes:
      - ./rpa:/app
      - /opt/whats-ticket/screenshots:/app/screenshots
      - /opt/whats-ticket/photos:/app/photos
    depends_on:
      - redis
      - postgres
    deploy:
      replicas: 4
      resources:
        limits:
          cpus: '2.0'
          memory: 2G

  audit_queue:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data

  audit_scheduler:
    image: node:18-alpine
    environment:
      REDIS_HOST: redis
      DB_HOST: postgres
    volumes:
      - ./scheduler:/app
    depends_on:
      - redis
      - postgres
```

### 3. Database Layer
```yaml
services:
  postgres:
    image: postgres:13-alpine
    environment:
      POSTGRES_DB: whatsticket
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 4G

  # Project-specific databases (for multi-project isolation)
  project1_db:
    image: postgres:13-alpine
    environment:
      POSTGRES_DB: project1_audit
      POSTGRES_USER: ${PROJECT1_DB_USER}
      POSTGRES_PASSWORD: ${PROJECT1_DB_PASSWORD}
    volumes:
      - project1_data:/var/lib/postgresql/data

  project2_db:
    image: postgres:13-alpine
    environment:
      POSTGRES_DB: project2_audit
      POSTGRES_USER: ${PROJECT2_DB_USER}
      POSTGRES_PASSWORD: ${PROJECT2_DB_PASSWORD}
    volumes:
      - project2_data:/var/lib/postgresql/data
```

### 4. Management Layer
```yaml
services:
  monitoring:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3001:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana:/etc/grafana/provisioning

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.17.0
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data

  logstash:
    image: docker.elastic.co/logstash/logstash:7.17.0
    volumes:
      - ./logging/logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:7.17.0
    ports:
      - "5601:5601"
    depends_on:
      - elasticsearch
```

## 📊 Scaling Strategy

### Horizontal Scaling
```yaml
# Docker Compose scaling configuration
services:
  whatsticket_backend:
    deploy:
      replicas: ${BACKEND_REPLICAS:-2}
      update_config:
        parallelism: 1
        delay: 10s
      restart_policy:
        condition: on-failure

  rpa_worker_pool:
    deploy:
      replicas: ${RPA_REPLICAS:-4}
      update_config:
        parallelism: 2
        delay: 10s
      restart_policy:
        condition: on-failure
        max_attempts: 3
```

### Load Balancing
```nginx
# nginx.conf
upstream whatsticket_backend {
    least_conn;
    server app-node1:8080 weight=3;
    server app-node2:8080 weight=3;
    server app-node3:8080 weight=2;
    server app-node4:8080 backup;
}

server {
    listen 80;
    server_name api.whatsapp.company.com;

    location / {
        proxy_pass http://whatsticket_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Health check
        proxy_connect_timeout 5s;
        proxy_read_timeout 30s;
        proxy_send_timeout 30s;
    }
}
```

## 🔒 Security Configuration

### Network Security
```yaml
# Security groups and firewall rules
security:
  firewall:
    - allow: tcp/22 (SSH)
    - allow: tcp/80 (HTTP)
    - allow: tcp/443 (HTTPS)
    - allow: tcp/5432 (PostgreSQL - internal only)
    - allow: tcp/6379 (Redis - internal only)
    - deny: all

  ssl:
    certificate: "/etc/letsencrypt/live/api.whatsapp.company.com/fullchain.pem"
    private_key: "/etc/letsencrypt/live/api.whatsapp.company.com/privkey.pem"

  cors:
    allowed_origins:
      - "https://app.whatsapp.company.com"
      - "https://dashboard.whatsapp.company.com"
    allowed_methods: ["GET", "POST", "PUT", "DELETE"]
    allowed_headers: ["Content-Type", "Authorization"]
```

### Application Security
```yaml
security:
  authentication:
    jwt:
      algorithm: "HS256"
      expiration: "24h"
      refresh_expiration: "7d"

    session:
      timeout: "30m"
      max_concurrent_sessions: 5

  authorization:
    rbac:
      roles:
        - admin
        - auditor
        - technician
        - viewer

      permissions:
        admin: ["*"]
        auditor: ["read", "write", "audit"]
        technician: ["read", "create_ticket"]
        viewer: ["read"]

  rate_limiting:
    window_size: "15m"
    max_requests: 1000
    burst: 100

  input_validation:
    strict_mode: true
    sanitize_html: true
    max_file_size: "10MB"
    allowed_file_types: ["jpg", "jpeg", "png", "gif"]
```

## 📈 Monitoring & Alerting

### Metrics Collection
```yaml
monitoring:
  prometheus:
    scrape_interval: "15s"
    evaluation_interval: "15s"

    metrics:
      - http_requests_total
      - http_request_duration_seconds
      - database_connections_active
      - rpa_workers_active
      - queue_size
      - error_rate

  alerts:
    - name: "HighErrorRate"
      condition: "error_rate > 0.05"
      duration: "5m"
      severity: "critical"

    - name: "HighResponseTime"
      condition: "http_request_duration_seconds > 2"
      duration: "5m"
      severity: "warning"

    - name: "LowAvailableWorkers"
      condition: "rpa_workers_active < 2"
      duration: "1m"
      severity: "critical"
```

### Logging Strategy
```yaml
logging:
  level: "INFO"
  format: "json"

  outputs:
    - file:
        path: "/var/log/whats-ticket/app.log"
        max_size: "100MB"
        max_files: 10

    - elasticsearch:
        hosts: ["elasticsearch:9200"]
        index: "whats-ticket-logs"

    - cloudwatch:
        region: "us-east-1"
        log_group: "/whats-ticket/production"

  log_categories:
    - audit_events
    - api_requests
    - rpa_operations
    - errors
    - performance
```

## 🔄 Deployment Process

### CI/CD Pipeline
```yaml
deployment:
  stages:
    - name: "Build"
      steps:
        - "npm install"
        - "npm run build"
        - "npm run test"
        - "docker build -t whatsticket:${COMMIT_SHA} ."

    - name: "Test"
      steps:
        - "docker-compose -f docker-compose.test.yml up --abort-on-container-exit"
        - "docker-compose -f docker-compose.test.yml down"

    - name: "Deploy Staging"
      steps:
        - "kubectl config use-context staging"
        - "kubectl apply -f k8s/staging/"
        - "kubectl rollout status deployment/whatsticket"

    - name: "Integration Test"
      steps:
        - "npm run test:e2e"
        - "npm run test:performance"

    - name: "Deploy Production"
      steps:
        - "kubectl config use-context production"
        - "kubectl apply -f k8s/production/"
        - "kubectl rollout status deployment/whatsticket"
        - "kubectl annotate deployment/whatsticket kubernetes.io/change-cause='${COMMIT_MESSAGE}'"
```

### Health Checks
```yaml
health_checks:
  whatsticket_backend:
    path: "/health"
    interval: "30s"
    timeout: "10s"
    retries: 3
    success_threshold: 1
    failure_threshold: 3

  database:
    query: "SELECT 1"
    interval: "30s"
    timeout: "5s"
    retries: 3

  rpa_workers:
    script: "node health-check.js"
    interval: "60s"
    timeout: "30s"
    retries: 2
```

## 🎯 Success Metrics

### Performance Metrics
- **Response Time**: <1s for WhatsApp responses, <30s for complete audits
- **Throughput**: 4,000+ tickets/day across all projects
- **Uptime**: 99.9% availability
- **Error Rate**: <1% for all operations

### Scalability Metrics
- **Concurrent Users**: Support 200+ technicians simultaneously
- **Database Performance**: <100ms query response time
- **Queue Processing**: <5s queue time for audit requests
- **Memory Usage**: <70% utilization during peak hours

### Security Metrics
- **Vulnerability Scans**: Zero critical vulnerabilities
- **Access Control**: 100% RBAC compliance
- **Data Encryption**: 100% data at rest and in transit
- **Audit Trail**: 100% operation logging

This standalone deployment architecture provides a robust, scalable foundation for the Gatekeeper RPA system with enterprise-grade capabilities for multi-project operations.