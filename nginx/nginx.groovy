pipelineJob('nginx-proxy-job') {
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
                        stage('Prepare Configuration') {
                            steps {
                                script {
                                    echo 'Creating nginx.conf and Dockerfile...'

                                    // Create nginx.conf
                                    writeFile file: 'nginx.conf', text: """
                                    upstream flask_app {
                                        server flask_app_expose1:5000;
                                    }
                                    server {
                                        listen 80;
                                    
                                        location / {
                                            proxy_pass http://flask_app;
                                            proxy_set_header X-Forwarded-For \$remote_addr;  # Inject source IP
                                            proxy_set_header Host \$host;
                                            proxy_set_header X-Real-IP \$remote_addr;
                                        }
                                    }
                                    """

                                    // Create Dockerfile
                                    writeFile file: 'Dockerfile', text: """
                                    FROM nginx:latest

                                    COPY nginx.conf /etc/nginx/conf.d/default.conf

                                    EXPOSE 80
                                    """
                                }
                            }
                        }

                        stage('Build Docker Image') {
                            steps {
                                script {
                                    echo 'Building Docker image...'
                                    image = docker.build(env.DOCKER_IMAGE, '-f nginx/Dockerfile nginx)
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
            ''')
            sandbox()
        }
    }
}
