package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Employee;
import com.ing.inghierarchy.repositories.EmployeeRepository;
import com.ing.inghierarchy.service.EmployeeService;
import com.ing.inghierarchy.web.request.EmployeeRequest;
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

import java.util.Optional;

import static com.ing.inghierarchy.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
class EmployeeControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void getEmployee() throws Exception {

        // Prepare
        var employee = Employee.builder().id("1").build();
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));

        // Test & Verify
        mockMvc.perform(get("/employee/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect((content().json(json("{'id':'1'}"))));
    }

    @Test
    void getEmployee_HttpException() throws Exception {

        // Prepare
        when(employeeRepository.findById("1")).thenThrow(IngHttpException.notFound("Employee not found"));

        // Test & Verify
        mockMvc.perform(get("/employee/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Employee not found'}"))));
    }

    @Test
    void createEmployee() throws Exception {

        // Prepare
        Employee employee = employee("First Last", "1", "1");
        when(employeeService.createEmployee(new ModelMapper().map(employee, EmployeeRequest.class))).thenReturn(employee);

        // Test & Verify
        mockMvc.perform(post("/employee")
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
    void createEmployee_InvalidRequest() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/employee")
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
    void updateEmployee() throws Exception {

        // Prepare
        Employee employee = employee("First Last", "1", "1");
        employee.setId("employee-id");
        when(employeeService.updateEmployee("employee-id", new ModelMapper().map(employee, EmployeeRequest.class))).thenReturn(employee);

        // Test & Verify
        mockMvc.perform(put("/employee/{id}", "employee-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(content().json(json("{" +
                        "'id':'employee-id'," +
                        "'name':'First Last'," +
                        "'corporateId':'1'," +
                        "'roleId':'1'" +
                        "}")));
    }

    @Test
    void updateEmployee_ManagerDoesNotExist() throws Exception {

        // Prepare
        Employee employee = employee("First Last", "1", "1");
        employee.setId("employee-id");
        when(employeeService.updateEmployee("employee-id", new ModelMapper().map(employee, EmployeeRequest.class))).thenThrow(IngHttpException.notFound("Employee does not exist"));

        // Test & Verify
        mockMvc.perform(put("/employee/{id}", "employee-id")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':'First Last'," +
                                "'corporateId':'1'," +
                                "'roleId':'1'" +
                                "}"))
                        .characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(json("{'message':'Employee does not exist'}")));
    }

    @Test
    void updateEmployee_InvalidRequest() throws Exception {

        // Test & Verify
        mockMvc.perform(put("/employee/{id}", "employee-id")
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
    void createEmployee_MalformedJson() throws Exception {

        // Test & Verify
        mockMvc.perform(post("/employee")
                        .contentType(APPLICATION_JSON)
                        .content(json("{" +
                                "'name':true" +
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
    void deleteEmployee() throws Exception {

        // Test & Verify
        mockMvc.perform(delete("/employee/{id}", "1"))
                .andExpect(status().isOk());

        verify(employeeService).deleteEmployee("1");
    }

    @Test
    void deleteEmployee_HttpException() throws Exception {

        // Prepare
        doThrow(IngHttpException.notFound("Employee not found")).when(employeeService).deleteEmployee("1");

        // Test & Verify
        mockMvc.perform(delete("/employee/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect((content().json(json("{'message':'Employee not found'}"))));
    }
}