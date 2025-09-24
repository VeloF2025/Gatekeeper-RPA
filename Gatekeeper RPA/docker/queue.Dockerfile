# Queue Worker Dockerfile for Message Processing
FROM node:18-alpine

# Install system dependencies
RUN apk add --no-cache \
    dumb-init \
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
COPY queue/package*.json ./

# Install dependencies
RUN npm ci --only=production && \
    npm cache clean --force

# Copy application code
COPY queue/ ./

# Create logs directory
RUN mkdir -p logs && \
    chown -R nextjs:nodejs /app

# Switch to non-root user
USER nextjs

# Expose port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8082/health || exit 1

# Start application
ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "index.js"]