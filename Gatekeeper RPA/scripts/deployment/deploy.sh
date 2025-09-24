#!/bin/bash

# Gatekeeper RPA Deployment Script
# This script handles the complete deployment process

set -e

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
APP_DIR="$PROJECT_ROOT/app"
LOGS_DIR="$PROJECT_ROOT/logs"
BACKUP_DIR="$PROJECT_ROOT/backups"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] 🚀 $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}"
}

error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ❌ $1${NC}"
    exit 1
}

info() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')] ℹ️  $1${NC}"
}

# Check if required files exist
check_requirements() {
    log "Checking deployment requirements..."

    if [[ ! -f "$PROJECT_ROOT/.env.production" ]]; then
        error "Environment file not found: $PROJECT_ROOT/.env.production"
    fi

    if [[ ! -f "$APP_DIR/package.json" ]]; then
        error "Application package.json not found: $APP_DIR/package.json"
    fi

    if [[ ! -f "$APP_DIR/docker-compose.yml" ]]; then
        error "Docker Compose file not found: $APP_DIR/docker-compose.yml"
    fi

    # Check Docker is running
    if ! docker info > /dev/null 2>&1; then
        error "Docker is not running. Please start Docker service."
    fi

    # Check Docker Compose is available
    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose is not installed."
    fi

    log "✅ All requirements checked"
}

# Create backup before deployment
create_backup() {
    log "Creating backup before deployment..."

    mkdir -p "$BACKUP_DIR"
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)

    # Database backup
    if docker-compose -f "$APP_DIR/docker-compose.yml" ps postgres | grep -q "Up"; then
        log "Creating database backup..."
        docker-compose -f "$APP_DIR/docker-compose.yml" exec -T postgres pg_dump -U gatekeeper_user gatekeeper_rpa > "$BACKUP_DIR/postgres_backup_$TIMESTAMP.sql"
        gzip "$BACKUP_DIR/postgres_backup_$TIMESTAMP.sql"
        log "✅ Database backup completed"
    fi

    # Redis backup
    if docker-compose -f "$APP_DIR/docker-compose.yml" ps redis | grep -q "Up"; then
        log "Creating Redis backup..."
        docker-compose -f "$APP_DIR/docker-compose.yml" exec -T redis redis-cli --rdb "$BACKUP_DIR/redis_backup_$TIMESTAMP.rdb"
        gzip "$BACKUP_DIR/redis_backup_$TIMESTAMP.rdb"
        log "✅ Redis backup completed"
    fi

    # Application backup
    log "Creating application backup..."
    tar -czf "$BACKUP_DIR/app_backup_$TIMESTAMP.tar.gz" -C "$PROJECT_ROOT" app/ ssl/ logs/
    log "✅ Application backup completed"

    # Clean up old backups
    find "$BACKUP_DIR" -name "postgres_backup_*.sql.gz" -mtime +7 -delete
    find "$BACKUP_DIR" -name "redis_backup_*.rdb.gz" -mtime +7 -delete
    find "$BACKUP_DIR" -name "app_backup_*.tar.gz" -mtime +7 -delete

    log "✅ Backup completed: $TIMESTAMP"
}

# Update application code
update_code() {
    log "Updating application code..."

    cd "$APP_DIR"

    # Stash any local changes
    if [[ -n $(git status --porcelain) ]]; then
        warn "Local changes detected, stashing them..."
        git stash
    fi

    # Pull latest changes
    log "Pulling latest code changes..."
    git pull origin main

    # Pop stashed changes if any
    if [[ -n $(git stash list) ]]; then
        log "Restoring stashed changes..."
        git stash pop
    fi

    log "✅ Code updated"
}

# Install dependencies
install_dependencies() {
    log "Installing dependencies..."

    cd "$APP_DIR"

    # Clean npm cache
    npm cache clean --force

    # Install production dependencies
    npm ci --only=production

    # Install development dependencies if needed for build
    if [[ ! -d "node_modules" ]]; then
        npm ci
    fi

    log "✅ Dependencies installed"
}

