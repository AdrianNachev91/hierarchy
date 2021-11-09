package com.ing.inghierarchy.web;

import static com.ing.inghierarchy.TestUtils.*;
import static com.ing.inghierarchy.TestUtils.json;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Role;
import com.ing.inghierarchy.repositories.RoleRepository;
import com.ing.inghierarchy.service.RoleService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class RoleControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void getRole_Found() throws Exception {
        Role role = Role.builder().id("1").title("role").build();
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));

        mockMvc.perform(get("/role/{id}", role.getId()))
                .andExpect(status().isOk())
                .andExpect((content().json(json("{" +
                        "   'id':'1'," +
                        "   'title':'role'" +
                        "}"))));
    }

    @Test
    void getRole_NotFound() throws Exception {
        Role role = Role.builder().id("1").title("role").build();
        when(roleRepository.findById("1")).thenThrow(IngHttpException.notFound("Role not found"));

        String jsonResponse =
                "{" +
                        String.format("'message':'%s'", "Role not found") +
                        "}";

        mockMvc.perform(get("/role/{id}", role.getId()))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json(jsonResponse))));
    }

    @Test
    void createRole() throws Exception {

        Role role = role("title").setId("1");
        when(roleService.createRole(roleRequest("title"))).thenReturn(role);

        mockMvc.perform(post("/role/")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':'title'}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "   'id':'1'," +
                        "   'title':title" +
                        "}")));
    }

    @Test
    void createRole_Invalid() throws Exception {

        mockMvc.perform(post("/role")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':''}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "   'errors':{" +
                        "       'title':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void updateRole() throws Exception {

        var role = role("updated-role").setId("id");
        when(roleService.updateRole("id", roleRequest("updated-role"))).thenReturn(role);

        mockMvc.perform(put("/role/{id}", "id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':'updated-role'}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "   'id':'id'," +
                        "   'title':'updated-role'" +
                        "}")));
    }

    @Test
    void updateRole_NotFound() throws Exception {

        when(roleService.updateRole("id", roleRequest("updated-role"))).thenThrow(IngHttpException.notFound("Role not found"));

        mockMvc.perform(put("/role/{id}", "id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':'updated-role'}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{'message':'Role not found'}")));
    }

    @Test
    void updateRole_Invalid() throws Exception {

        mockMvc.perform(put("/role/{id}", "id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{'title':''}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(json("{" +
                        "   'errors':{" +
                        "       'title':'must not be blank'" +
                        "   }" +
                        "}")));
    }

    @Test
    void deleteRole() throws Exception {

        mockMvc.perform(delete("/role/{id}", "id"))
                .andExpect(status().isOk());

        verify(roleService).deleteRole("id");
    }

    @Test
    void deleteRole_NotFound() throws Exception {

        doThrow(IngHttpException.notFound("No Role found to delete")).when(roleService).deleteRole("id");

        mockMvc.perform(delete("/role/{id}", "id"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(json("{'message':'No Role found to delete'}")));
    }
}