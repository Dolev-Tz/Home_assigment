pipelineJob("frestyle_python") {

  definition {
           cps {
             script('''
                 pipeline {
                    agent any
                    stages {
                        stage('Greet') {
                            steps {
                                echo "Hello!! python"
                            }
                         }
                      }
                   }
              '''.stripIndent())
       sandbox()
          }
      }
  }
