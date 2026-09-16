package br.com.medical.authservice.application.authentication.dto;

public class AuthenticationOutput {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    private AuthenticationOutput(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;
        this.tokenType = builder.tokenType;
        this.expiresIn = builder.expiresIn;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private long expiresIn;

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }


        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public AuthenticationOutput build() {
            return new AuthenticationOutput(this);
        }
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
