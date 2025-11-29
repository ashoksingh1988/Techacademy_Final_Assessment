pipeline {
    agent any

    triggers {
        // Poll SCM every 5 minutes to check for changes
        // Jenkins will check GitHub and auto-trigger if new commits detected
        pollSCM('H/5 * * * *') // Checks every 5 minutes
    }

    environment {
        MAVEN_HOME = 'C:\\\\Program Files\\\\Maven\\\\ApacheMaven\\\\apache-maven-3.9.11'
        JAVA_HOME = 'C:\\\\\\\\Program Files\\\\\\\\Common Files\\\\\\\\Oracle\\\\\\\\Java\\\\\\\\javapath'
        PYTHON_VERSION = "3.13"
        PATH = "C:\\\\\\\\Program Files\\\\\\\\Common Files\\\\\\\\Oracle\\\\\\\\Java\\\\\\\\javapath;C:\\\\\\\\Program Files\\\\\\\\Maven\\\\\\\\ApacheMaven\\\\\\\\apache-maven-3.9.11\\\\\\\\bin;C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Programs\\\\\\\\Python\\\\\\\\Python313;C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Programs\\\\\\\\Python\\\\\\\\Python313\\\\\\\\Scripts;C:\\\\\\\\Program Files\\\\\\\\nodejs;C:\\\\\\\\Program Files\\\\\\\\Git\\\\\\\\cmd;C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Android\\\\\\\\Sdk\\\\\\\\platform-tools;C:\\\\\\\\Users\\\\\\\\Asim\\\\\\\\AppData\\\\\\\\Local\\\\\\\\Android\\\\\\\\Sdk\\\\\\\\emulator;C:\\\\\\\\Windows\\\\\\\\System32"
        APPIUM_SERVER = "http://127.0.0.1:4723"
        SELENIUM_HUB = "http://localhost:4444"
        ANDROID_HOME = 'C:\\\\Users\\\\Asim\\\\AppData\\\\Local\\\\Android\\\\Sdk'
    }

    parameters {
        choice(
            name: "EXECUTION_MODE",
            choices: ["sequential", "parallel", "selective"],
            description: "How to execute the frameworks (default: sequential for auto-triggers)"
        )
        booleanParam(
            name: "RUN_JAVA_SELENIUM",
            defaultValue: true,
            description: "Execute Java Selenium Framework"
        )
        booleanParam(
            name: "RUN_JAVA_APPIUM",
            defaultValue: true,
            description: "Execute Java Appium Framework"
        )
        booleanParam(
            name: "RUN_PYTHON_SELENIUM",
            defaultValue: true,
            description: "Execute Python Selenium Framework"
        )
        booleanParam(
            name: "RUN_PYTHON_PLAYWRIGHT",
            defaultValue: true,
            description: "Execute Python Playwright Framework"
        )
        choice(
            name: "TEST_SUITE",
            choices: ["smoke", "regression", "full"],
            description: "Test suite to execute across frameworks (default: smoke for quick validation)"
        )
        choice(
            name: "ENVIRONMENT",
            choices: ["qa", "staging", "production"],
            description: "Target environment for testing"
        )
        booleanParam(
            name: "HEADLESS_MODE",
            defaultValue: true,
            description: "Run tests in headless mode (true = faster CI/CD, false = visible browser for demo)"
        )
    }

    stages {
        stage('Initialize') {
            steps {
                echo "Starting TechAcademy Master Pipeline..."
                echo "Triggered by: ${currentBuild.getBuildCauses()[0].shortDescription}"
                echo "Execution Mode: ${params.EXECUTION_MODE}"
                echo "Java Selenium: ${params.RUN_JAVA_SELENIUM}"
                echo "Java Appium: ${params.RUN_JAVA_APPIUM}"
                echo "Python Selenium: ${params.RUN_PYTHON_SELENIUM}"
                echo "Python Playwright: ${params.RUN_PYTHON_PLAYWRIGHT}"
                echo "Test Suite: ${params.TEST_SUITE}"
                echo "Environment: ${params.ENVIRONMENT}"
                echo "Headless Mode: ${params.HEADLESS_MODE}"
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    echo "Setting up test environment..."
                    
                    // Always start Appium server (framework is fully operational)
                    try {
                        timeout(time: 30, unit: 'SECONDS') {
                            bat(script: "batch-scripts\\jenkins-appium-start.bat", returnStatus: true)
                        }
                        echo "Appium server started successfully"
                        
                        // Verify device is connected only if Appium tests are enabled
                        if (params.RUN_JAVA_APPIUM) {
                            def deviceCheck = bat(script: "adb devices | findstr PZPVSC95GMKNGUBQ", returnStatus: true)
                            if (deviceCheck != 0) {
                                echo "WARNING: Device PZPVSC95GMKNGUBQ not connected!"
                                bat "adb devices"
                                // Don't fail the build, just warn
                            } else {
                                echo "Device PZPVSC95GMKNGUBQ is ready"
                            }
                        }
                    } catch (Exception e) {
                        echo "Environment setup completed with warnings: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Execute Frameworks') {
            steps {
                echo "Executing selected automation frameworks..."
                script {
                    def jobs = []
                    
                    if (params.RUN_JAVA_APPIUM) {
                        def appiumSuite = params.TEST_SUITE == "smoke" ? "smoke-tests" : params.TEST_SUITE == "regression" ? "regression-tests" : "diagnostic-tests"
                        jobs.add([
                            job: 'java-appium-pipeline',
                            parameters: [
                                [$class: 'StringParameterValue', name: 'TEST_SUITE', value: appiumSuite],
                                [$class: 'StringParameterValue', name: 'DEVICE_UDID', value: 'PZPVSC95GMKNGUBQ']
                            ]
                        ])
                    }
                    
                    if (params.RUN_JAVA_SELENIUM) {
                        def seleniumSuite = params.TEST_SUITE == "smoke" ? "smoke-tests" : params.TEST_SUITE == "regression" ? "regression-tests" : "smoke-tests"
                        jobs.add([
                            job: 'java-selenium-pipeline',
                            parameters: [
                                [$class: 'StringParameterValue', name: 'TEST_SUITE', value: seleniumSuite],
                                [$class: 'StringParameterValue', name: 'BROWSER_TYPE', value: 'chrome'],
                                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: params.ENVIRONMENT],
                                [$class: 'BooleanParameterValue', name: 'HEADLESS_MODE', value: params.HEADLESS_MODE],
                                [$class: 'BooleanParameterValue', name: 'MULTI_BROWSER', value: false]
                            ]
                        ])
                    }
                    
                    if (params.RUN_PYTHON_SELENIUM) {
                        jobs.add([
                            job: 'python-selenium-pipeline',
                            parameters: [
                                [$class: 'StringParameterValue', name: 'TEST_SUITE', value: params.TEST_SUITE],
                                [$class: 'StringParameterValue', name: 'BROWSER_TYPE', value: 'chrome'],
                                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: params.ENVIRONMENT],
                                [$class: 'BooleanParameterValue', name: 'HEADLESS_MODE', value: params.HEADLESS_MODE],
                                [$class: 'BooleanParameterValue', name: 'MULTI_BROWSER', value: false],
                                [$class: 'BooleanParameterValue', name: 'PARALLEL_EXECUTION', value: false]
                            ]
                        ])
                    }
                    
                    if (params.RUN_PYTHON_PLAYWRIGHT) {
                        jobs.add([
                            job: 'python-playwright-pipeline',
                            parameters: [
                                [$class: 'StringParameterValue', name: 'TEST_SUITE', value: params.TEST_SUITE],
                                [$class: 'StringParameterValue', name: 'BROWSER_TYPE', value: 'chromium'],
                                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: params.ENVIRONMENT],
                                [$class: 'BooleanParameterValue', name: 'HEADLESS_MODE', value: params.HEADLESS_MODE],
                                [$class: 'BooleanParameterValue', name: 'PARALLEL_EXECUTION', value: false]
                            ]
                        ])
                    }

                    if (params.EXECUTION_MODE == "parallel" && jobs.size() > 1) {
                        parallel jobs.collectEntries { jobConfig ->
                            [jobConfig.job, {
                                build job: jobConfig.job, parameters: jobConfig.parameters, propagate: false
                            }]
                        }
                    } else {
                        jobs.each { jobConfig ->
                            build job: jobConfig.job, parameters: jobConfig.parameters, propagate: false
                        }
                    }
                }
            }
        }

        stage('Consolidate Reports') {
            steps {
                script {
                    try {
                        bat """
                            if not exist consolidated-reports mkdir consolidated-reports
                            if exist java-selenium-automation\\target\\surefire-reports xcopy java-selenium-automation\\target\\surefire-reports consolidated-reports\\selenium\\ /E /I /Y 2>nul
                            if exist java-appium-automation\\target\\surefire-reports xcopy java-appium-automation\\target\\surefire-reports consolidated-reports\\appium\\ /E /I /Y 2>nul
                            if exist python-selenium-automation\\reports xcopy python-selenium-automation\\reports consolidated-reports\\python-selenium\\ /E /I /Y 2>nul
                            if exist python-playwright-automation\\reports xcopy python-playwright-automation\\reports consolidated-reports\\python-playwright\\ /E /I /Y 2>nul
                        """
                    } catch (Exception e) {
                        echo "Report consolidation: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Publish Results') {
            steps {
                script {
                    try {
                        archiveArtifacts artifacts: 'consolidated-reports/**/*', allowEmptyArchive: true
                    } catch (Exception e) {
                        echo "Archiving: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Notification') {
            steps {
                script {
                    try {
                        echo "Sending email notification to: ashokchandravanshi1988@gmail.com"
                        emailext(
                            subject: "Jenkins Build ${currentBuild.currentResult}: techacademy-master-pipeline #${env.BUILD_NUMBER}",
                            body: """<html>
                                <body>
                                    <h2>TechAcademy Master Pipeline - Build Report</h2>
                                    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                                    <p><strong>Status:</strong> ${currentBuild.currentResult}</p>
                                    <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                                    <p><strong>Build URL:</strong> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                                    <hr>
                                    <h3>Frameworks Executed:</h3>
                                    <ul>
                                        <li>Java Selenium: ${params.RUN_JAVA_SELENIUM ? 'Enabled' : 'Disabled'}</li>
                                        <li>Java Appium: ${params.RUN_JAVA_APPIUM ? 'Enabled' : 'Disabled'}</li>
                                        <li>Python Selenium: ${params.RUN_PYTHON_SELENIUM ? 'Enabled' : 'Disabled'}</li>
                                        <li>Python Playwright: ${params.RUN_PYTHON_PLAYWRIGHT ? 'Enabled' : 'Disabled'}</li>
                                    </ul>
                                    <hr>
                                    <p><strong>Attachments:</strong> See attached build.log</p>
                                    <p><strong>HTML Reports:</strong> Individual framework HTML reports are sent in separate emails.</p>
                                    <p>All framework reports are archived in Jenkins artifacts.</p>
                                    <p>Check Jenkins for detailed consolidated reports and logs.</p>
                                </body>
                            </html>""",
                            to: 'ashokchandravanshi1988@gmail.com',
                            mimeType: 'text/html',
                            attachLog: true,
                            replyTo: 'ashokchandravanshi1988@gmail.com'
                        )
                        echo "Email sent successfully"
                    } catch (Exception e) {
                        echo "WARNING: Email notification failed - ${e.getMessage()}"
                        echo "NOTE: Configure SMTP settings in Jenkins for email notifications"
                        echo "Go to: Manage Jenkins → Configure System → E-mail Notification"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                try {
                    bat "call batch-scripts\\jenkins-appium-stop.bat 2>nul"
                } catch (Exception e) {}
            }
            cleanWs()
        }
        success { echo "Pipeline completed successfully!" }
        failure { echo "Pipeline failed!" }
    }
}