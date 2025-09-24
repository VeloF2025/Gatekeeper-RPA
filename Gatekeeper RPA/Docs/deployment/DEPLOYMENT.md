# Gatekeeper RPA Deployment Documentation

## Overview

This document provides comprehensive deployment procedures for the Gatekeeper RPA system, including production deployment strategies, CI/CD pipelines, and operational guidelines.

## Table of Contents

1. [Deployment Architecture](#deployment-architecture)
2. [Prerequisites](#prerequisites)
3. [Environment Setup](#environment-setup)
4. [Production Deployment](#production-deployment)
5. [CI/CD Pipeline](#cicd-pipeline)
6. [Monitoring and Logging](#monitoring-and-logging)
7. [Security Configuration](#security-configuration)
8. [Backup and Recovery](#backup-and-recovery)
9. [Troubleshooting](#troubleshooting)
10. [Maintenance Procedures](#maintenance-procedures)

## Deployment Architecture

### Production Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │────│   Nginx Proxy   │────│  Next.js App    │
│   (HAProxy)     │    │   (Security)    │    │   (Port 3000)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                    ┌─────────────────┐
                    │   RPA Workers   │
                    │   (Port 8081)   │
                    └─────────────────┘
                                │
                    ┌─────────────────┐
                    │  Queue Workers  │
                    │   (Port 8082)   │
                    └─────────────────┘
                                │
    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
    │   PostgreSQL    │────│     Redis       │────│  Monitoring     │
    │   (Database)    │    │   (Cache)       │    │  (Prometheus)   │
    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

### High Availability Setup

- **Load Balancer**: HAProxy for distributing traffic
- **Reverse Proxy**: Nginx with SSL termination
- **Database**: PostgreSQL with read replicas
- **Cache**: Redis Cluster for high availability
- **Monitoring**: Prometheus + Grafana for observability

## Prerequisites

### System Requirements

- **Operating System**: Ubuntu 22.04 LTS or RHEL 9
- **CPU**: Minimum 8 cores, recommended 16 cores
- **Memory**: Minimum 16GB RAM, recommended 32GB
- **Storage**: Minimum 500GB SSD, recommended 1TB
- **Network**: 1Gbps bandwidth, static IP recommended

### Software Requirements

- **Docker**: 24.0+
- **Docker Compose**: 2.20+
- **Node.js**: 18.x LTS
- **PostgreSQL**: 15+
- **Redis**: 7.0+
- **Nginx**: 1.24+
- **HAProxy**: 2.8+

### Security Requirements

- **SSL Certificates**: Valid TLS certificates
- **Firewall**: Configured with allowed ports
- **SSH Access**: Key-based authentication only
- **Monitoring**: Security monitoring and alerts

## Environment Setup

### 1. Server Preparation

```bash
#!/bin/bash
# setup-server.sh

# Update system
sudo apt update && sudo apt upgrade -y

# Install required packages
sudo apt install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    software-properties-common \
    ufw \
    fail2ban \
    logrotate

# Configure firewall
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# Create application user
sudo useradd -m -s /bin/bash gatekeeper
sudo usermod -aG docker gatekeeper

# Create directories
sudo mkdir -p /opt/gatekeeper/{app,logs,backups,ssl}
sudo chown -R gatekeeper:gatekeeper /opt/gatekeeper
```

### 2. Docker Installation

```bash
#!/bin/bash
# install-docker.sh

# Add Docker's official GPG key
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# Add Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Start and enable Docker
sudo systemctl enable --now docker

# Add user to docker group
sudo usermod -aG docker $USER
```

### 3. Database Setup

```bash
#!/bin/bash
# setup-database.sh

# Install PostgreSQL
sudo apt install -y postgresql postgresql-contrib

# Initialize database
sudo -u postgres initdb -D /var/lib/postgresql/data

# Start PostgreSQL
sudo systemctl enable --now postgresql

# Create database and user
sudo -u postgres psql -c "CREATE DATABASE gatekeeper_rpa;"
sudo -u postgres psql -c "CREATE USER gatekeeper_user WITH ENCRYPTED PASSWORD 'your_secure_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE gatekeeper_rpa TO gatekeeper_user;"
sudo -u postgres psql -c "ALTER USER gatekeeper_user CREATEDB;"

# Install Redis
sudo apt install -y redis-server

# Configure Redis
sudo sed -i 's/^# requirepass foobared/requirepass your_redis_password/' /etc/redis/redis.conf
sudo systemctl enable --now redis-server
```

## Production Deployment

### 1. Environment Configuration

Create `.env.production`:

```bash
# Database
DATABASE_URL=postgresql://gatekeeper_user:your_secure_password@localhost:5432/gatekeeper_rpa

# Redis
REDIS_URL=redis://:your_redis_password@localhost:6379/0

# JWT
JWT_SECRET=your_jwt_secret_key_minimum_32_characters
JWT_REFRESH_SECRET=your_jwt_refresh_secret_key_minimum_32_characters
JWT_EXPIRES_IN=15m
JWT_REFRESH_EXPIRES_IN=7d

# Application
NODE_ENV=production
NEXT_PUBLIC_APP_URL=https://your-domain.com
NEXT_PUBLIC_API_URL=https://api.your-domain.com

# Clerk
NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY=your_clerk_publishable_key
CLERK_SECRET_KEY=your_clerk_secret_key

# External Services
WHATSAPP_API_TOKEN=your_whatsapp_api_token
WHATSAPP_PHONE_NUMBER_ID=your_phone_number_id
TICKET_SYSTEM_API_KEY=your_ticket_system_key
MAP_API_KEY=your_map_api_key

# Security
CORS_ORIGIN=https://your-domain.com
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100

# Monitoring
ENABLE_METRICS=true
METRICS_PORT=9090
```

### 2. SSL Certificate Setup

```bash
#!/bin/bash
# setup-ssl.sh

# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Test auto-renewal
sudo certbot renew --dry-run

# Create SSL directory
sudo mkdir -p /opt/gatekeeper/ssl
sudo cp /etc/letsencrypt/live/your-domain.com/fullchain.pem /opt/gatekeeper/ssl/
sudo cp /etc/letsencrypt/live/your-domain.com/privkey.pem /opt/gatekeeper/ssl/
sudo chown -R gatekeeper:gatekeeper /opt/gatekeeper/ssl
```

### 3. Application Deployment

```bash
#!/bin/bash
# deploy.sh

set -e

# Load environment
source .env.production

# Stop existing containers
docker-compose down

# Pull latest changes
git pull origin main

# Install dependencies
npm ci --only=production

# Build application
npm run build

# Generate database migrations
npm run db:generate

# Run database migrations
npm run db:migrate

# Build Docker images
docker-compose build --no-cache

# Start services
docker-compose up -d

# Wait for services to be ready
sleep 30

# Health check
curl -f http://localhost:3000/api/health || exit 1

echo "Deployment completed successfully!"
```

### 4. Nginx Configuration

```nginx
# /etc/nginx/sites-available/gatekeeper
upstream gatekeeper_app {
    server localhost:3000;
}

upstream rpa_workers {
    server localhost:8081;
}

upstream queue_workers {
    server localhost:8082;
}

server {
    listen 80;
    server_name your-domain.com www.your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com www.your-domain.com;

    # SSL configuration
    ssl_certificate /opt/gatekeeper/ssl/fullchain.pem;
    ssl_certificate_key /opt/gatekeeper/ssl/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Security headers
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload";
    add_header X-Content-Type-Options nosniff;
    add_header X-Frame-Options DENY;
    add_header X-XSS-Protection "1; mode=block";
    add_header Referrer-Policy "strict-origin-when-cross-origin";

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req zone=api burst=20 nodelay;

    # Main application
    location / {
        proxy_pass http://gatekeeper_app;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # API endpoints
    location /api/ {
        limit_req zone=api burst=20 nodelay;
        proxy_pass http://gatekeeper_app;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # RPA workers
    location /rpa/ {
        proxy_pass http://rpa_workers;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Queue workers
    location /queue/ {
        proxy_pass http://queue_workers;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Static files
    location /_next/static/ {
        alias /app/.next/static/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Health check
    location /health {
        access_log off;
        proxy_pass http://gatekeeper_app/api/health;
    }
}
```

## CI/CD Pipeline

### GitHub Actions Configuration

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

      redis:
        image: redis:7
        options: >-
          --health-cmd "redis-cli ping"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    strategy:
      matrix:
        node-version: [18.x, 20.x]

    steps:
    - uses: actions/checkout@v4

    - name: Setup Node.js ${{ matrix.node-version }}
      uses: actions/setup-node@v4
      with:
        node-version: ${{ matrix.node-version }}
        cache: 'npm'

    - name: Install dependencies
      run: npm ci

    - name: Run type checking
      run: npm run type-check

    - name: Run linting
      run: npm run lint

    - name: Run unit tests
      run: npm test
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/postgres
        REDIS_URL: redis://localhost:6379/0

    - name: Run integration tests
      run: npm run test:integration
      env:
        DATABASE_URL: postgresql://postgres:postgres@localhost:5432/postgres
        REDIS_URL: redis://localhost:6379/0

    - name: Build application
      run: npm run build

    - name: Build Docker images
      run: docker-compose build

  security-scan:
    runs-on: ubuntu-latest
    needs: test

    steps:
    - uses: actions/checkout@v4

    - name: Run security scan
      run: |
        docker run --rm -v "$(pwd):/app" owasp/zap2docker-stable zap-baseline.py \
          -t http://localhost:3000 \
          -r zap_report.html

    - name: Upload security report
      uses: actions/upload-artifact@v3
      with:
        name: security-report
        path: zap_report.html

  deploy:
    runs-on: ubuntu-latest
    needs: [test, security-scan]
    if: github.ref == 'refs/heads/main'

    steps:
    - uses: actions/checkout@v4

    - name: Setup Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '18.x'
        cache: 'npm'

    - name: Install dependencies
      run: npm ci

    - name: Build application
      run: npm run build

    - name: Deploy to production
      run: |
        echo "${{ secrets.SSH_PRIVATE_KEY }}" > deploy_key
        chmod 600 deploy_key
        ssh -o StrictHostKeyChecking=no -i deploy_key ${{ secrets.PRODUCTION_USER }}@${{ secrets.PRODUCTION_HOST }} 'cd /opt/gatekeeper && ./deploy.sh'
```

### Docker Compose Production

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  nextjs:
    build:
      context: .
      dockerfile: Dockerfile
      target: production
    environment:
      - NODE_ENV=production
      - DATABASE_URL=${DATABASE_URL}
      - REDIS_URL=${REDIS_URL}
      - JWT_SECRET=${JWT_SECRET}
      - JWT_REFRESH_SECRET=${JWT_REFRESH_SECRET}
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - gatekeeper-network
    restart: unless-stopped
    volumes:
      - ./logs:/app/logs

  rpa-workers:
    build:
      context: .
      dockerfile: docker/rpa.Dockerfile
    environment:
      - NODE_ENV=production
      - REDIS_URL=${REDIS_URL}
      - BROWSER_POOL_SIZE=5
      - MAX_CONCURRENT_AUDITS=10
    depends_on:
      redis:
        condition: service_healthy
    networks:
      - gatekeeper-network
    restart: unless-stopped
    volumes:
      - ./logs:/app/logs

  queue-workers:
    build:
      context: .
      dockerfile: docker/queue.Dockerfile
    environment:
      - NODE_ENV=production
      - REDIS_URL=${REDIS_URL}
      - DATABASE_URL=${DATABASE_URL}
      - QUEUE_CONCURRENCY=10
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - gatekeeper-network
    restart: unless-stopped
    volumes:
      - ./logs:/app/logs

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=${POSTGRES_DB}
      - POSTGRES_USER=${POSTGRES_USER}
      - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./backups:/backups
    networks:
      - gatekeeper-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}"]
      interval: 5s
      timeout: 3s
      retries: 5

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_data:/data
    networks:
      - gatekeeper-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "redis-cli", "auth", "${REDIS_PASSWORD}", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - nextjs
      - rpa-workers
      - queue-workers
    networks:
      - gatekeeper-network
    restart: unless-stopped

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'
    networks:
      - gatekeeper-network
    restart: unless-stopped

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3001:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana/dashboards:/etc/grafana/provisioning/dashboards
      - ./monitoring/grafana/datasources:/etc/grafana/provisioning/datasources
    depends_on:
      - prometheus
    networks:
      - gatekeeper-network
    restart: unless-stopped

volumes:
  postgres_data:
  redis_data:
  prometheus_data:
  grafana_data:

networks:
  gatekeeper-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

## Monitoring and Logging

### 1. Prometheus Configuration

```yaml
# monitoring/prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert_rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'nextjs-app'
    static_configs:
      - targets: ['nextjs:3000']
    metrics_path: '/metrics'
    scrape_interval: 10s

  - job_name: 'rpa-workers'
    static_configs:
      - targets: ['rpa-workers:8081']
    metrics_path: '/metrics'
    scrape_interval: 10s

  - job_name: 'queue-workers'
    static_configs:
      - targets: ['queue-workers:8082']
    metrics_path: '/metrics'
    scrape_interval: 10s

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres:5432']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'redis'
    static_configs:
      - targets: ['redis:6379']
    metrics_path: '/metrics'
    scrape_interval: 30s

  - job_name: 'nginx'
    static_configs:
      - targets: ['nginx:80']
    metrics_path: '/metrics'
    scrape_interval: 30s
```

### 2. Log Rotation

```bash
# /etc/logrotate.d/gatekeeper
/opt/gatekeeper/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 gatekeeper gatekeeper
    postrotate
        docker-compose exec nextjs pkill -USR1 node
    endscript
}
```

### 3. Health Check Script

```bash
#!/bin/bash
# health-check.sh

services=(
    "http://localhost:3000/api/health"
    "http://localhost:8081/health"
    "http://localhost:8082/health"
    "http://localhost:9090/-/healthy"
    "http://localhost:3001/api/health"
)

for service in "${services[@]}"; do
    if curl -f "$service" > /dev/null 2>&1; then
        echo "$(date): $service - OK"
    else
        echo "$(date): $service - FAILED"
        # Send alert
        curl -X POST "https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK" \
            -H "Content-Type: application/json" \
            -d "{\"text\":\"Service health check failed: $service\"}"
    fi
done
```

## Security Configuration

### 1. Firewall Rules

```bash
# /etc/ufw/before.rules
# Allow established connections
-A ufw-before-input -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT

# Allow localhost
-A ufw-before-input -s 127.0.0.0/8 -j ACCEPT

# Allow Docker containers
-A ufw-before-input -s 172.20.0.0/16 -j ACCEPT

# Rate limiting for SSH
-A ufw-before-input -p tcp --dport 22 -m conntrack --ctstate NEW -m recent --set
-A ufw-before-input -p tcp --dport 22 -m conntrack --ctstate NEW -m recent --update --seconds 60 --hitcount 4 -j DROP
```

### 2. Fail2Ban Configuration

```ini
# /etc/fail2ban/jail.local
[sshd]
enabled = true
port = ssh
filter = sshd
logpath = /var/log/auth.log
maxretry = 3
bantime = 3600

[nginx-http-auth]
enabled = true
port = http,https
filter = nginx-http-auth
logpath = /var/log/nginx/error.log
maxretry = 3
bantime = 3600

[nginx-limit-req]
enabled = true
port = http,https
filter = nginx-limit-req
logpath = /var/log/nginx/error.log
maxretry = 10
bantime = 3600
```

### 3. Security Headers

```nginx
# Security headers configuration
add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self' https:; frame-src 'self' https:; object-src 'none'; base-uri 'self'; form-action 'self';";
add_header Permissions-Policy "camera=(), microphone=(), geolocation=(), payment=(), usb=()";
add_header X-Content-Type-Options nosniff;
add_header X-Frame-Options DENY;
add_header X-XSS-Protection "1; mode=block";
add_header Referrer-Policy "strict-origin-when-cross-origin";
add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload";
```

## Backup and Recovery

### 1. Database Backup Script

```bash
#!/bin/bash
# backup-database.sh

BACKUP_DIR="/opt/gatekeeper/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/postgres_backup_$DATE.sql"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Create database backup
docker-compose exec postgres pg_dump -U gatekeeper_user gatekeeper_rpa > "$BACKUP_FILE"

# Compress backup
gzip "$BACKUP_FILE"

# Keep only last 30 days of backups
find "$BACKUP_DIR" -name "postgres_backup_*.sql.gz" -mtime +30 -delete

# Upload to cloud storage (optional)
# aws s3 cp "$BACKUP_FILE.gz" s3://your-backup-bucket/

echo "Database backup completed: $BACKUP_FILE.gz"
```

### 2. Redis Backup Script

```bash
#!/bin/bash
# backup-redis.sh

BACKUP_DIR="/opt/gatekeeper/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="$BACKUP_DIR/redis_backup_$DATE.rdb"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Create Redis backup
docker-compose exec redis redis-cli --rdb "$BACKUP_FILE"

# Compress backup
gzip "$BACKUP_FILE"

# Keep only last 7 days of backups
find "$BACKUP_DIR" -name "redis_backup_*.rdb.gz" -mtime +7 -delete

echo "Redis backup completed: $BACKUP_FILE.gz"
```

### 3. Application Backup Script

```bash
#!/bin/bash
# backup-application.sh

BACKUP_DIR="/opt/gatekeeper/backups"
DATE=$(date +%Y%m%d_%H%M%S)
APP_BACKUP_DIR="$BACKUP_DIR/app_backup_$DATE"

# Create backup directory
mkdir -p "$APP_BACKUP_DIR"

# Backup application files
cp -r /opt/gatekeeper/app "$APP_BACKUP_DIR/"
cp -r /opt/gatekeeper/ssl "$APP_BACKUP_DIR/"
cp -r /opt/gatekeeper/logs "$APP_BACKUP_DIR/"

# Backup configuration files
cp docker-compose.yml "$APP_BACKUP_DIR/"
cp .env.production "$APP_BACKUP_DIR/"
cp nginx/nginx.conf "$APP_BACKUP_DIR/"

# Create archive
tar -czf "$APP_BACKUP_DIR.tar.gz" -C "$BACKUP_DIR" "app_backup_$DATE"

# Clean up
rm -rf "$APP_BACKUP_DIR"

# Keep only last 30 days of backups
find "$BACKUP_DIR" -name "app_backup_*.tar.gz" -mtime +30 -delete

echo "Application backup completed: $APP_BACKUP_DIR.tar.gz"
```

### 4. Recovery Script

```bash
#!/bin/bash
# recover.sh

BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file>"
    exit 1
fi

# Stop services
docker-compose down

# Extract backup
tar -xzf "$BACKUP_FILE" -C /opt/gatekeeper/

# Restore database
LATEST_DB_BACKUP=$(ls -t /opt/gatekeeper/backups/postgres_backup_*.sql.gz | head -1)
if [ -n "$LATEST_DB_BACKUP" ]; then
    gunzip -c "$LATEST_DB_BACKUP" | docker-compose exec -T postgres psql -U gatekeeper_user gatekeeper_rpa
fi

# Start services
docker-compose up -d

echo "Recovery completed from: $BACKUP_FILE"
```

## Troubleshooting

### 1. Common Issues

#### Service Not Starting
```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs nextjs
docker-compose logs rpa-workers
docker-compose logs queue-workers

# Check health
curl http://localhost:3000/api/health
```

#### Database Connection Issues
```bash
# Check database connectivity
docker-compose exec postgres psql -U gatekeeper_user -d gatekeeper_rpa -c "SELECT 1;"

# Check database logs
docker-compose logs postgres

# Test connection from application
docker-compose exec nextjs node -e "const { Pool } = require('pg'); const pool = new Pool({ connectionString: process.env.DATABASE_URL }); pool.query('SELECT NOW()', (err, res) => { console.log(err || res.rows[0]); pool.end(); });"
```

#### Memory Issues
```bash
# Check container memory usage
docker stats --no-stream

# Monitor memory usage
docker-compose exec nextjs node -e "console.log(process.memoryUsage());"

# Restart services if needed
docker-compose restart
```

### 2. Performance Issues

#### High CPU Usage
```bash
# Identify CPU-intensive processes
docker-compose exec nextjs top

# Check database queries
docker-compose exec postgres psql -U gatekeeper_user -d gatekeeper_rpa -c "SELECT query, calls, total_time, mean_time FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;"

# Monitor Redis performance
docker-compose exec redis redis-cli info memory
```

#### Slow Response Times
```bash
# Check response times
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:3000/api/health

# Monitor network latency
ping localhost

# Check database connections
docker-compose exec postgres psql -U gatekeeper_user -d gatekeeper_rpa -c "SELECT count(*) FROM pg_stat_activity;"
```

### 3. Security Issues

#### Failed Authentication
```bash
# Check authentication logs
docker-compose logs nextjs | grep -i auth

# Verify JWT configuration
docker-compose exec nextjs node -e "console.log('JWT Secret length:', process.env.JWT_SECRET?.length || 0);"

# Check user sessions
docker-compose exec redis redis-cli keys "session:*"
```

#### SSL Certificate Issues
```bash
# Check certificate expiration
openssl x509 -enddate -noout -in /opt/gatekeeper/ssl/fullchain.pem

# Test SSL connection
openssl s_client -connect your-domain.com:443 -servername your-domain.com

# Renew certificate if needed
sudo certbot renew --dry-run
```

## Maintenance Procedures

### 1. Regular Maintenance Tasks

#### Daily Tasks
- Check system health and logs
- Monitor resource usage
- Verify backup completion
- Review security alerts

#### Weekly Tasks
- Apply security updates
- Rotate logs
- Clean up temporary files
- Review performance metrics

#### Monthly Tasks
- Full system audit
- Database maintenance (VACUUM, ANALYZE)
- SSL certificate renewal check
- Capacity planning review

### 2. Update Procedures

#### Security Updates
```bash
#!/bin/bash
# security-update.sh

# Update system packages
sudo apt update && sudo apt upgrade -y

# Update Docker images
docker-compose pull

# Rebuild and restart services
docker-compose build --no-cache
docker-compose up -d

# Clean up old images
docker image prune -f

echo "Security updates completed"
```

#### Application Updates
```bash
#!/bin/bash
# update-application.sh

# Backup current version
./backup-application.sh

# Pull latest changes
git pull origin main

# Update dependencies
npm ci

# Run database migrations
npm run db:migrate

# Rebuild and restart
docker-compose build --no-cache
docker-compose up -d

echo "Application update completed"
```

### 3. Capacity Planning

```bash
#!/bin/bash
# capacity-check.sh

# Check disk usage
df -h

# Check memory usage
free -h

# Check database size
docker-compose exec postgres psql -U gatekeeper_user -d gatekeeper_rpa -c "SELECT pg_size_pretty(pg_database_size('gatekeeper_rpa'));"

# Check Redis memory usage
docker-compose exec redis redis-cli info memory | grep used_memory

# Generate capacity report
echo "Capacity Report - $(date)" > /opt/gatekeeper/logs/capacity.log
echo "Disk Usage:" >> /opt/gatekeeper/logs/capacity.log
df -h >> /opt/gatekeeper/logs/capacity.log
echo "Memory Usage:" >> /opt/gatekeeper/logs/capacity.log
free -h >> /opt/gatekeeper/logs/capacity.log
echo "Database Size:" >> /opt/gatekeeper/logs/capacity.log
docker-compose exec postgres psql -U gatekeeper_user -d gatekeeper_rpa -c "SELECT pg_size_pretty(pg_database_size('gatekeeper_rpa'));" >> /opt/gatekeeper/logs/capacity.log

echo "Capacity check completed"
```

## Conclusion

This deployment documentation provides comprehensive procedures for deploying, maintaining, and troubleshooting the Gatekeeper RPA system in a production environment. Following these procedures will ensure a stable, secure, and performant deployment.

### Support

For additional support or questions:
- Review troubleshooting section
- Check application logs
- Monitor system metrics
- Consult the development team

### Next Steps

1. Review and customize configurations for your environment
2. Set up monitoring and alerting
3. Test backup and recovery procedures
4. Implement security hardening
5. Establish maintenance schedule