package com.hierarchy.repositories;

import com.hierarchy.domain.ManagementChain;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagementChainRepositoryCustom {

    List<ManagementChain> findAllByEmployeeId(String employeeId);
    void updateAttachedToTeam(String id, boolean attachedToTeam);
}
