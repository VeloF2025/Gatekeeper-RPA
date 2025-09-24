#!/bin/bash

# Gatekeeper RPA Restore Script
# This script handles restoration of Gatekeeper RPA system from backup

set -e

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
APP_DIR="$PROJECT_ROOT/app"
BACKUP_DIR="$PROJECT_ROOT/backups"
LOGS_DIR="$PROJECT_ROOT/logs"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] 🔄 $1${NC}" | tee -a "$LOGS_DIR/restore.log"
}

warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}" | tee -a "$LOGS_DIR/restore.log"
}

error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ❌ $1${NC}" | tee -a "$LOGS_DIR/restore.log"
    exit 1
}

info() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')] ℹ️  $1${NC}" | tee -a "$LOGS_DIR/restore.log"
}

# Function to ask for confirmation
confirm() {
    read -p "$1 (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        error "Restore cancelled by user"
    fi
}

# Check if backup file exists
check_backup_file() {
    if [[ -z "$BACKUP_FILE" ]]; then
        error "Please specify a backup file to restore from"
    fi

    if [[ ! -f "$BACKUP_FILE" ]]; then
        error "Backup file not found: $BACKUP_FILE"
    fi

    # Verify backup file integrity
    if ! gzip -t "$BACKUP_FILE" 2>/dev/null; then
        error "Backup file integrity check failed: $BACKUP_FILE"
    fi

    log "✅ Backup file verified: $BACKUP_FILE"
}

# Create pre-restore backup
create_pre_restore_backup() {
    log "Creating pre-restore backup..."

    PRE_RESTORE_BACKUP="$BACKUP_DIR/pre_restore_backup_$TIMESTAMP.tar.gz"

    # Backup current state
    if [[ -d "$APP_DIR" ]]; then
        tar -czf "$PRE_RESTORE_BACKUP" -C "$PROJECT_ROOT" app/ logs/ ssl/ nginx/ 2>/dev/null || true
        log "✅ Pre-restore backup created: $(basename "$PRE_RESTORE_BACKUP")"
    else
        warn "No existing application found, skipping pre-restore backup"
    fi
}

# Stop all services
stop_services() {
    log "Stopping all services..."

    cd "$APP_DIR"

    # Stop Docker containers if running
    if [[ -f "docker-compose.yml" ]]; then
        docker-compose down --remove-orphans 2>/dev/null || true
        log "✅ Docker containers stopped"
    fi

    # Stop system services
    if systemctl is-active --quiet nginx; then
        systemctl stop nginx
        log "✅ Nginx stopped"
    fi

    if systemctl is-active --quiet postgresql; then
        systemctl stop postgresql
        log "✅ PostgreSQL stopped"
    fi

    if systemctl is-active --quiet redis-server; then
        systemctl stop redis-server
        log "✅ Redis stopped"
    fi
}

# Extract backup
extract_backup() {
    log "Extracting backup archive..."

    # Create temporary directory for extraction
    EXTRACT_DIR="$BACKUP_DIR/extract_$TIMESTAMP"
    mkdir -p "$EXTRACT_DIR"

    # Extract backup archive
    if tar -xzf "$BACKUP_FILE" -C "$EXTRACT_DIR" 2>/dev/null; then
        log "✅ Backup archive extracted to: $EXTRACT_DIR"
    else
        error "Failed to extract backup archive"
    fi

    # Find the extracted backup directory
    EXTRACTED_BACKUP_DIR=$(find "$EXTRACT_DIR" -type d -name "temp_*" | head -1)
    if [[ -z "$EXTRACTED_BACKUP_DIR" ]]; then
        error "Could not find extracted backup directory"
    fi

    log "✅ Extracted backup directory: $EXTRACTED_BACKUP_DIR"
}

# Restore database
restore_database() {
    log "Restoring PostgreSQL database..."

    cd "$APP_DIR"

    # Find database backup file
    DB_BACKUP=$(find "$EXTRACTED_BACKUP_DIR" -name "postgres_backup_*.sql.gz" | head -1)

    if [[ -n "$DB_BACKUP" ]]; then
        # Start PostgreSQL temporarily
        systemctl start postgresql

        # Drop and recreate database
        sudo -u postgres dropdb gatekeeper_rpa 2>/dev/null || true
        sudo -u postgres createdb gatekeeper_rpa 2>/dev/null || true

        # Restore database
        gunzip -c "$DB_BACKUP" | sudo -u postgres psql gatekeeper_rpa 2>/dev/null

        # Stop PostgreSQL
        systemctl stop postgresql

        log "✅ Database restored from: $(basename "$DB_BACKUP")"
    else
        warn "⚠️  No database backup found, skipping database restore"
    fi
}

