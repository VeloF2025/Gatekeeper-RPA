import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { AuthService } from '@/services/auth/auth.service';
import { Database } from '@/database/database';
import { AuthenticationError, ValidationError } from '@/middleware/error-handler';

// Mock Database
const createMockDatabase = () => ({
  query: vi.fn(),
  getClient: vi.fn(),
  transaction: vi.fn(),
});

// Mock logger
vi.mock('@/utils/logger', () => ({
  logger: {
    info: vi.fn(),
    warn: vi.fn(),
    error: vi.fn(),
    debug: vi.fn(),
  },
}));

// Mock bcrypt and jwt
vi.mock('bcryptjs', () => {
  const mockCompare = vi.fn();
  const mockHash = vi.fn();
  return {
    default: {
      compare: mockCompare,
      hash: mockHash,
    },
    compare: mockCompare,
    hash: mockHash,
  };
});

vi.mock('jsonwebtoken', () => {
  const mockSign = vi.fn();
  const mockVerify = vi.fn();
  return {
    default: {
      sign: mockSign,
      verify: mockVerify,
    },
    sign: mockSign,
    verify: mockVerify,
  };
});

describe('AuthService', () => {
  let authService: AuthService;
  let mockDb: Database;
  let bcryptjs: { compare: any; hash: any };
  let jsonwebtoken: { sign: any; verify: any };

  beforeEach(async () => {
    vi.clearAllMocks();
    mockDb = createMockDatabase() as any;
    bcryptjs = await import('bcryptjs');
    jsonwebtoken = await import('jsonwebtoken');
    authService = new AuthService(mockDb);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('login', () => {
    const validCredentials = {
      username: 'testuser',
      password: 'testpassword123',
    };

    const mockUser = {
      id: 'user-id',
      username: 'testuser',
      email: 'test@example.com',
      password: 'hashedpassword',
      role: 'technician',
      isActive: true,
      fullName: 'Test User',
      phoneNumber: '+27821234567',
      lastLogin: null,
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    it('should login successfully with valid credentials', async () => {
      mockDb.query.mockResolvedValueOnce([mockUser]);
      bcryptjs.compare.mockResolvedValueOnce(true);
      bcryptjs.hash.mockResolvedValueOnce('new-hashed-password');
      jsonwebtoken.sign
        .mockReturnValueOnce('access-token')
        .mockReturnValueOnce('refresh-token');

      const result = await authService.login(validCredentials);

      expect(result.user).toEqual({
        id: mockUser.id,
        username: mockUser.username,
        email: mockUser.email,
        role: mockUser.role,
        fullName: mockUser.fullName,
        phoneNumber: mockUser.phoneNumber,
        isActive: mockUser.isActive,
        lastLogin: mockUser.lastLogin,
        createdAt: mockUser.createdAt,
        updatedAt: mockUser.updatedAt,
      });
      expect(result.accessToken).toBe('access-token');
      expect(result.refreshToken).toBe('refresh-token');

      expect(bcryptjs.compare).toHaveBeenCalledWith('testpassword123', 'hashedpassword');
      expect(jsonwebtoken.sign).toHaveBeenCalledTimes(2);
    });

    it('should throw AuthenticationError for invalid username', async () => {
      mockDb.query.mockResolvedValueOnce([]);

      await expect(authService.login(validCredentials))
        .rejects.toThrow(AuthenticationError);
    });

    it('should throw AuthenticationError for invalid password', async () => {
      mockDb.query.mockResolvedValueOnce([mockUser]);
      bcryptjs.compare.mockResolvedValueOnce(false);

      await expect(authService.login(validCredentials))
        .rejects.toThrow(AuthenticationError);
    });

    it('should throw AuthenticationError for inactive user', async () => {
      const inactiveUser = { ...mockUser, isActive: false };
      mockDb.query.mockResolvedValueOnce([inactiveUser]);
      const { compare } = await import('bcryptjs');
      (compare as any).mockResolvedValueOnce(true);

      await expect(authService.login(validCredentials))
        .rejects.toThrow('Account is disabled');
    });

    it('should throw ValidationError for missing credentials', async () => {
      await expect(authService.login({ username: '', password: 'test' }))
        .rejects.toThrow(ValidationError);
    });
  });

  describe('register', () => {
    const validRegistration = {
      username: 'newuser',
      email: 'newuser@example.com',
      password: 'password123',
      role: 'technician' as const,
      fullName: 'New User',
      phoneNumber: '+27821234567',
    };

    it('should register new user successfully', async () => {
      mockDb.query.mockResolvedValueOnce([]); // No existing user
      bcryptjs.hash.mockResolvedValueOnce('hashed-password');
      mockDb.query.mockResolvedValueOnce([{
        id: 'new-user-id',
        ...validRegistration,
        isActive: true,
        lastLogin: null,
        createdAt: new Date(),
        updatedAt: new Date(),
      }]);
      jsonwebtoken.sign
        .mockReturnValueOnce('access-token')
        .mockReturnValueOnce('refresh-token');

      const result = await authService.register(validRegistration);

      expect(result.user.username).toBe(validRegistration.username);
      expect(result.user.email).toBe(validRegistration.email);
      expect(result.accessToken).toBe('access-token');
      expect(result.refreshToken).toBe('refresh-token');
    });

    it('should throw ValidationError for existing username', async () => {
      mockDb.query.mockResolvedValueOnce([{ id: 'existing-user' }]);

      await expect(authService.register(validRegistration))
        .rejects.toThrow('User with this username or email already exists');
    });

    it('should throw ValidationError for invalid email', async () => {
      const invalidData = { ...validRegistration, email: 'invalid-email' };

      await expect(authService.register(invalidData))
        .rejects.toThrow('Valid email address is required');
    });

    it('should throw ValidationError for short password', async () => {
      const invalidData = { ...validRegistration, password: 'short' };

      await expect(authService.register(invalidData))
        .rejects.toThrow('Password must be at least 8 characters long');
    });
  });

  describe('refreshToken', () => {
    it('should refresh tokens successfully', async () => {
      const { verify, sign } = await import('jsonwebtoken');

      const mockPayload = {
        userId: 'user-id',
        username: 'testuser',
        role: 'technician',
        email: 'test@example.com',
      };

      jsonwebtoken.verify.mockReturnValueOnce(mockPayload);
      mockDb.query.mockResolvedValueOnce([{ exists: true }]);
      jsonwebtoken.sign
        .mockReturnValueOnce('new-access-token')
        .mockReturnValueOnce('new-refresh-token');

      const result = await authService.refreshToken('valid-refresh-token');

      expect(result.accessToken).toBe('new-access-token');
      expect(result.refreshToken).toBe('new-refresh-token');
    });

    it('should throw AuthenticationError for invalid refresh token', async () => {
      jsonwebtoken.verify.mockImplementation(() => {
        throw new Error('Invalid token');
      });

      await expect(authService.refreshToken('invalid-token'))
        .rejects.toThrow(AuthenticationError);
    });
  });

  describe('logout', () => {
    it('should logout successfully', async () => {
      mockDb.query.mockResolvedValueOnce({ rowCount: 1 });

      await expect(authService.logout('user-id', 'refresh-token'))
        .resolves.not.toThrow();
    });
  });

  describe('getCurrentUser', () => {
    it('should return current user data', async () => {
      const mockUser = {
        id: 'user-id',
        username: 'testuser',
        email: 'test@example.com',
        password: 'hashedpassword',
        role: 'technician',
      };

      mockDb.query.mockResolvedValueOnce([mockUser]);

      const result = await authService.getCurrentUser('user-id');

      expect(result).toEqual({
        id: mockUser.id,
        username: mockUser.username,
        email: mockUser.email,
        role: mockUser.role,
      });
      expect(result).not.toHaveProperty('password');
    });

    it('should return null for non-existent user', async () => {
      mockDb.query.mockResolvedValueOnce([]);

      const result = await authService.getCurrentUser('non-existent-id');

      expect(result).toBeNull();
    });
  });

  describe('getAllUsers', () => {
    it('should return all users without passwords', async () => {
      const mockUsers = [
        {
          id: 'user-1',
          username: 'user1',
          email: 'user1@example.com',
          password: 'password1',
          role: 'technician',
        },
        {
          id: 'user-2',
          username: 'user2',
          email: 'user2@example.com',
          password: 'password2',
          role: 'admin',
        },
      ];

      mockDb.query.mockResolvedValueOnce(mockUsers);

      const result = await authService.getAllUsers();

      expect(result).toHaveLength(2);
      result.forEach(user => {
        expect(user).not.toHaveProperty('password');
      });
    });
  });

  describe('changePassword', () => {
    it('should change password successfully', async () => {
      const mockUser = {
        id: 'user-id',
        password: 'current-hashed-password',
      };

      mockDb.query.mockResolvedValueOnce([mockUser]);
      bcryptjs.compare.mockResolvedValueOnce(true);
      bcryptjs.hash.mockResolvedValueOnce('new-hashed-password');

      await expect(authService.changePassword('user-id', 'current-password', 'new-password123'))
        .resolves.not.toThrow();
    });

    it('should throw ValidationError for short new password', async () => {
      await expect(authService.changePassword('user-id', 'current-password', 'short'))
        .rejects.toThrow('New password must be at least 8 characters long');
    });

    it('should throw AuthenticationError for incorrect current password', async () => {
      const mockUser = {
        id: 'user-id',
        password: 'current-hashed-password',
      };

      mockDb.query.mockResolvedValueOnce([mockUser]);
      bcryptjs.compare.mockResolvedValueOnce(false);

      await expect(authService.changePassword('user-id', 'wrong-password', 'new-password123'))
        .rejects.toThrow('Current password is incorrect');
    });
  });
});