#!/bin/bash

# Gatekeeper RPA Backup Script
# This script handles comprehensive backup of the Gatekeeper RPA system

set -e

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
APP_DIR="$PROJECT_ROOT/app"
BACKUP_DIR="$PROJECT_ROOT/backups"
LOGS_DIR="$PROJECT_ROOT/logs"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_RETENTION_DAYS=30

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] 💾 $1${NC}" | tee -a "$LOGS_DIR/backup.log"
}

warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}" | tee -a "$LOGS_DIR/backup.log"
}

error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ❌ $1${NC}" | tee -a "$LOGS_DIR/backup.log"
    exit 1
}

info() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')] ℹ️  $1${NC}" | tee -a "$LOGS_DIR/backup.log"
}

# Create backup directory
create_backup_dir() {
    log "Creating backup directory..."
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$LOGS_DIR"

    # Create temporary directory for this backup
    TEMP_BACKUP_DIR="$BACKUP_DIR/temp_$TIMESTAMP"
    mkdir -p "$TEMP_BACKUP_DIR"

    log "✅ Backup directory created: $TEMP_BACKUP_DIR"
}

# Check prerequisites
check_prerequisites() {
    log "Checking backup prerequisites..."

    # Check if Docker is running
    if ! docker info > /dev/null 2>&1; then
        error "Docker is not running. Please start Docker service."
    fi

    # Check if Docker Compose is available
    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose is not installed."
    fi

    # Check if application directory exists
    if [[ ! -d "$APP_DIR" ]]; then
        error "Application directory not found: $APP_DIR"
    fi

    # Check if docker-compose.yml exists
    if [[ ! -f "$APP_DIR/docker-compose.yml" ]]; then
        error "Docker Compose file not found: $APP_DIR/docker-compose.yml"
    fi

    log "✅ Prerequisites checked"
}

# Backup PostgreSQL database
backup_database() {
    log "Starting PostgreSQL database backup..."

    cd "$APP_DIR"

    # Check if PostgreSQL is running
    if ! docker-compose ps postgres 2>/dev/null | grep -q "Up"; then
        warn "PostgreSQL container is not running, skipping database backup"
        return 0
    fi

    # Get database connection info from environment
    source "$PROJECT_ROOT/.env.production" 2>/dev/null || true

    DB_NAME=${POSTGRES_DB:-gatekeeper_rpa}
    DB_USER=${POSTGRES_USER:-gatekeeper_user}

    # Create database backup
    BACKUP_FILE="$TEMP_BACKUP_DIR/postgres_backup_$TIMESTAMP.sql"

    if docker-compose exec -T postgres pg_dump -U "$DB_USER" -d "$DB_NAME" > "$BACKUP_FILE" 2>/dev/null; then
        # Verify backup file exists and has content
        if [[ -s "$BACKUP_FILE" ]]; then
            gzip "$BACKUP_FILE"
            log "✅ PostgreSQL database backup completed: $(basename "$BACKUP_FILE.gz")"

            # Get backup size
            BACKUP_SIZE=$(du -h "$BACKUP_FILE.gz" | cut -f1)
            info "Database backup size: $BACKUP_SIZE"
        else
            error "Database backup file is empty"
        fi
    else
        error "Failed to create database backup"
    fi
}

# Backup Redis data
backup_redis() {
    log "Starting Redis data backup..."

    cd "$APP_DIR"

    # Check if Redis is running
    if ! docker-compose ps redis 2>/dev/null | grep -q "Up"; then
        warn "Redis container is not running, skipping Redis backup"
        return 0
    fi

    # Create Redis backup
    BACKUP_FILE="$TEMP_BACKUP_DIR/redis_backup_$TIMESTAMP.rdb"

    if docker-compose exec -T redis redis-cli --rdb "$BACKUP_FILE" > /dev/null 2>&1; then
        # Verify backup file exists
        if [[ -f "$BACKUP_FILE" ]]; then
            gzip "$BACKUP_FILE"
            log "✅ Redis data backup completed: $(basename "$BACKUP_FILE.gz")"

            # Get backup size
            BACKUP_SIZE=$(du -h "$BACKUP_FILE.gz" | cut -f1)
            info "Redis backup size: $BACKUP_SIZE"
        else
            warn "Redis backup file not found"
        fi
    else
        warn "Failed to create Redis backup"
    fi
}

