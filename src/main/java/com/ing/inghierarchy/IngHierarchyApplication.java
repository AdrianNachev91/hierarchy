package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.domain.TeamMember;
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

		Role chapterLeadRole = mongoTemplate.save(role("Chapter Lead"), Role.COLLECTION_NAME);
		Role developerRole = mongoTemplate.save(role("Developer"), Role.COLLECTION_NAME);

		Manager manager1 = manager("Eric Janssen", true, null, "1", chapterLeadRole.getId());
		mongoTemplate.save((manager1), Manager.COLLECTION_NAME);

		TeamMember teamMember1 = teamMember("Adrian Nachev", "2", developerRole.getId());
		TeamMember teamMember2 = teamMember("Ryan van den Bogaard", "3", developerRole.getId());
		mongoTemplate.insert(List.of(teamMember1, teamMember2), TeamMember.COLLECTION_NAME);
	}

	private Manager manager(String name, boolean lead, String manages, String corporateId, String roleId) {
		return Manager.builder().name(name).lead(lead).manages(manages).corporateId(corporateId).roleId(roleId).build();
	}

	private Role role(String title) {
		return Role.builder().title(title).build();
	}

	public static TeamMember teamMember(String name, String corporateId, String roleId) {
		return TeamMember.builder().name(name).corporateId(corporateId).roleId(roleId).build();
	}
}
