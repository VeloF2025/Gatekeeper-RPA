import { database } from '../src/database/database';
import { authService } from '../src/services/auth/auth.service';
import * as schema from '../src/database/schemas/index';
import { logger } from '../src/utils/logger';
import config from '../src/config';
import { sql } from 'drizzle-orm';

/**
 * Database seeding script
 * This script populates the database with initial data for development/testing
 */
async function seedDatabase() {
  try {
    logger.info('Starting database seeding...');

    // Check if we should seed (only in development or if explicitly enabled)
    if (config.nodeEnv === 'production' && process.env.SEED_DATABASE !== 'true') {
      logger.info('Skipping database seeding in production mode');
      return;
    }

    // Test database connection
    const dbHealthy = await database.healthCheck();
    if (!dbHealthy) {
      throw new Error('Database is not healthy');
    }

    logger.info('Database connection successful');

    // Create default admin user
    const adminUser = await authService.createUser({
      email: 'admin@gatekeeper.test',
      username: 'admin',
      password: 'Admin123!',
      firstName: 'System',
      lastName: 'Administrator',
      role: 'admin',
      isActive: true,
      lastLogin: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      metadata: {
        department: 'IT',
        employeeId: 'ADMIN001'
      }
    });

    logger.info('Created admin user', { userId: adminUser.id, email: adminUser.email });

    // Create default manager user
    const managerUser = await authService.createUser({
      email: 'manager@gatekeeper.test',
      username: 'manager',
      password: 'Manager123!',
      firstName: 'Operations',
      lastName: 'Manager',
      role: 'manager',
      isActive: true,
      lastLogin: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      metadata: {
        department: 'Operations',
        employeeId: 'MGR001'
      }
    });

    logger.info('Created manager user', { userId: managerUser.id, email: managerUser.email });

    // Create default auditor user
    const auditorUser = await authService.createUser({
      email: 'auditor@gatekeeper.test',
      username: 'auditor',
      password: 'Auditor123!',
      firstName: 'Quality',
      lastName: 'Auditor',
      role: 'auditor',
      isActive: true,
      lastLogin: null,
      createdAt: new Date(),
      updatedAt: new Date(),
      metadata: {
        department: 'Quality Assurance',
        employeeId: 'AUD001'
      }
    });

    logger.info('Created auditor user', { userId: auditorUser.id, email: auditorUser.email });

    // Create sample tickets
    const sampleTickets = [
      {
        ticketNumber: 'TCK-DEMO-001',
        drNumber: 'DR2024001',
        technicianNumber: '+27820001111',
        technicianName: 'John Doe',
        messageContent: 'Installation complete at 123 Main St. Please audit.',
        priority: 'normal',
        status: 'pending'
      },
      {
        ticketNumber: 'TCK-DEMO-002',
        drNumber: 'DR2024002',
        technicianNumber: '+27820002222',
        technicianName: 'Jane Smith',
        messageContent: 'Fiber installation at 456 Oak Avenue done. Need verification.',
        priority: 'high',
        status: 'in_progress'
      },
      {
        ticketNumber: 'TCK-DEMO-003',
        drNumber: 'DR2024003',
        technicianNumber: '+27820003333',
        technicianName: 'Mike Johnson',
        messageContent: 'Urgent: Installation issues at 789 Pine Road',
        priority: 'urgent',
        status: 'assigned'
      }
    ];

    for (const ticketData of sampleTickets) {
      const ticket = await database.drizzle
        .insert(schema.tickets)
        .values({
          ...ticketData,
          metadata: { isDemo: true },
          createdAt: new Date(),
          updatedAt: new Date(),
          messageTimestamp: new Date(),
          completedAt: null,
          whatsappMessageId: null,
          externalTicketId: null,
          externalSystem: null,
          syncStatus: 'pending',
        })
        .returning();

      logger.info('Created sample ticket', {
        ticketId: ticket[0].id,
        ticketNumber: ticketData.ticketNumber,
        drNumber: ticketData.drNumber
      });

      // Create sample audit results for some tickets
      if (ticketData.ticketNumber === 'TCK-DEMO-001') {
        const auditResult = await database.drizzle
          .insert(schema.auditResults)
          .values({
            ticketId: ticket[0].id,
            propertyId: 'PROP001',
            jobId: 'JOB001',
            address: '123 Main St, Suburb, City, 1234',
            installationStatus: 'completed',
            photosRequired: [
              { type: 'property_exterior', category: 'required', required: true },
              { type: 'equipment_installed', category: 'required', required: true },
              { type: 'connection_point', category: 'required', required: true }
            ],
            photosFound: [
              { type: 'property_exterior', category: 'required', url: '/uploads/demo-photo-1.jpg' },
              { type: 'equipment_installed', category: 'required', url: '/uploads/demo-photo-2.jpg' }
            ],
            photosMissing: [
              { type: 'connection_point', category: 'required', reason: 'Not provided by technician' }
            ],
            complianceScore: '75.50',
            mlConfidenceScore: null,
            auditDetails: {
              totalPhotos: 2,
              auditTimestamp: new Date().toISOString(),
              notes: 'Partial compliance - missing connection point photo'
            },
            createdAt: new Date(),
            updatedAt: new Date()
          })
          .returning();

        logger.info('Created sample audit result', { auditResultId: auditResult[0].id });

        // Create sample audit photos
        const samplePhotos = [
          {
            auditResultId: auditResult[0].id,
            photoUrl: '/uploads/demo-photo-1.jpg',
            photoType: 'property_exterior',
            photoCategory: 'required',
            fileSize: 2048576, // 2MB
            fileFormat: 'jpg',
            complianceStatus: 'compliant',
            complianceNotes: 'Good quality photo showing property exterior'
          },
          {
            auditResultId: auditResult[0].id,
            photoUrl: '/uploads/demo-photo-2.jpg',
            photoType: 'equipment_installed',
            photoCategory: 'required',
            fileSize: 3072000, // 3MB
            fileFormat: 'jpg',
            complianceStatus: 'compliant',
            complianceNotes: 'Clear photo of installed equipment'
          }
        ];

        for (const photoData of samplePhotos) {
          const photo = await database.drizzle
            .insert(schema.auditPhotos)
            .values({
              ...photoData,
              uploadTimestamp: new Date(),
              createdAt: new Date()
            })
            .returning();

          logger.info('Created sample audit photo', { photoId: photo[0].id });
        }
      }

      // Create sample workflow events
      const workflowEvents = [
        {
          ticketId: ticket[0].id,
          eventType: 'ticket_created',
          eventData: { source: 'whatsapp', automated: true },
          eventSource: 'whatsapp_gateway'
        },
        {
          ticketId: ticket[0].id,
          eventType: 'ticket_assigned',
          eventData: { assignedTo: 'auto-assign', priority: ticketData.priority },
          eventSource: 'system',
          userId: adminUser.id
        }
      ];

      for (const eventData of workflowEvents) {
        const event = await database.drizzle
          .insert(schema.workflowEvents)
          .values({
            ...eventData,
            metadata: { isDemo: true },
            eventTimestamp: new Date()
          })
          .returning();

        logger.info('Created workflow event', { eventId: event[0].id, eventType: eventData.eventType });
      }

      // Create sample ticket metrics
      const metrics = [
        {
          ticketId: ticket[0].id,
          metricType: 'response_time',
          metricValue: '2.50',
          metricUnit: 'minutes',
          metadata: { source: 'system' }
        },
        {
          ticketId: ticket[0].id,
          metricType: 'processing_time',
          metricValue: '15.75',
          metricUnit: 'minutes',
          metadata: { source: 'audit_engine' }
        }
      ];

      for (const metricData of metrics) {
        const metric = await database.drizzle
          .insert(schema.ticketMetrics)
          .values({
            ...metricData,
            recordedAt: new Date()
          })
          .returning();

        logger.info('Created ticket metric', { metricId: metric[0].id, metricType: metricData.metricType });
      }
    }

    logger.info('Database seeding completed successfully');
    logger.info('Demo users created:');
    logger.info('  Admin: admin@gatekeeper.test / Admin123!');
    logger.info('  Manager: manager@gatekeeper.test / Manager123!');
    logger.info('  Auditor: auditor@gatekeeper.test / Auditor123!');
    logger.info(`Sample tickets created: ${sampleTickets.length}`);

  } catch (error) {
    logger.error('Database seeding failed', {
      error: error instanceof Error ? error.message : error
    });
    throw error;
  }
}

