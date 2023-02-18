package com.hierarchy.repositories;

import com.hierarchy.domain.TeamType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static com.hierarchy.TestUtils.teamType;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
class TeamTypeRepositoryTest {

    @Autowired
    private TeamTypeRepository teamTypeRepository;

    @BeforeEach
    void setUp() {
        teamTypeRepository.deleteAll();
    }

    @Test
    void findFirstByTitle() {

        // Prepare
        var teamType1 = teamType("title1").setId("1");
        var teamType2 = teamType("title2").setId("2");
        teamTypeRepository.saveAll(List.of(teamType1, teamType2));

        // Test
        Optional<TeamType> title1Result = teamTypeRepository.findFirstByTitle("title1");
        Optional<TeamType> title2Result = teamTypeRepository.findFirstByTitle("title2");
        Optional<TeamType> titleNoResult = teamTypeRepository.findFirstByTitle("no-results");

        // Verify
        assertThat(title1Result.isEmpty()).isFalse();
        assertThat(title1Result.get().getId()).isEqualTo("1");
        assertThat(title2Result.isEmpty()).isFalse();
        assertThat(title2Result.get().getId()).isEqualTo("2");
        assertThat(titleNoResult.isEmpty()).isTrue();
    }
}