package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
public class IngHierarchyApplication {

	@Autowired
	private MongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(IngHierarchyApplication.class, args);
	}

	@PostConstruct
	public void populate() {

		String role1Id = mongoTemplate.insert(role("Manager"), Role.COLLECTION_NAME).getId();

		var manager1 = manager("name-1", true, "managed-person-1", "1", role1Id);
		var manager2 = manager("name-2", false, "managed-person-2", "2", role1Id);

		mongoTemplate.insert(List.of(manager1, manager2), Manager.COLLECTION_NAME);
	}

	private Manager manager(String name, boolean lead, String manages, String corporateId, String roleId) {
		return Manager.builder().name(name).lead(lead).manages(manages).corporateId(corporateId).roleId(roleId).build();
	}

	private Role role(String title) {
		return Role.builder().title(title).build();
	}
}
