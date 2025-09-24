/**
 * Gatekeeper RPA Zero Trust Security Framework
 * Comprehensive security implementation with continuous validation
 */

import { createHash, randomBytes } from 'crypto';
import { sign, verify, decode } from 'jsonwebtoken';
import { compare, hash } from 'bcryptjs';
import { createClient } from 'redis';
import { logger } from '../logger';

// Security configuration
interface SecurityConfig {
  jwtSecret: string;
  jwtRefreshSecret: string;
  bcryptRounds: number;
  tokenExpiration: string;
  refreshTokenExpiration: string;
  maxLoginAttempts: number;
  lockoutDuration: number;
  rateLimitWindow: number;
  rateLimitMax: number;
  allowedOrigins: string[];
  ipWhitelist: string[];
  auditLogging: boolean;
  securityMonitoring: boolean;
}

export class ZeroTrustSecurity {
  private config: SecurityConfig;
  private redisClient: any;
  private securityMetrics: any;

  constructor(config: Partial<SecurityConfig> = {}) {
    this.config = {
      jwtSecret: process.env.JWT_SECRET || 'default-secret-key',
      jwtRefreshSecret: process.env.JWT_REFRESH_SECRET || 'default-refresh-secret',
      bcryptRounds: 12,
      tokenExpiration: '1h',
      refreshTokenExpiration: '7d',
      maxLoginAttempts: 5,
      lockoutDuration: 15 * 60 * 1000, // 15 minutes
      rateLimitWindow: 15 * 60 * 1000, // 15 minutes
      rateLimitMax: 100,
      allowedOrigins: process.env.ALLOWED_ORIGINS?.split(',') || ['http://localhost:3000'],
      ipWhitelist: process.env.IP_WHITELIST?.split(',') || [],
      auditLogging: true,
      securityMonitoring: true,
      ...config
    };

    this.redisClient = createClient({
      url: process.env.REDIS_URL || 'redis://localhost:6379'
    });

    this.securityMetrics = {
      authentications: 0,
      failedAuthentications: 0,
      securityEvents: 0,
      tokensIssued: 0,
      tokensRevoked: 0
    };

    this.initialize();
  }

  private async initialize() {
    try {
      await this.redisClient.connect();
      logger.info('Zero Trust Security Framework initialized');
    } catch (error) {
      logger.error('Failed to initialize Zero Trust Security Framework:', error);
    }
  }

  // Authentication with Zero Trust principles
  async authenticate(username: string, password: string, ipAddress: string, userAgent: string) {
    const sessionId = this.generateSessionId();
    const attemptKey = `auth_attempts:${ipAddress}:${username}`;

    try {
      // Check rate limiting
      const attempts = await this.redisClient.get(attemptKey);
      if (attempts && parseInt(attempts) >= this.config.maxLoginAttempts) {
        await this.logSecurityEvent('RATE_LIMIT_EXCEEDED', {
          username,
          ipAddress,
          reason: 'Too many authentication attempts'
        });
        throw new Error('Account temporarily locked due to too many attempts');
      }

      // Validate credentials (placeholder - implement actual user lookup)
      const user = await this.validateCredentials(username, password);

      if (!user) {
        // Increment failed attempts
        await this.redisClient.incr(attemptKey);
        await this.redisClient.expire(attemptKey, this.config.lockoutDuration / 1000);

        await this.logSecurityEvent('AUTHENTICATION_FAILED', {
          username,
          ipAddress,
          userAgent,
          reason: 'Invalid credentials'
        });

        this.securityMetrics.failedAuthentications++;
        throw new Error('Invalid credentials');
      }

      // Generate tokens
      const tokens = await this.generateTokens(user, sessionId);

      // Store session with security context
      const sessionContext = {
        userId: user.id,
        username: user.username,
        role: user.role,
        sessionId,
        ipAddress,
        userAgent,
        createdAt: new Date().toISOString(),
        lastActivity: new Date().toISOString(),
        securityLevel: this.calculateSecurityLevel(ipAddress, userAgent)
      };

      await this.redisClient.setEx(
        `session:${sessionId}`,
        7 * 24 * 60 * 60, // 7 days
        JSON.stringify(sessionContext)
      );

      // Clear failed attempts
      await this.redisClient.del(attemptKey);

      await this.logSecurityEvent('AUTHENTICATION_SUCCESS', {
        userId: user.id,
        username,
        ipAddress,
        sessionId
      });

      this.securityMetrics.authentications++;
      this.securityMetrics.tokensIssued++;

      return {
        user: {
          id: user.id,
          username: user.username,
          role: user.role
        },
        tokens,
        sessionId
      };

    } catch (error) {
      logger.error('Authentication error:', error);
      throw error;
    }
  }

