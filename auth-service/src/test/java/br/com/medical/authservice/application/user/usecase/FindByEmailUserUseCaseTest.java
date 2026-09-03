        package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.InvalidEmailException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindByEmailUserUseCase Tests")
class FindByEmailUserUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String EMAIL = "john.doe@email.com";
    private static final String PASSWORD = "password123";
    private static final Role ROLE = Role.MEDICO;

    @Mock
    private UserGateway userGateway;

    private FindByEmailUserUseCase findByEmailUserUseCase;

    @BeforeEach
    void setUp() {
        findByEmailUserUseCase =
                new FindByEmailUserUseCase(userGateway);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {

        User user = createUser();

        when(userGateway.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        UserOutput output =
                findByEmailUserUseCase.execute(EMAIL);

        assertNotNull(output);

        assertAll(
                () -> assertEquals(USER_ID, output.getId()),
                () -> assertEquals(USER_NAME, output.getUserName()),
                () -> assertEquals(EMAIL, output.getEmail()),
                () -> assertEquals(ROLE, output.getRole())
        );

        verify(userGateway)
                .findByEmail(EMAIL);

        verifyNoMoreInteractions(userGateway);
    }

    @Test
    @DisplayName("Should throw InvalidEmailException when user is not found")
    void shouldThrowExceptionWhenUserIsNotFound() {

        when(userGateway.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidEmailException.class,
                () -> findByEmailUserUseCase.execute(EMAIL)
        );

        verify(userGateway)
                .findByEmail(EMAIL);

        verifyNoMoreInteractions(userGateway);
    }

    @Test
    @DisplayName("Should pass the correct email to gateway")
    void shouldPassCorrectEmailToGateway() {

        User user = createUser();

        when(userGateway.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        findByEmailUserUseCase.execute(EMAIL);

        verify(userGateway)
                .findByEmail(eq(EMAIL));
    }

    @Test
    @DisplayName("Should return data from the user found by gateway")
    void shouldReturnDataFromUserFoundByGateway() {

        User user = User.builder()
                .id(10L)
                .userName("Jane Doe")
                .email("jane@email.com")
                .password("secure123")
                .role(Role.ADMIN)
                .isActive(false)
                .build();

        when(userGateway.findByEmail("jane@email.com"))
                .thenReturn(Optional.of(user));

        UserOutput output =
                findByEmailUserUseCase.execute("jane@email.com");

        assertAll(
                () -> assertEquals(10L, output.getId()),
                () -> assertEquals("Jane Doe", output.getUserName()),
                () -> assertEquals("jane@email.com", output.getEmail()),
                () -> assertEquals(Role.ADMIN, output.getRole()));
    }

    @Test
    @DisplayName("Should propagate exception thrown by gateway")
    void shouldPropagateExceptionThrownByGateway() {

        RuntimeException exception =
                new RuntimeException("Database unavailable");

        when(userGateway.findByEmail(EMAIL))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> findByEmailUserUseCase.execute(EMAIL)
        );

        assertSame(exception, thrown);

        verify(userGateway)
                .findByEmail(EMAIL);
    }

    private User createUser() {

        return User.builder()
                .id(USER_ID)
                .userName(USER_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .role(ROLE)
                .build();
    }
}
