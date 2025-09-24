/**
 * Project Setup Validator - Week 1 Sprint 1
 *
 * Validates that all Week 1 Sprint 1 requirements are met
 * following DGTS (Documentation-Driven Test Development) principles.
 *
 * Based on PRD/PRP requirements for Gatekeeper RPA system.
 */

export interface ValidationResult {
  isValid: boolean;
  score: number;
  errors: string[];
  warnings: string[];
  recommendations: string[];
  details: Record<string, any>;
}

export interface ProjectStructure {
  hasAppDir: boolean;
  hasNextConfig: boolean;
  hasPackageJson: boolean;
  hasTsConfig: boolean;
}

export interface TypeScriptConfig {
  compilerOptions: Record<string, any>;
}

export interface ESLintConfig {
  extends: string[];
  rules: Record<string, any>;
}

export interface DatabaseConfig {
  provider: string;
  url?: string;
  schema: string;
  ssl: boolean;
}

export interface SecurityConfig {
  authentication: {
    enabled: boolean;
    provider: string;
    multiFactor: boolean;
  };
  authorization: {
    enabled: boolean;
    rbac: boolean;
    leastPrivilege: boolean;
  };
  encryption: {
    atRest: boolean;
    inTransit: boolean;
    algorithm: string;
  };
}

export class ProjectSetupValidator {
  private readonly REQUIRED_NODE_VERSION = '^18.0.0';
  private readonly REQUIRED_NEXT_VERSION = '^1[4-9].0.0';
  private readonly COVERAGE_THRESHOLD = 95;
  private readonly MAX_BUNDLE_SIZE = 500000; // 500KB

