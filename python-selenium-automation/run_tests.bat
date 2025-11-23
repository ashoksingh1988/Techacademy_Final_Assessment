@echo off
echo ==========================================
echo   SAUCEDEMO AUTOMATION FRAMEWORK
echo   Python Selenium Test Execution
echo ==========================================
echo.

echo 1. Checking Python installation...
py --version
if %errorlevel% neq 0 (
    echo ❌ Python not found! Install Python 3.13+
    pause
    exit /b 1
)

echo.
echo 2. Checking current directory...
echo Current directory: %cd%
dir /b *.py >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Not in Python project directory!
    echo Navigate to: python-selenium-automation
    pause
    exit /b 1
)

echo ✅ In correct directory
echo.

echo 3. Checking required files...
if exist conftest.py (echo ✅ conftest.py found) else (echo ❌ conftest.py missing)
if exist "tests\test_saucedemo_comprehensive.py" (echo ✅ test file found) else (echo ❌ test file missing)
if exist requirements.txt (echo ✅ requirements.txt found) else (echo ❌ requirements.txt missing)
if exist pytest.ini (echo ✅ pytest.ini found) else (echo ❌ pytest.ini missing)

echo.
echo 4. Installing/updating dependencies...
echo Installing dependencies...
py -m pip install -r requirements.txt --quiet

echo ✅ Dependencies ready
echo.
echo ==========================================
echo   🚀 STARTING TEST EXECUTION
echo ==========================================
echo Expected execution time: 1-2 minutes
echo.

echo Running pytest tests with HTML reporting...
py -m pytest tests/ -v --html=reports/report.html --self-contained-html

echo.
echo ==========================================
echo   ✅ TEST EXECUTION COMPLETED!
echo ==========================================
echo.
echo 📊 Detailed results available at: reports/report.html
echo 🎯 Framework: Python + Selenium + pytest
echo 🌐 Target: SauceDemo Web Application
echo.
echo Thank you for using SauceDemo Automation Framework!
echo ==========================================
pause
