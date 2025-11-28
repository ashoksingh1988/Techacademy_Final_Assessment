from playwright.sync_api import Page
from pages.base_page import BasePage


class LoginPage(BasePage):
    """Login page object"""
    
    # Locators
    USERNAME_INPUT = "#user-name"
    PASSWORD_INPUT = "#password"
    LOGIN_BUTTON = "#login-button"
    ERROR_MESSAGE = "[data-test='error']"
    ERROR_BUTTON = ".error-button"
    
    def __init__(self, page: Page):
        super().__init__(page)
    
    def login(self, username: str, password: str):
        """Perform login with username and password"""
        self.fill(self.USERNAME_INPUT, username)
        self.fill(self.PASSWORD_INPUT, password)
        self.click(self.LOGIN_BUTTON)
        self.wait(0.5)
    
    def is_login_page_displayed(self) -> bool:
        """Check if login page is displayed"""
        return self.is_visible(self.LOGIN_BUTTON)
    
    def is_error_displayed(self) -> bool:
        """Check if error message is displayed"""
        return self.is_visible(self.ERROR_MESSAGE)
    
    def get_error_message(self) -> str:
        """Get error message text"""
        if self.is_error_displayed():
            return self.get_text(self.ERROR_MESSAGE)
        return ""
    
    def close_error_message(self):
        """Close error message"""
        if self.is_visible(self.ERROR_BUTTON):
            self.click(self.ERROR_BUTTON)
