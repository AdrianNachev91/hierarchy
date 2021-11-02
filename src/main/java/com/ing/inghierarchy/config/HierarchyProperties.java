package com.ing.inghierarchy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.ing.inghierarchy.IngHierarchyApplication.ING_HIERARCHY;

@Data
@ConfigurationProperties(ING_HIERARCHY)
public class HierarchyProperties {

    private String domain = "https://localhost:8080";
}
