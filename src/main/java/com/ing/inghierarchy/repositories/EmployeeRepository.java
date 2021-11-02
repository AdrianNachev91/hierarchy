package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Employee;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String>, EmployeeRepositoryCustom {
    List<Employee> findByRoleId(String roleId);
}
