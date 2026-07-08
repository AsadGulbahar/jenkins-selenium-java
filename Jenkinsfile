pipeline {
    agent any
    // Uncomment if your Jenkins global tools are configured.
    // tools {
    //     jdk 'JDK17'
    //     maven 'Maven-3.9'
    // }
    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
    }
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox'],
                description: 'Browser to run through Selenium container')
        choice(name: 'TEST_GROUP', choices: ['all', 'smoke', 'regression'],
                description: 'TestNG group selection')
        string(name: 'BASE_URL', defaultValue: 'https://www.saucedemo.com/',
                description: 'Application under test')
    }
    environment {
        MAVEN_CLI_OPTS = '--batch-mode --errors --fail-at-end --show-version --no-transfer-progress'
        SELENIUM_IMAGE_CHROME = 'selenium/standalone-chrome:4.44.0-20260505'
        SELENIUM_IMAGE_FIREFOX = 'selenium/standalone-firefox:4.44.0-20260505'
        SELENIUM_REMOTE_URL = 'http://localhost:4444'
        SAUCE_USERNAME = credentials('saucedemo-username')
        SAUCE_PASSWORD = credentials('saucedemo-password')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Validate Project') {
            steps {
                sh 'mvn $MAVEN_CLI_OPTS clean test-compile -DskipTests'
            }
        }
        stage('Start Selenium') {
            steps {
                script {
                    def image = params.BROWSER == 'firefox'
                            ? env.SELENIUM_IMAGE_FIREFOX
                            : env.SELENIUM_IMAGE_CHROME
                    sh """
                        docker rm -f selenium-browser-${BUILD_NUMBER} || true
                        docker run --detach \\
                            --name selenium-browser-${BUILD_NUMBER} \\
                            --shm-size=2g \\
                            --publish 4444:4444 \\
                            --publish 7900:7900 \\
                            --env SE_VNC_NO_PASSWORD=1 \\
                            ${image}
                    """
                }
            }
        }
        stage('Wait for Selenium Grid') {
            steps {
                sh """
                    for attempt in \$(seq 1 30); do
                        if curl --fail --silent "${SELENIUM_REMOTE_URL}/status" | grep -q '"ready": true'; then
                            echo "Selenium Grid is ready."
                            exit 0
                        fi
                        echo "Waiting for Selenium Grid: \${attempt}/30"
                        sleep 2
                    done
                    echo "Selenium Grid did not become ready."
                    docker logs selenium-browser-${BUILD_NUMBER} || true
                    exit 1
                """
            }
        }
        stage('Run Selenium Tests') {
            steps {
                script {
                    def groupArg = params.TEST_GROUP == 'all'
                            ? ''
                            : "-Dgroups=${params.TEST_GROUP}"
                    sh """
                        mvn ${MAVEN_CLI_OPTS} clean test \\
                            -DbaseUrl='${params.BASE_URL}' \\
                            -Dbrowser='${params.BROWSER}' \\
                            -DremoteUrl='${SELENIUM_REMOTE_URL}' \\
                            -Dusername='${SAUCE_USERNAME}' \\
                            -Dpassword='${SAUCE_PASSWORD}' \\
                            ${groupArg}
                    """
                }
            }
        }
    }
    post {
        always {
            sh 'docker logs selenium-browser-${BUILD_NUMBER} > selenium-container.log 2>&1 || true'
            junit allowEmptyResults: true,
                    testResults: 'target/surefire-reports/TEST-*.xml'
            archiveArtifacts allowEmptyArchive: true,
                    artifacts: 'target/surefire-reports/**,target/screenshots/**,test-output/**,selenium-container.log'
            sh 'docker rm -f selenium-browser-${BUILD_NUMBER} || true'
        }
    }
}
