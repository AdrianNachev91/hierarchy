package com.ing.inghierarchy.web;

import com.ing.inghierarchy.domain.ManagementChain;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.service.ManagementChainService;
import com.ing.inghierarchy.web.request.ManagementChainRequest;
import com.ing.inghierarchy.web.request.TeamRequest;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/management-chain")
@RequiredArgsConstructor
public class ManagementChainController {

    private final ManagementChainService managementChainService;

    @PostMapping
    @ResponseStatus(CREATED)
    public ManagementChain createManagementChain(@RequestBody @Valid ManagementChainRequest managementChainRequest) {
        return managementChainService.createManagementChain(managementChainRequest);
    }
}
