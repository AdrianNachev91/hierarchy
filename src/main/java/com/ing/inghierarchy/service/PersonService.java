package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.web.ManagerRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private RoleRepository roleRepository;
    private ManagerRepository managerRepository;

    @Autowired
    public PersonService(RoleRepository roleRepository, ManagerRepository managerRepository) {
        this.roleRepository = roleRepository;
        this.managerRepository = managerRepository;
    }

    public Manager createManager(ManagerRequest managerRequest) {

        if (!roleRepository.existsById(managerRequest.getRoleId())) {
            throw IngHttpException.forbidden("Cannot create a manager with non-existing role");
        }

        Manager manager = new ModelMapper().map(managerRequest, Manager.class);
        return managerRepository.save(manager);
    }

    public Manager updateManager(String id, ManagerRequest managerRequest) {

        if (!managerRepository.existsById(id)) {
            throw IngHttpException.notFound("Manager does not exist");
        }

        if (!roleRepository.existsById(managerRequest.getRoleId())) {
            throw IngHttpException.forbidden("Cannot create a manager with non-existing role");
        }

        Manager manager = new ModelMapper().map(managerRequest, Manager.class);
        manager.setId(id);
        return managerRepository.save(manager);
    }

    public void deleteManager(String id) {

        if (!managerRepository.existsById(id)) {
            throw IngHttpException.notFound("Manager does not exist");
        }

        managerRepository.deleteById(id);
    }
}
