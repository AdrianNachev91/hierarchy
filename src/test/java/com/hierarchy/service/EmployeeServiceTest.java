package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.Employee;
import com.hierarchy.repositories.EmployeeRepository;
import com.hierarchy.repositories.ManagementChainRepository;
import com.hierarchy.repositories.RoleRepository;
import com.hierarchy.repositories.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.hierarchy.TestUtils.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private EmployeeService employeeService;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ManagementChainRepository managementChainRepository;
    @Mock
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(roleRepository, employeeRepository, managementChainRepository, teamRepository);
    }

    @Test
    void createEmployee() {

        // Prepare
        var employeeRequest = employeeRequest("name", "corporate-id", "role-id");
        when(roleRepository.existsById("role-id")).thenReturn(true);
        var employee = employeeRequest.toEmployee();
        when(employeeRepository.save(employee)).thenReturn(employee);

        // Test
        Employee result = employeeService.createEmployee(employeeRequest);

        // Verify
        assertThat(result).isEqualTo(employee);
    }

    @Test
    void createEmployee_RoleDoesNotExist() {

        // Prepare
        var employeeRequest = employeeRequest("name", "corporate-id", "role-id");
        when(roleRepository.existsById("role-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> employeeService.createEmployee(employeeRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Cannot create an employee with non-existing role");
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(400);
    }

    @Test
    void updateEmployee() {

        // Prepare
        var employeeRequest = employeeRequest("name", "corporate-id", "role-id");
        when(employeeRepository.existsById("employee-id")).thenReturn(true);
        when(roleRepository.existsById("role-id")).thenReturn(true);
        var employee = employeeRequest.toEmployee();
        employee.setId("employee-id");
        when(employeeRepository.save(employee)).thenReturn(employee);

        // Test
        Employee result = employeeService.updateEmployee("employee-id", employeeRequest);

        // Verify
        assertThat(result).isEqualTo(employee);
    }

    @Test
    void updateEmployee_EmployeeDoesNotExist() {

        // Prepare
        var employeeRequest = employeeRequest("name", "corporate-id", "role-id");
        when(employeeRepository.existsById("employee-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> employeeService.updateEmployee("employee-id", employeeRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Employee does not exist");
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
    }

    @Test
    void updateEmployee_RoleDoesNotExist() {

        // Prepare
        var employeeRequest = employeeRequest("name",  "corporate-id", "role-id");
        when(employeeRepository.existsById("employee-id")).thenReturn(true);
        when(roleRepository.existsById("role-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> employeeService.updateEmployee("employee-id", employeeRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Cannot update an employee with non-existing role");
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(400);
    }

    @Test
    void deleteEmployee() {

        // Prepare
        when(employeeRepository.existsById("employee-id")).thenReturn(true);
        when(managementChainRepository.findAllByEmployeeId("employee-id")).thenReturn(emptyList());
        when(teamRepository.findAllByCrewContaining("employee-id")).thenReturn(emptyList());

        // Test
        employeeService.deleteEmployee("employee-id");

        // Verify
        verify(employeeRepository).deleteById("employee-id");
    }

    @Test
    void deleteEmployee_EmployeeDoesNotExist() {

        // Prepare
        when(employeeRepository.existsById("employee-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> employeeService.deleteEmployee("employee-id"));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Employee does not exist");
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
    }

    @Test
    void deleteEmployee_NotAllowed() {

        // Prepare
        when(employeeRepository.existsById("employee-id")).thenReturn(true);
        var chain1 = managementChain(true,
                Set.of(managerInChain("employee-id", "leader"), managerInChain("leader", null))
        ).setId("chain1");
        var chain2 = managementChain(false,
                Set.of(managerInChain("employee-id", null))
        ).setId("chain2");
        when(managementChainRepository.findAllByEmployeeId("employee-id")).thenReturn(List.of(chain1, chain2));
        var team1 = team("team 1", null, "employee-id", null).setCrew(List.of("employee-id", "e2", "e3"));
        var team2 = team("team 2", null, null, null).setCrew(List.of("e4", "e5", "employee-id"));
        var team3 = team("team 3", null, "employee-id", null).setCrew(List.of("e4", "employee-id", "e1"));
        when(teamRepository.findAllByCrewContaining("employee-id")).thenReturn(List.of(team1, team2, team3));
        when(teamRepository.findAllByLeadId("employee-id")).thenReturn(List.of(team1, team3));

        // Test
        Throwable e = catchThrowable(() -> employeeService.deleteEmployee("employee-id"));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Employee participates in the following chains: chain1, chain2\n" +
                "Employee is a member of the following teams: team 1, team 2, team 3\n" +
                "Employee is a lead of the following teams: team 1, team 3\n" +
                "Cannot be deleted.");
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(400);
    }
}