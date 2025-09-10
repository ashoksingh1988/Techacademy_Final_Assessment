package com.appium.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Enhanced DriverFactory with Local Chrome Support and Performance Optimization
 * Features: Local Chrome binary support, Enhanced reporting integration, Performance tracking
 *
 * @author Asim Kumar Singh
 * @version 2.0.0 - Enhanced for Lead Review Requirements
 */
public class EnhancedDriverFactory {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedDriverFactory.class);
    private static final ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();
    
    // Configuration constants
    private static final String DEFAULT_APPIUM_SERVER = "http://127.0.0.1:4723";
    private static final int DEFAULT_IMPLICIT_WAIT = 10;
    private static final int DEFAULT_PAGE_LOAD_TIMEOUT = 30;

    private EnhancedDriverFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates enhanced Android driver with local Chrome support
     */
    public static AppiumDriver createEnhancedAndroidDriver() {
        logger.info("🚀 Creating Enhanced Android Driver with Local Chrome Support");
        
        try {
            // Get configuration from environment or use defaults
            ConfigurationManager config = ConfigurationManager.getInstance();
            
            // Create UiAutomator2 options with enhanced configuration
            UiAutomator2Options options = createEnhancedUiAutomator2Options(config);
            
            // Configure Chrome browser options if needed for hybrid apps
            configureChromeOptions(options);
            
            // Create driver with enhanced configuration
            String appiumServerUrl = getAppiumServerUrl();
            AppiumDriver driver = new AndroidDriver(new URL(appiumServerUrl), options);
            
            // Configure enhanced timeouts and settings
            configureEnhancedDriverSettings(driver);
            
            // Store in thread local for thread-safe access
            driverThreadLocal.set(driver);
            
            logger.info("✅ Enhanced Android Driver created successfully");
            logger.info("📱 Device: {} | Platform: {} | App: {}", 
                       config.getDeviceName(), 
                       config.getPlatformVersion(), 
                       config.getAppPackage());
            
            return driver;
            
        } catch (MalformedURLException e) {
            logger.error("❌ Invalid Appium server URL", e);
            throw new RuntimeException("Failed to create Android driver: Invalid server URL", e);
        } catch (Exception e) {
            logger.error("❌ Failed to create Enhanced Android Driver", e);
            throw new RuntimeException("Failed to create Android driver", e);
        }
    }

    /**
     * Creates enhanced UiAutomator2 options with comprehensive configuration
     */
    private static UiAutomator2Options createEnhancedUiAutomator2Options(ConfigurationManager config) {
        UiAutomator2Options options = new UiAutomator2Options();
        
        // Basic device configuration
        options.setDeviceName(config.getDeviceName());
        options.setPlatformVersion(config.getPlatformVersion());
        options.setUdid(config.getDeviceUdid());
        
        // App configuration
        options.setAppPackage(config.getAppPackage());
        options.setAppActivity(config.getAppActivity());
        
        // Enhanced performance settings
        options.setNoReset(true);
        options.setFullReset(false);
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        
        // Skip server installation for better performance
        options.setSkipServerInstallation(true);
        
        // Enhanced automation settings
        options.setAutoGrantPermissions(true);
        options.setIgnoreHiddenApiPolicyError(true);
        options.setDisableWindowAnimation(true);
        
        // Performance optimization
        options.setSkipUnlock(true);
        options.setSkipLogcatCapture(false); // Keep for debugging
        
        // Enhanced reporting settings
        String enhancedReporting = System.getenv("ENHANCED_REPORTING");
        if ("true".equalsIgnoreCase(enhancedReporting)) {
            options.setCapability("enablePerformanceLogging", true);
            options.setCapability("enableNetworkLogging", true);
            logger.info("📊 Enhanced reporting enabled for performance tracking");
        }
        
        logger.info("⚙️ UiAutomator2 options configured with enhanced settings");
        return options;
    }

    /**
     * Configures Chrome options for hybrid app testing with local Chrome support
     */
    private static void configureChromeOptions(UiAutomator2Options options) {
        // Get Chrome binary path from environment
        String chromeBinary = System.getenv("CHROME_BINARY");
        String headlessMode = System.getenv("HEADLESS");
        
        if (chromeBinary != null && !chromeBinary.isEmpty()) {
            File chromeFile = new File(chromeBinary);
            if (chromeFile.exists()) {
                // Configure Chrome options for hybrid app testing
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setBinary(chromeBinary);
                
                // Set headless mode based on environment
                if (!"false".equalsIgnoreCase(headlessMode)) {
                    chromeOptions.addArguments("--headless");
                    logger.info("🔍 Chrome configured in headless mode");
                } else {
                    chromeOptions.addArguments("--start-maximized");
                    logger.info("🌐 Chrome configured in GUI mode with local binary: {}", chromeBinary);
                }
                
                // Performance optimizations for Chrome
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--disable-plugins");
                
                // Add Chrome options to Appium capabilities
                options.setCapability("chromeOptions", chromeOptions.asMap());
                
                logger.info("✅ Local Chrome binary configured for hybrid app testing");
            } else {
                logger.warn("⚠️ Chrome binary not found at: {}. Using default Chrome.", chromeBinary);
            }
        } else {
            logger.info("ℹ️ No custom Chrome binary specified. Using system Chrome.");
        }
    }

    /**
     * Configures enhanced driver settings for optimal performance
     */
    private static void configureEnhancedDriverSettings(AppiumDriver driver) {
        // Set enhanced timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(DEFAULT_IMPLICIT_WAIT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(DEFAULT_PAGE_LOAD_TIMEOUT));
        
        // Additional performance settings
        try {
            // Configure network conditions for better performance testing
            driver.setConnection(org.openqa.selenium.html5.ConnectionType.ALL);
            logger.info("📶 Network connection configured for performance testing");
        } catch (Exception e) {
            logger.debug("Network connection configuration not supported: {}", e.getMessage());
        }
        
        logger.info("⚙️ Enhanced driver settings configured successfully");
    }

    /**
     * Gets Appium server URL from environment or uses default
     */
    private static String getAppiumServerUrl() {
        String serverUrl = System.getenv("APPIUM_SERVER");
        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            serverUrl = DEFAULT_APPIUM_SERVER;
        }
        
        logger.info("🔗 Appium Server URL: {}", serverUrl);
        return serverUrl;
    }

    /**
     * Gets current driver instance (thread-safe)
     */
    public static AppiumDriver getDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver == null) {
            logger.warn("⚠️ No driver found in current thread. Creating new driver.");
            return createEnhancedAndroidDriver();
        }
        return driver;
    }

    /**
     * Quits driver and cleans up resources
     */
    public static void quitDriver() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                logger.info("🔄 Quitting Enhanced Android Driver");
                driver.quit();
                logger.info("✅ Driver quit successfully");
            } catch (Exception e) {
                logger.error("❌ Error while quitting driver", e);
            } finally {
                driverThreadLocal.remove();
            }
        } else {
            logger.info("ℹ️ No driver to quit in current thread");
        }
    }

    /**
     * Checks if driver is available and responsive
     */
    public static boolean isDriverAvailable() {
        try {
            AppiumDriver driver = driverThreadLocal.get();
            if (driver != null) {
                // Try to get current activity to check if driver is responsive
                String currentActivity = driver.getCurrentPackage();
                return currentActivity != null;
            }
        } catch (Exception e) {
            logger.debug("Driver availability check failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Gets driver capabilities for reporting
     */
    public static String getDriverCapabilities() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                return driver.getCapabilities().toString();
            } catch (Exception e) {
                logger.debug("Failed to get driver capabilities: {}", e.getMessage());
            }
        }
        return "Driver capabilities not available";
    }

    /**
     * Restarts driver if needed (for error recovery)
     */
    public static AppiumDriver restartDriver() {
        logger.info("🔄 Restarting Enhanced Android Driver");
        quitDriver();
        return createEnhancedAndroidDriver();
    }

    /**
     * Gets performance metrics from driver
     */
    public static String getPerformanceMetrics() {
        AppiumDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                // Get performance logs if available
                return driver.manage().logs().get("performance").toString();
            } catch (Exception e) {
                logger.debug("Performance metrics not available: {}", e.getMessage());
            }
        }
        return "Performance metrics not available";
    }
}
