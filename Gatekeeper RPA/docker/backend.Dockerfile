# WhatiTicket Backend Dockerfile
FROM node:18-alpine

# Install system dependencies
RUN apk add --no-cache \
    dumb-init \
    python3 \
    make \
    g++ \
    linux-headers \
    udev \
    ca-certificates \
    tzdata

# Set timezone
ENV TZ=UTC

# Create app directory
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nextjs -u 1001

# Copy package files
COPY backend/package*.json ./

# Install dependencies
RUN npm ci --only=production && \
    npm cache clean --force

# Copy application code
COPY backend/ ./

# Create necessary directories
RUN mkdir -p logs public/uploads && \
    chown -R nextjs:nodejs /app

# Switch to non-root user
USER nextjs

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Start application
ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "app.js"]