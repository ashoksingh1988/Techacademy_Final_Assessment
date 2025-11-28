@echo off
REM ============================================================================
REM Python Playwright Automation - Test Execution Script
REM ============================================================================

echo.
echo ========================================================================
echo             PYTHON PLAYWRIGHT AUTOMATION FRAMEWORK
echo ========================================================================
echo.

REM Check if Python is installed
py --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Python is not installed or not in PATH
    echo Please install Python 3.8 or higher
    pause
    exit /b 1
)

echo [INFO] Python version:
py --version

REM Check if pytest.ini exists
if not exist "pytest.ini" (
    echo [ERROR] pytest.ini not found
    echo Please run this script from the python-playwright-automation directory
    pause
    exit /b 1
)

REM Install dependencies
echo.
echo [INFO] Installing dependencies...
py -m pip install -r requirements.txt --quiet
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to install dependencies
    pause
    exit /b 1
)

REM Install Playwright browsers
echo.
echo [INFO] Installing Playwright browsers...
py -m playwright install chromium
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to install Playwright browsers
    pause
    exit /b 1
)

REM Run tests based on argument
echo.
echo [INFO] Running tests...
echo ========================================================================

if "%1"=="smoke" (
    echo Running SMOKE tests only...
    py -m pytest tests/ -m smoke -v --html=reports/report.html --self-contained-html
) else if "%1"=="regression" (
    echo Running REGRESSION tests...
    py -m pytest tests/ -m regression -v --html=reports/report.html --self-contained-html
) else if "%1"=="parallel" (
    echo Running tests in PARALLEL mode...
    py -m pytest tests/ -n auto -v --html=reports/report.html --self-contained-html
) else (
    echo Running ALL tests...
    py -m pytest tests/ -v --html=reports/report.html --self-contained-html
)

echo.
echo ========================================================================
echo [INFO] Test execution completed!
echo [INFO] Report location: reports\report.html
echo ========================================================================
echo.

pause
