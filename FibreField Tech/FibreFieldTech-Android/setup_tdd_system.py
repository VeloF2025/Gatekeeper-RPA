#!/usr/bin/env python3
"""
TDD System Setup Script
Sets up the TDD Parallel Execution System for the FibreField Tech Android app
"""

import os
import sys
import subprocess
import shutil
from pathlib import Path
import json
import argparse

def check_prerequisites():
    """Check if all prerequisites are installed"""
    print("🔍 Checking prerequisites...")

    issues = []

    # Check Python version
    if sys.version_info < (3, 8):
        issues.append("Python 3.8 or higher is required")

    # Check Git
    try:
        result = subprocess.run(["git", "--version"], capture_output=True, text=True)
        if result.returncode != 0:
            issues.append("Git is not installed or not in PATH")
    except FileNotFoundError:
        issues.append("Git is not installed")

    # Check Android SDK (basic check)
    android_home = os.environ.get('ANDROID_HOME')
    if not android_home:
        issues.append("ANDROID_HOME environment variable not set")

    # Check if we're in an Android project
    if not (Path.cwd() / "app" / "build.gradle").exists():
        issues.append("Not in an Android project directory")

    return issues

def install_python_dependencies():
    """Install required Python packages"""
    print("📦 Installing Python dependencies...")

    dependencies = [
        "pyyaml>=6.0",
        "tabulate>=0.9.0",
        "pandas>=1.5.0",
        "matplotlib>=3.5.0"
    ]

    for dep in dependencies:
        try:
            subprocess.run([sys.executable, "-m", "pip", "install", dep], check=True)
            print(f"✅ Installed {dep}")
        except subprocess.CalledProcessError as e:
            print(f"❌ Failed to install {dep}: {e}")
            return False

    return True

def setup_directory_structure():
    """Create necessary directories"""
    print("📁 Setting up directory structure...")

    directories = [
        "git_worktrees",
        "tdd_reports",
        "test_templates",
        "logs"
    ]

    for dir_name in directories:
        dir_path = Path(dir_name)
        dir_path.mkdir(exist_ok=True)
        print(f"✅ Created directory: {dir_name}")

def create_test_templates():
    """Create test template files"""
    print("📝 Creating test templates...")

    templates_dir = Path("test_templates")

    # Unit test template
    unit_test_template = """package com.fibreflow.tech

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After

/**
 * Unit Test Template
 * Replace this with your actual test class
 */
class FeatureUnitTest {

    @Before
    fun setUp() {
        // Setup code
    }

    @After
    fun tearDown() {
        // Cleanup code
    }

    @Test
    fun testFeatureFunction() {
        // Given
        val input = "test"

        // When
        val result = featureFunction(input)

        // Then
        assertEquals("Expected result", result)
    }
}
"""

    with open(templates_dir / "unit_test.kt", "w") as f:
        f.write(unit_test_template)

    # UI test template
    ui_test_template = """package com.fibreflow.tech

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Test Template
 */
@RunWith(AndroidJUnit4::class)
class FeatureUiTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testButtonClick() {
        // Test button click
        onView(withId(R.id.button))
            .perform(click())

        onView(withId(R.id.result))
            .check(matches(withText("Expected Result")))
    }
}
"""

    with open(templates_dir / "ui_test.kt", "w") as f:
        f.write(ui_test_template)

    print("✅ Created test templates")

