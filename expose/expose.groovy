pipelineJob('Expose Images') {
    definition {
        cps {
            script('''
            pipeline {
                agent any

                environment {
                    FLASK_IMAGE = 'dolevtzvieltovim/flask-docker-app'
                    NGINX_IMAGE = 'dolevtzvieltovim/nginx-proxy'
                    NGINX_PORT = '80'
                    LOCAL_PORT = '8081'
                }

                stages {
                    stage('Run Containers') {
                        steps {
                            script {
                                echo 'Running Flask and Nginx containers'

                                bat 'Docker network create app_network'
                                // Start Flask app container
                                bat "docker run -d -p 5000:5000 --network app_network --name flask_app_expose1 -v /var/run/docker.sock:/var/run/docker.sock ${FLASK_IMAGE}"
                                
                                // Start Nginx proxy container, mapping NGINX_PORT to LOCAL_PORT
                                bat "docker run -d --network app_network --name nginx_proxy_expose1 -p ${LOCAL_PORT}:${NGINX_PORT} ${NGINX_IMAGE}"
                            }
                        }
                    }

                    stage('Verify Setup') {
                        steps {
                            script {
                                echo 'Verifying that Nginx is proxying requests correctly'

                                def curlCommand = "curl -s -o NUL http://localhost:${LOCAL_PORT}"
                                
                                def responseCode = bat(script: "echo %ERRORLEVEL%", returnStdout: true).trim()

                                // Check the HTTP response code
                                if (responseCode == '0') {
                                    echo 'Request successful! Nginx is functioning correctly.'
                                } else {
                                    error "Request failed with HTTP status code: ${responseCode}"
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
