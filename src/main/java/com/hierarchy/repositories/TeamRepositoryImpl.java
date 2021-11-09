package com.hierarchy.repositories;

import com.hierarchy.domain.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class TeamRepositoryImpl implements TeamRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Team> fetchTeamsWithPaginationByTeamType(String teamTypeId, int limit, long offset) {

        Query query = Query.query(Criteria.where(Team.FIELD_TEAM_TYPE).is(teamTypeId))
                .skip(offset)
                .limit(limit)
                .with(Sort.by(Sort.DEFAULT_DIRECTION, Team.FIELD_MANAGED_BY));

        return mongoTemplate.find(query, Team.class, Team.COLLECTION_NAME);
    }
}
