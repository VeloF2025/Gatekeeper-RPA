import jwt from 'jsonwebtoken';
import bcrypt from 'bcryptjs';
import { v4 as uuidv4 } from 'uuid';
import { database } from '@/database/database';
import * as schema from '@/database/schemas/index';
import { User, UserRole } from '@/types';
import { logger } from '@/utils/logger';
import { AuthenticationError, ValidationError } from '@/middleware/error-handler';

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface RegisterData {
  username: string;
  email: string;
  password: string;
  role: UserRole;
  fullName: string;
  phoneNumber?: string;
}

export interface TokenPayload {
  userId: string;
  username: string;
  role: UserRole;
  email: string;
}

export class AuthService {
  private jwtSecret: string;
  private refreshTokenSecret: string;
  private accessTokenExpiry: string;
  private refreshTokenExpiry: string;

  constructor() {
    this.jwtSecret = process.env.JWT_SECRET || 'your-secret-key';
    this.refreshTokenSecret = process.env.REFRESH_TOKEN_SECRET || 'your-refresh-secret';
    this.accessTokenExpiry = process.env.ACCESS_TOKEN_EXPIRY || '1h';
    this.refreshTokenExpiry = process.env.REFRESH_TOKEN_EXPIRY || '7d';
  }

  /**
   * Register a new user
   */
  async register(userData: RegisterData): Promise<{
    user: Omit<User, 'password'>;
    accessToken: string;
    refreshToken: string;
  }> {
    try {
      // Validate input
      this.validateRegistrationData(userData);

      // Check if user already exists
      const existingUser = await this.findUserByUsernameOrEmail(userData.username, userData.email);
      if (existingUser) {
        throw new ValidationError('User with this username or email already exists');
      }

      // Hash password
      const hashedPassword = await bcrypt.hash(userData.password, 12);

      // Create user
      const userId = uuidv4();
      const user = await this.createUser({
        id: userId,
        username: userData.username,
        email: userData.email,
        password: hashedPassword,
        role: userData.role,
        fullName: userData.fullName,
        phoneNumber: userData.phoneNumber,
        isActive: true,
        lastLogin: null,
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      // Generate tokens
      const tokens = this.generateTokens({
        userId: user.id,
        username: user.username,
        role: user.role,
        email: user.email,
      });

      // Store refresh token
      await this.storeRefreshToken(userId, tokens.refreshToken);

      logger.info('User registered successfully', {
        userId: user.id,
        username: user.username,
        role: user.role,
      });

      return {
        user: this.sanitizeUser(user),
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
      };

    } catch (error) {
      logger.error('User registration failed', {
        error: error instanceof Error ? error.message : error,
        username: userData.username,
        email: userData.email,
      });

      throw error;
    }
  }

  /**
   * Authenticate user and generate tokens
   */
  async login(credentials: LoginCredentials): Promise<{
    user: Omit<User, 'password'>;
    accessToken: string;
    refreshToken: string;
  }> {
    try {
      // Validate input
      if (!credentials.username || !credentials.password) {
        throw new ValidationError('Username and password are required');
      }

      // Find user
      const user = await this.findUserByUsername(credentials.username);
      if (!user) {
        throw new AuthenticationError('Invalid credentials');
      }

      // Check if user is active
      if (!user.isActive) {
        throw new AuthenticationError('Account is disabled');
      }

      // Verify password
      const isPasswordValid = await bcrypt.compare(credentials.password, user.password);
      if (!isPasswordValid) {
        throw new AuthenticationError('Invalid credentials');
      }

      // Generate tokens
      const tokens = this.generateTokens({
        userId: user.id,
        username: user.username,
        role: user.role,
        email: user.email,
      });

      // Store refresh token
      await this.storeRefreshToken(user.id, tokens.refreshToken);

      // Update last login
      await this.updateLastLogin(user.id);

      logger.info('User logged in successfully', {
        userId: user.id,
        username: user.username,
        role: user.role,
      });

      return {
        user: this.sanitizeUser(user),
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
      };

    } catch (error) {
      logger.error('User login failed', {
        error: error instanceof Error ? error.message : error,
        username: credentials.username,
      });

      throw error;
    }
  }

  /**
   * Refresh access token
   */
  async refreshToken(refreshToken: string): Promise<{
    accessToken: string;
    refreshToken: string;
  }> {
    try {
      if (!refreshToken) {
        throw new AuthenticationError('Refresh token is required');
      }

      // Verify refresh token
      const decoded = jwt.verify(refreshToken, this.refreshTokenSecret) as TokenPayload;

      // Check if refresh token exists in database
      const storedToken = await this.getStoredRefreshToken(decoded.userId, refreshToken);
      if (!storedToken) {
        throw new AuthenticationError('Invalid refresh token');
      }

      // Generate new tokens
      const tokens = this.generateTokens({
        userId: decoded.userId,
        username: decoded.username,
        role: decoded.role,
        email: decoded.email,
      });

      // Store new refresh token
      await this.storeRefreshToken(decoded.userId, tokens.refreshToken);

      // Remove old refresh token
      await this.removeRefreshToken(decoded.userId, refreshToken);

      return {
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
      };

    } catch (error) {
      logger.error('Token refresh failed', {
        error: error instanceof Error ? error.message : error,
      });

      throw new AuthenticationError('Invalid or expired refresh token');
    }
  }

  /**
   * Logout user by invalidating refresh token
   */
  async logout(userId: string, refreshToken: string): Promise<void> {
    try {
      await this.removeRefreshToken(userId, refreshToken);

      logger.info('User logged out successfully', { userId });

    } catch (error) {
      logger.error('User logout failed', {
        error: error instanceof Error ? error.message : error,
        userId,
      });

      throw error;
    }
  }

  /**
   * Get current user by ID
   */
  async getCurrentUser(userId: string): Promise<Omit<User, 'password'> | null> {
    try {
      const user = await this.findUserById(userId);
      return user ? this.sanitizeUser(user) : null;

    } catch (error) {
      logger.error('Failed to get current user', {
        error: error instanceof Error ? error.message : error,
        userId,
      });

      throw error;
    }
  }

  /**
   * Get all users (admin only)
   */
  async getAllUsers(): Promise<Omit<User, 'password'>[]> {
    try {
      const users = await database.query(`
        SELECT id, username, email, role, full_name, phone_number, is_active, last_login, created_at, updated_at
        FROM users
        ORDER BY created_at DESC
      `);

      return users.map(this.sanitizeUser);

    } catch (error) {
      logger.error('Failed to get users', {
        error: error instanceof Error ? error.message : error,
      });

      throw error;
    }
  }

  /**
   * Change user password
   */
  async changePassword(userId: string, currentPassword: string, newPassword: string): Promise<void> {
    try {
      // Validate new password
      if (!newPassword || newPassword.length < 8) {
        throw new ValidationError('New password must be at least 8 characters long');
      }

      // Get current user
      const user = await this.findUserById(userId);
      if (!user) {
        throw new AuthenticationError('User not found');
      }

      // Verify current password
      const isCurrentPasswordValid = await bcrypt.compare(currentPassword, user.password);
      if (!isCurrentPasswordValid) {
        throw new AuthenticationError('Current password is incorrect');
      }

      // Hash new password
      const hashedNewPassword = await bcrypt.hash(newPassword, 12);

      // Update password
      await this.updateUserPassword(userId, hashedNewPassword);

      // Invalidate all refresh tokens for this user
      await this.removeAllRefreshTokens(userId);

      logger.info('Password changed successfully', { userId });

    } catch (error) {
      logger.error('Password change failed', {
        error: error instanceof Error ? error.message : error,
        userId,
      });

      throw error;
    }
  }

  /**
   * Create user (for seeding/admin purposes)
   */
  async createUser(userData: any): Promise<User> {
    try {
      const result = await database.drizzle
        .insert(schema.users)
        .values({
          id: userData.id,
          username: userData.username,
          email: userData.email,
          passwordHash: userData.password,
          firstName: userData.firstName,
          lastName: userData.lastName,
          role: userData.role,
          isActive: userData.isActive,
          lastLogin: userData.lastLogin,
          createdAt: userData.createdAt,
          updatedAt: userData.updatedAt,
          metadata: userData.metadata || {},
        })
        .returning();

      return result[0];
    } catch (error) {
      logger.error('Failed to create user', {
        error: error instanceof Error ? error.message : error,
        username: userData.username,
      });
      throw error;
    }
  }

  /**
   * Verify access token
   */
  async verifyAccessToken(token: string): Promise<TokenPayload | null> {
    try {
      const decoded = jwt.verify(token, this.jwtSecret) as TokenPayload;
      return decoded;
    } catch (error) {
      logger.error('Token verification failed', { error: error instanceof Error ? error.message : error });
      return null;
    }
  }

  /**
   * Get user by ID
   */
  async getUserById(userId: string): Promise<User | null> {
    return await this.findUserById(userId);
  }

  private validateRegistrationData(data: RegisterData): void {
    if (!data.username || data.username.length < 3) {
      throw new ValidationError('Username must be at least 3 characters long');
    }

    if (!data.email || !data.email.includes('@')) {
      throw new ValidationError('Valid email address is required');
    }

    if (!data.password || data.password.length < 8) {
      throw new ValidationError('Password must be at least 8 characters long');
    }

    if (!data.fullName || data.fullName.trim().length < 2) {
      throw new ValidationError('Full name is required');
    }

    if (!Object.values(UserRole).includes(data.role)) {
      throw new ValidationError('Invalid user role');
    }
  }

  private generateTokens(payload: TokenPayload): { accessToken: string; refreshToken: string } {
    const accessToken = jwt.sign(payload, this.jwtSecret, { expiresIn: this.accessTokenExpiry });
    const refreshToken = jwt.sign(payload, this.refreshTokenSecret, { expiresIn: this.refreshTokenExpiry });

    return { accessToken, refreshToken };
  }

  private async findUserByUsername(username: string): Promise<User | null> {
    const result = await database.query('SELECT * FROM users WHERE username = $1', [username]);
    return result.length > 0 ? result[0] : null;
  }

  private async findUserById(id: string): Promise<User | null> {
    const result = await database.query('SELECT * FROM users WHERE id = $1', [id]);
    return result.length > 0 ? result[0] : null;
  }

  private async findUserByUsernameOrEmail(username: string, email: string): Promise<User | null> {
    const result = await database.query(
      'SELECT * FROM users WHERE username = $1 OR email = $2',
      [username, email]
    );
    return result.length > 0 ? result[0] : null;
  }

  private async storeRefreshToken(userId: string, refreshToken: string): Promise<void> {
    const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000); // 7 days

    await database.query(`
      INSERT INTO api_sessions (user_id, token_hash, refresh_token_hash, expires_at, created_at)
      VALUES ($1, $2, $3, $4, $5)
    `, [userId, refreshToken, refreshToken, expiresAt, new Date()]);
  }

  private async getStoredRefreshToken(userId: string, refreshToken: string): Promise<boolean> {
    const result = await database.query(`
      SELECT 1 FROM api_sessions
      WHERE user_id = $1 AND token_hash = $2 AND expires_at > NOW() AND is_active = true
    `, [userId, refreshToken]);

    return result.length > 0;
  }

  private async removeRefreshToken(userId: string, refreshToken: string): Promise<void> {
    await database.query(`
      DELETE FROM api_sessions
      WHERE user_id = $1 AND token_hash = $2
    `, [userId, refreshToken]);
  }

  private async removeAllRefreshTokens(userId: string): Promise<void> {
    await database.query('DELETE FROM api_sessions WHERE user_id = $1', [userId]);
  }

  private async updateLastLogin(userId: string): Promise<void> {
    await database.query(
      'UPDATE users SET last_login = $1, updated_at = $2 WHERE id = $3',
      [new Date(), new Date(), userId]
    );
  }

  private async updateUserPassword(userId: string, hashedPassword: string): Promise<void> {
    await database.query(
      'UPDATE users SET password_hash = $1, updated_at = $2 WHERE id = $3',
      [hashedPassword, new Date(), userId]
    );
  }

  private sanitizeUser(user: User): Omit<User, 'password'> {
    const { password, ...sanitizedUser } = user;
    return sanitizedUser;
  }
}

// Export singleton instance
export const authService = new AuthService();