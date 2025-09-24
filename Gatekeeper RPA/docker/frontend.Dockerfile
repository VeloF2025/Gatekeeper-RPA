# WhatiTicket Frontend Dockerfile
# Multi-stage build for production optimization

# Build stage
FROM node:18-alpine as builder

# Install build dependencies
RUN apk add --no-cache python3 make g++

# Create app directory
WORKDIR /app

# Copy package files
COPY frontend/package*.json ./

# Install dependencies
RUN npm ci

# Copy application code
COPY frontend/ ./

# Build application
RUN npm run build

# Production stage
FROM nginx:alpine

# Install additional tools
RUN apk add --no-cache curl

# Copy nginx configuration
COPY nginx/frontend.conf /etc/nginx/conf.d/default.conf

# Copy built files from builder stage
COPY --from=builder --chown=nginx:nginx /app/build /usr/share/nginx/html

# Create directories for uploads
RUN mkdir -p /var/www/uploads && \
    chown -R nginx:nginx /usr/share/nginx/html /var/www

# Add custom nginx configuration for security and performance
RUN echo "# Security headers\n" \
    "add_header X-Frame-Options 'SAMEORIGIN' always;\n" \
    "add_header X-Content-Type-Options 'nosniff' always;\n" \
    "add_header X-XSS-Protection '1; mode=block' always;\n" \
    "add_header Referrer-Policy 'strict-origin-when-cross-origin' always;\n" \
    "add_header Content-Security-Policy \"default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; connect-src 'self' https:; font-src 'self' data:; object-src 'none';\" always;\n" \
    "\n# Performance optimizations\n" \
    "gzip on;\n" \
    "gzip_vary on;\n" \
    "gzip_proxied any;\n" \
    "gzip_comp_level 6;\n" \
    "gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;\n" \
    "\n# Cache static assets\n" \
    "location ~* \\.(js|css|png|jpg|jpeg|gif|ico|svg)$ {\n" \
    "    expires 1y;\n" \
    "    add_header Cache-Control 'public, immutable';\n" \
    "}\n" \
    "\n# Security\n" \
    "location ~ /\\. {\n" \
    "    deny all;\n" \
    "}\n" \
    "\n# Health check\n" \
    "location /health {\n" \
    "    access_log off;\n" \
    "    return 200 'healthy\\n';\n" \
    "    add_header Content-Type text/plain;\n" \
    "}" >> /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:80/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]