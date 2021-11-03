package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.ManagementChain;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.repositories.EmployeeRepository;
import com.ing.inghierarchy.repositories.ManagementChainRepository;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.repositories.TeamTypeRepository;
import com.ing.inghierarchy.web.request.IdsRequest;
import com.ing.inghierarchy.web.request.TeamRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final ManagementChainRepository managementChainRepository;
    private final TeamTypeRepository teamTypeRepository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    public Team createTeam(TeamRequest teamRequest) {

        checkManagementChainExist(teamRequest.getManagedBy());
        checkTeamTypeExists(teamRequest.getTeamType());

        Team team = teamRepository.save(new ModelMapper().map(teamRequest, Team.class));
        managementChainRepository.updateAttachedToTeam(teamRequest.getManagedBy(), true);
        return team;
    }

    public Team updateTeam(String id, TeamRequest teamRequest) {

        checkManagementChainExist(teamRequest.getManagedBy());
        checkTeamTypeExists(teamRequest.getTeamType());


        Team team = teamRepository.findById(id).orElseThrow(() ->
                IngHttpException.notFound("Team does not exist"));
        String oldManagedBy = team.getManagedBy();
        team.setTeamType(teamRequest.getTeamType());
        team.setTitle(teamRequest.getTitle());
        team.setManagedBy(teamRequest.getManagedBy());

        team = teamRepository.save(team);
        if (!oldManagedBy.equals(teamRequest.getManagedBy()) && !teamRepository.existsByManagedBy(oldManagedBy)) {
            ManagementChain oldManagementChain = managementChainRepository.findById(oldManagedBy).orElse(null);
            if (oldManagementChain != null) {
                oldManagementChain.setAttachedToTeam(false);
                managementChainRepository.save(oldManagementChain);
            } else {
                System.out.printf("Old management chain not found. Could not  be detached from team: %s", team.getTitle());
            }
        }

        return teamRepository.save(team);
    }

    public Team addTeamMembers(String id, IdsRequest idsRequest) {

        Team team = teamRepository.findById(id).orElseThrow(() ->
                IngHttpException.notFound("Team does not exist"));

        List<String> nonExistingEmployees = new ArrayList<>();
        for (String employeeId : idsRequest.getIds()) {
            if (!employeeRepository.existsById(employeeId)) {
                nonExistingEmployees.add(employeeId);
            }
        }
        if (!nonExistingEmployees.isEmpty()) {
            throw IngHttpException.notFound(String.format("Employees with ids: %s were not found", String.join(", ", nonExistingEmployees)));
        }

        team.getCrew().addAll(idsRequest.getIds());
        return teamRepository.save(team);
    }

    public Team removeTeamMembers(String id, IdsRequest idsRequest) {

        Team team = teamRepository.findById(id).orElseThrow(() ->
                IngHttpException.notFound("Team does not exist"));

        List<String> nonFoundInTeam = new ArrayList<>();
        for (String employeeId : idsRequest.getIds()) {
            if (!team.getCrew().contains(employeeId)) {
                nonFoundInTeam.add(employeeId);
            }
        }
        System.out.printf("Employees with ids: %s were not found in team%n", String.join(", ", nonFoundInTeam));
        team.getCrew().removeAll(idsRequest.getIds());
        return teamRepository.save(team);
    }

    public void deleteTeam(String id) {

        checkTeamExists(id);

        teamRepository.deleteById(id);
    }

    private void checkManagementChainExist(String managedBy) {
        if (!managementChainRepository.existsById(managedBy)) {
            throw IngHttpException.notFound("Management chain not found");
        }
    }

    private void checkTeamTypeExists(String teamTypeId) {
        if (!teamTypeRepository.existsById(teamTypeId)) {
            throw IngHttpException.notFound("Team type does not exist");
        }
    }

    private void checkTeamExists(String id) {
        if (!teamRepository.existsById(id)) {
            throw IngHttpException.notFound("Team does not exist");
        }
    }
}
