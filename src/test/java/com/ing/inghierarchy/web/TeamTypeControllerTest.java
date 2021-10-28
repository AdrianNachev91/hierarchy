package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.TeamType;
import com.ing.inghierarchy.service.TeamTypeService;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
class TeamTypeControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private TeamTypeService teamTypeService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void createTeamType() throws Exception {

        // Prepare
        TeamType teamType = teamType("Title");
        when(teamTypeService.createTeamType(new ModelMapper().map(teamType, TeamTypeRequest.class))).thenReturn(teamType);

        // Test & Verify
        mockMvc.perform(post("/team-type")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':'Title'}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{'title':'Title'}")));
    }

    @Test
    void createTeamType_INVALID() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/team-type")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':''}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'title':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void updateTeamType() throws Exception {

        // Prepare
        TeamType teamType = teamType("Title");
        when(teamTypeService.updateTeamType("teamType-id", new ModelMapper().map(teamType, TeamTypeRequest.class))).thenReturn(teamType.setId("teamType-id"));

        // Test & Verify
        mockMvc.perform(put("/team-type/{id}", "teamType-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':'Title'}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'id':'teamType-id'," +
                        "'title':'Title'" +
                        "}")));
    }

    @Test
    void updateTeamType_TEAM_TYPE_NOT_FOUND() throws Exception {

        // Prepare
        TeamTypeRequest teamTypeRequest = teamTypeRequest("Title");
        when(teamTypeService.updateTeamType("teamType-id", teamTypeRequest)).thenThrow(IngHttpException.notFound("Team type not found"));

        // Test & Verify
        mockMvc.perform(put("/team-type/{id}", "teamType-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':'Title'}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{'message':'Team type not found'}")));
    }

    @Test
    void updateTeamType_INVALID() throws Exception {

        // Test & Verify
        mockMvc.perform(put("/team-type/{id}", "id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':''}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'title':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void deleteTeamType() throws Exception {

        // Test & Verify
        mockMvc.perform(delete("/team-type/{id}", "teamType-id"))
                .andExpect(status().isOk());

        verify(teamTypeService).deleteTeamType("teamType-id");
    }

    @Test
    void deleteTeamType_HttpException() throws Exception {

        // Prepare
        doThrow(IngHttpException.notFound("Team type not found")).when(teamTypeService).deleteTeamType("teamType-id");

        // Test & Verify
        mockMvc.perform(delete("/team-type/{id}", "teamType-id"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Team type not found'}"))));
    }
}