def setup_gradle_config():
    """Setup Gradle configuration for testing"""
    print("⚙️  Setting up Gradle configuration...")

    # Check if JaCoCo is already configured
    build_file = Path("app/build.gradle")

    if build_file.exists():
        content = build_file.read_text()

        # Add JaCoCo if not present
        if "jacoco" not in content.lower():
            print("Adding JaCoCo configuration...")
            jacoco_config = """

// Test coverage configuration
apply plugin: 'jacoco'

jacoco {
    toolVersion = "0.8.7"
}

tasks.withType(Test) {
    jacoco.includeNoLocationClasses = true
    jacoco.excludes = ['jdk.internal.*']
}

task jacocoTestReport(type: JacocoReport, dependsOn: ['testDebugUnitTest']) {
    reports {
        xml.enabled = true
        html.enabled = true
    }

    def fileFilter = [
            '**/R.class',
            '**/R$*.class',
            '**/BuildConfig.*',
            '**/Manifest*.*',
            '**/*Test*.*',
            'android/**/*.*'
    ]
    def debugTree = fileTree(dir: "${buildDir}/tmp/kotlin-classes/debug", excludes: fileFilter)
    def mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files([mainSrc]))
    classDirectories.setFrom(files([debugTree]))
    executionData.setFrom(fileTree(dir: buildDir, includes: [
            'jacoco/testDebugUnitTest.exec',
            'outputs/code_coverage/debugAndroidTest/connected/*.ec'
    ]))
}
"""
            with open(build_file, "a") as f:
                f.write(jacoco_config)

        print("✅ Updated Gradle configuration")

def create_quality_config():
    """Create quality configuration files"""
    print("🔍 Creating quality configuration...")

    # Detekt configuration
    detekt_config = {
        "buildscript": {
            "repositories": {
                "mavenCentral()": None
            },
            "dependencies": {
                "classpath": "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.22.0"
            }
        },
        "plugins": {
            "io.gitlab.arturbosch.detekt": {},
            "jacoco": {}
        },
        "detekt": {
            "toolVersion": "1.22.0",
            "config": "detekt-config.yml",
            "buildUponDefaultConfig": True
        }
    }

    # Save detekt config (simplified)
    with open("detekt.yml", "w") as f:
        f.write("""
# Detekt configuration
build:
  maxIssues: 10
  excludeCorrectable: false

style:
  MagicNumber:
    active: false

complexity:
  LongMethod:
    threshold: 30
  LongParameterList:
    threshold: 8

comments:
  UndocumentedPublicClass:
    active: false

empty-blocks:
  EmptyFunctionBlock:
    active: false
""")

    print("✅ Created quality configuration files")

def create_pre_commit_hook():
    """Create pre-commit git hook for quality checks"""
    print("🪝 Creating pre-commit hook...")

    hooks_dir = Path(".git") / "hooks"
    hooks_dir.mkdir(exist_ok=True)

    pre_commit_script = """#!/bin/bash
# Pre-commit hook for TDD quality checks

echo "Running pre-commit quality checks..."

# Check for console logs
if grep -r "Log\\." "app/src/main/java/" > /dev/null; then
    echo "❌ Console logging detected. Please use proper logging framework."
    exit 1
fi

# Check for TODO comments (optional)
if grep -r "TODO" "app/src/main/java/" | wc -l | grep -v "^0$" > /dev/null; then
    echo "⚠️  TODO comments found. Please review before committing."
    # exit 1  # Uncomment to block commits with TODOs
fi

# Run unit tests
echo "Running unit tests..."
./gradlew test
if [ $? -ne 0 ]; then
    echo "❌ Unit tests failed. Please fix before committing."
    exit 1
fi

echo "✅ All quality checks passed!"
"""

    pre_commit_file = hooks_dir / "pre-commit"
    with open(pre_commit_file, "w") as f:
        f.write(pre_commit_script)

    # Make executable
    pre_commit_file.chmod(0o755)

    print("✅ Created pre-commit hook")

def setup_database():
    """Initialize the TDD system database"""
    print("💾 Setting up database...")

    try:
        # Import and run the TDD system to initialize DB
        from tdd_parallel_execution_system import TDDParallelExecutionSystem

        # Create system instance to initialize DB
        tdd_system = TDDParallelExecutionSystem()
        print("✅ Database initialized successfully")

    except Exception as e:
        print(f"❌ Failed to initialize database: {e}")
        return False

    return True

