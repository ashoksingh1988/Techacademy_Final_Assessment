@echo off
echo ========================================
echo Running Java Appium Tests
echo ========================================
echo.
echo Available test suites:
echo   1. smoke (default)
echo   2. regression
echo   3. parallel
echo   4. diagnostic
echo.

set SUITE=smoke
if not "%1"=="" set SUITE=%1

echo Running %SUITE%-tests.xml...
echo.

cd java-appium-automation
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/%SUITE%-tests.xml

echo.
echo ========================================
echo Test execution completed!
echo ========================================
pause
