package com.hierarchy;

import com.hierarchy.domain.*;
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

}
