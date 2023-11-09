def registry = 'https://devopsbackend.jfrog.io'
def imageName = 'devopsbackend.jfrog.io/devops-backend-docker-local/devops-backend'
def version   = '0.0.1'
pipeline {

    parameters {
      string(name: 'DOCKER_IMAGE_VERSION', defaultValue: "${BUILD_NUMBER}", description: 'Docker image version or tag')
    }

    agent {
        node {
            label 'maven'
        }
    }

    environment {
        PATH = "/opt/apache-maven-3.9.5/bin:$PATH"
        CODEARTIFACT_AUTH_TOKEN = credentials('CODEARTIFACT_AUTH_TOKEN')
        AWS_USER = credentials('AWS_USER')
        AWS_REGION = 'eu-west-1'
        AWS_CODEARTIFACTORY_REPO = credentials('AWS_CODEARTIFACTORY_REPO')
        DOCKER_IMAGE_NAME = 'devops-backend'
        DOCKER_IMAGE_TAG = "${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION}"
    }

    stages {
        stage("build") {
            steps {
                echo "---------- build started ----------"
                sh 'mvn clean deploy -Dmaven.test.skip=true -s settings.xml'
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
                timeout(time: 2, unit: 'MINUTES') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Publish JAR to AWS CodeArtifact') {
            steps {
                script {
                    // Publish the JAR to AWS CodeArtifact
                    sh "${MAVEN_HOME}/bin/mvn deploy -DrepositoryId=aws-devops-maven-artifactory-id -s settings.xml"
                }
            }
        }

        /*stage("Docker Build") {
          steps {
            script {
               echo '<--------------- Docker Build Started --------------->'
               app = docker.build(imageName+":"+version)
               echo '<--------------- Docker Build Ends --------------->'
            }
          }
        }*/

        stage("Docker Build") {
                  steps {
                    script {
                       echo '<--------------- Docker Build Started --------------->'
                       app = docker.build(DOCKER_IMAGE_TAG)
                       echo '<--------------- Docker Build Ends --------------->'
                    }
                  }
        }

        /*stage('Build Docker Image') {
                    steps {
                        script {
                            // Build a Docker image with your application and Dockerfile, and tag it with a unique version
                            sh "docker build -t ${DOCKER_IMAGE_TAG} ."
                        }
                    }
                }*/

        /*stage ("Docker Publish"){
            steps {
                script {
                   echo '<--------------- Docker Publish Started --------------->'
                    docker.withRegistry(registry, 'jfrog-artifact-credentials'){
                        app.push()
                    }
                   echo '<--------------- Docker Publish Ended --------------->'
                }
            }
        }*/

    }
}