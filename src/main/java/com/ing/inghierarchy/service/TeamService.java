package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.repositories.ManagementChainRepository;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.repositories.TeamTypeRepository;
import com.ing.inghierarchy.web.request.TeamRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final ManagementChainRepository managementChainRepository;
    private final TeamTypeRepository teamTypeRepository;
    private final TeamRepository teamRepository;

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
        team.setTeamType(teamRequest.getTeamType());
        team.setTitle(teamRequest.getTitle());
        team.setManagedBy(teamRequest.getManagedBy());

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
