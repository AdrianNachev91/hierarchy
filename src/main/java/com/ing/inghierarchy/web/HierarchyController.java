package com.ing.inghierarchy.web;

import com.ing.inghierarchy.service.HierarchyService;
import com.ing.inghierarchy.web.response.HierarchyStructureResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hierarchy")
@RequiredArgsConstructor
public class HierarchyController {

    private final HierarchyService hierarchyService;

    @GetMapping("/{teamTypeTitle}/{limit}/{offset}")
    private HierarchyStructureResponse getHierarchy(@Parameter(name = "teamTypeTitle", required = true) @PathVariable("teamTypeTitle") String teamTypeTitle,
                                                    @Parameter(name = "limit", required = true) @PathVariable("limit") int limit,
                                                    @Parameter(name = "offset", required = true) @PathVariable("offset") long offset) {
        return hierarchyService.getHierarchy(teamTypeTitle, limit, offset);
    }
}
