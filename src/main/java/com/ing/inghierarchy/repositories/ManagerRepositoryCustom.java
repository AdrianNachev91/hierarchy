package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Manager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerRepositoryCustom {

    List<Manager> findAllByIds(List<String> ids);
}
