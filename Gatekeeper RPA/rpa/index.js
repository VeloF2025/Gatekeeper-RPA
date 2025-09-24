/**
 * Gatekeeper RPA Workers Main Entry Point
 * Zero Trust security implementation for automation tasks
 */

const express = require('express');
const { createClient } = require('redis');
const { Queue } = require('bull');
const { v4: uuidv4 } = require('uuid');
const winston = require('winston');
const { Worker } = require('bullmq');
const io = require('socket.io')(3001, {
    cors: {
        origin: "*",
        methods: ["GET", "POST"]
    }
});

// Security configuration
const SECURITY_CONFIG = {
    maxConcurrentJobs: parseInt(process.env.MAX_CONCURRENT_AUDITS) || 3,
    browserPoolSize: parseInt(process.env.BROWSER_POOL_SIZE) || 5,
    jobTimeout: 30 * 60 * 1000, // 30 minutes
    maxRetryAttempts: 3,
    rateLimit: {
        windowMs: 15 * 60 * 1000, // 15 minutes
        max: 100 // limit each IP to 100 requests per windowMs
    }
};

// Zero Trust Security middleware
const security = {
    validateRequest(req, res, next) {
        // Validate authorization header
        const authHeader = req.headers['authorization'];
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({ error: 'Unauthorized' });
        }

        // Validate API key (simplified - implement proper JWT validation)
        const apiKey = authHeader.substring(7);
        if (!this.validateApiKey(apiKey)) {
            return res.status(401).json({ error: 'Invalid API key' });
        }

        next();
    },

    validateApiKey(apiKey) {
        // Implement proper API key validation
        return apiKey && apiKey.length > 0; // Simplified
    },

    sanitizeInput(input) {
        if (typeof input !== 'string') return input;
        return input
            .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '')
            .replace(/javascript:/gi, '')
            .trim();
    },

    logSecurityEvent(event, details) {
        logger.warn('Security Event', { event, details });
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
        new winston.transports.File({ filename: 'logs/rpa-error.log', level: 'error' }),
        new winston.transports.File({ filename: 'logs/rpa.log' }),
        new winston.transports.Console({
            format: winston.format.simple()
        })
    ]
});

// Initialize Redis connection
const redisClient = createClient({
    url: process.env.REDIS_URL || 'redis://localhost:6379'
});

// Create job queue
const jobQueue = new Queue('rpa-jobs', {
    connection: redisClient,
    defaultJobOptions: {
        removeOnComplete: 10,
        removeOnFail: 5,
        attempts: SECURITY_CONFIG.maxRetryAttempts,
        backoff: {
            type: 'exponential',
            delay: 2000
        }
    }
});

// Browser pool for automation
class BrowserPool {
    constructor(size = SECURITY_CONFIG.browserPoolSize) {
        this.size = size;
        this.pool = [];
        this.available = [];
        this.busy = new Set();
        this.initialized = false;
    }

    async initialize() {
        if (this.initialized) return;

        const { chromium } = require('playwright');

        for (let i = 0; i < this.size; i++) {
            const browser = await chromium.launch({
                headless: true,
                args: [
                    '--no-sandbox',
                    '--disable-setuid-sandbox',
                    '--disable-dev-shm-usage',
                    '--disable-accelerated-2d-canvas',
                    '--no-first-run',
                    '--no-zygote',
                    '--disable-gpu'
                ]
            });

            const context = await browser.newContext({
                viewport: { width: 1920, height: 1080 },
                userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
            });

            this.pool.push({ browser, context, id: uuidv4() });
            this.available.push(i);
        }

        this.initialized = true;
        logger.info(`Browser pool initialized with ${this.size} browsers`);
    }

    async getBrowser() {
        if (!this.initialized) {
            await this.initialize();
        }

        if (this.available.length === 0) {
            throw new Error('No browsers available in pool');
        }

        const index = this.available.pop();
        this.busy.add(index);
        return this.pool[index];
    }

    async releaseBrowser(browserId) {
        const index = this.pool.findIndex(b => b.id === browserId);
        if (index !== -1 && this.busy.has(index)) {
            this.busy.delete(index);
            this.available.push(index);
        }
    }

    async cleanup() {
        for (const { browser } of this.pool) {
            await browser.close();
        }
        this.pool = [];
        this.available = [];
        this.busy.clear();
        this.initialized = false;
    }
}

const browserPool = new BrowserPool();

