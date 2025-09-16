#!/usr/bin/env python3
"""
FibreField Tech Android App - Task Tracking Dashboard
Real-time monitoring and progress visualization
"""

import json
import os
from datetime import datetime, timedelta
from pathlib import Path
from typing import Dict, List, Any, Optional
import plotly.graph_objects as go
import plotly.express as px
from plotly.subplots import make_subplots
import pandas as pd
from dataclasses import dataclass, asdict
import logging
from enum import Enum

class TaskStatus(Enum):
    PENDING = "pending"
    IN_PROGRESS = "in_progress"
    COMPLETED = "completed"
    BLOCKED = "blocked"
    REVIEW = "review"
    TESTING = "testing"

class AgentStatus(Enum):
    IDLE = "idle"
    WORKING = "working"
    BLOCKED = "blocked"
    COMPLETED = "completed"
    ERROR = "error"

@dataclass
class TaskProgress:
    task_id: str
    title: str
    agent: str
    status: TaskStatus
    progress: float
    estimated_hours: float
    actual_hours: float
    phase: int
    sprint: int
    start_time: Optional[datetime] = None
    end_time: Optional[datetime] = None
    blocking_issues: List[str] = None
    quality_score: float = 0.0
    test_coverage: float = 0.0

    def __post_init__(self):
        if self.blocking_issues is None:
            self.blocking_issues = []

@dataclass
class AgentMetrics:
    name: str
    status: AgentStatus
    current_task: Optional[str]
    completed_tasks: int
    performance_score: float
    efficiency: float
    last_active: Optional[datetime]
    specialization: str