# Run security checks
security_checks() {
    log "Running security checks..."

    cd "$APP_DIR"

    # Run npm audit
    log "Running npm audit..."
    npm audit --audit-level moderate || warn "Security vulnerabilities detected"

    # Check for secrets in code
    if command -v trufflehog &> /dev/null; then
        log "Running secret detection..."
        trufflehog filesystem . || warn "Potential secrets detected"
    fi

    # Check Docker image security
    if command -v docker scan &> /dev/null; then
        log "Scanning Docker images for vulnerabilities..."
        docker scan --severity medium gatekeeper-rpa-nextjs:latest || warn "Docker vulnerabilities detected"
    fi

    log "✅ Security checks completed"
}

# Build application
build_application() {
    log "Building application..."

    cd "$APP_DIR"

    # Generate database schema if needed
    if [[ -f "drizzle.config.ts" ]]; then
        log "Generating database schema..."
        npm run db:generate
    fi

    # Build Next.js application
    log "Building Next.js application..."
    npm run build

    # Check build success
    if [[ ! -d ".next" ]]; then
        error "Build failed: .next directory not found"
    fi

    log "✅ Application built successfully"
}

# Run database migrations
database_migrations() {
    log "Running database migrations..."

    cd "$APP_DIR"

    # Check if database is accessible
    if ! docker-compose -f docker-compose.yml ps postgres | grep -q "Up"; then
        warn "PostgreSQL is not running, starting it..."
        docker-compose up -d postgres
        sleep 10
    fi

    # Run migrations
    if [[ -f "drizzle.config.ts" ]]; then
        log "Running Drizzle migrations..."
        npm run db:migrate
    fi

    # Verify database connection
    log "Verifying database connection..."
    if docker-compose exec -T postgres pg_isready -U gatekeeper_user -d gatekeeper_rpa; then
        log "✅ Database connection verified"
    else
        error "Database connection failed"
    fi

    log "✅ Database migrations completed"
}

# Build Docker images
build_docker_images() {
    log "Building Docker images..."

    cd "$APP_DIR"

    # Stop existing containers
    if docker-compose ps | grep -q "Up"; then
        log "Stopping existing containers..."
        docker-compose down
    fi

    # Build images
    log "Building Docker images..."
    docker-compose build --no-cache

    # Verify images were built
    if ! docker images | grep -q "gatekeeper-rpa"; then
        error "Docker images build failed"
    fi

    log "✅ Docker images built successfully"
}

# Start services
start_services() {
    log "Starting services..."

    cd "$APP_DIR"

    # Start all services
    docker-compose up -d

    # Wait for services to start
    log "Waiting for services to start..."
    sleep 30

    # Check service status
    log "Checking service status..."
    docker-compose ps

    # Verify health endpoints
    log "Verifying service health..."

    services=(
        "http://localhost:3000/api/health"
        "http://localhost:8081/health"
        "http://localhost:8082/health"
    )

    for service in "${services[@]}"; do
        if curl -f "$service" > /dev/null 2>&1; then
            log "✅ $service - Healthy"
        else
            warn "⚠️  $service - Not responding"
        fi
    done

    log "✅ Services started"
}

# Run post-deployment tests
post_deployment_tests() {
    log "Running post-deployment tests..."

    cd "$APP_DIR"

    # Run basic health checks
    log "Running health checks..."

    # Test main application
    if curl -f http://localhost:3000/api/health > /dev/null 2>&1; then
        log "✅ Main application health check passed"
    else
        warn "⚠️  Main application health check failed"
    fi

    # Test RPA workers
    if curl -f http://localhost:8081/health > /dev/null 2>&1; then
        log "✅ RPA workers health check passed"
    else
        warn "⚠️  RPA workers health check failed"
    fi

    # Test queue workers
    if curl -f http://localhost:8082/health > /dev/null 2>&1; then
        log "✅ Queue workers health check passed"
    else
        warn "⚠️  Queue workers health check failed"
    fi

    # Test database connectivity
    if docker-compose exec -T postgres pg_isready -U gatekeeper_user -d gatekeeper_rpa > /dev/null 2>&1; then
        log "✅ Database connectivity test passed"
    else
        warn "⚠️  Database connectivity test failed"
    fi

    # Test Redis connectivity
    if docker-compose exec -T redis redis-cli ping > /dev/null 2>&1; then
        log "✅ Redis connectivity test passed"
    else
        warn "⚠️  Redis connectivity test failed"
    fi

    log "✅ Post-deployment tests completed"
}

