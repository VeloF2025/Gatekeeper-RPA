import {
  pgTable,
  uuid,
  varchar,
  text,
  timestamp,
  jsonb,
  decimal,
  integer,
  boolean,
  primaryKey,
  index,
  unique,
  numeric
} from 'drizzle-orm/pg-core';
import { sql } from 'drizzle-orm';

// Core Tickets table for WhatsApp messages
export const tickets = pgTable('tickets', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  ticketNumber: varchar('ticket_number', { length: 50 }).unique().notNull(),
  drNumber: varchar('dr_number', { length: 50 }).notNull(),
  technicianNumber: varchar('technician_number', { length: 20 }).notNull(),
  technicianName: varchar('technician_name', { length: 100 }),
  messageContent: text('message_content'),
  messageTimestamp: timestamp('message_timestamp', { withTimezone: true }),
  status: varchar('status', { length: 20 }).default('pending').notNull(),
  priority: varchar('priority', { length: 10 }).default('normal').notNull(),
  assignedTo: varchar('assigned_to', { length: 100 }),
  createdAt: timestamp('created_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  updatedAt: timestamp('updated_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  completedAt: timestamp('completed_at', { withTimezone: true }),
  whatsappMessageId: varchar('whatsapp_message_id', { length: 100 }),
  externalTicketId: varchar('external_ticket_id', { length: 50 }),
  externalSystem: varchar('external_system', { length: 20 }),
  syncStatus: varchar('sync_status', { length: 20 }).default('pending').notNull(),
  metadata: jsonb('metadata').default({}),
}, (table) => ({
  ticketNumberIdx: index('tickets_ticket_number_idx').on(table.ticketNumber),
  drNumberIdx: index('tickets_dr_number_idx').on(table.drNumber),
  technicianNumberIdx: index('tickets_technician_number_idx').on(table.technicianNumber),
  statusIdx: index('tickets_status_idx').on(table.status),
  createdAtIdx: index('tickets_created_at_idx').on(table.createdAt),
}));

// Audit results storage
export const auditResults = pgTable('audit_results', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  ticketId: uuid('ticket_id').references(() => tickets.id, { onDelete: 'cascade' }).notNull(),
  propertyId: varchar('property_id', { length: 50 }),
  jobId: varchar('job_id', { length: 50 }),
  address: text('address'),
  installationStatus: varchar('installation_status', { length: 50 }),
  lastModified: timestamp('last_modified', { withTimezone: true }),
  photosRequired: jsonb('photos_required').default([]),
  photosFound: jsonb('photos_found').default([]),
  photosMissing: jsonb('photos_missing').default([]),
  complianceScore: decimal('compliance_score', { precision: 5, scale: 2 }),
  auditDetails: jsonb('audit_details').default({}),
  mlConfidenceScore: decimal('ml_confidence_score', { precision: 5, scale: 2 }),
  createdAt: timestamp('created_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  updatedAt: timestamp('updated_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
}, (table) => ({
  ticketIdIdx: index('audit_results_ticket_id_idx').on(table.ticketId),
  propertyIdIdx: index('audit_results_property_id_idx').on(table.propertyId),
  jobIdIdx: index('audit_results_job_id_idx').on(table.jobId),
  complianceScoreIdx: index('audit_results_compliance_score_idx').on(table.complianceScore),
}));

// Photo metadata and compliance
export const auditPhotos = pgTable('audit_photos', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  auditResultId: uuid('audit_result_id').references(() => auditResults.id, { onDelete: 'cascade' }).notNull(),
  photoUrl: text('photo_url').notNull(),
  photoType: varchar('photo_type', { length: 50 }).notNull(),
  photoCategory: varchar('photo_category', { length: 20 }).notNull(),
  fileSize: integer('file_size'),
  fileFormat: varchar('file_format', { length: 10 }),
  uploadTimestamp: timestamp('upload_timestamp', { withTimezone: true }),
  complianceStatus: varchar('compliance_status', { length: 20 }).default('pending'),
  complianceNotes: text('compliance_notes'),
  metadata: jsonb('metadata').default({}),
  createdAt: timestamp('created_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
}, (table) => ({
  auditResultIdIdx: index('audit_photos_audit_result_id_idx').on(table.auditResultId),
  photoTypeIdx: index('audit_photos_photo_type_idx').on(table.photoType),
  complianceStatusIdx: index('audit_photos_compliance_status_idx').on(table.complianceStatus),
}));

