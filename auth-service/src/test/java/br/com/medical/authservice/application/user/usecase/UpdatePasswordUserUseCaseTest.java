package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UpdatePasswordInput;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePasswordUserUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final String CURRENT_PASSWORD = "OldPassword123";
    private static final String NEW_PASSWORD = "NewPassword123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPassword";

    @Mock
    private UserGateway userGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UpdatePasswordUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdatePasswordUserUseCase(
                userGateway,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("Should update user password successfully")
    void shouldUpdatePasswordSuccessfully() {
        User user = createUser();

        UpdatePasswordInput input =
                UpdatePasswordInput.builder().password(NEW_PASSWORD).build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(NEW_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        when(userGateway.save(user))
                .thenReturn(user);

        var output = useCase.execute(USER_ID, input);

        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(USER_ID, output.getId()),
                () -> assertEquals(ENCODED_PASSWORD, user.getPassword())
        );

        verify(userGateway).findById(USER_ID);
        verify(passwordEncoder).encode(NEW_PASSWORD);
        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        UpdatePasswordInput input =
                UpdatePasswordInput.builder().password(NEW_PASSWORD).build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(USER_ID, input)
        );

        verify(userGateway).findById(USER_ID);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should encode new password before saving user")
    void shouldEncodePasswordBeforeSaving() {
        User user = createUser();

        UpdatePasswordInput input =
                 UpdatePasswordInput.builder().password(NEW_PASSWORD).build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(NEW_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        when(userGateway.save(user))
                .thenReturn(user);

        useCase.execute(USER_ID, input);

        InOrder inOrder = inOrder(
                userGateway,
                passwordEncoder
        );

        inOrder.verify(userGateway).findById(USER_ID);
        inOrder.verify(passwordEncoder).encode(NEW_PASSWORD);
        inOrder.verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should save user with encoded password")
    void shouldSaveUserWithEncodedPassword() {
        User user = createUser();

        UpdatePasswordInput input =
                UpdatePasswordInput.builder().password(NEW_PASSWORD).build();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(NEW_PASSWORD))
                .thenReturn(ENCODED_PASSWORD);

        when(userGateway.save(user))
                .thenReturn(user);

        useCase.execute(USER_ID, input);

        assertEquals(ENCODED_PASSWORD, user.getPassword());

        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Should propagate exception when password encoding fails")
    void shouldPropagateExceptionWhenPasswordEncodingFails() {
        User user = createUser();

        UpdatePasswordInput input =
                UpdatePasswordInput.builder().password(NEW_PASSWORD).build();

        RuntimeException exception =
                new RuntimeException("Password encoding failed");

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(NEW_PASSWORD))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(USER_ID, input)
        );

        assertSame(exception, thrown);

        verify(userGateway).findById(USER_ID);
        verify(passwordEncoder).encode(NEW_PASSWORD);
        verify(userGateway, never()).save(any());
    }

    private User createUser() {
        return User.builder()
                .id(USER_ID)
                .userName("John Doe")
                .email("john@example.com")
                .password(CURRENT_PASSWORD)
                .role(Role.MEDICO)
                .isActive(true)
                .build();
    }
}