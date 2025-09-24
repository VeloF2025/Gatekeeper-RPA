import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { authService } from '@/database/services/auth.service';
import { database } from '@/database/database';
import * as schema from '@/database/schemas';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { logger, errorLogger } from '@/utils/logger';

// Mock dependencies
vi.mock('@/database/database');
vi.mock('@/utils/logger');
vi.mock('bcryptjs');
vi.mock('jsonwebtoken');

const mockDatabase = vi.mocked(database);
const mockBcrypt = vi.mocked(bcrypt);
const mockJwt = vi.mocked(jwt);
const mockLogger = vi.mocked(logger);
const mockErrorLogger = vi.mocked(errorLogger);

describe('AuthService', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    // Setup default mock responses
    mockBcrypt.hash.mockResolvedValue('hashed-password');
    mockBcrypt.compare.mockResolvedValue(true);
    mockJwt.sign.mockReturnValue('fake-jwt-token');
    mockJwt.verify.mockReturnValue({ userId: 'test-user-id', type: 'access' });

    mockDatabase.drizzle.select.mockReturnValue({
      where: vi.fn().mockReturnThis(),
      and: vi.fn().mockReturnThis(),
      limit: vi.fn().mockResolvedValue([])
    } as any);

    mockDatabase.drizzle.insert.returnValues = [{
      id: 'test-user-id',
      email: 'test@example.com',
      username: 'testuser',
      passwordHash: 'hashed-password',
      firstName: 'Test',
      lastName: 'User',
      role: 'user',
      isActive: true,
      createdAt: new Date(),
      updatedAt: new Date(),
      metadata: {}
    }];

    mockDatabase.drizzle.update.returnValues = [{
      id: 'test-user-id',
      email: 'test@example.com',
      username: 'testuser',
      passwordHash: 'hashed-password',
      firstName: 'Updated',
      lastName: 'User',
      role: 'manager',
      isActive: true,
      lastLogin: new Date(),
      createdAt: new Date(),
      updatedAt: new Date(),
      metadata: {}
    }];
  });

  describe('createUser', () => {
    it('should create a new user with valid data', async () => {
      const userData = {
        email: 'test@example.com',
        username: 'testuser',
        password: 'Password123!',
        firstName: 'Test',
        lastName: 'User',
        role: 'user' as const
      };

      // Mock user lookup to return empty (no existing user)
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        or: vi.fn().mockResolvedValue([])
      } as any);

      const result = await authService.createUser(userData);

      expect(result).toEqual({
        id: 'test-user-id',
        email: 'test@example.com',
        username: 'testuser',
        firstName: 'Test',
        lastName: 'User',
        role: 'user',
        isActive: true,
        createdAt: expect.any(Date),
        updatedAt: expect.any(Date),
        metadata: {}
      });

      expect(mockBcrypt.hash).toHaveBeenCalledWith(userData.password, 12);
      expect(mockDatabase.drizzle.insert).toHaveBeenCalledWith(
        expect.objectContaining({
          email: userData.email,
          username: userData.username,
          passwordHash: 'hashed-password',
          firstName: userData.firstName,
          lastName: userData.lastName,
          role: userData.role
        })
      );

      expect(mockLogger.info).toHaveBeenCalledWith('User created successfully', {
        userId: 'test-user-id',
        email: 'test@example.com'
      });
    });

    it('should throw error if user already exists', async () => {
      const userData = {
        email: 'existing@example.com',
        username: 'existinguser',
        password: 'Password123!'
      };

      // Mock user lookup to return existing user
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        or: vi.fn().mockResolvedValue([{
          id: 'existing-user-id',
          email: 'existing@example.com',
          username: 'existinguser'
        }])
      } as any);

      await expect(authService.createUser(userData)).rejects.toThrow(
        'User with this email or username already exists'
      );

      expect(mockDatabase.drizzle.insert).not.toHaveBeenCalled();
    });

    it('should handle password hashing errors', async () => {
      const userData = {
        email: 'test@example.com',
        username: 'testuser',
        password: 'Password123!'
      };

      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        or: vi.fn().mockResolvedValue([])
      } as any);

      mockBcrypt.hash.mockRejectedValue(new Error('Hashing failed'));

      await expect(authService.createUser(userData)).rejects.toThrow('Hashing failed');

      expect(mockErrorLogger.error).toHaveBeenCalledWith(
        'Failed to create user',
        expect.objectContaining({
          error: 'Hashing failed',
          data: { email: 'test@example.com', username: 'testuser' }
        })
      );
    });
  });

  describe('login', () => {
    it('should login user with valid credentials', async () => {
      const loginData = {
        email: 'test@example.com',
        password: 'Password123!'
      };

      // Mock user lookup
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([{
          id: 'test-user-id',
          email: 'test@example.com',
          username: 'testuser',
          passwordHash: 'hashed-password',
          firstName: 'Test',
          lastName: 'User',
          role: 'user',
          isActive: true
        }])
      } as any);

      // Mock session creation
      mockDatabase.drizzle.insert.returnValues = [{
        id: 'session-id',
        userId: 'test-user-id',
        tokenHash: 'hashed-access-token',
        refreshTokenHash: 'hashed-refresh-token',
        expiresAt: new Date(Date.now() + 15 * 60 * 1000),
        isActive: true,
        createdAt: new Date()
      }];

      const result = await authService.login(loginData);

      expect(result).toEqual({
        user: {
          id: 'test-user-id',
          email: 'test@example.com',
          username: 'testuser',
          firstName: 'Test',
          lastName: 'User',
          role: 'user',
          isActive: true,
          lastLogin: expect.any(Date),
          createdAt: expect.any(Date),
          updatedAt: expect.any(Date),
          metadata: {}
        },
        accessToken: 'fake-jwt-token',
        refreshToken: 'fake-jwt-token',
        expiresIn: 900 // 15 minutes
      });

      expect(mockBcrypt.compare).toHaveBeenCalledWith(loginData.password, 'hashed-password');
      expect(mockJwt.sign).toHaveBeenCalledTimes(2); // Access and refresh tokens
      expect(mockLogger.info).toHaveBeenCalledWith('User logged in successfully', {
        userId: 'test-user-id',
        email: 'test@example.com'
      });
    });

    it('should throw error for invalid credentials', async () => {
      const loginData = {
        email: 'test@example.com',
        password: 'wrong-password'
      };

      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([{
          id: 'test-user-id',
          email: 'test@example.com',
          passwordHash: 'hashed-password',
          isActive: true
        }])
      } as any);

      mockBcrypt.compare.mockResolvedValue(false);

      await expect(authService.login(loginData)).rejects.toThrow('Invalid credentials');

      expect(mockLogger.warn).toHaveBeenCalledWith('Login failed - invalid credentials', {
        email: 'test@example.com'
      });
    });

    it('should throw error for non-existent user', async () => {
      const loginData = {
        email: 'nonexistent@example.com',
        password: 'Password123!'
      };

      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([])
      } as any);

      await expect(authService.login(loginData)).rejects.toThrow('Invalid credentials');
    });

    it('should throw error for inactive user', async () => {
      const loginData = {
        email: 'inactive@example.com',
        password: 'Password123!'
      };

      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([{
          id: 'inactive-user-id',
          email: 'inactive@example.com',
          passwordHash: 'hashed-password',
          isActive: false
        }])
      } as any);

      await expect(authService.login(loginData)).rejects.toThrow('Invalid credentials');
    });
  });

  describe('refreshToken', () => {
    it('should refresh access token with valid refresh token', async () => {
      const refreshToken = 'valid-refresh-token';

      // Mock session lookup
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([{
          id: 'session-id',
          userId: 'test-user-id',
          tokenHash: 'hashed-access-token',
          refreshTokenHash: 'hashed-refresh-token',
          expiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // 7 days from now
          isActive: true
        }])
      } as any);

      mockBcrypt.compare.mockResolvedValueOnce(true); // For access token
      mockBcrypt.compare.mockResolvedValueOnce(true); // For refresh token

      const result = await authService.refreshToken(refreshToken);

      expect(result).toEqual({
        accessToken: 'fake-jwt-token',
        expiresIn: 900
      });

      expect(mockJwt.sign).toHaveBeenCalledWith(
        { userId: 'test-user-id', type: 'access' },
        expect.any(String),
        { expiresIn: '15m' }
      );
    });

    it('should throw error for invalid refresh token', async () => {
      const refreshToken = 'invalid-refresh-token';

      mockJwt.verify.mockImplementationOnce(() => {
        throw new Error('Invalid token');
      });

      await expect(authService.refreshToken(refreshToken)).rejects.toThrow('Invalid refresh token');
    });

    it('should throw error for expired session', async () => {
      const refreshToken = 'expired-refresh-token';

      // Mock expired session
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([{
          id: 'session-id',
          userId: 'test-user-id',
          refreshTokenHash: 'hashed-refresh-token',
          expiresAt: new Date(Date.now() - 1000), // Expired 1 second ago
          isActive: true
        }])
      } as any);

      mockBcrypt.compare.mockResolvedValueOnce(true);

      await expect(authService.refreshToken(refreshToken)).rejects.toThrow('Session expired');
    });
  });

  describe('logout', () => {
    it('should logout user successfully', async () => {
      const accessToken = 'valid-access-token';

      // Mock JWT decode to get user ID
      vi.spyOn(jwt, 'decode').mockReturnValueOnce({ userId: 'test-user-id' });

      // Mock session lookup
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        eq: vi.fn().mockResolvedValue([{
          id: 'session-id',
          userId: 'test-user-id',
          tokenHash: 'hashed-access-token',
          isActive: true
        }])
      } as any);

      mockBcrypt.compare.mockResolvedValueOnce(true);

      await authService.logout(accessToken);

      expect(mockDatabase.drizzle.update).toHaveBeenCalledWith(
        expect.objectContaining({
          isActive: false
        })
      );

      expect(mockLogger.info).toHaveBeenCalledWith('User logged out successfully', {
        userId: 'test-user-id'
      });
    });
  });

  describe('getUserById', () => {
    it('should return user without password hash', async () => {
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue([{
          id: 'test-user-id',
          email: 'test@example.com',
          username: 'testuser',
          passwordHash: 'hashed-password',
          firstName: 'Test',
          lastName: 'User',
          role: 'user',
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date(),
          metadata: {}
        }])
      } as any);

      const result = await authService.getUserById('test-user-id');

      expect(result).toEqual({
        id: 'test-user-id',
        email: 'test@example.com',
        username: 'testuser',
        firstName: 'Test',
        lastName: 'User',
        role: 'user',
        isActive: true,
        createdAt: expect.any(Date),
        updatedAt: expect.any(Date),
        metadata: {}
      });

      expect(result).not.toHaveProperty('passwordHash');
    });

    it('should return null for non-existent user', async () => {
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockResolvedValue([])
      } as any);

      const result = await authService.getUserById('non-existent-id');

      expect(result).toBeNull();
    });
  });

  describe('updateUser', () => {
    it('should update user with valid data', async () => {
      const updateData = {
        firstName: 'Updated',
        lastName: 'User',
        role: 'manager' as const
      };

      mockDatabase.drizzle.update.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        set: vi.fn().mockReturnThis(),
        returning: vi.fn().mockResolvedValue([{
          id: 'test-user-id',
          email: 'test@example.com',
          username: 'testuser',
          passwordHash: 'hashed-password',
          firstName: 'Updated',
          lastName: 'User',
          role: 'manager',
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date(),
          metadata: {}
        }])
      } as any);

      const result = await authService.updateUser('test-user-id', updateData);

      expect(result).toEqual({
        id: 'test-user-id',
        email: 'test@example.com',
        username: 'testuser',
        firstName: 'Updated',
        lastName: 'User',
        role: 'manager',
        isActive: true,
        createdAt: expect.any(Date),
        updatedAt: expect.any(Date),
        metadata: {}
      });

      expect(mockLogger.info).toHaveBeenCalledWith('User updated successfully', {
        userId: 'test-user-id',
        updates: ['firstName', 'lastName', 'role']
      });
    });

    it('should hash new password when provided', async () => {
      const updateData = {
        password: 'NewPassword123!'
      };

      mockDatabase.drizzle.update.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        set: vi.fn().mockReturnThis(),
        returning: vi.fn().mockResolvedValue([{
          id: 'test-user-id',
          email: 'test@example.com',
          username: 'testuser',
          passwordHash: 'new-hashed-password',
          isActive: true,
          createdAt: new Date(),
          updatedAt: new Date(),
          metadata: {}
        }])
      } as any);

      await authService.updateUser('test-user-id', updateData);

      expect(mockBcrypt.hash).toHaveBeenCalledWith(updateData.password, 12);
    });

    it('should throw error for non-existent user', async () => {
      mockDatabase.drizzle.update.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        set: vi.fn().mockReturnThis(),
        returning: vi.fn().mockResolvedValue([])
      } as any);

      await expect(authService.updateUser('non-existent-id', { firstName: 'Updated' }))
        .rejects.toThrow('User not found');
    });
  });

  describe('verifyAccessToken', () => {
    it('should verify valid access token', async () => {
      const accessToken = 'valid-access-token';

      // Mock session lookup
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([{
          id: 'session-id',
          userId: 'test-user-id',
          tokenHash: 'hashed-access-token',
          expiresAt: new Date(Date.now() + 15 * 60 * 1000),
          isActive: true
        }])
      } as any);

      mockBcrypt.compare.mockResolvedValueOnce(true);

      const result = await authService.verifyAccessToken(accessToken);

      expect(result).toEqual({ userId: 'test-user-id' });
    });

    it('should return null for invalid access token', async () => {
      const accessToken = 'invalid-access-token';

      mockJwt.verify.mockImplementationOnce(() => {
        throw new Error('Invalid token');
      });

      const result = await authService.verifyAccessToken(accessToken);

      expect(result).toBeNull();
    });

    it('should return null for expired session', async () => {
      const accessToken = 'valid-access-token';

      // Mock expired session
      mockDatabase.drizzle.select.mockReturnValueOnce({
        where: vi.fn().mockReturnThis(),
        and: vi.fn().mockResolvedValue([{
          id: 'session-id',
          userId: 'test-user-id',
          tokenHash: 'hashed-access-token',
          expiresAt: new Date(Date.now() - 1000),
          isActive: true
        }])
      } as any);

      mockBcrypt.compare.mockResolvedValueOnce(true);

      const result = await authService.verifyAccessToken(accessToken);

      expect(result).toBeNull();
    });
  });
});