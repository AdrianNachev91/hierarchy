package com.ing.inghierarchy.web;

import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.service.TeamService;
import com.ing.inghierarchy.web.request.IdsRequest;
import com.ing.inghierarchy.web.request.TeamRequest;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Team createTeam(@RequestBody @Valid TeamRequest teamRequest) {
        return teamService.createTeam(teamRequest);
    }

    @PutMapping("/{id}")
    public Team updateTeam(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                           @RequestBody @Valid TeamRequest teamRequest) {
        return teamService.updateTeam(id, teamRequest);
    }

    @PutMapping("/add-members/{id}")
    public Team addMembers(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                           @RequestBody @Valid IdsRequest idsRequest) {
        return teamService.addTeamMembers(id, idsRequest);
    }

    @PutMapping("/remove-members/{id}")
    public Team removeMembers(@Parameter(name = "id", required = true) @PathVariable("id") String id,
                           @RequestBody @Valid IdsRequest idsRequest) {
        return teamService.removeTeamMembers(id, idsRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@Parameter(name = "id", required = true) @PathVariable("id") String id) {
        teamService.deleteTeam(id);
    }
}
