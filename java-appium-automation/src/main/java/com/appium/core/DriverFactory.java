package com.appium.core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class DriverFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<AndroidDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConcurrentHashMap<String, AndroidDriver> activeDrivers = new ConcurrentHashMap<>();
    
    private static final String DEFAULT_APPIUM_URL = "http://127.0.0.1:4723";
    
    private DriverFactory() {
    }
    
    public static AndroidDriver createAndroidDriver(String deviceName, String platformVersion, 
                                                   String appPackage, String appActivity) {
        
        logger.info("Initializing Android driver for device: {}, Platform: {}", deviceName, platformVersion);
        logger.info("Using manual UIAutomator2 server installation - skipServerInstallation=true");
        
        try {
            UiAutomator2Options options = buildDriverOptions(deviceName, platformVersion, appPackage, appActivity);
            URL serverUrl = new URL(getAppiumServerUrl());
            
            AndroidDriver driver = new AndroidDriver(serverUrl, options);
            configureDriver(driver);
            
            String threadName = Thread.currentThread().getName();
            driverThreadLocal.set(driver);
            activeDrivers.put(threadName, driver);
            
            logger.info("Android driver initialized successfully for thread: {}", threadName);
            return driver;
            
        } catch (MalformedURLException e) {
            logger.error("Invalid Appium server URL: {}", getAppiumServerUrl(), e);
            throw new RuntimeException("Failed to create driver due to invalid server URL", e);
        } catch (Exception e) {
            logger.error("Failed to initialize Android driver for device: {}", deviceName, e);
            throw new RuntimeException("Driver initialization failed", e);
        }
    }
    
    private static UiAutomator2Options buildDriverOptions(String deviceName, String platformVersion,
                                                          String appPackage, String appActivity) {
        
        UiAutomator2Options options = new UiAutomator2Options();
        
        // Device identification
        options.setDeviceName("OPPOA54");
        options.setPlatformName("Android");
        options.setPlatformVersion("11");
        options.setAutomationName("UiAutomator2");
        options.setCapability("udid", "PZPVSC95GMKNGUBQ");
        
        // App configuration - Your lead's confirmed settings
        if (appPackage != null && !appPackage.isEmpty()) {
            options.setCapability("appPackage", appPackage);
            if (appActivity != null && !appActivity.isEmpty()) {
                options.setCapability("appActivity", appActivity);
            }
            // Your lead's additional capabilities
            options.setCapability("appWaitActivity", "*");
        }
        
        // Manual UIAutomator2 server setup - CRITICAL for pre-installed APKs
        options.setCapability("skipServerInstallation", true);
        options.setCapability("skipDeviceInitialization", true);
        options.setCapability("disableHiddenApiPolicyCheck", true);
        options.setCapability("ignoreHiddenApiPolicyError", true);
        
        // Timeout configurations
        options.setNewCommandTimeout(Duration.ofSeconds(300));
        options.setCapability("uiautomator2ServerInstallTimeout", 90000);
        options.setCapability("uiautomator2ServerLaunchTimeout", 90000);
        options.setCapability("androidInstallTimeout", 120000);
        
        // Stability settings - Your lead's recommendations
        options.setCapability("unicodeKeyboard", false);
        options.setCapability("resetKeyboard", false);
        options.setCapability("noReset", true);  // Your lead's suggestion
        options.setCapability("fullReset", false);
        options.setCapability("autoGrantPermissions", false);
        options.setCapability("enforceXPath1", true);
        options.setCapability("disableIdLocatorAutocompletion", true);
        
        logger.info("Driver options configured for OPPOA54 (CPH2239) with manual UIAutomator2 setup");
        logger.info("App Package: {}, App Activity: {}", appPackage, appActivity);
        return options;
    }
    
    private static void configureDriver(AndroidDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        logger.debug("Driver configured with standard timeouts");
    }
    
    public static AndroidDriver getDriver() {
        AndroidDriver driver = driverThreadLocal.get();
        if (driver == null) {
            String threadName = Thread.currentThread().getName();
            logger.error("Driver not initialized for thread: {}", threadName);
            throw new RuntimeException("Driver not initialized for thread: " + threadName);
        }
        return driver;
    }
    
    public static boolean isDriverInitialized() {
        return driverThreadLocal.get() != null;
    }
    
    public static void quitDriver() {
        AndroidDriver driver = driverThreadLocal.get();
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
    
    public static int getActiveDriverCount() {
        return activeDrivers.size();
    }
    
    private static String getAppiumServerUrl() {
        return ConfigurationManager.getInstance().getProperty("appium.server.url", DEFAULT_APPIUM_URL);
    }
}
