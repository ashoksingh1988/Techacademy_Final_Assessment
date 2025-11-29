@echo off
REM Demo commit script - Safely triggers Jenkins auto-build for demonstration

cd /d "%~dp0.."

echo ========================================
echo DEMO: AUTO-TRIGGER JENKINS BUILD
echo ========================================
echo.

REM Update timestamp in demo-trigger.txt automatically
powershell -Command "(Get-Content batch-scripts\demo-trigger.txt) -replace 'Last Demo Trigger:.*', 'Last Demo Trigger: %date% %time%' | Set-Content batch-scripts\demo-trigger.txt"

REM Increment demo count
powershell -Command "$content = Get-Content batch-scripts\demo-trigger.txt; $content -replace 'Demo Count: (\d+)', { 'Demo Count: ' + ([int]$matches[1] + 1) } | Set-Content batch-scripts\demo-trigger.txt"

echo Modified demo-trigger.txt with new timestamp...
echo.

echo Adding changes to Git...
git add batch-scripts/demo-trigger.txt

echo Committing demo change...
git commit -m "Demo: Trigger Jenkins auto-build - timestamp update"

echo Pushing to GitHub...
git push origin master

echo.
echo ========================================
echo DEMO COMMIT SUCCESSFUL!
echo ========================================
echo.
echo What happens next:
echo 1. GitHub receives the commit
echo 2. Jenkins polls within 5 minutes
echo 3. Detects change in demo-trigger.txt
echo 4. Auto-triggers techacademy-master-pipeline
echo 5. All 3 frameworks execute automatically
echo.
echo SAFE: Only demo-trigger.txt was modified
echo Your actual test code is untouched!
echo ========================================
pause
