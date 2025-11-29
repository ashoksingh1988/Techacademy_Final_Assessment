@echo off
REM Fast Appium stop for Jenkins

REM Kill Appium processes silently
taskkill /F /IM node.exe /FI "WINDOWTITLE eq AppiumServer*" >nul 2>&1
taskkill /F /IM appium.exe >nul 2>&1

echo Appium stopped
exit /b 0