  /**
   * Validate Next.js 14+ App Router structure
   */
  validateNextJSStructure(structure: ProjectStructure): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { framework: 'next.js' }
    };

    let score = 0;

    // Check App Directory
    if (structure.hasAppDir) {
      score += 25;
    } else {
      result.errors.push('Missing Next.js App Router structure');
      result.recommendations.push('Create app/ directory structure');
    }

    // Check Next.js config
    if (structure.hasNextConfig) {
      score += 25;
    } else {
      result.errors.push('Missing next.config.js');
      result.recommendations.push('Create next.config.js with App Router support');
    }

    // Check package.json
    if (structure.hasPackageJson) {
      score += 25;
    } else {
      result.errors.push('Missing package.json');
      result.recommendations.push('Initialize package.json');
    }

    // Check TypeScript config
    if (structure.hasTsConfig) {
      score += 25;
    } else {
      result.errors.push('Missing tsconfig.json');
      result.recommendations.push('Create TypeScript configuration');
    }

    result.isValid = score === 100;
    result.score = score;
    result.details.version = this.REQUIRED_NEXT_VERSION;

    return result;
  }

  /**
   * Validate TypeScript strict configuration
   */
  validateTypeScriptConfig(config: TypeScriptConfig): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { strictMode: false }
    };

    const options = config.compilerOptions;
    let score = 0;

    // Check strict mode
    if (options.strict === true) {
      score += 20;
      result.details.strictMode = true;
    } else {
      result.errors.push('TypeScript strict mode not enabled');
      result.recommendations.push('Enable strict mode in tsconfig.json');
    }

    // Check noImplicitAny
    if (options.noImplicitAny === true) {
      score += 20;
    } else {
      result.errors.push('noImplicitAny not enabled');
    }

    // Check strictNullChecks
    if (options.strictNullChecks === true) {
      score += 20;
    } else {
      result.errors.push('strictNullChecks not enabled');
    }

    // Check target version
    if (options.target === 'ES2022' || options.target === 'ESNext') {
      score += 20;
    } else {
      result.warnings.push('Consider using ES2022 or higher');
    }

    // Check module system
    if (options.module === 'ESNext' || options.module === 'commonjs') {
      score += 20;
    } else {
      result.errors.push('Invalid module system');
    }

    result.isValid = score >= 80;
    result.score = score;

    return result;
  }

  /**
   * Validate ESLint configuration
   */
  validateESLintConfig(config: ESLintConfig): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { hasNoConsoleRule: false }
    };

    let score = 0;

    // Check extends array
    const hasNextjs = config.extends?.some(ext =>
      ext.includes('next') || ext.includes('@next')
    );

    const hasTypeScript = config.extends?.some(ext =>
      ext.includes('typescript') || ext.includes('@typescript-eslint')
    );

    if (hasNextjs) score += 30;
    if (hasTypeScript) score += 30;

    if (!hasNextjs) {
      result.errors.push('Missing Next.js ESLint config');
      result.recommendations.push('Add next/core-web-vitals to extends');
    }

    if (!hasTypeScript) {
      result.errors.push('Missing TypeScript ESLint config');
      result.recommendations.push('Add @typescript-eslint/recommended to extends');
    }

    // Check no-console rule
    if (config.rules?.['no-console'] === 'error' || config.rules?.['no-console'] === 2) {
      score += 20;
      result.details.hasNoConsoleRule = true;
    } else {
      result.errors.push('no-console rule not set to error');
      result.recommendations.push('Set no-console rule to error');
    }

    // Check no-explicit-any rule
    if (config.rules?.['@typescript-eslint/no-explicit-any'] === 'error') {
      score += 20;
    } else {
      result.errors.push('@typescript-eslint/no-explicit-any not enforced');
      result.recommendations.push('Enforce no-explicit-any rule');
    }

    result.isValid = score >= 80;
    result.score = score;

    return result;
  }

  /**
   * Validate database configuration
   */
  validateDatabaseConfig(config: DatabaseConfig): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { provider: config.provider }
    };

    let score = 0;

    // Check provider
    if (config.provider === 'postgresql') {
      score += 40;
    } else {
      result.errors.push('Invalid database provider');
      result.recommendations.push('Use PostgreSQL (Neon)');
    }

    // Check SSL
    if (config.ssl === true) {
      score += 30;
    } else {
      result.warnings.push('SSL not enabled for database');
    }

    // Check schema
    if (config.schema === 'drizzle') {
      score += 30;
    } else {
      result.errors.push('Invalid schema configuration');
      result.recommendations.push('Use Drizzle ORM schema');
    }

    result.isValid = score >= 80;
    result.score = score;

    return result;
  }

  /**
   * Validate security configuration
   */
  validateSecurityConfig(config: SecurityConfig): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { zeroTrustCompliant: false }
    };

    let score = 0;

    // Check authentication
    if (config.authentication.enabled && config.authentication.provider === 'clerk') {
      score += 25;
    } else {
      result.errors.push('Invalid authentication provider');
      result.recommendations.push('Use Clerk for authentication');
    }

    if (config.authentication.multiFactor) {
      score += 15;
    } else {
      result.warnings.push('Multi-factor authentication not enabled');
    }

    // Check authorization
    if (config.authorization.enabled && config.authorization.rbac) {
      score += 25;
    } else {
      result.errors.push('RBAC not properly configured');
    }

    if (config.authorization.leastPrivilege) {
      score += 15;
    }

    // Check encryption
    if (config.encryption.atRest && config.encryption.inTransit) {
      score += 20;
    } else {
      result.errors.push('Encryption not properly configured');
    }

    const isZeroTrustCompliant = score >= 80;
    result.details.zeroTrustCompliant = isZeroTrustCompliant;
    result.isValid = isZeroTrustCompliant;
    result.score = score;

    return result;
  }

  /**
   * Validate folder structure
   */
  validateFolderStructure(structure: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { nextJsCompliant: false }
    };

    const requiredFolders = ['app', 'components', 'lib', 'types', 'hooks', 'tests'];
    let score = 0;
    const missingFolders: string[] = [];

    // Check app directory structure
    if (structure.app?.exists && structure.app?.hasApi && structure.app?.hasLayout) {
      score += 30;
      result.details.nextJsCompliant = true;
    } else {
      missingFolders.push('app');
    }

    // Check other required folders
    const otherFolders = ['components', 'lib', 'types', 'hooks', 'tests'];
    otherFolders.forEach(folder => {
      if (structure[folder]?.exists) {
        score += 14; // 70 / 5 = 14
      } else {
        missingFolders.push(folder);
      }
    });

    if (missingFolders.length > 0) {
      result.errors.push(`Missing folders: ${missingFolders.join(', ')}`);
      result.recommendations.push(`Create missing folders: ${missingFolders.join(', ')}`);
    }

    result.isValid = score === 100;
    result.score = score;
    result.details.missingFolders = missingFolders;

    return result;
  }

  /**
   * Validate CI/CD configuration
   */
  validateCICDConfig(config: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { qualityGates: config.qualityGates }
    };

    let score = 0;

    // Check GitHub Actions
    if (config.hasGithubActions) {
      score += 30;
    } else {
      result.errors.push('GitHub Actions not configured');
      result.recommendations.push('Setup GitHub Actions workflows');
    }

    // Check workflows
    if (config.hasTestWorkflow && config.hasDeployWorkflow) {
      score += 30;
    } else {
      result.errors.push('Missing required workflows');
    }

    // Check quality gates
    const gates = config.qualityGates;
    if (gates?.testCoverage === 95 && gates?.linting && gates?.typeCheck) {
      score += 40;
    } else {
      result.errors.push('Quality gates not properly configured');
      result.recommendations.push('Configure proper quality gates');
    }

    result.isValid = score >= 80;
    result.score = score;

    return result;
  }

  /**
   * Validate TypeScript compilation
   */
  validateTypeCheck(checkResult: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { errorCount: checkResult.errors }
    };

    if (checkResult.errors === 0) {
      result.isValid = true;
      result.score = 100;
    } else {
      result.isValid = false;
      result.score = Math.max(0, 100 - (checkResult.errors * 10));
      result.errors.push(`TypeScript errors: ${checkResult.errors}`);
      result.recommendations.push('Fix all TypeScript errors');
    }

    return result;
  }

  /**
   * Validate ESLint results
   */
  validateLinting(lintResult: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { errorCount: lintResult.errors, warningCount: lintResult.warnings }
    };

    if (lintResult.errors === 0 && lintResult.warnings === 0) {
      result.isValid = true;
      result.score = 100;
    } else {
      result.isValid = false;
      const errorPenalty = lintResult.errors * 20;
      const warningPenalty = lintResult.warnings * 10;
      result.score = Math.max(0, 100 - errorPenalty - warningPenalty);

      if (lintResult.errors > 0) {
        result.errors.push(`ESLint errors: ${lintResult.errors}`);
      }
      if (lintResult.warnings > 0) {
        result.warnings.push(`ESLint warnings: ${lintResult.warnings}`);
      }

      result.recommendations.push('Fix all ESLint errors and warnings');
    }

    return result;
  }

  /**
   * Validate console usage
   */
  validateConsoleUsage(consoleCheck: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: {
        consoleLogCount: consoleCheck.consoleLogCount,
        consoleWarnCount: consoleCheck.consoleWarnCount,
        consoleErrorCount: consoleCheck.consoleErrorCount
      }
    };

    if (consoleCheck.consoleLogCount === 0 && consoleCheck.consoleWarnCount === 0) {
      result.isValid = true;
      result.score = 100;
    } else {
      result.isValid = false;
      const logPenalty = consoleCheck.consoleLogCount * 50;
      const warnPenalty = consoleCheck.consoleWarnCount * 25;
      result.score = Math.max(0, 100 - logPenalty - warnPenalty);

      if (consoleCheck.consoleLogCount > 0) {
        result.errors.push(`console.log statements: ${consoleCheck.consoleLogCount}`);
      }
      if (consoleCheck.consoleWarnCount > 0) {
        result.warnings.push(`console.warn statements: ${consoleCheck.consoleWarnCount}`);
      }

      result.recommendations.push('Remove all console.log and console.warn statements');
    }

    return result;
  }

  /**
   * Validate test coverage
   */
  validateTestCoverage(coverageResult: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { coveragePercentage: coverageResult.percentage }
    };

    if (coverageResult.percentage >= this.COVERAGE_THRESHOLD) {
      result.isValid = true;
      result.score = 100;
    } else {
      result.isValid = false;
      result.score = coverageResult.percentage;
      result.errors.push(`Coverage ${coverageResult.percentage}% is below ${this.COVERAGE_THRESHOLD}% threshold`);
      result.recommendations.push('Increase test coverage to meet minimum requirements');
    }

    return result;
  }

  /**
   * Validate bundle size
   */
  validateBundleSize(bundleAnalysis: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { mainBundleSize: bundleAnalysis.mainBundle }
    };

    const oversizedBundles: string[] = [];

    if (bundleAnalysis.mainBundle > this.MAX_BUNDLE_SIZE) {
      oversizedBundles.push('main');
    }

    if (oversizedBundles.length === 0) {
      result.isValid = true;
      result.score = 100;
    } else {
      result.isValid = false;
      result.score = Math.max(0, 100 - (oversizedBundles.length * 30));
      result.errors.push(`Oversized bundles: ${oversizedBundles.join(', ')}`);
      result.details.oversizedBundles = oversizedBundles;
      result.recommendations.push('Optimize bundle sizes to meet requirements');
    }

    return result;
  }

  /**
   * Validate performance metrics
   */
  validatePerformance(perfMetrics: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: {
        startupTime: perfMetrics.startupTime,
        responseTime: perfMetrics.responseTime
      }
    };

    let score = 100;

    // Deduct points for poor performance
    if (perfMetrics.startupTime > 5000) {
      score -= 30;
      result.errors.push('Startup time too slow');
    } else if (perfMetrics.startupTime > 3000) {
      score -= 15;
      result.warnings.push('Startup time could be improved');
    }

    if (perfMetrics.responseTime > 200) {
      score -= 30;
      result.errors.push('Response time too slow');
    } else if (perfMetrics.responseTime > 150) {
      score -= 15;
      result.warnings.push('Response time could be improved');
    }

    result.isValid = score >= 80;
    result.score = score;

    return result;
  }

  /**
   * Validate hot reload functionality
   */
  validateHotReload(hmrResult: any): ValidationResult {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { reloadTime: hmrResult.reloadTime }
    };

    if (hmrResult.enabled && hmrResult.reloadTime < 500) {
      result.isValid = true;
      result.score = 100;
    } else {
      result.isValid = false;
      result.score = hmrResult.enabled ? 70 : 0;
      result.errors.push('Hot reload not properly configured');
      result.recommendations.push('Configure hot reload for better development experience');
    }

    return result;
  }

  /**
   * Validate complete setup
   */
  async validateCompleteSetup(setup: any): Promise<ValidationResult> {
    const result: ValidationResult = {
      isValid: false,
      score: 0,
      errors: [],
      warnings: [],
      recommendations: [],
      details: { sprint: 'Week 1', readiness: 'incomplete' }
    };

    const validations = [
      { name: 'Project Structure', result: setup.projectStructure },
      { name: 'Development Tools', result: setup.developmentTools },
      { name: 'Database', result: setup.database },
      { name: 'Security', result: setup.security },
      { name: 'Quality Gates', result: setup.qualityGates }
    ];

    let totalScore = 0;
    let passingValidations = 0;

    for (const validation of validations) {
      if (validation.result.isValid) {
        passingValidations++;
        totalScore += 100;
      } else {
        result.errors.push(`${validation.name} validation failed`);
      }
    }

    const averageScore = totalScore / validations.length;
    result.score = averageScore;
    result.isValid = passingValidations === validations.length;
    result.details.readiness = result.isValid ? 'production-ready' : 'needs-work';

    if (!result.isValid) {
      result.recommendations.push('Review and fix failed validations');
    }

    return result;
  }
}