#!/usr/bin/env python3
"""
FibreField Tech Android - Agent Status Utility
Shows available agents and their status
"""

import yaml
import os
from pathlib import Path
from datetime import datetime

def load_config():
    """Load the project agents configuration"""
    config_path = Path.cwd() / ".archon" / "project_agents.yaml"
    with open(config_path, 'r') as f:
        return yaml.safe_load(f)

def print_agent_status():
    """Print status of all available agents"""
    config = load_config()

    print("\n" + "="*80)
    print("FIBREFIELD TECH ANDROID - AGENT STATUS")
    print("="*80)
    print(f"Project Complexity: {config.get('complexity_score', 'N/A')}/10")
    print(f"Configuration Generated: {config.get('project', {}).get('generated', 'N/A')}")
    print("\n" + "-"*80)

    print("\nAVAILABLE SPECIALIZED AGENTS:")
    print("-"*80)

    for agent in config.get('specialized_agents', []):
        name = agent.get('name', 'Unknown')
        command = agent.get('activation_command', 'No command')
        description = agent.get('description', 'No description')
        skills = agent.get('skills', [])

        print(f"\n* {name}")
        print(f"   Command: {command}")
        print(f"   Description: {description}")
        print(f"   Skills: {', '.join(skills[:3])}{'...' if len(skills) > 3 else ''}")

        # Quality gates
        quality_gates = agent.get('quality_gates', [])
        if quality_gates:
            print(f"   Quality Gates: {len(quality_gates)} rules")

    print("\n" + "-"*80)
    print("\nGLOBAL QUALITY RULES:")
    print("-"*80)

    for rule in config.get('global_quality_rules', [])[:5]:
        print(f"   - {rule}")

    if len(config.get('global_quality_rules', [])) > 5:
        print(f"   ... and {len(config.get('global_quality_rules', [])) - 5} more rules")

    print("\n" + "-"*80)
    print("\nANDROID-SPECIFIC RULES:")
    print("-"*80)

    for rule in config.get('android_specific_rules', [])[:5]:
        print(f"   - {rule}")

    print("\n" + "="*80)
    print("\nUSAGE EXAMPLES:")
    print("-"*80)
    print("   @Archon <task>                    # Activate with auto-selected agent")
    print("   @android-compose-ai <task>         # Activate Compose specialist")
    print("   @ml-cv-architect <task>           # Activate ML specialist")
    print("   @camera-workflow <task>           # Activate camera specialist")
    print("   @high-tech-ui <task>              # Activate UI specialist")
    print("\n   @Archon status                    # Show this status")
    print("   @Archon agents                     # List all agents")
    print("   @Archon review                     # Code review")

    print("\n" + "="*80)
    print("VALIDATION:")
    print("-" * 80)
    print("   Run validation before development:")
    print("   python .archon/agent_validation.py <agent> <task>")
    print("\n   Example:")
    print("   python .archon/agent_validation.py android-compose-ai 'Create ONT UI'")

    print("\n" + "="*80)

def print_agents_list():
    """Print just the list of agents"""
    config = load_config()

    print("\nAVAILABLE AGENTS:")
    print("-"*50)

    for agent in config.get('specialized_agents', []):
        name = agent.get('name', 'Unknown')
        command = agent.get('activation_command', 'No command')
        print(f"   {command:<30} # {name}")

if __name__ == "__main__":
    import sys

    if len(sys.argv) > 1 and sys.argv[1] == "--list":
        print_agents_list()
    else:
        print_agent_status()