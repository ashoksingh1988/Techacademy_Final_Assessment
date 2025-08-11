@echo off
echo ========================================
echo   APPIUM SETUP SCRIPT
echo ========================================
echo.

echo [1/5] Checking Node.js installation...
node --version
if %errorlevel% neq 0 (
    echo ERROR: Node.js is not installed. Please install Node.js first.
    pause
    exit /b 1
)
echo ✅ Node.js is available

echo.
echo [2/5] Installing Appium globally...
npm install -g appium
if %errorlevel% neq 0 (
    echo ERROR: Failed to install Appium. Try running as Administrator.
    pause
    exit /b 1
)
echo ✅ Appium installed successfully

echo.
echo [3/5] Installing UiAutomator2 driver...
appium driver install uiautomator2
if %errorlevel% neq 0 (
    echo WARNING: Failed to install UiAutomator2 driver. You can install it manually later.
)
echo ✅ UiAutomator2 driver installation attempted

echo.
echo [4/5] Checking device connection...
adb devices
echo.
echo ⚠️  Make sure your Android device is connected and USB debugging is enabled

echo.
echo [5/5] Setup complete!
echo.
echo ========================================
echo   NEXT STEPS:
echo ========================================
echo 1. Start Appium server: appium
echo 2. Run tests: mvn clean test -Dsuite=smoke
echo.
echo Press any key to start Appium server now...
pause > nul

echo.
echo Starting Appium server...
appium
