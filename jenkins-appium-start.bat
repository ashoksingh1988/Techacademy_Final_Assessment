@echo off
REM Fast Appium start for Jenkins - non-blocking

REM Check if already running (quick check, 1 sec timeout)
curl -s --max-time 1 http://127.0.0.1:4723/status >nul 2>&1
if %errorlevel% equ 0 (
    echo Appium already running
    exit /b 0
)

REM Start Appium in background (detached process)
echo Starting Appium...
start "AppiumServer" /MIN appium --allow-insecure chromedriver_autodownload >nul 2>&1

REM Quick verification (3 attempts, 1 sec each)
for /L %%i in (1,1,3) do (
    timeout /t 1 /nobreak >nul 2>&1
    curl -s --max-time 1 http://127.0.0.1:4723/status >nul 2>&1
    if !errorlevel! equ 0 (
        echo Appium ready
        exit /b 0
    )
)

REM If not ready after 3 seconds, continue anyway (tests will handle it)
echo Appium starting in background...
exit /b 0
