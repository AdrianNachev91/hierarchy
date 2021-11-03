package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.ManagementChain;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagementChainRepositoryCustom {

    List<ManagementChain> findAllByEmployeeId(String employeeId);
    void updateAttachedToTeam(String id, boolean attachedToTeam);
}
