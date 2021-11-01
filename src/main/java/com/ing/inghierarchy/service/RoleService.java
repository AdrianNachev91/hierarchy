package com.ing.inghierarchy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.repositories.TeamMemberRepository;
import com.ing.inghierarchy.repositories.ManagerRepository;

@Service
public class RoleService {
	
	private RoleRepository roleRepository;
	private TeamMemberRepository teamMemberRepository;
	private ManagerRepository managerRepository;
	
	@Autowired
	public RoleService(RoleRepository roleRepository, TeamMemberRepository teamMemberRepository, ManagerRepository managerRepository)
	{
		this.roleRepository = roleRepository;
		this.teamMemberRepository = teamMemberRepository;
		this.managerRepository = managerRepository;
	}
	
	public Role createRole(Role role) throws IngHttpException
	{
		Role returnRole = null;
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
		boolean canDelete = true;
		if(!teamMemberRepository.findByRoleId(deleteRole.getId()).isEmpty()) {
			canDelete = false;
		}
		if(!managerRepository.findByRoleId(deleteRole.getId()).isEmpty()) {
			canDelete = false;
		}
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
				exists = !roleRepository.findById(role.getId()).isEmpty();
			}
		}
		
		if(!exists)	{
			exists = roleExists(role.getTitle());
		}
		
		return exists;
	}
}
