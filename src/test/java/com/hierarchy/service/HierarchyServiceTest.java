package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.config.HierarchyProperties;
import com.hierarchy.domain.Team;
import com.hierarchy.repositories.ManagementChainRepository;
import com.hierarchy.repositories.TeamRepository;
import com.hierarchy.repositories.TeamTypeRepository;
import com.hierarchy.web.response.HierarchyStructureResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.hierarchy.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HierarchyServiceTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamTypeRepository teamTypeRepository;
    @Mock
    private ManagementChainRepository teamManagementRepository;
    @Mock
    private HierarchyTransformerService hierarchyTransformerService;

    private HierarchyService hierarchyService;

    @BeforeEach
    void setUp() {
        HierarchyProperties hierarchyProperties = new HierarchyProperties();
        hierarchyProperties.setDomain("http://test");

        hierarchyService = new HierarchyService(teamRepository, teamTypeRepository, teamManagementRepository, hierarchyTransformerService, hierarchyProperties);
    }

    @Test
    void getHierarchy() {

        // Prepare
        prepareHappyPathWithLimitAndOffset( 2, 2, "http://test/hierarchy/team%20type/2/4", "http://test/hierarchy/team%20type/2/0");

        // Test
        HierarchyStructureResponse result = hierarchyService.getHierarchy("team type", 2, 2);

        // Verify
        assertThat(result.getTotal()).isEqualTo(10);
    }

    @Test
    void getHierarchy_NoPrevious() {

        // Prepare
        prepareHappyPathWithLimitAndOffset(2, 0, "http://test/hierarchy/team%20type/2/2", null);

        // Test
        HierarchyStructureResponse result = hierarchyService.getHierarchy("team type", 2, 0);

        // Verify
        assertThat(result.getTotal()).isEqualTo(10);
    }

    @Test
    void getHierarchy_NoNext() {

        // Prepare
        prepareHappyPathWithLimitAndOffset(3, 9, null, "http://test/hierarchy/team%20type/3/6");

        // Test
        HierarchyStructureResponse result = hierarchyService.getHierarchy("team type", 3, 9);

        // Verify
        assertThat(result.getTotal()).isEqualTo(10);
    }

    @Test
    void getHierarchy_TeamTypeNotFound() {

        // Prepare
        when(teamTypeRepository.findFirstByTitle("team type")).thenReturn(Optional.empty());

        // Test
        Throwable e = catchThrowable(() -> hierarchyService.getHierarchy("team type", 3, 9));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat((e).getMessage()).isEqualTo("Team type not found");
    }

    @Test
    void getHierarchy_NoTeamsInTheRange() {

        // Prepare
        var teamType = teamType("team type").setId("teamType-id");
        when(teamTypeRepository.findFirstByTitle("team type")).thenReturn(Optional.of(teamType));
        when(teamRepository.countAllByTeamType("teamType-id")).thenReturn(8L);

        // Test
        Throwable e = catchThrowable(() -> hierarchyService.getHierarchy("team type", 3, 9));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat((e).getMessage()).isEqualTo("No teams found in the range");
    }

    @Test
    void getHierarchy_InvalidLimitAndOffset() {

        // Prepare
        var teamType = teamType("team type").setId("teamType-id");
        when(teamTypeRepository.findFirstByTitle("team type")).thenReturn(Optional.of(teamType));
        when(teamRepository.countAllByTeamType("teamType-id")).thenReturn(8L);

        // Test
        Throwable e = catchThrowable(() -> hierarchyService.getHierarchy("team type", 3, 8));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(400);
        assertThat((e).getMessage()).isEqualTo("Offset must be 0 or multiple of the limit");
    }

    private void prepareHappyPathWithLimitAndOffset(int limit, long offset, String expectedNext, String expectedPrevious) {

        var teamType = teamType("team type").setId("teamType-id");
        when(teamTypeRepository.findFirstByTitle("team type")).thenReturn(Optional.of(teamType));
        when(teamRepository.countAllByTeamType("teamType-id")).thenReturn(10L);

        var team1 = team("team1", "managerChain-id1", "lead-id1", "teamType-id")
                .setCrew(List.of("employee-id1", "employee-id2"));
        var team2 = team("team2", "managerChain-id2", "lead-id2", "teamType-id")
                .setCrew(List.of("employee-id3", "employee-id4"));
        List<Team> teams = List.of(team1, team2);
        when(teamRepository.fetchTeamsWithPaginationByTeamType("teamType-id", limit, offset)).thenReturn(teams);

        var teamManagement1 = managementChain(true, Set.of(
                managerInChain("manager-id1", "manager-id2"),
                managerInChain("manager-id2", null))
        ).setId("managerChain-id1");
        var teamManagement2 = managementChain(true, Set.of(
                managerInChain("manager-id1", "manager-id3"),
                managerInChain("manager-id3", null))
        ).setId("managerChain-id2");
        var teamManagement3 = managementChain(false, Set.of(
                managerInChain("manager-id1", "manager-id3"),
                managerInChain("manager-id3", null))
        ).setId("managerChain-id3");
        when(teamManagementRepository.findAllByIdIn(Set.of("managerChain-id1", "managerChain-id2"))).thenReturn(new ArrayList<>(List.of(teamManagement1, teamManagement2)));
        when(teamManagementRepository.findAllByAttachedToTeam(false)).thenReturn(List.of(teamManagement3));

        // No need to build a whole structure here.
        // All we need to check is that what we set as expected is returned
        var hierarchyStructureResponse = HierarchyStructureResponse.builder().total(10L).build();

        when(hierarchyTransformerService.rawDataToHierarchy(teams, List.of(teamManagement1, teamManagement2, teamManagement3), 10L,
                expectedNext, expectedPrevious)).thenReturn(hierarchyStructureResponse);
    }

}