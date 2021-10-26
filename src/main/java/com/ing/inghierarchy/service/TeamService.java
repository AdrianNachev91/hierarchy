package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.repositories.TeamTypeRepository;
import com.ing.inghierarchy.web.TeamRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final ManagerRepository managerRepository;
    private final TeamTypeRepository teamTypeRepository;
    private final TeamRepository teamRepository;

    public Team createTeam(TeamRequest teamRequest) {

        checkManagerExists(teamRequest.getManagedBy());
        checkTeamTypeExists(teamRequest.getTeamType());

        Team team = new ModelMapper().map(teamRequest, Team.class);
        return teamRepository.save(team);
    }

    public Team updateTeam(String id, TeamRequest teamRequest) {

        checkTeamExists(id);
        checkManagerExists(teamRequest.getManagedBy());
        checkTeamTypeExists(teamRequest.getTeamType());

        Team team = new ModelMapper().map(teamRequest, Team.class);
        team.setId(id);

        return teamRepository.save(team);
    }

    public void deleteTeam(String id) {

        checkTeamExists(id);

        teamRepository.deleteById(id);
    }

    private void checkManagerExists(String managedBy) {
        if (!managerRepository.existsByIdAndLead(managedBy, true)) {
            throw IngHttpException.notFound("Manager not found");
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
