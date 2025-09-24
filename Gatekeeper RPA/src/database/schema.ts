import { pgTable, text, timestamp, uuid, pgEnum, integer, boolean, numeric, jsonb } from 'drizzle-orm/pg-core';

// Enums
export const userRoleEnum = pgEnum('user_role', ['admin', 'auditor', 'technician', 'viewer']);
export const ticketStatusEnum = pgEnum('ticket_status', ['pending', 'assigned', 'in_progress', 'completed', 'failed', 'cancelled']);
export const priorityEnum = pgEnum('priority', ['low', 'normal', 'high', 'urgent']);
export const photoTypeEnum = pgEnum('photo_type', ['trench', 'ONT', 'termination', 'property', 'additional']);
export const messageTypeEnum = pgEnum('message_type', ['text', 'image', 'document', 'location']);
export const messageStatusEnum = pgEnum('message_status', ['pending', 'sent', 'delivered', 'read', 'failed']);
export const assignmentStatusEnum = pgEnum('assignment_status', ['assigned', 'reassigned', 'unassigned']);

// Users table
export const users = pgTable('users', {
  id: uuid('id').primaryKey().defaultRandom(),
  username: text('username').notNull().unique(),
  email: text('email').notNull().unique(),
  password: text('password').notNull(),
  full_name: text('full_name').notNull(),
  phone_number: text('phone_number'),
  role: userRoleEnum('role').notNull().default('technician'),
  is_active: boolean('is_active').notNull().default(true),
  last_login: timestamp('last_login'),
  created_at: timestamp('created_at').notNull().defaultNow(),
  updated_at: timestamp('updated_at').notNull().defaultNow()
});

// Refresh tokens table
export const refresh_tokens = pgTable('refresh_tokens', {
  id: uuid('id').primaryKey().defaultRandom(),
  user_id: uuid('user_id').notNull().references(() => users.id, { onDelete: 'cascade' }),
  token: text('token').notNull().unique(),
  expires_at: timestamp('expires_at').notNull(),
  created_at: timestamp('created_at').notNull().defaultNow(),
  revoked_at: timestamp('revoked_at')
});

// Tickets table
export const tickets = pgTable('tickets', {
  id: uuid('id').primaryKey().defaultRandom(),
  ticket_number: text('ticket_number').notNull().unique(),
  dr_number: text('dr_number').notNull(),
  technician_number: text('technician_number').notNull(),
  technician_name: text('technician_name').notNull(),
  message_content: text('message_content').notNull(),
  message_timestamp: timestamp('message_timestamp').notNull(),
  status: ticketStatusEnum('status').notNull().default('pending'),
  priority: priorityEnum('priority').notNull().default('normal'),
  assigned_to: uuid('assigned_to').references(() => users.id),
  property_id: text('property_id'),
  job_id: text('job_id'),
  address: text('address'),
  installation_status: text('installation_status'),
  compliance_score: numeric('compliance_score'), // 0-100
  completed_at: timestamp('completed_at'),
  created_at: timestamp('created_at').notNull().defaultNow(),
  updated_at: timestamp('updated_at').notNull().defaultNow()
});

// Photos table
export const photos = pgTable('photos', {
  id: uuid('id').primaryKey().defaultRandom(),
  ticket_id: uuid('ticket_id').notNull().references(() => tickets.id, { onDelete: 'cascade' }),
  type: photoTypeEnum('type').notNull(),
  url: text('url').notNull(),
  upload_time: timestamp('upload_time').notNull().defaultNow(),
  file_size: integer('file_size').notNull(),
  metadata: jsonb('metadata'),
  created_at: timestamp('created_at').notNull().defaultNow()
});

