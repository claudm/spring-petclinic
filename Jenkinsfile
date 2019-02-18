node 
{
   def mvnHome = tool 'M3'
              
   stage('Checkout')
        
        {
            def Uuid = UUID.randomUUID().toString()
            step_time("POST",Uuid,"Checkout","1.0","claudemir","start")
            
            git url: 'https://github.com/claudm/spring-petclinic.git'

            VERSION = readMavenPom().getVersion()

            step_time("PUT",Uuid,"Checkout",VERSION,"claudemir","stop")
        }

         
  stage('unit test')

       {
         def Uuid = UUID.randomUUID().toString()
         step_time("POST",Uuid,"unit test",VERSION,"claudemir","start")   
     
        sh "${mvnHome}/bin/mvn test"
         
        step_time("PUT",Uuid,"unit test",VERSION,"claudemir","stop")
           
       }
       
  stage('sonar')
  
       {
               def Uuid = UUID.randomUUID().toString()
               step_time("POST",Uuid,"sonar",VERSION,"claudemir","start")   

               sh "${mvnHome}/bin/mvn sonar:sonar -Dsonar.host.url=http://sonar:9000"
              
               step_time("PUT",Uuid,"sonar",VERSION,"claudemir","stop")
             
        
        }
        
  stage('build')
  
       {
          def Uuid = UUID.randomUUID().toString()
          step_time("POST",Uuid,"build",VERSION,"claudemir","start")   

          sh "${mvnHome}/bin/mvn clean package"
        
          stage 'deploy to repo'
          sh "${mvnHome}/bin/mvn -X -s /var/maven/settings.xml deploy:deploy-file    -DgroupId=nl.somecompany    -DartifactId=petclinic    -Dversion=1.0.0-SNAPSHOT    -DgeneratePom=true    -Dpackaging=war    -DrepositoryId=nexus    -Durl=http://nexus:8081/nexus/content/repositories/snapshots    -Dfile=target/petclinic.war"
        
          
          
          step_time("PUT",Uuid,"build",VERSION,"claudemir","stop")
       }

  stage('build docker image')
          {

          def Uuid = UUID.randomUUID().toString()
          step_time("POST",Uuid,"build docker image",VERSION,"claudemir","start")
          
          sh "sudo docker build -t claudm/petclinic:\$(git rev-parse HEAD) ."

          step_time("PUT",Uuid,"build docker image",VERSION,"claudemir","stop")
          
          }

       
     
  stage('UI test on docker instance') {
            def Uuid = UUID.randomUUID().toString()
            step_time("POST",Uuid,"UI test on docker instance",VERSION,"claudemir","start") 
            try {
                
                  

                
                sh "sudo docker run -d --name petclinic -p 9966:8080 --network devops_prodnetwork claudm/petclinic:\$(git rev-parse HEAD)"
                sh "${mvnHome}/bin/mvn verify -Dgrid.server.url=http://zalenium:4444/wd/hub/"
                
                step_time("PUT",Uuid,"UI test on docker instance",VERSION,"claudemir","stop")
                
              

            } catch (e) {
                sh "sudo docker stop  petclinic; sudo docker rm   petclinic"              
                sh "sudo docker run -d --name petclinic -p 9966:8080 --network devops_prodnetwork claudm/petclinic:\$(git rev-parse HEAD)"
                sh "${mvnHome}/bin/mvn verify -Dgrid.server.url=http://zalenium:4444/wd/hub/"
                
                step_time("PUT",Uuid,"UI test on docker instance",VERSION,"claudemir","stop")

               
            } finally {
                    echo 'here be test results'
                    junit "**/target/surefire-reports/TEST-*.xml"
                  
            }
    }
       

   stage('Performance test on docker instance')
       
       {
           
       def Uuid = UUID.randomUUID().toString()
       step_time("POST",Uuid,"Performance test on docker instance",VERSION,"claudemir","start")   
       
       sh "chmod +x loadtest.sh && ./loadtest.sh petclinic 8080"

       step_time("PUT",Uuid,"Performance test on docker instance",VERSION,"claudemir","stop")
       //stage 'shut down docker instance'
       //sh "sudo docker stop petclinic && sudo docker rm petclinic"
       }
     
      
   
}


import groovy.json.JsonOutput


def step_time(verb,Uuid,componente,versao,responsavel,status) {
    def VERB = verb
    def URL = 'api_devops/api/steps'
    def payload = JsonOutput.toJson([Uuid:Uuid, 
                                     Componente : componente,
                                     Versao   : versao,
                                     Responsavel  : responsavel,
                                     Status : status])
    sh ("curl -X ${VERB} -H 'Content-Type: application/json' -H 'Accept: application/json'  -d  '${payload}' ${URL}")
}
