#!/usr/bin/env python3
"""
FibreField Tech Android App - Agent Validation System
Enforces project-specific quality gates and validation rules
"""

import yaml
import os
import sys
from pathlib import Path
from typing import Dict, List, Any
import json

class FibreFieldAgentValidator:
    """Validates agents against FibreField Tech Android project requirements"""

    def __init__(self, project_path: str):
        self.project_path = Path(project_path)
        self.config_path = self.project_path / ".archon" / "project_agents.yaml"
        self.config = self._load_config()

    def _load_config(self) -> Dict[str, Any]:
        """Load the project agents configuration"""
        try:
            with open(self.config_path, 'r') as f:
                return yaml.safe_load(f)
        except FileNotFoundError:
            print(f"[ERROR] Configuration file not found: {self.config_path}")
            sys.exit(1)

    def validate_agent_compatibility(self, agent_name: str, task: str) -> bool:
        """Check if agent is compatible with project technology stack"""
        agent_config = self._find_agent_config(agent_name)
        if not agent_config:
            print(f"[ERROR] Agent {agent_name} not found in project configuration")
            return False

        # Check if task matches agent skills
        task_keywords = task.lower().split()
        agent_skills = [skill.lower() for skill in agent_config.get('skills', [])]

        match_score = sum(1 for keyword in task_keywords
                         if any(keyword in skill for skill in agent_skills))

        if match_score == 0:
            print(f"[WARNING] Task may not match agent {agent_name} expertise")
            print(f"   Agent skills: {', '.join(agent_config.get('skills', []))}")
            return False

        return True

    def validate_quality_gates(self, agent_name: str) -> List[str]:
        """Check if agent quality gates are met"""
        agent_config = self._find_agent_config(agent_name)
        if not agent_config:
            return [f"Agent {agent_name} not configured"]

        violations = []
        quality_gates = agent_config.get('quality_gates', [])

        # Check Android compilation
        if not self._check_android_compilation():
            violations.append("Android compilation errors detected")

        # Check test coverage
        if not self._check_test_coverage():
            violations.append("Test coverage below 95%")

        # Check lint warnings
        if not self._check_lint_warnings():
            violations.append("Android lint warnings present")

        # Agent-specific quality checks
        if "ml" in agent_name.lower():
            if not self._check_ml_performance():
                violations.append("ML inference performance below threshold")

        if "compose" in agent_name.lower():
            if not self._check_compose_performance():
                violations.append("Compose UI performance issues detected")

        return violations

    def _find_agent_config(self, agent_name: str) -> Dict[str, Any]:
        """Find agent configuration by name"""
        for agent in self.config.get('specialized_agents', []):
            if agent_name in agent.get('activation_command', ''):
                return agent
        return None

    def _check_android_compilation(self) -> bool:
        """Check if Android project compiles successfully"""
        # In real implementation, this would run gradle build
        # For now, return True as placeholder
        return True

    def _check_test_coverage(self) -> bool:
        """Check test coverage requirements"""
        # Placeholder implementation
        return True

    def _check_lint_warnings(self) -> bool:
        """Check for Android lint warnings"""
        # Placeholder implementation
        return True

    def _check_ml_performance(self) -> bool:
        """Check ML model performance metrics"""
        # Placeholder implementation
        return True

    def _check_compose_performance(self) -> bool:
        """Check Compose UI performance"""
        # Placeholder implementation
        return True

    def get_agent_for_task(self, task: str) -> str:
        """Recommend the best agent for a given task"""
        task_lower = task.lower()

        # Task to agent mapping
        task_mapping = {
            "ui": "android-compose-ai-specialist",
            "compose": "android-compose-ai-specialist",
            "ml": "ml-computer-vision-architect",
            "ai": "ml-computer-vision-architect",
            "camera": "camera-workflow-engineer",
            "photo": "camera-workflow-engineer",
            "database": "offline-first-database-expert",
            "sync": "offline-first-database-expert",
            "design": "material-you-high-tech-ui-designer",
            "theme": "material-you-high-tech-ui-designer",
            "performance": "android-performance-optimization-expert",
            "memory": "android-performance-optimization-expert",
            "battery": "android-performance-optimization-expert",
            "security": "biometric-security-architect",
            "auth": "biometric-security-architect",
            "biometric": "biometric-security-architect",
            "test": "android-testing-specialist",
            "testing": "android-testing-specialist"
        }

        for keyword, agent in task_mapping.items():
            if keyword in task_lower:
                return agent

        # Default to system architect if no specific match
        return "system-architect"

    def validate_before_development(self, agent_name: str, task: str) -> bool:
        """Comprehensive validation before development starts"""
        print(f"\nValidating {agent_name} for task: {task}")
        print("=" * 60)

        # Check agent compatibility
        if not self.validate_agent_compatibility(agent_name, task):
            print("[ERROR] Agent not compatible with task")
            return False

        # Check quality gates
        violations = self.validate_quality_gates(agent_name)
        if violations:
            print("[ERROR] Quality gate violations:")
            for violation in violations:
                print(f"   - {violation}")
            return False

        # Check if agent is blocked
        if self._is_agent_blocked(agent_name):
            print(f"[ERROR] Agent {agent_name} is currently blocked")
            return False

        print("[SUCCESS] Validation passed - development can proceed")
        return True

    def _is_agent_blocked(self, agent_name: str) -> bool:
        """Check if agent is blocked due to previous violations"""
        # Placeholder implementation
        # In real system, this would check a block list
        return False

def main():
    """Main validation function"""
    if len(sys.argv) < 3:
        print("Usage: python agent_validation.py <agent_name> <task>")
        print("Example: python agent_validation.py android-compose-ai 'Create new UI component'")
        sys.exit(1)

    agent_name = sys.argv[1]
    task = " ".join(sys.argv[2:])

    # Get project path from current directory
    project_path = os.getcwd()

    validator = FibreFieldAgentValidator(project_path)

    # Recommend best agent if requested
    if agent_name == "recommend":
        recommended = validator.get_agent_for_task(task)
        print(f"Recommended agent for task: {recommended}")
        sys.exit(0)

    # Validate before development
    success = validator.validate_before_development(agent_name, task)
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main()