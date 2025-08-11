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
 * Page Object Model for SauceDemo Inventory/Products Page
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class InventoryPage {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryPage.class);
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Page Elements using @FindBy annotations
    @FindBy(className = "title")
    private WebElement pageTitle;
    
    @FindBy(className = "app_logo")
    private WebElement appLogo;
    
    @FindBy(id = "shopping_cart_container")
    private WebElement shoppingCart;
    
    @FindBy(className = "inventory_list")
    private WebElement inventoryList;
    
    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;
    
    // Constructor
    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
        logger.debug("InventoryPage initialized");
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
     * Check if inventory page is displayed
     * @return true if inventory page is displayed
     */
    public boolean isInventoryPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            boolean isDisplayed = pageTitle.isDisplayed() && pageTitle.getText().equals("Products");
            logger.debug("Inventory page displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.warn("Inventory page not displayed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get inventory page title text
     * @return Page title text
     */
    public String getInventoryPageTitle() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitle));
            String title = pageTitle.getText();
            logger.debug("Inventory page title: {}", title);
            return title;
        } catch (Exception e) {
            logger.warn("Failed to get inventory page title: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Check if app logo is displayed
     * @return true if app logo is displayed
     */
    public boolean isAppLogoDisplayed() {
        try {
            boolean isDisplayed = appLogo.isDisplayed();
            logger.debug("App logo displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("App logo not displayed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if shopping cart is displayed
     * @return true if shopping cart is displayed
     */
    public boolean isShoppingCartDisplayed() {
        try {
            boolean isDisplayed = shoppingCart.isDisplayed();
            logger.debug("Shopping cart displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Shopping cart not displayed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if inventory list is displayed
     * @return true if inventory list is displayed
     */
    public boolean isInventoryListDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(inventoryList));
            boolean isDisplayed = inventoryList.isDisplayed();
            logger.debug("Inventory list displayed: {}", isDisplayed);
            return isDisplayed;
        } catch (Exception e) {
            logger.debug("Inventory list not displayed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current URL
     * @return Current URL
     */
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }
}
