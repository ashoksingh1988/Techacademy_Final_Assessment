package com.appium.utils;

import com.appium.core.ConfigurationManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced ReportManager - Comprehensive ExtentReports with Performance Metrics
 * Features: Success/Failure Screenshots, Performance Tracking, Test History, Step-by-step Logging
 *
 * @author Asim Kumar Singh
 * @version 2.0.0 - Enhanced for Lead Review Requirements
 */
public class EnhancedReportManager {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    
    // Performance tracking
    private static final Map<String, Long> testStartTimes = new ConcurrentHashMap<>();
    private static final Map<String, Long> stepStartTimes = new ConcurrentHashMap<>();
    private static final Map<String, Integer> testExecutionCounts = new ConcurrentHashMap<>();
    
    private static final String REPORT_NAME = "Appium_Android_Automation_Report";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static final String PERFORMANCE_THRESHOLD_MS = "5000"; // 5 seconds
    
    // Test statistics
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static long totalExecutionTime = 0;

    private EnhancedReportManager() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initializes Enhanced ExtentReports with comprehensive configuration
     */
    public static synchronized void initializeReports() {
        if (extentReports == null) {
            logger.info("Initializing Enhanced ExtentReports with Performance Tracking");

            ConfigurationManager config = ConfigurationManager.getInstance();
            String reportsDirectory = config.getReportsDirectory();
            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String reportFileName = REPORT_NAME + "_" + timestamp + ".html";
            String reportPath = reportsDirectory + File.separator + reportFileName;

            // Create reports directory if it doesn't exist
            createReportsDirectory(reportsDirectory);

            // Configure Enhanced Spark reporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureEnhancedSparkReporter(sparkReporter);

            // Initialize ExtentReports with enhanced features
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            setEnhancedSystemInformation();

            logger.info("Enhanced ExtentReports initialized successfully: {}", reportPath);
        }
    }

    /**
     * Enhanced Spark reporter configuration with timeline and performance features
     */
    private static void configureEnhancedSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setDocumentTitle("Enhanced Appium Android Automation Report");
        sparkReporter.config().setReportName("Android Mobile Application Test Results - Lead Review");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        sparkReporter.config().setEncoding("UTF-8");
        
        // Enable timeline for test history
        sparkReporter.config().setTimelineEnabled(true);
        
        // Enhanced CSS for better lead review experience
        sparkReporter.config().setCss(
            ".test-name { font-weight: bold; color: #2c3e50; }" +
            ".test-status { font-size: 14px; }" +
            ".category-name { background-color: #ecf0f1; padding: 5px; }" +
            ".performance-good { color: #27ae60; font-weight: bold; }" +
            ".performance-warning { color: #f39c12; font-weight: bold; }" +
            ".performance-critical { color: #e74c3c; font-weight: bold; }" +
            ".step-info { background-color: #f8f9fa; padding: 3px; margin: 2px 0; }" +
            ".screenshot-success { border: 2px solid #27ae60; }" +
            ".screenshot-failure { border: 2px solid #e74c3c; }"
        );
        
