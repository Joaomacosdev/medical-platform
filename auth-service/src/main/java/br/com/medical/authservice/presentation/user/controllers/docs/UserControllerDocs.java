package br.com.medical.authservice.presentation.user.controllers.docs;

import br.com.medical.authservice.presentation.user.requests.CreateUserRequest;
import br.com.medical.authservice.presentation.user.requests.UpdatePasswordRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateRoleRequest;
import br.com.medical.authservice.presentation.user.requests.UpdateUserRequest;
import br.com.medical.authservice.presentation.user.responses.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(
        name = "User Management",
        description = """
                Endpoints for managing users within the authentication service,
                including user creation, retrieval, profile updates,
                password management, role management and deletion.
                """
)
public interface UserControllerDocs {

    @Operation(
            summary = "Create a new user",
            description = """
                    Creates a new user account in the authentication service.
                    
                    The email address must be unique. The password is securely
                    processed by the application before persistence.
                    
                    This endpoint is intended for user registration and does not
                    require prior authentication.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully created",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already registered",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest
    );


    @Operation(
            summary = "Find user by ID",
            description = """
                    Retrieves a user by its unique identifier.
                    
                    The endpoint returns the user's public information.
                    Sensitive information, such as the user's password,
                    is never exposed in the response.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<UserResponse> getUserById(
            @Parameter(
                    name = "id",
                    description = "Unique identifier of the user",
                    required = true,
                    example = "1"
            )
            Long id
    );


    @Operation(
            summary = "Find users",
            description = """
                    Retrieves users according to the provided search criteria.
                    
                    The search can be performed using the user's email,
                    username, or both parameters.
                    
                    When no matching users are found, the service returns
                    HTTP 204 No Content.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users successfully retrieved",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = UserResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No users found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid search parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<?> getUsers(

            @Parameter(
                    name = "email",
                    description = "User's email address used as a search criterion",
                    example = "john.doe@email.com"
            )
            String email,

            @Parameter(
                    name = "userName",
                    description = "Username used as a search criterion",
                    example = "john.doe"
            )
            String userName
    );


    @Operation(
            summary = "Update user profile",
            description = """
                    Updates the profile information of an existing user.
                    
                    Only the fields supported by the update request should
                    be provided. The user must be authenticated and authorized
                    to perform this operation.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already registered",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<UserResponse> updateUser(

            @Parameter(
                    name = "id",
                    description = "Unique identifier of the user",
                    required = true,
                    example = "1"
            )
            Long id,

            @Valid @RequestBody
            UpdateUserRequest updateUserRequest
    );


    @Operation(
            summary = "Update user password",
            description = """
                    Updates the password of an existing user.
                    
                    The new password must comply with the password validation
                    rules defined by the authentication service.
                    
                    For security reasons, the password is never returned
                    in the API response.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password or request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<UserResponse> updateUserPassword(

            @Parameter(
                    name = "id",
                    description = "Unique identifier of the user",
                    required = true,
                    example = "1"
            )
            Long id,

            @Valid @RequestBody
            UpdatePasswordRequest updatePasswordRequest
    );


    @Operation(
            summary = "Update user role",
            description = """
                    Updates the authorization role assigned to an existing user.
                    
                    This operation changes the user's access level within the
                    application and therefore should only be available to
                    authorized administrators.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User role successfully updated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid role or request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<UserResponse> updateUserRole(

            @Parameter(
                    name = "id",
                    description = "Unique identifier of the user",
                    required = true,
                    example = "1"
            )
            Long id,

            @Valid @RequestBody
            UpdateRoleRequest updateRoleRequest
    );


    @Operation(
            summary = "Delete user",
            description = """
                    Permanently removes an existing user from the authentication
                    service.
                    
                    This operation requires authentication and appropriate
                    authorization privileges.
                    """)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid user ID",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    ResponseEntity<Void> deleteUserById(

            @Parameter(
                    name = "id",
                    description = "Unique identifier of the user",
                    required = true,
                    example = "1"
            )
            Long id
    );
}