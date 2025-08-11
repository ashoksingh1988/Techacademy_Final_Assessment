package com.selenium.tests;

import com.selenium.core.BaseTest;
import com.selenium.pages.InventoryPage;
import com.selenium.pages.LoginPage;
import com.selenium.utils.DataReader;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

/**
 * Test class for SauceDemo application containing all test cases as per capstone requirements
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class SauceDemoTests extends BaseTest {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    @Test(priority = 1, description = "1. Launch URL and verify Title using Assertions")
    public void verifyWebsiteTitle() {
        logger.info("Test 1: Verifying website title");

        try {
            loginPage = new LoginPage(driver);

            // Verify page is loaded
            Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page is not displayed");
            logger.info("✓ Login page is displayed successfully");

            // Verify page title using assertions
            String actualTitle = loginPage.getPageTitle();
            String expectedTitle = "Swag Labs";

            Assert.assertEquals(actualTitle, expectedTitle, "Page title does not match");
            logger.info("✓ Page title verified successfully. Expected: {}, Actual: {}", expectedTitle, actualTitle);

        } catch (Exception e) {
            logger.error("✗ Test failed: {}", e.getMessage());
            captureScreenshot("verifyWebsiteTitle");
            throw e;
        }
    }

    @Test(priority = 2, description = "2. Login with valid credentials and verify successful login")
    public void loginWithValidCredentials(ITestContext context) {
        logger.info("Test 2: Login with valid credentials");

        try {
            loginPage = new LoginPage(driver);
            inventoryPage = new InventoryPage(driver);

            // Read credentials from TestNG parameters
            String username = DataReader.getUsername(context);
            String password = DataReader.getPassword(context);

            logger.info("Using credentials - Username: {}", username);

            // Perform login
            loginPage.login(username, password);
            logger.info("Login attempted with valid credentials");

            // Verify login success
            Assert.assertTrue(loginPage.isLoginSuccessful(), "Login was not successful");
            logger.info("✓ Login successful - redirected to inventory page");

            // Verify inventory page is displayed
            Assert.assertTrue(inventoryPage.isInventoryPageDisplayed(), "Inventory page is not displayed");
            logger.info("✓ Inventory page is displayed successfully");

            // Verify page title after login
            String inventoryPageTitle = inventoryPage.getInventoryPageTitle();
            Assert.assertEquals(inventoryPageTitle, "Products", "Inventory page title does not match");
            logger.info("✓ Inventory page title verified: {}", inventoryPageTitle);

        } catch (Exception e) {
            logger.error("✗ Test failed: {}", e.getMessage());
            captureScreenshot("loginWithValidCredentials");
            throw e;
        }
    }

    @Test(priority = 3, description = "3. Login with invalid credentials and verify login failure")
    public void loginWithInvalidCredentials(ITestContext context) {
        logger.info("Test 3: Login with invalid credentials");

        try {
            loginPage = new LoginPage(driver);

            // Read invalid credentials from TestNG parameters
            String invalidUsername = DataReader.getInvalidUsername(context);
            String invalidPassword = DataReader.getInvalidPassword(context);

            logger.info("Using invalid credentials - Username: {}", invalidUsername);

            // Perform login with invalid credentials
            loginPage.login(invalidUsername, invalidPassword);
            logger.info("Login attempted with invalid credentials");

            // Verify login failure
            Assert.assertFalse(loginPage.isLoginSuccessful(), "Login should not be successful with invalid credentials");
            logger.info("✓ Login failed as expected with invalid credentials");

            // Verify error message is displayed
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message is not displayed");
            logger.info("✓ Error message is displayed");

            // Verify error message text
            String errorMessage = loginPage.getErrorMessage();
            Assert.assertTrue(errorMessage.contains("Username and password do not match"),
                    "Error message does not contain expected text");
            logger.info("✓ Error message verified: {}", errorMessage);

            // Verify user remains on login page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("saucedemo.com") && !currentUrl.contains("inventory"),
                    "User should remain on login page");
            logger.info("✓ User remains on login page as expected");

        } catch (Exception e) {
            logger.error("✗ Test failed: {}", e.getMessage());
            captureScreenshot("loginWithInvalidCredentials");
            throw e;
        }
    }

    @Test(priority = 4, description = "5. Verify positive case navigation and title using TestNG assertions")
    public void verifyPositiveCaseNavigationAndTitle(ITestContext context) {
        logger.info("Test 4: Verify positive case navigation and title");

        try {
            loginPage = new LoginPage(driver);
            inventoryPage = new InventoryPage(driver);

            // Read valid credentials from TestNG parameters
            String username = DataReader.getUsername(context);
            String password = DataReader.getPassword(context);

            // Perform login
            loginPage.login(username, password);
            logger.info("Login performed with valid credentials");

            // Verify navigation to correct page
            String currentUrl = inventoryPage.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("inventory.html"),
                    "User is not navigated to the correct inventory page");
            logger.info("✓ User navigated to correct page: {}", currentUrl);

            // Verify page title using TestNG assertions
            String pageTitle = inventoryPage.getPageTitle();
            Assert.assertEquals(pageTitle, "Swag Labs", "Page title does not match expected value");
            logger.info("✓ Page title verified using TestNG assertions: {}", pageTitle);

            // Additional verification - inventory page elements
            Assert.assertTrue(inventoryPage.isAppLogoDisplayed(), "App logo is not displayed");
            Assert.assertTrue(inventoryPage.isShoppingCartDisplayed(), "Shopping cart is not displayed");
            Assert.assertTrue(inventoryPage.isInventoryListDisplayed(), "Inventory list is not displayed");
            logger.info("✓ All inventory page elements are displayed correctly");

        } catch (Exception e) {
            logger.error("✗ Test failed: {}", e.getMessage());
            captureScreenshot("verifyPositiveCaseNavigationAndTitle");
            throw e;
        }
    }

    @Test(priority = 5, description = "6. Verify negative case navigation and title using TestNG assertions")
    public void verifyNegativeCaseNavigationAndTitle(ITestContext context) {
        logger.info("Test 5: Verify negative case navigation and title");

        try {
            loginPage = new LoginPage(driver);

            // Read invalid credentials from TestNG parameters
            String invalidUsername = DataReader.getInvalidUsername(context);
            String invalidPassword = DataReader.getInvalidPassword(context);

            // Perform login with invalid credentials
            loginPage.login(invalidUsername, invalidPassword);
            logger.info("Login attempted with invalid credentials");

            // Verify user is NOT navigated to inventory page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertFalse(currentUrl.contains("inventory.html"),
                    "User should not be navigated to inventory page with invalid credentials");
            logger.info("✓ User is not navigated to inventory page as expected: {}", currentUrl);

            // Verify user remains on login page
            Assert.assertTrue(currentUrl.contains("saucedemo.com") && !currentUrl.contains("inventory"),
                    "User should remain on login page");
            logger.info("✓ User remains on login page");

            // Verify page title using TestNG assertions (should still be login page title)
            String pageTitle = loginPage.getPageTitle();
            Assert.assertEquals(pageTitle, "Swag Labs", "Page title should remain as login page title");
            logger.info("✓ Page title verified using TestNG assertions (remains on login page): {}", pageTitle);

            // Verify login page elements are still displayed
            Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login page should still be displayed");
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
            logger.info("✓ Login page elements are displayed correctly after failed login");

        } catch (Exception e) {
            logger.error("✗ Test failed: {}", e.getMessage());
            captureScreenshot("verifyNegativeCaseNavigationAndTitle");
            throw e;
        }
    }
}
