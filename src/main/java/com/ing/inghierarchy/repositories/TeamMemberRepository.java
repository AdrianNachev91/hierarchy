package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.TeamMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends MongoRepository<TeamMember, String>, TeamMemberRepositoryCustom {

}
