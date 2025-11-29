# Java Appium Framework - Flow of Execution

Complete flow diagram showing how Java Appium framework executes mobile automation tests.

---

## 🎯 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    JAVA APPIUM FRAMEWORK                         │
│                 (Mobile Android Automation)                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │ Appium Server    │
                    │ (127.0.0.1:4723) │
                    └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │  Maven + TestNG  │
                    └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   BaseTest.java │
                    │  (Setup/Teardown)│
                    └─────────────────┘
                              │
                ┌─────────────┼─────────────┐
                ▼             ▼             ▼
        ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
        │DriverFactory│ │Page Objects  │ │ReportManager │
        └──────────────┘ └──────────────┘ └──────────────┘
                │             │             │
                └─────────────┼─────────────┘
                              ▼
                    ┌─────────────────┐
                    │ Android Device  │
                    │ (Phone/Emulator)│
                    └─────────────────┘
```

---

## 📊 Detailed Execution Flow

### **Step 1: Prerequisites Setup**

```
BEFORE RUNNING TESTS
       │
       ├─→ Start Appium Server
       │   └─→ Command: appium
       │       └─→ Server runs on: http://127.0.0.1:4723
       │
       ├─→ Connect Android Device/Emulator
       │   └─→ Command: adb devices
       │       └─→ Verify device shows as "device" (not "offline")
       │
       └─→ Install/Verify Apps on Device
           ├─→ Calculator (com.google.android.calculator)
           ├─→ ColorNote (com.socialnmobile.dictapps.notepad.color.note)
           ├─→ Files by Google (com.google.android.apps.nbu.files)
           └─→ Google Docs (com.google.android.apps.docs.editors.docs)
```

---

### **Step 2: Test Initiation**

```
USER RUNS COMMAND
       │
       ▼
run-java-appium.bat  OR  mvn clean test
       │
       ▼
Maven reads pom.xml
       │
       ├─→ Downloads dependencies (Appium Java Client, TestNG, etc.)
       ├─→ Compiles Java source code
       └─→ Finds TestNG XML file
       │
       ▼
TestNG reads XML file (smoke-tests.xml)
       │
       ├─→ Gets device parameters (deviceName, platformVersion)
       ├─→ Gets app parameters (appPackage, appActivity)
       └─→ Identifies test classes (CalculatorTests.java, etc.)
```

---

### **Step 3: Configuration Loading**

```
ConfigurationManager.java
       │
       ├─→ Reads application.properties
       │   ├─→ appium.server.url=http://127.0.0.1:4723
       │   ├─→ implicit.wait=10
       │   └─→ screenshot.location=reports/screenshots/
       │
       └─→ Reads devices.json
           ├─→ Device configurations
           ├─→ Platform versions
           └─→ Device capabilities
```

---

### **Step 4: Test Suite Setup (@BeforeSuite)**

```
BaseTest.java → @BeforeSuite
       │
       ├─→ Initialize Logging System
       │   └─→ Set log level, format, output destination
       │
       ├─→ Initialize ReportManager
       │   └─→ Create ExtentReports instance
       │   └─→ Set report configuration (mobile-specific)
       │
       ├─→ Verify Appium Server Running
       │   └─→ HTTP GET: http://127.0.0.1:4723/status
       │       ├─→ Response 200: Server ready ✓
       │       └─→ Response error: Exit with message ✗
       │
       └─→ Setup Screenshot Utility
           └─→ Create screenshots directory
```

---

### **Step 5: Test Method Setup (@BeforeMethod)**

```
BaseTest.java → @BeforeMethod (runs before EACH test)
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│           DriverFactory.createDriver()                    │
└──────────────────────────────────────────────────────────┘
       │
       ▼
