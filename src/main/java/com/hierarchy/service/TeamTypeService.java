package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.Team;
import com.hierarchy.domain.TeamType;
import com.hierarchy.repositories.TeamRepository;
import com.hierarchy.repositories.TeamTypeRepository;
import com.hierarchy.web.TeamTypeRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeamTypeService {

    private final TeamTypeRepository teamTypeRepository;
    private final TeamRepository teamRepository;

    public TeamType createTeamType(TeamTypeRequest teamTypeRequest) {

        TeamType teamType = new ModelMapper().map(teamTypeRequest, TeamType.class);
        return teamTypeRepository.save(teamType);
    }

    public TeamType updateTeamType(String id, TeamTypeRequest teamTypeRequest) {

        checkTeamTypeExists(id);

        TeamType teamType = new ModelMapper().map(teamTypeRequest, TeamType.class);
        teamType.setId(id);
        return teamTypeRepository.save(teamType);
    }

    public void deleteTeamType(String id) {

        checkTeamTypeExists(id);

        List<Team> teamsUsedIn = teamRepository.findAllByTeamType(id);
        if (!teamsUsedIn.isEmpty()) {
            List<String> teamNames = teamsUsedIn.stream().map(Team::getTitle).collect(Collectors.toList());
            throw HierarchyHttpException.badRequest(String.format("Team type is used in the following teams: %s. Cannot be deleted.", String.join(", ", teamNames)));
        }

        teamTypeRepository.deleteById(id);
    }

    private void checkTeamTypeExists(String teamTypeId) {
        if (!teamTypeRepository.existsById(teamTypeId)) {
            throw HierarchyHttpException.notFound("Team type does not exist");
        }
    }
}
