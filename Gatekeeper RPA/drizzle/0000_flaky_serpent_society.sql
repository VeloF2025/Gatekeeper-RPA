CREATE TABLE IF NOT EXISTS "api_sessions" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"user_id" uuid NOT NULL,
	"token_hash" varchar(255) NOT NULL,
	"refresh_token_hash" varchar(255),
	"expires_at" timestamp with time zone NOT NULL,
	"is_active" boolean DEFAULT true NOT NULL,
	"ip_address" varchar(45),
	"user_agent" text,
	"created_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"last_used" timestamp with time zone,
	"metadata" jsonb DEFAULT '{}'::jsonb,
	CONSTRAINT "api_sessions_token_hash_unique" UNIQUE("token_hash")
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "audit_photos" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"audit_result_id" uuid NOT NULL,
	"photo_url" text NOT NULL,
	"photo_type" varchar(50) NOT NULL,
	"photo_category" varchar(20) NOT NULL,
	"file_size" integer,
	"file_format" varchar(10),
	"upload_timestamp" timestamp with time zone,
	"compliance_status" varchar(20) DEFAULT 'pending',
	"compliance_notes" text,
	"metadata" jsonb DEFAULT '{}'::jsonb,
	"created_at" timestamp with time zone DEFAULT NOW() NOT NULL
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "audit_results" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"ticket_id" uuid NOT NULL,
	"property_id" varchar(50),
	"job_id" varchar(50),
	"address" text,
	"installation_status" varchar(50),
	"last_modified" timestamp with time zone,
	"photos_required" jsonb DEFAULT '[]'::jsonb,
	"photos_found" jsonb DEFAULT '[]'::jsonb,
	"photos_missing" jsonb DEFAULT '[]'::jsonb,
	"compliance_score" numeric(5, 2),
	"audit_details" jsonb DEFAULT '{}'::jsonb,
	"ml_confidence_score" numeric(5, 2),
	"created_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"updated_at" timestamp with time zone DEFAULT NOW() NOT NULL
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "rate_limits" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"identifier" varchar(255) NOT NULL,
	"window_start" timestamp with time zone NOT NULL,
	"window_end" timestamp with time zone NOT NULL,
	"request_count" integer DEFAULT 0 NOT NULL,
	"limit_type" varchar(50) NOT NULL,
	"metadata" jsonb DEFAULT '{}'::jsonb,
	CONSTRAINT "rate_limits_identifier_window_idx" UNIQUE("identifier","window_start")
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "ticket_assignments" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"ticket_id" uuid NOT NULL,
	"assigned_to" varchar(100) NOT NULL,
	"assigned_by" varchar(100) NOT NULL,
	"assigned_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"status" varchar(20) DEFAULT 'assigned' NOT NULL,
	"notes" text
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "ticket_metrics" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"ticket_id" uuid NOT NULL,
	"metric_type" varchar(50) NOT NULL,
	"metric_value" numeric(10, 2) NOT NULL,
	"metric_unit" varchar(20),
	"recorded_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"metadata" jsonb DEFAULT '{}'::jsonb
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "tickets" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"ticket_number" varchar(50) NOT NULL,
	"dr_number" varchar(50) NOT NULL,
	"technician_number" varchar(20) NOT NULL,
	"technician_name" varchar(100),
	"message_content" text,
	"message_timestamp" timestamp with time zone,
	"status" varchar(20) DEFAULT 'pending' NOT NULL,
	"priority" varchar(10) DEFAULT 'normal' NOT NULL,
	"assigned_to" varchar(100),
	"created_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"updated_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"completed_at" timestamp with time zone,
	"whatsapp_message_id" varchar(100),
	"external_ticket_id" varchar(50),
	"external_system" varchar(20),
	"sync_status" varchar(20) DEFAULT 'pending' NOT NULL,
	"metadata" jsonb DEFAULT '{}'::jsonb,
	CONSTRAINT "tickets_ticket_number_unique" UNIQUE("ticket_number")
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "users" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"email" varchar(255) NOT NULL,
	"username" varchar(100) NOT NULL,
	"password_hash" varchar(255) NOT NULL,
	"first_name" varchar(100),
	"last_name" varchar(100),
	"role" varchar(20) DEFAULT 'user' NOT NULL,
	"is_active" boolean DEFAULT true NOT NULL,
	"last_login" timestamp with time zone,
	"created_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"updated_at" timestamp with time zone DEFAULT NOW() NOT NULL,
	"metadata" jsonb DEFAULT '{}'::jsonb,
	CONSTRAINT "users_email_unique" UNIQUE("email"),
	CONSTRAINT "users_username_unique" UNIQUE("username")
);
--> statement-breakpoint
CREATE TABLE IF NOT EXISTS "workflow_events" (
	"id" uuid PRIMARY KEY DEFAULT gen_random_uuid() NOT NULL,
	"ticket_id" uuid NOT NULL,
	"event_type" varchar(50) NOT NULL,
	"event_data" jsonb DEFAULT '{}'::jsonb,
	"event_timestamp" timestamp with time zone DEFAULT NOW() NOT NULL,
	"event_source" varchar(50) NOT NULL,
	"user_id" varchar(100),
	"metadata" jsonb DEFAULT '{}'::jsonb
);
--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "api_sessions_user_id_idx" ON "api_sessions" ("user_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "api_sessions_token_hash_idx" ON "api_sessions" ("token_hash");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "api_sessions_expires_at_idx" ON "api_sessions" ("expires_at");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "api_sessions_is_active_idx" ON "api_sessions" ("is_active");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "audit_photos_audit_result_id_idx" ON "audit_photos" ("audit_result_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "audit_photos_photo_type_idx" ON "audit_photos" ("photo_type");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "audit_photos_compliance_status_idx" ON "audit_photos" ("compliance_status");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "audit_results_ticket_id_idx" ON "audit_results" ("ticket_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "audit_results_property_id_idx" ON "audit_results" ("property_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "audit_results_job_id_idx" ON "audit_results" ("job_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "audit_results_compliance_score_idx" ON "audit_results" ("compliance_score");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "rate_limits_identifier_type_idx" ON "rate_limits" ("identifier","limit_type");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "rate_limits_window_end_idx" ON "rate_limits" ("window_end");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "ticket_assignments_ticket_id_idx" ON "ticket_assignments" ("ticket_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "ticket_assignments_assigned_to_idx" ON "ticket_assignments" ("assigned_to");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "ticket_assignments_assigned_at_idx" ON "ticket_assignments" ("assigned_at");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "ticket_metrics_ticket_id_idx" ON "ticket_metrics" ("ticket_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "ticket_metrics_metric_type_idx" ON "ticket_metrics" ("metric_type");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "ticket_metrics_recorded_at_idx" ON "ticket_metrics" ("recorded_at");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "tickets_ticket_number_idx" ON "tickets" ("ticket_number");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "tickets_dr_number_idx" ON "tickets" ("dr_number");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "tickets_technician_number_idx" ON "tickets" ("technician_number");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "tickets_status_idx" ON "tickets" ("status");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "tickets_created_at_idx" ON "tickets" ("created_at");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "users_email_idx" ON "users" ("email");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "users_username_idx" ON "users" ("username");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "users_role_idx" ON "users" ("role");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "users_is_active_idx" ON "users" ("is_active");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "workflow_events_ticket_id_idx" ON "workflow_events" ("ticket_id");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "workflow_events_event_type_idx" ON "workflow_events" ("event_type");--> statement-breakpoint
CREATE INDEX IF NOT EXISTS "workflow_events_event_timestamp_idx" ON "workflow_events" ("event_timestamp");--> statement-breakpoint
DO $$ BEGIN
 ALTER TABLE "api_sessions" ADD CONSTRAINT "api_sessions_user_id_users_id_fk" FOREIGN KEY ("user_id") REFERENCES "users"("id") ON DELETE cascade ON UPDATE no action;
EXCEPTION
 WHEN duplicate_object THEN null;
END $$;
--> statement-breakpoint
DO $$ BEGIN
 ALTER TABLE "audit_photos" ADD CONSTRAINT "audit_photos_audit_result_id_audit_results_id_fk" FOREIGN KEY ("audit_result_id") REFERENCES "audit_results"("id") ON DELETE cascade ON UPDATE no action;
EXCEPTION
 WHEN duplicate_object THEN null;
END $$;
--> statement-breakpoint
DO $$ BEGIN
 ALTER TABLE "audit_results" ADD CONSTRAINT "audit_results_ticket_id_tickets_id_fk" FOREIGN KEY ("ticket_id") REFERENCES "tickets"("id") ON DELETE cascade ON UPDATE no action;
EXCEPTION
 WHEN duplicate_object THEN null;
END $$;
--> statement-breakpoint
DO $$ BEGIN
 ALTER TABLE "ticket_assignments" ADD CONSTRAINT "ticket_assignments_ticket_id_tickets_id_fk" FOREIGN KEY ("ticket_id") REFERENCES "tickets"("id") ON DELETE cascade ON UPDATE no action;
EXCEPTION
 WHEN duplicate_object THEN null;
END $$;
--> statement-breakpoint
DO $$ BEGIN
 ALTER TABLE "ticket_metrics" ADD CONSTRAINT "ticket_metrics_ticket_id_tickets_id_fk" FOREIGN KEY ("ticket_id") REFERENCES "tickets"("id") ON DELETE cascade ON UPDATE no action;
EXCEPTION
 WHEN duplicate_object THEN null;
END $$;
--> statement-breakpoint
DO $$ BEGIN
 ALTER TABLE "workflow_events" ADD CONSTRAINT "workflow_events_ticket_id_tickets_id_fk" FOREIGN KEY ("ticket_id") REFERENCES "tickets"("id") ON DELETE cascade ON UPDATE no action;
EXCEPTION
 WHEN duplicate_object THEN null;
END $$;
