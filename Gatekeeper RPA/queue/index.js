/**
 * Gatekeeper Queue Worker
 * Zero Trust security implementation for message processing
 */

const express = require('express');
const { createClient } = require('redis');
const { Queue, Worker } = require('bullmq');
const { Worker: JobWorker } = require('bullmq');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const helmet = require('helmet');
const cors = require('cors');
const winston = require('winston');
const { v4: uuidv4 } = require('uuid');
const axios = require('axios');
const Joi = require('joi');

// Security configuration
const SECURITY_CONFIG = {
    jwtSecret: process.env.JWT_SECRET || 'your-secret-key',
    jwtRefreshSecret: process.env.JWT_REFRESH_SECRET || 'your-refresh-secret',
    bcryptRounds: 12,
    maxLoginAttempts: 5,
    lockoutDuration: 15 * 60 * 1000, // 15 minutes
    rateLimit: {
        windowMs: 15 * 60 * 1000, // 15 minutes
        max: 100
    },
    allowedOrigins: process.env.ALLOWED_ORIGINS ? process.env.ALLOWED_ORIGINS.split(',') : ['http://localhost:3000']
};

// Zero Trust Security middleware
const security = {
    // JWT validation
    validateToken(req, res, next) {
        const token = req.headers.authorization?.replace('Bearer ', '');

        if (!token) {
            return res.status(401).json({ error: 'No token provided' });
        }

        try {
            const decoded = jwt.verify(token, SECURITY_CONFIG.jwtSecret);

            // Zero Trust: Validate all claims
            if (!decoded.userId || !decoded.role || !decoded.sessionId) {
                throw new Error('Invalid token claims');
            }

            // Check if token is blacklisted
            if (this.isTokenBlacklisted(token)) {
                throw new Error('Token is blacklisted');
            }

            req.user = decoded;
            next();
        } catch (error) {
            this.logSecurityEvent('INVALID_TOKEN', { error: error.message });
            res.status(401).json({ error: 'Invalid token' });
        }
    },

    // Token blacklist check
    isTokenBlacklisted(token) {
        // Implement Redis-based token blacklist
        return false; // Placeholder
    },

    // Rate limiting middleware
    rateLimiter(req, res, next) {
        const clientIp = req.ip;
        const now = Date.now();

        // Simple in-memory rate limiting (use Redis in production)
        if (!this.rateLimits) this.rateLimits = new Map();

        const record = this.rateLimits.get(clientIp) || { count: 0, resetTime: now + SECURITY_CONFIG.rateLimit.windowMs };

        if (now > record.resetTime) {
            record.count = 1;
            record.resetTime = now + SECURITY_CONFIG.rateLimit.windowMs;
        } else {
            record.count++;
        }

        this.rateLimits.set(clientIp, record);

        if (record.count > SECURITY_CONFIG.rateLimit.max) {
            this.logSecurityEvent('RATE_LIMIT_EXCEEDED', { ip: clientIp });
            return res.status(429).json({
                error: 'Too many requests',
                retryAfter: Math.ceil((record.resetTime - now) / 1000)
            });
        }

        next();
    },

    // Input validation
    validateInput(schema) {
        return (req, res, next) => {
            const { error } = schema.validate(req.body, {
                abortEarly: false,
                stripUnknown: true
            });

            if (error) {
                this.logSecurityEvent('VALIDATION_FAILURE', {
                    error: error.details,
                    path: req.path
                });
                return res.status(400).json({
                    error: 'Validation failed',
                    details: error.details
                });
            }

            next();
        };
    },

    // Security logging
    logSecurityEvent(event, details) {
        logger.warn('Security Event', {
            event,
            details,
            timestamp: new Date().toISOString(),
            severity: this.calculateSeverity(event)
        });
    },

    calculateSeverity(event) {
        const severityMap = {
            'INVALID_TOKEN': 'high',
            'RATE_LIMIT_EXCEEDED': 'medium',
            'VALIDATION_FAILURE': 'low',
            'BRUTE_FORCE_ATTEMPT': 'high',
            'UNAUTHORIZED_ACCESS': 'high'
        };
        return severityMap[event] || 'medium';
    },

    // Sanitize output
    sanitizeOutput(data) {
        const sensitiveFields = ['password', 'token', 'secret', 'key'];

        if (typeof data !== 'object' || data === null) return data;

        const sanitized = Array.isArray(data) ? [] : {};

        for (const [key, value] of Object.entries(data)) {
            if (sensitiveFields.some(field => key.toLowerCase().includes(field))) {
                sanitized[key] = '***REDACTED***';
            } else if (typeof value === 'object') {
                sanitized[key] = this.sanitizeOutput(value);
            } else {
                sanitized[key] = value;
            }
        }

        return sanitized;
    }
};