// Job processor for different RPA tasks
const jobProcessors = {
    async oneMapAudit(job) {
        const { url, credentials, auditType } = job.data;
        const browserData = await browserPool.getBrowser();

        try {
            const { context } = browserData;
            const page = await context.newPage();

            // Navigate to OneMap
            await page.goto(url, { waitUntil: 'networkidle' });

            // Login with credentials
            await page.fill('#username', credentials.username);
            await page.fill('#password', credentials.password);
            await page.click('#login-button');

            // Wait for dashboard
            await page.waitForSelector('.dashboard', { timeout: 10000 });

            // Perform audit based on type
            let result;
            switch (auditType) {
                case 'ticket_count':
                    result = await this.auditTicketCount(page);
                    break;
                case 'status_check':
                    result = await this.auditTicketStatus(page);
                    break;
                case 'compliance_check':
                    result = await this.auditCompliance(page);
                    break;
                default:
                    throw new Error(`Unknown audit type: ${auditType}`);
            }

            // Take screenshot for evidence
            const screenshot = await page.screenshot({ fullPage: true });

            await page.close();
            await browserPool.releaseBrowser(browserData.id);

            return {
                success: true,
                result,
                screenshot: screenshot.toString('base64'),
                timestamp: new Date().toISOString()
            };

        } catch (error) {
            await browserPool.releaseBrowser(browserData.id);
            throw error;
        }
    },

    async auditTicketCount(page) {
        // Extract ticket count from dashboard
        const countElement = await page.$('.ticket-count');
        const count = await countElement?.textContent() || '0';

        return {
            totalTickets: parseInt(count.replace(/[^\d]/g, '')),
            auditType: 'ticket_count'
        };
    },

    async auditTicketStatus(page) {
        // Extract ticket status information
        const statusElements = await page.$$('.ticket-status');
        const statuses = {};

        for (const element of statusElements) {
            const status = await element.textContent();
            const count = await element.$eval('.count', el => el.textContent) || '0';
            statuses[status.trim()] = parseInt(count.replace(/[^\d]/g, ''));
        }

        return {
            statuses,
            auditType: 'status_check'
        };
    },

    async auditCompliance(page) {
        // Perform compliance audit
        const complianceElements = await page.$$('.compliance-item');
        const compliance = [];

        for (const element of complianceElements) {
            const item = await element.textContent();
            const status = await element.$eval('.status', el => el.textContent) || 'unknown';
            compliance.push({ item, status });
        }

        return {
            compliance,
            auditType: 'compliance_check'
        };
    }
};

// Initialize Express app
const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Security middleware
app.use(security.validateRequest);

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({
        status: 'healthy',
        timestamp: new Date().toISOString(),
        service: 'rpa-workers',
        uptime: process.uptime(),
        browserPool: {
            total: browserPool.size,
            available: browserPool.available.length,
            busy: browserPool.busy.size
        },
        queue: {
            waiting: jobQueue.getWaiting().length,
            active: jobQueue.getActive().length,
            completed: jobQueue.getCompleted().length,
            failed: jobQueue.getFailed().length
        }
    });
});

// Submit audit job
app.post('/audit', async (req, res) => {
    try {
        const { url, credentials, auditType, priority = 'normal' } = req.body;

        // Validate input
        if (!url || !credentials || !auditType) {
            return res.status(400).json({ error: 'Missing required fields' });
        }

        // Create job
        const job = await jobQueue.add('oneMapAudit', {
            url: security.sanitizeInput(url),
            credentials: {
                username: security.sanitizeInput(credentials.username),
                password: security.sanitizeInput(credentials.password)
            },
            auditType: security.sanitizeInput(auditType)
        }, {
            priority: priority === 'high' ? 10 : 1,
            delay: 0,
            removeOnComplete: 10,
            removeOnFail: 5
        });

        res.json({
            success: true,
            jobId: job.id,
            message: 'Audit job submitted successfully'
        });

    } catch (error) {
        logger.error('Error submitting audit job:', error);
        res.status(500).json({ error: 'Failed to submit audit job' });
    }
});

// Get job status
app.get('/job/:id', async (req, res) => {
    try {
        const job = await jobQueue.getJob(req.params.id);

        if (!job) {
            return res.status(404).json({ error: 'Job not found' });
        }

        const state = await job.getState();
        const result = await job.returnvalue;

        res.json({
            id: job.id,
            state,
            data: job.data,
            result,
            createdAt: job.timestamp,
            processedAt: job.processedOn,
            finishedAt: job.finishedOn,
            attempts: job.attemptsMade,
            failedReason: job.failedReason
        });

    } catch (error) {
        logger.error('Error getting job status:', error);
        res.status(500).json({ error: 'Failed to get job status' });
    }
});

// Start server
const PORT = process.env.PORT || 8081;

async function startServer() {
    try {
        // Connect to Redis
        await redisClient.connect();
        logger.info('Connected to Redis');

        // Initialize browser pool
        await browserPool.initialize();

        // Start Express server
        app.listen(PORT, () => {
            logger.info(`RPA Workers server running on port ${PORT}`);
        });

        // Graceful shutdown
        process.on('SIGTERM', async () => {
            logger.info('Received SIGTERM, shutting down gracefully');
            await browserPool.cleanup();
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

module.exports = { app, logger, browserPool, jobQueue };