        // Custom JavaScript for enhanced functionality
        sparkReporter.config().setJs(
            "document.addEventListener('DOMContentLoaded', function() {" +
            "  console.log('Enhanced Appium Report Loaded');" +
            "  // Add performance indicators" +
            "  var performanceElements = document.querySelectorAll('.performance-metric');" +
            "  performanceElements.forEach(function(el) {" +
            "    var time = parseInt(el.textContent);" +
            "    if (time > 5000) el.classList.add('performance-critical');" +
            "    else if (time > 3000) el.classList.add('performance-warning');" +
            "    else el.classList.add('performance-good');" +
            "  });" +
            "});"
        );
    }

    /**
     * Sets enhanced system information including performance baselines
     */
    private static void setEnhancedSystemInformation() {
        // Basic system info
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("OS Version", System.getProperty("os.version"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User Name", System.getProperty("user.name"));
        
        // Enhanced info for lead review
        extentReports.setSystemInfo("Test Environment", ConfigurationManager.getInstance().getEnvironment());
        extentReports.setSystemInfo("Device Name", ConfigurationManager.getInstance().getDeviceName());
        extentReports.setSystemInfo("Platform Version", ConfigurationManager.getInstance().getPlatformVersion());
        extentReports.setSystemInfo("Appium Server", ConfigurationManager.getInstance().getAppiumServerUrl());
        
        // Performance baselines
        extentReports.setSystemInfo("Performance Threshold", PERFORMANCE_THRESHOLD_MS + "ms");
        extentReports.setSystemInfo("Screenshot Mode", "Success & Failure Capture Enabled");
        extentReports.setSystemInfo("Report Generation Time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        
        // Test execution metadata
        extentReports.setSystemInfo("Execution Mode", "Enhanced Reporting with Performance Tracking");
        extentReports.setSystemInfo("Report Version", "2.0.0 - Lead Review Edition");
    }

    /**
     * Creates a new test with enhanced tracking
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        extentTest.set(test);
        
        // Initialize performance tracking
        String threadId = Thread.currentThread().getName();
        testStartTimes.put(threadId, System.currentTimeMillis());
        
        // Track test execution count for history
        testExecutionCounts.merge(testName, 1, Integer::sum);
        
        // Log test start with metadata
        test.info(MarkupHelper.createLabel("TEST STARTED: " + testName, ExtentColor.BLUE));
        test.info("Test Description: " + description);
        test.info("Execution Count: " + testExecutionCounts.get(testName));
        test.info("Start Time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        
        totalTests++;
        logger.info("Enhanced test created: {} (Execution #{}) ", testName, testExecutionCounts.get(testName));
        return test;
    }

    /**
     * Logs a test step with performance tracking
     */
    public static void logStep(String stepDescription) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            String threadId = Thread.currentThread().getName();
            stepStartTimes.put(threadId + "_step", System.currentTimeMillis());
            
            test.info("<div class='step-info'><strong>STEP:</strong> " + stepDescription + "</div>");
            logger.info("Test step logged: {}", stepDescription);
        }
    }

    /**
     * Logs step completion with performance metrics
     */
    public static void logStepCompletion(String stepDescription) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            String threadId = Thread.currentThread().getName();
            Long stepStartTime = stepStartTimes.get(threadId + "_step");
            
            if (stepStartTime != null) {
                long stepDuration = System.currentTimeMillis() - stepStartTime;
                String performanceClass = getPerformanceClass(stepDuration);
                
                test.info("<div class='step-info'><strong>STEP COMPLETED:</strong> " + stepDescription + 
                         " <span class='" + performanceClass + "'>[" + stepDuration + "ms]</span></div>");
                
                stepStartTimes.remove(threadId + "_step");
            } else {
                test.info("<div class='step-info'><strong>STEP COMPLETED:</strong> " + stepDescription + "</div>");
            }
        }
    }

    /**
     * Determines performance class based on execution time
     */
    private static String getPerformanceClass(long duration) {
        if (duration > 5000) return "performance-critical";
        if (duration > 3000) return "performance-warning";
        return "performance-good";
    }

    /**
     * Creates reports directory if it doesn't exist
     */
    private static void createReportsDirectory(String reportsDirectory) {
        File directory = new File(reportsDirectory);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Reports directory created: {}", reportsDirectory);
            } else {
                logger.warn("Failed to create reports directory: {}", reportsDirectory);
            }
        }
    }

    /**
     * Gets current test instance
     */
    public static ExtentTest getCurrentTest() {
        return extentTest.get();
    }

    /**
     * Logs pass with optional screenshot and performance metrics
     */
    public static void logPass(String message, String screenshotPath) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            if (screenshotPath != null && !screenshotPath.isEmpty()) {
                test.pass(message, com.aventstack.extentreports.MediaEntityBuilder
                    .createScreenCaptureFromPath(screenshotPath)
                    .build());
            } else {
                test.pass(message);
            }
            
            // Track performance for passed test
            logTestCompletion(true);
            passedTests++;
        }
    }

    /**
     * Logs failure with screenshot and performance metrics
     */
    public static void logFail(String message, String screenshotPath) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            if (screenshotPath != null && !screenshotPath.isEmpty()) {
                test.fail(message, com.aventstack.extentreports.MediaEntityBuilder
                    .createScreenCaptureFromPath(screenshotPath)
                    .build());
            } else {
                test.fail(message);
            }
            
            // Track performance for failed test
            logTestCompletion(false);
            failedTests++;
        }
    }

    /**
     * Logs test completion with comprehensive performance metrics
     */
    private static void logTestCompletion(boolean passed) {
        ExtentTest test = extentTest.get();
        String threadId = Thread.currentThread().getName();
        Long testStartTime = testStartTimes.get(threadId);
        
        if (test != null && testStartTime != null) {
            long testDuration = System.currentTimeMillis() - testStartTime;
            totalExecutionTime += testDuration;
            
            // Log performance metrics
            String performanceClass = getPerformanceClass(testDuration);
            test.info("<div class='performance-summary'>" +
                     "<h4>Performance Metrics:</h4>" +
                     "<p>Total Execution Time: <span class='" + performanceClass + " performance-metric'>" + testDuration + "</span>ms</p>" +
                     "<p>Status: " + (passed ? "✅ PASSED" : "❌ FAILED") + "</p>" +
                     "<p>End Time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "</p>" +
                     "</div>");
            
            // Log test summary for lead review
            test.info(MarkupHelper.createLabel("TEST COMPLETED: " + (passed ? "PASSED" : "FAILED"), 
                     passed ? ExtentColor.GREEN : ExtentColor.RED));
            
            testStartTimes.remove(threadId);
        }
    }

    /**
     * Logs informational message
     */
    public static void logInfo(String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.info(message);
        }
    }

    /**
     * Logs warning message
     */
    public static void logWarning(String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.warning(message);
        }
    }

    /**
     * Finalizes reports with comprehensive summary for lead review
     */
    public static synchronized void flushReports() {
        if (extentReports != null) {
            // Add final summary
            ExtentTest summaryTest = extentReports.createTest("📊 EXECUTION SUMMARY - LEAD REVIEW");
            
            double passPercentage = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
            long avgExecutionTime = totalTests > 0 ? totalExecutionTime / totalTests : 0;
            
            summaryTest.info("<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px;'>" +
                           "<h3>📈 Test Execution Summary</h3>" +
                           "<table style='width: 100%; border-collapse: collapse;'>" +
                           "<tr><td style='padding: 5px; border: 1px solid #ddd;'><strong>Total Tests:</strong></td><td style='padding: 5px; border: 1px solid #ddd;'>" + totalTests + "</td></tr>" +
                           "<tr><td style='padding: 5px; border: 1px solid #ddd;'><strong>Passed:</strong></td><td style='padding: 5px; border: 1px solid #ddd; color: #27ae60;'>" + passedTests + "</td></tr>" +
                           "<tr><td style='padding: 5px; border: 1px solid #ddd;'><strong>Failed:</strong></td><td style='padding: 5px; border: 1px solid #ddd; color: #e74c3c;'>" + failedTests + "</td></tr>" +
                           "<tr><td style='padding: 5px; border: 1px solid #ddd;'><strong>Pass Rate:</strong></td><td style='padding: 5px; border: 1px solid #ddd;'>" + String.format("%.2f", passPercentage) + "%</td></tr>" +
                           "<tr><td style='padding: 5px; border: 1px solid #ddd;'><strong>Total Execution Time:</strong></td><td style='padding: 5px; border: 1px solid #ddd;'>" + totalExecutionTime + "ms</td></tr>" +
                           "<tr><td style='padding: 5px; border: 1px solid #ddd;'><strong>Average Test Time:</strong></td><td style='padding: 5px; border: 1px solid #ddd;'>" + avgExecutionTime + "ms</td></tr>" +
                           "</table>" +
                           "</div>");
            
            extentReports.flush();
            logger.info("Enhanced ExtentReports flushed successfully with {} total tests", totalTests);
            
            // Reset counters for next execution
            resetCounters();
        }
    }

    /**
     * Resets all counters for fresh execution
     */
    private static void resetCounters() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        totalExecutionTime = 0;
        testStartTimes.clear();
        stepStartTimes.clear();
    }

    /**
     * Gets test execution statistics
     */
    public static Map<String, Object> getExecutionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTests", totalTests);
        stats.put("passedTests", passedTests);
        stats.put("failedTests", failedTests);
        stats.put("totalExecutionTime", totalExecutionTime);
        stats.put("averageExecutionTime", totalTests > 0 ? totalExecutionTime / totalTests : 0);
        return stats;
    }
}
