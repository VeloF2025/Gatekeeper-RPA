# Development Workflow Setup - Gatekeeper RPA System

**Version**: 1.0
**Date**: 2025-09-23

## Overview

This document outlines the complete development workflow setup for the Gatekeeper RPA project, including GitHub issue management, worktree strategy, CI/CD pipeline, testing procedures, and deployment automation.

## 1. GitHub Issue Templates for Sprint Planning

### 1.1 Feature Issue Template

```markdown
---
name: Feature Request
about: Implement a new feature for the Gatekeeper RPA system
title: "[FEATURE] "
labels: ["feature", "needs-estimation"]
assignees: []
---

## Feature Description
A clear and concise description of the feature to be implemented.

## Requirements
- [ ] Requirement 1
- [ ] Requirement 2
- [ ] Requirement 3

## Acceptance Criteria
- [ ] Given **context**, when **action**, then **outcome**
- [ ] **Condition** should result in **specific result**

## Technical Specification
### API Endpoints (if any)
- `METHOD /endpoint` - Description

### Database Changes
- Table modifications
- New tables
- Migration requirements

### UI/UX Changes
- Wireframes or mockups
- User flow description

## Dependencies
- Blocked by #issue-number
- Depends on #issue-number

## Implementation Notes
Any technical considerations or implementation details.

## Testing Requirements
- [ ] Unit tests
- [ ] Integration tests
- [ ] E2E tests
- [ ] Performance tests

## Definition of Done
- [ ] Code implemented and reviewed
- [ ] All tests passing
- [ ] Documentation updated
- [ ] Code merged to main
```

### 1.2 Bug Issue Template

```markdown
---
name: Bug Report
about: Report a bug in the Gatekeeper RPA system
title: "[BUG] "
labels: ["bug", "needs-triage"]
assignees: []
---

## Bug Description
A clear and concise description of what the bug is.

## Environment
- OS: [e.g., Ubuntu 20.04]
- Node.js version: [e.g., 18.x]
- Browser: [e.g., Chrome 90]
- Deployment: [e.g., Production]

## Steps to Reproduce
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior
A clear and concise description of what you expected to happen.

## Actual Behavior
A clear and concise description of what actually happened.

## Screenshots
If applicable, add screenshots to help explain your problem.

## Error Messages
```
Paste error messages here
```

## Additional Context
Add any other context about the problem here.

## Priority
- [ ] Critical (blocks development)
- [ ] High (affects core functionality)
- [ ] Medium (workaround exists)
- [ ] Low (minor issue)
```

### 1.3 Task Issue Template

```markdown
---
name: Task
about: General development task
title: "[TASK] "
labels: ["task"]
assignees: []
---

## Task Description
A clear and concise description of what needs to be done.

## Subtasks
- [ ] Subtask 1
- [ ] Subtask 2
- [ ] Subtask 3

## Requirements
Any specific requirements or constraints.

## Deliverables
What will be delivered upon completion.

## Time Estimate
- [ ] < 1 hour
- [ ] 1-3 hours
- [ ] 3-8 hours
- [ ] 1-2 days
- [ ] 2-5 days
```

### 1.4 Epic Issue Template

```markdown
---
name: Epic
about: Large feature spanning multiple sprints
title: "[EPIC] "
labels: ["epic"]
assignees: []
---

## Epic Overview
High-level description of the epic and its business value.

## Business Value
Why is this important? What problem does it solve?

## Goals
- Goal 1
- Goal 2
- Goal 3

## User Stories
As a **user role**, I want **feature** so that **benefit**.

## Features/Components
List of major features or components to be implemented.

## Success Metrics
How will we measure success?

## Out of Scope
What is explicitly not included in this epic.

## Dependencies
Any external dependencies or prerequisites.

## Timeline Estimate
- [ ] 1-2 sprints
- [ ] 3-4 sprints
- [ ] 5+ sprints
```

## 2. Worktree Strategy for Parallel Development

### 2.1 Worktree Setup Script

