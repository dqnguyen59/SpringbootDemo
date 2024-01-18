package org.smartblackbox.springbootdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;

/**
 * SpringbootDemo application.
 * 
 * This is a Springboot demo application includes the following:
 * 
 * 	- ORM Hibernate
 * 	- H2Database (H2-Console) / MySQL Database (modify application.properties for Mysql-connector)
 *  - JSON Web Token Security
 *  - JUnit with MockMvc
 *  - SwaggerUI
 *  - (MySQL) Docker
 *  
 *  This demo contains a data class model with two tables:
 *  - Role [ADMIN_ROLE, USER_ROLE]
 *  - User
 *  
 *  Result:
 *  - REST-API (http://localhost:8080/SpringbootDemo/api/v1/*)
 *  - SwaggerUI (http://localhost:8080/SpringbootDemo/swagger-ui/index.html)
 *  - H2-Console (http://localhost:8080/SpringbootDemo/h2-console/)
 * 
 * Object-Relational Mapping (ORM) allows us to convert Java data class into relational database tables,
 * without the need to design a database model. Changes made in the code will have an effect on the
 * database. In other words, it will synchronize the database with the data class, 
 * using the spring.jpa.hibernate.ddl-auto=update.
 * 
 * For convenience, H2Database is used to have a relational database that will run in memory only.
 * There's a MySQL connector included, if one wishes to test it on a real database. 
 * 
 * JSON Web Token Security is used to have a secured Rest-API.
 * JUnit is included to test the API end-points using MockMvc.
 * 
 * Swagger-UI is included to document the API end-points.
 * 
 * When the application starts, the post method InitServerContext.init() will be called 
 * to perform anything that needs to be run first! Default user "sysadmin" will be created
 * during the InitServerContext.init().The password is Consts.SYS_ADMIN_PASSWORD.
 * sysadmin has two roles: ROLE_ADMIN and ROLE_USER.
 * When a new user is created, it will have the role ROLE_USER.
 * 
 * When using the API or Swagger-UI, you must sign in as "sysadmin" first.
 * Then you can do other things. Use the welcome-controller end-points to test the user access level.
 * 
 * @author Copyright (C) 2024  Duy Quoc Nguyen <d.q.nguyen@smartblackbox.org>
 *
 */
@Slf4j
@OpenAPIDefinition(servers = {@Server(url = "/SpringbootDemo", description = "Default Server URL")})
@SpringBootApplication
public class SpringbootDemoApplication {

	@Autowired
	Environment environment;
	
	@Bean
	public CommandLineRunner commandLineRunner(ConfigurableApplicationContext context) {
		String port = environment.getProperty("server.port");
		
		log.info(String.format("https://localhost:%s/swagger-ui/index.html", port));
		return args -> {

//			System.out.println("Spring Boot arguments");
//
//			String[] beanNames = context.getBeanDefinitionNames();
//			Arrays.sort(beanNames);
//			for (String beanName : beanNames) {
//				System.out.println(beanName);
//			}

		};
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDemoApplication.class, args);
	}

}
