package com.selenium.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DriverManager - Manages WebDriver instances using Factory pattern
 * Provides thread-safe driver creation and management for parallel execution
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class DriverManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConcurrentHashMap<String, WebDriver> activeDrivers = new ConcurrentHashMap<>();
    
    private DriverManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Creates and initializes WebDriver with specified browser
     * 
     * @param browserName Target browser (chrome, firefox, edge, safari)
     * @param headless Whether to run in headless mode
     * @return Configured WebDriver instance
     */
    public static WebDriver createDriver(String browserName, boolean headless) {
        logger.info("Initializing WebDriver for browser: {}, Headless: {}", browserName, headless);
        
        WebDriver driver;
        
        try {
            switch (browserName.toLowerCase()) {
                case "chrome":
                    driver = createChromeDriver(headless);
                    break;
                case "firefox":
                    driver = createFirefoxDriver(headless);
                    break;
                case "edge":
                    driver = createEdgeDriver(headless);
                    break;
                case "safari":
                    driver = createSafariDriver();
                    break;
                default:
                    logger.warn("Unknown browser: {}. Defaulting to Chrome", browserName);
                    driver = createChromeDriver(headless);
            }
            
            configureDriver(driver);
            
            // Store driver instances
            String threadName = Thread.currentThread().getName();
            driverThreadLocal.set(driver);
            activeDrivers.put(threadName, driver);
            
            logger.info("WebDriver initialized successfully for thread: {}", threadName);
            return driver;
            
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver for browser: {}", browserName, e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }
    
    /**
     * Creates Chrome WebDriver with options
     */
    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().clearDriverCache().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        // Standard Chrome options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        
        // OPTIMIZED OPTIONS FOR FASTER EXECUTION
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments("--disable-ipc-flooding-protection");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-sync");
        options.addArguments("--disable-plugins");
        
        return new ChromeDriver(options);
    }
    
    /**
     * Creates Firefox WebDriver with options
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().clearDriverCache().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        
        // OPTIMIZED OPTIONS FOR FASTER EXECUTION
        options.addPreference("dom.max_script_run_time", 30);
        options.addPreference("dom.max_chrome_script_run_time", 30);
        
        return new FirefoxDriver(options);
    }
    
    /**
     * Creates Edge WebDriver with options
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().clearDriverCache().setup();
        
        EdgeOptions options = new EdgeOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        // OPTIMIZED OPTIONS FOR FASTER EXECUTION
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        
        return new EdgeDriver(options);
    }
    
    /**
     * Creates Safari WebDriver (macOS only)
     */
    private static WebDriver createSafariDriver() {
        // Safari doesn't support headless mode
        return new SafariDriver();
    }
    
    /**
     * Configures driver with optimized timeouts and settings
     */
    private static void configureDriver(WebDriver driver) {
        // REDUCED TIMEOUTS FOR FASTER EXECUTION
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Reduced from 10
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15)); // Reduced from 30
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(15)); // Reduced from 30
        driver.manage().window().maximize();
        
        logger.debug("Driver configured with optimized timeouts and window maximized");
    }
    
    /**
     * Gets current thread's driver instance
     * 
     * @return WebDriver instance for current thread
     * @throws RuntimeException if driver not initialized
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            String threadName = Thread.currentThread().getName();
            logger.error("Driver not initialized for thread: {}", threadName);
            throw new RuntimeException("Driver not initialized for thread: " + threadName);
        }
        return driver;
    }
    
    /**
     * Checks if driver is initialized for current thread
     * 
     * @return true if driver exists for current thread
     */
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
    
    /**
     * Quits driver for current thread
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        String threadName = Thread.currentThread().getName();
        
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver quit successfully for thread: {}", threadName);
            } catch (Exception e) {
                logger.warn("Error while quitting driver for thread {}: {}", threadName, e.getMessage());
            } finally {
                driverThreadLocal.remove();
                activeDrivers.remove(threadName);
            }
        } else {
            logger.warn("No driver found to quit for thread: {}", threadName);
        }
    }
    
    /**
     * Quits all active drivers
     */
    public static void quitAllDrivers() {
        logger.info("Quitting all active drivers. Count: {}", activeDrivers.size());
        
        activeDrivers.values().parallelStream().forEach(driver -> {
            try {
                if (driver != null) {
                    driver.quit();
                }
            } catch (Exception e) {
                logger.warn("Error while quitting driver: {}", e.getMessage());
            }
        });
        
        activeDrivers.clear();
        driverThreadLocal.remove();
        logger.info("All drivers quit successfully");
    }
    
    /**
     * Gets total number of active drivers
     * 
     * @return Number of active driver instances
     */
    public static int getActiveDriverCount() {
        return activeDrivers.size();
    }
}