package br.com.medical.authservice.presentation.authentication.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "AuthenticateUserResponse",
        description = """
                Response payload returned after successful user authentication.
                
                Contains the access token used to authenticate requests,
                the refresh token used to obtain a new access token,
                the token type, and the access token expiration time.
                """
)
public class AuthenticateUserResponse {

    @Schema(
            description = """
                    JWT access token used to authenticate requests to
                    protected endpoints.
                    """,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String accessToken;


    @Schema(
            description = """
                    Refresh token used to obtain a new access token
                    when the current access token expires.
                    """,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;


    @Schema(
            description = "Authentication scheme used to authorize requests.",
            example = "Bearer",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tokenType;


    @Schema(
            description = """
                    Access token expiration time expressed in seconds.
                    """,
            example = "3600",
            format = "int64",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private long expiresIn;


    public AuthenticateUserResponse() {
    }


    public AuthenticateUserResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
