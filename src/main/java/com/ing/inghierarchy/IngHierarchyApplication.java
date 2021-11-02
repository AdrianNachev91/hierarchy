package com.ing.inghierarchy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.ing.inghierarchy.config")
public class IngHierarchyApplication {

	public static final String ING_HIERARCHY = "ing.hierarchy";

	public static void main(String[] args) {
		SpringApplication.run(IngHierarchyApplication.class, args);
	}
}
