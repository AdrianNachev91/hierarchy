package com.hierarchy.repositories;

import com.hierarchy.domain.ManagementChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static com.hierarchy.TestUtils.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
class ManagementChainRepositoryTest {

    @Autowired
    private ManagementChainRepository managementChainRepository;

    @BeforeEach
    void setUp() {
        managementChainRepository.deleteAll();
    }

    @Test
    void findAllByIdIn() {

        // Prepare
        var managementChain1 = managementChain(true, null).setId("1");
        var managementChain2 = managementChain(true, null).setId("2");
        var managementChain3 = managementChain(true, null).setId("3");
        managementChainRepository.saveAll(List.of(managementChain1, managementChain2, managementChain3));

        // Test
        List<ManagementChain> id1And2Results = managementChainRepository.findAllByIdIn(Set.of("1", "2"));
        List<ManagementChain> id3Results = managementChainRepository.findAllByIdIn(Set.of("3"));
        List<ManagementChain> idNoResults = managementChainRepository.findAllByIdIn(emptySet());

        // Verify
        assertThat(id1And2Results).hasSize(2);
        assertThat(id1And2Results.stream().map(ManagementChain::getId).collect(toList()))
                .containsExactlyInAnyOrder("1", "2");
        assertThat(id3Results).hasSize(1);
        assertThat(id3Results.stream().map(ManagementChain::getId).collect(toList()))
                .containsExactlyInAnyOrder("3");
        assertThat(idNoResults).isEmpty();
    }

    @Test
    void findAllByAttachedToTeam() {

        // Prepare
        var managementChain1 = managementChain(true, null).setId("1");
        var managementChain2 = managementChain(true, null).setId("2");
        var managementChain3 = managementChain(false, null).setId("3");
        managementChainRepository.saveAll(List.of(managementChain1, managementChain2, managementChain3));

        // Test
        List<ManagementChain> id1And2Results = managementChainRepository.findAllByAttachedToTeam(true);
        List<ManagementChain> id3Results = managementChainRepository.findAllByAttachedToTeam(false);

        // Verify
        assertThat(id1And2Results).hasSize(2);
        assertThat(id1And2Results.stream().map(ManagementChain::getId).collect(toList()))
                .containsExactlyInAnyOrder("1", "2");
        assertThat(id3Results).hasSize(1);
        assertThat(id3Results.stream().map(ManagementChain::getId).collect(toList()))
                .containsExactlyInAnyOrder("3");
    }

    @Test
    void findAllByEmployeeId() {

        // Prepare
        var managementChain1 = managementChain(true, List.of(managerInChain("1", "2"), managerInChain("2", null))).setId("1");
        var managementChain2 = managementChain(true, List.of(managerInChain("2", "3"), managerInChain("3", null))).setId("2");
        var managementChain3 = managementChain(false, List.of(managerInChain("4", "5"), managerInChain("5", null))).setId("3");
        managementChainRepository.saveAll(List.of(managementChain1, managementChain2, managementChain3));

        // Test
        List<ManagementChain> id2Results = managementChainRepository.findAllByEmployeeId("2");
        List<ManagementChain> id4Results = managementChainRepository.findAllByEmployeeId("4");
        List<ManagementChain> idNoResults = managementChainRepository.findAllByEmployeeId("non-existent");

        // Verify
        assertThat(id2Results).hasSize(2);
        assertThat(id2Results.stream().map(ManagementChain::getId).collect(toList()))
                .containsExactlyInAnyOrder("1", "2");
        assertThat(id4Results).hasSize(1);
        assertThat(id4Results.stream().map(ManagementChain::getId).collect(toList()))
                .containsExactlyInAnyOrder("3");
        assertThat(idNoResults).isEmpty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void updateAttachedToTeam() {

        // Prepare
        var managementChain = managementChain(true, null).setId("1");
        managementChainRepository.save(managementChain);

        // Test
        managementChainRepository.updateAttachedToTeam("1", false);
        ManagementChain result = managementChainRepository.findById("1").get();

        // Verify
        assertThat(result.isAttachedToTeam()).isFalse();
    }
}