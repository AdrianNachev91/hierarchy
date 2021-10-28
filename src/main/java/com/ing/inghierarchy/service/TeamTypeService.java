package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.domain.TeamType;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.repositories.TeamTypeRepository;
import com.ing.inghierarchy.web.TeamTypeRequest;
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
            throw IngHttpException.badRequest(String.format("Team type is used in the following teams: %s. Cannot be deleted.", String.join(", ", teamNames)));
        }

        teamTypeRepository.deleteById(id);
    }

    private void checkTeamTypeExists(String teamTypeId) {
        if (!teamTypeRepository.existsById(teamTypeId)) {
            throw IngHttpException.notFound("Team type does not exist");
        }
    }
}
