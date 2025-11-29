# 🔧 Batch Scripts Index

This folder contains all batch scripts and automation utilities for the TechAcademy Multi-Framework Test Automation Suite.

## 📜 Script Files:

### Execution Scripts:
- **run-java-appium.bat** - Execute Java Appium mobile tests
- **run-python-selenium.bat** - Execute Python Selenium tests
- **run-python-playwright.bat** - Execute Python Playwright tests
- **run-testng.bat** - Execute TestNG tests

### Jenkins Scripts:
- **jenkins-appium-start.bat** - Start Appium server for Jenkins
- **jenkins-appium-stop.bat** - Stop Appium server

### Git & Demo Scripts:
- **git-commit.bat** - Commit and push changes to GitHub
- **demo-commit.bat** - Demo script to trigger Jenkins auto-build
- **demo-trigger.txt** - Demo file for safe Jenkins triggering

## Usage:

All scripts should be executed from the **root directory** of the project:

```bash
# From root directory
batch-scripts\run-java-appium.bat
batch-scripts\git-commit.bat
batch-scripts\demo-commit.bat
```

These scripts are used by:
- Local development execution
- Jenkins CI/CD pipelines
- Demo presentations
