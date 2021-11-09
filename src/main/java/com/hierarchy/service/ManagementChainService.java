package com.hierarchy.service;

import com.hierarchy.Exceptions.HierarchyHttpException;
import com.hierarchy.domain.ManagementChain;
import com.hierarchy.repositories.EmployeeRepository;
import com.hierarchy.repositories.ManagementChainRepository;
import com.hierarchy.web.request.ManagementChainRequest;
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
            throw HierarchyHttpException.notFound(String.format("Employees with ids: %s were not found", String.join(", ", nonExistingManagers)));
        }

        return managementChainRepository.save(managementChainRequest.toManagementChain());
    }
}
