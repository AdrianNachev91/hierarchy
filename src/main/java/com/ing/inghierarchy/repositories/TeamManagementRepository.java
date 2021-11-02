package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.TeamManagement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TeamManagementRepository extends MongoRepository<TeamManagement, String>, TeamManagementRepositoryCustom{
    List<TeamManagement> findAllByIdIn(Set<String> ids);
}
