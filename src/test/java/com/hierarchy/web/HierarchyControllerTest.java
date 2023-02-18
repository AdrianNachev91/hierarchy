package com.hierarchy.web;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.service.HierarchyService;
import com.hierarchy.web.response.HierarchyStructureResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.hierarchy.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class HierarchyControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private HierarchyService hierarchyService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void getHierarchy() throws Exception {

        var hierarchyStructureResponse = HierarchyStructureResponse.builder().total(10).build();
        when(hierarchyService.getHierarchy("chapter", 10, 10)).thenReturn(hierarchyStructureResponse);

        mockMvc.perform(get("/hierarchy/{teamTypeTitle}/{limit}/{offset}", "chapter", 10, 10))
                .andExpect(status().isOk())
                .andExpect((content().json(json("{'total':10}"))));
    }

    @Test
    void getHierarchy_Exception() throws Exception {

        when(hierarchyService.getHierarchy("chapter", 10, 10)).thenThrow(HierarchyHttpException.badRequest("Something went wrong"));

        mockMvc.perform(get("/hierarchy/{teamTypeTitle}/{limit}/{offset}", "chapter", 10, 10))
                .andExpect(status().isBadRequest())
                .andExpect((content().json(json("{'message':'Something went wrong'}"))));
    }
}