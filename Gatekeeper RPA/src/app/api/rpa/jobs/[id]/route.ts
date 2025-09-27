import { NextRequest, NextResponse } from 'next/server';
import { authMiddleware } from '@/middleware/auth';
import { logger } from '@/utils/logger';
import { database } from '@/database/database';
import { eq } from 'drizzle-orm';
import { rpaJobs } from '@/database/schemas';

interface RouteParams {
  params: {
    id: string;
  };
}

/**
 * GET /api/rpa/jobs/[id] - Get specific RPA job details
 */
export async function GET(request: NextRequest, context: RouteParams) {
  const startTime = Date.now();

  try {
    // Check authentication
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult; // Return error response
    }

    const jobId = context.params.id;

    if (!jobId) {
      return NextResponse.json({ error: 'Job ID is required' }, { status: 400 });
    }

    logger.info('RPA job details requested', {
      jobId,
      userId: authResult.userId
    });

    // Get job details
    const [job] = await database.drizzle
      .select()
      .from(rpaJobs)
      .where(eq(rpaJobs.id, jobId));

    if (!job) {
      return NextResponse.json({ error: 'Job not found' }, { status: 404 });
    }

    // Calculate performance metrics
    const responseTime = Date.now() - startTime;
    let executionTime = null;
    let processingTime = null;

    if (job.startedAt && job.completedAt) {
      executionTime = new Date(job.completedAt).getTime() - new Date(job.startedAt).getTime();
    }

    if (job.createdAt && job.completedAt) {
      processingTime = new Date(job.completedAt).getTime() - new Date(job.createdAt).getTime();
    }

    logger.info('RPA job details retrieved successfully', {
      jobId,
      status: job.status,
      executionTime,
      processingTime,
      responseTime
    });

    return NextResponse.json({
      success: true,
      data: {
        job: {
          ...job,
          performanceMetrics: {
            executionTime: executionTime ? `${executionTime}ms` : null,
            processingTime: processingTime ? `${processingTime}ms` : null,
            responseTime: `${responseTime}ms`
          }
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

    logger.error('Error getting RPA job details', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to get RPA job details',
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
 * DELETE /api/rpa/jobs/[id] - Delete RPA job (admin only)
 */
export async function DELETE(request: NextRequest, context: RouteParams) {
  const startTime = Date.now();

  try {
    // Check authentication and admin role
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult; // Return error response
    }

    // Check if user has admin role
    const userRole = authResult.role || 'user';
    if (userRole !== 'admin') {
      return NextResponse.json({ error: 'Insufficient permissions' }, { status: 403 });
    }

    const jobId = context.params.id;

    if (!jobId) {
      return NextResponse.json({ error: 'Job ID is required' }, { status: 400 });
    }

    logger.info('RPA job deletion requested', {
      jobId,
      userId: authResult.userId,
      userRole
    });

    // Get job details before deletion for logging
    const [job] = await database.drizzle
      .select()
      .from(rpaJobs)
      .where(eq(rpaJobs.id, jobId));

    if (!job) {
      return NextResponse.json({ error: 'Job not found' }, { status: 404 });
    }

    // Don't allow deletion of in-progress jobs
    if (job.status === 'in_progress') {
      return NextResponse.json({
        error: 'Cannot delete job that is currently in progress'
      }, { status: 400 });
    }

    // Delete the job
    await database.drizzle
      .delete(rpaJobs)
      .where(eq(rpaJobs.id, jobId));

    const responseTime = Date.now() - startTime;

    logger.info('RPA job deleted successfully', {
      jobId,
      ticketId: job.ticketId,
      drNumber: job.drNumber,
      status: job.status,
      deletedBy: authResult.userId,
      responseTime
    });

    return NextResponse.json({
      success: true,
      message: 'RPA job deleted successfully',
      deletedJob: {
        id: job.id,
        ticketId: job.ticketId,
        drNumber: job.drNumber,
        status: job.status
      },
      performance: {
        responseTime,
        timestamp: new Date().toISOString()
      }
    });

  } catch (error) {
    const responseTime = Date.now() - startTime;
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';

    logger.error('Error deleting RPA job', {
      error: errorMessage,
      responseTime
    });

    return NextResponse.json(
      {
        error: 'Failed to delete RPA job',
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