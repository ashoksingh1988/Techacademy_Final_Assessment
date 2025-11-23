pipeline {
    agent any

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
            description: "How to execute the frameworks"
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
        choice(
            name: "TEST_SUITE",
            choices: ["smoke", "regression", "full"],
            description: "Test suite to execute across frameworks"
        )
        choice(
            name: "ENVIRONMENT",
            choices: ["qa", "staging", "production"],
            description: "Target environment for testing"
        )
    }

    stages {
        stage('Initialize') {
            steps {
                echo "Starting TechAcademy Master Pipeline..."
                echo "Execution Mode: ${params.EXECUTION_MODE}"
                echo "Java Selenium: ${params.RUN_JAVA_SELENIUM}"
                echo "Java Appium: ${params.RUN_JAVA_APPIUM}"
                echo "Python Selenium: ${params.RUN_PYTHON_SELENIUM}"
                echo "Test Suite: ${params.TEST_SUITE}"
                echo "Environment: ${params.ENVIRONMENT}"
            }
        }

        stage('Setup Environment') {
            steps {
                script {
                    echo "Setting up test environment..."
                    
                    // Start Appium if needed
                    if (params.RUN_JAVA_APPIUM) {
                        try {
                            timeout(time: 20, unit: 'SECONDS') {
                                bat(script: "jenkins-appium-start.bat", returnStatus: true)
                            }
                            
                            // Verify device is connected before proceeding
                            def deviceCheck = bat(script: "adb devices | findstr PZPVSC95GMKNGUBQ", returnStatus: true)
                            if (deviceCheck != 0) {
                                echo "ERROR: Device PZPVSC95GMKNGUBQ not connected!"
                                bat "adb devices"
                                error("Android device not found. Please connect device and enable USB debugging.")
                            } else {
                                echo "Device PZPVSC95GMKNGUBQ is ready"
                            }
                        } catch (Exception e) {
                            echo "Environment setup failed: ${e.getMessage()}"
                            throw e
                        }
                    } else {
                        echo "Skipping Appium setup (mobile tests disabled)"
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
                        def seleniumSuite = params.TEST_SUITE == "smoke" ? "smoke-tests" : params.TEST_SUITE == "regression" ? "regression-tests" : "cross-browser-tests"
                        jobs.add([
                            job: 'java-selenium-pipeline',
                            parameters: [
                                [$class: 'StringParameterValue', name: 'TEST_SUITE', value: seleniumSuite],
                                [$class: 'StringParameterValue', name: 'BROWSER_TYPE', value: 'chrome'],
                                [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: params.ENVIRONMENT],
                                [$class: 'BooleanParameterValue', name: 'HEADLESS_MODE', value: false]
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
                                [$class: 'BooleanParameterValue', name: 'HEADLESS_MODE', value: false]
                            ]
                        ])
                    }

                    if (params.EXECUTION_MODE == "parallel") {
                        parallel jobs.collectEntries { jobConfig ->
                            [jobConfig.job, {
                                build job: jobConfig.job, parameters: jobConfig.parameters
                            }]
                        }
                    } else {
                        jobs.each { jobConfig ->
                            build job: jobConfig.job, parameters: jobConfig.parameters
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
                            if exist python-selenium-automation\\reports xcopy python-selenium-automation\\reports consolidated-reports\\python\\ /E /I /Y 2>nul
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
                        emailext(
                            subject: "Test Pipeline - ${currentBuild.currentResult}",
                            body: "Build: ${env.BUILD_NUMBER} | Status: ${currentBuild.currentResult}",
                            to: 'ashokchandravanshi1988@gmail.com'
                        )
                    } catch (Exception e) {
                        echo "Email notification skipped"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                try {
                    bat "call jenkins-appium-stop.bat 2>nul"
                } catch (Exception e) {}
            }
            cleanWs()
        }
        success { echo "Pipeline completed successfully!" }
        failure { echo "Pipeline failed!" }
    }
}
