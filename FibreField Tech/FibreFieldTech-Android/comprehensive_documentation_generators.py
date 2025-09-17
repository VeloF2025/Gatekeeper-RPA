#!/usr/bin/env python3
"""
FibreField Tech Android App - Comprehensive Documentation Generators
Advanced documentation generators for all required documentation types with automation.
"""

import asyncio
import json
import logging
import os
import sys
import re
import yaml
import sqlite3
import markdown
import subprocess
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional, Tuple, Union
from dataclasses import dataclass, field, asdict
from enum import Enum
from pathlib import Path
import ast
import requests
from bs4 import BeautifulSoup
import graphviz
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np
from jinja2 import Template, Environment, FileSystemLoader, select_autoescape
import plantuml
import xml.etree.ElementTree as ET

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('documentation_generators.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class DocumentationFormat(Enum):
    MARKDOWN = "markdown"
    HTML = "html"
    PDF = "pdf"
    LATEX = "latex"
    JSON = "json"
    XML = "xml"

class DocumentationCategory(Enum):
    TECHNICAL = "technical"
    USER = "user"
    DEVELOPER = "developer"
    QUALITY = "quality"
    DEPLOYMENT = "deployment"
    API = "api"
    ARCHITECTURE = "architecture"
    TESTING = "testing"
    SECURITY = "security"
    PERFORMANCE = "performance"

class DocumentationLevel(Enum):
    OVERVIEW = "overview"
    DETAILED = "detailed"
    REFERENCE = "reference"
    TUTORIAL = "tutorial"

@dataclass
class DocumentationRequest:
    id: str
    category: DocumentationCategory
    format: DocumentationFormat
    level: DocumentationLevel
    title: str
    description: str
    source_data: Dict[str, Any]
    output_path: str
    template_path: str
    metadata: Dict[str, Any] = field(default_factory=dict)
    created_at: datetime = field(default_factory=datetime.now)

@dataclass
class DocumentationResult:
    request_id: str
    success: bool
    output_path: str
    generated_at: datetime
    file_size: int
    validation_errors: List[str] = field(default_factory=list)
    warnings: List[str] = field(default_factory=list)
    processing_time: float = 0.0

@dataclass
class CodeExample:
    id: str
    title: str
    description: str
    language: str
    code: str
    file_path: str
    line_start: int
    line_end: int
    dependencies: List[str] = field(default_factory=list)
    related_tests: List[str] = field(default_factory=list)

@dataclass
class DiagramSpec:
    id: str
    type: str
    title: str
    description: str
    source: str
    output_format: str
    output_path: str
    metadata: Dict[str, Any] = field(default_factory=dict)

class AdvancedDocumentationGenerator:
    """Advanced documentation generator with multiple output formats"""

    def __init__(self, config_path: str = None):
        self.config = self.load_config(config_path)
        self.env = self.setup_template_environment()
        self.database_path = "documentation_generators.db"
        self.code_examples: Dict[str, CodeExample] = {}
        self.diagrams: Dict[str, DiagramSpec] = {}
        self.results_cache: Dict[str, DocumentationResult] = {}

        # Initialize system
        self.initialize_database()
        self.setup_directories()
        self.load_existing_data()

        logger.info("Advanced Documentation Generator initialized")

    def load_config(self, config_path: str) -> Dict[str, Any]:
        """Load configuration from file"""
        if config_path and os.path.exists(config_path):
            with open(config_path, 'r') as f:
                return yaml.safe_load(f)
        else:
            return {
                "output_directory": "docs/generated",
                "template_directory": "templates",
                "asset_directory": "docs/assets",
                "supported_formats": ["markdown", "html", "pdf"],
                "code_extraction": {
                    "enabled": True,
                    "languages": ["kotlin", "java", "xml"],
                    "include_tests": True,
                    "max_examples_per_file": 10
                },
                "diagram_generation": {
                    "enabled": True,
                    "types": ["architecture", "sequence", "class", "activity"],
                    "engine": "graphviz"
                },
                "validation": {
                    "enabled": True,
                    "check_links": True,
                    "check_code_examples": True,
                    "check_structure": True
                },
                "automation": {
                    "enabled": True,
                    "watch_mode": False,
                    "auto_generate": True,
                    "trigger_events": ["phase_completion", "test_passed", "quality_gate_passed"]
                }
            }

    def setup_template_environment(self):
        """Setup Jinja2 template environment"""
        template_dir = self.config.get("template_directory", "templates")
        os.makedirs(template_dir, exist_ok=True)

        self.env = Environment(
            loader=FileSystemLoader(template_dir),
            autoescape=select_autoescape(['html', 'xml'])
        )

        # Add custom filters
        self.env.filters['code_highlight'] = self.highlight_code
        self.env.filters['format_date'] = self.format_date
        self.env.filters['truncate'] = self.truncate_text

        # Create default templates
        self.create_default_templates()

    def create_default_templates(self):
        """Create default documentation templates"""
        templates = {
            "technical_overview.md.j2": self.get_technical_overview_template(),
            "api_reference.md.j2": self.get_api_reference_template(),
            "user_guide.md.j2": self.get_user_guide_template(),
            "developer_guide.md.j2": self.get_developer_guide_template(),
            "quality_report.md.j2": self.get_quality_report_template(),
            "deployment_guide.md.j2": self.get_deployment_guide_template(),
            "architecture_documentation.md.j2": self.get_architecture_template(),
            "testing_documentation.md.j2": self.get_testing_template(),
            "security_documentation.md.j2": self.get_security_template(),
            "performance_documentation.md.j2": self.get_performance_template()
        }

        for template_name, template_content in templates.items():
            template_path = os.path.join(self.config["template_directory"], template_name)
            if not os.path.exists(template_path):
                with open(template_path, 'w', encoding='utf-8') as f:
                    f.write(template_content)

    def initialize_database(self):
        """Initialize SQLite database for tracking"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        # Create documentation requests table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS documentation_requests (
                id TEXT PRIMARY KEY,
                category TEXT NOT NULL,
                format TEXT NOT NULL,
                level TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                source_data TEXT,
                output_path TEXT,
                template_path TEXT,
                metadata TEXT,
                created_at TEXT,
                processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')

        # Create documentation results table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS documentation_results (
                request_id TEXT PRIMARY KEY,
                success BOOLEAN,
                output_path TEXT,
                generated_at TEXT,
                file_size INTEGER,
                validation_errors TEXT,
                warnings TEXT,
                processing_time REAL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (request_id) REFERENCES documentation_requests (id)
            )
        ''')

        # Create code examples table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS code_examples (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT,
                language TEXT NOT NULL,
                code TEXT NOT NULL,
                file_path TEXT,
                line_start INTEGER,
                line_end INTEGER,
                dependencies TEXT,
                related_tests TEXT,
                extracted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')

        # Create diagrams table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS diagrams (
                id TEXT PRIMARY KEY,
                type TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT,
                source TEXT NOT NULL,
                output_format TEXT NOT NULL,
                output_path TEXT,
                metadata TEXT,
                generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')

        conn.commit()
        conn.close()
        logger.info("Database initialized successfully")

    def setup_directories(self):
        """Setup required directories"""
        directories = [
            self.config["output_directory"],
            os.path.join(self.config["output_directory"], "technical"),
            os.path.join(self.config["output_directory"], "user"),
            os.path.join(self.config["output_directory"], "developer"),
            os.path.join(self.config["output_directory"], "quality"),
            os.path.join(self.config["output_directory"], "deployment"),
            os.path.join(self.config["output_directory"], "api"),
            os.path.join(self.config["output_directory"], "architecture"),
            os.path.join(self.config["output_directory"], "testing"),
            os.path.join(self.config["output_directory"], "security"),
            os.path.join(self.config["output_directory"], "performance"),
            self.config["asset_directory"],
            os.path.join(self.config["asset_directory"], "images"),
            os.path.join(self.config["asset_directory"], "diagrams"),
            os.path.join(self.config["asset_directory"], "code")
        ]

        for directory in directories:
            os.makedirs(directory, exist_ok=True)

    def load_existing_data(self):
        """Load existing data from database"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        # Load code examples
        cursor.execute("SELECT * FROM code_examples")
        for row in cursor.fetchall():
            example_data = {
                "id": row[0],
                "title": row[1],
                "description": row[2],
                "language": row[3],
                "code": row[4],
                "file_path": row[5],
                "line_start": row[6],
                "line_end": row[7],
                "dependencies": json.loads(row[8]) if row[8] else [],
                "related_tests": json.loads(row[9]) if row[9] else []
            }
            example = CodeExample(**example_data)
            self.code_examples[example.id] = example

        # Load diagrams
        cursor.execute("SELECT * FROM diagrams")
        for row in cursor.fetchall():
            diagram_data = {
                "id": row[0],
                "type": row[1],
                "title": row[2],
                "description": row[3],
                "source": row[4],
                "output_format": row[5],
                "output_path": row[6],
                "metadata": json.loads(row[7]) if row[7] else {}
            }
            diagram = DiagramSpec(**diagram_data)
            self.diagrams[diagram.id] = diagram

        conn.close()
        logger.info(f"Loaded {len(self.code_examples)} code examples and {len(self.diagrams)} diagrams")

    def generate_documentation(self, request: DocumentationRequest) -> DocumentationResult:
        """Generate documentation based on request"""
        start_time = datetime.now()

        try:
            # Extract code examples if enabled
            if self.config["code_extraction"]["enabled"]:
                self.extract_code_examples(request.source_data)

            # Generate diagrams if enabled
            if self.config["diagram_generation"]["enabled"]:
                self.generate_diagrams(request.source_data)

            # Get the appropriate template
            template = self.env.get_template(request.template_path)

            # Prepare template context
            context = self.prepare_template_context(request)

            # Generate documentation
            content = template.render(**context)

            # Convert to requested format
            if request.format == DocumentationFormat.MARKDOWN:
                final_content = content
            elif request.format == DocumentationFormat.HTML:
                final_content = self.convert_to_html(content)
            elif request.format == DocumentationFormat.PDF:
                final_content = self.convert_to_pdf(content)
            else:
                final_content = content

            # Save documentation
            self.save_documentation(request, final_content)

            # Validate documentation
            validation_errors = self.validate_documentation(request, final_content)

            # Create result
            result = DocumentationResult(
                request_id=request.id,
                success=len(validation_errors) == 0,
                output_path=request.output_path,
                generated_at=datetime.now(),
                file_size=len(final_content.encode('utf-8')),
                validation_errors=validation_errors,
                warnings=[],
                processing_time=(datetime.now() - start_time).total_seconds()
            )

            # Save result to database
            self.save_result_to_db(result)

            logger.info(f"Documentation generated successfully: {request.title}")
            return result

        except Exception as e:
            logger.error(f"Error generating documentation: {e}")
            return DocumentationResult(
                request_id=request.id,
                success=False,
                output_path="",
                generated_at=datetime.now(),
                file_size=0,
                validation_errors=[str(e)],
                warnings=[],
                processing_time=(datetime.now() - start_time).total_seconds()
            )

    def prepare_template_context(self, request: DocumentationRequest) -> Dict[str, Any]:
        """Prepare template context with all necessary data"""
        context = {
            "request": request,
            "generated_at": datetime.now().isoformat(),
            "version": self.config.get("version", "1.0.0"),
            "project_name": self.config.get("project_name", "FibreField Tech Android App"),
            "code_examples": self.get_relevant_code_examples(request),
            "diagrams": self.get_relevant_diagrams(request),
            "metrics": self.get_documentation_metrics(request),
            "navigation": self.generate_navigation(request),
            "toc": self.generate_table_of_contents(request)
        }

        # Add category-specific context
        if request.category == DocumentationCategory.TECHNICAL:
            context.update(self.get_technical_context(request))
        elif request.category == DocumentationCategory.USER:
            context.update(self.get_user_context(request))
        elif request.category == DocumentationCategory.DEVELOPER:
            context.update(self.get_developer_context(request))
        elif request.category == DocumentationCategory.QUALITY:
            context.update(self.get_quality_context(request))
        elif request.category == DocumentationCategory.DEPLOYMENT:
            context.update(self.get_deployment_context(request))

        return context

    def extract_code_examples(self, source_data: Dict[str, Any]):
        """Extract code examples from source data"""
        # Extract from files mentioned in source data
        files = source_data.get("files_created", [])

        for file_path in files:
            if file_path.endswith(('.kt', '.java')):
                self.extract_code_from_file(file_path)

    def extract_code_from_file(self, file_path: str):
        """Extract code examples from a single file"""
        try:
            if not os.path.exists(file_path):
                return

            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            # Parse file structure
            if file_path.endswith('.kt'):
                self.parse_kotlin_file(file_path, content)
            elif file_path.endswith('.java'):
                self.parse_java_file(file_path, content)

        except Exception as e:
            logger.error(f"Error extracting code from {file_path}: {e}")

    def parse_kotlin_file(self, file_path: str, content: str):
        """Parse Kotlin file and extract code examples"""
        # Extract classes and functions
        classes = re.findall(r'class\s+(\w+)[^{]*{([^}]*)}', content, re.DOTALL)
        functions = re.findall(r'fun\s+(\w+)\s*\([^)]*\)[^{]*{([^}]*)}', content, re.DOTALL)

        # Extract class examples
        for class_name, class_body in classes:
            example_id = f"{os.path.basename(file_path)}_{class_name}"

            if example_id not in self.code_examples:
                example = CodeExample(
                    id=example_id,
                    title=f"Class: {class_name}",
                    description=f"Kotlin class definition for {class_name}",
                    language="kotlin",
                    code=f"class {class_name} {{\n{class_body}\n}",
                    file_path=file_path,
                    line_start=0,
                    line_end=0
                )
                self.code_examples[example_id] = example
                self.save_code_example_to_db(example)

        # Extract function examples
        for func_name, func_body in functions:
            example_id = f"{os.path.basename(file_path)}_{func_name}"

            if example_id not in self.code_examples:
                example = CodeExample(
                    id=example_id,
                    title=f"Function: {func_name}",
                    description=f"Kotlin function definition for {func_name}",
                    language="kotlin",
                    code=f"fun {func_name}() {{\n{func_body}\n}",
                    file_path=file_path,
                    line_start=0,
                    line_end=0
                )
                self.code_examples[example_id] = example
                self.save_code_example_to_db(example)

    def parse_java_file(self, file_path: str, content: str):
        """Parse Java file and extract code examples"""
        # Extract classes and methods
        classes = re.findall(r'class\s+(\w+)[^{]*{([^}]*)}', content, re.DOTALL)
        methods = re.findall(r'public\s+\w+\s+(\w+)\s*\([^)]*\)[^{]*{([^}]*)}', content, re.DOTALL)

        # Extract class examples
        for class_name, class_body in classes:
            example_id = f"{os.path.basename(file_path)}_{class_name}"

            if example_id not in self.code_examples:
                example = CodeExample(
                    id=example_id,
                    title=f"Class: {class_name}",
                    description=f"Java class definition for {class_name}",
                    language="java",
                    code=f"public class {class_name} {{\n{class_body}\n}",
                    file_path=file_path,
                    line_start=0,
                    line_end=0
                )
                self.code_examples[example_id] = example
                self.save_code_example_to_db(example)

        # Extract method examples
        for method_name, method_body in methods:
            example_id = f"{os.path.basename(file_path)}_{method_name}"

            if example_id not in self.code_examples:
                example = CodeExample(
                    id=example_id,
                    title=f"Method: {method_name}",
                    description=f"Java method definition for {method_name}",
                    language="java",
                    code=f"public void {method_name}() {{\n{method_body}\n}",
                    file_path=file_path,
                    line_start=0,
                    line_end=0
                )
                self.code_examples[example_id] = example
                self.save_code_example_to_db(example)

    def generate_diagrams(self, source_data: Dict[str, Any]):
        """Generate diagrams from source data"""
        # Generate architecture diagram
        self.generate_architecture_diagram(source_data)

        # Generate sequence diagram
        self.generate_sequence_diagram(source_data)

        # Generate class diagram
        self.generate_class_diagram(source_data)

        # Generate activity diagram
        self.generate_activity_diagram(source_data)

    def generate_architecture_diagram(self, source_data: Dict[str, Any]):
        """Generate architecture diagram"""
        try:
            dot = graphviz.Digraph(comment='FibreField Tech Architecture')
            dot.attr(rankdir='TB', size='12,8')

            # Add components
            components = source_data.get("components", [])
            for component in components:
                dot.node(component.get("id", component.get("name")),
                         component.get("name", "Unknown"),
                         shape="box", style="rounded")

            # Add connections
            connections = source_data.get("connections", [])
            for connection in connections:
                dot.edge(connection.get("from"), connection.get("to"),
                         label=connection.get("type", "calls"))

            # Save diagram
            output_path = os.path.join(self.config["asset_directory"], "diagrams", "architecture.png")
            os.makedirs(os.path.dirname(output_path), exist_ok=True)

            dot.render(output_path.replace(".png", ""), format="png", cleanup=True)

            # Save diagram spec
            diagram = DiagramSpec(
                id="architecture_diagram",
                type="architecture",
                title="System Architecture",
                description="Overall system architecture diagram",
                source=dot.source,
                output_format="png",
                output_path=output_path
            )
            self.diagrams[diagram.id] = diagram
            self.save_diagram_to_db(diagram)

        except Exception as e:
            logger.error(f"Error generating architecture diagram: {e}")

    def generate_sequence_diagram(self, source_data: Dict[str, Any]):
        """Generate sequence diagram using PlantUML"""
        try:
            # Create PlantUML sequence diagram
            plantuml_code = """
@startuml Sequence Diagram
participant User
participant MainActivity
participant ViewModel
participant Repository
participant Database

User -> MainActivity : Launch App
MainActivity -> ViewModel : Initialize
ViewModel -> Repository : Load Data
Repository -> Database : Query
Database --> Repository : Return Data
Repository --> ViewModel : Return Data
ViewModel --> MainActivity : Update UI
MainActivity --> User : Display Data
@enduml
"""

            # Generate diagram
            output_path = os.path.join(self.config["asset_directory"], "diagrams", "sequence.png")
            os.makedirs(os.path.dirname(output_path), exist_ok=True)

            # Here you would use PlantUML to generate the diagram
            # For now, we'll create a placeholder

            diagram = DiagramSpec(
                id="sequence_diagram",
                type="sequence",
                title="Sequence Diagram",
                description="Application sequence diagram",
                source=plantuml_code,
                output_format="png",
                output_path=output_path
            )
            self.diagrams[diagram.id] = diagram
            self.save_diagram_to_db(diagram)

        except Exception as e:
            logger.error(f"Error generating sequence diagram: {e}")

    def generate_class_diagram(self, source_data: Dict[str, Any]):
        """Generate class diagram"""
        try:
            dot = graphviz.Digraph(comment='Class Diagram')
            dot.attr(rankdir='LR')

            # Add classes from code examples
            for example in self.code_examples.values():
                if example.language in ["kotlin", "java"]:
                    dot.node(example.id, example.title, shape="record")

            # Save diagram
            output_path = os.path.join(self.config["asset_directory"], "diagrams", "classes.png")
            os.makedirs(os.path.dirname(output_path), exist_ok=True)

            dot.render(output_path.replace(".png", ""), format="png", cleanup=True)

            diagram = DiagramSpec(
                id="class_diagram",
                type="class",
                title="Class Diagram",
                description="Application class diagram",
                source=dot.source,
                output_format="png",
                output_path=output_path
            )
            self.diagrams[diagram.id] = diagram
            self.save_diagram_to_db(diagram)

        except Exception as e:
            logger.error(f"Error generating class diagram: {e}")

    def generate_activity_diagram(self, source_data: Dict[str, Any]):
        """Generate activity diagram"""
        try:
            dot = graphviz.Digraph(comment='Activity Diagram')
            dot.attr(rankdir='TB')

            # Add activity nodes
            activities = source_data.get("activities", [])
            for i, activity in enumerate(activities):
                dot.node(str(i), activity.get("name", f"Activity {i}"), shape="ellipse")

            # Add flow
            for i in range(len(activities) - 1):
                dot.edge(str(i), str(i + 1))

            # Save diagram
            output_path = os.path.join(self.config["asset_directory"], "diagrams", "activity.png")
            os.makedirs(os.path.dirname(output_path), exist_ok=True)

            dot.render(output_path.replace(".png", ""), format="png", cleanup=True)

            diagram = DiagramSpec(
                id="activity_diagram",
                type="activity",
                title="Activity Diagram",
                description="Application activity diagram",
                source=dot.source,
                output_format="png",
                output_path=output_path
            )
            self.diagrams[diagram.id] = diagram
            self.save_diagram_to_db(diagram)

        except Exception as e:
            logger.error(f"Error generating activity diagram: {e}")

    def get_relevant_code_examples(self, request: DocumentationRequest) -> List[CodeExample]:
        """Get code examples relevant to the documentation request"""
        # Filter examples based on request category and source data
        relevant_examples = []

        for example in self.code_examples.values():
            if self.is_example_relevant(example, request):
                relevant_examples.append(example)

        return relevant_examples[:10]  # Limit to 10 examples

    def is_example_relevant(self, example: CodeExample, request: DocumentationRequest) -> bool:
        """Check if code example is relevant to the request"""
        # Simple relevance check based on file path and category
        if request.category == DocumentationCategory.TECHNICAL:
            return True  # All examples are relevant for technical docs
        elif request.category == DocumentationCategory.USER:
            return "ui" in example.file_path.lower() or "activity" in example.title.lower()
        elif request.category == DocumentationCategory.DEVELOPER:
            return True  # All examples are relevant for developer docs
        else:
            return True

    def get_relevant_diagrams(self, request: DocumentationRequest) -> List[DiagramSpec]:
        """Get diagrams relevant to the documentation request"""
        relevant_diagrams = []

        for diagram in self.diagrams.values():
            if self.is_diagram_relevant(diagram, request):
                relevant_diagrams.append(diagram)

        return relevant_diagrams

    def is_diagram_relevant(self, diagram: DiagramSpec, request: DocumentationRequest) -> bool:
        """Check if diagram is relevant to the request"""
        if request.category == DocumentationCategory.TECHNICAL:
            return diagram.type in ["architecture", "class"]
        elif request.category == DocumentationCategory.USER:
            return diagram.type in ["activity", "sequence"]
        elif request.category == DocumentationCategory.DEVELOPER:
            return True  # All diagrams are relevant for developer docs
        else:
            return True

    def get_documentation_metrics(self, request: DocumentationRequest) -> Dict[str, Any]:
        """Get documentation metrics"""
        return {
            "total_examples": len(self.code_examples),
            "total_diagrams": len(self.diagrams),
            "relevant_examples": len(self.get_relevant_code_examples(request)),
            "relevant_diagrams": len(self.get_relevant_diagrams(request)),
            "estimated_reading_time": self.estimate_reading_time(request),
            "complexity_score": self.calculate_complexity_score(request)
        }

    def estimate_reading_time(self, request: DocumentationRequest) -> int:
        """Estimate reading time in minutes"""
        # Simple estimation based on content length
        avg_words_per_minute = 200
        estimated_words = len(request.description.split()) + len(request.source_data.get("features_implemented", [])) * 50
        return max(1, estimated_words // avg_words_per_minute)

    def calculate_complexity_score(self, request: DocumentationRequest) -> float:
        """Calculate complexity score for documentation"""
        score = 0.0

        # Base complexity
        score += len(request.source_data.get("features_implemented", [])) * 0.1
        score += len(request.source_data.get("architectural_decisions", [])) * 0.2
        score += len(self.get_relevant_code_examples(request)) * 0.05
        score += len(self.get_relevant_diagrams(request)) * 0.1

        return min(score, 10.0)

    def generate_navigation(self, request: DocumentationRequest) -> List[Dict[str, str]]:
        """Generate navigation structure"""
        navigation = []

        # Add overview
        navigation.append({"title": "Overview", "path": "#overview"})

        # Add category-specific sections
        if request.category == DocumentationCategory.TECHNICAL:
            navigation.extend([
                {"title": "Architecture", "path": "#architecture"},
                {"title": "Components", "path": "#components"},
                {"title": "API Reference", "path": "#api-reference"},
                {"title": "Code Examples", "path": "#code-examples"}
            ])
        elif request.category == DocumentationCategory.USER:
            navigation.extend([
                {"title": "Getting Started", "path": "#getting-started"},
                {"title": "Features", "path": "#features"},
                {"title": "Workflows", "path": "#workflows"},
                {"title": "FAQ", "path": "#faq"}
            ])

        return navigation

    def generate_table_of_contents(self, request: DocumentationRequest) -> str:
        """Generate table of contents"""
        toc = "## Table of Contents\n\n"

        # Add navigation items
        for item in self.generate_navigation(request):
            toc += f"- [{item['title']}]({item['path']})\n"

        return toc

    def get_technical_context(self, request: DocumentationRequest) -> Dict[str, Any]:
        """Get technical documentation context"""
        return {
            "architecture": request.source_data.get("architectural_decisions", []),
            "components": request.source_data.get("components", []),
            "api_endpoints": request.source_data.get("api_endpoints", []),
            "database_schema": request.source_data.get("database_schema", {}),
            "technical_debt": request.source_data.get("technical_debt", [])
        }

    def get_user_context(self, request: DocumentationRequest) -> Dict[str, Any]:
        """Get user documentation context"""
        return {
            "features": request.source_data.get("features_implemented", []),
            "workflows": request.source_data.get("workflows", []),
            "user_stories": request.source_data.get("user_stories", []),
            "screenshots": request.source_data.get("screenshots", []),
            "getting_started": request.source_data.get("getting_started", [])
        }

    def get_developer_context(self, request: DocumentationRequest) -> Dict[str, Any]:
        """Get developer documentation context"""
        return {
            "setup_instructions": request.source_data.get("setup_instructions", []),
            "contribution_guidelines": request.source_data.get("contribution_guidelines", []),
            "testing_procedures": request.source_data.get("testing_procedures", []),
            "coding_standards": request.source_data.get("coding_standards", [])
        }

    def get_quality_context(self, request: DocumentationRequest) -> Dict[str, Any]:
        """Get quality documentation context"""
        return {
            "test_results": request.source_data.get("test_results", {}),
            "quality_metrics": request.source_data.get("quality_metrics", {}),
            "security_audit": request.source_data.get("security_audit", {}),
            "performance_metrics": request.source_data.get("performance_metrics", {})
        }

    def get_deployment_context(self, request: DocumentationRequest) -> Dict[str, Any]:
        """Get deployment documentation context"""
        return {
            "build_instructions": request.source_data.get("build_instructions", []),
            "deployment_procedures": request.source_data.get("deployment_procedures", []),
            "monitoring_setup": request.source_data.get("monitoring_setup", []),
            "troubleshooting": request.source_data.get("troubleshooting", [])
        }

    def convert_to_html(self, content: str) -> str:
        """Convert markdown to HTML"""
        try:
            html_content = markdown.markdown(content, extensions=['extra', 'codehilite', 'tables'])

            # Add HTML boilerplate
            html_template = """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FibreField Tech Android App - Documentation</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; max-width: 1200px; margin: 0 auto; padding: 20px; }
        pre { background-color: #f4f4f4; padding: 15px; border-radius: 5px; overflow-x: auto; }
        code { background-color: #f4f4f4; padding: 2px 4px; border-radius: 3px; }
        img { max-width: 100%; height: auto; }
    </style>
</head>
<body>
    {content}
</body>
</html>
            """

            return html_template.format(content=html_content)
        except Exception as e:
            logger.error(f"Error converting to HTML: {e}")
            return content

    def convert_to_pdf(self, content: str) -> bytes:
        """Convert markdown to PDF"""
        # This would use a library like weasyprint or pandoc
        # For now, return markdown content
        return content.encode('utf-8')

    def save_documentation(self, request: DocumentationRequest, content: Union[str, bytes]):
        """Save documentation to file"""
        os.makedirs(os.path.dirname(request.output_path), exist_ok=True)

        if isinstance(content, str):
            with open(request.output_path, 'w', encoding='utf-8') as f:
                f.write(content)
        else:
            with open(request.output_path, 'wb') as f:
                f.write(content)

    def validate_documentation(self, request: DocumentationRequest, content: str) -> List[str]:
        """Validate generated documentation"""
        errors = []

        # Check structure
        if not self.validate_structure(content):
            errors.append("Document structure is invalid")

        # Check for required sections based on category
        required_sections = self.get_required_sections(request.category)
        for section in required_sections:
            if section not in content:
                errors.append(f"Missing required section: {section}")

        # Check links if enabled
        if self.config["validation"]["check_links"]:
            link_errors = self.validate_links(content)
            errors.extend(link_errors)

        # Check code examples if enabled
        if self.config["validation"]["check_code_examples"]:
            code_errors = self.validate_code_examples(content)
            errors.extend(code_errors)

        return errors

    def validate_structure(self, content: str) -> bool:
        """Validate document structure"""
        # Check for basic markdown structure
        lines = content.split('\n')
        has_heading = any(line.startswith('#') for line in lines)
        has_content = len(lines) > 3

        return has_heading and has_content

    def get_required_sections(self, category: DocumentationCategory) -> List[str]:
        """Get required sections for documentation category"""
        sections_map = {
            DocumentationCategory.TECHNICAL: ["# Architecture", "# Components", "# API"],
            DocumentationCategory.USER: ["# Getting Started", "# Features"],
            DocumentationCategory.DEVELOPER: ["# Setup", "# Contributing"],
            DocumentationCategory.QUALITY: ["# Test Results", "# Quality Metrics"],
            DocumentationCategory.DEPLOYMENT: ["# Build", "# Deployment"]
        }

        return sections_map.get(category, [])

    def validate_links(self, content: str) -> List[str]:
        """Validate links in documentation"""
        errors = []

        # Extract markdown links
        link_pattern = r'\[([^\]]+)\]\(([^)]+)\)'
        links = re.findall(link_pattern, content)

        for link_text, link_url in links:
            if link_url.startswith('http'):
                try:
                    response = requests.head(link_url, timeout=5)
                    if response.status_code >= 400:
                        errors.append(f"Broken link: {link_url} (HTTP {response.status_code})")
                except Exception:
                    errors.append(f"Cannot validate link: {link_url}")

        return errors

    def validate_code_examples(self, content: str) -> List[str]:
        """Validate code examples in documentation"""
        errors = []

        # Extract code blocks
        code_pattern = r'```(\w+)\n([^`]+)```'
        code_blocks = re.findall(code_pattern, content)

        for language, code in code_blocks:
            # Basic syntax validation
            if language == "kotlin":
                if not self.validate_kotlin_syntax(code):
                    errors.append(f"Invalid Kotlin syntax in code block")
            elif language == "java":
                if not self.validate_java_syntax(code):
                    errors.append(f"Invalid Java syntax in code block")

        return errors

    def validate_kotlin_syntax(self, code: str) -> bool:
        """Basic Kotlin syntax validation"""
        # Simple validation - check for basic structure
        return "fun" in code or "class" in code or "val" in code or "var" in code

    def validate_java_syntax(self, code: str) -> bool:
        """Basic Java syntax validation"""
        # Simple validation - check for basic structure
        return "public" in code or "class" in code or "void" in code

    def save_code_example_to_db(self, example: CodeExample):
        """Save code example to database"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        cursor.execute('''
            INSERT OR REPLACE INTO code_examples (
                id, title, description, language, code, file_path,
                line_start, line_end, dependencies, related_tests
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ''', (
            example.id, example.title, example.description, example.language,
            example.code, example.file_path, example.line_start, example.line_end,
            json.dumps(example.dependencies), json.dumps(example.related_tests)
        ))

        conn.commit()
        conn.close()

    def save_diagram_to_db(self, diagram: DiagramSpec):
        """Save diagram to database"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        cursor.execute('''
            INSERT OR REPLACE INTO diagrams (
                id, type, title, description, source, output_format, output_path, metadata
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ''', (
            diagram.id, diagram.type, diagram.title, diagram.description,
            diagram.source, diagram.output_format, diagram.output_path,
            json.dumps(diagram.metadata)
        ))

        conn.commit()
        conn.close()

    def save_result_to_db(self, result: DocumentationResult):
        """Save documentation result to database"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        cursor.execute('''
            INSERT OR REPLACE INTO documentation_results (
                request_id, success, output_path, generated_at,
                file_size, validation_errors, warnings, processing_time
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ''', (
            result.request_id, result.success, result.output_path,
            result.generated_at.isoformat(), result.file_size,
            json.dumps(result.validation_errors), json.dumps(result.warnings),
            result.processing_time
        ))

        conn.commit()
        conn.close()

    # Template methods
    def get_technical_overview_template(self) -> str:
        """Get technical overview template"""
        return """# {{ request.title }}

## Overview
{{ request.description }}

## Architecture
{% if architecture %}
{% for decision in architecture %}
### {{ decision.title }}
{{ decision.description }}

**Rationale**: {{ decision.rationale }}

**Impact**: {{ decision.impact }}
{% endfor %}
{% endif %}

## Components
{% if components %}
{% for component in components %}
### {{ component.name }}
- **Type**: {{ component.type }}
- **Description**: {{ component.description }}
- **Technologies**: {{ component.technologies|join(', ') }}
- **Responsibilities**: {{ component.responsibilities }}
{% endfor %}
{% endif %}

## API Reference
{% if api_endpoints %}
{% for endpoint in api_endpoints %}
### {{ endpoint.method }} {{ endpoint.path }}
- **Description**: {{ endpoint.description }}
- **Parameters**: {{ endpoint.parameters|join(', ') }}
- **Response**: {{ endpoint.response }}
{% endfor %}
{% endif %}

## Code Examples
{% if code_examples %}
{% for example in code_examples %}
### {{ example.title }}
{{ example.description }}

```{{ example.language }}
{{ example.code }}
```
{% endfor %}
{% endif %}

## Diagrams
{% if diagrams %}
{% for diagram in diagrams %}
### {{ diagram.title }}
{{ diagram.description }}

![{{ diagram.title }}]({{ diagram.output_path }})
{% endfor %}
{% endif %}

*Generated on {{ generated_at }}*
"""

    def get_api_reference_template(self) -> str:
        """Get API reference template"""
        return """# API Reference

## Overview
This document provides comprehensive API reference documentation for the FibreField Tech Android App.

## Authentication
All API requests require authentication using Bearer tokens:

```http
Authorization: Bearer your_token_here
```

## Endpoints
{% if api_endpoints %}
{% for endpoint in api_endpoints %}
### {{ endpoint.method }} {{ endpoint.path }}
{{ endpoint.description }}

#### Parameters
{% for param in endpoint.parameters %}
- **{{ param.name }}** ({{ param.type }}): {{ param.description }}
{% endfor %}

#### Request Body
```json
{{ endpoint.request_body }}
```

#### Response
```json
{{ endpoint.response_body }}
```

#### Status Codes
- **200**: Success
- **400**: Bad Request
- **401**: Unauthorized
- **404**: Not Found
- **500**: Internal Server Error

#### Example
```http
{{ endpoint.method }} {{ endpoint.path }}
Content-Type: application/json
Authorization: Bearer token

{{ endpoint.request_body }}
```

{% endfor %}
{% endif %}

## Error Handling
The API uses standard HTTP status codes to indicate success or failure. Error responses include a JSON object with error details:

```json
{
    "error": {
        "code": "ERROR_CODE",
        "message": "Error description",
        "details": "Additional error information"
    }
}
```

## Rate Limiting
API requests are rate limited to 100 requests per minute per authentication token.

*Generated on {{ generated_at }}*
"""

    def get_user_guide_template(self) -> str:
        """Get user guide template"""
        return """# User Guide

## Getting Started
Welcome to the FibreField Tech Android App! This guide will help you get started with all the features.

### Installation
1. Download the app from the Google Play Store
2. Install and open the app
3. Follow the on-screen setup wizard

### First Run
- Create an account or sign in
- Grant necessary permissions
- Set up your profile

## Features
{% if features %}
{% for feature in features %}
### {{ feature.name }}
{{ feature.description }}

#### How to Use
{{ feature.usage_instructions }}

#### Screenshots
{% if feature.screenshots %}
{% for screenshot in feature.screenshots %}
![{{ feature.title }}]({{ screenshot }})
{% endfor %}
{% endif %}
{% endfor %}
{% endif %}

## Workflows
{% if workflows %}
{% for workflow in workflows %}
### {{ workflow.name }}
{{ workflow.description }}

#### Steps
{% for step in workflow.steps %}
{{ loop.index }}. {{ step }}
{% endfor %}
{% endfor %}
{% endif %}

## Troubleshooting
### Common Issues

#### App won't open
- Check your device meets minimum requirements
- Ensure you have enough storage space
- Restart your device

#### Features not working
- Check your internet connection
- Ensure you're logged in
- Update to the latest version

#### Data not syncing
- Check your internet connection
- Ensure sync is enabled in settings
- Try manual sync

## Support
If you need additional help:
- Check our FAQ section
- Contact support at support@fibrefield.tech
- Visit our help center at help.fibrefield.tech

*Generated on {{ generated_at }}*
"""

    def get_developer_guide_template(self) -> str:
        """Get developer guide template"""
        return """# Developer Guide

## Setup Instructions
{% if setup_instructions %}
{% for instruction in setup_instructions %}
### {{ instruction.title }}
{{ instruction.description }}

```bash
{{ instruction.command }}
```
{% endfor %}
{% endif %}

## Project Structure
```
FibreFieldTech-Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fibrefield/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle
├── docs/
├── scripts/
└── README.md
```

## Contributing
{% if contribution_guidelines %}
{% for guideline in contribution_guidelines %}
### {{ guideline.title }}
{{ guideline.content }}
{% endfor %}
{% endif %}

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Run all tests
6. Submit a pull request

### Code Style
- Follow Android coding standards
- Use Kotlin for new code
- Write comprehensive tests
- Document all public APIs
- Use proper error handling

## Testing
{% if testing_procedures %}
{% for procedure in testing_procedures %}
### {{ procedure.name }}
{{ procedure.description }}

```bash
{{ procedure.command }}
```
{% endfor %}
{% endif %}

### Running Tests
```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew connectedAndroidTest

# UI tests
./gradlew connectedCheck
```

### Test Coverage
We maintain >95% test coverage for all modules.

## Build and Deploy
```bash
# Build the project
./gradlew build

# Generate debug APK
./gradlew assembleDebug

# Generate release APK
./gradlew assembleRelease

# Run tests
./gradlew test
```

## Dependencies
### Core Dependencies
- AndroidX libraries
- Kotlin Coroutines
- Room Database
- Retrofit for networking
- Material Design components

### Development Dependencies
- JUnit for testing
- Mockito for mocking
- Espresso for UI testing

## Architecture
The app follows MVVM architecture with clean architecture principles:

- **Presentation Layer**: Activities, Fragments, ViewModels
- **Domain Layer**: Use cases, business logic
- **Data Layer**: Repositories, data sources

*Generated on {{ generated_at }}*
"""

    def get_quality_report_template(self) -> str:
        """Get quality report template"""
        return """# Quality Report

## Test Results
{% if test_results %}
### Overall Test Coverage: {{ test_results.overall.coverage|round(2) }}%

#### Test Summary
- **Total Tests**: {{ test_results.overall.total }}
- **Passed**: {{ test_results.overall.passed }}
- **Failed**: {{ test_results.overall.failed }}
- **Skipped**: {{ test_results.overall.skipped }}

#### Test Coverage by Module
{% for module, results in test_results.by_module.items() %}
##### {{ module }}
- **Coverage**: {{ results.coverage|round(2) }}%
- **Tests**: {{ results.total }}
- **Passed**: {{ results.passed }}
- **Failed**: {{ results.failed }}
{% endfor %}
{% endif %}

## Quality Metrics
{% if quality_metrics %}
{% for metric, value in quality_metrics.items() %}
### {{ metric.replace('_', ' ').title() }}
{{ value|round(2) }}%
{% endfor %}
{% endif %}

## Security Audit
{% if security_audit %}
### Security Score: {{ security_audit.score|round(2) }}/100

#### Security Issues
{% for issue in security_audit.issues %}
#### {{ issue.severity|title }}: {{ issue.title }}
- **Description**: {{ issue.description }}
- **Location**: {{ issue.location }}
- **Fix**: {{ issue.recommendation }}
{% endfor %}
{% endif %}

## Performance Metrics
{% if performance_metrics %}
### Load Times
- **App Start**: {{ performance_metrics.app_start_ms }}ms
- **Screen Load**: {{ performance_metrics.screen_load_ms }}ms
- **API Response**: {{ performance_metrics.api_response_ms }}ms

### Memory Usage
- **Average**: {{ performance_metrics.memory_usage_mb }}MB
- **Peak**: {{ performance_metrics.memory_peak_mb }}MB
- **Leak Check**: {{ performance_metrics.memory_leak_check }}
{% endif %}

## Code Quality
### Static Analysis Results
- **Code Smells**: {{ code_quality.code_smells }}
- **Vulnerabilities**: {{ code_quality.vulnerabilities }}
- **Technical Debt**: {{ code_quality.technical_debt }} hours

### Code Style Compliance
- **Kotlin Style**: {{ code_quality.kotlin_compliance }}%
- **Java Style**: {{ code_quality.java_compliance }}%

## Recommendations
Based on the quality analysis, the following improvements are recommended:

1. **Test Coverage**: Focus on increasing coverage in low-tested modules
2. **Performance**: Optimize critical paths for better user experience
3. **Security**: Address security issues to improve overall security posture
4. **Code Quality**: Refactor complex code sections for better maintainability

## Next Steps
1. Implement recommended fixes
2. Run quality validation again
3. Update documentation
4. Monitor metrics over time

*Generated on {{ generated_at }}*
"""

    def get_deployment_guide_template(self) -> str:
        """Get deployment guide template"""
        return """# Deployment Guide

## Build Instructions
{% if build_instructions %}
{% for instruction in build_instructions %}
### {{ instruction.title }}
{{ instruction.description }}

```bash
{{ instruction.command }}
```
{% endfor %}
{% endif %}

## Deployment Procedures
{% if deployment_procedures %}
{% for procedure in deployment_procedures %}
### {{ procedure.name }}
{{ procedure.description }}

#### Prerequisites
{% for prereq in procedure.prerequisites %}
- {{ prereq }}
{% endfor %}

#### Steps
{% for step in procedure.steps %}
{{ loop.index }}. {{ step }}
{% endfor %}

#### Verification
{{ procedure.verification }}
{% endfor %}
{% endif %}

## Environment Configuration
### Required Environment Variables
```env
# Application Configuration
APP_NAME=FibreFieldTech
APP_VERSION=1.0.0
ENVIRONMENT=production

# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/fibrefield
DB_USERNAME=app_user
DB_PASSWORD=secure_password

# API Configuration
API_BASE_URL=https://api.fibrefield.tech
API_KEY=your_api_key_here

# Security Configuration
JWT_SECRET=your_jwt_secret_here
ENCRYPTION_KEY=your_encryption_key_here
```

## Monitoring Setup
{% if monitoring_setup %}
{% for setup in monitoring_setup %}
### {{ setup.name }}
{{ setup.description }}

#### Configuration
```yaml
{{ setup.config }}
```

#### Alerts
{% for alert in setup.alerts %}
- **{{ alert.name }}**: {{ alert.condition }} - {{ alert.action }}
{% endfor %}
{% endfor %}
{% endif %}

## Troubleshooting
### Common Issues

#### Build Failures
- **Issue**: Gradle sync fails
- **Solution**: Check internet connection and clear Gradle cache
- **Command**: `./gradlew --refresh-dependencies`

#### Deployment Failures
- **Issue**: App crashes on startup
- **Solution**: Check logs for missing dependencies or configuration issues
- **Command**: `adb logcat | grep "FibreField"`

#### Performance Issues
- **Issue**: App is slow or unresponsive
- **Solution**: Check memory usage and optimize critical paths
- **Tool**: Android Profiler

#### Database Issues
- **Issue**: Database connection fails
- **Solution**: Check database credentials and network connectivity
- **Command**: `adb shell am start -a android.intent.action.VIEW -d content://settings/`

## Rollback Procedure
If a deployment causes issues, follow these steps to rollback:

1. **Identify the problematic version**
2. **Revert to the previous stable version**
3. **Restore database backup if needed**
4. **Monitor system health**
5. **Investigate the root cause**

```bash
# Rollback commands
git checkout <previous_version>
./gradlew clean build
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Maintenance
### Regular Tasks
- **Daily**: Monitor system health and performance
- **Weekly**: Check for security updates and patches
- **Monthly**: Review logs and optimize performance
- **Quarterly**: Full system audit and updates

### Backup Strategy
- **Database**: Daily backups with 30-day retention
- **Configuration**: Version controlled with git
- **User Data**: Regular backups with encryption
- **Logs**: 90-day retention with compression

## Security Considerations
- **Data Encryption**: All sensitive data must be encrypted at rest and in transit
- **API Security**: Use HTTPS and implement proper authentication
- **Code Security**: Regular security scans and vulnerability assessments
- **Deployment Security**: Secure deployment pipeline with proper access controls

*Generated on {{ generated_at }}*
"""

    def get_architecture_template(self) -> str:
        """Get architecture template"""
        return """# Architecture Documentation

## System Overview
The FibreField Tech Android App follows a modern architecture pattern that ensures scalability, maintainability, and testability.

## Architecture Pattern
The app implements MVVM (Model-View-ViewModel) architecture with Clean Architecture principles:

- **Presentation Layer**: Handles UI and user interactions
- **Domain Layer**: Contains business logic and use cases
- **Data Layer**: Manages data sources and persistence

## Component Overview
{% if components %}
{% for component in components %}
### {{ component.name }}
- **Type**: {{ component.type }}
- **Responsibility**: {{ component.responsibility }}
- **Technologies**: {{ component.technologies|join(', ') }}
- **Dependencies**: {{ component.dependencies|join(', ') }}

#### Key Features
{% for feature in component.features %}
- {{ feature }}
{% endfor %}
{% endfor %}
{% endif %}

## Data Flow
### User Interaction Flow
1. **User Action**: User interacts with UI components
2. **ViewModel**: Processes user input and updates UI state
3. **Use Case**: Executes business logic
4. **Repository**: Manages data operations
5. **Data Source**: Retrieves or stores data
6. **Response**: Data flows back through the layers

### API Communication Flow
1. **Request**: ViewModel requests data from repository
2. **Repository**: Calls appropriate data source
3. **Network**: Makes HTTP request to backend
4. **Response**: Backend returns data
5. **Processing**: Data is processed and cached
6. **Delivery**: Data is delivered to ViewModel
7. **UI Update**: ViewModel updates UI with new data

## Design Patterns
The app implements several design patterns to ensure code quality and maintainability:

### Repository Pattern
- **Purpose**: Abstract data sources from the rest of the app
- **Implementation**: Repository classes provide clean API for data operations
- **Benefits**: Decouples data sources, enables easy testing

### Observer Pattern
- **Purpose**: Notify UI of data changes
- **Implementation**: LiveData/StateFlow for reactive programming
- **Benefits**: Automatic UI updates, lifecycle-aware

### Dependency Injection
- **Purpose**: Manage dependencies and promote loose coupling
- **Implementation**: Hilt/Dagger for dependency injection
- **Benefits**: Easier testing, better maintainability

### Factory Pattern
- **Purpose**: Create objects without specifying exact classes
- **Implementation**: Factory methods for creating ViewModels and other objects
- **Benefits**: Flexible object creation, easier testing

## Module Structure
The app is organized into modules for better separation of concerns:

### App Module
- Main application module
- Contains app-level dependencies
- Defines application class

### Feature Modules
- Feature-specific functionality
- Self-contained with their own dependencies
- Can be developed and tested independently

### Core Modules
- Shared functionality across features
- Common utilities and base classes
- Network, database, and other core services

### Data Module
- Data layer implementation
- Database models and DAOs
- Network models and API services

## Security Architecture
### Data Security
- **Encryption**: All sensitive data encrypted at rest
- **Authentication**: Secure user authentication with token-based auth
- **Authorization**: Role-based access control
- **Network Security**: HTTPS with certificate pinning

### App Security
- **Code Obfuscation**: ProGuard/R8 for code obfuscation
- **Tamper Detection**: Runtime integrity checks
- **Secure Storage**: Android Keystore for sensitive data
- **Permission Management**: Minimal required permissions

## Performance Architecture
### Memory Management
- **Lazy Loading**: Resources loaded only when needed
- **Memory Caching**: Efficient caching strategies
- **Leak Prevention**: Proper lifecycle management
- **Optimization**: Regular performance monitoring

### Network Optimization
- **Caching**: Intelligent caching strategies
- **Compression**: Request/response compression
- **Batching**: Batch operations when possible
- **Offline Support**: Graceful offline handling

## Testing Architecture
### Test Pyramid
- **Unit Tests**: 70% - Test individual components
- **Integration Tests**: 20% - Test component interactions
- **UI Tests**: 10% - Test end-to-end functionality

### Test Coverage
- **Minimum Coverage**: 95% for all modules
- **Critical Path**: 100% coverage for core functionality
- **Security**: 100% coverage for security-related code

## Scalability Considerations
### Horizontal Scaling
- **Modular Architecture**: Easy to add new features
- **Plugin System**: Extensible with plugins
- **API Design**: RESTful APIs for easy integration

### Vertical Scaling
- **Performance Optimization**: Regular performance profiling
- **Resource Management**: Efficient resource utilization
- **Code Optimization**: Regular code refactoring

## Future Enhancements
### Planned Improvements
- **Microservices**: Consider microservices architecture for complex features
- **Server-Driven UI**: Explore server-driven UI for better flexibility
- **Advanced Analytics**: Integrate advanced analytics and monitoring
- **AI/ML Integration**: Add AI/ML capabilities for enhanced functionality

### Architecture Evolution
- **Continuous Improvement**: Regular architecture reviews
- **Technology Updates**: Keep up with latest Android development practices
- **Community Feedback**: Incorporate user and developer feedback
- **Best Practices**: Follow industry best practices and patterns

*Generated on {{ generated_at }}*
"""

    def get_testing_template(self) -> str:
        """Get testing template"""
        return """# Testing Documentation

## Testing Strategy
The FibreField Tech Android App follows a comprehensive testing strategy to ensure high quality and reliability.

## Test Pyramid
Our testing approach follows the test pyramid model:

- **Unit Tests (70%)**: Test individual components in isolation
- **Integration Tests (20%)**: Test component interactions
- **UI Tests (10%)**: Test end-to-end user scenarios

## Testing Frameworks
### Unit Testing
- **JUnit 5**: Primary unit testing framework
- **Mockito**: For mocking dependencies
- **Kotlin Coroutines Test**: For testing coroutines
- **Truth**: For fluent assertions

### Integration Testing
- **AndroidX Test**: Core testing utilities
- **Espresso**: UI testing framework
- **Robolectric**: For testing Android components on JVM
- **MockWebServer**: For testing network operations

### UI Testing
- **Espresso**: UI automation testing
- **UI Automator**: Cross-app UI testing
- **Barista**: Espresso extensions for easier testing
- **Screenshot Testing**: Visual regression testing

## Test Structure
```
app/src/test/
├── unit/
│   ├── viewmodel/
│   ├── usecase/
│   ├── repository/
│   └── utils/
├── integration/
│   ├── database/
│   ├── network/
│   └── repository/
└── android/
    ├── activity/
    ├── fragment/
    └── service/
```

## Test Categories
### Unit Tests
- **ViewModel Tests**: Test ViewModel logic and state management
- **Use Case Tests**: Test business logic and use cases
- **Repository Tests**: Test data layer logic (mocked dependencies)
- **Utility Tests**: Test utility functions and helpers

### Integration Tests
- **Database Tests**: Test Room database operations
- **Network Tests**: Test API client and network operations
- **Repository Integration**: Test repository with real dependencies
- **Service Tests**: Test Android services and broadcast receivers

### UI Tests
- **Activity Tests**: Test activity lifecycle and interactions
- **Fragment Tests**: Test fragment behavior and navigation
- **Dialog Tests**: Test dialog interactions and user input
- **Navigation Tests**: Test app navigation flows

## Test Coverage
### Coverage Targets
- **Overall Coverage**: 95% minimum
- **Critical Path Coverage**: 100%
- **Security-Related Code**: 100%
- **UI Components**: 90%

### Coverage Reports
Coverage reports are generated automatically and can be found at:
- **HTML Report**: `app/build/reports/coverage/debug/index.html`
- **XML Report**: `app/build/reports/coverage/debug/report.xml`

## Test Data Management
### Test Data Sources
- **Mock Data**: Generated using MockK/Mockito
- **Test Fixtures**: Pre-defined test data in JSON/XML files
- **Test Databases**: In-memory databases for testing
- **Test Assets**: Test files in `src/androidTest/assets/`

### Data Generation
- **Faker**: For generating realistic test data
- **Factory Pattern**: For creating test objects
- **Test Builders**: For building complex test objects
- **Random Data**: For edge case testing

## Continuous Testing
### CI/CD Integration
- **GitHub Actions**: Automated testing on PRs
- **Pre-commit Hooks**: Run tests before commits
- **Scheduled Tests**: Regular test runs on main branch
- **Parallel Testing**: Run tests in parallel for faster execution

### Test Execution
```bash
# Run unit tests
./gradlew test

# Run unit tests with coverage
./gradlew testDebugUnitTestCoverage

# Run integration tests
./gradlew connectedAndroidTest

# Run UI tests
./gradlew connectedCheck

# Run all tests
./gradlew test connectedAndroidTest
```

## Test Best Practices
### Unit Test Best Practices
- **Fast**: Tests should run quickly
- **Isolated**: Tests should not depend on each other
- **Repeatable**: Tests should produce consistent results
- **Self-Validating**: Tests should have clear pass/fail criteria
- **Timely**: Tests should be written with the code

### Integration Test Best Practices
- **Real Dependencies**: Use real dependencies when possible
- **Test Environment**: Set up proper test environment
- **Cleanup**: Clean up after tests to avoid interference
- **Error Handling**: Test error scenarios and edge cases
- **Performance**: Consider performance implications

### UI Test Best Practices
- **User Scenarios**: Test real user workflows
- **Idling Resources**: Handle async operations properly
- **Screen Orientation**: Test different screen orientations
- **Accessibility**: Include accessibility testing
- **Performance**: Consider UI performance in tests

## Test Examples
### Unit Test Example
```kotlin
@ExperimentalCoroutinesTest
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: FakeRepository

    @Before
    fun setUp() {
        repository = FakeRepository()
        viewModel = MainViewModel(repository)
    }

    @Test
    fun `load data should update ui state`() = runTest {
        // Given
        val testData = listOf("Item 1", "Item 2")
        repository.setTestData(testData)

        // When
        viewModel.loadData()

        // Then
        assertEquals(testData, viewModel.uiState.value.items)
    }
}
```

### Integration Test Example
```kotlin
@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: ItemDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.itemDao()
    }

    @Test
    fun insert_and_retrieve_item() {
        // Given
        val item = Item(id = 1, name = "Test Item")

        // When
        dao.insert(item)
        val result = dao.getItemById(1)

        // Then
        assertEquals(item, result)
    }
}
```

### UI Test Example
```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun clickButton_showsSnackbar() {
        // When
        onView(withId(R.id.action_button)).perform(click())

        // Then
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.action_completed)))
    }
}
```

## Test Configuration
### Gradle Configuration
```gradle
android {
    testOptions {
        unitTests {
            includeAndroidResources = true
            returnDefaultValues = true
        }
    }
}

dependencies {
    // Testing dependencies
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.mockito:mockito-core:4.0.0'
    testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0'

    // Android testing dependencies
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
    androidTestImplementation 'androidx.test:runner:1.4.0'
}
```

## Test Reports and Metrics
### Test Metrics
- **Test Execution Time**: Monitor test execution times
- **Test Failure Rate**: Track test failure rates
- **Test Coverage**: Maintain coverage metrics
- **Flaky Tests**: Identify and fix flaky tests

### Test Reports
- **JUnit XML Reports**: Standard test result reports
- **HTML Reports**: Visual test result reports
- **Coverage Reports**: Code coverage analysis
- **Performance Reports**: Test performance metrics

## Troubleshooting Tests
### Common Issues
- **Test Dependencies**: Ensure proper test dependencies
- **Test Environment**: Set up correct test environment
- **Mock Configuration**: Properly configure mocks
- **Async Operations**: Handle async operations in tests
- **UI Thread**: Run UI tests on correct thread

### Debugging Tests
- **Debug Mode**: Run tests in debug mode
- **Logging**: Add logging for troubleshooting
- **Breakpoints**: Use breakpoints for debugging
- **Test Isolation**: Run tests individually
- **Environment**: Check test environment setup

## Future Testing Enhancements
### Planned Improvements
- **Snapshot Testing**: Implement snapshot testing for UI components
- **Property-Based Testing**: Add property-based testing for complex logic
- **Performance Testing**: Add comprehensive performance testing
- **Mutation Testing**: Implement mutation testing for better coverage

### Tooling Improvements
- **Test Parallelization**: Improve test parallel execution
- **Test Caching**: Implement test caching for faster builds
- **Test Analytics**: Add test analytics and insights
- **Test Visualization**: Improve test result visualization

*Generated on {{ generated_at }}*
"""

    def get_security_template(self) -> str:
        """Get security template"""
        return """# Security Documentation

## Security Overview
The FibreField Tech Android App implements comprehensive security measures to protect user data and ensure secure operations.

## Security Architecture
### Security Layers
- **Application Security**: Code-level security controls
- **Data Security**: Data protection at rest and in transit
- **Network Security**: Secure communication protocols
- **Device Security**: Device-level security measures
- **User Security**: Authentication and authorization

### Security Principles
- **Defense in Depth**: Multiple layers of security controls
- **Least Privilege**: Minimal required permissions and access
- **Zero Trust**: Verify explicitly, use least privilege access
- **Encryption Everywhere**: Encrypt all sensitive data
- **Secure by Default**: Secure configurations out of the box

## Authentication & Authorization
### Authentication Methods
- **Email/Password**: Traditional username and password
- **Biometric**: Fingerprint and face recognition
- **Social Login**: Google, Facebook, and other social providers
- **Multi-Factor**: Optional multi-factor authentication

### Authorization Model
- **Role-Based Access Control (RBAC)**: Role-based permissions
- **Attribute-Based Access Control (ABAC)**: Context-aware permissions
- **Permission Scopes**: Granular permission management
- **Session Management**: Secure session handling

### Authentication Flow
1. **User Input**: User enters credentials
2. **Validation**: Client-side validation
3. **API Request**: Secure API call to authentication service
4. **Token Generation**: JWT token generation
5. **Token Storage**: Secure token storage
6. **Session Management**: Session lifecycle management

## Data Security
### Encryption at Rest
- **Database Encryption**: SQLCipher for database encryption
- **File Encryption**: Android Keystore for file encryption
- **SharedPreferences**: Encrypted preferences
- **Cache Encryption**: Secure cache implementation

### Encryption in Transit
- **HTTPS/TLS**: Secure communication protocol
- **Certificate Pinning**: SSL certificate pinning
- **Network Security Configuration**: Secure network configuration
- **API Security**: Secure API design and implementation

### Key Management
- **Android Keystore**: Hardware-backed key storage
- **Key Generation**: Secure key generation
- **Key Rotation**: Regular key rotation
- **Key Backup**: Secure key backup procedures

## Network Security
### Secure Communication
- **HTTPS Enforcement**: All communication over HTTPS
- **Certificate Validation**: Proper certificate validation
- **TLS Configuration**: Secure TLS configuration
- **Network Security Config**: Android network security configuration

### API Security
- **Authentication**: Secure API authentication
- **Authorization**: Proper API authorization
- **Rate Limiting**: API rate limiting
- **Input Validation**: Comprehensive input validation
- **Output Encoding**: Secure output encoding

### Network Monitoring
- **Traffic Analysis**: Monitor network traffic patterns
- **Anomaly Detection**: Detect suspicious network activity
- **DDoS Protection**: Protection against denial of service attacks
- **Man-in-the-Middle Protection**: Protect against MITM attacks

## Code Security
### Secure Coding Practices
- **Input Validation**: Validate all user inputs
- **Output Encoding**: Encode all outputs
- **Error Handling**: Secure error handling
- **Memory Management**: Proper memory management
- **Resource Management**: Secure resource handling

### Vulnerability Prevention
- **SQL Injection**: Parameterized queries and ORM usage
- **XSS Prevention**: Proper output encoding and CSP
- **CSRF Protection**: Anti-CSRF tokens
- **Buffer Overflows**: Safe string handling
- **Insecure Deserialization**: Safe deserialization practices

### Code Obfuscation
- **ProGuard/R8**: Code obfuscation and optimization
- **String Encryption**: Encrypt sensitive strings
- **Class Encryption**: Encrypt critical classes
- **Resource Protection**: Protect app resources

## Privacy Protection
### Data Collection
- **Minimal Data**: Collect only necessary data
- **Informed Consent**: Get user consent for data collection
- **Data Minimization**: Minimize data collection
- **Purpose Limitation**: Collect data for specific purposes

### Data Storage
- **Local Storage**: Secure local data storage
- **Cloud Storage**: Secure cloud data storage
- **Data Retention**: Proper data retention policies
- **Data Deletion**: Secure data deletion procedures

### User Privacy
- **Privacy Policy**: Clear privacy policy
- **User Controls**: User privacy controls
- **Data Access**: User data access controls
- **Data Portability**: Data portability features

## Platform Security
### Android Security Features
- **App Signing**: Proper app signing
- **Permission Model**: Android permission model
- **Sandboxing**: App sandboxing
- **SELinux**: Security-Enhanced Linux

### Device Security
- **Device Authentication**: Device authentication
- **Root Detection**: Root detection
- **Emulator Detection**: Emulator detection
- **Jailbreak Detection**: Jailbreak detection

## Security Testing
### Security Testing Methods
- **Penetration Testing**: Regular penetration testing
- **Vulnerability Scanning**: Automated vulnerability scanning
- **Static Analysis**: Static code analysis
- **Dynamic Analysis**: Dynamic application analysis

### Security Testing Tools
- **OWASP ZAP**: Web application security scanner
- **MobSF**: Mobile security framework
- **Android Studio**: Built-in security tools
- **Custom Scripts**: Custom security testing scripts

### Security Metrics
- **Vulnerability Count**: Track security vulnerabilities
- **Risk Score**: Calculate security risk scores
- **Compliance Status**: Monitor compliance status
- **Incident Response**: Track security incidents

## Incident Response
### Incident Response Plan
- **Detection**: Security incident detection
- **Analysis**: Incident analysis and assessment
- **Containment**: Incident containment procedures
- **Eradication**: Incident eradication
- **Recovery**: System recovery procedures
- **Lessons Learned**: Post-incident review

### Security Monitoring
- **Real-time Monitoring**: Real-time security monitoring
- **Log Analysis**: Security log analysis
- **Anomaly Detection**: Security anomaly detection
- **Alerting**: Security alerting system

## Compliance & Standards
### Regulatory Compliance
- **GDPR**: General Data Protection Regulation
- **CCPA**: California Consumer Privacy Act
- **HIPAA**: Health Insurance Portability and Accountability Act
- **SOC 2**: Service Organization Control 2

### Security Standards
- **OWASP MASVS**: Mobile Application Security Verification Standard
- **OWASP Mobile Top 10**: Mobile application security risks
- **NIST Standards**: National Institute of Standards and Technology
- **ISO 27001**: Information security management

## Security Best Practices
### Development Security
- **Secure SDLC**: Secure software development lifecycle
- **Code Reviews**: Security-focused code reviews
- **Security Training**: Security awareness training
- **Security Documentation**: Comprehensive security documentation

### Operations Security
- **Secure Deployment**: Secure deployment procedures
- **Patch Management**: Regular security patching
- **Backup Security**: Secure backup procedures
- **Disaster Recovery**: Disaster recovery planning

### User Security
- **Security Education**: User security education
- **Security Awareness**: Security awareness programs
- **Security Guidelines**: Security usage guidelines
- **Security Support**: Security support channels

## Future Security Enhancements
### Planned Security Improvements
- **Biometric Authentication**: Advanced biometric authentication
- **Hardware Security**: Hardware-backed security features
- **AI Security**: AI-powered security monitoring
- **Zero Trust Architecture**: Zero trust security model

### Security Innovation
- **Quantum Resistance**: Quantum-resistant cryptography
- **Privacy-Enhancing Technologies**: Privacy-enhancing technologies
- **Security Automation**: Security automation and orchestration
- **Security Analytics**: Advanced security analytics

*Generated on {{ generated_at }}*
"""

    def get_performance_template(self) -> str:
        """Get performance template"""
        return """# Performance Documentation

## Performance Overview
The FibreField Tech Android App is designed and optimized for high performance across various devices and network conditions.

## Performance Goals
### Target Metrics
- **App Startup Time**: < 2 seconds
- **Screen Load Time**: < 1 second
- **API Response Time**: < 500ms
- **Memory Usage**: < 200MB average
- **Battery Impact**: Minimal battery drain
- **Frame Rate**: 60 FPS for smooth animations

### Performance Benchmarks
- **Cold Start**: < 2 seconds
- **Warm Start**: < 500ms
- **Hot Start**: < 200ms
- **UI Responsiveness**: < 16ms per frame
- **Memory Efficiency**: < 150MB baseline
- **Network Efficiency**: < 1MB data transfer

## Performance Architecture
### Performance Layers
- **UI Layer**: Optimized UI rendering and animations
- **Business Layer**: Efficient business logic processing
- **Data Layer**: Optimized data access and caching
- **Network Layer**: Efficient network operations
- **System Layer**: System resource optimization

### Performance Patterns
- **Lazy Loading**: Load resources only when needed
- **Caching**: Multi-level caching strategy
- **Pagination**: Paginate large datasets
- **Background Processing**: Offload work to background threads
- **Resource Management**: Efficient resource utilization

## UI Performance
### Layout Optimization
- **Flat Layout Hierarchy**: Minimize layout nesting
- **ConstraintLayout**: Use ConstraintLayout for complex layouts
- **View Recycling**: Recycle views in lists and grids
- **Layout Caching**: Cache layout resources
- **Background Inflation**: Inflate layouts in background threads

### Animation Performance
- **Property Animators**: Use property animators for smooth animations
- **Hardware Acceleration**: Enable hardware acceleration
- **Overdraw Reduction**: Minimize overdraw
- **Frame Pacing**: Maintain consistent frame rate
- **Animation Caching**: Cache animation resources

### Memory Management
- **Image Loading**: Efficient image loading with Glide/Picasso
- **Memory Caching**: LRU cache for images and resources
- **Object Pooling**: Pool frequently used objects
- **Memory Monitoring**: Monitor memory usage and leaks
- **Garbage Collection**: Optimize garbage collection

## Network Performance
### Network Optimization
- **Request Batching**: Batch multiple requests
- **Data Compression**: Compress request/response data
- **Caching Strategy**: Implement intelligent caching
- **Connection Pooling**: Use connection pooling
- **Protocol Optimization**: Use efficient protocols (HTTP/2)

### API Performance
- **GraphQL**: Consider GraphQL for efficient data fetching
- **REST Optimization**: Optimize REST API design
- **Pagination**: Implement pagination for large datasets
- **Delta Updates**: Send only changed data
- **WebSockets**: Use WebSockets for real-time updates

### Offline Performance
- **Offline Caching**: Cache data for offline use
- **Sync Optimization**: Optimize data synchronization
- **Conflict Resolution**: Handle data conflicts
- **Background Sync**: Sync data in background
- **Queue Management**: Manage offline operation queues

## Database Performance
### Database Optimization
- **Room Database**: Use Room for efficient database operations
- **Indexing**: Proper database indexing
- **Query Optimization**: Optimize database queries
- **Batch Operations**: Use batch operations
- **Database Migration**: Efficient database migrations

### Data Access Patterns
- **Repository Pattern**: Abstract data access
- **Caching Strategy**: Multi-level caching
- **Lazy Loading**: Load data on demand
- **Prefetching**: Prefetch anticipated data
- **Data Pagination**: Paginate large datasets

## Memory Performance
### Memory Management
- **Memory Profiling**: Regular memory profiling
- **Leak Detection**: Detect and fix memory leaks
- **Object Lifecycle**: Manage object lifecycle properly
- **Resource Cleanup**: Clean up resources promptly
- **Memory Monitoring**: Monitor memory usage

### Memory Optimization
- **Object Pooling**: Pool frequently used objects
- **Memory Caching**: Implement efficient caching
- **Lazy Initialization**: Initialize objects lazily
- **Weak References**: Use weak references where appropriate
- **Memory Budget**: Set memory budgets

## Battery Performance
### Battery Optimization
- **Background Work**: Minimize background work
- **Network Optimization**: Optimize network usage
- **Sensor Usage**: Optimize sensor usage
- **Wake Locks**: Use wake locks sparingly
- **Location Services**: Optimize location services

### Power Management
- **Doze Mode**: Handle Android Doze mode
- **App Standby**: Handle app standby mode
- **Background Limits**: Respect background limits
- **Battery Optimization**: Follow battery optimization best practices
- **Power Profiling**: Profile power consumption

## Performance Monitoring
### Performance Metrics
- **Startup Time**: Monitor app startup time
- **UI Performance**: Track UI rendering performance
- **Memory Usage**: Monitor memory usage
- **Network Performance**: Track network performance
- **Battery Usage**: Monitor battery impact
- **Crash Rate**: Track app crash rate

### Monitoring Tools
- **Android Profiler**: Use Android Studio Profiler
- **Firebase Performance**: Firebase Performance Monitoring
- **Custom Metrics**: Custom performance metrics
- **APM Tools**: Application Performance Monitoring tools
- **Crash Reporting**: Crash reporting and analytics

### Performance Analytics
- **Performance Trends**: Analyze performance trends
- **User Experience**: Monitor user experience metrics
- **Device Performance**: Track performance across devices
- **Network Performance**: Monitor network performance
- **Performance Alerts**: Set up performance alerts

## Performance Testing
### Testing Strategies
- **Performance Testing**: Regular performance testing
- **Load Testing**: Test under heavy load
- **Stress Testing**: Test under extreme conditions
- **Endurance Testing**: Test long-term performance
- **Compatibility Testing**: Test across devices

### Testing Tools
- **Android Profiler**: Built-in performance profiling
- **JUnit Performance**: Performance testing with JUnit
- **Espresso**: UI performance testing
- **Custom Scripts**: Custom performance testing scripts
- **Cloud Testing**: Cloud-based performance testing

### Performance Benchmarks
- **Startup Benchmarks**: App startup benchmarks
- **UI Benchmarks**: UI performance benchmarks
- **Memory Benchmarks**: Memory usage benchmarks
- **Network Benchmarks**: Network performance benchmarks
- **Battery Benchmarks**: Battery usage benchmarks

## Performance Optimization
### Optimization Techniques
- **Code Optimization**: Optimize critical code paths
- **Algorithm Optimization**: Use efficient algorithms
- **Data Structure Optimization**: Use appropriate data structures
- **I/O Optimization**: Optimize I/O operations
- **Thread Optimization**: Optimize threading

### Optimization Tools
- **Android Profiler**: Profile performance bottlenecks
- **Traceview**: Trace method execution
- **Systrace**: Trace system events
- **GPU Inspector**: Inspect GPU performance
- **Memory Analyzer**: Analyze memory usage

### Optimization Process
1. **Performance Analysis**: Identify performance bottlenecks
2. **Benchmarking**: Establish performance baselines
3. **Optimization**: Implement optimizations
4. **Testing**: Verify performance improvements
5. **Monitoring**: Continuously monitor performance

## Performance Best Practices
### Development Best Practices
- **Performance Testing**: Test performance regularly
- **Code Reviews**: Include performance in code reviews
- **Performance Documentation**: Document performance considerations
- **Performance Training**: Train developers on performance
- **Performance Standards**: Establish performance standards

### Design Best Practices
- **Performance-First Design**: Design for performance
- **Scalability**: Design for scalability
- **Efficiency**: Choose efficient algorithms and data structures
- **Resource Management**: Manage resources efficiently
- **User Experience**: Prioritize user experience

### Operations Best Practices
- **Performance Monitoring**: Monitor performance continuously
- **Alerting**: Set up performance alerts
- **Incident Response**: Respond to performance issues
- **Capacity Planning**: Plan for capacity needs
- **Performance Reporting**: Report on performance metrics

## Future Performance Enhancements
### Planned Improvements
- **Kotlin Coroutines**: Enhanced coroutine usage
- **Jetpack Compose**: Migrate to Jetpack Compose
- **Advanced Caching**: Implement advanced caching strategies
- **Machine Learning**: Use ML for performance optimization
- **Cloud Integration**: Enhanced cloud performance integration

### Performance Innovation
- **5G Optimization**: Optimize for 5G networks
- **Edge Computing**: Edge computing integration
- **Server-Driven UI**: Server-driven UI performance
- **Predictive Loading**: Predictive resource loading
- **Adaptive Performance**: Adaptive performance optimization

*Generated on {{ generated_at }}*
"""

    # Template filters
    def highlight_code(self, code: str, language: str) -> str:
        """Highlight code syntax"""
        # This would use a syntax highlighting library
        return f"```{language}\n{code}\n```"

    def format_date(self, date_str: str, format_str: str = "%Y-%m-%d") -> str:
        """Format date string"""
        try:
            date = datetime.fromisoformat(date_str)
            return date.strftime(format_str)
        except:
            return date_str

    def truncate_text(self, text: str, length: int = 100) -> str:
        """Truncate text to specified length"""
        if len(text) <= length:
            return text
        return text[:length-3] + "..."

    def generate_comprehensive_documentation(self, source_data: Dict[str, Any]) -> Dict[str, DocumentationResult]:
        """Generate comprehensive documentation for all categories"""
        results = {}

        # Generate documentation for each category
        for category in DocumentationCategory:
            if category != DocumentationCategory.SECURITY and category != DocumentationCategory.PERFORMANCE:
                request = DocumentationRequest(
                    id=f"{category.value}_{int(datetime.now().timestamp())}",
                    category=category,
                    format=DocumentationFormat.MARKDOWN,
                    level=DocumentationLevel.DETAILED,
                    title=f"{category.value.title()} Documentation",
                    description=f"Comprehensive {category.value} documentation",
                    source_data=source_data,
                    output_path=os.path.join(self.config["output_directory"], category.value, f"{category.value}_documentation.md"),
                    template_path=f"{category.value}_documentation.md.j2"
                )

                result = self.generate_documentation(request)
                results[category.value] = result

        return results

async def main():
    """Main function to run the comprehensive documentation generators"""
    print("FibreField Tech Android App - Comprehensive Documentation Generators")
    print("=" * 70)

    # Initialize generator
    generator = AdvancedDocumentationGenerator()

    # Display initial status
    print(f"Output directory: {generator.config['output_directory']}")
    print(f"Code examples: {len(generator.code_examples)}")
    print(f"Diagrams: {len(generator.diagrams)}")
    print(f"Template directory: {generator.config['template_directory']}")

    # Example usage
    source_data = {
        "features_implemented": ["Feature 1", "Feature 2"],
        "architectural_decisions": [{"title": "Decision 1", "description": "Description"}],
        "components": [{"name": "Component 1", "type": "Service"}],
        "files_created": ["app/src/main/java/Example.kt"],
        "test_results": {"overall": {"coverage": 95.0}},
        "quality_metrics": {"performance": 85.0}
    }

    # Generate comprehensive documentation
    results = generator.generate_comprehensive_documentation(source_data)

    print("\n📚 Comprehensive documentation generated!")
    for category, result in results.items():
        status = "✅" if result.success else "❌"
        print(f"{status} {category.title()}: {result.output_path}")

    print(f"\n📊 Results:")
    print(f"- Total documentation: {len(results)}")
    print(f"- Successful: {sum(1 for r in results.values() if r.success)}")
    print(f"- Failed: {sum(1 for r in results.values() if not r.success)}")

if __name__ == "__main__":
    asyncio.run(main())