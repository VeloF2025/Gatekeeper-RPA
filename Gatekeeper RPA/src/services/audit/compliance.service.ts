import { PhotoType, Photo } from '@/types';
import { logger } from '@/utils/logger';

export interface ComplianceCheckResult {
  isCompliant: boolean;
  complianceScore: number;
  missingPhotos: PhotoType[];
  foundPhotos: PhotoType[];
  additionalPhotos: PhotoType[];
  issues: ComplianceIssue[];
  recommendations: string[];
}

export interface ComplianceIssue {
  type: 'missing' | 'invalid' | 'duplicate' | 'quality';
  photoType: PhotoType;
  description: string;
  severity: 'low' | 'medium' | 'high';
  suggestion: string;
}

export class ComplianceService {
  private readonly REQUIRED_PHOTOS: PhotoType[] = [
    PhotoType.TRENCH,
    PhotoType.ONT,
    PhotoType.TERMINATION,
    PhotoType.PROPERTY
  ];

  private readonly PHOTO_REQUIREMENTS = {
    [PhotoType.TRENCH]: {
      name: 'Trench Photo',
      description: 'Photo showing fiber optic trench installation',
      required: true,
      qualityStandards: [
        'Clear visibility of trench depth',
        'Proper cable placement',
        'Safety measures visible'
      ]
    },
    [PhotoType.ONT]: {
      name: 'ONT Photo',
      description: 'Photo showing Optical Network Terminal installation',
      required: true,
      qualityStandards: [
        'ONT clearly visible',
        'Power connection visible',
        'Fiber connection visible',
        'Labeling visible'
      ]
    },
    [PhotoType.TERMINATION]: {
      name: 'Termination Photo',
      description: 'Photo showing fiber termination point',
      required: true,
      qualityStandards: [
        'Termination point clearly visible',
        'Proper cable management',
        'Connection quality visible'
      ]
    },
    [PhotoType.PROPERTY]: {
      name: 'Property Photo',
      description: 'Photo showing the property with installation',
      required: true,
      qualityStandards: [
        'Full property visible',
        'Installation point visible',
        'Address/property ID visible'
      ]
    },
    [PhotoType.ADDITIONAL]: {
      name: 'Additional Photo',
      description: 'Additional supporting photos',
      required: false,
      qualityStandards: [
        'Clear and relevant to installation',
        'Shows additional details'
      ]
    }
  };

  /**
   * Check photo compliance against requirements
   */
  checkPhotoCompliance(photos: Photo[]): ComplianceCheckResult {
    try {
      // Validate input
      if (!Array.isArray(photos)) {
        throw new Error('Photos must be an array');
      }

      // Find required photos
      const foundTypes = new Set<PhotoType>();
      const photoByType = new Map<PhotoType, Photo[]>();
      const issues: ComplianceIssue[] = [];

      // Group photos by type
      for (const photo of photos) {
        if (!this.validatePhotoType(photo.type)) {
          issues.push({
            type: 'invalid',
            photoType: photo.type,
            description: `Invalid photo type: ${photo.type}`,
            severity: 'high',
            suggestion: 'Use valid photo types: trench, ONT, termination, property, additional'
          });
          continue;
        }

        if (!photoByType.has(photo.type)) {
          photoByType.set(photo.type, []);
        }
        photoByType.get(photo.type)!.push(photo);
        foundTypes.add(photo.type);
      }

      // Check for missing required photos
      const missingPhotos: PhotoType[] = [];
      for (const requiredType of this.REQUIRED_PHOTOS) {
        if (!foundTypes.has(requiredType)) {
          missingPhotos.push(requiredType);
          issues.push({
            type: 'missing',
            photoType: requiredType,
            description: `Missing required photo: ${this.PHOTO_REQUIREMENTS[requiredType].name}`,
            severity: 'high',
            suggestion: `Upload ${this.PHOTO_REQUIREMENTS[requiredType].description}`
          });
        }
      }

      // Check for duplicates
      for (const [type, typePhotos] of photoByType) {
        if (typePhotos.length > 1 && type !== PhotoType.ADDITIONAL) {
          issues.push({
            type: 'duplicate',
            photoType: type,
            description: `Multiple ${this.PHOTO_REQUIREMENTS[type].name} photos found`,
            severity: 'medium',
            suggestion: `Only one ${this.PHOTO_REQUIREMENTS[type].name} photo is required`
          });
        }
      }

      // Check photo quality (basic validation)
      this.validatePhotoQuality(photos, issues);

      // Calculate compliance score
      const requiredCount = this.REQUIRED_PHOTOS.length;
      const foundCount = this.REQUIRED_PHOTOS.filter(type => foundTypes.has(type)).length;
      const complianceScore = Math.round((foundCount / requiredCount) * 100);

      // Get additional photos
      const additionalPhotos = Array.from(foundTypes).filter(type => !this.REQUIRED_PHOTOS.includes(type));

      // Generate recommendations
      const recommendations = this.generateRecommendations(foundTypes, missingPhotos, issues);

      const result: ComplianceCheckResult = {
        isCompliant: missingPhotos.length === 0 && issues.filter(i => i.severity === 'high').length === 0,
        complianceScore,
        missingPhotos,
        foundPhotos: Array.from(foundTypes).filter(type => this.REQUIRED_PHOTOS.includes(type)),
        additionalPhotos,
        issues,
        recommendations
      };

      logger.info('Compliance check completed', {
        totalPhotos: photos.length,
        complianceScore,
        isCompliant: result.isCompliant,
        issuesCount: issues.length
      });

      return result;

    } catch (error) {
      logger.error('Failed to check photo compliance', {
        error: error instanceof Error ? error.message : error,
        photosCount: photos.length
      });

      throw error;
    }
  }

