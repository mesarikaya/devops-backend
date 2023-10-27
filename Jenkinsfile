pipeline {
    agent {
        node {
            label 'maven'
        }
    }

    environment {
        PATH = "/opt/apache-maven-3.9.5/bin:$PATH"
    }

    stages {
        stage("build") {
            steps {
                sh 'mvn clean deploy'
            }
        }

        stage('SonarQube Analysis') {
            environment{
                scannerHome = tool 'devops-backend-sonar-scanner'
            }

            steps{
                withSonarQubeEnv('devops-backend-sonarqube-server') {
                    sh "$scannerHome"/bin/sonar-scanner
                }
            }
        }
    }
}