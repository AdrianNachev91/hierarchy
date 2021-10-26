package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends MongoRepository<Team, String>, TeamRepositoryCustom{

}