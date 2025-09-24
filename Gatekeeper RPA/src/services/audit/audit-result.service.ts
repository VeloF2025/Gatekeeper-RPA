import { v4 as uuidv4 } from 'uuid';
import { Database } from '@/database/database';
import { AuditResult, Photo, PhotoType, ComplianceCheckResult } from '@/types';
import { ComplianceService } from './compliance.service';
import { logger, auditLogger } from '@/utils/logger';

export interface AuditData {
  ticketId: string;
  propertyId: string;
  jobId: string;
  address: string;
  installationStatus: string;
  photos: Photo[];
  metadata?: {
    lastModified?: string;
    technician?: string;
    auditSource?: string;
    [key: string]: unknown;
  };
}

export interface AuditProcessingOptions {
  saveToDatabase?: boolean;
  generateReport?: boolean;
  includeScreenshots?: boolean;
  validatePhotos?: boolean;
}

export class AuditResultService {
  private complianceService: ComplianceService;
  private db: Database;

  constructor(db: Database, complianceService?: ComplianceService) {
    this.db = db;
    this.complianceService = complianceService || new ComplianceService();
  }

  /**
   * Generate audit result from audit data
   */
  async generateAuditResult(
    data: AuditData,
    options: AuditProcessingOptions = {
      saveToDatabase: true,
      generateReport: false,
      includeScreenshots: false,
      validatePhotos: true
    }
  ): Promise<AuditResult> {
    try {
      // Validate input data
      this.validateAuditData(data);

      // Process photos for compliance
      const complianceResult = this.complianceService.checkPhotoCompliance(data.photos);

      // Prepare audit details
      const auditDetails = this.prepareAuditDetails(data, complianceResult, options);

      // Generate audit result
      const auditResult: AuditResult = {
        id: uuidv4(),
        ticketId: data.ticketId,
        propertyId: data.propertyId,
        jobId: data.jobId,
        address: data.address,
        installationStatus: data.installationStatus,
        photosRequired: this.complianceService.getRequiredPhotoTypes(),
        photosFound: complianceResult.foundPhotos,
        photosMissing: complianceResult.missingPhotos,
        additionalPhotos: complianceResult.additionalPhotos,
        complianceScore: complianceResult.complianceScore,
        auditDetails,
        createdAt: new Date()
      };

      // Save to database if requested
      if (options.saveToDatabase) {
        await this.saveAuditResult(auditResult);
      }

      // Generate report if requested
      if (options.generateReport) {
        await this.generateAuditReport(auditResult);
      }

      logger.info('Audit result generated successfully', {
        auditId: auditResult.id,
        ticketId: data.ticketId,
        complianceScore: auditResult.complianceScore,
        photosProcessed: data.photos.length
      });

      return auditResult;

    } catch (error) {
      logger.error('Failed to generate audit result', {
        error: error instanceof Error ? error.message : error,
        ticketId: data.ticketId,
        photosCount: data.photos.length
      });

      throw error;
    }
  }

  /**
   * Save audit result to database
   */
  private async saveAuditResult(auditResult: AuditResult): Promise<void> {
    try {
      const query = `
        INSERT INTO audit_results (
          id, ticket_id, property_id, job_id, address, installation_status,
          photos_required, photos_found, photos_missing, compliance_score, audit_details, created_at
        ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
      `;

      await this.db.query(query, [
        auditResult.id,
        auditResult.ticketId,
        auditResult.propertyId,
        auditResult.jobId,
        auditResult.address,
        auditResult.installationStatus,
        auditResult.photosRequired,
        auditResult.photosFound,
        auditResult.photosMissing,
        auditResult.complianceScore,
        JSON.stringify(auditResult.auditDetails),
        auditResult.createdAt
      ]);

      auditLogger.info('Audit result saved to database', {
        auditId: auditResult.id,
        ticketId: auditResult.ticketId,
        complianceScore: auditResult.complianceScore
      });

    } catch (error) {
      logger.error('Failed to save audit result to database', {
        error: error instanceof Error ? error.message : error,
        auditId: auditResult.id
      });

      throw error;
    }
  }

