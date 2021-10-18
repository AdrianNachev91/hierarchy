package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Manager;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.stream.Collectors;

public class ManagerRepositoryImpl implements ManagerRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Manager> findAllByIds(List<String> ids) {

        List<ObjectId> objectIds = ids.stream().map(ObjectId::new).collect(Collectors.toList());

        return mongoTemplate.find(Query.query(Criteria.where("_id").in(objectIds)), Manager.class, Manager.COLLECTION_NAME);
    }
}
