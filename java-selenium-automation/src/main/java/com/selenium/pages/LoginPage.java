package com.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Page Object Model for SauceDemo Login Page
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class LoginPage {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Page Elements using @FindBy annotations
    @FindBy(id = "user-name")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "login-button")
    private WebElement loginButton;
    
    @FindBy(xpath = "//h3[@data-test='error']")
    private WebElement errorMessage;
    
    @FindBy(className = "login_logo")
    private WebElement loginLogo;
    
    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
        logger.debug("LoginPage initialized");
    }
    
    /**
     * Get page title
     * @return Page title
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Page title: {}", title);
        return title;
    }
    
    /**
     * Check if login page is displayed
     * @return true if login page is displayed
     */
    public boolean isLoginPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginLogo));
            boolean isDisplayed = loginLogo.isDisplayed();
            logger.debug("Login page displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.warn("Login page not displayed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Enter username
     * @param username Username to enter
     */
    public void enterUsername(String username) {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            usernameField.clear();
            usernameField.sendKeys(username);
            logger.debug("Username entered: {}", username);
        } catch (Exception e) {
            logger.error("Failed to enter username: {}", e.getMessage());
            throw new RuntimeException("Failed to enter username", e);
        }
    }
    
    /**
     * Enter password
     * @param password Password to enter
     */
    public void enterPassword(String password) {
        try {
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            passwordField.clear();
            passwordField.sendKeys(password);
            logger.debug("Password entered");
        } catch (Exception e) {
            logger.error("Failed to enter password: {}", e.getMessage());
            throw new RuntimeException("Failed to enter password", e);
        }
    }
    
    /**
     * Click login button
     */
    public void clickLoginButton() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            loginButton.click();
            logger.debug("Login button clicked");
        } catch (Exception e) {
            logger.error("Failed to click login button: {}", e.getMessage());
            throw new RuntimeException("Failed to click login button", e);
        }
    }
    
    /**
     * Perform login with credentials
     * @param username Username
     * @param password Password
     */
    public void login(String username, String password) {
        logger.info("Performing login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        logger.info("Login attempt completed");
    }
    
    /**
     * Get error message text
     * @return Error message text
     */
    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            String message = errorMessage.getText();
            logger.debug("Error message: {}", message);
            return message;
        } catch (Exception e) {
            logger.warn("No error message found: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Check if error message is displayed
     * @return true if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        try {
            boolean isDisplayed = errorMessage.isDisplayed();
            logger.debug("Error message displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Error message not displayed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if login was successful by checking URL
     * @return true if login was successful
     */
    public boolean isLoginSuccessful() {
        try {
            wait.until(ExpectedConditions.urlContains("inventory"));
            boolean isSuccessful = driver.getCurrentUrl().contains("inventory");
            logger.debug("Login successful: {}", isSuccessful);
            return isSuccessful;
        } catch (Exception e) {
            logger.debug("Login not successful: {}", e.getMessage());
            return false;
        }
    }
}
