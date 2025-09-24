/**
 * DGTS (Documentation-Driven Test Development) Validator
 *
 * Implements anti-gaming validation to ensure tests and implementations
 * are created from actual requirements and not fabricated.
 *
 * Based on NLNH (No Lies No Hallucination) and DGTS principles.
 */

export interface GamingViolation {
  type: string;
  severity: 'critical' | 'high' | 'medium' | 'low';
  file: string;
  line?: number;
  description: string;
  weight: number;
}

export interface CodeChange {
  file: string;
  content: string;
  lines: number;
}

export interface TestFile {
  file: string;
  content: string;
  lines: number;
}

export interface GamingDetectionResult {
  hasGaming: boolean;
  gamingScore: number;
  violations: GamingViolation[];
  recommendations: string[];
  confidence: number;
}

export interface TestValidationResult {
  isValid: boolean;
  hasGaming: boolean;
  gamingScore: number;
  violations: GamingViolation[];
  requirementsDriven: boolean;
  coverage: number;
  recommendations: string[];
}

export interface GamingScoreResult {
  score: number;
  threshold: number;
  isValid: boolean;
  violations: GamingViolation[];
  recommendation: string;
}

export class DGTSValidator {
  private readonly GAMING_THRESHOLD = 0.3;
  private readonly CRITICAL_THRESHOLD = 0.5;

