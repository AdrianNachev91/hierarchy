package com.hierarchy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.hierarchy.config")
public class HierarchyApplication {

	public static final String ING_HIERARCHY = "ing.hierarchy";

	public static void main(String[] args) {
		SpringApplication.run(HierarchyApplication.class, args);
	}
}