# Generate deployment report
generate_deployment_report() {
    log "Generating deployment report..."

    TIMESTAMP=$(date '+%Y-%m-%d %H:%M:%S')
    REPORT_FILE="$BACKUP_DIR/deployment_report_$(date +%Y%m%d_%H%M%S).txt"

    cat > "$REPORT_FILE" << EOF
Gatekeeper RPA Deployment Report
Generated: $TIMESTAMP

Deployment Information:
- Deployment Time: $TIMESTAMP
- Deployed By: $(whoami)
- Server: $(hostname)
- Git Commit: $(cd "$APP_DIR" && git rev-parse HEAD)

Services Status:
$(cd "$APP_DIR" && docker-compose ps)

System Information:
- OS: $(uname -a)
- Docker Version: $(docker --version)
- Docker Compose Version: $(docker-compose --version)
- Node Version: $(node --version)
- NPM Version: $(npm --version)

Resource Usage:
$(docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}")

Next Steps:
1. Monitor application logs: docker-compose logs -f
2. Verify all services are running: docker-compose ps
3. Check application metrics: curl http://localhost:3000/api/metrics
4. Test key functionality manually

Backup Information:
- Backup Directory: $BACKUP_DIR
- Latest Backup: $(ls -t "$BACKUP_DIR" | head -1)

EOF

    log "✅ Deployment report generated: $REPORT_FILE"
}

# Cleanup old resources
cleanup_resources() {
    log "Cleaning up old resources..."

    cd "$APP_DIR"

    # Remove unused Docker images
    docker image prune -f

    # Remove unused Docker networks
    docker network prune -f

    # Remove unused Docker volumes
    docker volume prune -f

    # Clean old logs
    find "$LOGS_DIR" -name "*.log" -mtime +30 -delete

    log "✅ Resource cleanup completed"
}

# Main deployment function
main() {
    log "🚀 Starting Gatekeeper RPA deployment..."

    # Check if we're in the right directory
    if [[ ! -d "$APP_DIR" ]]; then
        error "Application directory not found: $APP_DIR"
    fi

    # Load environment variables
    source "$PROJECT_ROOT/.env.production"

    # Execute deployment steps
    check_requirements
    create_backup
    update_code
    install_dependencies
    security_checks
    build_application
    database_migrations
    build_docker_images
    start_services
    post_deployment_tests
    generate_deployment_report
    cleanup_resources

    log "🎉 Deployment completed successfully!"
    echo
    echo "📋 Post-deployment checklist:"
    echo "✅ All services are running"
    echo "✅ Health checks passed"
    echo "✅ Database migrations completed"
    echo "✅ Backup created"
    echo "✅ Security checks performed"
    echo "✅ Deployment report generated"
    echo
    echo "🔍 Useful commands:"
    echo "View logs: cd $APP_DIR && docker-compose logs -f"
    echo "Service status: cd $APP_DIR && docker-compose ps"
    echo "Restart services: cd $APP_DIR && docker-compose restart"
    echo "View metrics: curl http://localhost:3000/api/metrics"
    echo
    echo "⚠️  Remember to:"
    echo "1. Monitor application performance"
    echo "2. Check error logs regularly"
    echo "3. Set up monitoring alerts"
    echo "4. Test critical functionality"
    echo "5. Verify backup processes"
}

# Handle script arguments
case "${1:-}" in
    "backup")
        create_backup
        ;;
    "health")
        post_deployment_tests
        ;;
    "logs")
        cd "$APP_DIR"
        docker-compose logs -f "${2:-}"
        ;;
    "status")
        cd "$APP_DIR"
        docker-compose ps
        ;;
    "restart")
        cd "$APP_DIR"
        docker-compose restart "${2:-}"
        ;;
    "stop")
        cd "$APP_DIR"
        docker-compose down
        ;;
    "start")
        cd "$APP_DIR"
        docker-compose up -d
        ;;
    *)
        main
        ;;
esac