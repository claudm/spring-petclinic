#!groovy
pipeline {
    agent any
    tools {
        maven 'M3'
    }
    stages {

        stage('Checkout') {
            steps {
                git url: 'https://github.com/claudm/spring-petclinic.git'
            }
        }

        stage('unit test') {
            steps {
                sh "_JAVA_OPTIONS=-Djdk.net.URLClassPath.disableClassPathURLCheck=true mvn test"
            }
        }

        stage('sonar') {
            steps {
                sh "mvn sonar:sonar -Dsonar.host.url=http://sonar:9000"
            }
        }

        stage('build') {
            steps {
                sh "mvn clean package"
            }
        }

        stage('deploy to repo') {
            steps {
                sh "mvn -X -s /var/maven/settings.xml deploy:deploy-file \
                -DgroupId=nl.somecompany \
                -DartifactId=petclinic \
                -Dversion=1.0.0-SNAPSHOT \
                -DgeneratePom=true \
                -Dpackaging=war \
                -DrepositoryId=nexus \
                -Durl=http://nexus:8081/nexus/content/repositories/snapshots \
                -Dfile=target/petclinic.war"
            }
        }

        stage('build docker image') {
            steps {
                sh "sudo docker build -t pgoultiaev/petclinic:\$(git rev-parse HEAD) ."
            }
        }

        stage('UI test on docker instance') {
            steps {
                sh "sudo docker run -d --name petclinic -p 9966:8080 --network demopipeline_prodnetwork pgoultiaev/petclinic:\$(git rev-parse HEAD)"
                sh "mvn verify -Dgrid.server.url=http://selhub:4444/wd/hub/"
            }
        }

        stage('Performance test on docker instance') {
            steps {
                sh "chmod +x loadtest.sh && ./loadtest.sh petclinic 8080"
            }
        }

        stage('shut down docker instance') {
            steps {
                sh "sudo docker stop petclinic && sudo docker rm petclinic"
            }
        }
    }

    post {
        always {
            echo 'here be test results'
            junit "**/target/surefire-reports/TEST-*.xml"
        }
    }
}