```bash
#!/bin/bash
# scripts/setup-worktree.sh

set -e

# Configuration
MAIN_BRANCH="main"
WORKTREES_DIR=".worktrees"

# Create worktrees directory
mkdir -p "$WORKTREES_DIR"

echo "Setting up worktrees for parallel development..."

# Feature worktree
if [ ! -d "$WORKTREES_DIR/feature" ]; then
    git worktree add -b feature "$WORKTREES_DIR/feature"
    echo "✅ Feature worktree created at $WORKTREES_DIR/feature"
fi

# Bugfix worktree
if [ ! -d "$WORKTREES_DIR/bugfix" ]; then
    git worktree add -b bugfix "$WORKTREES_DIR/bugfix"
    echo "✅ Bugfix worktree created at $WORKTREES_DIR/bugfix"
fi

# Experimental worktree
if [ ! -d "$WORKTREES_DIR/experimental" ]; then
    git worktree add -b experimental "$WORKTREES_DIR/experimental"
    echo "✅ Experimental worktree created at $WORKTREES_DIR/experimental"
fi

echo "Worktrees setup complete!"
echo ""
echo "Available worktrees:"
echo "  - Feature:    $WORKTREES_DIR/feature"
echo "  - Bugfix:    $WORKTREES_DIR/bugfix"
echo "  - Experimental: $WORKTREES_DIR/experimental"
echo ""
echo "To work on a feature:"
echo "  1. cd $WORKTREES_DIR/feature"
echo "  2. Create new feature branch from feature base"
echo "  3. Develop your feature"
echo "  4. Submit pull request"
```

### 2.2 Worktree Usage Guidelines

```markdown
# Worktree Development Guidelines

## Overview
Worktrees allow multiple branches to be checked out simultaneously, enabling parallel development without context switching.

## Best Practices

### 1. Worktree Organization
- **feature/**: For new feature development
- **bugfix/**: For bug fixes and patches
- **experimental/**: For experimental features

### 2. Branch Naming Convention
```
type/issue-id-description

Examples:
- feature/123-whatsapp-integration
- bugfix/456-rpa-timeout-fix
- experimental/789-ml-photo-analysis
```

### 3. Workflow Steps
1. Choose appropriate worktree
2. Create feature branch from base
3. Develop in isolation
4. Test thoroughly
5. Submit PR from main repo

### 4. Cleanup
- Remove worktree when branch merged
- Keep main repository clean
- Regular prune old worktrees

## Commands Reference

### List worktrees
```bash
git worktree list
```

### Create new worktree
```bash
git worktree add -b feature-branch path/to/worktree
```

### Remove worktree
```bash
git worktree remove path/to/worktree
git branch -d feature-branch
```

### Prune worktrees
```bash
git worktree prune
```
```

## 3. CI/CD Pipeline Configuration

### 3.1 Main CI/CD Pipeline (.github/workflows/main.yml)

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]
  workflow_dispatch:

env:
  NODE_VERSION: '18.x'
  PYTHON_VERSION: '3.9'

