// Core Types for Gatekeeper RPA System

export interface User {
  id: string;
  username: string;
  email: string;
  password: string;
  fullName: string;
  phoneNumber?: string;
  role: UserRole;
  isActive: boolean;
  lastLogin?: Date;
  createdAt: Date;
  updatedAt: Date;
}

export interface WhatiTicketConfig {
  version: string;
  database: string;
  redis: boolean;
  security: {
    encryption: string;
    authentication: string;
  };
}

export interface WhatiTicketMessage {
  id: string;
  from: string;
  content: string;
  drNumber?: string;
  timestamp: number;
  messageType: 'text' | 'image' | 'document' | 'audio' | 'video';
  media?: MediaItem[];
}

export interface MediaItem {
  id: string;
  type: string;
  mimeType: string;
  fileSize: number;
  url?: string;
  caption?: string;
}

export interface Ticket {
  id: string;
  ticketNumber: string;
  drNumber: string;
  technicianNumber: string;
  technicianName: string;
  messageContent: string;
  messageTimestamp: Date;
  status: TicketStatus;
  priority: Priority;
  assignedTo?: string;
  propertyId?: string;
  jobId?: string;
  address?: string;
  installationStatus?: string;
  photos?: Photo[];
  complianceScore?: number;
  completedAt?: Date;
  createdAt: Date;
  updatedAt: Date;
}

export interface Photo {
  id: string;
  ticketId: string;
  type: PhotoType;
  url: string;
  uploadTime: Date;
  fileSize: number;
  metadata?: Record<string, unknown>;
}

export interface AuditResult {
  id: string;
  ticketId: string;
  propertyId: string;
  jobId: string;
  address: string;
  installationStatus: string;
  photosRequired: PhotoType[];
  photosFound: PhotoType[];
  photosMissing: PhotoType[];
  complianceScore: number;
  auditDetails: AuditDetails;
  createdAt: Date;
}

export interface AuditDetails {
  totalPhotos: number;
  auditTimestamp: string;
  photos: Photo[];
  compliance: ComplianceCheck[];
}

export interface ComplianceCheck {
  type: string;
  passed: boolean;
  message: string;
  score: number;
}

export interface WhatsAppMessage {
  id: string;
  from: string;
  to: string;
  body: string;
  type: MessageType;
  timestamp: Date;
  status: MessageStatus;
  ticketId?: string;
}

export interface RateLimitConfig {
  points: number;
  duration: number;
}

export interface SecurityConfig {
  jwtSecret: string;
  jwtExpiresIn: string;
  bcryptRounds: number;
  enableHelmet: boolean;
  enableCORS: boolean;
  enableRateLimit: boolean;
  enableCSRF: boolean;
}

export interface DatabaseConfig {
  url: string;
  ssl: boolean;
  pool: {
    min: number;
    max: number;
    idle: number;
  };
}

export interface RedisConfig {
  url: string;
  password?: string;
  db: number;
}

export interface WhatsAppConfig {
  apiKey: string;
  baseUrl: string;
  webhookSecret: string;
}

export interface OneMapConfig {
  baseUrl: string;
  username: string;
  password: string;
  timeout: number;
  retries: number;
}

export interface AppConfig {
  nodeEnv: string;
  port: number;
  apiVersion: string;
  database: DatabaseConfig;
  redis: RedisConfig;
  security: SecurityConfig;
  whatsapp: WhatsAppConfig;
  onemap: OneMapConfig;
  rateLimit: RateLimitConfig;
  upload: {
    maxFileSize: number;
    path: string;
  };
  logging: {
    level: string;
    file: string;
  };
  monitoring: {
    enabled: boolean;
    port: number;
  };

  whatiTicket?: {
    version: string;
    database: string;
    redis: boolean;
    encryptionKey: string;
    jwtSecret: string;
    webhookUrl: string;
    verifyToken: string;
  };
}

export interface ValidationResult {
  isValid: boolean;
  errors: string[];
  warnings: string[];
}

export interface AuditContext {
  ticketId: string;
  userId: string;
  startTime: Date;
  endTime?: Date;
  steps: AuditStep[];
  screenshots: string[];
  errors: AuditError[];
}

export interface AuditStep {
  name: string;
  status: 'pending' | 'in_progress' | 'completed' | 'failed';
  startTime: Date;
  endTime?: Date;
  result?: unknown;
  error?: string;
}

export interface AuditError {
  step: string;
  message: string;
  timestamp: Date;
  severity: 'low' | 'medium' | 'high' | 'critical';
  details?: Record<string, unknown>;
}

export interface DGTSValidation {
  hasGaming: boolean;
  violations: DGTSViolation[];
  gamingScore: number;
  recommendations: string[];
}

export interface DGTSViolation {
  type: string;
  severity: 'low' | 'medium' | 'high' | 'critical';
  file: string;
  line?: number;
  message: string;
  suggestion: string;
}

// Enums
export enum UserRole {
  ADMIN = 'admin',
  AUDITOR = 'auditor',
  TECHNICIAN = 'technician',
  VIEWER = 'viewer'
}

export enum TicketStatus {
  PENDING = 'pending',
  ASSIGNED = 'assigned',
  IN_PROGRESS = 'in_progress',
  COMPLETED = 'completed',
  FAILED = 'failed',
  CANCELLED = 'cancelled'
}

export enum Priority {
  LOW = 'low',
  NORMAL = 'normal',
  HIGH = 'high',
  URGENT = 'urgent'
}

export enum PhotoType {
  TRENCH = 'trench',
  ONT = 'ONT',
  TERMINATION = 'termination',
  PROPERTY = 'property',
  ADDITIONAL = 'additional'
}

export enum MessageType {
  TEXT = 'text',
  IMAGE = 'image',
  DOCUMENT = 'document',
  LOCATION = 'location'
}

export enum MessageStatus {
  PENDING = 'pending',
  SENT = 'sent',
  DELIVERED = 'delivered',
  READ = 'read',
  FAILED = 'failed'
}