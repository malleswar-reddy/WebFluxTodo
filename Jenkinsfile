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
                        git url: 'https://github.com/malleswar-reddy/WebFluxTodo.git', branch: 'devlop'
                    } catch (Exception e) {
                        error "Failed to checkout branch 'devlop': ${e.message}"
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
            }
        }

        stage('Package') {
            steps {
                sh './gradlew bootJar --no-daemon'
                archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            }
        }
    }

    post {
        always {
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'build/reports/jacoco',
                reportFiles: 'index.html',
                reportName: 'JaCoCo Coverage Report'
            ])
        }
    }
}
