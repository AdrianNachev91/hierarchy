package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.repositories.ManagerRepository;
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
}