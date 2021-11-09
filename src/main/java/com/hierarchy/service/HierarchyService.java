package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.config.HierarchyProperties;
import com.hierarchy.domain.*;
import com.hierarchy.repositories.*;
import com.hierarchy.web.response.*;
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
    private final ManagementChainRepository teamManagementRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final HierarchyProperties hierarchyProperties;

    public HierarchyStructureResponse getHierarchy(String teamTypeTitle, int limit, long offset) {

        long totalNrTeams = teamRepository.count();

        if (totalNrTeams < offset) {
            throw HierarchyHttpException.notFound("No teams found in the range");
        }

        if (offset != 0 && offset < limit) {
            throw HierarchyHttpException.badRequest("Offset that is not 0 cannot be less than limit");
        }

        TeamType teamType = teamTypeRepository.findFirstByTitle(teamTypeTitle).orElseThrow(() -> HierarchyHttpException.notFound("Team type not found"));

        String previous = offset != 0 ? String.format("%s/hierarchy/%s/%d/%d", hierarchyProperties.getDomain(), teamTypeTitle, limit, offset - limit) : null;
        String next = offset + limit < totalNrTeams ? String.format("%s/hierarchy/%s/%d/%d", hierarchyProperties.getDomain(), teamTypeTitle, limit, offset + limit) : null;

        List<Team> teams = teamRepository.fetchTeamsWithPaginationByTeamType(teamType.getId(), limit, offset);

        var hierarchyStructureResponse = HierarchyStructureResponse.builder().build();

        Set<String> teamManagementsToFetch = teams.stream().map(Team::getManagedBy).collect(toSet());
        List<ManagementChain> teamManagements = teamManagementRepository.findAllByIdIn(teamManagementsToFetch);
        teamManagements.addAll(teamManagementRepository.findAllByAttachedToTeam(false)); // Always see chains that have no teams

        var hierarchyChain = new HierarchyChain();
        hierarchyStructureResponse
                .setHierarchyChain(hierarchyChain)
                .setPrevious(previous)
                .setNext(next);

        for (ManagementChain teamManagement : teamManagements) {
            ManagerResponse previousManagerResponse = null;
            ManagementChain.ManagerInChain currentManager = getFirstInChain(teamManagement).orElseThrow(() ->
                    HierarchyHttpException.badRequest("There is no first in chain for management chain: " + teamManagement.getId()));
            boolean firstInChain = true;

            while (notLastInChain(teamManagement, currentManager)) {
                Employee currentManagerDetails = employeeRepository.findById(currentManager.getManagerId()).orElseThrow(() ->
                        HierarchyHttpException.notFound("Employee not found"));
                ManagerResponse managerResponse;
                if (firstInChain && hierarchyChain.getManager() != null) {
                    managerResponse = hierarchyChain.getManager(); // switch to already existing manager, assumption is that first in chain will always be the same for each chain
                    hierarchyChain.setManager(managerResponse);
                } else {
                    managerResponse = new ManagerResponse();
                    managerResponse.setId(currentManagerDetails.getId());
                    managerResponse.setCorporateId(currentManagerDetails.getCorporateId());
                    managerResponse.setName(currentManagerDetails.getName());
                    managerResponse.setRole(roleRepository.findById(currentManagerDetails.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle());

                    if (hierarchyChain.getManager() == null) {
                        hierarchyChain.setManager(managerResponse);
                    }
                }

                var manages = currentManager.getManages();
                if (previousManagerResponse != null) {
                    previousManagerResponse.getManages().add(managerResponse);
                }
                previousManagerResponse = managerResponse;
                currentManager = teamManagement.getManagersChain().stream().filter(m -> m.getManagerId().equals(manages)).findFirst().orElseThrow(() ->
                        HierarchyHttpException.badRequest("Broken link in management chain: " + teamManagement.getId()));
                firstInChain = false;
            }

            // By this point currentManager is set to the team lead or last manager in the chain
            Employee currentManagerDetails = employeeRepository.findById(currentManager.getManagerId()).orElseThrow(() ->
                    HierarchyHttpException.notFound("Employee not found"));
            var managerResponse = new ManagerResponse();
            managerResponse.setId(currentManagerDetails.getId());
            managerResponse.setCorporateId(currentManagerDetails.getCorporateId());
            managerResponse.setName(currentManagerDetails.getName());
            managerResponse.setRole(roleRepository.findById(currentManagerDetails.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle());
            if (previousManagerResponse != null) {
                previousManagerResponse.getManages().add(managerResponse);
            }
            List<Team> teamsManaged = teams.stream().filter(t -> t.getManagedBy().equals(teamManagement.getId())).collect(toList());
            for(Team team : teamsManaged) {
                var teamResponse = new TeamResponse();
                teamResponse.setId(team.getId());
                teamResponse.setTitle(team.getTitle());
                teamResponse.setTeamType(teamTypeRepository.findById(team.getTeamType()).orElse(TeamType.builder().title("Not assigned").build()).getTitle());

                Employee lead = employeeRepository.findById(team.getLeadId()).orElseThrow(() ->
                        HierarchyHttpException.notFound(String.format("Team lead for team: %s not found", team.getTitle())));
                var leadResponse = TeamMemberResponse.builder()
                        .id(lead.getId())
                        .name(lead.getName())
                        .corporateId(lead.getCorporateId())
                        .role(roleRepository.findById(lead.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle())
                        .build();
                teamResponse.setLead(leadResponse);

                for (String teamMemberId : team.getCrew()) {
                    Employee teamMember = employeeRepository.findById(teamMemberId).orElse(null);
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

        return hierarchyStructureResponse;
    }

    private boolean notLastInChain(ManagementChain teamManagement, ManagementChain.ManagerInChain currentManager) {
        return teamManagement.getManagersChain().stream().map(ManagementChain.ManagerInChain::getManagerId).collect(toSet()).contains(currentManager.getManages());
    }

    private Optional<ManagementChain.ManagerInChain> getFirstInChain(ManagementChain teamManagement) {
        // todo Check if there is no more than 1 first in chain
        for (ManagementChain.ManagerInChain managerInChain : teamManagement.getManagersChain()) {
            if (isNotManagedByAnyone(teamManagement, managerInChain)) {
                return Optional.of(managerInChain);
            }
        }
        return Optional.empty();
    }

    private boolean isNotManagedByAnyone(ManagementChain teamManagement, ManagementChain.ManagerInChain managerInChain) {
        return !teamManagement.getManagersChain().stream().map(ManagementChain.ManagerInChain::getManages).collect(toSet()).contains(managerInChain.getManagerId());
    }
}
