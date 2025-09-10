"""
Enhanced Report Manager for Python Selenium - Comprehensive reporting with performance metrics
Features: Success/Failure Screenshots, Performance Tracking, Test History, Step-by-step Logging

@author: Asim Kumar Singh
@version: 2.0.0 - Enhanced for Lead Review Requirements
"""

import os
import json
import time
import logging
from datetime import datetime
from typing import Dict, Any, Optional
from pathlib import Path
import base64

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class EnhancedReportManager:
    """Enhanced reporting manager with comprehensive metrics and screenshots"""
    
    def __init__(self):
        self.report_data = {
            'test_results': [],
            'performance_metrics': {},
            'screenshots': {},
            'execution_summary': {},
            'system_info': {}
        }
        self.test_start_times = {}
        self.step_start_times = {}
        self.test_execution_counts = {}
        self.current_test = None
        
        # Statistics
        self.total_tests = 0
        self.passed_tests = 0
        self.failed_tests = 0
        self.skipped_tests = 0
        self.total_execution_time = 0
        
        # Performance thresholds
        self.PERFORMANCE_THRESHOLD_MS = 5000
        
        # Initialize report directory
        self.report_dir = Path("reports")
        self.screenshot_dir = self.report_dir / "screenshots"
        self._create_directories()
        
        # Initialize system info
        self._set_system_information()
    
    def _create_directories(self):
        """Create necessary directories for reports and screenshots"""
        directories = [
            self.report_dir,
            self.screenshot_dir,
            self.screenshot_dir / "success",
            self.screenshot_dir / "failure", 
            self.screenshot_dir / "steps"
        ]
        
        for directory in directories:
            directory.mkdir(parents=True, exist_ok=True)
            logger.info(f"Directory ensured: {directory}")
    
    def _set_system_information(self):
        """Set comprehensive system information for lead review"""
        import platform
        import sys
        
        self.report_data['system_info'] = {
            'operating_system': platform.system(),
            'os_version': platform.version(),
            'python_version': sys.version,
            'platform': platform.platform(),
            'processor': platform.processor(),
            'test_environment': os.getenv('TEST_ENVIRONMENT', 'qa'),
            'browser': os.getenv('BROWSER_TYPE', 'chrome'),
            'headless_mode': os.getenv('HEADLESS_MODE', 'true'),
            'base_url': os.getenv('BASE_URL', 'https://www.saucedemo.com'),
            'performance_threshold': f"{self.PERFORMANCE_THRESHOLD_MS}ms",
            'screenshot_mode': 'Success & Failure Capture Enabled',
            'report_generation_time': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            'execution_mode': 'Enhanced Reporting with Performance Tracking',
            'report_version': '2.0.0 - Lead Review Edition',
            'framework': 'Python Selenium with Enhanced Reporting'
        }
    
    def start_test(self, test_name: str, description: str = "") -> Dict[str, Any]:
        """Start a new test with enhanced tracking"""
        test_id = f"{test_name}_{int(time.time())}"
        
        # Track execution count for history
        if test_name not in self.test_execution_counts:
            self.test_execution_counts[test_name] = 0
        self.test_execution_counts[test_name] += 1
        
        # Initialize test data
        test_data = {
            'test_id': test_id,
            'test_name': test_name,
            'description': description,
            'execution_count': self.test_execution_counts[test_name],
            'start_time': datetime.now().isoformat(),
            'status': 'RUNNING',
            'steps': [],
            'performance_metrics': {},
            'screenshots': [],
            'duration_ms': 0
        }
        
        self.current_test = test_data
        self.test_start_times[test_id] = time.time()
        self.total_tests += 1
        
        logger.info(f"Enhanced test started: {test_name} (Execution #{self.test_execution_counts[test_name]})")
        
        # Log test start
        self.log_step(f"TEST STARTED: {test_name}")
        self.log_info(f"Test Description: {description}")
        self.log_info(f"Execution Count: {self.test_execution_counts[test_name]}")
        self.log_info(f"Start Time: {datetime.now().strftime('%H:%M:%S')}")
        
        return test_data
    
    def log_step(self, step_description: str):
        """Log a test step with performance tracking"""
        if not self.current_test:
            logger.warning("No active test to log step")
            return
        
        step_id = f"step_{len(self.current_test['steps'])}"
        self.step_start_times[step_id] = time.time()
        
        step_data = {
            'step_id': step_id,
            'description': step_description,
            'timestamp': datetime.now().isoformat(),
            'status': 'RUNNING'
        }
        
        self.current_test['steps'].append(step_data)
        logger.info(f"Test step logged: {step_description}")
    
    def log_step_completion(self, step_description: str):
        """Log step completion with performance metrics"""
        if not self.current_test:
            return
        
        # Find the last running step
        for step in reversed(self.current_test['steps']):
            if step['status'] == 'RUNNING':
                step_id = step['step_id']
                if step_id in self.step_start_times:
                    step_duration = (time.time() - self.step_start_times[step_id]) * 1000
                    step['duration_ms'] = step_duration
                    step['performance_level'] = self._get_performance_level(step_duration)
                    del self.step_start_times[step_id]
                
                step['status'] = 'COMPLETED'
                step['completion_time'] = datetime.now().isoformat()
                break
        
        logger.info(f"Test step completed: {step_description}")
    
    def log_web_performance_metrics(self, action: str, load_time_ms: float, url: str):
        """Log web-specific performance metrics"""
        if not self.current_test:
            return
        
        performance_data = {
            'action': action,
            'url': url,
            'load_time_ms': load_time_ms,
            'performance_level': self._get_performance_level(load_time_ms),
            'timestamp': datetime.now().isoformat()
        }
        
        if 'web_metrics' not in self.current_test['performance_metrics']:
            self.current_test['performance_metrics']['web_metrics'] = []
        
        self.current_test['performance_metrics']['web_metrics'].append(performance_data)
        
        logger.info(f"Web performance logged: {action} - {load_time_ms}ms")
    
    def log_info(self, message: str):
        """Log informational message"""
        if self.current_test:
            if 'info_logs' not in self.current_test:
                self.current_test['info_logs'] = []
            
            self.current_test['info_logs'].append({
                'message': message,
                'timestamp': datetime.now().isoformat(),
                'level': 'INFO'
            })
    
    def log_warning(self, message: str):
        """Log warning message"""
        if self.current_test:
            if 'warning_logs' not in self.current_test:
                self.current_test['warning_logs'] = []
            
            self.current_test['warning_logs'].append({
                'message': message,
                'timestamp': datetime.now().isoformat(),
                'level': 'WARNING'
            })
    
    def log_pass(self, message: str, screenshot_path: Optional[str] = None):
        """Log test pass with optional screenshot"""
        if not self.current_test:
            return
        
        self.current_test['status'] = 'PASSED'
        self.current_test['result_message'] = message
        
        if screenshot_path:
            self.current_test['screenshots'].append({
                'type': 'SUCCESS',
                'path': screenshot_path,
                'timestamp': datetime.now().isoformat()
            })
        
        self._finalize_test(True)
        self.passed_tests += 1
        
        logger.info(f"Test passed: {message}")
    
    def log_fail(self, message: str, screenshot_path: Optional[str] = None, error_details: Optional[str] = None):
        """Log test failure with screenshot and error details"""
        if not self.current_test:
            return
        
        self.current_test['status'] = 'FAILED'
        self.current_test['result_message'] = message
        
        if error_details:
            self.current_test['error_details'] = error_details
        
        if screenshot_path:
            self.current_test['screenshots'].append({
                'type': 'FAILURE',
                'path': screenshot_path,
                'timestamp': datetime.now().isoformat()
            })
        
        self._finalize_test(False)
        self.failed_tests += 1
        
        logger.error(f"Test failed: {message}")
    
    def log_skip(self, message: str, reason: str = ""):
        """Log test skip"""
        if not self.current_test:
            return
        
        self.current_test['status'] = 'SKIPPED'
        self.current_test['result_message'] = message
        self.current_test['skip_reason'] = reason
        
        self._finalize_test(None)
        self.skipped_tests += 1
        
        logger.warning(f"Test skipped: {message}")
    
    def _finalize_test(self, passed: Optional[bool]):
        """Finalize test with comprehensive performance metrics"""
        if not self.current_test:
            return
        
        test_id = self.current_test['test_id']
        if test_id in self.test_start_times:
            test_duration = (time.time() - self.test_start_times[test_id]) * 1000
            self.current_test['duration_ms'] = test_duration
            self.current_test['performance_level'] = self._get_performance_level(test_duration)
            self.total_execution_time += test_duration
            del self.test_start_times[test_id]
        
        self.current_test['end_time'] = datetime.now().isoformat()
        
        # Add performance summary
        self.current_test['performance_summary'] = {
            'total_execution_time_ms': self.current_test.get('duration_ms', 0),
            'performance_level': self.current_test.get('performance_level', 'Unknown'),
            'steps_count': len(self.current_test.get('steps', [])),
            'screenshots_count': len(self.current_test.get('screenshots', []))
        }
        
        # Add to results
        self.report_data['test_results'].append(self.current_test.copy())
        self.current_test = None
    
    def _get_performance_level(self, duration_ms: float) -> str:
        """Get performance level description"""
        if duration_ms < 1000:
            return "🚀 Excellent"
        elif duration_ms < 3000:
            return "✅ Good"
        elif duration_ms < 5000:
            return "⚠️ Acceptable"
        else:
            return "❌ Poor"
    
    def generate_html_report(self) -> str:
        """Generate comprehensive HTML report for lead review"""
        timestamp = datetime.now().strftime('%Y-%m-%d_%H-%M-%S')
        report_filename = f"Python_Selenium_Enhanced_Report_{timestamp}.html"
        report_path = self.report_dir / report_filename
        
        # Calculate summary statistics
        pass_percentage = (self.passed_tests / self.total_tests * 100) if self.total_tests > 0 else 0
        avg_execution_time = (self.total_execution_time / self.total_tests) if self.total_tests > 0 else 0
        
        # Generate HTML content
        html_content = self._generate_html_content(pass_percentage, avg_execution_time)
        
        # Write HTML file
        with open(report_path, 'w', encoding='utf-8') as f:
            f.write(html_content)
        
        logger.info(f"Enhanced HTML report generated: {report_path}")
        return str(report_path)
    
    def _generate_html_content(self, pass_percentage: float, avg_execution_time: float) -> str:
        """Generate HTML content for the report"""
        return f"""
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enhanced Python Selenium Automation Report - Lead Review</title>
    <style>
        body {{ font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }}
        .container {{ max-width: 1200px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 20px rgba(0,0,0,0.1); }}
        .header {{ text-align: center; margin-bottom: 30px; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border-radius: 10px; }}
        .summary {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-bottom: 30px; }}
        .summary-card {{ background: #f8f9fa; padding: 20px; border-radius: 8px; text-align: center; border-left: 4px solid #007bff; }}
        .summary-card.passed {{ border-left-color: #28a745; }}
        .summary-card.failed {{ border-left-color: #dc3545; }}
        .summary-card.performance {{ border-left-color: #ffc107; }}
        .test-results {{ margin-top: 30px; }}
        .test-item {{ background: white; margin: 10px 0; padding: 20px; border-radius: 8px; border: 1px solid #dee2e6; }}
        .test-item.passed {{ border-left: 4px solid #28a745; }}
        .test-item.failed {{ border-left: 4px solid #dc3545; }}
        .test-item.skipped {{ border-left: 4px solid #ffc107; }}
        .performance-good {{ color: #28a745; font-weight: bold; }}
        .performance-warning {{ color: #ffc107; font-weight: bold; }}
        .performance-critical {{ color: #dc3545; font-weight: bold; }}
        .screenshot {{ max-width: 300px; margin: 10px 0; border: 2px solid #dee2e6; border-radius: 5px; }}
        .system-info {{ background: #e9ecef; padding: 15px; border-radius: 8px; margin-top: 20px; }}
        .steps {{ background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; }}
        .timestamp {{ color: #6c757d; font-size: 0.9em; }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🐍 Enhanced Python Selenium Automation Report</h1>
            <h2>Lead Review Edition - Version 2.0.0</h2>
            <p>Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
        </div>
        
        <div class="summary">
            <div class="summary-card">
                <h3>Total Tests</h3>
                <h2>{self.total_tests}</h2>
            </div>
            <div class="summary-card passed">
                <h3>Passed</h3>
                <h2>{self.passed_tests}</h2>
            </div>
            <div class="summary-card failed">
                <h3>Failed</h3>
                <h2>{self.failed_tests}</h2>
            </div>
            <div class="summary-card">
                <h3>Skipped</h3>
                <h2>{self.skipped_tests}</h2>
            </div>
            <div class="summary-card performance">
                <h3>Pass Rate</h3>
                <h2>{pass_percentage:.1f}%</h2>
            </div>
            <div class="summary-card performance">
                <h3>Avg Time</h3>
                <h2>{avg_execution_time:.0f}ms</h2>
            </div>
        </div>
        
        <div class="system-info">
            <h3>📋 System Information</h3>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 10px;">
                {self._generate_system_info_html()}
            </div>
        </div>
        
        <div class="test-results">
            <h3>📊 Test Results</h3>
            {self._generate_test_results_html()}
        </div>
    </div>
</body>
</html>
        """
    
    def _generate_system_info_html(self) -> str:
        """Generate system information HTML"""
        html = ""
        for key, value in self.report_data['system_info'].items():
            html += f"<p><strong>{key.replace('_', ' ').title()}:</strong> {value}</p>"
        return html
    
    def _generate_test_results_html(self) -> str:
        """Generate test results HTML"""
        html = ""
        for test in self.report_data['test_results']:
            status_class = test['status'].lower()
            html += f"""
            <div class="test-item {status_class}">
                <h4>{test['test_name']} - {test['status']}</h4>
                <p><strong>Description:</strong> {test.get('description', 'N/A')}</p>
                <p><strong>Duration:</strong> <span class="{self._get_performance_class(test.get('duration_ms', 0))}">{test.get('duration_ms', 0):.0f}ms</span></p>
                <p><strong>Performance Level:</strong> {test.get('performance_level', 'Unknown')}</p>
                
                {self._generate_test_steps_html(test.get('steps', []))}
                {self._generate_test_screenshots_html(test.get('screenshots', []))}
                
                <p class="timestamp">Executed: {test.get('start_time', 'Unknown')}</p>
            </div>
            """
        return html
    
    def _generate_test_steps_html(self, steps) -> str:
        """Generate test steps HTML"""
        if not steps:
            return ""
        
        html = "<div class='steps'><h5>📝 Test Steps:</h5><ul>"
        for step in steps:
            duration = step.get('duration_ms', 0)
            performance_class = self._get_performance_class(duration)
            html += f"<li>{step['description']} <span class='{performance_class}'>({duration:.0f}ms)</span></li>"
        html += "</ul></div>"
        return html
    
    def _generate_test_screenshots_html(self, screenshots) -> str:
        """Generate screenshots HTML"""
        if not screenshots:
            return ""
        
        html = "<div><h5>📸 Screenshots:</h5>"
        for screenshot in screenshots:
            html += f"<p><strong>{screenshot['type']}:</strong> {screenshot['path']}</p>"
        html += "</div>"
        return html
    
    def _get_performance_class(self, duration_ms: float) -> str:
        """Get CSS class for performance level"""
        if duration_ms > 5000:
            return "performance-critical"
        elif duration_ms > 3000:
            return "performance-warning"
        else:
            return "performance-good"
    
    def save_json_report(self) -> str:
        """Save detailed JSON report for programmatic access"""
        timestamp = datetime.now().strftime('%Y-%m-%d_%H-%M-%S')
        json_filename = f"Python_Selenium_Report_{timestamp}.json"
        json_path = self.report_dir / json_filename
        
        # Add execution summary
        self.report_data['execution_summary'] = {
            'total_tests': self.total_tests,
            'passed_tests': self.passed_tests,
            'failed_tests': self.failed_tests,
            'skipped_tests': self.skipped_tests,
            'pass_percentage': (self.passed_tests / self.total_tests * 100) if self.total_tests > 0 else 0,
            'total_execution_time_ms': self.total_execution_time,
            'average_execution_time_ms': (self.total_execution_time / self.total_tests) if self.total_tests > 0 else 0,
            'report_generation_time': datetime.now().isoformat()
        }
        
        with open(json_path, 'w', encoding='utf-8') as f:
            json.dump(self.report_data, f, indent=2, ensure_ascii=False)
        
        logger.info(f"JSON report saved: {json_path}")
        return str(json_path)

# Global instance for easy access
enhanced_report_manager = EnhancedReportManager()
