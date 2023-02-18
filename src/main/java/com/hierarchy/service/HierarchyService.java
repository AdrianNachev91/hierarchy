package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.config.HierarchyProperties;
import com.hierarchy.domain.*;
import com.hierarchy.repositories.*;
import com.hierarchy.web.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class HierarchyService {

    private final TeamRepository teamRepository;
    private final TeamTypeRepository teamTypeRepository;
    private final ManagementChainRepository teamManagementRepository;
    private final HierarchyTransformerService hierarchyTransformerService;
    private final HierarchyProperties hierarchyProperties;

    public HierarchyStructureResponse getHierarchy(String teamTypeTitle, int limit, long offset) {

        TeamType teamType = teamTypeRepository.findFirstByTitle(teamTypeTitle).orElseThrow(() -> HierarchyHttpException.notFound("Team type not found"));

        long totalNrTeams = teamRepository.countAllByTeamType(teamType.getId());

        validateLimitAndOffset(limit, offset, totalNrTeams);

        String previous = offset != 0 ? String.format("%s/hierarchy/%s/%d/%d", hierarchyProperties.getDomain(), UriUtils.encode(teamTypeTitle, "UTF-8"), limit, offset - limit) : null;
        String next = offset + limit < totalNrTeams ? String.format("%s/hierarchy/%s/%d/%d", hierarchyProperties.getDomain(), UriUtils.encode(teamTypeTitle, "UTF-8"), limit, offset + limit) : null;

        List<Team> teams = teamRepository.fetchTeamsWithPaginationByTeamType(teamType.getId(), limit, offset);

        Set<String> teamManagementsToFetch = teams.stream().map(Team::getManagedBy).collect(toSet());
        List<ManagementChain> teamManagements = teamManagementRepository.findAllByIdIn(teamManagementsToFetch);
        teamManagements.addAll(teamManagementRepository.findAllByAttachedToTeam(false)); // Always see chains that have no teams

        return hierarchyTransformerService.rawDataToHierarchy(teams, teamManagements, totalNrTeams, next, previous);
    }

    private void validateLimitAndOffset(int limit, long offset, long totalNrTeams) {
        if (totalNrTeams < offset) {
            throw HierarchyHttpException.notFound("No teams found in the range");
        }

        if (offset % limit != 0) {
            throw HierarchyHttpException.badRequest("Offset must be 0 or multiple of the limit");
        }
    }


}
