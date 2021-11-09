package com.hierarchy.web;

import static org.springframework.http.HttpStatus.CREATED;

import javax.validation.Valid;

import com.hierarchy.web.request.RoleRequest;
import com.hierarchy.repositories.RoleRepository;
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

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.Role;
import com.hierarchy.service.RoleService;

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
		return roleRepository.findById(id).orElseThrow(() -> HierarchyHttpException.notFound("Role not found"));
	}
	
	@PostMapping
    @ResponseStatus(CREATED)
    public Role createRole(@RequestBody @Valid RoleRequest roleRequest) {
        return roleService.createRole(roleRequest);
    }

    @PutMapping("/{id}")
    public Role updateRole(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                                 @RequestBody @Valid RoleRequest roleRequest) {
        return roleService.updateRole(id, roleRequest);
    }
    
    @DeleteMapping("/{id}")
    public void deleteRole(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
		roleService.deleteRole(id);
    }
}