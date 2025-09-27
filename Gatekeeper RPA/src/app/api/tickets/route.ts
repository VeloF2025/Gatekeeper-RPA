import { NextRequest, NextResponse } from 'next/server';
import { ticketService } from '@/services/ticket/ticket.service';
import { createTicketSchema, ticketFiltersSchema } from '@/validation/schemas';
import { validateBody, authMiddleware, requireRole, rateLimit, securityHeaders, handleError } from '@/middleware/auth';
import { logger } from '@/utils/logger';

/**
 * GET /api/tickets
 * Get tickets with filtering and pagination
 */
export async function GET(request: NextRequest) {
  try {
    // Apply rate limiting
    const rateLimitResult = await rateLimit(100, 15 * 60 * 1000)(request); // 100 requests per 15 minutes
    if (rateLimitResult instanceof NextResponse) {
      return rateLimitResult;
    }

    // Authenticate user
    const authResult = await authMiddleware(request);
    if (authResult instanceof NextResponse) {
      return authResult;
    }

    // Parse query parameters
    const searchParams = request.nextUrl.searchParams;

    const filters = {
      status: searchParams.get('status') ? [searchParams.get('status')].filter(Boolean) : undefined,
      priority: searchParams.get('priority') ? [searchParams.get('priority')].filter(Boolean) : undefined,
      assignedTo: searchParams.get('assignedTo') || undefined,
      technicianNumber: searchParams.get('technicianNumber') || undefined,
      drNumber: searchParams.get('drNumber') || undefined,
      dateFrom: searchParams.get('dateFrom') ? new Date(searchParams.get('dateFrom')!) : undefined,
      dateTo: searchParams.get('dateTo') ? new Date(searchParams.get('dateTo')!) : undefined,
      page: parseInt(searchParams.get('page') || '1'),
      limit: parseInt(searchParams.get('limit') || '50'),
      sortBy: (searchParams.get('sortBy') as any) || 'createdAt',
      sortOrder: (searchParams.get('sortOrder') as any) || 'desc',
    };

    // Validate filters
    const validatedFilters = ticketFiltersSchema.parse(filters);

    // Transform filters to match expected types
    const transformedFilters = {
      ...validatedFilters,
      status: validatedFilters.status
        ? Array.isArray(validatedFilters.status)
          ? validatedFilters.status
          : [validatedFilters.status]
        : undefined,
      priority: validatedFilters.priority
        ? Array.isArray(validatedFilters.priority)
          ? validatedFilters.priority
          : [validatedFilters.priority]
        : undefined,
    };

    // Get tickets
    const tickets = await ticketService.getTickets(transformedFilters);

    logger.info('Tickets retrieved successfully', {
      userId: authResult.userId,
      filters: validatedFilters,
      count: tickets.data.length,
      total: tickets.pagination.total
    });

    // Return success response
    const response = NextResponse.json({
      success: true,
      data: tickets,
      message: `Retrieved ${tickets.data.length} tickets`,
      timestamp: new Date()
    });

    // Apply security headers
    return securityHeaders(response);
  } catch (error) {
    return handleError(error, 'Get tickets');
  }
}

/**
 * POST /api/tickets
 * Create a new ticket
 */
export async function POST(request: NextRequest) {
  try {
    // Apply rate limiting
    const rateLimitResult = await rateLimit(50, 15 * 60 * 1000)(request); // 50 requests per 15 minutes
    if (rateLimitResult instanceof NextResponse) {
      return rateLimitResult;
    }

    // Authenticate and authorize user
    const authResult = await requireRole(['admin', 'manager', 'user'])(request);
    if (authResult instanceof NextResponse) {
      return authResult;
    }

    // Validate request body
    const bodyResult = await validateBody(createTicketSchema)(request);
    if (bodyResult instanceof NextResponse) {
      return bodyResult;
    }

    const ticketData = bodyResult;

    // Create ticket
    const ticket = await ticketService.createTicket({
      drNumber: ticketData.drNumber,
      technicianNumber: ticketData.technicianNumber,
      technicianName: ticketData.technicianName || 'Unknown',
      messageContent: ticketData.messageContent || '',
      priority: ticketData.priority as any
    });

    logger.info('Ticket created successfully', {
      userId: authResult.userId,
      userRole: authResult.userRole,
      ticketId: ticket.id,
      ticketNumber: ticket.ticketNumber,
      drNumber: ticket.drNumber,
      technicianNumber: ticket.technicianNumber
    });

    // Return success response
    const response = NextResponse.json({
      success: true,
      data: ticket,
      message: 'Ticket created successfully',
      timestamp: new Date()
    }, { status: 201 });

    // Apply security headers
    return securityHeaders(response);
  } catch (error) {
    // Handle specific error cases
    if (error instanceof Error && error.message.includes('already exists')) {
      return securityHeaders(
        NextResponse.json(
          {
            success: false,
            error: 'Ticket conflict',
            message: error.message,
            timestamp: new Date()
          },
          { status: 409 }
        )
      );
    }

    return handleError(error, 'Create ticket');
  }
}