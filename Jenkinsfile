pipeline {
    agent any
    tools {
        jdk 'JDK' // Must match the name in Global Tool Configuration
    }

    environment {
        GRADLE_OPTS = '-Dorg.gradle.jvmargs="-Xmx2g -XX:MaxMetaspaceSize=512m"'
        JAVA_HOME = tool 'JDK'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    try {
                        git url: 'https://github.com/malleswar-reddy/WebFluxTodo.git', branch: 'develop' // Fixed typo
                    } catch (Exception e) {
                        error "Failed to checkout branch 'develop': ${e.message}"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test --no-daemon'
            }
        }

        stage('Test') {
            parallel {
                stage('CommonService Tests') {
                    steps {
                        sh './gradlew :CommonService:test --no-daemon'
                    }
                }
                stage('UserManagement Tests') {
                    steps {
                        sh './gradlew :UserManagement:test --no-daemon'
                    }
                }
            }
        }

        stage('Coverage Report') {
            steps {
                sh './gradlew jacocoTestReport --no-daemon'
                jacoco(execPattern: '**/build/jacoco/test.exec')
            }
        }

        stage('Package') {
            steps {
                sh './gradlew bootJar --no-daemon'
//                 archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint Wrote: true
            }
        }
    }

    /* post {
        always {
            cleanWs() // Clean workspace after build
        }
        success {
            echo 'Build, tests, and packaging completed successfully.'
            emailext(
                subject: "SUCCESS: WebFluxTodo Build #${env.BUILD_NUMBER}",
                body: "The build and tests for WebFluxTodo succeeded.\n\nBuild URL: ${env.BUILD_URL}\nCoverage Report: ${env.BUILD_URL}jacoco/",
                to: 'team@example.com', // Replace with actual team email
                attachLog: true
            )
        }
        failure {
            echo 'Build, tests, or packaging failed.'
            emailext(
                subject: "FAILURE: WebFluxTodo Build #${env.BUILD_NUMBER}",
                body: "The build or tests for WebFluxTodo failed.\n\nBuild URL: ${env.BUILD_URL}\nCheck the logs for details.",
                to: 'team@example.com', // Replace with actual team email
                attachLog: true
            )
        }
    } */
}