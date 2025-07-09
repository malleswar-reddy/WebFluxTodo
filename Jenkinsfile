pipeline {
    agent any

    tools {
        jdk 'JDK' // Match name from Global Tool Configuration
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
                        git url: 'https://github.com/malleswar-reddy/WebFluxTodo.git', branch: "${env.BRANCH_NAME}"
                    } catch (Exception e) {
                        error "Failed to checkout branch '${env.BRANCH_NAME}': ${e.message}"
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

        stage('Deploy') {
            when {
                branch 'dev'
            }
            steps {
                echo "Deploying dev branch build..."
                sh '''
                    echo "Simulated deployment to dev server"
                    # Example: scp target to server or run docker/k8s commands
                '''
            }
        }
    }

    post {
        always {
            // Publish test and coverage reports
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'UserManagement/build/reports/tests/test',
                reportFiles: 'index.html',
                reportName: 'UserManagement Test Report'
            ])
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'CommonService/build/reports/jacoco/test/html',
                reportFiles: 'index.html',
                reportName: 'CommonService JaCoCo Coverage Report'
            ])
            publishHTML(target: [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'UserManagement/build/reports/tests/test',
                reportFiles: 'index.html',
                reportName: 'UserManagement JaCoCo Coverage Report'
            ])

            script {
                def getJacocoCoverage = { reportPath ->
                    if (fileExists(reportPath)) {
                        def xml = readFile(reportPath)
                        def matcher = xml =~ '<counter type="LINE" missed="\\d+" covered="(\\d+)"'
                        if (matcher.find()) {
                            def covered = matcher[0][1].toInteger()
                            def totalMatch = xml =~ '<counter type="LINE" missed="(\\d+)" covered="(\\d+)"'
                            if (totalMatch.find()) {
                                def missed = totalMatch[0][1].toInteger()
                                def total = missed + covered
                                return String.format("%.2f", (covered / total) * 100)
                            }
                        }
                    }
                    return 'N/A'
                }

                def commonCoverage = getJacocoCoverage('CommonService/build/reports/jacoco/test/jacocoTestReport.xml')
                def userCoverage = getJacocoCoverage('UserManagement/build/reports/jacoco/test/jacocoTestReport.xml')

                try {
                    emailext(
                        subject: "Jenkins Build ${currentBuild.currentResult}: Job ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        body: """
                            <h2>Build Status: ${currentBuild.currentResult}</h2>
                            <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                            <p><strong>Branch:</strong> ${env.BRANCH_NAME}</p>
                            <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                            <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                            <h3>JaCoCo Test Coverage</h3>
                            <p><strong>CommonService:</strong> Line Coverage: ${commonCoverage}%</p>
                            <p><strong>UserManagement:</strong> Line Coverage: ${userCoverage}%</p>
                            <p><a href="${env.BUILD_URL}artifact/CommonService/build/reports/jacoco/test/html/index.html">CommonService JaCoCo Report</a></p>
                            <p><a href="${env.BUILD_URL}artifact/UserManagement/build/reports/tests/test/index.html">UserManagement JaCoCo Report</a></p>
                            <p><a href="${env.BUILD_URL}testReport">View Test Reports</a></p>
                            <p><a href="${env.BUILD_URL}console">View Console Output</a></p>
                        """,
                        mimeType: 'text/html',
                        to: 'malleswar.mca@gmail.com', cc: 'malleswar.mca@gmail.com'
                    )
                } catch (Exception e) {
                    echo "Failed to send email: ${e.message}"
                    currentBuild.result = 'UNSTABLE'
                }
            }
        }
    }
}
