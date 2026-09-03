package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UpdateRoleInput;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRoleUserUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String USER_EMAIL = "john@example.com";
    private static final String PASSWORD = "Password123";

    @Mock
    private UserGateway userGateway;

    private UpdateRoleUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateRoleUserUseCase(userGateway);
    }

    @Test
    @DisplayName("Should update user role successfully")
    void shouldUpdateUserRoleSuccessfully() {
        User user = createUser(Role.MEDICO);

        UpdateRoleInput input =  UpdateRoleInput.builder()
                .role(Role.ADMIN)
                .build();
        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.save(user))
                .thenReturn(user);

        var output = useCase.execute(USER_ID, input);

        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(USER_ID, output.getId()),
                () -> assertEquals(Role.ADMIN, output.getRole())
        );

        assertEquals(Role.ADMIN, user.getRole());

        verify(userGateway).findById(USER_ID);
        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        UpdateRoleInput input =  UpdateRoleInput.builder()
                .role(Role.ADMIN)
                .build();
        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(USER_ID, input)
        );

        verify(userGateway).findById(USER_ID);
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should save user with updated role")
    void shouldSaveUserWithUpdatedRole() {
        User user = createUser(Role.MEDICO);

        UpdateRoleInput input =  UpdateRoleInput.builder()
                .role(Role.ADMIN)
                .build();


        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.save(user))
                .thenReturn(user);

        useCase.execute(USER_ID, input);

        assertEquals(Role.ADMIN, user.getRole());

        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should find user before saving updated role")
    void shouldFindUserBeforeSaving() {
        User user = createUser(Role.MEDICO);

        UpdateRoleInput input =  UpdateRoleInput.builder()
                .role(Role.ADMIN)
                .build();
        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.save(user))
                .thenReturn(user);

        useCase.execute(USER_ID, input);

        InOrder inOrder = inOrder(userGateway);

        inOrder.verify(userGateway).findById(USER_ID);
        inOrder.verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should propagate exception when finding user fails")
    void shouldPropagateExceptionWhenFindingUserFails() {
        UpdateRoleInput input =  UpdateRoleInput.builder()
                .role(Role.ADMIN)
                .build();
        RuntimeException exception =
                new RuntimeException("Database error");

        when(userGateway.findById(USER_ID))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(USER_ID, input)
        );

        assertSame(exception, thrown);

        verify(userGateway).findById(USER_ID);
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception when saving user fails")
    void shouldPropagateExceptionWhenSavingUserFails() {
        User user = createUser(Role.MEDICO);

        UpdateRoleInput input =  UpdateRoleInput.builder()
                .role(Role.ADMIN)
                .build();

        RuntimeException exception =
                new RuntimeException("Database error");

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.save(user))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(USER_ID, input)
        );

        assertSame(exception, thrown);

        assertEquals(Role.ADMIN, user.getRole());

        verify(userGateway).findById(USER_ID);
        verify(userGateway).save(user);
    }

    private User createUser(Role role) {
        return User.builder()
                .id(USER_ID)
                .userName(USER_NAME)
                .email(USER_EMAIL)
                .password(PASSWORD)
                .role(role)
                .isActive(true)
                .build();
    }
}