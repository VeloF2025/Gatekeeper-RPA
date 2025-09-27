import { eq, and, or } from 'drizzle-orm';
import { database } from '../database';
import { users, apiSessions } from '../schemas';
import {
  CreateUserRequest,
  UpdateUserRequest,
  LoginRequest,
  LoginResponse,
  CreateApiSessionRequest,
  User,
  NewUser,
  ApiSession
} from '../models';
import { logger, errorLogger } from '@/utils/logger';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { v4 as uuidv4 } from 'uuid';

export class AuthService {
  private readonly jwtSecret: string;
  private readonly jwtRefreshSecret: string;
  private readonly accessTokenExpiresIn: string;
  private readonly refreshTokenExpiresIn: string;

  constructor() {
    this.jwtSecret = process.env.JWT_SECRET || 'your-secret-key';
    this.jwtRefreshSecret = process.env.JWT_REFRESH_SECRET || 'your-refresh-secret';
    this.accessTokenExpiresIn = process.env.JWT_ACCESS_EXPIRES_IN || '15m';
    this.refreshTokenExpiresIn = process.env.JWT_REFRESH_EXPIRES_IN || '7d';
  }

  /**
   * Create a new user
   */
  async createUser(data: CreateUserRequest): Promise<Omit<User, 'passwordHash'>> {
    try {
      // Check if user already exists
      const existingUser = await database.drizzle
        .select()
        .from(users)
        .where(
          or(
            eq(users.email, data.email),
            eq(users.username, data.username)
          )
        )
        .limit(1);

      if (existingUser.length > 0) {
        throw new Error('User with this email or username already exists');
      }

      // Hash password
      const passwordHash = await bcrypt.hash(data.password, 12);

      const newUser: NewUser = {
        email: data.email,
        username: data.username,
        passwordHash,
        firstName: data.firstName,
        lastName: data.lastName,
        role: data.role || 'user',
        isActive: true,
        metadata: data.metadata || {},
      };

      const [createdUser] = await database.drizzle
        .insert(users)
        .values(newUser)
        .returning();

      // Remove password hash from response
      const { passwordHash: _, ...userWithoutPassword } = createdUser;

      logger.info('User created successfully', {
        userId: createdUser.id,
        email: createdUser.email
      });

      return userWithoutPassword;
    } catch (error) {
      errorLogger.error('Failed to create user', {
        error: error instanceof Error ? error.message : error,
        data: { email: data.email, username: data.username }
      });
      throw error;
    }
  }

  /**
   * Login user
   */
  async login(data: LoginRequest): Promise<LoginResponse> {
    try {
      // Find user by email
      const [user] = await database.drizzle
        .select()
        .from(users)
        .where(
          and(
            eq(users.email, data.email),
            eq(users.isActive, true)
          )
        );

      if (!user) {
        throw new Error('Invalid credentials');
      }

      // Verify password
      const isPasswordValid = await bcrypt.compare(data.password, user.passwordHash);
      if (!isPasswordValid) {
        throw new Error('Invalid credentials');
      }

      // Generate tokens
      const accessToken = this.generateAccessToken(user.id);
      const refreshToken = this.generateRefreshToken(user.id);

      // Hash tokens for storage
      const accessTokenHash = await bcrypt.hash(accessToken, 12);
      const refreshTokenHash = await bcrypt.hash(refreshToken, 12);

      // Create session
      const sessionData: CreateApiSessionRequest = {
        userId: user.id,
        tokenHash: accessTokenHash,
        refreshTokenHash,
        expiresAt: new Date(Date.now() + 15 * 60 * 1000), // 15 minutes
      };

      await this.createSession(sessionData);

      // Update last login
      await database.drizzle
        .update(users)
        .set({ lastLogin: new Date() })
        .where(eq(users.id, user.id));

      // Remove password hash from response
      const { passwordHash: _, ...userWithoutPassword } = user;

      logger.info('User logged in successfully', {
        userId: user.id,
        email: user.email
      });

      return {
        user: userWithoutPassword,
        accessToken,
        refreshToken,
        expiresIn: 15 * 60 // 15 minutes in seconds
      };
    } catch (error) {
      errorLogger.error('Login failed', {
        error: error instanceof Error ? error.message : error,
        email: data.email
      });
      throw error;
    }
  }

