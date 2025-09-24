#!/bin/bash

# WhatiTicket + Gatekeeper RPA Docker Setup Script
# This script sets up the complete Docker environment

set -e

echo "🚀 Setting up WhatiTicket + Gatekeeper RPA Docker Environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi

    print_status "Docker and Docker Compose are installed"
}

# Create necessary directories
create_directories() {
    print_status "Creating necessary directories..."

    mkdir -p ../logs/nginx
    mkdir -p ../logs/backend
    mkdir -p ../logs/rpa
    mkdir -p ../logs/queue
    mkdir -p ../public/uploads
    mkdir -p /opt/whats-ticket/screenshots
    mkdir -p /opt/whats-ticket/photos
    mkdir -p /opt/whats-ticket/logs
    mkdir -p /opt/whats-ticket/backups
    mkdir -p nginx/ssl
    mkdir -p monitoring/grafana/dashboards
    mkdir -p monitoring/grafana/datasources

    # Set proper permissions
    chmod 755 ../logs
    chmod 755 /opt/whats-ticket
    chmod 755 ../public/uploads

    print_status "Directories created successfully"
}

# Generate environment file
generate_env_file() {
    if [ ! -f .env ]; then
        print_status "Generating environment file..."
        cp .env.example .env
        print_warning "Please edit .env file with your configuration"
        print_warning "Run: nano .env"
    else
        print_status "Environment file already exists"
    fi
}

# Generate secure passwords
generate_passwords() {
    print_status "Generating secure passwords..."

    # Generate JWT secrets
    JWT_SECRET=$(openssl rand -base64 32)
    JWT_REFRESH_SECRET=$(openssl rand -base64 32)

    # Generate database passwords
    DB_PASSWORD=$(openssl rand -base64 16)
    MYSQL_ROOT_PASSWORD=$(openssl rand -base64 16)

    # Generate Grafana password
    GRAFANA_PASSWORD=$(openssl rand -base64 12)

    # Generate Redis password
    REDIS_PASSWORD=$(openssl rand -base64 16)

    print_status "Generated secure passwords"

    # Update .env file with generated passwords
    if [ -f .env ]; then
        sed -i "s/your_super_secure_jwt_secret_key_minimum_32_characters_long/$JWT_SECRET/g" .env
        sed -i "s/your_super_secure_refresh_secret_key_minimum_32_characters_long/$JWT_REFRESH_SECRET/g" .env
        sed -i "s/your_secure_database_password/$DB_PASSWORD/g" .env
        sed -i "s/your_secure_root_password/$MYSQL_ROOT_PASSWORD/g" .env
        sed -i "s/your_grafana_admin_password/$GRAFANA_PASSWORD/g" .env
        sed -i "s/your_redis_password_optional/$REDIS_PASSWORD/g" .env
    fi
}

# Create database initialization script
create_database_init() {
    print_status "Creating database initialization script..."

    mkdir -p database

    cat > database/init.sql << 'EOF'
-- Database initialization script for WhatiTicket + Gatekeeper RPA

-- Create main database if not exists
CREATE DATABASE IF NOT EXISTS whatsticket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create project-specific databases
CREATE DATABASE IF NOT EXISTS project1_audit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS project2_audit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use main database
USE whatsticket;

-- Create users and grant privileges
CREATE USER IF NOT EXISTS 'whatsuser'@'%' IDENTIFIED BY '${DB_PASS}';
GRANT ALL PRIVILEGES ON whatsticket.* TO 'whatsuser'@'%';
GRANT ALL PRIVILEGES ON project1_audit.* TO 'whatsuser'@'%';
GRANT ALL PRIVILEGES ON project2_audit.* TO 'whatsuser'@'%';
GRANT ALL PRIVILEGES ON project1_audit.* TO 'project1_user'@'%';
GRANT ALL PRIVILEGES ON project2_audit.* TO 'project2_user'@'%';

-- Create project-specific users
CREATE USER IF NOT EXISTS 'project1_user'@'%' IDENTIFIED BY '${PROJECT1_DB_PASSWORD}';
CREATE USER IF NOT EXISTS 'project2_user'@'%' IDENTIFIED BY '${PROJECT2_DB_PASSWORD}';

-- Flush privileges
FLUSH PRIVILEGES;

-- Show created databases
SHOW DATABASES;

EOF

    print_status "Database initialization script created"
}

