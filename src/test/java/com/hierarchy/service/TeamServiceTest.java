package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.Team;
import com.hierarchy.repositories.EmployeeRepository;
import com.hierarchy.repositories.ManagementChainRepository;
import com.hierarchy.repositories.TeamRepository;
import com.hierarchy.repositories.TeamTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static com.hierarchy.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    private TeamService teamService;

    @Mock
    private ManagementChainRepository managementChainRepository;
    @Mock
    private TeamTypeRepository teamTypeRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(managementChainRepository, teamTypeRepository, teamRepository, employeeRepository);
    }

    @Test
    void createTeam() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(true);
        when(employeeRepository.existsById("leadId")).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);
        var team = new ModelMapper().map(teamRequest, Team.class);
        when(teamRepository.save(team)).thenReturn(team.setId("team-id"));

        // Test
        Team result = teamService.createTeam(teamRequest);

        // Verify
        assertThat(result.getId()).isEqualTo("team-id");
    }

    @Test
    void createTeam_ManagementChainNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.createTeam(teamRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Management chain not found");
        verify(employeeRepository, never()).existsById(anyString());
        verify(teamTypeRepository, never()).existsById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void createTeam_LeadNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(true);
        when(employeeRepository.existsById("leadId")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.createTeam(teamRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Lead not found");
        verify(teamTypeRepository, never()).existsById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void createTeam_TeamTypeNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(true);
        when(employeeRepository.existsById("leadId")).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.createTeam(teamRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Team type does not exist");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(true);
        when(employeeRepository.existsById("leadId")).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);
        var team = new ModelMapper().map(teamRequest, Team.class);
        when(teamRepository.findById("team-id")).thenReturn(Optional.of(team));
        when(teamRepository.save(team)).thenReturn(team.setId("team-id"));

        // Test
        Team result = teamService.updateTeam("team-id", teamRequest);

        // Verify
        assertThat(result.getId()).isEqualTo("team-id");
    }

    @Test
    void updateTeam_TeamNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(true);
        when(employeeRepository.existsById("leadId")).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);
        when(teamRepository.findById("team-id")).thenReturn(Optional.empty());

        // Test
        Throwable e = catchThrowable(() -> teamService.updateTeam("team-id", teamRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Team does not exist");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_ManagementChainNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.updateTeam("team-id", teamRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Management chain not found");
        verify(employeeRepository, never()).existsById(anyString());
        verify(teamTypeRepository, never()).existsById(anyString());
        verify(teamRepository, never()).findById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_LeadNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(true);
        when(employeeRepository.existsById("leadId")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.updateTeam("team-id", teamRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Lead not found");
        verify(teamTypeRepository, never()).existsById(anyString());
        verify(teamRepository, never()).findById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_TeamTypeNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "leadId", "teamType-id");
        when(managementChainRepository.existsById("managedBy-id")).thenReturn(true);
        when(employeeRepository.existsById("leadId")).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.updateTeam("team-id", teamRequest));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(((HierarchyHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Team type does not exist");
        verify(teamRepository, never()).findById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void deleteTeam() {

        // Prepare
        when(teamRepository.existsById("team-id")).thenReturn(true);

        // Test
        teamService.deleteTeam("team-id");

        // Verify
        verify(teamRepository).deleteById("team-id");
    }

    @Test
    void deleteTeam_TeamDoesNotExist() {

        // Prepare
        when(teamRepository.existsById("team-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.deleteTeam("team-id"));

        // Verify
        assertThat(e).isInstanceOf(HierarchyHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Team does not exist");
    }
}