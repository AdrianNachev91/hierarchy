package com.ing.inghierarchy.web;

import static com.ing.inghierarchy.TestUtils.json;
import static org.mockito.Mockito.when;
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

        Role role = Role.builder().id("1").title("role").build();
        when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(null);
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());
        when(roleService.roleExists(role)).thenReturn(false);
        when(roleService.createRole(role)).thenReturn(role);

        String jsonRequest =
                "{" +
                        String.format("'id':'%s'", role.getId()) + "," +
                        String.format("'title':'%s'", role.getTitle()) +
                        "}";

        String jsonResponse =
                "{" +
                        String.format("'id':'%s'", role.getId()) + "," +
                        String.format("'title':'%s'", role.getTitle()) +
                        "}";

        mockMvc.perform(post("/role/")
                        .contentType(APPLICATION_JSON)
                        .content(json(jsonRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(content().json(json(jsonResponse)));
    }

    @Test
    void createRole_Exists() throws Exception {

        Role role = Role.builder().id("1").title("role").build();
        when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(role);
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(roleService.roleExists(role)).thenReturn(true);
        when(roleService.createRole(role)).thenThrow(IngHttpException.badRequest("Role already exists"));

        String jsonRequest =
                "{" +
                        String.format("'id':'%s'", role.getId()) + "," +
                        String.format("'title':'%s'", role.getTitle()) +
                        "}";

        String jsonResponse =
                "{" +
                        String.format("'message':'%s'", "Role already exists") +
                        "}";

        mockMvc.perform(post("/role/")
                        .contentType(APPLICATION_JSON)
                        .content(json(jsonRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(jsonResponse)));
    }

    @Test
    void updateRole() throws Exception {

        Role role = Role.builder().id("1").title("role").build();
        role.setTitle("updated-role");
        when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(null);
        when(roleRepository.findById(role.getId())).thenReturn(Optional.empty());
        when(roleService.roleExists(role)).thenReturn(false);
        when(roleService.updateRole(role.getId(), role)).thenReturn(role);

        String jsonRequest =
                "{" +
                        String.format("'id':'%s'", role.getId()) + "," +
                        String.format("'title':'%s'", role.getTitle()) +
                        "}";

        String jsonResponse =
                "{" +
                        String.format("'id':'%s'", role.getId()) + "," +
                        String.format("'title':'%s'", role.getTitle()) +
                        "}";

        mockMvc.perform(put("/role/{id}", role.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json(jsonRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(json(jsonResponse)));
    }

    @Test
    void updateRole_Exists() throws Exception {

        Role role = Role.builder().id("1").title("role").build();
        role.setTitle("updated-role");
        when(roleRepository.findRoleByTitle(role.getTitle())).thenReturn(role);
        when(roleRepository.findById(role.getId())).thenReturn(Optional.of(role));
        when(roleService.roleExists(role)).thenReturn(true);
        when(roleService.updateRole(role.getId(), role)).thenThrow(IngHttpException.badRequest("Role already exists"));

        String jsonRequest =
                "{" +
                        String.format("'id':'%s'", role.getId()) + "," +
                        String.format("'title':'%s'", role.getTitle()) +
                        "}";

        String jsonResponse =
                "{" +
                        String.format("'message':'%s'", "Role already exists") +
                        "}";

        mockMvc.perform(put("/role/{id}", role.getId())
                        .contentType(APPLICATION_JSON)
                        .content(json(jsonRequest))
                        .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(jsonResponse)));
    }

    @Test
    void deleteRole() throws Exception {

        Role role = Role.builder().id("1").title("role").build();
        boolean isDeleted = true;
        when(roleService.deleteRole(role.getId())).thenReturn(isDeleted);


        mockMvc.perform(delete("/role/{id}", role.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("true")));
    }

    @Test
    void deleteRole_NotFound() throws Exception {

        Role role = Role.builder().id("1").title("role").build();
        when(roleService.deleteRole(role.getId())).thenThrow(IngHttpException.notFound("No Role found to delete"));

        String jsonResponse =
                "{" +
                        String.format("'message':'%s'", "No Role found to delete") +
                        "}";

        mockMvc.perform(delete("/role/{id}", role.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().json(json(jsonResponse)));
    }

    @Test
    void deleteRole_InUse() throws Exception {

        Role role = Role.builder().id("1").title("role").build();
        when(roleService.deleteRole(role.getId())).thenThrow(IngHttpException.badRequest("Role is in use"));

        String jsonResponse =
                "{" +
                        String.format("'message':'%s'", "Role is in use") +
                        "}";

        mockMvc.perform(delete("/role/{id}", role.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json(jsonResponse)));

    }
}