# 🚀 Execution Guide

## Quick Start

### 1. Environment Check
```bash
# Verify setup
scripts\setup-environment.bat
```

### 2. Start Appium
```bash
# Start Appium server
appium
```

### 3. Run Tests
```bash
# Smoke tests
scripts\run-tests.bat --suite smoke

# All tests
scripts\run-tests.bat --suite regression
```

## Test Execution Options

### Test Suites

#### Smoke Tests
```bash
# Quick validation tests
scripts\run-tests.bat --suite smoke
```

#### Regression Tests
```bash
# Complete test suite
scripts\run-tests.bat --suite regression
```

#### Parallel Tests
```bash
# Multi-device execution
scripts\run-tests.bat --suite parallel --parallel true --threads 3
```

### Command Line Options

#### Basic Execution
```bash
# Default smoke tests
scripts\run-tests.bat

# Specific suite
scripts\run-tests.bat --suite regression

# Custom device
scripts\run-tests.bat --device pixel_7_pro --platform 13
```

#### Advanced Options
```bash
# Parallel execution
scripts\run-tests.bat --suite parallel --parallel true --threads 2

# Custom configuration
scripts\run-tests.bat --suite smoke --device samsung_galaxy_s23 --platform 13
```

### Maven Execution

#### Direct Maven Commands
```bash
# Smoke tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml

# Regression tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/regression-tests.xml

# Parallel tests
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/parallel-tests.xml
```

#### With Parameters
```bash
# Custom device and platform
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml -Ddevice.name=pixel_7_pro -Dplatform.version=13

# Enable parallel execution
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/parallel-tests.xml -Dexecution.parallel.enabled=true -Dexecution.parallel.thread.count=3
```

## Test Configuration

### Device Selection

#### Predefined Devices
```bash
# Use predefined device configurations
--device pixel_7_pro
--device samsung_galaxy_s23
--device android_emulator_api_33
```

#### Custom Device
```bash
# Override device parameters
-Ddevice.name="Custom Device"
-Dplatform.version=12
-Dautomation.name=UiAutomator2
```

### Application Configuration

#### System Apps
```bash
# Settings app (default)
-Dapp.package=com.android.settings
-Dapp.activity=.Settings

# Calculator app
-Dapp.package=com.google.android.calculator
-Dapp.activity=com.android.calculator2.Calculator
```

#### Custom Apps
```bash
# Custom APK
-Dapp.path=/path/to/your/app.apk
```

## Parallel Execution

### Multi-Device Setup
1. Connect multiple devices
2. Configure device capabilities
3. Run parallel suite

```bash
# Check connected devices
adb devices

# Run parallel tests
scripts\run-tests.bat --suite parallel --parallel true --threads 3
```

### Device Configuration
Edit `src/test/resources/config/devices.json`:

```json
{
  "devices": {
    "device1": {
      "deviceName": "Device 1",
      "udid": "device1_udid",
      "systemPort": 8200
    },
    "device2": {
      "deviceName": "Device 2", 
      "udid": "device2_udid",
      "systemPort": 8201
    }
  }
}
```

## Reporting

### Report Generation
Reports are automatically generated in the `reports/` directory:

- **HTML Report**: `reports/Appium_Android_Automation_Report_[timestamp].html`
- **Screenshots**: `reports/screenshots/`
- **TestNG Reports**: `target/surefire-reports/`

### Viewing Reports
```bash
# Reports are automatically opened after execution
# Or manually open:
start reports\Appium_Android_Automation_Report_[timestamp].html
```

### Report Configuration
Edit `src/test/resources/application.properties`:

```properties
# Reporting settings
reporting.output.directory=reports
reporting.screenshot.on.failure=true
reporting.screenshot.on.success=false
reporting.detailed.logs=true
```

## IDE Execution

### IntelliJ IDEA
1. Right-click on test class/method
2. Select "Run" or "Debug"
3. Configure run parameters if needed

### Eclipse
1. Right-click on test class
2. Run As → TestNG Test
3. Configure TestNG run configuration

## CI/CD Integration

### Jenkins
```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                bat 'scripts\\run-tests.bat --suite regression'
            }
        }
    }
    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'reports',
                reportFiles: '*.html',
                reportName: 'Appium Test Report'
            ])
        }
    }
}
```

### GitHub Actions
```yaml
name: Appium Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
      - name: Run tests
        run: ./scripts/run-tests.bat --suite smoke
```

## Debugging

### Debug Mode
```bash
# Enable debug logging
-Dlogging.level=DEBUG

# Verbose TestNG output
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml -Dverbose=true
```

### IDE Debugging
1. Set breakpoints in test code
2. Run in debug mode
3. Step through execution
4. Inspect variables and driver state

### Appium Inspector
1. Start Appium server
2. Open Appium Inspector
3. Connect to session
4. Inspect elements

## Performance Monitoring

### Execution Time
```bash
# Monitor test execution time
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml -Dtime=true
```

### Resource Usage
- Monitor CPU and memory usage during execution
- Check device performance impact
- Optimize wait times and timeouts

## Best Practices

### Before Execution
1. Verify environment setup
2. Check device connectivity
3. Start Appium server
4. Validate test data

### During Execution
1. Monitor test progress
2. Check for failures
3. Review logs for issues
4. Capture screenshots on failures

### After Execution
1. Review test reports
2. Analyze failures
3. Update test data if needed
4. Clean up resources

## Troubleshooting

### Common Issues
- Device not detected: Check ADB connection
- Appium server not running: Start Appium
- Test failures: Check application state
- Timeout issues: Adjust wait times

### Log Analysis
```bash
# Check Appium logs
# Check TestNG reports in target/surefire-reports/
# Review ExtentReports for detailed information
```
