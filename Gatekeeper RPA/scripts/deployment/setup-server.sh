#!/bin/bash

# Gatekeeper RPA Server Setup Script
# This script sets up a fresh server for Gatekeeper RPA deployment

set -e

echo "🚀 Starting Gatekeeper RPA server setup..."

# Configuration variables
SERVER_USER="gatekeeper"
APP_DIR="/opt/gatekeeper"
DB_NAME="gatekeeper_rpa"
DB_USER="gatekeeper_user"
DB_PASSWORD=$(openssl rand -base64 32)
REDIS_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 32)
JWT_REFRESH_SECRET=$(openssl rand -base64 32)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
}

# Check if running as root
if [[ $EUID -ne 0 ]]; then
   error "This script must be run as root"
   exit 1
fi

# Check if system is supported
if ! command -v apt-get &> /dev/null; then
    error "This script supports only Ubuntu/Debian systems"
    exit 1
fi

# Update system
log "Updating system packages..."
apt-get update && apt-get upgrade -y

# Install required packages
log "Installing required packages..."
apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    software-properties-common \
    ufw \
    fail2ban \
    logrotate \
    htop \
    vim \
    git \
    wget \
    unzip \
    nginx \
    postgresql \
    postgresql-contrib \
    redis-server \
    supervisor

# Configure firewall
log "Configuring firewall..."
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable

# Create application user
log "Creating application user..."
if ! id "$SERVER_USER" &>/dev/null; then
    useradd -m -s /bin/bash -G sudo,docker $SERVER_USER
    echo "$SERVER_USER ALL=(ALL) NOPASSWD:/usr/bin/docker-compose,/usr/bin/docker" > /etc/sudoers.d/gatekeeper-docker
    chmod 440 /etc/sudoers.d/gatekeeper-docker
fi

# Create directories
log "Creating application directories..."
mkdir -p $APP_DIR/{app,logs,backups,ssl,scripts}
chown -R $SERVER_USER:$SERVER_USER $APP_DIR
chmod 755 $APP_DIR

# Install Docker
log "Installing Docker..."
# Add Docker's official GPG key
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

# Add Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker packages
apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Start and enable Docker
systemctl enable --now docker

# Add user to docker group
usermod -aG docker $SERVER_USER

# Install Docker Compose
log "Installing Docker Compose..."
curl -SL https://github.com/docker/compose/releases/download/v2.20.3/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Configure PostgreSQL
log "Configuring PostgreSQL..."
systemctl enable --now postgresql

# Create database and user
sudo -u postgres psql -c "CREATE DATABASE $DB_NAME;"
sudo -u postgres psql -c "CREATE USER $DB_USER WITH ENCRYPTED PASSWORD '$DB_PASSWORD';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;"
sudo -u postgres psql -c "ALTER USER $DB_USER CREATEDB;"

