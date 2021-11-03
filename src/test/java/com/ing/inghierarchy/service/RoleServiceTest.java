package com.ing.inghierarchy.service;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ing.inghierarchy.domain.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ing.inghierarchy.Exceptions.IngHttpException;
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
    void roleExists_NotFoundPart() {
    	var role = Role.builder().title("role").build();
    	when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(null);
    	
    	boolean exists = roleService.roleExists(role.getTitle());
    	assertThat(exists).isEqualTo(false);
    }
    
    @Test
    void roleExists_ExistsPart() {
    	var role = Role.builder().title("role").build();
    	when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(role);
    	
    	boolean exists = roleService.roleExists(role.getTitle());
    	assertThat(exists).isEqualTo(true);
    }
    
    
    @Test
    void createRole_Duplicate() {
    	var role = Role.builder().title("role").build();
    	when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(role);
    	
    	Exception ex = assertThrows(IngHttpException.class, () -> roleService.createRole(role));
    	assertThat(ex.getMessage()).isEqualTo("Role already exists");
    }
    
    @Test
    void createRole_New() {
    	var role = Role.builder().title("role").build();
    	when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(null);
    	when(roleRepository.save(role)).thenReturn(role);
    	
    	Role createdRole = roleService.createRole(role);
    	
    	assertThat(createdRole).isEqualTo(role);
    }
    
    
    @Test
    void updateRole_found() {
    	var role = Role.builder().title("role").build();
		role.setTitle("updated-role");
    	when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
    	when(roleRepository.save(role)).thenReturn(role);
    	
    	Role updatedRole = null;
    	try {
			updatedRole = roleService.updateRole(role.getId(), role);
		} catch (Exception e) {
			//not expected
		}
    	
    	assertThat(updatedRole).isEqualTo(role);
    }
    
    @Test
    void updateRole_notfound() {
    	var role = Role.builder().title("role").build();
		role.setTitle("updated-role");
    	when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());
    	
    	Throwable e = catchThrowable(() ->  roleService.updateRole(role.getId(), role));
		
    	
    	assertThat(e.getMessage()).isEqualTo("No Role found to update");
    }
    
    @Test
    void deleteRole_notfound() {
    	var role = Role.builder().title("role").build();
    	when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());
    	
    	Throwable e = catchThrowable(() ->  roleService.deleteRole(role.getId()));
		
    	assertThat(e.getMessage()).isEqualTo("No Role found to delete");
    }

    @Test
    void deleteRole_found() {
    	var role = Role.builder().id("1").title("role").build();

    	when(employeeRepository.findByRoleId(role.getId())).thenReturn(emptyList());
    	when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
    	
    	boolean deleted = roleService.deleteRole(role.getId());
    	
    	assertThat(deleted).isEqualTo(true);
    }
    
    @Test
    void deleteRole_found_deletionFailed() {
    	var role = Role.builder().id("1").title("role").build();
    	List<Employee> managerList = new ArrayList<>();
    	managerList.add(Employee.builder().roleId(role.getId()).build());
    	
    	when(employeeRepository.findByRoleId(role.getId())).thenReturn(managerList);
    	when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
    	
    	Throwable e = catchThrowable(() ->  roleService.deleteRole(role.getId()));
		
    	assertThat(e.getMessage()).isEqualTo("Role is in use");
    }
}