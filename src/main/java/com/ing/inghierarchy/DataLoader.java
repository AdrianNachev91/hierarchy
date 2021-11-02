package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.domain.TeamType;
import com.ing.inghierarchy.repositories.*;
import com.ing.inghierarchy.service.PersonService;
import com.ing.inghierarchy.service.RoleService;
import com.ing.inghierarchy.service.TeamService;
import com.ing.inghierarchy.service.TeamTypeService;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

	private final RoleRepository roleRepo;
	private final TeamMemberRepository memberRepo;
	private final TeamTypeRepository teamTypeRepository;
	private final TeamRepository teamRepository;
	private final ManagerRepository managerRepository;
	private final TeamManagementRepository teamManagementRepository;
	
	@Override
	public void run(ApplicationArguments args) {
		Role roleDeptManager = roleRepo.save(Role.builder().title("Department Manager").build());
		Role roleManagementSupport = roleRepo.save(Role.builder().title("Management Support").build());
		Role roleBusinessManager = roleRepo.save(Role.builder().title("Business Manager").build());
		Role roleChapterLead = roleRepo.save(Role.builder().title("Chapter Lead").build());
		Role roleCDFront = roleRepo.save(Role.builder().title("CD Front").build());
		Role roleCDServices = roleRepo.save(Role.builder().title("CD Services").build());
		Role roleCDStack = roleRepo.save(Role.builder().title("CD Stack").build());
		Role roleKYCAnalytics = roleRepo.save(Role.builder().title("KYC Analytics").build());
		Role roleKYCServicesFront = roleRepo.save(Role.builder().title("KYC Services Front").build());
		Role roleKYCStackMonitoringAndQA = roleRepo.save(Role.builder().title("KYC Stack Monitoring and QA").build());
		Role roleKYCWorkflow = roleRepo.save(Role.builder().title("KYC Workflow").build());
		Role roleOnboardingFront = roleRepo.save(Role.builder().title("Onboarding Front").build());
		Role roleContentDelivery = roleRepo.save(Role.builder().title("Content Delivery").build());
		Role roleArchiving = roleRepo.save(Role.builder().title("Archiving").build());
		Role roleIntelligentAutomation = roleRepo.save(Role.builder().title("Intelligent Automation").build());
		Role roleDataProcessingAndOps = roleRepo.save(Role.builder().title("Data Processing & Ops").build());
		Role roleMLEngineer = roleRepo.save(Role.builder().title("Machine Learning Engineer").build());
		Role roleInsightsAndDataServing = roleRepo.save(Role.builder().title("Insights And Data Serving").build());
		Role roleDataWrangling = roleRepo.save(Role.builder().title("Data Wrangling").build());
		Role roleAnalyticalReposting = roleRepo.save(Role.builder().title("Analytical Reporting And Tools").build());
		Role roleBackendDev = roleRepo.save(Role.builder().title("Backend Developer").build());
		Role roleFrontendDev = roleRepo.save(Role.builder().title("Frontend Developer").build());
		Role roleOps = roleRepo.save(Role.builder().title("Ops").build());
	}
}
