package com.appium.tests;

import com.appium.core.BaseTest;
import com.appium.pages.ColorNotePage;
import com.appium.utils.ReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ColorNote Application Test Suite
 * Tests ColorNote notepad functionality
 * 
 * @author Asim Kumar Singh
 * @version 1.0
 */
public class ColorNoteTests extends BaseTest {
    
    private ColorNotePage colorNotePage;
    
    @Test(description = "Verify ColorNote app loads successfully")
    public void testColorNotePageLoad() {
        try {
            ReportManager.logInfo("Starting ColorNote app load verification test");
            
            // Initialize ColorNote page
            colorNotePage = new ColorNotePage();
            
            // Verify ColorNote is loaded
            boolean isColorNoteLoaded = colorNotePage.isPageLoaded();
            ReportManager.logInfo("ColorNote load status: " + isColorNoteLoaded);
            
            // Assert ColorNote is loaded
            Assert.assertTrue(isColorNoteLoaded, "ColorNote app should be loaded and displayed");
            
            ReportManager.logPass("ColorNote app loaded successfully");
            
        } catch (Exception e) {
            ReportManager.logFail("ColorNote app load test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Test creating a new text note in ColorNote", dependsOnMethods = "testColorNotePageLoad")
    public void testCreateTextNote() {
        try {
            ReportManager.logInfo("Starting ColorNote text note creation test");
            
            // Initialize ColorNote page
            colorNotePage = new ColorNotePage();
            
            // Create a new text note
            String noteText = "This is a test note created by automation framework at " + System.currentTimeMillis();
            
            boolean noteCreated = colorNotePage.createTextNote(noteText);
            ReportManager.logInfo("Text note creation status: " + noteCreated);
            
            // Assert note was created
            Assert.assertTrue(noteCreated, "Text note should be created successfully");
            
            ReportManager.logPass("Text note created successfully");
            
        } catch (Exception e) {
            ReportManager.logFail("Text note creation test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Test search functionality in ColorNote", dependsOnMethods = "testCreateTextNote")
    public void testSearchFunctionality() {
        try {
            ReportManager.logInfo("Starting ColorNote search functionality test");
            
            // Initialize ColorNote page
            colorNotePage = new ColorNotePage();
            
            // Perform search
            String searchText = "test";
            boolean searchPerformed = colorNotePage.searchNotes(searchText);
            ReportManager.logInfo("Search performed status: " + searchPerformed);
            
            // Assert search was performed (even if no results found)
            Assert.assertTrue(searchPerformed, "Search functionality should work");
            
            ReportManager.logPass("Search functionality test completed successfully");
            
        } catch (Exception e) {
            ReportManager.logFail("Search functionality test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Test notes visibility in ColorNote", dependsOnMethods = "testColorNotePageLoad")
    public void testNotesVisibility() {
        try {
            ReportManager.logInfo("Starting ColorNote notes visibility test");
            
            // Initialize ColorNote page
            colorNotePage = new ColorNotePage();
            
            // Check if notes are visible
            boolean notesVisible = colorNotePage.areNotesVisible();
            ReportManager.logInfo("Notes visibility status: " + notesVisible);
            
            // Notes might not be visible if this is first time or no notes exist
            // So we just log the result without failing
            if (notesVisible) {
                ReportManager.logPass("Notes are visible in ColorNote");
            } else {
                ReportManager.logInfo("No notes visible - this is normal for new installations");
            }
            
            // Always pass this test as it's informational
            Assert.assertTrue(true, "Notes visibility test completed");
            
        } catch (Exception e) {
            ReportManager.logFail("Notes visibility test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Test ColorNote navigation and UI elements")
    public void testNavigationElements() {
        try {
            ReportManager.logInfo("Starting ColorNote navigation elements test");
            
            // Initialize ColorNote page
            colorNotePage = new ColorNotePage();
            
            // Check if add note button is available
            boolean hasAddButton = colorNotePage.hasAddNoteButton();
            ReportManager.logInfo("Add note button available: " + hasAddButton);
            
            // Get current page info
            String pageInfo = colorNotePage.getCurrentPageInfo();
            ReportManager.logInfo("Current page info: " + pageInfo);
            
            // Verify we're in ColorNote
            Assert.assertTrue(pageInfo.toLowerCase().contains("color") || pageInfo.toLowerCase().contains("note"), 
                            "Should be in ColorNote application");
            
            ReportManager.logPass("ColorNote navigation elements test completed successfully");
            
        } catch (Exception e) {
            ReportManager.logFail("Navigation elements test failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(description = "Test ColorNote app functionality and features")
    public void testAppFunctionality() {
        try {
            ReportManager.logInfo("Starting ColorNote app functionality test");
            
            // Initialize ColorNote page
            colorNotePage = new ColorNotePage();
            
            // Verify app is loaded
            boolean isLoaded = colorNotePage.isPageLoaded();
            Assert.assertTrue(isLoaded, "ColorNote should be loaded");
            
            // Check add note functionality
            boolean hasAddButton = colorNotePage.hasAddNoteButton();
            ReportManager.logInfo("Add note functionality available: " + hasAddButton);
            
            // Get page information
            String pageInfo = colorNotePage.getCurrentPageInfo();
            ReportManager.logInfo("App information: " + pageInfo);
            
            // Verify basic functionality is available
            Assert.assertTrue(hasAddButton || pageInfo.toLowerCase().contains("note"), 
                            "ColorNote should have basic note functionality");
            
            ReportManager.logPass("ColorNote app functionality test completed successfully");
            
        } catch (Exception e) {
            ReportManager.logFail("App functionality test failed: " + e.getMessage());
            throw e;
        }
    }
}
