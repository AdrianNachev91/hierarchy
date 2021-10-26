package com.ing.inghierarchy.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.ing.inghierarchy.TestUtils.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
class ManagerRepositoryTest {

    @Autowired
    private ManagerRepository managerRepository;

    @BeforeEach
    void setUp() {
        managerRepository.deleteAll();
    }

    @Test
    void existsByIdAndLead() {

        // Prepare
        var manager1 = manager(null, true, null, null, null);
        manager1.setId("id-1");
        var manager2 = manager(null, true, null, null, null);
        manager2.setId("id-2");
        var manager3 = manager(null, false, null, null, null);
        manager3.setId("id-3");
        managerRepository.saveAll(List.of(manager1, manager2, manager3));

        // Test & Verify
        assertThat(managerRepository.existsByIdAndLead("id-1", false)).isFalse();
        assertThat(managerRepository.existsByIdAndLead("id-2", true)).isTrue();
        assertThat(managerRepository.existsByIdAndLead("id-3", true)).isFalse();
    }
}