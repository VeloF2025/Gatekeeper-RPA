# WhatiTicket Setup Guide for Gatekeeper RPA

## Overview
This guide provides step-by-step instructions for setting up WhatiTicket Community platform to support the Gatekeeper RPA audit system for 10+ projects with 20+ technicians per project.

## 🎯 Setup Requirements

### Hardware Requirements
- **Server**: Ubuntu 20.04+ VPS with 4GB RAM, 2 CPU cores, 80GB SSD
- **Database**: MySQL 8.0+ (separate server recommended)
- **Storage**: 50GB+ for media and logs
- **Network**: Static IP, SSL certificate, ports 80/443 open

### Software Requirements
- **Node.js**: v16+ (LTS recommended)
- **MySQL**: 8.0+
- **PM2**: Process manager
- **Nginx**: Reverse proxy
- **Docker**: (Optional but recommended)

### Domain Requirements
- **Main Domain**: `whatsapp.yourcompany.com`
- **Backend Subdomain**: `api.whatsapp.yourcompany.com`
- **Frontend Subdomain**: `app.whatsapp.yourcompany.com`

## 🚀 Installation Process

### Phase 1: Server Preparation

#### 1. Update System and Install Dependencies
```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install essential packages
sudo apt install -y curl wget git nginx software-properties-common

# Install Node.js 18 LTS
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# Install PM2 globally
sudo npm install -g pm2

# Verify installations
node --version  # Should be v18.x
npm --version   # Should be latest
pm2 --version  # Should be latest
```

#### 2. Install MySQL Database
```bash
# Install MySQL
sudo apt update
sudo apt install -y mysql-server

# Secure MySQL installation
sudo mysql_secure_installation

# Create WhatiTicket database
mysql -u root -p
```

```sql
-- Execute these commands in MySQL
CREATE DATABASE whatsticket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'whatsuser'@'localhost' IDENTIFIED BY 'your_strong_password';
GRANT ALL PRIVILEGES ON whatsticket.* TO 'whatsuser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### 3. Setup Project Directory
```bash
# Create project directory
sudo mkdir -p /opt/whats-ticket
sudo chown $USER:$USER /opt/whats-ticket
cd /opt/whats-ticket

# Clone WhatiTicket repository
git clone https://github.com/canove/whaticket-community.git .
```

### Phase 2: Backend Configuration

#### 4. Configure Backend Environment
```bash
# Navigate to backend directory
cd backend

# Install dependencies
npm install

# Create environment file
cp .env.example .env

# Edit environment configuration
nano .env
```

```env
# Database Configuration
DB_DIALECT=mysql
DB_HOST=localhost
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

# File Storage
UPLOAD_DIR=../public/uploads
MAX_FILE_SIZE=10485760  # 10MB

# Email Configuration (Optional)
MAIL_HOST=smtp.yourcompany.com
MAIL_PORT=587
MAIL_USER=your_email@yourcompany.com
MAIL_PASS=your_email_password
MAIL_FROM=noreply@yourcompany.com
```

#### 5. Setup Database Schema
```bash
# Run database migrations
npx sequelize-cli db:migrate

# Run database seeders (optional)
npx sequelize-cli db:seed:all
```

#### 6. Start Backend Service
```bash
# Start backend with PM2
pm2 start app.js --name "whats-ticket-backend"

# Save PM2 configuration
pm2 save

# Set PM2 to start on boot
pm2 startup
```

### Phase 3: Frontend Configuration

#### 7. Configure Frontend Environment
```bash
# Navigate to frontend directory
cd ../frontend

# Install dependencies
npm install

# Create environment file
cp .env.example .env.local

# Edit environment configuration
nano .env.local
```

```env
REACT_APP_BACKEND_URL=https://api.whatsapp.yourcompany.com
REACT_APP_FRONTEND_URL=https://app.whatsapp.yourcompany.com
```

#### 8. Build Frontend Application
```bash
# Build frontend for production
npm run build

