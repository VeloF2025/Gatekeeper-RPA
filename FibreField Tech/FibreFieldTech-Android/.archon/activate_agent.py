#!/usr/bin/env python3
"""
FibreField Tech Android - Agent Activation Script
Quick activation of specialized agents with validation
"""

import os
import sys
import subprocess
from pathlib import Path

def activate_agent(agent_name: str, task: str):
    """Activate a specialized agent with validation"""

    # First, run validation
    validator_cmd = [
        sys.executable,
        str(Path.cwd() / ".archon" / "agent_validation.py"),
        agent_name,
        task
    ]

    print(f"\n🔍 Running validation for {agent_name}...")
    print("-" * 50)

    try:
        result = subprocess.run(
            validator_cmd,
            capture_output=True,
            text=True,
            cwd=Path.cwd()
        )

        if result.returncode != 0:
            print("❌ Validation failed:")
            print(result.stdout)
            if result.stderr:
                print("Errors:")
                print(result.stderr)
            return False

        print("✅ Validation passed!")
        print(result.stdout)

    except Exception as e:
        print(f"❌ Validation error: {e}")
        return False

    # If validation passes, proceed with agent activation
    print(f"\n🚀 Activating {agent_name}...")
    print(f"📋 Task: {task}")
    print("-" * 50)

    # In a real implementation, this would activate the actual AI agent
    # For now, we'll show the agent configuration
    return True

def main():
    """Main function"""
    if len(sys.argv) < 3:
        print("Usage: python activate_agent.py <agent_command> <task>")
        print("\nAvailable agents:")
        print("  @android-compose-ai")
        print("  @ml-cv-architect")
        print("  @camera-workflow")
        print("  @offline-db-expert")
        print("  @high-tech-ui")
        print("  @android-perf")
        print("  @bio-security")
        print("  @android-test")
        sys.exit(1)

    agent_command = sys.argv[1]
    task = " ".join(sys.argv[2:])

    # Remove @ if present
    if agent_command.startswith("@"):
        agent_command = agent_command[1:]

    # Map command to agent name
    agent_mapping = {
        "android-compose-ai": "android-compose-ai-specialist",
        "ml-cv-architect": "ml-computer-vision-architect",
        "camera-workflow": "camera-workflow-engineer",
        "offline-db-expert": "offline-first-database-expert",
        "high-tech-ui": "material-you-high-tech-ui-designer",
        "android-perf": "android-performance-optimization-expert",
        "bio-security": "biometric-security-architect",
        "android-test": "android-testing-specialist"
    }

    agent_name = agent_mapping.get(agent_command)
    if not agent_name:
        print(f"❌ Unknown agent: {agent_command}")
        sys.exit(1)

    # Activate the agent
    success = activate_agent(agent_name, task)

    if success:
        print(f"\n✨ Agent {agent_command} is ready to work on your task!")
        print("\n💡 Tips:")
        print("   - Check .archon/README.md for agent-specific guidelines")
        print("   - Monitor memory usage during development")
        print("   - Test on multiple API levels")
        print("   - Validate AI model performance")

if __name__ == "__main__":
    main()