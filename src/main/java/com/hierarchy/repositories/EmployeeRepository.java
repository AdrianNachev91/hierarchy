package com.hierarchy.repositories;

import com.hierarchy.domain.Employee;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String>, EmployeeRepositoryCustom {
    List<Employee> findAllByRoleId(String roleId);
}
