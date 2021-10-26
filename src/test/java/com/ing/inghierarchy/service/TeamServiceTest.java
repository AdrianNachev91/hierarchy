package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.repositories.TeamTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static com.ing.inghierarchy.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    private TeamService teamService;

    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private TeamTypeRepository teamTypeRepository;
    @Mock
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(managerRepository, teamTypeRepository, teamRepository);
    }

    @Test
    void createTeam() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(managerRepository.existsByIdAndLead("managedBy-id", true)).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);
        var team = new ModelMapper().map(teamRequest, Team.class);
        when(teamRepository.save(team)).thenReturn(team.setId("team-id"));

        // Test
        Team result = teamService.createTeam(teamRequest);

        // Verify
        assertThat(result.getId()).isEqualTo("team-id");
    }

    @Test
    void createTeam_ManagerNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(managerRepository.existsByIdAndLead("managedBy-id", true)).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.createTeam(teamRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(((IngHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Manager not found");
        verify(teamTypeRepository, never()).existsById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void createTeam_TeamTypeNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(managerRepository.existsByIdAndLead("managedBy-id", true)).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.createTeam(teamRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(((IngHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Team type does not exist");
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(teamRepository.existsById("team-id")).thenReturn(true);
        when(managerRepository.existsByIdAndLead("managedBy-id", true)).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);
        var team = new ModelMapper().map(teamRequest, Team.class);
        when(teamRepository.save(team)).thenReturn(team.setId("team-id"));

        // Test
        Team result = teamService.updateTeam("team-id", teamRequest);

        // Verify
        assertThat(result.getId()).isEqualTo("team-id");
    }

    @Test
    void updateTeam_TeamNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(teamRepository.existsById("team-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.updateTeam("team-id", teamRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(((IngHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Team does not exist");
        verify(managerRepository, never()).existsByIdAndLead(anyString(), anyBoolean());
        verify(teamTypeRepository, never()).existsById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_ManagerNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(teamRepository.existsById("team-id")).thenReturn(true);
        when(managerRepository.existsByIdAndLead("managedBy-id", true)).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.updateTeam("team-id", teamRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(((IngHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Manager not found");
        verify(teamTypeRepository, never()).existsById(anyString());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    void updateTeam_TeamTypeNotFound() {

        // Prepare
        var teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(teamRepository.existsById("team-id")).thenReturn(true);
        when(managerRepository.existsByIdAndLead("managedBy-id", true)).thenReturn(true);
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamService.updateTeam("team-id", teamRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(((IngHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Team type does not exist");
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
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Team does not exist");
    }
}