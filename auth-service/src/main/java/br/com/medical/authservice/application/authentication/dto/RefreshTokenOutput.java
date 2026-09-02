package br.com.medical.authservice.application.authentication.dto;

public class RefreshTokenOutput {


    private String accessToken;
    private String refreshToken;


    private RefreshTokenOutput(Builder builder) {
        this.accessToken = builder.accessToken;
        this.refreshToken = builder.refreshToken;

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{
        private String accessToken;
        private String refreshToken;


        public Builder accessToken(String accessToken){
            this.accessToken = accessToken;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }



        public RefreshTokenOutput build() {
            return new RefreshTokenOutput(this);
        }
    }


    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }


}
