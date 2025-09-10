pipeline {
    agent any

    environment {
        MAVEN_HOME = 'C:\\\\Program Files\\\\Maven\\\\ApacheMaven\\\\apache-maven-3.9.11'
        JAVA_HOME = 'C:\\\\Program Files\\\\Java\\\\jdk-24'
        PYTHON_VERSION = "3.13"
        PATH = "C:\\\\Program Files\\\\Maven\\\\ApacheMaven\\\\apache-maven-3.9.11\\\\bin;C:\\\\Program Files\\\\Java\\\\jdk-24\\\\bin;C:\\\\Users\\\\Asim\\\\AppData\\\\Local\\\\Programs\\\\Python\\\\Python313;C:\\\\Program Files\\\\Git\\\\cmd;C:\\\\Program Files\\\\nodejs;C:\\\\Windows\\\\System32"
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
                echo "Setting up master test environment..."
                script {
                    try {
                        def appiumStatus = bat(
                            script: "timeout 3 curl -s ${APPIUM_SERVER}/status 2>nul || echo Appium not running",
                            returnStdout: true
                        ).trim()
                        
                        if (appiumStatus.contains('Appium not running')) {
                            echo "Appium server is not running. Mobile tests will run in demo mode."
                        } else {
                            echo "Appium server is running: ${appiumStatus}"
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
                echo "Consolidating reports from all frameworks..."
                script {
                    try {
                        bat """
                            REM Create consolidated report directory
                            if not exist consolidated-reports mkdir consolidated-reports

                            REM Copy individual framework reports
                            if exist java-selenium-automation\\target\\surefire-reports xcopy java-selenium-automation\\target\\surefire-reports consolidated-reports\\selenium\\ /E /I /Y 2>nul
                            if exist java-appium-automation\\target\\surefire-reports xcopy java-appium-automation\\target\\surefire-reports consolidated-reports\\appium\\ /E /I /Y 2>nul
                            if exist python-selenium-automation\\reports xcopy python-selenium-automation\\reports consolidated-reports\\python\\ /E /I /Y 2>nul

                            REM Generate master index
                            echo ^<html^>^<body^>^<h1^>Master Test Report^</h1^>^</body^>^</html^> > consolidated-reports\\index.html
                        """
                    } catch (Exception e) {
                        echo "Report consolidation completed with warnings: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Publish Results') {
            steps {
                echo "Publishing consolidated test results..."
                script {
                    try {
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'consolidated-reports',
                            reportFiles: 'index.html',
                            reportName: 'Master Test Report'
                        ])
                        
                        archiveArtifacts artifacts: 'consolidated-reports/**/*', allowEmptyArchive: true
                    } catch (Exception e) {
                        echo "Report publishing completed with warnings: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Notification') {
            steps {
                echo "Sending master pipeline notifications..."
                script {
                    try {
                        emailext(
                            subject: "TechAcademy Master Pipeline - ${currentBuild.currentResult}",
                            body: """
                                <h2>TechAcademy Master Pipeline Report</h2>
                                <p><strong>Build:</strong> ${env.BUILD_NUMBER}</p>
                                <p><strong>Status:</strong> ${currentBuild.currentResult}</p>
                                <p><strong>Execution Mode:</strong> ${params.EXECUTION_MODE}</p>
                                <p><strong>Java Selenium:</strong> ${params.RUN_JAVA_SELENIUM}</p>
                                <p><strong>Java Appium:</strong> ${params.RUN_JAVA_APPIUM}</p>
                                <p><strong>Python Selenium:</strong> ${params.RUN_PYTHON_SELENIUM}</p>
                                <p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>
                                <p><strong>Environment:</strong> ${params.ENVIRONMENT}</p>
                                <p><strong>Consolidated Reports:</strong> <a href="${env.BUILD_URL}Master_Test_Report/">View Master Report</a></p>
                            """,
                            mimeType: 'text/html',
                            to: 'ashokchandravanshi1988@gmail.com'
                        )
                    } catch (Exception e) {
                        echo "Notification completed with warnings: ${e.getMessage()}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Cleaning up master workspace..."
            script {
                try {
                    bat """
                        REM Clean up virtual environment
                        if exist master-venv rmdir /s /q master-venv 2>nul

                        REM Clean up temporary files  
                        if exist master-reports rmdir /s /q master-reports 2>nul
                    """
                } catch (Exception e) {
                    echo "Cleanup completed with warnings: ${e.getMessage()}"
                }
            }
            cleanWs()
        }
        success {
            echo "Master pipeline executed successfully!"
        }
        failure {
            echo "Master pipeline failed. Check individual framework logs."
        }
    }
}