// Configure logging
const logger = winston.createLogger({
    level: 'info',
    format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.errors({ stack: true }),
        winston.format.json()
    ),
    transports: [
        new winston.transports.File({ filename: 'logs/queue-error.log', level: 'error' }),
        new winston.transports.File({ filename: 'logs/queue.log' }),
        new winston.transports.Console({
            format: winston.format.simple()
        })
    ]
});

// Validation schemas
const schemas = {
    login: Joi.object({
        username: Joi.string().alphanum().min(3).max(30).required(),
        password: Joi.string().min(8).required()
    }),

    message: Joi.object({
        recipient: Joi.string().required(),
        content: Joi.string().max(1600).required(),
        messageType: Joi.string().valid('text', 'image', 'document').default('text'),
        priority: Joi.string().valid('low', 'normal', 'high').default('normal')
    }),

    auditJob: Joi.object({
        url: Joi.string().uri().required(),
        credentials: Joi.object({
            username: Joi.string().required(),
            password: Joi.string().required()
        }).required(),
        auditType: Joi.string().valid('ticket_count', 'status_check', 'compliance_check').required(),
        priority: Joi.string().valid('low', 'normal', 'high').default('normal')
    })
};

// Initialize Redis connection
const redisClient = createClient({
    url: process.env.REDIS_URL || 'redis://localhost:6379'
});

// Create queues
const queues = {
    whatsapp: new Queue('whatsapp-messages', {
        connection: redisClient,
        defaultJobOptions: {
            removeOnComplete: 100,
            removeOnFail: 50,
            attempts: 3,
            backoff: {
                type: 'exponential',
                delay: 2000
            }
        }
    }),

    audits: new Queue('audit-jobs', {
        connection: redisClient,
        defaultJobOptions: {
            removeOnComplete: 50,
            removeOnFail: 25,
            attempts: 3,
            backoff: {
                type: 'exponential',
                delay: 5000
            }
        }
    }),

    notifications: new Queue('notifications', {
        connection: redisClient,
        defaultJobOptions: {
            removeOnComplete: 200,
            removeOnFail: 100,
            attempts: 2,
            backoff: {
                type: 'fixed',
                delay: 1000
            }
        }
    })
};

