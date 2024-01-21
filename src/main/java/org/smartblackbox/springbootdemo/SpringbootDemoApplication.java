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
 * Please read readme.md.
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
