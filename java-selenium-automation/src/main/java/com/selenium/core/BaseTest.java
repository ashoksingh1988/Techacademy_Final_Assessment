package com.selenium.core;

import com.selenium.utils.ConfigurationManager;
import com.selenium.utils.ReportManager;
import com.selenium.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * BaseTest - Foundation class for all test classes
 * Provides common setup, teardown, and utility methods
 * Handles driver lifecycle and test reporting
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public abstract class BaseTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected ConfigurationManager config;
    
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        logger.info("=== Test Suite Setup Started ===");
        config = ConfigurationManager.getInstance();
        ReportManager.initializeReports();
        logger.info("=== Test Suite Setup Completed ===");
    }
    
    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        String className = this.getClass().getSimpleName();
        logger.info("=== Test Class Setup: {} ===", className);
    }
    
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless", "baseUrl"})
    public void testSetup(@Optional("chrome") String browser,
                         @Optional("false") String headless,
                         @Optional("https://www.saucedemo.com") String baseUrl,
                         Method method) {
        
        String testName = method.getName();
        logger.info("=== Test Setup: {} ===", testName);
        
        // Resolve configuration parameters
        browser = resolveParameter("browser", browser, "chrome");
        boolean isHeadless = Boolean.parseBoolean(resolveParameter("headless", headless, "false"));
        baseUrl = resolveParameter("base.url", baseUrl, "https://www.saucedemo.com");
        
        logTestConfiguration(testName, browser, isHeadless, baseUrl);
        
        try {
            // Initialize driver
            driver = DriverManager.createDriver(browser, isHeadless);
            
            // Navigate to base URL
            driver.get(baseUrl);
            logger.info("Navigated to: {}", baseUrl);
            
            // Setup test reporting
            setupTestReporting(method, browser);
            
            logger.info("Test setup completed successfully for: {}", testName);
            
        } catch (Exception e) {
            logger.error("Test setup failed for: {}", testName, e);
            throw new RuntimeException("Test setup failed", e);
        }
    }
    
    @AfterMethod(alwaysRun = true)
    public void testTeardown(ITestResult result) {
        String testName = result.getName();
        
        try {
            handleTestResult(result);
        } catch (Exception e) {
            logger.warn("Error handling test result for: {}", testName, e);
        } finally {
            // Always quit driver
            DriverManager.quitDriver();
            logger.info("=== Test Teardown Completed: {} ===", testName);
        }
    }
    
    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        String className = this.getClass().getSimpleName();
        logger.info("=== Test Class Teardown: {} ===", className);
    }
    
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        logger.info("=== Test Suite Teardown Started ===");
        
        try {
            // Ensure all drivers are quit
            DriverManager.quitAllDrivers();
            
            // Finalize reports
            ReportManager.flushReports();
            
            // Log summary
            logSuiteSummary();
            
        } catch (Exception e) {
            logger.error("Error during suite teardown", e);
        } finally {
            logger.info("=== Test Suite Teardown Completed ===");
        }
    }
    
    /**
     * Resolves parameter value from multiple sources
     */
    private String resolveParameter(String configKey, String parameterValue, String defaultValue) {
        if (parameterValue != null && !parameterValue.trim().isEmpty()) {
            return parameterValue.trim();
        }
        
        String systemProperty = System.getProperty(configKey);
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty.trim();
        }
        
        String configValue = config.getProperty(configKey);
        if (configValue != null && !configValue.trim().isEmpty()) {
            return configValue.trim();
        }
        
        return defaultValue;
    }
    
    /**
     * Logs test configuration details
     */
    private void logTestConfiguration(String testName, String browser, boolean headless, String baseUrl) {
        logger.info("Test Configuration for: {}", testName);
        logger.info("  Browser: {}", browser);
        logger.info("  Headless: {}", headless);
        logger.info("  Base URL: {}", baseUrl);
        logger.info("  Thread: {}", Thread.currentThread().getName());
    }
    
    /**
     * Sets up test reporting
     */
    private void setupTestReporting(Method method, String browser) {
        String testName = method.getName();
        String description = getTestDescription(method);
        
        ReportManager.createTest(testName, description);
        ReportManager.logInfo("Test started on browser: " + browser);
        ReportManager.assignDevice(browser);
        ReportManager.assignCategory(this.getClass().getSimpleName());
    }
    
    /**
     * Gets test description from annotation or generates default
     */
    private String getTestDescription(Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);
        if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
            return testAnnotation.description();
        }
        return "Test: " + method.getName();
    }
    
    /**
     * Handles test result and reporting
     */
    private void handleTestResult(ITestResult result) {
        String testName = result.getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                logger.info("✅ Test PASSED: {} ({}ms)", testName, duration);
                ReportManager.logPass("Test completed successfully in " + duration + "ms");
                break;
                
            case ITestResult.FAILURE:
                logger.error("❌ Test FAILED: {} ({}ms)", testName, duration);
                handleTestFailure(result, testName);
                break;
                
            case ITestResult.SKIP:
                logger.warn("⏭️ Test SKIPPED: {}", testName);
                String skipReason = result.getThrowable() != null ? 
                    result.getThrowable().getMessage() : "Unknown reason";
                ReportManager.logSkip("Test skipped: " + skipReason);
                break;
                
            default:
                logger.warn("Test completed with unknown status: {}", testName);
        }
    }
    
    /**
     * Handles test failure with screenshot and logging
     */
    private void handleTestFailure(ITestResult result, String testName) {
        String errorMessage = result.getThrowable() != null ? 
            result.getThrowable().getMessage() : "Unknown error";
        
        ReportManager.logFail("Test failed: " + errorMessage);
        
        // Capture screenshot if enabled and driver available
        if (config.isScreenshotOnFailureEnabled() && DriverManager.isDriverInitialized()) {
            try {
                String screenshotPath = ScreenshotUtils.captureScreenshot(testName + "_failure");
                if (screenshotPath != null) {
                    ReportManager.addScreenshot(screenshotPath);
                    logger.info("Screenshot captured: {}", screenshotPath);
                }
            } catch (Exception e) {
                logger.warn("Failed to capture screenshot: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Logs suite execution summary
     */
    private void logSuiteSummary() {
        logger.info("Suite execution completed");
        logger.info("Reports location: {}", config.getReportsDirectory());
    }
    
    /**
     * Captures screenshot for test documentation
     * 
     * @param screenshotName Name for the screenshot
     */
    protected void captureScreenshot(String screenshotName) {
        try {
            if (DriverManager.isDriverInitialized()) {
                String screenshotPath = ScreenshotUtils.captureScreenshot(screenshotName);
                if (screenshotPath != null) {
                    ReportManager.addScreenshot(screenshotPath);
                    logger.info("Screenshot captured: {}", screenshotPath);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Gets current driver instance
     * 
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }
}
