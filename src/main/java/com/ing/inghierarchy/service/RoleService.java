package com.ing.inghierarchy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.repositories.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
	
	private final RoleRepository roleRepository;
	private final EmployeeRepository employeeRepository;
	
	public Role createRole(Role role) throws IngHttpException
	{
		Role returnRole;
		if(!roleExists(role)) {
			returnRole = Role.builder()
					.title(role.getTitle())
					.build();
			returnRole = roleRepository.save(returnRole);
		} else {
			throw IngHttpException.badRequest("Role already exists");
		}
		return returnRole;
	}
	
	public Role updateRole(String id, Role role) throws IngHttpException
	{
		Role updateRole = roleRepository.findById(id).orElseThrow(() -> IngHttpException.notFound("No Role found to update"));
		
		if(!roleExists(role.getTitle()))
		{
			updateRole.setTitle(role.getTitle());
			updateRole = roleRepository.save(updateRole);
		} else {
			throw IngHttpException.badRequest("Role already exists");
		}
		
		return updateRole;
	}
	
	public boolean deleteRole(String id) throws IngHttpException  {
		Role deleteRole = roleRepository.findById(id).orElseThrow(() -> IngHttpException.notFound("No Role found to delete"));
		boolean canDelete = employeeRepository.findByRoleId(deleteRole.getId()).isEmpty();
		if(canDelete) {
			roleRepository.delete(deleteRole);
		} else {
			throw IngHttpException.badRequest("Role is in use");
		}
		return true;
	}
	
	public boolean roleExists(String title) {
		return (roleRepository.findRoleByTitle(title) != null);
	}
	
	public boolean roleExists(Role role) {
		boolean exists = false;
		if(role.getId() != null) {
			if(!role.getId().isEmpty()) {
				exists = roleRepository.findById(role.getId()).isPresent();
			}
		}
		
		if(!exists)	{
			exists = roleExists(role.getTitle());
		}
		
		return exists;
	}
}
