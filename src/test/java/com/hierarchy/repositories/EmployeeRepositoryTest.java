package com.hierarchy.repositories;

import com.hierarchy.domain.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.hierarchy.TestUtils.employee;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void findAllByRoleId() {

        // Prepare
        var employee1 = employee(null, "1", "roleId1").setId("1");
        var employee2 = employee(null, "2", "roleId1").setId("2");
        var employee3 = employee(null, "3", "roleId2").setId("3");
        employeeRepository.saveAll(List.of(employee1, employee2, employee3));

        // Test
        List<Employee> role1Results = employeeRepository.findAllByRoleId("roleId1");
        List<Employee> role2Results = employeeRepository.findAllByRoleId("roleId2");
        List<Employee> roleNoResults = employeeRepository.findAllByRoleId("no-results");

        // Verify
        assertThat(role1Results).hasSize(2);
        assertThat(role1Results.stream().map(Employee::getId).collect(toList()))
                .containsExactlyInAnyOrder("1", "2");
        assertThat(role2Results).hasSize(1);
        assertThat(role2Results.stream().map(Employee::getId).collect(toList()))
                .containsExactlyInAnyOrder("3");
        assertThat(roleNoResults).isEmpty();
    }
}