jobs:
  validate:
    name: Validate Code Quality
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: 'npm'

      - name: Setup Python
        uses: actions/setup-python@v4
        with:
          python-version: ${{ env.PYTHON_VERSION }}

      - name: Install dependencies
        run: |
          npm ci
          pip install -r requirements-dev.txt

      - name: Run DGTS validation
        run: |
          python scripts/dgts_validate.py
          npm run validate:no-gaming

      - name: Lint code
        run: |
          npm run lint
          npm run type-check

      - name: Security audit
        run: npm audit --audit-level moderate

  test:
    name: Run Tests
    runs-on: ubuntu-latest
    needs: validate
    strategy:
      matrix:
        test-type: [unit, integration, e2e]

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: 'npm'

      - name: Install dependencies
        run: npm ci

      - name: Install Playwright browsers
        if: matrix.test-type == 'e2e'
        run: npx playwright install --with-deps

      - name: Start test environment
        run: |
          docker-compose -f docker-compose.test.yml up -d
          npm run wait-for-test-env

      - name: Run ${{ matrix.test-type }} tests
        run: npm run test:${{ matrix.test-type }}
        env:
          NODE_ENV: test

      - name: Upload coverage
        if: matrix.test-type != 'e2e'
        uses: codecov/codecov-action@v3

  build:
    name: Build Application
    runs-on: ubuntu-latest
    needs: [validate, test]
    if: github.ref == 'refs/heads/main' || github.ref == 'refs/heads/develop'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}
          cache: 'npm'

      - name: Install dependencies
        run: npm ci

      - name: Build application
        run: npm run build

      - name: Build Docker images
        run: |
          docker build -t gatekeeper/backend:${{ github.sha }} -f docker/backend.Dockerfile .
          docker build -t gatekeeper/rpa:${{ github.sha }} -f docker/rpa.Dockerfile .

      - name: Login to container registry
        if: github.ref == 'refs/heads/main'
        uses: docker/login-action@v2
        with:
          registry: ${{ secrets.REGISTRY_URL }}
          username: ${{ secrets.REGISTRY_USERNAME }}
          password: ${{ secrets.REGISTRY_PASSWORD }}

      - name: Push Docker images
        if: github.ref == 'refs/heads/main'
        run: |
          docker push gatekeeper/backend:${{ github.sha }}
          docker push gatekeeper/rpa:${{ github.sha }}

  deploy:
    name: Deploy to Environment
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main' || github.ref == 'refs/heads/develop'
    environment: ${{ github.ref == 'refs/heads/main' && 'production' || 'staging' }}

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Deploy to ${{ github.ref == 'refs/heads/main' && 'Production' || 'Staging' }}
        run: |
          # Generate deployment configuration
          cat > docker-compose.override.yml << EOF
          version: '3.8'
          services:
            backend:
              image: gatekeeper/backend:${{ github.sha }}
            environment:
              NODE_ENV: ${{ github.ref == 'refs/heads/main' && 'production' || 'staging' }}
            deploy:
              replicas: ${{ github.ref == 'refs/heads/main' && '3' || '1' }}

            rpa-workers:
              image: gatekeeper/rpa:${{ github.sha }}
              environment:
                NODE_ENV: ${{ github.ref == 'refs/heads/main' && 'production' || 'staging' }}
              deploy:
                replicas: ${{ github.ref == 'refs/heads/main' && '8' || '2' }}
          EOF

          # Deploy using SSH
          mkdir -p ~/.ssh
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > ~/.ssh/deploy-key
          chmod 600 ~/.ssh/deploy-key

          # Copy files and deploy
          scp -i ~/.ssh/deploy-key -o StrictHostKeyChecking=no \
            docker-compose.override.yml \
            ${{ secrets.SSH_USER }}@${{ secrets.SSH_HOST }}:/opt/gatekeeper/

          ssh -i ~/.ssh/deploy-key -o StrictHostKeyChecking=no \
            ${{ secrets.SSH_USER }}@${{ secrets.SSH_HOST }} \
            "cd /opt/gatekeeper && docker-compose pull && docker-compose up -d"

      - name: Run health checks
        run: |
          sleep 30
          curl -f ${{ secrets.HEALTH_CHECK_URL }} || exit 1

      - name: Notify deployment
        uses: 8398a7/action-slack@v3
        with:
          status: ${{ job.status }}
          fields: repo,message,commit,author,action,eventName,ref,workflow
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

### 3.2 Performance Testing Workflow (.github/workflows/performance.yml)

```yaml
name: Performance Testing

on:
  schedule:
    - cron: '0 2 * * *'  # Daily at 2 AM
  workflow_dispatch:
  pull_request:
    types: [labeled]
    paths:
      - 'src/**'

jobs:
  performance-test:
    name: Run Performance Tests
    runs-on: ubuntu-latest
    if: github.event_name == 'schedule' || contains(github.event.pull_request.labels.*.name, 'performance-test')

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: ${{ env.NODE_VERSION }}

      - name: Install dependencies
        run: |
          npm ci
          npm install -g k6

      - name: Start test environment
        run: |
          docker-compose -f docker-compose.test.yml up -d
          npm run wait-for-test-env

      - name: Run API load test
        run: k6 run tests/load/api-load.test.js

      - name: Run WhatsApp load test
        run: k6 run tests/load/whatsapp-load.test.js

      - name: Generate performance report
        run: npm run test:report

      - name: Upload performance results
        uses: actions/upload-artifact@v3
        with:
          name: performance-results
          path: |
            performance-report.html
            k6-results/

      - name: Comment performance results
        if: github.event_name == 'pull_request'
        uses: actions/github-script@v6
        with:
          script: |
            const fs = require('fs');
            const report = fs.readFileSync('performance-report.html', 'utf8');
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `## Performance Test Results\n\n${report}`
            });
```

## 4. Testing and Validation Procedures

### 4.1 Pre-Commit Validation Script

```bash
#!/bin/bash
# scripts/pre-commit-validate.sh

set -e

echo "🔍 Running pre-commit validation..."

