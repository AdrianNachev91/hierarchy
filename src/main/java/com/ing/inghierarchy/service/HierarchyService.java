package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.config.HierarchyProperties;
import com.ing.inghierarchy.domain.*;
import com.ing.inghierarchy.repositories.*;
import com.ing.inghierarchy.web.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class HierarchyService {

    private final TeamRepository teamRepository;
    private final TeamTypeRepository teamTypeRepository;
    private final TeamManagementRepository teamManagementRepository;
    private final RoleRepository roleRepository;
    private final ManagerRepository managerRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final HierarchyProperties hierarchyProperties;

    public HierarchyStructureResponse getHierarchy(String teamTypeTitle, int limit, long offset) {

        long totalNrTeams = teamRepository.count();

        if (totalNrTeams < offset) {
            throw IngHttpException.notFound("No teams found in the range");
        }

        if (offset != 0 && offset < limit) {
            throw IngHttpException.badRequest("Offset that is not 0 cannot be less than limit");
        }

        TeamType teamType = teamTypeRepository.findFirstByTitle(teamTypeTitle).orElseThrow(() -> IngHttpException.notFound("Team type not found"));

        String previous = offset != 0 ? String.format("%s/hierarchy/%s/%d/%d", teamTypeTitle, hierarchyProperties.getDomain(), limit, offset - limit) : null;
        String next = offset + limit <= totalNrTeams ? String.format("%s/hierarchy/%s/%d/%d", teamTypeTitle, hierarchyProperties.getDomain(), limit, offset + limit) : null;

        List<Team> teams = teamRepository.fetchTeamsWithPaginationByTeamType(teamType.getId(), limit, offset);

        var hierarchyStructureResponse = HierarchyStructureResponse.builder().build();

        Set<String> teamManagementsToFetch = teams.stream().map(Team::getManagedBy).collect(toSet());
        List<TeamManagement> teamManagements = teamManagementRepository.findAllByIdIn(teamManagementsToFetch);

        var hierarchyChain = new HierarchyChain();
        hierarchyStructureResponse
                .setHierarchyChain(hierarchyChain)
                .setPrevious(previous)
                .setNext(next);

        for (TeamManagement teamManagement : teamManagements) {
            // todo Ascertain that all managers in chain exist
            List<Manager> managers = managerRepository.findAllByIdIn(teamManagement.getManagerChain());
            ManagerResponse previousManagerResponse = null;
            Manager currentManager = getFirstInChain(teamManagement, managers).orElseThrow(() ->
                    IngHttpException.badRequest("There is no first in chain for management chain: " + teamManagement.getId()));
            boolean firstInChain = true;

            while (teamManagement.getManagerChain().contains(currentManager.getManages())) {
                ManagerResponse managerResponse;
                if (firstInChain && hierarchyChain.getManager() != null) {
                    managerResponse = hierarchyChain.getManager(); // switch to already existing manager, assumption is that first in chain will always be the same for each chain
                } else {
                    managerResponse = new ManagerResponse();
                    managerResponse.setId(currentManager.getId());
                    managerResponse.setCorporateId(currentManager.getCorporateId());
                    managerResponse.setName(currentManager.getName());
                    managerResponse.setRole(roleRepository.findById(currentManager.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle());

                    if (hierarchyChain.getManager() == null) {
                        hierarchyChain.setManager(managerResponse);
                    }
                }

                var manages = currentManager.getManages();
                if (previousManagerResponse != null) {
                    previousManagerResponse.getManages().add(managerResponse);
                }
                previousManagerResponse = managerResponse;
                currentManager = managers.stream().filter(m -> m.getId().equals(manages)).findFirst().orElseThrow(() ->
                        IngHttpException.badRequest("Broken link in management chain: " + teamManagement.getId()));
                hierarchyChain.setManager(managerResponse);
                firstInChain = false;
            }

            // By this point currentManager is set to the team lead
            var managerResponse = new ManagerResponse();
            managerResponse.setId(currentManager.getId());
            managerResponse.setCorporateId(currentManager.getCorporateId());
            managerResponse.setName(currentManager.getName());
            managerResponse.setRole(roleRepository.findById(currentManager.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle());
            List<Team> teamsManaged = teams.stream().filter(t -> t.getManagedBy().equals(teamManagement.getId())).collect(toList());
            for(Team team : teamsManaged) {
                var teamResponse = new TeamResponse();
                teamResponse.setId(team.getId());
                teamResponse.setTitle(team.getTitle());
                for (String teamMemberId : team.getCrew()) {
                    TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElse(null);
                    if (teamMember != null) {
                        var teamMemberResponse = new TeamMemberResponse();
                        teamMemberResponse.setId(teamMember.getId());
                        teamMemberResponse.setName(teamMember.getName());
                        teamMemberResponse.setCorporateId(teamMember.getCorporateId());
                        teamMemberResponse.setRole(roleRepository.findById(teamMember.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle());
                        teamResponse.getCrew().add(teamMemberResponse);
                    }
                }
                managerResponse.getTeamsManaged().add(teamResponse);
            }
        }



        return null;
    }

    private Optional<Manager> getFirstInChain(TeamManagement teamManagement, List<Manager> managers) {
        // todo Check if there is no more than 1 first in chain
        for (Manager manager : managers) {
            if (isNotManagedByAnyone(teamManagement, manager)) {
                return Optional.of(manager);
            }
        }
        return Optional.empty();
    }

    private boolean isNotManagedByAnyone(TeamManagement teamManagement, Manager manager) {
        return !teamManagement.getManagerChain().contains(manager.getManages());
    }
}
