package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.ManagementChain;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ManagementChainRepository extends MongoRepository<ManagementChain, String>, ManagementChainRepositoryCustom {
    List<ManagementChain> findAllByIdIn(Set<String> ids);
    List<ManagementChain> findAllByAttachedToTeam(boolean attachedToTeam);
}
