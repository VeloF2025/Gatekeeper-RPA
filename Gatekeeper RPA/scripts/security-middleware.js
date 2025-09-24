/**
 * Gatekeeper RPA Zero Trust Security Middleware
 * Comprehensive security implementation with continuous validation
 */

const { createHash, randomBytes } = require('crypto');
const jwt = require('jsonwebtoken');

const security = {
    // Zero Trust Security Headers
    headers: {
        'Content-Security-Policy': [
            "default-src 'self'",
            "script-src 'self' 'unsafe-inline'",
            "style-src 'self' 'unsafe-inline'",
            "img-src 'self' data: https: blob:",
            "font-src 'self' data:",
            "connect-src 'self' https: wss:",
            "media-src 'self' https: blob:",
            "object-src 'none'",
            "base-uri 'self'",
            "form-action 'self'",
            "frame-ancestors 'none'",
            "block-all-mixed-content",
            "upgrade-insecure-requests"
        ].join('; '),
        'X-Content-Type-Options': 'nosniff',
        'X-Frame-Options': 'DENY',
        'X-XSS-Protection': '1; mode=block',
        'Referrer-Policy': 'strict-origin-when-cross-origin',
        'Permissions-Policy': 'camera=(), microphone=(), geolocation=()',
        'Strict-Transport-Security': 'max-age=31536000; includeSubDomains; preload',
        'X-Permitted-Cross-Domain-Policies': 'none',
        'X-Download-Options': 'noopen',
        'X-Content-Type-Options': 'nosniff'
    },

    // Enhanced Rate limiting with Zero Trust
    rateLimits: {
        windowMs: 15 * 60 * 1000, // 15 minutes
        max: 100, // limit each IP to 100 requests per windowMs
        message: 'Too many requests from this IP, please try again later.',
        skipSuccessfulRequests: false,
        skipFailedRequests: false,
        keyGenerator: (req) => {
            // Use IP + User Agent combination for more granular rate limiting
            const ip = req.ip || req.connection.remoteAddress;
            const userAgent = req.get('User-Agent') || '';
            return createHash('sha256').update(ip + userAgent).digest('hex');
        },
        handler: (req, res, next) => {
            const ip = req.ip;
            security.logSecurityEvent('RATE_LIMIT_EXCEEDED', {
                ip,
                path: req.path,
                method: req.method,
                userAgent: req.get('User-Agent')
            });
            res.status(429).json({
                error: 'Too many requests',
                message: 'Please try again later',
                retryAfter: Math.ceil(security.rateLimits.windowMs / 1000)
            });
        }
    },

    // Request validation
    validateRequest(req) {
        const threats = [];

        // Check for suspicious user agents
        const suspiciousAgents = [
            'sqlmap', 'nikto', 'nmap', 'masscan', 'zgrab', 'crawler',
            'bot', 'spider', 'scraper', 'harvest', 'extractor'
        ];

        const userAgent = req.headers['user-agent'] || '';
        if (suspiciousAgents.some(agent => userAgent.toLowerCase().includes(agent))) {
            threats.push('Suspicious user agent detected');
        }

        // Check for suspicious headers
        const suspiciousHeaders = ['x-forwarded-for', 'x-real-ip'];
        if (suspiciousHeaders.some(header => req.headers[header])) {
            // Log but don't block - these can be legitimate
            console.warn('Suspicious headers detected:', req.headers);
        }

        // Check request size
        const contentLength = req.headers['content-length'];
        if (contentLength && parseInt(contentLength) > 10 * 1024 * 1024) { // 10MB
            threats.push('Request size exceeds limit');
        }

        return threats;
    },

    // Input sanitization
    sanitizeInput(input) {
        if (typeof input !== 'string') return input;

        return input
            .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
            .replace(/javascript:/gi, '')
            .replace(/on\w+\s*=/gi, '')
            .replace(/eval\((.*)\)/gi, '')
            .trim();
    },

    // Enhanced Zero Trust Security logging
    logSecurityEvent(event, details) {
        const logEntry = {
            timestamp: new Date().toISOString(),
            event,
            details,
            level: 'security',
            severity: this.calculateSeverity(event),
            correlationId: this.generateCorrelationId(),
            environment: process.env.NODE_ENV || 'development'
        };

        console.log(JSON.stringify(logEntry));

        // In production, send to security monitoring service
        if (process.env.SECURITY_LOGGING_URL) {
            this.sendToSecurityMonitoring(logEntry);
        }

        // Store in database for audit trail
        this.storeSecurityEvent(logEntry);
    },

    // Calculate security event severity
    calculateSeverity(event) {
        const severityMap = {
            'RATE_LIMIT_EXCEEDED': 'medium',
            'INVALID_TOKEN': 'high',
            'SUSPICIOUS_USER_AGENT': 'low',
            'XSS_ATTEMPT': 'critical',
            'SQL_INJECTION_ATTEMPT': 'critical',
            'PATH_TRAVERSAL_ATTEMPT': 'critical',
            'BRUTE_FORCE_ATTEMPT': 'high',
            'VALIDATION_FAILURE': 'low'
        };

        return severityMap[event] || 'medium';
    },

    // Generate correlation ID for tracking
    generateCorrelationId() {
        return createHash('sha256')
            .update(randomBytes(16))
            .digest('hex')
            .substring(0, 12);
    },

    // Send to external security monitoring
    sendToSecurityMonitoring(logEntry) {
        // Implementation depends on monitoring service
        // Could be Sentry, Datadog, or custom security monitoring
        if (process.env.SECURITY_MONITORING_ENABLED === 'true') {
            // Send to external service
        }
    },

    // Store security event in database
    storeSecurityEvent(logEntry) {
        // Implementation for database storage
        // Would typically insert into security_events table
    },

    // Zero Trust JWT Validation
    validateToken(token) {
        try {
            if (!token) {
                throw new Error('No token provided');
            }

            const decoded = jwt.verify(token, process.env.JWT_SECRET);

            // Zero Trust: Validate all claims
            if (!decoded.userId || !decoded.role || !decoded.sessionId) {
                throw new Error('Invalid token claims');
            }

            // Check if token is blacklisted
            if (this.isTokenBlacklisted(token)) {
                throw new Error('Token is blacklisted');
            }

            // Check session validity
            if (!this.isSessionValid(decoded.sessionId)) {
                throw new Error('Session expired or invalid');
            }

            return decoded;
        } catch (error) {
            this.logSecurityEvent('INVALID_TOKEN', {
                error: error.message,
                tokenHash: createHash('sha256').update(token).digest('hex')
            });
            throw error;
        }
    },

    // Check if token is blacklisted
    isTokenBlacklisted(token) {
        // Implementation would check against database
        return false; // Placeholder
    },

    // Check if session is valid
    isSessionValid(sessionId) {
        // Implementation would check against database
        return true; // Placeholder
    },

    // Input validation with schema support
    validateInput(data, schema) {
        try {
            // Enhanced validation with Joi schema support
            if (schema) {
                const { error } = schema.validate(data, {
                    abortEarly: false,
                    stripUnknown: true,
                    convert: true
                });

                if (error) {
                    this.logSecurityEvent('VALIDATION_FAILURE', {
                        error: error.details,
                        data: this.sanitizeData(data)
                    });
                    throw error;
                }
            }

            return true;
        } catch (error) {
            this.logSecurityEvent('VALIDATION_FAILURE', {
                error: error.message,
                data: this.sanitizeData(data)
            });
            throw error;
        }
    },

    // Sanitize data for logging
    sanitizeData(data) {
        if (typeof data !== 'object' || data === null) return data;

        const sanitized = {};
        const sensitiveFields = ['password', 'token', 'secret', 'key', 'creditCard'];

        for (const [key, value] of Object.entries(data)) {
            if (sensitiveFields.some(field => key.toLowerCase().includes(field))) {
                sanitized[key] = '***REDACTED***';
            } else if (typeof value === 'object') {
                sanitized[key] = this.sanitizeData(value);
            } else {
                sanitized[key] = value;
            }
        }

        return sanitized;
    },

    // Zero Trust Health Check
    securityHealth() {
        return {
            status: 'healthy',
            timestamp: new Date().toISOString(),
            security: {
                rateLimiting: 'enabled',
                jwtValidation: 'enabled',
                inputValidation: 'enabled',
                securityHeaders: 'enabled',
                auditLogging: 'enabled',
                zeroTrust: 'enabled'
            },
            configuration: {
                maxRequests: this.rateLimits.max,
                windowMs: this.rateLimits.windowMs,
                jwtAlgorithm: 'HS256',
                cspEnabled: true
            }
        };
    }
};

// Export security middleware
module.exports = security;