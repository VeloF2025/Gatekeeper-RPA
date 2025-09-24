import { describe, it, expect } from 'vitest';
import { z } from 'zod';
import {
  createTicketSchema,
  updateTicketSchema,
  ticketFiltersSchema,
  createAuditResultSchema,
  updateAuditResultSchema,
  createAuditPhotoSchema,
  createUserSchema,
  updateUserSchema,
  loginSchema,
  refreshTokenSchema,
  createTicketAssignmentSchema,
  createTicketMetricSchema,
  createWorkflowEventSchema,
  createApiSessionSchema,
  createRateLimitSchema,
  apiResponseSchema,
  apiErrorResponseSchema,
  paginationSchema,
  paginatedResponseSchema,
  schemas,
  type CreateTicketRequest,
  type UpdateTicketRequest,
  type LoginRequest,
  type CreateUserRequest,
  type ApiResponse,
  type ApiErrorResponse,
  type Pagination
} from '@/validation/schemas';

describe('Validation Schemas', () => {
  describe('createTicketSchema', () => {
    it('should validate valid ticket data', () => {
      const validData = {
        drNumber: 'DR123',
        technicianNumber: '+27820001111',
        technicianName: 'John Doe',
        messageContent: 'Installation complete',
        priority: 'high' as const
      };

      const result = createTicketSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should validate with custom ticket number', () => {
      const validData = {
        ticketNumber: 'TCK-001',
        drNumber: 'DR123',
        technicianNumber: '+27820001111',
        priority: 'normal' as const
      };

      const result = createTicketSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should reject invalid phone number', () => {
      const invalidData = {
        drNumber: 'DR123',
        technicianNumber: 'invalid-phone',
        priority: 'normal' as const
      };

      const result = createTicketSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject missing drNumber', () => {
      const invalidData = {
        technicianNumber: '+27820001111',
        priority: 'normal' as const
      };

      const result = createTicketSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject invalid priority', () => {
      const invalidData = {
        drNumber: 'DR123',
        technicianNumber: '+27820001111',
        priority: 'invalid' as const
      };

      const result = createTicketSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should apply default priority when not provided', () => {
      const data = {
        drNumber: 'DR123',
        technicianNumber: '+27820001111'
      };

      const result = createTicketSchema.safeParse(data);
      expect(result.success).toBe(true);
      if (result.success) {
        expect(result.data.priority).toBe('normal');
      }
    });
  });

  describe('updateTicketSchema', () => {
    it('should validate valid update data', () => {
      const validData = {
        status: 'completed' as const,
        priority: 'high' as const,
        assignedTo: 'user123',
        syncStatus: 'synced' as const
      };

      const result = updateTicketSchema.safeParse(validData);
      expect(result.success).toBe(true);
    });

    it('should validate empty update object', () => {
      const result = updateTicketSchema.safeParse({});
      expect(result.success).toBe(true);
    });

    it('should reject invalid status', () => {
      const invalidData = {
        status: 'invalid' as const
      };

      const result = updateTicketSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it('should reject invalid metadata structure', () => {
      const invalidData = {
        metadata: 'invalid-metadata'
      };

      const result = updateTicketSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe('ticketFiltersSchema', () => {
    it('should validate valid filters', () => {
      const validFilters = {
        status: ['pending', 'in_progress'],
        priority: 'high',
        assignedTo: 'user123',
        page: 2,
        limit: 25,
        sortBy: 'createdAt' as const,
        sortOrder: 'asc' as const
      };

      const result = ticketFiltersSchema.safeParse(validFilters);
      expect(result.success).toBe(true);
    });

    it('should apply default values when not provided', () => {
      const data = {};

      const result = ticketFiltersSchema.safeParse(data);
      expect(result.success).toBe(true);
      if (result.success) {
        expect(result.data.page).toBe(1);
        expect(result.data.limit).toBe(50);
        expect(result.data.sortBy).toBe('createdAt');
        expect(result.data.sortOrder).toBe('desc');
      }
    });

    it('should validate date range filters', () => {
      const validFilters = {
        dateFrom: new Date('2024-01-01'),
        dateTo: new Date('2024-12-31'),
        limit: 10
      };

      const result = ticketFiltersSchema.safeParse(validFilters);
      expect(result.success).toBe(true);
    });

    it('should reject invalid page number', () => {
      const invalidFilters = {
        page: -1
      };

      const result = ticketFiltersSchema.safeParse(invalidFilters);
      expect(result.success).toBe(false);
    });

    it('should reject limit over maximum', () => {
      const invalidFilters = {
        limit: 101
      };

      const result = ticketFiltersSchema.safeParse(invalidFilters);
      expect(result.success).toBe(false);
    });
  });

  describe('createUserSchema', () => {
    it('should validate valid user data', () => {
      const validUser = {
        email: 'user@example.com',
        username: 'testuser',
        password: 'Password123!',
        firstName: 'Test',
        lastName: 'User',
        role: 'admin' as const,
        metadata: { department: 'IT' }
      };

      const result = createUserSchema.safeParse(validUser);
      expect(result.success).toBe(true);
    });

    it('should apply default role when not provided', () => {
      const data = {
        email: 'user@example.com',
        username: 'testuser',
        password: 'Password123!'
      };

      const result = createUserSchema.safeParse(data);
      expect(result.success).toBe(true);
      if (result.success) {
        expect(result.data.role).toBe('user');
      }
    });

    it('should reject invalid email', () => {
      const invalidUser = {
        email: 'invalid-email',
        username: 'testuser',
        password: 'Password123!'
      };

      const result = createUserSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);
    });

    it('should reject invalid username with special characters', () => {
      const invalidUser = {
        email: 'user@example.com',
        username: 'test-user@',
        password: 'Password123!'
      };

      const result = createUserSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);
    });

    it('should reject short password', () => {
      const invalidUser = {
        email: 'user@example.com',
        username: 'testuser',
        password: 'short'
      };

      const result = createUserSchema.safeParse(invalidUser);
      expect(result.success).toBe(false);
    });
  });

  describe('loginSchema', () => {
    it('should validate valid login data', () => {
      const validLogin = {
        email: 'user@example.com',
        password: 'Password123!'
      };

      const result = loginSchema.safeParse(validLogin);
      expect(result.success).toBe(true);
    });

    it('should reject missing email', () => {
      const invalidLogin = {
        password: 'Password123!'
      };

      const result = loginSchema.safeParse(invalidLogin);
      expect(result.success).toBe(false);
    });

    it('should reject missing password', () => {
      const invalidLogin = {
        email: 'user@example.com'
      };

      const result = loginSchema.safeParse(invalidLogin);
      expect(result.success).toBe(false);
    });
  });

  describe('createAuditResultSchema', () => {
    it('should validate valid audit result data', () => {
      const validAudit = {
        ticketId: '550e8400-e29b-41d4-a716-446655440000',
        propertyId: 'PROP001',
        jobId: 'JOB001',
        address: '123 Main St',
        installationStatus: 'completed',
        photosRequired: [
          { type: 'property_exterior', category: 'required', required: true },
          { type: 'equipment_installed', category: 'required', required: true }
        ]
      };

      const result = createAuditResultSchema.safeParse(validAudit);
      expect(result.success).toBe(true);
    });

    it('should reject invalid ticket ID', () => {
      const invalidAudit = {
        ticketId: 'invalid-uuid',
        propertyId: 'PROP001'
      };

      const result = createAuditResultSchema.safeParse(invalidAudit);
      expect(result.success).toBe(false);
    });

    it('should reject invalid photos required structure', () => {
      const invalidAudit = {
        ticketId: '550e8400-e29b-41d4-a716-446655440000',
        photosRequired: 'invalid'
      };

      const result = createAuditResultSchema.safeParse(invalidAudit);
      expect(result.success).toBe(false);
    });
  });

  describe('createAuditPhotoSchema', () => {
    it('should validate valid audit photo data', () => {
      const validPhoto = {
        auditResultId: '550e8400-e29b-41d4-a716-446655440000',
        photoUrl: 'https://example.com/photo.jpg',
        photoType: 'property_exterior',
        photoCategory: 'required' as const,
        fileSize: 2048576,
        fileFormat: 'jpg',
        complianceStatus: 'compliant' as const,
        complianceNotes: 'Good quality photo'
      };

      const result = createAuditPhotoSchema.safeParse(validPhoto);
      expect(result.success).toBe(true);
    });

    it('should reject invalid photo URL', () => {
      const invalidPhoto = {
        auditResultId: '550e8400-e29b-41d4-a716-446655440000',
        photoUrl: 'invalid-url',
        photoType: 'property_exterior',
        photoCategory: 'required' as const
      };

      const result = createAuditPhotoSchema.safeParse(invalidPhoto);
      expect(result.success).toBe(false);
    });

    it('should reject invalid photo category', () => {
      const invalidPhoto = {
        auditResultId: '550e8400-e29b-41d4-a716-446655440000',
        photoUrl: 'https://example.com/photo.jpg',
        photoType: 'property_exterior',
        photoCategory: 'invalid' as const
      };

      const result = createAuditPhotoSchema.safeParse(invalidPhoto);
      expect(result.success).toBe(false);
    });
  });

  describe('createTicketAssignmentSchema', () => {
    it('should validate valid assignment data', () => {
      const validAssignment = {
        ticketId: '550e8400-e29b-41d4-a716-446655440000',
        assignedTo: 'user123',
        assignedBy: 'admin',
        status: 'assigned' as const,
        notes: 'Please review this ticket'
      };

      const result = createTicketAssignmentSchema.safeParse(validAssignment);
      expect(result.success).toBe(true);
    });

    it('should apply default status when not provided', () => {
      const data = {
        ticketId: '550e8400-e29b-41d4-a716-446655440000',
        assignedTo: 'user123',
        assignedBy: 'admin'
      };

      const result = createTicketAssignmentSchema.safeParse(data);
      expect(result.success).toBe(true);
      if (result.success) {
        expect(result.data.status).toBe('assigned');
      }
    });
  });

  describe('apiResponseSchema and apiErrorResponseSchema', () => {
    it('should validate successful API response', () => {
      const successResponse = {
        success: true,
        data: { id: '123', name: 'Test' },
        message: 'Operation successful',
        timestamp: new Date()
      };

      const result = apiResponseSchema(z.object({ id: z.string(), name: z.string() })).safeParse(successResponse);
      expect(result.success).toBe(true);
    });

    it('should validate error API response', () => {
      const errorResponse = {
        success: false,
        error: 'Validation failed',
        message: 'Invalid input data',
        validationErrors: [
          { field: 'email', message: 'Invalid email format' },
          { field: 'password', message: 'Password too short' }
        ],
        timestamp: new Date()
      };

      const result = apiErrorResponseSchema.safeParse(errorResponse);
      expect(result.success).toBe(true);
    });

    it('should reject API response with success=false but data', () => {
      const invalidResponse = {
        success: false,
        data: { id: '123' },
        timestamp: new Date()
      };

      const result = apiResponseSchema(z.object({ id: z.string(), name: z.string() })).safeParse(invalidResponse);
      expect(result.success).toBe(false);
    });
  });

  describe('paginationSchema and paginatedResponseSchema', () => {
    it('should validate pagination data', () => {
      const pagination = {
        page: 1,
        limit: 50,
        total: 100,
        pages: 2,
        hasNext: true,
        hasPrev: false
      };

      const result = paginationSchema.safeParse(pagination);
      expect(result.success).toBe(true);
    });

    it('should validate paginated response', () => {
      const paginatedResponse = {
        data: [
          { id: '1', name: 'Item 1' },
          { id: '2', name: 'Item 2' }
        ],
        pagination: {
          page: 1,
          limit: 50,
          total: 100,
          pages: 2,
          hasNext: true,
          hasPrev: false
        }
      };

      const result = paginatedResponseSchema(z.object({ id: z.string(), name: z.string() })).safeParse(paginatedResponse);
      expect(result.success).toBe(true);
    });

    it('should reject invalid pagination data', () => {
      const invalidPagination = {
        page: -1,
        limit: 101,
        total: -1,
        pages: 0,
        hasNext: 'invalid',
        hasPrev: false
      };

      const result = paginationSchema.safeParse(invalidPagination);
      expect(result.success).toBe(false);
    });
  });

  describe('Type inference', () => {
    it('should correctly infer types from schemas', () => {
      // These are type compilation tests
      const createTicketData: CreateTicketRequest = {
        drNumber: 'DR123',
        technicianNumber: '+27820001111'
      };

      const updateTicketData: UpdateTicketRequest = {
        status: 'completed',
        priority: 'high'
      };

      const loginData: LoginRequest = {
        email: 'user@example.com',
        password: 'Password123!'
      };

      const createUser: CreateUserRequest = {
        email: 'user@example.com',
        username: 'testuser',
        password: 'Password123!'
      };

      const successResponse: ApiResponse<{ id: string }> = {
        success: true,
        data: { id: '123' },
        timestamp: new Date()
      };

      const errorResponse: ApiErrorResponse = {
        success: false,
        error: 'Validation failed',
        timestamp: new Date()
      };

      expect(createTicketData).toBeDefined();
      expect(updateTicketData).toBeDefined();
      expect(loginData).toBeDefined();
      expect(createUser).toBeDefined();
      expect(successResponse).toBeDefined();
      expect(errorResponse).toBeDefined();
    });
  });

  describe('Schema exports', () => {
    it('should export all required schemas', () => {
      expect(schemas.createTicket).toBeDefined();
      expect(schemas.updateTicket).toBeDefined();
      expect(schemas.ticketFilters).toBeDefined();
      expect(schemas.createAuditResult).toBeDefined();
      expect(schemas.updateAuditResult).toBeDefined();
      expect(schemas.createAuditPhoto).toBeDefined();
      expect(schemas.createUser).toBeDefined();
      expect(schemas.updateUser).toBeDefined();
      expect(schemas.login).toBeDefined();
      expect(schemas.refreshToken).toBeDefined();
      expect(schemas.createTicketAssignment).toBeDefined();
      expect(schemas.createTicketMetric).toBeDefined();
      expect(schemas.createWorkflowEvent).toBeDefined();
      expect(schemas.createApiSession).toBeDefined();
      expect(schemas.createRateLimit).toBeDefined();
      expect(schemas.apiResponse).toBeDefined();
      expect(schemas.apiErrorResponse).toBeDefined();
      expect(schemas.pagination).toBeDefined();
      expect(schemas.paginatedResponse).toBeDefined();
    });
  });

  describe('Edge cases and complex validation', () => {
    it('should handle complex metadata structures', () => {
      const complexMetadata = {
        nested: { value: 123 },
        array: [1, 2, 3],
        boolean: true,
        nullValue: null,
        date: new Date()
      };

      const userWithComplexMetadata = {
        email: 'user@example.com',
        username: 'testuser',
        password: 'Password123!',
        metadata: complexMetadata
      };

      const result = createUserSchema.safeParse(userWithComplexMetadata);
      expect(result.success).toBe(true);
    });

    it('should validate phone numbers with different formats', () => {
      const validPhones = [
        '+27820001111',
        '+1234567890',
        '27820001111', // without +
        '+442071838750'
      ];

      validPhones.forEach(phone => {
        const data = {
          drNumber: 'DR123',
          technicianNumber: phone
        };

        const result = createTicketSchema.safeParse(data);
        expect(result.success).toBe(true);
      });
    });

    it('should reject invalid UUID formats', () => {
      const invalidUuids = [
        'invalid-uuid',
        '550e8400-e29b-41d4-a716',
        '550e8400-e29b-41d4-a716-446655440000-extra',
        1234567890
      ];

      invalidUuids.forEach(uuid => {
        const data = {
          ticketId: uuid,
          assignedTo: 'user123',
          assignedBy: 'admin'
        };

        const result = createTicketAssignmentSchema.safeParse(data);
        expect(result.success).toBe(false);
      });
    });

    it('should handle optional fields in update schemas', () => {
      const minimalUpdate = {};
      const result = updateTicketSchema.safeParse(minimalUpdate);
      expect(result.success).toBe(true);
    });

    it('should validate nested array structures in audit data', () => {
      const complexAuditUpdate = {
        photosFound: [
          {
            type: 'property_exterior',
            category: 'required',
            url: 'https://example.com/photo1.jpg',
            fileSize: 2048576,
            fileFormat: 'jpg',
            complianceStatus: 'compliant' as const
          },
          {
            type: 'equipment_installed',
            category: 'required',
            url: 'https://example.com/photo2.jpg',
            complianceStatus: 'non_compliant' as const
          }
        ],
        photosMissing: [
          {
            type: 'connection_point',
            category: 'required',
            reason: 'Not provided by technician'
          }
        ]
      };

      const result = updateAuditResultSchema.safeParse(complexAuditUpdate);
      expect(result.success).toBe(true);
    });
  });
});