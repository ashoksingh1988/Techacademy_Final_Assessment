@echo off
setlocal enabledelayedexpansion

REM ==========================================
REM Appium Environment Setup Verification
REM ==========================================

echo.
echo ========================================
echo   APPIUM ENVIRONMENT SETUP CHECKER
echo ========================================
echo.

echo Verifying environment setup for Appium Android automation...
echo.

set ERROR_COUNT=0
set WARNING_COUNT=0

REM Check Java
echo [1/9] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed or not in PATH
    echo    Please install Java JDK 11 or higher
    echo    Download from: https://adoptium.net/
    set /a ERROR_COUNT+=1
) else (
    echo ✅ Java is installed
    for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        echo    Version: %%g
    )
)
echo.

REM Check Maven
echo [2/9] Checking Maven installation...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed or not in PATH
    echo    Please install Apache Maven 3.6+
    echo    Download from: https://maven.apache.org/download.cgi
    set /a ERROR_COUNT+=1
) else (
    echo ✅ Maven is installed
    for /f "tokens=3" %%g in ('mvn -version 2^>^&1 ^| findstr "Apache Maven"') do (
        echo    Version: %%g
    )
)
echo.

REM Check Android SDK
echo [3/9] Checking Android SDK...
if "%ANDROID_HOME%"=="" (
    echo ❌ ANDROID_HOME environment variable is not set
    echo    Please install Android Studio and set ANDROID_HOME
    echo    Example: set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
    set /a ERROR_COUNT+=1
) else (
    echo ✅ ANDROID_HOME is set
    echo    Path: %ANDROID_HOME%
    if exist "%ANDROID_HOME%\platform-tools\adb.exe" (
        echo ✅ Android SDK Platform Tools found
    ) else (
        echo ⚠️  Android SDK Platform Tools not found in expected location
        set /a WARNING_COUNT+=1
    )
)
echo.

REM Check ADB
echo [4/9] Checking ADB (Android Debug Bridge)...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ADB is not installed or not in PATH
    echo    Please install Android SDK Platform Tools
    echo    Add to PATH: %ANDROID_HOME%\platform-tools
    set /a ERROR_COUNT+=1
) else (
    echo ✅ ADB is installed
    for /f "tokens=*" %%g in ('adb version 2^>^&1 ^| findstr "Android Debug Bridge"') do (
        echo    %%g
    )
)
echo.

REM Check Node.js
echo [5/9] Checking Node.js installation...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js is not installed or not in PATH
    echo    Please install Node.js 16+ (required for Appium)
    echo    Download from: https://nodejs.org/
    set /a ERROR_COUNT+=1
) else (
    echo ✅ Node.js is installed
    for /f "tokens=*" %%g in ('node --version 2^>^&1') do (
        echo    Version: %%g
    )
)
echo.

REM Check NPM
echo [6/9] Checking NPM (Node Package Manager)...
npm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ NPM is not installed or not in PATH
    echo    NPM should be included with Node.js installation
    set /a ERROR_COUNT+=1
) else (
    echo ✅ NPM is installed
    for /f "tokens=*" %%g in ('npm --version 2^>^&1') do (
        echo    Version: %%g
    )
)
echo.

REM Check Appium
echo [7/9] Checking Appium installation...
appium --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Appium is not installed
    echo    Please install Appium: npm install -g appium
    echo    Recommended version: 2.0+
    set /a ERROR_COUNT+=1
) else (
    echo ✅ Appium is installed
    for /f "tokens=*" %%g in ('appium --version 2^>^&1') do (
        echo    Version: %%g
    )
)
echo.

REM Check Appium UiAutomator2 driver
echo [8/9] Checking Appium UiAutomator2 driver...
appium driver list 2>nul | findstr "uiautomator2" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ UiAutomator2 driver is not installed
    echo    Please install: appium driver install uiautomator2
    set /a ERROR_COUNT+=1
) else (
    echo ✅ UiAutomator2 driver is installed
    for /f "tokens=*" %%g in ('appium driver list 2^>nul ^| findstr "uiautomator2"') do (
        echo    %%g
    )
)
echo.

REM Check Android devices
echo [9/9] Checking Android device connection...
adb devices 2>nul | findstr "device" | findstr -v "List" >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  No Android devices detected
    echo    Please connect an Android device or start an emulator
    echo    Commands:
    echo      - Check devices: adb devices
    echo      - Start emulator: emulator -avd YOUR_AVD_NAME
    set /a WARNING_COUNT+=1
) else (
    echo ✅ Android device(s) detected
    echo    Connected devices:
    for /f "skip=1 tokens=1,2" %%a in ('adb devices 2^>nul') do (
        if not "%%a"=="" if not "%%b"=="" (
            echo      - %%a (%%b)
        )
    )
)
echo.

REM Summary
echo ========================================
echo           SETUP SUMMARY
echo ========================================
echo.

if %ERROR_COUNT% gtr 0 (
    echo ❌ Setup Issues Found: %ERROR_COUNT% error(s), %WARNING_COUNT% warning(s)
    echo.
    echo Required Actions:
    if %ERROR_COUNT% gtr 0 (
        echo   1. Install missing components listed above
        echo   2. Configure environment variables
        echo   3. Add tools to system PATH
    )
    echo.
    echo Installation Guide:
    echo   1. Java JDK 11+: https://adoptium.net/
    echo   2. Maven 3.6+: https://maven.apache.org/download.cgi
    echo   3. Android Studio: https://developer.android.com/studio
    echo   4. Set ANDROID_HOME environment variable
    echo   5. Add Android SDK tools to PATH
    echo   6. Node.js 16+: https://nodejs.org/
    echo   7. Appium: npm install -g appium
    echo   8. UiAutomator2 driver: appium driver install uiautomator2
    echo.
) else (
    echo ✅ Environment Setup Complete!
    echo.
    echo All required components are installed and configured.
    echo.
    echo Next Steps:
    echo   1. Connect an Android device or start an emulator
    echo   2. Start Appium server: appium
    echo   3. Run tests: scripts\run-tests.bat --suite smoke
    echo.
    echo Quick Start Commands:
    echo   - Check devices: adb devices
    echo   - Start Appium: appium
    echo   - Run smoke tests: scripts\run-tests.bat --suite smoke
    echo   - Run all tests: scripts\run-tests.bat --suite regression
    echo.
)

if %WARNING_COUNT% gtr 0 (
    echo ⚠️  Warnings: %WARNING_COUNT%
    echo   Some optional components may need attention
    echo.
)

REM Additional Information
echo ========================================
echo        ADDITIONAL INFORMATION
echo ========================================
echo.
echo Framework Information:
echo   - Framework: Appium Android Automation
echo   - Language: Java 11+
echo   - Build Tool: Maven
echo   - Test Framework: TestNG
echo   - Reporting: ExtentReports
echo.
echo Useful Commands:
echo   - Environment check: scripts\setup-environment.bat
echo   - Run tests: scripts\run-tests.bat --help
echo   - View devices: adb devices
echo   - Start Appium: appium
echo   - Check Appium: curl http://127.0.0.1:4723/status
echo.

echo ========================================
echo.
pause
exit /b %ERROR_COUNT%
