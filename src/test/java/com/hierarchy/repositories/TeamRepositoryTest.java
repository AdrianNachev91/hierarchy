package com.hierarchy.repositories;

import com.hierarchy.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.hierarchy.TestUtils.team;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();
    }

    @Test
    void findAllByManagedBy() {

        // Prepare
        var team1 = team("team 1", "managedBy-id1", null, null).setId("team1-id");
        var team2 = team("team 2", "managedBy-id2", null, null).setId("team2-id");
        var team3 = team("team 3", "managedBy-id1", null, null).setId("team3-id");
        teamRepository.saveAll(List.of(team1, team2, team3));

        // Test
        List<Team> managedBy1Results = teamRepository.findAllByManagedBy("managedBy-id1");
        List<Team> managedBy2Results = teamRepository.findAllByManagedBy("managedBy-id2");
        List<Team> managedByNoResults = teamRepository.findAllByManagedBy("no-results");

        // Verify
        assertThat(managedBy1Results).hasSize(2);
        assertThat(managedBy1Results.stream().map(Team::getId).collect(toList()))
                .containsExactlyInAnyOrder("team1-id", "team3-id");
        assertThat(managedBy2Results).hasSize(1);
        assertThat(managedBy2Results.stream().map(Team::getId).collect(toList()))
                .containsExactlyInAnyOrder("team2-id");
        assertThat(managedByNoResults).isEmpty();
    }

    @Test
    void findAllByCrewContaining() {

        // Prepare
        var team1 = team("team 1", null, null, null).setId("team1-id").setCrew(List.of("member1", "member2"));
        var team2 = team("team 2", null, null, null).setId("team2-id").setCrew(List.of("member2"));
        var team3 = team("team 3", null, null, null).setId("team3-id").setCrew(List.of("member3", "member4"));
        teamRepository.saveAll(List.of(team1, team2, team3));

        // Test
        List<Team> member1Results = teamRepository.findAllByCrewContaining("member1");
        List<Team> member2Results = teamRepository.findAllByCrewContaining("member2");
        List<Team> memberNoResults = teamRepository.findAllByCrewContaining("no-results");

        // Verify
        assertThat(member1Results).hasSize(1);
        assertThat(member1Results.stream().map(Team::getId).collect(toList()))
                .containsExactlyInAnyOrder("team1-id");
        assertThat(member2Results).hasSize(2);
        assertThat(member2Results.stream().map(Team::getId).collect(toList()))
                .containsExactlyInAnyOrder("team1-id", "team2-id");
        assertThat(memberNoResults).isEmpty();
    }

    @Test
    void findAllByTeamType() {

        // Prepare
        var team1 = team("team 1", null, null, "teamType1").setId("team1-id");
        var team2 = team("team 2", null, null, "teamType1").setId("team2-id");
        var team3 = team("team 3", null, null, "teamType2").setId("team3-id");
        teamRepository.saveAll(List.of(team1, team2, team3));

        // Test
        List<Team> teamType1Results = teamRepository.findAllByTeamType("teamType1");
        List<Team> teamType2Results = teamRepository.findAllByTeamType("teamType2");
        List<Team> teamTypeNoResults = teamRepository.findAllByTeamType("no-results");

        // Verify
        assertThat(teamType1Results).hasSize(2);
        assertThat(teamType1Results.stream().map(Team::getId).collect(toList()))
                .containsExactlyInAnyOrder("team1-id", "team2-id");
        assertThat(teamType2Results).hasSize(1);
        assertThat(teamType2Results.stream().map(Team::getId).collect(toList()))
                .containsExactlyInAnyOrder("team3-id");
        assertThat(teamTypeNoResults).isEmpty();
    }
}