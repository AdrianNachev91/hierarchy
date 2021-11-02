package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Manager;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends MongoRepository<Manager, String>, ManagerRepositoryCustom {
    boolean existsByIdAndLead(String id, boolean lead);
    List<Manager> findByRoleId(String roleId);
    List<Manager> findAllByIdIn(Set<String> managerIds);
}
