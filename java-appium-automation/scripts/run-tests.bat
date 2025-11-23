@echo off
REM Appium Test Execution - Optimized for Jenkins

cd /d "%~dp0.."

REM Parse arguments (defaults: suite=smoke, device=Android_Device, platform=11)
set SUITE=smoke
set DEVICE=Android_Device
set PLATFORM=11

:parse_args
if "%~1"=="" goto :run_tests
if "%~1"=="--suite" set SUITE=%~2 & shift & shift & goto :parse_args
if "%~1"=="--device" set DEVICE=%~2 & shift & shift & goto :parse_args
if "%~1"=="--platform" set PLATFORM=%~2 & shift & shift & goto :parse_args
if "%~1"=="--help" goto :show_help
shift
goto :parse_args

:run_tests
REM Map suite name to XML file
if "%SUITE%"=="smoke" set SUITE_FILE=smoke-tests.xml
if "%SUITE%"=="regression" set SUITE_FILE=regression-tests.xml
if "%SUITE%"=="parallel" set SUITE_FILE=parallel-tests.xml
if "%SUITE%"=="diagnostic" set SUITE_FILE=diagnostic-tests.xml

if not defined SUITE_FILE (
    echo ERROR: Invalid suite '%SUITE%'. Use: smoke, regression, parallel, diagnostic
    exit /b 1
)

echo Running %SUITE% tests...
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/%SUITE_FILE% -Ddevice.name=%DEVICE% -Dplatform.version=%PLATFORM%
exit /b %errorlevel%

:show_help
echo Usage: run-tests.bat [--suite smoke^|regression^|parallel^|diagnostic] [--device NAME] [--platform VERSION]
echo Examples:
echo   run-tests.bat
echo   run-tests.bat --suite regression
echo   run-tests.bat --suite smoke --device pixel_7_pro --platform 13
exit /b 0
