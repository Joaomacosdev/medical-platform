package br.com.medical.authservice.presentation.authentication.controller.docs;

import br.com.medical.authservice.presentation.authentication.requests.AuthenticateUserRequest;
import br.com.medical.authservice.presentation.authentication.requests.RefreshTokenRequest;
import br.com.medical.authservice.presentation.authentication.responses.AuthenticateUserResponse;
import br.com.medical.authservice.presentation.authentication.responses.RefreshTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
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
        name = "Authentication",
        description = """
                Endpoints responsible for user authentication and token management,
                including user login and refresh token operations.
                """
)
public interface AuthControllerDocs {

    @Operation(
            summary = "Authenticate user",
            description = """
                    Authenticates a user using their credentials.
                    
                    When the provided credentials are valid, the authentication
                    service generates an access token and a refresh token.
                    
                    This endpoint does not require prior authentication.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully authenticated",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthenticateUserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
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
    ResponseEntity<AuthenticateUserResponse> authenticateUser(
            AuthenticateUserRequest authenticateUserRequest
    );


    @Operation(
            summary = "Refresh access token",
            description = """
                    Generates a new access token using a valid refresh token.
                    
                    The refresh token must be valid and not expired.
                    This endpoint allows the client to obtain a new access
                    token without requiring the user to authenticate again.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token successfully refreshed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RefreshTokenResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
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
    ResponseEntity<RefreshTokenResponse> refreshToken(
            RefreshTokenRequest refreshTokenRequest
    );
}

