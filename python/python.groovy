pipelineJob("Flask Image") {

  definition {
           cps {
             script('''
             def image
                 pipeline {
                    agent any

                    environment {
                        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
                        DOCKER_IMAGE = 'dolevtzvieltovim/flask-docker-app'
                    }
                    stages {
                        stage('Checkout Code') {
                            steps {
                                git url: 'https://github.com/Dolev-Tz/Home-assignment.git', branch: 'main'
                            }
                        }

                        stage('Build Docker Image') {
                            steps {
                                script {
                                    image = docker.build(env.DOCKER_IMAGE, '-f python/Dockerfile flask')
                                }
                            }
                        }

                        stage('Push to Docker Hub') {
                            steps {
                                script {
                                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {
                                        image.push('latest')
                                    }
                                }
                            }
                        }
                      }
                   }
              '''.stripIndent())
       sandbox()
          }
      }
  }
