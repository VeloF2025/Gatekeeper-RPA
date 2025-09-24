import { z } from 'zod';

// Common validation patterns
const uuidSchema = z.string().uuid();
const emailSchema = z.string().email();
const phoneSchema = z.string().regex(/^\+?[1-9]\d{1,14}$/);
const urlSchema = z.string().url();

// Ticket validation schemas
export const createTicketSchema = z.object({
  ticketNumber: z.string().min(1).max(50).optional(),
  drNumber: z.string().min(1).max(50),
  technicianNumber: phoneSchema,
  technicianName: z.string().max(100).optional(),
  messageContent: z.string().max(5000).optional(),
  messageTimestamp: z.date().optional(),
  priority: z.enum(['low', 'normal', 'high', 'urgent']).optional().default('normal'),
  whatsappMessageId: z.string().max(100).optional(),
  externalTicketId: z.string().max(50).optional(),
  externalSystem: z.string().max(20).optional(),
});

export const updateTicketSchema = z.object({
  status: z.enum(['pending', 'assigned', 'in_progress', 'completed', 'failed', 'cancelled']).optional(),
  priority: z.enum(['low', 'normal', 'high', 'urgent']).optional(),
  assignedTo: z.string().max(100).optional(),
  completedAt: z.date().optional(),
  syncStatus: z.enum(['pending', 'synced', 'failed']).optional(),
  metadata: z.record(z.unknown()).optional(),
});

export const ticketFiltersSchema = z.object({
  status: z.union([z.string(), z.array(z.string())]).optional(),
  priority: z.union([z.string(), z.array(z.string())]).optional(),
  assignedTo: z.string().max(100).optional(),
  technicianNumber: phoneSchema.optional(),
  drNumber: z.string().max(50).optional(),
  dateFrom: z.date().optional(),
  dateTo: z.date().optional(),
  page: z.number().int().positive().optional().default(1),
  limit: z.number().int().positive().max(100).optional().default(50),
  sortBy: z.enum(['createdAt', 'updatedAt', 'priority', 'status']).optional().default('createdAt'),
  sortOrder: z.enum(['asc', 'desc']).optional().default('desc'),
});

// Audit result validation schemas
export const createAuditResultSchema = z.object({
  ticketId: uuidSchema,
  propertyId: z.string().max(50).optional(),
  jobId: z.string().max(50).optional(),
  address: z.string().max(500).optional(),
  installationStatus: z.string().max(50).optional(),
  photosRequired: z.array(z.object({
    type: z.string().min(1),
    category: z.string().min(1),
    required: z.boolean(),
  })).optional(),
  auditDetails: z.record(z.unknown()).optional(),
});

export const updateAuditResultSchema = z.object({
  propertyId: z.string().max(50).optional(),
  jobId: z.string().max(50).optional(),
  address: z.string().max(500).optional(),
  installationStatus: z.string().max(50).optional(),
  photosFound: z.array(z.object({
    type: z.string().min(1),
    category: z.string().min(1),
    url: urlSchema,
    fileSize: z.number().positive().optional(),
    fileFormat: z.string().max(10).optional(),
    complianceStatus: z.enum(['pending', 'compliant', 'non_compliant', 'review_required']).optional(),
  })).optional(),
  photosMissing: z.array(z.object({
    type: z.string().min(1),
    category: z.string().min(1),
    reason: z.string().min(1),
  })).optional(),
  complianceScore: z.number().min(0).max(100).optional(),
  mlConfidenceScore: z.number().min(0).max(1).optional(),
  auditDetails: z.record(z.unknown()).optional(),
});

// Audit photo validation schemas
export const createAuditPhotoSchema = z.object({
  auditResultId: uuidSchema,
  photoUrl: urlSchema,
  photoType: z.string().min(1).max(50),
  photoCategory: z.enum(['required', 'optional', 'evidence']),
  fileSize: z.number().positive().optional(),
  fileFormat: z.string().max(10).optional(),
  complianceStatus: z.enum(['pending', 'compliant', 'non_compliant', 'review_required']).optional(),
  complianceNotes: z.string().max(1000).optional(),
  metadata: z.record(z.unknown()).optional(),
});

// User validation schemas
export const createUserSchema = z.object({
  email: emailSchema,
  username: z.string().min(3).max(100).regex(/^[a-zA-Z0-9_]+$/),
  password: z.string().min(8).max(100),
  firstName: z.string().max(100).optional(),
  lastName: z.string().max(100).optional(),
  role: z.enum(['admin', 'manager', 'user', 'auditor']).optional().default('user'),
  metadata: z.record(z.unknown()).optional(),
});

export const updateUserSchema = z.object({
  email: emailSchema.optional(),
  username: z.string().min(3).max(100).regex(/^[a-zA-Z0-9_]+$/).optional(),
  password: z.string().min(8).max(100).optional(),
  firstName: z.string().max(100).optional(),
  lastName: z.string().max(100).optional(),
  role: z.enum(['admin', 'manager', 'user', 'auditor']).optional(),
  isActive: z.boolean().optional(),
  metadata: z.record(z.unknown()).optional(),
});

export const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1),
});

export const refreshTokenSchema = z.object({
  refreshToken: z.string().min(1),
});

