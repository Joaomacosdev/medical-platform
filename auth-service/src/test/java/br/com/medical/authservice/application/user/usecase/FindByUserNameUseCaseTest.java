package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.InvalidUserNameException;
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
@DisplayName("FindByUserNameUseCase Tests")
class FindByUserNameUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String EMAIL = "john.doe@email.com";
    private static final String PASSWORD = "password123";
    private static final Role ROLE = Role.MEDICO;

    @Mock
    private UserGateway userGateway;

    private FindByUserNameUseCase findByUserNameUseCase;

    @BeforeEach
    void setUp() {
        findByUserNameUseCase =
                new FindByUserNameUseCase(userGateway);
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void shouldFindUserByUsernameSuccessfully() {

        User user = createUser();

        when(userGateway.findByUserName(USER_NAME))
                .thenReturn(Optional.of(user));

        UserOutput output =
                findByUserNameUseCase.execute(USER_NAME);

        assertNotNull(output);

        assertAll(
                () -> assertEquals(USER_ID, output.getId()),
                () -> assertEquals(USER_NAME, output.getUserName()),
                () -> assertEquals(EMAIL, output.getEmail()),
                () -> assertEquals(ROLE, output.getRole())
        );

        verify(userGateway)
                .findByUserName(USER_NAME);
    }

    @Test
    @DisplayName("Should throw InvalidUserNameException when user is not found")
    void shouldThrowExceptionWhenUserIsNotFound() {

        when(userGateway.findByUserName(USER_NAME))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidUserNameException.class,
                () -> findByUserNameUseCase.execute(USER_NAME)
        );

        verify(userGateway)
                .findByUserName(USER_NAME);

        verify(userGateway, never())
                .save(any());
    }

    @Test
    @DisplayName("Should pass the correct username to gateway")
    void shouldPassCorrectUsernameToGateway() {

        User user = createUser();

        when(userGateway.findByUserName(USER_NAME))
                .thenReturn(Optional.of(user));

        findByUserNameUseCase.execute(USER_NAME);

        verify(userGateway)
                .findByUserName(eq(USER_NAME));
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

        when(userGateway.findByUserName("Jane Doe"))
                .thenReturn(Optional.of(user));

        UserOutput output =
                findByUserNameUseCase.execute("Jane Doe");

        assertAll(
                () -> assertEquals(10L, output.getId()),
                () -> assertEquals("Jane Doe", output.getUserName()),
                () -> assertEquals("jane@email.com", output.getEmail()),
                () -> assertEquals(Role.ADMIN, output.getRole())
        );
    }

    @Test
    @DisplayName("Should propagate exception thrown by gateway")
    void shouldPropagateExceptionThrownByGateway() {

        RuntimeException exception =
                new RuntimeException("Database unavailable");

        when(userGateway.findByUserName(USER_NAME))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> findByUserNameUseCase.execute(USER_NAME)
        );

        assertSame(exception, thrown);

        verify(userGateway)
                .findByUserName(USER_NAME);
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
