package com.hierarchy.repositories;


import com.hierarchy.domain.ManagementChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class ManagementChainRepositoryImpl implements ManagementChainRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ManagementChain> findAllByEmployeeId(String employeeId) {

        Query query = Query.query(Criteria.where(ManagementChain.FIELD_MANAGER_ID).is(employeeId));

        return mongoTemplate.find(query, ManagementChain.class, ManagementChain.COLLECTION_NAME);
    }

    @Override
    public void updateAttachedToTeam(String id, boolean attachedToTeam) {
        mongoTemplate.findAndModify(
                Query.query(Criteria.where(ManagementChain.FIELD_ID).is(id)),
                Update.update(ManagementChain.FIELD_ATTACHED_TO_TEAM, attachedToTeam),
                ManagementChain.class, ManagementChain.COLLECTION_NAME);
    }
}