Read Test Parameters from TestNG XML
       │
       ├─→ deviceName = "Android_Device"
       ├─→ platformName = "Android"
       ├─→ platformVersion = "11"
       ├─→ appPackage = "com.google.android.calculator"
       ├─→ appActivity = "com.android.calculator2.Calculator"
       └─→ automationName = "UIAutomator2"
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│         Build Desired Capabilities (UiAutomator2Options)  │
└──────────────────────────────────────────────────────────┘
       │
       ├─→ Set platform: Android
       ├─→ Set device name
       ├─→ Set platform version
       ├─→ Set app package & activity
       ├─→ Set automation: UIAutomator2
       ├─→ Set noReset: true (don't clear app data)
       └─→ Set fullReset: false (don't reinstall app)
       │
       ▼
┌──────────────────────────────────────────────────────────┐
│    Connect to Appium Server (Create AndroidDriver)       │
└──────────────────────────────────────────────────────────┘
       │
       └─→ new AndroidDriver(serverURL, capabilities)
               │
               ▼
       Appium Server communicates with device
               │
               ├─→ Launches ADB (Android Debug Bridge)
               ├─→ Connects to device via ADB
               ├─→ Installs Appium Settings (if needed)
               ├─→ Starts UIAutomator2 server on device
               └─→ Launches specified app
               │
               ▼
       Calculator app opens on device/emulator
```

**Appium Architecture:**
```
Test Code → Appium Client → HTTP Request → Appium Server
                                               │
                                               ▼
                                           ADB Bridge
                                               │
                                               ▼
                                      UIAutomator2 Server (on device)
                                               │
                                               ▼
                                         Android Device
                                               │
                                               ▼
                                         Mobile App
```

---

### **Step 6: Test Execution**

```
CalculatorTests.java → @Test methods execute
       │
       ▼
┌────────────────────────────────────────────────────────────┐
│  Test 1: testCalculatorPageLoad()                           │
│  Priority: 1                                                │
└────────────────────────────────────────────────────────────┘
       │
       ▼
┌────────────────────────────────────────────────────────────┐
│   CalculatorPage calculatorPage = new CalculatorPage(driver)│
└────────────────────────────────────────────────────────────┘
       │
       ├─→ CalculatorPage constructor initializes elements
       │   └─→ AppiumFieldDecorator.decorate(driver, this)
       │       └─→ Finds all @AndroidFindBy annotated elements
       │
       ▼
calculatorPage.isCalculatorDisplayed()
       │
       └─→ Verify calculator display element is visible
               │
               ├─→ If PASS: Mark test as passed ✓
               └─→ If FAIL: Mark test as failed ✗, take screenshot
```

---

### **Step 7: Page Object Interaction**

```
┌──────────────────────────────────────────────────────────────┐
│                  CalculatorPage.java                          │
└──────────────────────────────────────────────────────────────┘
       │
       ▼
Element Locators (defined using @AndroidFindBy)
       │
       ├─→ @AndroidFindBy(id = "com.google.android.calculator:id/digit_5")
       ├─→ @AndroidFindBy(id = "com.google.android.calculator:id/op_add")
       ├─→ @AndroidFindBy(id = "com.google.android.calculator:id/digit_3")
       ├─→ @AndroidFindBy(id = "com.google.android.calculator:id/eq")
       └─→ @AndroidFindBy(id = "com.google.android.calculator:id/result")
       │
       ▼
calculatorPage.performAddition(5, 3)
       │
       ├─→ Find digit 5 button → Tap it
       ├─→ Find plus (+) button → Tap it
       ├─→ Find digit 3 button → Tap it
       ├─→ Find equals (=) button → Tap it
       └─→ Read result from display
       │
       ▼
String result = calculatorPage.getResult()
       │
       └─→ Assert.assertEquals("8", result)
```

**Element Finding Flow:**
```
@AndroidFindBy(id = "com.google.android.calculator:id/digit_5")
       │
       ▼
Appium Client → HTTP POST /element
       │
       ▼
Appium Server → UIAutomator2 Command
       │
       ▼
UIAutomator2 Server (on device) → Searches UI hierarchy
       │
       ▼
Returns WebElement reference
       │
       ▼
Test can interact with element (click, sendKeys, getText)
```

---

### **Step 8: Multiple App Testing**

```
Test Suite has 4 app test classes:
       │
       ├─→ CalculatorTests.java
       │   ├─→ Close Calculator app
       │   └─→ Launch ColorNote app
       │
       ├─→ ColorNoteTests.java
       │   ├─→ Close ColorNote app
       │   └─→ Launch Files app
       │
       ├─→ FilesTests.java
       │   ├─→ Close Files app
       │   └─→ Launch Google Docs app
       │
       └─→ GoogleDocsTests.java
           └─→ Close Google Docs app
```

**App Switching Flow:**
```
@AfterMethod (after test)
       │
       └─→ driver.quit() → Closes current app session
               │
               ▼
@BeforeMethod (before next test)
       │
       └─→ DriverFactory.createDriver(newAppPackage, newAppActivity)
               │
               └─→ Appium launches different app
```

---

### **Step 9: Test Cleanup (@AfterMethod)**

```
BaseTest.java → @AfterMethod (runs after EACH test)
       │
       ├─→ Capture screenshot from device
       │   └─→ ScreenshotUtils.takeScreenshot(driver, testName)
       │       └─→ driver.getScreenshotAs(OutputType.FILE)
       │       └─→ Save to: reports/screenshots/
       │
       ├─→ Log test result
       │   └─→ ReportManager.logTest(testName, status, deviceInfo)
       │
       └─→ Close app session
           └─→ driver.quit()
               ├─→ Stops UIAutomator2 server on device
               └─→ Closes app
```

---

### **Step 10: Test Suite Cleanup (@AfterSuite)**

```
BaseTest.java → @AfterSuite (runs once after ALL tests)
       │
       ├─→ Flush ExtentReports
       │   └─→ ReportManager.flushReports()
       │       └─→ Write all test data to HTML file
       │       └─→ Generate: Appium_Android_Automation_Report_<timestamp>.html
       │
       ├─→ Close any remaining driver instances
       │   └─→ Check if driver != null, then quit()
       │
       └─→ Print execution summary
           └─→ "Tests run: 12, Passed: 11, Failed: 1"
           └─→ "Device: Android_Device, Platform: 11"
```

---

## 🔄 Complete Flow Diagram (Start to Finish)

```
┌─────────────────────────────────────────────────────────────┐
│ Step 1: Start Appium Server (appium)                        │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 2: Connect Android device/emulator (adb devices)       │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 3: User runs → run-java-appium.bat                     │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 4: Maven builds, TestNG loads smoke-tests.xml          │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 5: @BeforeSuite → Setup logging, reports, verify server│
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 6: @BeforeMethod → DriverFactory creates AndroidDriver │
│         Appium launches Calculator app on device             │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 7: @Test(priority=1) → testCalculatorPageLoad()        │
│         CalculatorPage → Verify app loaded                  │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 8: @AfterMethod → Screenshot, log result, close app    │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 9: Repeat Steps 6-8 for Calculator tests               │
│         Then switch to ColorNote app and run its tests      │
│         Then Files app, then Google Docs app                │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 10: @AfterSuite → Generate HTML report, print summary  │
└───────────────────────────┬─────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ Step 11: DONE! Report saved, Appium server still running    │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗂️ File Interaction Map

```
User Command
    │
    ├─→ run-java-appium.bat
    │       │
    │       └─→ java-appium-automation/pom.xml
    │               │
    │               └─→ src/test/resources/suites/smoke-tests.xml
    │                       │
    │                       ├─→ src/test/java/com/appium/tests/CalculatorTests.java
    │                       ├─→ src/test/java/com/appium/tests/ColorNoteTests.java
    │                       ├─→ src/test/java/com/appium/tests/FilesTests.java
    │                       └─→ src/test/java/com/appium/tests/GoogleDocsTests.java
    │                               │
    │                               ├─→ src/main/java/com/appium/core/BaseTest.java
    │                               │       │
    │                               │       ├─→ src/main/java/com/appium/core/DriverFactory.java
    │                               │       └─→ src/main/java/com/appium/utils/ReportManager.java
    │                               │
    │                               └─→ src/main/java/com/appium/pages/CalculatorPage.java
    │
    └─→ Output
            ├─→ reports/Appium_Android_Automation_Report_*.html
            └─→ reports/screenshots/success/*.png
            └─→ reports/screenshots/failure/*.png
```

---

## 📚 Key Classes and Their Roles

### **1. BaseTest.java**
- **Role:** Foundation class, test lifecycle management
- **Methods:**
  - `@BeforeSuite` → Verify Appium server, setup reports
  - `@BeforeMethod` → Launch app on device
  - `@AfterMethod` → Close app, save screenshot
  - `@AfterSuite` → Final cleanup and reporting

### **2. DriverFactory.java**
- **Role:** Appium driver creation and configuration
- **Methods:**
  - `createDriver(deviceName, appPackage, appActivity)` → Create AndroidDriver
  - `buildCapabilities()` → Set UiAutomator2 options
  - `getDriver()` → Return current driver instance

### **3. CalculatorPage.java**
- **Role:** Calculator app interactions
- **Elements:**
  - `digit_0` to `digit_9`, `opAdd`, `opSub`, `equals`, `result`
- **Methods:**
  - `performAddition(a, b)` → Tap digits and operators
  - `getResult()` → Read calculation result
  - `clearCalculator()` → Tap clear button

### **4. ColorNotePage.java**
- **Role:** ColorNote app interactions
- **Elements:**
  - `addNoteButton`, `noteTitleField`, `noteBodyField`, `saveButton`
- **Methods:**
  - `createNote(title, body)` → Create new note
  - `isNoteDisplayed(title)` → Verify note exists

### **5. ConfigurationManager.java**
- **Role:** Load configuration from files
- **Methods:**
  - `getAppiumServerUrl()` → Returns server URL
  - `getDeviceConfig(name)` → Get device settings from devices.json
  - `getTimeout()` → Get wait timeout value

### **6. WaitHelper.java**
- **Role:** Smart waiting for mobile elements
- **Methods:**
  - `waitForElement(element, seconds)` → Explicit wait
  - `waitForElementToBeClickable(element)` → Wait until tappable

---

## 🎯 Data Flow

```
TestNG XML (smoke-tests.xml)
    │
    ├─→ <parameter name="deviceName" value="Android_Device"/>
    ├─→ <parameter name="platformVersion" value="11"/>
    ├─→ <parameter name="appPackage" value="com.google.android.calculator"/>
    └─→ <parameter name="appActivity" value="com.android.calculator2.Calculator"/>
                │
                ▼
        ConfigurationManager.java
                │
                ▼
        DriverFactory.java
                │
                ▼
        UiAutomator2Options (Capabilities)
                │
                ▼
        Appium Server
                │
                ▼
        Android Device
```

---

## ⚙️ Configuration Files

### **smoke-tests.xml**
```xml
<test name="Calculator App Tests">
    <parameter name="deviceName" value="Android_Device"/>
    <parameter name="platformVersion" value="11"/>
    <parameter name="appPackage" value="com.google.android.calculator"/>
    <parameter name="appActivity" value="com.android.calculator2.Calculator"/>
    <classes>
        <class name="com.appium.tests.CalculatorTests"/>
    </classes>
</test>
```

### **devices.json**
```json
{
  "devices": [
    {
      "deviceName": "Pixel_7_Pro",
      "platformVersion": "13",
      "udid": "emulator-5554"
    }
  ]
}
```

### **application.properties**
```properties
appium.server.url=http://127.0.0.1:4723
implicit.wait=10
explicit.wait=30
screenshot.location=reports/screenshots/
```

---

## 🔌 Appium Server Communication

### **Session Creation Request:**
```json
POST http://127.0.0.1:4723/session
{
  "capabilities": {
    "alwaysMatch": {
      "platformName": "Android",
      "appium:deviceName": "Android_Device",
      "appium:platformVersion": "11",
      "appium:appPackage": "com.google.android.calculator",
      "appium:appActivity": "com.android.calculator2.Calculator",
      "appium:automationName": "UIAutomator2"
    }
  }
}
```

### **Element Interaction:**
```
Find Element:  POST /session/{sessionId}/element
Click Element: POST /session/{sessionId}/element/{elementId}/click
Get Text:      GET  /session/{sessionId}/element/{elementId}/text
```

---

**Last Updated:** 2025-11-28  
**Framework Version:** 1.0
