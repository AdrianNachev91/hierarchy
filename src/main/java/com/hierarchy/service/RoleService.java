package com.hierarchy.service;

import com.hierarchy.domain.Employee;
import com.hierarchy.web.request.RoleRequest;
import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.Role;
import com.hierarchy.repositories.EmployeeRepository;
import com.hierarchy.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    public Role createRole(RoleRequest roleRequest) throws HierarchyHttpException {

        Role role = new ModelMapper().map(roleRequest, Role.class);
        return roleRepository.save(role);
    }

    public Role updateRole(String id, RoleRequest roleRequest) throws HierarchyHttpException {

        Role role = roleRepository.findById(id).orElseThrow(() -> HierarchyHttpException.notFound("No Role found to update"));

        role.setTitle(roleRequest.getTitle());
        return roleRepository.save(role);
    }

    public void deleteRole(String id) throws HierarchyHttpException {

        checkRoleExists(id);
        List<Employee> employeesWithRole = employeeRepository.findAllByRoleId(id);
        if (!employeesWithRole.isEmpty()) {
            List<String> corporateIds = employeesWithRole.stream().map(Employee::getCorporateId).collect(Collectors.toList());
            throw HierarchyHttpException.badRequest(String.format("Role is used by the following employees: %s\nCannot delete.", String.join(", ", corporateIds)));
        }
        roleRepository.deleteById(id);
    }

    private void checkRoleExists(String id) {

        if (!roleRepository.existsById(id)) {
            throw HierarchyHttpException.notFound("No role found to delete");
        }
    }
}
