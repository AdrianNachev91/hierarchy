package com.hierarchy.web;

import com.hierarchy.domain.TeamType;
import com.hierarchy.service.TeamTypeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/team-type")
@RequiredArgsConstructor
public class TeamTypeController {

    private final TeamTypeService teamTypeService;

    @PostMapping
    @ResponseStatus(CREATED)
    public TeamType createTeam(@RequestBody @Valid TeamTypeRequest teamTypeRequest) {
        return teamTypeService.createTeamType(teamTypeRequest);
    }

    @PutMapping("/{id}")
    public TeamType updateTeam(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                           @RequestBody @Valid TeamTypeRequest teamRequest) {
        return teamTypeService.updateTeamType(id, teamRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        teamTypeService.deleteTeamType(id);
    }
}
