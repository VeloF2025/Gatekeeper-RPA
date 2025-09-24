#!/usr/bin/env python3
"""
DGTS (Documentation-Driven Test Development) Validation Script
Ensures compliance with anti-gaming measures and quality gates
"""

import os
import sys
import re
import json
import subprocess
import ast
import time
from pathlib import Path
from datetime import datetime
from typing import Dict, List, Tuple, Optional
from dataclasses import dataclass, asdict
import logging

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('dgts_validation.log'),
        logging.StreamHandler()
    ]
)

@dataclass
class ValidationResult:
    """Represents the result of a validation check"""
    name: str
    passed: bool
    message: str
    details: Optional[Dict] = None
    severity: str = 'medium'  # low, medium, high, critical

@dataclass
class GamingPattern:
    """Represents a detected gaming pattern"""
    type: str
    description: str
    file: str
    line: int
    severity: str
    confidence: float

class DGTSValidator:
    """Main validator class for DGTS compliance"""

    def __init__(self, project_root: str = None):
        self.project_root = Path(project_root) if project_root else Path.cwd()
        self.gaming_patterns = []
        self.validation_results = []
        self.gaming_score = 0.0

        # Define gaming patterns to detect
        self.pattern_definitions = {
            'FAKE_IMPLEMENTATION': {
                'patterns': [
                    r'return\s+["\']mock_data["\']',
                    r'return\s+["\']fake["\']',
                    r'return\s+["\']placeholder["\']',
                    r'return\s+{\s*["\']mock["\']:\s*true\s*}',
                ],
                'severity': 'critical',
                'weight': 1.0
            },
            'COMMENTED_VALIDATION': {
                'patterns': [
                    r'//\s*validation_required',
                    r'//\s*TODO:\s*implement',
                    r'//\s*FIXME:\s*remove',
                    r'//\s*HACK:',
                ],
                'severity': 'high',
                'weight': 0.8
            },
            'STUB_FUNCTION': {
                'patterns': [
                    r'pass\s*#\s*TODO',
                    r'raise\s+NotImplementedError',
                    r'return\s+None\s*#\s*stub',
                    r'def\s+\w+\([^)]*\):\s*pass\s*$',
                ],
                'severity': 'high',
                'weight': 0.6
            },
            'ALWAYS_TRUE_ASSERTION': {
                'patterns': [
                    r'assert\s+True',
                    r'expect\(true\)\.toBe\(true\)',
                    r'assertEqual\(True,\s*True\)',
                ],
                'severity': 'high',
                'weight': 0.9
            },
            'DISABLED_TEST': {
                'patterns': [
                    r'@skip',
                    r'@pytest\.mark\.skip',
                    r'xdescribe\(',
                    r'xit\(',
                    r'test\.skip\(',
                ],
                'severity': 'medium',
                'weight': 0.4
            },
            'EMPTY_TEST': {
                'patterns': [
                    r'def\s+test_\w+\([^)]*\):\s*pass\s*$',
                    r'test\([^)]*\)\s*{\s*}',
                ],
                'severity': 'medium',
                'weight': 0.5
            },
            'CONSOLE_LOG': {
                'patterns': [
                    r'console\.log\(',
                    r'console\.debug\(',
                    r'console\.info\(',
                ],
                'severity': 'high',
                'weight': 0.7
            }
        }

    def validate_project(self) -> bool:
        """Run all validation checks"""
        logging.info("Starting DGTS validation...")
        start_time = time.time()

        # Run all validations
        results = []

        results.append(self._validate_test_existence())
        results.append(self._validate_test_coverage())
        results.append(self._validate_code_quality())
        results.append(self._validate_gaming_patterns())
        results.append(self._validate_documentation())
        results.append(self._validate_dependencies())

        # Calculate overall result
        passed = all(result.passed for result in results)
        self.validation_results = results

        # Generate report
        self._generate_report()

        execution_time = time.time() - start_time
        logging.info(f"DGTS validation completed in {execution_time:.2f}s")

        return passed

    def _validate_test_existence(self) -> ValidationResult:
        """Validate that test files exist for all source files"""
        logging.info("Validating test file existence...")

        source_files = list(self.project_root.glob("src/**/*.ts"))
        source_files.extend(list(self.project_root.glob("src/**/*.tsx")))
        source_files.extend(list(self.project_root.glob("src/**/*.js")))
        source_files.extend(list(self.project_root.glob("src/**/*.py")))

        missing_tests = []
        test_ratio = 0.0

        if source_files:
            test_files = 0
            for source_file in source_files:
                # Find corresponding test file
                test_file_patterns = [
                    source_file.with_suffix('.spec.ts'),
                    source_file.with_suffix('.test.ts'),
                    source_file.with_suffix('.spec.js'),
                    source_file.with_suffix('.test.js'),
                    source_file.with_suffix('_test.py'),
                    source_file.with_name(f"test_{source_file.name}"),
                ]

                test_exists = any(pattern.exists() for pattern in test_file_patterns)
                if not test_exists and not source_file.name.startswith('index'):
                    missing_tests.append(str(source_file))
                else:
                    test_files += 1

            test_ratio = test_files / len(source_files)

        passed = len(missing_tests) == 0
        message = f"Test coverage: {test_ratio:.1%} ({len(missing_tests)} files missing tests)"

        return ValidationResult(
            name="Test Existence",
            passed=passed,
            message=message,
            details={
                "source_files": len(source_files),
                "test_files": len(source_files) - len(missing_tests),
                "missing_tests": missing_tests,
                "test_ratio": test_ratio
            },
            severity='high' if test_ratio < 0.8 else 'medium'
        )

    def _validate_test_coverage(self) -> ValidationResult:
        """Validate test coverage meets requirements"""
        logging.info("Validating test coverage...")

        try:
            # Run coverage report
            result = subprocess.run(
                ['npm', 'run', 'test:coverage', '--', '--silent'],
                capture_output=True,
                text=True,
                cwd=self.project_root
            )

            # Parse coverage from output (simplified)
            coverage = 0.0
            if result.returncode == 0:
                # Extract coverage percentage from output
                coverage_match = re.search(r'All files[^|]*\|\s*([\d.]+)', result.stdout)
                if coverage_match:
                    coverage = float(coverage_match.group(1))

            passed = coverage >= 95.0
            message = f"Test coverage: {coverage:.1f}% (target: 95%)"

            return ValidationResult(
                name="Test Coverage",
                passed=passed,
                message=message,
                details={"coverage": coverage, "target": 95.0},
                severity='critical'
            )

        except Exception as e:
            logging.error(f"Error validating coverage: {e}")
            return ValidationResult(
                name="Test Coverage",
                passed=False,
                message=f"Error running coverage: {str(e)}",
                severity='critical'
            )

    def _validate_code_quality(self) -> ValidationResult:
        """Validate code quality with linting"""
        logging.info("Validating code quality...")

        errors = []
        warnings = []

        # Run ESLint
        try:
            result = subprocess.run(
                ['npm', 'run', 'lint', '--', '--format=json'],
                capture_output=True,
                text=True,
                cwd=self.project_root
            )

            if result.stdout:
                lint_results = json.loads(result.stdout)
                for issue in lint_results:
                    if issue.get('severity') == 2:  # Error
                        errors.append(issue)
                    else:  # Warning
                        warnings.append(issue)
        except Exception as e:
            logging.error(f"Error running lint: {e}")

        # Check for TypeScript errors
        try:
            result = subprocess.run(
                ['npm', 'run', 'type-check'],
                capture_output=True,
                text=True,
                cwd=self.project_root
            )

            if result.returncode != 0:
                # Parse TypeScript errors
                for line in result.stderr.split('\n'):
                    if 'error TS' in line:
                        errors.append({"message": line.strip()})
        except Exception as e:
            logging.error(f"Error running type check: {e}")

        passed = len(errors) == 0 and len(warnings) == 0
        message = f"Code quality: {len(errors)} errors, {len(warnings)} warnings"

        return ValidationResult(
            name="Code Quality",
            passed=passed,
            message=message,
            details={"errors": len(errors), "warnings": len(warnings)},
            severity='critical' if errors else 'high' if warnings else 'low'
        )

    def _validate_gaming_patterns(self) -> ValidationResult:
        """Detect gaming patterns in code"""
        logging.info("Detecting gaming patterns...")

        self.gaming_patterns = []
        total_weight = 0.0
        max_weight = 0.0

        # Scan source files
        source_extensions = ['.ts', '.tsx', '.js', '.jsx', '.py']
        for ext in source_extensions:
            for file_path in self.project_root.rglob(f"*{ext}"):
                if 'node_modules' in str(file_path):
                    continue
                if 'test' in file_path.parts and 'spec' not in file_path.name:
                    continue

                patterns = self._scan_file_for_gaming_patterns(file_path)
                self.gaming_patterns.extend(patterns)

        # Calculate gaming score
        for pattern in self.gaming_patterns:
            pattern_def = self.pattern_definitions.get(pattern.type, {})
            weight = pattern_def.get('weight', 0.5) * pattern.confidence
            total_weight += weight
            max_weight += pattern_def.get('weight', 0.5)

        self.gaming_score = total_weight / max_weight if max_weight > 0 else 0.0

        passed = self.gaming_score < 0.3
        message = f"Gaming score: {self.gaming_score:.2f} (threshold: 0.3)"

        return ValidationResult(
            name="Anti-Gaming",
            passed=passed,
            message=message,
            details={
                "gaming_score": self.gaming_score,
                "patterns_found": len(self.gaming_patterns),
                "patterns": [asdict(p) for p in self.gaming_patterns]
            },
            severity='critical'
        )

    def _scan_file_for_gaming_patterns(self, file_path: Path) -> List[GamingPattern]:
        """Scan a single file for gaming patterns"""
        patterns = []

        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                lines = content.split('\n')

            for pattern_name, pattern_def in self.pattern_definitions.items():
                for pattern_regex in pattern_def['patterns']:
                    for line_num, line in enumerate(lines, 1):
                        matches = re.finditer(pattern_regex, line, re.IGNORECASE)
                        for match in matches:
                            patterns.append(GamingPattern(
                                type=pattern_name,
                                description=f"Matched pattern: {match.group()}",
                                file=str(file_path),
                                line=line_num,
                                severity=pattern_def['severity'],
                                confidence=0.9
                            ))

        except Exception as e:
            logging.error(f"Error scanning file {file_path}: {e}")

        return patterns

    def _validate_documentation(self) -> ValidationResult:
        """Validate documentation requirements"""
        logging.info("Validating documentation...")

        required_docs = [
            'PRD - Gatekeeper RPA.md',
            'PRP - Gatekeeper RPA.md',
            'TDD_SPECS_Gatekeeper_RPA.md'
        ]

        missing_docs = []
        existing_docs = []

        for doc in required_docs:
            if (self.project_root / doc).exists():
                existing_docs.append(doc)
            else:
                missing_docs.append(doc)

        passed = len(missing_docs) == 0
        message = f"Documentation: {len(existing_docs)}/{len(required_docs)} required docs found"

        return ValidationResult(
            name="Documentation",
            passed=passed,
            message=message,
            details={
                "required_docs": required_docs,
                "existing_docs": existing_docs,
                "missing_docs": missing_docs
            },
            severity='medium'
        )

    def _validate_dependencies(self) -> ValidationResult:
        """Validate dependency management"""
        logging.info("Validating dependencies...")

        vulnerabilities = []
        outdated = []

        # Check for npm vulnerabilities
        try:
            result = subprocess.run(
                ['npm', 'audit', '--json'],
                capture_output=True,
                text=True,
                cwd=self.project_root
            )

            if result.stdout:
                audit_result = json.loads(result.stdout)
                vulnerabilities = audit_result.get('vulnerabilities', {})
        except Exception as e:
            logging.error(f"Error running npm audit: {e}")

        # Check for outdated packages
        try:
            result = subprocess.run(
                ['npm', 'outdated', '--json'],
                capture_output=True,
                text=True,
                cwd=self.project_root
            )

            if result.stdout:
                outdated = json.loads(result.stdout)
        except Exception as e:
            logging.error(f"Error checking outdated packages: {e}")

        passed = len(vulnerabilities) == 0
        message = f"Dependencies: {len(vulnerabilities)} vulnerabilities, {len(outdated)} outdated packages"

        return ValidationResult(
            name="Dependencies",
            passed=passed,
            message=message,
            details={
                "vulnerabilities": len(vulnerabilities),
                "outdated_packages": len(outdated),
                "vulnerability_details": vulnerabilities
            },
            severity='critical' if vulnerabilities else 'medium'
        )

    def _generate_report(self):
        """Generate validation report"""
        report = {
            "timestamp": datetime.now().isoformat(),
            "project_root": str(self.project_root),
            "overall_result": all(result.passed for result in self.validation_results),
            "gaming_score": self.gaming_score,
            "results": [asdict(result) for result in self.validation_results]
        }

        # Save report
        report_file = self.project_root / 'dgts_validation_report.json'
        with open(report_file, 'w') as f:
            json.dump(report, f, indent=2)

        # Print summary
        print("\n" + "="*60)
        print("DGTS VALIDATION REPORT")
        print("="*60)
        print(f"Overall Result: {'PASS' if report['overall_result'] else 'FAIL'}")
        print(f"Gaming Score: {self.gaming_score:.2f}")
        print("-"*60)

        for result in self.validation_results:
            status = "✅ PASS" if result.passed else "❌ FAIL"
            print(f"{status} {result.name}: {result.message}")

        print("="*60)

        if not report['overall_result']:
            print("\nValidation failed. Please fix the issues before committing.")
            sys.exit(1)

def main():
    """Main entry point"""
    import argparse

    parser = argparse.ArgumentParser(description='DGTS Validation Script')
    parser.add_argument('--project-root', type=str, help='Project root directory')
    parser.add_argument('--output', type=str, help='Output file for report')
    args = parser.parse_args()

    validator = DGTSValidator(args.project_root)
    passed = validator.validate_project()

    sys.exit(0 if passed else 1)

if __name__ == '__main__':
    main()