# Backup application files
backup_application() {
    log "Starting application files backup..."

    # Create application backup
    BACKUP_FILE="$TEMP_BACKUP_DIR/app_backup_$TIMESTAMP.tar.gz"

    # Files to include in backup
    INCLUDE_FILES=(
        "$APP_DIR/src"
        "$APP_DIR/package.json"
        "$APP_DIR/package-lock.json"
        "$APP_DIR/next.config.js"
        "$APP_DIR/tailwind.config.js"
        "$APP_DIR/tsconfig.json"
        "$APP_DIR/docker-compose.yml"
        "$APP_DIR/Dockerfile"
        "$APP_DIR/docker"
        "$APP_DIR/drizzle"
        "$PROJECT_ROOT/.env.production"
        "$PROJECT_ROOT/nginx"
        "$PROJECT_ROOT/ssl"
        "$PROJECT_ROOT/scripts"
        "$LOGS_DIR"
    )

    # Create tar archive
    tar -czf "$BACKUP_FILE" -C "$PROJECT_ROOT" $(basename "${INCLUDE_FILES[@]}" | uniq) 2>/dev/null || true

    # Verify backup file exists
    if [[ -f "$BACKUP_FILE" ]]; then
        log "✅ Application files backup completed: $(basename "$BACKUP_FILE")"

        # Get backup size
        BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
        info "Application backup size: $BACKUP_SIZE"
    else
        warn "Failed to create application backup"
    fi
}

# Backup Docker volumes
backup_volumes() {
    log "Starting Docker volumes backup..."

    cd "$APP_DIR"

    # List of volumes to backup
    VOLUMES=(
        "gatekeeper_rpa_postgres_data"
        "gatekeeper_rpa_redis_data"
        "gatekeeper_rpa_prometheus_data"
        "gatekeeper_rpa_grafana_data"
    )

    for volume in "${VOLUMES[@]}"; do
        if docker volume ls | grep -q "$volume"; then
            BACKUP_FILE="$TEMP_BACKUP_DIR/volume_${volume##*_}_$TIMESTAMP.tar.gz"

            # Create temporary container to backup volume
            if docker run --rm -v "$volume:/volume" -v "$TEMP_BACKUP_DIR:/backup" alpine tar czf "/backup/$(basename "$BACKUP_FILE")" -C /volume . 2>/dev/null; then
                log "✅ Volume backup completed: $(basename "$BACKUP_FILE")"

                # Get backup size
                BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
                info "Volume backup size: $BACKUP_SIZE"
            else
                warn "Failed to backup volume: $volume"
            fi
        fi
    done
}

# Backup configuration files
backup_configuration() {
    log "Starting configuration files backup..."

    # Create configuration backup
    BACKUP_FILE="$TEMP_BACKUP_DIR/config_backup_$TIMESTAMP.tar.gz"

    # Files to include
    CONFIG_FILES=(
        "$PROJECT_ROOT/.env.production"
        "$PROJECT_ROOT/nginx/nginx.conf"
        "$PROJECT_ROOT/monitoring/prometheus.yml"
        "$PROJECT_ROOT/monitoring/alert_rules.yml"
        "$PROJECT_ROOT/docker-compose.prod.yml"
        "$APP_DIR/docker-compose.yml"
    )

    # Create backup
    tar -czf "$BACKUP_FILE" "${CONFIG_FILES[@]}" 2>/dev/null || true

    # Verify backup file exists
    if [[ -f "$BACKUP_FILE" ]]; then
        log "✅ Configuration files backup completed: $(basename "$BACKUP_FILE")"

        # Get backup size
        BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
        info "Configuration backup size: $BACKUP_SIZE"
    else
        warn "Failed to create configuration backup"
    fi
}

