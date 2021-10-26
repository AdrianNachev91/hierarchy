package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.domain.TeamType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamTypeRepository extends MongoRepository<TeamType, String>, TeamTypeRepositoryCustom{

}