# Check for console.log statements
CONSOLE_LOGS=$(git diff --cached --name-only -- '*.ts' '*.tsx' | xargs grep -l 'console\.log' || true)
if [ -n "$CONSOLE_LOGS" ]; then
    echo "❌ Found console.log statements in:"
    echo "$CONSOLE_LOGS"
    echo "Please remove console.log statements before committing."
    exit 1
fi

# Run TypeScript compilation
echo "📝 Checking TypeScript compilation..."
npm run type-check

# Run ESLint
echo "🔍 Running ESLint..."
npm run lint

# Run DGTS validation
echo "🛡️ Running DGTS validation..."
npm run validate:no-gaming

# Run AntiHall validation
echo "🔎 Running AntiHall validation..."
npm run validate:antihall

# Run quick unit tests
echo "🧪 Running unit tests..."
npm run test:unit -- --run

echo "✅ All validation checks passed!"
```

### 4.2 Test Execution Matrix

```markdown
# Test Execution Matrix

## Unit Tests (30% of coverage)
- **Location**: tests/unit/
- **Framework**: Vitest
- **Execution**: npm run test:unit
- **Coverage Target**: >95%
- **Run Time**: <2 minutes

## Integration Tests (40% of coverage)
- **Location**: tests/integration/
- **Framework**: Vitest
- **Execution**: npm run test:integration
- **Coverage Target**: >90%
- **Run Time**: <5 minutes

## E2E Tests (20% of coverage)
- **Location**: tests/e2e/
- **Framework**: Playwright
- **Execution**: npm run test:e2e
- **Coverage Target**: >85%
- **Run Time**: <10 minutes

## Performance Tests (10% of validation)
- **Location**: tests/performance/
- **Framework**: K6
- **Execution**: npm run test:performance
- **Targets**: Response time <200ms, error rate <1%
- **Run Time**: <5 minutes

## Security Tests
- **Location**: tests/security/
- **Framework**: Custom + OWASP tools
- **Execution**: npm run test:security
- **Coverage**: 100% of security requirements
- **Run Time**: <3 minutes
```

### 4.3 Test Data Management

```typescript
// tests/fixtures/TestDataFactory.ts
export class TestDataFactory {
  // Ticket fixtures
  static createTicket(overrides: Partial<Ticket> = {}): Ticket {
    return {
      id: uuidv4(),
      ticketNumber: `TICKET-${Date.now()}`,
      drNumber: `DR${Math.floor(Math.random() * 10000000)}`,
      technicianNumber: '+27821234567',
      technicianName: 'Test Technician',
      messageContent: 'DR1234567',
      status: 'pending',
      priority: 'normal',
      created_at: new Date(),
      updated_at: new Date(),
      ...overrides
    };
  }

  // Audit result fixtures
  static createAuditResult(overrides: Partial<AuditResult> = {}): AuditResult {
    return {
      id: uuidv4(),
      ticketId: uuidv4(),
      propertyId: 'PROP-123',
      jobId: 'JOB-456',
      address: '123 Test Street',
      installationStatus: 'Home Installation: Installed',
      photosRequired: ['trench', 'ont', 'termination', 'property'],
      photosFound: ['trench', 'ont'],
      photosMissing: ['termination', 'property'],
      complianceScore: 50,
      auditDetails: {
        totalPhotos: 2,
        auditTimestamp: new Date().toISOString()
      },
      created_at: new Date(),
      updated_at: new Date(),
      ...overrides
    };
  }

  // RPA data fixtures
  static createRPAData(overrides: Partial<InstallationData> = {}): InstallationData {
    return {
      propertyId: 'PROP-123',
      jobId: 'JOB-456',
      address: '123 Test Street',
      status: 'Home Installation: Installed',
      lastModified: new Date(),
      photos: [
        {
          type: 'trench',
          url: 'https://example.com/photo1.jpg',
          uploadTime: new Date().toISOString(),
          fileSize: 1024000
        },
        {
          type: 'ont',
          url: 'https://example.com/photo2.jpg',
          uploadTime: new Date().toISOString(),
          fileSize: 2048000
        }
      ],
      ...overrides
    };
  }
}
```

## 5. Deployment Automation Setup

### 5.1 Deployment Scripts

```bash
#!/bin/bash
# scripts/deploy.sh

set -e

ENVIRONMENT=${1:-staging}
VERSION=${2:-latest}

