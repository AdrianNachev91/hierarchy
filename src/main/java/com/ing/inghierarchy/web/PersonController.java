package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.service.PersonService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/person")
public class PersonController {

    private ManagerRepository managerRepository;
    private PersonService personService;

    @Autowired
    public PersonController(ManagerRepository managerRepository, PersonService personService) {
        this.managerRepository = managerRepository;
        this.personService = personService;
    }

    @GetMapping("/manager/{id}")
    public Manager getManager(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        return managerRepository.findById(id).orElseThrow(() -> IngHttpException.notFound("Manager not found"));
    }

    @PostMapping("/manager")
    @ResponseStatus(CREATED)
    public Manager createManager(@RequestBody @Valid ManagerRequest managerRequest) {
        return personService.createManager(managerRequest);
    }

    @PutMapping("/manager/{id}")
    public Manager updateManager(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                                 @RequestBody @Valid ManagerRequest managerRequest) {
        return personService.updateManager(id, managerRequest);
    }

    @DeleteMapping("/manager/{id}")
    public void deleteManager(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        personService.deleteManager(id);
    }
}