// Audit Results table
export const audit_results = pgTable('audit_results', {
  id: uuid('id').primaryKey().defaultRandom(),
  ticket_id: uuid('ticket_id').notNull().references(() => tickets.id, { onDelete: 'cascade' }),
  property_id: text('property_id').notNull(),
  job_id: text('job_id').notNull(),
  address: text('address').notNull(),
  installation_status: text('installation_status').notNull(),
  photos_required: text('photos_required').array().notNull().default([]),
  photos_found: text('photos_found').array().notNull().default([]),
  photos_missing: text('photos_missing').array().notNull().default([]),
  compliance_score: numeric('compliance_score').notNull(),
  audit_details: jsonb('audit_details').notNull(),
  created_at: timestamp('created_at').notNull().defaultNow()
});

// WhatsApp Messages table
export const whatsapp_messages = pgTable('whatsapp_messages', {
  id: text('id').primaryKey(),
  from: text('from').notNull(),
  to: text('to'),
  body: text('body').notNull(),
  type: messageTypeEnum('type').notNull().default('text'),
  timestamp: timestamp('timestamp').notNull(),
  status: messageStatusEnum('status').notNull().default('pending'),
  ticket_id: uuid('ticket_id').references(() => tickets.id),
  created_at: timestamp('created_at').notNull().defaultNow(),
  updated_at: timestamp('updated_at').notNull().defaultNow()
});

// Ticket Assignments table
export const ticket_assignments = pgTable('ticket_assignments', {
  id: uuid('id').primaryKey().defaultRandom(),
  ticket_id: uuid('ticket_id').notNull().references(() => tickets.id, { onDelete: 'cascade' }),
  assigned_to: uuid('assigned_to').notNull().references(() => users.id),
  assigned_by: uuid('assigned_by').notNull().references(() => users.id),
  assigned_at: timestamp('assigned_at').notNull().defaultNow(),
  status: assignmentStatusEnum('status').notNull().default('assigned'),
  notes: text('notes'),
  created_at: timestamp('created_at').notNull().defaultNow()
});

// Rate Limiting table
export const rate_limits = pgTable('rate_limits', {
  id: uuid('id').primaryKey().defaultRandom(),
  key: text('key').notNull().unique(),
  points: integer('points').notNull().default(0),
  reset_time: timestamp('reset_time').notNull(),
  created_at: timestamp('created_at').notNull().defaultNow(),
  updated_at: timestamp('updated_at').notNull().defaultNow()
});

// Audit Logs table
export const audit_logs = pgTable('audit_logs', {
  id: uuid('id').primaryKey().defaultRandom(),
  user_id: uuid('user_id').references(() => users.id),
  action: text('action').notNull(),
  resource_type: text('resource_type').notNull(),
  resource_id: text('resource_id'),
  details: jsonb('details'),
  ip_address: text('ip_address'),
  user_agent: text('user_agent'),
  timestamp: timestamp('timestamp').notNull().defaultNow()
});

// Security Events table
export const security_events = pgTable('security_events', {
  id: uuid('id').primaryKey().defaultRandom(),
  event_type: text('event_type').notNull(),
  severity: text('severity').notNull(), // low, medium, high, critical
  description: text('description').notNull(),
  details: jsonb('details'),
  user_id: uuid('user_id').references(() => users.id),
  ip_address: text('ip_address'),
  user_agent: text('user_agent'),
  timestamp: timestamp('timestamp').notNull().defaultNow(),
  resolved: boolean('resolved').notNull().default(false),
  resolved_at: timestamp('resolved_at'),
  resolved_by: uuid('resolved_by').references(() => users.id)
});

// Performance Metrics table
export const performance_metrics = pgTable('performance_metrics', {
  id: uuid('id').primaryKey().defaultRandom(),
  metric_name: text('metric_name').notNull(),
  metric_value: numeric('metric_value').notNull(),
  metric_type: text('metric_type').notNull(), // counter, gauge, histogram
  tags: jsonb('tags'),
  timestamp: timestamp('timestamp').notNull().defaultNow()
});

// Sessions table
export const sessions = pgTable('sessions', {
  id: uuid('id').primaryKey().defaultRandom(),
  user_id: uuid('user_id').notNull().references(() => users.id, { onDelete: 'cascade' }),
  token: text('token').notNull().unique(),
  expires_at: timestamp('expires_at').notNull(),
  created_at: timestamp('created_at').notNull().defaultNow(),
  last_accessed: timestamp('last_accessed').notNull().defaultNow(),
  is_active: boolean('is_active').notNull().default(true),
  ip_address: text('ip_address'),
  user_agent: text('user_agent')
});

