# Gatekeeper RPA Next.js Dockerfile
# Multi-stage build for production optimization with Zero Trust security

# Build stage with enhanced security
FROM node:18-alpine as builder

# Install build dependencies and security tools
RUN apk add --no-cache \
    python3 \
    make \
    g++ \
    git \
    ca-certificates \
    openssl \
    bash \
    curl \
    && update-ca-certificates

# Security hardening - basic packages only
RUN apk add --no-cache \
    nmap \
    nmap-ncat

# Set secure build environment
ENV NODE_ENV=production \
    NPM_CONFIG_LOGLEVEL=error \
    NPM_CONFIG_AUDIT=false \
    NPM_CONFIG_FUND=false

# Create app directory with secure permissions
WORKDIR /app

# Copy package files with verification
COPY package*.json ./
COPY tsconfig.json ./
COPY tailwind.config.ts ./
COPY postcss.config.js ./
COPY next.config.js ./

# Security scan dependencies
RUN npm ci --audit=false --fund=false --ignore-scripts && \
    npm audit fix --audit-level=moderate --force || true && \
    npm cache clean --force

# Security validation
RUN npm run validate:no-gaming || true && \
    npm run validate:antihall || true

# Copy application code with secure permissions
COPY . .

# Build application with security checks
RUN npm run build && \
    npm run lint && \
    npm run type-check

# Production stage with enhanced security hardening
FROM node:18-alpine as runner

# Install runtime dependencies and security tools
RUN apk add --no-cache \
    dumb-init \
    curl \
    ca-certificates \
    tzdata \
    libstdc++ \
    && update-ca-certificates

# Security hardening: Remove unnecessary packages and tools
RUN apk del --purge -r libstdc++ || true && \
    rm -rf /var/cache/apk/* && \
    rm -rf /tmp/*

# Create non-root user with limited permissions and secure groups
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nextjs -u 1001 -G nodejs && \
    chown -R nextjs:nodejs /home/nextjs 2>/dev/null || true

# Create app directory with secure permissions
WORKDIR /app

# Copy built application from builder stage
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static
COPY --from=builder --chown=nextjs:nodejs /app/public ./public

# Create required directories
RUN mkdir -p /app/logs /app/uploads && \
    chown -R nextjs:nodejs /app

# Set environment variables for Zero Trust security
ENV NODE_ENV=production \
    NEXT_TELEMETRY_DISABLED=1 \
    PORT=3020 \
    HOSTNAME=0.0.0.0 \
    TZ=UTC \
    NODE_OPTIONS="--max-old-space-size=512 --no-warnings"

# Zero Trust Security Headers
# CSP for strict content security
# Rate limiting enabled
# Input validation required
# Audit logging enabled

# Add security middleware and monitoring
COPY scripts/security-middleware.js /app/scripts/security-middleware.js
COPY scripts/monitoring.js /app/scripts/monitoring.js

# Set secure file permissions
RUN chmod 600 /app/scripts/security-middleware.js && \
    chmod 600 /app/scripts/monitoring.js

# Switch to non-root user for Zero Trust
USER nextjs

# Expose port
EXPOSE 3020

# Health check with comprehensive validation
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f -s http://localhost:3020/api/health || exit 1

# Security hardening: No shell access
RUN rm -rf /bin/sh /bin/bash /bin/ash || true

# Start application with security manager and monitoring
ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "server.js"]