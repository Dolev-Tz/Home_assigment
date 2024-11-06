pipelineJob('Nginx Image') {
    definition {
        cps {
            script(''' 
            def image
                pipeline {
                    agent any

                    environment {
                        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials-id')
                        DOCKER_IMAGE = 'dolevtzvieltovim/nginx-proxy'
                    }

                    stages {
                        stage('Checkout Code') {
                            steps {
                                git url: 'https://github.com/Dolev-Tz/Home_assignment.git', branch: 'main'
                            }
                        }

                        stage('Build Docker Image') {
                            steps {
                                script {
                                    echo 'Building Docker image...'
                                    image = docker.build(env.DOCKER_IMAGE, '-f nginx/Dockerfile nginx')
                                }
                            }
                        }
                        
                        stage('Push to Docker Hub') {
                            steps {
                                script {
                                    echo 'Pushing image to Docker Hub...'
                                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS) {
                                        image.push('latest')
                                    }
                                }
                            }
                        }
                    }
                }
            ''')
            sandbox()
        }
    }
}
