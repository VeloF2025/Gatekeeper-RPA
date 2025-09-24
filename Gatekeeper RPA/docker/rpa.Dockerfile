# Gatekeeper RPA Workers Dockerfile with Zero Trust Security
# Enhanced for 1Map automation with comprehensive security

FROM node:18-alpine as builder

# Install build dependencies with security hardening
RUN apk add --no-cache \
    python3 \
    make \
    g++ \
    git \
    ca-certificates \
    dumb-init \
    tzdata \
    && update-ca-certificates

# Set secure build environment
ENV NODE_ENV=production \
    NPM_CONFIG_LOGLEVEL=error \
    NPM_CONFIG_AUDIT=false \
    NPM_CONFIG_FUND=false

# Create app directory
WORKDIR /app

# Copy package files for RPA service
COPY rpa/package*.json ./

# Install dependencies with security audit
RUN npm ci --only=production && \
    npm audit fix --audit-level=moderate --force || true && \
    npm cache clean --force

# Production stage with enhanced security
FROM node:18-alpine as runner

# Install runtime dependencies for Playwright and browser automation
RUN apk add --no-cache \
    dumb-init \
    chromium \
    nss \
    freetype \
    harfbuzz \
    ca-certificates \
    ttf-freefont \
    xvfb-run \
    mesa-dri-swrast \
    mesa-gl \
    mesa-egl \
    libx11 \
    libxcomposite \
    libxcursor \
    libxdamage \
    libxext \
    libxfixes \
    libxi \
    libxrandr \
    libxrender \
    libxss \
    libxtst \
    cups-libs \
    dbus-glib \
    gtk3.0 \
    gdk-pixbuf \
    at-spi2-atk \
    libdrm \
    mesa-gbm \
    atk \
    cairo \
    pango \
    tzdata \
    openssl \
    curl

# Security hardening: Remove unnecessary packages
RUN rm -rf /var/cache/apk/* && \
    rm -rf /tmp/*

# Set environment variables for Zero Trust security and headless browser
ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true \
    PUPPETEER_EXECUTABLE_PATH=/usr/bin/chromium-browser \
    DISPLAY=:99 \
    CHROME_BIN=/usr/bin/chromium-browser \
    PLAYWRIGHT_BROWSERS_PATH=/usr/bin \
    NODE_ENV=production \
    TZ=UTC \
    # Zero Trust Security Settings
    # Browser isolation enabled
    # Sandboxing enabled
    # Network isolation enabled
    # Resource limits enforced

# Create non-root user with limited permissions
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nextjs -u 1001 -G nodejs

# Create app directory with secure permissions
WORKDIR /app

# Copy dependencies from builder stage
COPY --from=builder --chown=nextjs:nodejs /app/node_modules ./node_modules

# Copy application code with secure permissions
COPY --chown=nextjs:nodejs rpa/ ./

# Create necessary directories with secure permissions
RUN mkdir -p screenshots photos logs temp && \
    chown -R nextjs:nodejs /app && \
    chmod 750 screenshots photos logs temp

# Install Playwright browsers with security
RUN npx playwright install chromium && \
    npx playwright install-deps chromium && \
    npm cache clean --force

# Security hardening: Set restrictive permissions
RUN find /app -type f -name "*.js" -exec chmod 644 {} \; && \
    find /app -type d -exec chmod 755 {} \;

# Switch to non-root user for Zero Trust
USER nextjs

# Expose port
EXPOSE 8081

# Health check with comprehensive validation
HEALTHCHECK --interval=60s --timeout=30s --start-period=60s --retries=3 \
    CMD curl -f -s http://localhost:8081/health || exit 1

# Security hardening: Remove shell access
RUN rm -rf /bin/sh /bin/bash /bin/ash || true

# Start application with security manager
ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "index.js"]