/**
 * Reset database (drop all tables and recreate)
 */
async function resetDatabase() {
  try {
    logger.info('Starting database reset...');

    // Drop all tables in reverse order of dependencies
    const tables = [
      'workflow_events',
      'ticket_metrics',
      'ticket_assignments',
      'audit_photos',
      'audit_results',
      'tickets',
      'api_sessions',
      'users',
      'rate_limits'
    ];

    for (const table of tables) {
      await database.executeQuery(sql`DROP TABLE IF EXISTS ${sql.raw(table)} CASCADE`);
      logger.info(`Dropped table: ${table}`);
    }

    logger.info('Database reset completed');
  } catch (error) {
    logger.error('Database reset failed', {
      error: error instanceof Error ? error.message : error
    });
    throw error;
  }
}

// Main execution
async function main() {
  const args = process.argv.slice(2);
  const command = args[0] || 'seed';

  try {
    switch (command) {
      case 'seed':
        await seedDatabase();
        break;
      case 'reset':
        await resetDatabase();
        await seedDatabase();
        break;
      case 'reset-only':
        await resetDatabase();
        break;
      default:
        throw new Error(`Unknown command: ${command}`);
    }

    logger.info('Database operation completed successfully');
    process.exit(0);
  } catch (error) {
    logger.error('Database operation failed', {
      error: error instanceof Error ? error.message : error
    });
    process.exit(1);
  }
}

// Run if this file is executed directly
if (require.main === module) {
  main();
}

export { seedDatabase, resetDatabase };