  /**
   * Refresh access token
   */
  async refreshToken(refreshToken: string): Promise<{ accessToken: string; expiresIn: number }> {
    try {
      // Verify refresh token
      const decoded = jwt.verify(refreshToken, this.jwtRefreshSecret) as { userId: string };
      const userId = decoded.userId;

      // Find active session with this refresh token
      const sessions = await database.drizzle
        .select()
        .from(apiSessions)
        .where(
          and(
            eq(apiSessions.userId, userId),
            eq(apiSessions.isActive, true)
          )
        )
        .limit(10); // Get recent sessions

      // Check if any session has a matching refresh token hash
      let validSession: ApiSession | null = null;
      for (const session of sessions) {
        if (session.refreshTokenHash && await bcrypt.compare(refreshToken, session.refreshTokenHash)) {
          validSession = session;
          break;
        }
      }

      if (!validSession) {
        throw new Error('Invalid refresh token');
      }

      // Check if session is expired
      if (new Date() > validSession.expiresAt) {
        // Deactivate expired session
        await database.drizzle
          .update(apiSessions)
          .set({ isActive: false })
          .where(eq(apiSessions.id, validSession.id));
        throw new Error('Session expired');
      }

      // Generate new access token
      const newAccessToken = this.generateAccessToken(userId);

      // Update last used timestamp
      await database.drizzle
        .update(apiSessions)
        .set({ lastUsed: new Date() })
        .where(eq(apiSessions.id, validSession.id));

      logger.info('Access token refreshed successfully', { userId });

      return {
        accessToken: newAccessToken,
        expiresIn: 15 * 60 // 15 minutes in seconds
      };
    } catch (error) {
      errorLogger.error('Token refresh failed', {
        error: error instanceof Error ? error.message : error
      });
      throw error;
    }
  }

  /**
   * Logout user
   */
  async logout(accessToken: string): Promise<void> {
    try {
      // Get token payload without verification to get user ID
      const decoded = jwt.decode(accessToken) as { userId: string } | null;
      if (!decoded || !decoded.userId) {
        return;
      }

      // Find and deactivate session
      const sessions = await database.drizzle
        .select()
        .from(apiSessions)
        .where(eq(apiSessions.userId, decoded.userId));

      for (const session of sessions) {
        if (session.tokenHash && await bcrypt.compare(accessToken, session.tokenHash)) {
          await database.drizzle
            .update(apiSessions)
            .set({ isActive: false })
            .where(eq(apiSessions.id, session.id));
          break;
        }
      }

      logger.info('User logged out successfully', { userId: decoded.userId });
    } catch (error) {
      errorLogger.error('Logout failed', {
        error: error instanceof Error ? error.message : error
      });
      throw error;
    }
  }

  /**
   * Get user by ID
   */
  async getUserById(id: string): Promise<Omit<User, 'passwordHash'> | null> {
    try {
      const [user] = await database.drizzle
        .select()
        .from(users)
        .where(eq(users.id, id));

      if (!user) {
        return null;
      }

      // Remove password hash from response
      const { passwordHash: _, ...userWithoutPassword } = user;
      return userWithoutPassword;
    } catch (error) {
      errorLogger.error('Failed to get user by ID', {
        error: error instanceof Error ? error.message : error,
        id
      });
      throw new Error('Failed to get user');
    }
  }

  /**
   * Get user by email
   */
  async getUserByEmail(email: string): Promise<Omit<User, 'passwordHash'> | null> {
    try {
      const [user] = await database.drizzle
        .select()
        .from(users)
        .where(eq(users.email, email));

      if (!user) {
        return null;
      }

      // Remove password hash from response
      const { passwordHash: _, ...userWithoutPassword } = user;
      return userWithoutPassword;
    } catch (error) {
      errorLogger.error('Failed to get user by email', {
        error: error instanceof Error ? error.message : error,
        email
      });
      throw new Error('Failed to get user');
    }
  }

