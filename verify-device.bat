@echo off
REM Device Verification Helper - Run before Jenkins build

echo ========================================
echo Android Device Verification
echo ========================================
echo.

echo [1/5] Checking ADB installation...
adb version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: ADB not found in PATH
    echo Please install Android SDK Platform Tools
    pause
    exit /b 1
)
echo OK - ADB is installed
echo.

echo [2/5] Listing connected devices...
adb devices
echo.

echo [3/5] Checking for target device PZPVSC95GMKNGUBQ...
adb devices | findstr "PZPVSC95GMKNGUBQ" >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Device PZPVSC95GMKNGUBQ not found!
    echo.
    echo Troubleshooting steps:
    echo 1. Connect device via USB cable
    echo 2. Enable USB Debugging: Settings ^> Developer Options ^> USB Debugging
    echo 3. Accept USB debugging authorization popup on device
    echo 4. Run: adb devices
    echo.
    pause
    exit /b 1
)
echo OK - Device PZPVSC95GMKNGUBQ is connected
echo.

echo [4/5] Getting device details...
adb -s PZPVSC95GMKNGUBQ shell getprop ro.build.version.release 2>nul
adb -s PZPVSC95GMKNGUBQ shell getprop ro.product.model 2>nul
echo.

echo [5/5] Testing Appium server connection...
curl -s --max-time 2 http://127.0.0.1:4723/status >nul 2>&1
if %errorlevel% equ 0 (
    echo OK - Appium server is running
) else (
    echo WARNING: Appium server not running
    echo Run: appium
)
echo.

echo ========================================
echo Verification Complete!
echo ========================================
echo.
echo Your device is ready for Jenkins pipeline
echo You can now trigger the build with confidence
echo.
pause
