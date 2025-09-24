import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest';
import { ComplianceService } from '@/services/audit/compliance.service';
import { PhotoType, Photo } from '@/types';

// Mock logger
vi.mock('@/utils/logger', () => ({
  logger: {
    info: vi.fn(),
    warn: vi.fn(),
    error: vi.fn(),
    debug: vi.fn()
  }
}));

describe('ComplianceService', () => {
  let service: ComplianceService;

  beforeEach(() => {
    vi.clearAllMocks();
    service = new ComplianceService();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  describe('checkPhotoCompliance', () => {
    it('should pass compliance with all required photos', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 },
        { id: '3', ticketId: 'ticket-1', type: PhotoType.TERMINATION, url: 'photo3.jpg', uploadTime: new Date(), fileSize: 1536000 },
        { id: '4', ticketId: 'ticket-1', type: PhotoType.PROPERTY, url: 'photo4.jpg', uploadTime: new Date(), fileSize: 3072000 }
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(true);
      expect(result.complianceScore).toBe(100);
      expect(result.missingPhotos).toHaveLength(0);
      expect(result.foundPhotos).toHaveLength(4);
      expect(result.issues).toHaveLength(0);
    });

    it('should fail compliance with missing photos', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 }
        // Missing termination and property photos
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(false);
      expect(result.complianceScore).toBe(50);
      expect(result.missingPhotos).toEqual(expect.arrayContaining([PhotoType.TERMINATION, PhotoType.PROPERTY]));
      expect(result.foundPhotos).toHaveLength(2);
      expect(result.issues).toHaveLength(2);

      // Check that missing photo issues are high severity
      const missingIssues = result.issues.filter(issue => issue.type === 'missing');
      expect(missingIssues).toHaveLength(2);
      missingIssues.forEach(issue => {
        expect(issue.severity).toBe('high');
      });
    });

    it('should handle no photos', () => {
      const photos: Photo[] = [];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(false);
      expect(result.complianceScore).toBe(0);
      expect(result.missingPhotos).toHaveLength(4);
      expect(result.foundPhotos).toHaveLength(0);
      expect(result.issues).toHaveLength(4); // All required photos missing
    });

    it('should detect invalid photo types', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: 'invalid_type' as PhotoType, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 }
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(false);
      expect(result.complianceScore).toBe(25); // Only 1 out of 4 required photos
      expect(result.issues).toHaveLength(4); // 3 missing + 1 invalid type

      const invalidTypeIssue = result.issues.find(issue => issue.type === 'invalid');
      expect(invalidTypeIssue).toBeDefined();
      expect(invalidTypeIssue?.severity).toBe('high');
    });

    it('should detect duplicate photos', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 },
        { id: '3', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo3.jpg', uploadTime: new Date(), fileSize: 1536000 },
        { id: '4', ticketId: 'ticket-1', type: PhotoType.TERMINATION, url: 'photo4.jpg', uploadTime: new Date(), fileSize: 3072000 },
        { id: '5', ticketId: 'ticket-1', type: PhotoType.PROPERTY, url: 'photo5.jpg', uploadTime: new Date(), fileSize: 4096000 }
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(true);
      expect(result.complianceScore).toBe(100);
      expect(result.issues).toHaveLength(1);

      const duplicateIssue = result.issues.find(issue => issue.type === 'duplicate');
      expect(duplicateIssue).toBeDefined();
      expect(duplicateIssue?.photoType).toBe(PhotoType.TRENCH);
      expect(duplicateIssue?.severity).toBe('medium');
    });

    it('should detect quality issues', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 500 }, // Too small
        { id: '2', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 15 * 1024 * 1024 }, // Too large
        { id: '3', ticketId: 'ticket-1', type: PhotoType.TERMINATION, url: 'invalid-url', uploadTime: new Date(), fileSize: 1024000 }, // Invalid URL
        { id: '4', ticketId: 'ticket-1', type: PhotoType.PROPERTY, url: 'photo4.jpg', uploadTime: new Date('2024-01-01'), fileSize: 1024000 } // Too old
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(false);
      expect(result.complianceScore).toBe(100); // All required photos present
      expect(result.qualityIssues).toHaveLength(4);

      expect(result.issues).toEqual(
        expect.arrayContaining([
          expect.objectContaining({ type: 'quality', photoType: PhotoType.TRENCH }),
          expect.objectContaining({ type: 'quality', photoType: PhotoType.ONT }),
          expect.objectContaining({ type: 'invalid', photoType: PhotoType.TERMINATION }),
          expect.objectContaining({ type: 'quality', photoType: PhotoType.PROPERTY })
        ])
      );
    });

    it('should handle additional photos correctly', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 },
        { id: '3', ticketId: 'ticket-1', type: PhotoType.TERMINATION, url: 'photo3.jpg', uploadTime: new Date(), fileSize: 1536000 },
        { id: '4', ticketId: 'ticket-1', type: PhotoType.PROPERTY, url: 'photo4.jpg', uploadTime: new Date(), fileSize: 3072000 },
        { id: '5', ticketId: 'ticket-1', type: PhotoType.ADDITIONAL, url: 'photo5.jpg', uploadTime: new Date(), fileSize: 1024000 }
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.isCompliant).toBe(true);
      expect(result.complianceScore).toBe(100);
      expect(result.additionalPhotos).toHaveLength(1);
      expect(result.additionalPhotos).toContain(PhotoType.ADDITIONAL);
    });

    it('should generate appropriate recommendations', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 }
        // Missing termination and property
      ];

      const result = service.checkPhotoCompliance(photos);

      expect(result.recommendations).toEqual(
        expect.arrayContaining([
          expect.stringContaining('2 required photo(s) missing'),
          expect.stringContaining('Add Termination Photo'),
          expect.stringContaining('Add Property Photo')
        ])
      );
    });

    it('should validate input array', () => {
      expect(() => service.checkPhotoCompliance(null as any)).toThrow('Photos must be an array');
      expect(() => service.checkPhotoCompliance('invalid' as any)).toThrow('Photos must be an array');
    });
  });

  describe('validatePhotoType', () => {
    it('should validate known photo types', () => {
      expect(service.validatePhotoType('trench')).toBe(true);
      expect(service.validatePhotoType('ONT')).toBe(true);
      expect(service.validatePhotoType('termination')).toBe(true);
      expect(service.validatePhotoType('property')).toBe(true);
      expect(service.validatePhotoType('additional')).toBe(true);
    });

    it('should reject unknown photo types', () => {
      expect(service.validatePhotoType('unknown')).toBe(false);
      expect(service.validatePhotoType('')).toBe(false);
      expect(service.validatePhotoType(null as any)).toBe(false);
    });
  });

  describe('calculateComplianceScore', () => {
    it('should calculate score correctly', () => {
      const requiredPhotos = [PhotoType.TRENCH, PhotoType.ONT, PhotoType.TERMINATION, PhotoType.PROPERTY];
      const foundPhotos = [PhotoType.TRENCH, PhotoType.ONT];

      const score = service.calculateComplianceScore(requiredPhotos, foundPhotos);

      expect(score).toBe(50);
    });

    it('should handle empty arrays', () => {
      const score = service.calculateComplianceScore([], []);

      expect(score).toBe(100); // No requirements means 100% compliant
    });

    it('should handle all photos found', () => {
      const requiredPhotos = [PhotoType.TRENCH, PhotoType.ONT, PhotoType.TERMINATION, PhotoType.PROPERTY];
      const foundPhotos = [...requiredPhotos];

      const score = service.calculateComplianceScore(requiredPhotos, foundPhotos);

      expect(score).toBe(100);
    });
  });

  describe('getPhotoRequirements', () => {
    it('should return requirements for valid photo type', () => {
      const requirements = service.getPhotoRequirements(PhotoType.TRENCH);

      expect(requirements).toMatchObject({
        name: 'Trench Photo',
        description: expect.stringContaining('trench'),
        required: true,
        qualityStandards: expect.arrayContaining([
          'Clear visibility of trench depth',
          'Proper cable placement',
          'Safety measures visible'
        ])
      });
    });

    it('should return null for invalid photo type', () => {
      const requirements = service.getPhotoRequirements('invalid' as PhotoType);

      expect(requirements).toBeNull();
    });
  });

  describe('getRequiredPhotoTypes', () => {
    it('should return all required photo types', () => {
      const requiredTypes = service.getRequiredPhotoTypes();

      expect(requiredTypes).toHaveLength(4);
      expect(requiredTypes).toContain(PhotoType.TRENCH);
      expect(requiredTypes).toContain(PhotoType.ONT);
      expect(requiredTypes).toContain(PhotoType.TERMINATION);
      expect(requiredTypes).toContain(PhotoType.PROPERTY);
      expect(requiredTypes).not.toContain(PhotoType.ADDITIONAL);
    });
  });

  describe('isPhotoRequired', () => {
    it('should correctly identify required photos', () => {
      expect(service.isPhotoRequired(PhotoType.TRENCH)).toBe(true);
      expect(service.isPhotoRequired(PhotoType.ONT)).toBe(true);
      expect(service.isPhotoRequired(PhotoType.TERMINATION)).toBe(true);
      expect(service.isPhotoRequired(PhotoType.PROPERTY)).toBe(true);
      expect(service.isPhotoRequired(PhotoType.ADDITIONAL)).toBe(false);
    });
  });

  describe('validatePhoto', () => {
    const validPhoto: Photo = {
      id: 'photo-1',
      ticketId: 'ticket-1',
      type: PhotoType.TRENCH,
      url: 'https://example.com/photo1.jpg',
      uploadTime: new Date().toISOString(),
      fileSize: 1024000
    };

    it('should validate complete photo', () => {
      const result = service.validatePhoto(validPhoto);

      expect(result.isValid).toBe(true);
      expect(result.issues).toHaveLength(0);
    });

    it('should detect missing required fields', () => {
      const incompletePhoto = { ...validPhoto };
      delete incompletePhoto.id;

      const result = service.validatePhoto(incompletePhoto);

      expect(result.isValid).toBe(false);
      expect(result.issues).toContain('Photo ID is required');
    });

    it('should validate photo type', () => {
      const invalidTypePhoto = { ...validPhoto, type: 'invalid' as PhotoType };

      const result = service.validatePhoto(invalidTypePhoto);

      expect(result.isValid).toBe(false);
      expect(result.issues).toContain('Invalid photo type: invalid');
    });

    it('should validate URL format', () => {
      const invalidUrlPhoto = { ...validPhoto, url: 'invalid-url' };

      const result = service.validatePhoto(invalidUrlPhoto);

      expect(result.isValid).toBe(false);
      expect(result.issues).toContain('Invalid photo URL format');
    });

    it('should validate file size', () => {
      const invalidSizePhoto = { ...validPhoto, fileSize: -1 };

      const result = service.validatePhoto(invalidSizePhoto);

      expect(result.isValid).toBe(false);
      expect(result.issues).toContain('File size must be greater than 0');
    });

    it('should generate warnings for large files', () => {
      const largeFilePhoto = { ...validPhoto, fileSize: 60 * 1024 * 1024 }; // 60MB

      const result = service.validatePhoto(largeFilePhoto);

      expect(result.isValid).toBe(true);
      expect(result.warnings).toContain('Large file size detected. Consider compressing.');
    });

    it('should validate upload time format', () => {
      const invalidTimePhoto = { ...validPhoto, uploadTime: 'invalid-date' };

      const result = service.validatePhoto(invalidTimePhoto);

      expect(result.isValid).toBe(false);
      expect(result.issues).toContain('Invalid upload time format');
    });

    it('should warn about future upload times', () => {
      const futureTimePhoto = { ...validPhoto, uploadTime: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString() };

      const result = service.validatePhoto(futureTimePhoto);

      expect(result.isValid).toBe(true);
      expect(result.warnings).toContain('Upload time is in the future');
    });
  });

  describe('getComplianceStats', () => {
    it('should calculate comprehensive statistics', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 },
        { id: '3', ticketId: 'ticket-1', type: PhotoType.ADDITIONAL, url: 'photo3.jpg', uploadTime: new Date(), fileSize: 1536000 }
      ];

      const stats = service.getComplianceStats(photos);

      expect(stats.totalPhotos).toBe(3);
      expect(stats.requiredPhotosFound).toBe(2);
      expect(stats.additionalPhotos).toBe(1);
      expect(stats.uniqueTypes).toBe(3);
      expect(stats.averageFileSize).toBe(1546666); // Average of 3 files
      expect(stats.complianceScore).toBe(50); // 2 out of 4 required photos
    });

    it('should handle empty photo array', () => {
      const stats = service.getComplianceStats([]);

      expect(stats.totalPhotos).toBe(0);
      expect(stats.requiredPhotosFound).toBe(0);
      expect(stats.additionalPhotos).toBe(0);
      expect(stats.uniqueTypes).toBe(0);
      expect(stats.averageFileSize).toBe(0);
      expect(stats.complianceScore).toBe(0);
    });
  });

  describe('bulkValidatePhotos', () => {
    it('should validate multiple photos', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: 'invalid' as PhotoType, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 },
        { id: '3', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo3.jpg', uploadTime: new Date(), fileSize: 1536000 }
      ];

      const result = service.bulkValidatePhotos(photos);

      expect(result.validPhotos).toHaveLength(2);
      expect(result.invalidPhotos).toHaveLength(1);
      expect(result.validationResults).toHaveLength(3);

      const invalidResult = result.validationResults.find(r => !r.isValid);
      expect(invalidResult).toBeDefined();
      expect(invalidResult?.issues).toContain('Invalid photo type: invalid');
    });

    it('should handle all valid photos', () => {
      const photos: Photo[] = [
        { id: '1', ticketId: 'ticket-1', type: PhotoType.TRENCH, url: 'photo1.jpg', uploadTime: new Date(), fileSize: 1024000 },
        { id: '2', ticketId: 'ticket-1', type: PhotoType.ONT, url: 'photo2.jpg', uploadTime: new Date(), fileSize: 2048000 }
      ];

      const result = service.bulkValidatePhotos(photos);

      expect(result.validPhotos).toHaveLength(2);
      expect(result.invalidPhotos).toHaveLength(0);
      expect(result.validationResults).toHaveLength(2);
      result.validationResults.forEach(r => expect(r.isValid).toBe(true));
    });
  });
});