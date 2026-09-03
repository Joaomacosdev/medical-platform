        package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListUserUseCase Tests")
class ListUserUseCaseTest {

    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;

    private static final String FIRST_USER_NAME = "John Doe";
    private static final String SECOND_USER_NAME = "Jane Doe";

    private static final String FIRST_EMAIL = "john@email.com";
    private static final String SECOND_EMAIL = "jane@email.com";

    private static final String PASSWORD = "password123";

    @Mock
    private UserGateway userGateway;

    private ListUserUseCase listUserUseCase;

    @BeforeEach
    void setUp() {
        listUserUseCase = new ListUserUseCase(userGateway);
    }

    @Test
    @DisplayName("Should return all users as UserOutput")
    void shouldReturnAllUsersAsUserOutput() {

        User firstUser = createUser(
                FIRST_USER_ID,
                FIRST_USER_NAME,
                FIRST_EMAIL,
                Role.MEDICO
        );

        User secondUser = createUser(
                SECOND_USER_ID,
                SECOND_USER_NAME,
                SECOND_EMAIL,
                Role.ADMIN
        );

        when(userGateway.findAll())
                .thenReturn(List.of(firstUser, secondUser));

        List<UserOutput> output = listUserUseCase.execute();

        assertNotNull(output);
        assertEquals(2, output.size());

        assertAll(
                () -> assertEquals(
                        FIRST_USER_ID,
                        output.get(0).getId()
                ),
                () -> assertEquals(
                        FIRST_USER_NAME,
                        output.get(0).getUserName()
                ),
                () -> assertEquals(
                        FIRST_EMAIL,
                        output.get(0).getEmail()
                ),
                () -> assertEquals(
                        Role.MEDICO,
                        output.get(0).getRole()
                ),

                () -> assertEquals(
                        SECOND_USER_ID,
                        output.get(1).getId()
                ),
                () -> assertEquals(
                        SECOND_USER_NAME,
                        output.get(1).getUserName()
                ),
                () -> assertEquals(
                        SECOND_EMAIL,
                        output.get(1).getEmail()
                ),
                () -> assertEquals(
                        Role.ADMIN,
                        output.get(1).getRole()
                )

        );

        verify(userGateway, times(1))
                .findAll();
    }

    @Test
    @DisplayName("Should return empty list when there are no users")
    void shouldReturnEmptyListWhenThereAreNoUsers() {

        when(userGateway.findAll())
                .thenReturn(List.of());

        List<UserOutput> output = listUserUseCase.execute();

        assertNotNull(output);
        assertTrue(output.isEmpty());

        verify(userGateway, times(1))
                .findAll();
    }

    @Test
    @DisplayName("Should preserve the order of users")
    void shouldPreserveOrderOfUsers() {

        User firstUser = createUser(
                FIRST_USER_ID,
                FIRST_USER_NAME,
                FIRST_EMAIL,
                Role.MEDICO
        );

        User secondUser = createUser(
                SECOND_USER_ID,
                SECOND_USER_NAME,
                SECOND_EMAIL,
                Role.ADMIN
        );

        when(userGateway.findAll())
                .thenReturn(List.of(firstUser, secondUser));

        List<UserOutput> output = listUserUseCase.execute();

        assertEquals(
                FIRST_USER_ID,
                output.get(0).getId()
        );

        assertEquals(
                SECOND_USER_ID,
                output.get(1).getId()
        );
    }

    @Test
    @DisplayName("Should propagate exception thrown by gateway")
    void shouldPropagateExceptionThrownByGateway() {

        RuntimeException exception =
                new RuntimeException("Database unavailable");

        when(userGateway.findAll())
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> listUserUseCase.execute()
        );

        assertSame(exception, thrown);

        verify(userGateway, times(1))
                .findAll();
    }

    private User createUser(
            Long id,
            String userName,
            String email,
            Role role
    ) {
        return User.builder()
                .id(id)
                .userName(userName)
                .email(email)
                .password(PASSWORD)
                .role(role)
                .build();
    }
}
