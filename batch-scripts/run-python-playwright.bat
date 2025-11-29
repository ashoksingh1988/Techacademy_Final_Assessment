@echo off
REM ============================================================================
REM Run Python Playwright Tests from Root Directory
REM ============================================================================

echo.
echo ========================================================================
echo        RUNNING PYTHON PLAYWRIGHT AUTOMATION FRAMEWORK
echo ========================================================================
echo.

cd python-playwright-automation

if "%1"=="" (
    call run_tests.bat
) else (
    call run_tests.bat %1
)

cd ..