// Ticket assignment tracking
export const ticketAssignments = pgTable('ticket_assignments', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  ticketId: uuid('ticket_id').references(() => tickets.id, { onDelete: 'cascade' }).notNull(),
  assignedTo: varchar('assigned_to', { length: 100 }).notNull(),
  assignedBy: varchar('assigned_by', { length: 100 }).notNull(),
  assignedAt: timestamp('assigned_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  status: varchar('status', { length: 20 }).default('assigned').notNull(),
  notes: text('notes'),
}, (table) => ({
  ticketIdIdx: index('ticket_assignments_ticket_id_idx').on(table.ticketId),
  assignedToIdx: index('ticket_assignments_assigned_to_idx').on(table.assignedTo),
  assignedAtIdx: index('ticket_assignments_assigned_at_idx').on(table.assignedAt),
}));

// Performance metrics
export const ticketMetrics = pgTable('ticket_metrics', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  ticketId: uuid('ticket_id').references(() => tickets.id, { onDelete: 'cascade' }).notNull(),
  metricType: varchar('metric_type', { length: 50 }).notNull(),
  metricValue: decimal('metric_value', { precision: 10, scale: 2 }).notNull(),
  metricUnit: varchar('metric_unit', { length: 20 }),
  recordedAt: timestamp('recorded_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  metadata: jsonb('metadata').default({}),
}, (table) => ({
  ticketIdIdx: index('ticket_metrics_ticket_id_idx').on(table.ticketId),
  metricTypeIdx: index('ticket_metrics_metric_type_idx').on(table.metricType),
  recordedAtIdx: index('ticket_metrics_recorded_at_idx').on(table.recordedAt),
}));

// Workflow events for audit trail
export const workflowEvents = pgTable('workflow_events', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  ticketId: uuid('ticket_id').references(() => tickets.id, { onDelete: 'cascade' }).notNull(),
  eventType: varchar('event_type', { length: 50 }).notNull(),
  eventData: jsonb('event_data').default({}),
  eventTimestamp: timestamp('event_timestamp', { withTimezone: true }).default(sql`NOW()`).notNull(),
  eventSource: varchar('event_source', { length: 50 }).notNull(),
  userId: varchar('user_id', { length: 100 }),
  metadata: jsonb('metadata').default({}),
}, (table) => ({
  ticketIdIdx: index('workflow_events_ticket_id_idx').on(table.ticketId),
  eventTypeIdx: index('workflow_events_event_type_idx').on(table.eventType),
  eventTimestampIdx: index('workflow_events_event_timestamp_idx').on(table.eventTimestamp),
}));

// Users table for authentication
export const users = pgTable('users', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  email: varchar('email', { length: 255 }).unique().notNull(),
  username: varchar('username', { length: 100 }).unique().notNull(),
  passwordHash: varchar('password_hash', { length: 255 }).notNull(),
  firstName: varchar('first_name', { length: 100 }),
  lastName: varchar('last_name', { length: 100 }),
  role: varchar('role', { length: 20 }).default('user').notNull(),
  isActive: boolean('is_active').default(true).notNull(),
  lastLogin: timestamp('last_login', { withTimezone: true }),
  createdAt: timestamp('created_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  updatedAt: timestamp('updated_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  metadata: jsonb('metadata').default({}),
}, (table) => ({
  emailIdx: index('users_email_idx').on(table.email),
  usernameIdx: index('users_username_idx').on(table.username),
  roleIdx: index('users_role_idx').on(table.role),
  isActiveIdx: index('users_is_active_idx').on(table.isActive),
}));

// API Sessions table for JWT token management
export const apiSessions = pgTable('api_sessions', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  userId: uuid('user_id').references(() => users.id, { onDelete: 'cascade' }).notNull(),
  tokenHash: varchar('token_hash', { length: 255 }).unique().notNull(),
  refreshTokenHash: varchar('refresh_token_hash', { length: 255 }),
  expiresAt: timestamp('expires_at', { withTimezone: true }).notNull(),
  isActive: boolean('is_active').default(true).notNull(),
  ipAddress: varchar('ip_address', { length: 45 }),
  userAgent: text('user_agent'),
  createdAt: timestamp('created_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  lastUsed: timestamp('last_used', { withTimezone: true }),
  metadata: jsonb('metadata').default({}),
}, (table) => ({
  userIdIdx: index('api_sessions_user_id_idx').on(table.userId),
  tokenHashIdx: index('api_sessions_token_hash_idx').on(table.tokenHash),
  expiresAtIdx: index('api_sessions_expires_at_idx').on(table.expiresAt),
  isActiveIdx: index('api_sessions_is_active_idx').on(table.isActive),
}));

