package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.ing.inghierarchy.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
class TeamControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void createTeam() throws Exception {

        // Prepare
        Team team = team("Team name", "managedBy-id", "teamType-id");
        when(teamService.createTeam(new ModelMapper().map(team, TeamRequest.class))).thenReturn(team);

        // Test & Verify
        mockMvc.perform(post("/team")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'title':'Team name'," +
                                "'managedBy':'managedBy-id'," +
                                "'teamType':'teamType-id'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'title':'Team name'," +
                        "'managedBy':'managedBy-id'," +
                        "'teamType':'teamType-id'" +
                        "}")));
    }

    @Test
    void createTeam_MANAGER_NOT_FOUND() throws Exception {

        // Prepare
        TeamRequest teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(teamService.createTeam(teamRequest)).thenThrow(IngHttpException.notFound("Manager not found"));

        // Test & Verify
        mockMvc.perform(post("/team")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'title':'Team name'," +
                                "'managedBy':'managedBy-id'," +
                                "'teamType':'teamType-id'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{'message':'Manager not found'}")));
    }

    @Test
    void createTeam_INVALID() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/team")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'managedBy':''," +
                                "'teamType':''" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'title':'must not be blank'," +
                        "       'managedBy':'must not be blank'," +
                        "       'teamType':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void updateTeam() throws Exception {

        // Prepare
        Team team = team("Team name", "managedBy-id", "teamType-id");
        when(teamService.updateTeam("team-id", new ModelMapper().map(team, TeamRequest.class))).thenReturn(team.setId("team-id"));

        // Test & Verify
        mockMvc.perform(put("/team/{id}", "team-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'title':'Team name'," +
                                "'managedBy':'managedBy-id'," +
                                "'teamType':'teamType-id'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'id':'team-id'," +
                        "'title':'Team name'," +
                        "'managedBy':'managedBy-id'," +
                        "'teamType':'teamType-id'" +
                        "}")));
    }

    @Test
    void updateTeam_TEAM_NOT_FOUND() throws Exception {

        // Prepare
        TeamRequest teamRequest = teamRequest("Team name", "managedBy-id", "teamType-id");
        when(teamService.updateTeam("team-id", teamRequest)).thenThrow(IngHttpException.notFound("Team not found"));

        // Test & Verify
        mockMvc.perform(put("/team/{id}", "team-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'title':'Team name'," +
                                "'managedBy':'managedBy-id'," +
                                "'teamType':'teamType-id'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{'message':'Team not found'}")));
    }

    @Test
    void updateTeam_INVALID() throws Exception {

        // Test & Verify
        mockMvc.perform(put("/team/{id}", "id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'managedBy':''," +
                                "'teamType':''" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'title':'must not be blank'," +
                        "       'managedBy':'must not be blank'," +
                        "       'teamType':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void deleteTeam() throws Exception {

        // Test & Verify
        mockMvc.perform(delete("/team/{id}", "team-1"))
                .andExpect(status().isOk());

        verify(teamService).deleteTeam("team-1");
    }

    @Test
    void deleteTeam_HttpException() throws Exception {

        // Prepare
        doThrow(IngHttpException.notFound("Team not found")).when(teamService).deleteTeam("team-1");

        // Test & Verify
        mockMvc.perform(delete("/team/{id}", "team-1"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Team not found'}"))));
    }
}