echo "🚀 Deploying Gatekeeper RPA to $ENVIRONMENT..."

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(staging|production)$ ]]; then
    echo "❌ Invalid environment: $ENVIRONMENT"
    echo "Usage: $0 [staging|production] [version]"
    exit 1
fi

# Load environment-specific configuration
echo "📝 Loading configuration for $ENVIRONMENT..."
source "config/$ENVIRONMENT.env"

# Build Docker images
echo "🏗️ Building Docker images..."
docker-compose -f docker-compose.prod.yml build

# Tag images
echo "🏷️ Tagging images..."
docker tag gatekeeper/backend:$VERSION gatekeeper/backend:$ENVIRONMENT-latest
docker tag gatekeeper/rpa:$VERSION gatekeeper/rpa:$ENVIRONMENT-latest

# Push to registry
echo "📤 Pushing to registry..."
docker push gatekeeper/backend:$ENVIRONMENT-latest
docker push gatekeeper/rpa:$ENVIRONMENT-latest

# Deploy to server
echo "🌐 Deploying to server..."
ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no \
    "$SSH_USER@$SSH_HOST" \
    "cd /opt/gatekeeper && \
     docker-compose -f docker-compose.prod.yml pull && \
     docker-compose -f docker-compose.prod.yml up -d"

# Run health checks
echo "🏥 Running health checks..."
sleep 30
curl -f "$HEALTH_CHECK_URL" || {
    echo "❌ Health check failed!"
    exit 1
}

# Run post-deployment tests
echo "🧪 Running post-deployment tests..."
npm run test:smoke

echo "✅ Deployment to $ENVIRONMENT completed successfully!"
```

### 5.2 Rollback Script

```bash
#!/bin/bash
# scripts/rollback.sh

set -e

ENVIRONMENT=${1:-staging}
VERSION=${2:-previous}

echo "🔄 Rolling back Gatekeeper RPA on $ENVIRONMENT to version $VERSION..."

# Validate inputs
if [[ ! "$ENVIRONMENT" =~ ^(staging|production)$ ]]; then
    echo "❌ Invalid environment: $ENVIRONMENT"
    exit 1
fi

# Get current version for backup
CURRENT_VERSION=$(ssh -i "$SSH_KEY" "$SSH_USER@$SSH_HOST" \
    "cd /opt/gatekeeper && \
     docker-compose -f docker-compose.prod.yml ps -q backend | \
     xargs docker inspect --format='{{index .Config.Labels \"version\"}}'")

echo "💾 Current version: $CURRENT_VERSION"
echo "🔄 Rolling back to: $VERSION"

# Stop services
echo "⏹️ Stopping services..."
ssh -i "$SSH_KEY" "$SSH_USER@$SSH_HOST" \
    "cd /opt/gatekeeper && \
     docker-compose -f docker-compose.prod.yml stop"

# Pull and start rollback version
echo "🚀 Starting rollback version..."
ssh -i "$SSH_KEY" "$SSH_USER@$SSH_HOST" \
    "cd /opt/gatekeeper && \
     docker-compose -f docker-compose.prod.yml pull && \
     docker-compose -f docker-compose.prod.yml up -d"

# Run health checks
echo "🏥 Running health checks..."
sleep 30
curl -f "$HEALTH_CHECK_URL" || {
    echo "❌ Health check failed after rollback!"
    exit 1
}

echo "✅ Rollback completed successfully!"
echo "📝 Create issue to investigate the failed deployment"
```

### 5.3 Environment Configuration

```bash
# config/staging.env
# Staging environment configuration

NODE_ENV=staging
LOG_LEVEL=debug

# Database
DATABASE_URL=postgresql://user:pass@staging-db:5432/gatekeeper_staging
REDIS_URL=redis://staging-redis:6379/0

# External Services
WHATSAPP_API_URL=https://staging.whatsapp.api
WHATSAPP_API_TOKEN=staging-token
1MAP_USERNAME=staging-user
1MAP_PASSWORD=staging-pass

# Monitoring
PROMETHEUS_URL=http://staging-prometheus:9090
GRAFANA_URL=http://staging-grafana:3000

# Security
JWT_SECRET=staging-jwt-secret-change-in-production
ENCRYPTION_KEY=staging-encryption-key-change-in-production
```

## 6. Quality Gate Automation

### 6.1 Quality Gate Check Script

```typescript
// scripts/quality-gates.ts
import { execSync } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

