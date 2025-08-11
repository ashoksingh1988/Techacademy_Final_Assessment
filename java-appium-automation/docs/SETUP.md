# 🛠️ Setup Guide

## Prerequisites

### Required Software
- **Java JDK 11+** - Runtime environment
- **Maven 3.6+** - Build and dependency management
- **Android Studio** - Android SDK and development tools
- **Node.js 16+** - Required for Appium
- **Appium 2.0+** - Mobile automation server

### Environment Variables
```bash
JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
PATH=%PATH%;%JAVA_HOME%\bin;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools
```

## Installation Steps

### 1. Java Installation
```bash
# Download and install Java JDK 11+
# Verify installation
java -version
javac -version
```

### 2. Maven Installation
```bash
# Download Maven from https://maven.apache.org/download.cgi
# Extract and add to PATH
# Verify installation
mvn -version
```

### 3. Android Studio Setup
```bash
# Download from https://developer.android.com/studio
# Install Android Studio
# Install Android SDK
# Set ANDROID_HOME environment variable
# Verify ADB
adb version
```

### 4. Node.js and Appium
```bash
# Install Node.js from https://nodejs.org/
node --version
npm --version

# Install Appium globally
npm install -g appium

# Install UiAutomator2 driver
appium driver install uiautomator2

# Verify installation
appium --version
appium driver list
```

## Device Setup

### Physical Device
1. Enable Developer Options
2. Enable USB Debugging
3. Connect via USB
4. Verify connection: `adb devices`

### Android Emulator
1. Open Android Studio
2. Go to AVD Manager
3. Create Virtual Device
4. Start emulator
5. Verify connection: `adb devices`

## Framework Setup

### 1. Environment Verification
```bash
# Run environment checker
scripts\setup-environment.bat
```

### 2. Start Appium Server
```bash
# Start Appium server
appium

# Verify server is running
curl http://127.0.0.1:4723/status
```

### 3. Run Tests
```bash
# Run smoke tests
scripts\run-tests.bat --suite smoke

# Run all tests
scripts\run-tests.bat --suite regression
```

## Troubleshooting

### Common Issues

#### Java Issues
```bash
# Issue: Java not found
# Solution: Install JDK and set JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
```

#### Android SDK Issues
```bash
# Issue: ANDROID_HOME not set
# Solution: Set environment variable
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk
```

#### ADB Issues
```bash
# Issue: ADB not found
# Solution: Add platform-tools to PATH
set PATH=%PATH%;%ANDROID_HOME%\platform-tools
```

#### Appium Issues
```bash
# Issue: Appium not found
# Solution: Install globally
npm install -g appium

# Issue: Driver not found
# Solution: Install UiAutomator2 driver
appium driver install uiautomator2
```

#### Device Connection Issues
```bash
# Issue: Device not detected
# Solutions:
adb kill-server
adb start-server
adb devices

# Enable USB debugging
# Try different USB cable
# Install device drivers
```

### Verification Commands
```bash
# Check Java
java -version

# Check Maven
mvn -version

# Check Android SDK
echo %ANDROID_HOME%

# Check ADB
adb version
adb devices

# Check Node.js
node --version

# Check Appium
appium --version
appium driver list

# Check Appium server
curl http://127.0.0.1:4723/status
```

## Configuration

### Device Configuration
Edit `src/test/resources/config/devices.json` to add your devices:

```json
{
  "devices": {
    "your_device": {
      "deviceName": "Your Device Name",
      "platformName": "Android",
      "platformVersion": "13",
      "automationName": "UiAutomator2",
      "udid": "your_device_udid"
    }
  }
}
```

### Application Properties
Edit `src/test/resources/application.properties`:

```properties
# Server configuration
appium.server.url=http://127.0.0.1:4723

# Default device
device.name=Android_Device
platform.version=13

# Timeouts
timeouts.implicit.wait=10
timeouts.explicit.wait=30

# Reporting
reporting.screenshot.on.failure=true
reporting.output.directory=reports
```

## IDE Setup

### IntelliJ IDEA
1. Import Maven project
2. Set Project SDK to Java 11+
3. Enable annotation processing
4. Install TestNG plugin

### Eclipse
1. Import existing Maven project
2. Set Java Build Path
3. Install TestNG plugin
4. Configure Maven integration

## Next Steps
- [Execution Guide](EXECUTION.md)
- [Framework Architecture](ARCHITECTURE.md)
- [Troubleshooting](TROUBLESHOOTING.md)
