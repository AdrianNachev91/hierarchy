package com.ing.inghierarchy.repositories;

import com.ing.inghierarchy.domain.Team;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepositoryCustom {
    List<Team> fetchTeamsWithPaginationByTeamType(String teamTypeId, int limit, long offset);
}
