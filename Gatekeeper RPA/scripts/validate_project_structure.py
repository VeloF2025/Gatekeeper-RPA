#!/usr/bin/env python3
"""
Project Structure Validation Script
Validates that all required files and directories exist
"""

import os
import json
import logging
from pathlib import Path
from typing import Dict, List, Set
from datetime import datetime

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

class ProjectStructureValidator:
    """Validates the Gatekeeper RPA project structure"""

    def __init__(self, project_root: str = None):
        self.project_root = Path(project_root) if project_root else Path.cwd()
        self.results = {
            "required": {"passed": 0, "total": 0, "missing": []},
            "recommended": {"passed": 0, "total": 0, "missing": []},
            "optional": {"passed": 0, "total": 0, "missing": []}
        }

    def validate_structure(self) -> Dict:
        """Run complete structure validation"""
        logging.info(f"Validating project structure at: {self.project_root}")

        # Define required structure
        required_structure = self._get_required_structure()
        recommended_structure = self._get_recommended_structure()
        optional_structure = self._get_optional_structure()

        # Validate each category
        self._validate_category(required_structure, "required")
        self._validate_category(recommended_structure, "recommended")
        self._validate_category(optional_structure, "optional")

        # Generate report
        report = self._generate_report()

        # Save report
        report_file = self.project_root / "project_structure_validation.json"
        with open(report_file, 'w') as f:
            json.dump(report, f, indent=2)

        return report

    def _get_required_structure(self) -> Dict[str, List]:
        """Define required project structure"""
        return {
            "directories": [
                "src",
                "src/app",
                "src/components",
                "src/lib",
                "src/types",
                "tests",
                "tests/unit",
                "tests/integration",
                "tests/e2e",
                "docs",
                "docker",
                "scripts"
            ],
            "files": [
                "PRD - Gatekeeper RPA.md",
                "PRP - Gatekeeper RPA.md",
                "TDD_SPECS_Gatekeeper_RPA.md",
                "PROJECT_PLAN_SUMMARY.md",
                "package.json",
                "tsconfig.json",
                "tailwind.config.js",
                "docker-compose.yml",
                "docker/docker-compose.prod.yml",
                "docker/backend.Dockerfile",
                "docker/frontend.Dockerfile",
                "docker/rpa.Dockerfile",
                "docker/nginx/nginx.conf",
                "scripts/validate_dgts.py",
                "scripts/validate_project_structure.py"
            ],
            "config_files": [
                ".env.example",
                ".gitignore",
                ".eslintrc.json",
                ".prettierrc",
                "jest.config.js",
                "playwright.config.ts"
            ]
        }

    def _get_recommended_structure(self) -> Dict[str, List]:
        """Define recommended project structure"""
        return {
            "directories": [
                "src/app/api",
                "src/hooks",
                "src/utils",
                "src/services",
                "src/models",
                "tests/security",
                "tests/performance",
                "docs/api",
                "docs/architecture",
                "docs/deployment",
                "docker/scripts",
                "docker/monitoring"
            ],
            "files": [
                "README.md",
                "CHANGELOG.md",
                "CONTRIBUTING.md",
                "SECURITY.md",
                "docker/docker-compose.dev.yml",
                "docker/monitoring/prometheus.yml",
                "docker/monitoring/grafana/dashboard.json",
                "scripts/deploy.sh",
                "scripts/backup.sh",
                "docs/WHATSAPP_TICKETING_ARCHITECTURE.md",
                "docs/DOCKER_DEPLOYMENT_GUIDE.md",
                "docs/1MAP_RPA_INTEGRATION_GUIDE.md"
            ]
        }

    def _get_optional_structure(self) -> Dict[str, List]:
        """Define optional project structure"""
        return {
            "directories": [
                "src/middleware",
                "src/store",
                "src/styles",
                "public/images",
                "public/icons",
                "tools",
                ".github/workflows",
                ".vscode"
            ],
            "files": [
                "Dockerfile",
                ".dockerignore",
                ".nvmrc",
                "CODE_OF_CONDUCT.md",
                "LICENSE",
                ".github/dependabot.yml",
                ".github/ISSUE_TEMPLATE/bug_report.md",
                ".github/ISSUE_TEMPLATE/feature_request.md",
                ".github/PULL_REQUEST_TEMPLATE.md",
                ".vscode/settings.json",
                ".vscode/extensions.json"
            ]
        }

    def _validate_category(self, structure: Dict, category: str):
        """Validate a category of structure"""
        logging.info(f"Validating {category} structure...")

        for item_type, items in structure.items():
            for item in items:
                item_path = self.project_root / item

                exists = item_path.exists()
                self.results[category]["total"] += 1

                if exists:
                    self.results[category]["passed"] += 1
                    logging.info(f"  ✓ {item}")
                else:
                    self.results[category]["missing"].append(str(item))
                    logging.warning(f"  ✗ {item}")

    def _generate_report(self) -> Dict:
        """Generate validation report"""
        report = {
            "timestamp": datetime.now().isoformat(),
            "project_root": str(self.project_root),
            "overall_score": self._calculate_overall_score(),
            "categories": {}
        }

        # Calculate scores for each category
        for category, data in self.results.items():
            if data["total"] > 0:
                score = (data["passed"] / data["total"]) * 100
            else:
                score = 100

            report["categories"][category] = {
                "score": round(score, 1),
                "passed": data["passed"],
                "total": data["total"],
                "missing": data["missing"]
            }

        # Add recommendations
        report["recommendations"] = self._generate_recommendations()

        return report

    def _calculate_overall_score(self) -> float:
        """Calculate overall project score"""
        weights = {
            "required": 0.6,
            "recommended": 0.3,
            "optional": 0.1
        }

        total_score = 0
        total_weight = 0

        for category, weight in weights.items():
            data = self.results[category]
            if data["total"] > 0:
                score = (data["passed"] / data["total"]) * 100
                total_score += score * weight
                total_weight += weight

        return round(total_score / total_weight, 1) if total_weight > 0 else 0

    def _generate_recommendations(self) -> List[Dict]:
        """Generate improvement recommendations"""
        recommendations = []

        # Check required items
        if self.results["required"]["missing"]:
            recommendations.append({
                "priority": "critical",
                "category": "Required",
                "message": f"Missing {len(self.results['required']['missing'])} required files/directories",
                "items": self.results["required"]["missing"][:5]  # Show first 5
            })

        # Check recommended items
        if self.results["recommended"]["passed"] / self.results["recommended"]["total"] < 0.8:
            recommendations.append({
                "priority": "high",
                "category": "Recommended",
                "message": "Consider adding recommended structure for better organization",
                "items": self.results["recommended"]["missing"][:5]
            })

        # Check optional items
        if self.results["optional"]["passed"] / self.results["optional"]["total"] < 0.5:
            recommendations.append({
                "priority": "low",
                "category": "Optional",
                "message": "Optional items can improve developer experience",
                "items": self.results["optional"]["missing"][:5]
            })

        # Add best practice recommendations
        if not (self.project_root / ".github" / "workflows").exists():
            recommendations.append({
                "priority": "medium",
                "category": "Best Practices",
                "message": "Consider adding GitHub Actions workflows for CI/CD"
            })

        if not (self.project_root / "docs" / "api").exists():
            recommendations.append({
                "priority": "medium",
                "category": "Documentation",
                "message": "Consider creating API documentation"
            })

        return recommendations

    def print_summary(self):
        """Print validation summary"""
        report = self._generate_report()

        print("\n" + "="*60)
        print("PROJECT STRUCTURE VALIDATION REPORT")
        print("="*60)
        print(f"Overall Score: {report['overall_score']}%")
        print("-"*60)

        for category, data in report["categories"].items():
            status = "✅" if data["score"] >= 80 else "⚠️" if data["score"] >= 50 else "❌"
            print(f"{status} {category.title()}: {data['score']}% ({data['passed']}/{data['total']})")

        print("-"*60)

        if report["recommendations"]:
            print("\nRECOMMENDATIONS:")
            for rec in report["recommendations"]:
                priority_icon = {
                    "critical": "🔴",
                    "high": "🟠",
                    "medium": "🟡",
                    "low": "🔵"
                }.get(rec["priority"], "⚪")
                print(f"{priority_icon} {rec['message']}")

        print("="*60)

def main():
    """Main entry point"""
    import argparse

    parser = argparse.ArgumentParser(description='Validate project structure')
    parser.add_argument('--project-root', type=str, help='Project root directory')
    parser.add_argument('--output', type=str, help='Output JSON file')
    args = parser.parse_args()

    validator = ProjectStructureValidator(args.project_root)
    report = validator.validate_structure()

    # Print summary
    validator.print_summary()

    # Exit with appropriate code
    success = report["overall_score"] >= 80 and len(report["categories"]["required"]["missing"]) == 0
    exit(0 if success else 1)

if __name__ == '__main__':
    main()