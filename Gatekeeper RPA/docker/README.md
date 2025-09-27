# Docker Setup for WhatiTicket + Gatekeeper RPA

## Overview
This guide provides Docker-based deployment for the complete Gatekeeper RPA system, including WhatiTicket community platform and all supporting services.

## 🏗️ Architecture Overview

### Docker Architecture
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
│  │ • Sequelize     │  │ • Browser Pool  │  │ • Neon          │           │
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

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Domain name (e.g., `whatsapp.yourcompany.com`)
- SSL certificates (Let's Encrypt recommended)

### 1. Clone and Setup
```bash
# Clone the repository
git clone https://github.com/canove/whaticket-community.git whatsticket-gatekeeper
cd whatsticket-gatekeeper

# Copy Docker configuration files
cp -r ../Gatekeeper\ RPA/docker/* .

# Create environment files
cp .env.example .env
cp docker-compose.env.example docker-compose.env
```

### 2. Configure Environment
```bash
# Edit environment configuration
nano .env
```

```env
# Database Configuration
DB_DIALECT=mysql
DB_HOST=mysql
DB_PORT=3306
DB_USER=whatsuser
DB_PASS=your_strong_password
DB_NAME=whatsticket

# JWT Configuration
JWT_SECRET=your_super_secure_jwt_secret_key_change_this
JWT_REFRESH_SECRET=your_super_secure_refresh_secret_key_change_this

# Server Configuration
PORT=8080
BACKEND_URL=https://api.whatsapp.yourcompany.com
FRONTEND_URL=https://app.whatsapp.yourcompany.com

# WhatsApp Configuration
WA_BUSINESS_TOKEN=your_whatsapp_business_api_token
WA_BUSINESS_ID=your_whatsapp_business_id
WA_PHONE_NUMBER_ID=your_whatsapp_phone_number_id

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379

# File Storage
UPLOAD_DIR=public/uploads
MAX_FILE_SIZE=10485760  # 10MB
```

### 3. Start Services
```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

### 4. Initialize Database
```bash
# Run database migrations
docker-compose exec backend npx sequelize-cli db:migrate

# Run database seeders
docker-compose exec backend npx sequelize-cli db:seed:all
```

### 5. Setup SSL (Optional)
```bash
# Run SSL setup script
./scripts/setup-ssl.sh
```

## 🐳 Docker Configuration

### Docker Compose Services
```yaml
version: '3.8'

services:
  # Database
  mysql:
    image: mysql:8.0
    command: --default-authentication-plugin=mysql_native_password
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_USER: ${DB_USER}
      MYSQL_PASSWORD: ${DB_PASS}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"
    networks:
      - whatsticket-network
    restart: unless-stopped

  # Redis
  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    networks:
      - whatsticket-network
    restart: unless-stopped

  # WhatiTicket Backend
  backend:
    build:
      context: .
      dockerfile: docker/backend.Dockerfile
    environment:
      NODE_ENV: production
      DB_DIALECT: mysql
      DB_HOST: mysql
      DB_PORT: 3306
      DB_USER: ${DB_USER}
      DB_PASS: ${DB_PASS}
      DB_NAME: ${DB_NAME}
      JWT_SECRET: ${JWT_SECRET}
      JWT_REFRESH_SECRET: ${JWT_REFRESH_SECRET}
      REDIS_HOST: redis
      REDIS_PORT: 6379
      BACKEND_URL: ${BACKEND_URL}
      FRONTEND_URL: ${FRONTEND_URL}
    volumes:
      - ./backend:/app
      - ./public/uploads:/app/public/uploads
      - /opt/whats-ticket/logs:/app/logs
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    networks:
      - whatsticket-network
    restart: unless-stopped

  # WhatiTicket Frontend
  frontend:
    build:
      context: .
      dockerfile: docker/frontend.Dockerfile
    volumes:
      - ./frontend/dist:/usr/share/nginx/html
    ports:
      - "3000:80"
    depends_on:
      - backend
    networks:
      - whatsticket-network
    restart: unless-stopped

  # Nginx Reverse Proxy
  nginx:
    image: nginx:alpine
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
      - ./public/uploads:/var/www/uploads
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - backend
      - frontend
    networks:
      - whatsticket-network
    restart: unless-stopped

  # RPA Workers
  rpa-workers:
    build:
      context: .
      dockerfile: docker/rpa.Dockerfile
    environment:
      NODE_ENV: production
      REDIS_HOST: redis
      REDIS_PORT: 6379
      BROWSER_POOL_SIZE: 10
      1MAP_USERNAME: ${1MAP_USERNAME}
      1MAP_PASSWORD: ${1MAP_PASSWORD}
    volumes:
      - ./rpa:/app
      - /opt/whats-ticket/screenshots:/app/screenshots
      - /opt/whats-ticket/photos:/app/photos
      - /opt/whats-ticket/logs:/app/logs
    depends_on:
      - redis
    networks:
      - whatsticket-network
    restart: unless-stopped
    deploy:
      replicas: 4

  # Queue Worker
  queue-worker:
    build:
      context: .
      dockerfile: docker/queue.Dockerfile
    environment:
      NODE_ENV: production
      REDIS_HOST: redis
      REDIS_PORT: 6379
      DB_HOST: mysql
      DB_USER: ${DB_USER}
      DB_PASS: ${DB_PASS}
      DB_NAME: ${DB_NAME}
    depends_on:
      - mysql
      - redis
    networks:
      - whatsticket-network
    restart: unless-stopped

  # Monitoring
  prometheus:
    image: prom/prometheus
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    ports:
      - "9090:9090"
    networks:
      - whatsticket-network
    restart: unless-stopped

  grafana:
    image: grafana/grafana
    environment:
      GF_SECURITY_ADMIN_PASSWORD: ${GRAFANA_PASSWORD}
    volumes:
      - grafana_data:/var/lib/grafana
      - ./monitoring/grafana:/etc/grafana/provisioning
    ports:
      - "3001:3000"
    networks:
      - whatsticket-network
    restart: unless-stopped

volumes:
  mysql_data:
  redis_data:
  prometheus_data:
  grafana_data:

networks:
  whatsticket-network:
    driver: bridge
```

### Dockerfiles

#### Backend Dockerfile
```dockerfile
# docker/backend.Dockerfile
FROM node:18-alpine

# Install system dependencies
RUN apk add --no-cache \
    dumb-init \
    python3 \
    make \
    g++ \
    linux-headers \
    udev

# Create app directory
WORKDIR /app

# Copy package files
COPY backend/package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy application code
COPY backend/ ./

# Create logs directory
RUN mkdir -p logs

# Create uploads directory
RUN mkdir -p public/uploads

# Set permissions
RUN chown -R node:node /app

# Switch to non-root user
USER node

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Expose port
EXPOSE 8080

# Start application
CMD ["dumb-init", "node", "app.js"]
```

#### Frontend Dockerfile
```dockerfile
# docker/frontend.Dockerfile
FROM node:18-alpine as build

# Install build dependencies
RUN apk add --no-cache python3 make g++

# Create app directory
WORKDIR /app

# Copy package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm ci

# Copy application code
COPY frontend/ ./

# Build application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built files
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx/frontend.conf /etc/nginx/conf.d/default.conf

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:80 || exit 1

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

#### RPA Dockerfile
```dockerfile
# docker/rpa.Dockerfile
FROM node:18-alpine

# Install system dependencies for Playwright
RUN apk add --no-cache \
    dumb-init \
    chromium \
    nss \
    freetype \
    harfbuzz \
    ca-certificates \
    ttf-freefont

# Install Playwright browsers
RUN npx playwright install chromium

# Create app directory
WORKDIR /app

# Copy package files
COPY rpa/package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy application code
COPY rpa/ ./

# Create directories
RUN mkdir -p screenshots photos logs

# Set environment variables
ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true \
    PUPPETEER_EXECUTABLE_PATH=/usr/bin/chromium-browser

# Health check
HEALTHCHECK --interval=60s --timeout=30s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/health || exit 1

# Expose port
EXPOSE 8081

# Start application
CMD ["dumb-init", "node", "index.js"]
```

#### Queue Worker Dockerfile
```dockerfile
# docker/queue.Dockerfile
FROM node:18-alpine

# Install system dependencies
RUN apk add --no-cache dumb-init

# Create app directory
WORKDIR /app

# Copy package files
COPY queue/package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy application code
COPY queue/ ./

# Create logs directory
RUN mkdir -p logs

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8082/health || exit 1

# Expose port
EXPOSE 8082

# Start application
CMD ["dumb-init", "node", "index.js"]
```

## 🔧 Configuration Files

### Nginx Configuration
```nginx
# nginx/nginx.conf
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:80;
    }

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=5r/m;

    # Compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # Security headers
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header X-XSS-Protection "1; mode=block";
    add_header Referrer-Policy "strict-origin-when-cross-origin";

    # HTTP server
    server {
        listen 80;
        server_name api.whatsapp.yourcompany.com app.whatsapp.yourcompany.com;

        # Let's Encrypt challenge
        location /.well-known/acme-challenge/ {
            root /var/www/certbot;
        }

        # Redirect to HTTPS
        location / {
            return 301 https://$host$request_uri;
        }
    }

    # HTTPS server
    server {
        listen 443 ssl http2;
        server_name api.whatsapp.yourcompany.com;

        ssl_certificate /etc/nginx/ssl/api.whatsapp.yourcompany.com/fullchain.pem;
        ssl_certificate_key /etc/nginx/ssl/api.whatsapp.yourcompany.com/privkey.pem;

        # Security
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;
        ssl_session_cache shared:SSL:10m;
        ssl_session_timeout 1d;

        # Backend API
        location / {
            limit_req zone=api burst=20 nodelay;
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;

            # Timeouts
            proxy_connect_timeout 5s;
            proxy_read_timeout 30s;
            proxy_send_timeout 30s;
        }

        # File uploads
        location /uploads {
            alias /var/www/uploads;
            expires 1y;
            add_header Cache-Control "public, immutable";
        }

        # Health check
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }

    server {
        listen 443 ssl http2;
        server_name app.whatsapp.yourcompany.com;

        ssl_certificate /etc/nginx/ssl/app.whatsapp.yourcompany.com/fullchain.pem;
        ssl_certificate_key /etc/nginx/ssl/app.whatsapp.yourcompany.com/privkey.pem;

        # Security
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;
        ssl_session_cache shared:SSL:10m;
        ssl_session_timeout 1d;

        # Frontend application
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Health check
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

## 📊 Monitoring & Management

### Health Checks
```bash
# Check overall system health
./scripts/health-check.sh

# Check individual services
docker-compose exec backend curl -f http://localhost:8080/health
docker-compose exec rpa-workers curl -f http://localhost:8081/health
docker-compose exec queue-worker curl -f http://localhost:8082/health
```

### Log Management
```bash
# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f backend
docker-compose logs -f rpa-workers

# View logs with timestamps
docker-compose logs -f --tail=100 -t
```

### Performance Monitoring
```bash
# Access Grafana dashboard
# URL: http://localhost:3020
# Username: admin
# Password: ${GRAFANA_PASSWORD}

# Access Prometheus metrics
# URL: http://localhost:9090
```

## 🔧 Management Commands

### Service Management
```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Restart specific service
docker-compose restart backend
docker-compose restart rpa-workers

# Scale services
docker-compose up -d --scale rpa-workers=8
docker-compose up -d --scale backend=3
```

### Database Management
```bash
# Access database shell
docker-compose exec mysql mysql -u ${DB_USER} -p

# Create backup
./scripts/backup.sh

# Restore backup
./scripts/restore.sh backup-file.sql

# Run database migrations
docker-compose exec backend npx sequelize-cli db:migrate

# Run database seeders
docker-compose exec backend npx sequelize-cli db:seed:all
```

### SSL Management
```bash
# Setup SSL certificates
./scripts/setup-ssl.sh

# Renew SSL certificates
./scripts/renew-ssl.sh

# View SSL certificate status
./scripts/ssl-status.sh
```

## 🚀 Production Deployment

### 1. Environment Preparation
```bash
# Create production environment
cp docker-compose.yml docker-compose.prod.yml

# Edit production configuration
nano docker-compose.prod.yml
```

### 2. Security Hardening
```bash
# Generate secure passwords
openssl rand -base64 32

# Update environment variables
nano .env
```

### 3. Deployment
```bash
# Deploy production environment
docker-compose -f docker-compose.prod.yml up -d

# Verify deployment
./scripts/health-check.sh
```

### 4. Monitoring Setup
```bash
# Setup monitoring dashboards
./scripts/setup-monitoring.sh

# Configure alerts
./scripts/setup-alerts.sh
```

## 🛠️ Development Environment

### Local Development
```bash
# Start development environment
docker-compose -f docker-compose.dev.yml up -d

# Access services
# Backend: http://localhost:8080
# Frontend: http://localhost:3000
# Database: localhost:3306
# Redis: localhost:6379
```

### Development Commands
```bash
# Install development dependencies
docker-compose exec backend npm install

# Run tests
docker-compose exec backend npm test

# Run database migrations
docker-compose exec backend npx sequelize-cli db:migrate

# Access development logs
docker-compose logs -f backend
```

## 🔧 Troubleshooting

### Common Issues

#### Service Won't Start
```bash
# Check service logs
docker-compose logs backend
docker-compose logs rpa-workers

# Check resource usage
docker-compose stats

# Restart services
docker-compose restart
```

#### Database Connection Issues
```bash
# Check database service
docker-compose exec mysql mysql -u root -p

# Check network connectivity
docker-compose exec backend ping mysql

# Verify database credentials
docker-compose exec backend env | grep DB_
```

#### Performance Issues
```bash
# Check system resources
docker-compose stats

# Monitor database performance
docker-compose exec mysql mysql -u root -p -e "SHOW PROCESSLIST;"

# Check Redis performance
docker-compose exec redis redis-cli INFO
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

#### Data Recovery
```bash
# Create backup before maintenance
./scripts/backup.sh

# Restore from backup
./scripts/restore.sh backup-file.sql
```

This Docker setup provides a complete, production-ready environment for the Gatekeeper RPA system with WhatiTicket integration.