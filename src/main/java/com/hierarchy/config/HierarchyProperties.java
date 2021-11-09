package com.hierarchy.config;

import com.hierarchy.HierarchyApplication;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(HierarchyApplication.ING_HIERARCHY)
public class HierarchyProperties {

    private String domain = "http://localhost:8080";
}
