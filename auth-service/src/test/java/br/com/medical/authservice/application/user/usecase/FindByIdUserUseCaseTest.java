package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
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
@DisplayName("FindByIdUserUseCase Tests")
class FindByIdUserUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String EMAIL = "john.doe@email.com";
    private static final String PASSWORD = "password123";
    private static final Role ROLE = Role.MEDICO;

    @Mock
    private UserGateway userGateway;

    private FindByIdUserUseCase findByIdUserUseCase;

    @BeforeEach
    void setUp() {
        findByIdUserUseCase =
                new FindByIdUserUseCase(userGateway);
    }

    @Test
    @DisplayName("Should find user by id successfully")
    void shouldFindUserByIdSuccessfully() {

        User user = createUser();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserOutput output =
                findByIdUserUseCase.execute(USER_ID);

        assertNotNull(output);

        assertAll(
                () -> assertEquals(USER_ID, output.getId()),
                () -> assertEquals(USER_NAME, output.getUserName()),
                () -> assertEquals(EMAIL, output.getEmail()),
                () -> assertEquals(ROLE, output.getRole()));

        verify(userGateway)
                .findById(USER_ID);

        verifyNoMoreInteractions(userGateway);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> findByIdUserUseCase.execute(USER_ID)
        );

        verify(userGateway)
                .findById(USER_ID);

        verifyNoMoreInteractions(userGateway);
    }

    @Test
    @DisplayName("Should pass the correct id to gateway")
    void shouldPassCorrectIdToGateway() {

        User user = createUser();

        when(userGateway.findById(USER_ID))
                .thenReturn(Optional.of(user));

        findByIdUserUseCase.execute(USER_ID);

        verify(userGateway)
                .findById(eq(USER_ID));
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

        when(userGateway.findById(10L))
                .thenReturn(Optional.of(user));

        UserOutput output =
                findByIdUserUseCase.execute(10L);

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

        when(userGateway.findById(USER_ID))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> findByIdUserUseCase.execute(USER_ID)
        );

        assertSame(exception, thrown);

        verify(userGateway)
                .findById(USER_ID);
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
