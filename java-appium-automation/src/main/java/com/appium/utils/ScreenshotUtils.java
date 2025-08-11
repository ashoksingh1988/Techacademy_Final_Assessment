package com.appium.utils;

import com.appium.core.ConfigurationManager;
import com.appium.core.DriverFactory;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils - Handles screenshot capture and management
 * Provides various screenshot formats and automatic cleanup functionality
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class ScreenshotUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_SUBDIRECTORY = "screenshots";
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss-SSS";
    
    private ScreenshotUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Captures screenshot with automatic naming based on test name
     * 
     * @param testName Name of the test for screenshot naming
     * @return Path to the captured screenshot file
     */
    public static String captureScreenshot(String testName) {
        try {
            AndroidDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            
            String screenshotDirectory = createScreenshotDirectory();
            String fileName = generateScreenshotFileName(testName);
            String fullPath = screenshotDirectory + File.separator + fileName;
            
            return captureAndSaveScreenshot(driver, fullPath);
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Captures screenshot with custom filename
     * 
     * @param customFileName Custom filename for the screenshot
     * @return Path to the captured screenshot file
     */
    public static String captureScreenshotWithCustomName(String customFileName) {
        try {
            AndroidDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            
            String screenshotDirectory = createScreenshotDirectory();
            String fileName = ensurePngExtension(customFileName);
            String fullPath = screenshotDirectory + File.separator + fileName;
            
            return captureAndSaveScreenshot(driver, fullPath);
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot with custom name: {}", customFileName, e);
            return null;
        }
    }
    
    /**
     * Captures screenshot and returns as byte array
     * 
     * @return Screenshot as byte array, null if capture fails
     */
    public static byte[] captureScreenshotAsBytes() {
        try {
            AndroidDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            logger.debug("Screenshot captured as byte array");
            return screenshot;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as bytes", e);
            return null;
        }
    }
    
    /**
     * Captures screenshot and returns as base64 string
     * 
     * @return Screenshot as base64 string, null if capture fails
     */
    public static String captureScreenshotAsBase64() {
        try {
            AndroidDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Driver is null, cannot capture screenshot");
                return null;
            }
            
            String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
            logger.debug("Screenshot captured as base64 string");
            return base64Screenshot;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as base64", e);
            return null;
        }
    }
    
    /**
     * Creates screenshot directory if it doesn't exist
     * 
     * @return Path to screenshot directory
     */
    private static String createScreenshotDirectory() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        String reportsDirectory = config.getReportsDirectory();
        String screenshotDirectory = reportsDirectory + File.separator + SCREENSHOT_SUBDIRECTORY;
        
        File directory = new File(screenshotDirectory);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.debug("Screenshot directory created: {}", screenshotDirectory);
            } else {
                logger.warn("Failed to create screenshot directory: {}", screenshotDirectory);
                // Fallback to reports directory
                screenshotDirectory = reportsDirectory;
            }
        }
        
        return screenshotDirectory;
    }
    
    /**
     * Generates screenshot filename with timestamp
     * 
     * @param testName Base name for the screenshot
     * @return Generated filename with timestamp and extension
     */
    private static String generateScreenshotFileName(String testName) {
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String sanitizedTestName = sanitizeFileName(testName);
        return sanitizedTestName + "_" + timestamp + ".png";
    }
    
    /**
     * Sanitizes filename by removing invalid characters
     * 
     * @param fileName Original filename
     * @return Sanitized filename
     */
    private static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "screenshot";
        }
        
        // Replace invalid characters with underscores
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Ensures filename has .png extension
     * 
     * @param fileName Original filename
     * @return Filename with .png extension
     */
    private static String ensurePngExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "screenshot.png";
        }
        
        String sanitized = sanitizeFileName(fileName);
        if (!sanitized.toLowerCase().endsWith(".png")) {
            sanitized += ".png";
        }
        
        return sanitized;
    }
    
    /**
     * Captures screenshot from driver and saves to specified path
     * 
     * @param driver AndroidDriver instance
     * @param fullPath Full path where screenshot should be saved
     * @return Path to saved screenshot, null if save fails
     */
    private static String captureAndSaveScreenshot(AndroidDriver driver, String fullPath) {
        try {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(fullPath);
            
            FileUtils.copyFile(screenshotFile, destinationFile);
            
            logger.info("Screenshot captured successfully: {}", fullPath);
            return fullPath;
            
        } catch (IOException e) {
            logger.error("Failed to save screenshot to: {}", fullPath, e);
            return null;
        }
    }
    
    /**
     * Cleans up old screenshots older than specified days
     * 
     * @param daysToKeep Number of days to keep screenshots
     * @return Number of files deleted
     */
    public static int cleanupOldScreenshots(int daysToKeep) {
        try {
            String screenshotDirectory = createScreenshotDirectory();
            File directory = new File(screenshotDirectory);
            
            if (!directory.exists() || !directory.isDirectory()) {
                logger.warn("Screenshot directory does not exist: {}", screenshotDirectory);
                return 0;
            }
            
            File[] files = directory.listFiles();
            if (files == null) {
                logger.warn("No files found in screenshot directory");
                return 0;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);
            int deletedCount = 0;
            
            for (File file : files) {
                if (file.isFile() && isScreenshotFile(file) && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                        logger.debug("Deleted old screenshot: {}", file.getName());
                    } else {
                        logger.warn("Failed to delete old screenshot: {}", file.getName());
                    }
                }
            }
            
            logger.info("Cleaned up {} old screenshots (older than {} days)", deletedCount, daysToKeep);
            return deletedCount;
            
        } catch (Exception e) {
            logger.error("Error during screenshot cleanup", e);
            return 0;
        }
    }
    
    /**
     * Checks if file is a screenshot file based on extension
     * 
     * @param file File to check
     * @return true if file is a screenshot file
     */
    private static boolean isScreenshotFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
    }
    
    /**
     * Gets the screenshot directory path
     * 
     * @return Screenshot directory path
     */
    public static String getScreenshotDirectory() {
        return createScreenshotDirectory();
    }
    
    /**
     * Gets total number of screenshot files in directory
     * 
     * @return Number of screenshot files
     */
    public static int getScreenshotCount() {
        try {
            String screenshotDirectory = createScreenshotDirectory();
            File directory = new File(screenshotDirectory);
            
            if (!directory.exists() || !directory.isDirectory()) {
                return 0;
            }
            
            File[] files = directory.listFiles();
            if (files == null) {
                return 0;
            }
            
            int count = 0;
            for (File file : files) {
                if (file.isFile() && isScreenshotFile(file)) {
                    count++;
                }
            }
            
            return count;
            
        } catch (Exception e) {
            logger.error("Error counting screenshots", e);
            return 0;
        }
    }
}
