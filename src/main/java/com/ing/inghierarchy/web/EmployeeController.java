package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Employee;
import com.ing.inghierarchy.repositories.EmployeeRepository;
import com.ing.inghierarchy.service.EmployeeService;
import com.ing.inghierarchy.web.request.EmployeeRequest;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public Employee getEmployee(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        return employeeRepository.findById(id).orElseThrow(() -> IngHttpException.notFound("Employee not found"));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Employee createEmployee(@RequestBody @Valid EmployeeRequest employeeRequest) {
        return employeeService.createEmployee(employeeRequest);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                                 @RequestBody @Valid EmployeeRequest employeeRequest) {
        return employeeService.updateEmployee(id, employeeRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteManager(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        employeeService.deleteEmployee(id);
    }
}
