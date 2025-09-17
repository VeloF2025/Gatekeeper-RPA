#!/usr/bin/env python3
"""
FibreField Tech Android App - TDD Integration System
Real-time integration with TDD parallel execution system for documentation generation.
"""

import asyncio
import json
import logging
import os
import sys
import time
import yaml
import sqlite3
import requests
import websockets
import threading
import queue
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional, Tuple, Callable
from dataclasses import dataclass, field, asdict
from enum import Enum
from pathlib import Path
import subprocess
import uuid

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('tdd_integration.log'),
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)

class IntegrationEventType(Enum):
    PHASE_COMPLETED = "phase_completed"
    TEST_PASSED = "test_passed"
    TEST_FAILED = "test_failed"
    QUALITY_GATE_PASSED = "quality_gate_passed"
    QUALITY_GATE_FAILED = "quality_gate_failed"
    DOCUMENTATION_REQUIRED = "documentation_required"
    AGENT_ASSIGNED = "agent_assigned"
    TASK_COMPLETED = "task_completed"
    SYSTEM_ERROR = "system_error"

class IntegrationStatus(Enum):
    CONNECTED = "connected"
    DISCONNECTED = "disconnected"
    ERROR = "error"
    SYNCING = "syncing"

@dataclass
class IntegrationEvent:
    id: str
    event_type: IntegrationEventType
    source_system: str
    timestamp: datetime
    data: Dict[str, Any]
    processed: bool = False
    processed_at: Optional[datetime] = None

@dataclass
class TDDSystemConfig:
    api_base_url: str = "http://localhost:8081"
    websocket_url: str = "ws://localhost:8081/ws"
    api_key: str = ""
    timeout: int = 30
    retry_attempts: int = 3
    retry_delay: float = 1.0
    max_events_per_batch: int = 100
    event_retention_days: int = 30

