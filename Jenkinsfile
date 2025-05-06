pipeline {
    agent any

    environment {
        JAVA_HOME = tool 'JDK_17'   // make sure JDK_17 is configured in Jenkins tools
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
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
