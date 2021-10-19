package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, String>, RoleRepositoryCustom{

}
