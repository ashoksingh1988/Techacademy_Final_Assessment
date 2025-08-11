package com.appium.pages;

import com.appium.core.DriverFactory;
import com.appium.utils.WaitHelper;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * BasePage - Foundation class for all page objects
 * Provides common element interaction methods and utilities
 * Implements standard page object patterns and best practices
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public abstract class BasePage {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final AndroidDriver driver;
    protected final WaitHelper waitHelper;
    
    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.waitHelper = new WaitHelper(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
        logger.debug("Initialized page: {}", this.getClass().getSimpleName());
    }
    
    // ==================== ELEMENT INTERACTION METHODS ====================
    
    /**
     * Clicks on the specified element with wait
     * 
     * @param element WebElement to click
     */
    protected void click(WebElement element) {
        waitHelper.waitForElementToBeClickable(element);
        element.click();
        logger.debug("Clicked element: {}", getElementDescription(element));
    }
    
    /**
     * Clicks on element located by the specified locator
     * 
     * @param locator By locator to find element
     */
    protected void click(By locator) {
        WebElement element = waitHelper.waitForElementToBeClickable(locator);
        element.click();
        logger.debug("Clicked element with locator: {}", locator);
    }
    
    /**
     * Enters text into the specified element
     * 
     * @param element WebElement to enter text
     * @param text Text to enter
     */
    protected void enterText(WebElement element, String text) {
        waitHelper.waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
        logger.debug("Entered text '{}' into element: {}", text, getElementDescription(element));
    }
    
    /**
     * Enters text into element located by the specified locator
     * 
     * @param locator By locator to find element
     * @param text Text to enter
     */
    protected void enterText(By locator, String text) {
        WebElement element = waitHelper.waitForElementToBeVisible(locator);
        element.clear();
        element.sendKeys(text);
        logger.debug("Entered text '{}' into element with locator: {}", text, locator);
    }
    
    /**
     * Gets text from the specified element
     * 
     * @param element WebElement to get text from
     * @return Element text content
     */
    protected String getText(WebElement element) {
        waitHelper.waitForElementToBeVisible(element);
        String text = element.getText();
        logger.debug("Retrieved text '{}' from element: {}", text, getElementDescription(element));
        return text;
    }
    
    /**
     * Gets text from element located by the specified locator
     * 
     * @param locator By locator to find element
     * @return Element text content
     */
    protected String getText(By locator) {
        WebElement element = waitHelper.waitForElementToBeVisible(locator);
        String text = element.getText();
        logger.debug("Retrieved text '{}' from element with locator: {}", text, locator);
        return text;
    }
    
    /**
     * Checks if element is displayed
     * 
     * @param element WebElement to check
     * @return true if element is displayed
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            logger.debug("Element not displayed: {}", getElementDescription(element));
            return false;
        }
    }
    
    /**
     * Checks if element is enabled
     * 
     * @param element WebElement to check
     * @return true if element is enabled
     */
    protected boolean isEnabled(WebElement element) {
        try {
            waitHelper.waitForElementToBeVisible(element);
            return element.isEnabled();
        } catch (Exception e) {
            logger.debug("Element not enabled: {}", getElementDescription(element));
            return false;
        }
    }
    
    // ==================== MOBILE-SPECIFIC METHODS ====================
    
    /**
     * Finds element by accessibility ID
     * 
     * @param accessibilityId Accessibility ID
     * @return WebElement found by accessibility ID
     */
    protected WebElement findByAccessibilityId(String accessibilityId) {
        return driver.findElement(AppiumBy.accessibilityId(accessibilityId));
    }
    
    /**
     * Finds elements by accessibility ID
     * 
     * @param accessibilityId Accessibility ID
     * @return List of WebElements found by accessibility ID
     */
    protected List<WebElement> findElementsByAccessibilityId(String accessibilityId) {
        return driver.findElements(AppiumBy.accessibilityId(accessibilityId));
    }
    
    /**
     * Finds element using Android UIAutomator selector
     * 
     * @param uiAutomatorSelector UIAutomator selector string
     * @return WebElement found by UIAutomator
     */
    protected WebElement findByUIAutomator(String uiAutomatorSelector) {
        return driver.findElement(AppiumBy.androidUIAutomator(uiAutomatorSelector));
    }
    
    /**
     * Finds elements using Android UIAutomator selector
     * 
     * @param uiAutomatorSelector UIAutomator selector string
     * @return List of WebElements found by UIAutomator
     */
    protected List<WebElement> findElementsByUIAutomator(String uiAutomatorSelector) {
        return driver.findElements(AppiumBy.androidUIAutomator(uiAutomatorSelector));
    }
    
    // ==================== GESTURE METHODS ====================
    
    /**
     * Scrolls down on the screen
     */
    protected void scrollDown() {
        driver.executeScript("mobile: scrollGesture", 
            java.util.Map.of(
                "left", 100, "top", 100, 
                "width", 200, "height", 200, 
                "direction", "down", "percent", 3.0
            ));
        logger.debug("Performed scroll down gesture");
    }
    
    /**
     * Scrolls up on the screen
     */
    protected void scrollUp() {
        driver.executeScript("mobile: scrollGesture", 
            java.util.Map.of(
                "left", 100, "top", 100, 
                "width", 200, "height", 200, 
                "direction", "up", "percent", 3.0
            ));
        logger.debug("Performed scroll up gesture");
    }
    
    /**
     * Swipes left on the screen
     */
    protected void swipeLeft() {
        driver.executeScript("mobile: swipeGesture", 
            java.util.Map.of(
                "left", 100, "top", 100, 
                "width", 200, "height", 200, 
                "direction", "left", "percent", 0.75
            ));
        logger.debug("Performed swipe left gesture");
    }
    
    /**
     * Swipes right on the screen
     */
    protected void swipeRight() {
        driver.executeScript("mobile: swipeGesture", 
            java.util.Map.of(
                "left", 100, "top", 100, 
                "width", 200, "height", 200, 
                "direction", "right", "percent", 0.75
            ));
        logger.debug("Performed swipe right gesture");
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Hides keyboard if visible
     */
    protected void hideKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
                logger.debug("Keyboard hidden");
            }
        } catch (Exception e) {
            logger.debug("Keyboard not visible or error hiding keyboard: {}", e.getMessage());
        }
    }
    
    /**
     * Gets current activity name
     * 
     * @return Current activity name
     */
    public String getCurrentActivity() {
        return driver.currentActivity();
    }
    
    /**
     * Gets current package name
     * 
     * @return Current package name
     */
    protected String getCurrentPackage() {
        return driver.getCurrentPackage();
    }
    
    /**
     * Gets element description for logging
     */
    private String getElementDescription(WebElement element) {
        try {
            return element.toString();
        } catch (Exception e) {
            return "Unknown element";
        }
    }
    
    /**
     * Waits for page to be loaded (to be implemented by subclasses)
     * 
     * @return true if page is loaded
     */
    public abstract boolean isPageLoaded();
}
