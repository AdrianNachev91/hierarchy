package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.ManagementChain;
import com.ing.inghierarchy.repositories.EmployeeRepository;
import com.ing.inghierarchy.repositories.ManagementChainRepository;
import com.ing.inghierarchy.web.request.ManagementChainRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class ManagementChainService {

    private final EmployeeRepository employeeRepository;
    private final ManagementChainRepository managementChainRepository;

    public ManagementChain createManagementChain(ManagementChainRequest managementChainRequest) {

        Set<String> managerIds = managementChainRequest.getManagersChain().stream().map(ManagementChain.ManagerInChain::getManagerId).collect(toSet());

        List<String> nonExistingManagers = new ArrayList<>();
        for (String managerId : managerIds) {
            if (!employeeRepository.existsById(managerId)) {
                nonExistingManagers.add(managerId);
            }
        }
        if (!nonExistingManagers.isEmpty()) {
            throw IngHttpException.notFound(String.format("Employees with ids: %s were not found", String.join(", ", nonExistingManagers)));
        }

        return managementChainRepository.save(managementChainRequest.toManagementChain());
    }
}