# Copy build files to nginx directory
sudo cp -r build/* /var/www/whatsapp/
```

### Phase 4: Nginx Configuration

#### 9. Configure Nginx for Backend
```bash
# Create nginx configuration for backend
sudo nano /etc/nginx/sites-available/api.whatsapp.yourcompany.com
```

```nginx
server {
    listen 80;
    server_name api.whatsapp.yourcompany.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

#### 10. Configure Nginx for Frontend
```bash
# Create nginx configuration for frontend
sudo nano /etc/nginx/sites-available/app.whatsapp.yourcompany.com
```

```nginx
server {
    listen 80;
    server_name app.whatsapp.yourcompany.com;
    root /var/www/whatsapp;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /static/ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

#### 11. Enable Sites and Test Configuration
```bash
# Enable nginx sites
sudo ln -s /etc/nginx/sites-available/api.whatsapp.yourcompany.com /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/app.whatsapp.yourcompany.com /etc/nginx/sites-enabled/

# Test nginx configuration
sudo nginx -t

# Reload nginx
sudo systemctl reload nginx
```

### Phase 5: SSL Configuration

#### 12. Install SSL Certificates
```bash
# Install Certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtain SSL certificates
sudo certbot --nginx -d api.whatsapp.yourcompany.com -d app.whatsapp.yourcompany.com

# Test auto-renewal
sudo certbot renew --dry-run
```

### Phase 6: WhatsApp Business API Setup

#### 13. Configure WhatsApp Business Platform
```bash
# Create WhatsApp business account
# Visit: https://developers.facebook.com/docs/whatsapp/getting-started/

# Get required credentials:
# - Business ID
# - Phone Number ID
# - Permanent Access Token
# - Webhook Verification Token

# Update backend .env file with WhatsApp credentials
```

#### 14. Configure Webhooks
```bash
# Create webhook endpoint for WhatsApp messages
# WhatiTicket will automatically set up webhooks when you configure WhatsApp

# Test webhook endpoint
curl -X POST https://api.whatsapp.yourcompany.com/webhook/whatsapp \
  -H "Content-Type: application/json" \
  -d '{"test": true}'
```

## 🔧 Multi-Project Configuration

### 15. Setup Multi-Project Structure
```bash
# Create project directories
cd /opt/whats-ticket/backend/src
mkdir -p projects

# Create project configuration files
cat > projects/project-config.json << EOF
{
  "projects": {
    "project1": {
      "name": "Velocity Fibre",
      "whatsappNumber": "+27123456789",
      "technicians": 20,
      "prefix": "VF",
      "database": "whatsticket_project1"
    },
    "project2": {
      "name": "FibreFlow Tech",
      "whatsappNumber": "+27123456790",
      "technicians": 20,
      "prefix": "FFT",
      "database": "whatsticket_project2"
    }
  }
}
EOF
```

### 16. Create Project Databases
```sql
-- Create separate databases for each project
CREATE DATABASE whatsticket_project1 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE whatsticket_project2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create users for each project
CREATE USER 'project1_user'@'localhost' IDENTIFIED BY 'project1_password';
CREATE USER 'project2_user'@'localhost' IDENTIFIED BY 'project2_password';

-- Grant permissions
GRANT ALL PRIVILEGES ON whatsticket_project1.* TO 'project1_user'@'localhost';
GRANT ALL PRIVILEGES ON whatsticket_project2.* TO 'project2_user'@'localhost';
FLUSH PRIVILEGES;
```

## 📊 Scaling Configuration

### 17. Configure for High Volume (200+ technicians)
```bash
# Update PM2 configuration for multiple instances
pm2 delete whats-ticket-backend
pm2 start app.js --name "whats-ticket-backend" -i 4  # 4 instances

# Configure database connection pooling
# Add to backend .env:
DB_POOL_MIN=5
DB_POOL_MAX=20
DB_POOL_ACQUIRE=30000
DB_POOL_IDLE=10000

# Configure rate limiting
# Add to backend configuration:
RATE_LIMIT_WINDOW_MS=900000  # 15 minutes
RATE_LIMIT_MAX_REQUESTS=1000
```

### 18. Setup Monitoring and Logging
```bash
# Install logging service
npm install winston winston-daily-rotate-file

# Create logging configuration
cat > logging.config.js << EOF
module.exports = {
  level: 'info',
  format: 'json',
  defaultMeta: { service: 'whats-ticket' },
  transports: [
    new winston.transports.DailyRotateFile({
      filename: 'logs/error-%DATE%.log',
      datePattern: 'YYYY-MM-DD',
      level: 'error',
    }),
    new winston.transports.DailyRotateFile({
      filename: 'logs/combined-%DATE%.log',
      datePattern: 'YYYY-MM-DD',
    }),
  ],
};
EOF
```

## 🚀 Production Deployment

### 19. Final Configuration and Testing
```bash
# Restart all services
pm2 restart all
sudo systemctl restart nginx

# Check service status
pm2 status
sudo systemctl status nginx

# Test all endpoints
curl -X GET https://api.whatsapp.yourcompany.com/health
curl -X GET https://app.whatsapp.yourcompany.com/

# Test WhatsApp integration
# Send test message to configured WhatsApp number
```

### 20. Setup Backup and Maintenance
```bash
# Create backup script
cat > /opt/whats-ticket/backup.sh << EOF
#!/bin/bash
# Backup databases
mysqldump -u root -p whatsticket > /backups/whats-ticket-\$(date +%Y%m%d).sql
mysqldump -u root -p whatsticket_project1 > /backups/project1-\$(date +%Y%m%d).sql
mysqldump -u root -p whatsticket_project2 > /backups/project2-\$(date +%Y%m%d).sql

# Backup application files
tar -czf /backups/whats-ticket-app-\$(date +%Y%m%d).tar.gz /opt/whats-ticket/

# Clean old backups (keep 30 days)
find /backups -name "*.sql" -mtime +30 -delete
find /backups -name "*.tar.gz" -mtime +30 -delete
EOF

# Make backup script executable
chmod +x /opt/whats-ticket/backup.sh

# Setup cron job for daily backups
echo "0 2 * * * /opt/whats-ticket/backup.sh" | sudo crontab -
```

## 🔒 Security Configuration

### 21. Implement Security Measures
```bash
# Configure firewall
sudo ufw enable
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Setup fail2ban
sudo apt install -y fail2ban
sudo systemctl enable fail2ban
sudo systemctl start fail2ban

# Configure fail2ban for nginx
sudo nano /etc/fail2ban/jail.local
```

```ini
[nginx-http-auth]
enabled = true
filter = nginx-http-auth
port = http,https
logpath = /var/log/nginx/error.log

[nginx-limit-req]
enabled = true
filter = nginx-limit-req
port = http,https
logpath = /var/log/nginx/error.log
```

## 📈 Performance Optimization

### 22. Optimize for High Volume
```bash
# Configure MySQL for high performance
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf
```

```ini
[mysqld]
# Performance settings
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M
max_connections = 200
query_cache_type = 1
query_cache_size = 128M
```

```bash
# Restart MySQL
sudo systemctl restart mysql

# Configure PHP (if used)
sudo nano /etc/php/7.4/fpm/php.ini
```

```ini
memory_limit = 256M
max_execution_time = 300
upload_max_filesize = 10M
post_max_size = 10M
```

## 🎯 Success Criteria

After completing this setup, you should have:

- ✅ WhatiTicket platform running with SSL
- ✅ WhatsApp Business API integration
- ✅ Multi-project support (10+ projects)
- ✅ High-volume handling (200+ technicians)
- ✅ Comprehensive logging and monitoring
- ✅ Backup and disaster recovery
- ✅ Security hardening
- ✅ Performance optimization

## 🚨 Troubleshooting

### Common Issues
- **WhatsApp messages not receiving**: Check webhook configuration and API tokens
- **Database connection errors**: Verify database credentials and MySQL service status
- **Performance issues**: Check PM2 instance count and database optimization
- **SSL certificate errors**: Verify domain configuration and Certbot setup

### Support Resources
- [WhatiTicket Community Documentation](https://github.com/canove/whaticket-community)
- [WhatsApp Business API Documentation](https://developers.facebook.com/docs/whatsapp/)
- [Nginx Configuration Guide](https://nginx.org/en/docs/)
- [MySQL Performance Tuning](https://dev.mysql.com/doc/refman/8.0/en/performance.html)

This setup provides a robust foundation for your Gatekeeper RPA system with enterprise-grade WhatsApp ticketing capabilities.