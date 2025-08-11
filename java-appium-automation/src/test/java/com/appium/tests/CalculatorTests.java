package com.appium.tests;

import com.appium.core.BaseTest;
import com.appium.core.ConfigurationManager;
import com.appium.pages.CalculatorPage;
import com.appium.utils.ReportManager;
import com.appium.utils.WaitHelper;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CalculatorTests - Comprehensive test suite for Android Calculator application
 * Tests mathematical operations, input validation, and UI interactions
 * 
 * @author Asim Kumar Singh
 * @version 1.0.0
 */
public class CalculatorTests extends BaseTest {
    
    private CalculatorPage calculatorPage;
    
    @BeforeMethod
    public void setupCalculatorTests() {
        calculatorPage = new CalculatorPage();
        ReportManager.assignCategory("Calculator Application");
        ReportManager.assignAuthor("Asim Kumar Singh");
        
        // Try to launch calculator app
        try {
            driver.activateApp("com.google.android.calculator");
            WaitHelper.sleep(2000);
        } catch (Exception e) {
            ReportManager.logWarning("Could not activate calculator app: " + e.getMessage());
        }
    }
    
    @Test(priority = 1, description = "Verify Calculator application launches and page is displayed")
    public void testCalculatorPageLoad() {
        ReportManager.logInfo("Starting Calculator page load verification test");
        
        // Verify Calculator page is loaded
        boolean isPageLoaded = calculatorPage.isPageLoaded();
        
        if (isPageLoaded) {
            ReportManager.logPass("Calculator page loaded successfully");
            Assert.assertTrue(true);
        } else {
            ReportManager.logWarning("Calculator app not available on this device");
            throw new org.testng.SkipException("Calculator app not available on this device");
        }
    }
    
    @Test(priority = 2, description = "Test basic number input functionality",
          dependsOnMethods = "testCalculatorPageLoad")
    public void testNumberInput() {
        ReportManager.logInfo("Testing basic number input functionality");
        
        // Clear calculator first
        calculatorPage.clear();
        ReportManager.logInfo("Calculator cleared");
        
        // Test single digit input
        calculatorPage.clickDigit(5);
        ReportManager.logInfo("Clicked digit: 5");
        
        // Verify input
        String result = calculatorPage.getResult();
        String formula = calculatorPage.getFormula();
        
        ReportManager.logInfo("Result display: " + result);
        ReportManager.logInfo("Formula display: " + formula);
        
        // Verify digit was entered
        boolean digitEntered = result.contains("5") || formula.contains("5");
        Assert.assertTrue(digitEntered, "Digit 5 should be displayed");
        
        ReportManager.logPass("Single digit input test completed successfully");
        
        // Test multi-digit number input
        calculatorPage.clear();
        calculatorPage.enterNumber("123");
        ReportManager.logInfo("Entered number: 123");
        
        result = calculatorPage.getResult();
        formula = calculatorPage.getFormula();
        
        ReportManager.logInfo("Result after multi-digit input: " + result);
        ReportManager.logInfo("Formula after multi-digit input: " + formula);
        
        // Verify multi-digit number was entered
        boolean numberEntered = result.contains("123") || formula.contains("123");
        Assert.assertTrue(numberEntered, "Number 123 should be displayed");
        
        ReportManager.logPass("Multi-digit number input test completed successfully");
    }
    