# Configure PostgreSQL for production
echo "host $DB_NAME $DB_USER 127.0.0.1/32 md5" >> /etc/postgresql/*/main/pg_hba.conf
systemctl restart postgresql

# Configure Redis
log "Configuring Redis..."
systemctl enable --now redis-server

# Update Redis configuration
sed -i "s/^# requirepass foobared/requirepass $REDIS_PASSWORD/" /etc/redis/redis.conf
sed -i "s/^# maxmemory-policy noeviction/maxmemory-policy allkeys-lru/" /etc/redis/redis.conf
systemctl restart redis-server

# Configure Nginx
log "Configuring Nginx..."
systemctl enable --now nginx

# Create Nginx configuration
cat > /etc/nginx/sites-available/gatekeeper << EOF
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
    server_name _;
    return 301 https://\$host\$request_uri;
}

server {
    listen 443 ssl http2;
    server_name _;

    # SSL configuration (will be updated by Let's Encrypt)
    ssl_certificate /etc/ssl/certs/ssl-cert-snakeoil.pem;
    ssl_certificate_key /etc/ssl/private/ssl-cert-snakeoil.key;
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
    limit_req_zone \$binary_remote_addr zone=api:10m rate=10r/s;
    limit_req zone=api burst=20 nodelay;

    # Main application
    location / {
        proxy_pass http://gatekeeper_app;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;
    }

    # API endpoints
    location /api/ {
        limit_req zone=api burst=20 nodelay;
        proxy_pass http://gatekeeper_app;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # RPA workers
    location /rpa/ {
        proxy_pass http://rpa_workers;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # Queue workers
    location /queue/ {
        proxy_pass http://queue_workers;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }

    # Health check
    location /health {
        access_log off;
        proxy_pass http://gatekeeper_app/api/health;
    }
}
EOF

# Enable the site
ln -sf /etc/nginx/sites-available/gatekeeper /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default

# Test Nginx configuration
nginx -t

# Configure Fail2Ban
log "Configuring Fail2Ban..."
cat > /etc/fail2ban/jail.local << EOF
[sshd]
enabled = true
port = ssh
filter = sshd
logpath = /var/log/auth.log
maxretry = 3
bantime = 3600
findtime = 600

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
EOF

systemctl enable --now fail2ban

# Configure log rotation
log "Configuring log rotation..."
cat > /etc/logrotate.d/gatekeeper << EOF
$APP_DIR/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 $SERVER_USER $SERVER_USER
    postrotate
        docker-compose -f $APP_DIR/app/docker-compose.yml exec nextjs pkill -USR1 node 2>/dev/null || true
    endscript
}
EOF

# Create environment file
log "Creating environment configuration..."
cat > $APP_DIR/.env.production << EOF
# Database
DATABASE_URL=postgresql://$DB_USER:$DB_PASSWORD@localhost:5432/$DB_NAME

# Redis
REDIS_URL=redis://:$REDIS_PASSWORD@localhost:6379/0

# JWT
JWT_SECRET=$JWT_SECRET
JWT_REFRESH_SECRET=$JWT_REFRESH_SECRET
JWT_EXPIRES_IN=15m
JWT_REFRESH_EXPIRES_IN=7d

# Application
NODE_ENV=production
NEXT_PUBLIC_APP_URL=https://your-domain.com
NEXT_PUBLIC_API_URL=https://api.your-domain.com

# Security
CORS_ORIGIN=https://your-domain.com
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100

# Monitoring
ENABLE_METRICS=true
METRICS_PORT=9090
EOF

# Set permissions
chown $SERVER_USER:$SERVER_USER $APP_DIR/.env.production
chmod 600 $APP_DIR/.env.production

# Create backup script
log "Creating backup script..."
cat > $APP_DIR/scripts/backup.sh << 'EOF'
#!/bin/bash

BACKUP_DIR="/opt/gatekeeper/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Database backup
log "Creating database backup..."
docker-compose exec postgres pg_dump -U gatekeeper_user gatekeeper_rpa > "$BACKUP_DIR/postgres_backup_$DATE.sql"
gzip "$BACKUP_DIR/postgres_backup_$DATE.sql"

# Redis backup
log "Creating Redis backup..."
docker-compose exec redis redis-cli --rdb "$BACKUP_DIR/redis_backup_$DATE.rdb"
gzip "$BACKUP_DIR/redis_backup_$DATE.rdb"

# Application backup
log "Creating application backup..."
tar -czf "$BACKUP_DIR/app_backup_$DATE.tar.gz" -C /opt/gatekeeper app/ ssl/ logs/

# Clean up old backups
find "$BACKUP_DIR" -name "postgres_backup_*.sql.gz" -mtime +30 -delete
find "$BACKUP_DIR" -name "redis_backup_*.rdb.gz" -mtime +7 -delete
find "$BACKUP_DIR" -name "app_backup_*.tar.gz" -mtime +30 -delete

echo "Backup completed: $DATE"
EOF

chmod +x $APP_DIR/scripts/backup.sh
chown $SERVER_USER:$SERVER_USER $APP_DIR/scripts/backup.sh

# Create health check script
log "Creating health check script..."
cat > $APP_DIR/scripts/health-check.sh << 'EOF'
#!/bin/bash

services=(
    "http://localhost:3000/api/health"
    "http://localhost:8081/health"
    "http://localhost:8082/health"
)

for service in "${services[@]}"; do
    if curl -f "$service" > /dev/null 2>&1; then
        echo "$(date): $service - OK"
    else
        echo "$(date): $service - FAILED"
        # Send notification (configure your notification method)
        # curl -X POST "YOUR_WEBHOOK_URL" -d '{"text":"Service health check failed: '$service'"}'
    fi
done
EOF

chmod +x $APP_DIR/scripts/health-check.sh
chown $SERVER_USER:$SERVER_USER $APP_DIR/scripts/health-check.sh

# Add to crontab
log "Setting up cron jobs..."
(crontab -l 2>/dev/null; echo "0 2 * * * $APP_DIR/scripts/backup.sh") | crontab -
(crontab -l 2>/dev/null; echo "*/5 * * * * $APP_DIR/scripts/health-check.sh") | crontab -

# Create systemd service for monitoring
log "Creating monitoring service..."
cat > /etc/systemd/system/gatekeeper-monitor.service << EOF
[Unit]
Description=Gatekeeper RPA Monitoring
After=network.target