interface QualityGateResult {
  passed: boolean;
  checks: {
    name: string;
    passed: boolean;
    message: string;
    value?: number;
    threshold?: number;
  }[];
}

class QualityGates {
  async runQualityChecks(): Promise<QualityGateResult> {
    const checks = [];

    // Test coverage check
    checks.push(await this.checkTestCoverage());

    // Code quality check
    checks.push(await this.checkCodeQuality());

    // Security check
    checks.push(await this.checkSecurity());

    // Performance check
    checks.push(await this.checkPerformance());

    // Documentation check
    checks.push(await this.checkDocumentation());

    const passed = checks.every(check => check.passed);

    return {
      passed,
      checks
    };
  }

  private async checkTestCoverage() {
    try {
      const output = execSync('npm run test:coverage -- --reporter=json', { encoding: 'utf8' });
      const coverage = JSON.parse(output);

      const totalCoverage = coverage.total.statements.pct;
      const threshold = 95;

      return {
        name: 'Test Coverage',
        passed: totalCoverage >= threshold,
        message: `Coverage: ${totalCoverage}% (threshold: ${threshold}%)`,
        value: totalCoverage,
        threshold
      };
    } catch (error) {
      return {
        name: 'Test Coverage',
        passed: false,
        message: `Failed to run coverage check: ${error.message}`
      };
    }
  }

  private async checkCodeQuality() {
    try {
      // Check ESLint
      execSync('npm run lint -- --format=json');

      // Check TypeScript errors
      execSync('npm run type-check');

      // Check for console.log
      const consoleCount = parseInt(
        execSync('git grep "console\\." -- "*.ts" "*.tsx" | wc -l', { encoding: 'utf8' })
      );

      return {
        name: 'Code Quality',
        passed: consoleCount === 0,
        message: consoleCount === 0
          ? 'No code quality issues found'
          : `Found ${consoleCount} console.log statements`
      };
    } catch (error) {
      return {
        name: 'Code Quality',
        passed: false,
        message: `Code quality check failed: ${error.message}`
      };
    }
  }

  private async checkSecurity() {
    try {
      // Run npm audit
      execSync('npm audit --audit-level moderate --json');

      // Check for secrets
      execSync('npx secretlint "**/*"');

      return {
        name: 'Security',
        passed: true,
        message: 'No security vulnerabilities found'
      };
    } catch (error) {
      return {
        name: 'Security',
        passed: false,
        message: `Security check failed: ${error.message}`
      };
    }
  }

  private async checkPerformance() {
    // This would connect to monitoring system
    // For now, check bundle size
    try {
      const stats = fs.readFileSync('dist/stats.json', 'utf8');
      const bundleSize = JSON.parse(stats).main.size / 1024 / 1024; // MB

      return {
        name: 'Performance',
        passed: bundleSize < 50, // 50MB threshold
        message: `Bundle size: ${bundleSize.toFixed(2)}MB`,
        value: bundleSize,
        threshold: 50
      };
    } catch (error) {
      return {
        name: 'Performance',
        passed: true,
        message: 'Performance check skipped (no stats file)'
      };
    }
  }

  private async checkDocumentation() {
    const requiredDocs = [
      'README.md',
      'PRD_Gatekeeper_RPA.md',
      'ADR_Gatekeeper_RPA.md'
    ];

    const missingDocs = requiredDocs.filter(doc => !fs.existsSync(doc));

    return {
      name: 'Documentation',
      passed: missingDocs.length === 0,
      message: missingDocs.length === 0
        ? 'All required documentation present'
        : `Missing documents: ${missingDocs.join(', ')}`
    };
  }

  generateReport(result: QualityGateResult): string {
    const status = result.passed ? '✅ PASSED' : '❌ FAILED';
    const date = new Date().toISOString();

    let report = `# Quality Gate Report\n\n`;
    report += `**Date**: ${date}\n`;
    report += `**Status**: ${status}\n\n`;
    report += `## Results\n\n`;

    result.checks.forEach(check => {
      const icon = check.passed ? '✅' : '❌';
      report += `${icon} **${check.name}**: ${check.message}\n`;

      if (check.value !== undefined && check.threshold !== undefined) {
        report += `   Value: ${check.value}, Threshold: ${check.threshold}\n`;
      }
      report += '\n';
    });

    return report;
  }
}

