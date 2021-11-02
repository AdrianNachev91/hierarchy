package com.ing.inghierarchy.repositories;


import com.ing.inghierarchy.domain.ManagementChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static com.ing.inghierarchy.domain.ManagementChain.*;

public class ManagementChainRepositoryImpl implements ManagementChainRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ManagementChain> findAllByEmployeeId(String employeeId) {

        Query query = Query.query(Criteria.where(FIELD_MANAGER_ID).is(employeeId));

        return mongoTemplate.find(query, ManagementChain.class, COLLECTION_NAME);
    }

    @Override
    public void updateAttachedToTeam(String id, boolean attachedToTeam) {
        mongoTemplate.findAndModify(
                Query.query(Criteria.where(FIELD_ID).is(id)),
                Update.update(FIELD_ATTACHED_TO_TEAM, attachedToTeam),
                ManagementChain.class, COLLECTION_NAME);
    }
}
