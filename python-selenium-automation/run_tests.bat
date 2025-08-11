@echo off
echo ========================================
echo SauceDemo Python Automation with pytest
echo ========================================

echo Installing dependencies...
pip install -r requirements.txt

echo.
echo Running pytest tests...
pytest tests/ -v --html=reports/report.html --self-contained-html

echo.
echo ========================================
echo Test execution completed!
echo Check reports/report.html for detailed results
echo ========================================
pause