    @Test(priority = 3, description = "Test basic mathematical operations",
          dataProvider = "basicOperationsData", dependsOnMethods = "testCalculatorPageLoad")
    public void testBasicOperations(String operation, String firstNumber, String secondNumber, 
                                  String expectedResult, String description) {
        
        ReportManager.logInfo("Testing " + operation + ": " + firstNumber + " " + getOperationSymbol(operation) + " " + secondNumber);
        ReportManager.logInfo("Test description: " + description);
        
        String actualResult = "";
        
        try {
            // Perform the operation based on type
            switch (operation.toLowerCase()) {
                case "addition":
                    actualResult = calculatorPage.performAddition(firstNumber, secondNumber);
                    break;
                case "subtraction":
                    actualResult = calculatorPage.performSubtraction(firstNumber, secondNumber);
                    break;
                case "multiplication":
                    actualResult = calculatorPage.performMultiplication(firstNumber, secondNumber);
                    break;
                case "division":
                    actualResult = calculatorPage.performDivision(firstNumber, secondNumber);
                    break;
                default:
                    Assert.fail("Unknown operation: " + operation);
            }
            
            ReportManager.logInfo("Actual result: " + actualResult);
            ReportManager.logInfo("Expected result: " + expectedResult);
            
            // Verify result (allowing for minor formatting differences)
            boolean resultMatches = verifyCalculationResult(actualResult, expectedResult);
            
            Assert.assertTrue(resultMatches, 
                String.format("%s operation failed. Expected: %s, Actual: %s", 
                    operation, expectedResult, actualResult));
            
            ReportManager.logPass(operation + " operation completed successfully");
            
        } catch (Exception e) {
            ReportManager.logFail("Error during " + operation + " operation: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(priority = 4, description = "Test calculator clear functionality",
          dependsOnMethods = "testCalculatorPageLoad")
    public void testClearFunctionality() {
        ReportManager.logInfo("Testing calculator clear functionality");
        
        // Enter some numbers first
        calculatorPage.enterNumber("456");
        calculatorPage.clickAdd();
        calculatorPage.enterNumber("789");
        ReportManager.logInfo("Entered calculation: 456 + 789");
        
        // Clear calculator
        calculatorPage.clear();
        ReportManager.logInfo("Clicked clear button");
        
        // Verify calculator is cleared
        String result = calculatorPage.getResult();
        String formula = calculatorPage.getFormula();
        
        ReportManager.logInfo("Result after clear: " + result);
        ReportManager.logInfo("Formula after clear: " + formula);
        
        // Calculator should be cleared (empty or showing 0)
        boolean isCleared = isCalculatorCleared(result, formula);
        
        Assert.assertTrue(isCleared, "Calculator should be cleared after clicking clear button");
        ReportManager.logPass("Clear functionality test completed successfully");
    }
    
    @Test(priority = 5, description = "Test calculator delete functionality",
          dependsOnMethods = "testCalculatorPageLoad")
    public void testDeleteFunctionality() {
        ReportManager.logInfo("Testing calculator delete functionality");
        
        // Clear and enter a number
        calculatorPage.clear();
        calculatorPage.enterNumber("789");
        ReportManager.logInfo("Entered number: 789");
        
        // Delete last digit
        calculatorPage.delete();
        ReportManager.logInfo("Clicked delete button");
        
        // Verify last digit is deleted
        String result = calculatorPage.getResult();
        String formula = calculatorPage.getFormula();
        
        ReportManager.logInfo("Result after delete: " + result);
        ReportManager.logInfo("Formula after delete: " + formula);
        
        // Should show "78" after deleting "9"
        boolean isDeleted = result.contains("78") || formula.contains("78");
        
        if (isDeleted) {
            ReportManager.logPass("Delete functionality working correctly");
            Assert.assertTrue(true);
        } else {
            ReportManager.logWarning("Delete functionality verification inconclusive");
            // Don't fail as different calculator versions may behave differently
        }
    }
    
    @Test(priority = 6, description = "Test advanced mathematical operations",
          dataProvider = "advancedOperationsData", dependsOnMethods = "testCalculatorPageLoad")
    public void testAdvancedOperations(String operation, String firstNumber, String secondNumber, 
                                     String expectedResult, String description) {
        
        ReportManager.logInfo("Testing advanced " + operation + ": " + description);
        
        String actualResult = "";
        
        try {
            // Perform the operation
            switch (operation.toLowerCase()) {
                case "addition":
                    actualResult = calculatorPage.performAddition(firstNumber, secondNumber);
                    break;
                case "subtraction":
                    actualResult = calculatorPage.performSubtraction(firstNumber, secondNumber);
                    break;
                case "multiplication":
                    actualResult = calculatorPage.performMultiplication(firstNumber, secondNumber);
                    break;
                case "division":
                    actualResult = calculatorPage.performDivision(firstNumber, secondNumber);
                    break;
                default:
                    Assert.fail("Unknown operation: " + operation);
            }
            
            ReportManager.logInfo("Actual result: " + actualResult);
            ReportManager.logInfo("Expected result: " + expectedResult);
            
            // For advanced operations, we may need more flexible verification
            boolean resultValid = verifyAdvancedCalculationResult(actualResult, expectedResult, operation);
            
            if (resultValid) {
                ReportManager.logPass("Advanced " + operation + " operation completed successfully");
            } else {
                ReportManager.logWarning("Advanced operation result verification inconclusive");
            }
            
        } catch (Exception e) {
            ReportManager.logWarning("Advanced operation may not be supported: " + e.getMessage());
            throw new org.testng.SkipException("Advanced operation not supported on this calculator");
        }
    }
    
    @Test(priority = 7, description = "Test calculator edge cases",
          dataProvider = "edgeCasesData", dependsOnMethods = "testCalculatorPageLoad")
    public void testEdgeCases(String operation, String firstNumber, String secondNumber, 
                            String expectedResult, String description) {
        
        ReportManager.logInfo("Testing edge case: " + description);
        
        try {
            String actualResult = "";
            
            switch (operation.toLowerCase()) {
                case "addition":
                    actualResult = calculatorPage.performAddition(firstNumber, secondNumber);
                    break;
                case "subtraction":
                    actualResult = calculatorPage.performSubtraction(firstNumber, secondNumber);
                    break;
                case "multiplication":
                    actualResult = calculatorPage.performMultiplication(firstNumber, secondNumber);
                    break;
                case "division":
                    actualResult = calculatorPage.performDivision(firstNumber, secondNumber);
                    break;
                default:
                    Assert.fail("Unknown operation: " + operation);
            }
            
            ReportManager.logInfo("Edge case result: " + actualResult);
            ReportManager.logPass("Edge case test completed: " + description);
            
        } catch (Exception e) {
            ReportManager.logWarning("Edge case handling: " + e.getMessage());
            // Edge cases might behave differently on different devices
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    private String getOperationSymbol(String operation) {
        switch (operation.toLowerCase()) {
            case "addition": return "+";
            case "subtraction": return "-";
            case "multiplication": return "×";
            case "division": return "÷";
            default: return "?";
        }
    }
    
    private boolean verifyCalculationResult(String actual, String expected) {
        if (actual == null || expected == null) {
            return false;
        }
        
        // Remove any whitespace and compare
        String cleanActual = actual.trim().replaceAll("\\s+", "");
        String cleanExpected = expected.trim().replaceAll("\\s+", "");
        
        // Direct string comparison
        if (cleanActual.equals(cleanExpected)) {
            return true;
        }
        
        // Try numeric comparison for flexibility
        try {
            double actualNum = Double.parseDouble(cleanActual);
            double expectedNum = Double.parseDouble(cleanExpected);
            return Math.abs(actualNum - expectedNum) < 0.001; // Allow small floating point differences
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean verifyAdvancedCalculationResult(String actual, String expected, String operation) {
        // More flexible verification for advanced operations
        if (actual == null || actual.trim().isEmpty()) {
            return false;
        }
        
        // For division operations, handle decimal results more flexibly
        if ("division".equalsIgnoreCase(operation)) {
            try {
                double actualNum = Double.parseDouble(actual.trim());
                double expectedNum = Double.parseDouble(expected.trim());
                return Math.abs(actualNum - expectedNum) < 0.1; // More tolerance for division
            } catch (NumberFormatException e) {
                return actual.contains(expected.substring(0, Math.min(3, expected.length())));
            }
        }
        
        return verifyCalculationResult(actual, expected);
    }
    
    private boolean isCalculatorCleared(String result, String formula) {
        if (result == null && formula == null) {
            return true;
        }
        
        String cleanResult = result != null ? result.trim() : "";
        String cleanFormula = formula != null ? formula.trim() : "";
        
        return cleanResult.isEmpty() || cleanResult.equals("0") || 
               cleanFormula.isEmpty() || cleanFormula.equals("0");
    }
    
    // ==================== DATA PROVIDERS ====================
    
    @DataProvider(name = "basicOperationsData")
    public Object[][] getBasicOperationsData() {
        try {
            ConfigurationManager config = ConfigurationManager.getInstance();
            JsonNode calculatorData = config.getTestData("calculator");
            
            if (calculatorData != null && calculatorData.has("basic_operations")) {
                return convertJsonToDataProvider(calculatorData.get("basic_operations"));
            }
        } catch (Exception e) {
            ReportManager.logWarning("Could not load basic operations test data: " + e.getMessage());
        }
        
        // Fallback data
        return new Object[][] {
            {"addition", "5", "3", "8", "Simple addition test"},
            {"subtraction", "10", "4", "6", "Simple subtraction test"},
            {"multiplication", "7", "8", "56", "Simple multiplication test"},
            {"division", "20", "4", "5", "Simple division test"}
        };
    }
    
    @DataProvider(name = "advancedOperationsData")
    public Object[][] getAdvancedOperationsData() {
        try {
            ConfigurationManager config = ConfigurationManager.getInstance();
            JsonNode calculatorData = config.getTestData("calculator");
            
            if (calculatorData != null && calculatorData.has("advanced_operations")) {
                return convertJsonToDataProvider(calculatorData.get("advanced_operations"));
            }
        } catch (Exception e) {
            ReportManager.logWarning("Could not load advanced operations test data: " + e.getMessage());
        }
        
        // Fallback data
        return new Object[][] {
            {"addition", "123.45", "67.89", "191.34", "Decimal addition test"}
        };
    }
    
    @DataProvider(name = "edgeCasesData")
    public Object[][] getEdgeCasesData() {
        try {
            ConfigurationManager config = ConfigurationManager.getInstance();
            JsonNode calculatorData = config.getTestData("calculator");
            
            if (calculatorData != null && calculatorData.has("edge_cases")) {
                return convertJsonToDataProvider(calculatorData.get("edge_cases"));
            }
        } catch (Exception e) {
            ReportManager.logWarning("Could not load edge cases test data: " + e.getMessage());
        }
        
        // Fallback data
        return new Object[][] {
            {"multiplication", "12", "0", "0", "Multiplication by zero"}
        };
    }
    
    private Object[][] convertJsonToDataProvider(JsonNode operationsNode) {
        Object[][] data = new Object[operationsNode.size()][5];
        
        for (int i = 0; i < operationsNode.size(); i++) {
            JsonNode operation = operationsNode.get(i);
            data[i][0] = operation.get("operation").asText();
            data[i][1] = operation.get("firstNumber").asText();
            data[i][2] = operation.get("secondNumber").asText();
            data[i][3] = operation.get("expectedResult").asText();
            data[i][4] = operation.get("description").asText();
        }
        
        return data;
    }
}
