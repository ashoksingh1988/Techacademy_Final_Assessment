@echo off
setlocal enabledelayedexpansion

REM ==========================================
REM Selenium Web Automation Test Execution
REM ==========================================

echo.
echo ========================================
echo   SELENIUM WEB AUTOMATION FRAMEWORK
echo ========================================
echo.

REM Set default values
set SUITE=smoke
set BROWSER=chrome
set HEADLESS=false
set BASE_URL=https://www.saucedemo.com

REM Parse command line arguments
:parse_args
if "%~1"=="" goto :validate_args
if "%~1"=="--suite" (
    set SUITE=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--browser" (
    set BROWSER=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--headless" (
    set HEADLESS=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--url" (
    set BASE_URL=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--help" (
    goto :show_help
)
shift
goto :parse_args

:validate_args
echo Configuration:
echo   Suite: %SUITE%
echo   Browser: %BROWSER%
echo   Headless: %HEADLESS%
echo   Base URL: %BASE_URL%
echo.

REM Validate suite parameter
if "%SUITE%"=="smoke" (
    set SUITE_FILE=selenium-smoke-tests.xml
) else if "%SUITE%"=="regression" (
    set SUITE_FILE=selenium-regression-tests.xml
) else (
    echo ERROR: Invalid suite '%SUITE%'. Valid options: smoke, regression
    goto :error_exit
)

REM Check prerequisites
echo Checking prerequisites...

REM Check Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 11 or higher
    goto :error_exit
)
echo ✓ Java is available

REM Check Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Apache Maven
    goto :error_exit
)
echo ✓ Maven is available

echo.
echo Prerequisites check completed!
echo.

REM Build Maven command
set MAVEN_CMD=mvn clean test
set MAVEN_CMD=%MAVEN_CMD% -Dsurefire.suiteXmlFiles=src/test/resources/suites/%SUITE_FILE%
set MAVEN_CMD=%MAVEN_CMD% -Dbrowser=%BROWSER%
set MAVEN_CMD=%MAVEN_CMD% -Dheadless=%HEADLESS%
set MAVEN_CMD=%MAVEN_CMD% -Dbase.url=%BASE_URL%

REM Add timestamp for reports
for /f "tokens=2 delims==" %%a in ('wmic OS Get localdatetime /value') do set "dt=%%a"
set "YY=%dt:~2,2%" & set "YYYY=%dt:~0,4%" & set "MM=%dt:~4,2%" & set "DD=%dt:~6,2%"
set "HH=%dt:~8,2%" & set "Min=%dt:~10,2%" & set "Sec=%dt:~12,2%"
set "timestamp=%YYYY%-%MM%-%DD%_%HH%-%Min%-%Sec%"

set MAVEN_CMD=%MAVEN_CMD% -Dci.build.number=%timestamp%

echo Executing command:
echo %MAVEN_CMD%
echo.

REM Execute tests
echo ========================================
echo   STARTING TEST EXECUTION
echo ========================================
echo.

%MAVEN_CMD%

set TEST_EXIT_CODE=%errorlevel%

echo.
echo ========================================
echo   TEST EXECUTION COMPLETED
echo ========================================
echo.

REM Check test results
if %TEST_EXIT_CODE% equ 0 (
    echo ✓ Tests completed successfully!
) else (
    echo ✗ Tests completed with failures
)

echo.
echo Test Results:
echo   Exit Code: %TEST_EXIT_CODE%
echo   Reports: reports\
echo   Screenshots: reports\screenshots\
echo   Logs: target\surefire-reports\
echo.

REM Open reports if available
if exist "reports\*.html" (
    set /p OPEN_REPORT="Open test report? (y/N): "
    if /i "!OPEN_REPORT!"=="y" (
        for %%f in (reports\*.html) do (
            start "" "%%f"
            goto :report_opened
        )
        :report_opened
    )
)

goto :end

:show_help
echo.
echo Usage: run-tests.bat [OPTIONS]
echo.
echo Options:
echo   --suite SUITE        Test suite to run (smoke, regression)
echo   --browser BROWSER    Browser to use (chrome, firefox, edge, safari)
echo   --headless BOOLEAN   Run in headless mode (true/false)
echo   --url URL           Base URL for testing
echo   --help              Show this help message
echo.
echo Examples:
echo   run-tests.bat --suite smoke
echo   run-tests.bat --suite regression --browser firefox --headless true
echo   run-tests.bat --suite smoke --browser chrome --url https://www.saucedemo.com
echo.
goto :end

:error_exit
echo.
echo Execution aborted due to errors.
echo.
exit /b 1

:end
echo.
echo Script completed.
echo.
pause
exit /b %TEST_EXIT_CODE%