// RPA Executions table
export const rpa_executions = pgTable('rpa_executions', {
  id: uuid('id').primaryKey().defaultRandom(),
  dr_number: text('dr_number').notNull(),
  user_id: uuid('user_id').notNull().references(() => users.id),
  status: text('status').notNull().default('pending'), // pending, running, completed, failed, cancelled
  progress: numeric('progress').notNull().default(0), // 0-100
  execution_details: jsonb('execution_details'),
  screenshots: text('screenshots').array().default([]), // Array of screenshot paths/URLs
  logs: text('logs').array().default([]), // Array of log entries
  error_message: text('error_message'),
  started_at: timestamp('started_at'),
  completed_at: timestamp('completed_at'),
  duration: numeric('duration'), // Duration in milliseconds
  ticket_id: uuid('ticket_id').references(() => tickets.id),
  audit_result_id: uuid('audit_result_id').references(() => audit_results.id),
  options: jsonb('options'), // RPA execution options
  created_at: timestamp('created_at').notNull().defaultNow(),
  updated_at: timestamp('updated_at').notNull().defaultNow()
});

// Indexes for performance
export const indexes = {
  // Users indexes
  users_email_idx: `CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)`,
  users_username_idx: `CREATE INDEX IF NOT EXISTS idx_users_username ON users(username)`,
  users_role_idx: `CREATE INDEX IF NOT EXISTS idx_users_role ON users(role)`,

  // Tickets indexes
  tickets_dr_number_idx: `CREATE INDEX IF NOT EXISTS idx_tickets_dr_number ON tickets(dr_number)`,
  tickets_status_idx: `CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets(status)`,
  tickets_technician_number_idx: `CREATE INDEX IF NOT EXISTS idx_tickets_technician_number ON tickets(technician_number)`,
  tickets_assigned_to_idx: `CREATE INDEX IF NOT EXISTS idx_tickets_assigned_to ON tickets(assigned_to)`,
  tickets_created_at_idx: `CREATE INDEX IF NOT EXISTS idx_tickets_created_at ON tickets(created_at)`,

  // Photos indexes
  photos_ticket_id_idx: `CREATE INDEX IF NOT EXISTS idx_photos_ticket_id ON photos(ticket_id)`,
  photos_type_idx: `CREATE INDEX IF NOT EXISTS idx_photos_type ON photos(type)`,

  // WhatsApp messages indexes
  whatsapp_messages_from_idx: `CREATE INDEX IF NOT EXISTS idx_whatsapp_messages_from ON whatsapp_messages(from)`,
  whatsapp_messages_ticket_id_idx: `CREATE INDEX IF NOT EXISTS idx_whatsapp_messages_ticket_id ON whatsapp_messages(ticket_id)`,
  whatsapp_messages_timestamp_idx: `CREATE INDEX IF NOT EXISTS idx_whatsapp_messages_timestamp ON whatsapp_messages(timestamp)`,

  // Ticket assignments indexes
  ticket_assignments_ticket_id_idx: `CREATE INDEX IF NOT EXISTS idx_ticket_assignments_ticket_id ON ticket_assignments(ticket_id)`,
  ticket_assignments_assigned_to_idx: `CREATE INDEX IF NOT EXISTS idx_ticket_assignments_assigned_to ON ticket_assignments(assigned_to)`,

  // Rate limiting indexes
  rate_limits_key_idx: `CREATE INDEX IF NOT EXISTS idx_rate_limits_key ON rate_limits(key)`,
  rate_limits_reset_time_idx: `CREATE INDEX IF NOT EXISTS idx_rate_limits_reset_time ON rate_limits(reset_time)`,

  // Refresh tokens indexes
  refresh_tokens_user_id_idx: `CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id)`,
  refresh_tokens_token_idx: `CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token)`,
  refresh_tokens_expires_at_idx: `CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens(expires_at)`,

  // Audit logs indexes
  audit_logs_user_id_idx: `CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id)`,
  audit_logs_action_idx: `CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action)`,
  audit_logs_timestamp_idx: `CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp)`,

  // Security events indexes
  security_events_severity_idx: `CREATE INDEX IF NOT EXISTS idx_security_events_severity ON security_events(severity)`,
  security_events_event_type_idx: `CREATE INDEX IF NOT EXISTS idx_security_events_event_type ON security_events(event_type)`,
  security_events_timestamp_idx: `CREATE INDEX IF NOT EXISTS idx_security_events_timestamp ON security_events(timestamp)`,

  // Performance metrics indexes
  performance_metrics_name_idx: `CREATE INDEX IF NOT EXISTS idx_performance_metrics_name ON performance_metrics(metric_name)`,
  performance_metrics_timestamp_idx: `CREATE INDEX IF NOT EXISTS idx_performance_metrics_timestamp ON performance_metrics(timestamp)`,

  // Sessions indexes
  sessions_user_id_idx: `CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id)`,
  sessions_token_idx: `CREATE INDEX IF NOT EXISTS idx_sessions_token ON sessions(token)`,
  sessions_expires_at_idx: `CREATE INDEX IF NOT EXISTS idx_sessions_expires_at ON sessions(expires_at)`,

  // RPA executions indexes
  rpa_executions_dr_number_idx: `CREATE INDEX IF NOT EXISTS idx_rpa_executions_dr_number ON rpa_executions(dr_number)`,
  rpa_executions_user_id_idx: `CREATE INDEX IF NOT EXISTS idx_rpa_executions_user_id ON rpa_executions(user_id)`,
  rpa_executions_status_idx: `CREATE INDEX IF NOT EXISTS idx_rpa_executions_status ON rpa_executions(status)`,
  rpa_executions_created_at_idx: `CREATE INDEX IF NOT EXISTS idx_rpa_executions_created_at ON rpa_executions(created_at)`,
  rpa_executions_ticket_id_idx: `CREATE INDEX IF NOT EXISTS idx_rpa_executions_ticket_id ON rpa_executions(ticket_id)`
};