[Service]
Type=simple
User=$SERVER_USER
WorkingDirectory=$APP_DIR
ExecStart=$APP_DIR/scripts/health-check.sh
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

systemctl enable gatekeeper-monitor

# Install Certbot for SSL
log "Installing Certbot..."
apt-get install -y certbot python3-certbot-nginx

# Create deployment script
log "Creating deployment script..."
cat > $APP_DIR/scripts/deploy.sh << 'EOF'
#!/bin/bash

set -e

log "Starting deployment..."

# Load environment
source .env.production

# Stop existing containers
if [ -f "docker-compose.yml" ]; then
    docker-compose down
fi

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
if curl -f http://localhost:3000/api/health; then
    log "Deployment completed successfully!"
else
    error "Health check failed after deployment"
    exit 1
fi
EOF

chmod +x $APP_DIR/scripts/deploy.sh
chown $SERVER_USER:$SERVER_USER $APP_DIR/scripts/deploy.sh

# Create security hardening script
log "Creating security hardening script..."
cat > $APP_DIR/scripts/security-hardening.sh << 'EOF'
#!/bin/bash

log "Applying security hardening..."

# Update system
apt-get update && apt-get upgrade -y

# Update Docker images
docker-compose pull

# Clean up old Docker images
docker image prune -f

# Check for vulnerable packages
debsecan --format detail

# Reset firewall rules
ufw --force reset
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable

# Restart services
systemctl restart fail2ban
systemctl restart nginx

log "Security hardening completed"
EOF

chmod +x $APP_DIR/scripts/security-hardening.sh
chown $SERVER_USER:$SERVER_USER $APP_DIR/scripts/security-hardening.sh

# Display setup completion message
log "✅ Server setup completed successfully!"
echo
echo "📋 Next steps:"
echo "1. Configure your domain name in Nginx configuration"
echo "2. Obtain SSL certificate: certbot --nginx -d your-domain.com"
echo "3. Update environment variables in $APP_DIR/.env.production"
echo "4. Deploy your application: cd $APP_DIR/app && ./scripts/deploy.sh"
echo "5. Configure monitoring and alerting"
echo
echo "🔑 Important passwords and secrets (save these securely):"
echo "Database Password: $DB_PASSWORD"
echo "Redis Password: $REDIS_PASSWORD"
echo "JWT Secret: $JWT_SECRET"
echo "JWT Refresh Secret: $JWT_REFRESH_SECRET"
echo
echo "📁 Important directories:"
echo "Application: $APP_DIR/app"
echo "Logs: $APP_DIR/logs"
echo "Backups: $APP_DIR/backups"
echo "Scripts: $APP_DIR/scripts"
echo
echo "🔧 Useful commands:"
echo "View logs: docker-compose logs -f"
echo "Restart services: docker-compose restart"
echo "Backup: $APP_DIR/scripts/backup.sh"
echo "Health check: $APP_DIR/scripts/health-check.sh"
echo "Security hardening: $APP_DIR/scripts/security-hardening.sh"
echo
echo "⚠️  Remember to:"
echo "- Configure proper monitoring and alerting"
echo "- Set up off-site backups"
echo "- Implement proper access controls"
echo "- Regular security updates"

# Create security summary file
cat > $APP_DIR/setup-summary.txt << EOF
Gatekeeper RPA Setup Summary
Generated: $(date)

Server Configuration:
- User: $SERVER_USER
- App Directory: $APP_DIR
- Database: $DB_NAME
- Database User: $DB_USER

Credentials:
- Database Password: $DB_PASSWORD
- Redis Password: $REDIS_PASSWORD
- JWT Secret: $JWT_SECRET
- JWT Refresh Secret: $JWT_REFRESH_SECRET

Services Status:
- Docker: Installed and running
- PostgreSQL: Installed and configured
- Redis: Installed and configured
- Nginx: Installed and configured
- Fail2Ban: Installed and configured
- Firewall: Configured

Next Steps:
1. Configure domain name
2. Obtain SSL certificate
3. Deploy application
4. Set up monitoring
5. Configure backups

Important Files:
- Environment: $APP_DIR/.env.production
- Nginx Config: /etc/nginx/sites-available/gatekeeper
- Backup Script: $APP_DIR/scripts/backup.sh
- Health Check: $APP_DIR/scripts/health-check.sh
- Deploy Script: $APP_DIR/scripts/deploy.sh

EOF

chown $SERVER_USER:$SERVER_USER $APP_DIR/setup-summary.txt

log "Setup complete! Please review the setup summary at $APP_DIR/setup-summary.txt"