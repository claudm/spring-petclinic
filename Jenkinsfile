#!groovy

node {
 stage 'Checkout'
 git url: 'https://github.com/pgoultiaev/spring-petclinic.git'

 // Get the maven tool.
 // ** NOTE: This 'M3' maven tool must be configured
 // **       in the global configuration.
 def mvnHome = tool 'M3'

 // Mark the code build 'stage'....
 stage 'unit test'
 sh "\${mvnHome}/bin/mvn test"

 stage 'sonar'
 sh "\${mvnHome}/bin/mvn sonar:sonar -Dsonar.host.url=http://sonar:9000"

 stage 'build'
 sh "\${mvnHome}/bin/mvn clean package"

 stage 'deploy to repo'
 sh "\${mvnHome}/bin/mvn -X -s /var/maven/settings.xml deploy:deploy-file \
 -DgroupId=nl.somecompany \
 -DartifactId=petclinic \
 -Dversion=1.0.0-SNAPSHOT \
 -DgeneratePom=true \
 -Dpackaging=war \
 -DrepositoryId=nexus \
 -Durl=http://nexus:8081/content/repositories/snapshots \
 -Dfile=target/petclinic.war"

 stage 'build docker image'
 sh "sudo docker build -t pgoultiaev/petclinic:\$(git rev-parse HEAD) ."
}
