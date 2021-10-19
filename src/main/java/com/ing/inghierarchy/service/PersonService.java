package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.repositories.TeamMemberRepository;
import com.ing.inghierarchy.web.ManagerRequest;
import com.ing.inghierarchy.web.TeamMemberRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private RoleRepository roleRepository;
    private ManagerRepository managerRepository;
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    public PersonService(RoleRepository roleRepository, ManagerRepository managerRepository, TeamMemberRepository teamMemberRepository) {
        this.roleRepository = roleRepository;
        this.managerRepository = managerRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public Manager createManager(ManagerRequest managerRequest) {

        checkRoleExists(managerRequest.getRoleId(), "Cannot create a manager with non-existing role");

        Manager manager = new ModelMapper().map(managerRequest, Manager.class);
        return managerRepository.save(manager);
    }

    public Manager updateManager(String id, ManagerRequest managerRequest) {

        checkManagerExists(id);
        checkRoleExists(managerRequest.getRoleId(), "Cannot create a manager with non-existing role");

        Manager manager = new ModelMapper().map(managerRequest, Manager.class);
        manager.setId(id);
        return managerRepository.save(manager);
    }

    public void deleteManager(String id) {

        checkManagerExists(id);

        managerRepository.deleteById(id);
    }

    public TeamMember createTeamMember(TeamMemberRequest teamMemberRequest) {

        checkRoleExists(teamMemberRequest.getRoleId(), "Cannot create a team member with non-existing role");

        TeamMember manager = new ModelMapper().map(teamMemberRequest, TeamMember.class);
        return teamMemberRepository.save(manager);
    }

    public TeamMember updateTeamMember(String id, TeamMemberRequest teamMemberRequest) {

        checkTeamMemberExists(id);
        checkRoleExists(teamMemberRequest.getRoleId(), "Cannot create a team member with non-existing role");

        TeamMember teamMember = new ModelMapper().map(teamMemberRequest, TeamMember.class);
        teamMember.setId(id);
        return teamMemberRepository.save(teamMember);
    }

    public void deleteTeamMember(String id) {

        checkTeamMemberExists(id);

        teamMemberRepository.deleteById(id);
    }

    private void checkRoleExists(String roleId, String s) {
        if (!roleRepository.existsById(roleId)) {
            throw IngHttpException.forbidden(s);
        }
    }

    private void checkManagerExists(String id) {
        if (!managerRepository.existsById(id)) {
            throw IngHttpException.notFound("Manager does not exist");
        }
    }

    private void checkTeamMemberExists(String id) {
        if (!teamMemberRepository.existsById(id)) {
            throw IngHttpException.notFound("Team member does not exist");
        }
    }
}