# Create MySQL configuration
create_mysql_config() {
    print_status "Creating MySQL configuration..."

    mkdir -p database

    cat > database/my.cnf << 'EOF'
# MySQL configuration for WhatiTicket + Gatekeeper RPA

[mysqld]
# General settings
default-character-set = utf8mb4
collation-server = utf8mb4_unicode_ci
character-set-server = utf8mb4

# Performance settings
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
max_connections = 200
query_cache_type = 1
query_cache_size = 128M
query_cache_limit = 4M

# Storage settings
innodb_file_per_table = 1
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# Logging
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# Security
skip-name-resolve
bind-address = 0.0.0.0

[mysql]
default-character-set = utf8mb4

[client]
default-character-set = utf8mb4

EOF

    print_status "MySQL configuration created"
}

# Create Redis configuration
create_redis_config() {
    print_status "Creating Redis configuration..."

    mkdir -p redis

    cat > redis/redis.conf << 'EOF'
# Redis configuration for WhatiTicket + Gatekeeper RPA

# General settings
port 6379
bind 0.0.0.0
timeout 300
tcp-keepalive 60

# Memory management
maxmemory 512mb
maxmemory-policy allkeys-lru

# Persistence
appendonly yes
appendfsync everysec
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb

# Security
# requirepass your_redis_password

# Logging
loglevel notice
logfile ""

# Data directory
dir /data

EOF

    print_status "Redis configuration created"
}

# Create SSL setup script
create_ssl_setup() {
    print_status "Creating SSL setup script..."

    mkdir -p scripts

    cat > scripts/setup-ssl.sh << 'EOF'
#!/bin/bash

# SSL setup script for WhatiTicket + Gatekeeper RPA

set -e

echo "🔒 Setting up SSL certificates..."

# Check if domain is provided
if [ -z "$SSL_DOMAINS" ]; then
    echo "Error: SSL_DOMAINS environment variable not set"
    exit 1
fi

# Check if email is provided
if [ -z "$SSL_EMAIL" ]; then
    echo "Error: SSL_EMAIL environment variable not set"
    exit 1
fi

# Create certbot directory
mkdir -p /var/www/certbot

# Create nginx configuration for Let's Encrypt challenge
cat > nginx/ssl.conf << 'EOL'
server {
    listen 80;
    server_name $SSL_DOMAINS;

    # Let's Encrypt challenge
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}
EOL

# Start nginx with SSL configuration
docker-compose restart nginx

# Wait for nginx to start
sleep 5

# Request SSL certificate
docker run --rm \
    -v /var/www/certbot:/var/www/certbot \
    -v nginx/ssl:/etc/letsencrypt \
    certbot/certbot \
    certonly --webroot \
    --webroot-path=/var/www/certbot \
    --email $SSL_EMAIL \
    --agree-tos \
    --no-eff-email \
    -d $SSL_DOMAINS

echo "✅ SSL certificates obtained successfully"

EOF

    chmod +x scripts/setup-ssl.sh

    print_status "SSL setup script created"
}

# Create health check script
create_health_check() {
    print_status "Creating health check script..."

    mkdir -p scripts

    cat > scripts/health-check.sh << 'EOF'
#!/bin/bash

# Health check script for WhatiTicket + Gatekeeper RPA

echo "🔍 Performing health check..."

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

HEALTHY=0
UNHEALTHY=0

# Function to check service health
check_service() {
    local service_name=$1
    local health_url=$2

    if curl -f -s "$health_url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $service_name is healthy"
        HEALTHY=$((HEALTHY + 1))
    else
        echo -e "${RED}✗${NC} $service_name is unhealthy"
        UNHEALTHY=$((UNHEALTHY + 1))
    fi
}

# Check all services
echo "Checking services..."

# Backend
check_service "Backend" "http://localhost:8080/health"

# Frontend
check_service "Frontend" "http://localhost:3000/health"

# RPA Workers
check_service "RPA Workers" "http://localhost:8081/health"

# Queue Worker
check_service "Queue Worker" "http://localhost:8082/health"

# Database
if docker-compose exec mysql mysqladmin ping -h localhost -u ${DB_USER} -p${DB_PASS} > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} MySQL is healthy"
    HEALTHY=$((HEALTHY + 1))
