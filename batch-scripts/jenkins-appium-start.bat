@echo off
setlocal enabledelayedexpansion
REM Jenkins Appium & Device Setup - Robust Auto-Start

REM Execute PowerShell script to start Appium service
echo Starting Appium service via PowerShell...
powershell.exe -ExecutionPolicy Bypass -File "%~dp0start-appium-service.ps1"
if %errorlevel% neq 0 (
    echo ERROR: Failed to start Appium service
    exit /b 1
)

echo Appium service is running

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
