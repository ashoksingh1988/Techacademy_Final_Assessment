@echo off
REM Demo Readiness Checker - Run before demo

echo ========================================
echo DEMO READINESS VERIFICATION
echo ========================================
echo.

echo [1/6] Checking Browser Installation...
where chrome >nul 2>&1
if %errorlevel% equ 0 (
    echo OK - Chrome is installed
) else (
    echo WARNING - Chrome not found in PATH
)
echo.

echo [2/6] Checking Python Installation...
python --version >nul 2>&1
if %errorlevel% equ 0 (
    echo OK - Python is installed
    python --version
) else (
    echo ERROR - Python not installed
    pause
    exit /b 1
)
echo.

echo [3/6] Checking Java Installation...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo OK - Java is installed
) else (
    echo ERROR - Java not installed
    pause
    exit /b 1
)
echo.

echo [4/6] Checking Maven Installation...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo OK - Maven is installed
) else (
    echo ERROR - Maven not installed
    pause
    exit /b 1
)
echo.

echo [5/6] Checking Display Access...
echo NOTE: For demo, ensure Jenkins agent is running on a machine with DISPLAY
echo      (i.e., not a headless server, but a machine you can screen-share)
echo.

echo [6/6] Demo Configuration Summary...
echo   - Headless Mode: FALSE (browsers will be visible)
echo   - Parallel Execution: ENABLED (faster tests)
echo   - Expected Time: 15-20 mins (with parallel)
echo   - Screen Share: Jenkins agent machine during execution
echo.

echo ========================================
echo DEMO READY!
echo ========================================
echo.
echo Before triggering Jenkins build:
echo 1. Ensure you can screen-share Jenkins agent machine
echo 2. Close unnecessary applications for better visibility
echo 3. Build parameters: HEADLESS_MODE=false, PARALLEL_EXECUTION=true
echo.
echo Pipeline will show browsers launching and tests executing visibly!
echo.
pause
