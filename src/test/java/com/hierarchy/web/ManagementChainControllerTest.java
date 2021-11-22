package com.hierarchy.web;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.service.ManagementChainService;
import com.hierarchy.web.request.ManagementChainRequest;
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

import java.util.List;

import static com.hierarchy.TestUtils.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ManagementChainControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private ManagementChainService managementChainService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void createManagementChain() throws Exception {

        // Prepare
        var managementChain = managementChain(false, List.of(
                managerInChain("1", "2"),
                managerInChain("2", null)));
        when(managementChainService.createManagementChain(new ModelMapper().map(managementChain, ManagementChainRequest.class))).thenReturn(managementChain.setId("managementChain-id"));

        // Test & Verify
        mockMvc.perform(post("/management-chain")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "   'managersChain':[" +
                                "       {'managerId':'1','manages':'2'}," +
                                "       {'managerId':'2'}" +
                                "   ]" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "   'id':'managementChain-id'," +
                        "   'managersChain':[" +
                        "       {'managerId':'1','manages':'2'}," +
                        "       {'managerId':'2'}" +
                        "   ]" +
                        "}")));
    }

    @Test
    void createManagementChain_Exception() throws Exception {

        // Prepare
        var managementChain = managementChain(false, List.of(
                managerInChain("1", "2"),
                managerInChain("2", null)));
        when(managementChainService.createManagementChain(new ModelMapper().map(managementChain, ManagementChainRequest.class))).thenThrow(HierarchyHttpException.badRequest("Something went wrong"));

        // Test & Verify
        mockMvc.perform(post("/management-chain")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "   'managersChain':[" +
                                "       {'managerId':'1','manages':'2'}," +
                                "       {'managerId':'2'}" +
                                "   ]" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{'message':'Something went wrong'}")));
    }

    @Test
    void createManagementChain_INVALID() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/management-chain")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "   'managersChain':[]" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "'errors':" +
                        "   {" +
                        "       'managersChain':'must not be empty'" +
                        "   }" +
                        "}")));
    }
}