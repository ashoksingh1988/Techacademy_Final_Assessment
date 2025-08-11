package com.appium.utils;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * WaitHelper - Provides robust wait strategies for element interactions
 * Implements explicit waits with various conditions for reliable automation
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class WaitHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(WaitHelper.class);
    
    private final AndroidDriver driver;
    private final WebDriverWait defaultWait;
    private final int defaultTimeoutSeconds;
    
    public WaitHelper(AndroidDriver driver) {
        this(driver, 30); // Default 30 seconds timeout
    }
    
    public WaitHelper(AndroidDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.defaultTimeoutSeconds = timeoutSeconds;
        this.defaultWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }
    
    // ==================== ELEMENT VISIBILITY WAITS ====================
    
    /**
     * Waits for element to be visible
     * 
     * @param element WebElement to wait for
     * @return WebElement when visible
     * @throws RuntimeException if element not visible within timeout
     */
    public WebElement waitForElementToBeVisible(WebElement element) {
        try {
            logger.debug("Waiting for element to be visible: {}", element);
            return defaultWait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            logger.error("Element not visible within {} seconds: {}", defaultTimeoutSeconds, element);
            throw new RuntimeException("Element not visible within timeout", e);
        }
    }
    
    /**
     * Waits for element to be visible by locator
     * 
     * @param locator By locator
     * @return WebElement when visible
     * @throws RuntimeException if element not visible within timeout
     */
    public WebElement waitForElementToBeVisible(By locator) {
        try {
            logger.debug("Waiting for element to be visible: {}", locator);
            return defaultWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element not visible within {} seconds: {}", defaultTimeoutSeconds, locator);
            throw new RuntimeException("Element not visible within timeout", e);
        }
    }
    
    /**
     * Waits for all elements to be visible
     * 
     * @param locator By locator
     * @return List of WebElements when visible
     * @throws RuntimeException if elements not visible within timeout
     */
    public List<WebElement> waitForAllElementsToBeVisible(By locator) {
        try {
            logger.debug("Waiting for all elements to be visible: {}", locator);
            return defaultWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            logger.error("Elements not visible within {} seconds: {}", defaultTimeoutSeconds, locator);
            throw new RuntimeException("Elements not visible within timeout", e);
        }
    }
    
    // ==================== ELEMENT CLICKABILITY WAITS ====================
    
    /**
     * Waits for element to be clickable
     * 
     * @param element WebElement to wait for
     * @return WebElement when clickable
     * @throws RuntimeException if element not clickable within timeout
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        try {
            logger.debug("Waiting for element to be clickable: {}", element);
            return defaultWait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            logger.error("Element not clickable within {} seconds: {}", defaultTimeoutSeconds, element);
            throw new RuntimeException("Element not clickable within timeout", e);
        }
    }
    
    /**
     * Waits for element to be clickable by locator
     * 
     * @param locator By locator
     * @return WebElement when clickable
     * @throws RuntimeException if element not clickable within timeout
     */
    public WebElement waitForElementToBeClickable(By locator) {
        try {
            logger.debug("Waiting for element to be clickable: {}", locator);
            return defaultWait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            logger.error("Element not clickable within {} seconds: {}", defaultTimeoutSeconds, locator);
            throw new RuntimeException("Element not clickable within timeout", e);
        }
    }
    
    // ==================== ELEMENT PRESENCE WAITS ====================
    
    /**
     * Waits for element to be present in DOM
     * 
     * @param locator By locator
     * @return WebElement when present
     * @throws RuntimeException if element not present within timeout
     */
    public WebElement waitForElementToBePresent(By locator) {
        try {
            logger.debug("Waiting for element to be present: {}", locator);
            return defaultWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element not present within {} seconds: {}", defaultTimeoutSeconds, locator);
            throw new RuntimeException("Element not present within timeout", e);
        }
    }
    
    /**
     * Waits for all elements to be present in DOM
     * 
     * @param locator By locator
     * @return List of WebElements when present
     * @throws RuntimeException if elements not present within timeout
     */
    public List<WebElement> waitForAllElementsToBePresent(By locator) {
        try {
            logger.debug("Waiting for all elements to be present: {}", locator);
            return defaultWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            logger.error("Elements not present within {} seconds: {}", defaultTimeoutSeconds, locator);
            throw new RuntimeException("Elements not present within timeout", e);
        }
    }
    
    // ==================== TEXT-BASED WAITS ====================
    
    /**
     * Waits for element to contain specific text
     * 
     * @param locator By locator
     * @param text Expected text
     * @return true when text is present
     * @throws RuntimeException if text not present within timeout
     */
    public boolean waitForElementToContainText(By locator, String text) {
        try {
            logger.debug("Waiting for element {} to contain text: {}", locator, text);
            return defaultWait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (Exception e) {
            logger.error("Text '{}' not present in element {} within {} seconds", text, locator, defaultTimeoutSeconds);
            throw new RuntimeException("Text not present within timeout", e);
        }
    }
    
    /**
     * Waits for element to contain specific text
     * 
     * @param element WebElement
     * @param text Expected text
     * @return true when text is present
     * @throws RuntimeException if text not present within timeout
     */
    public boolean waitForElementToContainText(WebElement element, String text) {
        try {
            logger.debug("Waiting for element {} to contain text: {}", element, text);
            return defaultWait.until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception e) {
            logger.error("Text '{}' not present in element {} within {} seconds", text, element, defaultTimeoutSeconds);
            throw new RuntimeException("Text not present within timeout", e);
        }
    }
    
    // ==================== INVISIBILITY WAITS ====================
    
    /**
     * Waits for element to become invisible
     * 
     * @param locator By locator
     * @return true when element is invisible
     * @throws RuntimeException if element still visible after timeout
     */
    public boolean waitForElementToBeInvisible(By locator) {
        try {
            logger.debug("Waiting for element to be invisible: {}", locator);
            return defaultWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element still visible after {} seconds: {}", defaultTimeoutSeconds, locator);
            throw new RuntimeException("Element still visible after timeout", e);
        }
    }
    
    /**
     * Waits for element to become invisible
     * 
     * @param element WebElement
     * @return true when element is invisible
     * @throws RuntimeException if element still visible after timeout
     */
    public boolean waitForElementToBeInvisible(WebElement element) {
        try {
            logger.debug("Waiting for element to be invisible: {}", element);
            return defaultWait.until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception e) {
            logger.error("Element still visible after {} seconds: {}", defaultTimeoutSeconds, element);
            throw new RuntimeException("Element still visible after timeout", e);
        }
    }
    
    // ==================== ATTRIBUTE-BASED WAITS ====================
    
    /**
     * Waits for element attribute to contain specific value
     * 
     * @param locator By locator
     * @param attribute Attribute name
     * @param value Expected value
     * @return true when attribute contains value
     * @throws RuntimeException if attribute doesn't contain value within timeout
     */
    public boolean waitForElementAttributeToContain(By locator, String attribute, String value) {
        try {
            logger.debug("Waiting for element {} attribute '{}' to contain: {}", locator, attribute, value);
            return defaultWait.until(ExpectedConditions.attributeContains(locator, attribute, value));
        } catch (Exception e) {
            logger.error("Attribute '{}' doesn't contain '{}' in element {} within {} seconds", 
                        attribute, value, locator, defaultTimeoutSeconds);
            throw new RuntimeException("Attribute condition not met within timeout", e);
        }
    }
    
    // ==================== SELECTION WAITS ====================
    
    /**
     * Waits for element to be selected
     * 
     * @param element WebElement
     * @return true when element is selected
     * @throws RuntimeException if element not selected within timeout
     */
    public boolean waitForElementToBeSelected(WebElement element) {
        try {
            logger.debug("Waiting for element to be selected: {}", element);
            return defaultWait.until(ExpectedConditions.elementToBeSelected(element));
        } catch (Exception e) {
            logger.error("Element not selected within {} seconds: {}", defaultTimeoutSeconds, element);
            throw new RuntimeException("Element not selected within timeout", e);
        }
    }
    
    /**
     * Waits for element to be selected by locator
     * 
     * @param locator By locator
     * @return true when element is selected
     * @throws RuntimeException if element not selected within timeout
     */
    public boolean waitForElementToBeSelected(By locator) {
        try {
            logger.debug("Waiting for element to be selected: {}", locator);
            return defaultWait.until(ExpectedConditions.elementToBeSelected(locator));
        } catch (Exception e) {
            logger.error("Element not selected within {} seconds: {}", defaultTimeoutSeconds, locator);
            throw new RuntimeException("Element not selected within timeout", e);
        }
    }
    
    // ==================== CUSTOM WAITS ====================
    
    /**
     * Waits for custom condition with specified timeout
     * 
     * @param timeoutSeconds Custom timeout in seconds
     * @param condition Custom expected condition
     * @param <T> Return type of the condition
     * @return Result of the condition
     * @throws RuntimeException if condition not met within timeout
     */
    public <T> T waitForCondition(int timeoutSeconds, org.openqa.selenium.support.ui.ExpectedCondition<T> condition) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            logger.debug("Waiting for custom condition with timeout: {} seconds", timeoutSeconds);
            return customWait.until(condition);
        } catch (Exception e) {
            logger.error("Custom condition not met within {} seconds", timeoutSeconds);
            throw new RuntimeException("Custom condition not met within timeout", e);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Simple sleep method (use sparingly - prefer explicit waits)
     * 
     * @param milliseconds Time to sleep in milliseconds
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
    }
    
    /**
     * Gets the default timeout value
     * 
     * @return Default timeout in seconds
     */
    public int getDefaultTimeout() {
        return defaultTimeoutSeconds;
    }
}
