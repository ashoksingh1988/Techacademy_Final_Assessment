package com.appium.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/**
 * CalculatorPage - Page Object for Android Calculator application
 * Provides methods to interact with Calculator screen elements and perform operations
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class CalculatorPage extends BasePage {
    
    // ==================== DISPLAY ELEMENTS ====================
    
    @AndroidFindBy(id = "com.google.android.calculator:id/result_final")
    private WebElement resultDisplay;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/result_preview")
    private WebElement previewDisplay;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/formula")
    private WebElement formulaDisplay;
    
    // ==================== NUMBER BUTTONS ====================
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_0")
    private WebElement digit0;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_1")
    private WebElement digit1;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_2")
    private WebElement digit2;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_3")
    private WebElement digit3;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_4")
    private WebElement digit4;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_5")
    private WebElement digit5;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_6")
    private WebElement digit6;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_7")
    private WebElement digit7;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_8")
    private WebElement digit8;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/digit_9")
    private WebElement digit9;
    
    // ==================== OPERATION BUTTONS ====================
    
    @AndroidFindBy(id = "com.google.android.calculator:id/op_add")
    private WebElement addButton;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/op_sub")
    private WebElement subtractButton;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/op_mul")
    private WebElement multiplyButton;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/op_div")
    private WebElement divideButton;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/eq")
    private WebElement equalsButton;
    
    // ==================== CONTROL BUTTONS ====================
    
    @AndroidFindBy(id = "com.google.android.calculator:id/clr")
    private WebElement clearButton;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/del")
    private WebElement deleteButton;
    
    @AndroidFindBy(id = "com.google.android.calculator:id/dec_point")
    private WebElement decimalButton;
    
    // ==================== PAGE VERIFICATION ====================
    
    /**
     * Verifies if Calculator page is loaded and displayed
     * 
     * @return true if Calculator page is loaded
     */
    @Override
    public boolean isPageLoaded() {
        try {
            return getCurrentPackage().contains("calculator") || 
                   isDisplayed(digit0) || 
                   isDisplayed(resultDisplay);
        } catch (Exception e) {
            logger.debug("Calculator page not loaded: {}", e.getMessage());
            return false;
        }
    }
    
    // ==================== NUMBER INPUT METHODS ====================
    
    /**
     * Clicks a specific digit button
     * 
     * @param digit Digit to click (0-9)
     */
    public void clickDigit(int digit) {
        logger.debug("Clicking digit: {}", digit);
        
        WebElement digitButton = getDigitButton(digit);
        if (digitButton != null && isDisplayed(digitButton)) {
            click(digitButton);
        } else {
            // Fallback using UIAutomator
            clickDigitFallback(digit);
        }
    }
    
    /**
     * Gets the WebElement for a specific digit
     */
    private WebElement getDigitButton(int digit) {
        switch (digit) {
            case 0: return digit0;
            case 1: return digit1;
            case 2: return digit2;
            case 3: return digit3;
            case 4: return digit4;
            case 5: return digit5;
            case 6: return digit6;
            case 7: return digit7;
            case 8: return digit8;
            case 9: return digit9;
            default: return null;
        }
    }
    
    /**
     * Fallback method to click digit using UIAutomator
     */
    private void clickDigitFallback(int digit) {
        try {
            WebElement digitElement = findByUIAutomator(
                String.format("new UiSelector().text(\"%d\")", digit));
            click(digitElement);
        } catch (Exception e) {
            logger.error("Failed to click digit {} using fallback method", digit, e);
            throw new RuntimeException("Unable to click digit: " + digit);
        }
    }
    
    /**
     * Enters a multi-digit number
     * 
     * @param number Number to enter as string
     */
    public void enterNumber(String number) {
        logger.info("Entering number: {}", number);
        
        for (char character : number.toCharArray()) {
            if (Character.isDigit(character)) {
                int digit = Character.getNumericValue(character);
                clickDigit(digit);
            } else if (character == '.' && isDisplayed(decimalButton)) {
                click(decimalButton);
            }
        }
    }
    
    // ==================== OPERATION METHODS ====================
    
    /**
     * Clicks the addition button
     */
    public void clickAdd() {
        logger.debug("Clicking add button");
        if (isDisplayed(addButton)) {
            click(addButton);
        } else {
            clickOperationFallback("+");
        }
    }
    
    /**
     * Clicks the subtraction button
     */
    public void clickSubtract() {
        logger.debug("Clicking subtract button");
        if (isDisplayed(subtractButton)) {
            click(subtractButton);
        } else {
            clickOperationFallback("−");
        }
    }
    
    /**
     * Clicks the multiplication button
     */
    public void clickMultiply() {
        logger.debug("Clicking multiply button");
        if (isDisplayed(multiplyButton)) {
            click(multiplyButton);
        } else {
            clickOperationFallback("×");
        }
    }
    
    /**
     * Clicks the division button
     */
    public void clickDivide() {
        logger.debug("Clicking divide button");
        if (isDisplayed(divideButton)) {
            click(divideButton);
        } else {
            clickOperationFallback("÷");
        }
    }
    
    /**
     * Clicks the equals button
     */
    public void clickEquals() {
        logger.debug("Clicking equals button");
        if (isDisplayed(equalsButton)) {
            click(equalsButton);
        } else {
            clickOperationFallback("=");
        }
    }
    
    /**
     * Fallback method to click operation using UIAutomator
     */
    private void clickOperationFallback(String operation) {
        try {
            WebElement operationElement = findByUIAutomator(
                String.format("new UiSelector().text(\"%s\")", operation));
            click(operationElement);
        } catch (Exception e) {
            logger.error("Failed to click operation {} using fallback method", operation, e);
            throw new RuntimeException("Unable to click operation: " + operation);
        }
    }
    
    // ==================== CONTROL METHODS ====================
    
    /**
     * Clears the calculator display
     */
    public void clear() {
        logger.debug("Clearing calculator");
        if (isDisplayed(clearButton)) {
            click(clearButton);
        } else {
            clickControlFallback("C");
        }
    }
    
    /**
     * Deletes the last entered digit/operation
     */
    public void delete() {
        logger.debug("Deleting last entry");
        if (isDisplayed(deleteButton)) {
            click(deleteButton);
        } else {
            clickControlFallback("DEL");
        }
    }
    
    /**
     * Fallback method to click control buttons using UIAutomator
     */
    private void clickControlFallback(String control) {
        try {
            WebElement controlElement = findByUIAutomator(
                String.format("new UiSelector().textContains(\"%s\")", control));
            click(controlElement);
        } catch (Exception e) {
            logger.error("Failed to click control {} using fallback method", control, e);
            throw new RuntimeException("Unable to click control: " + control);
        }
    }
    
    // ==================== RESULT RETRIEVAL METHODS ====================
    
    /**
     * Gets the current result from the display
     * 
     * @return Current result as string
     */
    public String getResult() {
        if (isDisplayed(resultDisplay)) {
            return getText(resultDisplay);
        } else if (isDisplayed(previewDisplay)) {
            return getText(previewDisplay);
        } else {
            return getResultFallback();
        }
    }
    
    /**
     * Gets the current formula from the display
     * 
     * @return Current formula as string
     */
    public String getFormula() {
        if (isDisplayed(formulaDisplay)) {
            return getText(formulaDisplay);
        } else {
            return getFormulaFallback();
        }
    }
    
    /**
     * Fallback method to get result using UIAutomator
     */
    private String getResultFallback() {
        try {
            WebElement resultElement = findByUIAutomator(
                "new UiSelector().resourceIdMatches(\".*result.*\")");
            return getText(resultElement);
        } catch (Exception e) {
            logger.warn("Unable to get result using fallback method: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Fallback method to get formula using UIAutomator
     */
    private String getFormulaFallback() {
        try {
            WebElement formulaElement = findByUIAutomator(
                "new UiSelector().resourceIdMatches(\".*formula.*\")");
            return getText(formulaElement);
        } catch (Exception e) {
            logger.warn("Unable to get formula using fallback method: {}", e.getMessage());
            return "";
        }
    }
    
    // ==================== CALCULATION METHODS ====================
    
    /**
     * Performs addition operation
     * 
     * @param firstNumber First operand
     * @param secondNumber Second operand
     * @return Result of addition
     */
    public String performAddition(String firstNumber, String secondNumber) {
        logger.info("Performing addition: {} + {}", firstNumber, secondNumber);
        clear();
        enterNumber(firstNumber);
        clickAdd();
        enterNumber(secondNumber);
        clickEquals();
        return getResult();
    }
    
    /**
     * Performs subtraction operation
     * 
     * @param firstNumber First operand
     * @param secondNumber Second operand
     * @return Result of subtraction
     */
    public String performSubtraction(String firstNumber, String secondNumber) {
        logger.info("Performing subtraction: {} - {}", firstNumber, secondNumber);
        clear();
        enterNumber(firstNumber);
        clickSubtract();
        enterNumber(secondNumber);
        clickEquals();
        return getResult();
    }
    
    /**
     * Performs multiplication operation
     * 
     * @param firstNumber First operand
     * @param secondNumber Second operand
     * @return Result of multiplication
     */
    public String performMultiplication(String firstNumber, String secondNumber) {
        logger.info("Performing multiplication: {} × {}", firstNumber, secondNumber);
        clear();
        enterNumber(firstNumber);
        clickMultiply();
        enterNumber(secondNumber);
        clickEquals();
        return getResult();
    }
    
    /**
     * Performs division operation
     * 
     * @param firstNumber First operand
     * @param secondNumber Second operand
     * @return Result of division
     */
    public String performDivision(String firstNumber, String secondNumber) {
        logger.info("Performing division: {} ÷ {}", firstNumber, secondNumber);
        clear();
        enterNumber(firstNumber);
        clickDivide();
        enterNumber(secondNumber);
        clickEquals();
        return getResult();
    }
}