else
    echo -e "${RED}✗${NC} MySQL is unhealthy"
    UNHEALTHY=$((UNHEALTHY + 1))
fi

# Redis
if docker-compose exec redis redis-cli ping > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Redis is healthy"
    HEALTHY=$((HEALTHY + 1))
else
    echo -e "${RED}✗${NC} Redis is unhealthy"
    UNHEALTHY=$((UNHEALTHY + 1))
fi

# Summary
echo ""
echo "Health Check Summary:"
echo "  Healthy services: $HEALTHY"
echo "  Unhealthy services: $UNHEALTHY"

if [ $UNHEALTHY -eq 0 ]; then
    echo -e "${GREEN}✅ All services are healthy${NC}"
    exit 0
else
    echo -e "${RED}❌ Some services are unhealthy${NC}"
    exit 1
fi

EOF

    chmod +x scripts/health-check.sh

    print_status "Health check script created"
}

# Create backup script
create_backup_script() {
    print_status "Creating backup script..."

    mkdir -p scripts

    cat > scripts/backup.sh << 'EOF'
#!/bin/bash

# Backup script for WhatiTicket + Gatekeeper RPA

set -e

echo "💾 Creating backup..."

BACKUP_DIR="/opt/whats-ticket/backups"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Backup main database
echo "Backing up main database..."
docker-compose exec mysql mysqldump -u ${DB_USER} -p${DB_PASS} whatsticket > "$BACKUP_DIR/whatsticket_$DATE.sql"

# Backup project databases
echo "Backing up project databases..."
docker-compose exec mysql mysqldump -u ${PROJECT1_DB_USER} -p${PROJECT1_DB_PASSWORD} project1_audit > "$BACKUP_DIR/project1_audit_$DATE.sql"
docker-compose exec mysql mysqldump -u ${PROJECT2_DB_USER} -p${PROJECT2_DB_PASSWORD} project2_audit > "$BACKUP_DIR/project2_audit_$DATE.sql"

# Backup application files
echo "Backing up application files..."
tar -czf "$BACKUP_DIR/application_$DATE.tar.gz" \
    -C /opt/whats-ticket \
    screenshots \
    photos \
    logs \
    --exclude="*.log" \
    --exclude="*.tmp"

# Compress SQL backups
echo "Compressing backups..."
gzip "$BACKUP_DIR/whatsticket_$DATE.sql"
gzip "$BACKUP_DIR/project1_audit_$DATE.sql"
gzip "$BACKUP_DIR/project2_audit_$DATE.sql"

# Clean old backups (keep 30 days)
echo "Cleaning old backups..."
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +30 -delete
find "$BACKUP_DIR" -name "*.tar.gz" -mtime +30 -delete

echo "✅ Backup completed successfully"
echo "Backup files:"
ls -la "$BACKUP_DIR"/*"$DATE"*

EOF

    chmod +x scripts/backup.sh

    print_status "Backup script created"
}

# Main setup process
main() {
    echo "🚀 Starting WhatiTicket + Gatekeeper RPA Docker Setup..."
    echo ""

    # Check prerequisites
    check_docker

    # Create directories and files
    create_directories
    generate_env_file
    generate_passwords
    create_database_init
    create_mysql_config
    create_redis_config
    create_ssl_setup
    create_health_check
    create_backup_script

    echo ""
    echo "✅ Setup completed successfully!"
    echo ""
    echo "Next steps:"
    echo "1. Edit the .env file with your configuration: nano .env"
    echo "2. Start the services: docker-compose up -d"
    echo "3. Initialize the database: docker-compose exec backend npx sequelize-cli db:migrate"
    echo "4. Run health check: ./scripts/health-check.sh"
    echo ""
    echo "Useful commands:"
    echo "  View logs: docker-compose logs -f"
    echo "  Stop services: docker-compose down"
    echo "  Restart services: docker-compose restart"
    echo "  Scale services: docker-compose up -d --scale rpa-workers=8"
    echo ""
}

# Run main function
main "$@"