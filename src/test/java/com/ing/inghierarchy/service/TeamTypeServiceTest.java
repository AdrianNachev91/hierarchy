package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.TeamType;
import com.ing.inghierarchy.repositories.TeamRepository;
import com.ing.inghierarchy.repositories.TeamTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static com.ing.inghierarchy.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamTypeServiceTest {

    private TeamTypeService teamTypeService;

    @Mock
    private TeamTypeRepository teamTypeRepository;
    @Mock
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamTypeService = new TeamTypeService(teamTypeRepository, teamRepository);
    }

    @Test
    void createTeamType() {

        // Prepare
        var teamTypeRequest = teamTypeRequest("Title");
        var teamType = new ModelMapper().map(teamTypeRequest, TeamType.class);
        when(teamTypeRepository.save(teamType)).thenReturn(teamType.setId("teamType-id"));

        // Test
        TeamType result = teamTypeService.createTeamType(teamTypeRequest);

        // Verify
        assertThat(result.getId()).isEqualTo("teamType-id");
    }

    @Test
    void updateTeamType() {

        // Prepare
        var teamTypeRequest = teamTypeRequest("Title");
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);
        var teamType = new ModelMapper().map(teamTypeRequest, TeamType.class);
        when(teamTypeRepository.save(teamType)).thenReturn(teamType.setId("teamType-id"));

        // Test
        TeamType result = teamTypeService.updateTeamType("teamType-id", teamTypeRequest);

        // Verify
        assertThat(result.getId()).isEqualTo("teamType-id");
    }

    @Test
    void updateTeamType_TeamTypeNotFound() {

        // Prepare
        var teamTypeRequest = teamTypeRequest("Title");
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamTypeService.updateTeamType("teamType-id", teamTypeRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(((IngHttpException)e).getHttpStatus()).isEqualTo(404);
        assertThat(e.getMessage()).isEqualTo("Team type does not exist");
        verify(teamTypeRepository, never()).save(any(TeamType.class));
    }

    @Test
    void deleteTeamType() {

        // Prepare
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);

        // Test
        teamTypeService.deleteTeamType("teamType-id");

        // Verify
        verify(teamTypeRepository).deleteById("teamType-id");
    }

    @Test
    void deleteTeamType_TeamTypeDoesNotExist() {

        // Prepare
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> teamTypeService.deleteTeamType("teamType-id"));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Team type does not exist");
    }

    @Test
    void deleteTeamType_NotAllowed() {

        // Prepare
        when(teamTypeRepository.existsById("teamType-id")).thenReturn(true);
        var team1 = team("team 1", null, "teamType-id");
        var team2 = team("team 2", null, "teamType-id");
        var team3 = team("team 3", null, "teamType-id");
        when(teamRepository.findAllByTeamType("teamType-id")).thenReturn(List.of(team1, team2, team3));

        // Test
        Throwable e = catchThrowable(() -> teamTypeService.deleteTeamType("teamType-id"));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Team type is used in the following teams: team 1, team 2, team 3. Cannot be deleted.");
        assertThat(((IngHttpException)e).getHttpStatus()).isEqualTo(400);
    }
}