import { NextRequest, NextResponse } from 'next/server';
import { whatsappService } from '@/services/whatsappService';
import { WhatiTicketService } from '@/services/whatiTicketService';
import logger from '@/lib/logger';
import { securityService } from '@/services/securityService';

const whatiTicketService = new WhatiTicketService();

export async function GET(request: NextRequest) {
  try {
    const searchParams = request.nextUrl.searchParams;
    const hubChallenge = searchParams.get('hub.challenge');
    const hubVerifyToken = searchParams.get('hub.verify_token');

    logger.info('WhatsApp webhook verification request', {
      hasChallenge: !!hubChallenge,
      hasVerifyToken: !!hubVerifyToken
    });

    // Validate webhook request
    if (!hubChallenge || !hubVerifyToken) {
      logger.warn('Missing webhook verification parameters');
      return NextResponse.json(
        { error: 'Missing verification parameters' },
        { status: 400 }
      );
    }

    // Verify the webhook
    const isValid = await whatsappService.validateWebhub(hubChallenge, hubVerifyToken);
    if (!isValid) {
      logger.warn('Webhook verification failed');
      return NextResponse.json(
        { error: 'Verification failed' },
        { status: 403 }
      );
    }

    logger.info('WhatsApp webhook verified successfully');

    // Return the challenge for verification
    return new NextResponse(hubChallenge, {
      status: 200,
      headers: {
        'Content-Type': 'text/plain'
      }
    });
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    logger.error('WhatsApp webhook verification error', { error: errorMessage });

    await securityService.logAuditTrail({
      type: 'whatsapp_webhook_verification_failed',
      data: {
        error: errorMessage,
        url: request.url
      },
      timestamp: Date.now(),
      userId: 'system'
    });

    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const requestId = generateRequestId();

    logger.info('WhatsApp webhook received', {
      requestId,
      objectType: body.object,
      entryCount: body.entry?.length || 0
    });

    // Validate request structure
    if (!body.object || !body.entry || !Array.isArray(body.entry)) {
      logger.warn('Invalid webhook structure', { requestId });
      return NextResponse.json(
        { error: 'Invalid webhook structure' },
        { status: 400 }
      );
    }

    // Process each entry
    const processingResults = [];
    for (const entry of body.entry) {
      if (entry.changes && Array.isArray(entry.changes)) {
        for (const change of entry.changes) {
          if (change.value?.messages && Array.isArray(change.value.messages)) {
            for (const message of change.value.messages) {
              try {
                const result = await processWhatsAppMessage(message, entry.id, change.value);
                processingResults.push(result);
              } catch (error) {
                logger.error('Failed to process WhatsApp message', {
                  requestId,
                  messageId: message.id,
                  error: error instanceof Error ? error.message : 'Unknown error'
                });
                processingResults.push({
                  messageId: message.id,
                  success: false,
                  error: error instanceof Error ? error.message : 'Unknown error'
                });
              }
            }
          }
        }
      }
    }

    // Log webhook processing
    await securityService.logAuditTrail({
      type: 'whatsapp_webhook_processed',
      data: {
        requestId,
        totalMessages: processingResults.length,
        successfulMessages: processingResults.filter(r => r.success).length,
        failedMessages: processingResults.filter(r => !r.success).length
      },
      timestamp: Date.now(),
      userId: 'system'
    });

    return NextResponse.json({
      success: true,
      requestId,
      processed: processingResults.length,
      successful: processingResults.filter(r => r.success).length,
      failed: processingResults.filter(r => !r.success).length
    });
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    logger.error('WhatsApp webhook processing error', { error: errorMessage });

    await securityService.logAuditTrail({
      type: 'whatsapp_webhook_processing_failed',
      data: {
        error: errorMessage,
        url: request.url
      },
      timestamp: Date.now(),
      userId: 'system'
    });

    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}

async function processWhatsAppMessage(message: any, _entryId: string, _metadata: any) {
  const messageId = message.id;
  const from = message.from;
  const timestamp = parseInt(message.timestamp) * 1000; // Convert to milliseconds

  logger.info('Processing WhatsApp message', {
    messageId,
    from,
    messageType: message.type,
    timestamp
  });

  // Extract message content based on type
  let content = '';
  let mediaItems = [];

  switch (message.type) {
    case 'text':
      content = message.text?.body || '';
      break;
    case 'image':
      content = message.image?.caption || 'Image message';
      mediaItems.push({
        id: message.image.id,
        type: 'image',
        mimeType: message.image.mime_type,
        fileSize: 0, // Will be fetched when downloading
        caption: message.image.caption
      });
      break;
    case 'document':
      content = message.document?.caption || `Document: ${message.document.filename}`;
      mediaItems.push({
        id: message.document.id,
        type: 'document',
        mimeType: message.document.mime_type,
        fileSize: 0,
        caption: message.document.caption
      });
      break;
    case 'audio':
      content = 'Audio message';
      mediaItems.push({
        id: message.audio.id,
        type: 'audio',
        mimeType: message.audio.mime_type,
        fileSize: 0
      });
      break;
    case 'video':
      content = message.video?.caption || 'Video message';
      mediaItems.push({
        id: message.video.id,
        type: 'video',
        mimeType: message.video.mime_type,
        fileSize: 0,
        caption: message.video.caption
      });
      break;
    default:
      content = `Unsupported message type: ${message.type}`;
  }

  // Create WhatiTicket message object
  const whatiTicketMessage = {
    id: messageId,
    from,
    content,
    timestamp,
    messageType: message.type,
    media: mediaItems.length > 0 ? mediaItems : undefined
  };

  // Process the complete workflow
  const workflowResult = await whatiTicketService.processCompleteWorkflow(whatiTicketMessage);

  logger.info('WhatsApp message workflow completed', {
    messageId,
    success: workflowResult.success,
    ticketId: workflowResult.ticketId,
    confidence: workflowResult.confidence
  });

  return {
    messageId,
    success: workflowResult.success,
    ticketId: workflowResult.ticketId,
    confidence: workflowResult.confidence,
    auditTrailLength: workflowResult.auditTrail.length,
    responseSent: workflowResult.responseSent
  };
}

function generateRequestId(): string {
  return `req_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
}