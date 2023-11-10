def registry = 'https://devopsbackend.jfrog.io'
def imageName = 'devopsbackend.jfrog.io/devops-backend-docker-local/devops-backend'
def version   = '0.0.1'
pipeline {

    parameters {
      string(name: 'DOCKER_IMAGE_VERSION', defaultValue: "0.0.1", description: 'Docker image version or tag')
      string(name: 'BUILD_NUMBER', defaultValue: "${BUILD_NUMBER}", description: 'Build numberg')
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
        ECR_REPO_NAME = 'dev-devops-container-repository'
        DOCKER_IMAGE_TAG = "${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_VERSION}-${BUILD_NUMBER}"
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
                    sh "mvn deploy -DrepositoryId=aws-devops-maven-artifactory-id -s settings.xml"
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

        stage("Docker Push to AWS ECR") {
            steps {
                script {
                    echo '<--------------- Docker Push to AWS ECR Started --------------->'
                    withAWS(credentials: 'AWS_ECR_CREDENTIALS', region: ${AWS_REGION}) {
                        // Retrieve an authentication token and authenticate Docker client to the registry.
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                        // Tag the Docker image for AWS ECR
                        sh "docker tag ${DOCKER_IMAGE_TAG} ${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${DOCKER_IMAGE_VERSION}"

                        // Push the Docker image to AWS ECR
                        sh "docker push ${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${DOCKER_IMAGE_VERSION}"
                    }
                    echo '<--------------- Docker Push to AWS ECR Ended --------------->'
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