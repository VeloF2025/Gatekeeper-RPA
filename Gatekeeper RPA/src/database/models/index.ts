import {
  Ticket,
  NewTicket,
  AuditResult,
  NewAuditResult,
  AuditPhoto,
  NewAuditPhoto,
  TicketAssignment,
  NewTicketAssignment,
  TicketMetric,
  NewTicketMetric,
  WorkflowEvent,
  NewWorkflowEvent,
  User,
  NewUser,
  ApiSession,
  NewApiSession,
  RateLimit,
  NewRateLimit
} from '../schemas';

// Extended interfaces with business logic validation
export interface CreateTicketRequest {
  ticketNumber: string;
  drNumber: string;
  technicianNumber: string;
  technicianName?: string;
  messageContent?: string;
  messageTimestamp?: Date;
  priority?: 'low' | 'normal' | 'high' | 'urgent';
  whatsappMessageId?: string;
  externalTicketId?: string;
  externalSystem?: string;
}

export interface UpdateTicketRequest {
  status?: 'pending' | 'assigned' | 'in_progress' | 'completed' | 'failed' | 'cancelled';
  priority?: 'low' | 'normal' | 'high' | 'urgent';
  assignedTo?: string;
  completedAt?: Date;
  syncStatus?: 'pending' | 'synced' | 'failed';
  metadata?: Record<string, unknown>;
}

export interface TicketFilters {
  status?: string | string[];
  priority?: string | string[];
  assignedTo?: string;
  technicianNumber?: string;
  drNumber?: string;
  dateFrom?: Date;
  dateTo?: Date;
  page?: number;
  limit?: number;
  sortBy?: 'createdAt' | 'updatedAt' | 'priority' | 'status';
  sortOrder?: 'asc' | 'desc';
}

export interface CreateAuditResultRequest {
  ticketId: string;
  propertyId?: string;
  jobId?: string;
  address?: string;
  installationStatus?: string;
  photosRequired?: Array<{
    type: string;
    category: string;
    required: boolean;
  }>;
  auditDetails?: Record<string, unknown>;
}

export interface UpdateAuditResultRequest {
  propertyId?: string;
  jobId?: string;
  address?: string;
  installationStatus?: string;
  photosFound?: Array<{
    type: string;
    category: string;
    url: string;
    fileSize?: number;
    fileFormat?: string;
    complianceStatus?: string;
  }>;
  photosMissing?: Array<{
    type: string;
    category: string;
    reason: string;
  }>;
  complianceScore?: number;
  mlConfidenceScore?: number;
  auditDetails?: Record<string, unknown>;
}

export interface CreateAuditPhotoRequest {
  auditResultId: string;
  photoUrl: string;
  photoType: string;
  photoCategory: string;
  fileSize?: number;
  fileFormat?: string;
  complianceStatus?: 'pending' | 'compliant' | 'non_compliant' | 'review_required';
  complianceNotes?: string;
  metadata?: Record<string, unknown>;
}

export interface CreateTicketAssignmentRequest {
  ticketId: string;
  assignedTo: string;
  assignedBy: string;
  status?: 'assigned' | 'accepted' | 'declined' | 'reassigned';
  notes?: string;
}

export interface CreateTicketMetricRequest {
  ticketId: string;
  metricType: string;
  metricValue: number;
  metricUnit?: string;
  metadata?: Record<string, unknown>;
}

export interface CreateWorkflowEventRequest {
  ticketId: string;
  eventType: string;
  eventData: Record<string, unknown>;
  eventSource: string;
  userId?: string;
  metadata?: Record<string, unknown>;
}

export interface CreateUserRequest {
  email: string;
  username: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role?: 'admin' | 'manager' | 'user' | 'auditor';
  metadata?: Record<string, unknown>;
}

export interface UpdateUserRequest {
  email?: string;
  username?: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  role?: 'admin' | 'manager' | 'user' | 'auditor';
  isActive?: boolean;
  metadata?: Record<string, unknown>;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  user: Omit<User, 'passwordHash'>;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface CreateApiSessionRequest {
  userId: string;
  tokenHash: string;
  refreshTokenHash?: string;
  expiresAt: Date;
  ipAddress?: string;
  userAgent?: string;
  metadata?: Record<string, unknown>;
}

// Pagination interface
export interface PaginatedResponse<T> {
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    pages: number;
    hasNext: boolean;
    hasPrev: boolean;
  };
}

// API Response interfaces
export interface ApiResponse<T = unknown> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
  timestamp: Date;
}

export interface ValidationError {
  field: string;
  message: string;
}

export interface ApiErrorResponse {
  success: false;
  error: string;
  message?: string;
  validationErrors?: ValidationError[];
  timestamp: Date;
}

// Business logic types
export type TicketStatus = 'pending' | 'assigned' | 'in_progress' | 'completed' | 'failed' | 'cancelled';
export type TicketPriority = 'low' | 'normal' | 'high' | 'urgent';
export type AuditPhotoType = 'property_exterior' | 'equipment_installed' | 'connection_point' | 'customer_premises' | 'completion_certificate' | 'other';
export type AuditPhotoCategory = 'required' | 'optional' | 'evidence';
export type ComplianceStatus = 'pending' | 'compliant' | 'non_compliant' | 'review_required';
export type UserRole = 'admin' | 'manager' | 'user' | 'auditor';
export type SessionStatus = 'active' | 'expired' | 'revoked';

// Export all types for easy import
export type {
  // Database types from schemas
  Ticket,
  NewTicket,
  AuditResult,
  NewAuditResult,
  AuditPhoto,
  NewAuditPhoto,
  TicketAssignment,
  NewTicketAssignment,
  TicketMetric,
  NewTicketMetric,
  WorkflowEvent,
  NewWorkflowEvent,
  User,
  NewUser,
  ApiSession,
  NewApiSession,
  RateLimit,
  NewRateLimit,
};