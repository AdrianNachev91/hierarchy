package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends MongoRepository<Team, String>, TeamRepositoryCustom{

    List<Team> findAllByManagedBy(String managedBy);
    List<Team> findAllByCrewContaining(String teamMemberId);
}