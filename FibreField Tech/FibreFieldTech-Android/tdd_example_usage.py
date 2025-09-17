#!/usr/bin/env python3
"""
Example usage of the TDD Parallel Execution System
Demonstrates how to set up and run the system for the FibreField Tech Android app
"""

import asyncio
import sys
from pathlib import Path
from tdd_parallel_execution_system import TDDParallelExecutionSystem, TaskType

async def run_example():
    """Run example TDD execution"""
    print("🚀 FibreField Tech Android App - TDD Parallel Execution System Example")
    print("=" * 80)

    # Initialize the system
    tdd_system = TDDParallelExecutionSystem("agent_config.yaml")

    # Example features to develop (from the 55 implementation tasks)
    example_features = [
        "db_room_implementation",
        "camera_system",
        "ai_model_integration",
        "ui_components",
        "authentication_system"
    ]

    print(f"\n📋 Features to develop:")
    for i, feature in enumerate(example_features, 1):
        print(f"   {i}. {feature}")

    print(f"\n🔍 System Status:")
    print(f"   - Agents Available: {len(tdd_system.agents)}")
    print(f"   - Quality Gates: {len(tdd_system.quality_gates)} configured")
    print(f"   - Worktrees Directory: {tdd_system.worktrees_dir}")
    print(f"   - Reports Directory: {tdd_system.reports_dir}")

    # Display agent information
    print(f"\n🤖 Available Agents:")
    for agent in tdd_system.agents.values():
        print(f"   - {agent.name}")
        print(f"     Type: {agent.type}")
        print(f"     Capabilities: {', '.join(agent.capabilities[:3])}...")
        print(f"     Max Concurrent Tasks: {agent.max_concurrent_tasks}")
        print()

    # Create sample test specifications for each feature
    print("📝 Creating Test Specifications...")
    test_specs_by_feature = {}

    for feature in example_features:
        test_specs = []

        # Unit Test
        unit_test = tdd_system.create_test_specification(
            feature_id=feature,
            title=f"Unit Tests for {feature}",
            description=f"Comprehensive unit tests for {feature} functionality",
            test_type=TaskType.UNIT_TEST,
            requirements=[
                f"{feature} core functionality",
                f"{feature} error handling",
                f"{feature} boundary conditions"
            ],
            acceptance_criteria=[
                "All functions return expected results",
                "Error cases handled gracefully",
                "Boundary conditions validated"
            ],
            edge_cases=[
                "Null inputs",
                "Empty inputs",
                "Invalid inputs",
                "Network failures",
                "Memory constraints"
            ],
            expected_behavior=f"{feature} should work correctly under all conditions"
        )
        test_specs.append(unit_test)

        # Integration Test
        integration_test = tdd_system.create_test_specification(
            feature_id=feature,
            title=f"Integration Tests for {feature}",
            description=f"Integration tests for {feature} with other components",
            test_type=TaskType.INTEGRATION_TEST,
            requirements=[
                f"{feature} integrates with database",
                f"{feature} handles API responses",
                f"{feature} manages state correctly"
            ],
            acceptance_criteria=[
                "Data flows correctly between components",
                "API failures handled gracefully",
                "State persists correctly"
            ],
            edge_cases=[
                "Database connection loss",
                "API timeouts",
                "Concurrent access",
                "Large datasets"
            ],
            expected_behavior=f"{feature} should integrate seamlessly with other components"
        )
        test_specs.append(integration_test)

        # UI Test (if applicable)
        if feature in ["ui_components", "camera_system"]:
            ui_test = tdd_system.create_test_specification(
                feature_id=feature,
                title=f"UI Tests for {feature}",
                description=f"UI interaction tests for {feature}",
                test_type=TaskType.UI_TEST,
                requirements=[
                    f"{feature} UI renders correctly",
                    f"{feature} responds to user input",
                    f"{feature} handles screen rotation"
                ],
                acceptance_criteria=[
                    "UI elements display correctly",
                    "User interactions work as expected",
                    "Screen orientation changes handled"
                ],
                edge_cases=[
                    "Different screen sizes",
                    "Low memory conditions",
                    "Fast user input",
                    "Accessibility mode"
                ],
                expected_behavior=f"{feature} UI should be responsive and accessible"
            )
            test_specs.append(ui_test)

        # Performance Test
        perf_test = tdd_system.create_test_specification(
            feature_id=feature,
            title=f"Performance Tests for {feature}",
            description=f"Performance and efficiency tests for {feature}",
            test_type=TaskType.PERFORMANCE_TEST,
            requirements=[
                f"{feature} executes efficiently",
                f"{feature} uses minimal memory",
                f"{feature} handles large datasets"
            ],
            acceptance_criteria=[
                "Execution time under threshold",
                "Memory usage within limits",
                "Battery impact minimal"
            ],
            edge_cases=[
                "Large datasets",
                "Repeated execution",
                "Background processing",
                "Low battery mode"
            ],
            expected_behavior=f"{feature} should perform efficiently under all conditions"
        )
        test_specs.append(perf_test)

        # Security Test (if applicable)
        if feature in ["db_room_implementation", "authentication_system"]:
            security_test = tdd_system.create_test_specification(
                feature_id=feature,
                title=f"Security Tests for {feature}",
                description=f"Security and vulnerability tests for {feature}",
                test_type=TaskType.SECURITY_TEST,
                requirements=[
                    f"{feature} data is encrypted",
                    f"{feature} validates inputs",
                    f"{feature} prevents unauthorized access"
                ],
                acceptance_criteria=[
                    "Sensitive data encrypted at rest",
                    "Input validation prevents injection",
                    "Access control enforced"
                ],
                edge_cases=[
                    "SQL injection attempts",
                    "Malformed inputs",
                    "Privilege escalation",
                    "Data interception"
                ],
                expected_behavior=f"{feature} should be secure against all known threats"
            )
            test_specs.append(security_test)

        test_specs_by_feature[feature] = test_specs

    print(f"\n✅ Created {len(tdd_system.test_specs)} test specifications")

    # Display test summary
    print("\n📊 Test Specification Summary:")
    for feature, specs in test_specs_by_feature.items():
        print(f"\n   {feature}:")
        for spec in specs:
            print(f"     - {spec.test_type.value}: {spec.title}")

    # Ask user if they want to proceed with execution
    print("\n" + "=" * 80)
    print("⚠️  WARNING: This will create git worktrees and modify the repository")
    print("Make sure you have committed any pending changes before proceeding!")
    print("=" * 80)

    response = input("\nDo you want to proceed with TDD execution? (y/N): ")
    if response.lower() != 'y':
        print("❌ TDD execution cancelled")
        return

    # Run TDD execution
    print("\n🚀 Starting TDD Parallel Execution...")
    try:
        await tdd_system.run_tdd_system(example_features)
        print("\n✅ TDD execution completed successfully!")
    except Exception as e:
        print(f"\n❌ TDD execution failed: {e}")
        # Ensure cleanup even on failure
        tdd_system.cleanup_worktrees()

    # Display final report
    print("\n📋 Final Report:")
    report = tdd_system.generate_tdd_report()
    print(report)

    # Save report
    report_file = tdd_system.reports_dir / f"example_tdd_report_{asyncio.get_event_loop().time()}.md"
    with open(report_file, 'w') as f:
        f.write(report)
    print(f"\n💾 Report saved to: {report_file}")