  /**
   * Get audit result by ID
   */
  async getAuditResult(auditId: string): Promise<AuditResult | null> {
    try {
      // Validate audit ID format
      if (!uuidv4.validate(auditId)) {
        throw new Error('Invalid audit ID format');
      }

      const query = `
        SELECT * FROM audit_results
        WHERE id = $1
      `;

      const result = await this.db.query(query, [auditId]);

      if (result.length === 0) {
        return null;
      }

      const row = result[0];
      return {
        id: row.id,
        ticketId: row.ticket_id,
        propertyId: row.property_id,
        jobId: row.job_id,
        address: row.address,
        installationStatus: row.installation_status,
        photosRequired: row.photos_required,
        photosFound: row.photos_found,
        photosMissing: row.photos_missing,
        complianceScore: parseFloat(row.compliance_score),
        auditDetails: row.audit_details,
        createdAt: new Date(row.created_at)
      };

    } catch (error) {
      logger.error('Failed to get audit result', {
        error: error instanceof Error ? error.message : error,
        auditId
      });

      throw error;
    }
  }

  /**
   * Get audit result by ticket ID
   */
  async getAuditResultByTicketId(ticketId: string): Promise<AuditResult | null> {
    try {
      // Validate ticket ID format
      if (!uuidv4.validate(ticketId)) {
        throw new Error('Invalid ticket ID format');
      }

      const query = `
        SELECT * FROM audit_results
        WHERE ticket_id = $1
        ORDER BY created_at DESC
        LIMIT 1
      `;

      const result = await this.db.query(query, [ticketId]);

      if (result.length === 0) {
        return null;
      }

      const row = result[0];
      return {
        id: row.id,
        ticketId: row.ticket_id,
        propertyId: row.property_id,
        jobId: row.job_id,
        address: row.address,
        installationStatus: row.installation_status,
        photosRequired: row.photos_required,
        photosFound: row.photos_found,
        photosMissing: row.photos_missing,
        complianceScore: parseFloat(row.compliance_score),
        auditDetails: row.audit_details,
        createdAt: new Date(row.created_at)
      };

    } catch (error) {
      logger.error('Failed to get audit result by ticket ID', {
        error: error instanceof Error ? error.message : error,
        ticketId
      });

      throw error;
    }
  }

  /**
   * Get audit results with filtering and pagination
   */
  async getAuditResults(
    filters: {
      ticketId?: string;
      propertyId?: string;
      jobId?: string;
      complianceScore?: { min?: number; max?: number };
      startDate?: Date;
      endDate?: Date;
    },
    page: number = 1,
    limit: number = 50
  ): Promise<{
    results: AuditResult[];
    total: number;
    page: number;
    totalPages: number;
  }> {
    try {
      // Validate pagination
      if (page < 1) throw new Error('Page must be greater than 0');
      if (limit < 1 || limit > 100) throw new Error('Limit must be between 1 and 100');

      const offset = (page - 1) * limit;
      const whereConditions: string[] = [];
      const countParams: unknown[] = [];
      const dataParams: unknown[] = [];

      // Build filter conditions
      if (filters.ticketId) {
        if (!uuidv4.validate(filters.ticketId)) {
          throw new Error('Invalid ticket ID format');
        }
        whereConditions.push(`ticket_id = $${countParams.length + 1}`);
        countParams.push(filters.ticketId);
        dataParams.push(filters.ticketId);
      }

      if (filters.propertyId) {
        whereConditions.push(`property_id = $${countParams.length + 1}`);
        countParams.push(filters.propertyId);
        dataParams.push(filters.propertyId);
      }

      if (filters.jobId) {
        whereConditions.push(`job_id = $${countParams.length + 1}`);
        countParams.push(filters.jobId);
        dataParams.push(filters.jobId);
      }

      if (filters.complianceScore) {
        if (filters.complianceScore.min !== undefined) {
          whereConditions.push(`compliance_score >= $${countParams.length + 1}`);
          countParams.push(filters.complianceScore.min);
          dataParams.push(filters.complianceScore.min);
        }

        if (filters.complianceScore.max !== undefined) {
          whereConditions.push(`compliance_score <= $${countParams.length + 1}`);
          countParams.push(filters.complianceScore.max);
          dataParams.push(filters.complianceScore.max);
        }
      }

      if (filters.startDate) {
        whereConditions.push(`created_at >= $${countParams.length + 1}`);
        countParams.push(filters.startDate);
        dataParams.push(filters.startDate);
      }

      if (filters.endDate) {
        whereConditions.push(`created_at <= $${countParams.length + 1}`);
        countParams.push(filters.endDate);
        dataParams.push(filters.endDate);
      }

      // Build where clause
      const whereClause = whereConditions.length > 0
        ? 'WHERE ' + whereConditions.join(' AND ')
        : '';

      // Get total count
      const countQuery = `SELECT COUNT(*) as total FROM audit_results ${whereClause}`;
      const countResult = await this.db.query(countQuery, countParams);
      const total = parseInt(countResult[0].total, 10);

      // Get paginated results
      dataParams.push(limit, offset);
      const dataQuery = `
        SELECT * FROM audit_results
        ${whereClause}
        ORDER BY created_at DESC
        LIMIT $${dataParams.length - 1} OFFSET $${dataParams.length}
      `;

      const results = await this.db.query(dataQuery, dataParams);
      const mappedResults = results.map(row => ({
        id: row.id,
        ticketId: row.ticket_id,
        propertyId: row.property_id,
        jobId: row.job_id,
        address: row.address,
        installationStatus: row.installation_status,
        photosRequired: row.photos_required,
        photosFound: row.photos_found,
        photosMissing: row.photos_missing,
        complianceScore: parseFloat(row.compliance_score),
        auditDetails: row.audit_details,
        createdAt: new Date(row.created_at)
      }));

      const totalPages = Math.ceil(total / limit);

      return {
        results: mappedResults,
        total,
        page,
        totalPages
      };

    } catch (error) {
      logger.error('Failed to get audit results', {
        error: error instanceof Error ? error.message : error,
        filters,
        page,
        limit
      });

      throw error;
    }
  }

