pipeline {
    agent any
    tools {
        maven 'Maven' // Must match the name in Global Tool Configuration
        jdk 'JDK'     // Must match the name in Global Tool Configuration
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/malleswar-reddy/WebFluxTodo.git', branch: 'devlop'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'  // build without tests first
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'                 // run tests
            }
        }

        stage('Package') {
            steps {
                sh './gradlew bootJar'              // optional: create JAR
            }
        }
    }

    post {
        success {
            echo 'Build and tests completed successfully.'
        }
        failure {
            echo 'Build or tests failed.'
        }
    }
}
