@echo off
setlocal enabledelayedexpansion

REM ==========================================
REM Appium Android Framework Test Execution
REM ==========================================

echo.
echo ========================================
echo   APPIUM ANDROID AUTOMATION FRAMEWORK
echo ========================================
echo.

REM Set default values
set SUITE=smoke
set DEVICE=Android_Device
set PLATFORM=13
set PARALLEL=false
set THREAD_COUNT=1

REM Parse command line arguments
:parse_args
if "%~1"=="" goto :validate_args
if "%~1"=="--suite" (
    set SUITE=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--device" (
    set DEVICE=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--platform" (
    set PLATFORM=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--parallel" (
    set PARALLEL=%~2
    shift
    shift
    goto :parse_args
)
if "%~1"=="--threads" (
    set THREAD_COUNT=%~2
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
echo   Device: %DEVICE%
echo   Platform: %PLATFORM%
echo   Parallel: %PARALLEL%
echo   Threads: %THREAD_COUNT%
echo.

REM Validate suite parameter
if "%SUITE%"=="smoke" (
    set SUITE_FILE=smoke-tests.xml
) else if "%SUITE%"=="regression" (
    set SUITE_FILE=regression-tests.xml
) else if "%SUITE%"=="parallel" (
    set SUITE_FILE=parallel-tests.xml
) else (
    echo ERROR: Invalid suite '%SUITE%'. Valid options: smoke, regression, parallel
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

REM Check Appium server
echo Checking Appium server...
curl -s http://127.0.0.1:4723/status >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Appium server is not running on http://127.0.0.1:4723
    echo Please start Appium server before running tests
    echo.
    echo To start Appium server, run: appium
    echo.
    set /p CONTINUE="Continue anyway? (y/N): "
    if /i not "!CONTINUE!"=="y" goto :error_exit
) else (
    echo ✓ Appium server is running
)

REM Check Android device
echo Checking Android device connection...
adb devices | findstr "device" >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: No Android device detected
    echo Please connect an Android device or start an emulator
    echo.
    echo To check devices, run: adb devices
    echo.
    set /p CONTINUE="Continue anyway? (y/N): "
    if /i not "!CONTINUE!"=="y" goto :error_exit
) else (
    echo ✓ Android device detected
)

echo.
echo Prerequisites check completed!
echo.

REM Build Maven command
set MAVEN_CMD=mvn clean test
set MAVEN_CMD=%MAVEN_CMD% -Dsurefire.suiteXmlFiles=src/test/resources/suites/%SUITE_FILE%
set MAVEN_CMD=%MAVEN_CMD% -Ddevice.name=%DEVICE%
set MAVEN_CMD=%MAVEN_CMD% -Dplatform.version=%PLATFORM%
set MAVEN_CMD=%MAVEN_CMD% -Dexecution.parallel.enabled=%PARALLEL%
set MAVEN_CMD=%MAVEN_CMD% -Dexecution.parallel.thread.count=%THREAD_COUNT%

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
echo   --suite SUITE        Test suite to run (smoke, regression, parallel)
echo   --device DEVICE      Device name or configuration
echo   --platform VERSION   Android platform version
echo   --parallel BOOLEAN   Enable parallel execution (true/false)
echo   --threads COUNT      Number of parallel threads
echo   --help              Show this help message
echo.
echo Examples:
echo   run-tests.bat --suite smoke
echo   run-tests.bat --suite regression --device pixel_7_pro --platform 13
echo   run-tests.bat --suite parallel --parallel true --threads 3
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
