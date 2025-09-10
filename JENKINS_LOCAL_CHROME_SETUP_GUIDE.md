# Jenkins Local Chrome Configuration Guide
## Enhanced Automation Framework - Lead Review Edition

### 🎯 **Objective**
Configure Jenkins to use local Chrome browser from `C:\Users\Asim\Downloads\ChromeSetup` instead of headless mode for better debugging and visual verification during test execution.

---

## 📋 **Prerequisites**
- Jenkins installed and running on Windows
- Chrome browser installed at `C:\Users\Asim\Downloads\ChromeSetup\chrome.exe`
- Administrative access to Windows system
- Current working automation frameworks (Java Appium, Java Selenium, Python Selenium)

---

## 🔧 **Step 1: Jenkins Service Configuration**

### **1.1 Stop Jenkins Service**
```powershell
# Run as Administrator in PowerShell
Stop-Service Jenkins
```

### **1.2 Change Service Account**
```powershell
# Configure Jenkins to run as your user account instead of Windows service
sc.exe config Jenkins obj= ".\Asim" password= "YOUR_WINDOWS_PASSWORD"
```

### **1.3 Grant "Log on as a service" Right**
1. Open **Local Security Policy** (`secpol.msc`)
2. Navigate to: **Local Policies** → **User Rights Assignment**
3. Double-click **"Log on as a service"**
4. Click **"Add User or Group"**
5. Add your user account: `Asim`
6. Click **OK** and **Apply**

### **1.4 Start Jenkins Service**
```powershell
# Start Jenkins with new configuration
Start-Service Jenkins
```

### **1.5 Verify Service Status**
```powershell
# Check if Jenkins is running under your user account
Get-Service Jenkins
Get-WmiObject -Class Win32_Service -Filter "Name='Jenkins'" | Select-Object Name, StartName, State
```

---

## 🌐 **Step 2: Jenkins Global Configuration**

### **2.1 Access Jenkins Configuration**
1. Open Jenkins web interface: `http://localhost:8080`
2. Navigate to: **Manage Jenkins** → **Configure System**

### **2.2 Add Global Environment Variables**
In the **Global Properties** section, add these environment variables:

| Variable Name | Value |
|---------------|-------|
| `CHROME_BINARY` | `C:\Users\Asim\Downloads\ChromeSetup\chrome.exe` |
| `DISPLAY` | `:0` |
| `HEADLESS_MODE` | `false` |
| `BROWSER_TYPE` | `chrome` |

### **2.3 Save Configuration**
Click **Save** to apply the global environment variables.

---

## 📝 **Step 3: Update Framework Jenkinsfiles**

### **3.1 Java Appium Framework**
File: `java-appium-automation/Jenkinsfile`

**Current Environment Section:**
```groovy
environment {
    JAVA_HOME = 'C:\\\\\\\\Program Files\\\\\\\\Java\\\\\\\\jdk-24'
    HEADLESS = 'true'
}
```

**Updated Environment Section:**
```groovy
environment {
    JAVA_HOME = 'C:\\\\\\\\Program Files\\\\\\\\Java\\\\\\\\jdk-24'
    CHROME_BINARY = 'C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\Downloads\\\\\\\\ChromeSetup\\\\\\\\chrome.exe'
    HEADLESS = 'false'
    DISPLAY = ':0'
}
```

### **3.2 Java Selenium Framework**
File: `java-selenium-automation/Jenkinsfile`

**Current Environment Section:**
```groovy
environment {
    JAVA_HOME = 'C:\\\\\\\\Program Files\\\\\\\\Java\\\\\\\\jdk-24'
    BROWSER = 'chrome'
    HEADLESS = 'true'
}
```

**Updated Environment Section:**
```groovy
environment {
    JAVA_HOME = 'C:\\\\\\\\Program Files\\\\\\\\Java\\\\\\\\jdk-24'
    BROWSER = 'chrome'
    CHROME_BINARY = 'C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\Downloads\\\\\\\\ChromeSetup\\\\\\\\chrome.exe'
    HEADLESS = 'false'
    DISPLAY = ':0'
}
```

### **3.3 Python Selenium Framework**
File: `python-selenium-automation/Jenkinsfile`

**Current Environment Section:**
```groovy
environment {
    PATH = "C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Programs\\\\\\\\Python\\\\\\\\Python312;C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Programs\\\\\\\\Python\\\\\\\\Python312\\\\\\\\Scripts;${env.PATH}"
}
```

**Updated Environment Section:**
```groovy
environment {
    PATH = "C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Programs\\\\\\\\Python\\\\\\\\Python312;C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Programs\\\\\\\\Python\\\\\\\\Python312\\\\\\\\Scripts;${env.PATH}"
    CHROME_BINARY = 'C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\Downloads\\\\\\\\ChromeSetup\\\\\\\\chrome.exe'
    HEADLESS_MODE = 'false'
    BROWSER_TYPE = 'chrome'
    DISPLAY = ':0'
}
```

