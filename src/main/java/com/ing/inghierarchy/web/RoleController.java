package com.ing.inghierarchy.web;

import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.service.RoleService;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/role")
public class RoleController {

	private RoleService roleService;
	private RoleRepository roleRepository;
	
	@Autowired
	public RoleController(RoleService roleService, RoleRepository roleRepository)
	{
		this.roleRepository = roleRepository;
		this.roleService = roleService;
	}
	
	@GetMapping(value="/{id}")
	public Role getRole(@Parameter(name = "id", required = true) @PathVariable("id") String id)
	{
		return roleRepository.findById(id).orElseThrow(() -> IngHttpException.notFound("Role not found"));
	}
	
	@PostMapping("/")
    @ResponseStatus(CREATED)
    public Role createRole(@RequestBody @Valid Role role) {
        return roleService.createRole(role);
    }

    @PutMapping("/{id}")
    public Role updateRole(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                                 @RequestBody @Valid Role role) {
        return roleService.updateRole(id, role);
    }
    
    @DeleteMapping("/{id}")
    public boolean deleteRole(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
		return roleService.deleteRole(id);
    }
}