  // Token validation with Zero Trust
  async validateToken(token: string, ipAddress: string, userAgent: string) {
    try {
      // Check if token is blacklisted
      const isBlacklisted = await this.redisClient.get(`blacklist:${token}`);
      if (isBlacklisted) {
        await this.logSecurityEvent('TOKEN_BLACKLISTED', {
          tokenHash: this.hashToken(token),
          ipAddress
        });
        throw new Error('Token is blacklisted');
      }

      // Verify JWT token
      const decoded = verify(token, this.config.jwtSecret) as any;

      // Validate session exists
      const sessionData = await this.redisClient.get(`session:${decoded.sessionId}`);
      if (!sessionData) {
        await this.logSecurityEvent('SESSION_NOT_FOUND', {
          sessionId: decoded.sessionId,
          userId: decoded.userId
        });
        throw new Error('Session not found');
      }

      const session = JSON.parse(sessionData);

      // Zero Trust: Validate session context
      if (session.ipAddress !== ipAddress) {
        await this.logSecurityEvent('IP_ADDRESS_MISMATCH', {
          sessionId: decoded.sessionId,
          expectedIp: session.ipAddress,
          actualIp: ipAddress
        });
        throw new Error('IP address mismatch');
      }

      // Update session activity
      session.lastActivity = new Date().toISOString();
      await this.redisClient.setEx(
        `session:${decoded.sessionId}`,
        7 * 24 * 60 * 60,
        JSON.stringify(session)
      );

      return {
        valid: true,
        user: {
          id: decoded.userId,
          role: decoded.role,
          sessionId: decoded.sessionId
        },
        session
      };

    } catch (error) {
      await this.logSecurityEvent('TOKEN_VALIDATION_FAILED', {
        tokenHash: this.hashToken(token),
        error: error.message
      });
      throw error;
    }
  }

  // Token refresh with security validation
  async refreshToken(refreshToken: string, ipAddress: string, userAgent: string) {
    try {
      const decoded = verify(refreshToken, this.config.jwtRefreshSecret) as any;

      // Validate session
      const sessionData = await this.redisClient.get(`session:${decoded.sessionId}`);
      if (!sessionData) {
        throw new Error('Session not found');
      }

      const session = JSON.parse(sessionData);

      // Zero Trust: Validate context
      if (session.ipAddress !== ipAddress) {
        throw new Error('IP address mismatch');
      }

      // Generate new tokens
      const newTokens = await this.generateTokens(
        { id: decoded.userId, role: decoded.role },
        decoded.sessionId
      );

      // Blacklist old refresh token
      await this.redisClient.setEx(
        `blacklist:${refreshToken}`,
        24 * 60 * 60, // 24 hours
        'true'
      );

      await this.logSecurityEvent('TOKEN_REFRESHED', {
        userId: decoded.userId,
        sessionId: decoded.sessionId
      });

      return newTokens;

    } catch (error) {
      await this.logSecurityEvent('TOKEN_REFRESH_FAILED', {
        tokenHash: this.hashToken(refreshToken),
        error: error.message
      });
      throw error;
    }
  }

  // Session revocation
  async revokeSession(sessionId: string, reason: string = 'user_logout') {
    try {
      const sessionData = await this.redisClient.get(`session:${sessionId}`);
      if (sessionData) {
        const session = JSON.parse(sessionData);

        // Blacklist all tokens for this session
        await this.redisClient.setEx(
          `session_blacklist:${sessionId}`,
          24 * 60 * 60,
          'true'
        );

        // Remove session
        await this.redisClient.del(`session:${sessionId}`);

        await this.logSecurityEvent('SESSION_REVOKED', {
          sessionId,
          userId: session.userId,
          reason
        });

        this.securityMetrics.tokensRevoked++;
      }
    } catch (error) {
      logger.error('Session revocation error:', error);
      throw error;
    }
  }

  // Input validation and sanitization
  validateInput(input: any, schema: any): any {
    try {
      // Implement Joi schema validation
      const { error, value } = schema.validate(input, {
        abortEarly: false,
        stripUnknown: true,
        convert: true
      });

      if (error) {
        this.logSecurityEvent('INPUT_VALIDATION_FAILED', {
          error: error.details,
          input: this.sanitizeData(input)
        });
        throw new Error(`Validation failed: ${error.details.map(d => d.message).join(', ')}`);
      }

      return this.sanitizeData(value);
    } catch (error) {
      throw error;
    }
  }