  /**
   * Get audit statistics
   */
  async getAuditStatistics(
    startDate?: Date,
    endDate?: Date
  ): Promise<{
    totalAudits: number;
    averageComplianceScore: number;
    compliantAudits: number;
    nonCompliantAudits: number;
    photoStatistics: {
      totalPhotos: number;
      averagePhotosPerAudit: number;
      mostCommonMissingPhotos: Array<{ type: PhotoType; count: number }>;
    };
    topIssues: Array<{ issue: string; count: number }>;
  }> {
    try {
      let dateFilter = '';
      const params: unknown[] = [];

      if (startDate || endDate) {
        const conditions: string[] = [];

        if (startDate) {
          conditions.push('created_at >= $' + (params.length + 1));
          params.push(startDate);
        }

        if (endDate) {
          conditions.push('created_at <= $' + (params.length + 1));
          params.push(endDate);
        }

        dateFilter = 'WHERE ' + conditions.join(' AND ');
      }

      // Get basic statistics
      const statsQuery = `
        SELECT
          COUNT(*) as total_audits,
          AVG(compliance_score) as avg_compliance_score,
          SUM(CASE WHEN compliance_score >= 90 THEN 1 ELSE 0 END) as compliant_audits,
          SUM(CASE WHEN compliance_score < 90 THEN 1 ELSE 0 END) as non_compliant_audits
        FROM audit_results
        ${dateFilter}
      `;

      const statsResult = await this.db.query(statsQuery, params);

      // Get photo statistics
      const photoStatsQuery = `
        SELECT
          AVG(JSON_ARRAY_LENGTH(audit_details->'photos')) as avg_photos,
          SUM(JSON_ARRAY_LENGTH(audit_details->'photos')) as total_photos
        FROM audit_results
        ${dateFilter}
      `;

      const photoStatsResult = await this.db.query(photoStatsQuery, params);

      // Get most common missing photos
      const missingPhotosQuery = `
        SELECT
          UNNEST(photos_missing) as photo_type,
          COUNT(*) as count
        FROM audit_results
        ${dateFilter}
        GROUP BY photo_type
        ORDER BY count DESC
        LIMIT 5
      `;

      const missingPhotosResult = await this.db.query(missingPhotosQuery, params);

      return {
        totalAudits: parseInt(statsResult[0].total_audits, 10),
        averageComplianceScore: parseFloat(statsResult[0].avg_compliance_score || 0),
        compliantAudits: parseInt(statsResult[0].compliant_audits, 10),
        nonCompliantAudits: parseInt(statsResult[0].non_compliant_audits, 10),
        photoStatistics: {
          totalPhotos: parseInt(photoStatsResult[0].total_photos || 0, 10),
          averagePhotosPerAudit: parseFloat(photoStatsResult[0].avg_photos || 0),
          mostCommonMissingPhotos: missingPhotosResult.map(row => ({
            type: row.photo_type,
            count: parseInt(row.count, 10)
          }))
        },
        topIssues: [] // Can be expanded based on audit_details analysis
      };

    } catch (error) {
      logger.error('Failed to get audit statistics', {
        error: error instanceof Error ? error.message : error,
        startDate,
        endDate
      });

      throw error;
    }
  }

