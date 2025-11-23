package com.appium.core;

import com.appium.utils.ReportManager;
import com.appium.utils.ScreenshotUtils;
import io.appium.java_client.android.AndroidDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * BaseTest - Foundation class for all test classes
 * Provides common setup, teardown, and utility methods
 * Handles driver lifecycle and test reporting
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public abstract class BaseTest {
    
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected AndroidDriver driver;
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
    @Parameters({"deviceName", "platformVersion", "appPackage", "appActivity"})
    public void testSetup(@Optional("") String deviceName,
                         @Optional("") String platformVersion,
                         @Optional("") String appPackage,
                         @Optional("") String appActivity,
                         Method method) {
        
        String testName = method.getName();
        logger.info("=== Test Setup: {} ===", testName);
        
        // Ensure configuration is initialized (safety in case @BeforeSuite didn't run due to suite config)
        if (config == null) {
            config = ConfigurationManager.getInstance();
        }
        
        // Resolve configuration parameters
        deviceName = resolveParameter("device.name", deviceName, "Android_Device");
        platformVersion = resolveParameter("platform.version", platformVersion, "11");
        appPackage = resolveParameter("app.package", appPackage, "");
        appActivity = resolveParameter("app.activity", appActivity, "");
        
        logTestConfiguration(testName, deviceName, platformVersion, appPackage, appActivity);
        
        try {
            // Check Appium server availability first to avoid hard failures on environments without Appium/device
            if (!isAppiumServerAvailable()) {
                String serverUrl = config.getProperty("appium.server.url", "http://127.0.0.1:4723");
                String message = "Appium server is not reachable at " + serverUrl + " — skipping test '" + testName + "'";
                logger.warn(message);
                ReportManager.logSkip(message);
                throw new SkipException(message);
            }

            // Initialize driver
            driver = DriverFactory.createAndroidDriver(deviceName, platformVersion, appPackage, appActivity);
            
            // Setup test reporting
            setupTestReporting(method, deviceName, platformVersion);
            
            logger.info("Test setup completed successfully for: {}", testName);
            
        } catch (SkipException se) {
            throw se; // propagate skip without wrapping
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
            DriverFactory.quitDriver();
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
            DriverFactory.quitAllDrivers();
            
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
    private void logTestConfiguration(String testName, String deviceName, String platformVersion,
                                    String appPackage, String appActivity) {
        logger.info("Test Configuration for: {}", testName);
        logger.info("  Device Name: {}", deviceName);
        logger.info("  Platform Version: {}", platformVersion);
        logger.info("  App Package: {}", appPackage.isEmpty() ? "Not specified" : appPackage);
        logger.info("  App Activity: {}", appActivity.isEmpty() ? "Not specified" : appActivity);
        logger.info("  Thread: {}", Thread.currentThread().getName());
    }
    
    /**
     * Sets up test reporting
     */
    private void setupTestReporting(Method method, String deviceName, String platformVersion) {
        String testName = method.getName();
        String description = getTestDescription(method);
        
        ReportManager.createTest(testName, description);
        ReportManager.logInfo("Test started on device: " + deviceName + " (Android " + platformVersion + ")");
        ReportManager.assignDevice(deviceName);
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
        if (config.isScreenshotOnFailureEnabled() && DriverFactory.isDriverInitialized()) {
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
     * Gets current driver instance
     * 
     * @return AndroidDriver instance
     */
    protected AndroidDriver getDriver() {
        return DriverFactory.getDriver();
    }

    // Checks if the Appium server is reachable by calling the /status endpoint
    private boolean isAppiumServerAvailable() {
        String baseUrl = config != null ? config.getProperty("appium.server.url", "http://127.0.0.1:4723") : "http://127.0.0.1:4723";
        String statusUrl = baseUrl.endsWith("/") ? baseUrl + "status" : baseUrl + "/status";
        try {
            URL url = new URL(statusUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            int code = connection.getResponseCode();
            return code == 200;
        } catch (Exception e) {
            logger.warn("Appium server not reachable at {}: {}", statusUrl, e.getMessage());
            return false;
        }
    }
}
