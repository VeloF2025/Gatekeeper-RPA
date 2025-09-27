import { NextRequest, NextResponse } from 'next/server';
import { authMiddleware } from '@/middleware/auth';
import { logger } from '@/utils/logger';
import { RPAService } from '@/services/rpa/rpa.service';
import { database } from '@/database/database';
import { eq, and, desc, sql } from 'drizzle-orm';
import { rpaJobs } from '@/database/schemas';

// Global RPA service instance (singleton pattern)
let rpaService: RPAService | null = null;

/**
 * Initialize RPA service if not already initialized
 */
async function getOrCreateRPAService(): Promise<RPAService> {
  if (!rpaService) {
    rpaService = new RPAService();
    // Initialize with default options
    await rpaService.initialize({
      headless: process.env.NODE_ENV === 'production',
      timeout: 30000,
      screenshots: process.env.NODE_ENV === 'production',
      retries: 3
    });
  }
  return rpaService;
}

/**
 * POST /api/rpa/jobs - Submit RPA job
 */
export async function POST(request: NextRequest) {
  const startTime = Date.now();

  try {
    // Check authentication
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult; // Return error response
    }

    const body = await request.json();
    const { ticketId, drNumber, options = {} } = body;

    // Validate required fields
    if (!ticketId || !drNumber) {
      return NextResponse.json(
        { error: 'ticketId and drNumber are required' },
        { status: 400 }
      );
    }

    // Validate DR number format
    if (!/^DR\d{7}$/i.test(drNumber)) {
      return NextResponse.json(
        { error: 'Invalid DR number format. Expected: DR1234567' },
        { status: 400 }
      );
    }

    logger.info('RPA job submission requested', {
      ticketId,
      drNumber,
      userId: authResult.userId,
      timestamp: new Date().toISOString()
    });

    // Get RPA service
    const rpa = await getOrCreateRPAService();

    // Create job record in database
    const jobData = {
      ticketId,
      drNumber,
      status: 'pending' as const,
      metadata: {
        submittedBy: authResult.userId,
        submittedAt: new Date().toISOString(),
        options,
        userAgent: request.headers.get('user-agent')
      }
    };

    const [newJob] = await database.drizzle.insert(rpaJobs).values(jobData).returning();

    logger.info('RPA job created in database', {
      jobId: newJob.id,
      ticketId,
      drNumber,
      status: newJob.status
    });

    // Execute RPA audit asynchronously
    (async () => {
      try {
        // Update job status to in_progress
        await database.drizzle
          .update(rpaJobs)
          .set({
            status: 'in_progress',
            startedAt: new Date()
          })
          .where(eq(rpaJobs.id, newJob.id));

        logger.info('RPA job execution started', { jobId: newJob.id, drNumber });

        // Execute the audit
        const result = await rpa.executeFullAudit(drNumber, {
          screenshots: true,
          timeout: 30000,
          retries: 3,
          ...options
        });

        // Update job record with results
        const updateData: any = {
          status: result.success ? 'completed' : 'failed',
          completedAt: new Date(),
          result: result.data,
          error: result.error,
          metadata: {
            ...jobData.metadata,
            executionTime: result.executionTime,
            stepsCount: result.steps.length,
            errorsCount: result.errors.length,
            screenshotsCount: result.screenshots?.length || 0
          }
        };

        await database.drizzle
          .update(rpaJobs)
          .set(updateData)
          .where(eq(rpaJobs.id, newJob.id));

        logger.info('RPA job execution completed', {
          jobId: newJob.id,
          drNumber,
          success: result.success,
          executionTime: result.executionTime,
          stepsCount: result.steps.length
        });

      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';

        // Update job record with error
        await database.drizzle
          .update(rpaJobs)
          .set({
            status: 'failed',
            completedAt: new Date(),
            error: errorMessage,
            metadata: {
              ...jobData.metadata,
              failedAt: new Date().toISOString()
            }
          })
          .where(eq(rpaJobs.id, newJob.id));

        logger.error('RPA job execution failed', {
          jobId: newJob.id,
          drNumber,
          error: errorMessage
        });
      }
    })();

    const responseTime = Date.now() - startTime;

    return NextResponse.json({
      success: true,
      message: 'RPA job submitted successfully',
      job: {
        id: newJob.id,
        ticketId: newJob.ticketId,
        drNumber: newJob.drNumber,
        status: newJob.status,
        createdAt: newJob.createdAt,
        submittedBy: authResult.userId
      },
      performance: {
        responseTime,
        timestamp: new Date().toISOString()
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error submitting RPA job', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to submit RPA job',
        details: errorMessage,
        performance: {
          responseTime,
          timestamp: new Date().toISOString()
        }
      },
      { status: 500 }
    );
  }
}

/**
 * GET /api/rpa/jobs - Get RPA jobs with filtering and pagination
 */