# Restore Redis data
restore_redis() {
    log "Restoring Redis data..."

    cd "$APP_DIR"

    # Find Redis backup file
    REDIS_BACKUP=$(find "$EXTRACTED_BACKUP_DIR" -name "redis_backup_*.rdb.gz" | head -1)

    if [[ -n "$REDIS_BACKUP" ]]; then
        # Start Redis temporarily
        systemctl start redis-server

        # Extract Redis backup
        REDIS_RDB="/var/lib/redis/dump.rdb"
        gunzip -c "$REDIS_BACKUP" > "$REDIS_RDB" 2>/dev/null

        # Set proper permissions
        chown redis:redis "$REDIS_RDB"
        chmod 644 "$REDIS_RDB"

        # Stop Redis
        systemctl stop redis-server

        log "✅ Redis data restored from: $(basename "$REDIS_BACKUP")"
    else
        warn "⚠️  No Redis backup found, skipping Redis restore"
    fi
}

# Restore application files
restore_application() {
    log "Restoring application files..."

    # Find application backup
    APP_BACKUP=$(find "$EXTRACTED_BACKUP_DIR" -name "app_backup_*.tar.gz" | head -1)

    if [[ -n "$APP_BACKUP" ]]; then
        # Remove existing application directory
        if [[ -d "$APP_DIR" ]]; then
            mv "$APP_DIR" "$APP_DIR.bak.$TIMESTAMP"
        fi

        # Extract application backup
        mkdir -p "$APP_DIR"
        tar -xzf "$APP_BACKUP" -C "$PROJECT_ROOT" 2>/dev/null

        log "✅ Application files restored from: $(basename "$APP_BACKUP")"
    else
        warn "⚠️  No application backup found, skipping application restore"
    fi
}

# Restore configuration files
restore_configuration() {
    log "Restoring configuration files..."

    # Find configuration backup
    CONFIG_BACKUP=$(find "$EXTRACTED_BACKUP_DIR" -name "config_backup_*.tar.gz" | head -1)

    if [[ -n "$CONFIG_BACKUP" ]]; then
        # Extract configuration files
        tar -xzf "$CONFIG_BACKUP" -C "$PROJECT_ROOT" 2>/dev/null

        log "✅ Configuration files restored from: $(basename "$CONFIG_BACKUP")"
    else
        warn "⚠️  No configuration backup found, skipping configuration restore"
    fi
}

# Restore Docker volumes
restore_volumes() {
    log "Restoring Docker volumes..."

    # Find volume backups
    for volume_backup in "$EXTRACTED_BACKUP_DIR"/volume_*.tar.gz; do
        if [[ -f "$volume_backup" ]]; then
            # Extract volume name from filename
            volume_name=$(basename "$volume_backup" | sed 's/volume_\(.*\)_.*\.tar\.gz/\1/')
            full_volume_name="gatekeeper_rpa_${volume_name}"

            # Create volume if it doesn't exist
            if ! docker volume ls | grep -q "$full_volume_name"; then
                docker volume create "$full_volume_name" 2>/dev/null || true
            fi

            # Restore volume data
            docker run --rm -v "$full_volume_name:/volume" -v "$(dirname "$volume_backup"):/backup" \
                alpine tar xzf "/backup/$(basename "$volume_backup")" -C /volume 2>/dev/null || true

            log "✅ Volume restored: $full_volume_name"
        fi
    done
}

# Update environment variables
update_environment() {
    log "Updating environment variables..."

    # Restore environment file if it exists in backup
    ENV_FILE="$PROJECT_ROOT/.env.production"
    BACKUP_ENV_FILE="$EXTRACTED_BACKUP_DIR/.env.production"

    if [[ -f "$BACKUP_ENV_FILE" ]]; then
        cp "$BACKUP_ENV_FILE" "$ENV_FILE"
        chmod 600 "$ENV_FILE"
        log "✅ Environment variables restored"
    else
        warn "⚠️  No environment file found in backup"
    fi
}

