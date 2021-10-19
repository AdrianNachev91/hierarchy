package com.ing.inghierarchy.web;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.TeamMemberRepository;
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
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    public PersonController(ManagerRepository managerRepository, PersonService personService, TeamMemberRepository teamMemberRepository) {
        this.managerRepository = managerRepository;
        this.personService = personService;
        this.teamMemberRepository = teamMemberRepository;
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

    @GetMapping("/team-member/{id}")
    public TeamMember getTeamMember(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        return teamMemberRepository.findById(id).orElseThrow(() -> IngHttpException.notFound("Team member not found"));
    }

    @PostMapping("/team-member")
    @ResponseStatus(CREATED)
    public TeamMember createTeamMember(@RequestBody @Valid TeamMemberRequest teamMemberRequest) {
        return personService.createTeamMember(teamMemberRequest);
    }

    @PutMapping("/team-member/{id}")
    public TeamMember updateTeamMember(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                                 @RequestBody @Valid TeamMemberRequest teamMemberRequest) {
        return personService.updateTeamMember(id, teamMemberRequest);
    }

    @DeleteMapping("/team-member/{id}")
    public void deleteTeamMember(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        personService.deleteTeamMember(id);
    }
}
