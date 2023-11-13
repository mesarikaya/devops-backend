def registry = 'https://devopsbackend.jfrog.io'
def imageName = 'devopsbackend.jfrog.io/devops-backend-docker-local/devops-backend'
def version   = '0.0.1'
pipeline {

    parameters {
      string(name: 'DOCKER_IMAGE_VERSION', defaultValue: "0.0.1", description: 'Docker image version or tag')
      string(name: 'BUILD_NUMBER', defaultValue: "${BUILD_NUMBER}", description: 'Build number')
    }

    agent {
        node {
            label 'maven'
        }
    }

    environment {
        PATH = "/opt/apache-maven-3.9.5/bin:$PATH"
        CODEARTIFACT_AUTH_TOKEN = credentials('CODEARTIFACT_AUTH_TOKEN')
        AWS_CREDENTIALS = credentials('AWS_CREDENTIALS')
        AWS_USER = credentials('AWS_USER')
        AWS_REGION = 'eu-west-1'
        AWS_CODEARTIFACTORY_REPO = credentials('AWS_CODEARTIFACTORY_REPO')
        DOCKER_IMAGE_NAME = 'devops-backend'
        ECR_REPO_NAME = 'dev-devops-container-repository'
        DOCKER_IMAGE_TAG = "${DOCKER_IMAGE_NAME}-${DOCKER_IMAGE_VERSION}-${BUILD_NUMBER}"
        NAMESPACE_NAME = 'devops-infra'
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

        /*stage('SonarQube Analysis') {
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
                timeout(time: 30, unit: 'MINUTES') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    waitForQualityGate abortPipeline: true
                }
            }
        }*/

        stage('Publish JAR to AWS CodeArtifact') {
            steps {
                script {
                    // Publish the JAR to AWS CodeArtifact
                    sh "mvn deploy -DrepositoryId=aws-devops-maven-artifactory-id -s settings.xml"
                }
            }
        }

        stage("Docker Build and Push to AWS ECR") {
            steps {
                script {
                    echo '<--------------- Docker Build and Push to AWS ECR Started --------------->'
                    withCredentials([usernamePassword(credentialsId: 'AWS_CREDENTIALS', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        // Retrieve an authentication token and authenticate Docker client to the registry.
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                        echo '<--------------- Docker Build Started --------------->'
                        // Build Docker image
                        sh "docker build -t ${DOCKER_IMAGE_TAG} ."
                        echo '<--------------- Docker Build Ended --------------->'

                        echo '<--------------- Docker Push to AWS ECR Started --------------->'
                        // Tag the Docker image for AWS ECR
                        sh "docker tag ${DOCKER_IMAGE_TAG} ${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${DOCKER_IMAGE_TAG}"

                        // Push the Docker image to AWS ECR
                        sh "docker push ${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${DOCKER_IMAGE_TAG}"
                        echo '<--------------- Docker Push to AWS ECR Ended --------------->'
                    }
                    echo '<--------------- Docker Build and Push to AWS ECR Ended --------------->'
                }
            }
        }

        stage("Create Namespace") {
            steps {
                script {
                    def namespaceYamlPath = 'kubernetes/namespace.yml'

                    // Replace placeholders in the YAML file with actual values
                    sh "envsubst < ${namespaceYamlPath} > ${namespaceYamlPath}.temp"

                    // Check if the namespace already exists
                    def namespaceExists = sh(script: "kubectl get namespace ${NAMESPACE_NAME}", returnStatus: true) == 0

                    if (!namespaceExists) {
                        // Apply the modified namespace YAML
                        sh "kubectl apply -f ${namespaceYamlPath}.temp"
                        echo "Namespace created or updated from ${namespaceYamlPath}."
                    } else {
                        echo "Namespace ${NAMESPACE_NAME} already exists."
                    }
                }
            }
        }

        stage("Create Deployment on Kubernetes") {
            steps {
                script {
                    // Specify the location containing the Kubernetes deployment manifests
                    def deploymentYamlPath = 'kubernetes/deployment.yml'

                    // Dynamic replacements in the Kubernetes manifest
                    def awsAccountId = sh(script: 'aws sts get-caller-identity --query "Account" --output text', returnStdout: true).trim()
                    def imageTag = "${AWS_USER}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO_NAME}:${DOCKER_IMAGE_VERSION}"

                    // Replace placeholders in the Deployment YAML
                    sh "sed -i 's|<AWS_USER>|${AWS_USER}|g' ${deploymentYamlPath}"
                    sh "sed -i 's|<AWS_REGION>|${AWS_REGION}|g' ${deploymentYamlPath}"
                    sh "sed -i 's|<ECR_REPO_NAME>|${ECR_REPO_NAME}|g' ${deploymentYamlPath}"
                    sh "sed -i 's|<DOCKER_IMAGE_TAG>|${DOCKER_IMAGE_TAG}|g' ${deploymentYamlPath}"

                    // Apply the Kubernetes manifests using kubectl
                    sh "kubectl apply -f ${deploymentYamlPath}"
                }
            }
        }
    }
    stage("Create Service on Kubernetes") {
        steps {
            script {
                // Specify the location containing the Kubernetes service manifests
                def serviceYamlPath = 'kubernetes/service.yml'

                // Replace placeholders in the Deployment YAML
                sh "sed -i 's|<NAMESPACE_NAME>|${NAMESPACE_NAME}|g' ${serviceYamlPath}"

                // Apply the Kubernetes manifests using kubectl
                sh "kubectl apply -f ${serviceYamlPath}"
            }
        }
    }
}