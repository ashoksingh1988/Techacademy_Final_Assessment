package com.appium.utils;

import com.appium.core.ConfigurationManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ReportManager - Manages ExtentReports for comprehensive test reporting
 * Provides thread-safe reporting capabilities for parallel execution
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class ReportManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    
    private static final String REPORT_NAME = "Appium_Android_Automation_Report";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    
    private ReportManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initializes ExtentReports with configuration
     */
    public static synchronized void initializeReports() {
        if (extentReports == null) {
            logger.info("Initializing ExtentReports");
            
            ConfigurationManager config = ConfigurationManager.getInstance();
            String reportsDirectory = config.getReportsDirectory();
            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            String reportFileName = REPORT_NAME + "_" + timestamp + ".html";
            String reportPath = reportsDirectory + File.separator + reportFileName;
            
            // Create reports directory if it doesn't exist
            createReportsDirectory(reportsDirectory);
            
            // Configure Spark reporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            // Initialize ExtentReports
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            setSystemInformation();
            
            logger.info("ExtentReports initialized successfully: {}", reportPath);
        }
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
     * Configures the Spark reporter with custom settings
     */
    private static void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setDocumentTitle("Appium Android Automation Report");
        sparkReporter.config().setReportName("Android Mobile Application Test Results");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        sparkReporter.config().setEncoding("UTF-8");
        
        // Custom CSS for better appearance
        sparkReporter.config().setCss(
            ".test-name { font-weight: bold; }" +
            ".test-status { font-size: 14px; }" +
            ".category-name { background-color: #f8f9fa; }"
        );
    }
    
    /**
     * Sets system information in the report
     */
    private static void setSystemInformation() {
        extentReports.setSystemInfo("Operating System", System.getProperty("os.name"));
        extentReports.setSystemInfo("OS Version", System.getProperty("os.version"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("User Name", System.getProperty("user.name"));
        extentReports.setSystemInfo("Framework", "Appium + TestNG + Maven");
        extentReports.setSystemInfo("Automation Tool", "Appium Java Client");
        extentReports.setSystemInfo("Report Generated", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    
    /**
     * Creates a new test in the report
     * 
     * @param testName Name of the test
     * @param description Description of the test
     */
    public static void createTest(String testName, String description) {
        if (extentReports == null) {
            initializeReports();
        }
        
        ExtentTest test = extentReports.createTest(testName, description);
        extentTest.set(test);
        logger.debug("Created test in report: {}", testName);
    }
    
    /**
     * Gets current test instance for the thread
     * 
     * @return Current ExtentTest instance
     */
    public static ExtentTest getCurrentTest() {
        return extentTest.get();
    }
    
    // ==================== LOGGING METHODS ====================
    
    /**
     * Logs an info message
     * 
     * @param message Info message
     */
    public static void logInfo(String message) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            test.log(Status.INFO, message);
            logger.debug("Logged info: {}", message);
        }
    }
    
    /**
     * Logs a pass message
     * 
     * @param message Pass message
     */
    public static void logPass(String message) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            test.log(Status.PASS, message);
            logger.debug("Logged pass: {}", message);
        }
    }
    
    /**
     * Logs a fail message
     * 
     * @param message Fail message
     */
    public static void logFail(String message) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            test.log(Status.FAIL, message);
            logger.debug("Logged fail: {}", message);
        }
    }
    
    /**
     * Logs a skip message
     * 
     * @param message Skip message
     */
    public static void logSkip(String message) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            test.log(Status.SKIP, message);
            logger.debug("Logged skip: {}", message);
        }
    }
    
    /**
     * Logs a warning message
     * 
     * @param message Warning message
     */
    public static void logWarning(String message) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            test.log(Status.WARNING, message);
            logger.debug("Logged warning: {}", message);
        }
    }
    
    // ==================== SCREENSHOT METHODS ====================
    
    /**
     * Adds screenshot to the current test
     * 
     * @param screenshotPath Path to the screenshot file
     */
    public static void addScreenshot(String screenshotPath) {
        ExtentTest test = getCurrentTest();
        if (test != null && screenshotPath != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath);
                logger.debug("Added screenshot to report: {}", screenshotPath);
            } catch (Exception e) {
                logger.warn("Failed to add screenshot to report: {}", e.getMessage());
                test.log(Status.WARNING, "Failed to attach screenshot: " + e.getMessage());
            }
        }
    }
    
    /**
     * Adds screenshot with message to the current test
     * 
     * @param message Message to log with screenshot
     * @param screenshotPath Path to the screenshot file
     */
    public static void addScreenshotWithMessage(String message, String screenshotPath) {
        ExtentTest test = getCurrentTest();
        if (test != null && screenshotPath != null) {
            try {
                test.info(message).addScreenCaptureFromPath(screenshotPath);
                logger.debug("Added screenshot with message to report: {}", message);
            } catch (Exception e) {
                logger.warn("Failed to add screenshot with message: {}", e.getMessage());
                test.log(Status.WARNING, message + " (Screenshot attachment failed: " + e.getMessage() + ")");
            }
        }
    }
    
    /**
     * Adds base64 screenshot to the current test
     * 
     * @param base64Screenshot Base64 encoded screenshot
     */
    public static void addBase64Screenshot(String base64Screenshot) {
        ExtentTest test = getCurrentTest();
        if (test != null && base64Screenshot != null) {
            try {
                test.addScreenCaptureFromBase64String(base64Screenshot);
                logger.debug("Added base64 screenshot to report");
            } catch (Exception e) {
                logger.warn("Failed to add base64 screenshot: {}", e.getMessage());
                test.log(Status.WARNING, "Failed to attach base64 screenshot: " + e.getMessage());
            }
        }
    }
    
    // ==================== TEST METADATA METHODS ====================
    
    /**
     * Assigns category to the current test
     * 
     * @param category Category name
     */
    public static void assignCategory(String category) {
        ExtentTest test = getCurrentTest();
        if (test != null && category != null) {
            test.assignCategory(category);
            logger.debug("Assigned category to test: {}", category);
        }
    }
    
    /**
     * Assigns author to the current test
     * 
     * @param author Author name
     */
    public static void assignAuthor(String author) {
        ExtentTest test = getCurrentTest();
        if (test != null && author != null) {
            test.assignAuthor(author);
            logger.debug("Assigned author to test: {}", author);
        }
    }
    
    /**
     * Assigns device to the current test
     * 
     * @param device Device name
     */
    public static void assignDevice(String device) {
        ExtentTest test = getCurrentTest();
        if (test != null && device != null) {
            test.assignDevice(device);
            logger.debug("Assigned device to test: {}", device);
        }
    }
    
    // ==================== REPORT FINALIZATION ====================
    
    /**
     * Flushes the reports (writes to file)
     */
    public static synchronized void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed successfully");
        }
    }
    
    /**
     * Cleans up thread local test reference
     */
    public static void removeTest() {
        extentTest.remove();
    }
    
    /**
     * Gets the ExtentReports instance
     * 
     * @return ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        return extentReports;
    }
}
