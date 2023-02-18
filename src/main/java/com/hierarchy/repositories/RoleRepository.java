package com.hierarchy.repositories;

import com.hierarchy.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, String>, RoleRepositoryCustom{

}