---

## 🔍 **Step 4: Framework Code Updates**

### **4.1 Java Frameworks - WebDriver Configuration**
Update WebDriver initialization to use local Chrome binary:

```java
// In DriverFactory or WebDriver setup class
ChromeOptions options = new ChromeOptions();

// Use local Chrome binary if specified
String chromeBinary = System.getenv("CHROME_BINARY");
if (chromeBinary != null && !chromeBinary.isEmpty()) {
    options.setBinary(chromeBinary);
}

// Set headless mode based on environment
String headlessMode = System.getenv("HEADLESS");
if ("false".equalsIgnoreCase(headlessMode)) {
    // Run in GUI mode
    options.addArguments("--start-maximized");
} else {
    // Run in headless mode
    options.addArguments("--headless");
}

WebDriver driver = new ChromeDriver(options);
```

### **4.2 Python Framework - WebDriver Configuration**
Update `conftest.py` or WebDriver setup:

```python
from selenium.webdriver.chrome.options import Options

chrome_options = Options()

# Use local Chrome binary if specified
chrome_binary = os.getenv('CHROME_BINARY')
if chrome_binary and os.path.exists(chrome_binary):
    chrome_options.binary_location = chrome_binary

# Set headless mode based on environment
headless_mode = os.getenv('HEADLESS_MODE', 'true').lower() == 'true'
if headless_mode:
    chrome_options.add_argument('--headless')
else:
    chrome_options.add_argument('--start-maximized')

driver = webdriver.Chrome(options=chrome_options)
```

---

## ✅ **Step 5: Verification and Testing**

### **5.1 Test Jenkins Service**
```powershell
# Verify Jenkins is running under your user account
Get-Process -Name "jenkins" | Select-Object ProcessName, UserName
```

### **5.2 Test Local Chrome Access**
```powershell
# Verify Chrome executable exists and is accessible
Test-Path "C:\Users\Asim\Downloads\ChromeSetup\chrome.exe"
& "C:\Users\Asim\Downloads\ChromeSetup\chrome.exe" --version
```

### **5.3 Run Test Pipeline**
1. Trigger a test build from Jenkins
2. Monitor console output for Chrome browser launching
3. Verify tests run in visible Chrome windows
4. Check enhanced reports for screenshots and performance metrics

---

## 🚨 **Troubleshooting**

### **Issue 1: Jenkins Service Won't Start**
```powershell
# Check Windows Event Logs
Get-EventLog -LogName System -Source "Service Control Manager" -Newest 10 | Where-Object {$_.Message -like "*Jenkins*"}

# Reset service configuration if needed
sc.exe config Jenkins obj= "LocalSystem"
Start-Service Jenkins
```

### **Issue 2: Chrome Binary Not Found**
```powershell
# Verify Chrome installation
Get-ChildItem "C:\Users\Asim\Downloads\ChromeSetup\" -Filter "*.exe"

# Alternative Chrome locations to check
Test-Path "C:\Program Files\Google\Chrome\Application\chrome.exe"
Test-Path "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"
```

### **Issue 3: Permission Denied**
```powershell
# Grant full control to Jenkins user
icacls "C:\Users\Asim\Downloads\ChromeSetup" /grant "Asim:(OI)(CI)F" /T
```

### **Issue 4: Display Issues**
- Ensure Windows desktop is unlocked during test execution
- Consider using Windows Task Scheduler for Jenkins service
- Verify DISPLAY environment variable is set correctly

---

## 📊 **Expected Results**

After successful configuration:

1. **Visual Execution**: Chrome browser windows will be visible during test execution
2. **Enhanced Screenshots**: Both success and failure screenshots will show actual browser UI
3. **Better Debugging**: Ability to see real-time test execution for troubleshooting
4. **Lead Review**: Visual evidence of test execution for comprehensive review

---

## 🔄 **Rollback Plan**

If issues occur, revert to headless mode:

```groovy
environment {
    HEADLESS = 'true'
    // Remove or comment out CHROME_BINARY and DISPLAY
}
```

And restart Jenkins service:
```powershell
Restart-Service Jenkins
```

---

## 📞 **Support**

For additional support:
1. Check Jenkins logs: `C:\Program Files\Jenkins\jenkins.err.log`
2. Review Windows Event Logs for service issues
3. Verify Chrome browser compatibility with WebDriver versions
4. Test individual framework execution outside Jenkins first

---

**✅ Configuration Complete!**
Your Jenkins automation frameworks are now configured to use local Chrome browser for enhanced visual testing and comprehensive lead review capabilities.