  /**
   * Validate photo type
   */
  validatePhotoType(type: string): type is PhotoType {
    return Object.values(PhotoType).includes(type as PhotoType);
  }

  /**
   * Validate photo quality
   */
  private validatePhotoQuality(photos: Photo[], issues: ComplianceIssue[]): void {
    for (const photo of photos) {
      // Check file size (basic validation)
      if (photo.fileSize < 1024) { // Less than 1KB
        issues.push({
          type: 'quality',
          photoType: photo.type,
          description: `File size too small: ${photo.fileSize} bytes`,
          severity: 'medium',
          suggestion: 'Upload higher quality photos (minimum 1KB)'
        });
      }

      if (photo.fileSize > 10 * 1024 * 1024) { // More than 10MB
        issues.push({
          type: 'quality',
          photoType: photo.type,
          description: `File size too large: ${photo.fileSize} bytes`,
          severity: 'medium',
          suggestion: 'Compress photos to under 10MB'
        });
      }

      // Check upload time (basic validation)
      const uploadTime = new Date(photo.uploadTime);
      const now = new Date();
      const timeDiff = now.getTime() - uploadTime.getTime();

      if (timeDiff > 24 * 60 * 60 * 1000) { // More than 24 hours old
        issues.push({
          type: 'quality',
          photoType: photo.type,
          description: 'Photo uploaded more than 24 hours ago',
          severity: 'low',
          suggestion: 'Upload recent photos for current audit'
        });
      }

      // Validate URL format
      try {
        new URL(photo.url);
      } catch {
        issues.push({
          type: 'invalid',
          photoType: photo.type,
          description: 'Invalid photo URL format',
          severity: 'high',
          suggestion: 'Use valid photo URLs'
        });
      }
    }
  }

  /**
   * Calculate compliance score
   */
  calculateComplianceScore(requiredPhotos: PhotoType[], foundPhotos: PhotoType[]): number {
    if (requiredPhotos.length === 0) {
      return 100; // No requirements means 100% compliant
    }

    const foundCount = requiredPhotos.filter(type => foundPhotos.includes(type)).length;
    return Math.round((foundCount / requiredPhotos.length) * 100);
  }

  /**
   * Generate compliance recommendations
   */
  private generateRecommendations(
    foundTypes: Set<PhotoType>,
    missingPhotos: PhotoType[],
    issues: ComplianceIssue[]
  ): string[] {
    const recommendations: string[] = [];

    // General recommendations based on compliance
    if (missingPhotos.length === 0) {
      recommendations.push('✅ All required photos are present. Great job!');
    } else {
      recommendations.push(`⚠️ ${missingPhotos.length} required photo(s) missing`);
    }

    // Specific recommendations for missing photos
    for (const missingType of missingPhotos) {
      const requirement = this.PHOTO_REQUIREMENTS[missingType];
      recommendations.push(`📸 Add ${requirement.name}: ${requirement.description}`);
    }

    // Quality recommendations
    const qualityIssues = issues.filter(issue => issue.type === 'quality');
    if (qualityIssues.length > 0) {
      recommendations.push('🔍 Check photo quality and resolution');
    }

    // Duplicate recommendations
    const duplicateIssues = issues.filter(issue => issue.type === 'duplicate');
    if (duplicateIssues.length > 0) {
      recommendations.push('🗂️ Remove duplicate photos');
    }

    // Additional photo recommendations
    const additionalCount = Array.from(foundTypes).filter(type => !this.REQUIRED_PHOTOS.includes(type)).length;
    if (additionalCount > 0) {
      recommendations.push(`📎 ${additionalCount} additional photo(s) found - good documentation!`);
    }

    return recommendations;
  }