def create_run_script():
    """Create convenient run scripts"""
    print("📜 Creating run scripts...")

    # Main run script
    run_script = """#!/bin/bash
# TDD System Runner

echo "🚀 Starting TDD Parallel Execution System..."

# Check if features are provided
if [ $# -eq 0 ]; then
    echo "Usage: ./run_tdd.sh <feature1> [feature2] [feature3] ..."
    echo "Example: ./run_tdd.sh camera_system ai_model_integration"
    exit 1
fi

# Convert arguments to comma-separated list
FEATURES=$(IFS=,; echo "$*")

# Run the TDD system
python tdd_parallel_execution_system.py --features $FEATURES
"""

    with open("run_tdd.sh", "w") as f:
        f.write(run_script)

    # Make executable
    Path("run_tdd.sh").chmod(0o755)

    # Monitor script
    monitor_script = """#!/bin/bash
# TDD System Monitor

echo "📊 Starting TDD System Monitor..."

# Run monitor with 10 second refresh interval
python tdd_monitor.py --monitor 10
"""

    with open("monitor_tdd.sh", "w") as f:
        f.write(monitor_script)

    # Make executable
    Path("monitor_tdd.sh").chmod(0o755)

    print("✅ Created run scripts")

def verify_setup():
    """Verify that the setup was successful"""
    print("\n🔍 Verifying setup...")

    success = True

    # Check directories
    required_dirs = ["git_worktrees", "tdd_reports", "test_templates", "logs"]
    for dir_name in required_dirs:
        if not Path(dir_name).exists():
            print(f"❌ Directory missing: {dir_name}")
            success = False

    # Check files
    required_files = [
        "tdd_parallel_execution_system.py",
        "tdd_monitor.py",
        "agent_config.yaml",
        "test_templates/unit_test.kt",
        "test_templates/ui_test.kt"
    ]

    for file_name in required_files:
        if not Path(file_name).exists():
            print(f"❌ File missing: {file_name}")
            success = False

    # Check if we can import the system
    try:
        from tdd_parallel_execution_system import TDDParallelExecutionSystem
        print("✅ TDD system can be imported")
    except ImportError as e:
        print(f"❌ Cannot import TDD system: {e}")
        success = False

    return success

def main():
    parser = argparse.ArgumentParser(description='Setup TDD System')
    parser.add_argument('--skip-deps', action='store_true', help='Skip installing dependencies')
    parser.add_argument('--verify-only', action='store_true', help='Only verify existing setup')

    args = parser.parse_args()

    print("FibreField Tech Android App - TDD System Setup")
    print("=" * 60)

    if args.verify_only:
        success = verify_setup()
        if success:
            print("\n✅ Setup verification successful!")
        else:
            print("\n❌ Setup verification failed!")
            sys.exit(1)
        return

    # Check prerequisites
    print("\n1. Checking prerequisites...")
    issues = check_prerequisites()
    if issues:
        print("\n❌ Prerequisites not met:")
        for issue in issues:
            print(f"   - {issue}")
        print("\nPlease fix these issues before continuing.")
        sys.exit(1)
    print("✅ All prerequisites met")

    # Install dependencies
    if not args.skip_deps:
        print("\n2. Installing Python dependencies...")
        if not install_python_dependencies():
            print("❌ Failed to install dependencies")
            sys.exit(1)
        print("✅ Dependencies installed")

    # Setup directory structure
    print("\n3. Setting up directory structure...")
    setup_directory_structure()

    # Create test templates
    print("\n4. Creating test templates...")
    create_test_templates()

    # Setup Gradle configuration
    print("\n5. Setting up Gradle configuration...")
    setup_gradle_config()

    # Create quality configuration
    print("\n6. Creating quality configuration...")
    create_quality_config()

    # Create pre-commit hook
    print("\n7. Creating pre-commit hook...")
    create_pre_commit_hook()

    # Setup database
    print("\n8. Setting up database...")
    if not setup_database():
        print("❌ Failed to setup database")
        sys.exit(1)

    # Create run scripts
    print("\n9. Creating run scripts...")
    create_run_script()

    # Verify setup
    print("\n10. Verifying setup...")
    if verify_setup():
        print("\n🎉 Setup completed successfully!")
        print("\nNext steps:")
        print("1. Run './run_tdd.sh <feature_name>' to start TDD development")
        print("2. Run './monitor_tdd.sh' to monitor the system")
        print("3. Check the documentation for detailed usage")
    else:
        print("\n❌ Setup verification failed!")
        sys.exit(1)

if __name__ == "__main__":
    main()