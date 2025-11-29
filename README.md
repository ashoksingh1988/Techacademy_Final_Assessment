# 🚀 TechAcademy Multi-Framework Test Automation Suite

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)
[![Python](https://img.shields.io/badge/Python-3.13-blue.svg)](https://www.python.org/)
[![Selenium](https://img.shields.io/badge/Selenium-4.18.1-green.svg)](https://www.selenium.dev/)
[![Appium](https://img.shields.io/badge/Appium-9.3.0-purple.svg)](https://appium.io/)
[![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red.svg)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.11-blue.svg)](https://maven.apache.org/)

## 📋 Overview

A comprehensive, enterprise-grade test automation framework supporting **Web**, **Mobile**, and **API** testing across multiple technology stacks. Built for scalability, maintainability, and seamless CI/CD integration.

## ✨ Key Features

- 🌐 **Multi-Platform Support**: Web (Selenium) + Mobile (Appium)
- 🔧 **Multi-Language**: Java + Python implementations
- 📊 **Rich Reporting**: ExtentReports with screenshots
- 🔄 **CI/CD Ready**: Jenkins pipelines with GitHub Actions
- 🎯 **Page Object Model**: Clean, maintainable test architecture
- ⚡ **Parallel Execution**: Faster test runs
- 🐳 **Dockerized**: Container-ready for cloud deployment
- 🎨 **Flexible Configuration**: Property-based and JSON configs

---

## 📦 Project Structure

```
SauceDemoAutomation/
│
├── 📱 java-appium-automation/          # Mobile automation (Android)
│   ├── src/main/java/com/appium/
│   │   ├── core/                       # Driver factory & base classes
│   │   ├── pages/                      # Page objects (Calculator, ColorNote, Files, Google Docs)
│   │   └── utils/                      # Utilities (Reports, Screenshots, Waits)
│   └── src/test/java/com/appium/tests/
│
├── 🌐 java-selenium-automation/        # Web automation (Selenium)
│   ├── src/main/java/com/selenium/
│   │   ├── core/                       # Driver manager & base test
│   │   ├── pages/                      # Page objects (Login, Inventory)
│   │   └── utils/                      # Utilities (Config, Reports, Data)
│   └── src/test/java/com/selenium/tests/
│
├── 🐍 python-selenium-automation/      # Python web automation
│   ├── pages/                          # Page objects
│   ├── tests/                          # Test cases
│   └── conftest.py                     # Pytest fixtures
│
├── 🎭 python-playwright-automation/    # Python Playwright automation
│   ├── pages/                          # Page objects
│   ├── tests/                          # Test cases
│   └── conftest.py                     # Pytest fixtures
│
├── 📚 framework-docs/                  # Framework documentation
│   ├── FRAMEWORK_EXECUTION_GUIDE.md    # Complete execution guide
│   ├── JAVA_APPIUM_FLOW.md             # Java Appium workflow
│   ├── JAVA_SELENIUM_FLOW.md           # Java Selenium workflow
│   ├── PYTHON_PLAYWRIGHT_FLOW.md       # Python Playwright workflow
│   └── PYTHON_SELENIUM_FLOW.md         # Python Selenium workflow
│
├── 🔧 batch-scripts/                   # All batch files & utilities
│   ├── run-java-appium.bat             # Execute Java Appium
│   ├── run-python-selenium.bat         # Execute Python Selenium
│   ├── run-python-playwright.bat       # Execute Python Playwright
│   ├── jenkins-appium-start.bat        # Start Appium for Jenkins
│   ├── jenkins-appium-stop.bat         # Stop Appium
│   ├── git-commit.bat                  # Git commit & push
│   ├── demo-commit.bat                 # Demo trigger script
│   └── demo-trigger.txt                # Demo trigger file
│
└── 🔧 CI/CD Configuration
    ├── Jenkinsfile                     # Master pipeline orchestrator
    ├── */Jenkinsfile                   # Framework-specific pipelines
    └── .github/workflows/              # GitHub Actions
```

### 📁 Folder Organization:

- **`framework-docs/`** - All framework documentation and execution flow guides
- **`batch-scripts/`** - All batch files, Git utilities, and demo scripts
- **Framework folders** - Self-contained automation frameworks with their own Jenkinsfiles

---

## 🛠️ Technology Stack

### Java Frameworks
| Technology | Version | Purpose |
|------------|---------|---------|
| ☕ **Selenium WebDriver** | 4.18.1 | Web automation |
| 📱 **Appium** | 9.3.0 | Mobile automation |
| ✅ **TestNG** | 7.9.0 | Test framework |
| 🔧 **WebDriverManager** | 5.7.0 | Driver management |
| 📊 **ExtentReports** | 5.1.2 | Test reporting |
| 📦 **Maven** | 3.9.11 | Build & dependency management |

### Python Frameworks
| Technology | Version | Purpose |
|------------|---------|---------|
| 🐍 **Python** | 3.13 | Programming language |
| 🌐 **Selenium** | Latest | Web automation |
| ✅ **Pytest** | Latest | Test framework |
| 📊 **pytest-html** | Latest | HTML reporting |

---

## 🚀 Quick Start

### Prerequisites

```bash
✅ Java JDK 11+
✅ Python 3.13+
✅ Maven 3.9+
✅ Node.js & Appium (for mobile tests)
✅ Android SDK (for mobile tests)
✅ Git
```

### 🔧 Installation

1. **Clone the repository**
```bash
git clone https://github.com/ashoksingh1988/Techacademy_Final_Assessment.git
cd SauceDemoAutomation
```

2. **Setup Java Selenium**
```bash
cd java-selenium-automation
mvn clean install
```

3. **Setup Python Selenium**
```bash
cd python-selenium-automation
pip install -r requirements.txt
```

4. **Setup Java Appium** (Optional - for mobile testing)
```bash
cd java-appium-automation
mvn clean install
# Start Appium server
batch-scripts\jenkins-appium-start.bat
```

---

## ▶️ Running Tests

### 🌐 Java Selenium Tests

```bash
# Run smoke tests
cd java-selenium-automation
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/selenium-smoke-tests.xml

# Run with specific browser
mvn test -Dbrowser=chrome -Dheadless=false

# Run regression tests
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/selenium-regression-tests.xml
```

### 🐍 Python Selenium Tests

```bash
cd python-selenium-automation

# Run all tests
pytest tests/ -v --html=reports/report.html

# Run specific test
pytest tests/test_saucedemo_comprehensive.py -v

# Run with specific browser
HEADLESS=false pytest tests/ -v
```

### 📱 Java Appium Tests (Mobile)

```bash
cd java-appium-automation

# Run smoke tests
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml

# Run with specific device
mvn test -Ddevice.name=PZPVSC95GMKNGUBQ -Dplatform.version=11
```

---

## 🔄 Jenkins CI/CD - Automated Build Triggering

### 🎯 Auto-Trigger Configuration (SCM Polling)

The framework is configured with **automated Jenkins build triggering** using Git SCM Polling:

**How It Works:**
```
Developer Pushes Code to GitHub
        ↓
Jenkins Polls GitHub Every 5 Minutes
        ↓
Detects New Commits Automatically
        ↓
Auto-Triggers: techacademy-master-pipeline
        ↓
Executes All Enabled Frameworks
```

**Configuration Details:**
- ✅ **Polling Schedule**: Every 5 minutes (`H/5 * * * *`)
- ✅ **Trigger**: Defined in `Jenkinsfile` using `pollSCM()`
- ✅ **Auto-Executes**: Java Selenium, Python Selenium, Python Playwright, Java Appium
- ✅ **No Manual Intervention**: Fully automated on Git push

**Jenkins Settings Required:**
1. Navigate to: `Jenkins → techacademy-master-pipeline → Configure`
2. Under **Build Triggers**: Check **"Poll SCM"**
3. Schedule field should show: `H/5 * * * *` (auto-populated from Jenkinsfile)
4. Click **Save**

### Master Pipeline Configuration

The framework includes a **master orchestrator pipeline** that coordinates all test executions:

**Pipeline Parameters:**
- ✅ **EXECUTION_MODE**: `parallel` | `sequential` | `selective`
- ✅ **RUN_JAVA_SELENIUM**: Execute web tests (Enabled by default)
- ✅ **RUN_JAVA_APPIUM**: Execute mobile tests (Enabled by default)
- ✅ **RUN_PYTHON_SELENIUM**: Execute Python tests (Enabled by default)
- ✅ **RUN_PYTHON_PLAYWRIGHT**: Execute Playwright tests (Enabled by default)
- ✅ **TEST_SUITE**: `smoke` | `regression` | `full`
- ✅ **ENVIRONMENT**: `qa` | `staging` | `production`
- ✅ **HEADLESS_MODE**: `true` | `false` (false = visible browser for demos)

**Email Notifications:**
- 📧 **Recipient**: ashokchandravanshi1988@gmail.com
- 📊 **Contains**: Build status, duration, framework execution details
- 🔗 **Includes**: Direct link to Jenkins build + build logs
- ⚙️ **Setup Required**: Configure SMTP in Jenkins (Manage Jenkins → Configure System → E-mail Notification)

### 🎬 Demo Mode - Quick Trigger

For live demonstrations without modifying actual code:

**Method 1: Automated Demo Script**
```bash
demo-commit.bat
```
- Auto-updates timestamp in `demo-trigger.txt`
- Commits and pushes to GitHub
- Jenkins auto-detects within 5 minutes
- Triggers full pipeline execution

**Method 2: Manual Demo Trigger**
1. Edit `demo-trigger.txt` (change Demo Count number)
2. Run `git-commit.bat` to push changes
3. Wait 2-5 minutes for Jenkins to detect and trigger

**Why Use Demo Mode:**
- ✅ **Zero risk** to actual test code
- ✅ **Safe for multiple demos**
- ✅ **Only modifies** dummy file
- ✅ **Perfect for presentations** to stakeholders

### 🎯 Demo Mode Configuration

**Default Settings (Optimized for Live Demos):**
- 🌐 **Browser**: Chrome only (visible execution)
- 👁️ **Headless Mode**: Disabled (browser launches visibly)
- 🚫 **Multi-Browser**: Disabled (prevents delays)
- ⚡ **Parallel Execution**: Disabled (reliable execution)

**To Enable Multi-Browser Demo:**
1. Go to Jenkins → `java-selenium-pipeline`
2. Click "Build with Parameters"
3. Set `MULTI_BROWSER = true`
4. Set `HEADLESS_MODE = false`
5. Click "Build" → Chrome, Firefox, Edge will launch! 🎬

---

## 📊 Test Reports

### Report Locations

```
📁 Reports are auto-generated at:
├── java-selenium-automation/reports/         # Selenium web reports
├── java-appium-automation/reports/           # Appium mobile reports
├── python-selenium-automation/reports/       # Python test reports
└── consolidated-reports/                     # Jenkins consolidated reports
```

### 📸 Screenshot Management

- ✅ **On Failure**: Auto-captured with timestamps
- ✅ **On Success**: Optional success screenshots
- ✅ **Stored In**: `reports/screenshots/`

---

## 🐳 Docker Support

### Build Docker Images

```bash
# Selenium
cd java-selenium-automation
docker build -t selenium-automation .

# Appium
cd java-appium-automation
docker build -t appium-automation .

# Python
cd python-selenium-automation
docker build -t python-selenium-automation .
```

### Run in Docker

```bash
docker run --rm selenium-automation
docker run --rm appium-automation
docker run --rm python-selenium-automation
```

---

## 🧪 Test Scenarios

### 🌐 Web Tests (Selenium)
- ✅ User login with valid/invalid credentials
- ✅ Product inventory browsing
- ✅ Add/remove items to cart
- ✅ Checkout flow
- ✅ Logout functionality

### 📱 Mobile Tests (Appium)
- ✅ Calculator operations
- ✅ ColorNote app interactions
- ✅ Files app navigation
- ✅ Google Docs functionality

---

## 🔧 Configuration

### Java Configuration
**Location**: `src/test/resources/application.properties`

```properties
# Application
app.url=https://www.saucedemo.com
app.timeout=10

# Browser
browser.type=chrome
browser.headless=false

# Reporting
reports.directory=reports
screenshot.on.failure=true
```

### Python Configuration
**Location**: `conftest.py` + `pytest.ini`

```python
# Browser options
HEADLESS = os.getenv('HEADLESS', 'false').lower() == 'true'
BROWSER_TYPE = os.getenv('BROWSER_TYPE', 'chrome')
```

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 Best Practices

✅ **Page Object Model** - Separation of test logic and page interactions  
✅ **Data-Driven Testing** - Externalized test data in JSON/properties  
✅ **Explicit Waits** - Robust element synchronization  
✅ **Singleton Pattern** - Driver management  
✅ **Logging** - Comprehensive test execution logs  
✅ **Version Control** - Git-based workflow  
✅ **CI/CD Integration** - Jenkins + GitHub Actions  

---

## 🐛 Troubleshooting

### 📧 Email Notification Setup (SMTP Configuration)

**Issue**: Email notifications failing with "Connection error sending email"

**Solution**: Configure Jenkins SMTP settings (one-time setup)

**Step-by-Step Configuration:**

1. **Go to Jenkins Dashboard**
   - Navigate to: `Manage Jenkins → Configure System`

2. **Scroll to "E-mail Notification" Section**

3. **Configure SMTP Server** (for Gmail):
   ```
   SMTP server: smtp.gmail.com
   Default user e-mail suffix: @gmail.com
   ✓ Use SMTP Authentication
       User Name: ashokchandravanshi1988@gmail.com
       Password: [Your App-Specific Password]
   ✓ Use SSL
   SMTP Port: 465
   ```

4. **Gmail App Password Setup** (Required for security):
   - Go to: https://myaccount.google.com/apppasswords
   - Sign in to your Google account
   - Click "Select app" → Choose "Mail"
   - Click "Select device" → Choose "Other (Custom name)" → Enter "Jenkins"
   - Click "Generate"
   - Copy the 16-character password (example: `abcd efgh ijkl mnop`)
   - Paste this password in Jenkins SMTP Password field

5. **Extended E-mail Notification** (for HTML emails):
   - Scroll to "Extended E-mail Notification" section
   - SMTP server: `smtp.gmail.com`
   - SMTP Port: `465`
   - Click "Advanced"
   - ✓ Use SMTP Authentication
   - User Name: `ashokchandravanshi1988@gmail.com`
   - Password: [Same App-Specific Password]
   - ✓ Use SSL
   - Default Content Type: `text/html`

6. **Test Email Configuration**:
   - Check "✓ Test configuration by sending test e-mail"
   - Test e-mail recipient: `ashokchandravanshi1988@gmail.com`
   - Click "Test configuration"
   - Should see: "Email was successfully sent"

7. **Click Save**

**Alternative SMTP Providers:**

**Outlook/Hotmail:**
```
SMTP server: smtp-mail.outlook.com
SMTP Port: 587
Use TLS (not SSL)
```

**Office 365:**
```
SMTP server: smtp.office365.com
SMTP Port: 587
Use TLS
```

**Note**: After configuration, Jenkins will send HTML-formatted emails with:
- ✅ Build status and number
- ✅ Execution duration
- ✅ Framework execution summary
- ✅ Direct link to Jenkins build
- ✅ Attached build logs

---

### Python PATH Issues in Jenkins
**Issue**: `'python' is not recognized as an internal or external command`

**Solution**: Explicit PATH configuration in Jenkinsfile
```groovy
environment {
    PYTHON_HOME = 'C:\\Users\\<user>\\AppData\\Local\\Programs\\Python\\Python313'
    PATH = "${PYTHON_HOME};${PYTHON_HOME}\\Scripts;C:\\Windows\\System32;${PATH}"
}
```

### Browser Not Launching
**Issue**: Tests run but browser doesn't appear

**Solution**: Set `HEADLESS_MODE = false` in Jenkins parameters

### Parallel Execution Failures
**Issue**: File locking errors during parallel runs

**Solution**: Disable `MULTI_BROWSER` in Jenkins (default configuration)

---

## 📞 Support & Contact

**Author**: Ashok Singh  
**Email**: ashokchandravanshi1988@gmail.com  
**Repository**: [GitHub - Techacademy_Final_Assessment](https://github.com/ashoksingh1988/Techacademy_Final_Assessment)

---

## 📄 License

This project is part of TechAcademy Final Assessment.

---

## 🎯 Roadmap

- [ ] API Testing Integration (RestAssured)
- [ ] Performance Testing (JMeter/Gatling)
- [ ] Visual Regression Testing
- [ ] BDD Framework (Cucumber)
- [ ] Cloud Testing (BrowserStack/Sauce Labs)
- [ ] AI-Powered Test Generation

---

<div align="center">

### ⭐ Star this repo if you find it helpful!

**Built with ❤️ for Quality Assurance Excellence**

</div>