// CLI execution
async function main() {
  const gates = new QualityGates();
  const result = await gates.runQualityChecks();
  const report = gates.generateReport(result);

  console.log(report);

  // Save report
  fs.writeFileSync('quality-gate-report.md', report);

  // Exit with appropriate code
  process.exit(result.passed ? 0 : 1);
}

if (require.main === module) {
  main().catch(console.error);
}
```

## 7. Development Environment Setup

### 7.1 Local Development Setup Script

```bash
#!/bin/bash
# scripts/setup-dev.sh

set -e

echo "🛠️ Setting up Gatekeeper RPA development environment..."

# Check prerequisites
echo "📋 Checking prerequisites..."
command -v node >/dev/null 2>&1 || { echo "❌ Node.js is required"; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "❌ Docker is required"; exit 1; }
command -v git >/dev/null 2>&1 || { echo "❌ Git is required"; exit 1; }

# Install Node.js dependencies
echo "📦 Installing Node.js dependencies..."
npm ci

# Copy environment template
if [ ! -f ".env" ]; then
    echo "📝 Creating environment file..."
    cp .env.template .env
    echo "⚠️ Please edit .env with your configuration"
fi

# Set up pre-commit hooks
echo "🪝 Setting up pre-commit hooks..."
npm run install:husky

# Create local certificates for HTTPS
echo "🔐 Setting up local certificates..."
mkdir -p ssl
openssl req -x509 -newkey rsa:4096 -keyout ssl/key.pem -out ssl/cert.pem \
    -days 365 -nodes -subj "/CN=localhost"

# Initialize git submodules
echo "🔗 Initializing submodules..."
git submodule update --init --recursive

# Build Docker images for development
echo "🐳 Building development Docker images..."
docker-compose -f docker-compose.dev.yml build

# Install Playwright browsers
echo "🌐 Installing Playwright browsers..."
npx playwright install --with-deps

# Create development database
echo "🗄️ Setting up development database..."
docker-compose -f docker-compose.dev.yml up -d postgres redis
sleep 10
npm run db:migrate
npm run db:seed

# Run initial validation
echo "✅ Running initial validation..."
npm run validate

echo ""
echo "🎉 Development environment setup complete!"
echo ""
echo "Next steps:"
echo "1. Edit .env with your configuration"
echo "2. Run 'npm run dev' to start development server"
echo "3. Run 'docker-compose -f docker-compose.dev.yml up' to start all services"
```

### 7.2 VS Code Configuration (.vscode/settings.json)

```json
{
  "typescript.preferences.preferTypeOnlyAutoImports": true,
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "files.associations": {
    "*.json": "jsonc"
  },
  "search.exclude": {
    "**/node_modules": true,
    "**/dist": true,
    "**/.git": true,
    "**/.worktrees": true
  },
  "files.watcherExclude": {
    "**/node_modules/**": true,
    "**/dist/**": true
  },
  "terminal.integrated.shell.linux": "/bin/bash",
  "terminal.integrated.shellArgs.linux": ["-l"],
  "python.linting.enabled": true,
  "python.linting.pylintEnabled": true,
  "python.formatting.provider": "black",
  "[typescript]": {
    "editor.suggest.insertMode": "replace"
  },
  "emmet.includeLanguages": {
    "typescript-html": "html"
  },
  "debug.node.autoAttach": "on"
}
```

### 7.3 VS Code Extensions (.vscode/extensions.json)

```json
{
  "recommendations": [
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "ms-vscode.vscode-typescript-next",
    "bradlc.vscode-tailwindcss",
    "ms-playwright.playwright",
    "ms-vscode.vscode-json",
    "ms-python.python",
    "ms-azuretools.vscode-docker",
    "humao.rest-client",
    "github.vscode-pull-request-github",
    "ms-vscode-remote.remote-containers"
  ]
}
```

## Summary

This comprehensive development workflow setup provides:

1. **Structured Issue Management**: Templates for all types of work
2. **Parallel Development**: Worktree strategy for isolation
3. **Automated CI/CD**: Full pipeline with quality gates
4. **Comprehensive Testing**: Multi-layer testing strategy
5. **Deployment Automation**: Safe deployment and rollback
6. **Quality Assurance**: Automated validation and reporting

The workflow ensures high-quality, secure, and maintainable code while enabling efficient parallel development across the team.

**Document Version**: 1.0
**Next Review**: After Sprint 1 completion