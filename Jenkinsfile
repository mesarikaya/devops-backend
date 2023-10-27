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
                echo "---------- build started ----------"
                sh 'mvn clean deploy -Dmaven.test.skip=true'
                echo "---------- build completed ----------"
            }
        }

        stage("test"){
            steps {
                echo "---------- unit test started ----------"
                sh 'mvn surefire-report:report'
                echo "---------- unit test completed ----------"
            }
        }

        stage('SonarQube Analysis') {
            environment{
                scannerHome = tool 'devops-backend-sonar-scanner'
            }

            steps{
                withSonarQubeEnv('devops-backend-sonarqube-server') {
                    sh "${scannerHome}/bin/sonar-scanner"
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}