  /**
   * Generate audit report
   */
  private async generateAuditReport(auditResult: AuditResult): Promise<string> {
    const report = `
# Audit Report

**Audit ID**: ${auditResult.id}
**Ticket ID**: ${auditResult.ticketId}
**Property ID**: ${auditResult.propertyId}
**Job ID**: ${auditResult.jobId}
**Address**: ${auditResult.address}

## Results

**Installation Status**: ${auditResult.installationStatus}
**Compliance Score**: ${auditResult.complianceScore}%
**Audit Date**: ${auditResult.createdAt.toLocaleString()}

## Photo Requirements

### Required Photos (${auditResult.photosRequired.length})
${auditResult.photosRequired.map(type => `- ${type}`).join('\n')}

### Found Photos (${auditResult.photosFound.length})
${auditResult.photosFound.map(type => `- ${type}`).join('\n')}

### Missing Photos (${auditResult.photosMissing.length})
${auditResult.photosMissing.length > 0
  ? auditResult.photosMissing.map(type => `- ⚠️ ${type}`).join('\n')
  : 'None ✅'
}

### Additional Photos (${auditResult.additionalPhotos.length})
${auditResult.additionalPhotos.length > 0
  ? auditResult.additionalPhotos.map(type => `- ${type}`).join('\n')
  : 'None'
}

## Summary

${auditResult.complianceScore >= 90
  ? '🎉 **Excellent**: All required photos present and high compliance score!'
  : auditResult.complianceScore >= 70
  ? '✅ **Good**: Most requirements met with minor improvements needed.'
  : auditResult.complianceScore >= 50
  ? '⚠️ **Needs Attention**: Several missing photos or quality issues.'
  : '❌ **Critical**: Significant compliance issues requiring immediate attention.'
}

Generated at: ${new Date().toISOString()}
`;

    logger.info('Audit report generated', {
      auditId: auditResult.id,
      reportLength: report.length
    });

    return report;
  }

  /**
   * Validate audit data
   */
  private validateAuditData(data: AuditData): void {
    if (!data.ticketId || !uuidv4.validate(data.ticketId)) {
      throw new Error('Valid ticket ID is required');
    }

    if (!data.propertyId || data.propertyId.trim() === '') {
      throw new Error('Property ID is required');
    }

    if (!data.jobId || data.jobId.trim() === '') {
      throw new Error('Job ID is required');
    }

    if (!data.address || data.address.trim() === '') {
      throw new Error('Address is required');
    }

    if (!data.installationStatus || data.installationStatus.trim() === '') {
      throw new Error('Installation status is required');
    }

    if (!Array.isArray(data.photos)) {
      throw new Error('Photos must be an array');
    }

    // Validate each photo
    for (const photo of data.photos) {
      if (!photo.id || !photo.type || !photo.url || !photo.uploadTime) {
        throw new Error('Each photo must have id, type, url, and uploadTime');
      }
    }
  }

  /**
   * Prepare audit details
   */
  private prepareAuditDetails(
    data: AuditData,
    complianceResult: ComplianceCheckResult,
    options: AuditProcessingOptions
  ) {
    return {
      totalPhotos: data.photos.length,
      auditTimestamp: new Date().toISOString(),
      photos: data.photos.map(photo => ({
        id: photo.id,
        type: photo.type,
        url: photo.url,
        uploadTime: photo.uploadTime,
        fileSize: photo.fileSize,
        metadata: photo.metadata || {}
      })),
      compliance: complianceResult.issues.map(issue => ({
        type: issue.type,
        passed: issue.type !== 'missing',
        message: issue.description,
        score: issue.severity === 'high' ? 0 : issue.severity === 'medium' ? 5 : 10
      })),
      metadata: {
        ...data.metadata,
        auditSource: options.includeScreenshots ? 'rpa_with_screenshots' : 'rpa',
        validationEnabled: options.validatePhotos,
        generatedAt: new Date().toISOString()
      }
    };
  }
}