  /**
   * Get detailed requirements for a photo type
   */
  getPhotoRequirements(type: PhotoType) {
    return this.PHOTO_REQUIREMENTS[type] || null;
  }

  /**
   * Get all required photo types
   */
  getRequiredPhotoTypes(): PhotoType[] {
    return [...this.REQUIRED_PHOTOS];
  }

  /**
   * Check if a specific photo type is required
   */
  isPhotoRequired(type: PhotoType): boolean {
    return this.REQUIRED_PHOTOS.includes(type);
  }

  /**
   * Validate single photo
   */
  validatePhoto(photo: Photo): {
    isValid: boolean;
    issues: string[];
    warnings: string[];
  } {
    const issues: string[] = [];
    const warnings: string[] = [];

    // Validate required fields
    if (!photo.id) issues.push('Photo ID is required');
    if (!photo.ticketId) issues.push('Ticket ID is required');
    if (!photo.type) issues.push('Photo type is required');
    if (!photo.url) issues.push('Photo URL is required');
    if (!photo.uploadTime) issues.push('Upload time is required');
    if (photo.fileSize === undefined || photo.fileSize === null) {
      issues.push('File size is required');
    }

    // Validate photo type
    if (photo.type && !this.validatePhotoType(photo.type)) {
      issues.push(`Invalid photo type: ${photo.type}`);
    }

    // Validate URL format
    if (photo.url) {
      try {
        new URL(photo.url);
      } catch {
        issues.push('Invalid photo URL format');
      }
    }

    // Validate file size
    if (photo.fileSize !== undefined) {
      if (photo.fileSize <= 0) {
        issues.push('File size must be greater than 0');
      }
      if (photo.fileSize > 50 * 1024 * 1024) { // 50MB
        warnings.push('Large file size detected. Consider compressing.');
      }
    }

    // Validate upload time
    if (photo.uploadTime) {
      const uploadTime = new Date(photo.uploadTime);
      const now = new Date();

      if (isNaN(uploadTime.getTime())) {
        issues.push('Invalid upload time format');
      }

      if (uploadTime > now) {
        warnings.push('Upload time is in the future');
      }

      const timeDiff = now.getTime() - uploadTime.getTime();
      if (timeDiff > 30 * 24 * 60 * 60 * 1000) { // 30 days
        warnings.push('Photo uploaded more than 30 days ago');
      }
    }

    return {
      isValid: issues.length === 0,
      issues,
      warnings
    };
  }

  /**
   * Get compliance statistics
   */
  getComplianceStats(photos: Photo[]): {
    totalPhotos: number;
    requiredPhotosFound: number;
    additionalPhotos: number;
    uniqueTypes: number;
    averageFileSize: number;
    complianceScore: number;
  } {
    const compliance = this.checkPhotoCompliance(photos);

    const totalSize = photos.reduce((sum, photo) => sum + photo.fileSize, 0);
    const averageFileSize = photos.length > 0 ? totalSize / photos.length : 0;

    const uniqueTypes = new Set(photos.map(p => p.type)).size;
    const additionalPhotos = photos.filter(p => !this.REQUIRED_PHOTOS.includes(p.type)).length;
    const requiredPhotosFound = compliance.foundPhotos.length;

    return {
      totalPhotos: photos.length,
      requiredPhotosFound,
      additionalPhotos,
      uniqueTypes,
      averageFileSize: Math.round(averageFileSize),
      complianceScore: compliance.complianceScore
    };
  }

  /**
   * Bulk validate multiple photos
   */
  bulkValidatePhotos(photos: Photo[]): {
    validPhotos: Photo[];
    invalidPhotos: Photo[];
    validationResults: Array<{
      photo: Photo;
      isValid: boolean;
      issues: string[];
      warnings: string[];
    }>;
  } {
    const validPhotos: Photo[] = [];
    const invalidPhotos: Photo[] = [];
    const validationResults = [];

    for (const photo of photos) {
      const result = this.validatePhoto(photo);
      validationResults.push({
        photo,
        isValid: result.isValid,
        issues: result.issues,
        warnings: result.warnings
      });

      if (result.isValid) {
        validPhotos.push(photo);
      } else {
        invalidPhotos.push(photo);
      }
    }

    return {
      validPhotos,
      invalidPhotos,
      validationResults
    };
  }
}