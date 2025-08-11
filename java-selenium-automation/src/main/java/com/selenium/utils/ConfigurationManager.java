package com.selenium.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigurationManager - Centralized configuration management
 * Handles properties files and environment variables
 * Implements Singleton pattern for global access
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class ConfigurationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static volatile ConfigurationManager instance;
    
    private final Properties properties;
    
    private ConfigurationManager() {
        this.properties = new Properties();
        loadConfigurations();
    }
    
    /**
     * Gets singleton instance using double-checked locking
     * 
     * @return ConfigurationManager instance
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads all configuration files
     */
    private void loadConfigurations() {
        loadPropertiesFile();
    }
    
    /**
     * Loads properties from application.properties
     */
    private void loadPropertiesFile() {
        try (InputStream input = getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("Application properties loaded successfully");
            } else {
                logger.warn("application.properties not found, using default values");
            }
        } catch (IOException e) {
            logger.error("Failed to load application properties", e);
        }
    }
    
    /**
     * Gets resource as stream with proper error handling
     */
    private InputStream getResourceAsStream(String resourcePath) {
        return getClass().getClassLoader().getResourceAsStream(resourcePath);
    }
    
    /**
     * Gets property value by key
     * 
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        // Check system properties first (for command line overrides)
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        
        // Check environment variables
        String envValue = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envValue != null) {
            return envValue;
        }
        
        // Check properties file
        return properties.getProperty(key);
    }
    
    /**
     * Gets property value with default fallback
     * 
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Gets integer property value
     * 
     * @param key Property key
     * @param defaultValue Default value if key not found or invalid
     * @return Integer property value
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property {}: {}", key, getProperty(key));
            return defaultValue;
        }
    }
    
    /**
     * Gets boolean property value
     * 
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Boolean property value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    // Convenience methods for common configurations
    
    public String getBaseUrl() {
        return getProperty("base.url", "https://www.saucedemo.com");
    }
    
    public String getDefaultBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public boolean isHeadlessMode() {
        return getBooleanProperty("headless", false);
    }
    
    public int getImplicitWaitTimeout() {
        return getIntProperty("timeouts.implicit.wait", 10);
    }
    
    public int getExplicitWaitTimeout() {
        return getIntProperty("timeouts.explicit.wait", 30);
    }
    
    public int getPageLoadTimeout() {
        return getIntProperty("timeouts.page.load", 60);
    }
    
    public boolean isScreenshotOnFailureEnabled() {
        return getBooleanProperty("reporting.screenshot.on.failure", true);
    }
    
    public String getReportsDirectory() {
        return getProperty("reporting.output.directory", "reports");
    }
    
    public boolean isParallelExecutionEnabled() {
        return getBooleanProperty("execution.parallel.enabled", false);
    }
    
    public int getParallelThreadCount() {
        return getIntProperty("execution.parallel.thread.count", 1);
    }
}
