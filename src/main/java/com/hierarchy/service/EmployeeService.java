package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.Employee;
import com.hierarchy.domain.ManagementChain;
import com.hierarchy.domain.Team;
import com.hierarchy.repositories.EmployeeRepository;
import com.hierarchy.repositories.ManagementChainRepository;
import com.hierarchy.repositories.RoleRepository;
import com.hierarchy.repositories.TeamRepository;
import com.hierarchy.web.request.EmployeeRequest;
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

        List<Team> teamsPartOf = teamRepository.findAllByCrewContaining(id);
        if (!teamsPartOf.isEmpty()) {
            List<String> teamNames = teamsPartOf.stream().map(Team::getTitle).collect(Collectors.toList());
            errorBuilder.append("Employee is a member of the following teams: ").append(String.join(", ", teamNames)).append("\n");
        }

        List<Team> teamsLeadOf = teamRepository.findAllByLeadId(id);
        if (!teamsPartOf.isEmpty()) {
            List<String> teamNames = teamsLeadOf.stream().map(Team::getTitle).collect(Collectors.toList());
            errorBuilder.append("Employee is a lead of the following teams: ").append(String.join(", ", teamNames)).append("\n");
        }

        if (errorBuilder.length() != 0) {
            throw HierarchyHttpException.badRequest(errorBuilder.append("Cannot be deleted.").toString());
        }

        employeeRepository.deleteById(id);
    }

    private void checkRoleExists(String roleId, String s) {
        if (!roleRepository.existsById(roleId)) {
            throw HierarchyHttpException.badRequest(s);
        }
    }

    private void checkEmployeeExists(String id) {
        if (!employeeRepository.existsById(id)) {
            throw HierarchyHttpException.notFound("Employee does not exist");
        }
    }
}
