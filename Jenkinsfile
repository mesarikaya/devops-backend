def registry = 'https://devopsbackend.jfrog.io'
def imageName = 'devopsbackend.jfrog.io/devops-backend-docker-local/devops-backend'
def version   = '0.0.1'
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
                timeout(time: 2, unit: 'MINUTES') {
                    // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
                    // true = set pipeline to UNSTABLE, false = don't
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage("Jar Publish") {
            steps {
                script {
                        echo '<--------------- Jar Publish Started --------------->'
                         def server = Artifactory.newServer url:registry+"/artifactory" ,  credentialsId:"jfrog-artifact-credentials"
                         def properties = "buildid=${env.BUILD_ID},commitid=${GIT_COMMIT}";
                         def uploadSpec = """{
                              "files": [
                                {
                                  "pattern": "jarstaging/(*)",
                                  "target": "libs-release-local/{1}",
                                  "flat": "false",
                                  "props" : "${properties}",
                                  "exclusions": [ "*.sha1", "*.md5"]
                                }
                             ]
                         }"""
                         def buildInfo = server.upload(uploadSpec)
                         buildInfo.env.collect()
                         server.publishBuildInfo(buildInfo)
                         echo '<--------------- Jar Publish Ended --------------->'
                }
            }
        }

        stage("Docker Build") {
          steps {
            script {
               echo '<--------------- Docker Build Started --------------->'
               app = docker.build(imageName+":"+version)
               echo '<--------------- Docker Build Ends --------------->'
            }
          }
        }

        stage ("Docker Publish"){
            steps {
                script {
                   echo '<--------------- Docker Publish Started --------------->'
                    docker.withRegistry(registry, 'jfrog-artifact-credentials'){
                        app.push()
                    }
                   echo '<--------------- Docker Publish Ended --------------->'
                }
            }
        }

    }
}