// Tables are already exported individually above

// Export types
export type User = typeof users.$inferSelect;
export type NewUser = typeof users.$inferInsert;
export type RefreshToken = typeof refresh_tokens.$inferSelect;
export type NewRefreshToken = typeof refresh_tokens.$inferInsert;
export type Ticket = typeof tickets.$inferSelect;
export type NewTicket = typeof tickets.$inferInsert;
export type Photo = typeof photos.$inferSelect;
export type NewPhoto = typeof photos.$inferInsert;
export type AuditResult = typeof audit_results.$inferSelect;
export type NewAuditResult = typeof audit_results.$inferInsert;
export type WhatsAppMessage = typeof whatsapp_messages.$inferSelect;
export type NewWhatsAppMessage = typeof whatsapp_messages.$inferInsert;
export type TicketAssignment = typeof ticket_assignments.$inferSelect;
export type NewTicketAssignment = typeof ticket_assignments.$inferInsert;
export type RateLimit = typeof rate_limits.$inferSelect;
export type NewRateLimit = typeof rate_limits.$inferInsert;
export type AuditLog = typeof audit_logs.$inferSelect;
export type NewAuditLog = typeof audit_logs.$inferInsert;
export type SecurityEvent = typeof security_events.$inferSelect;
export type NewSecurityEvent = typeof security_events.$inferInsert;
export type PerformanceMetric = typeof performance_metrics.$inferSelect;
export type NewPerformanceMetric = typeof performance_metrics.$inferInsert;
export type Session = typeof sessions.$inferSelect;
export type NewSession = typeof sessions.$inferInsert;
export type RPAExecution = typeof rpa_executions.$inferSelect;
export type NewRPAExecution = typeof rpa_executions.$inferInsert;