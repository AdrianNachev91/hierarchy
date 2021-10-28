package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.repositories.TeamMemberRepository;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.web.request.ManagerRequest;
import com.ing.inghierarchy.web.request.TeamMemberRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final RoleRepository roleRepository;
    private final ManagerRepository managerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

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

        List<Team> teamsManaged = teamRepository.findAllByManagedBy(id);
        if (!teamsManaged.isEmpty()) {
            List<String> teamNames = teamsManaged.stream().map(Team::getTitle).collect(Collectors.toList());
            throw IngHttpException.badRequest(String.format("Manager is a leader of the following teams: %s. Cannot be deleted.", String.join(", ", teamNames)));
        }

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

        List<Team> teamsMemberOf = teamRepository.findAllByCrewContaining(id);
        if (!teamsMemberOf.isEmpty()) {
            List<String> teamNames = teamsMemberOf.stream().map(Team::getTitle).collect(Collectors.toList());
            throw IngHttpException.badRequest(String.format("Team member is a member of the following teams: %s. Cannot be deleted.", String.join(", ", teamNames)));
        }

        teamMemberRepository.deleteById(id);
    }

    private void checkRoleExists(String roleId, String s) {
        if (!roleRepository.existsById(roleId)) {
            throw IngHttpException.badRequest(s);
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
