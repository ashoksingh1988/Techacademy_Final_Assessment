@echo off
REM ==========================================
REM Jenkins Appium Server Starter
REM ==========================================

echo Starting Appium server for Jenkins...

REM Check if Appium is already running
timeout 2 curl -s http://127.0.0.1:4723/status >nul 2>&1
if %errorlevel% equ 0 (
    echo Appium server is already running
    exit /b 0
)

REM Start Appium server in background
echo Starting new Appium server instance...
start /B appium --allow-insecure chromedriver_autodownload --log appium-jenkins.log

REM Wait for server to start (max 10 seconds)
echo Waiting for Appium to be ready...
set MAX_ATTEMPTS=10
set ATTEMPT=0

:check_loop
set /a ATTEMPT+=1
if %ATTEMPT% gtr %MAX_ATTEMPTS% (
    echo ERROR: Appium failed to start after %MAX_ATTEMPTS% attempts
    exit /b 1
)

timeout 1 >nul 2>&1
curl -s http://127.0.0.1:4723/status >nul 2>&1
if %errorlevel% neq 0 (
    echo Attempt %ATTEMPT%/%MAX_ATTEMPTS% - waiting...
    goto check_loop
)

echo Appium server started successfully!
curl -s http://127.0.0.1:4723/status
exit /b 0