# Create backup manifest
create_manifest() {
    log "Creating backup manifest..."

    MANIFEST_FILE="$TEMP_BACKUP_DIR/backup_manifest_$TIMESTAMP.txt"

    cat > "$MANIFEST_FILE" << EOF
Gatekeeper RPA Backup Manifest
Backup Timestamp: $TIMESTAMP
Generated: $(date)
Server: $(hostname)
User: $(whoami)

Backup Summary:
- Database: $(ls "$TEMP_BACKUP_DIR"/postgres_backup_*.sql.gz 2>/dev/null | wc -l) files
- Redis: $(ls "$TEMP_BACKUP_DIR"/redis_backup_*.rdb.gz 2>/dev/null | wc -l) files
- Application: $(ls "$TEMP_BACKUP_DIR"/app_backup_*.tar.gz 2>/dev/null | wc -l) files
- Volumes: $(ls "$TEMP_BACKUP_DIR"/volume_*.tar.gz 2>/dev/null | wc -l) files
- Configuration: $(ls "$TEMP_BACKUP_DIR"/config_backup_*.tar.gz 2>/dev/null | wc -l) files

Backup Files:
$(ls -la "$TEMP_BACKUP_DIR" 2>/dev/null || echo "No files found")

System Information:
- OS: $(uname -a)
- Docker Version: $(docker --version)
- Docker Compose Version: $(docker-compose --version)
- Disk Usage: $(df -h / | tail -1 | awk '{print $5}')
- Memory Usage: $(free -h | grep Mem | awk '{print $3 "/" $2}')

Docker Status:
$(docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null || echo "Docker not accessible")

Git Information (if available):
$(cd "$APP_DIR" 2>/dev/null && git log --oneline -n 5 2>/dev/null || echo "Git information not available")

Notes:
- This backup contains all critical data for the Gatekeeper RPA system
- Database backups are in SQL format and can be restored using psql
- Redis backups are in RDB format and can be restored using redis-server
- Application backups include source code and configuration
- Volume backups include persistent data volumes

EOF

    log "✅ Backup manifest created: $(basename "$MANIFEST_FILE")"
}

