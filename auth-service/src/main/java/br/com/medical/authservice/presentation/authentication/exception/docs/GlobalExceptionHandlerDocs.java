package br.com.medical.authservice.presentation.authentication.exception.docs;

import br.com.medical.authservice.domain.authentication.exception.InvalidCredentialsException;
import br.com.medical.authservice.domain.authentication.exception.InvalidTokenException;
import br.com.medical.authservice.domain.authentication.exception.TokenGenerationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;

public interface GlobalExceptionHandlerDocs {

    @Operation(
            summary = "Handle invalid credentials",
            description = """
                Handles authentication attempts with invalid credentials.

                This error is returned when the provided email, username or
                password does not match the credentials registered in the
                authentication service.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "InvalidCredentials",
                                    summary = "Invalid credentials response",
                                    value = """
                                        {
                                          "type": "https://api.medical-plataform-auth-service.com/errors/INVALID_CREDENTIALS",
                                          "title": "Invalid authentication credentials.",
                                          "status": 401,
                                          "detail": "Invalid credentials",
                                          "instance": "/api/auth/v1/login",
                                          "code": "INVALID_CREDENTIALS",
                                          "timestamp": "2026-08-27T22:00:00Z"
                                        }
                                        """
                            )
                    )
            )
    })
    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle invalid authentication token",
            description = """
                Handles requests containing an invalid or expired authentication token.

                This error is returned when the provided JWT token cannot be
                validated or is no longer valid.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired authentication token",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "InvalidToken",
                                    summary = "Invalid token response",
                                    value = """
                                        {
                                          "type": "https://api.medical-plataform-auth-service.com/errors/INVALID_TOKEN",
                                          "title": "The authentication token is invalid or expired.",
                                          "status": 401,
                                          "detail": "Token de autenticação inválido ou expirado.",
                                          "instance": "/api/users/v1/me",
                                          "code": "INVALID_TOKEN",
                                          "timestamp": "2026-08-27T22:00:00Z"
                                        }
                                        """
                            )
                    )
            )
    })
    @ExceptionHandler(InvalidTokenException.class)
    ProblemDetail handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle token generation failure",
            description = """
                Handles failures that occur while generating authentication tokens.

                This error is returned when the authentication service is unable
                to generate a valid access or refresh token.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "500",
                    description = "Token generation failure",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "TokenGenerationError",
                                    summary = "Token generation failure response",
                                    value = """
                                        {
                                          "type": "https://api.medical-plataform-auth-service.com/errors/TOKEN_GENERATION_ERROR",
                                          "title": "The authentication token could not be generated.",
                                          "status": 500,
                                          "detail": "Não foi possível gerar o token de autenticação.",
                                          "instance": "/api/auth/v1/login",
                                          "code": "TOKEN_GENERATION_ERROR",
                                          "timestamp": "2026-08-27T22:00:00Z"
                                        }
                                        """
                            )
                    )
            )
    })
    @ExceptionHandler(TokenGenerationException.class)
    ProblemDetail handleTokenGeneration(
            TokenGenerationException ex,
            HttpServletRequest request
    );

}
