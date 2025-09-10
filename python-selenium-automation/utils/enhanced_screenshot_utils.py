"""
Enhanced Screenshot Utils for Python Selenium - Comprehensive screenshot management
Features: Success/Failure Screenshots, Performance Tracking, Organized Storage

@author: Asim Kumar Singh
@version: 2.0.0 - Enhanced for Lead Review Requirements
"""

import os
import time
import logging
from datetime import datetime
from pathlib import Path
from typing import Optional, Dict, Any
from selenium import webdriver
from selenium.webdriver.common.by import By
from PIL import Image
import base64

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class EnhancedScreenshotUtils:
    """Enhanced screenshot utilities with comprehensive organization and performance tracking"""
    
    def __init__(self):
        self.screenshot_format = "%Y-%m-%d_%H-%M-%S-%f"
        self.report_dir = Path("reports")
        self.screenshot_base_dir = self.report_dir / "screenshots"
        
        # Create organized directories
        self.success_dir = self.screenshot_base_dir / "success"
        self.failure_dir = self.screenshot_base_dir / "failure"
        self.step_dir = self.screenshot_base_dir / "steps"
        
        self._create_directories()
        
        # Statistics tracking
        self.screenshot_stats = {
            'success_count': 0,
            'failure_count': 0,
            'step_count': 0,
            'total_capture_time_ms': 0
        }
    
    def _create_directories(self):
        """Create necessary directories for organized screenshot storage"""
        directories = [
            self.report_dir,
            self.screenshot_base_dir,
            self.success_dir,
            self.failure_dir,
            self.step_dir
        ]
        
        for directory in directories:
            directory.mkdir(parents=True, exist_ok=True)
            logger.info(f"Screenshot directory ensured: {directory}")
    
    def capture_success_screenshot(self, driver: webdriver, test_name: str, step_description: str) -> Optional[str]:
        """Capture screenshot for successful test scenarios"""
        return self._capture_screenshot(
            driver=driver,
            test_name=test_name,
            description=step_description,
            screenshot_type="SUCCESS",
            target_dir=self.success_dir
        )
    
    def capture_failure_screenshot(self, driver: webdriver, test_name: str, error_description: str) -> Optional[str]:
        """Capture screenshot for failed test scenarios"""
        return self._capture_screenshot(
            driver=driver,
            test_name=test_name,
            description=error_description,
            screenshot_type="FAILURE",
            target_dir=self.failure_dir
        )
    
    def capture_step_screenshot(self, driver: webdriver, test_name: str, step_description: str) -> Optional[str]:
        """Capture screenshot for test steps (detailed documentation)"""
        return self._capture_screenshot(
            driver=driver,
            test_name=test_name,
            description=step_description,
            screenshot_type="STEP",
            target_dir=self.step_dir
        )
    
    def _capture_screenshot(self, driver: webdriver, test_name: str, description: str, 
                          screenshot_type: str, target_dir: Path) -> Optional[str]:
        """Generic screenshot capture method with enhanced organization"""
        try:
            start_time = time.time()
            
            # Clean names for filename
            clean_test_name = self._clean_filename(test_name)
            clean_description = self._clean_filename(description)
            
            # Create timestamp for unique filename
            timestamp = datetime.now().strftime(self.screenshot_format)[:-3]  # Remove microseconds
            
            # Create filename with enhanced naming convention
            filename = f"{screenshot_type}_{clean_test_name}_{clean_description}_{timestamp}.png"
            
            # Full file path
            file_path = target_dir / filename
            
            # Capture screenshot
            screenshot_taken = driver.save_screenshot(str(file_path))
            
            if screenshot_taken:
                # Calculate capture time
                capture_time_ms = (time.time() - start_time) * 1000
                self.screenshot_stats['total_capture_time_ms'] += capture_time_ms
                
                # Update statistics
                if screenshot_type == "SUCCESS":
                    self.screenshot_stats['success_count'] += 1
                elif screenshot_type == "FAILURE":
                    self.screenshot_stats['failure_count'] += 1
                elif screenshot_type == "STEP":
                    self.screenshot_stats['step_count'] += 1
                
                # Log success
                logger.info(f"Screenshot captured: {screenshot_type} - {file_path} ({capture_time_ms:.0f}ms)")
                
                # Return relative path for HTML report
                return self._get_relative_path_for_report(str(file_path))
            else:
                logger.error(f"Failed to save screenshot: {file_path}")
                return None
                
        except Exception as e:
            logger.error(f"Error capturing screenshot for {test_name}: {str(e)}")
            return None
    
    def capture_screenshot_with_custom_name(self, driver: webdriver, custom_filename: str) -> Optional[str]:
        """Capture screenshot with custom filename"""
        try:
            start_time = time.time()
            
            timestamp = datetime.now().strftime(self.screenshot_format)[:-3]
            filename = f"{self._clean_filename(custom_filename)}_{timestamp}.png"
            
            file_path = self.screenshot_base_dir / filename
            
            screenshot_taken = driver.save_screenshot(str(file_path))
            
            if screenshot_taken:
                capture_time_ms = (time.time() - start_time) * 1000
                self.screenshot_stats['total_capture_time_ms'] += capture_time_ms
                
                logger.info(f"Custom screenshot captured: {file_path} ({capture_time_ms:.0f}ms)")
                return self._get_relative_path_for_report(str(file_path))
            else:
                logger.error(f"Failed to save custom screenshot: {file_path}")
                return None
                
        except Exception as e:
            logger.error(f"Error capturing custom screenshot: {str(e)}")
            return None
    
    def capture_full_page_screenshot(self, driver: webdriver, test_name: str, description: str) -> Optional[str]:
        """Capture full page screenshot (if supported by driver)"""
        try:
            # For Chrome, we can use execute_cdp_cmd for full page screenshots
            if hasattr(driver, 'execute_cdp_cmd'):
                # Get page dimensions
                page_rect = driver.execute_cdp_cmd('Page.getLayoutMetrics', {})
                screenshot_config = {
                    'captureBeyondViewport': True,
                    'fromSurface': True,
                    'clip': {
                        'x': 0,
                        'y': 0,
                        'width': page_rect['contentSize']['width'],
                        'height': page_rect['contentSize']['height'],
                        'scale': 1
                    }
                }
                
                result = driver.execute_cdp_cmd('Page.captureScreenshot', screenshot_config)
                screenshot_data = result['data']
                
                # Save full page screenshot
                clean_test_name = self._clean_filename(test_name)
                clean_description = self._clean_filename(description)
                timestamp = datetime.now().strftime(self.screenshot_format)[:-3]
                filename = f"FULLPAGE_{clean_test_name}_{clean_description}_{timestamp}.png"
                
                fullpage_dir = self.screenshot_base_dir / "fullpage"
                fullpage_dir.mkdir(exist_ok=True)
                file_path = fullpage_dir / filename
                
                # Decode and save
                with open(file_path, 'wb') as f:
                    f.write(base64.b64decode(screenshot_data))
                
                logger.info(f"Full page screenshot captured: {file_path}")
                return self._get_relative_path_for_report(str(file_path))
            else:
                # Fallback to regular screenshot
                return self.capture_step_screenshot(driver, test_name, f"{description}_FULLPAGE")
                
        except Exception as e:
            logger.error(f"Error capturing full page screenshot: {str(e)}")
            # Fallback to regular screenshot
            return self.capture_step_screenshot(driver, test_name, f"{description}_FULLPAGE")
    
    def capture_element_screenshot(self, driver: webdriver, element, test_name: str, element_description: str) -> Optional[str]:
        """Capture screenshot of a specific element"""
        try:
            start_time = time.time()
            
            clean_test_name = self._clean_filename(test_name)
            clean_description = self._clean_filename(element_description)
            timestamp = datetime.now().strftime(self.screenshot_format)[:-3]
            filename = f"ELEMENT_{clean_test_name}_{clean_description}_{timestamp}.png"
            
            element_dir = self.screenshot_base_dir / "elements"
            element_dir.mkdir(exist_ok=True)
            file_path = element_dir / filename
            
            # Capture element screenshot
            element_screenshot = element.screenshot_as_png
            
            with open(file_path, 'wb') as f:
                f.write(element_screenshot)
            
            capture_time_ms = (time.time() - start_time) * 1000
            self.screenshot_stats['total_capture_time_ms'] += capture_time_ms
            
            logger.info(f"Element screenshot captured: {file_path} ({capture_time_ms:.0f}ms)")
            return self._get_relative_path_for_report(str(file_path))
            
        except Exception as e:
            logger.error(f"Error capturing element screenshot: {str(e)}")
            return None
    
    def capture_screenshot_with_timing(self, driver: webdriver, test_name: str, description: str) -> Dict[str, Any]:
        """Capture screenshot with detailed timing information"""
        start_time = time.time()
        screenshot_path = self.capture_success_screenshot(driver, test_name, description)
        end_time = time.time()
        
        capture_time_ms = (end_time - start_time) * 1000
        
        result = {
            'screenshot_path': screenshot_path,
            'capture_time_ms': capture_time_ms,
            'success': screenshot_path is not None,
            'timestamp': datetime.now().isoformat()
        }
        
        logger.info(f"Screenshot capture timing: {capture_time_ms:.0f}ms for {test_name}")
        return result
    
    def _clean_filename(self, filename: str) -> str:
        """Clean filename to remove invalid characters"""
        if not filename or not filename.strip():
            return "unnamed"
        
        # Remove or replace invalid characters
        cleaned = "".join(c if c.isalnum() or c in "._-" else "_" for c in filename)
        # Replace multiple underscores with single
        cleaned = "_".join(filter(None, cleaned.split("_")))
        # Limit length
        return cleaned[:50] if len(cleaned) > 50 else cleaned
    
    def _get_relative_path_for_report(self, absolute_path: str) -> str:
        """Convert absolute path to relative path for HTML report"""
        if "reports" in absolute_path:
            reports_index = absolute_path.find("reports")
            return absolute_path[reports_index:].replace("\\", "/")
        return absolute_path.replace("\\", "/")
    
    def get_screenshot_statistics(self) -> Dict[str, Any]:
        """Get comprehensive screenshot statistics"""
        total_screenshots = (self.screenshot_stats['success_count'] + 
                           self.screenshot_stats['failure_count'] + 
                           self.screenshot_stats['step_count'])
        
        avg_capture_time = (self.screenshot_stats['total_capture_time_ms'] / total_screenshots 
                          if total_screenshots > 0 else 0)
        
        return {
            'success_count': self.screenshot_stats['success_count'],
            'failure_count': self.screenshot_stats['failure_count'],
            'step_count': self.screenshot_stats['step_count'],
            'total_count': total_screenshots,
            'total_capture_time_ms': self.screenshot_stats['total_capture_time_ms'],
            'average_capture_time_ms': avg_capture_time,
            'directories': {
                'success': str(self.success_dir),
                'failure': str(self.failure_dir),
                'steps': str(self.step_dir)
            }
        }
    
    def cleanup_old_screenshots(self, days_to_keep: int = 7):
        """Clean up old screenshots (keeps only recent ones)"""
        cutoff_time = time.time() - (days_to_keep * 24 * 60 * 60)
        
        directories_to_clean = [
            self.success_dir,
            self.failure_dir,
            self.step_dir,
            self.screenshot_base_dir / "fullpage",
            self.screenshot_base_dir / "elements"
        ]
        
        total_deleted = 0
        
        for directory in directories_to_clean:
            if directory.exists():
                for file_path in directory.glob("*.png"):
                    if file_path.stat().st_mtime < cutoff_time:
                        try:
                            file_path.unlink()
                            total_deleted += 1
                            logger.info(f"Deleted old screenshot: {file_path.name}")
                        except Exception as e:
                            logger.error(f"Failed to delete {file_path}: {str(e)}")
        
        logger.info(f"Cleanup completed: {total_deleted} old screenshots deleted")
        return total_deleted
    
    def compress_screenshots(self, quality: int = 85):
        """Compress existing screenshots to reduce file size"""
        directories_to_compress = [
            self.success_dir,
            self.failure_dir,
            self.step_dir
        ]
        
        total_compressed = 0
        total_size_saved = 0
        
        for directory in directories_to_compress:
            if directory.exists():
                for file_path in directory.glob("*.png"):
                    try:
                        # Get original size
                        original_size = file_path.stat().st_size
                        
                        # Open and compress image
                        with Image.open(file_path) as img:
                            # Convert to RGB if necessary (PNG with transparency)
                            if img.mode in ('RGBA', 'LA'):
                                background = Image.new('RGB', img.size, (255, 255, 255))
                                background.paste(img, mask=img.split()[-1] if img.mode == 'RGBA' else None)
                                img = background
                            
                            # Save as JPEG with compression
                            compressed_path = file_path.with_suffix('.jpg')
                            img.save(compressed_path, 'JPEG', quality=quality, optimize=True)
                        
                        # Get compressed size
                        compressed_size = compressed_path.stat().st_size
                        size_saved = original_size - compressed_size
                        
                        if size_saved > 0:
                            # Remove original PNG and rename JPEG to PNG
                            file_path.unlink()
                            compressed_path.rename(file_path.with_suffix('.png'))
                            
                            total_compressed += 1
                            total_size_saved += size_saved
                            
                            logger.info(f"Compressed {file_path.name}: saved {size_saved} bytes")
                        else:
                            # Remove compressed version if no savings
                            compressed_path.unlink()
                            
                    except Exception as e:
                        logger.error(f"Failed to compress {file_path}: {str(e)}")
        
        logger.info(f"Compression completed: {total_compressed} files compressed, {total_size_saved} bytes saved")
        return {'files_compressed': total_compressed, 'bytes_saved': total_size_saved}

# Global instance for easy access
enhanced_screenshot_utils = EnhancedScreenshotUtils()
