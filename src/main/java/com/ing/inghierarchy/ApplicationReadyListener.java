package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.*;
import com.ing.inghierarchy.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationReadyListener {

    private final MongoTemplate mongoTemplate;
    private final MongoConverter mongoConverter;

    private final RoleRepository roleRepo;
    private final TeamTypeRepository teamTypeRepository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository managerRepository;
    private final ManagementChainRepository teamManagementRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {

        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoConverter.getMappingContext());

        IndexOperations indexOpsEmployee = mongoTemplate.indexOps(Employee.class);
        for (IndexDefinition indexDef : resolver.resolveIndexFor(Employee.class)) {
            try {
                indexOpsEmployee.ensureIndex(indexDef);
            } catch (UncategorizedMongoDbException e) {
                // NOP, happens when indexing is already in progress
            }
        }

        IndexOperations indexOpsTeam = mongoTemplate.indexOps(Team.class);
        for (IndexDefinition indexDef : resolver.resolveIndexFor(Team.class)) {
            try {
                indexOpsTeam.ensureIndex(indexDef);
            } catch (UncategorizedMongoDbException e) {
                // NOP, happens when indexing is already in progress
            }
        }

        IndexOperations indexOpsTeamType = mongoTemplate.indexOps(TeamType.class);
        for (IndexDefinition indexDef : resolver.resolveIndexFor(TeamType.class)) {
            try {
                indexOpsTeamType.ensureIndex(indexDef);
            } catch (UncategorizedMongoDbException e) {
                // NOP, happens when indexing is already in progress
            }
        }

        IndexOperations indexOpsRole = mongoTemplate.indexOps(Role.class);
        for (IndexDefinition indexDef : resolver.resolveIndexFor(Role.class)) {
            try {
                indexOpsRole.ensureIndex(indexDef);
            } catch (UncategorizedMongoDbException e) {
                // NOP, happens when indexing is already in progress
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        Role roleDeptManager = roleRepo.save(Role.builder().title("Department Manager").build());
        Role roleManagementSupport = roleRepo.save(Role.builder().title("Management Support").build());
        Role roleBusinessManager = roleRepo.save(Role.builder().title("Business Manager").build());
        Role roleFeatureEngineers = roleRepo.save(Role.builder().title("Feature Emgineers NL").build());
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
