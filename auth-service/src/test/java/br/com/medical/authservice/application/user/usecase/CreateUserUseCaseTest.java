        package br.com.medical.authservice.application.user.usecase;

import br.com.medical.authservice.application.user.dto.CreateUserInput;
import br.com.medical.authservice.application.user.dto.UserOutput;
import br.com.medical.authservice.domain.user.entities.User;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.domain.user.exception.EmailAlreadyExistsException;
import br.com.medical.authservice.domain.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase Tests")
class CreateUserUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String EMAIL = "john.doe@email.com";
    private static final String PASSWORD = "password123";
    private static final Role ROLE = Role.MEDICO;

    @Mock
    private UserGateway userGateway;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCase(userGateway);
    }

    @Test
    @DisplayName("Should create user successfully when email does not exist")
    void shouldCreateUserSuccessfullyWhenEmailDoesNotExist() {

        CreateUserInput input = createInput();

        User savedUser = createUser();

        when(userGateway.existsByEmail(EMAIL))
                .thenReturn(false);

        when(userGateway.save(any(User.class)))
                .thenReturn(savedUser);

        UserOutput output = createUserUseCase.execute(input);

        assertNotNull(output);

        verify(userGateway)
                .existsByEmail(EMAIL);

        verify(userGateway)
                .save(any(User.class));

        verifyNoMoreInteractions(userGateway);
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        CreateUserInput input = createInput();

        when(userGateway.existsByEmail(EMAIL))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> createUserUseCase.execute(input)
        );

        verify(userGateway)
                .existsByEmail(EMAIL);

        verify(userGateway, never())
                .save(any(User.class));
    }

    @Test
    @DisplayName("Should save the user with the correct data")
    void shouldSaveUserWithCorrectData() {

        CreateUserInput input = createInput();

        User savedUser = createUser();

        when(userGateway.existsByEmail(EMAIL))
                .thenReturn(false);

        when(userGateway.save(any(User.class)))
                .thenReturn(savedUser);

        createUserUseCase.execute(input);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userGateway)
                .save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertAll(
                () -> assertEquals(USER_NAME, userToSave.getUserName()),
                () -> assertEquals(EMAIL, userToSave.getEmail()),
                () -> assertEquals(PASSWORD, userToSave.getPassword()),
                () -> assertEquals(ROLE, userToSave.getRole()),
                () -> assertTrue(userToSave.isActive())
        );
    }

    @Test
    @DisplayName("Should return the saved user as UserOutput")
    void shouldReturnSavedUserAsOutput() {

        CreateUserInput input = createInput();

        User savedUser = createUser();

        when(userGateway.existsByEmail(EMAIL))
                .thenReturn(false);

        when(userGateway.save(any(User.class)))
                .thenReturn(savedUser);

        UserOutput output = createUserUseCase.execute(input);

        assertNotNull(output);

        assertAll(
                () -> assertEquals(USER_ID, output.getId()),
                () -> assertEquals(USER_NAME, output.getUserName()),
                () -> assertEquals(EMAIL, output.getEmail()),
                () -> assertEquals(ROLE, output.getRole())
        );
    }

    @Test
    @DisplayName("Should check email existence before saving user")
    void shouldCheckEmailExistenceBeforeSavingUser() {

        CreateUserInput input = createInput();

        User savedUser = createUser();

        when(userGateway.existsByEmail(EMAIL))
                .thenReturn(false);

        when(userGateway.save(any(User.class)))
                .thenReturn(savedUser);

        createUserUseCase.execute(input);

        var inOrder = inOrder(userGateway);

        inOrder.verify(userGateway)
                .existsByEmail(EMAIL);

        inOrder.verify(userGateway)
                .save(any(User.class));
    }

    @Test
    @DisplayName("Should propagate exception thrown by gateway when checking email")
    void shouldPropagateExceptionThrownByGatewayWhenCheckingEmail() {

        CreateUserInput input = createInput();

        RuntimeException exception =
                new RuntimeException("Database unavailable");

        when(userGateway.existsByEmail(EMAIL))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createUserUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(userGateway)
                .existsByEmail(EMAIL);

        verify(userGateway, never())
                .save(any(User.class));
    }

    @Test
    @DisplayName("Should propagate exception thrown by gateway when saving user")
    void shouldPropagateExceptionThrownByGatewayWhenSavingUser() {

        CreateUserInput input = createInput();

        RuntimeException exception =
                new RuntimeException("Database error");

        when(userGateway.existsByEmail(EMAIL))
                .thenReturn(false);

        when(userGateway.save(any(User.class)))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createUserUseCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(userGateway)
                .existsByEmail(EMAIL);

        verify(userGateway)
                .save(any(User.class));
    }

    private CreateUserInput createInput() {

        return  CreateUserInput
                .builder()
                .userName(USER_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .role(ROLE)
                .build();
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
