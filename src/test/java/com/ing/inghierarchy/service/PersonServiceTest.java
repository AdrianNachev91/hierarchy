package com.ing.inghierarchy.service;

import com.ing.inghierarchy.Exceptions.IngHttpException;
import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.repositories.ManagerRepository;
import com.ing.inghierarchy.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ing.inghierarchy.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    private PersonService personService;

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ManagerRepository managerRepository;

    @BeforeEach
    void setUp() {
        personService = new PersonService(roleRepository, managerRepository);
    }

    @Test
    void createManager() {

        // Prepare
        var managerRequest = managerRequest("name", true, "manages", "corporate-id", "role-id");
        when(roleRepository.existsById("role-id")).thenReturn(true);
        var manager = manager("name", true, "manages", "corporate-id", "role-id");
        when(managerRepository.save(manager)).thenReturn(manager);

        // Test
        Manager result = personService.createManager(managerRequest);

        // Verify
        assertThat(result).isEqualTo(manager);
    }

    @Test
    void createManager_RoleDoesNotExist() {

        // Prepare
        var managerRequest = managerRequest("name", true, "manages", "corporate-id", "role-id");
        when(roleRepository.existsById("role-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> personService.createManager(managerRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Cannot create a manager with non-existing role");
    }

    @Test
    void updateManager() {

        // Prepare
        var managerRequest = managerRequest("name", true, "manages", "corporate-id", "role-id");
        when(managerRepository.existsById("manager-id")).thenReturn(true);
        when(roleRepository.existsById("role-id")).thenReturn(true);
        var manager = manager("name", true, "manages", "corporate-id", "role-id");
        manager.setId("manager-id");
        when(managerRepository.save(manager)).thenReturn(manager);

        // Test
        Manager result = personService.updateManager("manager-id", managerRequest);

        // Verify
        assertThat(result).isEqualTo(manager);
    }

    @Test
    void updateManager_ManagerDoesNotExist() {

        // Prepare
        var managerRequest = managerRequest("name", true, "manages", "corporate-id", "role-id");
        when(managerRepository.existsById("manager-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> personService.updateManager("manager-id", managerRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Manager does not exist");
    }

    @Test
    void updateManager_RoleDoesNotExist() {

        // Prepare
        var managerRequest = managerRequest("name", true, "manages", "corporate-id", "role-id");
        when(managerRepository.existsById("manager-id")).thenReturn(true);
        when(roleRepository.existsById("role-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> personService.updateManager("manager-id", managerRequest));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Cannot create a manager with non-existing role");
    }

    @Test
    void deleteManager() {

        // Prepare
        when(managerRepository.existsById("manager-id")).thenReturn(true);

        // Test
        personService.deleteManager("manager-id");

        // Verify
        verify(managerRepository).deleteById("manager-id");
    }

    @Test
    void deleteManager_ManagerDoesNotExist() {

        // Prepare
        when(managerRepository.existsById("manager-id")).thenReturn(false);

        // Test
        Throwable e = catchThrowable(() -> personService.deleteManager("manager-id"));

        // Verify
        assertThat(e).isInstanceOf(IngHttpException.class);
        assertThat(e.getMessage()).isEqualTo("Manager does not exist");
    }
}