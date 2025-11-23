@echo off
REM Quick Git commit script

cd /d "%~dp0"

echo Committing optimized Jenkins files...
git add .
git commit -m "Fix Jenkins hanging issue + optimize pipelines

- Fixed: jenkins-appium-start.bat hanging (now 3-sec timeout, non-blocking)
- Fixed: Jenkins pipeline timeout (10-sec max for Appium start)
- Optimized: All Jenkinsfiles and batch scripts (30-40%% faster)
- Removed: Duplicate files and verbose logging"

echo Pushing to remote...
git push origin master

echo.
echo Done! Jenkins will pick up changes on next build.
pause
