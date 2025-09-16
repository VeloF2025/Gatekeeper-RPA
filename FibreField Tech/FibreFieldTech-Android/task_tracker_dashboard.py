#!/usr/bin/env python3
"""
FibreField Tech Android App - Task Tracker Dashboard
Real-time monitoring dashboard for parallel execution system
"""

import asyncio
import json
import logging
from datetime import datetime, timedelta
from typing import Dict, List, Any
from dataclasses import dataclass, asdict
from enum import Enum
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
import numpy as np
from pathlib import Path
import webbrowser
import threading
import time
from http.server import HTTPServer, SimpleHTTPRequestHandler
import socketserver
import os

# Import the parallel execution system
from parallel_execution_system import ParallelExecutionSystem, TaskStatus

class DashboardConfig:
    def __init__(self):
        self.port = 8080
        self.refresh_interval = 5  # seconds
        self.history_file = "progress_history.json"
        self.dashboard_dir = "dashboard"
        self.max_history_points = 100

class TaskTrackerDashboard:
    def __init__(self, execution_system: ParallelExecutionSystem):
        self.execution_system = execution_system
        self.config = DashboardConfig()
        self.progress_history = []
        self.start_time = datetime.now()

        # Create dashboard directory
        Path(self.config.dashboard_dir).mkdir(exist_ok=True)

        # Setup logging
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)

    def generate_progress_chart(self):
        """Generate progress burndown chart"""
        plt.figure(figsize=(12, 8))

        # Prepare data
        times = [entry['timestamp'] for entry in self.progress_history]
        completed = [entry['completed_tasks'] for entry in self.progress_history]
        total = [entry['total_tasks'] for entry in self.progress_history]

        # Create subplots
        fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(15, 10))
        fig.suptitle('FibreField Tech Android App - Progress Dashboard', fontsize=16, fontweight='bold')

        # Progress over time
        ax1.plot(times, completed, 'g-', linewidth=2, label='Completed')
        ax1.plot(times, total, 'b--', linewidth=2, label='Total')
        ax1.set_title('Task Progress Over Time')
        ax1.set_xlabel('Time')
        ax1.set_ylabel('Number of Tasks')
        ax1.legend()
        ax1.grid(True, alpha=0.3)

        # Progress percentage
        progress_percent = [c/t*100 if t > 0 else 0 for c, t in zip(completed, total)]
        ax2.fill_between(times, progress_percent, alpha=0.7, color='cyan')
        ax2.set_title('Overall Progress Percentage')
        ax2.set_xlabel('Time')
        ax2.set_ylabel('Progress (%)')
        ax2.set_ylim(0, 100)
        ax2.grid(True, alpha=0.3)

        # Agent utilization
        agent_util = [entry['agent_utilization'] for entry in self.progress_history]
        ax3.plot(times, agent_util, 'orange', linewidth=2, marker='o')
        ax3.set_title('Agent Utilization')
        ax3.set_xlabel('Time')
        ax3.set_ylabel('Utilization (%)')
        ax3.set_ylim(0, 100)
        ax3.grid(True, alpha=0.3)

        # Quality scores
        quality_scores = [entry['average_quality_score'] for entry in self.progress_history]
        ax4.plot(times, quality_scores, 'purple', linewidth=2, marker='s')
        ax4.set_title('Average Quality Score')
        ax4.set_xlabel('Time')
        ax4.set_ylabel('Quality Score')
        ax4.set_ylim(0, 1)
        ax4.grid(True, alpha=0.3)

        # Format x-axis
        for ax in [ax1, ax2, ax3, ax4]:
            ax.xaxis.set_major_formatter(mdates.DateFormatter('%H:%M'))
            ax.xaxis.set_major_locator(mdates.MinuteLocator(interval=10))
            plt.setp(ax.xaxis.get_majorticklabels(), rotation=45)

        plt.tight_layout()
        plt.savefig(f'{self.config.dashboard_dir}/progress_chart.png', dpi=150, bbox_inches='tight')
        plt.close()

    def generate_quality_radar(self):
        """Generate quality metrics radar chart"""
        progress = self.execution_system.get_progress_summary()

        # Quality metrics
        metrics = {
            'Quality Score': progress['average_quality_score'] * 100,
            'Progress %': progress['progress_percentage'],
            'Agent Utilization': progress['agent_utilization'],
            'Test Coverage': 95,  # Target
            'Performance': 90,  # Target
            'Reliability': 95   # Target
        }

        # Create radar chart
        angles = np.linspace(0, 2 * np.pi, len(metrics), endpoint=False).tolist()
        values = list(metrics.values())
        values += values[:1]  # Complete the circle
        angles += angles[:1]

        fig, ax = plt.subplots(figsize=(10, 10), subplot_kw=dict(projection='polar'))
        ax.plot(angles, values, 'o-', linewidth=2, color='cyan', markersize=8)
        ax.fill(angles, values, alpha=0.25, color='cyan')
        ax.set_xticks(angles[:-1])
        ax.set_xticklabels(metrics.keys())
        ax.set_ylim(0, 100)
        ax.set_title('Quality Metrics Radar', size=16, weight='bold', pad=20)
        ax.grid(True)

        # Add target zones
        ax.fill_between(angles, 80, 100, alpha=0.1, color='green', label='Target Zone')
        ax.fill_between(angles, 60, 80, alpha=0.1, color='yellow', label='Acceptable Zone')
        ax.fill_between(angles, 0, 60, alpha=0.1, color='red', label='Needs Improvement')
        ax.legend(loc='upper right', bbox_to_anchor=(1.3, 1.0))

        plt.savefig(f'{self.config.dashboard_dir}/quality_radar.png', dpi=150, bbox_inches='tight')
        plt.close()

    def generate_agent_status_chart(self):
        """Generate agent status visualization"""
        agents = self.execution_system.agents

        # Prepare data
        agent_names = list(agents.keys())
        tasks_completed = [agent.tasks_completed for agent in agents.values()]
        quality_scores = [agent.quality_score * 100 for agent in agents.values()]

        fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(15, 6))

        # Tasks completed per agent
        bars1 = ax1.bar(agent_names, tasks_completed, color='cyan', alpha=0.7)
        ax1.set_title('Tasks Completed by Agent')
        ax1.set_xlabel('Agent')
        ax1.set_ylabel('Tasks Completed')
        ax1.tick_params(axis='x', rotation=45)

        # Add value labels on bars
        for bar in bars1:
            height = bar.get_height()
            ax1.text(bar.get_x() + bar.get_width()/2., height,
                    f'{int(height)}', ha='center', va='bottom')

        # Quality scores per agent
        bars2 = ax2.bar(agent_names, quality_scores, color='orange', alpha=0.7)
        ax2.set_title('Quality Scores by Agent')
        ax2.set_xlabel('Agent')
        ax2.set_ylabel('Quality Score (%)')
        ax2.set_ylim(0, 100)
        ax2.tick_params(axis='x', rotation=45)

        # Add value labels on bars
        for bar in bars2:
            height = bar.get_height()
            ax2.text(bar.get_x() + bar.get_width()/2., height,
                    f'{height:.1f}%', ha='center', va='bottom')

        plt.tight_layout()
        plt.savefig(f'{self.config.dashboard_dir}/agent_status.png', dpi=150, bbox_inches='tight')
        plt.close()

    def generate_html_dashboard(self):
        """Generate HTML dashboard"""
        progress = self.execution_system.get_progress_summary()

        # Calculate estimated completion time
        if progress['progress_percentage'] > 0:
            elapsed_time = progress['elapsed_time']
            estimated_total_time = elapsed_time / (progress['progress_percentage'] / 100)
            remaining_time = estimated_total_time - elapsed_time
            eta = datetime.now() + timedelta(hours=remaining_time)
        else:
            eta = "Calculating..."

        # Generate HTML
        html = f"""
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FibreField Tech Android App - Task Tracker Dashboard</title>
    <style>
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background: linear-gradient(135deg, #0a0e27 0%, #1a1f3a 100%);
            color: white;
            min-height: 100vh;
        }}
        .container {{
            max-width: 1400px;
            margin: 0 auto;
        }}
        .header {{
            text-align: center;
            margin-bottom: 30px;
            padding: 20px;
            background: rgba(0, 212, 255, 0.1);
            border-radius: 15px;
            border: 1px solid rgba(0, 212, 255, 0.3);
        }}
        .header h1 {{
            margin: 0;
            color: #00d4ff;
            font-size: 2.5em;
            font-weight: bold;
        }}
        .status-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        .status-card {{
            background: rgba(255, 255, 255, 0.1);
            padding: 20px;
            border-radius: 10px;
            border: 1px solid rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
        }}
        .status-card h3 {{
            margin: 0 0 15px 0;
            color: #00d4ff;
            font-size: 1.3em;
        }}
        .metric {{
            display: flex;
            justify-content: space-between;
            margin: 10px 0;
            padding: 8px 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }}
        .metric:last-child {{
            border-bottom: none;
        }}
        .metric-value {{
            font-weight: bold;
            color: #00ff88;
        }}
        .progress-bar {{
            width: 100%;
            height: 20px;
            background: rgba(255, 255, 255, 0.2);
            border-radius: 10px;
            overflow: hidden;
            margin: 10px 0;
        }}
        .progress-fill {{
            height: 100%;
            background: linear-gradient(90deg, #00d4ff, #00ff88);
            width: {progress['progress_percentage']}%;
            transition: width 0.5s ease;
        }}
        .charts-section {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        .chart-container {{
            background: rgba(255, 255, 255, 0.1);
            padding: 20px;
            border-radius: 10px;
            border: 1px solid rgba(255, 255, 255, 0.2);
            text-align: center;
        }}
        .chart-container img {{
            max-width: 100%;
            height: auto;
            border-radius: 5px;
        }}
        .agent-list {{
            background: rgba(255, 255, 255, 0.1);
            padding: 20px;
            border-radius: 10px;
            border: 1px solid rgba(255, 255, 255, 0.2);
        }}
        .agent-item {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }}
        .agent-status {{
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: bold;
        }}
        .status-available {{ background: #00ff88; color: #000; }}
        .status-working {{ background: #ffaa00; color: #000; }}
        .status-offline {{ background: #ff3b30; color: #fff; }}
        .footer {{
            text-align: center;
            margin-top: 30px;
            padding: 20px;
            background: rgba(0, 212, 255, 0.1);
            border-radius: 10px;
            border: 1px solid rgba(0, 212, 255, 0.3);
        }}
        .last-update {{
            font-size: 0.9em;
            color: rgba(255, 255, 255, 0.7);
        }}
    </style>
    <meta http-equiv="refresh" content="{self.config.refresh_interval}">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚀 FibreField Tech Android App</h1>
            <p>Parallel Execution System Dashboard</p>
        </div>

        <div class="status-grid">
            <div class="status-card">
                <h3>📊 Overall Progress</h3>
                <div class="progress-bar">
                    <div class="progress-fill"></div>
                </div>
                <div class="metric">
                    <span>Progress:</span>
                    <span class="metric-value">{progress['progress_percentage']:.1f}%</span>
                </div>
                <div class="metric">
                    <span>Tasks Completed:</span>
                    <span class="metric-value">{progress['completed_tasks']}/{progress['total_tasks']}</span>
                </div>
                <div class="metric">
                    <span>Failed Tasks:</span>
                    <span class="metric-value">{progress['failed_tasks']}</span>
                </div>
                <div class="metric">
                    <span>ETA:</span>
                    <span class="metric-value">{eta if isinstance(eta, str) else eta.strftime('%Y-%m-%d %H:%M')}</span>
                </div>
            </div>

            <div class="status-card">
                <h3>🤖 Agent Status</h3>
                <div class="metric">
                    <span>Active Agents:</span>
                    <span class="metric-value">{progress['active_agents']}/{len(self.execution_system.agents)}</span>
                </div>
                <div class="metric">
                    <span>Agent Utilization:</span>
                    <span class="metric-value">{progress['agent_utilization']:.1f}%</span>
                </div>
                <div class="metric">
                    <span>Total Tasks by Agents:</span>
                    <span class="metric-value">{sum(agent.tasks_completed for agent in self.execution_system.agents.values())}</span>
                </div>
                <div class="metric">
                    <span>Average Quality Score:</span>
                    <span class="metric-value">{progress['average_quality_score']:.2f}</span>
                </div>
            </div>

            <div class="status-card">
                <h3>⏱️ Performance Metrics</h3>
                <div class="metric">
                    <span>Elapsed Time:</span>
                    <span class="metric-value">{progress['elapsed_time']:.1f} hours</span>
                </div>
                <div class="metric">
                    <span>Avg Task Duration:</span>
                    <span class="metric-value">{progress['elapsed_time']/max(1, progress['completed_tasks']):.1f} hours</span>
                </div>
                <div class="metric">
                    <span>Quality Gate Pass Rate:</span>
                    <span class="metric-value">{(progress['average_quality_score']*100):.1f}%</span>
                </div>
                <div class="metric">
                    <span>System Efficiency:</span>
                    <span class="metric-value">{(progress['agent_utilization'] * progress['average_quality_score'] / 100):.1f}%</span>
                </div>
            </div>
        </div>

        <div class="charts-section">
            <div class="chart-container">
                <h3>📈 Progress Over Time</h3>
                <img src="progress_chart.png" alt="Progress Chart">
            </div>

            <div class="chart-container">
                <h3>🎯 Quality Metrics</h3>
                <img src="quality_radar.png" alt="Quality Radar">
            </div>

            <div class="chart-container">
                <h3>🤖 Agent Performance</h3>
                <img src="agent_status.png" alt="Agent Status">
            </div>
        </div>

        <div class="agent-list">
            <h3>🤖 Agent Details</h3>
        """

        # Add agent details
        for agent_name, agent in self.execution_system.agents.items():
            status_class = f"status-{agent.status}"
            html += f"""
            <div class="agent-item">
                <div>
                    <strong>{agent_name.replace('-', ' ').title()}</strong>
                    <br>
                    <small>Tasks: {agent.tasks_completed} | Quality: {agent.quality_score:.2f}</small>
                </div>
                <div>
                    <span class="agent-status {status_class}">{agent.status.upper()}</span>
                </div>
            </div>
            """

        html += f"""
        </div>

        <div class="footer">
            <p><strong>FibreField Tech Android App - Parallel Execution System</strong></p>
            <p class="last-update">Last Updated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
            <p>Page refreshes every {self.config.refresh_interval} seconds</p>
        </div>
    </div>
</body>
</html>
        """

        # Write HTML file
        with open(f'{self.config.dashboard_dir}/index.html', 'w', encoding='utf-8') as f:
            f.write(html)

    def update_progress_history(self):
        """Update progress history"""
        progress = self.execution_system.get_progress_summary()
        progress['timestamp'] = datetime.now()

        self.progress_history.append(progress)

        # Keep only recent history
        if len(self.progress_history) > self.config.max_history_points:
            self.progress_history = self.progress_history[-self.config.max_history_points:]

    def generate_dashboard(self):
        """Generate complete dashboard"""
        self.logger.info("Generating dashboard...")

        # Update progress history
        self.update_progress_history()

        # Generate charts
        self.generate_progress_chart()
        self.generate_quality_radar()
        self.generate_agent_status_chart()

        # Generate HTML dashboard
        self.generate_html_dashboard()

        self.logger.info("Dashboard generated successfully")

    def start_dashboard_server(self):
        """Start dashboard web server"""
        os.chdir(self.config.dashboard_dir)

        class DashboardHandler(SimpleHTTPRequestHandler):
            def __init__(self, *args, **kwargs):
                super().__init__(*args, directory='.', **kwargs)

            def log_message(self, format, *args):
                pass  # Suppress log messages

        with socketserver.TCPServer(("", self.config.port), DashboardHandler) as httpd:
            self.logger.info(f"Dashboard server started at http://localhost:{self.config.port}")
            httpd.serve_forever()

    async def run_dashboard_loop(self):
        """Run dashboard update loop"""
        self.logger.info("Starting dashboard update loop...")

        while True:
            try:
                self.generate_dashboard()
                await asyncio.sleep(self.config.refresh_interval)
            except Exception as e:
                self.logger.error(f"Dashboard update failed: {e}")
                await asyncio.sleep(5)

async def main():
    """Main function to run the dashboard"""
    print("🚀 FibreField Tech Android App - Task Tracker Dashboard")
    print("=" * 60)

    # Initialize execution system
    execution_system = ParallelExecutionSystem()

    # Initialize dashboard
    dashboard = TaskTrackerDashboard(execution_system)

    # Start dashboard server in a separate thread
    server_thread = threading.Thread(target=dashboard.start_dashboard_server)
    server_thread.daemon = True
    server_thread.start()

    # Generate initial dashboard
    dashboard.generate_dashboard()

    print(f"📊 Dashboard server started at http://localhost:{dashboard.config.port}")
    print(f"🔄 Dashboard refreshes every {dashboard.config.refresh_interval} seconds")
    print("Press Ctrl+C to stop...")

    # Run dashboard update loop
    await dashboard.run_dashboard_loop()

if __name__ == "__main__":
    asyncio.run(main())