// Job processors
const jobProcessors = {
    // WhatsApp message processor
    async processWhatsAppMessage(job) {
        const { recipient, content, messageType, priority } = job.data;

        try {
            // Call WhatsApp Business API
            const response = await axios.post(
                `https://graph.facebook.com/v17.0/${process.env.WHATSAPP_PHONE_NUMBER_ID}/messages`,
                {
                    messaging_product: 'whatsapp',
                    to: recipient,
                    type: messageType,
                    [messageType]: {
                        body: content
                    }
                },
                {
                    headers: {
                        'Authorization': `Bearer ${process.env.WHATSAPP_ACCESS_TOKEN}`,
                        'Content-Type': 'application/json'
                    }
                }
            );

            logger.info('WhatsApp message sent', {
                messageId: response.data.messages?.[0]?.id,
                recipient,
                messageType,
                priority
            });

            return {
                success: true,
                messageId: response.data.messages?.[0]?.id,
                timestamp: new Date().toISOString()
            };

        } catch (error) {
            logger.error('WhatsApp message failed', {
                error: error.message,
                recipient,
                priority
            });
            throw error;
        }
    },

    // Audit job processor
    async processAuditJob(job) {
        const { url, credentials, auditType, priority } = job.data;

        try {
            // Forward to RPA workers
            const response = await axios.post('http://rpa-workers:8081/audit', {
                url,
                credentials,
                auditType,
                priority
            }, {
                headers: {
                    'Authorization': `Bearer ${process.env.INTERNAL_API_KEY}`,
                    'Content-Type': 'application/json'
                },
                timeout: 30000 // 30 second timeout
            });

            logger.info('Audit job forwarded to RPA workers', {
                jobId: job.id,
                auditType,
                priority,
                rpaJobId: response.data.jobId
            });

            return {
                success: true,
                jobId: response.data.jobId,
                timestamp: new Date().toISOString()
            };

        } catch (error) {
            logger.error('Audit job forwarding failed', {
                error: error.message,
                auditType,
                priority
            });
            throw error;
        }
    },

    // Notification processor
    async processNotification(job) {
        const { recipient, subject, message, type } = job.data;

        try {
            // Implement notification logic (email, SMS, in-app)
            logger.info('Notification processed', {
                recipient,
                subject,
                type,
                timestamp: new Date().toISOString()
            });

            return {
                success: true,
                notificationId: uuidv4(),
                timestamp: new Date().toISOString()
            };

        } catch (error) {
            logger.error('Notification processing failed', {
                error: error.message,
                recipient,
                type
            });
            throw error;
        }
    }
};

// Initialize Express app
const app = express();
app.use(helmet());
app.use(cors({
    origin: SECURITY_CONFIG.allowedOrigins,
    credentials: true
}));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Security middleware
app.use(security.rateLimiter);

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({
        status: 'healthy',
        timestamp: new Date().toISOString(),
        service: 'queue-worker',
        uptime: process.uptime(),
        queues: {
            whatsapp: {
                waiting: queues.whatsapp.getWaiting().length,
                active: queues.whatsapp.getActive().length,
                completed: queues.whatsapp.getCompleted().length,
                failed: queues.whatsapp.getFailed().length
            },
            audits: {
                waiting: queues.audits.getWaiting().length,
                active: queues.audits.getActive().length,
                completed: queues.audits.getCompleted().length,
                failed: queues.audits.getFailed().length
            },
            notifications: {
                waiting: queues.notifications.getWaiting().length,
                active: queues.notifications.getActive().length,
                completed: queues.notifications.getCompleted().length,
                failed: queues.notifications.getFailed().length
            }
        }
    });
});

// Authentication endpoint
app.post('/auth/login', security.validateInput(schemas.login), async (req, res) => {
    try {
        const { username, password } = req.body;

        // Validate credentials (placeholder - implement proper user lookup)
        const user = await validateUser(username, password);

        if (!user) {
            security.logSecurityEvent('LOGIN_FAILED', { username });
            return res.status(401).json({ error: 'Invalid credentials' });
        }

        // Generate tokens
        const sessionId = uuidv4();
        const accessToken = jwt.sign(
            { userId: user.id, role: user.role, sessionId },
            SECURITY_CONFIG.jwtSecret,
            { expiresIn: '1h' }
        );

        const refreshToken = jwt.sign(
            { userId: user.id, sessionId },
            SECURITY_CONFIG.jwtRefreshSecret,
            { expiresIn: '7d' }
        );

        // Store session in Redis
        await redisClient.setEx(
            `session:${sessionId}`,
            7 * 24 * 60 * 60, // 7 days
            JSON.stringify({ userId: user.id, role: user.role })
        );

        logger.info('User logged in', { username, userId: user.id });

        res.json({
            success: true,
            accessToken,
            refreshToken,
            user: {
                id: user.id,
                username: user.username,
                role: user.role
            }
        });

    } catch (error) {
        logger.error('Login error', { error: error.message });
        res.status(500).json({ error: 'Login failed' });
    }
});

