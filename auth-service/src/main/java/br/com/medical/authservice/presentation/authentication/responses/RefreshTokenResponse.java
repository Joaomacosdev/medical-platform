package br.com.medical.authservice.presentation.authentication.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "RefreshTokenResponse",
        description = """
                Response payload returned after successfully refreshing
                the user's authentication tokens.
                
                Contains a new access token and the refresh token used
                during the refresh operation.
                """
)
public class RefreshTokenResponse {

    @Schema(
            description = """
                    New JWT access token used to authenticate requests
                    to protected endpoints.
                    """,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String accessToken;


    @Schema(
            description = """
                    Refresh token returned by the authentication service.
                    
                    Depending on the token rotation strategy, this may be
                    a newly generated refresh token.
                    """,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;


    public RefreshTokenResponse() {
    }


    public RefreshTokenResponse(
            String accessToken,
            String refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
