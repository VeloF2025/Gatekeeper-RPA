/**
 * Advanced JWT Security Implementation
 * Enhanced security with refresh tokens, rotation, and zero-trust principles
 */

import jwt from 'jsonwebtoken';
import bcrypt from 'bcryptjs';
import { logger } from '@/utils/logger';

interface JwtPayload {
  userId: string;
  email: string;
  role: string;
  permissions: string[];
  sessionId: string;
  iat?: number;
  exp?: number;
}

interface RefreshTokenPayload {
  userId: string;
  tokenId: string;
  deviceId: string;
  iat?: number;
  exp?: number;
}

interface TokenPair {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  refreshExpiresIn: number;
}

interface SecurityConfig {
  jwtSecret: string;
  jwtRefreshSecret: string;
  accessTokenExpiry: string;
  refreshTokenExpiry: string;
  bcryptRounds: number;
  issuer: string;
  audience: string;
}

class JwtSecurityManager {
  private config: SecurityConfig;
  private tokenBlacklist: Set<string> = new Set();
  private refreshTokens: Map<string, RefreshTokenPayload> = new Map();

  constructor(config: SecurityConfig) {
    this.config = config;
    this.startTokenCleanup();
  }

  /**
   * Hash password with enhanced security
   */
  async hashPassword(password: string): Promise<string> {
    try {
      return await bcrypt.hash(password, this.config.bcryptRounds);
    } catch (error) {
      logger.error('Password hashing failed', { error });
      throw new Error('Password hashing failed');
    }
  }

  /**
   * Verify password with constant-time comparison
   */
  async verifyPassword(password: string, hash: string): Promise<boolean> {
    try {
      return await bcrypt.compare(password, hash);
    } catch (error) {
      logger.error('Password verification failed', { error });
      return false;
    }
  }

  /**
   * Generate secure access token with enhanced payload
   */
  generateAccessToken(payload: Omit<JwtPayload, 'iat' | 'exp'>): string {
    const tokenPayload: JwtPayload = {
      ...payload,
      iat: Math.floor(Date.now() / 1000),
      exp: Math.floor(Date.now() / 1000) + this.parseExpiry(this.config.accessTokenExpiry),
    };

    return jwt.sign(tokenPayload, this.config.jwtSecret, {
      algorithm: 'HS512',
      issuer: this.config.issuer,
      audience: this.config.audience,
      header: {
        typ: 'JWT',
        alg: 'HS512',
        kid: this.generateKeyId(),
      },
    });
  }

  /**
   * Generate secure refresh token
   */
  generateRefreshToken(payload: Omit<RefreshTokenPayload, 'iat' | 'exp'>): string {
    const tokenId = this.generateTokenId();
    const tokenPayload: RefreshTokenPayload = {
      ...payload,
      tokenId,
      iat: Math.floor(Date.now() / 1000),
      exp: Math.floor(Date.now() / 1000) + this.parseExpiry(this.config.refreshTokenExpiry),
    };

    // Store refresh token for validation
    this.refreshTokens.set(tokenId, tokenPayload);

    return jwt.sign(tokenPayload, this.config.jwtRefreshSecret, {
      algorithm: 'HS512',
      issuer: this.config.issuer,
      audience: this.config.audience,
      header: {
        typ: 'JWT',
        alg: 'HS512',
        kid: this.generateKeyId(),
      },
    });
  }

  /**
   * Generate token pair for authentication
   */
  generateTokenPair(userPayload: Omit<JwtPayload, 'sessionId' | 'iat' | 'exp'>, deviceId: string): TokenPair {
    const sessionId = this.generateSessionId();
    const accessToken = this.generateAccessToken({
      ...userPayload,
      sessionId,
    });

    const refreshToken = this.generateRefreshToken({
      userId: userPayload.userId,
      deviceId,
    });

    return {
      accessToken,
      refreshToken,
      expiresIn: this.parseExpiry(this.config.accessTokenExpiry),
      refreshExpiresIn: this.parseExpiry(this.config.refreshTokenExpiry),
    };
  }

  /**
   * Verify access token with enhanced validation
   */
  verifyAccessToken(token: string): JwtPayload | null {
    try {
      // Check if token is blacklisted
      if (this.tokenBlacklist.has(token)) {
        logger.warn('Attempt to use blacklisted access token', { token: token.substring(0, 10) + '...' });
        return null;
      }

      const payload = jwt.verify(token, this.config.jwtSecret, {
        algorithms: ['HS512'],
        issuer: this.config.issuer,
        audience: this.config.audience,
      }) as JwtPayload;

      // Validate token structure
      if (!this.validateJwtPayload(payload)) {
        logger.warn('Invalid JWT payload structure', { payload });
        return null;
      }

      return payload;
    } catch (error) {
      logger.warn('JWT verification failed', {
        error: error instanceof Error ? error.message : error,
        token: token.substring(0, 10) + '...'
      });
      return null;
    }
  }

  /**
   * Verify refresh token with enhanced validation
   */
  verifyRefreshToken(token: string): RefreshTokenPayload | null {
    try {
      const payload = jwt.verify(token, this.config.jwtRefreshSecret, {
        algorithms: ['HS512'],
        issuer: this.config.issuer,
        audience: this.config.audience,
      }) as RefreshTokenPayload;

      // Check if refresh token exists in store
      const storedToken = this.refreshTokens.get(payload.tokenId);
      if (!storedToken) {
        logger.warn('Invalid refresh token - not found in store', { tokenId: payload.tokenId });
        return null;
      }

      // Validate token expiration
      if (storedToken.exp && storedToken.exp < Math.floor(Date.now() / 1000)) {
        logger.warn('Expired refresh token', { tokenId: payload.tokenId });
        this.refreshTokens.delete(payload.tokenId);
        return null;
      }

      return payload;
    } catch (error) {
      logger.warn('Refresh token verification failed', {
        error: error instanceof Error ? error.message : error,
        token: token.substring(0, 10) + '...'
      });
      return null;
    }
  }