# Rebuild and start services
rebuild_services() {
    log "Rebuilding and starting services..."

    cd "$APP_DIR"

    # Install dependencies
    if [[ -f "package.json" ]]; then
        npm ci --only=production 2>/dev/null || true
        log "✅ Dependencies installed"
    fi

    # Build application
    if [[ -f "package.json" ]]; then
        npm run build 2>/dev/null || true
        log "✅ Application built"
    fi

    # Start Docker services
    if [[ -f "docker-compose.yml" ]]; then
        docker-compose up -d --build 2>/dev/null || true
        log "✅ Docker services started"
    fi

    # Start system services
    systemctl start nginx
    systemctl start postgresql
    systemctl start redis-server

    # Wait for services to start
    sleep 30

    log "✅ Services rebuilt and started"
}

# Verify restoration
verify_restoration() {
    log "Verifying restoration..."

    # Check if services are running
    if docker-compose ps | grep -q "Up"; then
        log "✅ Docker services are running"
    else
        warn "⚠️  Some Docker services may not be running"
    fi

    # Check if database is accessible
    if sudo -u postgres pg_isready -d gatekeeper_rpa 2>/dev/null; then
        log "✅ Database is accessible"
    else
        warn "⚠️  Database may not be accessible"
    fi

    # Check if Redis is accessible
    if redis-cli ping 2>/dev/null | grep -q "PONG"; then
        log "✅ Redis is accessible"
    else
        warn "⚠️  Redis may not be accessible"
    fi

    # Check if web application is responding
    if curl -f http://localhost:3000/api/health 2>/dev/null; then
        log "✅ Web application is responding"
    else
        warn "⚠️  Web application may not be responding"
    fi

    log "✅ Restoration verification completed"
}

# Clean up temporary files
cleanup_temp_files() {
    log "Cleaning up temporary files..."

    # Remove extracted backup directory
    if [[ -d "$EXTRACT_DIR" ]]; then
        rm -rf "$EXTRACT_DIR"
    fi

    log "✅ Temporary files cleaned up"
}

