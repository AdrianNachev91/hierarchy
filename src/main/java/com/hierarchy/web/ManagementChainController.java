package com.hierarchy.web;

import com.hierarchy.domain.ManagementChain;
import com.hierarchy.service.ManagementChainService;
import com.hierarchy.web.request.ManagementChainRequest;
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