  /**
   * Detect gaming patterns in code changes
   */
  detectGamingPatterns(codeChanges: CodeChange[]): GamingDetectionResult {
    const violations: GamingViolation[] = [];
    let totalWeight = 0;

    // Define gaming patterns to detect
    const gamingPatterns = [
      {
        type: 'FAKE_IMPLEMENTATION',
        pattern: /return\s+["']mock_data["']/g,
        severity: 'critical' as const,
        weight: 1.0,
        description: 'Hardcoded mock return values'
      },
      {
        type: 'COMMENTED_VALIDATION',
        pattern: /\/\/\s*validation_required/g,
        severity: 'high' as const,
        weight: 0.8,
        description: 'Commented out validation rules'
      },
      {
        type: 'DISABLED_CODE',
        pattern: /if\s*\(\s*false\s*\)/g,
        severity: 'high' as const,
        weight: 0.7,
        description: 'Disabled code blocks'
      },
      {
        type: 'STUB_FUNCTION',
        pattern: /\/\/\s*TODO:\s*implement/gi,
        severity: 'medium' as const,
        weight: 0.6,
        description: 'Unimplemented stub functions'
      },
      {
        type: 'EMPTY_CATCH',
        pattern: /catch\s*\([^)]*\)\s*\{\s*\}/g,
        severity: 'high' as const,
        weight: 0.8,
        description: 'Empty catch blocks'
      },
      {
        type: 'HARDCODED_VALUES',
        pattern: /const\s+\w+\s*=\s*["']test["']/g,
        severity: 'medium' as const,
        weight: 0.4,
        description: 'Excessive hardcoded test values'
      }
    ];

    for (const change of codeChanges) {
      for (const pattern of gamingPatterns) {
        const matches = change.content.match(pattern.pattern);
        if (matches) {
          const line = this.findLineNumber(change.content, pattern.pattern);
          violations.push({
            type: pattern.type,
            severity: pattern.severity,
            file: change.file,
            line,
            description: pattern.description,
            weight: pattern.weight
          });
          totalWeight += pattern.weight * matches.length;
        }
      }
    }

    // Calculate gaming score (0.0 = clean, 1.0 = heavily gamed)
    const gamingScore = Math.min(totalWeight / 5, 1.0); // Normalize to 0-1

    const recommendations = this.generateGamingRecommendations(violations, gamingScore);

    return {
      hasGaming: violations.length > 0,
      gamingScore,
      violations,
      recommendations,
      confidence: this.calculateConfidence(violations, codeChanges)
    };
  }

  /**
   * Validate test files for gaming patterns
   */
  validateTests(testFiles: TestFile[]): TestValidationResult {
    const violations: GamingViolation[] = [];
    let totalWeight = 0;

    for (const testFile of testFiles) {
      // Check for meaningless tests
      const meaninglessTests = this.detectMeaninglessTests(testFile);
      violations.push(...meaninglessTests);
      totalWeight += meaninglessTests.reduce((sum, v) => sum + v.weight, 0);

      // Check for fake assertions
      const fakeAssertions = this.detectFakeAssertions(testFile);
      violations.push(...fakeAssertions);
      totalWeight += fakeAssertions.reduce((sum, v) => sum + v.weight, 0);

      // Check for test coverage gaming
      const coverageGaming = this.detectCoverageGaming(testFile);
      violations.push(...coverageGaming);
      totalWeight += coverageGaming.reduce((sum, v) => sum + v.weight, 0);
    }

    const gamingScore = Math.min(totalWeight / 10, 1.0);
    const requirementsDriven = this.checkRequirementsDriven(testFiles);
    const coverage = this.estimateTestCoverage(testFiles);

    const recommendations = this.generateTestRecommendations(violations, gamingScore);

    return {
      isValid: gamingScore < this.GAMING_THRESHOLD,
      hasGaming: violations.length > 0,
      gamingScore,
      violations,
      requirementsDriven,
      coverage,
      recommendations
    };
  }

  /**
   * Validate test specifications are derived from requirements
   */
  validateTestSpecifications(specifications: any): TestValidationResult {
    const violations: GamingViolation[] = [];
    let score = 100;

    // Check if tests are requirements-driven
    if (!specifications.hasTestRequirements) {
      violations.push({
        type: 'MISSING_REQUIREMENTS',
        severity: 'critical' as const,
        file: 'specifications',
        description: 'Tests not derived from PRD/PRP requirements',
        weight: 1.0
      });
      score -= 40;
    }

    if (!specifications.requirementsMapped) {
      violations.push({
        type: 'UNMAPPED_REQUIREMENTS',
        severity: 'high' as const,
        file: 'specifications',
        description: 'Test requirements not mapped to PRD/PRP',
        weight: 0.8
      });
      score -= 30;
    }

    if (!specifications.prdBased) {
      violations.push({
        type: 'NON_PRD_BASED',
        severity: 'high' as const,
        file: 'specifications',
        description: 'Tests not based on PRD requirements',
        weight: 0.7
      });
      score -= 20;
    }

    if (!specifications.acceptanceCriteriaDefined) {
      violations.push({
        type: 'MISSING_ACCEPTANCE_CRITERIA',
        severity: 'medium' as const,
        file: 'specifications',
        description: 'Acceptance criteria not defined',
        weight: 0.5
      });
      score -= 10;
    }

    const gamingScore = Math.max(0, (100 - score) / 100);

    return {
      isValid: score >= 70,
      hasGaming: violations.length > 0,
      gamingScore,
      violations,
      requirementsDriven: specifications.prdBased && specifications.requirementsMapped,
      coverage: 0, // Coverage estimated separately
      recommendations: this.generateRequirementsRecommendations(violations)
    };
  }

  /**
   * Validate gaming score against threshold
   */
  validateGamingScore(gamingResult: GamingScoreResult): GamingScoreResult {
    const isValid = gamingResult.score < this.GAMING_THRESHOLD;
    let recommendation = 'No gaming detected. Continue development.';

    if (!isValid) {
      if (gamingResult.score >= this.CRITICAL_THRESHOLD) {
        recommendation = 'CRITICAL: Heavy gaming detected. Development blocked until violations are fixed.';
      } else {
        recommendation = 'Gaming detected. Fix violations before proceeding with development.';
      }
    }

    return {
      ...gamingResult,
      isValid,
      recommendation
    };
  }

  /**
   * Detect meaningless test patterns
   */
  private detectMeaninglessTests(testFile: TestFile): GamingViolation[] {
    const violations: GamingViolation[] = [];

    // Check for empty tests
    const emptyTestPattern = /test\(['"][^'"]*['"]\s*,\s*\(\)\s*=>\s*\{\s*\}/g;
    const emptyTests = testFile.content.match(emptyTestPattern);
    if (emptyTests) {
      violations.push({
        type: 'EMPTY_TEST',
        severity: 'high' as const,
        file: testFile.file,
        description: 'Empty test implementations',
        weight: 0.8
      });
    }

    // Check for always-true assertions
    const alwaysTruePattern = /expect\((true|false|"true"|"false"|1|0)\)\.(toBe|toEqual|toStrictEqual)\(\1\)/g;
    const alwaysTrueAssertions = testFile.content.match(alwaysTruePattern);
    if (alwaysTrueAssertions) {
      violations.push({
        type: 'ALWAYS_TRUE_ASSERTION',
        severity: 'high' as const,
        file: testFile.file,
        description: 'Assertions that always pass',
        weight: 0.9
      });
    }

    // Check for placeholder comments
    const placeholderPattern = /\/\/\s*TODO.*test/gi;
    const placeholders = testFile.content.match(placeholderPattern);
    if (placeholders) {
      violations.push({
        type: 'PLACEHOLDER_TEST',
        severity: 'medium' as const,
        file: testFile.file,
        description: 'Placeholder test implementations',
        weight: 0.6
      });
    }

    return violations;
  }

  /**
   * Detect fake assertion patterns
   */
  private detectFakeAssertions(testFile: TestFile): GamingViolation[] {
    const violations: GamingViolation[] = [];

    // Check for mocked test data without validation
    const mockDataPattern = /const\s+\w+\s*=\s*["']mock.*["']/g;
    const mockDataMatches = testFile.content.match(mockDataPattern);
    if (mockDataMatches && mockDataMatches.length > 5) {
      violations.push({
        type: 'EXCESSIVE_MOCK_DATA',
        severity: 'medium' as const,
        file: testFile.file,
        description: 'Excessive use of mock data without real validation',
        weight: 0.5
      });
    }

    // Check for stub implementations
    const stubPattern = /\/\/\s*stub.*implementation/gi;
    const stubs = testFile.content.match(stubPattern);
    if (stubs) {
      violations.push({
        type: 'STUB_IMPLEMENTATION',
        severity: 'high' as const,
        file: testFile.file,
        description: 'Stub implementations in tests',
        weight: 0.7
      });
    }

    return violations;
  }

  /**
   * Detect test coverage gaming
   */
  private detectCoverageGaming(testFile: TestFile): GamingViolation[] {
    const violations: GamingViolation[] = [];

    // Check for coverage-only tests (tests that exist only for coverage)
    const coverageOnlyPattern = /test\(['"][^'"]*coverage.*['"]/gi;
    const coverageTests = testFile.content.match(coverageOnlyPattern);
    if (coverageTests) {
      violations.push({
        type: 'COVERAGE_ONLY_TEST',
        severity: 'medium' as const,
        file: testFile.file,
        description: 'Tests written only for coverage purposes',
        weight: 0.4
      });
    }

    // Check for skipped tests
    const skippedPattern = /test\.skip\(/g;
    const skippedTests = testFile.content.match(skippedPattern);
    if (skippedTests) {
      violations.push({
        type: 'EXCESSIVE_SKIPPED_TESTS',
        severity: 'low' as const,
        file: testFile.file,
        description: 'Excessive skipped tests',
        weight: 0.2
      });
    }

    return violations;
  }

  /**
   * Check if tests are requirements-driven
   */
  private checkRequirementsDriven(testFiles: TestFile[]): boolean {
    // Look for evidence of requirements mapping
    let hasRequirementsReferences = false;
    let hasAcceptanceCriteria = false;

    for (const testFile of testFiles) {
      // Check for PRD/PRP references in comments
      const prdReferences = testFile.content.match(/\/\/\s*(PRD|PRP|Requirement|Acceptance)/gi);
      if (prdReferences) {
        hasRequirementsReferences = true;
      }

      // Check for structured test descriptions that match requirements
      const structuredTests = testFile.content.match(/(should|must|will)\s+(not\s+)?[A-Z]/g);
      if (structuredTests && structuredTests.length > 3) {
        hasAcceptanceCriteria = true;
      }
    }

    return hasRequirementsReferences && hasAcceptanceCriteria;
  }

  /**
   * Estimate test coverage based on file analysis
   */
  private estimateTestCoverage(testFiles: TestFile[]): number {
    // This is a simplified estimation - real coverage analysis would be more complex
    let totalLines = 0;
    let testLines = 0;

    for (const testFile of testFiles) {
      totalLines += testFile.lines;

      // Count lines that look like actual test assertions
      const assertionLines = testFile.content.match(/expect\(/g) || [];
      const testDescriptionLines = testFile.content.match(/(test|it|describe)\(/g) || [];

      testLines += assertionLines.length * 2 + testDescriptionLines.length;
    }

    return totalLines > 0 ? Math.min((testLines / totalLines) * 100, 100) : 0;
  }

  /**
   * Calculate confidence in detection results
   */
  private calculateConfidence(violations: GamingViolation[], codeChanges: CodeChange[]): number {
    if (violations.length === 0) return 0.8; // High confidence in clean code

    const criticalViolations = violations.filter(v => v.severity === 'critical').length;
    const totalConfidence = Math.min(0.5 + (criticalViolations * 0.1) + (violations.length * 0.05), 0.95);

    return totalConfidence;
  }

  /**
   * Find line number for a pattern in content
   */
  private findLineNumber(content: string, pattern: RegExp): number | undefined {
    const lines = content.split('\n');
    for (let i = 0; i < lines.length; i++) {
      if (lines[i].match(pattern)) {
        return i + 1;
      }
    }
    return undefined;
  }

  /**
   * Generate gaming recommendations
   */
  private generateGamingRecommendations(violations: GamingViolation[], score: number): string[] {
    const recommendations: string[] = [];

    if (score >= this.CRITICAL_THRESHOLD) {
      recommendations.push('CRITICAL: Heavy gaming detected. Development blocked.');
      recommendations.push('Review and fix all critical violations immediately.');
      recommendations.push('Re-implement features with proper testing.');
    } else if (score >= this.GAMING_THRESHOLD) {
      recommendations.push('Gaming patterns detected. Fix violations before proceeding.');
      recommendations.push('Implement real functionality instead of stubs.');
    } else if (violations.length > 0) {
      recommendations.push('Minor gaming patterns detected. Consider improvements.');
    }

    // Specific recommendations based on violation types
    const violationTypes = violations.map(v => v.type);
    if (violationTypes.includes('FAKE_IMPLEMENTATION')) {
      recommendations.push('Replace mock implementations with real functionality.');
    }
    if (violationTypes.includes('COMMENTED_VALIDATION')) {
      recommendations.push('Enable all validation rules and fix underlying issues.');
    }
    if (violationTypes.includes('EMPTY_TEST')) {
      recommendations.push('Implement proper test assertions and logic.');
    }

    return recommendations;
  }

  /**
   * Generate test recommendations
   */
  private generateTestRecommendations(violations: GamingViolation[], score: number): string[] {
    const recommendations: string[] = [];

    if (score >= this.GAMING_THRESHOLD) {
      recommendations.push('Tests show gaming patterns. Rewrite tests to validate real functionality.');
    }

    if (violations.some(v => v.type === 'EMPTY_TEST')) {
      recommendations.push('Implement proper test logic in empty test cases.');
    }

    if (violations.some(v => v.type === 'ALWAYS_TRUE_ASSERTION')) {
      recommendations.push('Replace always-true assertions with meaningful validations.');
    }

    if (violations.some(v => v.type === 'STUB_IMPLEMENTATION')) {
      recommendations.push('Replace stub implementations with real test scenarios.');
    }

    return recommendations;
  }

  /**
   * Generate requirements-based recommendations
   */
  private generateRequirementsRecommendations(violations: GamingViolation[]): string[] {
    const recommendations: string[] = [];

    if (violations.some(v => v.type === 'MISSING_REQUIREMENTS')) {
      recommendations.push('Create tests based on PRD/PRP requirements before implementation.');
    }

    if (violations.some(v => v.type === 'UNMAPPED_REQUIREMENTS')) {
      recommendations.push('Map all test cases to specific PRD/PRP requirements.');
    }

    if (violations.some(v => v.type === 'NON_PRD_BASED')) {
      recommendations.push('Base all tests on documented PRD requirements.');
    }

    if (violations.some(v => v.type === 'MISSING_ACCEPTANCE_CRITERIA')) {
      recommendations.push('Define clear acceptance criteria for each requirement.');
    }

    return recommendations;
  }

  /**
   * Get validation summary
   */
  getValidationSummary(results: TestValidationResult): {
    status: 'pass' | 'fail' | 'warning';
    summary: string;
    actionItems: string[];
  } {
    let status: 'pass' | 'fail' | 'warning' = 'pass';
    let summary = 'Validation passed successfully.';
    const actionItems: string[] = [];

    if (results.gamingScore >= this.CRITICAL_THRESHOLD) {
      status = 'fail';
      summary = 'Critical gaming detected. Development blocked.';
      actionItems.push('Block development until gaming violations are fixed');
      actionItems.push('Complete re-implementation required');
    } else if (results.gamingScore >= this.GAMING_THRESHOLD) {
      status = 'fail';
      summary = 'Gaming patterns detected. Fix required.';
      actionItems.push('Fix all gaming violations');
      actionItems.push('Re-implement problematic areas');
    } else if (!results.requirementsDriven) {
      status = 'warning';
      summary = 'Tests not requirements-driven. Improvement needed.';
      actionItems.push('Map tests to PRD/PRP requirements');
      actionItems.push('Improve test documentation');
    }

    return {
      status,
      summary,
      actionItems
    };
  }
}