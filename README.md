# Springboot Demo

SpringbootDemo application.

This Springboot demo application includes the following:

	- ORM Hibernate
	- H2Database (H2-Console) / MySQL Database (modify application.properties for Mysql-connector)
	- JSON Web Token Security
	- JUnit with MockMvc
	- SwaggerUI
	- Docker
 
 This demo contains a simple data class model with two tables:
 
	- Role [ROOT_ROLE, ADMIN_ROLE, USER_ROLE]
	- User
 
 Result:
 
	- REST-API (http://localhost:8080/SpringbootDemo/api/v1/*)
	- SwaggerUI (http://localhost:8080/SpringbootDemo/swagger-ui/index.html)
	- H2-Console (http://localhost:8080/SpringbootDemo/h2-console/)

Object-Relational Mapping (ORM) allows us to convert Java data class into relational database tables,
without the need to design a database model. Changes made in the code will have an effect on the
database. In other words, it will synchronize the database with the data class, 
using the spring.jpa.hibernate.ddl-auto=update. When running for the first time it will create a database and its tables.

For convenience, H2Database is used to have a relational database that will run in memory only.
There's a MySQL connector included, if one wishes to test it on a real database. You can use docker/mysql_docker.sh to create a docker image/container.

	mysql_docker.sh -b	# build MySQL-server docker image
	mysql_docker.sh -n -d	# Run docker and MySQL server
	mysql_docker.sh -q	# Stop docker and MySQL server

JSON Web Token Security is used to have a secured Rest-API.
JUnit is included to test the API end-points using MockMvc.

Swagger-UI is included to document the API end-points.

When the application starts, the post method InitServerContext.init() will be called 
to perform anything that needs to be executed first! Default user "root" will be created
during the InitServerContext.init(). The default password is "secret".
Root user has the highest role ROOT.
When a new user sign up for an account, it can have the ADMIN or USER  role.

When using the API or Swagger-UI, you must sign in as "root" first.
Then you can do other things. Use the welcome-controller end-points to test the user access level.

Changing root password is prohibit for this demo.

Check this link for a working demo: 
<a href="https://tomcat.smartblackbox.org/SpringbootDemo/swagger-ui/index.html">https://tomcat.smartblackbox.org/SpringbootDemo/swagger-ui/index.html</a>

_______________________________________________________________________________
## Development setup

Install OpenJDK 17
Minimum required Java version is 17:

    sudo apt install openjdk-17-jdk

Install Eclipse IDE Java EE:
    
    https://www.eclipse.org/downloads/

Clone this project and open the folder in Eclipse.

_______________________________________________________________________________
## Required initializations

**Install Lombok in Eclipse**

Right mouse click on the project folder and click on "Gradle"-> "Refresh Gradle Project".

Install Lombok:
	Find the full path lombok-X.X.X.jar in "Project and External Dependencies" and run it like here below:
	
	java -jar $HOME/.gradle/caches/modules-2/files-2.1/org.projectlombok/lombok/1.18.30/f195ee86e6c896ea47a1d39defbe20eb59cd149d/lombok-1.18.30.jar

	Then follow the instructions when a new "Project Lombok Installer" Window appears.
	
Restart Exclipse.

Right mouse click on project folder and click on "Gradle"-> "Refresh Gradle Project".

_______________________________________________________________________________
## Build

**Run the following to build the project for stand alone application:**

    ./gradlew clean
    ./gradlew bootJar

This will create the Jar file:
    
    "build/libs/SpringbootDemo-X.X.X-SNAPSHOT.jar"

Run jar file:

    java -jar build/libs/SpringbootDemo-X.X.X-SNAPSHOT.jar

**Run the following to build the project for Tomcat server:**

    ./gradlew clean
    ./gradlew bootWar

Rename SpringbootDemo-X.X.X-SNAPSHOT.war to SpringbootDemo.war and copy this file to /opt/tomcat/webapps

Todo: Tomcat installation

_______________________________________________________________________________
## Swagger UI

After successfully running the application, the link below can be accessed.

http://localhost:8888/swagger-ui/index.html

Username: root

Password: secret

Change default admin password here "Consts.java" SYS_ADMIN_DEFAULT_PASSWORD="secret"

Check this link for a working demo: <a href="https://tomcat.smartblackbox.org/SpringbootDemo/swagger-ui/index.html">https://tomcat.smartblackbox.org/SpringbootDemo/swagger-ui/index.html</a>
_______________________________________________________________________________

