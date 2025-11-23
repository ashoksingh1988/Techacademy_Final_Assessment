@echo off
REM Selenium Test Execution - Optimized for Jenkins

cd /d "%~dp0.."

REM Parse arguments (defaults: suite=smoke, browser=chrome, headless=false)
set SUITE=smoke
set BROWSER=chrome
set HEADLESS=false
set BASE_URL=https://www.saucedemo.com

:parse_args
if "%~1"=="" goto :run_tests
if "%~1"=="--suite" set SUITE=%~2 & shift & shift & goto :parse_args
if "%~1"=="--browser" set BROWSER=%~2 & shift & shift & goto :parse_args
if "%~1"=="--headless" set HEADLESS=%~2 & shift & shift & goto :parse_args
if "%~1"=="--url" set BASE_URL=%~2 & shift & shift & goto :parse_args
if "%~1"=="--help" goto :show_help
shift
goto :parse_args

:run_tests
REM Map suite name to XML file
if "%SUITE%"=="smoke" set SUITE_FILE=selenium-smoke-tests.xml
if "%SUITE%"=="regression" set SUITE_FILE=selenium-regression-tests.xml

if not defined SUITE_FILE (
    echo ERROR: Invalid suite '%SUITE%'. Use: smoke, regression
    exit /b 1
)

echo Running %SUITE% tests on %BROWSER%...
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/%SUITE_FILE% -Dbrowser=%BROWSER% -Dheadless=%HEADLESS% -Dbase.url=%BASE_URL%
exit /b %errorlevel%

:show_help
echo Usage: run-tests.bat [--suite smoke^|regression] [--browser chrome^|firefox^|edge] [--headless true^|false] [--url URL]
echo Examples:
echo   run-tests.bat
echo   run-tests.bat --suite regression --browser firefox
echo   run-tests.bat --suite smoke --headless true
exit /b 0
