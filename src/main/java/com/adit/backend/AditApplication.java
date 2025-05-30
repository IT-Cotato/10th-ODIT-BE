package com.adit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AditApplication {

	public static void main(String[] args) {
		SpringApplication.run(AditApplication.class, args);
	}

}