class TDDIntegrationSystem:
    """Real-time integration system for TDD parallel execution"""

    def __init__(self, config_path: str = None):
        self.config = self.load_config(config_path)
        self.status = IntegrationStatus.DISCONNECTED
        self.event_queue = queue.Queue()
        self.event_handlers: Dict[IntegrationEventType, List[Callable]] = {}
        self.active_connections: Dict[str, Any] = {}
        self.event_cache: List[IntegrationEvent] = []
        self.database_path = "tdd_integration.db"
        self.running = False

        # Initialize system
        self.initialize_database()
        self.load_event_handlers()
        self.connect_to_tdd_system()

        logger.info("TDD Integration System initialized")

    def load_config(self, config_path: str) -> TDDSystemConfig:
        """Load configuration from file"""
        if config_path and os.path.exists(config_path):
            with open(config_path, 'r') as f:
                config_data = yaml.safe_load(f)
                return TDDSystemConfig(**config_data)
        else:
            return TDDSystemConfig()

    def initialize_database(self):
        """Initialize SQLite database for integration tracking"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        # Create integration events table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS integration_events (
                id TEXT PRIMARY KEY,
                event_type TEXT NOT NULL,
                source_system TEXT NOT NULL,
                timestamp TEXT NOT NULL,
                data TEXT NOT NULL,
                processed BOOLEAN DEFAULT FALSE,
                processed_at TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')

        # Create system connections table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS system_connections (
                id TEXT PRIMARY KEY,
                system_name TEXT NOT NULL,
                connection_type TEXT NOT NULL,
                status TEXT NOT NULL,
                last_heartbeat TEXT,
                connection_data TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')

        # Create integration metrics table
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS integration_metrics (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                metric_name TEXT NOT NULL,
                metric_value REAL NOT NULL,
                timestamp TEXT NOT NULL,
                metadata TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')

        conn.commit()
        conn.close()
        logger.info("Database initialized successfully")

    def load_event_handlers(self):
        """Load event handlers for different event types"""
        self.event_handlers = {
            IntegrationEventType.PHASE_COMPLETED: [self.handle_phase_completed],
            IntegrationEventType.TEST_PASSED: [self.handle_test_passed],
            IntegrationEventType.TEST_FAILED: [self.handle_test_failed],
            IntegrationEventType.QUALITY_GATE_PASSED: [self.handle_quality_gate_passed],
            IntegrationEventType.QUALITY_GATE_FAILED: [self.handle_quality_gate_failed],
            IntegrationEventType.DOCUMENTATION_REQUIRED: [self.handle_documentation_required],
            IntegrationEventType.AGENT_ASSIGNED: [self.handle_agent_assigned],
            IntegrationEventType.TASK_COMPLETED: [self.handle_task_completed],
            IntegrationEventType.SYSTEM_ERROR: [self.handle_system_error]
        }

    def connect_to_tdd_system(self):
        """Connect to TDD system"""
        try:
            # Test API connection
            response = requests.get(
                f"{self.config.api_base_url}/health",
                timeout=self.config.timeout
            )

            if response.status_code == 200:
                self.status = IntegrationStatus.CONNECTED
                logger.info("Connected to TDD system")
                self.start_event_listeners()
            else:
                self.status = IntegrationStatus.ERROR
                logger.error(f"Failed to connect to TDD system: {response.status_code}")

        except Exception as e:
            self.status = IntegrationStatus.ERROR
            logger.error(f"Error connecting to TDD system: {e}")
            # Retry connection
            self.schedule_reconnection()

    def schedule_reconnection(self):
        """Schedule reconnection attempt"""
        def reconnect():
            time.sleep(self.config.retry_delay)
            if self.status != IntegrationStatus.CONNECTED:
                self.connect_to_tdd_system()

        threading.Thread(target=reconnect, daemon=True).start()

    def start_event_listeners(self):
        """Start event listeners"""
        # Start WebSocket listener
        threading.Thread(target=self.websocket_listener, daemon=True).start()

        # Start HTTP polling for events
        threading.Thread(target=self.http_poller, daemon=True).start()

        # Start event processor
        threading.Thread(target=self.event_processor, daemon=True).start()

    def websocket_listener(self):
        """Listen for events via WebSocket"""
        while self.running:
            try:
                if self.status == IntegrationStatus.CONNECTED:
                    async def listen_websocket():
                        async with websockets.connect(self.config.websocket_url) as websocket:
                            self.active_connections["websocket"] = websocket
                            logger.info("WebSocket connection established")

                            while self.running:
                                try:
                                    message = await websocket.recv()
                                    event_data = json.loads(message)
                                    self.handle_incoming_event(event_data)
                                except websockets.exceptions.ConnectionClosed:
                                    break
                                except Exception as e:
                                    logger.error(f"WebSocket error: {e}")
                                    break

                    asyncio.run(listen_websocket())

            except Exception as e:
                logger.error(f"WebSocket listener error: {e}")
                self.status = IntegrationStatus.ERROR
                self.schedule_reconnection()

            time.sleep(5)  # Wait before retrying

    def http_poller(self):
        """Poll for events via HTTP"""
        while self.running:
            try:
                if self.status == IntegrationStatus.CONNECTED:
                    response = requests.get(
                        f"{self.config.api_base_url}/events/recent",
                        timeout=self.config.timeout,
                        headers={"Authorization": f"Bearer {self.config.api_key}"}
                    )

                    if response.status_code == 200:
                        events = response.json()
                        for event_data in events:
                            self.handle_incoming_event(event_data)

            except Exception as e:
                logger.error(f"HTTP poller error: {e}")

            time.sleep(10)  # Poll every 10 seconds

    def event_processor(self):
        """Process queued events"""
        while self.running:
            try:
                # Process events from queue
                events_to_process = []

                # Collect events from queue
                while not self.event_queue.empty() and len(events_to_process) < self.config.max_events_per_batch:
                    events_to_process.append(self.event_queue.get())

                # Process events
                for event in events_to_process:
                    self.process_event(event)

                # Clean up old events from cache
                self.cleanup_old_events()

            except Exception as e:
                logger.error(f"Event processor error: {e}")

            time.sleep(1)  # Process events every second

    def handle_incoming_event(self, event_data: Dict[str, Any]):
        """Handle incoming event from TDD system"""
        try:
            event = IntegrationEvent(
                id=event_data.get("id", str(uuid.uuid4())),
                event_type=IntegrationEventType(event_data["event_type"]),
                source_system=event_data.get("source_system", "tdd_system"),
                timestamp=datetime.fromisoformat(event_data["timestamp"]),
                data=event_data.get("data", {})
            )

            # Add to queue
            self.event_queue.put(event)

            # Cache event
            self.event_cache.append(event)
            self.save_event_to_db(event)

            logger.debug(f"Received event: {event.event_type.value}")

        except Exception as e:
            logger.error(f"Error handling incoming event: {e}")

    def process_event(self, event: IntegrationEvent):
        """Process a single event"""
        try:
            # Get handlers for this event type
            handlers = self.event_handlers.get(event.event_type, [])

            # Execute handlers
            for handler in handlers:
                try:
                    handler(event)
                except Exception as e:
                    logger.error(f"Error in event handler {handler.__name__}: {e}")

            # Mark as processed
            event.processed = True
            event.processed_at = datetime.now()
            self.update_event_in_db(event)

            # Record metrics
            self.record_metric("events_processed", 1.0, {"event_type": event.event_type.value})

        except Exception as e:
            logger.error(f"Error processing event {event.id}: {e}")

    def handle_phase_completed(self, event: IntegrationEvent):
        """Handle phase completion event"""
        phase_data = event.data
        phase_id = phase_data.get("phase_id")
        phase_name = phase_data.get("phase_name")

        logger.info(f"Phase completed: {phase_name} ({phase_id})")

        # Trigger documentation generation
        self.trigger_documentation_generation(phase_id, phase_name, phase_data)

        # Update project metrics
        self.record_metric("phases_completed", 1.0, {"phase_id": phase_id})

    def handle_test_passed(self, event: IntegrationEvent):
        """Handle test passed event"""
        test_data = event.data
        test_id = test_data.get("test_id")
        test_name = test_data.get("test_name")

        logger.info(f"Test passed: {test_name} ({test_id})")

        # Update test metrics
        self.record_metric("tests_passed", 1.0, {"test_id": test_id})

        # Check if all tests for a feature have passed
        self.check_feature_completion(test_data)

    def handle_test_failed(self, event: IntegrationEvent):
        """Handle test failed event"""
        test_data = event.data
        test_id = test_data.get("test_id")
        test_name = test_data.get("test_name")
        error_message = test_data.get("error_message")

        logger.warning(f"Test failed: {test_name} ({test_id}) - {error_message}")

        # Update test metrics
        self.record_metric("tests_failed", 1.0, {"test_id": test_id})

        # Notify about test failure
        self.notify_test_failure(test_data)

    def handle_quality_gate_passed(self, event: IntegrationEvent):
        """Handle quality gate passed event"""
        gate_data = event.data
        gate_name = gate_data.get("gate_name")
        gate_score = gate_data.get("score")

        logger.info(f"Quality gate passed: {gate_name} (score: {gate_score})")

        # Update quality metrics
        self.record_metric("quality_gates_passed", 1.0, {"gate_name": gate_name})

        # Check if all quality gates have passed
        self.check_quality_gates_completion(gate_data)

    def handle_quality_gate_failed(self, event: IntegrationEvent):
        """Handle quality gate failed event"""
        gate_data = event.data
        gate_name = gate_data.get("gate_name")
        failure_reason = gate_data.get("failure_reason")

        logger.warning(f"Quality gate failed: {gate_name} - {failure_reason}")

        # Update quality metrics
        self.record_metric("quality_gates_failed", 1.0, {"gate_name": gate_name})

        # Notify about quality gate failure
        self.notify_quality_gate_failure(gate_data)

    def handle_documentation_required(self, event: IntegrationEvent):
        """Handle documentation required event"""
        doc_data = event.data
        doc_type = doc_data.get("doc_type")
        doc_id = doc_data.get("doc_id")

        logger.info(f"Documentation required: {doc_type} ({doc_id})")

        # Queue documentation generation
        self.queue_documentation_generation(doc_data)

        # Update documentation metrics
        self.record_metric("documentation_required", 1.0, {"doc_type": doc_type})

    def handle_agent_assigned(self, event: IntegrationEvent):
        """Handle agent assigned event"""
        agent_data = event.data
        agent_name = agent_data.get("agent_name")
        task_id = agent_data.get("task_id")

        logger.info(f"Agent assigned: {agent_name} to task {task_id}")

        # Update agent metrics
        self.record_metric("agents_assigned", 1.0, {"agent_name": agent_name})

        # Track agent assignment
        self.track_agent_assignment(agent_data)

    def handle_task_completed(self, event: IntegrationEvent):
        """Handle task completed event"""
        task_data = event.data
        task_id = task_data.get("task_id")
        task_name = task_data.get("task_name")
        quality_score = task_data.get("quality_score")

        logger.info(f"Task completed: {task_name} ({task_id}) - Quality: {quality_score}")

        # Update task metrics
        self.record_metric("tasks_completed", 1.0, {"task_id": task_id})
        self.record_metric("quality_score", quality_score, {"task_id": task_id})

        # Check if phase is complete
        self.check_phase_completion(task_data)

    def handle_system_error(self, event: IntegrationEvent):
        """Handle system error event"""
        error_data = event.data
        error_message = error_data.get("error_message")
        error_type = error_data.get("error_type")

        logger.error(f"System error: {error_type} - {error_message}")

        # Update error metrics
        self.record_metric("system_errors", 1.0, {"error_type": error_type})

        # Notify about system error
        self.notify_system_error(error_data)

    def trigger_documentation_generation(self, phase_id: str, phase_name: str, phase_data: Dict[str, Any]):
        """Trigger documentation generation for completed phase"""
        try:
            # Import documentation generation agent
            from documentation_generation_agent import DocumentationGenerationAgent

            # Initialize documentation agent
            doc_agent = DocumentationGenerationAgent()

            # Create phase completion event
            phase_completion = {
                "phase_id": phase_id,
                "phase_name": phase_name,
                "completion_time": datetime.now().isoformat(),
                "test_results": phase_data.get("test_results", {}),
                "quality_metrics": phase_data.get("quality_metrics", {}),
                "features_implemented": phase_data.get("features_implemented", []),
                "architectural_decisions": phase_data.get("architectural_decisions", []),
                "files_created": phase_data.get("files_created", []),
                "documentation_requirements": phase_data.get("documentation_requirements", [])
            }

            # Handle phase completion
            doc_agent.handle_phase_completion(phase_completion)

            logger.info(f"Documentation generation triggered for phase: {phase_name}")

        except ImportError:
            logger.error("Documentation generation agent not found")
        except Exception as e:
            logger.error(f"Error triggering documentation generation: {e}")

    def queue_documentation_generation(self, doc_data: Dict[str, Any]):
        """Queue documentation generation task"""
        try:
            # Send documentation generation request to TDD system
            response = requests.post(
                f"{self.config.api_base_url}/documentation/queue",
                json=doc_data,
                timeout=self.config.timeout,
                headers={"Authorization": f"Bearer {self.config.api_key}"}
            )

            if response.status_code == 200:
                logger.info("Documentation generation queued successfully")
            else:
                logger.error(f"Failed to queue documentation generation: {response.status_code}")

        except Exception as e:
            logger.error(f"Error queueing documentation generation: {e}")

    def check_feature_completion(self, test_data: Dict[str, Any]):
        """Check if all tests for a feature have passed"""
        feature_id = test_data.get("feature_id")
        if feature_id:
            try:
                # Query TDD system for feature test status
                response = requests.get(
                    f"{self.config.api_base_url}/features/{feature_id}/test-status",
                    timeout=self.config.timeout,
                    headers={"Authorization": f"Bearer {self.config.api_key}"}
                )

                if response.status_code == 200:
                    status_data = response.json()
                    if status_data.get("all_tests_passed"):
                        logger.info(f"All tests passed for feature: {feature_id}")
                        # Trigger feature completion event
                        self.trigger_feature_completion(feature_id, status_data)

            except Exception as e:
                logger.error(f"Error checking feature completion: {e}")

    def check_phase_completion(self, task_data: Dict[str, Any]):
        """Check if phase is complete after task completion"""
        phase_id = task_data.get("phase_id")
        if phase_id:
            try:
                # Query TDD system for phase completion status
                response = requests.get(
                    f"{self.config.api_base_url}/phases/{phase_id}/completion-status",
                    timeout=self.config.timeout,
                    headers={"Authorization": f"Bearer {self.config.api_key}"}
                )

                if response.status_code == 200:
                    status_data = response.json()
                    if status_data.get("is_complete"):
                        logger.info(f"Phase completed: {phase_id}")
                        # Trigger phase completion event
                        self.trigger_phase_completion(phase_id, status_data)

            except Exception as e:
                logger.error(f"Error checking phase completion: {e}")

    def check_quality_gates_completion(self, gate_data: Dict[str, Any]):
        """Check if all quality gates have passed"""
        phase_id = gate_data.get("phase_id")
        if phase_id:
            try:
                # Query TDD system for quality gates status
                response = requests.get(
                    f"{self.config.api_base_url}/phases/{phase_id}/quality-gates",
                    timeout=self.config.timeout,
                    headers={"Authorization": f"Bearer {self.config.api_key}"}
                )

                if response.status_code == 200:
                    gates_data = response.json()
                    all_passed = all(gate["status"] == "passed" for gate in gates_data.get("gates", []))
                    if all_passed:
                        logger.info(f"All quality gates passed for phase: {phase_id}")
                        # Trigger quality gates completion event
                        self.trigger_quality_gates_completion(phase_id, gates_data)

            except Exception as e:
                logger.error(f"Error checking quality gates completion: {e}")

    def trigger_feature_completion(self, feature_id: str, status_data: Dict[str, Any]):
        """Trigger feature completion event"""
        event_data = {
            "event_type": "feature_completed",
            "source_system": "tdd_integration",
            "timestamp": datetime.now().isoformat(),
            "data": {
                "feature_id": feature_id,
                "status_data": status_data
            }
        }

        self.send_event_to_tdd_system(event_data)

    def trigger_phase_completion(self, phase_id: str, status_data: Dict[str, Any]):
        """Trigger phase completion event"""
        event_data = {
            "event_type": "phase_completed",
            "source_system": "tdd_integration",
            "timestamp": datetime.now().isoformat(),
            "data": {
                "phase_id": phase_id,
                "status_data": status_data
            }
        }

        self.send_event_to_tdd_system(event_data)

    def trigger_quality_gates_completion(self, phase_id: str, gates_data: Dict[str, Any]):
        """Trigger quality gates completion event"""
        event_data = {
            "event_type": "quality_gates_completed",
            "source_system": "tdd_integration",
            "timestamp": datetime.now().isoformat(),
            "data": {
                "phase_id": phase_id,
                "gates_data": gates_data
            }
        }

        self.send_event_to_tdd_system(event_data)

    def send_event_to_tdd_system(self, event_data: Dict[str, Any]):
        """Send event to TDD system"""
        try:
            response = requests.post(
                f"{self.config.api_base_url}/events",
                json=event_data,
                timeout=self.config.timeout,
                headers={"Authorization": f"Bearer {self.config.api_key}"}
            )

            if response.status_code == 200:
                logger.info("Event sent to TDD system successfully")
            else:
                logger.error(f"Failed to send event to TDD system: {response.status_code}")

        except Exception as e:
            logger.error(f"Error sending event to TDD system: {e}")

    def notify_test_failure(self, test_data: Dict[str, Any]):
        """Notify about test failure"""
        # This would integrate with notification systems
        logger.warning(f"Test failure notification: {test_data.get('test_name')}")

    def notify_quality_gate_failure(self, gate_data: Dict[str, Any]):
        """Notify about quality gate failure"""
        # This would integrate with notification systems
        logger.warning(f"Quality gate failure notification: {gate_data.get('gate_name')}")

    def notify_system_error(self, error_data: Dict[str, Any]):
        """Notify about system error"""
        # This would integrate with notification systems
        logger.error(f"System error notification: {error_data.get('error_message')}")

    def track_agent_assignment(self, agent_data: Dict[str, Any]):
        """Track agent assignment"""
        # This would update agent tracking metrics
        logger.info(f"Agent assignment tracked: {agent_data.get('agent_name')}")

    def save_event_to_db(self, event: IntegrationEvent):
        """Save event to database"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        cursor.execute('''
            INSERT OR REPLACE INTO integration_events (
                id, event_type, source_system, timestamp, data,
                processed, processed_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        ''', (
            event.id, event.event_type.value, event.source_system,
            event.timestamp.isoformat(), json.dumps(event.data),
            event.processed, event.processed_at.isoformat() if event.processed_at else None
        ))

        conn.commit()
        conn.close()

    def update_event_in_db(self, event: IntegrationEvent):
        """Update event in database"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        cursor.execute('''
            UPDATE integration_events
            SET processed = ?, processed_at = ?
            WHERE id = ?
        ''', (
            event.processed, event.processed_at.isoformat() if event.processed_at else None,
            event.id
        ))

        conn.commit()
        conn.close()

    def record_metric(self, metric_name: str, value: float, metadata: Dict[str, Any] = None):
        """Record integration metric"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        cursor.execute('''
            INSERT INTO integration_metrics (metric_name, metric_value, timestamp, metadata)
            VALUES (?, ?, ?, ?)
        ''', (
            metric_name, value, datetime.now().isoformat(),
            json.dumps(metadata) if metadata else None
        ))

        conn.commit()
        conn.close()

    def cleanup_old_events(self):
        """Clean up old events from cache"""
        cutoff_date = datetime.now() - timedelta(days=self.config.event_retention_days)
        self.event_cache = [event for event in self.event_cache if event.timestamp > cutoff_date]

    def get_integration_status(self) -> Dict[str, Any]:
        """Get current integration status"""
        return {
            "status": self.status.value,
            "active_connections": len(self.active_connections),
            "queued_events": self.event_queue.qsize(),
            "cached_events": len(self.event_cache),
            "uptime": time.time() - getattr(self, 'start_time', time.time())
        }

    def get_metrics_summary(self) -> Dict[str, Any]:
        """Get integration metrics summary"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        # Get recent metrics
        cursor.execute('''
            SELECT metric_name, COUNT(*) as count, AVG(metric_value) as avg_value
            FROM integration_metrics
            WHERE timestamp > datetime('now', '-1 hour')
            GROUP BY metric_name
        ''')

        metrics = {}
        for row in cursor.fetchall():
            metrics[row[0]] = {
                "count": row[1],
                "average": row[2]
            }

        conn.close()

        return metrics

    def start_integration(self):
        """Start the integration system"""
        self.running = True
        self.start_time = time.time()
        logger.info("TDD Integration System started")

        # Start background processes
        self.start_event_listeners()

    def stop_integration(self):
        """Stop the integration system"""
        self.running = False
        logger.info("TDD Integration System stopped")

        # Close active connections
        for connection in self.active_connections.values():
            try:
                if hasattr(connection, 'close'):
                    connection.close()
            except Exception as e:
                logger.error(f"Error closing connection: {e}")

        self.active_connections.clear()

async def main():
    """Main function to run the TDD integration system"""
    print("FibreField Tech Android App - TDD Integration System")
    print("=" * 70)

    # Initialize integration system
    integration = TDDIntegrationSystem()

    # Display initial status
    status = integration.get_integration_status()
    print(f"Status: {status['status']}")
    print(f"Connections: {status['active_connections']}")
    print(f"Queued events: {status['queued_events']}")

    # Start integration
    integration.start_integration()

    print("\n🔗 TDD Integration System started!")
    print("📡 Listening for events from TDD system")
    print("🔄 Processing events and triggering documentation generation")
    print("📋 Press Ctrl+C to stop integration")

    try:
        # Keep the system running
        while integration.running:
            await asyncio.sleep(1)

            # Print status updates every 30 seconds
            if int(time.time()) % 30 == 0:
                status = integration.get_integration_status()
                metrics = integration.get_metrics_summary()
                print(f"\n📊 Status: {status['status']} | "
                      f"Events: {status['queued_events']} queued | "
                      f"Metrics: {len(metrics)} types")

    except KeyboardInterrupt:
        print("\n⏹️  Stopping TDD integration system...")
        integration.stop_integration()
        print("✅ TDD integration system stopped")

if __name__ == "__main__":
    asyncio.run(main())