// Rate limiting table
export const rateLimits = pgTable('rate_limits', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  identifier: varchar('identifier', { length: 255 }).notNull(),
  windowStart: timestamp('window_start', { withTimezone: true }).notNull(),
  windowEnd: timestamp('window_end', { withTimezone: true }).notNull(),
  requestCount: integer('request_count').default(0).notNull(),
  limitType: varchar('limit_type', { length: 50 }).notNull(),
  metadata: jsonb('metadata').default({}),
}, (table) => ({
  identifierWindowIdx: unique('rate_limits_identifier_window_idx').on(table.identifier, table.windowStart),
  identifierTypeIdx: index('rate_limits_identifier_type_idx').on(table.identifier, table.limitType),
  windowEndIdx: index('rate_limits_window_end_idx').on(table.windowEnd),
}));

// RPA Jobs table for automation task management
export const rpaJobs = pgTable('rpa_jobs', {
  id: uuid('id').default(sql`gen_random_uuid()`).primaryKey(),
  ticketId: uuid('ticket_id').references(() => tickets.id, { onDelete: 'cascade' }).notNull(),
  drNumber: varchar('dr_number', { length: 50 }).notNull(),
  status: varchar('status', { length: 20 }).default('pending').notNull(),
  createdAt: timestamp('created_at', { withTimezone: true }).default(sql`NOW()`).notNull(),
  startedAt: timestamp('started_at', { withTimezone: true }),
  completedAt: timestamp('completed_at', { withTimezone: true }),
  retryCount: integer('retry_count').default(0).notNull(),
  error: text('error'),
  result: jsonb('result'),
  metadata: jsonb('metadata').default({}),
}, (table) => ({
  ticketIdIdx: index('rpa_jobs_ticket_id_idx').on(table.ticketId),
  drNumberIdx: index('rpa_jobs_dr_number_idx').on(table.drNumber),
  statusIdx: index('rpa_jobs_status_idx').on(table.status),
  createdAtIdx: index('rpa_jobs_created_at_idx').on(table.createdAt),
  startedAtIdx: index('rpa_jobs_started_at_idx').on(table.startedAt),
  completedAtIdx: index('rpa_jobs_completed_at_idx').on(table.completedAt),
}));

// Export all table types
export type Ticket = typeof tickets.$inferSelect;
export type NewTicket = typeof tickets.$inferInsert;
export type AuditResult = typeof auditResults.$inferSelect;
export type NewAuditResult = typeof auditResults.$inferInsert;
export type AuditPhoto = typeof auditPhotos.$inferSelect;
export type NewAuditPhoto = typeof auditPhotos.$inferInsert;
export type TicketAssignment = typeof ticketAssignments.$inferSelect;
export type NewTicketAssignment = typeof ticketAssignments.$inferInsert;
export type TicketMetric = typeof ticketMetrics.$inferSelect;
export type NewTicketMetric = typeof ticketMetrics.$inferInsert;
export type WorkflowEvent = typeof workflowEvents.$inferSelect;
export type NewWorkflowEvent = typeof workflowEvents.$inferInsert;
export type User = typeof users.$inferSelect;
export type NewUser = typeof users.$inferInsert;
export type ApiSession = typeof apiSessions.$inferSelect;
export type NewApiSession = typeof apiSessions.$inferInsert;
export type RateLimit = typeof rateLimits.$inferSelect;
export type NewRateLimit = typeof rateLimits.$inferInsert;
export type RPAJob = typeof rpaJobs.$inferSelect;
export type NewRPAJob = typeof rpaJobs.$inferInsert;

// Export all tables
export const schema = {
  tickets,
  auditResults,
  auditPhotos,
  ticketAssignments,
  ticketMetrics,
  workflowEvents,
  users,
  apiSessions,
  rateLimits,
  rpaJobs,
};