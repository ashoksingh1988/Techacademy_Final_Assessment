@echo off
setlocal enabledelayedexpansion
REM Jenkins Appium & Device Setup - Non-blocking

REM Set Node.js path for Jenkins compatibility
set PATH=C:\Program Files\nodejs;%PATH%

REM Check if Appium already running
curl -s --max-time 1 http://127.0.0.1:4723/status >nul 2>&1
if %errorlevel% equ 0 (
    echo Appium already running
    goto :verify_device
)

REM Start Appium in background with full path to Node.exe and Appium main script
echo Starting Appium server...
start "AppiumServer" /MIN "C:\Program Files\nodejs\node.exe" "C:\Users\Asim\AppData\Roaming\npm\node_modules\appium\build\lib\main.js" --allow-insecure chromedriver_autodownload

REM Wait for Appium to be ready (max 10 seconds)
for /L %%i in (1,1,10) do (
    timeout /t 1 /nobreak >nul 2>&1
    curl -s --max-time 1 http://127.0.0.1:4723/status >nul 2>&1
    if !errorlevel! equ 0 (
        echo Appium server ready
        goto :verify_device
    )
)

echo WARNING: Appium may not be ready yet

:verify_device
REM Quick device check (non-blocking)
echo Checking Android device...
adb devices 2>nul | findstr "device" | findstr -v "List" >nul 2>&1
if %errorlevel% equ 0 (
    echo Device detected
    adb devices
) else (
    echo WARNING: No Android device detected
    echo Please ensure device is connected and USB debugging is enabled
)

exit /b 0