def demonstrate_customization():
    """Demonstrate system customization options"""
    print("\n🔧 Customization Examples")
    print("=" * 50)

    print("\n1. Custom Quality Gates:")
    print("""
    tdd_system.quality_gates = {
        "test_coverage_minimum": 98,  # Higher requirement
        "performance_targets": {
            "test_execution_time_ms": 3000,  # Stricter
            "ui_thread_time_ms": 16,
            "memory_usage_mb": 400  # Lower limit
        },
        "zero_tolerance_rules": [
            "compilation_errors",
            "console_logging",
            "undefined_errors",
            "security_vulnerabilities",
            "code_duplication"  # Custom rule
        ]
    }
    """)

    print("\n2. Custom Agent Configuration:")
    print("""
    # In agent_config.yaml
    agents:
      - name: custom_ml_agent
        type: ml_specialist
        capabilities:
          - tensorflow_lite
          - on_device_inference
          - model_optimization
          - custom_algorithm
        max_concurrent_tasks: 1
        quality_standards:
          - inference_under_50ms
          - model_size_under_10mb
          - battery_optimized
    """)

    print("\n3. Custom Test Types:")
    print("""
    class TaskType(Enum):
        UNIT_TEST = "unit_test"
        INTEGRATION_TEST = "integration_test"
        UI_TEST = "ui_test"
        PERFORMANCE_TEST = "performance_test"
        SECURITY_TEST = "security_test"
        ACCESSIBILITY_TEST = "accessibility_test"
        CUSTOM_TEST = "custom_test"  # Add custom type
    """)

def show_advanced_usage():
    """Show advanced usage patterns"""
    print("\n🚀 Advanced Usage Patterns")
    print("=" * 50)

    print("\n1. Batch Processing:")
    print("""
    # Process features in batches
    batch_size = 3
    all_features = [...]  # Your list of features

    for i in range(0, len(all_features), batch_size):
        batch = all_features[i:i + batch_size]
        await tdd_system.run_tdd_system(batch)
        # Analyze results before next batch
    """)

    print("\n2. Custom Test Generation:")
    print("""
    async def generate_custom_test_content(test_spec: TestSpecification) -> str:
        # Use AI model to generate tests
        ai_response = await ai_model.generate(
            prompt=f"Create {test_spec.test_type} test for: {test_spec.description}",
            context=test_spec.acceptance_criteria
        )
        return ai_response.content

    # Override method
    tdd_system.generate_test_content = generate_custom_test_content
    """)

    print("\n3. Integration with CI/CD:")
    print("""
    # In CI/CD pipeline
    steps:
      - name: Run TDD System
        run: |
          python tdd_parallel_execution_system.py \\
            --features ${{ env.FEATURES }} \\
            --config agent_config.yaml

      - name: Check Quality Gates
        run: |
          python check_quality_gates.py \\
            --report reports/latest.md \\
            --threshold 95
    """)

def main():
    """Main function"""
    if len(sys.argv) > 1 and sys.argv[1] == "--help":
        print("Usage:")
        print("  python tdd_example_usage.py              # Run example")
        print("  python tdd_example_usage.py --help       # Show this help")
        print("  python tdd_example_usage.py --custom     # Show customization")
        print("  python tdd_example_usage.py --advanced   # Show advanced usage")
        return

    if len(sys.argv) > 1 and sys.argv[1] == "--custom":
        demonstrate_customization()
        return

    if len(sys.argv) > 1 and sys.argv[1] == "--advanced":
        show_advanced_usage()
        return

    # Run example
    asyncio.run(run_example())

if __name__ == "__main__":
    main()