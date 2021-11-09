package com.ing.inghierarchy.service;

import com.ing.inghierarchy.domain.Employee;
import com.ing.inghierarchy.web.request.RoleRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.repositories.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    public Role createRole(RoleRequest roleRequest) throws IngHttpException {

        Role role = new ModelMapper().map(roleRequest, Role.class);
        return roleRepository.save(role);
    }

    public Role updateRole(String id, RoleRequest roleRequest) throws IngHttpException {

        Role role = roleRepository.findById(id).orElseThrow(() -> IngHttpException.notFound("No Role found to update"));

        role.setTitle(roleRequest.getTitle());
        return roleRepository.save(role);
    }

    public void deleteRole(String id) throws IngHttpException {

        checkRoleExists(id);
        List<Employee> employeesWithRole = employeeRepository.findAllByRoleId(id);
        if (!employeesWithRole.isEmpty()) {
            List<String> corporateIds = employeesWithRole.stream().map(Employee::getCorporateId).collect(Collectors.toList());
            throw IngHttpException.badRequest(String.format("Role is used by the following employees: %s\nCannot delete.", String.join(", ", corporateIds)));
        }
        roleRepository.deleteById(id);
    }

    private void checkRoleExists(String id) {

        if (!roleRepository.existsById(id)) {
            throw IngHttpException.notFound("No role found to delete");
        }
    }
}
