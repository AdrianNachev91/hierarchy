package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.*;
import com.hierarchy.repositories.EmployeeRepository;
import com.hierarchy.repositories.RoleRepository;
import com.hierarchy.repositories.TeamTypeRepository;
import com.hierarchy.web.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class HierarchyTransformerService {

    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamTypeRepository teamTypeRepository;

    public HierarchyStructureResponse rawDataToHierarchy(List<Team> teams, List<ManagementChain> teamManagements, long totalNrTeams, String nextPage, String previousPage) {
        var hierarchyStructureResponse = HierarchyStructureResponse.builder().total(totalNrTeams).build();

        var hierarchyChain = new HierarchyChain();
        hierarchyStructureResponse
                .setHierarchyChain(hierarchyChain)
                .setPrevious(previousPage)
                .setNext(nextPage);

        for (ManagementChain teamManagement : teamManagements) {
            populateHierarchyChain(hierarchyChain, teamManagement, teams);
        }

        return hierarchyStructureResponse;
    }

    private void populateHierarchyChain(HierarchyChain hierarchyChain, ManagementChain teamManagement, List<Team> teams) {
        ManagerResponse previousManagerResponse = null;
        ManagementChain.ManagerInChain currentManager = getFirstInChain(teamManagement).orElseThrow(() ->
                HierarchyHttpException.badRequest("There is no first in chain for management chain: " + teamManagement.getId()));
        boolean firstInChain = true;

        while (notLastInChain(teamManagement, currentManager)) {
            ManagerResponse managerResponse;
            if (firstInChain && hierarchyChain.getManager() != null) {
                managerResponse = hierarchyChain.getManager(); // switch to already existing manager, assumption is that first in chain will always be the same for each chain
//                hierarchyChain.setManager(managerResponse);
            } else {
                managerResponse = managerResponseFromEmployeeId(currentManager.getManagerId());

                if (hierarchyChain.getManager() == null) {
                    hierarchyChain.setManager(managerResponse);
                }
            }

            var manages = currentManager.getManages();
            if (previousManagerResponse != null) {
                if (managerResponseIsNotContained(previousManagerResponse, managerResponse)) {
                    previousManagerResponse.getManages().add(managerResponse);
                } else {
                    String responseId = managerResponse.getId();
                    // we already know it is contained
                    // noinspection OptionalGetWithoutIsPresent
                    managerResponse = previousManagerResponse.getManages().stream().filter(m -> m.getId().equals(responseId)).findFirst().get();
                }
            }
            previousManagerResponse = managerResponse;
            currentManager = teamManagement.getManagersChain().stream().filter(m -> m.getManagerId().equals(manages)).findFirst().orElseThrow(() ->
                    HierarchyHttpException.badRequest("Broken link in management chain: " + teamManagement.getId())); // get next manager
            firstInChain = false;
        }

        // By this point currentManager is set to the team lead or last manager in the chain
        finalizeCurrentHierarchy(previousManagerResponse, currentManager, teamManagement, teams);
    }

    private Optional<ManagementChain.ManagerInChain> getFirstInChain(ManagementChain teamManagement) {
        Optional<ManagementChain.ManagerInChain> firstInChain = Optional.empty();
        for (ManagementChain.ManagerInChain managerInChain : teamManagement.getManagersChain()) {
            if (isNotManagedByAnyone(teamManagement, managerInChain)) {
                if (firstInChain.isEmpty()) {
                    firstInChain = Optional.of(managerInChain);
                } else {
                    throw HierarchyHttpException.badRequest(String.format("There are more than 1 first in chain in: %s", teamManagement.getId()));
                }
            }
        }

        return firstInChain;
    }

    private boolean isNotManagedByAnyone(ManagementChain teamManagement, ManagementChain.ManagerInChain managerInChain) {
        return !teamManagement.getManagersChain().stream().map(ManagementChain.ManagerInChain::getManages).collect(toSet()).contains(managerInChain.getManagerId());
    }

    private boolean notLastInChain(ManagementChain teamManagement, ManagementChain.ManagerInChain currentManager) {
        return teamManagement.getManagersChain().stream().map(ManagementChain.ManagerInChain::getManagerId).collect(toSet()).contains(currentManager.getManages());
    }

    private ManagerResponse managerResponseFromEmployeeId(String employeeId) {

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                HierarchyHttpException.notFound("Employee not found"));

        return ManagerResponse.builder()
                .id(employee.getId())
                .corporateId(employee.getCorporateId())
                .name(employee.getName())
                .role(roleRepository.findById(employee.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle()).build();
    }

    private void finalizeCurrentHierarchy(ManagerResponse previousManagerResponse, ManagementChain.ManagerInChain currentManager, ManagementChain teamManagement, List<Team> teams) {
        var managerResponse = managerResponseFromEmployeeId(currentManager.getManagerId());
        if (previousManagerResponse != null && managerResponseIsNotContained(previousManagerResponse, managerResponse)) {
            if (managerResponseIsNotContained(previousManagerResponse, managerResponse)) {
                previousManagerResponse.getManages().add(managerResponse);
            } else {
                String responseId = managerResponse.getId();
                // we already know it is contained
                // noinspection OptionalGetWithoutIsPresent
                managerResponse = previousManagerResponse.getManages().stream().filter(m -> m.getId().equals(responseId)).findFirst().get();
            }
        }
        List<Team> teamsManaged = teams.stream().filter(t -> t.getManagedBy().equals(teamManagement.getId())).collect(toList());
        for(Team team : teamsManaged) {
            var teamResponse = teamToTeamResponse(team);
            managerResponse.getTeamsManaged().add(teamResponse);
        }
    }

    private boolean managerResponseIsNotContained(ManagerResponse previousManagerResponse, ManagerResponse managerResponse) {
        return !previousManagerResponse.getManages().stream().map(ManagerResponse::getId).collect(toList()).contains(managerResponse.getId());
    }

    private TeamResponse teamToTeamResponse(Team team) {
        var teamResponse = new TeamResponse();
        teamResponse.setId(team.getId());
        teamResponse.setTitle(team.getTitle());
        teamResponse.setTeamType(teamTypeRepository.findById(team.getTeamType()).orElse(TeamType.builder().title("Not assigned").build()).getTitle());


        var leadResponse = leadResponseFromEmployeeId(team.getLeadId(), team.getTitle());
        teamResponse.setLead(leadResponse);

        for (String teamMemberId : team.getCrew()) {
            var teamMemberResponse = teamMemberResponseFromEmployeeId(teamMemberId);
            if (teamMemberResponse != null) {
                teamResponse.getCrew().add(teamMemberResponse);
            }
        }
        return teamResponse;
    }

    private TeamMemberResponse leadResponseFromEmployeeId(String employeeId, String teamTitle) {

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                HierarchyHttpException.notFound(String.format("Team lead for team: %s not found", teamTitle)));

        return buildEmployeeResponse(employee);
    }

    private TeamMemberResponse teamMemberResponseFromEmployeeId(String employeeId) {

        Employee employee = employeeRepository.findById(employeeId).orElse(null);

        if (employee != null) {
            return buildEmployeeResponse(employee);
        }
        return null;
    }

    private TeamMemberResponse buildEmployeeResponse(Employee employee) {
        return TeamMemberResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .corporateId(employee.getCorporateId())
                .role(roleRepository.findById(employee.getRoleId()).orElse(Role.builder().title("Not assigned").build()).getTitle())
                .build();
    }
}
