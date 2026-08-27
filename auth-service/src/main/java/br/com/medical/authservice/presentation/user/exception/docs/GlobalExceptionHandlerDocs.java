package br.com.medical.authservice.presentation.user.exception.docs;

import br.com.medical.authservice.domain.user.exception.EmailAlreadyExistsException;
import br.com.medical.authservice.domain.user.exception.InvalidEmailException;
import br.com.medical.authservice.domain.user.exception.InvalidIsActiveExeption;
import br.com.medical.authservice.domain.user.exception.InvalidPasswordException;
import br.com.medical.authservice.domain.user.exception.InvalidRoleException;
import br.com.medical.authservice.domain.user.exception.InvalidUserNameException;
import br.com.medical.authservice.domain.user.exception.UserNotFoundException;
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
            summary = "Handle user not found",
            description = """
                    Handles requests where the requested user does not exist.
                    
                    This error is returned when an operation attempts to retrieve,
                    update, delete or otherwise access a user that cannot be found
                    using the provided identifier.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "UserNotFound",
                                    summary = "User not found response",
                                    value = """
                                            {
                                              "type": "https://api.medical-plataform-auth-service.com/errors/USER_NOT_FOUND",
                                              "title": "User not found.",
                                              "status": 404,
                                              "detail": "User not found",
                                              "instance": "/v1/users/999",
                                              "code": "USER_NOT_FOUND",
                                              "timestamp": "2026-08-27T22:00:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail handleNotFound(
            UserNotFoundException ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle duplicate email",
            description = """
                    Handles attempts to create or update a user using an email
                    address that is already registered in the system.
                    
                    Email addresses must be unique within the authentication service.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already registered",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "EmailAlreadyExists",
                                    summary = "Email already registered response",
                                    value = """
                                            {
                                              "type": "https://api.medical-plataform-auth-service.com/errors/EMAIL_ALREADY_EXISTS",
                                              "title": "The provided email is already registered.",
                                              "status": 409,
                                              "detail": "Email already exists",
                                              "instance": "/v1/users",
                                              "code": "EMAIL_ALREADY_EXISTS",
                                              "timestamp": "2026-08-27T22:00:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @ExceptionHandler(EmailAlreadyExistsException.class)
    ProblemDetail handleEmailAlreadyExists(
            EmailAlreadyExistsException ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle invalid email",
            description = """
                    Handles requests containing an invalid email address.
                    
                    The error is returned when the provided email does not satisfy
                    the email format or business validation rules defined by the
                    authentication service.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email address",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "InvalidEmail",
                                    summary = "Invalid email response",
                                    value = """
                                            {
                                              "type": "https://api.medical-plataform-auth-service.com/errors/INVALID_EMAIL",
                                              "title": "The provided email is invalid.",
                                              "status": 400,
                                              "detail": "Invalid email",
                                              "instance": "/v1/users",
                                              "code": "INVALID_EMAIL",
                                              "timestamp": "2026-08-27T22:00:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @ExceptionHandler(InvalidEmailException.class)
    ProblemDetail handleInvalidEmail(
            InvalidEmailException ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle inactive user",
            description = """
                    Handles operations involving a user whose account is inactive.
                    
                    Inactive users are not permitted to perform operations that
                    require an active account.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "403",
                    description = "User account is inactive",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "InactiveUser",
                                    summary = "Inactive user response",
                                    value = """
                                            {
                                              "type": "https://api.medical-plataform-auth-service.com/errors/USER_INACTIVE",
                                              "title": "User account is inactive.",
                                              "status": 403,
                                              "detail": "User is inactive",
                                              "instance": "/v1/users/1",
                                              "code": "USER_INACTIVE",
                                              "timestamp": "2026-08-27T22:00:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @ExceptionHandler(InvalidIsActiveExeption.class)
    ProblemDetail handleInactiveUser(
            InvalidIsActiveExeption ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle invalid password",
            description = """
                    Handles requests containing an invalid password.
                    
                    The error is returned when the password does not comply with
                    the password requirements established by the authentication
                    service.
                    
                    Password values are never exposed in the error response.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "InvalidPassword",
                                    summary = "Invalid password response",
                                    value = """
                                            {
                                              "type": "https://api.medical-plataform-auth-service.com/errors/INVALID_PASSWORD",
                                              "title": "The provided password is invalid.",
                                              "status": 400,
                                              "detail": "Invalid password",
                                              "instance": "/v1/users",
                                              "code": "INVALID_PASSWORD",
                                              "timestamp": "2026-08-27T22:00:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @ExceptionHandler(InvalidPasswordException.class)
    ProblemDetail handleInvalidPassword(
            InvalidPasswordException ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle invalid role",
            description = """
                    Handles requests containing an invalid user role.
                    
                    The error is returned when the requested role is not supported
                    or does not comply with the authorization rules of the system.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user role",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "InvalidRole",
                                    summary = "Invalid role response",
                                    value = """
                                            {
                                              "type": "https://api.medical-plataform-auth-service.com/errors/INVALID_ROLE",
                                              "title": "The provided role is invalid.",
                                              "status": 400,
                                              "detail": "Invalid role",
                                              "instance": "/v1/users/1/role",
                                              "code": "INVALID_ROLE",
                                              "timestamp": "2026-08-27T22:00:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @ExceptionHandler(InvalidRoleException.class)
    ProblemDetail handleInvalidRole(
            InvalidRoleException ex,
            HttpServletRequest request
    );


    @Operation(
            summary = "Handle invalid username",
            description = """
                    Handles requests containing an invalid username.
                    
                    The error is returned when the username does not comply with
                    the validation or business rules defined by the application.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid username",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "InvalidUserName",
                                    summary = "Invalid username response",
                                    value = """
                                            {
                                              "type": "https://api.medical-plataform-auth-service.com/errors/INVALID_USERNAME",
                                              "title": "The provided username is invalid.",
                                              "status": 400,
                                              "detail": "Invalid username",
                                              "instance": "/v1/users",
                                              "code": "INVALID_USERNAME",
                                              "timestamp": "2026-08-27T22:00:00Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @ExceptionHandler(InvalidUserNameException.class)
    ProblemDetail handleInvalidUserName(
            InvalidUserNameException ex,
            HttpServletRequest request
    );
}

