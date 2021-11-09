package com.ing.inghierarchy.service;

import static com.ing.inghierarchy.TestUtils.*;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.ing.inghierarchy.domain.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.repositories.EmployeeRepository;
import com.ing.inghierarchy.repositories.RoleRepository;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
	private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    
    @BeforeEach
    void setUp() {
    	roleService = new RoleService(roleRepository, employeeRepository);
    }
    
    @Test
    void createRole() {
    	var role = role("role");
    	when(roleRepository.save(role)).thenReturn(role);
    	
    	Role result = roleService.createRole(roleRequest("role"));
    	
    	assertThat(result).isEqualTo(role);
    }
    
    
    @Test
    void updateRole() {
    	var roleRequest = roleRequest("updated-role");
		var role = role("original-role").setId("1");
    	when(roleRepository.findById("1")).thenReturn(Optional.of(role));
    	when(roleRepository.save(role.setTitle("updated-role"))).thenReturn(role);
    	
    	Role result = roleService.updateRole(role.getId(), roleRequest);
    	
    	assertThat(result).isEqualTo(role);
    }
    
    @Test
    void updateRole_notfound() {
    	var roleRequest = roleRequest("updated-role");
    	when(roleRepository.findById("1")).thenReturn(Optional.empty());
    	
    	Throwable e = catchThrowable(() ->  roleService.updateRole("1", roleRequest));
		
    	
    	assertThat(e.getMessage()).isEqualTo("No Role found to update");
    }

    @Test
    void deleteRole() {

		when(roleRepository.existsById("id")).thenReturn(true);
		when(employeeRepository.findAllByRoleId("id")).thenReturn(emptyList());

    	roleService.deleteRole("id");
    	
    	verify(roleRepository).deleteById("id");
    }

	@Test
	void deleteRole_NotFound() {

		when(roleRepository.existsById("id")).thenReturn(false);

		Throwable e = catchThrowable(() ->  roleService.deleteRole("id"));

		assertThat(e.getMessage()).isEqualTo("No role found to delete");
		verify(employeeRepository, never()).findAllByRoleId(anyString());
		verify(roleRepository, never()).deleteById(anyString());
	}
    
    @Test
    void deleteRole_InUse() {

		var employee1 = employee(null, "corporateId-1", "1");
		var employee2 = employee(null, "corporateId-2", "1");
    	List<Employee> employeesUsingRole = List.of(employee1, employee2);

		when(roleRepository.existsById("id")).thenReturn(true);
    	when(employeeRepository.findAllByRoleId("id")).thenReturn(employeesUsingRole);
    	
    	Throwable e = catchThrowable(() ->  roleService.deleteRole("id"));
		
    	assertThat(e.getMessage()).isEqualTo("Role is used by the following employees: corporateId-1, corporateId-2\n" +
				"Cannot delete.");
		verify(roleRepository, never()).deleteById(anyString());
    }
}