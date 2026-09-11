package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UpdateUserInput;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.EmailAlreadyExistsException;
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
class UpdateProfileUserUseCaseTest {

    private static final Long USER_ID = 1L;

    private static final String CURRENT_NAME = "John Doe";
    private static final String CURRENT_EMAIL = "john@example.com";

    private static final String NEW_NAME = "Jane Doe";
    private static final String NEW_EMAIL = "jane@example.com";
    private static final String PASSWORD = "Password123";

    @Mock
    private UserGateway userGateway;

    private UpdateProfileUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProfileUserUseCase(userGateway);
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUserProfileSuccessfully() {
        User user = createUser();

        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.existsByEmail(NEW_EMAIL))
                .thenReturn(false);

        when(userGateway.save(user))
                .thenReturn(user);

        var output = useCase.execute(USER_ID, input);

        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(USER_ID, output.getId()),
                () -> assertEquals(NEW_NAME, output.getUserName()),
                () -> assertEquals(NEW_EMAIL, output.getEmail())
        );

        assertEquals(NEW_NAME, user.getUserName());
        assertEquals(NEW_EMAIL, user.getEmail());

        verify(userGateway).findById(USER_ID);
        verify(userGateway).existsByEmail(NEW_EMAIL);
        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(USER_ID, input)
        );

        verify(userGateway).findById(USER_ID);
        verify(userGateway, never()).existsByEmail(anyString());
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already in use")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        User user = createUser();

        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.existsByEmail(NEW_EMAIL))
                .thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> useCase.execute(USER_ID, input)
        );

        assertNotNull(exception);

        assertAll(
                () -> assertEquals(CURRENT_NAME, user.getUserName()),
                () -> assertEquals(CURRENT_EMAIL, user.getEmail())
        );

        verify(userGateway).findById(USER_ID);
        verify(userGateway).existsByEmail(NEW_EMAIL);
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should check email availability before updating profile")
    void shouldCheckEmailBeforeUpdatingProfile() {
        User user = createUser();

        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.existsByEmail(NEW_EMAIL))
                .thenReturn(false);

        when(userGateway.save(user))
                .thenReturn(user);

        useCase.execute(USER_ID, input);

        InOrder inOrder = inOrder(userGateway);

        inOrder.verify(userGateway).findById(USER_ID);
        inOrder.verify(userGateway).existsByEmail(NEW_EMAIL);
        inOrder.verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should save the updated user")
    void shouldSaveUpdatedUser() {
        User user = createUser();

        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.existsByEmail(NEW_EMAIL))
                .thenReturn(false);

        when(userGateway.save(user))
                .thenReturn(user);

        useCase.execute(USER_ID, input);

        assertAll(
                () -> assertEquals(NEW_NAME, user.getUserName()),
                () -> assertEquals(NEW_EMAIL, user.getEmail())
        );

        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should propagate exception when finding user fails")
    void shouldPropagateExceptionWhenFindingUserFails() {
        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
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
        verify(userGateway, never()).existsByEmail(anyString());
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception when checking email fails")
    void shouldPropagateExceptionWhenCheckingEmailFails() {
        User user = createUser();

        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        RuntimeException exception =
                new RuntimeException("Database error");

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.existsByEmail(NEW_EMAIL))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(USER_ID, input)
        );

        assertSame(exception, thrown);

        assertAll(
                () -> assertEquals(CURRENT_NAME, user.getUserName()),
                () -> assertEquals(CURRENT_EMAIL, user.getEmail())
        );

        verify(userGateway).findById(USER_ID);
        verify(userGateway).existsByEmail(NEW_EMAIL);
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate exception when saving user fails")
    void shouldPropagateExceptionWhenSavingUserFails() {
        User user = createUser();

        UpdateUserInput input =  UpdateUserInput.builder()
                .username(NEW_NAME)
                .email(NEW_EMAIL)
                .build();

        RuntimeException exception =
                new RuntimeException("Database error");

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(userGateway.existsByEmail(NEW_EMAIL))
                .thenReturn(false);

        when(userGateway.save(user))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(USER_ID, input)
        );

        assertSame(exception, thrown);

        verify(userGateway).findById(USER_ID);
        verify(userGateway).existsByEmail(NEW_EMAIL);
        verify(userGateway).save(user);
    }

    private User createUser() {
        return User.builder()
                .id(USER_ID)
                .userName(CURRENT_NAME)
                .email(CURRENT_EMAIL)
                .password(PASSWORD)
                .role(Role.MEDICO)
                .isActive(true)
                .build();
    }
}