// Queue management endpoints
app.post('/queue/whatsapp', security.validateToken, security.validateInput(schemas.message), async (req, res) => {
    try {
        const job = await queues.whatsapp.add('send-message', req.body, {
            priority: req.body.priority === 'high' ? 10 : 1,
            delay: 0
        });

        logger.info('WhatsApp message queued', {
            jobId: job.id,
            recipient: req.body.recipient,
            messageType: req.body.messageType
        });

        res.json({
            success: true,
            jobId: job.id,
            message: 'Message queued successfully'
        });

    } catch (error) {
        logger.error('Error queuing WhatsApp message', { error: error.message });
        res.status(500).json({ error: 'Failed to queue message' });
    }
});

app.post('/queue/audit', security.validateToken, security.validateInput(schemas.auditJob), async (req, res) => {
    try {
        const job = await queues.audits.add('process-audit', req.body, {
            priority: req.body.priority === 'high' ? 10 : 1,
            delay: 0
        });

        logger.info('Audit job queued', {
            jobId: job.id,
            auditType: req.body.auditType,
            priority: req.body.priority
        });

        res.json({
            success: true,
            jobId: job.id,
            message: 'Audit job queued successfully'
        });

    } catch (error) {
        logger.error('Error queuing audit job', { error: error.message });
        res.status(500).json({ error: 'Failed to queue audit job' });
    }
});

// Job status endpoint
app.get('/job/:queue/:id', security.validateToken, async (req, res) => {
    try {
        const queue = queues[req.params.queue];
        if (!queue) {
            return res.status(404).json({ error: 'Queue not found' });
        }

        const job = await queue.getJob(req.params.id);

        if (!job) {
            return res.status(404).json({ error: 'Job not found' });
        }

        const state = await job.getState();
        const result = await job.returnvalue;

        res.json(security.sanitizeOutput({
            id: job.id,
            state,
            data: job.data,
            result,
            createdAt: job.timestamp,
            processedAt: job.processedOn,
            finishedAt: job.finishedOn,
            attempts: job.attemptsMade,
            failedReason: job.failedReason
        }));

    } catch (error) {
        logger.error('Error getting job status', { error: error.message });
        res.status(500).json({ error: 'Failed to get job status' });
    }
});

// Initialize workers
const workers = {
    whatsapp: new JobWorker('whatsapp-messages', async (job) => {
        return await jobProcessors.processWhatsAppMessage(job);
    }, { connection: redisClient }),

    audits: new JobWorker('audit-jobs', async (job) => {
        return await jobProcessors.processAuditJob(job);
    }, { connection: redisClient }),

    notifications: new JobWorker('notifications', async (job) => {
        return await jobProcessors.processNotification(job);
    }, { connection: redisClient })
};

// Helper functions
async function validateUser(username, password) {
    // Placeholder implementation - replace with actual user validation
    if (username === 'admin' && password === 'password') {
        return { id: 1, username: 'admin', role: 'admin' };
    }
    return null;
}

// Start server
const PORT = process.env.PORT || 8082;

async function startServer() {
    try {
        // Connect to Redis
        await redisClient.connect();
        logger.info('Connected to Redis');

        // Start Express server
        app.listen(PORT, () => {
            logger.info(`Queue Worker server running on port ${PORT}`);
        });

        // Graceful shutdown
        process.on('SIGTERM', async () => {
            logger.info('Received SIGTERM, shutting down gracefully');
            await Promise.all(Object.values(workers).map(worker => worker.close()));
            await redisClient.quit();
            process.exit(0);
        });

    } catch (error) {
        logger.error('Failed to start server:', error);
        process.exit(1);
    }
}

// Start the application
startServer();

module.exports = { app, logger, queues, security };