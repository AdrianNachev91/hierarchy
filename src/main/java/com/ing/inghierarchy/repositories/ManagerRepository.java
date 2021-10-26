package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Manager;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends MongoRepository<Manager, String>, ManagerRepositoryCustom {
    boolean existsByIdAndLead(String id, boolean lead);
}
