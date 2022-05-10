pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                gradle clean bootJar
                archiveArtifacts artifacts: '**/build/libs/Asmodeus-*.jar', fingerprint: true
                echo 'Asmodeus Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Asmodeus Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Asmodeus Deploying....'
            }
        }
    }
}