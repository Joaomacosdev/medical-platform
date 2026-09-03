package br.com.medical.authservice.application.authentication.usecase;

import br.com.medical.authservice.application.authentication.dto.AuthenticateUserInput;
import br.com.medical.authservice.application.authentication.dto.AuthenticationOutput;
import br.com.medical.authservice.domain.authentication.gatewys.AuthenticationGateway;
import br.com.medical.authservice.domain.authentication.gatewys.TokenGateway;
import br.com.medical.authservice.domain.authentication.model.AuthenticationResult;
import br.com.medical.authservice.domain.user.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "john@example.com";
    private static final String PASSWORD = "Password123";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    private AuthenticationGateway authenticationGateway;

    @Mock
    private TokenGateway tokenGateway;

    private AuthenticateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AuthenticateUserUseCase(
                authenticationGateway,
                tokenGateway
        );
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldAuthenticateUserSuccessfully() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);

        AuthenticationResult result =
                new AuthenticationResult(
                        USER_ID,
                        EMAIL,
                        Role.ADMIN);

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenReturn(result);

        when(tokenGateway.generate(
                USER_ID,
                EMAIL,
                result.role()
        )).thenReturn(ACCESS_TOKEN);

        when(tokenGateway.generateRefreshToken(USER_ID))
                .thenReturn(REFRESH_TOKEN);

        AuthenticationOutput output = useCase.execute(input);

        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(ACCESS_TOKEN, output.getAccessToken()),
                () -> assertEquals(REFRESH_TOKEN, output.getRefreshToken()),
                () -> assertEquals("Bearer", output.getTokenType()),
                () -> assertEquals(3600, output.getExpiresIn())
        );

        verify(authenticationGateway)
                .authenticate(EMAIL, PASSWORD);

        verify(tokenGateway)
                .generate(USER_ID, EMAIL, result.role());

        verify(tokenGateway)
                .generateRefreshToken(USER_ID);
    }

    @Test
    @DisplayName("Should authenticate using provided email and password")
    void shouldAuthenticateUsingProvidedCredentials() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);
        AuthenticationResult result =
                new AuthenticationResult(
                        USER_ID,
                        EMAIL,
                        Role.ADMIN);

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenReturn(result);

        when(tokenGateway.generate(
                USER_ID,
                EMAIL,
                result.role()
        )).thenReturn(ACCESS_TOKEN);

        when(tokenGateway.generateRefreshToken(USER_ID))
                .thenReturn(REFRESH_TOKEN);

        useCase.execute(input);

        verify(authenticationGateway)
                .authenticate(EMAIL, PASSWORD);
    }

    @Test
    @DisplayName("Should generate access token using authenticated user data")
    void shouldGenerateAccessTokenUsingAuthenticatedUserData() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);

        AuthenticationResult result =
                new AuthenticationResult(
                        USER_ID,
                        EMAIL,
                        Role.ADMIN);

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenReturn(result);

        when(tokenGateway.generate(
                USER_ID,
                EMAIL,
                result.role()
        )).thenReturn(ACCESS_TOKEN);

        when(tokenGateway.generateRefreshToken(USER_ID))
                .thenReturn(REFRESH_TOKEN);

        useCase.execute(input);

        verify(tokenGateway)
                .generate(
                        result.userId(),
                        result.email(),
                        result.role()
                );
    }

    @Test
    @DisplayName("Should generate refresh token using authenticated user id")
    void shouldGenerateRefreshTokenUsingUserId() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);

        AuthenticationResult result =
                new AuthenticationResult(
                        USER_ID,
                        EMAIL,
                        Role.ADMIN);

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenReturn(result);

        when(tokenGateway.generate(
                USER_ID,
                EMAIL,
                result.role()
        )).thenReturn(ACCESS_TOKEN);

        when(tokenGateway.generateRefreshToken(USER_ID))
                .thenReturn(REFRESH_TOKEN);

        useCase.execute(input);

        verify(tokenGateway)
                .generateRefreshToken(USER_ID);
    }

    @Test
    @DisplayName("Should generate access token before refresh token")
    void shouldGenerateAccessTokenBeforeRefreshToken() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);

        AuthenticationResult result =
                new AuthenticationResult(
                        USER_ID,
                        EMAIL,
                        Role.ADMIN);

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenReturn(result);

        when(tokenGateway.generate(
                USER_ID,
                EMAIL,
                result.role()
        )).thenReturn(ACCESS_TOKEN);

        when(tokenGateway.generateRefreshToken(USER_ID))
                .thenReturn(REFRESH_TOKEN);

        useCase.execute(input);

        InOrder inOrder = inOrder(
                authenticationGateway,
                tokenGateway
        );

        inOrder.verify(authenticationGateway)
                .authenticate(EMAIL, PASSWORD);

        inOrder.verify(tokenGateway)
                .generate(USER_ID, EMAIL, result.role());

        inOrder.verify(tokenGateway)
                .generateRefreshToken(USER_ID);
    }

    @Test
    @DisplayName("Should propagate exception when authentication fails")
    void shouldPropagateAuthenticationException() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);

        RuntimeException exception =
                new RuntimeException("Invalid credentials");

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(authenticationGateway)
                .authenticate(EMAIL, PASSWORD);

        verifyNoInteractions(tokenGateway);
    }

    @Test
    @DisplayName("Should propagate exception when access token generation fails")
    void shouldPropagateAccessTokenException() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);

        AuthenticationResult result =
                new AuthenticationResult(
                        USER_ID,
                        EMAIL,
                        Role.ADMIN);

        RuntimeException exception =
                new RuntimeException("Token generation failed");

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenReturn(result);

        when(tokenGateway.generate(
                USER_ID,
                EMAIL,
                result.role()
        )).thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(authenticationGateway)
                .authenticate(EMAIL, PASSWORD);

        verify(tokenGateway)
                .generate(USER_ID, EMAIL, result.role());

        verify(tokenGateway, never())
                .generateRefreshToken(anyLong());
    }

    @Test
    @DisplayName("Should propagate exception when refresh token generation fails")
    void shouldPropagateRefreshTokenException() {
        AuthenticateUserInput input =
                new AuthenticateUserInput(EMAIL, PASSWORD);

        AuthenticationResult result =
                new AuthenticationResult(
                        USER_ID,
                        EMAIL,
                        Role.ADMIN
                );

        RuntimeException exception =
                new RuntimeException("Refresh token generation failed");

        when(authenticationGateway.authenticate(EMAIL, PASSWORD))
                .thenReturn(result);

        when(tokenGateway.generate(
                USER_ID,
                EMAIL,
                result.role()
        )).thenReturn(ACCESS_TOKEN);

        when(tokenGateway.generateRefreshToken(USER_ID))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(input)
        );

        assertSame(exception, thrown);

        verify(authenticationGateway)
                .authenticate(EMAIL, PASSWORD);

        verify(tokenGateway)
                .generate(USER_ID, EMAIL, result.role());

        verify(tokenGateway)
                .generateRefreshToken(USER_ID);
    }
}