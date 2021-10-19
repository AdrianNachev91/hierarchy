package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.TeamMemberRepository;
import com.ing.inghierarchy.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.ing.inghierarchy.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class PersonControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private ManagerRepository managerRepository;
    @MockBean
    private PersonService personService;
    @MockBean
    private TeamMemberRepository teamMemberRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void getManager() throws Exception {

        // Prepare
        var manager = Manager.builder().id("1").build();
        when(managerRepository.findById("1")).thenReturn(Optional.of(manager));

        // Test & Verify
        mockMvc.perform(get("/person/manager/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect((content().json(json("{'id':'1'}"))));
    }

    @Test
    void getManager_HttpException() throws Exception {

        // Prepare
        when(managerRepository.findById("1")).thenThrow(IngHttpException.notFound("Manager not found"));

        // Test & Verify
        mockMvc.perform(get("/person/manager/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Manager not found'}"))));
    }

    @Test
    void createManager() throws Exception {

        // Prepare
        Manager manager = manager("First Last", true, "employee", "1", "1");
        when(personService.createManager(new ModelMapper().map(manager, ManagerRequest.class))).thenReturn(manager);

        // Test & Verify
        mockMvc.perform(post("/person/manager")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'lead':true," +
                                "'manages':'employee'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(content().json(json("{" +
                        "'name':'First Last'," +
                        "'lead':true," +
                        "'manages':'employee'," +
                        "'corporateId':'1'," +
                        "'roleId':'1'" +
                        "}")));
    }

    @Test
    void createManager_InvalidRequest() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/person/manager")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'lead':true," +
                                "'manages':'employee'," +
                                "'corporateId':null," +
                                "'roleId':null" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'roleId':'must not be blank'," +
                        "       'name':'must not be blank'," +
                        "       'corporateId':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void updateManager() throws Exception {

        // Prepare
        Manager manager = manager("First Last", true, "employee", "1", "1");
        manager.setId("manager-id");
        when(personService.updateManager("manager-id", new ModelMapper().map(manager, ManagerRequest.class))).thenReturn(manager);

        // Test & Verify
        mockMvc.perform(put("/person/manager/{id}", "manager-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'lead':true," +
                                "'manages':'employee'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("{" +
                        "'id':'manager-id'," +
                        "'name':'First Last'," +
                        "'lead':true," +
                        "'manages':'employee'," +
                        "'corporateId':'1'," +
                        "'roleId':'1'" +
                        "}")));
    }

    @Test
    void updateManager_ManagerDoesNotExist() throws Exception {

        // Prepare
        Manager manager = manager("First Last", true, "employee", "1", "1");
        manager.setId("manager-id");
        when(personService.updateManager("manager-id", new ModelMapper().map(manager, ManagerRequest.class))).thenThrow(IngHttpException.notFound("Manager does not exist"));

        // Test & Verify
        mockMvc.perform(put("/person/manager/{id}", "manager-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'lead':true," +
                                "'manages':'employee'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(json("{'message':'Manager does not exist'}")));
    }

    @Test
    void updateManager_InvalidRequest() throws Exception {

        // Test & Verify
        mockMvc.perform(put("/person/manager/{id}", "manager-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'lead':true," +
                                "'manages':'employee'," +
                                "'corporateId':null," +
                                "'roleId':null" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'roleId':'must not be blank'," +
                        "       'name':'must not be blank'," +
                        "       'corporateId':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void createManager_MalformedJson() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/person/manager")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':true," +
                                "'lead':'yes'," +
                                "'manages':true," +
                                "'corporateId':true," +
                                "'roleId':true" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json("{" +
                        "'message':'Malformed JSON request'" +
                        "}")));
    }

    @Test
    void deleteManager() throws Exception {

        // Test & Verify
        mockMvc.perform(delete("/person/manager/{id}", "1"))
                .andExpect(status().isOk());

        verify(personService).deleteManager("1");
    }

    @Test
    void deleteManager_HttpException() throws Exception {

        // Prepare
        doThrow(IngHttpException.notFound("Manager not found")).when(personService).deleteManager("1");

        // Test & Verify
        mockMvc.perform(delete("/person/manager/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Manager not found'}"))));
    }

    @Test
    void getTeamMember() throws Exception {

        // Prepare
        var teamMember = TeamMember.builder().id("1").build();
        when(teamMemberRepository.findById("1")).thenReturn(Optional.of(teamMember));

        // Test & Verify
        mockMvc.perform(get("/person/team-member/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect((content().json(json("{'id':'1'}"))));
    }

    @Test
    void getTeamMember_HttpException() throws Exception {

        // Prepare
        when(teamMemberRepository.findById("1")).thenThrow(IngHttpException.notFound("Team member not found"));

        // Test & Verify
        mockMvc.perform(get("/person/team-member/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Team member not found'}"))));
    }

    @Test
    void createTeamMember() throws Exception {

        // Prepare
        TeamMember teamMember = teamMember("First Last", "1", "1");
        when(personService.createTeamMember(new ModelMapper().map(teamMember, TeamMemberRequest.class))).thenReturn(teamMember);

        // Test & Verify
        mockMvc.perform(post("/person/team-member")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(content().json(json("{" +
                        "'name':'First Last'," +
                        "'corporateId':'1'," +
                        "'roleId':'1'" +
                        "}")));
    }

    @Test
    void createTeamMember_InvalidRequest() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/person/team-member")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'corporateId':null," +
                                "'roleId':null" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'roleId':'must not be blank'," +
                        "       'name':'must not be blank'," +
                        "       'corporateId':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void createTeamMember_MalformedJson() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/person/team-member")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':10" +
                                "'corporateId':true" +
                                "'roleId':true" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json("{" +
                        "'message':'Malformed JSON request'" +
                        "}")));
    }

    @Test
    void deleteTeamMember() throws Exception {

        // Test & Verify
        mockMvc.perform(delete("/person/team-member/{id}", "1"))
                .andExpect(status().isOk());

        verify(personService).deleteTeamMember("1");
    }

    @Test
    void deleteTeamMember_HttpException() throws Exception {

        // Prepare
        doThrow(IngHttpException.notFound("Team member not found")).when(personService).deleteTeamMember("1");

        // Test & Verify
        mockMvc.perform(delete("/person/team-member/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Team member not found'}"))));
    }

    @Test
    void updateTeamMember() throws Exception {

        // Prepare
        TeamMember teamMember = teamMember("First Last", "1", "1");
        teamMember.setId("team-member-id");
        when(personService.updateTeamMember("team-member-id", new ModelMapper().map(teamMember, TeamMemberRequest.class))).thenReturn(teamMember);

        // Test & Verify
        mockMvc.perform(put("/person/team-member/{id}", "team-member-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("{" +
                        "'id':'team-member-id'," +
                        "'name':'First Last'," +
                        "'corporateId':'1'," +
                        "'roleId':'1'" +
                        "}")));
    }

    @Test
    void updateTeamMember_TeamMemberDoesNotExist() throws Exception {

        // Prepare
        TeamMember teamMember = teamMember("First Last", "1", "1");
        teamMember.setId("team-member-id");
        when(personService.updateTeamMember("team-member-id", new ModelMapper().map(teamMember, TeamMemberRequest.class))).thenThrow(IngHttpException.notFound("Team member does not exist"));

        // Test & Verify
        mockMvc.perform(put("/person/team-member/{id}", "team-member-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(json("{'message':'Team member does not exist'}")));
    }

    @Test
    void updateTeamMember_InvalidRequest() throws Exception {

        // Test & Verify
        mockMvc.perform(put("/person/team-member/{id}", "team-member-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'corporateId':null," +
                                "'roleId':null" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'roleId':'must not be blank'," +
                        "       'name':'must not be blank'," +
                        "       'corporateId':'must not be blank'" +
                        "   }" +
                        "}")));
    }
}