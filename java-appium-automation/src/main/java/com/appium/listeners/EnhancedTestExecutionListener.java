package com.appium.listeners;

import com.appium.utils.EnhancedReportManager;
import com.appium.utils.EnhancedScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;

import java.util.Arrays;

/**
 * Enhanced TestExecutionListener - Comprehensive test lifecycle management
 * Features: Success/Failure screenshots, Performance tracking, Detailed logging
 *
 * @author Asim Kumar Singh
 * @version 2.0.0 - Enhanced for Lead Review Requirements
 */
public class EnhancedTestExecutionListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedTestExecutionListener.class);

    @Override
    public void onStart(ITestContext context) {
        logger.info("=== ENHANCED TEST SUITE STARTED: {} ===", context.getName());
        
        // Initialize enhanced reporting
        EnhancedReportManager.initializeReports();
        
        // Log suite information
        logger.info("Test Suite: {}", context.getName());
        logger.info("Total Tests: {}", context.getAllTestMethods().length);
        logger.info("Parallel Mode: {}", context.getCurrentXmlTest().getParallel());
        logger.info("Thread Count: {}", context.getCurrentXmlTest().getThreadCount());
        
        // Clean up old screenshots (keep last 7 days)
        EnhancedScreenshotUtils.cleanupOldScreenshots(7);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String testDescription = result.getMethod().getDescription();
        String className = result.getTestClass().getName();
        
        logger.info("🚀 STARTING TEST: {} in class {}", testName, className);
        
        // Create enhanced test in report
        EnhancedReportManager.createTest(testName, 
            testDescription != null ? testDescription : "Test: " + testName);
        
        // Log test start details
        EnhancedReportManager.logStep("Test execution started");
        EnhancedReportManager.logInfo("Test Class: " + className);
        EnhancedReportManager.logInfo("Test Method: " + testName);
        
        // Log test parameters if any
        Object[] parameters = result.getParameters();
        if (parameters != null && parameters.length > 0) {
            EnhancedReportManager.logInfo("Test Parameters: " + Arrays.toString(parameters));
        }
        
        // Log test groups if any
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            EnhancedReportManager.logInfo("Test Groups: " + Arrays.toString(groups));
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        long executionTime = result.getEndMillis() - result.getStartMillis();
        
        logger.info("✅ TEST PASSED: {} (Execution time: {}ms)", testName, executionTime);
        
        // Capture success screenshot
        String successScreenshot = EnhancedScreenshotUtils.captureSuccessScreenshot(
            testName, "Test completed successfully");
        
        // Log success with screenshot and performance metrics
        EnhancedReportManager.logStepCompletion("Test execution completed successfully");
        EnhancedReportManager.logPass(
            "✅ TEST PASSED: " + testName + " | Execution Time: " + executionTime + "ms", 
            successScreenshot);
        
        // Log additional success metrics
        EnhancedReportManager.logInfo("📊 Performance Summary:");
        EnhancedReportManager.logInfo("• Execution Time: " + executionTime + "ms");
        EnhancedReportManager.logInfo("• Status: PASSED");
        EnhancedReportManager.logInfo("• Screenshot: " + (successScreenshot != null ? "Captured" : "Not Available"));
        
        // Log success pattern for trend analysis
        if (executionTime < 3000) {
            EnhancedReportManager.logInfo("🚀 Fast Execution: Test completed under 3 seconds");
        } else if (executionTime > 10000) {
            EnhancedReportManager.logWarning("⚠️ Slow Execution: Test took more than 10 seconds");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
        long executionTime = result.getEndMillis() - result.getStartMillis();
        
        logger.error("❌ TEST FAILED: {} - Error: {} (Execution time: {}ms)", 
            testName, errorMessage, executionTime);
        
        // Capture failure screenshot
        String failureScreenshot = EnhancedScreenshotUtils.captureFailureScreenshot(
            testName, "Test failed: " + errorMessage);
        
        // Log failure with detailed information
        EnhancedReportManager.logStepCompletion("Test execution failed");
        
        // Create detailed failure message
        StringBuilder failureDetails = new StringBuilder();
        failureDetails.append("❌ TEST FAILED: ").append(testName).append("\n");
        failureDetails.append("📋 Error Details:\n");
        failureDetails.append("• Message: ").append(errorMessage).append("\n");
        failureDetails.append("• Execution Time: ").append(executionTime).append("ms\n");
        
        // Add stack trace if available
        if (result.getThrowable() != null) {
            failureDetails.append("• Exception Type: ").append(result.getThrowable().getClass().getSimpleName()).append("\n");
            
            // Add relevant stack trace lines (first 5 lines)
            StackTraceElement[] stackTrace = result.getThrowable().getStackTrace();
            if (stackTrace.length > 0) {
                failureDetails.append("• Stack Trace (Top 5 lines):\n");
                for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                    failureDetails.append("  ").append(stackTrace[i].toString()).append("\n");
                }
            }
        }
        
        EnhancedReportManager.logFail(failureDetails.toString(), failureScreenshot);
        
        // Log failure analysis for lead review
        EnhancedReportManager.logInfo("🔍 Failure Analysis:");
        EnhancedReportManager.logInfo("• Test Name: " + testName);
        EnhancedReportManager.logInfo("• Failure Time: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        EnhancedReportManager.logInfo("• Screenshot: " + (failureScreenshot != null ? "Captured" : "Not Available"));
        
        // Categorize failure type for better analysis
        if (errorMessage != null) {
            if (errorMessage.toLowerCase().contains("timeout")) {
                EnhancedReportManager.logWarning("⏱️ Timeout Issue: Consider increasing wait times or checking element locators");
            } else if (errorMessage.toLowerCase().contains("element")) {
                EnhancedReportManager.logWarning("🎯 Element Issue: Check element locators and page synchronization");
            } else if (errorMessage.toLowerCase().contains("assertion")) {
                EnhancedReportManager.logWarning("🔍 Assertion Failure: Verify expected vs actual values");
            } else if (errorMessage.toLowerCase().contains("connection")) {
                EnhancedReportManager.logWarning("🌐 Connection Issue: Check network connectivity and server availability");
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String skipReason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown reason";
        
        logger.warn("⏭️ TEST SKIPPED: {} - Reason: {}", testName, skipReason);
        
        // Create test entry for skipped test
        EnhancedReportManager.createTest(testName, "Skipped Test: " + testName);
        
        // Log skip details
        EnhancedReportManager.logWarning("⏭️ TEST SKIPPED: " + testName);
        EnhancedReportManager.logInfo("Skip Reason: " + skipReason);
        
        // Capture screenshot for context (optional)
        String skipScreenshot = EnhancedScreenshotUtils.captureScreenshotWithCustomName("SKIPPED_" + testName);
        if (skipScreenshot != null) {
            EnhancedReportManager.logInfo("Context screenshot captured for skipped test");
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info("⚠️ TEST FAILED BUT WITHIN SUCCESS PERCENTAGE: {}", testName);
        
        // Treat as partial success
        EnhancedReportManager.logWarning("⚠️ TEST FAILED BUT WITHIN SUCCESS PERCENTAGE: " + testName);
        EnhancedReportManager.logInfo("This test failed but is within the acceptable success percentage threshold");
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("=== ENHANCED TEST SUITE COMPLETED: {} ===", context.getName());
        
        // Log suite completion statistics
        int totalTests = context.getAllTestMethods().length;
        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();
        
        logger.info("📊 Suite Statistics:");
        logger.info("• Total Tests: {}", totalTests);
        logger.info("• Passed: {}", passedTests);
        logger.info("• Failed: {}", failedTests);
        logger.info("• Skipped: {}", skippedTests);
        
        // Get screenshot statistics
        EnhancedScreenshotUtils.ScreenshotStatistics screenshotStats = 
            EnhancedScreenshotUtils.getScreenshotStatistics();
        
        logger.info("📸 Screenshot Statistics:");
        logger.info("• Success Screenshots: {}", screenshotStats.getSuccessCount());
        logger.info("• Failure Screenshots: {}", screenshotStats.getFailureCount());
        logger.info("• Step Screenshots: {}", screenshotStats.getStepCount());
        logger.info("• Total Screenshots: {}", screenshotStats.getTotalCount());
        
        // Calculate suite execution time
        long suiteStartTime = context.getStartDate().getTime();
        long suiteEndTime = context.getEndDate().getTime();
        long totalSuiteTime = suiteEndTime - suiteStartTime;
        
        logger.info("⏱️ Suite Execution Time: {}ms ({}s)", totalSuiteTime, totalSuiteTime / 1000);
        
        // Finalize reports with comprehensive summary
        EnhancedReportManager.flushReports();
        
        logger.info("📋 Enhanced reports generated successfully");
        logger.info("🎯 Reports are ready for lead review with comprehensive metrics and screenshots");
    }

    /**
     * Custom method to log test steps during execution
     * Can be called from test methods for detailed step logging
     */
    public static void logTestStep(String stepDescription) {
        logger.info("📝 Test Step: {}", stepDescription);
        EnhancedReportManager.logStep(stepDescription);
    }

    /**
     * Custom method to log test step completion with screenshot
     */
    public static void logTestStepWithScreenshot(String stepDescription, String testName) {
        logger.info("📝 Test Step Completed: {}", stepDescription);
        
        // Capture step screenshot
        String stepScreenshot = EnhancedScreenshotUtils.captureStepScreenshot(testName, stepDescription);
        
        EnhancedReportManager.logStepCompletion(stepDescription);
        if (stepScreenshot != null) {
            EnhancedReportManager.logInfo("Step screenshot captured: " + stepDescription);
        }
    }

    /**
     * Custom method to log performance metrics during test execution
     */
    public static void logPerformanceMetric(String action, long responseTimeMs) {
        logger.info("📊 Performance: {} completed in {}ms", action, responseTimeMs);
        
        String performanceLevel;
        if (responseTimeMs < 1000) {
            performanceLevel = "🚀 Excellent";
        } else if (responseTimeMs < 3000) {
            performanceLevel = "✅ Good";
        } else if (responseTimeMs < 5000) {
            performanceLevel = "⚠️ Acceptable";
        } else {
            performanceLevel = "❌ Poor";
        }
        
        EnhancedReportManager.logInfo("📊 Performance Metric: " + action + " | " + 
                                    responseTimeMs + "ms | " + performanceLevel);
    }
}
