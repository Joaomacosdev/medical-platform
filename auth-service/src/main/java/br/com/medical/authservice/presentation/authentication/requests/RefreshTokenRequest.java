package br.com.medical.authservice.presentation.authentication.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "RefreshTokenRequest",
        description = """
                Request payload used to obtain a new access token.
                
                The client must provide a valid refresh token previously
                issued by the authentication service.
                """
)
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(
            description = """
                    Refresh token previously issued by the authentication service.
                    
                    The token must be valid and not expired.
                    """,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;


    public RefreshTokenRequest() {
    }


    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public String getRefreshToken() {
        return refreshToken;
    }
}
