import express from 'express';
import cors from 'cors';
import helmet from 'helmet';
import compression from 'compression';
import expressRateLimit from 'express-rate-limit';
import { config } from '@/config';
import { Database } from '@/database/database';
import { logger } from '@/utils/logger';
import { setupRoutes } from '@/api/routes';
import { setupErrorHandling } from '@/middleware/error-handler';
import { setupSecurityMiddleware } from '@/middleware/security';

export class Application {
  private app: express.Application;
  private db: Database;

  constructor() {
    this.app = express();
    this.db = new Database();
    this.setupMiddleware();
    this.setupRoutes();
    this.setupErrorHandling();
  }

  private setupMiddleware(): void {
    // Security middleware
    this.app.use(helmet({
      contentSecurityPolicy: {
        directives: {
          defaultSrc: ["'self'"],
          styleSrc: ["'self'", "'unsafe-inline'"],
          scriptSrc: ["'self'"],
          imgSrc: ["'self'", "data:", "https:"],
        },
      },
    }));

    // CORS configuration
    this.app.use(cors({
      origin: config.app.allowedOrigins,
      credentials: true,
      methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'],
      allowedHeaders: ['Content-Type', 'Authorization', 'X-API-Key'],
    }));

    // Rate limiting
    const limiter = expressRateLimit({
      windowMs: 15 * 60 * 1000, // 15 minutes
      max: 100, // limit each IP to 100 requests per windowMs
      message: 'Too many requests from this IP, please try again later.',
      standardHeaders: true,
      legacyHeaders: false,
    });

    this.app.use('/api/', limiter);

    // Body parsing
    this.app.use(compression());
    this.app.use(express.json({ limit: '10mb' }));
    this.app.use(express.urlencoded({ extended: true, limit: '10mb' }));

    // Security middleware
    setupSecurityMiddleware(this.app);

    // Request logging
    this.app.use((req, res, next) => {
      logger.info('Incoming request', {
        method: req.method,
        url: req.url,
        ip: req.ip,
        userAgent: req.get('User-Agent'),
      });
      next();
    });
  }

  private setupRoutes(): void {
    setupRoutes(this.app, this.db);

    // Health check endpoint
    this.app.get('/health', (req, res) => {
      res.json({
        status: 'healthy',
        timestamp: new Date().toISOString(),
        version: '1.0.0',
      });
    });

    // 404 handler
    this.app.use('*', (req, res) => {
      res.status(404).json({
        error: 'Not Found',
        message: `Route ${req.originalUrl} not found`,
      });
    });
  }

  private setupErrorHandling(): void {
    setupErrorHandling(this.app);
  }

  public async start(): Promise<void> {
    try {
      // Initialize database
      await this.db.initialize();
      logger.info('Database initialized successfully');

      // Start server
      const port = config.app.port;
      this.app.listen(port, () => {
        logger.info(`Server running on port ${port}`);
        logger.info(`Environment: ${config.app.env}`);
      });

    } catch (error) {
      logger.error('Failed to start application', {
        error: error instanceof Error ? error.message : error,
      });
      process.exit(1);
    }
  }

  public getApp(): express.Application {
    return this.app;
  }
}

// Initialize and start application
const app = new Application();

if (require.main === module) {
  app.start().catch((error) => {
    logger.error('Application startup failed', {
      error: error instanceof Error ? error.message : error,
    });
    process.exit(1);
  });
}

export default app;