class TaskTrackerDashboard:
    """Real-time task tracking and visualization dashboard"""

    def __init__(self, project_root: Optional[str] = None):
        self.project_root = Path(project_root) if project_root else Path.cwd()
        self.data_dir = self.project_root / "dashboard_data"
        self.data_dir.mkdir(exist_ok=True)

        # Initialize logging
        self.logger = self._setup_logging()

        # Load project data
        self.tasks: Dict[str, TaskProgress] = {}
        self.agents: Dict[str, AgentMetrics] = {}
        self.phases = {
            1: "Core Infrastructure",
            2: "Core Features",
            3: "Advanced Features",
            4: "Finalization & Deployment"
        }

        # Load existing data
        self._load_data()

    def _setup_logging(self) -> logging.Logger:
        """Setup logging for dashboard"""
        logger = logging.getLogger('task_tracker')
        logger.setLevel(logging.INFO)

        log_file = self.data_dir / f"dashboard_{datetime.now().strftime('%Y%m%d')}.log"
        handler = logging.FileHandler(log_file)
        formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')
        handler.setFormatter(formatter)
        logger.addHandler(handler)

        return logger

    def _load_data(self):
        """Load existing task and agent data"""
        # Load tasks
        tasks_file = self.data_dir / "tasks.json"
        if tasks_file.exists():
            with open(tasks_file, 'r') as f:
                tasks_data = json.load(f)
                for task_id, task_data in tasks_data.items():
                    # Convert string dates back to datetime
                    if task_data.get('start_time'):
                        task_data['start_time'] = datetime.fromisoformat(task_data['start_time'])
                    if task_data.get('end_time'):
                        task_data['end_time'] = datetime.fromisoformat(task_data['end_time'])

                    self.tasks[task_id] = TaskProgress(**task_data)

        # Load agents
        agents_file = self.data_dir / "agents.json"
        if agents_file.exists():
            with open(agents_file, 'r') as f:
                agents_data = json.load(f)
                for agent_name, agent_data in agents_data.items():
                    if agent_data.get('last_active'):
                        agent_data['last_active'] = datetime.fromisoformat(agent_data['last_active'])
                    self.agents[agent_name] = AgentMetrics(**agent_data)

    def _save_data(self):
        """Save current task and agent data"""
        # Save tasks
        tasks_file = self.data_dir / "tasks.json"
        tasks_data = {task_id: asdict(task) for task_id, task in self.tasks.items()}
        # Convert datetime to string for JSON serialization
        for task_data in tasks_data.values():
            if task_data.get('start_time'):
                task_data['start_time'] = task_data['start_time'].isoformat()
            if task_data.get('end_time'):
                task_data['end_time'] = task_data['end_time'].isoformat()

        with open(tasks_file, 'w') as f:
            json.dump(tasks_data, f, indent=2)

        # Save agents
        agents_file = self.data_dir / "agents.json"
        agents_data = {name: asdict(agent) for name, agent in self.agents.items()}
        for agent_data in agents_data.values():
            if agent_data.get('last_active'):
                agent_data['last_active'] = agent_data['last_active'].isoformat()

        with open(agents_file, 'w') as f:
            json.dump(agents_data, f, indent=2)

    def update_task_progress(self, task_id: str, **kwargs):
        """Update progress for a specific task"""
        if task_id not in self.tasks:
            self.logger.error(f"Task {task_id} not found")
            return False

        task = self.tasks[task_id]

        # Update provided fields
        for key, value in kwargs.items():
            if hasattr(task, key):
                setattr(task, key, value)

        # Log update
        self.logger.info(f"Updated task {task_id}: {kwargs}")

        # Save data
        self._save_data()

        return True

    def update_agent_status(self, agent_name: str, **kwargs):
        """Update status for a specific agent"""
        if agent_name not in self.agents:
            self.logger.error(f"Agent {agent_name} not found")
            return False

        agent = self.agents[agent_name]

        # Update provided fields
        for key, value in kwargs.items():
            if hasattr(agent, key):
                setattr(agent, key, value)

        # Log update
        self.logger.info(f"Updated agent {agent_name}: {kwargs}")

        # Save data
        self._save_data()

        return True

    def generate_progress_chart(self) -> go.Figure:
        """Generate overall progress chart"""
        # Group tasks by phase
        phase_data = {}
        for phase_id in self.phases:
            phase_tasks = [t for t in self.tasks.values() if t.phase == phase_id]
            if phase_tasks:
                completed = sum(1 for t in phase_tasks if t.status == TaskStatus.COMPLETED)
                total = len(phase_tasks)
                phase_data[self.phases[phase_id]] = {
                    'completed': completed,
                    'total': total,
                    'progress': (completed / total) * 100 if total > 0 else 0
                }

        # Create bar chart
        fig = go.Figure()

        phases = list(phase_data.keys())
        progress = [phase_data[p]['progress'] for p in phases]
        completed = [phase_data[p]['completed'] for p in phases]
        total = [phase_data[p]['total'] for p in phases]

        fig.add_trace(go.Bar(
            name='Progress',
            x=phases,
            y=progress,
            text=[f"{c}/{t}" for c, t in zip(completed, total)],
            textposition='auto',
            marker_color=['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728']
        ))

        fig.update_layout(
            title="Project Progress by Phase",
            xaxis_title="Phase",
            yaxis_title="Progress (%)",
            yaxis=dict(range=[0, 100]),
            showlegend=False,
            height=400
        )

        return fig

    def generate_burndown_chart(self, sprint: int) -> go.Figure:
        """Generate sprint burndown chart"""
        # Get tasks for current sprint
        sprint_tasks = [t for t in self.tasks.values() if t.sprint == sprint]

        # Calculate ideal burndown
        total_hours = sum(t.estimated_hours for t in sprint_tasks)
        days_in_sprint = 14  # 2-week sprint

        ideal_hours = []
        remaining_hours = total_hours
        for day in range(days_in_sprint + 1):
            ideal_hours.append(remaining_hours)
            remaining_hours -= total_hours / days_in_sprint

        # Calculate actual burndown (simplified)
        actual_hours = []
        completed_hours = sum(
            t.actual_hours for t in sprint_tasks
            if t.status == TaskStatus.COMPLETED
        )

        for day in range(days_in_sprint + 1):
            if day == 0:
                actual_hours.append(total_hours)
            else:
                # Simplified actual progress
                progress_factor = min(day / days_in_sprint, 1.0)
                actual_hours.append(total_hours - (completed_hours * progress_factor))

        # Create burndown chart
        fig = go.Figure()

        days = list(range(days_in_sprint + 1))

        fig.add_trace(go.Scatter(
            name='Ideal Burndown',
            x=days,
            y=ideal_hours,
            mode='lines',
            line=dict(dash='dash', color='gray')
        ))

        fig.add_trace(go.Scatter(
            name='Actual Burndown',
            x=days,
            y=actual_hours,
            mode='lines+markers',
            line=dict(color='blue')
        ))

        fig.update_layout(
            title=f"Sprint {sprint} Burndown Chart",
            xaxis_title="Days",
            yaxis_title="Remaining Hours",
            height=400
        )

        return fig

    def generate_agent_utilization_chart(self) -> go.Figure:
        """Generate agent utilization chart"""
        agents_data = []
        for agent in self.agents.values():
            utilization = 0
            if agent.status == AgentStatus.WORKING:
                utilization = 100
            elif agent.status == AgentStatus.IDLE:
                utilization = 0
            else:
                utilization = 50  # Blocked or other states

            agents_data.append({
                'agent': agent.name,
                'utilization': utilization,
                'specialization': agent.specialization,
                'performance': agent.performance_score * 100
            })

        df = pd.DataFrame(agents_data)

        # Create utilization chart
        fig = go.Figure()

        fig.add_trace(go.Bar(
            name='Utilization',
            x=df['agent'],
            y=df['utilization'],
            text=df['utilization'].astype(int).astype(str) + '%',
            textposition='auto',
            marker_color=df['performance'],
            colorscale='RdYlGn',
            marker=dict(showscale=True, colorbar=dict(title="Performance Score"))
        ))

        fig.update_layout(
            title="Agent Utilization and Performance",
            xaxis_title="Agent",
            yaxis_title="Utilization (%)",
            yaxis=dict(range=[0, 100]),
            height=400
        )

        return fig

    def generate_quality_metrics_chart(self) -> go.Figure:
        """Generate quality metrics dashboard"""
        metrics = {
            'Test Coverage': [],
            'Quality Score': [],
            'Performance': [],
            'Security': []
        }

        for phase_id in self.phases:
            phase_tasks = [t for t in self.tasks.values() if t.phase == phase_id]

            if phase_tasks:
                # Calculate metrics for phase
                avg_test_coverage = sum(t.test_coverage for t in phase_tasks) / len(phase_tasks)
                avg_quality_score = sum(t.quality_score for t in phase_tasks) / len(phase_tasks)

                metrics['Test Coverage'].append(avg_test_coverage * 100)
                metrics['Quality Score'].append(avg_quality_score * 100)
                metrics['Performance'].append(95 - (phase_id - 1) * 5)  # Simulated
                metrics['Security'].append(100)  # Simulated perfect security

        # Create radar chart
        fig = go.Figure()

        phases = list(self.phases.values())

        for metric, values in metrics.items():
            fig.add_trace(go.Scatterpolar(
                r=values,
                theta=phases,
                fill='toself',
                name=metric
            ))

        fig.update_layout(
            polar=dict(
                radialaxis=dict(
                    visible=True,
                    range=[0, 100]
                )),
            showlegend=True,
            title="Quality Metrics by Phase",
            height=500
        )

        return fig

    def generate_task_status_pie(self) -> go.Figure:
        """Generate task status distribution pie chart"""
        status_counts = {}
        for task in self.tasks.values():
            status = task.status.value
            status_counts[status] = status_counts.get(status, 0) + 1

        fig = go.Figure(data=[go.Pie(
            labels=list(status_counts.keys()),
            values=list(status_counts.values()),
            hole=.3,
            textinfo='label+percent'
        )])

        fig.update_layout(
            title="Task Status Distribution",
            height=400
        )

        return fig

    def generate_timeline_gantt(self) -> go.Figure:
        """Generate project timeline Gantt chart"""
        # Prepare data for Gantt chart
        gantt_data = []
        colors = {
            'Core Infrastructure': '#1f77b4',
            'Core Features': '#ff7f0e',
            'Advanced Features': '#2ca02c',
            'Finalization & Deployment': '#d62728'
        }

        for task in self.tasks.values():
            if task.start_time and task.end_time:
                gantt_data.append(dict(
                    Task=task.title[:40] + '...' if len(task.title) > 40 else task.title,
                    Start=task.start_time,
                    Finish=task.end_time,
                    Phase=self.phases.get(task.phase, 'Unknown'),
                    Agent=task.agent
                ))

        if not gantt_data:
            # Create empty chart if no timeline data
            fig = go.Figure()
            fig.update_layout(
                title="Project Timeline (No data available)",
                height=600
            )
            return fig

        df = pd.DataFrame(gantt_data)

        fig = px.timeline(
            df,
            x_start="Start",
            x_end="Finish",
            y="Task",
            color="Phase",
            color_discrete_map=colors,
            hover_data=["Agent"]
        )

        fig.update_layout(
            title="Project Timeline",
            height=600,
            xaxis_title="Date",
            showlegend=True
        )

        return fig

    def generate_dashboard_html(self) -> str:
        """Generate complete dashboard HTML"""
        # Generate all charts
        progress_chart = self.generate_progress_chart()
        burndown_chart = self.generate_burndown_chart(sprint=1)
        utilization_chart = self.generate_agent_utilization_chart()
        quality_chart = self.generate_quality_metrics_chart()
        status_pie = self.generate_task_status_pie()
        timeline_chart = self.generate_timeline_gantt()

        # Calculate summary metrics
        total_tasks = len(self.tasks)
        completed_tasks = sum(1 for t in self.tasks.values() if t.status == TaskStatus.COMPLETED)
        blocked_tasks = sum(1 for t in self.tasks.values() if t.status == TaskStatus.BLOCKED)
        active_agents = sum(1 for a in self.agents.values() if a.status == AgentStatus.WORKING)
        avg_performance = sum(a.performance_score for a in self.agents.values()) / len(self.agents) if self.agents else 0

        # Generate HTML
        html = f"""
        <!DOCTYPE html>
        <html>
        <head>
            <title>FibreField Tech Android App - Task Dashboard</title>
            <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
            <style>
                body {{
                    font-family: Arial, sans-serif;
                    margin: 0;
                    padding: 20px;
                    background-color: #f5f5f5;
                }}
                .header {{
                    background-color: #2c3e50;
                    color: white;
                    padding: 20px;
                    border-radius: 5px;
                    margin-bottom: 20px;
                }}
                .metrics {{
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                    gap: 20px;
                    margin-bottom: 30px;
                }}
                .metric-card {{
                    background: white;
                    padding: 20px;
                    border-radius: 5px;
                    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    text-align: center;
                }}
                .metric-value {{
                    font-size: 2em;
                    font-weight: bold;
                    color: #3498db;
                }}
                .metric-label {{
                    color: #7f8c8d;
                    margin-top: 5px;
                }}
                .chart-container {{
                    background: white;
                    padding: 20px;
                    border-radius: 5px;
                    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    margin-bottom: 30px;
                }}
                .charts-grid {{
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
                    gap: 30px;
                }}
                .full-width {{
                    grid-column: 1 / -1;
                }}
                .refresh-time {{
                    text-align: right;
                    color: #7f8c8d;
                    font-size: 0.9em;
                    margin-top: 20px;
                }}
            </style>
        </head>
        <body>
            <div class="header">
                <h1>FibreField Tech Android App - Task Dashboard</h1>
                <p>Real-time project monitoring and progress tracking</p>
            </div>

            <div class="metrics">
                <div class="metric-card">
                    <div class="metric-value">{total_tasks}</div>
                    <div class="metric-label">Total Tasks</div>
                </div>
                <div class="metric-card">
                    <div class="metric-value">{completed_tasks}</div>
                    <div class="metric-label">Completed</div>
                </div>
                <div class="metric-card">
                    <div class="metric-value">{blocked_tasks}</div>
                    <div class="metric-label">Blocked</div>
                </div>
                <div class="metric-card">
                    <div class="metric-value">{active_agents}</div>
                    <div class="metric-label">Active Agents</div>
                </div>
                <div class="metric-card">
                    <div class="metric-value">{avg_performance:.1%}</div>
                    <div class="metric-label">Avg Performance</div>
                </div>
            </div>

            <div class="charts-grid">
                <div class="chart-container">
                    <div id="progress-chart"></div>
                </div>
                <div class="chart-container">
                    <div id="status-pie"></div>
                </div>
                <div class="chart-container full-width">
                    <div id="burndown-chart"></div>
                </div>
                <div class="chart-container">
                    <div id="utilization-chart"></div>
                </div>
                <div class="chart-container">
                    <div id="quality-chart"></div>
                </div>
                <div class="chart-container full-width">
                    <div id="timeline-chart"></div>
                </div>
            </div>

            <div class="refresh-time">
                Last updated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
            </div>

            <script>
                // Render all charts
                Plotly.newPlot('progress-chart', {progress_chart.to_json()});
                Plotly.newPlot('burndown-chart', {burndown_chart.to_json()});
                Plotly.newPlot('utilization-chart', {utilization_chart.to_json()});
                Plotly.newPlot('quality-chart', {quality_chart.to_json()});
                Plotly.newPlot('status-pie', {status_pie.to_json()});
                Plotly.newPlot('timeline-chart', {timeline_chart.to_json()});

                // Auto-refresh every 30 seconds
                setTimeout(function() {{
                    location.reload();
                }}, 30000);
            </script>
        </body>
        </html>
        """

        return html

    def save_dashboard(self, output_path: Optional[str] = None):
        """Save dashboard HTML file"""
        if output_path is None:
            output_path = self.data_dir / "dashboard.html"

        html = self.generate_dashboard_html()
        with open(output_path, 'w') as f:
            f.write(html)

        self.logger.info(f"Dashboard saved to {output_path}")
        return output_path

    def export_progress_report(self, format_type: str = 'json') -> str:
        """Export progress report in specified format"""
        report_data = {
            'timestamp': datetime.now().isoformat(),
            'summary': {
                'total_tasks': len(self.tasks),
                'completed_tasks': sum(1 for t in self.tasks.values() if t.status == TaskStatus.COMPLETED),
                'blocked_tasks': sum(1 for t in self.tasks.values() if t.status == TaskStatus.BLOCKED),
                'in_progress_tasks': sum(1 for t in self.tasks.values() if t.status == TaskStatus.IN_PROGRESS),
                'overall_progress': (sum(1 for t in self.tasks.values() if t.status == TaskStatus.COMPLETED) / len(self.tasks)) * 100 if self.tasks else 0
            },
            'phases': {},
            'agents': {
                name: {
                    'status': agent.status.value,
                    'completed_tasks': agent.completed_tasks,
                    'performance_score': agent.performance_score,
                    'efficiency': agent.efficiency
                }
                for name, agent in self.agents.items()
            },
            'blocking_issues': [
                {
                    'task_id': task.task_id,
                    'title': task.title,
                    'issues': task.blocking_issues
                }
                for task in self.tasks.values()
                if task.blocking_issues
            ]
        }

        # Add phase details
        for phase_id, phase_name in self.phases.items():
            phase_tasks = [t for t in self.tasks.values() if t.phase == phase_id]
            if phase_tasks:
                report_data['phases'][phase_name] = {
                    'total_tasks': len(phase_tasks),
                    'completed_tasks': sum(1 for t in phase_tasks if t.status == TaskStatus.COMPLETED),
                    'progress': (sum(1 for t in phase_tasks if t.status == TaskStatus.COMPLETED) / len(phase_tasks)) * 100,
                    'estimated_hours': sum(t.estimated_hours for t in phase_tasks),
                    'actual_hours': sum(t.actual_hours for t in phase_tasks if t.actual_hours > 0)
                }

        if format_type.lower() == 'json':
            output_path = self.data_dir / f"progress_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
            with open(output_path, 'w') as f:
                json.dump(report_data, f, indent=2)
        elif format_type.lower() == 'csv':
            # Convert to CSV format
            df = pd.DataFrame([
                {
                    'task_id': task.task_id,
                    'title': task.title,
                    'agent': task.agent,
                    'status': task.status.value,
                    'progress': task.progress,
                    'phase': task.phase,
                    'estimated_hours': task.estimated_hours,
                    'actual_hours': task.actual_hours,
                    'quality_score': task.quality_score,
                    'test_coverage': task.test_coverage
                }
                for task in self.tasks.values()
            ])
            output_path = self.data_dir / f"progress_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
            df.to_csv(output_path, index=False)

        self.logger.info(f"Progress report exported to {output_path}")
        return str(output_path)

def main():
    """Main entry point for dashboard"""
    # Initialize dashboard
    dashboard = TaskTrackerDashboard()

    # Save dashboard HTML
    dashboard.save_dashboard()

    # Export progress report
    dashboard.export_progress_report('json')
    dashboard.export_progress_report('csv')

    print("Dashboard generated successfully!")
    print(f"Dashboard HTML: {dashboard.data_dir / 'dashboard.html'}")

if __name__ == "__main__":
    main()