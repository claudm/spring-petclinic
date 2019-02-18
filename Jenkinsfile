node 
{
   def mvnHome = tool 'M3'
              
   stage('Checkout')
        
        {
            
            git url: 'https://github.com/claudm/spring-petclinic.git'
        }

         
  stage('unit test')

       {

     
        sh "${mvnHome}/bin/mvn test"
           
       }
       
  stage('sonar')
  
       {
              
              sh "${mvnHome}/bin/mvn sonar:sonar -Dsonar.host.url=http://sonar:9000"
             
        
        }
        
  stage('build')
  
       {
          sh "${mvnHome}/bin/mvn clean package"
        
          stage 'deploy to repo'
          sh "${mvnHome}/bin/mvn -X -s /var/maven/settings.xml deploy:deploy-file    -DgroupId=nl.somecompany    -DartifactId=petclinic    -Dversion=1.0.0-SNAPSHOT    -DgeneratePom=true    -Dpackaging=war    -DrepositoryId=nexus    -Durl=http://nexus:8081/nexus/content/repositories/snapshots    -Dfile=target/petclinic.war"
        
          stage 'build docker image'
          sh "sudo docker build -t pgoultiaev/petclinic:\$(git rev-parse HEAD) ."
       }
       
       
     try {
         stage('UI test on docker instance') {
              def CONTAINER_NAME="petclinic"
              def status = sh(returnStdout: true, script: "sudo docker ps -f name=$CONTAINER_NAME\$").trim()
        
              if (status != 0) {
                   
                    sh "sudo docker stop $CONTAINER_NAME && sudo docker rm $CONTAINER_NAME"
             
              }
              
              sh "sudo docker run -d --name petclinic -p 9966:8080 --network petclinic-demo-pipeline_prodnetwork pgoultiaev/petclinic:\$(git rev-parse HEAD)"
              sh "${mvnHome}/bin/mvn verify -Dgrid.server.url=http://zalenium:4444/wd/hub/"
        }
        echo 'Enviar mensagem para o slack como ok'
    } catch (e) {
        echo 'This will run only if failed'

        // Since we're catching the exception in order to report on it,
        // we need to re-throw it, to ensure that the build is marked as failed
        throw e
    } finally {
        def currentResult = currentBuild.result ?: 'SUCCESS'
        if (currentResult == 'UNSTABLE') {
            echo ' echo 'Enviar mensagem para o slack com erro''
        }

        def previousResult = currentBuild.previousBuild?.result
        if (previousResult != null && previousResult != currentResult) {
            echo 'here be test results'
            junit "**/target/surefire-reports/TEST-*.xml"
        }

        
    }
       

   stage('Performance test on docker instance')
       
       {
       
       sh "chmod +x loadtest.sh && ./loadtest.sh petclinic 8080"
    
       //stage 'shut down docker instance'
       //sh "sudo docker stop petclinic && sudo docker rm petclinic"
       }
     
      
   
}

