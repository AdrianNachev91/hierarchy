package com.hierarchy.repositories;

import com.hierarchy.domain.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends MongoRepository<Team, String>, TeamRepositoryCustom{

    List<Team> findAllByManagedBy(String managedBy);
    List<Team> findAllByCrewContaining(String teamMemberId);
    List<Team> findAllByTeamType(String teamTypeId);
    List<Team> findAllByLeadId(String leadId);
    boolean existsByManagedBy(String managedBy);
}