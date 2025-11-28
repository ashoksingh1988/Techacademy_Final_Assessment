from playwright.sync_api import Page, expect
from typing import Optional
import time


class BasePage:
    """Base page class with common methods for all pages"""
    
    def __init__(self, page: Page):
        self.page = page
    
    def navigate_to(self, url: str):
        """Navigate to a URL"""
        self.page.goto(url)
        self.page.wait_for_load_state("networkidle")
    
    def click(self, selector: str):
        """Click on an element"""
        self.page.click(selector)
    
    def fill(self, selector: str, text: str):
        """Fill input field with text"""
        self.page.fill(selector, text)
    
    def get_text(self, selector: str) -> str:
        """Get text content of an element"""
        return self.page.text_content(selector)
    
    def is_visible(self, selector: str, timeout: int = 5000) -> bool:
        """Check if element is visible"""
        try:
            self.page.wait_for_selector(selector, state="visible", timeout=timeout)
            return True
        except:
            return False
    
    def is_hidden(self, selector: str, timeout: int = 5000) -> bool:
        """Check if element is hidden"""
        try:
            self.page.wait_for_selector(selector, state="hidden", timeout=timeout)
            return True
        except:
            return False
    
    def wait_for_selector(self, selector: str, timeout: int = 10000):
        """Wait for element to appear"""
        self.page.wait_for_selector(selector, timeout=timeout)
    
    def get_attribute(self, selector: str, attribute: str) -> Optional[str]:
        """Get attribute value of an element"""
        return self.page.get_attribute(selector, attribute)
    
    def get_current_url(self) -> str:
        """Get current page URL"""
        return self.page.url
    
    def get_title(self) -> str:
        """Get page title"""
        return self.page.title()
    
    def wait(self, seconds: float):
        """Explicit wait for seconds"""
        time.sleep(seconds)
    
    def scroll_to_element(self, selector: str):
        """Scroll to element"""
        self.page.locator(selector).scroll_into_view_if_needed()
    
    def get_element_count(self, selector: str) -> int:
        """Get count of elements matching selector"""
        return self.page.locator(selector).count()
