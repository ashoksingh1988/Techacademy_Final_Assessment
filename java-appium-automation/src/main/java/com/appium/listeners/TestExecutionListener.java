package com.appium.listeners;

import com.appium.core.ConfigurationManager;
import com.appium.core.DriverFactory;
import com.appium.utils.ReportManager;
import com.appium.utils.ScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

/**
 * TestExecutionListener - Comprehensive TestNG listener for test lifecycle management
 * Handles suite, test, and method level events with detailed logging and reporting
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class TestExecutionListener implements ISuiteListener, ITestListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestExecutionListener.class);
    private ConfigurationManager config;
    
    // ==================== SUITE LEVEL EVENTS ====================
    
    @Override
    public void onStart(ISuite suite) {
        logger.info("========================================");
        logger.info("TEST SUITE STARTED: {}", suite.getName());
        logger.info("========================================");
        
        config = ConfigurationManager.getInstance();
        
        // Initialize reporting
        ReportManager.initializeReports();
        
        // Log suite configuration
        logSuiteConfiguration(suite);
        
        logger.info("Suite initialization completed successfully");
    }
    
    @Override
    public void onFinish(ISuite suite) {
        logger.info("========================================");
        logger.info("TEST SUITE FINISHED: {}", suite.getName());
        logger.info("========================================");
        
        try {
            // Ensure all drivers are properly closed
            DriverFactory.quitAllDrivers();
            
            // Finalize reports
            ReportManager.flushReports();
            
            // Cleanup old screenshots (keep last 7 days)
            int cleanedFiles = ScreenshotUtils.cleanupOldScreenshots(7);
            logger.info("Cleaned up {} old screenshot files", cleanedFiles);
            
            // Log suite summary
            logSuiteSummary(suite);
            
        } catch (Exception e) {
            logger.error("Error during suite cleanup", e);
        }
        
        logger.info("Suite execution completed successfully");
    }
    
    // ==================== TEST LEVEL EVENTS ====================
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);
        String className = result.getTestClass().getName();
        
        logger.info("----------------------------------------");
        logger.info("TEST STARTED: {}.{}", className, testName);
        logger.info("----------------------------------------");
        
        // Create test in report
        String description = getTestDescription(result);
        ReportManager.createTest(testName, description);
        
        // Log test information
        logTestStartInformation(result);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("✅ TEST PASSED: {} (Duration: {}ms)", testName, duration);
        
        ReportManager.logPass("Test completed successfully in " + duration + "ms");
        
        // Capture success screenshot if enabled
        captureSuccessScreenshotIfEnabled(result);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        Throwable throwable = result.getThrowable();
        
        logger.error("❌ TEST FAILED: {} (Duration: {}ms)", testName, duration);
        if (throwable != null) {
            logger.error("Failure reason: {}", throwable.getMessage());
        }
        
        // Log failure in report
        String errorMessage = throwable != null ? throwable.getMessage() : "Unknown error";
        ReportManager.logFail("Test failed after " + duration + "ms: " + errorMessage);
        
        // Capture failure screenshot
        captureFailureScreenshot(result);
        
        // Log stack trace if available
        logStackTrace(throwable);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);
        Throwable throwable = result.getThrowable();
        
        logger.warn("⏭️ TEST SKIPPED: {}", testName);
        
        String skipReason = throwable != null ? throwable.getMessage() : "Unknown reason";
        logger.warn("Skip reason: {}", skipReason);
        
        ReportManager.logSkip("Test skipped: " + skipReason);
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = getTestName(result);
        logger.warn("⚠️ TEST FAILED BUT WITHIN SUCCESS PERCENTAGE: {}", testName);
        
        ReportManager.logWarning("Test failed but within success percentage threshold");
    }
    
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        String testName = getTestName(result);
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.error("⏰ TEST FAILED WITH TIMEOUT: {} (Duration: {}ms)", testName, duration);
        
        ReportManager.logFail("Test failed due to timeout after " + duration + "ms");
        
        // Capture timeout screenshot
        captureFailureScreenshot(result);
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Logs suite configuration details
     */
    private void logSuiteConfiguration(ISuite suite) {
        logger.info("Suite Configuration:");
        logger.info("  Suite Name: {}", suite.getName());
        logger.info("  XML File: {}", suite.getXmlSuite().getFileName());
        logger.info("  Parallel Mode: {}", suite.getXmlSuite().getParallel());
        logger.info("  Thread Count: {}", suite.getXmlSuite().getThreadCount());
        logger.info("  Test Count: {}", suite.getXmlSuite().getTests().size());
        
        // Log framework configuration
        logger.info("Framework Configuration:");
        logger.info("  Appium Server: {}", config.getAppiumServerUrl());
        logger.info("  Reports Directory: {}", config.getReportsDirectory());
        logger.info("  Screenshot on Failure: {}", config.isScreenshotOnFailureEnabled());
        logger.info("  Parallel Execution: {}", config.isParallelExecutionEnabled());
    }
    
    /**
     * Logs test start information
     */
    private void logTestStartInformation(ITestResult result) {
        String testName = getTestName(result);
        String className = result.getTestClass().getName();
        String threadName = Thread.currentThread().getName();
        
        ReportManager.logInfo("Test Class: " + className);
        ReportManager.logInfo("Test Method: " + testName);
        ReportManager.logInfo("Thread: " + threadName);
        ReportManager.logInfo("Start Time: " + new java.util.Date(result.getStartMillis()));
        
        // Assign metadata
        ReportManager.assignCategory(getSimpleClassName(className));
        ReportManager.assignAuthor("Asim Kumar Singh");
        
        // Log device information if driver is available
        logDeviceInformation();
    }
    
    /**
     * Logs device information if driver is available
     */
    private void logDeviceInformation() {
        try {
            if (DriverFactory.isDriverInitialized()) {
                var driver = DriverFactory.getDriver();
                String deviceName = (String) driver.getCapabilities().getCapability("deviceName");
                String platformVersion = (String) driver.getCapabilities().getCapability("platformVersion");
                
                if (deviceName != null) {
                    ReportManager.logInfo("Device: " + deviceName);
                    ReportManager.assignDevice(deviceName);
                }
                if (platformVersion != null) {
                    ReportManager.logInfo("Platform Version: " + platformVersion);
                }
            }
        } catch (Exception e) {
            logger.debug("Could not retrieve device information: {}", e.getMessage());
        }
    }
    
    /**
     * Captures screenshot on test failure
     */
    private void captureFailureScreenshot(ITestResult result) {
        if (config.isScreenshotOnFailureEnabled() && DriverFactory.isDriverInitialized()) {
            try {
                String testName = getTestName(result);
                String screenshotPath = ScreenshotUtils.captureScreenshot(testName + "_failure");
                
                if (screenshotPath != null) {
                    ReportManager.addScreenshotWithMessage("Failure Screenshot", screenshotPath);
                    logger.info("Failure screenshot captured: {}", screenshotPath);
                } else {
                    logger.warn("Failed to capture failure screenshot for test: {}", testName);
                }
            } catch (Exception e) {
                logger.error("Error capturing failure screenshot", e);
            }
        }
    }
    
    /**
     * Captures screenshot on test success if enabled
     */
    private void captureSuccessScreenshotIfEnabled(ITestResult result) {
        boolean captureOnSuccess = config.getBooleanProperty("reporting.screenshot.on.success", false);
        
        if (captureOnSuccess && DriverFactory.isDriverInitialized()) {
            try {
                String testName = getTestName(result);
                String screenshotPath = ScreenshotUtils.captureScreenshot(testName + "_success");
                
                if (screenshotPath != null) {
                    ReportManager.addScreenshotWithMessage("Success Screenshot", screenshotPath);
                    logger.debug("Success screenshot captured: {}", screenshotPath);
                }
            } catch (Exception e) {
                logger.debug("Error capturing success screenshot", e);
            }
        }
    }
    
    /**
     * Logs stack trace for failures
     */
    private void logStackTrace(Throwable throwable) {
        if (throwable != null) {
            StringBuilder stackTrace = new StringBuilder();
            stackTrace.append("Stack Trace:\n");
            
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append("  at ").append(element.toString()).append("\n");
            }
            
            ReportManager.logFail(stackTrace.toString());
        }
    }
    
    /**
     * Logs suite execution summary
     */
    private void logSuiteSummary(ISuite suite) {
        logger.info("Suite Execution Summary:");
        logger.info("  Total Tests: {}", suite.getAllMethods().size());
        logger.info("  Reports Location: {}", config.getReportsDirectory());
        logger.info("  Screenshots Count: {}", ScreenshotUtils.getScreenshotCount());
        logger.info("  Active Drivers: {}", DriverFactory.getActiveDriverCount());
    }
    
    /**
     * Gets test name from ITestResult
     */
    private String getTestName(ITestResult result) {
        return result.getMethod().getMethodName();
    }
    
    /**
     * Gets test description from ITestResult
     */
    private String getTestDescription(ITestResult result) {
        String description = result.getMethod().getDescription();
        return description != null && !description.isEmpty() ? description : "Test: " + getTestName(result);
    }
    
    /**
     * Gets simple class name from full class name
     */
    private String getSimpleClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }
}
