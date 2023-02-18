package com.hierarchy.repositories;

import com.hierarchy.domain.TeamType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamTypeRepository extends MongoRepository<TeamType, String>, TeamTypeRepositoryCustom{
    Optional<TeamType> findFirstByTitle(String title);
}
