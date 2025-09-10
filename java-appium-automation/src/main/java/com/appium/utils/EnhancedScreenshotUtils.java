package com.appium.utils;

import com.appium.core.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Enhanced ScreenshotUtils - Captures screenshots for both success and failure scenarios
 * Features: Success screenshots, failure screenshots, performance tracking, organized storage
 *
 * @author Asim Kumar Singh
 * @version 2.0.0 - Enhanced for Lead Review Requirements
 */
public class EnhancedScreenshotUtils {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedScreenshotUtils.class);
    private static final String SCREENSHOT_FORMAT = "yyyy-MM-dd_HH-mm-ss-SSS";
    private static final String SUCCESS_FOLDER = "screenshots/success";
    private static final String FAILURE_FOLDER = "screenshots/failure";
    private static final String STEP_FOLDER = "screenshots/steps";

    private EnhancedScreenshotUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Captures screenshot for successful test scenarios
     * @param testName Name of the test
     * @param stepDescription Description of the successful step
     * @return Path to the captured screenshot
     */
    public static String captureSuccessScreenshot(String testName, String stepDescription) {
        return captureScreenshot(testName, stepDescription, "SUCCESS", SUCCESS_FOLDER);
    }

    /**
     * Captures screenshot for failed test scenarios
     * @param testName Name of the test
     * @param errorDescription Description of the failure
     * @return Path to the captured screenshot
     */
    public static String captureFailureScreenshot(String testName, String errorDescription) {
        return captureScreenshot(testName, errorDescription, "FAILURE", FAILURE_FOLDER);
    }

    /**
     * Captures screenshot for test steps (for detailed step-by-step documentation)
     * @param testName Name of the test
     * @param stepDescription Description of the step
     * @return Path to the captured screenshot
     */
    public static String captureStepScreenshot(String testName, String stepDescription) {
        return captureScreenshot(testName, stepDescription, "STEP", STEP_FOLDER);
    }

    /**
     * Generic screenshot capture method with enhanced organization
     * @param testName Name of the test
     * @param description Description of the scenario
     * @param type Type of screenshot (SUCCESS/FAILURE/STEP)
     * @param folder Target folder for organization
     * @return Path to the captured screenshot
     */
    private static String captureScreenshot(String testName, String description, String type, String folder) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot for: {}", testName);
                return null;
            }

            // Create timestamp for unique filename
            String timestamp = new SimpleDateFormat(SCREENSHOT_FORMAT).format(new Date());
            
            // Clean test name and description for filename
            String cleanTestName = cleanFileName(testName);
            String cleanDescription = cleanFileName(description);
            
            // Create filename with enhanced naming convention
            String fileName = String.format("%s_%s_%s_%s.png", 
                type, cleanTestName, cleanDescription, timestamp);

            // Create full directory path
            String reportsDirectory = "reports"; // Can be made configurable
            String fullFolderPath = reportsDirectory + File.separator + folder;
            
            // Ensure directory exists
            createDirectoryIfNotExists(fullFolderPath);

            // Full file path
            String filePath = fullFolderPath + File.separator + fileName;

            // Capture screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            // Save screenshot
            File screenshotFile = new File(filePath);
            FileUtils.writeByteArrayToFile(screenshotFile, screenshotBytes);

            // Log success
            logger.info("Screenshot captured successfully: {} - {}", type, filePath);
            
            // Return relative path for HTML report
            return getRelativePathForReport(filePath);

        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test: {} - Error: {}", testName, e.getMessage(), e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error while capturing screenshot for test: {} - Error: {}", testName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Captures screenshot with custom filename
     * @param customFileName Custom filename for the screenshot
     * @return Path to the captured screenshot
     */
    public static String captureScreenshotWithCustomName(String customFileName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot with custom name: {}", customFileName);
                return null;
            }

            String timestamp = new SimpleDateFormat(SCREENSHOT_FORMAT).format(new Date());
            String fileName = cleanFileName(customFileName) + "_" + timestamp + ".png";
            
            String reportsDirectory = "reports";
            String fullFolderPath = reportsDirectory + File.separator + "screenshots";
            createDirectoryIfNotExists(fullFolderPath);

            String filePath = fullFolderPath + File.separator + fileName;

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            File screenshotFile = new File(filePath);
            FileUtils.writeByteArrayToFile(screenshotFile, screenshotBytes);

            logger.info("Custom screenshot captured: {}", filePath);
            return getRelativePathForReport(filePath);

        } catch (Exception e) {
            logger.error("Failed to capture custom screenshot: {} - Error: {}", customFileName, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Captures full page screenshot (if supported by driver)
     * @param testName Name of the test
     * @param description Description of the scenario
     * @return Path to the captured screenshot
     */
    public static String captureFullPageScreenshot(String testName, String description) {
        // For mobile apps, this is the same as regular screenshot
        // But can be enhanced for web views or specific scenarios
        return captureScreenshot(testName, description + "_FULLPAGE", "FULLPAGE", "screenshots/fullpage");
    }

    /**
     * Captures screenshot with performance timing
     * @param testName Name of the test
     * @param description Description of the scenario
     * @return Screenshot capture result with timing information
     */
    public static ScreenshotResult captureScreenshotWithTiming(String testName, String description) {
        long startTime = System.currentTimeMillis();
        String screenshotPath = captureSuccessScreenshot(testName, description);
        long endTime = System.currentTimeMillis();
        
        long captureTime = endTime - startTime;
        logger.info("Screenshot capture time: {}ms for test: {}", captureTime, testName);
        
        return new ScreenshotResult(screenshotPath, captureTime, screenshotPath != null);
    }

    /**
     * Cleans filename to remove invalid characters
     * @param fileName Original filename
     * @return Cleaned filename
     */
    private static String cleanFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "unnamed";
        }
        
        // Remove or replace invalid characters
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_")
                      .replaceAll("_{2,}", "_")  // Replace multiple underscores with single
                      .substring(0, Math.min(fileName.length(), 50)); // Limit length
    }

    /**
     * Creates directory if it doesn't exist
     * @param directoryPath Path to the directory
     */
    private static void createDirectoryIfNotExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Screenshot directory created: {}", directoryPath);
            } else {
                logger.warn("Failed to create screenshot directory: {}", directoryPath);
            }
        }
    }

    /**
     * Converts absolute path to relative path for HTML report
     * @param absolutePath Absolute file path
     * @return Relative path for HTML report
     */
    private static String getRelativePathForReport(String absolutePath) {
        // Convert to relative path for HTML report
        if (absolutePath != null && absolutePath.contains("reports")) {
            int reportsIndex = absolutePath.indexOf("reports");
            return absolutePath.substring(reportsIndex);
        }
        return absolutePath;
    }

    /**
     * Gets screenshot statistics for reporting
     * @return Screenshot statistics
     */
    public static ScreenshotStatistics getScreenshotStatistics() {
        String reportsDir = "reports";
        
        int successCount = countScreenshotsInFolder(reportsDir + File.separator + SUCCESS_FOLDER);
        int failureCount = countScreenshotsInFolder(reportsDir + File.separator + FAILURE_FOLDER);
        int stepCount = countScreenshotsInFolder(reportsDir + File.separator + STEP_FOLDER);
        
        return new ScreenshotStatistics(successCount, failureCount, stepCount);
    }

    /**
     * Counts screenshots in a specific folder
     * @param folderPath Path to the folder
     * @return Number of screenshots
     */
    private static int countScreenshotsInFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            return files != null ? files.length : 0;
        }
        return 0;
    }

    /**
     * Cleans up old screenshots (keeps only recent ones)
     * @param daysToKeep Number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        String reportsDir = "reports" + File.separator + "screenshots";
        File screenshotsDir = new File(reportsDir);
        
        if (screenshotsDir.exists()) {
            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);
            cleanupDirectory(screenshotsDir, cutoffTime);
        }
    }

    /**
     * Recursively cleans up directory
     * @param directory Directory to clean
     * @param cutoffTime Cutoff time for deletion
     */
    private static void cleanupDirectory(File directory, long cutoffTime) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    cleanupDirectory(file, cutoffTime);
                } else if (file.lastModified() < cutoffTime && file.getName().endsWith(".png")) {
                    if (file.delete()) {
                        logger.info("Deleted old screenshot: {}", file.getName());
                    }
                }
            }
        }
    }

    /**
     * Screenshot capture result with timing information
     */
    public static class ScreenshotResult {
        private final String path;
        private final long captureTimeMs;
        private final boolean success;

        public ScreenshotResult(String path, long captureTimeMs, boolean success) {
            this.path = path;
            this.captureTimeMs = captureTimeMs;
            this.success = success;
        }

        public String getPath() { return path; }
        public long getCaptureTimeMs() { return captureTimeMs; }
        public boolean isSuccess() { return success; }
    }

    /**
     * Screenshot statistics for reporting
     */
    public static class ScreenshotStatistics {
        private final int successCount;
        private final int failureCount;
        private final int stepCount;

        public ScreenshotStatistics(int successCount, int failureCount, int stepCount) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.stepCount = stepCount;
        }

        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
        public int getStepCount() { return stepCount; }
        public int getTotalCount() { return successCount + failureCount + stepCount; }
    }
}
