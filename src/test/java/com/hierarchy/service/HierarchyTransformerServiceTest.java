package com.hierarchy.service;

import com.hierarchy.domain.Team;
import com.hierarchy.repositories.EmployeeRepository;
import com.hierarchy.repositories.RoleRepository;
import com.hierarchy.repositories.TeamTypeRepository;
import com.hierarchy.web.response.HierarchyStructureResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.hierarchy.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HierarchyTransformerServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private TeamTypeRepository teamTypeRepository;

    HierarchyTransformerService hierarchyTransformerService;

    @BeforeEach
    void setUp() {
        hierarchyTransformerService = new HierarchyTransformerService(roleRepository, employeeRepository, teamTypeRepository);
    }

    @Test
    void rawDataToHierarchy() {

        // Prepare
        var team1 = team("team1", "managerChain-id1", "lead-id1", "teamType-id")
                .setCrew(List.of("employeeId1", "employeeId2")).setId("teamId1");
        var team2 = team("team2", "managerChain-id2", "lead-id2", "teamType-id")
                .setCrew(List.of("employeeId3", "employeeId4")).setId("teamId2");
        List<Team> teams = List.of(team1, team2);

        var teamManagement1 = managementChain(true, Set.of(
                managerInChain("manager-id1", "manager-id2"),
                managerInChain("manager-id2", null))
        ).setId("managerChain-id1");
        var teamManagement2 = managementChain(true, Set.of(
                managerInChain("manager-id1", "manager-id3"),
                managerInChain("manager-id3", null))
        ).setId("managerChain-id2");
        var teamManagement3 = managementChain(false, Set.of(
                managerInChain("manager-id1", "manager-id4"),
                managerInChain("manager-id4", null))
        ).setId("managerChain-id3");
        var teamManagements = List.of(teamManagement1, teamManagement2, teamManagement3);

        var manager1 = employee("Manager1", "corporateId7", "roleId5").setId("manager-id1");
        var manager2 = employee("Manager2", "corporateId8", "roleId6").setId("manager-id2");
        var manager3 = employee("Manager3", "corporateId9", "roleId7").setId("manager-id3");
        var manager4 = employee("Manager4", "corporateId10", "roleId7").setId("manager-id4");
        var employee1 = employee("Employee1", "corporateId1", "roleId1").setId("employeeId1");
        var employee2 = employee("Employee2", "corporateId2", "roleId1").setId("employeeId2");
        var employee3 = employee("Employee3", "corporateId3", "roleId2").setId("employeeId3");
        var lead1 = employee("Lead1", "corporateId5", "roleId4").setId("lead-id1");
        var lead2 = employee("Lead2", "corporateId6", "roleId4").setId("lead-id2");

        when(teamTypeRepository.findById("teamType-id")).thenReturn(Optional.of(teamType(" team type")));

        // team 1
        when(employeeRepository.findById("manager-id1")).thenReturn(Optional.of(manager1));
        when(roleRepository.findById("roleId5")).thenReturn(Optional.of(role("role 5")));
        when(employeeRepository.findById("manager-id2")).thenReturn(Optional.of(manager2));
        when(roleRepository.findById("roleId6")).thenReturn(Optional.of(role("role 6")));
        when(employeeRepository.findById("lead-id1")).thenReturn(Optional.of(lead1));
        when(roleRepository.findById("roleId4")).thenReturn(Optional.of(role("role 4")));
        when(employeeRepository.findById("employeeId1")).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById("employeeId2")).thenReturn(Optional.of(employee2));
        when(roleRepository.findById("roleId1")).thenReturn(Optional.of(role("role 1")));

        // team 2
        when(employeeRepository.findById("manager-id3")).thenReturn(Optional.of(manager3));
        when(roleRepository.findById("roleId7")).thenReturn(Optional.of(role("role 7")));
        when(employeeRepository.findById("lead-id2")).thenReturn(Optional.of(lead2));
        when(employeeRepository.findById("employeeId3")).thenReturn(Optional.of(employee3));
        when(roleRepository.findById("roleId2")).thenReturn(Optional.of(role("role 2")));
        when(employeeRepository.findById("employeeId4")).thenReturn(Optional.empty());

        // detached chain
        when(employeeRepository.findById("manager-id4")).thenReturn(Optional.of(manager4));

        HierarchyStructureResponse result = hierarchyTransformerService.rawDataToHierarchy(teams, teamManagements, 10L, "previous page link", "next page link");
        System.out.println();

        assertThat(result.getHierarchyChain().getManager().getName()).isEqualTo("Manager1");
        var chain = result.getHierarchyChain().getManager().getManages();
        assertThat(chain).hasSize(3);
        var chainTeams1 = chain.get(0).getTeamsManaged();
        assertThat(chainTeams1).hasSize(1);
        var chainTeams2 = chain.get(1).getTeamsManaged();
        assertThat(chainTeams2).hasSize(1);
        assertThat(chain.get(2).getTeamsManaged()).hasSize(0);
        assertThat(chainTeams1.get(0).getCrew()).hasSize(2);
        assertThat(chainTeams2.get(0).getCrew()).hasSize(1);
    }
}