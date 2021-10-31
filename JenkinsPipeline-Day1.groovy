pipeline {
    environment {
        registry = "starlord93/final"
    }
    tools {
        maven 'M3'
    }
    agent any
    stages {
        stage('SCM') {
            steps {
                git 'https://github.com/sridharsam/Final_Project_CI-CD.git'
            }
        }
        stage('Maven Build') {
            steps {
                sh 'mvn clean package'
                sh 'mv target/*.war target/webapp.war'
            }
        }
        stage('Docker Build') {
            steps {
                sh "docker build . -t $registry:$BUILD_NUMBER"
            }
        }
        stage('DockerHub Push') {
            steps {
                withCredentials([string(credentialsId: 'DockerPwd', variable: 'PWD')]) {
                    sh "docker login -u starlord93 -p $PWD"
                }
                sh "docker push $registry:$BUILD_NUMBER"
            }
        }
        stage('Remove Unused docker image & Running Container') {
            steps{
                sh "docker rmi $registry:$BUILD_NUMBER"
                sh "docker stop tomcat_test"
                sh "docker rm -f tomcat_test"
            }
        }
        stage('Docker Deployment') {
            steps {
                sh "docker run -d -p 8090:8080 --name tomcat_test $registry:$BUILD_NUMBER"
            }
        }
    }
}