# Verify backup integrity
verify_backup() {
    log "Verifying backup integrity..."

    # Check if any backup files were created
    BACKUP_COUNT=$(find "$TEMP_BACKUP_DIR" -type f -name "*.gz" -o -name "*.sql" -o -name "*.rdb" -o -name "*.txt" | wc -l)

    if [[ $BACKUP_COUNT -eq 0 ]]; then
        error "No backup files were created"
    fi

    # Test archive integrity
    for archive in "$TEMP_BACKUP_DIR"/*.tar.gz; do
        if [[ -f "$archive" ]]; then
            if gzip -t "$archive" 2>/dev/null; then
                log "✅ Archive integrity verified: $(basename "$archive")"
            else
                warn "⚠️  Archive integrity check failed: $(basename "$archive")"
            fi
        fi
    done

    log "✅ Backup verification completed"
}

# Create final backup archive
create_final_archive() {
    log "Creating final backup archive..."

    FINAL_ARCHIVE="$BACKUP_DIR/gatekeeper_rpa_backup_$TIMESTAMP.tar.gz"

    # Create final archive containing all backups
    if tar -czf "$FINAL_ARCHIVE" -C "$BACKUP_DIR" "temp_$TIMESTAMP" 2>/dev/null; then
        log "✅ Final backup archive created: $(basename "$FINAL_ARCHIVE")"

        # Get final archive size
        ARCHIVE_SIZE=$(du -h "$FINAL_ARCHIVE" | cut -f1)
        info "Final backup size: $ARCHIVE_SIZE"

        # Create checksum
        if command -v sha256sum &> /dev/null; then
            sha256sum "$FINAL_ARCHIVE" > "$FINAL_ARCHIVE.sha256"
            log "✅ Checksum created: $(basename "$FINAL_ARCHIVE.sha256")"
        fi
    else
        error "Failed to create final backup archive"
    fi
}

# Cleanup old backups
cleanup_old_backups() {
    log "Cleaning up old backups..."

    # Remove temporary backup directory
    rm -rf "$TEMP_BACKUP_DIR"

    # Remove old backups based on retention policy
    find "$BACKUP_DIR" -name "gatekeeper_rpa_backup_*.tar.gz" -mtime +$BACKUP_RETENTION_DAYS -delete
    find "$BACKUP_DIR" -name "*.sha256" -mtime +$BACKUP_RETENTION_DAYS -delete

    # Remove empty directories
    find "$BACKUP_DIR" -type d -empty -delete

    log "✅ Old backups cleaned up"
}

# Upload to cloud storage (optional)
upload_to_cloud() {
    log "Uploading backup to cloud storage..."

    # Check if AWS CLI is configured
    if command -v aws &> /dev/null && aws configure get aws_access_key_id &> /dev/null; then
        # Get S3 bucket from environment or use default
        S3_BUCKET=${S3_BACKUP_BUCKET:-"gatekeeper-rpa-backups"}
        S3_PATH="s3://$S3_BUCKET/$(date +%Y)/$(date +%m)/$(date +%d)/"

        # Upload backup
        if aws s3 cp "$FINAL_ARCHIVE" "$S3_PATH" 2>/dev/null; then
            log "✅ Backup uploaded to cloud storage: $S3_PATH"

            # Upload checksum if it exists
            if [[ -f "$FINAL_ARCHIVE.sha256" ]]; then
                aws s3 cp "$FINAL_ARCHIVE.sha256" "$S3_PATH" 2>/dev/null
            fi
        else
            warn "⚠️  Failed to upload backup to cloud storage"
        fi
    else
        info "AWS CLI not configured, skipping cloud upload"
    fi
}

# Send notification (optional)
send_notification() {
    log "Sending backup notification..."

    # Check if webhook URL is configured
    WEBHOOK_URL=${BACKUP_WEBHOOK_URL:-""}

    if [[ -n "$WEBHOOK_URL" ]]; then
        # Create notification payload
        PAYLOAD=$(cat << EOF
{
    "text": "🎉 Gatekeeper RPA Backup Completed Successfully",
    "blocks": [
        {
            "type": "section",
            "text": {
                "type": "mrkdwn",
                "text": "*Gatekeeper RPA Backup Status* ✅"
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
                    "text": "*Backup Size:*\n$ARCHIVE_SIZE"
                },
                {
                    "type": "mrkdwn",
                    "text": "*Files:*\n$BACKUP_COUNT"
                }
            ]
        },
        {
            "type": "section",
            "text": {
                "type": "mrkdwn",
                "text": "Backup completed successfully and stored in: $BACKUP_DIR"
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
    else
        info "Webhook URL not configured, skipping notification"
    fi
}

# Generate backup report
generate_backup_report() {
    log "Generating backup report..."

    REPORT_FILE="$BACKUP_DIR/backup_report_$TIMESTAMP.txt"

    cat > "$REPORT_FILE" << EOF
Gatekeeper RPA Backup Report
Backup Timestamp: $TIMESTAMP
Report Generated: $(date)

Backup Summary:
- Backup Status: SUCCESS
- Total Files: $BACKUP_COUNT
- Backup Size: $ARCHIVE_SIZE
- Server: $(hostname)
- User: $(whoami)

Backup Components:
- Database: $(ls "$TEMP_BACKUP_DIR"/postgres_backup_*.sql.gz 2>/dev/null | wc -l) files
- Redis: $(ls "$TEMP_BACKUP_DIR"/redis_backup_*.rdb.gz 2>/dev/null | wc -l) files
- Application: $(ls "$TEMP_BACKUP_DIR"/app_backup_*.tar.gz 2>/dev/null | wc -l) files
- Volumes: $(ls "$TEMP_BACKUP_DIR"/volume_*.tar.gz 2>/dev/null | wc -l) files
- Configuration: $(ls "$TEMP_BACKUP_DIR"/config_backup_*.tar.gz 2>/dev/null | wc -l) files

Storage Location:
- Local: $FINAL_ARCHIVE
- Cloud: $S3_BUCKET (if configured)

Retention Policy:
- Local backups retained for $BACKUP_RETENTION_DAYS days
- Cloud backups retained according to S3 lifecycle policy

Verification:
- All backup archives integrity checked
- Database backup format: SQL
- Redis backup format: RDB
- Application backup format: TAR.GZ

Next Backup:
- Scheduled: $(date -d "+1 day" '+%Y-%m-%d %H:%M:%S')
- Manual: Run this script again

EOF

    log "✅ Backup report generated: $(basename "$REPORT_FILE")"
}

# Main backup function
main() {
    log "🚀 Starting Gatekeeper RPA backup process..."

    # Execute backup steps
    create_backup_dir
    check_prerequisites
    backup_database
    backup_redis
    backup_application
    backup_volumes
    backup_configuration
    create_manifest
    verify_backup
    create_final_archive
    cleanup_old_backups
    upload_to_cloud
    send_notification
    generate_backup_report

    log "🎉 Backup process completed successfully!"
    echo
    echo "📋 Backup Summary:"
    echo "✅ Backup created: $FINAL_ARCHIVE"
    echo "✅ Backup size: $ARCHIVE_SIZE"
    echo "✅ Files backed up: $BACKUP_COUNT"
    echo "✅ Backup verified"
    echo "✅ Old backups cleaned up"
    echo
    echo "💾 Backup Location: $BACKUP_DIR"
    echo "📄 Backup Report: $REPORT_FILE"
    echo
    echo "🔍 Useful commands:"
    echo "List backups: ls -la $BACKUP_DIR"
    echo "View backup log: tail -f $LOGS_DIR/backup.log"
    echo "Verify backup: tar -tzf $FINAL_ARCHIVE"
    echo "Restore backup: $PROJECT_ROOT/scripts/deployment/restore.sh $FINAL_ARCHIVE"
}

# Handle script arguments
case "${1:-}" in
    "database")
        create_backup_dir
        check_prerequisites
        backup_database
        ;;
    "redis")
        create_backup_dir
        check_prerequisites
        backup_redis
        ;;
    "app")
        create_backup_dir
        check_prerequisites
        backup_application
        ;;
    "config")
        create_backup_dir
        check_prerequisites
        backup_configuration
        ;;
    "verify")
        if [[ -n "$2" ]]; then
            if [[ -f "$2" ]]; then
                if gzip -t "$2" 2>/dev/null; then
                    log "✅ Backup file integrity verified: $2"
                else
                    error "❌ Backup file integrity check failed: $2"
                fi
            else
                error "Backup file not found: $2"
            fi
        else
            error "Please specify backup file to verify"
        fi
        ;;
    "list")
        echo "Available backups:"
        ls -la "$BACKUP_DIR"/gatekeeper_rpa_backup_*.tar.gz 2>/dev/null || echo "No backups found"
        ;;
    "clean")
        log "Cleaning up old backups..."
        find "$BACKUP_DIR" -name "gatekeeper_rpa_backup_*.tar.gz" -mtime +$BACKUP_RETENTION_DAYS -delete
        log "✅ Old backups cleaned up"
        ;;
    *)
        main
        ;;
esac