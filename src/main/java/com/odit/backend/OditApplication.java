package com.odit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OditApplication {

	public static void main(String[] args) {
		SpringApplication.run(OditApplication.class, args);
	}

}