export async function GET(request: NextRequest) {
  const startTime = Date.now();

  try {
    // Check authentication
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult; // Return error response
    }

    const { searchParams } = new URL(request.url);
    const jobId = searchParams.get('jobId');
    const ticketId = searchParams.get('ticketId');
    const drNumber = searchParams.get('drNumber');
    const status = searchParams.get('status');
    const page = parseInt(searchParams.get('page') || '1');
    const limit = parseInt(searchParams.get('limit') || '10');
    const offset = (page - 1) * limit;

    logger.info('RPA jobs list requested', {
      jobId,
      ticketId,
      drNumber,
      status,
      page,
      limit,
      userId: authResult.userId
    });

    // Build query conditions
    const conditions = [];
    if (jobId) conditions.push(eq(rpaJobs.id, jobId));
    if (ticketId) conditions.push(eq(rpaJobs.ticketId, ticketId));
    if (drNumber) conditions.push(eq(rpaJobs.drNumber, drNumber));
    if (status && ['pending', 'in_progress', 'completed', 'failed'].includes(status)) {
      conditions.push(eq(rpaJobs.status, status));
    }

    // Query jobs with conditions, pagination and ordering
    const jobs = await database.drizzle
      .select({
        id: rpaJobs.id,
        ticketId: rpaJobs.ticketId,
        drNumber: rpaJobs.drNumber,
        status: rpaJobs.status,
        createdAt: rpaJobs.createdAt,
        startedAt: rpaJobs.startedAt,
        completedAt: rpaJobs.completedAt,
        retryCount: rpaJobs.retryCount,
        error: rpaJobs.error,
        result: rpaJobs.result,
        metadata: rpaJobs.metadata
      })
      .from(rpaJobs)
      .where(conditions.length > 0 ? and(...conditions) : undefined)
      .orderBy(desc(rpaJobs.createdAt))
      .limit(limit)
      .offset(offset);

    // Get total count
    const totalCountQuery = database.drizzle
      .select({ count: sql`count(*)` })
      .from(rpaJobs)
      .where(conditions.length > 0 ? and(...conditions) : undefined);

    const totalCount = await totalCountQuery;
    const totalPages = Math.ceil(Number(totalCount[0].count) / limit);

    // Get job statistics
    const stats = await database.drizzle
      .select({
        status: rpaJobs.status,
        count: rpaJobs.id
      })
      .from(rpaJobs)
      .groupBy(rpaJobs.status);

    const responseTime = Date.now() - startTime;

    logger.info('RPA jobs retrieved successfully', {
      jobsCount: jobs.length,
      totalCount: totalCount[0].count,
      responseTime
    });

    return NextResponse.json({
      success: true,
      data: {
        jobs,
        pagination: {
          currentPage: page,
          totalPages,
          totalItems: totalCount[0].count,
          itemsPerPage: limit,
          hasNext: page < totalPages,
          hasPrev: page > 1
        },
        statistics: {
          byStatus: stats.reduce((acc: Record<string, number>, stat: { status: string; count: string | number }) => {
            acc[stat.status] = typeof stat.count === 'string' ? parseInt(stat.count) : stat.count;
            return acc;
          }, {} as Record<string, number>),
          totalJobs: totalCount[0].count
        }
      },
      performance: {
        responseTime,
        timestamp: new Date().toISOString()
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error getting RPA jobs', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to get RPA jobs',
        details: errorMessage,
        performance: {
          responseTime,
          timestamp: new Date().toISOString()
        }
      },
      { status: 500 }
    );
  }
}

/**
 * PUT /api/rpa/jobs/:id - Update RPA job status
 */
export async function PUT(request: NextRequest) {
  const startTime = Date.now();

  try {
    // Check authentication
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult; // Return error response
    }

    const url = new URL(request.url);
    const jobId = url.pathname.split('/').pop();
    const body = await request.json();
    const { status, error, metadata } = body;

    if (!jobId) {
      return NextResponse.json({ error: 'Job ID is required' }, { status: 400 });
    }

    if (!status || !['pending', 'in_progress', 'completed', 'failed'].includes(status)) {
      return NextResponse.json({
        error: 'Valid status is required (pending, in_progress, completed, failed)'
      }, { status: 400 });
    }

    logger.info('RPA job update requested', {
      jobId,
      status,
      userId: authResult.userId
    });

    // Update job
    const updateData: any = { status };

    if (status === 'in_progress') {
      updateData.startedAt = new Date();
    } else if (['completed', 'failed'].includes(status)) {
      updateData.completedAt = new Date();
    }

    if (error) updateData.error = error;
    if (metadata) updateData.metadata = metadata;

    const [updatedJob] = await database.drizzle
      .update(rpaJobs)
      .set(updateData)
      .where(eq(rpaJobs.id, jobId))
      .returning();

    if (!updatedJob) {
      return NextResponse.json({ error: 'Job not found' }, { status: 404 });
    }

    const responseTime = Date.now() - startTime;

    logger.info('RPA job updated successfully', {
      jobId,
      oldStatus: updatedJob.status,
      newStatus: status,
      responseTime
    });

    return NextResponse.json({
      success: true,
      message: 'RPA job updated successfully',
      job: updatedJob,
      performance: {
        responseTime,
        timestamp: new Date().toISOString()
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error updating RPA job', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to update RPA job',
        details: errorMessage,
        performance: {
          responseTime,
          timestamp: new Date().toISOString()
        }
      },
      { status: 500 }
    );
  }
}