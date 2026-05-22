pipeline {
    agent any
    stages {
        stage('clone and clean repo') {
            steps {
                bat "if exist demoic rmdir /s /q demoic"
                bat "git clone https://github.com/Maharo-Andri/demoic"
                bat "mvn clean -f DemoIC"
            }
        }
        stage('Test') {
            steps {
                bat "mvn test -f DemoIC"
            }
        }
        stage('Deploy') {
            steps {
                bat "mvn package -f DemoIC"
                bat "mvn deploy -f DemoIC -s C:\\Users\\Maharo\\.m2\\settings.xml"
                bat "mvn sonar:sonar -f DemoIC -Dsonar.token=squ_3b44443669924d1f455d6a358434eb167d62e7b0"
            }
        }
       stage('Docker Build & Push') {
            steps {
                bat "copy DemoIC\\target\\DemoIC-0.0.1-SNAPSHOT.jar demoic\\DemoIC-0.0.1-SNAPSHOT.jar"
                bat "docker build -t maharoandri/demoic:latest -f demoic\\Dockerfile demoic\\"
                bat "docker login -u maharoandri -p docker_010203"
                bat "docker push maharoandri/demoic:latest"
            }
        }
    }
    post {
            failure {
                emailext body: 'Le Build $BUILD_NUMBER a echouer',
                subject: 'Build Jenkins echouer',
                to: 'mhrmaharo@gmail.com'
            }
        }
}