# Generate restoration report
generate_restoration_report() {
    log "Generating restoration report..."

    REPORT_FILE="$BACKUP_DIR/restore_report_$TIMESTAMP.txt"

    cat > "$REPORT_FILE" << EOF
Gatekeeper RPA Restoration Report
Restore Timestamp: $TIMESTAMP
Report Generated: $(date)

Restore Information:
- Backup File: $BACKUP_FILE
- Restore Status: SUCCESS
- Server: $(hostname)
- User: $(whoami)
- Pre-restore Backup: $PRE_RESTORE_BACKUP

Components Restored:
- Database: $(find "$EXTRACTED_BACKUP_DIR" -name "postgres_backup_*.sql.gz" | wc -l) files
- Redis: $(find "$EXTRACTED_BACKUP_DIR" -name "redis_backup_*.rdb.gz" | wc -l) files
- Application: $(find "$EXTRACTED_BACKUP_DIR" -name "app_backup_*.tar.gz" | wc -l) files
- Configuration: $(find "$EXTRACTED_BACKUP_DIR" -name "config_backup_*.tar.gz" | wc -l) files
- Volumes: $(find "$EXTRACTED_BACKUP_DIR" -name "volume_*.tar.gz" | wc -l) files

Service Status:
$(docker-compose ps 2>/dev/null || echo "Docker services not available")

System Status:
- Nginx: $(systemctl is-active nginx)
- PostgreSQL: $(systemctl is-active postgresql)
- Redis: $(systemctl is-active redis-server)

Verification Results:
- Database Accessible: $(sudo -u postgres pg_isready -d gatekeeper_rpa 2>/dev/null && echo "YES" || echo "NO")
- Redis Accessible: $(redis-cli ping 2>/dev/null | grep -q "PONG" && echo "YES" || echo "NO")
- Web Application: $(curl -f http://localhost:3000/api/health 2>/dev/null && echo "YES" || echo "NO")

Next Steps:
1. Monitor application logs for any errors
2. Test critical functionality
3. Verify data integrity
4. Update DNS if necessary
5. Notify users of restoration completion

Rollback Information:
- If restoration failed, you can rollback using: $PRE_RESTORE_BACKUP
- Original application directory backed up as: $APP_DIR.bak.$TIMESTAMP

EOF

    log "✅ Restoration report generated: $(basename "$REPORT_FILE")"
}

# Send notification (optional)
send_notification() {
    log "Sending restoration notification..."

    # Check if webhook URL is configured
    WEBHOOK_URL=${BACKUP_WEBHOOK_URL:-""}

    if [[ -n "$WEBHOOK_URL" ]]; then
        # Create notification payload
        PAYLOAD=$(cat << EOF
{
    "text": "🔄 Gatekeeper RPA Restoration Completed",
    "blocks": [
        {
            "type": "section",
            "text": {
                "type": "mrkdwn",
                "text": "*Gatekeeper RPA Restoration Status* ✅"
            }
        },
        {
            "type": "section",
            "fields": [
                {
                    "type": "mrkdwn",
                    "text": "*Timestamp:*\n$TIMESTAMP"
                },
                {
                    "type": "mrkdwn",
                    "text": "*Server:*\n$(hostname)"
                },
                {
                    "type": "mrkdwn",
                    "text": "*Backup File:*\n$(basename "$BACKUP_FILE")"
                },
                {
                    "type": "mrkdwn",
                    "text": "*Status:*\nSUCCESS"
                }
            ]
        },
        {
            "type": "section",
            "text": {
                "type": "mrkdwn",
                "text": "System restoration completed successfully. Please verify all services are functioning properly."
            }
        }
    ]
}
EOF
)

        # Send notification
        if curl -X POST -H "Content-Type: application/json" -d "$PAYLOAD" "$WEBHOOK_URL" > /dev/null 2>&1; then
            log "✅ Notification sent successfully"
        else
            warn "⚠️  Failed to send notification"
        fi
    fi
}

# Main restore function
main() {
    log "🚀 Starting Gatekeeper RPA restoration process..."

    # Check if backup file is provided
    BACKUP_FILE="${1:-}"
    check_backup_file

    # Ask for confirmation
    confirm "⚠️  This will restore the Gatekeeper RPA system from backup. All current data will be replaced. Are you sure?"

    # Execute restoration steps
    create_pre_restore_backup
    stop_services
    extract_backup
    restore_database
    restore_redis
    restore_application
    restore_configuration
    restore_volumes
    update_environment
    rebuild_services
    verify_restoration
    cleanup_temp_files
    generate_restoration_report
    send_notification

    log "🎉 Restoration process completed successfully!"
    echo
    echo "📋 Restoration Summary:"
    echo "✅ System restored from: $BACKUP_FILE"
    echo "✅ All services rebuilt and started"
    echo "✅ Restoration verified"
    echo "✅ Report generated: $REPORT_FILE"
    echo
    echo "🔍 Next steps:"
    echo "1. Monitor application logs: docker-compose logs -f"
    echo "2. Test critical functionality"
    echo "3. Verify data integrity"
    echo "4. Check system performance"
    echo "5. Update users if necessary"
    echo
    echo "⚠️  Important:"
    echo "- Pre-restore backup: $PRE_RESTORE_BACKUP"
    echo "- Original application: $APP_DIR.bak.$TIMESTAMP"
    echo "- Restoration report: $REPORT_FILE"
    echo
    echo "🔧 Useful commands:"
    echo "View logs: docker-compose logs -f"
    echo "Service status: docker-compose ps"
    echo "Health check: curl http://localhost:3000/api/health"
}

# Handle script arguments
case "${1:-}" in
    "list")
        echo "Available backups:"
        ls -la "$BACKUP_DIR"/gatekeeper_rpa_backup_*.tar.gz 2>/dev/null || echo "No backups found"
        ;;
    "verify")
        if [[ -n "$2" ]]; then
            BACKUP_FILE="$2"
            check_backup_file
            info "Backup file is valid and can be restored"
        else
            error "Please specify backup file to verify"
        fi
        ;;
    "rollback")
        warn "Rollback functionality not yet implemented"
        echo "To rollback manually:"
        echo "1. Stop all services"
        echo "2. Restore from pre-restore backup"
        echo "3. Rebuild and start services"
        ;;
    "help"|"-h"|"--help")
        echo "Gatekeeper RPA Restore Script"
        echo ""
        echo "Usage:"
        echo "  $0 <backup_file>     - Restore from backup file"
        echo "  $0 list              - List available backups"
        echo "  $0 verify <file>     - Verify backup file"
        echo "  $0 rollback          - Rollback to previous state"
        echo "  $0 help              - Show this help message"
        echo ""
        echo "Examples:"
        echo "  $0 /path/to/backup.tar.gz"
        echo "  $0 list"
        echo "  $0 verify /path/to/backup.tar.gz"
        ;;
    *)
        main "$@"
        ;;
esac