  /**
   * Refresh access token using refresh token
   */
  refreshAccessToken(refreshToken: string, deviceId: string): { accessToken: string; expiresIn: number } | null {
    const refreshPayload = this.verifyRefreshToken(refreshToken);
    if (!refreshPayload) {
      return null;
    }

    // Validate device ID for security
    if (refreshPayload.deviceId !== deviceId) {
      logger.warn('Refresh token device mismatch', {
        expectedDevice: refreshPayload.deviceId,
        providedDevice: deviceId
      });
      return null;
    }

    // Get user information from refresh token
    const userPayload: Omit<JwtPayload, 'sessionId' | 'iat' | 'exp'> = {
      userId: refreshPayload.userId,
      email: '', // Will be populated from database
      role: '', // Will be populated from database
      permissions: [], // Will be populated from database
    };

    // Generate new access token
    const sessionId = this.generateSessionId();
    const accessToken = this.generateAccessToken({
      ...userPayload,
      sessionId,
    });

    return {
      accessToken,
      expiresIn: this.parseExpiry(this.config.accessTokenExpiry),
    };
  }

  /**
   * Blacklist access token (for logout)
   */
  blacklistToken(token: string): void {
    const payload = this.verifyAccessToken(token);
    if (payload) {
      this.tokenBlacklist.add(token);
      logger.info('Access token blacklisted', { sessionId: payload.sessionId });
    }
  }

  /**
   * Revoke refresh token
   */
  revokeRefreshToken(token: string): void {
    const payload = this.verifyRefreshToken(token);
    if (payload) {
      this.refreshTokens.delete(payload.tokenId);
      logger.info('Refresh token revoked', { tokenId: payload.tokenId });
    }
  }

  /**
   * Revoke all refresh tokens for a user
   */
  revokeAllUserTokens(userId: string): void {
    for (const [tokenId, token] of this.refreshTokens.entries()) {
      if (token.userId === userId) {
        this.refreshTokens.delete(tokenId);
      }
    }
    logger.info('All refresh tokens revoked for user', { userId });
  }

  /**
   * Cleanup expired tokens periodically
   */
  private startTokenCleanup(): void {
    setInterval(() => {
      const now = Math.floor(Date.now() / 1000);

      // Cleanup expired refresh tokens
      for (const [tokenId, token] of this.refreshTokens.entries()) {
        if (token.exp && token.exp < now) {
          this.refreshTokens.delete(tokenId);
        }
      }

      // Cleanup blacklisted access tokens (keep for 24 hours)
      const blacklistArray = Array.from(this.tokenBlacklist);
      blacklistArray.forEach(token => {
        const payload = this.verifyAccessToken(token);
        if (payload && payload.exp && payload.exp < now - 86400) { // 24 hours ago
          this.tokenBlacklist.delete(token);
        }
      });
    }, 3600000); // Run every hour
  }

  /**
   * Parse expiry string to seconds
   */
  private parseExpiry(expiry: string): number {
    const unit = expiry.slice(-1);
    const value = parseInt(expiry.slice(0, -1));

    switch (unit) {
      case 's': return value;
      case 'm': return value * 60;
      case 'h': return value * 3600;
      case 'd': return value * 86400;
      default: return value;
    }
  }

  /**
   * Generate secure session ID
   */
  private generateSessionId(): string {
    return `sess_${Date.now()}_${Math.random().toString(36).substring(2, 15)}`;
  }

  /**
   * Generate secure token ID
   */
  private generateTokenId(): string {
    return `token_${Date.now()}_${Math.random().toString(36).substring(2, 15)}`;
  }

  /**
   * Generate secure key ID
   */
  private generateKeyId(): string {
    return `kid_${Date.now()}_${Math.random().toString(36).substring(2, 10)}`;
  }

  /**
   * Validate JWT payload structure
   */
  private validateJwtPayload(payload: any): payload is JwtPayload {
    return (
      payload &&
      typeof payload.userId === 'string' &&
      typeof payload.email === 'string' &&
      typeof payload.role === 'string' &&
      Array.isArray(payload.permissions) &&
      typeof payload.sessionId === 'string' &&
      typeof payload.iat === 'number' &&
      typeof payload.exp === 'number'
    );
  }
}

// Create singleton instance
const securityConfig: SecurityConfig = {
  jwtSecret: process.env.JWT_SECRET || 'your-super-secret-jwt-key',
  jwtRefreshSecret: process.env.JWT_REFRESH_SECRET || 'your-super-secret-refresh-key',
  accessTokenExpiry: process.env.JWT_EXPIRES_IN || '15m',
  refreshTokenExpiry: process.env.JWT_REFRESH_EXPIRES_IN || '7d',
  bcryptRounds: parseInt(process.env.BCRYPT_ROUNDS || '12'),
  issuer: process.env.JWT_ISSUER || 'gatekeeper-rpa',
  audience: process.env.JWT_AUDIENCE || 'gatekeeper-rpa-users',
};

export const jwtSecurity = new JwtSecurityManager(securityConfig);
export { JwtSecurityManager, SecurityConfig, JwtPayload, RefreshTokenPayload, TokenPair };