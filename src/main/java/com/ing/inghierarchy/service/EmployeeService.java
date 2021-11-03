package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.*;
import com.ing.inghierarchy.repositories.*;
import com.ing.inghierarchy.web.request.EmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final ManagementChainRepository managementChainRepository;
    private final TeamRepository teamRepository;

    public Employee createEmployee(EmployeeRequest employeeRequest) {

        checkRoleExists(employeeRequest.getRoleId(), "Cannot create an employee with non-existing role");

        Employee employee = employeeRequest.toEmployee();
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(String id, EmployeeRequest employeeRequest) {

        checkEmployeeExists(id);
        checkRoleExists(employeeRequest.getRoleId(), "Cannot update an employee with non-existing role");

        Employee employee = employeeRequest.toEmployee();
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(String id) {

        checkEmployeeExists(id);

        List<ManagementChain> managementChainsParticipating = managementChainRepository.findAllByEmployeeId(id);

        var errorBuilder = new StringBuilder();
        if (!managementChainsParticipating.isEmpty()) {
            List<String> chainIds = managementChainsParticipating.stream().map(ManagementChain::getId).collect(Collectors.toList());
            errorBuilder.append("Employee participates in the following chains: ").append(String.join(", ", chainIds)).append("\n");
        }

        List<Team> teamsManaged = teamRepository.findAllByCrewContaining(id);
        if (!teamsManaged.isEmpty()) {
            List<String> teamNames = teamsManaged.stream().map(Team::getTitle).collect(Collectors.toList());
            errorBuilder.append("Employee is a member of the following teams: ").append(String.join(", ", teamNames)).append("\n");
        }
        if (errorBuilder.length() != 0) {
            throw IngHttpException.badRequest(errorBuilder.append("Cannot be deleted.").toString());
        }

        employeeRepository.deleteById(id);
    }

    private void checkRoleExists(String roleId, String s) {
        if (!roleRepository.existsById(roleId)) {
            throw IngHttpException.badRequest(s);
        }
    }

    private void checkEmployeeExists(String id) {
        if (!employeeRepository.existsById(id)) {
            throw IngHttpException.notFound("Employee does not exist");
        }
    }
}
