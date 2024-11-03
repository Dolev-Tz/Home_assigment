pipelineJob('nginx-proxy-job') {
    description('Job to build and push Nginx proxy for Flask app.')

    definition {
        cps {
            script("""
                pipeline {
                    agent any

                    environment {
                        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
                        DOCKER_IMAGE = 'dolevtzvieltovim/nginx-proxy'
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
                                    image = docker.build(env.DOCKER_IMAGE)
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
            """)
            sandbox()
        }
    }
}