// Ticket assignment validation schemas
export const createTicketAssignmentSchema = z.object({
  ticketId: uuidSchema,
  assignedTo: z.string().min(1).max(100),
  assignedBy: z.string().min(1).max(100),
  status: z.enum(['assigned', 'accepted', 'declined', 'reassigned']).optional().default('assigned'),
  notes: z.string().max(2000).optional(),
});

// Ticket metrics validation schemas
export const createTicketMetricSchema = z.object({
  ticketId: uuidSchema,
  metricType: z.string().min(1).max(50),
  metricValue: z.number(),
  metricUnit: z.string().max(20).optional(),
  metadata: z.record(z.unknown()).optional(),
});

// Workflow event validation schemas
export const createWorkflowEventSchema = z.object({
  ticketId: uuidSchema,
  eventType: z.string().min(1).max(50),
  eventData: z.record(z.unknown()),
  eventSource: z.string().min(1).max(50),
  userId: z.string().max(100).optional(),
  metadata: z.record(z.unknown()).optional(),
});

// API session validation schemas
export const createApiSessionSchema = z.object({
  userId: uuidSchema,
  tokenHash: z.string().min(1),
  refreshTokenHash: z.string().min(1).optional(),
  expiresAt: z.date(),
  ipAddress: z.string().ip().optional(),
  userAgent: z.string().max(500).optional(),
  metadata: z.record(z.unknown()).optional(),
});

// Rate limit validation schemas
export const createRateLimitSchema = z.object({
  identifier: z.string().min(1).max(255),
  windowStart: z.date(),
  windowEnd: z.date(),
  requestCount: z.number().int().min(0),
  limitType: z.string().min(1).max(50),
  metadata: z.record(z.unknown()).optional(),
});

// Response validation schemas
export const apiResponseSchema = <T>(dataSchema: z.ZodType<T>) => z.object({
  success: z.literal(true),
  data: dataSchema,
  message: z.string().optional(),
  timestamp: z.date(),
});

export const apiErrorResponseSchema = z.object({
  success: z.literal(false),
  error: z.string(),
  message: z.string().optional(),
  validationErrors: z.array(z.object({
    field: z.string(),
    message: z.string(),
  })).optional(),
  timestamp: z.date(),
});

// Pagination validation schemas
export const paginationSchema = z.object({
  page: z.number().int().positive(),
  limit: z.number().int().positive().max(100),
  total: z.number().int().nonnegative(),
  pages: z.number().int().nonnegative(),
  hasNext: z.boolean(),
  hasPrev: z.boolean(),
});

export const paginatedResponseSchema = <T>(dataSchema: z.ZodType<T>) => z.object({
  data: z.array(dataSchema),
  pagination: paginationSchema,
});

// Export all schemas
export const schemas = {
  createTicket: createTicketSchema,
  updateTicket: updateTicketSchema,
  ticketFilters: ticketFiltersSchema,
  createAuditResult: createAuditResultSchema,
  updateAuditResult: updateAuditResultSchema,
  createAuditPhoto: createAuditPhotoSchema,
  createUser: createUserSchema,
  updateUser: updateUserSchema,
  login: loginSchema,
  refreshToken: refreshTokenSchema,
  createTicketAssignment: createTicketAssignmentSchema,
  createTicketMetric: createTicketMetricSchema,
  createWorkflowEvent: createWorkflowEventSchema,
  createApiSession: createApiSessionSchema,
  createRateLimit: createRateLimitSchema,
  apiResponse: apiResponseSchema,
  apiErrorResponse: apiErrorResponseSchema,
  pagination: paginationSchema,
  paginatedResponse: paginatedResponseSchema,
};

// Export types inferred from schemas
export type CreateTicketRequest = z.infer<typeof createTicketSchema>;
export type UpdateTicketRequest = z.infer<typeof updateTicketSchema>;
export type TicketFilters = z.infer<typeof ticketFiltersSchema>;
export type CreateAuditResultRequest = z.infer<typeof createAuditResultSchema>;
export type UpdateAuditResultRequest = z.infer<typeof updateAuditResultSchema>;
export type CreateAuditPhotoRequest = z.infer<typeof createAuditPhotoSchema>;
export type CreateUserRequest = z.infer<typeof createUserSchema>;
export type UpdateUserRequest = z.infer<typeof updateUserSchema>;
export type LoginRequest = z.infer<typeof loginSchema>;
export type RefreshTokenRequest = z.infer<typeof refreshTokenSchema>;
export type CreateTicketAssignmentRequest = z.infer<typeof createTicketAssignmentSchema>;
export type CreateTicketMetricRequest = z.infer<typeof createTicketMetricSchema>;
export type CreateWorkflowEventRequest = z.infer<typeof createWorkflowEventSchema>;
export type CreateApiSessionRequest = z.infer<typeof createApiSessionSchema>;
export type CreateRateLimitRequest = z.infer<typeof createRateLimitSchema>;
export type ApiResponse<T = unknown> = z.infer<ReturnType<typeof apiResponseSchema<unknown>>>;
export type ApiErrorResponse = z.infer<typeof apiErrorResponseSchema>;
export type Pagination = z.infer<typeof paginationSchema>;
export type PaginatedResponse<T = unknown> = z.infer<ReturnType<typeof paginatedResponseSchema<unknown>>>;