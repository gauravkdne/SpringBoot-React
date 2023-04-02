# Job Marketplace - Full Stack App

1.    Required Tools
             a. JDK 1.8
             b. Maven 3.x
             c. MySQL 8.0
            
2.    Server Configuration
            Spring boot server requires a user named ‘admin’ to be present in the MySQL database. You can either create a new user named “admin” with all privileges in MySQL database or if you choose to use existing user then edit following properties under ‘/job-marketplace/server/src/main/resources/application.yml’
            username: <USER_NAME>
            password: <PASSWORD>
            
3.    Database Configuration
            a.    Run mysql -u admin -p < job-marketplace/db/db.sql
                  This will execute necessary queries to setup the database
            
4.    Running the Server
            a.    Navigate to server
                    cd job-marketplace/server
            b.    Run mvn clean install
                    job-marketplace/server/mvn clean install
            c.    Run the generated server jar
                        i.    cd job-marketplace/server/target
                        ii.    java -jar job-marketplace.jar
                        
5.    Running the client
            a.    Launch a browser (Chrome, Safari, Firefox or IE)
            b.    Enter URL http://localhost:11000/

