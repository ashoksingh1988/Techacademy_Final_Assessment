pipeline {
    agent any
    
    environment {
        MAVEN_HOME = tool 'Maven'
        JAVA_HOME = tool 'JDK11'
        PYTHON_VERSION = '3.9'
        PATH = "${MAVEN_HOME}/bin:${JAVA_HOME}/bin:${PATH}"
        APPIUM_SERVER = 'http://127.0.0.1:4723'
        SELENIUM_HUB = 'http://localhost:4444'
    }
    
    parameters {
        choice(
            name: 'EXECUTION_MODE',
            choices: ['sequential', 'parallel', 'selective'],
            description: 'How to execute the frameworks'
        )
        booleanParam(
            name: 'RUN_JAVA_SELENIUM',
            defaultValue: true,
            description: 'Execute Java Selenium Framework'
        )
        booleanParam(
            name: 'RUN_JAVA_APPIUM',
            defaultValue: true,
            description: 'Execute Java Appium Framework'
        )
        booleanParam(
            name: 'RUN_PYTHON_SELENIUM',
            defaultValue: true,
            description: 'Execute Python Selenium Framework'
        )
        choice(
            name: 'TEST_SUITE',
            choices: ['smoke', 'regression', 'full'],
            description: 'Test suite to execute across frameworks'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'staging', 'production'],
            description: 'Target environment for testing'
        )
    }
    
    stages {
        stage('Pre-Execution Setup') {
            steps {
                echo 'Setting up master execution environment...'
                script {
                    // Create master reports directory
                    sh 'mkdir -p master-reports/{java-selenium,java-appium,python-selenium}'
                    
                    // Check Appium server if mobile tests are enabled
                    if (params.RUN_JAVA_APPIUM) {
                        def appiumStatus = sh(
                            script: "curl -s ${APPIUM_SERVER}/status || echo 'Appium not running'",
                            returnStdout: true
                        ).trim()
                        
                        if (appiumStatus.contains('Appium not running')) {
                            error 'Appium server is not running. Please start Appium server for mobile tests.'
                        }
                    }
                    
                    // Setup Python environment if Python tests are enabled
                    if (params.RUN_PYTHON_SELENIUM) {
                        sh '''
                            python3 -m venv master-venv
                            source master-venv/bin/activate
                            pip install --upgrade pip
                        '''
                    }
                }
            }
        }
        
        stage('Framework Execution') {
            when {
                expression { params.EXECUTION_MODE == 'parallel' }
            }
            parallel {
                stage('Java Selenium Framework') {
                    when {
                        expression { params.RUN_JAVA_SELENIUM }
                    }
                    steps {
                        echo 'Executing Java Selenium Framework...'
                        script {
                            def suiteFile = params.TEST_SUITE == 'smoke' ? 'smoke-tests.xml' : 
                                          params.TEST_SUITE == 'regression' ? 'regression-tests.xml' : 
                                          'cross-browser-tests.xml'
                            
                            dir('java-selenium-automation') {
                                sh """
                                    mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/${suiteFile} \
                                                   -Dbrowser=chrome \
                                                   -Denvironment=${params.ENVIRONMENT} \
                                                   -Dheadless=true
                                """
                                
                                // Copy reports to master directory
                                sh 'cp -r reports/* ../master-reports/java-selenium/ || true'
                                sh 'cp -r target/surefire-reports/* ../master-reports/java-selenium/ || true'
                            }
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'java-selenium-automation/reports/**/*', allowEmptyArchive: true
                        }
                    }
                }
                
                stage('Java Appium Framework') {
                    when {
                        expression { params.RUN_JAVA_APPIUM }
                    }
                    steps {
                        echo 'Executing Java Appium Framework...'
                        script {
                            def suiteFile = params.TEST_SUITE == 'smoke' ? 'smoke-tests.xml' : 'regression-tests.xml'
                            
                            dir('java-appium-automation') {
                                sh """
                                    mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/suites/${suiteFile} \
                                                   -Ddevice.name=Android_Device \
                                                   -Dplatform.version=11 \
                                                   -Dappium.server=${APPIUM_SERVER}
                                """
                                
                                // Copy reports to master directory
                                sh 'cp -r reports/* ../master-reports/java-appium/ || true'
                                sh 'cp -r target/surefire-reports/* ../master-reports/java-appium/ || true'
                            }
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'java-appium-automation/reports/**/*', allowEmptyArchive: true
                        }
                    }
                }
                
                stage('Python Selenium Framework') {
                    when {
                        expression { params.RUN_PYTHON_SELENIUM }
                    }
                    steps {
                        echo 'Executing Python Selenium Framework...'
                        script {
                            def testPath = params.TEST_SUITE == 'smoke' ? 'tests/smoke/' : 
                                         params.TEST_SUITE == 'regression' ? 'tests/regression/' : 'tests/'
                            
                            dir('python-selenium-automation') {
                                sh '''
                                    source ../master-venv/bin/activate
                                    pip install -r requirements.txt
                                    pip install pytest-html pytest-xdist
                                    
                                    export BROWSER=chrome
                                    export HEADLESS=true
                                    export ENVIRONMENT=${ENVIRONMENT}
                                    
                                    pytest ${testPath} -v \
                                        --html=reports/python-test-report.html \
                                        --self-contained-html \
                                        --junit-xml=reports/python-tests.xml
                                '''
                                
                                // Copy reports to master directory
                                sh 'cp -r reports/* ../master-reports/python-selenium/ || true'
                            }
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: 'python-selenium-automation/reports/**/*', allowEmptyArchive: true
                        }
                    }
                }
            }
        }
        
        stage('Sequential Execution') {
            when {
                expression { params.EXECUTION_MODE == 'sequential' }
            }
            stages {
                stage('1. Java Selenium') {
                    when {
                        expression { params.RUN_JAVA_SELENIUM }
                    }
                    steps {
                        echo 'Step 1: Executing Java Selenium Framework...'
                        build job: 'java-selenium-pipeline', 
                              parameters: [
                                  string(name: 'TEST_SUITE', value: params.TEST_SUITE),
                                  string(name: 'ENVIRONMENT', value: params.ENVIRONMENT)
                              ]
                    }
                }
                
                stage('2. Java Appium') {
                    when {
                        expression { params.RUN_JAVA_APPIUM }
                    }
                    steps {
                        echo 'Step 2: Executing Java Appium Framework...'
                        build job: 'java-appium-pipeline', 
                              parameters: [
                                  string(name: 'TEST_SUITE', value: params.TEST_SUITE),
                                  string(name: 'DEVICE_NAME', value: 'Android_Device')
                              ]
                    }
                }
                
                stage('3. Python Selenium') {
                    when {
                        expression { params.RUN_PYTHON_SELENIUM }
                    }
                    steps {
                        echo 'Step 3: Executing Python Selenium Framework...'
                        build job: 'python-selenium-pipeline', 
                              parameters: [
                                  string(name: 'TEST_SUITE', value: params.TEST_SUITE),
                                  string(name: 'ENVIRONMENT', value: params.ENVIRONMENT)
                              ]
                    }
                }
            }
        }
        
        stage('Selective Execution') {
            when {
                expression { params.EXECUTION_MODE == 'selective' }
            }
            steps {
                script {
                    def jobs = []
                    
                    if (params.RUN_JAVA_SELENIUM) {
                        jobs.add([
                            job: 'java-selenium-pipeline',
                            parameters: [
                                string(name: 'TEST_SUITE', value: params.TEST_SUITE),
                                string(name: 'ENVIRONMENT', value: params.ENVIRONMENT)
                            ]
                        ])
                    }
                    
                    if (params.RUN_JAVA_APPIUM) {
                        jobs.add([
                            job: 'java-appium-pipeline',
                            parameters: [
                                string(name: 'TEST_SUITE', value: params.TEST_SUITE)
                            ]
                        ])
                    }
                    
                    if (params.RUN_PYTHON_SELENIUM) {
                        jobs.add([
                            job: 'python-selenium-pipeline',
                            parameters: [
                                string(name: 'TEST_SUITE', value: params.TEST_SUITE),
                                string(name: 'ENVIRONMENT', value: params.ENVIRONMENT)
                            ]
                        ])
                    }
                    
                    // Execute selected jobs
                    jobs.each { jobConfig ->
                        build job: jobConfig.job, parameters: jobConfig.parameters
                    }
                }
            }
        }
        
        stage('Consolidate Reports') {
            steps {
                echo 'Consolidating reports from all frameworks...'
                script {
                    sh '''
                        # Create consolidated report directory
                        mkdir -p consolidated-reports
                        
                        # Copy all framework reports
                        cp -r master-reports/* consolidated-reports/ || true
                        
                        # Generate master summary report
                        cat > consolidated-reports/master-summary.html << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <title>Master Test Execution Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .framework { border: 1px solid #ddd; margin: 10px 0; padding: 15px; }
        .header { background-color: #f5f5f5; padding: 10px; font-weight: bold; }
        .pass { color: green; }
        .fail { color: red; }
    </style>
</head>
<body>
    <h1>Master Test Execution Report</h1>
    <p><strong>Execution Date:</strong> $(date)</p>
    <p><strong>Environment:</strong> ${ENVIRONMENT}</p>
    <p><strong>Test Suite:</strong> ${TEST_SUITE}</p>
    
    <div class="framework">
        <div class="header">Java Selenium Framework</div>
        <p>Status: Executed</p>
        <p>Reports: <a href="java-selenium/">View Reports</a></p>
    </div>
    
    <div class="framework">
        <div class="header">Java Appium Framework</div>
        <p>Status: Executed</p>
        <p>Reports: <a href="java-appium/">View Reports</a></p>
    </div>
    
    <div class="framework">
        <div class="header">Python Selenium Framework</div>
        <p>Status: Executed</p>
        <p>Reports: <a href="python-selenium/">View Reports</a></p>
    </div>
</body>
</html>
EOF
                    '''
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'consolidated-reports',
                        reportFiles: 'master-summary.html',
                        reportName: 'Master Test Report',
                        reportTitles: 'Consolidated Test Results'
                    ])
                    
                    archiveArtifacts artifacts: 'consolidated-reports/**/*', allowEmptyArchive: true
                }
            }
        }
        
        stage('Quality Gates & Notifications') {
            steps {
                echo 'Checking overall quality gates...'
                script {
                    // Send comprehensive notification
                    emailext (
                        subject: "Master Test Execution - ${currentBuild.currentResult}",
                        body: """
                            <h2>Master Test Execution Report</h2>
                            <p><strong>Build:</strong> ${env.BUILD_NUMBER}</p>
                            <p><strong>Status:</strong> ${currentBuild.currentResult}</p>
                            <p><strong>Execution Mode:</strong> ${params.EXECUTION_MODE}</p>
                            <p><strong>Test Suite:</strong> ${params.TEST_SUITE}</p>
                            <p><strong>Environment:</strong> ${params.ENVIRONMENT}</p>
                            
                            <h3>Frameworks Executed:</h3>
                            <ul>
                                ${params.RUN_JAVA_SELENIUM ? '<li>✅ Java Selenium Framework</li>' : '<li>⏭️ Java Selenium Framework (Skipped)</li>'}
                                ${params.RUN_JAVA_APPIUM ? '<li>✅ Java Appium Framework</li>' : '<li>⏭️ Java Appium Framework (Skipped)</li>'}
                                ${params.RUN_PYTHON_SELENIUM ? '<li>✅ Python Selenium Framework</li>' : '<li>⏭️ Python Selenium Framework (Skipped)</li>'}
                            </ul>
                            
                            <p><strong>Consolidated Reports:</strong> <a href="${env.BUILD_URL}Master_Test_Report/">View Master Report</a></p>
                        """,
                        mimeType: 'text/html',
                        to: 'asim.kumar.singh@company.com'
                    )
                }
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up master workspace...'
            sh '''
                # Clean up virtual environment
                rm -rf master-venv/ || true
                
                # Clean up temporary files
                rm -rf master-reports/ || true
            '''
            cleanWs()
        }
        success {
            echo 'Master pipeline executed successfully!'
        }
        failure {
            echo 'Master pipeline failed. Check individual framework logs.'
        }
    }
}
