# Docker Deployment Guide

## Overview
This comprehensive guide covers deploying the Gatekeeper RPA system with WhatiTicket using Docker containers.

## 🚀 Quick Start

### Prerequisites
- Docker 20.10+
- Docker Compose v2.0+
- Domain name (e.g., `whatsapp.yourcompany.com`)
- SSL certificates (Let's Encrypt)

### Step 1: Clone and Setup
```bash
# Clone WhatiTicket repository
git clone https://github.com/canove/whaticket-community.git whatsticket-gatekeeper
cd whatsticket-gatekeeper

# Copy Docker configuration
cp -r ../Gatekeeper\ RPA/docker/* .

# Run setup script
chmod +x docker/scripts/setup.sh
./docker/scripts/setup.sh
```

### Step 2: Configure Environment
```bash
# Edit environment file
nano docker/.env

# Required configuration:
# - Database credentials
# - JWT secrets
# - WhatsApp Business API credentials
# - 1Map credentials
# - SSL configuration
```

### Step 3: Start Services
```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f

# Run health check
./docker/scripts/health-check.sh
```

### Step 4: Initialize Database
```bash
# Run database migrations
docker-compose exec backend npx sequelize-cli db:migrate

# Run database seeders
docker-compose exec backend npx sequelize-cli db:seed:all
```

### Step 5: Setup SSL
```bash
# Run SSL setup
./docker/scripts/setup-ssl.sh
```

## 🐳 Docker Compose Configuration

### Production Environment
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  backend:
    build:
      context: .
      dockerfile: docker/backend.Dockerfile
    environment:
      NODE_ENV: production
      # ... production environment variables
    deploy:
      replicas: 3
      resources:
        limits:
          cpus: '2.0'
          memory: 2G
        reservations:
          cpus: '1.0'
          memory: 1G

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

  # ... other services with production configuration
```

### Development Environment
```yaml
# docker-compose.dev.yml
version: '3.8'

services:
  backend:
    build:
      context: .
      dockerfile: docker/backend.Dockerfile
      target: development
    volumes:
      - ./backend:/app
      - /app/node_modules
    environment:
      NODE_ENV: development
    command: npm run dev

  # ... other services for development
```

## 🔧 Service Management

### Scaling Services
```bash
# Scale RPA workers for high load
docker-compose up -d --scale rpa-workers=8

# Scale backend services
docker-compose up -d --scale backend=3

# Scale queue workers
docker-compose up -d --scale queue-worker=4
```

### Health Monitoring
```bash
# Check overall health
./docker/scripts/health-check.sh

# Check individual services
docker-compose exec backend curl -f http://localhost:8080/health
docker-compose exec rpa-workers curl -f http://localhost:8081/health

# Monitor resource usage
docker-compose stats
```

### Log Management
```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f rpa-workers

# View logs with timestamps
docker-compose logs -f --tail=100 -t

# Export logs
docker-compose logs > system.log
```

## 📊 Monitoring and Metrics

### Access Monitoring Dashboards
```bash
# Grafana Dashboard
# URL: http://localhost:3020
# Username: admin
# Password: ${GRAFANA_PASSWORD}

# Prometheus Metrics
# URL: http://localhost:9090
```

### Key Metrics to Monitor
- **Response Time**: <1s for WhatsApp responses
- **Throughput**: 4,000+ tickets/day
- **Error Rate**: <1% for all operations
- **Database Performance**: <100ms query time
- **Queue Processing**: <5s queue time
- **Memory Usage**: <70% utilization
- **CPU Usage**: <80% utilization

### Alert Configuration
```yaml
# monitoring/alerts.yml
groups:
  - name: whatsticket-alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"

      - alert: SlowResponseTime
        expr: histogram_quantile(0.95, http_request_duration_seconds_bucket) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Slow response time detected"

      - alert: LowAvailableWorkers
        expr: up{job="rpa-workers"} < 2
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Low number of available RPA workers"
```

## 🔒 Security Configuration

### SSL/TLS Setup
```bash
# Automated SSL setup with Let's Encrypt
./docker/scripts/setup-ssl.sh

# Manual SSL certificate installation
# 1. Place certificates in nginx/ssl/
# 2. Update nginx configuration
# 3. Restart nginx service
```

### Network Security
```yaml
# Docker network security
networks:
  whatsticket-network:
    driver: bridge
    internal: false  # Allow external access
    ipam:
      config:
        - subnet: 172.20.0.0/16

# Service isolation
services:
  backend:
    networks:
      - whatsticket-network
    cap_drop:
      - ALL
    cap_add:
      - CHOWN
      - NET_BIND_SERVICE

  rpa-workers:
    networks:
      - whatsticket-network
    cap_drop:
      - ALL
    cap_add:
      - CHOWN
      - NET_BIND_SERVICE
      - SYS_ADMIN  # Required for Playwright
```

### Environment Security
```bash
# Generate secure passwords
openssl rand -base64 32

# Set secure file permissions
chmod 600 .env
chmod 600 docker/.env

# Use Docker secrets for sensitive data
echo "database_password" | docker secret create db_password -
```

## 💾 Backup and Recovery

### Automated Backups
```bash
# Create backup
./docker/scripts/backup.sh

# Schedule daily backups (cron)
0 2 * * * /path/to/backup.sh

# List backups
ls -la /opt/whats-ticket/backups/

# Restore backup
./docker/scripts/restore.sh backup-file.sql
```

### Database Backup Strategy
```yaml
# Backup configuration
backup:
  schedule: "0 2 * * *"  # Daily at 2 AM
  retention: 30 days
  compression: true
  encryption: true

  databases:
    - name: whatsticket
      user: whatsuser
    - name: project1_audit
      user: project1_user
    - name: project2_audit
      user: project2_user

  storage:
    local: /opt/whats-ticket/backups/
    remote: s3://backup-bucket/whats-ticket/
```

## 🔄 Updates and Maintenance

### Rolling Updates
```bash
# Pull latest images
docker-compose pull

# Update services with zero downtime
docker-compose up -d --no-deps backend
docker-compose up -d --no-deps frontend

# Update database schema
docker-compose exec backend npx sequelize-cli db:migrate
```

### Service Restart Strategy
```yaml
# Restart policy configuration
services:
  backend:
    restart_policy:
      condition: on-failure
      delay: 5s
      max_attempts: 3
      window: 120s

  rpa-workers:
    restart_policy:
      condition: any
      delay: 10s
      max_attempts: 5
      window: 300s
```

### Health Check Configuration
```yaml
# Health checks for all services
services:
  backend:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  rpa-workers:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/health"]
      interval: 60s
      timeout: 30s
      retries: 3
      start_period: 120s
```

## 🚨 Troubleshooting

### Common Issues

#### Service Won't Start
```bash
# Check service logs
docker-compose logs backend
docker-compose logs rpa-workers

# Check resource usage
docker-compose stats

# Check network connectivity
docker-compose exec backend ping mysql
docker-compose exec backend ping redis

# Restart services
docker-compose restart
```

#### Database Connection Issues
```bash
# Check database service
docker-compose exec mysql mysql -u root -p

# Check database connectivity
docker-compose exec backend npx sequelize-cli db:migrate:status

# Reset database connection pool
docker-compose restart backend
```

#### Performance Issues
```bash
# Monitor resource usage
docker-compose stats

# Check database performance
docker-compose exec mysql mysql -u root -p -e "SHOW PROCESSLIST;"

# Check Redis performance
docker-compose exec redis redis-cli INFO

# Scale services
docker-compose up -d --scale rpa-workers=8
```

### Recovery Procedures

#### Complete System Reset
```bash
# Stop all services
docker-compose down

# Remove volumes (WARNING: This will delete all data)
docker-compose down -v

# Restart from scratch
docker-compose up -d
```

#### Partial Recovery
```bash
# Restart specific services
docker-compose restart backend
docker-compose restart rpa-workers

# Restore from backup
./docker/scripts/restore.sh backup-file.sql
```

## 📈 Performance Optimization

### Docker Optimization
```yaml
# Resource limits and reservations
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '2.0'
          memory: 2G
        reservations:
          cpus: '1.0'
          memory: 1G

  rpa-workers:
    deploy:
      resources:
        limits:
          cpus: '4.0'
          memory: 4G
        reservations:
          cpus: '2.0'
          memory: 2G
```

### Database Optimization
```sql
-- MySQL optimization
SET GLOBAL innodb_buffer_pool_size = 1G;
SET GLOBAL max_connections = 200;
SET GLOBAL query_cache_size = 128M;

-- Create indexes for performance
CREATE INDEX idx_tickets_dr_number ON tickets(dr_number);
CREATE INDEX idx_audit_results_ticket_id ON audit_results(ticket_id);
CREATE INDEX idx_audit_photos_result_id ON audit_photos(audit_result_id);
```

### Application Optimization
```javascript
// Node.js optimization
const cluster = require('cluster');
const os = require('os');

if (cluster.isMaster) {
  const cpuCount = os.cpus().length;
  for (let i = 0; i < cpuCount; i++) {
    cluster.fork();
  }
} else {
  // Worker process
  require('./app.js');
}
```

## 🎯 Production Checklist

### Pre-Deployment
- [ ] Configure all environment variables
- [ ] Set up SSL certificates
- [ ] Configure monitoring and alerting
- [ ] Set up backup procedures
- [ ] Test all services locally
- [ ] Run security scans
- [ ] Performance test the system

### Post-Deployment
- [ ] Verify all services are running
- [ ] Test end-to-end functionality
- [ ] Monitor system performance
- [ ] Verify backup processes
- [ ] Test failover procedures
- [ ] Configure alerting thresholds
- [ ] Document the deployment

This Docker deployment guide provides everything needed to deploy and manage the Gatekeeper RPA system in production using Docker containers.