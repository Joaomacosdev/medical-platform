package br.com.medical.authservice.presentation.user.controllers;

import br.com.medical.authservice.application.user.dto.*;
import br.com.medical.authservice.application.user.usecase.*;
import br.com.medical.authservice.domain.user.enums.Role;
import br.com.medical.authservice.infra.security.CustomUserDetails;
import br.com.medical.authservice.presentation.user.requests.CreateUserRequest;
import br.com.medical.authservice.presentation.user.requests.UpdatePasswordRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateRoleRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateUserRequest;
import br.com.medical.authservice.presentation.user.responses.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "John Doe";
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "Password123";

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private FindByIdUserUseCase findByIdUserUseCase;

    @Mock
    private FindByEmailUserUseCase findByEmailUserUseCase;

    @Mock
    private FindByUserNameUseCase findByUserNameUseCase;

    @Mock
    private ListUserUseCase listUserUseCase;

    @Mock
    private UpdateProfileUserUseCase updateProfileUserUseCase;

    @Mock
    private UpdatePasswordUserUseCase updatePasswordUserUseCase;

    @Mock
    private UpdateRoleUserUseCase updateRoleUserUseCase;

    @Mock
    private DeleteByIdUserUseCase deleteByIdUserUseCase;

    @Mock
    private CustomUserDetails userDetails;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(
                createUserUseCase,
                findByIdUserUseCase,
                findByEmailUserUseCase,
                findByUserNameUseCase,
                listUserUseCase,
                updateProfileUserUseCase,
                updatePasswordUserUseCase,
                updateRoleUserUseCase,
                deleteByIdUserUseCase
        );
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        CreateUserRequest request = createUserRequest();
        UserOutput output = createUserOutput();

        when(createUserUseCase.execute(any(CreateUserInput.class)))
                .thenReturn(output);

        ResponseEntity<UserResponse> response =
                controller.createUser(request);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(USER_ID, response.getBody().getId()),
                () -> assertEquals(USER_NAME, response.getBody().getUserName()),
                () -> assertEquals(EMAIL, response.getBody().getEmail())
        );

        verify(createUserUseCase).execute(any(CreateUserInput.class));
    }

    @Test
    @DisplayName("Should get current authenticated user")
    void shouldGetCurrentUser() {
        UserOutput output = createUserOutput();

        when(userDetails.getUserId())
                .thenReturn(USER_ID);

        when(findByIdUserUseCase.execute(USER_ID))
                .thenReturn(output);

        ResponseEntity<UserResponse> response =
                controller.getCurrentUser(userDetails);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(USER_ID, response.getBody().getId()),
                () -> assertEquals(USER_NAME, response.getBody().getUserName()),
                () -> assertEquals(EMAIL, response.getBody().getEmail())
        );

        verify(userDetails).getUserId();
        verify(findByIdUserUseCase).execute(USER_ID);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldGetUserByEmail() {
        UserOutput output = createUserOutput();

        when(findByEmailUserUseCase.execute(EMAIL))
                .thenReturn(output);

        ResponseEntity<?> response =
                controller.getUsers(EMAIL, null);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertInstanceOf(UserResponse.class, response.getBody()),
                () -> assertEquals(
                        USER_ID,
                        ((UserResponse) response.getBody()).getId()
                )
        );

        verify(findByEmailUserUseCase).execute(EMAIL);
        verify(findByUserNameUseCase, never()).execute(anyString());
        verify(listUserUseCase, never()).execute();
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldGetUserByUserName() {
        UserOutput output = createUserOutput();

        when(findByUserNameUseCase.execute(USER_NAME))
                .thenReturn(output);

        ResponseEntity<?> response =
                controller.getUsers(null, USER_NAME);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertInstanceOf(UserResponse.class, response.getBody()),
                () -> assertEquals(
                        USER_ID,
                        ((UserResponse) response.getBody()).getId()
                )
        );

        verify(findByUserNameUseCase).execute(USER_NAME);
        verify(findByEmailUserUseCase, never()).execute(anyString());
        verify(listUserUseCase, never()).execute();
    }

    @Test
    @DisplayName("Should list all users when no filter is provided")
    void shouldListAllUsers() {
        UserOutput user1 = createUserOutput(
                1L,
                "John Doe",
                "john@example.com"
        );

        UserOutput user2 = createUserOutput(
                2L,
                "Jane Doe",
                "jane@example.com"
        );

        when(listUserUseCase.execute())
                .thenReturn(List.of(user1, user2));

        ResponseEntity<?> response =
                controller.getUsers(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertInstanceOf(List.class, response.getBody());

        List<?> users = (List<?>) response.getBody();

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertInstanceOf(UserResponse.class, users.get(0)),
                () -> assertInstanceOf(UserResponse.class, users.get(1)),
                () -> assertEquals(
                        1L,
                        ((UserResponse) users.get(0)).getId()
                ),
                () -> assertEquals(
                        2L,
                        ((UserResponse) users.get(1)).getId()
                )
        );

        verify(listUserUseCase).execute();
        verify(findByEmailUserUseCase, never()).execute(anyString());
        verify(findByUserNameUseCase, never()).execute(anyString());
    }

    @Test
    @DisplayName("Should prioritize email filter when email and username are provided")
    void shouldPrioritizeEmailFilter() {
        UserOutput output = createUserOutput();

        when(findByEmailUserUseCase.execute(EMAIL))
                .thenReturn(output);

        ResponseEntity<?> response =
                controller.getUsers(EMAIL, USER_NAME);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(findByEmailUserUseCase).execute(EMAIL);
        verify(findByUserNameUseCase, never()).execute(anyString());
        verify(listUserUseCase, never()).execute();
    }

    @Test
    @DisplayName("Should update user profile successfully")
    void shouldUpdateUser() {
        UpdateUserRequest request = createUpdateUserRequest();
        UserOutput output = createUserOutput(
                USER_ID,
                "Jane Doe",
                "jane@example.com"
        );

        when(updateProfileUserUseCase.execute(
                eq(USER_ID),
                any(UpdateUserInput.class)
        )).thenReturn(output);

        ResponseEntity<UserResponse> response =
                controller.updateUser(USER_ID, request);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(USER_ID, response.getBody().getId()),
                () -> assertEquals(
                        "Jane Doe",
                        response.getBody().getUserName()
                ),
                () -> assertEquals(
                        "jane@example.com",
                        response.getBody().getEmail()
                )
        );

        verify(updateProfileUserUseCase)
                .execute(eq(USER_ID), any(UpdateUserInput.class));
    }

    @Test
    @DisplayName("Should update user password successfully")
    void shouldUpdateUserPassword() {
        UpdatePasswordRequest request =
                createUpdatePasswordRequest();

        UserOutput output = createUserOutput();

        when(updatePasswordUserUseCase.execute(
                eq(USER_ID),
                any(UpdatePasswordInput.class)
        )).thenReturn(output);

        ResponseEntity<UserResponse> response =
                controller.updateUserPassword(USER_ID, request);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(USER_ID, response.getBody().getId())
        );

        verify(updatePasswordUserUseCase)
                .execute(eq(USER_ID), any(UpdatePasswordInput.class));
    }

    @Test
    @DisplayName("Should update user role successfully")
    void shouldUpdateUserRole() {
        UpdateRoleRequest request =
                createUpdateRoleRequest();

        UserOutput output = createUserOutput();

        when(updateRoleUserUseCase.execute(
                eq(USER_ID),
                any(UpdateRoleInput.class)
        )).thenReturn(output);

        ResponseEntity<UserResponse> response =
                controller.updateUserRole(USER_ID, request);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(USER_ID, response.getBody().getId())
        );

        verify(updateRoleUserUseCase)
                .execute(eq(USER_ID), any(UpdateRoleInput.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserById() {
        ResponseEntity<Void> response =
                controller.deleteUserById(USER_ID);

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        assertNull(response.getBody());

        verify(deleteByIdUserUseCase)
                .execute(USER_ID);
    }

    @Test
    @DisplayName("Should execute delete use case before returning response")
    void shouldExecuteDeleteBeforeReturningResponse() {
        controller.deleteUserById(USER_ID);

        InOrder inOrder = inOrder(deleteByIdUserUseCase);

        inOrder.verify(deleteByIdUserUseCase)
                .execute(USER_ID);
    }

    private CreateUserRequest createUserRequest() {
        return new CreateUserRequest(
                USER_NAME,
                EMAIL,
                PASSWORD,
                Role.MEDICO
        );
    }

    private UpdateUserRequest createUpdateUserRequest() {
        return new UpdateUserRequest(
                "Jane Doe",
                "jane@example.com"
        );
    }

    private UpdatePasswordRequest createUpdatePasswordRequest() {
        return new UpdatePasswordRequest(
                "NewPassword123"
        );
    }

    private UpdateRoleRequest createUpdateRoleRequest() {
        return new UpdateRoleRequest(
                Role.ADMIN
        );
    }

    private UserOutput createUserOutput() {
        return createUserOutput(
                USER_ID,
                USER_NAME,
                EMAIL
        );
    }

    private UserOutput createUserOutput(
            Long id,
            String userName,
            String email
    ) {
        return  UserOutput.builder()
                .id(id)
                .userName(userName)
                .email(email)
                .role(Role.ADMIN)
                .build();
    }
}