  // Security event logging
  async logSecurityEvent(eventType: string, details: any) {
    if (!this.config.auditLogging) return;

    const event = {
      timestamp: new Date().toISOString(),
      eventType,
      details,
      severity: this.calculateSeverity(eventType),
      correlationId: this.generateCorrelationId(),
      environment: process.env.NODE_ENV || 'development'
    };

    logger.warn('Security Event', event);

    if (this.config.securityMonitoring) {
      // Send to external monitoring service
      await this.sendToSecurityMonitoring(event);
    }

    this.securityMetrics.securityEvents++;
  }

  // Security health check
  async securityHealth() {
    try {
      const redisConnected = await this.redisClient.ping();

      return {
        status: 'healthy',
        timestamp: new Date().toISOString(),
        framework: 'zero-trust',
        redis: {
          connected: redisConnected === 'PONG'
        },
        metrics: this.securityMetrics,
        configuration: {
          maxLoginAttempts: this.config.maxLoginAttempts,
          lockoutDuration: this.config.lockoutDuration,
          rateLimitMax: this.config.rateLimitMax,
          auditLogging: this.config.auditLogging,
          securityMonitoring: this.config.securityMonitoring
        }
      };
    } catch (error) {
      logger.error('Security health check error:', error);
      return {
        status: 'unhealthy',
        timestamp: new Date().toISOString(),
        error: error.message
      };
    }
  }

  // Private helper methods
  private async validateCredentials(username: string, password: string) {
    // Placeholder implementation - replace with actual user lookup
    if (username === 'admin' && password === 'password') {
      return {
        id: 1,
        username: 'admin',
        role: 'admin',
        passwordHash: await hash('password', this.config.bcryptRounds)
      };
    }
    return null;
  }

  private async generateTokens(user: any, sessionId: string) {
    const accessToken = sign(
      {
        userId: user.id,
        role: user.role,
        sessionId,
        type: 'access'
      },
      this.config.jwtSecret,
      { expiresIn: this.config.tokenExpiration }
    );

    const refreshToken = sign(
      {
        userId: user.id,
        sessionId,
        type: 'refresh'
      },
      this.config.jwtRefreshSecret,
      { expiresIn: this.config.refreshTokenExpiration }
    );

    return { accessToken, refreshToken };
  }

  private generateSessionId(): string {
    return createHash('sha256')
      .update(randomBytes(32))
      .digest('hex')
      .substring(0, 32);
  }

  private generateCorrelationId(): string {
    return createHash('sha256')
      .update(Date.now().toString() + Math.random().toString())
      .digest('hex')
      .substring(0, 16);
  }

  private hashToken(token: string): string {
    return createHash('sha256').update(token).digest('hex');
  }

  private calculateSeverity(eventType: string): string {
    const severityMap = {
      'AUTHENTICATION_SUCCESS': 'low',
      'AUTHENTICATION_FAILED': 'medium',
      'RATE_LIMIT_EXCEEDED': 'high',
      'TOKEN_BLACKLISTED': 'high',
      'SESSION_REVOKED': 'medium',
      'IP_ADDRESS_MISMATCH': 'high',
      'INPUT_VALIDATION_FAILED': 'low',
      'TOKEN_VALIDATION_FAILED': 'medium'
    };
    return severityMap[eventType] || 'medium';
  }

  private calculateSecurityLevel(ipAddress: string, userAgent: string): string {
    // Implement security level calculation based on IP reputation, user agent, etc.
    let score = 100;

    // Reduce score for suspicious user agents
    const suspiciousAgents = ['bot', 'crawler', 'spider', 'scraper'];
    if (suspiciousAgents.some(agent => userAgent.toLowerCase().includes(agent))) {
      score -= 30;
    }

    // Adjust based on IP (implement IP reputation check)
    if (this.config.ipWhitelist.length > 0 && !this.config.ipWhitelist.includes(ipAddress)) {
      score -= 20;
    }

    if (score >= 80) return 'high';
    if (score >= 60) return 'medium';
    return 'low';
  }

  private sanitizeData(data: any): any {
    if (typeof data !== 'object' || data === null) return data;

    const sanitized = Array.isArray(data) ? [] : {};
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
  }

  private async sendToSecurityMonitoring(event: any) {
    // Implement sending to external security monitoring service
    // Could be Sentry, Datadog, or custom security monitoring
    logger.info('Security monitoring event:', event);
  }
}

// Export singleton instance
export const zeroTrustSecurity = new ZeroTrustSecurity();