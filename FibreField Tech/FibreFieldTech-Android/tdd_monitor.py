#!/usr/bin/env python3
"""
TDD System Monitor and Management Tool
Provides real-time monitoring and management capabilities for the TDD Parallel Execution System
"""

import argparse
import asyncio
import json
import sqlite3
import sys
from datetime import datetime, timedelta
from pathlib import Path
from typing import Dict, List, Any
import matplotlib.pyplot as plt
import pandas as pd
from tabulate import tabulate

class TDDMonitor:
    def __init__(self, database_path: str = "tdd_execution.db"):
        self.database_path = database_path
        self.base_path = Path.cwd()
        self.worktrees_dir = self.base_path / "git_worktrees"
        self.reports_dir = self.base_path / "tdd_reports"

    def get_system_status(self) -> Dict[str, Any]:
        """Get current system status"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        # Get worktree tasks status
        cursor.execute("SELECT status, COUNT(*) FROM worktree_tasks GROUP BY status")
        task_status = dict(cursor.fetchall())

        # Get test specifications status
        cursor.execute("SELECT status, COUNT(*) FROM test_specifications GROUP BY status")
        test_status = dict(cursor.fetchall())

        # Get recent execution log entries
        cursor.execute("""
            SELECT task_id, agent_name, action, status, timestamp
            FROM execution_log
            ORDER BY timestamp DESC
            LIMIT 10
        """)
        recent_logs = cursor.fetchall()

        # Get active agents
        cursor.execute("""
            SELECT w.assigned_agent, COUNT(*) as active_tasks
            FROM worktree_tasks w
            WHERE w.status IN ('writing', 'implementing', 'validating')
            GROUP BY w.assigned_agent
        """)
        active_agents = dict(cursor.fetchall())

        conn.close()

        return {
            "timestamp": datetime.now().isoformat(),
            "task_status": task_status,
            "test_status": test_status,
            "recent_logs": recent_logs,
            "active_agents": active_agents,
            "total_tasks": sum(task_status.values()),
            "total_tests": sum(test_status.values())
        }

    def get_agent_performance(self) -> pd.DataFrame:
        """Get agent performance metrics"""
        conn = sqlite3.connect(self.database_path)

        # Calculate agent performance from execution log
        query = """
        SELECT
            agent_name,
            COUNT(*) as total_actions,
            SUM(CASE WHEN status = 'passed' THEN 1 ELSE 0 END) as successful_actions,
            AVG(
                CASE
                    WHEN duration_ms IS NOT NULL THEN duration_ms
                    ELSE 0
                END
            ) as avg_duration_ms
        FROM execution_log
        WHERE agent_name IS NOT NULL
        GROUP BY agent_name
        """

        df = pd.read_sql_query(query, conn)
        conn.close()

        if not df.empty:
            df['success_rate'] = (df['successful_actions'] / df['total_actions'] * 100).round(2)
            df['avg_duration_sec'] = (df['avg_duration_ms'] / 1000).round(2)

        return df

    def get_quality_metrics(self) -> Dict[str, Any]:
        """Get quality metrics across all tasks"""
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        # Get average quality scores
        cursor.execute("""
            SELECT
                AVG(json_extract(quality_metrics, '$.quality_score')) as avg_quality_score,
                AVG(json_extract(quality_metrics, '$.test_coverage')) as avg_coverage,
                COUNT(*) as total_evaluated
            FROM worktree_tasks
            WHERE quality_metrics IS NOT NULL
        """)
        quality_stats = cursor.fetchone()

        # Get quality violations
        cursor.execute("""
            SELECT
                status,
                COUNT(*) as count
            FROM worktree_tasks
            WHERE status = 'failed'
            GROUP BY status
        """)
        violations = dict(cursor.fetchall())

        conn.close()

        return {
            "average_quality_score": round(quality_stats[0] or 0, 2),
            "average_test_coverage": round(quality_stats[1] or 0, 2),
            "tasks_evaluated": quality_stats[2] or 0,
            "quality_violations": violations.get('failed', 0)
        }

    def get_progress_timeline(self, hours: int = 24) -> pd.DataFrame:
        """Get progress timeline for specified hours"""
        conn = sqlite3.connect(self.database_path)

        # Get task completions over time
        query = """
        SELECT
            datetime(timestamp) as hour,
            COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed,
            COUNT(CASE WHEN status = 'failed' THEN 1 END) as failed,
            COUNT(*) as total
        FROM execution_log
        WHERE timestamp >= datetime('now', '-{} hours')
        GROUP BY datetime(timestamp, 'localtime', 'start of hour')
        ORDER BY hour
        """.format(hours)

        df = pd.read_sql_query(query, conn)
        conn.close()

        return df

    def display_status_dashboard(self):
        """Display real-time status dashboard"""
        status = self.get_system_status()
        quality = self.get_quality_metrics()

        print("\n" + "=" * 80)
        print("🎯 TDD Parallel Execution System - Status Dashboard")
        print("=" * 80)
        print(f"📅 Last Updated: {status['timestamp']}")

        # System Overview
        print("\n📊 SYSTEM OVERVIEW")
        print("-" * 40)
        print(f"Total Tasks: {status['total_tasks']}")
        print(f"Total Tests: {status['total_tests']}")
        print(f"Average Quality Score: {quality['average_quality_score']}%")
        print(f"Average Test Coverage: {quality['average_test_coverage']}%")

        # Task Status
        print("\n📋 TASK STATUS")
        print("-" * 40)
        task_data = []
        for status_name, count in status['task_status'].items():
            percentage = (count / status['total_tasks'] * 100) if status['total_tasks'] > 0 else 0
            task_data.append([status_name.title(), count, f"{percentage:.1f}%"])

        print(tabulate(task_data, headers=["Status", "Count", "Percentage"], tablefmt="grid"))

        # Active Agents
        if status['active_agents']:
            print("\n🤖 ACTIVE AGENTS")
            print("-" * 40)
            agent_data = []
            for agent, tasks in status['active_agents'].items():
                agent_data.append([agent, tasks])

            print(tabulate(agent_data, headers=["Agent", "Active Tasks"], tablefmt="grid"))

        # Recent Activity
        print("\n📝 RECENT ACTIVITY")
        print("-" * 40)
        for log in status['recent_logs'][:5]:
            timestamp = datetime.fromisoformat(log[4]).strftime("%H:%M:%S")
            print(f"[{timestamp}] {log[1]} - {log[2]} ({log[3]})")

        # Quality Alerts
        if quality['quality_violations'] > 0:
            print(f"\n🚨 QUALITY ALERTS: {quality['quality_violations']} violations detected")

        print("\n" + "=" * 80)

    def display_agent_report(self):
        """Display detailed agent performance report"""
        df = self.get_agent_performance()

        if df.empty:
            print("\nNo agent performance data available")
            return

        print("\n" + "=" * 80)
        print("🤖 AGENT PERFORMANCE REPORT")
        print("=" * 80)

        # Prepare data for display
        display_data = df[[
            'agent_name', 'total_actions', 'successful_actions',
            'success_rate', 'avg_duration_sec'
        ]].copy()

        display_data.columns = [
            'Agent', 'Total Actions', 'Successful', 'Success Rate (%)', 'Avg Duration (s)'
        ]

        print(tabulate(display_data, headers="keys", tablefmt="grid", showindex=False))

        # Top performers
        top_agents = df.nlargest(3, 'success_rate')
        print("\n🏆 TOP PERFORMING AGENTS")
        print("-" * 40)
        for _, agent in top_agents.iterrows():
            print(f"• {agent['agent_name']}: {agent['success_rate']}% success rate")

        print("\n" + "=" * 80)

    def display_quality_report(self):
        """Display quality metrics report"""
        quality = self.get_quality_metrics()

        print("\n" + "=" * 80)
        print("🔍 QUALITY METRICS REPORT")
        print("=" * 80)

        print(f"\n📊 OVERALL QUALITY")
        print("-" * 40)
        print(f"Average Quality Score: {quality['average_quality_score']}%")
        print(f"Average Test Coverage: {quality['average_test_coverage']}%")
        print(f"Tasks Evaluated: {quality['tasks_evaluated']}")
        print(f"Quality Violations: {quality['quality_violations']}")

        # Get quality trends
        conn = sqlite3.connect(self.database_path)
        cursor = conn.cursor()

        # Quality trend by hour
        cursor.execute("""
            SELECT
                datetime(timestamp, 'localtime', 'start of hour') as hour,
                AVG(
                    CASE
                        WHEN status = 'passed' THEN 100
                        WHEN status = 'failed' THEN 0
                        ELSE NULL
                    END
                ) as quality_score
            FROM execution_log
            WHERE timestamp >= datetime('now', '-24 hours')
                AND status IN ('passed', 'failed')
            GROUP BY hour
            ORDER BY hour
        """)
        quality_trend = cursor.fetchall()
        conn.close()

        if quality_trend:
            print(f"\n📈 QUALITY TREND (Last 24 Hours)")
            print("-" * 40)
            for hour, score in quality_trend[-5:]:  # Show last 5 hours
                if score:
                    print(f"• {hour}: {score:.1f}%")

        print("\n" + "=" * 80)

    def generate_visualization(self, output_type: str = "screen"):
        """Generate performance visualizations"""
        # Get data
        timeline_df = self.get_progress_timeline()
        agent_df = self.get_agent_performance()

        if timeline_df.empty:
            print("No data available for visualization")
            return

        # Create figure with subplots
        fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(15, 10))
        fig.suptitle('TDD System Performance Dashboard', fontsize=16)

        # 1. Task Completion Timeline
        ax1.plot(timeline_df['hour'], timeline_df['completed'], 'g-o', label='Completed')
        ax1.plot(timeline_df['hour'], timeline_df['failed'], 'r-o', label='Failed')
        ax1.set_title('Task Completion Timeline')
        ax1.set_xlabel('Time')
        ax1.set_ylabel('Number of Tasks')
        ax1.legend()
        ax1.tick_params(axis='x', rotation=45)

        # 2. Agent Performance
        if not agent_df.empty:
            ax2.bar(agent_df['agent_name'], agent_df['success_rate'])
            ax2.set_title('Agent Success Rates')
            ax2.set_xlabel('Agent')
            ax2.set_ylabel('Success Rate (%)')
            ax2.tick_params(axis='x', rotation=45)

        # 3. Task Status Distribution
        status = self.get_system_status()
        if status['task_status']:
            labels = list(status['task_status'].keys())
            sizes = list(status['task_status'].values())
            ax3.pie(sizes, labels=labels, autopct='%1.1f%%')
            ax3.set_title('Task Status Distribution')

        # 4. Quality Metrics
        quality = self.get_quality_metrics()
        metrics = ['Quality Score', 'Test Coverage']
        values = [quality['average_quality_score'], quality['average_test_coverage']]
        ax4.bar(metrics, values, color=['blue', 'green'])
        ax4.set_title('Quality Metrics')
        ax4.set_ylabel('Percentage')
        ax4.set_ylim(0, 100)

        plt.tight_layout()

        if output_type == "screen":
            plt.show()
        elif output_type == "file":
            output_file = self.reports_dir / f"performance_dashboard_{datetime.now().strftime('%Y%m%d_%H%M%S')}.png"
            plt.savefig(output_file, dpi=300, bbox_inches='tight')
            print(f"Dashboard saved to: {output_file}")

    def cleanup_old_worktrees(self, days: int = 7):
        """Clean up old git worktrees"""
        print(f"\n🧹 Cleaning up worktrees older than {days} days...")

        cutoff_date = datetime.now() - timedelta(days=days)
        cleaned_count = 0

        # Iterate through worktrees directory
        for worktree_path in self.worktrees_dir.rglob("*"):
            if worktree_path.is_dir() and worktree_path.name.startswith("worktree_"):
                try:
                    # Check modification time
                    mod_time = datetime.fromtimestamp(worktree_path.stat().st_mtime)

                    if mod_time < cutoff_date:
                        print(f"Removing old worktree: {worktree_path.name}")

                        # Remove worktree using git
                        result = subprocess.run(
                            ["git", "worktree", "remove", str(worktree_path)],
                            capture_output=True, text=True
                        )

                        if result.returncode == 0:
                            # Remove directory if it still exists
                            if worktree_path.exists():
                                import shutil
                                shutil.rmtree(worktree_path)
                            cleaned_count += 1
                        else:
                            print(f"Failed to remove worktree: {result.stderr}")

                except Exception as e:
                    print(f"Error cleaning up {worktree_path}: {e}")

        print(f"✅ Cleaned up {cleaned_count} old worktrees")

    def export_report(self, format: str = "json"):
        """Export comprehensive report"""
        report_data = {
            "timestamp": datetime.now().isoformat(),
            "system_status": self.get_system_status(),
            "quality_metrics": self.get_quality_metrics(),
            "agent_performance": self.get_agent_performance().to_dict('records'),
            "progress_timeline": self.get_progress_timeline().to_dict('records')
        }

        if format == "json":
            output_file = self.reports_dir / f"tdd_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
            with open(output_file, 'w') as f:
                json.dump(report_data, f, indent=2)
            print(f"JSON report exported to: {output_file}")

        elif format == "csv":
            # Export different tables as CSV
            output_dir = self.reports_dir / f"csv_export_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
            output_dir.mkdir(exist_ok=True)

            # Agent performance
            self.get_agent_performance().to_csv(output_dir / "agent_performance.csv", index=False)

            # Progress timeline
            self.get_progress_timeline().to_csv(output_dir / "progress_timeline.csv", index=False)

            print(f"CSV reports exported to: {output_dir}")

async def monitor_mode(interval: int = 10):
    """Run in continuous monitoring mode"""
    monitor = TDDMonitor()

    print("🔄 Starting continuous monitoring mode...")
    print(f"📊 Refresh interval: {interval} seconds")
    print("Press Ctrl+C to stop monitoring\n")

    try:
        while True:
            monitor.display_status_dashboard()
            await asyncio.sleep(interval)
    except KeyboardInterrupt:
        print("\n⏹️  Monitoring stopped")

def main():
    parser = argparse.ArgumentParser(description='TDD System Monitor')
    parser.add_argument('--status', action='store_true', help='Show system status')
    parser.add_argument('--agents', action='store_true', help='Show agent performance report')
    parser.add_argument('--quality', action='store_true', help='Show quality metrics report')
    parser.add_argument('--visualize', choices=['screen', 'file'], help='Generate performance visualization')
    parser.add_argument('--monitor', type=int, metavar='SECONDS', help='Run in continuous monitoring mode')
    parser.add_argument('--cleanup', type=int, metavar='DAYS', help='Clean up worktrees older than DAYS')
    parser.add_argument('--export', choices=['json', 'csv'], help='Export report in specified format')
    parser.add_argument('--database', default='tdd_execution.db', help='Database file path')

    args = parser.parse_args()

    monitor = TDDMonitor(args.database)

    if args.monitor:
        asyncio.run(monitor_mode(args.monitor))
    elif args.status:
        monitor.display_status_dashboard()
    elif args.agents:
        monitor.display_agent_report()
    elif args.quality:
        monitor.display_quality_report()
    elif args.visualize:
        monitor.generate_visualization(args.visualize)
    elif args.cleanup:
        monitor.cleanup_old_worktrees(args.cleanup)
    elif args.export:
        monitor.export_report(args.export)
    else:
        # Default: show status dashboard
        monitor.display_status_dashboard()

if __name__ == "__main__":
    main()