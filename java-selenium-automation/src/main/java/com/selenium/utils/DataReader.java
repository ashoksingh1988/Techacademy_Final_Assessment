package com.selenium.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

/**
 * DataReader utility class to read test data from various sources
 * Supports TestNG parameters, system properties, and configuration files
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class DataReader {
    
    private static final Logger logger = LoggerFactory.getLogger(DataReader.class);
    
    /**
     * Read parameter from TestNG context
     * @param context TestNG context
     * @param parameterName Parameter name
     * @return Parameter value
     */
    public static String getParameter(ITestContext context, String parameterName) {
        String parameterValue = context.getCurrentXmlTest().getParameter(parameterName);
        if (parameterValue == null) {
            logger.warn("Parameter '{}' not found in TestNG XML", parameterName);
            return "";
        }
        logger.debug("Parameter '{}' = '{}'", parameterName, parameterValue);
        return parameterValue;
    }
    
    /**
     * Read parameter with default value
     * @param context TestNG context
     * @param parameterName Parameter name
     * @param defaultValue Default value if parameter not found
     * @return Parameter value or default value
     */
    public static String getParameter(ITestContext context, String parameterName, String defaultValue) {
        String parameterValue = context.getCurrentXmlTest().getParameter(parameterName);
        String result = parameterValue != null ? parameterValue : defaultValue;
        logger.debug("Parameter '{}' = '{}' (default: '{}')", parameterName, result, defaultValue);
        return result;
    }
    
    /**
     * Read username from TestNG parameters
     * @param context TestNG context
     * @return Username
     */
    public static String getUsername(ITestContext context) {
        return getParameter(context, "username", "standard_user");
    }
    
    /**
     * Read password from TestNG parameters
     * @param context TestNG context
     * @return Password
     */
    public static String getPassword(ITestContext context) {
        return getParameter(context, "password", "secret_sauce");
    }
    
    /**
     * Read invalid username from TestNG parameters
     * @param context TestNG context
     * @return Invalid username
     */
    public static String getInvalidUsername(ITestContext context) {
        return getParameter(context, "invalidUsername", "invalid_user");
    }
    
    /**
     * Read invalid password from TestNG parameters
     * @param context TestNG context
     * @return Invalid password
     */
    public static String getInvalidPassword(ITestContext context) {
        return getParameter(context, "invalidPassword", "invalid_password");
    }
    
    /**
     * Read base URL from TestNG parameters
     * @param context TestNG context
     * @return Base URL
     */
    public static String getBaseUrl(ITestContext context) {
        return getParameter(context, "baseUrl", "https://www.saucedemo.com");
    }
    
    /**
     * Read browser from TestNG parameters
     * @param context TestNG context
     * @return Browser name
     */
    public static String getBrowser(ITestContext context) {
        return getParameter(context, "browser", "chrome");
    }
    
    /**
     * Read headless mode from TestNG parameters
     * @param context TestNG context
     * @return Headless mode
     */
    public static boolean isHeadless(ITestContext context) {
        String headless = getParameter(context, "headless", "false");
        return Boolean.parseBoolean(headless);
    }
}
