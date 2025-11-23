@echo off
REM ==========================================
REM Jenkins Appium Server Stopper
REM ==========================================

echo Stopping Appium server...

REM Find and kill Appium processes
taskkill /F /IM node.exe /FI "WINDOWTITLE eq Appium*" >nul 2>&1
taskkill /F /IM appium.exe >nul 2>&1

REM Wait a moment
timeout 2 >nul 2>&1

REM Verify it's stopped
timeout 2 curl -s http://127.0.0.1:4723/status >nul 2>&1
if %errorlevel% equ 0 (
    echo WARNING: Appium may still be running
    exit /b 1
) else (
    echo Appium server stopped successfully
    exit /b 0
)