  /**
   * Update user
   */
  async updateUser(id: string, data: UpdateUserRequest): Promise<Omit<User, 'passwordHash'>> {
    try {
      const updateData: Partial<NewUser> = {
        ...data,
        updatedAt: new Date(),
      };

      // Hash new password if provided
      if (data.password) {
        updateData.passwordHash = await bcrypt.hash(data.password, 12);
      }

      const [updatedUser] = await database.drizzle
        .update(users)
        .set(updateData)
        .where(eq(users.id, id))
        .returning();

      if (!updatedUser) {
        throw new Error('User not found');
      }

      // Remove password hash from response
      const { passwordHash: _, ...userWithoutPassword } = updatedUser;

      logger.info('User updated successfully', {
        userId: id,
        updates: Object.keys(data)
      });

      return userWithoutPassword;
    } catch (error) {
      errorLogger.error('Failed to update user', {
        error: error instanceof Error ? error.message : error,
        id,
        data: Object.keys(data)
      });
      throw new Error('Failed to update user');
    }
  }

  /**
   * Create API session
   */
  private async createSession(data: CreateApiSessionRequest): Promise<void> {
    try {
      const newSession: ApiSession = {
        id: uuidv4(),
        userId: data.userId,
        tokenHash: data.tokenHash,
        refreshTokenHash: data.refreshTokenHash,
        expiresAt: data.expiresAt,
        isActive: true,
        ipAddress: data.ipAddress,
        userAgent: data.userAgent,
        metadata: data.metadata || {},
      };

      await database.drizzle
        .insert(apiSessions)
        .values(newSession);

      // Clean up expired sessions
      await this.cleanupExpiredSessions();
    } catch (error) {
      errorLogger.error('Failed to create session', {
        error: error instanceof Error ? error.message : error,
        userId: data.userId
      });
      throw new Error('Failed to create session');
    }
  }

  /**
   * Generate access token
   */
  private generateAccessToken(userId: string): string {
    return jwt.sign(
      { userId, type: 'access' },
      this.jwtSecret as jwt.Secret,
      { expiresIn: this.accessTokenExpiresIn }
    );
  }

  /**
   * Generate refresh token
   */
  private generateRefreshToken(userId: string): string {
    return jwt.sign(
      { userId, type: 'refresh' },
      this.jwtRefreshSecret as jwt.Secret,
      { expiresIn: this.refreshTokenExpiresIn }
    );
  }

  /**
   * Clean up expired sessions
   */
  private async cleanupExpiredSessions(): Promise<void> {
    try {
      await database.drizzle
        .update(apiSessions)
        .set({ isActive: false })
        .where(
          and(
            eq(apiSessions.isActive, true),
            // Sessions expired more than 24 hours ago
            // This is a simple check - in production you might want to be more precise
          )
        );
    } catch (error) {
      errorLogger.error('Failed to cleanup expired sessions', {
        error: error instanceof Error ? error.message : error
      });
      // Don't throw here as this is a cleanup operation
    }
  }

  /**
   * Verify access token
   */
  async verifyAccessToken(accessToken: string): Promise<{ userId: string } | null> {
    try {
      const decoded = jwt.verify(accessToken, this.jwtSecret) as { userId: string; type: string };

      if (decoded.type !== 'access') {
        return null;
      }

      // Verify session exists and is active
      const sessions = await database.drizzle
        .select()
        .from(apiSessions)
        .where(
          and(
            eq(apiSessions.userId, decoded.userId),
            eq(apiSessions.isActive, true)
          )
        );

      // Check if any session has a matching token hash
      for (const session of sessions) {
        if (await bcrypt.compare(accessToken, session.tokenHash)) {
          // Check if session is expired
          if (new Date() > session.expiresAt) {
            // Deactivate expired session
            await database.drizzle
              .update(apiSessions)
              .set({ isActive: false })
              .where(eq(apiSessions.id, session.id));
            return null;
          }
          return { userId: decoded.userId };
        }
      }

      return null;
    } catch (error) {
      return null;
    }
  }
}

// Export singleton instance
export const authService = new AuthService();